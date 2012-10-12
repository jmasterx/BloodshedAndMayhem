package com.jkgames.gta;

public class Vector2D
{
	public float x;
	public float y;
	
	public Vector2D()
	{
		
	}
	
	public Vector2D(float x, float y)
	{
		setX(x);
		setY(y);
	}
	
	public float dot( Vector2D v )
	{
		return ((v.x * x) + (v.y * y));

	}

	public float length()
	{
		return (float)Math.sqrt(((x * x) + (y * y)));
	}

	public void normalize()
	{
		float len = length();
		if(len == 0.0f)
		{
			return;
		}

		x /= len;
		y /= len;
	}
	
	public static Vector2D fromPoints(float x1, float y1, float x2, float y2)
	{
		float x = x2 - x1;
		float y = y2 - y1;
		
		return new Vector2D(x,y);
		
	}
	
	public static Vector2D fromPointsNormalized(float x1, float y1, float x2, float y2)
	{
		float x = x2 - x1;
		float y = y2 - y1;
		Vector2D v = new Vector2D(x,y);
		v.normalize();
		
		return v;
		
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x) 
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y) 
	{
		this.y = y;
	}
}
