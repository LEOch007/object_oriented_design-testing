package mcm;
public class LogOff extends Command
{
	public Session execute(String[] args, Session session) throws Exception {
		if (args.length != 0)
			throw new Exception("Command arguments not matched");

		session.setCurrentUser(null);
		session.clearAll();

		// No need to undo.
		return session;
	}

	public Command prototype() {
		return new LogOff();
	}
}
