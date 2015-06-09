package com.greed;

/* GreedModel
*
* Purpose: This class is the model for a Dice game called greed, it has methods 
* that are used to create all rules for the game. GreedModel contains all 
* information about the current game such as score, turns and all the dices 
* values and it is GreedModel that has the methods that throws the dice and checks
* if the score is correct and it calculates the score.
*
* v1.0
*
* Copyright (c). 2014-01-28 Christer Jakobsson.
*/
import java.util.Random;

/**
 * Class that is the logic and game rules for a game called Greed.
 * Also contains all values for a game of greed and uses a class Dice.
 * 
 * @author Christer
 *
 */
public class GreedModel {

	
	private static final int SCORE_TO_WIN = 10000;
	private static final int FIRST_THROW_SCORE = 300;
	private int score;
	private int turns;
	private int allDiceUsedScore; 
	private int nrOfThrow;
	private Dice[] dice;
	private int dicesUsed;
	private int[] scoreDice;
	private int[] innerScoreDices;
	private int nrOfDices;

	/**
	 * Constructor that initialises variables for when the user is gonna play with 
	 * 6 dices
	 */
	public GreedModel() {
		nrOfThrow = 0;
		nrOfDices = 6;
		setupDice(6);
		score = 0;
		turns = 0;
		innerScoreDices = new int[nrOfDices + 1];
	}

	/**
	 * Constructor that initialises variables and where the user can choose how many
	 * dices he wants to play with.
	 * 
	 * @param i Dices to be used
	 */
	public GreedModel(int i) {
		nrOfDices = i;
		nrOfThrow = 0;
		setupDice(i);
		score = 0;
		turns = 0;
		innerScoreDices = new int[i + 1];
		allDiceUsedScore = 0;
	}

	/**
	 * Used if all dices gives a score and the player wants to try and throw again
	 * to get more score, saves the score for the current dices and then resets all
	 * dices so they all can be used again.
	 */
	public void allDicesUsedThrowAgain() {
		setTempTurnScore();
		unMarkAndUnlock();
	}

	/**
	 * Calculates the score for the array of dice values thats parameter, return this 
	 * score.
	 * 
	 * @param diceNrValue
	 * @return Score for the dices that get sent in as param
	 */
	private int calcScore(int[] diceNrValue) {
		int tScore = 0;
		dicesUsed = 0;
		boolean isStraight = isStraight();
		
		scoreDice = null;
		scoreDice = new int[dice.length + 1];
		
		if (!isStraight) {
			for (int i = 1; i < diceNrValue.length; i++) {
				if (diceNrValue[i] >= 3) {
					dicesUsed += 3;
					if (i == 1) {
						scoreDice[i] += 3;
						tScore += 1000;
					} else {
						scoreDice[i] += 3;
						tScore += i * 100;
					}

					if (diceNrValue[i] > 3) {
						if (i == 1) {
							scoreDice[i] += diceNrValue[i] - 3;
							dicesUsed += diceNrValue[i] - 3;
							tScore += (diceNrValue[i] - 3) * 100;
						} else if (i == 5) {
							scoreDice[i] += diceNrValue[i] - 3;
							dicesUsed += diceNrValue[i] - 3;
							tScore += (diceNrValue[i] - 3) * 50;
						}
					}
				} else {
					if (i == 1) {
						scoreDice[i] += diceNrValue[i];
						dicesUsed += diceNrValue[i];
						tScore += diceNrValue[i] * 100;
					}
					if (i == 5) {
						scoreDice[i] += diceNrValue[i];
						dicesUsed += diceNrValue[i];
						tScore += diceNrValue[i] * 50;
					}
				}

			}
		} else if(isStraight){
			dicesUsed = 6;
			tScore = 1000;
		}
		return tScore;
	}

	/**
	 * puts the temporary scoreDices in an array. cause scoreDice changes every
	 * time calcScore is called.
	 */
	private void correctScoringDices() {
		for (int i = 0; i < scoreDice.length; i++) {
			innerScoreDices[i] += scoreDice[i];
		}
	}

	/**
	 * Ends the turn, resets variables than need resetting and increases turns.
	 */
	public void endTurn() {
		turns++;
		innerScoreDices = null;
		innerScoreDices = new int[nrOfDices + 1];
		dicesUsed = 0;
		resetDice();
		nrOfThrow = 0;
		allDiceUsedScore = 0;
	}

	/**
	 * Getter for allDiceUsedScore
	 * 
	 * @return an integer with the score when all dices gave score.
	 */
	public int getAllDiceUsedScore() {
		return allDiceUsedScore;
	}

