package TravelInfoModel;
/* TravelModel
 * 
 * Purpose: Has methods that is needed for a JTable to show data.
 * 
 * v1.0
 *
 * Copyright (c). 2014-01-08 Christer Jakobsson.
 */
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * This class holds the information from all the travelOffers read from the site
 * and it has methods that specifies what that will be shown in the JTable.
 * 
 * @author Christer
 *
 */
public class TravelModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	ArrayList<TravelOffer> travels = null;

	/**
	 * Constructor, creates a new ArrayList.
	 */
	public TravelModel() {
		if(travels == null) {
			travels = new ArrayList<TravelOffer>();
		} else {
			travels.clear();
		}
	}
	
	/**
	 * Getter for row count of the table, this is the same as the size of
	 * the ArrayList.
	 */
	@Override
	public int getRowCount() {
		return travels.size();
	}

	/**
	 * Getter for the column count of the table.
	 */
	@Override
	public int getColumnCount() {
		return 4;
	}

	/**
	 * Getter for a value at a specific cell in the table.
	 */
	@Override
	public String getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0 :
				return Integer.toString(rowIndex+1);
			case 1 :
				return travels.get(rowIndex).getDestinationName();
			case 2 :
				return travels.get(rowIndex).getOutDate();
			case 3 :
				return travels.get(rowIndex).getCurrentPrice();
		}
		return null;
	}

	/**
	 * Adds the parameter offer to the list.
	 * 
	 * @param t TravelOffer
	 */
	public void addOffer(TravelOffer t) {
		travels.add(t);
		this.fireTableRowsInserted(travels.size(), travels.size());
	}

	/**
	 * Getter for the ArrayList travels.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<TravelOffer> getTravels() {
		return travels;
	}

	/**
	 * Getter for the column names.
	 */
	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
			case 0 :
				return "Nr";
			case 1 :
				return "Destination";
			case 2 :
				return "Date";
			case 3 :
				return "Price";
		}
		return null;
	}

	/**
	 * Setter for travels
	 *  
	 * @param readXml
	 */
	public void setTravels(ArrayList<TravelOffer> readXml) {
		travels.clear();
		travels.addAll(readXml);
	}
}
