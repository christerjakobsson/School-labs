
public class TestSetUpTearDown implements TestClass {
	private MyDouble myDouble;
	
	public TestSetUpTearDown() {
	}
	
	public boolean testPlus() {
		myDouble.Plus();
		return myDouble.getValue() == 3;
	}
	
	public void setUp() {
		 myDouble = null;
		 myDouble.Plus();
	}
	
	public void tearDown() {
		myDouble = null;
		myDouble.Squared();
	}
}
