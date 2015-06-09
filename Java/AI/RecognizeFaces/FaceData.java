/**
 * FaceData: Class that is a data carrier for a Face image, stores each pixel
 * value in a matrix of integers. Also stores the image id as a string.
 */
public class FaceData {

	private final String imageID;
	private final int[][] faceData;

	/**
	 * Constructor
	 * 
	 * @param imageID
	 *            the line over the image, e.g. "Image1"
	 * @param faceData
	 *            the actual face in greyscale within range [0-31]
	 */
	public FaceData(String imageID, int[][] faceData) {
		this.imageID = imageID;
		this.faceData = faceData;
	}

	/**
	 * Getter for imageID
	 * 
	 * @return imageID
	 */
	public String getImageID() {
		return imageID;
	}

	/**
	 * Getter for faceData.
	 * 
	 * @return FaceData
	 */
	public int[][] getFaceData() {
		return faceData;
	}
}