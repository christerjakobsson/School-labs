/**
 * @author Christer Jakobsson dv1cjn
 */
public abstract class Robot {
	private Position position;
	protected Maze maze;
	protected char facing = 'S';
	protected Position current;
	protected Position nextPos;
	
	/** maze inherits the properties from the parameter maze.
	 * maze.
	 * 
	 * @param maze
	 */
	public Robot(Maze maze) {
		this.maze = maze;
	}
	
	/** An abstract method so that there can be different types of robots and 
	 * they all use the same abstract method to move.
	 * 
	 */
	public abstract void move();
	
	/** Returns the current position.
	 * 
	 * @return a variable position
	 */
	public Position getCurrentPosition() {
		return this.position;
		
	}
	
	/** sets the current position to the parameter position.
	 * 
	 * @param position 
	 */
	protected void setCurrentPosition(Position position) {
		this.position = position;
	}
	
	/** Checks if the variable position is the goal.
	 * 
	 * @return true if position is goal, else returns false.
	 */
	public boolean hasReachedGoal() { 
		if(maze.isGoal(this.position) == true) {
			return true;
		} else return false;
	}
	
	/** Changes the current facing, simulating that the robot turns left.
	 * 
	 * @param facing
	 */
	protected void turnLeft(char facing) {
		if(facing == 'N') {
			this.facing = 'W';
		}
		else if(facing == 'E') {
			this.facing = 'N';
		}
		else if(facing == 'S') {
			this.facing = 'E';
		}
		else if(facing == 'W') {
			this.facing = 'S';
		}
		
	}

	/** Changes the current facing to simulate that the robot 
	 * turned right.
	 * 
	 * @param facing
	 */
	protected void turnRight(char facing) {
		if(facing  == 'N') {
			this.facing = 'E';
		}
		else if(facing == 'E') {
			this.facing = 'S';
		}
		else if(facing == 'S') {
			this.facing = 'W';
		}
		else if(facing == 'W') {
			this.facing = 'N';
		}
	}
}
