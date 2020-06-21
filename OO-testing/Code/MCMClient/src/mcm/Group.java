package mcm;

import java.io.*;
import java.util.*;

public class Group implements Serializable {
	public static HashSet groupTable = new HashSet();
	private static final long serialVersionUID = 7042986066171048488l;

	public static Group add(String name, User creator) {
		Group g = find(name);
		if (g != null)
			return null;
		else
			g =	new Group(name, creator);
		if (groupTable.add(g))
			return g;
		else
			return null;
	}

	public static Group remove(String name) {
		Group g = find(name);
		if (groupTable.remove(g))
			return g;
		else
			return null;
	}

	public static Group find(String name) {
		Iterator iterator = groupTable.iterator();

		while (iterator.hasNext()) {
			Group g = (Group) iterator.next();
			if (g.name.equals(name)) {
				return g;
			}
		}
		return null;
	}

	public static Iterator getIterator() {
		return groupTable.iterator();
	}

	public static void open(ObjectInputStream ins) throws Exception {
		groupTable = (HashSet) ins.readObject();
	}

	public static void save(ObjectOutputStream outs) throws Exception {
		outs.writeObject(groupTable);
	}

	private String name;
	private User creator;

	public Group(String name, User creator) {
		this.name = name;
		this.creator = creator;
	}

	public String getName() {
		return name;
	}

	public User getCreator() {
		return creator;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Group))
			return false;

		Group g = (Group) o;
		if (g.name.equals(this.name))
			return true;
		else
			return false;
	}
}