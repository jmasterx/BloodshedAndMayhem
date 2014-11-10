package com.jkgames.gta;

import android.graphics.Bitmap;

public class Pedestrian extends Character implements Comparable
{
	private float distanceFromPlayer;

	public Pedestrian(Bitmap animation, Bitmap blood) 
	{
		super(animation,blood);
		setType(TYPE_PED);
	}
	
	public float getDistanceFromPlayer()
	{
		return distanceFromPlayer;
	}
	public void setDistanceFromPlayer(float distanceFromPlayer)
	{
		this.distanceFromPlayer = distanceFromPlayer;
	}

	public int compareTo(Object another) 
	{
		Pedestrian o2 = (Pedestrian)another;
		
	     if (getDistanceFromPlayer() > o2.getDistanceFromPlayer())
	        {
	           return 1;
	        } 
	        else if (getDistanceFromPlayer() < o2.getDistanceFromPlayer())
	        {
	           return -1;
	        }
	     
	        return 0;
	}
	
	public void update(float timeStep)
	{
		super.update(timeStep);
		if(JMath.randomRange(0, 250) == 50)
		{
			if(getState() != Character.CHARACTER_STATE_DEAD)
			setAngle(getAngle() + JMath.randomRange(-1, 1));
		}
	}
	
}
