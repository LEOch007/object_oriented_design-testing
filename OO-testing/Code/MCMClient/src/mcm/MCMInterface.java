package mcm;

import java.rmi.*;

public interface MCMInterface extends Remote
{
	public Session parseCommandLine(String command, Session session) throws Exception;
}
