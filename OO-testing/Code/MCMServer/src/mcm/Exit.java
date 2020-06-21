package mcm;
public class Exit extends Command
{
	public Session execute(String[] args, Session session) throws Exception {
		if (args.length != 0)
			throw new Exception("Command arguments not matched");

		System.exit(0);
		RetrieveInformation.setCurrentTimestamp();
		// No need to undo.
		return session;
	}

	public Command prototype() {
		return new Exit();
	}
}
