package com.greed;

/* HelpActivity
*
* Purpose: Used to give the user information about the rules of the game and
* the way the score is calculated.
*
* v1.0
*
* Copyright (c). 2014-01-28 Christer Jakobsson.
*/
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**
 * Class that extends activity and shows the rules and how the score is 
 * calculated.
 * 
 * @author Christer
 *
 */
public class HelpActivity extends Activity {
	
	private Button backButton;
	
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		backButton = (Button) findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			    finish();
			}
		});
	}
}

