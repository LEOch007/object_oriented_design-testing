package mcm;
import java.util.*;
import java.io.*;

public class Session implements Serializable
{
	public static final String TimeFormat = "dd-MM-yyyy HH:mm";
//	private static final Session soleInstance = new Session();
	private User currentUser;
	private Command currentCommand;
	private Stack commandHistory = new Stack();
	
	private static final long serialVersionUID = 2718759272444247236l;

	public Session() {
	}

/*	public static Session getInstance() {
		return soleInstance;
	}
*/
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public Command getCurrentCommand() {
		return currentCommand;
	}

	public void addCommand(Command currentCommand) {
		this.currentCommand = currentCommand;
		if (commandHistory != null)
			commandHistory.add(currentCommand);
	}

	public Command pop() {
		if (commandHistory != null) {
			Command command = (Command) commandHistory.pop();
			if (!commandHistory.empty()) {
				this.currentCommand = (Command) commandHistory.peek();
			} else 
				this.currentCommand = null;
			return command;
		}
		else
			return null;
	}

	public void clearAll() {
		currentCommand = null;
		if (commandHistory != null) 
			commandHistory.clear();
		
	}

	public Stack getCommandHistory() {
		return commandHistory;
	}
}