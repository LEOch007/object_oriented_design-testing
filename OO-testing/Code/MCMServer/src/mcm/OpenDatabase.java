package mcm;
import java.io.*;
import java.util.*;

public class OpenDatabase extends Command
{
	public Session execute(String[] args, Session session) throws Exception {
		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		// Open a database.
		ObjectInputStream ins = new ObjectInputStream(new FileInputStream(args[0]));
		User.open(ins);
		Group.open(ins);
		Participates.open(ins);
		Appointment.open(ins);
		
		// A logged-on user will be automatically logged off when a database is opened.
		session.setCurrentUser(null);

		RetrieveInformation.setCurrentTimestamp();

		// No need to undo.
		return session;
	}

	public Command prototype() {
		return new OpenDatabase();
	}
}
