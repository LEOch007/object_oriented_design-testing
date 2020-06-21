package mcm;

import java.util.*;
import java.text.*;

public class DeleteAppointment extends Command
{
	public Appointment appointment;
	public Session session;
	public String description;

	public Session execute(String[] args, Session session) throws Exception {
		description = "delete ";
		for (int i=0; i<args.length; i++) {
			description += args[i] + " ";
		}

		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		Date startTime = new SimpleDateFormat(Session.TimeFormat).parse(args[0]);

		User u = session.getCurrentUser();
		if (u != null) { // Only the logged-on user can delete an appointment
			appointment = Appointment.remove(startTime, u);
			if (appointment == null) {
				throw new Exception("NO such appointment");
			}
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
		else if (appointment == null)
			throw new Exception("Undo failure: unrecoverable");

		try {
			Session s = new Session();
			s.setCurrentUser(this.session.getCurrentUser());
			String startTime = new SimpleDateFormat(Session.TimeFormat).format(appointment.getStartTime());
			String endTime = new SimpleDateFormat(Session.TimeFormat).format(appointment.getEndTime());
			if (!(appointment instanceof GroupAppointment)) 
				new InsertAppointment().execute(new String[] { startTime, endTime,
													     appointment.getDescription() }, s);		
			else
				new InsertAppointment().execute(new String[] { startTime, endTime,
													     appointment.getDescription(), 
													     ((GroupAppointment) appointment).getGroup().getName() }, s);		
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
		return new DeleteAppointment();
	}
}