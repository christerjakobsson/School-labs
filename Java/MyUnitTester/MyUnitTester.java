/*
 * MyUnitTester
 *
 * Version 1.0
 *
 * Christer Jakobsson
 *
 * Class thats runs a class made for testing another class.
 */
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

/**
 *
 * The purpose of this class is to test a class much like JUnit3 does.
 *
 * @author Christer
 */
public class MyUnitTester {

	private int testFailed;
	private int testSucceded;
	private int testFailByException;
	private String newLine = "\n";
	private String printResult;

	/**
	 * Empty constructor, creates the MyUnitTester object.
	 */
	public MyUnitTester() {
	}

	/**
	 * Method that runs the tests in the given class, prints the results.
	 *
	 * @param className
	 *            String with the name of the class that will be tested.
	 *
	 * @throws ClassNotFoundException
	 *             Thrown Exception if class not found.
	 */
	public String runTests(String className) throws ClassNotFoundException {

		if (checkIfTest(className)) {

			Method[] methods = null;
			Class<?> x = null;
			Object obj = null;

			printResult = "";
			testFailed = 0; /* Counter for failed tests */
			testSucceded = 0; /* Counter for successful tests */
			testFailByException = 0; /* Counter for tests failed by exception */

			x = Class.forName(className);
			methods = x.getMethods();

			/* Creates a class object of the class. */
			try {
				obj = x.newInstance();
			} catch (InstantiationException e1) {
				JOptionPane.showMessageDialog(null, "Failed to create instance"
						+ " of the class "+className, "Error",
						JOptionPane.ERROR_MESSAGE);
//				printResult = "Failed to create instance of the class " + className
//						+ newLine;
			} catch (IllegalAccessException e1) {
				JOptionPane.showMessageDialog(null, "Failed to create instance"
						+ " of the class " + className, "Error",
						JOptionPane.ERROR_MESSAGE);
//				printResult = "Failed to create instance of the class "
//						+ className + newLine;
			}

			/* Saves the setUp and tearDown methods for easier access */
			Method setUp = null;
			Method tearDown = null;

			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().contentEquals("setUp")) {
					setUp = methods[i];
				}
				if (methods[i].getName().contentEquals("tearDown")) {
					tearDown = methods[i];
				}
			}

			/* Invokes methods that start their name with "test" */
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().startsWith("test")) {
					printResult += methods[i].getName();

					try {
						if (setUp != null) {
							setUp.invoke(obj);
						}

						try {
							if (!(Boolean)methods[i].invoke(obj)) {
								printResult += ": Fail" + newLine;
								testFailed++;
							} else {
								printResult += ": Success" + newLine;
								testSucceded++;
							}

						} catch (IllegalAccessException e) {
							JOptionPane.showMessageDialog(null, "Failed to run "
									+ methods[i].getName()
									+ ", method does not have access to the "
									+ "definition of the class" + newLine,
									"Error", JOptionPane.ERROR_MESSAGE);

						} catch (IllegalArgumentException e) {
							JOptionPane.showMessageDialog(null, "Failed to run "
									+ methods[i].getName()
									+ ", method got passed wrong arguments?",
									"Error", JOptionPane.ERROR_MESSAGE);

						} catch (InvocationTargetException e) {
							printResult += ": Fail, generated a "
									+ e.getTargetException() + newLine;
							testFailByException++;
						}

						if (tearDown != null) {
							tearDown.invoke(obj);
						}

					} catch (IllegalAccessException e) {
						JOptionPane.showMessageDialog(null, "Failed to run setUp or tearDown,"
								+ " test not run", "Error",
								JOptionPane.ERROR_MESSAGE);

						printResult += "Failed to run setUp or tearDown,"
								+ " test not run" + newLine;
					} catch (IllegalArgumentException e) {
						JOptionPane.showMessageDialog(null, "Failed to run setUp or tearDown,"
								+ " test not run", "Error",
								JOptionPane.ERROR_MESSAGE);
					} catch (InvocationTargetException e) {
						printResult += ": Fail at setUp or tearDown, test not run.";
					}
				}
			}

			/* Adds a summary of the result */
			printResult += newLine + newLine + testSucceded + " tests succeded"
					+ newLine;
			printResult += testFailed + " tests failed" + newLine;
			printResult += testFailByException + " tests failed because of "
					+ "an exception" + newLine + newLine;
		}
		return printResult;
	}

	/**
	 * This method tests if the name the user entered as the class he/she wanted
	 * to test is a correct class, implements TestClass and have a constructor
	 * that take zero parameters.
	 *
	 * @param testName
	 *            String with the name of the class that will be checked.
	 * @return True if the class is a correct testClass, else false.
	 *
	 * @throws ClassNotFoundException
	 *             Thrown Exception if class not found.
	 */
	public boolean checkIfTest(String testName) throws ClassNotFoundException {
		Class<?> x = null;
		Constructor<?>[] constructors = null;
		Class<?>[] temp = null;

		boolean interfaceOk = false;
		boolean constructorOk = false;

		x = Class.forName(testName);

		constructors = x.getConstructors();
		temp = x.getInterfaces();

		/* Checks if the class implements the interface TestClass */
		if (temp != null) {
			for (int i = 0; i < temp.length; i++) {
				if (temp[i].getName().equals("TestClass")) {
					interfaceOk = true;
				}
			}
		}

		/*
		 * Checks that the constructor isn't null and that there only is one
		 * constructor
		 */
		if (constructors != null && constructors.length == 1) {

			/* Checks that the constructor doesn't take any parameters */
			if (constructors[0].getParameterAnnotations().length == 0) {
				constructorOk = true;
			}
		}

		if (!constructorOk) {
			JOptionPane.showMessageDialog(null, "Fail, either there is more"
					+ " than one constructor or the constructor takes "
					+ "parameters", "Error", JOptionPane.ERROR_MESSAGE);
		}

		if (!interfaceOk) {
			JOptionPane.showMessageDialog(null, "Fail, the class doesnt "
					+ "implement TestClass", "Error",
					JOptionPane.ERROR_MESSAGE);

		}

		return interfaceOk && constructorOk;
	}
}