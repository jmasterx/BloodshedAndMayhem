package com.jkgames.gta;

import android.graphics.Bitmap;

public class Player extends Character
{
	public static final float PLAYER_VELOCITY = 1.5f;

	public Player(Bitmap animation, Bitmap blood) 
	{
		super(animation,blood);
		setVelocity(PLAYER_VELOCITY);
		setType(TYPE_PLAYER);
	}

}
