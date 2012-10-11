//Class Name: Gta.java
//Purpose: Main class for Gta game
//Created by Josh on 2012-08-24

package com.jkgames.gta;

import com.jkgames.gta.R.array;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class Gta extends Activity implements OnClickListener
{
	private int gameDifficulty;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View instructionsButton = findViewById(R.id.instructions_button);
        instructionsButton.setOnClickListener(this);
        
        View newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.game_difficulty_button:
    		new AlertDialog.Builder(this)
    		.setTitle(R.string.difficulty_label)
    		.setItems(array.difficulties, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) 
				{
					gameDifficulty = which;
				}
			})
			.show();
    		break;
    	case R.id.exit_button:
    		finish();
    		return true;
    	}
    	return false;
    }

	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.instructions_button:
			Intent i = new Intent(this,Instructions.class);
			startActivity(i);
			break;
		case R.id.new_game_button:
			startNewGame();
			break;
		}
		
	}
	
	private void startNewGame()
	{
		Intent gIntent = new Intent(this,Game.class);
		gIntent.putExtra("game_difficulty", gameDifficulty);
		startActivity(gIntent);
	}
}