	/**
	 * Creates an integer array with that has how many of each value there is 
	 * amongst the dices. used to see if the first throw gave enough points.
	 * 
	 * @return integer array with how many of each dice value there is.
	 */
	public int[] getAllDiceValues() {
		int[] diceNrValue = new int[nrOfDices+1];

		for (int i = 0; i < dice.length; i++) {
			diceNrValue[dice[i].getVal()]++;
		}
		return diceNrValue;
	}
	
	
	/**
	 * Getter for dice
	 * 
	 * @return an array with Dices
	 */
	public Dice[] getDice() {
		return dice;
	}

	/**
	 * Getter for diceUsed.
	 * 
	 * @return integer that show how many dices gave score.
	 */
	public int getDicesUsed() {
		return dicesUsed;
	}
	
	/**
	 * Creates a integer array with dice values from of all the locked dices.
	 * 
	 * @return integer array with locked dices values.
	 */
	private int[] getLockedDiceValues() {
		int[] diceNrValue = new int[7];

		for (int i = 0; i < dice.length; i++) {
			if (dice[i].isLocked()) {
				diceNrValue[dice[i].getVal()]++;
			}
		}
		return diceNrValue;
	}

	/**
	 * Creates a integer array with dice values from all of the marked dices.
	 * 
	 * @return integer array with marked dice values.
	 */
	private int[] getMarkedDiceValues() {
		int[] diceNrValue = new int[7];

		for (int i = 0; i < dice.length; i++) {
			if (dice[i].isMarked()) {
				diceNrValue[dice[i].getVal()]++;
			}
		}
		return diceNrValue;
	}

	/**
	 * Creates a integer array with dice values from all the non locked dices.
	 * 
	 * @return integer array with non locked dice values.
	 */
	private int[] getNonLockedDiceValues() {
		int[] diceNrValue = new int[nrOfDices + 1];

		for (int i = 0; i < dice.length; i++) {
			if (!dice[i].isLocked()) {
				diceNrValue[dice[i].getVal()]++;
			}
		}
		return diceNrValue;
	}

	/**
	 * Getter for nrOfDices
	 * 
	 * @return Integer that tells how many dices there is.
	 */
	public int getNrOfDices() {
		return nrOfDices;
	}

	/**
	 * Getter for nrOfThrow
	 * 
	 * @return Integer that tells which number of throw it is.
	 */
	public int getNrOfThrow() {
		return nrOfThrow;
	}

	/**
	 * Getter for score.
	 * 
	 * @return Integer that contains the score.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Getter for scoreDice.
	 * 
	 * @return Integer with temporary dices that gave score.
	 */
	public int[] getScoreDice() {
		return scoreDice;
	}

	/**
	 * Getter for turns.
	 * 
	 * @return Integer that tells how many turns there has been.
	 */
	public int getTurns() {
		return turns;
	}
	
	/**
	 * Gets turnscore, the turnscore get calculated by calculating the score 
	 * on the dices that are locked + the score for dices thats not locked and
	 * + allDiceUsedScore.
	 * 
	 * @return The score for the current turn.
	 */
	public int getTurnScore() {
		if(isStraight()) {
			return 1000;
		} else {
			return calcScore(getLockedDiceValues())
				+ calcScore(getNonLockedDiceValues()) + allDiceUsedScore;
		}
	}

	/**
	 * Creates a integer array with dice values from all unmarked dices.
	 * 
	 * @return integer array with dice values for unmarked dices.
	 */
	private int[] getUnmarkedDiceValues() {
		int[] diceNrValue = new int[dice.length + 1];

		for (int i = 0; i < dice.length; i++) {
			if (!dice[i].isMarked()) {
				diceNrValue[dice[i].getVal()]++;
			}
		}
		return diceNrValue;
	}

	/**
	 * Check if score is more than 10000 cause then the player has won.
	 * 
	 * @return True of false depending if the score is more then 10 000 or not.
	 */
	public boolean hasWon() {
		return score >= SCORE_TO_WIN;
	}

	/**
	 * Checks if all dices give points, if so returns true else returns false.
	 * 
	 * @return true if all dices give points false otherwise.
	 */
	public boolean isAllDicesUsed() {
		calcScore(getLockedDiceValues());
		int dices = 0;
		dices = dicesUsed;
		calcScore(getNonLockedDiceValues());
		dices += dicesUsed;
				
		return dices == dice.length || isStraight();
	}

	/**
	 * Checks if the thrown dices give enough score to continue the turn.
	 * 
	 * @return True if the unmarked dice score is ok false otherwise.
	 */
	public boolean isScoreOk() {
		int unmarkedScore = calcScore(getUnmarkedDiceValues());

		if (unmarkedScore >= FIRST_THROW_SCORE && nrOfThrow == 1) {
			return true;
		} else if (unmarkedScore > 0 && nrOfThrow > 1) {
			return true;
		} else
			return false;
	}

	/**
	 * Checks if a dice value is one of the dices that give points.
	 * 
	 * @param val
	 * @return true if the diceValue give points false otherwise.
	 */
	private boolean isScoringDice(int val) {
		return innerScoreDices[val] > 0;
	}

