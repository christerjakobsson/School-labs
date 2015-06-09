/* TravelOffer
 *
 * Purpose: Holds information about one Offer from fritidsresors site.
 *
 * v1.0
 *
 * Copyright (c). 2014-01-08 Christer Jakobsson.
 */

package TravelInfoModel;


/**
 * Class that holds all the needed information about a Offer.
 * used by TravelModel to create the JTable.
 *
 * @author Christer
 *
 */
public class TravelOffer {
	private String departureName;
	private String outDate;
	private String destinationName;
	private String cityName;
	private String currentPrice;
	private String originalPrice;
	private String hotelName;
	private String contentLink;
	private String hotelImage;
	private String hotelGrade;
	private String bookLink;
	private int journeyLengthWeeks;

	/**
	 * Constructor
	 */
	public TravelOffer() {
	}

	/**
	 * Getter for departureName.
	 *
	 * @return String
	 */
	public String getDepartureName() {
		return departureName;
	}

	/**
	 * Setter for departureName
	 *
	 * @param departureName
	 */
	public void setDepartureName(String departureName) {
		this.departureName = departureName;
	}

	/**
	 * Getter for outDate
	 *
	 * @return String
	 */
	public String getOutDate() {
		return outDate;
	}

	/**
	 * Setter for outDate.
	 *
	 * @param outDate
	 */
	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}

	/**
	 * Getter for destinationName
	 *
	 * @return String
	 */
	public String getDestinationName() {
		return destinationName;
	}

	/**
	 * Setter for destinationName
	 *
	 * @param destinationName
	 */
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	/**
	 * Getter for cityName
	 *
	 * @return String
	 */
	public String getCityName() {
		return cityName;
	}

	/**
	 * Setter for cityName
	 *
	 * @param cityName
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * Getter for currentPrice
	 *
	 * @return String
	 */
	public String getCurrentPrice() {
		return currentPrice;
	}

	/**
	 * Setter for currentPrice
	 *
	 * @param string
	 */
	public void setCurrentPrice(String string) {
		if (!string.equals("Empty")) {
			this.currentPrice = string;
		}
	}

	/**
	 * Getter for originalPrice
	 *
	 * @return String
	 */
	public String getOriginalPrice() {
		return originalPrice;
	}

	/**
	 * Setter for originalPrice
	 *
	 * @param string
	 */
	public void setOriginalPrice(String string) {
		this.originalPrice = string;
	}

	/**
	 * Getter for hotelName
	 *
	 * @return String
	 */
	public String getHotelName() {
		return hotelName;
	}

	/**
	 * Setter for hotelName
	 *
	 * @param hotelName
	 */
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	/**
	 * Getter for hotelImage
	 *
	 * @return String
	 */
	public String getHotelImage() {
		return hotelImage;
	}

	/**
	 * Setter for hotelImage
	 *
	 * @param string
	 */
	public void setHotelImage(String string) {
		hotelImage = string;
	}

	/**
	 * Getter for hotelGrade
	 *
	 * @return String
	 */
	public String getHotelGrade() {
		return hotelGrade;
	}

	/**
	 * Setter for hotelGrade
	 *
	 * @param string
	 */
	public void setHotelGrade(String string) {
		if (!string.equals("Empty")) {
			this.hotelGrade = string;
		}
	}

	/**
	 * Getter for contentLink
	 *
	 * @return String
	 */
	public String getContentLink() {
		return contentLink;
	}

	/**
	 * Setter for contentLink
	 *
	 * @param string
	 */
	public void setContentLink(String string) {
			contentLink = string;
	}

	/**
	 * Getter for bookLink
	 *
	 * @return String
	 */
	public String getBookLink() {
		return bookLink;
	}

	/**
	 * Setter for bookLink
	 *
	 * @param string
	 */
	public void setBookLink(String string) {
		bookLink = string;
	}

	/**
	 * Getter for journeyLengthWeeks
	 *
	 * @return Integer
	 */
	public int getJourneyLengthWeeks() {
		return journeyLengthWeeks;
	}

	/**
	 * Setter for journeyLengthWeeks
	 *
	 * @param string
	 */
	public void setJourneyLengthWeeks(String string) {
		if (!string.equals("Empty")) {
			this.journeyLengthWeeks = Integer.parseInt(string);
		}
	}
}