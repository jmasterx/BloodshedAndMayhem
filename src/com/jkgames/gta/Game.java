//Class Name: Game.java
//Purpose: Main activity class for puzzle
//Created by Josh on 2012-09-04

package com.jkgames.gta;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity
{
	GameView view;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view = new GameView(this);
        setContentView(view);
        view.requestFocus();
        view.setOnTouchListener(view);
        
    }
    
	@Override
	public void onResume ()
	{
		super.onResume ();

		// tell the view to start the animation
		view.resume ();
	}

	@Override
	public void onPause ()
	{
		super.onPause ();

		// tell the view to stop the animation
		view.pause ();
	}

}
