package mcm;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import java.util.*;
import java.text.*;

public class MainPage extends JPanel
{
	public MCMClient frame;

	public Object[] groupSet;
	public Object[] participatesSet;
	public Object[] appointmentSet;

	public JTable groupTable = new JTable();
	public JTable appointmentTable = new JTable();

	public String startTimeEdit = "";
	public String endTimeEdit = "";
	public String descriptionEdit = "";
	public String groupEdit = "";
	public String groupNameEdit = "";

	public JScrollPane groupPane = new JScrollPane(groupTable);
	public JScrollPane appointmentPane = new JScrollPane(appointmentTable);
	public JPanel groupUpperPane = new JPanel();
	public JPanel appointmentUpperPane = new JPanel();

	public JPanel currentPanel;

	public Session session;
	public Session result;
	public MCMInterface server;

	public javax.swing.Timer timer;
	public long timestamp = 0;

	public JButton createGroup = new JButton("Create");
	public JButton removeGroup = new JButton("Remove");
	public JButton joinGroup = new JButton("Join");
	public JButton withdrawGroup = new JButton("Withdraw");
	public JButton insertAppointment = new JButton("Insert");
	public JButton deleteAppointment = new JButton("Delete");

	public AbstractAction undoAction;
	public JButton undoButton;

	//public JList historyList;

