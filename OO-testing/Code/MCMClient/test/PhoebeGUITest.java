import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import mcm.LoginPage;
import mcm.MCMClient;
import mcm.MainPage;
import mcm.Session;
import mcm.User;

import org.junit.jupiter.api.Test;

public class PhoebeGUITest {
	
	LoginPage page;

	@Test
	public void test() {
		/*
		 * Mock log on action
		 * */
		
		//create a new LoginPage
		page = new LoginPage(new MCMClient());
		
		//Set text of a text field
		page.nameField.setText("phoebe");
		
		//Get the action listeners of a button and perform action
		ActionListener[] listeners = page.loginButton.getActionListeners();
		for(int i = 0; i < listeners.length; i++) {
			listeners[i].actionPerformed(null);
		}
		
		//Assert session and session's current user is not null after log in
		assertNotNull(page.frame.session);
		assertNotNull(page.frame.session.getCurrentUser());
		
		//Assert the logged in user name is the user name we used, "phoebe"
		String currentUserName = page.frame.session.getCurrentUser().getName();
		assertTrue(currentUserName.equals("phoebe"));
		
		//Assert true that after log in, the logoff button is enabled and current panel is the main page
		assertTrue(page.frame.logoffButton.isEnabled());
		assertEquals(page.frame.mainPage, page.frame.currentPanel);
		
		//Get the menu bar
		JMenuBar menuBar = page.frame.menuBar;
		//Get the main page
		MainPage main = page.frame.mainPage;
		//Invoke retrieveInfo
		main.retrieveInfo();
		
		/*
		 *  to test group panel
		 */
		main.frame.groupBox.getModel().setSelected(true);
		assertTrue(main.groupPane.isShowing());
		assertEquals(2,main.groupSet.length);
		
		//join
		main.groupTable.changeSelection(1, 0, false, false); main.retrieveInfo();						//select
		assertEquals("NO",main.groupTable.getValueAt(1, 2));
		listeners = main.joinGroup.getActionListeners();
		for(int i=0;i<listeners.length;i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//join
		assertEquals("YES",main.groupTable.getValueAt(1, 2));
		
		//undo
		menuBar.getMenu(1).getItem(0).getAction().actionPerformed(null);main.retrieveInfo();
		assertEquals("NO",main.groupTable.getValueAt(1, 2));
		
		//withdraw
		main.groupTable.changeSelection(2, 0, false, false); main.retrieveInfo();						//select
		assertEquals("YES",main.groupTable.getValueAt(2, 2));
		listeners = main.withdrawGroup.getActionListeners();
		for(int i=0;i<listeners.length;i++) { listeners[i].actionPerformed(null);} main.retrieveInfo();	//join
		assertEquals("NO",main.groupTable.getValueAt(2, 2));
		
		//undo
		menuBar.getMenu(1).getItem(0).getAction().actionPerformed(null);main.retrieveInfo();
		assertEquals("YES",main.groupTable.getValueAt(2, 2));
		
		//Get the first menu in the menu bar
		JMenu menu = menuBar.getMenu(0);
		//Get menu item of position 0 (log off) and perform action
		menu.getItem(0).getAction().actionPerformed(null);
		assertEquals(main.frame.loginPage, main.frame.currentPanel);
		
	}
	

}
