package com.mydoku;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class that is the model for the game, contains all the rules for a sudoku
 * game, the game board and methods used to check if game is correct and if game
 * is completed.
 * 
 * @author Christer
 * @version 1.0
 * @since 2014-08-21
 */
public class MydokuModel {

	private Square[] solution;
	private int[] game;
	private int[] originalGame;
	private boolean[] check;
	private boolean isHint;
	private int hintIndex;
	private int hintVal;
	private final static int SIZE = 81;

	/**
	 * Contstructor, sets the size of the game board. Uses the standard 9x9 grid
	 * represented by arrays of size 81
	 */
	public MydokuModel() {
		isHint = false;
		game = new int[SIZE];
		solution = new Square[SIZE];
		originalGame = new int[SIZE];
		check = new boolean[SIZE];
	}

	/**
	 * Creates a new game.
	 */
	public void newGame(int difficulty) {
		generateGrid();
		copy(solution, game);

		makeGame(difficulty, game);
		originalGame = game.clone();

	}

	/**
	 * Used during development for debugging.
	 * 
	 * @param arrayToPrint
	 */
	@SuppressWarnings("unused")
	private void printGame(int[] arrayToPrint) {
		for (int i = 0; i < arrayToPrint.length; i++) {
			if (i % 9 == 0) {
				System.out.println();
			}
			if (arrayToPrint[i] != 0) {
				System.out.print(arrayToPrint[i] + " ");
			} else {
				System.out.print(" 0 ");
			}
		}
		System.out.println();
	}

	/**
	 * Gets the value that the parameter index should have.
	 * 
	 * @param index
	 * @return
	 */
	public int getSolutionCell(int index) {
		return solution[index].getValue();
	}

	/**
	 * Creates the game array from the generated Square array.
	 * 
	 * @param solution
	 * @param game
	 */
	private void copy(Square[] solution, int[] game) {
		for (int i = 0; i < solution.length; i++) {
			game[i] = solution[i].getValue();
		}
	}

	/**
	 * Prints the grid to system out.
	 */
	public void printGrid(Square[] printArray) {
		for (int i = 0; i < printArray.length; i++) {
			if (i % 9 == 0) {
				System.out.println();
			}
			if (printArray[i].getValue() != 0) {
				System.out.print(printArray[i].getValue() + " ");
			} else {
				System.out.print("  ");
			}
		}
	}

	/**
	 * Constructs the game array, removes a number of random cells depending on
	 * which difficulty the player have chosen.
	 * 
	 * @param difficulty
	 * @param game
	 */
	public void makeGame(int difficulty, int[] game) {
		// easy 42 empty cells
		if (difficulty == 1) {
			removeRandomCells(game, 42);
		}
		// medium 47 empty cells
		if (difficulty == 2) {
			removeRandomCells(game, 47);
		}
		// hard 51 empty cells
		if (difficulty == 3) {
			removeRandomCells(game, 51);
		}
	}

	/**
	 * Removes set number of cells from the solution array. Random which cells
	 * will be removed.
	 * 
	 * @param game
	 * @param cellsToBeRemoved
	 */
	private void removeRandomCells(int[] game, int cellsToBeRemoved) {
		int[] emptyCells;
		emptyCells = new int[cellsToBeRemoved];
		Random rand = new Random();

		for (int i = 0; i < emptyCells.length; i++) {
			emptyCells[i] = rand.nextInt(80) + 1;
		}

		boolean keepGoing = true;
		while (keepGoing) {
			keepGoing = false;
			for (int i = 0; i < emptyCells.length; i++) {
				for (int j = 0; j < emptyCells.length; j++) {
					if (!(i == j)) {
						if (emptyCells[i] == emptyCells[j]) {
							emptyCells[i] = rand.nextInt(80) + 1;
							keepGoing = true;
						}
					}
				}
			}
		}
		for (int i : emptyCells) {
			game[i] = 0;
		}
	}

	/**
	 * Getter for original game array.
	 * 
	 * @return
	 */
	public int[] getOriginalGame() {
		return originalGame;
	}

