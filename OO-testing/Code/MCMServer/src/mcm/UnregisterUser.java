package mcm;
import java.util.*;

public class UnregisterUser extends Command
{
	public Session execute(String[] args, Session session) throws Exception {
		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		// ACTION 
		User u = User.remove(args[0]);

		if (u != null) {
			HashSet deleteSet = new HashSet();

			// remove the group that the user to be unregistered creates
			Iterator iteratorP = Participates.getIterator();
			for (; iteratorP.hasNext(); ) {
				Participates p = (Participates) iteratorP.next();
				if (p.getUser().equals(u) && p.getGroup().getCreator().equals(u)) {
					Group.remove(p.getGroup().getName());
					deleteSet.add(p.getGroup());
				}
			}

			// remove the participations of the users for the removed groups.
			// also, remove the participations of the unregistered user in any groups.
			iteratorP = Participates.getIterator();
			for (; iteratorP.hasNext(); ) {
				Participates p = (Participates) iteratorP.next();
				if (deleteSet.contains(p.getGroup()) || p.getUser().equals(u)) {
					iteratorP.remove();
				}
			}

			// remove the group appointments for the removed groups.
			// Also, all appointments will be automatically deleted if its initiator is unregistered.
			Iterator iteratorA = Appointment.getIterator();
			for (; iteratorA.hasNext(); ) {
				Appointment a = (Appointment) iteratorA.next();
				if (a.getInitiator().equals(u) || (a instanceof GroupAppointment && deleteSet.contains(((GroupAppointment)a).getGroup()))) {
					iteratorA.remove();
				}
			}
			
			// A user will be automatically logged off if the logged-on user is unregistered himself/herself.
			if (session.getCurrentUser() != null && session.getCurrentUser().equals(u)) {
				session.setCurrentUser(null);
				// Very important to clear the command history when logging off.
				session.clearAll();
			}		
		}

		RetrieveInformation.setCurrentTimestamp();
		return session;
	}

	public Command prototype() {
		return new UnregisterUser();
	}
}