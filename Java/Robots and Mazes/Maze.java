/**
 *  @author Christer dv12cjn
 */
import java.io.BufferedReader;
import java.io.IOException;


public class Maze {
	
	private char[][] mazeData;
	private int rows = 1;
	private int col = 0;
	
	/** Creates a matrix based on the current maze file.
	 * 
	 * @param myFileReader. A FileReader to the current maze file.
	 * @throws Exception 
 	 */
	public Maze(java.io.Reader myFileReader) {
		String data = "";
        String currentLine = null;
        BufferedReader br = new BufferedReader(myFileReader);
        try {
			currentLine = br.readLine();
			col = currentLine.length();
			data += currentLine;
		
			while(((currentLine = br.readLine()) != null)) {
				rows++;
	            data += currentLine;
			}
			mazeData = new char[col][rows];
			int count = 0; 
			for(int i = 0; i < rows; i++) {
	            for(int j = 0; j < col; j++) {
	            	mazeData[j][i] = data.charAt(count);
	            	count++;
	            }
			}
			if(!goalAndStartExist() ){			
				throw new RuntimeException();
			}
        } catch (IOException e) {
			System.exit(1);
		}
}

	/** Checks if there exists a goal in the maze if not prints a error and 
	 *  exits program.
	 * @return 
	 * @throws Exception 
	 */
	private boolean goalAndStartExist() {
		boolean goal = false;
		boolean start = false;
		
			for(int i = 0; i < mazeData[i].length; i++) {
				for(int j = 0; j < mazeData.length; j++) {
					if(mazeData[j][i] == 'G') {
						goal = true;
					}
					if(mazeData[j][i] == 'S') {
						start = true;
					}
				}
		}
			return start && goal;
	}
	

	/** Checks if a position is a wall, if it is return false else returns true.
	 * 
	 * @param position
	 * @return true if the positions not a wall. 
	 * @return false if out of bounds of the matrix.
	 */
	public boolean isMovable(Position position) {
	
		try {
			return mazeData[position.getX()][position.getY()] != '*';
		} catch (Exception e) {
			return false;
		}
	}
	
	/** Checks if a position is the goal if it is return true else return
	 *  false.
	 * 
	 * @param position		 
	 * @return true if at goal, else false.
	 */
	public boolean isGoal(Position position) {
		
		return mazeData[position.getX()][position.getY()] == 'G';
	}
		
		/** Checks which position is the starting position and
		 *  returns that position.
		 * 
		 * @return the starting position, null if there is no starting point
		 * @throws Exception 
		 */
		public Position getStartPosition() throws Exception{
			for(int i = 0; i < col; i++) {
				for(int j = 0; j < rows; j++) {
					if(mazeData[i][j] == 'S') {
						return new Position(i,j);
					}
				}
			}
			return null;
		}
}