	/**
	 * Generates the game board as a array of squares.
	 */
	public void generateGrid() {
		clear();

		Square[] square = new Square[SIZE];

		for (int i = 0; i < square.length; i++) {
			square[i] = new Square();
		}

		ArrayList<ArrayList<Integer>> available = new ArrayList<ArrayList<Integer>>();

		for (int x = 0; x < SIZE; x++) {

			ArrayList<Integer> temp = new ArrayList<Integer>();
			for (int i = 0; i < 9; i++) {
				temp.add(i + 1);
			}
			available.add(temp);
		}

		int c = 0;
		while (c != SIZE) {
			if (available.get(c).size() != 0) {
				int i = getRandomNumber(available.get(c).size());
				int z = available.get(c).get(i);

				if (!conflicts(square, createSquare(c, z))) {
					square[c] = createSquare(c, z);
					available.get(c).remove(i);
					c++;
				} else {
					available.get(c).remove(i);
				}
			} else {
				available.get(c).clear();
				for (int y = 0; y < 9; y++) {
					available.get(c).add(y + 1);
				}
				square[c] = new Square();
				if (c != 0) {
					c--;
				}
			}
		}

		for (int j = 0; j < SIZE; j++) {
			solution[j] = square[j];
		}
	}

	/**
	 * Clears all the boards.
	 */
	private void clear() {
		game = new int[SIZE];
		solution = new Square[SIZE];
		originalGame = new int[SIZE];
		check = new boolean[SIZE];
	}

	/**
	 * generates a random number between zero and up to parameter upper.
	 * 
	 * @param upper
	 * @return
	 */
	private int getRandomNumber(int upper) {
		return new Random().nextInt(upper);
	}

	public int getHintVal() {
		return hintVal;
	}

