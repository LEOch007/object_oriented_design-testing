import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import mcm.Appointment;
import mcm.CreateGroup;
import mcm.DeleteAppointment;
import mcm.Group;
import mcm.GroupAppointment;
import mcm.InsertAppointment;
import mcm.JoinGroup;
import mcm.Participates;
import mcm.RemoveGroup;
import mcm.RetrieveInformation;
import mcm.Session;
import mcm.Undo;
import mcm.User;
import mcm.WithdrawGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

class AppointmentTest {
	private static Date s1,s2,s3;
	private static Date e1,e2,e3;
	private static User u1,u2,u3,u4;
	private static Group g1,g2;
	private static DateFormat df;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// initialize variables
		df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		
		s1 = df.parse("18-03-2020 08:00");
		e1 = df.parse("18-03-2020 10:00");	
		s2 = df.parse("18-03-2020 14:00");
		e2 = df.parse("18-03-2020 20:00");
		s3 = df.parse("20-03-2020 8:00");
		e3 = df.parse("20-03-2020 18:00");
		
		u1 = new User("Jim","jim@gmail.com");
		u2 = new User("Jerry","jerry@world.com");
		u3 = new User("Jas","jas@qq.com");
		u4 = new User("Kane","kane@lion.com");
		
		g1 = new Group("Group g1 of u1",u1);
		g2 = new Group("Group g2 of u4",u4);
		
		// construct the scenario
		/* 
		 * scenario 1: 
		 * Group g1 of u1: u3
		 * User 2 adds Appointment(delivery time) from s3 to e3
		 */
		User.add(u1.getName(), u1.getEmail());
		User.add(u3.getName(), u3.getEmail());
		g1 = Group.add(g1.getName(), u1);
		Participates.add(u3, g1);
		Appointment.add(s3, e3, "conducting work after g1's second mtg", u2);
		
