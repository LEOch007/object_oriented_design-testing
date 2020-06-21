package mcm;

import java.util.*;
import java.text.*;

public class InsertAppointment extends Command 
{
	public Appointment appointment;
	public Session session;
	public String description;

	public Session execute(String[] args, Session session) throws Exception {
		description = "insert ";
		for (int i=0; i<args.length; i++) {
			description += args[i] + " ";
		}
		
		if (args.length != 3 && args.length != 4 )
			throw new Exception("Command arguments not matched");

		User u = session.getCurrentUser();
		Date startTime = new SimpleDateFormat(Session.TimeFormat).parse(args[0]);
		Date endTime = new SimpleDateFormat(Session.TimeFormat).parse(args[1]);
		
		if (startTime.compareTo(endTime) >= 0)
			throw new Exception("Appointment insertion failure: start time NOT earlier than end time");

		if (u != null) { // Only the logged-on user can insert an appointment
			Group g = null;

			if (args.length == 3) {
				if (!isConflict(u, startTime, endTime)) {
					appointment = Appointment.add(startTime, endTime, args[2], u);
				} else {
					throw new Exception("Individual appointment insertion failure: appointment time conflict");
				}
			}
			else if (args.length == 4) {
				g = Group.find(args[3]);
				if (g != null) {
					Participates p = Participates.find(u, g);
					if (p == null) // A group appointment can only be initiated by a member of the appointment's group
						throw new Exception("Group appointment insertion failure: user not group member");

					Iterator iteratorP = Participates.getIterator();
					for (; iteratorP.hasNext(); ) {
						p = (Participates) iteratorP.next();
						if (p.getGroup().equals(g)) {
							if (isConflict(p.getUser(), startTime, endTime)) {
								throw new Exception("Group appointment insertion failure: appointment time conflict");
							}
						}
					}
					appointment = GroupAppointment.add(startTime, endTime, args[2], u, g);
				} else {
					throw new Exception("Group appointment insertion failure: group does not exist");
				}
			}
		}

		RetrieveInformation.setCurrentTimestamp();
		this.session = session;
		session.addCommand(this);
		return session;
	}

	public void undo(Session session) throws Exception {
		if (this.session == null || session == null)
			throw new Exception("NO command to undo");
		else if (!this.session.getCurrentUser().equals(session.getCurrentUser()))
			throw new Exception("Undo failure: user not matched");
		else if (appointment == null)
			throw new Exception("Undo failure: unrecoverable");

		try {
			Session s = new Session();
			s.setCurrentUser(this.session.getCurrentUser());
			String startTime = new SimpleDateFormat(Session.TimeFormat).format(appointment.getStartTime());
			String endTime = new SimpleDateFormat(Session.TimeFormat).format(appointment.getEndTime());
			new DeleteAppointment().execute(new String[] {startTime}, s);
			RetrieveInformation.setCurrentTimestamp();
		}
		catch (Exception e) {
//	System.out.println(e);
			throw new Exception("Undo failure: unrecoverable");
		}
	}

	// Check if an appointment conflicts with the schedule of a specified user.
	public static boolean isConflict(User u, Date startTime, Date endTime) {
		boolean isConflict = false;
		Iterator iterator = Appointment.getIterator();
		while (iterator.hasNext()) { // check if each appointment of the initiator does not conflict with the new appointment
			Appointment a = (Appointment) iterator.next();
			if (a.getInitiator().equals(u) && a.isConflict(startTime, endTime)) {
				isConflict = true;
				break;
			}
		}

		Iterator iteratorP = Participates.getIterator();
init:		for (; iteratorP.hasNext(); ) { // check for each group to which the intiator belongs
			Participates p = (Participates) iteratorP.next();
			if (p.getUser().equals(u)) {
				Group g1 = p.getGroup();
	
				Iterator iteratorP1 = Participates.getIterator(); 
				for (; iteratorP1.hasNext(); ) { // for each group member
					Participates p1 = (Participates) iteratorP1.next();
					if (p1.getGroup().equals(g1)) {
						Iterator iteratorA = Appointment.getIterator();
						for (; iteratorA.hasNext(); ) { // for each group appointment, for that group, that the member initiates
							Appointment a = (Appointment) iteratorA.next();
							if (a instanceof GroupAppointment && ((GroupAppointment)a).getGroup().equals(g1) && a.getInitiator().equals(p1.getUser())) {
								if (a.isConflict(startTime, endTime)) {
									isConflict = true;
									break init;
								}
							}
						}
					}
				}
			}
		}
		return isConflict;
	}

	public String toString() {
		return description;
	}

	public Command prototype() {
		return new InsertAppointment();
	}
}