package TravelInfoGui;

/* TravelInfoGui
 *
 * Purpose: Used to show a couple of windows and give the user a interface to
 * use the program with, also for showing the user the results
 *
 * v1.0
 *
 * Copyright (c). 2014-01-08 Christer Jakobsson.
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import TravelInfoController.TravelInfoController;
import TravelInfoModel.TravelModel;
import TravelInfoModel.TravelOffer;

/**
 * Class that represents the Graphical Interface of the program. used to give
 * the user a easy way to use the program.
 *
 * @author Christer
 *
 */
public class TravelInfoGui {

	private int updateTimer;
	private int frameHeight;
	private int frameWidth;
	private String bookString;
	private String contentString;
	private JFrame frame;
	private TravelInfoController travController;
	private Timer timer;
	private JTable table;
	private JTextField searchField;
	private TableRowSorter<TravelModel> sorter;
	private JProgressBar progBar;
	private JTextField timeToUpdate;
	private SimpleDateFormat sdf;
	private JDialog updateDialog;
	private JTextField statusText;
	private JButton refresh;
	private JDialog offerDialog;
	private JDialog browserDialog;
	private boolean dontListen;
	private JPanel middlePanel;
	private JPanel bottomPanel;
	private JScrollPane pane;
	private JLabel hotelImageLabel;

	/**
	 * Constructor, gets the current TravelInfoControlle as parameter.
	 *
	 * @author Christer
	 * @param travelController
	 */
	public TravelInfoGui(TravelInfoController travelController) {
		this.travController = travelController;
		sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		browserDialog = new JDialog();
		offerDialog = new JDialog();
	}

