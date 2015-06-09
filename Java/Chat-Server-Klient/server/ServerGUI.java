import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
import java.text.SimpleDateFormat;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

/**
 * 
 * @author Jonas
 *
 */
public class ServerGUI {
	private JFrame frame;
	private Container pane;
	private JPanel messageLogPanel;
	private JPanel headerPanel;
	private JPanel inputPanel;
	private JTextArea textLog;
	private JTextField textIPField;
	private JTextField textPortField;
	private JTextField textTopicField;
	private JTextField textIntPortField;
	private Server server;
	private JDialog jd;
	private Listener listener;
	
	/**
	 * Creates the GUI and takes a server as parameter
	 * @param s
	 */
	public ServerGUI(Server s) {
		server = s;

		// Creates frame
		frame = new JFrame("ChriJon Server");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setSize(600, 500);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane
						.showConfirmDialog(frame, "Shutdown ChriJon Server?",
								"Exit", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					try {
						server.saveSettings();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					server.shutDown();
				}
			}
		});
		pane = frame.getContentPane();
		createChatComponents();
		setupServerGUI();
		systemOutToConsole();
		jd.setVisible(true);
		listener = new Listener(server);
	}	

	/**
	 * Creates components for the serverGUI
	 */
	private void createChatComponents() {
		// Creates the console
		textLog = new JTextArea();
		DefaultCaret caret = (DefaultCaret) textLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textLog.setEditable(false);
		textLog.setFont(new Font("Courier", Font.PLAIN, 12));
		textLog.setLineWrap(true);
		textLog.setWrapStyleWord(true);
		textLog.setBackground(new Color(248, 251, 253));
		// Scroll for the text area
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
		// Creates the menus
		JMenuBar menuBar = new JMenuBar();

		// Creates dns dialog
		JPanel pan = new JPanel();
		jd = new JDialog(frame, "Set DNS address");
		
		pan.add(new JLabel("Topic: "));
		pan.add(textTopicField = new JTextField(server.getTopic()));
		textTopicField.setPreferredSize(new Dimension(100, 24));
		
		pan.add(new JLabel("IP: "));
		pan.add(textIPField = new JTextField(server.getDNS()));
		textIPField.setPreferredSize(new Dimension(100, 24));
		
		pan.add(new JLabel("Port: "));
		pan.add(textPortField = new JTextField(server.getDNSPort()));
		textPortField.setPreferredSize(new Dimension(50, 24));
		
		pan.add(new JLabel("Int.Port: "));
		pan.add(textIntPortField = new JTextField(server.getInternalPort()));
		textIntPortField.setPreferredSize(new Dimension(50, 24));
		
		JButton serverOkButton = new JButton("Connect");
		serverOkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.print("Trying to connect to " + server.getDNS()
						+ ":" + server.getDNSPort());
				server.setDNS(textIPField.getText());
				server.setDNSPort(textPortField.getText());
				server.setTopic(textTopicField.getText());
				server.setInternalPort(textIntPortField.getText());
				try {
					server.connectDNS();
					listener.start();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(jd,
							"Can't connect to DNS server!", "Message",
							JOptionPane.WARNING_MESSAGE);
					System.out.print("Connection failed");
					e.printStackTrace();
				}
				jd.setVisible(false);
			}
		});
		pan.add(serverOkButton);
		jd.setBounds(100, 200, 630, 60);
		jd.add(pan);
		jd.setAlwaysOnTop(true);
		jd.setResizable(false);
		jd.setLocationRelativeTo(null);

		// Filemenu
		JMenu dropFile = new JMenu("File");
		dropFile.setMnemonic(KeyEvent.VK_F);
		JMenuItem serverFile = new JMenuItem("Set DNS server");
		serverFile.setMnemonic(KeyEvent.VK_N);
		serverFile.setToolTipText("Set address for DNS server");
		serverFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jd.setVisible(true);
			}
		});
		dropFile.add(serverFile);

		JMenuItem invertFile = new JMenuItem("Invert terminal");
		invertFile.setMnemonic(KeyEvent.VK_I);
		invertFile.setToolTipText("Switch terminal colors");
		invertFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (Color.WHITE.equals(textLog.getBackground())) {
					textLog.setForeground(Color.white);
					textLog.setBackground(Color.black);					
				} else {
					textLog.setForeground(Color.black);
					textLog.setBackground(Color.white);					
				}
			}
		});
		dropFile.add(invertFile);

		JMenuItem saveFile = new JMenuItem("Save log");
		saveFile.setMnemonic(KeyEvent.VK_L);
		saveFile.setToolTipText("Export textlog to a txt-file");
		saveFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					FileWriter fw;
					try {
						fw = new FileWriter(fileChooser.getSelectedFile());
						fw.write(textLog.getText());
						System.out.print("Logfile saved successfully!");
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		dropFile.add(saveFile);

		JMenuItem exitFile = new JMenuItem("Exit");
		exitFile.setMnemonic(KeyEvent.VK_E);
		exitFile.setToolTipText("Exit application");
		exitFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (JOptionPane
						.showConfirmDialog(frame, "Shutdown ChriJon Server?",
								"Exit", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					try {
						server.saveSettings();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					server.shutDown();
				}
			}
		});
		dropFile.add(exitFile);

		menuBar.add(dropFile);

		// Adds the frame for the header
		headerPanel = new JPanel(new BorderLayout());
		headerPanel.setPreferredSize(new Dimension(500, 20));
		headerPanel.setBackground(new Color(43, 187, 216));
		headerPanel.add(menuBar);

		// Adds the log panel
		messageLogPanel = new JPanel(new BorderLayout());
		messageLogPanel.setPreferredSize(new Dimension(350, 550));
		messageLogPanel.add(areaScrollPane);

		// Adds the panel for the bottom
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.add(Box.createGlue());
		inputPanel = new JPanel(new BorderLayout());
		inputPanel.setPreferredSize(new Dimension(500, 50));
		inputPanel.setBackground(new Color(184, 207, 229));
		inputPanel.add(horizontalBox);		
	}
	
	/**
	 * Draws the server window
	 */
	private void setupServerGUI(){
		pane.add(headerPanel, BorderLayout.PAGE_START);
		pane.add(messageLogPanel, BorderLayout.CENTER);
	    pane.add(inputPanel, BorderLayout.PAGE_END);
		frame.setVisible(true);
	}

	/**
	 * Overriders system.out.print();
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
	 * Uppdates textlog with system.out messages
	 * @param text
	 */
	private void updateTextArea(final String text) {
	    SwingUtilities.invokeLater(new Runnable(){	      
	    public void run(){
	    	java.util.Date date = new java.util.Date();
	    	String formattedDate;
	    	SimpleDateFormat sdf = new SimpleDateFormat("[dd/MM/yy] HH:mm:ss >> ");
	    	formattedDate = sdf.format(date);	    	
	        textLog.append(formattedDate +" " + text + "\r\n");
	      }
	    });
	}	
}
