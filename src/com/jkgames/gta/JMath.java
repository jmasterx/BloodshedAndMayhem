package com.jkgames.gta;

import android.graphics.RectF;

public class JMath 
{
	static public int randomRange(int min, int max)
	{
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	static public boolean isInRange(int val, int min, int max)
	{
		return val >= min && val <= max;
	}
	
	static public int colFromPos(float startX,float width, float x)
	{
		x -= startX;
		return (int)Math.floor(x / width);
		
	}
	
	static public int rowFromPos(float startY,float height, float y)
	{
		y -= startY;
		return (int)Math.floor(y / height);
		
	}
	
	static public float valueAt( float valueA, float valueB, float t )
	{
		return (1.0f - t) * valueA + t * valueB;
	}
	
	static public float distance(RectF a, RectF b)
	{
		return (float)Math.sqrt((Math.pow(a.centerX()-b.centerX(),2)) 
				+ (Math.pow(a.centerY()-b.centerY(),2)));
	}
	
	static public float calcMovementTime(RectF a, RectF b, float unitsPerSec)
	{
		if(unitsPerSec == 0.0f)
		{
			return 0.0f;
		}
		
		float dist = distance(a, b);
		return dist / unitsPerSec;
	}
	
}