	/**
	 * Checks that the new square doesn't have conflicts with any other of the
	 * squares, follows the rules of sudoku to check that the square is allowed
	 * to be the value it has.
	 * 
	 * @param square
	 * @param test
	 * @return
	 */
	private boolean conflicts(Square[] square, Square test) {

		for (Square s : square) {
			if (s.getAcross() != 0 && s.getAcross() == test.getAcross()) {
				if (s.getValue() == test.getValue()) {
					return true;
				}
			} else if (s.getDown() != 0 && s.getDown() == test.getDown()) {
				if (s.getValue() == test.getValue()) {
					return true;
				}
			} else if (s.getRegion() != 0 && s.getRegion() == test.getRegion()) {
				if (s.getValue() == test.getValue()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Creates a square and sets all its values.
	 * 
	 * @param number
	 * @param value
	 * @return
	 */
	private Square createSquare(int number, int value) {
		Square item = new Square();

		number += 1;
		item.setAcross(getRow(number));
		item.setDown(getColumn(number));
		item.setRegion(getRegionFromNumber(number));
		item.setValue(value);
		item.setIndex(number - 1);

		return item;
	}

	/**
	 * Get which region a certain index is part of.
	 * 
	 * @param index
	 * @return
	 */
	private int getRegionFromNumber(int index) {
		int k = 0;
		int a = getRow(index);
		int d = getColumn(index);

		if (1 <= a && a < 4 && 1 <= d && d < 4) {
			k = 1;
		} else if (4 <= a && a < 7 && 1 <= d && d < 4) {
			k = 2;
		} else if (7 <= a && a < 10 && 1 <= d && d < 4) {
			k = 3;
		} else if (1 <= a && a < 4 && 4 <= d && d < 7) {
			k = 4;
		} else if (4 <= a && a < 7 && 4 <= d && d < 7) {
			k = 5;
		} else if (7 <= a && a < 10 && 4 <= d && d < 7) {
			k = 6;
		} else if (1 <= a && a < 4 && 7 <= d && d < 10) {
			k = 7;
		} else if (4 <= a && a < 7 && 7 <= d && d < 10) {
			k = 8;
		} else if (7 <= a && a < 10 && 7 <= d && d < 10) {
			k = 9;
		}

		return k;
	}

	/**
	 * Used by createSquare to get which column the square belongs in.
	 * 
	 * @param n
	 * @return
	 */
	private int getColumn(int n) {
		int k;
		if (getRow(n) == 9) {
			k = n / 9;
		} else {
			k = (n / 9) + 1;
		}

		return k;
	}

	/***
	 * Used by createSquare to get which row the square belongs to.
	 * 
	 * @param n
	 * @return
	 */
	private int getRow(int n) {
		int k;
		k = n % 9;

		if (k == 0) {
			return 9;
		} else
			return k;
	}

	/**
	 * Getter for solution.
	 * 
	 * @return
	 */
	public int[] getGame() {
		return game;
	}

	/**
	 * Sets the cell with the index to the value of val, all cells with a zero
	 * as value are cells which the user needs to set.
	 * 
	 * @param index
	 * @param val
	 */
	public void setValue(int index, int val) {
		if (originalGame[index] == 0) {
			game[index] = val;
		}
	}

	/**
	 * Gets the value stored in this place.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public int getVal(int x, int y) {
		return game[convertFromRowCol(x, y)];
	}

	/**
	 * Sets the value of the hint and which index it has.
	 * 
	 * @param index
	 * @param val
	 */
	public synchronized void setHint(int index, int val) {
		if (val == 0) {
			isHint = false;
		} else {
			isHint = true;
			hintIndex = index;
		}
		System.out.println("index " + index + " Value " + val);
		if (originalGame[index] == 0) {
			hintVal = val;
		}
	}

	/**
	 * Getter for the hints index
	 * 
	 * @return
	 */
	public synchronized int getHintIndex() {
		return hintIndex;
	}

	/**
	 * Returns true if there is a hint present on the board or not.
	 * 
	 * @return
	 */
	public synchronized boolean isHint() {
		return isHint;
	}

	/**
	 * Converts from a Row, Column orientation to a array index. A sudoku board
	 * is a 9*9 grid, but i have chosen to represent it as a one dimensional
	 * array.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int convertFromRowCol(int x, int y) {
		int result = 0;
		while (y > 0) {
			result += 9;
			y--;
		}
		result += x;
		return result;
	}

	/**
	 * checks if all cells in the game is filled.
	 * 
	 * @return
	 */
	public boolean isAllCellsFilled() {

		for (int i = 0; i < game.length; i++) {
			if (game[i] == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all values in the game array is correct.
	 * 
	 * @return True if all values are correct.
	 */
	public boolean checkGame() {
		for (int i = 0; i < game.length; i++) {
			if (game[i] != 0) {
				check[i] = game[i] == solution[i].getValue();
			} else {
				check[i] = true;
			}
		}

		for (int i = 0; i < check.length; i++) {
			if (!check[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the originalGame arrays value in a specific cell.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public int getOriginalGameVal(int i, int j) {
		return originalGame[convertFromRowCol(i, j)];

	}

	/**
	 * Setter for game.
	 * 
	 * @param intArray
	 */
	public void setGame(int[] intArray) {
		game = intArray;
	}

	/**
	 * Setter for originalGame.
	 * 
	 * @param intArray
	 */
	public void setOriginalGame(int[] intArray) {
		originalGame = intArray;
	}

	/**
	 * Setter for solution.
	 * 
	 * @param intArray
	 */
	public void setSolution(Square[] intArray) {
		solution = intArray;
	}

	/**
	 * Setter for check array.
	 * 
	 * @param booleanArray
	 */
	public void setCheck(boolean[] booleanArray) {
		check = booleanArray;
	}

	/**
	 * Getter for check.
	 * 
	 * @return
	 */
	public boolean[] getCheck() {
		return check;
	}

	/**
	 * Generates a hint from any of the cells which is filled with a zero which
	 * means that they dont have a value yet.
	 * 
	 * @return
	 */
	public synchronized int generateHint() {
		Random rng = new Random();
		int random = rng.nextInt(80);

		do {
			random = rng.nextInt(80);
		} while (game[random] != 0);

		System.out.println("Random " + random);
		return random;
	}
}