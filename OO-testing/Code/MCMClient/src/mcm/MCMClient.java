package mcm;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class MCMClient extends JFrame
{
	public MCMInterface server;
	public Session session;

	public Container container = getContentPane();
	public JMenuBar menuBar = new JMenuBar();
	public JPanel toolBar = new JPanel();
	public JPanel currentPanel;

	public LoginPage loginPage;
	public MainPage mainPage; 
//	public HistoryPage historyPage;
//	public JPanel blank = new JPanel();

	public AbstractAction logoffAction;
	public AbstractAction undoAction;
	public JCheckBox groupBox = new JCheckBox("Group");
	public JCheckBox appointmentBox = new JCheckBox("Schedule");
//	public JCheckBox historyBox = new JCheckBox("History");
	public JButton logoffButton = new JButton("Logoff");
	public JButton undoButton = new JButton("Undo");

	public MCMClient() {
		super("Multi-User Calendar Manager");

		setMenu();
		setToolBar();
		setListeners();
		initializeMainPanel();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void setMenu() {
		JMenu fileMenu = new JMenu("File");
		logoffAction = new AbstractAction("Logoff") {
				public void actionPerformed(ActionEvent e) {
					showLoginPage();
				}
			};
		logoffAction.setEnabled(false);
		fileMenu.add(logoffAction);
		fileMenu.addSeparator();
		fileMenu.add(new AbstractAction("Exit") {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Exit!");
//					System.exit(0);
				}
			});
		menuBar.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");
		undoAction = new AbstractAction("Undo") {
				public void actionPerformed(ActionEvent e) {
					mainPage.undo();
				}
			};
		undoAction.setEnabled(false);
		editMenu.add(undoAction);
		menuBar.add(editMenu);
/*
		JMenu lookAndFeelMenu = new JMenu("Look and Feel");
		lookAndFeelMenu.add(new AbstractAction("Metal", null) {
					public void actionPerformed(ActionEvent e) {
						try {
							UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
							SwingUtilities.updateComponentTreeUI(MCMClient.this);
							if (currentPanel instanceof LoginPage)
								pack();						}
						catch (Exception ex) { }
					}
				});
		lookAndFeelMenu.add(new AbstractAction("Motif", null) {
					public void actionPerformed(ActionEvent e) {
						try {
							UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
							SwingUtilities.updateComponentTreeUI(MCMClient.this);
							if (currentPanel instanceof LoginPage)
								pack();
						}
						catch (Exception ex) { }
					}
				});
		lookAndFeelMenu.add(new AbstractAction("Windows", null) {
					public void actionPerformed(ActionEvent e) {
						try {
							UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
							SwingUtilities.updateComponentTreeUI(MCMClient.this);
							if (currentPanel instanceof LoginPage)
								pack();
						}
						catch (Exception ex) { }
					}
				});
		menuBar.add(lookAndFeelMenu);
*/
		setJMenuBar(menuBar);		
	}

	public void setToolBar() {
		container.add(toolBar, BorderLayout.NORTH);
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));

		JToolBar logoffToolBar = new JToolBar();
		logoffToolBar.add(logoffButton);
		toolBar.add(logoffToolBar);
		logoffButton.setEnabled(false);

		JToolBar undoToolBar = new JToolBar();
		undoToolBar.add(undoButton);
		toolBar.add(undoToolBar);
		undoButton.setEnabled(false);

		JToolBar commandToolBar = new JToolBar();
		commandToolBar.add(appointmentBox);
		commandToolBar.add(groupBox);
		//appointmentBox.setSelected(true);
		ButtonGroup commandGroup = new ButtonGroup();
		commandGroup.add(appointmentBox);
		commandGroup.add(groupBox);
		toolBar.add(commandToolBar);
		appointmentBox.setEnabled(false);
		groupBox.setEnabled(false);
/*
		JToolBar historyToolBar = new JToolBar();
		historyToolBar.add(historyBox);
		toolBar.add(historyToolBar);
		historyBox.setEnabled(false);
*/	}

	public void setListeners() {
/*		historyBox.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent e) {
									if (historyBox.getModel().isSelected()) {
										container.remove(blank);
										container.add(historyPage, BorderLayout.EAST);
									} else {
										container.remove(historyBox);
										container.add(blank, BorderLayout.EAST);
									}
									updateMainPanel();
							}
						});
*/
		logoffButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								showLoginPage();
							}
						});

		undoButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								mainPage.undo();
							}
						});
	}

	public void initializeMainPanel() {
		loginPage = new LoginPage(this);
		mainPage = new MainPage(this, groupBox, appointmentBox, undoAction, undoButton);
//		historyPage = new HistoryPage(this, mainPage);

		showLoginPage();
	}

	public void updateMainPanel() {
		container.validate();
		container.repaint();
	}

	public void showLoginPage() {
		mainPage.stop();
		currentPanel = loginPage;
		logoffAction.setEnabled(false);
		undoAction.setEnabled(false);
		groupBox.setEnabled(false);
		groupBox.setSelected(false);
		appointmentBox.setEnabled(false);
		appointmentBox.setSelected(false);
//		historyBox.setEnabled(false);
//		historyBox.setSelected(false);
		logoffButton.setEnabled(false);
		undoButton.setEnabled(false);
		container.remove(mainPage);
		container.add(loginPage, BorderLayout.CENTER);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int screenHeight = d.height;
		int screenWidth = d.width;
		setSize(screenWidth/2, screenHeight/2);
		setLocation(screenWidth/4, screenHeight/4);
		pack();
//		updateMainPanel();
	}

	public void showMainPage() {
		mainPage.restart();
		currentPanel = mainPage;
		logoffAction.setEnabled(true);
		groupBox.setEnabled(true);
		appointmentBox.setEnabled(true);
		appointmentBox.setSelected(true);
//		historyBox.setEnabled(true);
		logoffButton.setEnabled(true);
		container.remove(loginPage);
		container.add(mainPage, BorderLayout.CENTER);
		setExtendedState(MAXIMIZED_BOTH);
		updateMainPanel();
	}

	public void setServer(MCMInterface server) {
		this.server = server;
	}

	public MCMInterface getServer() {
		return server;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public static void main(String[] args) {
		new MCMClient();
	}
}