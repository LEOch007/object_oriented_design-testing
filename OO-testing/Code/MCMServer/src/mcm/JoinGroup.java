package mcm;
import java.util.*;

public class JoinGroup extends Command
{
	private Session session;
	private Participates participates;
	private String description;

	public Session execute(String[] args, Session session) throws Exception {
		description = "join ";
		for (int i=0; i<args.length; i++) {
			description += args[i] + " ";
		}
		
		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		User u = session.getCurrentUser();

		if (u != null) { // Only logged-on user can join group.
			Group g = Group.find(args[0]);

			if (g != null) { // A user can only join an existing group.

				// A user cannot join a group if the duration of any appointment in the user's calendar overlaps with the duration of any group appointment in the group.
				Iterator iteratorA = Appointment.getIterator();
				for (; iteratorA.hasNext(); ) {
					Appointment a = (Appointment) iteratorA.next();
					if (a instanceof GroupAppointment && ((GroupAppointment) a).getGroup().equals(g)) {
						if (InsertAppointment.isConflict(u, a.getStartTime(), a.getEndTime())) {
							throw new Exception("Group joining failure: appointment time conflict");
						}
					}
				}

				participates = Participates.add(u, g);
				if (participates == null) {
					throw new Exception("Already joined the group");
				}
			} else
			throw new Exception("Group does not exist");
		} else
			throw new Exception("NO logged-on user");

		RetrieveInformation.setCurrentTimestamp();
		this.session = session;
		session.addCommand(this);
		return session;
	}

	public void undo(Session session) throws Exception {
//System.out.println("Undoing");
		if (this.session == null || session == null)
			throw new Exception("NO command to undo");
		else if (!this.session.getCurrentUser().equals(session.getCurrentUser()))
			throw new Exception("Undo failure: user not matched");
		else if (participates == null) {
//System.out.println("Undoing2");
			throw new Exception("Undo failure: unrecoverable");
}

		try {
			Session s = new Session();
			s.setCurrentUser(this.session.getCurrentUser());
			
			new WithdrawGroup().execute(new String[] {participates.getGroup().getName()}, s);
			RetrieveInformation.setCurrentTimestamp();
		}
		catch (Exception e) {
//	System.out.println(e);
			throw new Exception("Undo failure: unrecoverable");
//throw e;
		}
	}

	public String toString() {
		return description;
	}

	public Command prototype() {
		return new JoinGroup();
	}
}
