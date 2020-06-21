package mcm;
import java.io.*;
import java.util.*;
import java.text.*;

public class GroupAppointment extends Appointment implements Serializable {
	private static final long serialVersionUID = 964777230352060774l;
	
	public static Appointment add(Date startTime, Date endTime, String description, User initiator, Group group) {
		return Appointment.add(new GroupAppointment(startTime, endTime, description, initiator, group));		
	}
	
	private Group group;

	public GroupAppointment(Date startTime, Date endTime, String description, User initiator, Group group) {
		super(startTime, endTime, description, initiator);
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	public String toString() {
		return super.toString() + " " + group.getName();
	}
}