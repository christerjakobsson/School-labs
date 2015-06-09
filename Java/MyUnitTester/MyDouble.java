/*
 * MyDouble
 * 
 * Version 1.0
 * 
 * Christer Jakobsson
 * 
 * Class that does some simple calculations on a double variable.
 * 
 */
public class MyDouble {

	private double myDouble;

	/**
	 * Initializes the variable myDouble.
	 */
	public MyDouble() {
		myDouble = 2;
	}

	/**
	 * Doubles the value of myDouble.
	 */
	public void Doubler() {
		myDouble = myDouble * 2;
	}

	/**
	 * Calculates Square on myDouble.
	 */
	public void Squared() {
		myDouble = myDouble * myDouble;
	}

	/**
	 * Calculates myDouble - 1.
	 */
	public void Minus() {
		myDouble = myDouble - 1;
	}

	public void Plus() {
		myDouble = myDouble + 1;
	}

	/**
	 * Getter for myDouble.
	 * 
	 * @return myDouble, A double.
	 */
	public double getValue() {
		return myDouble;
	}
}
