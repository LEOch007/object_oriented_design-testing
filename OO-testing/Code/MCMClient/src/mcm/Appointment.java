package mcm;

import java.io.*;
import java.util.*;
import java.text.*;

public class Appointment implements Serializable {
// Static
	public static HashSet appointmentTable = new HashSet();
	private static final long serialVersionUID = 8723579288775196101l;
	
	public static Appointment add(Date startTime, Date endTime, String description, User initiator) {
		Appointment a = find(startTime, initiator);
		if (a != null)
			return null;
		else
		    a = new Appointment(startTime, endTime, description, initiator);
		if (appointmentTable.add(a))
			return a;
		else
			return null;
	}

	public static Appointment add(Appointment a) {
		if (appointmentTable.add(a))
			return a;
		else
			return null;
	}

	public static Appointment remove(Date startTime, User initiator) {
		Appointment a = find(startTime, initiator);
		if (appointmentTable.remove(a)) 
			return a;
		else 
			return null;
	}

	public static Appointment find(Date startTime, User initiator) {
		Iterator iterator = appointmentTable.iterator();

		while (iterator.hasNext()) {
			Appointment g = (Appointment) iterator.next();
			if (g.startTime.equals(startTime) && g.initiator.equals(initiator)) {
				return g;
			}
		}
		return null;
	}

	public static Iterator getIterator() {
		return appointmentTable.iterator();
	}
	
	public static void open(ObjectInputStream ins) throws Exception {
		appointmentTable = (HashSet) ins.readObject();
	}

	public static void save(ObjectOutputStream outs) throws Exception {
		outs.writeObject(appointmentTable);
	}

// Instance
	public Date startTime;
	public Date endTime;
	public String description;
	public User initiator;

	public Appointment(Date startTime, Date endTime, String description, User initiator) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
		this.initiator = initiator;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getDescription() {
		return description;
	}

	public User getInitiator() {
		return initiator;
	}

	public boolean isConflict(Date startTime, Date endTime) {
		if (startTime.compareTo(this.startTime) >= 0 && startTime.compareTo(this.endTime) < 0)
			return true;
		else if (this.startTime.compareTo(startTime) >= 0 && this.startTime.compareTo(endTime) < 0)
			return true;
		else
			return false;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Appointment))
			return false;

		Appointment a = (Appointment) o;
		if (a.startTime.equals(this.startTime) && a.initiator.equals(this.initiator))
			return true;
		else
			return false;
	}

	public int hashCode() {
		return this.startTime.hashCode() * this.initiator.hashCode();
	}

	public String toString() {
		return initiator.getName() + " " + 
			new SimpleDateFormat(Session.TimeFormat).format(startTime) + " " +
			new SimpleDateFormat(Session.TimeFormat).format(endTime) + " " + 
			description;
	}
}