	/**
	 * TimerTask that will update some components on the frame depending on if
	 * the program is updating the list with the travels or not.
	 */
	public void updateGui() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {

					if (travController.getTravelInfo().getRunning()) {
						pane.getVerticalScrollBar().setValue(0);
						table.clearSelection();
						table.setEnabled(false);
						progBar.setIndeterminate(true);
						frame.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						table.setVisible(false);
						searchField.setEnabled(false);
						timeToUpdate.setText("Updating...");
						refresh.setEnabled(false);
					} else if (!travController.getTravelInfo().getRunning()) {
						table.setEnabled(true);
						progBar.setIndeterminate(false);
						frame.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						table.setVisible(true);
						refresh.setEnabled(true);
						searchField.setEnabled(true);
						timeToUpdate.setText(sdf.format(travController
								.getTimeLeft()));
						middlePanel.repaint();
						bottomPanel.repaint();
					}
				}
			}, 0, 1000);
		}
	}

	/**
	 * Comparator thats used for the columns that have Integer values so they
	 * get sorted correctly.
	 */
	Comparator<String> comparator = new Comparator<String>() {
		public int compare(String firstString, String secondString) {
			table.clearSelection();
			String[] strings1 = firstString.split("\\D+");
			String[] strings2 = secondString.split("\\D+");

			int n1 = Integer.parseInt(strings1[0]);
			int n2 = Integer.parseInt(strings2[0]);

			if (n1 == n2) {
				return firstString.compareTo(secondString);
			}
			return n1 - n2;
		}
	};

	/**
	 * Shows a JDialog with information about the travel the user selected with
	 * the mouse in the JTable.
	 *
	 * @param travel
	 */
	private void showCurrentTravelOffer(TravelOffer travel) {
		if (!offerDialog.isVisible()) {
			offerDialog = new JDialog();
			offerDialog.setTitle(travel.getDestinationName());
			offerDialog.setLayout(new BorderLayout());
			bookString = travel.getBookLink();
			contentString = travel.getContentLink();

			offerDialog.setResizable(false);


			offerDialog.setAlwaysOnTop(true);

			JPanel offerPane = new JPanel();
			offerPane.setLayout(new BorderLayout());
			offerPane.setBorder(BorderFactory.createRaisedBevelBorder());

			JTextArea travelInfoArea = new JTextArea();
			travelInfoArea.setBorder(BorderFactory.createEtchedBorder());
			travelInfoArea.setEditable(false);

			travelInfoArea.append("Pricecheck.\nOriginal Price: "
					+ travel.getOriginalPrice() + "\nCurrent Price: "
					+ travel.getCurrentPrice() + "\n\n" + "Hotel Information\n"
					+ "Name: " + travel.getHotelName() + "\n" + "Grade: "
					+ travel.getHotelGrade() + "\n"
					+ "\nTravel Information\nDeparture: "
					+ travel.getDepartureName() + "\n" + "Destination: "
					+ travel.getDestinationName() + "\nLength (Weeks): "
					+ travel.getJourneyLengthWeeks() + "\nDate: "
					+ travel.getOutDate());

			JButton book = new JButton("Book");
			book.addActionListener(travController);

			JButton contentLink = new JButton("More Info");
			contentLink.addActionListener(travController);

			JLabel imageText = new JLabel("Hotel Picture");
			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

			travelInfoArea.setAlignmentX(Component.CENTER_ALIGNMENT);
			leftPanel.add(travelInfoArea);

			imageText.setAlignmentX(Component.CENTER_ALIGNMENT);
			leftPanel.add(imageText);
			book.setAlignmentX(Component.CENTER_ALIGNMENT);
			leftPanel.add(book);
			contentLink.setAlignmentX(Component.CENTER_ALIGNMENT);
			leftPanel.add(contentLink);

			offerPane.add(leftPanel, BorderLayout.WEST);
			offerDialog.pack();

			Image hotelImage = null;
			try {
				URL url = new URL(travel.getHotelImage());
				hotelImage = ImageIO.read(url);
			} catch (IOException e) {
				printError("Image Error: "+e.getMessage());
			}

			ImageIcon hotelIcon = new ImageIcon(hotelImage);
			hotelImageLabel = new JLabel(hotelIcon);

			offerPane.add(hotelImageLabel, BorderLayout.EAST);

			offerDialog.add(offerPane);
			offerDialog.pack();
			offerDialog.setLocationRelativeTo(frame);
			offerDialog.setVisible(true);
		}
	}

	/**
	 * Getter for contentString.
	 *
	 * @return String
	 */
	public String getContentString() {
		return contentString;
	}

	/**
	 * Getter for bookString.
	 *
	 * @return String
	 */
	public String getBookString() {
		return bookString;
	}

	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Instantiates the JFrame that holds the components to show the JTable and
	 * manipulate it.
	 */
	public void setupGui() {
		frame = new JFrame("Travelinfo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
		frame.setMinimumSize(new Dimension(400, 200));
		frame.pack();
		frame.setLocationRelativeTo(null);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				travController.savePreferences();
				frame.dispose();
			}
		});

		JPanel menuPanel = buildMenuPanel();
		middlePanel = buildMiddlePanel();
		bottomPanel = buildBottomPanel();

		frame.add(menuPanel, BorderLayout.NORTH);
		frame.add(middlePanel, BorderLayout.CENTER);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		frame.setVisible(true);
	}

	/**
	 * Getter for frameWidth
	 *
	 * @return Integer
	 */
	public int getFrameWidth() {
		return frameWidth;
	}

	/**
	 * Getter for frameHeight
	 *
	 * @return Integer
	 */
	public int getFrameHeight() {
		return frameHeight;
	}

	/**
	 * Setter for frameHeight
	 *
	 * @param frameHeight
	 */
	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
	}

	/**
	 * Creates a panel that holds the components at the bottom of the JFrame.
	 *
	 * @return JPanel
	 */
	private JPanel buildBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		JPanel upper = new JPanel();
		JPanel lower = new JPanel();
		timeToUpdate = new JTextField();
		timeToUpdate.setEditable(false);
		timeToUpdate.setPreferredSize(new Dimension(200, 24));
		JLabel updateTime = new JLabel("Updates in");

		upper.add(updateTime);
		upper.add(timeToUpdate);

		progBar = new JProgressBar();
		progBar.setVisible(true);
		lower.add(progBar);

		bottomPanel.add(upper, BorderLayout.NORTH);
		bottomPanel.add(lower, BorderLayout.CENTER);
		return bottomPanel;
	}

	/**
	 * Filters the JTable using the text from searhField.
	 */
	private void newFilter() {
		dontListen = true;
		RowFilter<TravelModel, Object> rf = null;
		if (searchField.getText() != null) {
			rf = RowFilter.regexFilter(searchField.getText(), 1);
		}
		sorter.setRowFilter(rf);
		dontListen = false;
	}

	/**
	 * Getter for dontListen
	 *
	 * @return boolean
	 */
	public boolean isDontListen() {
		return dontListen;
	}

	/**
	 * Creates a panel that holds the components at the middle of the JFrame.
	 *
	 * @return JPanel
	 */
	private JPanel buildMiddlePanel() {
		JPanel middlePan = new JPanel(new BorderLayout());
		JLabel searchLabel = new JLabel("Find Destination");
		searchField = new JTextField();

		searchField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				newFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				newFilter();
			}
		});

		searchField.setPreferredSize(new Dimension(130, 24));

		refresh = new JButton("Refresh");
		refresh.addActionListener(travController);

		JPanel upperPane = new JPanel(new FlowLayout());

		upperPane.add(searchLabel);
		upperPane.add(searchField);
		upperPane.add(refresh);

		table = new JTable(travController.getTravelModel());
		sorter = new TableRowSorter<TravelModel>(
				travController.getTravelModel());
		table.setRowSorter(sorter);

		sorter.setComparator(3, comparator);
		sorter.setComparator(0, comparator);

		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						if (event.getValueIsAdjusting() && !isDontListen()) {
							try {
								int travelIndex = table
										.convertRowIndexToModel(table
												.getSelectedRow());

								showCurrentTravelOffer(travController
										.getTravelModel().getTravels()
										.get(travelIndex));
							} catch (Exception e) {
								printError("Table error: " + e.getMessage());
							}
						}
					}

				});

		pane = new JScrollPane(table);
		pane.setBorder(BorderFactory
				.createTitledBorder("Pick a destination to know more"));
		middlePan.add(upperPane, BorderLayout.NORTH);
		middlePan.add(pane, BorderLayout.CENTER);
		return middlePan;
	}

	/**
	 * Creates a panel that holds the menu components at the top of the JFrame.
	 *
	 * @return JPanel
	 */
	private JPanel buildMenuPanel() {
		JPanel menuPan = new JPanel(new BorderLayout());
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");

		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(travController);
		JMenuItem refresh = new JMenuItem("Refresh");
		refresh.addActionListener(travController);

		file.add(refresh);
		file.add(quit);

		JMenu options = new JMenu("Options");

		JMenuItem updateInterval = new JMenuItem("Set Update Interval");
		updateInterval.addActionListener(travController);

		options.add(updateInterval);

		JMenu help = new JMenu("Help");
		JMenuItem helpItem = new JMenuItem("Help");
		helpItem.addActionListener(travController);

		help.add(helpItem);

		menuBar.add(file);
		menuBar.add(options);
		menuBar.add(help);

		menuPan.add(menuBar);
		return menuPan;
	}

	/**
	 * Shows a JDialog where the user can set the updateInterval with 30 minutes
	 * intervals.
	 */
	public void setUpdateInterval() {
		updateDialog = new JDialog();

		updateDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		updateDialog.setPreferredSize(new Dimension(350, 150));
		updateDialog.pack();
		updateDialog.setLocationRelativeTo(frame);
		updateTimer = 30 * 1000 * 60;

		updateDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				travController.setUpdateTimer(updateTimer * 1000);
			}
		});

		JPanel boxPane = new JPanel();
		boxPane.setLayout(new BoxLayout(boxPane, BoxLayout.Y_AXIS));
		JLabel updateLabel = new JLabel(
				"Set Update Interval (30 minute frequency)", JLabel.CENTER);
		updateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JSlider updateTimeSlider = new JSlider(JSlider.HORIZONTAL, 30, 1440, 30);
		updateTimeSlider.setSnapToTicks(true);

		JButton okeyButton = new JButton("Ok");
		okeyButton.addActionListener(travController);
		okeyButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

		statusText = new JTextField();
		statusText.setSize(150, 24);
		updateTimeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				updateTimer = source.getValue();
				int hours = updateTimer / 60;
				int minutes = updateTimer % 60;
				statusText.setText(updateTimer + " minutes (" + hours
						+ " Hours " + minutes + " Minutes)");
			}
		});

		updateTimeSlider.setMajorTickSpacing(60);
		updateTimeSlider.setMinorTickSpacing(30);
		updateTimeSlider.setPaintTicks(true);
		updateTimeSlider.setPaintTrack(true);
		updateTimeSlider.setBorder(BorderFactory.createEmptyBorder());

		boxPane.add(updateLabel);
		boxPane.add(updateTimeSlider);
		boxPane.add(statusText);
		boxPane.add(okeyButton);

		updateDialog.add(boxPane);
		updateDialog.setVisible(true);
	}

	/**
	 * Getter for updateTimer.
	 *
	 * @return Integer
	 */
	public int getUpdateTimer() {
		return updateTimer;
	}

	/**
	 * Getter for updateDialog.
	 *
	 * @return JDialog
	 */
	public JDialog getUpdateDialog() {
		return updateDialog;
	}

	/**
	 * Getter for table.
	 *
	 * @return JTable
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * Shows a JDialog that acts as a very simplified browser to show the user
	 * the bookLink site or the contentLink site
	 *
	 * @param link
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public void browser(final String link) {
		if (link != null) {

			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URL(link).toURI());
				} catch (MalformedURLException e) {
					printError("URL Error: " + e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					printError("ERROR " + e.getMessage());
				} catch (URISyntaxException e) {
					printError("Syntax Error: " + e.getMessage());
				}
			} else {
				if (!browserDialog.isVisible()) {

					browserDialog.setTitle("Booking Site");
					browserDialog.setSize(new Dimension(800, 600));
					browserDialog.setLocationRelativeTo(frame);
					JEditorPane editorPane = new JEditorPane();

					browserDialog
							.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

					editorPane.setEditable(false);
					editorPane.setContentType("text/html");

					JScrollPane scrollPane = new JScrollPane(editorPane);
					browserDialog.add(scrollPane, BorderLayout.CENTER);
					try {
						editorPane.setPage(link);
					} catch (IOException e) {
						editorPane.setText("Error 404");
					}
					editorPane
							.addHyperlinkListener(new LinkListener(editorPane));
					browserDialog.setVisible(true);
				}
			}
		} else
			printError("Link not found");
	}

	/**
	 * Creates and shows a JOptionpane with an error message.
	 *
	 * @author Christer Jakobsson
	 *
	 * @param message
	 *            the error message that was created.
	 */
	public void printError(String message) {
		JOptionPane.showMessageDialog(null, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * A JDialog with a textArea that contains some text meant to help the user.
	 *
	 * @author Christer Jakobsson
	 */
	public void helpDialog() {
		JDialog help = new JDialog();
		help.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		JTextArea helpText = new JTextArea();
		helpText.setEditable(false);
		helpText.append("* Refresh updates the table\n\n* Click on a travel to "
				+ "know more about it\n\n* Go to Options->Set Update Interval"
				+ " to change the update interval\n\n* Click a column to sort"
				+ " by it\n\n* Type in 'Find Destination' to find destination");

		help.add(helpText);
		help.pack();
		help.setLocationRelativeTo(null);
		help.setVisible(true);
	}

	/**
	 * Getter for offerDialog
	 *
	 * @return JDialog
	 */
	public JDialog getOfferDialog() {
		return offerDialog;
	}

	/**
	 * Setter for frameWidth
	 *
	 * @param frameWidth
	 */
	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}
}