public class Clock {
	// Creates the hours and minutes for the clock.
	private numberDisplay hours = new numberDisplay(0,24);
	private numberDisplay minutes = new numberDisplay(0,60);
	private int alarmh;
	private int alarmm;

	
	// Create the string that acts as display and an integer that acts as
	// on or off for the alarm.
	private String displayString;
	protected boolean onOrOff = false;
	
	// Creates a Clock object with the time set to 00:00.
	public Clock() {
		this(0,0);
	}
	// Create a Clock object with the time set to the parameters hour and minute.
	public Clock(int hour, int minute) {
		hours.setValue(hour);
		minutes.setValue(minute);
	}
	// Increases the time with one minute and checks if it did a wrap around, 
	// if so hour increases with one. and if the alarm is on calls checkAlarm.
	public void timeTick() {
		minutes.increment();
    	if(minutes.didWrapAround()) { 
    		hours.increment();
	       	}
    	if(onOrOff) {
    		checkAlarm();
    	}
	}
	// Sets the time to the parameters hour and minute. 
	public void setTime(int hour, int minute) {
		hours.setValue(hour);
		minutes.setValue(minute);
	}
	// Invokes updateDisplay and return the string displayString.
	public String getTime() {
		updateDisplay();
		return displayString;
	}
	// Puts together the current values of hours and minutes and formats a 
	// string that looks like a clock.
	private void updateDisplay() {
		displayString = hours.getDisplayValue() + ":" + minutes.getDisplayValue();
	}
	// Sets the alarm to the parameters hour and minute.
	public void setAlarm(int hour, int minute) {
		alarmh = hour;
		alarmm = minute;  
	}
	
	// Checks if the alarm clock has the same values as the clock and prints an
	// alarm if it is.
	public void checkAlarm() {
		if((alarmh == hours.getValue()) && 
				(alarmm == minutes.getValue())) {
			System.out.print("alarm ");
		}
	}
}
