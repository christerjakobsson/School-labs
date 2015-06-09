package TravelInfoModel;

/* TravelModel
 * 
 * Purpose: This class reads xml data from the site
 * http://www.fritidsresor.se/Blandade-Sidor/feeds/tradera/
 * and puts each Travel node in a Information class (TravelOffer)
 * and puts all offers in an ArrayList (travels).
 * 
 * v1.0
 * 
 * Copyright (c). 2014-01-08 Christer Jakobsson.

 */
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class that reads xml data from fritidsresor and saves the data in an array.
 * 
 * @author Christer
 * 
 */
public class TravelInfo implements TravelInfoConstants {

	private NodeList listDest;
	private Document doc;
	private volatile boolean running = true;

	/**
	 * Constructor, creates the class and sets a TrvelModel.
	 * 
	 * @param tm
	 */
	public TravelInfo() {

	}

	/**
	 * Gets the data from the site and creates a NodeList with elements with the
	 * tag name Offer.
	 * 
	 * if all goes well return true if listDest is null return false
	 * 
	 * @return boolean
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public boolean createNodeList() throws ParserConfigurationException,
			IOException, SAXException {
		try {

			running = true;
			DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();

			URL xmlUrl = new URL(
					"http://www.fritidsresor.se/Blandade-Sidor/feeds/tradera/");

			HttpURLConnection connection = (HttpURLConnection) xmlUrl
					.openConnection();
			connection.setConnectTimeout(20000);
			connection.setReadTimeout(20000);
			InputStream in = connection.getInputStream();

			doc = docBuilder.parse(in);
			listDest = doc.getElementsByTagName("Offer");
		} catch (ParserConfigurationException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (SAXException e) {
			throw e;
		}

		if (listDest != null) {
			running = false;
			return true;
		} else {
			running = false;
			return false;
		}	
	}

	/**
	 * Iterates the whole list and adds each TravelOffer to a ArrayList 
	 * which gets returned and in turn sets the travelModels list to this list
	 * 
	 * @return ArrayList
	 * 
	 * @throws IOException
	 */
	public ArrayList<TravelOffer> readXml() throws IOException {
		ArrayList<TravelOffer> travels = new ArrayList<TravelOffer>();
		for (int travelIndex = 0; travelIndex < listDest.getLength(); travelIndex++) {
			travels.add(next(travelIndex));
		}
		
		running = false;
		return travels;
	}

	/**
	 * Gets the value from the Elements tag (String) and returns the value as a
	 * string unless the tag is empty then it returns Empty.
	 * 
	 * @param tag
	 * @param element
	 * @return String
	 */
	private String getTagValue(String tag, Element element) {
		NodeList nlList = element.getElementsByTagName(tag).item(0)
				.getChildNodes();
		Node nValue = (Node) nlList.item(0);

		if (nlList.getLength() <= 0) {
			return "Empty";
		} else
			return nValue.getNodeValue();
	}

	/**
	 * Iterator that sets all the fields in the TravelOffer and returns it.
	 * 
	 * @param i
	 * @return TravelOffer
	 * @throws IOException
	 */
	private TravelOffer next(int i) throws IOException {
		TravelOffer offer = new TravelOffer();
		Node offerNode = listDest.item(i);

		if (offerNode.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) offerNode;

			offer.setBookLink(getTagValue(BOOKLINK, element));
			offer.setCityName(getTagValue(CITYNAME, element));
			offer.setCurrentPrice(getTagValue(CURRENTPRICE, element));
			offer.setDepartureName(getTagValue(DEPARTURENAME, element));
			offer.setDestinationName(getTagValue(DESTINATIONNAME, element));
			offer.setHotelGrade(getTagValue(HOTELGRADE, element));
			offer.setHotelImage(getTagValue(HOTELIMAGE, element));
			offer.setHotelName(getTagValue(HOTELNAME, element));
			offer.setJourneyLengthWeeks(getTagValue(JOURNEYLENGTHWEEKS, element));
			offer.setOriginalPrice(getTagValue(ORIGINALPRICE, element));
			offer.setOutDate(getTagValue(OUTDATE, element));
		}
		return offer;
	}

	/**
	 * Getter for boolean running
	 * 
	 * @return boolean
	 */
	public boolean getRunning() {
		return running;
	}

	/**
	 * Setter for running
	 * 
	 * @param boolean
	 */
	public void setRunning(boolean b) {
		running = b;
	}
}