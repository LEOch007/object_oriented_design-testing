package mcm;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.rmi.*;

public class LoginPage extends JPanel
{
	public MCMClient frame;
	public JTextField nameField = new JTextField(15);
	public JTextField serverField = new JTextField(15);
	public JLabel messageLabel = new JLabel();
	public JButton loginButton = new JButton("Log in");

	{
		serverField.setText("//127.0.0.1:1099/Server");
	}

	public LoginPage(MCMClient frame) {
		this.frame = frame;

			JPanel wholePanel = new JPanel();
			setLayout(new BorderLayout());
			setBorder(new BevelBorder(BevelBorder.LOWERED));

			JPanel northPanel = new JPanel();
			northPanel.add(new JLabel("WELCOME TO MULTI-USER CALENDAR MANAGER"));
			add(northPanel, BorderLayout.NORTH);


			add(wholePanel, BorderLayout.CENTER);
			wholePanel.setLayout(new BorderLayout());
			
			JPanel searchInfo = new JPanel();
			JPanel innerPanel = new JPanel();
			innerPanel.add(searchInfo);
			wholePanel.add(innerPanel);
			searchInfo.setLayout(new GridLayout(2, 2, 1, 1));

			searchInfo.add(new JLabel("Username:"));
			searchInfo.add(nameField);

			searchInfo.add(new JLabel("Server:"));
			searchInfo.add(serverField);

			loginButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									MCMInterface server = null;
									if (nameField.getText().equals("") || serverField.getText().equals("")) {
//										JOptionPane.showMessageDialog(null, "Username and server cannot be empty strings", 
//										"Input Error", JOptionPane.INFORMATION_MESSAGE);
										System.err.println("Input Error: Username and server cannot be empty strings");
										return;
									}

									try {
										server = (MCMInterface) Naming.lookup(serverField.getText());
									}
									catch (Exception ex) {
//										JOptionPane.showMessageDialog(null, ex.getMessage(), 
//										"Connection Error", JOptionPane.INFORMATION_MESSAGE);
//										ex.printStackTrace();
										System.err.println("Connection Error: " + ex.getMessage());
										return;
									}

									Session session = new Session();

									try
									{
										session = server.parseCommandLine("logon \"" + nameField.getText() + "\"", session);
									}
									catch (Exception ex) {
//										JOptionPane.showMessageDialog(null, ex.getMessage(), 
//										"Command Error", JOptionPane.INFORMATION_MESSAGE);
//										ex.printStackTrace();
										System.err.println("Command Error: " + ex.getMessage());
										return;
									}

									if (session.getCurrentUser() == null) {
//										JOptionPane.showMessageDialog(null, "User not yet registered", 
//										"Command Error", JOptionPane.INFORMATION_MESSAGE);
										System.err.println("Command Error: User not yet registered");
										return;
									}

									LoginPage.this.frame.setSession(session);
									LoginPage.this.frame.setServer(server);
									LoginPage.this.frame.showMainPage();
								}
							});
			wholePanel.add(loginButton, BorderLayout.SOUTH);
	}
}
