package TravelInfoController;

/* TravelInfoController
 *
 * Purpose: Used to create the class that reads the xml and the gui and
 * controll all actions happening.
 *
 * v1.0
 *
 * Copyright (c). 2014-01-08 Christer Jakobsson.
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import TravelInfoGui.TravelInfoGui;
import TravelInfoModel.TravelInfo;
import TravelInfoModel.TravelModel;
/**
 * This class acts as a controller for the program, it creates a TravelInfo that
 * reads data from the website xml and it creates a TravelInfoGui
 *
 * @author Christer
 *
 */
public class TravelInfoController implements ActionListener {

	private TravelInfo travelInfo;
	private volatile TravelModel travelModel = null;
	private long updateTime;
	private TravelInfoGui travelGui;
	private Timer timer;
	private volatile long timeLeft;
	private Preferences myPrefs;
	private boolean timersIsRunning;
	private Timer decreaseTimer;

	/**
	 * Constructor, gets the saved preferences from the last run if there are
	 * any, creates a TravelModel, TravelInfo and TravelInfoGui.
	 *
	 */
	public TravelInfoController() {
		travelModel = new TravelModel();
		travelInfo = new TravelInfo();
		travelGui = new TravelInfoGui(this);
		loadPreferences();
		timeLeft = updateTime;
		travelGui.setupGui();
		travelGui.updateGui();
		updateTravelList();
	}

	/**
	 * Loads saved settings using Preferences.
	 */
	public void loadPreferences() {
		myPrefs = Preferences.userRoot();
		updateTime = myPrefs.getInt("UPDATETIMER", 1800000);
		travelGui.setFrameHeight(myPrefs.getInt("FRAMEHEIGHT", 400));
		travelGui.setFrameWidth(myPrefs.getInt("FRAMEWIDTH", 600));
	}

	/**
	 * Adds a new assignment to the timer.
	 *
	 * @param frequency
	 * @param task
	 */
	private void addAssignment(int frequency, TimerTask task) {
		decreaseTimer.schedule(task, frequency, frequency);
	}

	/**
	 * Starts timer that will decrease the updateTime with one second every
	 * second.
	 */
	private void startTimer() {
		decreaseTimer = new Timer();
		addAssignment(1000, new decreaseTimeLeft());
	}

	/**
	 * Starts a TimerTask that will update the list with all the offers at
	 * a interval the user have set, (30 minute intervals).
	 *
	 */
	private void updateTravelList() {
		timersIsRunning = true;
		startTimer();

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				timeLeft = updateTime;
				try {
					if (travelInfo.createNodeList()) {
						travelModel.setTravels(travelInfo.readXml());
					} else sendMessage("Cant create nodeList, try again");
				} catch (ParserConfigurationException e) {
					travelInfo.setRunning(false);
					sendMessage("Parser error: "+e.getMessage());
				} catch (java.net.SocketTimeoutException e) {
					travelInfo.setRunning(false);
					sendMessage("SocketTimeout: "+e.getMessage());
				} catch (IOException e) {
					travelInfo.setRunning(false);
					travelGui.printError("Can't connect to "+ e.getMessage());
				} catch (SAXException e) {
					travelInfo.setRunning(false);
					sendMessage("SAXE error: "+e.getMessage());
				}
			}
		}, 0, updateTime);
	}

	/**
	 * prints a error message in the interface, method gets called from another
	 * thread so needs to do invokeLater.
	 *
	 * @param string
	 */
	public void sendMessage(final String string) {
		  SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				travelGui.printError(string);
			}
		  });
	}

	/**
	 * Cancels the timers and sets a boolean to false.
	 */
	public void cancelTimer() {
		timer.cancel();
		decreaseTimer.cancel();
		timersIsRunning = false;
	}

	/**
	 * Getter for a boolean that indicates if the timers are running or not.
	 * @return
	 */
	public boolean isTimersRunning() {
		return timersIsRunning;
	}

	/**
	 * Inner class
	 * TimerTask that decreases the value of timeLeft with one second every
	 * second.
	 *
	 * @author Christer
	 *
	 */
	private class decreaseTimeLeft extends TimerTask {

		@Override
		public void run() {
			if (timeLeft == 0) {
				timeLeft = updateTime;
			} else
				timeLeft -= 1000;
		}
	}

	/**
	 * Getter for long timeLeft.
	 *
	 * @return long
	 */
	public long getTimeLeft() {
		return timeLeft;
	}

	/**
	 * Getter for travelInfo
	 *
	 * @return TravelInfo
	 */
	public TravelInfo getTravelInfo() {
		return travelInfo;
	}

	/**
	 * ActionListener for the buttons in the TravelInfoGui.
	 */
	@Override
	public void actionPerformed(ActionEvent c) {
		String value = c.getActionCommand();

		if (value.equals("Refresh")) {
			cancelTimer();
			updateTravelList();
		} else if (value.equals("Set Update Interval")) {
			travelGui.setUpdateInterval();
		} else if (value.equals("Refresh List")) {
			cancelTimer();
			updateTravelList();
		} else if (value.equals("Ok")) {
			setUpdateTimer(travelGui.getUpdateTimer());
			travelGui.getUpdateDialog().dispose();
		} else if (value.equals("Book")) {
			travelGui.getOfferDialog().setVisible(false);
			travelGui.browser(travelGui.getBookString());
		} else if (value.equals("More Info")) {
			travelGui.getOfferDialog().setVisible(false);
			travelGui.browser(travelGui.getContentString());
		} else if(value.equals("Quit")) {
			cancelTimer();
			System.exit(0);
		} else if(value.equals("Help")) {
			travelGui.helpDialog();
		}
	}

	/**
	 * Getter for the TravelModel travelModel.
	 *
	 * @return TravelModel
	 */
	public TravelModel getTravelModel() {
		return travelModel;
	}

	/**
	 * Saves the preferences in the registry.
	 */
	public void savePreferences() {
		myPrefs.putLong("UPDATETIMER", updateTime);
		travelGui.setFrameHeight(travelGui.getFrame().getHeight());
		travelGui.setFrameWidth(travelGui.getFrame().getWidth());
		myPrefs.putInt("FRAMEWIDTH", travelGui.getFrameWidth());
		myPrefs.putInt("FRAMEHIGHT", travelGui.getFrameHeight());
	}

	/**
	 * Sets the updateTimer to a value, updateTimer is the time in minutes
	 * and this gets converted to milliseconds and then set to updateTime
	 * and timeLeft.
	 *
	 * @param updateTimer
	 */
	public void setUpdateTimer(int updateTimer) {
		cancelTimer();
		updateTime = updateTimer * 60 * 1000;
		timeLeft = updateTime;
		updateTravelList();
	}
}