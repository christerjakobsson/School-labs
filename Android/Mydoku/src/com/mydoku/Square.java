package com.mydoku;


/**
 * Class used to generate sudoku board, each Square has values that represents
 * which across, down, region, value and index they have. This gets used by 
 * the MydokuModel to check that the board is correct.
 *
 * @author Christer
 * @version 1.0
 * @since 2014-08-21
 */
public class Square {

	private int across;
	private	int down;
	private int region;
	private int value;
	private int index;
	
	/**
	 * Constructor for class Square.
	 */
	public Square() {
		across = 0;
		down = 0;
		region = 0;
		value = 0;
		index = 0;
	}
	
	/**
	 * Getter for across.
	 * @return
	 */
	public int getAcross() {
		return across;
	}
	
	/**
	 * Setter for across.
	 * @param across
	 */
	public void setAcross(int across) {
		this.across = across;
	}
	
	/**
	 * Getter for down.
	 * @return
	 */
	public int getDown() {
		return down;
	}
	
	/**
	 * Setter for down.
	 * @param down
	 */
	public void setDown(int down) {
		this.down = down;
	}
	
	/**
	 * Getter for region.
	 * @return
	 */
	public int getRegion() {
		return region;
	}
	
	/**
	 * Setter for region.
	 * @param region
	 */
	public void setRegion(int region) {
		this.region = region;
	}
	
	/**
	 * Getter for value.
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Setter for value.
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	/**
	 * Getter for index.
	 * @return
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Setter for index
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}