package mcm;
public class CreateGroup extends Command
{
	private Group group;
	private Session session;
	private String description;

	public Session execute(String[] args, Session session) throws Exception {
		description = "create ";
		for (int i=0; i<args.length; i++) {
			description += args[i] + " ";
		}

		if (args.length != 1)
			throw new Exception("Command arguments not matched");

		User u = session.getCurrentUser();

		if (u != null) { // Only the logged-on user can create a group.
			Group g = Group.add(args[0], u);

			// When a user creates a group, the user will automatically be a member of the group.
			if (g != null) {
				this.group = g;
				Participates.add(u, g);
			} else
				throw new Exception("Group already exists");
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
		else if (group == null)
			throw new Exception("Undo failure: unrecoverable");

		try {
			Session s = new Session();
			s.setCurrentUser(this.session.getCurrentUser());
			new RemoveGroup().execute(new String[] { group.getName() }, s);		
			RetrieveInformation.setCurrentTimestamp();
		}
		catch (Exception e) {
			throw new Exception("Undo failure: unrecoverable");
		}
	}

	public String toString() {
		return description;
	}

	public Command prototype() {
		return new CreateGroup();
	}
}
