package mcm;
import java.io.*;
import java.util.*;

public class Participates implements Serializable
{
	private static HashSet participatesTable = new HashSet();
	private static final long serialVersionUID = 6483265342838241214l;
	
	public static Participates add(User u, Group g) {
		Participates p = find(u, g);
		if (p != null)
			return null;
		else
			p =	new Participates(u, g);
		if (participatesTable.add(p))
			return p;
		else
			return null;
	}

	public static Participates remove(User u, Group g) {
		Participates p = find(u, g);
		if (participatesTable.remove(p)) 
			return p;
		else
			return null;
	}

	public static Participates find(User u, Group g) {
		Iterator iterator = participatesTable.iterator();

		while (iterator.hasNext()) {
			Participates p = (Participates) iterator.next();
			if (p.getUser().equals(u) && p.getGroup().equals(g)) {
				return p;
			}
		}

		return null;
	}
	
	public static Iterator getIterator() {
		return participatesTable.iterator();
	}

	public static void open(ObjectInputStream ins) throws Exception {
		participatesTable = (HashSet) ins.readObject();
	}

	public static void save(ObjectOutputStream outs) throws Exception {
		outs.writeObject(participatesTable);
	}

	private User user;
	private Group group;

	public Participates(User u, Group g) {
		this.user = u;
		this.group = g;
	}

	public User getUser() {
		return user;
	}

	public Group getGroup() {
		return group;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Participates))
			return false;

		Participates p = (Participates) o;
		if (p.getUser().equals(user) && p.getGroup().equals(group))
			return true;
		else
			return false;
	}

	public int hashCode() {
		return user.hashCode() * group.hashCode();
	}
}
