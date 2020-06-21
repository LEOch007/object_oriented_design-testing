package mcm;
import java.io.*;

public class SaveDatabase extends Command {
	public Session execute(String[] args, Session session) throws Exception {
		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		// Save a database
		ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream(args[0]));
		User.save(outs);
		Group.save(outs);
		Participates.save(outs);
		Appointment.save(outs);

		// No need to undo
		return session;
	}

	public Command prototype() {
		return new SaveDatabase();
	}
}