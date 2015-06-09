/** 
 * @author Christer dv12cjn
 */ 
import java.util.ArrayList;
import java.util.Stack;

public class MemoryRobot extends Robot {
	private Stack<Position> places = new Stack<Position>();
	private ArrayList<Position> markedPos = new ArrayList<Position>();
		
	/** Inherits the maze from Robot and sets the starting position for the 
	 * memoryRobot.
	 * 
	 * @param maze
	 */
	public MemoryRobot(Maze maze) {
		super(maze);
			try {
				super.setCurrentPosition(maze.getStartPosition());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/*
	/** Moves the MemoryRobot one position based on its heading, if there
	 * is a wall in that direction it will turn right and try moving forward in
	 * that direction.
	 * If it gets to a dead end it will pop the stack until it finds a new 
	 * route.
	 */
	public void move() {
		current = super.getCurrentPosition();
		
		if(possibleDirections()) {
		
			switch(facing) {
				case 'E': {
					nextPos = current.getPosToEast();
					if(maze.isMovable(nextPos) && 
							!(markedPos.contains(nextPos))) {
						setCurrentPosition(nextPos);
						markedPos.add(nextPos);
						places.push(nextPos);
					} else {
						turnRight(facing);
						move();
					}
					break;
				}
				case 'W': {
					nextPos = current.getPosToWest();
					if(maze.isMovable(nextPos) && 
							!(markedPos.contains(nextPos))) {
						setCurrentPosition(nextPos);
						markedPos.add(nextPos);
						places.push(nextPos);
					} else {
						turnRight(facing);
						move();
					}
					break;
				}
				case 'N': {
					nextPos = current.getPosToNorth();
					if(maze.isMovable(nextPos) && 
							!(markedPos.contains(nextPos))) {
						setCurrentPosition(nextPos);
						markedPos.add(nextPos);
						places.push(nextPos);
					} else {
						turnRight(facing);
						move();
					}
					break;
				}
				case 'S': {
					nextPos = current.getPosToSouth();
					if(maze.isMovable(nextPos) && 
							!(markedPos.contains(nextPos))) {
						setCurrentPosition(nextPos);
						markedPos.add(nextPos);
						places.push(nextPos);
					} else {
						turnRight(facing);
						move();
					}
					break;
				}
			}
		} else {
			setCurrentPosition(places.pop());
		}
	}
	
	/*
	 * Checks if the robot can go to any direction based on if there is
	 * a wall there and if it has been there before.
	 *  returns true if it can go anywhere new and false otherwise.
	 */
	private boolean possibleDirections() {
		Position east = current.getPosToEast();
		Position west = current.getPosToWest();
		Position north = current.getPosToNorth();
		Position south = current.getPosToSouth();
	
		if(maze.isMovable(east) && !(markedPos.contains(east))) {
			return true;
		}
		else if(maze.isMovable(west) && !(markedPos.contains(west))) {
			return true;
		}
		else if(maze.isMovable(north) && !(markedPos.contains(north))) {
			return true;
		}
		else if(maze.isMovable(south) && !(markedPos.contains(south))) {
			return true;
		}
		else return false;
	}
}