		/*
		 * scenario 2:
		 * Group g2 of u4: u1
		 */
		User.add(u4.getName(), u4.getEmail());
		g2 = Group.add(g2.getName(), u4);
		Participates.add(u1, g2);
	}
	
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// release variables
		s1 = null; s2=null; s3=null;
		e1 = null; e2=null; e3=null;
		u1 = null; u2 = null; u3=null;u4=null;
		g1 = null; g2 = null;
	}
	
	@BeforeEach
	void setUp() throws Exception {
		
	}

	@AfterEach
	void tearDown() throws Exception {
		
	}
	
	// to test Appointment class
	@Test
	void testAppointment() throws Exception {
		// setup
		Date start = new Date();
		Date stop = new Date();
		String description = "CSIT5100";
		User user = new User("Jim", "jim@world.com");
		
		Date start1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2020-03-18 08:00");
		Date stop1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2020-03-18 10:00");
		String description1 = "NBA game";
		User user1 = new User("Kane", "kane@world.com");
		
		Appointment appointment = new Appointment(start, stop, description, user);
		Appointment.add(appointment);
		Appointment foundAppointment = Appointment.find(start, user);
		
		// test add and get function
		assertNull(Appointment.add(start, stop, description, user));
		assertTrue(appointment.equals(foundAppointment));
		assertTrue(appointment.getStartTime().equals(start));
		assertTrue(appointment.getEndTime().equals(stop));
		assertTrue(appointment.getDescription().equals(description));
		assertTrue(appointment.getInitiator().equals(user));
		
		// test remove and toString function
		Appointment temp = Appointment.remove(start, user);
		assertTrue(temp.equals(foundAppointment));
		String message = temp.toString();
		assertEquals(message,temp.toString());
		assertNull(Appointment.remove(new Date(100), new User("Jerry","je@126.com")));
		
		// test isConflict function
		Appointment app1 = new Appointment(start1,stop1,description1,user1);
		assertTrue(app1.isConflict(start1, stop1));
		
	}
	
	// to test User class
	@Test
	void testUser() {
		String name = "Jerry";
		String email = "je@126.com";
		
		// test add function
		User.add(name, email);
		User u = new User(name,email);
		assertTrue(User.find(name).equals(u));
		assertNull(User.add(name, email)); //duplicated
		
		// test get function
		assertTrue(u.getEmail().equals("je@126.com"));
		assertTrue(u.getName().equals("Jerry"));
		
		// test remove function
		assertTrue(User.remove(name).equals(u));
		assertNull(User.remove("Harry"));
	}
	
	// to test Group class
	@Test
	void testGroup() {
		String name = "Yoga mtg";
		User creator = new User("Jas","jas@qq.com");
		
		// test add function
		Group.add(name, creator);
		Group g = new Group(name,creator);
		assertTrue(Group.find(name).equals(g));
		assertNull(Group.add(name, creator));
		
		// test get function
		assertEquals( g.getName(), name);
		assertEquals( g.getCreator(), creator);
		
		// test remove function
		assertEquals(Group.remove("FridayAction"),null);
		assertEquals(Group.remove(name),g);
	}
	
	// to test Participates class
	@Test
	void testPartcipates() {
		User joiner = new User("Poter","po@qq.com");
		User creator = new User("Green","green@gmail.com");
		Group group = new Group("Poter's birthday",creator);
		
		// test add function
		Participates.add(joiner, group);
		Participates findpart = Participates.find(joiner, group);
		Participates part = new Participates(joiner,group);
		assertTrue(findpart.equals(part));
		assertNull(Participates.add(joiner, group));
		
		// test get function
		assertEquals(part.getUser(),joiner);
		assertEquals(part.getGroup(),group);
		
		// test remove function
		assertNull(Participates.remove(u1, g1));
		assertEquals(part,Participates.remove(joiner, group));
		assertFalse(part.equals(Participates.remove(joiner, group)));
	}
	
	// to test GroupAppointment class
	@Test
	void testGroupAppointment() {
		// test add function
		GroupAppointment.add(s1,e1,"first group mtg of u1",u1,g1);
		assertNotNull(Appointment.find(s1, u1));
		
		// test get function
		GroupAppointment GA = new GroupAppointment(s1,e1,"first group mtg of u1",u1,g1);
		assertTrue(GA.getGroup().equals(g1));
		Appointment app = Appointment.find(s1, u1);
		assertEquals(app.toString(),GA.toString()); //expected,actual
	}
	
	// to test InsertAppointment class
	@Test
	void testInsertAppointment() throws Exception {
		InsertAppointment IA = new InsertAppointment();
		
		/* 
		 * User 3: Jas insert appointment of Group u1's first mtg 
		 */
		Session s = new Session();
		s.setCurrentUser(u3);
		
		String description = "first group mtg of u1";
		String[] args = new String[] {df.format(s1),df.format(e1),description,g1.getName()};
		
		// test execute
		Session ns = IA.execute(args,s);	
		assertEquals(ns.currentUser,u3);
		assertEquals(description,Appointment.find(s1, u3).getDescription());
		
		// test undo
		IA.undo(ns); 
		assertNull(Appointment.find(s1, u3));
		
		
		/*
		 *  User 2: Jerry insert appointment of his first work schedule 
		 */
		Session ss = new Session();
		ss.setCurrentUser(u2);
		
		description = "conducting work after g1's first mtg";
		String[] arg = new String[] {df.format(s2),df.format(e2),description};
		
		// test execute
		ns = IA.execute(arg, ss);
		assertEquals(ns.currentUser, Appointment.find(s2, u2).getInitiator());
		
		// test undo
		IA.undo(ns);
		assertNull(Appointment.find(s2, u2));
	}
	
	// to test DeleteAppointment class
	@Test
	void testDeleteAppointment() throws Exception {
		DeleteAppointment DA = new DeleteAppointment();
		
		/*
		 * User 2: Jerry delete appointment of his second work schedule
		 */
		Session ss = new Session();
		ss.setCurrentUser(u2);
		String[] arg = new String[] {df.format(s3)};
		
		// test Session class function
		assertNull(ss.getCurrentCommand());
		assertTrue(ss.getCommandHistory().isEmpty());
		
		// test execute
		assertNotNull(Appointment.find(s3, u2));
		Session ns = DA.execute(arg, ss);
		assertNull(Appointment.find(s3, u2));
		assertEquals("delete "+df.format(s3)+" " , DA.toString());
		
		// test Session class function
		assertNotNull(ss.getCurrentCommand());
		assertFalse(ss.getCommandHistory().isEmpty());
		
		// test undo
		DA.undo(ns);
		assertNotNull(Appointment.find(s3, u2));
		assertNotNull(ns.pop());
	}
	
	// to test JoinGroup class
	@Test
	void testJoinGroup() throws Exception {	
		JoinGroup JG = new JoinGroup();
		
		/*
		 *  User 2: Jerry join Group g1;
		 */
		Session s = new Session();
		s.setCurrentUser(u2);
		String[] args = new String[] {g1.getName()};
		
		// test execute
		Session ns = JG.execute(args,s);
		assertEquals("join Group g1 of u1 ",JG.toString());
		assertNotNull(Participates.find(u2, g1));
		
		// test undo
		JG.undo(ns);
		assertNull(Participates.find(u2, g1));
	}
	
	// to test WithdrawGroup class
	@Test
	void testWithdrawGroup() throws Exception {
		//TODO: bug "leader cannot withdraw the group but the message is wrong"
		WithdrawGroup WG = new WithdrawGroup();
		
		/*
		 *  User 1: Jim Withdraw Group g2; 
		 */
		Session s = new Session();
		s.setCurrentUser(u1);
		String[] args = new String[] {g2.getName()};
		GroupAppointment.add(s3, e3, "Group 2 mtg", u1,g2);
		
		// test execute
		assertNotNull(Appointment.find(s3, u1));
		assertNotNull(Participates.find(u1, g2));
		Session ns = WG.execute(args, s);
		assertNull(Participates.find(u1, g2));
		assertEquals("withdraw Group g2 of u4 ",WG.toString());
		assertNull(Appointment.find(s3, u1));
		
		// test undo
		WG.undo(ns);
		assertNotNull(Participates.find(u1, g2));
		assertNotNull(Appointment.find(s3, u1));
	}
	
	// to test CreateGroup class
	@Test
	void testCreateGroup() throws Exception{
		CreateGroup CG = new CreateGroup();
		
		/*
		 * User 3: Jas create a Group g3
		 * and user 4 Kane join in g3
		 */
		Session s = new Session();
		s.setCurrentUser(u3);
		String gname = "Group g3 of u3";
		String[] args = new String[] {gname};
		
		// test execute
		assertNull(Group.find(gname));
		Session ns = CG.execute(args, s);
		assertNotNull(Group.find(gname));
		assertEquals("create Group g3 of u3 ",CG.toString());
		assertNotNull(Participates.add(u4,Group.find(gname)));
		assertNotNull(Participates.find(u4,Group.find(gname)));
		
		// test undo
		CG.undo(ns);
		assertNull(Group.find(gname));
		assertNull(Participates.find(u4,Group.find(gname)));
		
	}
	
	// to test RemoveGroup class
	@Test
	void testRemoveGroup() throws Exception{
		RemoveGroup RG = new RemoveGroup();
		
		/*
		 * User 4: Kane remove Group g2 of u4
		 */
		Session s = new Session();
		s.setCurrentUser(u4);
		String[] args = new String[] {g2.getName()};
		
		// add a User u3 to Group g2
		Participates.add(u3, g2);
		GroupAppointment.add(s2, e2, "first group mtg of g2", u3,g2);
		assertNotNull(Appointment.find(s2, u3));	//appointment added
		
		// add an unregistered user u5 to Group g2
		User u5 = new User("unregistered Poter", "poter@unregister.com");	//unregistered user
		Participates.add(u5, g2);
		GroupAppointment.add(s1, e1, "movie time", u5,g2);
		assertNotNull(Appointment.find(s1, u5));	//appointment added
		
		// test execute
		assertEquals(u4,Group.find(g2.getName()).getCreator());
		assertNotNull(Participates.find(u1, g2));
		assertNotNull(Participates.find(u3, g2));
		assertNotNull(Participates.find(u5, g2)); 	//unregistered user
		Session ns = RG.execute(args, s);
		assertNull(Group.find(g2.getName()));
		
		// original participating member has no relation with Group g2
		assertNull(Participates.find(u1, g2)); 
		assertNull(Participates.find(u3, g2));
		assertNull(Participates.find(u5, g2));
		assertNull(Appointment.find(s1, u5));	//appointment deleted because it belongs to group2
		assertNull(Appointment.find(s2, u3));
		
		// test undo
		RG.undo(ns);
		assertNotNull(Group.find(g2.getName()));
		// old participating relation back
		assertNotNull(Participates.find(u1, g2));
		assertNotNull(Participates.find(u3, g2));
		assertNull(Participates.find(u5, g2));	 //unregistered user would be removed from the group2
		assertNull(Appointment.find(s1, u5));	//appointment deleted because of unregistering
		assertNotNull(Appointment.find(s2, u3));
	}
	
	// to test RetrieveInformation class
	@Test
	void testRetrieveInformation() throws Exception {
		RetrieveInformation RI = new RetrieveInformation();
		
		/*
		 * User 1: Jim RetrieveInformation
		 */
		Session s = new Session();
		s.setCurrentUser(u1);
		String[] args = new String[] {"100000"};
		// test execute
		Session ns = RI.execute(args, s);
		assertEquals("retrieve 100000 ",RI.toString());
		assertFalse(RI.getGroupSet().isEmpty());
		assertTrue(RI.getAppointmentSet().isEmpty());
		
	}
	
	// to test Undo class
	@Test
	void testUndo() throws Exception {
		Undo ud = new Undo();
		Session s = new Session();
		String[] args = new String[]{};
		// to test throw exception
		assertThrows(Exception.class, ()->{
			ud.execute(args,s);
		} );
	}
}

