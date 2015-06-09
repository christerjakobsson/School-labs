import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultCaret;

public class ChatGUI extends Thread {

	private JFrame frame;
	private Container pane;
	private JPanel messageLogPanel;
	private JPanel usersPanel;
	private JPanel headerPanel;
	private JPanel inputPanel;
	private JPanel topicPanel;
	private JTextPane textTopic;
	private JTextArea textLog;
	private JTextArea textUsers;
	private JTextField textIPField;
	private JTextField textPortField;
	private JTextField textInputField;
	private JTextField textEncryptField;
	private JTextField textNickField;
	private JTextField textTopicField;
	private JTextField textWhoIsField;
	private DefaultListModel listModel;
	private JList serverList;
	private JCheckBox compressBox;
	private JCheckBox encryptBox;
	private JButton sendButton;
	private JButton serverConnectButton;
	private JDialog jdServer;
	private JDialog jdNick;
	private JDialog jdEncrypt;
	private JDialog jdCompress;
	private JDialog jdTopic;
	private JDialog jdWhoIs;
	private JMenuItem whoActions;
	private JMenuItem topicActions;
	private Client client;
	private Packager pk = new Packager(client);

	/**
	 * Creates the GUI with the client as input. The gui can get the public
	 * methods in the client. in the constructor methods that paints the
	 * interface is made and the run thread is started.
	 */
	public ChatGUI(Client c) {
		client = c;
		setName("ChatGui");
		// Creates frame
		frame = new JFrame("ChriJon Client (logged in as: "
				+ client.getNickName() + ")");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setSize(600, 500);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane
						.showConfirmDialog(frame, "Shutdown ChriJon Client?",
								"Exit", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					try {
						client.saveSettings();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					client.shutDown();
				}
			}
		});
		pane = frame.getContentPane();
		createChatComponents();
		setupChatGUI();
		systemOutToConsole();
		jdServer.setVisible(true);
		jdNick.setVisible(true);

		// Starts the run
		this.start();
	}

	/**
	 * appentToLog adds text to the conversationwindow
	 */
	private void appendToLog() {
		if (textInputField.getText().length() <= 65535) {
			client.sendMessage(textInputField.getText(),
					client.currentMessageSettings(), client.getEncryptionKey());
		} else {
			System.out.print("Too much text! Max 65535 characters");
		}
		textInputField.setText("");
	}

	/**
	 * changedtext tests if the text in the textinputfield has changed and
	 * enables/disables Send button
	 *
	 */
	private void changedText() {
		if (textInputField.getText().equals("")) {
			sendButton.setEnabled(false);
		} else {
			sendButton.setEnabled(true);
		}
	}

	/**
	 * createChatComponens creates the graphical components as buttons, dialog
	 * windows, panels and menus.
	 *
	 */
	private void createChatComponents() {
		// Skapar konsolen
		textLog = new JTextArea();
		DefaultCaret caret = (DefaultCaret) textLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textLog.setBackground(new Color(248, 251, 253));
		textLog.setEditable(false);
		textLog.setFont(new Font("Courier", Font.PLAIN, 12));
		textLog.setLineWrap(true);
		textLog.setWrapStyleWord(true);
		// Skroll till textarean i konsolen
		JScrollPane areaScrollPane = new JScrollPane(textLog);
		areaScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setPreferredSize(new Dimension(350, 550));
		areaScrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						textLog.select(textLog.getHeight() + 300000, 0);
					}
				});

		// Skapar userlistan
		textUsers = new JTextArea();
		DefaultCaret caret2 = (DefaultCaret) textUsers.getCaret();
		caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textUsers.setEditable(false);
		textUsers.setFont(new Font("Courier", Font.PLAIN, 12));
		textUsers.setBackground(new Color(248, 251, 253));

		// Skroll till textarean i userlistan
		JScrollPane areaScrollPane2 = new JScrollPane(textUsers);
		areaScrollPane2
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane2.setPreferredSize(new Dimension(150, 550));
		areaScrollPane2.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						textUsers.select(textUsers.getHeight() + 300000, 0);
					}
				});

		// Skapar textinmatningsfältet som testar för förändringar
		textInputField = new JTextField();
		textInputField.setAutoscrolls(true);
		textInputField.setFont(new Font("Courier", Font.PLAIN, 12));
		textInputField.setFocusable(true);
		textInputField.getDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void changedUpdate(DocumentEvent e) {
						changedText();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						changedText();
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						changedText();
					}
				});
		textInputField.setFocusable(true);
		textInputField.getDocument().addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						changedText();
					}

					public void insertUpdate(DocumentEvent e) {
						changedText();
					}

					public void removeUpdate(DocumentEvent e) {
						changedText();
					}
				});
		textInputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (sendButton.isEnabled()) {
					appendToLog();
				}
			}
		});

		// Skapar sendknappen
		sendButton = new JButton("Send");
		sendButton.setPreferredSize(new Dimension(150, 46));
		sendButton.setEnabled(false);
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appendToLog();
			}
		});

		// Menyn

		// Skapar verktygsf�ltet
		JMenuBar menuBar = new JMenuBar();

		// Filemenyn
		JMenu dropFile = new JMenu("File");
		dropFile.setMnemonic(KeyEvent.VK_F);

		// File-> Save log
		JMenuItem saveFile = new JMenuItem("Save log");
		saveFile.setMnemonic(KeyEvent.VK_L);
		saveFile.setToolTipText("Export messagelog to a txt-file");
		saveFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					FileWriter fw;
					try {
						fw = new FileWriter(fileChooser.getSelectedFile());
						fw.write(textLog.getText());
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		dropFile.add(saveFile);

		// File-> Clear log
		JMenuItem clearFile = new JMenuItem("Clear log");
		clearFile.setMnemonic(KeyEvent.VK_C);
		clearFile.setToolTipText("Clear messagelog");
		clearFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				textLog.setText("");
			}
		});
		dropFile.add(clearFile);

		// File-> Exit
		JMenuItem exitFile = new JMenuItem("Exit");
		exitFile.setMnemonic(KeyEvent.VK_E);
		exitFile.setToolTipText("Exit application");
		exitFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (JOptionPane
						.showConfirmDialog(frame, "Shutdown ChriJon Client?",
								"Exit", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					try {
						client.saveSettings();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					client.shutDown();
				}
			}
		});
		dropFile.add(exitFile);
		menuBar.add(dropFile);

		// Actions-menyn
		JMenu dropActions = new JMenu("Actions");
		dropActions.setMnemonic(KeyEvent.VK_A);

		// Actions-> Select chat server
		JMenuItem serversActions = new JMenuItem("Select chat server");
		serversActions.setMnemonic(KeyEvent.VK_S);
		serversActions.setToolTipText("Choose chat server");
		serversActions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdServer.setVisible(true);
			}
		});
		dropActions.add(serversActions);

		// Actions-> Change topic
		topicActions = new JMenuItem("Change topic");
		topicActions.setMnemonic(KeyEvent.VK_W);
		topicActions.setToolTipText("Set topic for discussion");
		topicActions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdTopic.setVisible(true);
			}
		});
		topicActions.setEnabled(false);
		dropActions.add(topicActions);
		menuBar.add(dropActions);

		// Actions-> Who is
		whoActions = new JMenuItem("Who is...");
		whoActions.setMnemonic(KeyEvent.VK_W);
		whoActions.setToolTipText("Get IP of users");
		whoActions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdWhoIs.setVisible(true);
			}
		});
		whoActions.setEnabled(false);
		dropActions.add(whoActions);
		menuBar.add(dropActions);

		// Actions-> Leave chat server
		final JMenuItem leaveActions = new JMenuItem("Leave chat server");
		leaveActions.setMnemonic(KeyEvent.VK_L);
		leaveActions.setToolTipText("Stop chatting on server");
		leaveActions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (JOptionPane.showConfirmDialog(frame, "Leave chat server?",
						"Exit", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					textUsers.setText("");
					client.setKeepGoing(false);
					client.stopConnection();
					topicActions.setEnabled(false);
					whoActions.setEnabled(false);
					leaveActions.setEnabled(false);
					textTopic.setText("");
					textUsers.setText("");
					System.out.print("You left the server");
					client.getUsersList().clear();
				}
			}
		});
		leaveActions.setEnabled(false);
		dropActions.add(leaveActions);
		menuBar.add(dropActions);

		// Optionsmenyn
		JMenu dropOptions = new JMenu("Options");
		dropOptions.setMnemonic(KeyEvent.VK_O);
		// Options-> Set nickname
		JMenuItem nickOptions = new JMenuItem("Set nickname");
		nickOptions.setMnemonic(KeyEvent.VK_N);
		nickOptions.setToolTipText("Choose name for user");
		nickOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdNick.setVisible(true);
			}
		});
		dropOptions.add(nickOptions);
		// Options-> Set encryption key
		JMenuItem encryptOptions = new JMenuItem("Set encryption on/off");
		encryptOptions.setMnemonic(KeyEvent.VK_E);
		encryptOptions
				.setToolTipText("Choose to encrypt messages with a secret key");
		encryptOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdEncrypt.setVisible(true);
			}
		});
		dropOptions.add(encryptOptions);
		// Options-> Set compression
		JMenuItem compressOptions = new JMenuItem("Set compression on/off");
		compressOptions.setMnemonic(KeyEvent.VK_C);
		compressOptions
				.setToolTipText("Choose if messages should be compressed");
		compressOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdCompress.setVisible(true);
			}
		});
		dropOptions.add(compressOptions);

		// Options->Invert terminal
		JMenuItem invertFile = new JMenuItem("Invert terminal");
		invertFile.setMnemonic(KeyEvent.VK_I);
		invertFile.setToolTipText("Switch terminal colors");
		invertFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (Color.WHITE.equals(textLog.getBackground())) {
					textLog.setForeground(Color.white);
					textLog.setBackground(Color.black);
					textUsers.setForeground(Color.white);
					textUsers.setBackground(Color.black);
					textInputField.setForeground(Color.white);
					textInputField.setBackground(Color.black);
				} else {
					textLog.setForeground(Color.black);
					textLog.setBackground(Color.white);
					textUsers.setForeground(Color.black);
					textUsers.setBackground(Color.white);
					textInputField.setForeground(Color.black);
					textInputField.setBackground(Color.white);
				}
			}
		});
		dropOptions.add(invertFile);

		menuBar.add(dropOptions);

		// Dialoger

		// Skapar dns/chat server-dialog
		JPanel pan = new JPanel();
		jdServer = new JDialog(frame, "Select chat server");
		pan.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		pan.add(new JLabel("DNS: "), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		pan.add(textIPField = new JTextField(client.getDNS()));
		textIPField.setPreferredSize(new Dimension(100, 24));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		pan.add(new JLabel("Port: "), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 0;
		pan.add(textPortField = new JTextField(client.getDNSPort()));
		textPortField.setPreferredSize(new Dimension(50, 24));
		listModel = new DefaultListModel();
		listModel.addElement("Empty");
		serverList = new JList(listModel);
		if (!listModel.isEmpty()) {
			serverList.setSelectedIndex(0);
		}
		// Skroll till listan
		JScrollPane serverScrollPane = new JScrollPane(serverList);
		serverScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		serverScrollPane.setPreferredSize(new Dimension(200, 200));
		JButton dnsRefreshButton = new JButton("Refresh");
		serverConnectButton = new JButton("Connect");
		serverConnectButton.setEnabled(false);
		// Refreshknappen
		dnsRefreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				client.setDNS(textIPField.getText());
				client.setDNSPort(textPortField.getText());
				serverConnectButton.setEnabled(false);
				listModel.removeAllElements();
				try {
					client.retrieveServerList();
					for (int i = 0; i < client.getServers().size(); i++) {
						listModel.addElement(i + 1 + "."
								+ client.getServers().get(i).getTopic() + " ("
								+ client.getServers().get(i).getNrOfClients()
								+ " users)");
					}
					if (client.getServers().size() == 0) {
						listModel.addElement("No chat servers on this DNS");
						serverConnectButton.setEnabled(false);
					} else {
						serverList.setSelectedIndex(0);
						serverConnectButton.setEnabled(true);
					}
				} catch (IOException e) {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(jdServer,
							"Connection timed out", "Message",
							JOptionPane.WARNING_MESSAGE);
					e.printStackTrace();
				}
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 4;
		c.gridy = 0;
		pan.add(dnsRefreshButton, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy = 1;
		pan.add(new JLabel("Available chat servers: "), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy = 2;
		pan.add(serverScrollPane, c);
		// Connectknappen
		serverConnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					client.connectToServer(
							client.getServers()
									.get(serverList.getSelectedIndex()).getIp(),
							client.getServers()
									.get(serverList.getSelectedIndex())
									.getPort());
					topicActions.setEnabled(true);
					whoActions.setEnabled(true);
					leaveActions.setEnabled(true);
					textLog.setText("");
					textUsers.setText("");
					textTopic.setText("Topic: "
							+ client.getServers()
									.get(serverList.getSelectedIndex())
									.getTopic());
					client.setTopic(client.getServers()
							.get(serverList.getSelectedIndex()).getTopic());
				} catch (IOException e) {
					System.out.print("Connection failed");
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(jdServer,
							"Can't connect to server!", "Message",
							JOptionPane.WARNING_MESSAGE);
					e.printStackTrace();
				}
				jdServer.setVisible(false);
			}
		});
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		pan.add(serverConnectButton, c);
		JButton dnsCancelButton = new JButton("Cancel");
		dnsCancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdServer.setVisible(false);
			}
		});
		c.gridwidth = 2;
		c.gridx = 4;
		c.gridy = 3;
		pan.add(dnsCancelButton, c);
		jdServer.setBounds(100, 200, 360, 336);
		jdServer.add(pan);
		jdServer.setAlwaysOnTop(true);
		jdServer.setResizable(false);
		jdServer.setLocationRelativeTo(null);

		// Skapar nickdialog
		JPanel pan2 = new JPanel();
		jdNick = new JDialog(frame, "Set nickname");
		jdNick.setAlwaysOnTop(true);
		pan2.add(new JLabel("Nickname: "));
		pan2.add(textNickField = new JTextField(client.getNickName()));
		textNickField.setPreferredSize(new Dimension(200, 24));
		textNickField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdNick.setVisible(false);
				client.setTempName(warningMessages(textNickField.getText(),
						client.getTempName()));

				textNickField.setText(client.getNickName());

				if (client.getKeepGoing()) {
					client.sendPacket(pk.opChNick(client.getTempName()));
				}else{
					frame.setTitle("ChriJon Client (logged in as: "
							+ client.getTempName() + ")");
				}
			}
		});
		JButton nickOkButton = new JButton("Ok");
		nickOkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdNick.setVisible(false);
				client.setTempName(warningMessages(textNickField.getText(),
						client.getTempName()));
				frame.setTitle("ChriJon Client (logged in as: "
						+ client.getTempName() + ")");
				textNickField.setText(client.getNickName());
				if (client.getKeepGoing()) {
					client.sendPacket(pk.opChNick(client.getTempName()));
				}
			}
		});
		pan2.add(nickOkButton);
		jdNick.setBounds(100, 200, 400, 74);
		jdNick.add(pan2);
		jdNick.setAlwaysOnTop(true);
		jdNick.setResizable(false);
		jdNick.setLocationRelativeTo(null);

		// Skapar encryptdialog
		JPanel pan3 = new JPanel();
		jdEncrypt = new JDialog(frame, "Set encrytion on/off");
		pan3.add(new JLabel("Use encryption: "));
		pan3.add(encryptBox = new JCheckBox());
		encryptBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				client.switchEncryption();
				if (encryptBox.isSelected()) {
					textEncryptField.setEnabled(true);
				} else {
					textEncryptField.setEnabled(false);
				}
			}
		});
		pan3.add(new JLabel("Key: "));
		pan3.add(textEncryptField = new JTextField());
		textEncryptField.setText("foobar");
		textEncryptField.setPreferredSize(new Dimension(80, 24));
		textEncryptField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdEncrypt.setVisible(false);
				client.setEncryptionKey(warningMessages(
						textEncryptField.getText(), client.getEncryptionKey()));
				textEncryptField.setText(client.getEncryptionKey());
				if (encryptBox.isSelected()) {
					System.out.print("Encryption set to ON with key "
							+ textEncryptField.getText());
					client.setEncryptionKey(textEncryptField.getText());
				} else {
					System.out.print("Encryption set to OFF");
				}
			}
		});
		textEncryptField.setEnabled(false);
		JButton encryptOkButton = new JButton("Ok");
		encryptOkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdEncrypt.setVisible(false);
				client.setEncryptionKey(warningMessages(
						textEncryptField.getText(), client.getEncryptionKey()));
				textEncryptField.setText(client.getEncryptionKey());
				if (encryptBox.isSelected()) {
					System.out.print("Encryption set to ON with key "
							+ textEncryptField.getText());
					client.setEncryptionKey(textEncryptField.getText());
				} else {
					System.out.print("Encryption set to OFF");
				}
			}
		});
		pan3.add(encryptOkButton);
		jdEncrypt.setBounds(100, 200, 400, 74);
		jdEncrypt.add(pan3);
		jdEncrypt.setAlwaysOnTop(true);
		jdEncrypt.setResizable(false);
		jdEncrypt.setLocationRelativeTo(null);

		// Skapar compressdialog
		JPanel pan4 = new JPanel();
		jdCompress = new JDialog(frame, "Set compression on/off");
		pan4.add(new JLabel("Use compression: "));
		pan4.add(compressBox = new JCheckBox());
		compressBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				client.switchCompression();
			}
		});
		JButton compressionOkButton = new JButton("Ok");
		compressionOkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdCompress.setVisible(false);
				if (compressBox.isSelected()) {
					System.out.print("Compression set to ON");
				} else {
					System.out.print("Compression set to OFF");
				}
			}
		});
		pan4.add(compressionOkButton);
		jdCompress.setBounds(100, 200, 400, 74);
		jdCompress.add(pan4);
		jdCompress.setAlwaysOnTop(true);
		jdCompress.setResizable(false);
		jdCompress.setLocationRelativeTo(null);

		// Skapar topicdialog
		JPanel pan5 = new JPanel();
		jdTopic = new JDialog(frame, "Change topic");
		pan5.add(new JLabel("Topic: "));
		pan5.add(textTopicField = new JTextField());
		textTopicField.setPreferredSize(new Dimension(200, 24));
		JButton topicOkButton = new JButton("Ok");
		topicOkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdTopic.setVisible(false);
				String topic = textTopicField.getText();
				textTopic.setText("Topic: " + topic);
				if (topic.length() > 255) {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(jdTopic,
							"Used more than 255 characters", "Error",
							JOptionPane.WARNING_MESSAGE);
				} else if (!topic.contentEquals("")) {
					client.sendPacket(pk.opChangeTopicPacket(topic));
					client.setTopic(topic);
				}
				textTopicField.setText("");
			}
		});
		pan5.add(topicOkButton);
		jdTopic.setBounds(100, 200, 400, 74);
		jdTopic.add(pan5);
		jdTopic.setAlwaysOnTop(true);
		jdTopic.setResizable(false);
		jdTopic.setLocationRelativeTo(null);

		// Skapar whoisdialog
		JPanel pan6 = new JPanel();
		jdWhoIs = new JDialog(frame, "Who is...");
		pan6.add(new JLabel("Username: "));
		pan6.add(textWhoIsField = new JTextField());
		textWhoIsField.setPreferredSize(new Dimension(200, 24));
		JButton whoIsOkButton = new JButton("Ok");
		whoIsOkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jdWhoIs.setVisible(false);
				client.setWhoIsNick(textWhoIsField.getText());
				if (!textWhoIsField.getText().contentEquals("")) {
					String list = textUsers.getText();
					Boolean isInList = false;
					Scanner scanner = new Scanner(list);
					scanner.useDelimiter("\\s*\n\\s*");
					while (scanner.hasNext()) {
						if (client.getWhoIsNick().contentEquals(scanner.next())) {
							isInList = true;
						}
					}
					scanner.close();
					if (isInList) {
						client.sendPacket(pk.opWhoIs(client.getWhoIsNick()));
					} else
						System.out.print("User not in list");
				}
			}
		});
		pan6.add(whoIsOkButton);
		jdWhoIs.setBounds(100, 200, 400, 74);
		jdWhoIs.add(pan6);
		jdWhoIs.setAlwaysOnTop(true);
		jdWhoIs.setResizable(false);
		jdWhoIs.setLocationRelativeTo(null);

		// GUIblocken

		// Lägger till rutan för headern
		headerPanel = new JPanel(new BorderLayout());
		headerPanel.setPreferredSize(new Dimension(500, 20));
		headerPanel.setBackground(new Color(184, 207, 229));
		headerPanel.add(menuBar);

		// Lägger till rutan för topic
		topicPanel = new JPanel(new BorderLayout());
		topicPanel.setPreferredSize(new Dimension(500, 20));
		topicPanel.setBackground(new Color(43, 187, 216));
		textTopic = new JTextPane();
		textTopic.setEditable(false);
		textTopic.setFont(new Font("Arial", Font.BOLD, 12));
		textTopic.setBackground(new Color(184, 207, 229));
		topicPanel.add(textTopic);

		// Lägger till rutan för loggen
		messageLogPanel = new JPanel(new BorderLayout());
		messageLogPanel.setPreferredSize(new Dimension(350, 550));
		messageLogPanel.add(areaScrollPane);

		// Lägger till rutan för textinmatning
		Box box = Box.createHorizontalBox();
		box.add(textInputField);
		box.add(sendButton);
		inputPanel = new JPanel(new BorderLayout());
		inputPanel.setPreferredSize(new Dimension(500, 30));
		inputPanel.setBackground(new Color(184, 207, 229));
		inputPanel.add(box);

		// Lägger till rutan för användarlistan
		usersPanel = new JPanel(new BorderLayout());
		usersPanel.setPreferredSize(new Dimension(150, 550));
		usersPanel.setBackground(new Color(184, 207, 229));
		usersPanel.setBorder(BorderFactory.createTitledBorder("Users"));
		usersPanel.add(areaScrollPane2);
	}

	/**
	 * Start the thread that uppdates userlist and the nick that it gets from
	 * the server. it uppdates every other seconds until keepGoing is set to
	 * false. .
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (client.getKeepGoing()) {
				updateUserName();
				updateUsers();
				textTopic.setText("Topic: " + client.getTopic());
			} else {
				jdWhoIs.setVisible(false);
				jdTopic.setVisible(false);
				textTopic.setText("");
				textUsers.setText("");
			}
		}
	}

	/**
	 * Arranges the layout of panels, runs after the grapical components have
	 * been created.
	 */
	private void setupChatGUI() {
		pane.add(headerPanel, BorderLayout.PAGE_START);
		messageLogPanel.add(topicPanel, BorderLayout.NORTH);
		pane.add(messageLogPanel, BorderLayout.CENTER);
		pane.add(inputPanel, BorderLayout.PAGE_END);
		pane.add(usersPanel, BorderLayout.LINE_END);
		frame.setVisible(true);
	}

	/**
	 * overriders system.out messages and prints them in the conversation
	 * window.
	 */
	private void systemOutToConsole() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}
		};
		System.setOut(new PrintStream(out, true));
	}

	/**
	 * Uppdates textlog with system.out messages.
	 */
	private void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textLog.append(text + "\r\n");
			}
		});
	}

	/**
	 * Uppdates username in the frames title
	 */
	private void updateUserName() {
		frame.setTitle("ChriJon Client (logged in as: " + client.getNickName()
				+ ")");
	}

	/**
	 * Updates the userList
	 *
	 */
	private void updateUsers() {
		textUsers.setText("");
		int i = 0;
		while (i < client.getUsersList().size()) {
			textUsers.append(client.getUsersList().get(i) + "\n");
			i++;
		}
	}

	/**
	 * Tests if the change in Dialog show a error message if not
	 */
	private String warningMessages(String newText, String oldText) {
		String s = "";
		if (newText.contentEquals("")) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(jdNick, "Field is empty", "Error",
					JOptionPane.WARNING_MESSAGE);
			s = oldText;
		} else if (newText.length() > 255) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(jdNick,
					"Used more than 255 characters", "Error",
					JOptionPane.WARNING_MESSAGE);
			s = oldText;
		} else if (newText.contentEquals(oldText)) {
			s = oldText;
		} else {
			s = newText;
		}
		return s;
	}
}