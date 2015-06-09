import java.util.Random;

/**
 * Node: This class is used by the neural network and represents a position in
 * the image. For the perceptron to be able to learn, each Node contains 4
 * weights, one weight for each facial expression. When the network is learning
 * it adjusts the node weights depending on the result it gets when checking
 * against each expression.
 */
public class Node {

	private int nodeValue;
	private final double[] weights;

	/**
	 * Constructor, initiates the weights array and randomizes values for each
	 * index as a double between -10 and 10.
	 */
	public Node() {
		weights = new double[4];
		Random r = new Random();
		for (int i = 0; i < weights.length; i++) {
			weights[i] = -10 + (20) * r.nextDouble();
		}
	}

	/**
	 * Setter for the weights
	 *
	 * @param i
	 *          which index to set
	 * @param value
	 *          the value to set.
	 */
	public void setWeights(int i, double value) {
		this.weights[i] = value;
	}

	/**
	 * Getter for nodeValue
	 *
	 * @return nodeValue: Integer
	 */
	public int getNodeValue() {
		return nodeValue;
	}

	/**
	 * Getter for the weights array.
	 *
	 * @return weights: double[]
	 */
	public double[] getWeights() {
		return weights;
	}

	/**
	 * Setter for nodeValue
	 *
	 * @param val
	 *          value to set
	 */
	public void setNodeValue(int val) {
		nodeValue = val;
	}
}