package mcm;
import java.util.*;
import java.text.*;

public class RemoveGroup extends Command
{
	private Session session;
	private Group group;
	private HashSet participatesSet = new HashSet();
	private HashSet appointmentSet = new HashSet();
	private String description;

	public Session execute(String[] args, Session session) throws Exception {
		description = "remove ";
		for (int i=0; i<args.length; i++) {
			description += args[i] + " ";
		}
		
		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		User u = session.getCurrentUser();

		if (u != null) { // Only logged-on user can remove a group.
			Group g = Group.find(args[0]);

			if (g != null) { // Only existing groups can be removed
				if (g.getCreator().equals(u)) { // Only the group creator can remove the group
					// ACTION
					group = Group.remove(args[0]);

					// Remove all participations of that group.
					Iterator iteratorP = Participates.getIterator();
					for (; iteratorP.hasNext(); ) {
						Participates p = (Participates) iteratorP.next();
						if (p.getGroup().equals(g)) {
							iteratorP.remove();
							participatesSet.add(p);
						}
					}

					// A group appointment is automatically deleted when its corresponding group is removed.
					Iterator iteratorA = Appointment.getIterator();

					for (; iteratorA.hasNext(); ) {
						Appointment a = (Appointment) iteratorA.next();
						if (a instanceof GroupAppointment && ((GroupAppointment)a).getGroup().equals(g)) {
							iteratorA.remove(); 
							appointmentSet.add(a);
						}
					}

				} else
					throw new Exception("User not group creator");
			} else
				throw new Exception("NO such group");
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
		else if (group == null)
			throw new Exception("Undo failure: unrecoverable");

		try {
			Session s = new Session();
			s.setCurrentUser(this.session.getCurrentUser());

			// Remove unregistered users from participatesSet. and appointmentSet
			Iterator iteratorP = participatesSet.iterator();
			for (; iteratorP.hasNext(); ) {
				Participates p = (Participates) iteratorP.next();

				if (User.find(p.getUser().getName()) == null) {
					iteratorP.remove();
					Iterator iteratorA = appointmentSet.iterator();
					for (; iteratorA.hasNext(); ) {
						Appointment a = (Appointment) iteratorA.next();

						if (a.getInitiator().equals(p.getUser())) {
							iteratorA.remove(); 
						}
					}
				} 
			}

			// Check feasibility
			// Check the schedules of all members-to-be.
			Group g = group;
			iteratorP = participatesSet.iterator();
			for (; iteratorP.hasNext(); ) {
				Participates p = (Participates) iteratorP.next();
				if (p.getGroup().equals(g)) {
					Iterator iteratorA = appointmentSet.iterator();
					for (; iteratorA.hasNext(); ) {
						Appointment a = (Appointment) iteratorA.next();
//	System.out.println(a);
						Date startTime = a.getStartTime();
						Date endTime = a.getEndTime();
					
						if (InsertAppointment.isConflict(p.getUser(), startTime, endTime)) {
							throw new Exception("Group appointment insertion failure: appointment time conflict");
						}
					}
				}
			}

			// Remove the group creator from participatesSet.
			iteratorP = participatesSet.iterator();
			for (; iteratorP.hasNext(); ) {
				Participates p = (Participates) iteratorP.next();
				if (p.getUser().equals(group.getCreator())) {
					iteratorP.remove();
				}
			}

			// Action
			new CreateGroup().execute(new String[] { group.getName() }, s);		

			iteratorP = participatesSet.iterator();
			for (; iteratorP.hasNext(); ) {
				Participates p = (Participates) iteratorP.next();

				Session s1 = new Session();
				s1.setCurrentUser(p.getUser());
				new JoinGroup().execute(new String[] { group.getName() }, s1);
			}

			Iterator iteratorA = appointmentSet.iterator();
			for (; iteratorA.hasNext(); ) {
				Appointment a = (Appointment) iteratorA.next();
				
				Session s1 = new Session();
				s1.setCurrentUser(a.getInitiator());
				String startTime = new SimpleDateFormat(Session.TimeFormat).format(a.getStartTime());
				String endTime = new SimpleDateFormat(Session.TimeFormat).format(a.getEndTime());
				new InsertAppointment().execute(new String[] { startTime, endTime, a.getDescription(), group.getName()}, s1);
				RetrieveInformation.setCurrentTimestamp();
			}
		}
		catch (Exception e) {
//		System.out.println(e);
//		e.printStackTrace();
			throw new Exception("Undo failure: unrecoverable");
		}
	}

	public String toString() {
		return description;
	}

	public Command prototype() {
		return new RemoveGroup();
	}
}