import java.io.FileNotFoundException;

/**
 * Faces: Main class of the program, used to get the program arguments and then
 * create a FaceHandler which reads each of the files and stores the data from
 * them in suitable data structures. Faces then Creates a training class and
 * starts the training on the network, gives the filedata to the training. After
 * the training is complete and has returned true Faces runs the test on the
 * given test file.
 */
public class Faces {

	/**
	 * Constructor which runs training and the guessing.
	 * 
	 * @param args
	 */
	private Faces(String[] args) {
		try {
			FileHandler fh = new FileHandler(args[0], args[1], args[2]);
			Training training = new Training();
			training.setTrainingData(fh.getTrainingData());
			training.setFacit(fh.getFacit());

			if (training.startTraining()) {
				training.runGuesses(fh.getTestData());
			} else {
				System.err.println("Failure when training");
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
		}
	}

	/**
	 * Main that checks if the program arguments is correct size and if so sends
	 * the args to Faces
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 3) {
			new Faces(args);
		} else {
			System.err.println("Not enough arguments to run program.");
		}
	}
}