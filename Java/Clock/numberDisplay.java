public class numberDisplay {
	//Create and initialize variables.  
	private int minLimit = 0;
	private int maxLimit = 0;
	private int value = 0;
	// Returns true if hours value or minutes value did a wrap around.
	public Boolean didWrapAround() {
		return value == minLimit;
	}
	// Increases the value of the clock with one minute and checks if the clock
	// Needs to do a wrap. 
	public void increment() {
		value = value + 1;
		if(value == maxLimit) {
			value = minLimit;
		}
	}
	// Returns a string containing the display with the right format.
	public String getDisplayValue() {
		int digits = Integer.toString(maxLimit).length();
		String val = Integer.toString(value);
		String temp = "";
		String fromTo = "";
		
		for(int i = 0; i  < (digits-1); i++) {
			fromTo = temp+val;
			if(fromTo.length() >= digits) {
				i = digits;
			}
			else {
				temp += "0";
			}
		}
		return "" + temp + value;
	}
	//returns value of the integer value. 
	public int getValue() {
		return value;
	}
	//Checks if minLimit is smaller then maxLimit if not print an error 
	//but proceeding as usual.
	public void setValue(int newValue) {
		if(newValue < minLimit || newValue > maxLimit) {
			System.err.println("Value not between the limits");
			System.exit(1);
		}
		else value = newValue;
	}
	// Create a numberDisplay, if minLimit is bigger than maxLimit switch the
	// values. set the value to minLimit. 
	public numberDisplay(int minLimit, int maxLimit) {
		int temp = 0;
		if(minLimit > maxLimit) {
			temp = maxLimit;
			this.maxLimit = minLimit;
			this.minLimit = temp;
			value = maxLimit;
			System.err.println("Min is bigger than max, switching values");
		} else {
			this.minLimit = minLimit;
			this.maxLimit = maxLimit;
			value = minLimit;
		}
	}
}