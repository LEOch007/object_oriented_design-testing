import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.ActionListener;

import javax.swing.*;

import org.junit.jupiter.api.Test;

import mcm.LoginPage;
import mcm.MCMClient;
import mcm.MainPage;
import mcm.User;

class SamGUITest {

	LoginPage loginpage;
	
	@Test
	void test() {
		
		// log in from loginpage
		loginpage = new LoginPage(new MCMClient());
		loginpage.nameField.setText("sam");
		loginpage.serverField.setText("//127.0.0.1:1099/Server");
		ActionListener[] listeners = loginpage.loginButton.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);}
		
		// enter into mainpage
		JMenuBar menuBar = loginpage.frame.menuBar; //Get the menu bar		
		MainPage main = loginpage.frame.mainPage;   //Get the main page	
		main.retrieveInfo();						//Invoke retrieveInfo
		
		/*
		 *  to test appointment(schedule) panel
		 */
		assertEquals(4,main.appointmentSet.length);
		
		// delete
		main.appointmentTable.changeSelection(1, 0, false, false); main.retrieveInfo();						//select
		assertEquals(1,main.appointmentTable.getSelectedRow());
		assertEquals(0,main.appointmentTable.getSelectedColumn());
		listeners = main.deleteAppointment.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//delete once
		
		main.appointmentTable.changeSelection(1, 0, false, false); main.retrieveInfo();						//select
		listeners = main.deleteAppointment.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//delete twice
		assertEquals(2,main.appointmentSet.length); 		//change to 2 appointments due to deletion
		
		// undo
		listeners = main.undoButton.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//undo once
		listeners = main.undoButton.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//undo twice
		assertEquals(4,main.appointmentSet.length);
		
		// insert
		main.appointmentTable.setValueAt("20-02-2020 10:00", 0, 0);
		main.appointmentTable.setValueAt("20-02-2020 11:00", 0, 1);
		main.appointmentTable.setValueAt("yoga", 0, 2);
		listeners = main.insertAppointment.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null); } main.retrieveInfo();	//insert
		assertEquals(5,main.appointmentSet.length);
		
		// undo
		listeners = main.undoButton.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//undo
		assertEquals(4,main.appointmentSet.length);
		
		// insert appointment with group tab
		main.appointmentTable.setValueAt("20-02-2020 10:00", 0, 0);
		main.appointmentTable.setValueAt("20-02-2020 11:00", 0, 1);
		main.appointmentTable.setValueAt("workshop lab", 0, 2);
		main.appointmentTable.setValueAt("COMP201", 0, 3);
		listeners = main.insertAppointment.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null); } main.retrieveInfo();	//insert
		assertEquals(5,main.appointmentSet.length);
		
		// undo
		listeners = main.undoButton.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//undo
		assertEquals(4,main.appointmentSet.length);
		
		/*
		 *  to test group panel
		 */
		main.showGroupPage();
		assertTrue(main.groupPane.isShowing());
		assertEquals(2,main.groupSet.length);
		
		// select
		main.groupTable.changeSelection(0,0,false,false); main.retrieveInfo();
		assertEquals(0,main.groupTable.getSelectedRow());
		assertEquals(0,main.groupTable.getSelectedColumn());
		
		// create 
		main.groupTable.setValueAt("Group 1", 0, 0);				//Input the string
		assertEquals("Group 1", main.groupTable.getValueAt(0, 0));
		listeners = main.createGroup.getActionListeners();			//Trigger the create button
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null); } main.retrieveInfo();	//create
		assertEquals(3,main.groupSet.length);
		
		// undo
		listeners = main.undoButton.getActionListeners();			//Trigger the undo button
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//undo
		assertEquals(2,main.groupSet.length);
		
		// remove 
		main.groupTable.changeSelection(1,0,false,false); main.retrieveInfo();			//select
		listeners = main.removeGroup.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//remove
		assertEquals(1,main.groupSet.length);
		
		// undo
		listeners = main.undoButton.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//undo
		assertEquals(2,main.groupSet.length);
		
		/*
		 * User Sam log off
		 */
		assertTrue(main.frame.logoffButton.isEnabled());
		listeners = main.frame.logoffButton.getActionListeners();
		for(int i=0; i<listeners.length; i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//log off
		assertEquals(main.frame.loginPage, main.frame.currentPanel);
	}
}
