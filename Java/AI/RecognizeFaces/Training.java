import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Training: Trains the Perceptron. Uses the data read from FileHandler and
 * trains until it has a suitable percentage of correct answers on a set of
 * images.
 * 
 */
public class Training {

	private final static int IMAGE_SIZE = 20;
	private ArrayList<FaceData> allData;
	private ArrayList<FaceData> trainingData;
	private ArrayList<FaceData> testTrainingData;
	private HashMap<String, Integer> facit;
	private final Node[][] neuralNetwork;
	private double learningRate = 1;
	private double percent = 0;
	private long counter;

	/**
	 * Constructor initializes a matrix which is the neural network for each
	 * index corresponding to a pixel in the image.
	 */
	public Training() {
		neuralNetwork = new Node[IMAGE_SIZE][IMAGE_SIZE];
		for (int i = 0; i < IMAGE_SIZE; i++) {
			for (int j = 0; j < IMAGE_SIZE; j++) {
				neuralNetwork[i][j] = new Node();
			}
		}
	}

	/**
	 * Set trainingData
	 * 
	 * @param trainingData
	 */
	public void setTrainingData(ArrayList<FaceData> trainingData) {
		this.allData = trainingData;
	}

	/**
	 * Set facit file.
	 * 
	 * @param facit
	 */
	public void setFacit(HashMap<String, Integer> facit) {
		this.facit = facit;
	}

	/**
	 * Takes the list containing all image files and splits it into two lists,
	 * one for learning and one for testing when the learning is complete.
	 */
	private void populateLists() {
		int testSize = (int) (allData.size() * 0.25);
		testTrainingData = new ArrayList<FaceData>(allData.subList(0, testSize));
		trainingData = new ArrayList<FaceData>(allData.subList(testSize,
				allData.size()));
	}

	/**
	 * Trains the network once with all the images in the trainingData list and
	 * adjusts each nodes weights depending on the error values. Then runs the
	 * scoreChecker to check if the network has learned enough, and the loop can
	 * quit.
	 * 
	 * @return true if training worked, else false.
	 */
	public boolean startTraining() {
		if (allData == null || facit == null) {
			return false;
		}

		int iterate = 0;
		do {
			iterate++;
			Collections.shuffle(allData);
			populateLists();

			for (FaceData f : trainingData) {
				buildNetwork(f);
				for (int i = 0; i < 4; i++) {
					double act = calculateActivation(i + 1);
					adjustWeights(i + 1, facit.get(f.getImageID()), act);
				}
			}

			if (learningRate + 0.01 > 0) {
				learningRate -= 0.01;
			}
		} while (scoreChecker());
		System.err.println("Training iterations " + iterate);
		return true;
	}

	/**
	 * Sets the networks nodevalues to the pixelvalues from the image. This is
	 * then used to calculate the errors and adjust the weights.
	 * 
	 * @param f
	 */
	private void buildNetwork(FaceData f) {
		for (int i = 0; i < f.getFaceData().length; i++) {
			for (int j = 0; j < f.getFaceData()[i].length; j++) {
				neuralNetwork[i][j].setNodeValue(f.getFaceData()[i][j]);
			}
		}
	}

	/**
	 * Calculates the activation record for the current mood. Used to train the
	 * neural network and to check which mood a picture is.
	 * 
	 * @param mood
	 * @return the activation record, 1 if activated, else 0.
	 */
	private double calculateActivation(int mood) {
		double aSum = 0;
		for (int i = 0; i < neuralNetwork.length; i++) {
			for (int j = 0; j < neuralNetwork[i].length; j++) {
				aSum += neuralNetwork[i][j].getNodeValue()
						* neuralNetwork[i][j].getWeights()[mood - 1];
			}
		}
		return activationFunc(aSum);
	}

	/**
	 * Activation function.
	 * 
	 * @param aSum
	 * @return 1 is aSum is > 0.5, 0 otherwise
	 */
	private double activationFunc(double aSum) {
		if (aSum > 0.5) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Adjust the weights for each node based on the error value, learning rate
	 * and the nodeValue.
	 * 
	 * @param mood
	 *            which mood to calculate and adjust for, range allowed [1-4]
	 * @param curFacit
	 *            which mood the face actual is in
	 * @param act
	 *            activation value for the current face and mood
	 */
	private void adjustWeights(int mood, int curFacit, double act) {
		int desiredOutput = 0;
		if (curFacit == mood) {
			desiredOutput = 1;
		}

		double error = desiredOutput - act;
		if (error != 0) {
			for (int i = 0; i < neuralNetwork.length; i++) {
				for (int j = 0; j < neuralNetwork[i].length; j++) {
					double adjustVal = (learningRate * error * neuralNetwork[i][j]
							.getNodeValue());

					neuralNetwork[i][j].setWeights(mood - 1,
							neuralNetwork[i][j].getWeights()[mood - 1]
									+ adjustVal);
				}
			}
		}
	}

	/**
	 * Checks if the neural network has learned enough to be done. Important to
	 * not train to little or to much.
	 * 
	 * @return true if training is complete false otherwise
	 */
	private boolean scoreChecker() {
		int hitCount = 0;
		int mood = 0;

		for (FaceData fd : testTrainingData) {
			int[] acts = new int[4];
			buildNetwork(fd);

			for (int i = 0; i < 4; i++) {
				double act = calculateActivation(i + 1);
				if (act == 1) {
					acts[i]++;
					mood = i + 1;
				}
			}

			if (!isOnlyOneAct(acts)) {
				return true;
			}

			if (facit.get(fd.getImageID()) == mood) {
				hitCount++;
			}
		}

		if (((double) hitCount / (double) testTrainingData.size() * 100) > percent) {
			percent = ((double) hitCount / (double) testTrainingData.size() * 100);
		}

		if (percent >= 85) {
			counter++;
			if (counter == 3)
				return false;
		} else {
			counter = 0;
		}
		return true;
	}

	/**
	 * Goes through all the images in the data array and calculates the act for
	 * all facial expressions and if the act is equal to 1, Guess that the image
	 * shows that expression and print, the image id a and which facial
	 * expression guessed on in the same format as the facit file.
	 * 
	 * @param data
	 *            ArrayList containing all the images.
	 */
	public void runGuesses(ArrayList<FaceData> data) {
		for (FaceData fd : data) {
			buildNetwork(fd);
			boolean guess = true;
			int[] acts = new int[4];

			for (int i = 0; i < 4; i++) {
				double act = calculateActivation(i + 1);
				if (act == 1) {
					acts[i]++;
					guess = false;
				}
			}
			if (isOnlyOneAct(acts)) {
				for (int i = 0; i < acts.length; i++) {
					if (acts[i] == 1) {
						System.out.println(fd.getImageID() + " " + (i + 1));
					}
				}
			} else if (!guess) {
				Random r = new Random();
				int g = r.nextInt(4);
				while (acts[g] == 0) {
					g = r.nextInt(4);
				}
				System.out.println(fd.getImageID() + " " + (g + 1));
			} else {
				Random r = new Random();
				int g = r.nextInt(3) + 1;
				System.out.println(fd.getImageID() + " " + g);
			}
		}
	}

	/**
	 * Checks if there is more then one of the indexes that has the value 1,
	 * return true if only one is, else false.
	 * 
	 * @param acts
	 * @return
	 */
	private boolean isOnlyOneAct(int[] acts) {
		int times = 0;
		for (int i : acts) {
			if (i == 1) {
				times++;
			}
		}
		return times == 1;
	}
}