package mcm;
import java.util.*;

public class RetrieveInformation extends Command
{
	private static long timestamp;
	
	private static final long serialVersionUID = 3102594311829532409l;

	public static void setCurrentTimestamp() {
		timestamp = new GregorianCalendar().getTimeInMillis();
	}

	public static long getCurrentTimestamp() {
		return timestamp;
	}

	private HashSet groupSet = new HashSet();
	private HashSet participatesSet = new HashSet();
	private HashSet appointmentSet = new HashSet();
	private long localTimestamp;

	private String description;

	public Session execute(String[] args, Session session) throws Exception {
		description = "retrieve ";
		for (int i=0; i<args.length; i++) {
			description += args[i] + " ";
		}

		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		User u = session.getCurrentUser();

		if (u != null) { 
			u = User.find(u.getName());
			if (u == null) {
				throw new Exception("User has been unregistered");
			}

			this.localTimestamp = RetrieveInformation.getCurrentTimestamp();
			if (Long.parseLong(args[0]) == this.localTimestamp) {
				session.addCommand(this);
				return session;
			}

			Iterator iteratorG = Group.getIterator();
			for (; iteratorG.hasNext(); ) {
				groupSet.add(iteratorG.next());
			}

			Iterator iteratorP = Participates.getIterator();
			for (; iteratorP.hasNext(); ) {
				Participates p = (Participates) iteratorP.next();

				if (p.getUser().equals(u)) {
					participatesSet.add(p);
				}
			}

			Iterator iterator = Appointment.getIterator();
			while (iterator.hasNext()) { // check if each appointment of the initiator does not conflict with the new appointment
				Appointment a = (Appointment) iterator.next();
				if (a.getInitiator().equals(u)) {
					appointmentSet.add(a);
				}
			}

			iteratorP = Participates.getIterator();
init:			for (; iteratorP.hasNext(); ) { // check for each group to which the intiator belongs
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
									appointmentSet.add(a);
								}
							}
						}
					}
				}
			}
		} else
			throw new Exception("NO logged-on user");

		session.addCommand(this);
		return session;
	}

	public String toString() {
		return description;
	}

	public Command prototype() {
		return new RetrieveInformation();
	}

	public HashSet getGroupSet() {
		return groupSet;
	}

	public HashSet getAppointmentSet() {
		return appointmentSet;
	}

	public HashSet getParticipatesSet() {
		return participatesSet;
	}

	public long getTimestamp() {
		return localTimestamp;
	}
}