	public MainPage(MCMClient frame, final JCheckBox groupBox, final JCheckBox appointmentBox, AbstractAction undoAction,
					JButton undoButton) {
		this.frame = frame;

		this.undoAction = undoAction;
		this.undoButton = undoButton;

		setLayout(new BorderLayout());
		groupBox.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent e) {
									if (groupBox.getModel().isSelected()) {
										showGroupPage();
									}
									updateMainPanel();
							}
						});

		appointmentBox.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent e) {
									if (appointmentBox.getModel().isSelected()) {
										showAppointmentPage();
									}
									updateMainPanel();
							}
						});	

		groupTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		groupTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
								public void valueChanged(ListSelectionEvent e) {
									int index = groupTable.getSelectionModel().getMinSelectionIndex();
									if (index != -1) {
										if (index == 0) {
											removeGroup.setEnabled(false);
											joinGroup.setEnabled(false);
											withdrawGroup.setEnabled(false);
										} else {
											TableModel model = groupTable.getModel();
											if (model.getValueAt(index, 1).equals("YES"))
												removeGroup.setEnabled(true);
											else
												removeGroup.setEnabled(false);

											if (model.getValueAt(index, 2).equals("YES")) {
												if (!model.getValueAt(index, 1).equals("YES"))
													withdrawGroup.setEnabled(true);
												else
													withdrawGroup.setEnabled(false);
												joinGroup.setEnabled(false);
											} else {
												withdrawGroup.setEnabled(false);
												joinGroup.setEnabled(true);
											}
										}
									}
								}
						});
		appointmentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
								public void valueChanged(ListSelectionEvent e) {
									int index = appointmentTable.getSelectionModel().getMinSelectionIndex();
									if (index != -1) {
		//System.out.println(index);
										if (index == 0) {
											if (!(startTimeEdit.equals("")) && !(endTimeEdit.equals("")) && !(descriptionEdit.equals(""))) {
												deleteAppointment.setEnabled(false);
											} else {
												deleteAppointment.setEnabled(false);
											}
										} else {
											TableModel model = appointmentTable.getModel();
											if (model.getValueAt(index, 4).equals("YES"))
												deleteAppointment.setEnabled(true);
											else
												deleteAppointment.setEnabled(false);
										}
									}
								}
						});

		setGroupUpperPane();
		setAppointmentUpperPane();
		setListeners();
	}

	public void setListeners() {
		createGroup.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									try {
										if (groupNameEdit.equals(""))
											throw new Exception("Group name cannot be empty");
										result = server.parseCommandLine("create \"" + groupNameEdit + "\"", session);
										groupNameEdit = "";
										prepareUndo();
									}
									catch (Exception ex) {
//										JOptionPane.showMessageDialog(null, ex.getMessage(), 
//										"Command Error", JOptionPane.INFORMATION_MESSAGE);
										System.err.println("Command Error: " + ex.getMessage());
										return;
									}
								}
			});

		removeGroup.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									try {
										int index = groupTable.getSelectionModel().getMinSelectionIndex();
										if (index == -1)
											return;
										Object o = groupTable.getModel().getValueAt(index, 0);
										result = server.parseCommandLine("remove \"" + o + "\"", session);
										prepareUndo();
									}
									catch (Exception ex) {
//										JOptionPane.showMessageDialog(null, ex.getMessage(), 
//										"Command Error", JOptionPane.INFORMATION_MESSAGE);
										System.err.println("Command Error: " + ex.getMessage());
										return;
									}
								}
			});

		joinGroup.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									try {
										int index = groupTable.getSelectionModel().getMinSelectionIndex();
										if (index == -1)
											return;

										Object o = groupTable.getModel().getValueAt(index, 0);
										result = server.parseCommandLine("join \"" + o + "\"", session);
										prepareUndo();
									}
									catch (Exception ex) {
//										JOptionPane.showMessageDialog(null, ex.getMessage(), 
//										"Command Error", JOptionPane.INFORMATION_MESSAGE);
										System.err.println("Command Error: " + ex.getMessage());
										return;
									}
								}
			});

		withdrawGroup.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									try {
										int index = groupTable.getSelectionModel().getMinSelectionIndex();
										if (index == -1)
											return;

										Object o = groupTable.getModel().getValueAt(index, 0);
										result = server.parseCommandLine("withdraw \"" + o + "\"", session);
										prepareUndo();
									}
									catch (Exception ex) {
//										JOptionPane.showMessageDialog(null, ex.getMessage(), 
//										"Command Error", JOptionPane.INFORMATION_MESSAGE);
										System.err.println("Command Error: " + ex.getMessage());
										return;
									}
								}
			});

		insertAppointment.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									try {
										if (startTimeEdit.equals("") || endTimeEdit.equals("") || descriptionEdit.equals(""))
											throw new Exception("Start time, end time and description cannot be empty");
										if (groupEdit.equals(""))
											result = server.parseCommandLine("insert \"" + startTimeEdit + "\" \"" + 
																endTimeEdit + "\" \"" + 
																descriptionEdit + "\"", session);
										else 
											result = server.parseCommandLine("insert \"" + startTimeEdit + "\" \"" + 
																endTimeEdit + "\" \"" + 
																descriptionEdit + "\" \"" + groupEdit + "\"", session);
										startTimeEdit = endTimeEdit = descriptionEdit = groupEdit = "";
										prepareUndo();
									}
									catch (Exception ex) {
//										JOptionPane.showMessageDialog(null, ex.getMessage(), 
//										"Command Error", JOptionPane.INFORMATION_MESSAGE);
										System.err.println("Command Error: " + ex.getMessage());
										return;
									}
								}
			});

		deleteAppointment.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									try {
										int index = appointmentTable.getSelectionModel().getMinSelectionIndex();
										if (index == -1)
											return;

										Object o = appointmentTable.getModel().getValueAt(index, 0);
										result = server.parseCommandLine("delete \"" + o + "\"", session);
										prepareUndo();
									}
									catch (Exception ex) {
//										JOptionPane.showMessageDialog(null, ex.getMessage(), 
//										"Command Error", JOptionPane.INFORMATION_MESSAGE);
										System.err.println("Command Error: " + ex.getMessage());
										return;
									}
								}
			});
	}

	public void setGroupUpperPane() {
		groupUpperPane.setLayout(new FlowLayout(FlowLayout.LEFT));

		JToolBar createToolBar = new JToolBar();
		createToolBar.add(createGroup);
		groupUpperPane.add(createToolBar);

		JToolBar removeToolBar = new JToolBar();
		removeToolBar.add(removeGroup);
		groupUpperPane.add(removeToolBar);

		JToolBar joinToolBar = new JToolBar();
		joinToolBar.add(joinGroup);
		groupUpperPane.add(joinToolBar);

		JToolBar withdrawToolBar = new JToolBar();
		withdrawToolBar.add(withdrawGroup);
		groupUpperPane.add(withdrawToolBar);
	}

	public void setAppointmentUpperPane() {
		appointmentUpperPane.setLayout(new FlowLayout(FlowLayout.LEFT));

		JToolBar insertToolBar = new JToolBar();
		insertToolBar.add(insertAppointment);
		appointmentUpperPane.add(insertToolBar);

		JToolBar deleteToolBar = new JToolBar();
		deleteToolBar.add(deleteAppointment);
		appointmentUpperPane.add(deleteToolBar);
	}

	public void updateMainPanel() {
		validate();
		repaint();
	}

	public void showGroupPage() {
		remove(appointmentPane);
		remove(appointmentUpperPane);
		add(groupUpperPane, BorderLayout.NORTH);
		add(groupPane, BorderLayout.CENTER);
	}

	public void showAppointmentPage() {
		remove(groupPane);
		remove(groupUpperPane);
		add(appointmentUpperPane, BorderLayout.NORTH);
		add(appointmentPane, BorderLayout.CENTER);
	}

	public void restart() {
		startTimeEdit = endTimeEdit = descriptionEdit = groupEdit = groupNameEdit = "";
		createGroup.setEnabled(true);
		removeGroup.setEnabled(false);
		joinGroup.setEnabled(false);
		withdrawGroup.setEnabled(false);
		insertAppointment.setEnabled(true);
		deleteAppointment.setEnabled(false);
		timestamp = 0;
		session = frame.getSession();
		server = frame.getServer();
		retrieveInfo();
		timer = new javax.swing.Timer(1000, new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											retrieveInfo();
										}
								});
		timer.start();
	}

	public void retrieveInfo() {
		try {
			Session s = new Session();
			s.setCurrentUser(session.getCurrentUser());

			Session s1 = server.parseCommandLine("retrieve " + timestamp, s);	
			if (s1.getCurrentUser() == null)
				throw new Exception();

			RetrieveInformation r = (RetrieveInformation) s1.getCurrentCommand();

			if (r.getTimestamp() == timestamp) {
				return;
			} else {
				timestamp = r.getTimestamp();
			}
												
			groupSet = r.getGroupSet().toArray();
			Arrays.sort(groupSet, new Comparator() {
					public int compare(Object o1, Object o2) {
						Group g1 = (Group) o1;
						Group g2 = (Group) o2;
						return g1.getName().compareTo(g2.getName());
					}
				});
			participatesSet = r.getParticipatesSet().toArray();
			appointmentSet = r.getAppointmentSet().toArray();
			Arrays.sort(appointmentSet, new Comparator() {
				public int compare(Object o1, Object o2) {
					Appointment a1 = (Appointment) o1;
					Appointment a2 = (Appointment) o2;
					int userNameCompare = a1.getInitiator().getName().compareTo(a2.getInitiator().getName());
					int startTimeCompare = a1.getStartTime().compareTo(a2.getStartTime());
					if (userNameCompare < 0 || (userNameCompare == 0 && startTimeCompare < 0)) {
						return -1;
					} else if (userNameCompare == 0 && startTimeCompare == 0) {
						return 0;
					} else {
						return 1;
					}
				}

				public boolean equals(Object o) {
					return this.equals(o);
				}
			});
			updateTable();
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "User unregistered at server OR server down", 
			"Command Error", JOptionPane.INFORMATION_MESSAGE);
			ex.printStackTrace();
			System.err.println("User unregistered at server OR server down");
			frame.showLoginPage();
		}
	}

	public void updateTable() {
		groupTable.setModel(new AbstractTableModel() {
				String[] headers = {"Name", "Creator", "Member"};
				public int getRowCount() { return groupSet.length+1; }
				public int getColumnCount() { return headers.length; }
				public Object getValueAt(int r, int c) {
					if (r == 0) {
						switch(c) {
							case 0: return groupNameEdit;
						}
						return "";
					}

					switch (c) {
						case 0: return ((Group) groupSet[r-1]).getName();
						case 1: return ((Group) groupSet[r-1]).getCreator().equals(session.getCurrentUser()) ? "YES" : "NO";
						case 2: 
							for (int i=0; i<participatesSet.length; i++) {
								Participates p = (Participates) participatesSet[i];
								Group g = (Group) groupSet[r-1];
								User u = session.getCurrentUser();
								if (p.getUser().equals(u) && p.getGroup().equals(g)) {
									return "YES";
								}
							}
							return "NO";
					}
					return "";
				}
				public void setValueAt(Object value, int row, int col) {
					groupNameEdit = (String) value;
				}
				public String getColumnName(int c) { return headers[c]; }
				public boolean isCellEditable(int r, int c) {  
					if (r == 0 && c == 0)
						return true;
					else 
						return false;
				}
			});

		appointmentTable.setModel(new AbstractTableModel() {
				String[] headers = {"Start time", "End time", "Description", "Group", "Initiator"};
				public int getRowCount() { return appointmentSet.length+1; }
				public int getColumnCount() { return headers.length; }
				public Object getValueAt(int r, int c) {
					if (r == 0) {
						switch(c) {
							case 0: return startTimeEdit;
							case 1: return endTimeEdit;
							case 2: return descriptionEdit;
							case 3: return groupEdit;
						}
						return "N/A";
					}
		
					Appointment a = (Appointment) appointmentSet[r-1];
					String startTime = new SimpleDateFormat(Session.TimeFormat).format(a.getStartTime());
					String endTime = new SimpleDateFormat(Session.TimeFormat).format(a.getEndTime());

					switch (c) {
						case 0: return startTime;
						case 1: return endTime;
						case 2: return a.getDescription();
						case 3: return a instanceof GroupAppointment? ((GroupAppointment)a).getGroup().getName() : "N/A";
						case 4: return a.getInitiator().equals(session.getCurrentUser())? "YES": "NO";
					}
					return "";
				}
				public void setValueAt(Object value, int row, int col) {
					switch (col) {
						case 0: startTimeEdit = (String) value; break;
						case 1: endTimeEdit = (String) value; break;
						case 2: descriptionEdit = (String) value; break;
						case 3: groupEdit = (String) value;
					}
				}
				public String getColumnName(int c) { return headers[c]; }
				public boolean isCellEditable(int r, int c) {  
					if (r == 0 && c != 4)
						return true;
					else 
						return false;
				}
			});
	}

	public void stop() {
		if (timer != null) {
			timer.stop();
		}
		if (session != null) {
			session.clearAll();
		}
	}

	public void undo() {
		try
		{
			server.parseCommandLine("undo", session);
		}
		catch (Exception e)
		{
//			JOptionPane.showMessageDialog(null, e.getMessage(), 
//								"Command Error", JOptionPane.INFORMATION_MESSAGE);
			System.err.println("Command Error: " + e.getMessage());
								return;
		}
		
		session.pop();

		if (session.getCurrentCommand() == null) {
			undoButton.setEnabled(false);
			undoAction.setEnabled(false);
		}

/*		if (historyList != null) {
			historyList.setListData(session.getCommandHistory().toArray());
		}
*/	}

	public void prepareUndo() {

		Command command = result.getCurrentCommand();
		session.addCommand(command);
		if (session.getCurrentCommand() != null) {
			undoButton.setEnabled(true);
			undoAction.setEnabled(true);
		}

		createGroup.setEnabled(true);
		removeGroup.setEnabled(false);
		joinGroup.setEnabled(false);
		withdrawGroup.setEnabled(false);
		insertAppointment.setEnabled(true);
		deleteAppointment.setEnabled(false);
		
/*		if (historyList != null) {
			historyList.setListData(session.getCommandHistory().toArray());
		}
*/	}
/*
	public void lookAfter(JList historyList) {
		this.historyList = historyList;
	}*/
}
