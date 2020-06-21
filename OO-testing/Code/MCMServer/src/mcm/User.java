package mcm;
import java.io.*;
import java.util.*;

public class User implements Serializable {
// Static
	private static HashSet userTable = new HashSet();
	
	private static final long serialVersionUID = 5334294390198335632l;

	public static User add(String name, String email) {
		User u = find(name);
		if (u != null)
			return null;
		else
			u =	new User(name, email);

		if (userTable.add(u))
			return u;
		else
			return null;
	}

	public static User remove(String name) {
		User u = find(name);
		if (userTable.remove(u))
			return u;
		else
			return null;
	}

	public static User find(String name) {
		Iterator iterator = userTable.iterator();

		while (iterator.hasNext()) {
			User u = (User) iterator.next();
			if (u.name.equals(name)) {
				return u;
			}
		}
		return null;
	}

	public static Iterator getIterator() {
		return userTable.iterator();
	}

	public static void open(ObjectInputStream ins) throws Exception {
		userTable = (HashSet) ins.readObject();
	}

	public static void save(ObjectOutputStream outs) throws Exception {
		outs.writeObject(userTable);
	}

// Instance
	private String name;
	private String email;

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public boolean equals(Object o) {
		if (!(o instanceof User))
			return false;

		User u = (User) o;
		if (u.name.equals(this.name))
			return true;
		else
			return false;
	}

	public int hashCode() {
		return this.name.hashCode();
	}
}