/*
 * MyUnitTesteGUI
 *
 * Version 1.0
 *
 * Christer Jakobsson
 *
 * Class that creates a Grapical interface for the class MyUnitTester.
 */
import java.awt.*;

import javax.swing.*;

/**
 * Class that creates a Graphical interface for MyUnitTester. Class implements
 * ActionListener to record when a button is pressed and so forth.
 *
 * @author Christer Jakobsson
 *
 */
public class MyUnitTesterGui {

	private JTextField textField;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JButton runTest;
	private JFrame frame;
	private JLabel label;
	private JButton clear;

	/**
	 * Constructor. Takes a MyUnitTester object as parameter and runs the
	 * setupGUI method ant the systemOutToConsole method.
	 *
	 * @param myUnitTester
	 *            A MyUnitTester object that runs a testClass.
	 */
	public MyUnitTesterGui() {
		setupGUI();
	}

	/**
	 * Creates the containers for the Graphical interface and configures it to
	 * look decent.
	 */
	private void setupGUI() {

		/* Creates a frame. */
		frame = new JFrame("My Unit Tester");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(300, 250));
		frame.setPreferredSize(new Dimension(600, 500));
		frame.pack();
		frame.setLocationRelativeTo(null);

		/* Creates three panels with each of their objects in them. */
		JPanel upperPanel = buildUpperPanel();
		JPanel middlePanel = buildMiddlePanel();
		JPanel bottomPanel = buildBottomPanel();

		/* Adds the panels to the frame */
		frame.add(upperPanel, BorderLayout.NORTH);
		frame.add(middlePanel, BorderLayout.CENTER);
		frame.add(bottomPanel, BorderLayout.SOUTH);

		frame.setVisible(true);
	}

	/**
	 * Creates the bottom panel and adds it components to it.
	 *
	 * @return The bottom panel.
	 */
	private JPanel buildBottomPanel() {
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		clear = new JButton("Clear");
		clear.addActionListener(new ButtonListener(this));

		bottomPanel.add(clear, BorderLayout.CENTER);
		return bottomPanel;
	}

	/**
	 * Creates the middle panel and its components
	 *
	 * @return The middle panel.
	 */
	private JPanel buildMiddlePanel() {
		JPanel middlePanel = new JPanel( new BorderLayout());
		middlePanel.setBorder(BorderFactory.createTitledBorder("Test Results"));

		/*
		 * Creates a textArea where the result will be printed
		 */
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);

		middlePanel.add(scrollPane, BorderLayout.CENTER);

		return middlePanel;
	}

	/**
	 * Creates the upper panel and its components.
	 *
	 * @return The upper panel.
	 */
	private JPanel buildUpperPanel() {
		JPanel upperPanel = new JPanel(new BorderLayout());

		/*
		 * Creates a label and the textField for filename input.
		 */
		label = new JLabel("Filename");
		textField = new JTextField("Filename here");
		textField.addActionListener(new ButtonListener(this));

		runTest = new JButton("Run Tests");
		runTest.addActionListener(new ButtonListener(this));

		upperPanel.add(label, BorderLayout.WEST);
		upperPanel.add(textField, BorderLayout.CENTER);
		upperPanel.add(runTest, BorderLayout.EAST);

		return upperPanel;
	}

	/**
	 * Returns the textfield.
	 *
	 * @return textField.
	 */
	public JTextField getTextField() {
		return textField;
	}

	/**
	 * Clears the textarea.
	 */
	public void clearTextArea() {
		textArea.setText("");
	}

	/**
	 * Appends str to the textarea.
	 *
	 * @param str
	 */
	public void printTextArea(String str) {
		textArea.append(str);
	}
}