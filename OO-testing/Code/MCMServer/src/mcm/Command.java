package mcm;
import java.io.*;

public abstract class Command implements Serializable
{
//	protected Session session = Session.getInstance();

	public abstract Session execute(String[] args, Session session) throws Exception;
	public abstract Command prototype();

	public void undo(Session session) throws Exception {
	}
}