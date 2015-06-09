package com.mydoku;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * Class that extends view and are used to draw a Sudoku board with the values
 * for each cell from the model.
 * 
 * @author Christer
 * @version 1.0
 * @since 2014-08-21
 */
public class BoardView extends View {

	private MydokuModel model;
	private float width;
	private float height;
	private int selX;
	private int selY;
	private final Rect selRect = new Rect();

	/**
	 * Constructor, sets the context and the model.
	 * 
	 * @param context
	 * @param model
	 */
	public BoardView(Context context, MydokuModel model) {
		super(context);
		this.model = model;
		setVerticalScrollBarEnabled(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	/**
	 * If the size of the view changes, recalculates the height and width
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		width = (w / 9f);
		height = (float) ((h / 9f) * 0.68);

		getRect(selX, selY, selRect);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * Gets the selected cells pixel rectangle.
	 * 
	 * @param x
	 * @param y
	 * @param rect
	 */
	private void getRect(int x, int y, Rect rect) {
		rect.set((int) (x * width) - 1, (int) (y * height) - 1, (int) (x
				* width + width) - 1, (int) (y * height + height) - 1);
	}

	/**
	 * Draws the game board.
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Paint dark = drawBoardAndBackground(canvas);

		drawGridLines(canvas, dark);

		drawSelectedCell(canvas);

		drawNumbers(canvas);
	}

	/**
	 * Draws the numbers in the center of the cell. Red color for the numbers
	 * prefilled and black for numbers the player have filled in.
	 * 
	 * @param canvas
	 */
	private void drawNumbers(Canvas canvas) {
		// Draw the numbers
		// Define color and style for numbers
		Paint numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		numberPaint.setColor(getResources().getColor(R.color.black));
		numberPaint.setStyle(Style.FILL);
		numberPaint.setTextSize(height * 0.75f);
		numberPaint.setTextScaleX(width / height);
		numberPaint.setTextAlign(Paint.Align.CENTER);

		// Draw the number in the center of the cell
		FontMetrics fm = numberPaint.getFontMetrics();
		float x = width / 2;
		float y = height / 2 - (fm.ascent + fm.descent) / 2;

		// Draw hint number if a shake have occured.
		if (model.isHint()) {
			showHint(canvas, numberPaint, x, y);
		}

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (model.getVal(i, j) != 0) {
					if (model.getOriginalGameVal(i, j) == 0) {
						numberPaint.setColor(Color.BLACK);
					} else {
						numberPaint.setColor(Color.RED);
					}
					canvas.drawText("" + model.getVal(i, j), i * width + x, j
							* height + y, numberPaint);
				}
			}
		}
	}

	/**
	 * Draws a grey rectangle on the cell that is marked.
	 * 
	 * @param canvas
	 */
	private void drawSelectedCell(Canvas canvas) {
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.darker_gray));
		canvas.drawRect(selRect, selected);
	}

	/**
	 * Draw the grid lines.
	 * 
	 * @param canvas
	 * @param dark
	 */
	private void drawGridLines(Canvas canvas, Paint dark) {
		// Draw the minor grid lines
		for (int i = 0; i < 9; i++) {
			canvas.drawLine(0, i * height, getWidth(), i * height, dark);
			canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, dark);
			canvas.drawLine(i * width, 0, i * width, height * 9, dark);
			canvas.drawLine(i * width + 1, 0, i * width + 1, height * 9, dark);
		}

		// Draw the major grid lines
		for (int i = 0; i < 9; i++) {
			if (i % 3 == 0) {
				canvas.drawLine(0, (i * height) + 3, getWidth(),
						(i * height) + 3, dark);
				canvas.drawLine(0, (i * height) - 3, getWidth(),
						(i * height) - 3, dark);
				canvas.drawLine(0, (i * height) + 1, getWidth(),
						(i * height) + 1, dark);
				canvas.drawLine(0, (i * height) - 1, getWidth(),
						(i * height) - 1, dark);
				canvas.drawLine(0, (i * height), getWidth(), (i * height), dark);
				canvas.drawLine(0, (i * height) + 2, getWidth(),
						(i * height) + 2, dark);
				canvas.drawLine(0, (i * height) - 2, getWidth(),
						(i * height) - 2, dark);

				canvas.drawLine((i * width) + 3, 0, (i * width) + 3,
						height * 9, dark);
				canvas.drawLine((i * width) - 3, 0, (i * width) - 3,
						height * 9, dark);
				canvas.drawLine((i * width) + 1, 0, (i * width) + 1,
						height * 9, dark);
				canvas.drawLine((i * width) - 1, 0, (i * width) - 1,
						height * 9, dark);
				canvas.drawLine(i * width, 0, i * width, height * 9, dark);
				canvas.drawLine((i * width) + 2, 0, (i * width) + 2,
						height * 9, dark);
				canvas.drawLine((i * width) - 2, 0, (i * width) - 2,
						height * 9, dark);
			}
			canvas.drawLine(0, (9 * height) + 1, getWidth(), (9 * height) + 1,
					dark);
			canvas.drawLine(0, 9 * height, getWidth(), 9 * height, dark);
			canvas.drawLine(0, (9 * height) - 1, getWidth(), (9 * height) - 1,
					dark);
		}
	}

	/**
	 * Draw the background and the board, define the color for the gridlines.
	 * 
	 * @param canvas
	 * @return
	 */
	private Paint drawBoardAndBackground(Canvas canvas) {
		// Draw the background
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.background_light));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);

		// Draw the board
		// Define colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.black));
		return dark;
	}

	/**
	 * Gets the selected cell converted to one dimensional array.
	 * 
	 * @return
	 */
	public int getSelectedCell() {
		int y = selY;
		int x = selX;

		while (y > 0) {
			x += 9;
			y -= 1;
		}
		return x;
	}

	/**
	 * checks if the value of x is between zero and nine.
	 * 
	 * @param x
	 * @return
	 */
	private boolean isInRange(int x) {
		return x < 9 && x >= 0;
	}

	/**
	 * Used by the GameActivity to move the selected cell a position depending
	 * on the angle of the phone.
	 * 
	 * @param direction
	 */
	public void moveGyro(String direction) {
		switch (direction) {
		case "UP":
			if (isInRange(selY - 1)) {
				select(selX, selY - 1);
			}
			break;
		case "DOWN":
			if (isInRange(selY + 1)) {
				select(selX, selY + 1);
			}
			break;
		case "LEFT":
			if (isInRange(selX - 1)) {
				select(selX - 1, selY);
			}
			break;
		case "RIGHT":
			if (isInRange(selY + 1)) {
				select(selX + 1, selY);
			}
		default:
			break;
		}
	}

	/**
	 * Creates the selected cell.
	 * 
	 * @param x
	 * @param y
	 */
	private void select(int x, int y) {
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}

	/**
	 * Gets called when the screen gets touched. Sends the coordinates touched
	 * to select method.
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

		select((int) (event.getX() / width), (int) (event.getY() / height));
		return true;
	}

	/**
	 * Draws the hint number on the cell where the hint should be shown.
	 * 
	 * @param canvas
	 * @param numberPaint
	 * @param x
	 * @param y
	 */
	public void showHint(Canvas canvas, Paint numberPaint, float x, float y) {

		numberPaint.setColor(Color.BLUE);

		int i = model.getHintIndex();
		int j = 0;

		while (i > 9) {
			i -= 9;
			j++;
		}

		canvas.drawText("" + model.getHintVal(), i * width + x, j * height + y,
				numberPaint);
	}

}