/**
 * @author Christer jakobsson dv12cjn
 */
public class RightHandRuleRobot extends Robot {
	
	/** Inherits the maze from Robot and sets the starting position for the
	 * RightHandRuleBot. 
	 * 
	 * @param maze
	 */
	public RightHandRuleRobot(Maze maze) {
		super(maze);
			try {
				super.setCurrentPosition(maze.getStartPosition());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	/** Moves the RightHandRuleRobot one step to a position based on its facing
	 * and that it should always have a wall to the right of its facing.
	 * If it can't go forward it turns right, and if it can't move right it
	 * turns left.
	 */
	public void move() {			
		
		current = super.getCurrentPosition();
		switch(facing) {
			case 'N': {
				if(isWall(current.getPosToNorth()) && 
						isWall(current.getPosToEast())) {
					turnLeft(facing);
					move();
				}
				else if(!isWall(current.getPosToEast())) {
					turnRight(facing);
					nextPos = current.getPosToEast();
					super.setCurrentPosition(nextPos);
				}
				else if(!isWall(current.getPosToNorth())) {
					nextPos = current.getPosToNorth();
					super.setCurrentPosition(nextPos);
				}
				break;
			}
			case 'S': {
				if(isWall(current.getPosToSouth()) && 
						isWall(current.getPosToWest())) {
					turnLeft(facing);
					move();
				}
				else if(!isWall(current.getPosToWest())) {
					turnRight(facing);
					nextPos = current.getPosToWest();
					super.setCurrentPosition(nextPos);
				}
				else if(!isWall(current.getPosToSouth())) {
					nextPos = current.getPosToSouth();
					super.setCurrentPosition(nextPos);
				}
				break;
			}		
			case 'E': {	 
				if(isWall(current.getPosToEast()) && 
						isWall(current.getPosToSouth())) {
					turnLeft(facing);
					move();
				}
				else if(!isWall(current.getPosToSouth())) {
					turnRight(facing);
					nextPos = current.getPosToSouth();
					super.setCurrentPosition(nextPos);
				}
				else if(!isWall(current.getPosToEast())) {
					nextPos = current.getPosToEast();
					super.setCurrentPosition(nextPos);
				}
				break;
			}
			case 'W': {
			if(isWall(current.getPosToWest()) && 
					isWall(current.getPosToNorth())) {
				turnLeft(facing);
				 move();
			 }
			else if(!isWall(current.getPosToNorth())) {
				turnRight(facing);
				nextPos = current.getPosToNorth();
				super.setCurrentPosition(nextPos);
			}
			else if(!isWall(current.getPosToWest())) {
				nextPos = current.getPosToWest();
				super.setCurrentPosition(nextPos);
			}
			break;
			}		
		}
	}
	
	public boolean isWall(Position pos) {
		return !maze.isMovable(pos);
	}
}
