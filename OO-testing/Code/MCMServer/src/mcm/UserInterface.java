package mcm;
import java.io.*;

public class UserInterface {

	public static void main(String[] args) {
		if (args.length == 1) {
			try {
				readInput(args[0]);				
			}
			catch (IOException e) {
				System.err.println(e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Usage: java -jar MCM.jar commandFile");
		}
	}

	public static void readInput(String filename) throws Exception {
		BufferedReader ins = new BufferedReader(new FileReader((filename)));

		Session session = new Session();
		CommandParser parser = CommandParser.getInstance();
		String command;
		while ((command = ins.readLine()) != null) {
			try {
				parser.parseCommandLine(command, session);	
			}
			catch (Exception e)
			{  //e.printStackTrace();
			}
		}

		ins = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print("> ");
			System.out.flush();
			command = ins.readLine();
			try {
				parser.parseCommandLine(command, session);
			}
			catch (Exception e)
			{  //e.printStackTrace(); 
			//System.out.println(e);
			}
		}

		// Print all appointments of each registered user.
//		new PrintResults().execute(null);
	}
}