package com.greed;

/* Dice
*
* Purpose: This class is used to store information for one dice used in the game 
* Greed.
 *
* v1.0
*
* Copyright (c). 2014-01-28 Christer Jakobsson.
*/
public class Dice {

	private boolean marked;
	private boolean locked;
	private int val;
	
	/**
	 * Constructor that initializes a dice to the value thats param.
	 * 
	 * @param value
	 */
	public Dice(int value) {
		marked = false;
		locked = false;
		val = value + 1;
	}

	/**
	 * Constructor that initializes a dice to zero.
	 */
	public Dice() {
		marked = false;
		locked = false;
		val = 0;
	}
	
	/**
	 * Getter for marked.
	 * 
	 * @return true if the dice is marked false otherwise.
	 */
	public boolean isMarked() {
		return marked;
	}
	
	/**
	 * Setter for marked.
	 * 
	 * @param marked
	 */
	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	/**
	 * Getter for locked. 
	 * 
	 * @return true if dice is locked, false otherwise.
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Setter for locked
	 * 
	 * @param locked
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * Getter for val.
	 * 
	 * @return The value of the dice.
	 */
	public int getVal() {
		return val;
	}

	/**
	 * Setter for val.
	 * 
	 * @param val
	 */
	public void setVal(int val) {
		this.val = val;
	}

	/**
	 * Resets the dices value.
	 * @param i
	 */
	public void resetDice() {
		marked = false;
		locked = false;
	}
	
	/**
	 * Marks the dice if its unmarked and unmarks the dice if its marked.
	 */
	public void markUnmark() {
		if(marked) {
			marked = false;
		} else marked = true;
	}
}
