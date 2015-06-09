/**
 * @author Christerdv12cjn
 */

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		FileReader myFileReader = null;
	
		try {
			myFileReader = new FileReader("maze2.maze");
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!");
		}
		try {
			Maze theMaze = new Maze(myFileReader);
			Robot handBot = new RightHandRuleRobot(theMaze);
			Robot memBot = new MemoryRobot(theMaze);
			
			int steps = 0;
			while(!memBot.hasReachedGoal() && !handBot.hasReachedGoal()) {
				handBot.move();
				memBot.move();
				System.out.print("memBot(x,y) = ");
				System.out.print(memBot.getCurrentPosition().getX()+",");
				System.out.println(memBot.getCurrentPosition().getY());
				System.out.println(memBot.facing);
					
				System.out.print("handBot(x,y) = ");
				System.out.print(handBot.getCurrentPosition().getX()+",");
				System.out.println(handBot.getCurrentPosition().getY());
				
				steps++;
			}
			if(memBot.hasReachedGoal()) {
				System.out.print("MemoryRobot Won! It took him "+steps+" steps");
			}
			if(handBot.hasReachedGoal()) {
				System.out.print("RightHandRobot Won! It took him "+steps+" steps");
			}
		} catch(RuntimeException e) {
			e.printStackTrace();
			System.out.println("Invalid maze format");
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong when creating the matris");
		}
	}
}