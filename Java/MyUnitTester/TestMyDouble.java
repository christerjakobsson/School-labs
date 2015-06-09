
public class TestMyDouble implements TestClass{

	private MyDouble myDouble;

	public TestMyDouble() {
	}

	public void setUp() {
		myDouble = new MyDouble();
	}

	/* This test should succeed */
	public void tearDown() {
		myDouble = null;
	}

	/* This test should succeed */
	public boolean testInitialisation() {
		return (myDouble.getValue()==2);
	}

	/* This test should succeed */
	public boolean testDoubler() {
		myDouble.Doubler();
		return myDouble.getValue()==4;
	}

	/* This test should succeed */
	public boolean testSquared() {
		myDouble.Squared();
		return myDouble.getValue()==4;
	}

	/* This test should succeed */
	public boolean testMinus() {
		myDouble.Minus();
		return myDouble.getValue()==1;
	}

	/* This test should succeed */
	public boolean testPlus() {
		myDouble.Plus();
		return myDouble.getValue()==3;
	}

	/* This test should fail */
	public boolean testFailingByException() {
		myDouble=null;
		myDouble.Minus();
		return true;
	}

	/* This test should fail */
	public boolean testFailing() {
		return false;
	}

}
