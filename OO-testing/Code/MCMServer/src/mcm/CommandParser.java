package mcm;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

public class CommandParser extends UnicastRemoteObject implements MCMInterface
{
	private static CommandParser soleInstance;

	static {
		try {
			soleInstance = new CommandParser();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap commandMap = new HashMap();

	private CommandParser() throws Exception {
		commandMap.put("register", new RegisterUser());
		commandMap.put("unregister", new UnregisterUser());
		commandMap.put("logon", new LogOn());
		commandMap.put("logoff", new LogOff());
		commandMap.put("create", new CreateGroup());
		commandMap.put("remove", new RemoveGroup());
		commandMap.put("join", new JoinGroup());
		commandMap.put("withdraw", new WithdrawGroup());
		commandMap.put("insert", new InsertAppointment());
		commandMap.put("delete", new DeleteAppointment());
		commandMap.put("open", new OpenDatabase());
		commandMap.put("save", new SaveDatabase());
		commandMap.put("print", new PrintResults());
		commandMap.put("exit", new Exit());
		commandMap.put("undo", new Undo());
		commandMap.put("retrieve", new RetrieveInformation());

		java.rmi.registry.LocateRegistry.createRegistry(1099);
		Naming.rebind("Server", this);		
	}

	public static CommandParser getInstance() {
		return soleInstance;
	}

	public synchronized Session parseCommandLine(String command, Session session) throws Exception {
		//System.out.println(command);
		StringTokenizer st = new StringTokenizer(command);
		if (!st.hasMoreTokens()) throw new Exception("Command not found");

		String type = st.nextToken();
		ArrayList args = new ArrayList();

		while (st.hasMoreTokens()) {
			String pre = st.nextToken(" ");

			if (pre.charAt(0) == '"') {
				if (pre.length() > 1 && pre.charAt(pre.length()-1) == '"') {
					args.add(pre.substring(1, pre.length()-1));
				} else {
					String in = st.nextToken("\"");
					args.add(pre.substring(1, pre.length()) + in);
					if (st.hasMoreTokens()) {
						st.nextToken(" ");
					} 
				}
			} else {
				args.add(pre.trim());
			}
		}

		String[] argsArray = new String[args.size()];
		for (int i=0; i<args.size(); i++) {
			argsArray[i] = (String) args.get(i);
		}

		Command currentCommand = (Command) commandMap.get(type);
		if (currentCommand != null) {
			return currentCommand.prototype().execute(argsArray, session);
		}
		
		throw new Exception("Command not found");
	}
}