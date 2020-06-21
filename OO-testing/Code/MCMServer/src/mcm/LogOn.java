package mcm;
public class LogOn extends Command
{
	public Session execute(String[] args, Session session) throws Exception {
		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		User u = User.find(args[0]);

		// Only registered users can logon the program. && Only one user can logon the program at a time.
		if (u != null && session.getCurrentUser() == null) {
			session.setCurrentUser(u);
		}

		// No need to undo
		return session;
	}

	public Command prototype() {
		return new LogOn();
	}
}