package mcm;
import java.util.*;

public class RegisterUser extends Command {
	public Session execute(String[] args, Session session) throws Exception {
		if (args.length != 2)
			throw new Exception("Command arguments not matched");

		User u = User.add(args[0], args[1]);

		return session;
	}

	public Command prototype() {
		return new RegisterUser();
	}
}