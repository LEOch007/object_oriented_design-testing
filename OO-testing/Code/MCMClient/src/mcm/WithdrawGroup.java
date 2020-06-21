package mcm;

import java.util.*;
import java.text.*;

public class WithdrawGroup extends Command
{
	public Session session;
	public Participates participates;
	public HashSet appointmentSet = new HashSet();
	public String description;

	public Session execute(String[] args, Session session) throws Exception {
		description = "withdraw ";
		for (int i=0; i<args.length; i++) {
			description += args[i] + " ";
		}

		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		User u = session.getCurrentUser();
		if (u != null) { // Only the logged-on user can withdraw group
			Group g = Group.find(args[0]);

			if (g != null && !(g.getCreator().equals(u))) { // A user can only withdraw an existing group. The leader of the group cannot withdraw the group.
				Participates p = Participates.remove(u, g);
				participates = p;

				// A group appointment is automatically deleted when its initiator withdraw the group.
				if (p != null) {
					Iterator iteratorA = Appointment.getIterator();

					for (; iteratorA.hasNext(); ) { 
						Appointment a = (Appointment) iteratorA.next();
						if (a instanceof GroupAppointment && ((GroupAppointment)a).getGroup().equals(g) && a.getInitiator().equals(u)) {
							iteratorA.remove();
							appointmentSet.add(a);
						}				
					}
				} else {
					throw new Exception("User does not belong to the group");
				}
			} else
				throw new Exception("Group does not exist OR User is not the leader of the group");
		} else
			throw new Exception("NO logged-on user");

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
		else if (participates == null)
			throw new Exception("Undo failure: unrecoverable");

		try {
			Session s = new Session();
			s.setCurrentUser(this.session.getCurrentUser());

			// Check feasibility

			// c1. Check the schedules of all group members.
			Group g = participates.getGroup();
			Iterator iteratorP = Participates.getIterator();
			for (; iteratorP.hasNext(); ) {
				Participates p = (Participates) iteratorP.next();
				if (p.getGroup().equals(g)) {
					Iterator iteratorA = appointmentSet.iterator();
					for (; iteratorA.hasNext(); ) {
						Appointment a = (Appointment) iteratorA.next();
						Date startTime = a.getStartTime();
						Date endTime = a.getEndTime();
					
						if (InsertAppointment.isConflict(p.getUser(), startTime, endTime)) {
							throw new Exception("Group appointment insertion failure: appointment time conflict");
						}
					}
				}
			}

			// c2. Check the schedule of the member-to-be.
			Iterator iteratorA = appointmentSet.iterator();
			for (; iteratorA.hasNext(); ) {
				Appointment a = (Appointment) iteratorA.next();
				Date startTime = a.getStartTime();
				Date endTime = a.getEndTime();
					
				if (InsertAppointment.isConflict(participates.getUser(), startTime, endTime)) {
					throw new Exception("Group appointment insertion failure: appointment time conflict");
				}
			}

			// Action

			// c3. Note that this action implicitly checks whether or not the member-to-be has schedule conflict with the associated group.
			// Also, note that c2 and c3 are different cases.
			new JoinGroup().execute(new String[] {participates.getGroup().getName()}, s);

			iteratorA = appointmentSet.iterator();
			for (; iteratorA.hasNext(); ) {
				Appointment a = (Appointment) iteratorA.next();
				String startTime = new SimpleDateFormat(Session.TimeFormat).format(a.getStartTime());
				String endTime = new SimpleDateFormat(Session.TimeFormat).format(a.getEndTime());
				new InsertAppointment().execute(new String[] { startTime, endTime, a.getDescription(),
													     participates.getGroup().getName()}, s);

			}
			RetrieveInformation.setCurrentTimestamp();
		}
		catch (Exception e) {
//	System.out.println(e);
			throw new Exception("Undo failure: unrecoverable");
		}
	}

	public String toString() {
		return description;
	}

	public Command prototype() {
		return new WithdrawGroup();
	}
}
