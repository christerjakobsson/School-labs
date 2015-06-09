import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This class will read the training-file and test-file to ArrayLists of
 * FaceData objects, the facit-file is read to a HashMap of Strings(keys) and
 * Integers(values). It will skip all lines which are either empty or starts
 * with a hash-sign (#). FaceData
 */
public class FileHandler {

	private static final int IMAGE_SIZE = 20;
	private ArrayList<FaceData> trainingData;
	private ArrayList<FaceData> testData;
	private HashMap<String, Integer> facit;

	/**
	 * Constructor, Reads data from all the in files and stores them in in
	 * suitable data structures.
	 * 
	 * @param trainingFile
	 *            the .txt file with the training faces
	 * @param trainingFacit
	 *            the .txt file with the facit
	 * @param testFile
	 *            the .txt file to evaluate
	 * @throws FileNotFoundException
	 */
	public FileHandler(String trainingFile, String trainingFacit,
			String testFile) throws FileNotFoundException {

		trainingData = readToFaceData(trainingFile);
		testData = readToFaceData(testFile);
		facit = readFacitFile(trainingFacit);
	}

	/**
	 * Reads data from a file containing facial expression images, and stores
	 * them in a FaceData object.
	 * 
	 * @param faceFile
	 * @return the data as an ArrayList
	 * @throws FileNotFoundException
	 */
	private ArrayList<FaceData> readToFaceData(String faceFile)
			throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(faceFile));

		String currentLine;
		ArrayList<FaceData> data = new ArrayList<FaceData>();

		try {
			while ((currentLine = jumpToNextImage(br)) != null) {
				String imageID = currentLine;
				int[][] matrix = new int[IMAGE_SIZE][IMAGE_SIZE];
				for (int i = 0; i < IMAGE_SIZE; i++) {
					currentLine = br.readLine();

					StringTokenizer st = new StringTokenizer(currentLine);
					int j = 0;
					while (st.hasMoreTokens()) {
						matrix[i][j] = Integer.parseInt(st.nextToken());
						j++;
					}
				}
				data.add(new FaceData(imageID, matrix));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return data;
	}

	/**
	 * Reads a facit file and stores the correct answer for each image in a
	 * HashMap.
	 * 
	 * @param facitFile
	 * @return the data as an HashMap
	 * @throws FileNotFoundException
	 */
	private HashMap<String, Integer> readFacitFile(String facitFile)
			throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(facitFile));
		String currentLine;
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();

		try {
			while ((currentLine = jumpToNextImage(br)) != null) {
				StringTokenizer st = new StringTokenizer(currentLine);
				if (st.countTokens() == 2) {
					tempMap.put(st.nextToken(),
							Integer.parseInt(st.nextToken()));
				} else {
					System.err.println("Error in file format: <" + facitFile
							+ ">");
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return tempMap;
	}

	/**
	 * Reads lines until tempLine contains the first line of a image.
	 * 
	 * @param br
	 *            bufferedReader
	 * @return the next line to read
	 * @throws IOException
	 */
	private String jumpToNextImage(BufferedReader br) throws IOException {
		String tempLine;
		while ((tempLine = br.readLine()) != null
				&& (tempLine.equals("") || tempLine.startsWith("#"))) {
		}
		return tempLine;
	}

	/**
	 * Getter for testData
	 * 
	 * @return testData: ArrayList<FaceData>
	 */
	public ArrayList<FaceData> getTestData() {
		return testData;
	}

	/**
	 * Getter for trainingData
	 * 
	 * @return trainingData: ArrayList<FaceData>
	 */
	public ArrayList<FaceData> getTrainingData() {
		return trainingData;
	}

	/**
	 * Getter for facit file
	 * 
	 * @return facit: HashMap<String, Integer>
	 */
	public HashMap<String, Integer> getFacit() {
		return facit;
	}
}