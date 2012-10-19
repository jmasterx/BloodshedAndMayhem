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
	
	public float cross(Vector2D b)
	{
		return ((x*b.y) - (y*b.x));
	}
	
	public Vector2D project(Vector2D v)
	{
		float dot = dot(v);
		return new Vector2D(dot * v.x,dot * v.y);
	}
	
	public Vector2D divide(Vector2D r)
	{
		return new Vector2D(x / r.x,y / r.y);
	}
	
	public Vector2D divide(float r)
	{
		return new Vector2D(x / r,y / r);
	}
	
	public Vector2D multiply(Vector2D r)
	{
		return new Vector2D(x * r.x,y * r.y);
	}
	
	public Vector2D multiply(float r)
	{
		return new Vector2D(x * r,y * r);
	}
	
	public Vector2D subtract(Vector2D r)
	{
		return new Vector2D(x - r.x,y - r.y);
	}
	
	public Vector2D subtract(float r)
	{
		return new Vector2D(x - r,y - r);
	}
	
	public Vector2D add(Vector2D r)
	{
		return new Vector2D(x + r.x,y + r.y);
	}
	
	public Vector2D add(float r)
	{
		return new Vector2D(x + r,y + r);
	}
	
	public static Vector2D add(Vector2D l, Vector2D r)
    {
        return new Vector2D(l.x + r.x, l.y + r.y);
    }

    public static Vector2D subtract(Vector2D l, Vector2D r)
    {
        return new Vector2D(l.x - r.x, l.y - r.y);
    }

    public static Vector2D negative(Vector2D r)
    {
        Vector2D temp = new Vector2D(-r.x, -r.y);
        return temp;
    }

    public static Vector2D scalarMultiply(Vector2D l, float r)
    {
        return new Vector2D(l.x * r, l.y * r);
    }

    public static Vector2D scalarDivide(Vector2D l, float r)
    {
        return new Vector2D(l.x / r, l.y / r);
    }

    public static float dot(Vector2D l, Vector2D r)
    {
        return (l.x * r.x + l.y * r.y);
    }


    public static float cross(Vector2D l, Vector2D r)
    {
        return (l.x*r.y - l.y*r.x);
    }


    public Vector2D project(Vector2D v, Float mag)
    {
        float thisDotV = dot(this, v);
        mag = thisDotV;
        return scalarMultiply(v, thisDotV);
    }

}
