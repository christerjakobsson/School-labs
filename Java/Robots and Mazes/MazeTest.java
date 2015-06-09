/**
 * @author Christer dv12cjn
 */
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;

public class MazeTest extends TestCase {
	Maze testM;
	Position testPos;
	@Before
	public void setUp() throws FileNotFoundException, Exception {;
	 testM = new Maze(new FileReader("maze.maze"));
	 testPos = null;
	} //Method createMaze


	@Test
	public void testMazeNull() throws FileNotFoundException, Exception {
		assertNotNull(testM);
	}
	 	
	@Test
	public void testIsMovable() throws Exception {
		testPos = new Position(0,0);
		assertFalse(testM.isMovable(testPos));
	}


	@Test
	public void testIsGoal() {
		testPos = new Position(8,6);
		assertTrue(testM.isGoal(testPos));
	}

	@Test
	public void testGetStartPosition() throws Exception {
		testPos = new Position(1,0);
		assertEquals(testPos, testM.getStartPosition());
	}

}