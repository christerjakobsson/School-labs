/**
 * @author Christer dv12cjn
 * 
 * Class thats takes care of positioning in a matrix.
 */
public class Position {
	private int x, y;
	
	/** Sets the variables x and y to the parameters x and y.
	 * 
	 * @param x
	 * @param y
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/** Return the variable x.
	 * 
	 * @return variable x.
	 */
	public int getX() {
		return this.x;
	}
	
	/** Return the variable y.
	 * 
	 * @return variable y.
	 */
	public int getY() {
		return this.y;
	}
	
	/** Returns the position to the south of the current position.
	 * 
	 * @return a Position one step to south from the current position,
	 */
	public Position getPosToSouth() {
		return new Position(x, y+1);
	}
	
	/** Returns the position to the north of the current position.
	 * 
	 * @return a Position one step to north from the current position.
	 */
	public Position getPosToNorth() {
		return new Position(x, y-1);
		
	}
	
	/** Returns a position to the west of the current position.
	 * 
	 * @return a Position one step to west from the current position.
	 */
	public Position getPosToWest() {
		return new Position(x-1, y);	
	}
	
	/** Returns a position to the east of the current position.
	 * 
	 * @return a Position one step to east from the current position.
	 */
	public Position getPosToEast() {
		return new Position(x+1, y);
	}

	@Override
	/**
	 * Creates a hash key depending off current x and y.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	/**
	 *  Checks if two positions are equal returns a boolean.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}