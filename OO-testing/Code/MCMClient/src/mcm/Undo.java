package mcm;

public class Undo extends Command 
{
	public Session execute(String[] args, Session session) throws Exception {
		if (args.length != 0)
			throw new Exception("Command arguments not matched");

		Command command = session.getCurrentCommand();
		if (command == null) {
			throw new Exception("Undo failure");
		}

		command.undo(session);
		session.pop();
//System.out.println(session.getCurrentCommand());
		RetrieveInformation.setCurrentTimestamp();
		// No need to undo.
		return session;
	}

	public Command prototype() {
		return new Undo();
	}
}