	/**
	 * Checks if the thrown dices is a straight.
	 * 
	 * @return True if it is a straight, false otherwise.
	 */
	private boolean isStraight() {
		int[] diceNrValue = getAllDiceValues();
		
		for (int i = 1; i < diceNrValue.length; i++) {
			if (diceNrValue[i] != 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the marked dices score is ok so the player can throw again.
	 * 
	 * @return True if marked dices score is ok false otherwise.
	 */
	public boolean isThrowScoreOk() {
		int markedScore = calcScore(getMarkedNonLockedDiceValues());

		if (markedScore >= 300 && nrOfThrow == 1) {
			return true;
		} else if (markedScore > 0 && nrOfThrow > 1) {
			return true;
		} else
			return false;
	}

	private int[] getMarkedNonLockedDiceValues() {
		int[] diceNrValue = new int[nrOfDices+1];

		for (int i = 0; i < dice.length; i++) {
			if(!dice[i].isLocked() && dice[i].isMarked()) {
					diceNrValue[dice[i].getVal()]++;
			}
		}
		return diceNrValue;
	}

	/**
	 * Locks the dices that gave score the current turn.
	 */
	public void lockScoreDices() {
		correctScoringDices();
		for (int i = 0; i < dice.length; i++) {
			if (dice[i].isMarked() && isScoringDice(dice[i].getVal())) {
				dice[i].setLocked(true);
			} else
				dice[i].setLocked(false);
		}
	}

	/**
	 * Resets the game if the player wants to start again.
	 */
	public void reset() {
		innerScoreDices = null;
		innerScoreDices = new int[nrOfDices + 1];
		score = 0;
		dicesUsed = 0;
		turns = 0;
		resetDice();
		allDiceUsedScore = 0;
	}

	/**
	 * Sets all dices to they're starting values.
	 */
	private void resetDice() {
		for (int i = 0; i < dice.length; i++) {
			dice[i].resetDice();
		}
	}
	
	public boolean isSavedOk() {
		int markedScore = calcScore(getMarkedDiceValues());

		if (markedScore >= FIRST_THROW_SCORE && nrOfThrow == 1) {
			return true;
		} else if(markedScore > 0 && nrOfThrow > 1) {
			return true;
		} else
			return false;		
	}
	
	/**
	 * Saves the score from the turn into score.
	 * 
	 * @return The turn score.
	 */
	public int saveScore() {	
		int turnScore = calcScore(getMarkedDiceValues()) + allDiceUsedScore;
		score += turnScore;
		return turnScore;
	}
	

	/**
	 * Setter for allDiceUsed.
	 * 
	 * @param score
	 */
	public void setAllDiceUsedScore(int score) {
		allDiceUsedScore = score;
	}

	/**
	 * Setter for dicesUsed
	 * @param dices
	 */
	public void setDicesUsed(int dices) {
		dicesUsed = dices;
	}

	/**
	 * Setter for nrOfDices
	 * 
	 * @param nrOfDices
	 */
	public void setNrOfDices(int nrOfDices) {
		this.nrOfDices = nrOfDices;
	}

	/**
	 * Setter for nrOfThrow.
	 * 
	 * @param nrOfThrow
	 */
	public void setNrOfThrow(int nrOfThrow) {
		this.nrOfThrow = nrOfThrow;
	}

	/**
	 * Setter for score
	 * 
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * Setter for scoreDice.
	 * 
	 * @param score
	 */
	public void setScoreDice(int[] score) {
		scoreDice = score;
	}

	/**
	 * Sets the temporary turnscore for when all dices gave points and the player
	 * wants to try and throw again.
	 */
	public void setTempTurnScore() {
		allDiceUsedScore += calcScore(getLockedDiceValues())
				+ calcScore(getNonLockedDiceValues());
	}

	/**
	 * Setter for turns.
	 * 
	 * @param turns
	 */
	public void setTurns(int turns) {
		this.turns = turns;
	}

	/**
	 * Initialises the array with Dices and give each dice a value.
	 * 
	 * @param size
	 */
	private void setupDice(int size) {
		dice = new Dice[size];
		for (int i = 0; i < size; i++) {
			dice[i] = new Dice(i);
		}
	}
	
	/**
	 * Throws the dices that is not marked or locked.
	 * increases nrOfThrow with one.
	 */
	public void throwDice() {
		nrOfThrow++;

		Random rng = new Random();

		for (int i = 0; i < dice.length; i++) {
			if (!dice[i].isMarked() && !dice[i].isLocked()) {
				dice[i].setVal(rng.nextInt(nrOfDices) + 1);
			}
		}
	}

	/**
	 * Unmarks and unlocks all dices.
	 */
	private void unMarkAndUnlock() {
		for (int i = 0; i < dice.length; i++) {
			dice[i].setMarked(false);
			dice[i].setLocked(false);
		}
	}
}
