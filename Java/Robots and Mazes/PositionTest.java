/**
 * @author Christer dv12cjn
 */
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PositionTest {
	Position testPos;
	
	@Before
	public void setUp() {
		testPos = new Position(4,3);	
	} 
	
	@Test
	public final void testPosition() {
		assertEquals(new Position(4,3), testPos);
	}
	
	@Test 
	public void testNull() {
		assertNotNull(testPos);
	}

	@Test
	public void testGetY() {
		assertEquals(3, testPos.getY());	
	}
	
	@Test
	public  void testGetX() {
		assertEquals(4, testPos.getX());
	}

	@Test
	public void testGetPosToSouth() {
		assertEquals(new Position(4,4),testPos.getPosToSouth());
	}

	@Test
	public void testGetPosToNorth() {
		assertEquals(new Position(4,2),testPos.getPosToNorth());
	}

	@Test
	public void testGetPosToWest() {
		assertEquals(new Position(3,3),testPos.getPosToWest());
	}

	@Test
	public void testGetPosToEast() {
		assertEquals(new Position(5,3), testPos.getPosToEast());
	}
	
	public void equalsTest() {
		testPos = new Position(4,4);
		assertTrue(testPos.equals(new Position(4,4)));
	}
}
