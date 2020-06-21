package mcm;
import java.util.*;

public class PrintResults extends Command {
	public Session execute(String[] args, Session session) throws Exception {
		if (args.length != 0)
			throw new Exception("Command arguments not matched");
		
		ArrayList appointmentList = new ArrayList();

		Iterator iteratorU = User.getIterator();
		for (; iteratorU.hasNext(); ) {
			User u = (User) iteratorU.next();

			Iterator iteratorA = Appointment.getIterator();
			for (; iteratorA.hasNext(); ){ 
				Appointment a = (Appointment) iteratorA.next();
				if (!(a instanceof GroupAppointment) && a.getInitiator().equals(u)) {
					appointmentList.add(new Appointment(a.getStartTime(), a.getEndTime(), a.getDescription(), u));
				} else if (a instanceof GroupAppointment && a.getInitiator().equals(u)) {
					Group g = ((GroupAppointment) a).getGroup();

					Iterator iteratorP = Participates.getIterator();
					for (; iteratorP.hasNext(); ) {
						Participates p = (Participates) iteratorP.next();
						if (p.getGroup().equals(g)) {
							appointmentList.add(new GroupAppointment(a.getStartTime(), a.getEndTime(), a.getDescription(),
										      p.getUser(), g));
						}
					}
				}
			}
		}

		Collections.sort(appointmentList, new Comparator() {
				public int compare(Object o1, Object o2) {
					Appointment a1 = (Appointment) o1;
					Appointment a2 = (Appointment) o2;
					int userNameCompare = a1.getInitiator().getName().compareTo(a2.getInitiator().getName());
					int startTimeCompare = a1.getStartTime().compareTo(a2.getStartTime());
					if (userNameCompare < 0 || (userNameCompare == 0 && startTimeCompare < 0)) {
						return -1;
					} else if (userNameCompare == 0 && startTimeCompare == 0) {
						return 0;
					} else {
						return 1;
					}
				}

				public boolean equals(Object o) {
					return this.equals(o);
				}
			});

		Iterator iteratorA = appointmentList.iterator();
		for (; iteratorA.hasNext(); ) {
			System.out.println(iteratorA.next()); 
		}

		// No need to undo.
		return session;
	}

	public Command prototype() {
		return new PrintResults();
	}
}