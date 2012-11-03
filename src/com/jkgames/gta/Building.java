package com.jkgames.gta;

import android.graphics.Bitmap;

public class Building extends Entity
{
	private Bitmap image;
	private float scale = 1.0f;
	private int direction = DIRECTION_TOP;
	public static final int DIRECTION_TOP = 0;
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_BOTTOM = 2;
	public static final int DIRECTION_RIGHT = 3;
	
	public Building(Bitmap image, int direction, float scale, float cx, float cy)
	{
		this.image = image;
		setDirection(direction);
		setScale(scale);
		setCenter(cx,cy);
		setRect(scale,cx,cy,direction);
	}
	
	public void setRect(float scale, float cx, float cy, int direction)
	{
		float w = (direction == DIRECTION_TOP  || direction == DIRECTION_BOTTOM)
				? scale * image.getWidth() : scale * image.getHeight();
		float h = (direction == DIRECTION_TOP  || direction == DIRECTION_BOTTOM)
						? scale * image.getHeight() : scale * image.getWidth();
						
		setSize(w, h);
		setCenter(cx, cy);
	}
	
	public Bitmap getImage()
	{
		return image;
	}

	public void setImage(Bitmap image) 
	{
		this.image = image;
	}

	public float getScale() 
	{
		return scale;
	}
	
	public float getDirectionalWidth()
	{
		if(getDirection() == DIRECTION_TOP || getDirection() == DIRECTION_BOTTOM)
		{
			return getWidth();
		}
		else
		{
			return getHeight();
		}
	}
	
	public float getDirectionalHeight()
	{
		if(getDirection() == DIRECTION_TOP || getDirection() == DIRECTION_BOTTOM)
		{
			return getHeight();
		}
		else
		{
			return getWidth();
		}
	}

	public void setScale(float scale)
	{
		this.scale = scale;
		setSize(image.getWidth() * scale, image.getHeight() * scale);
	}

	public int getDirection() 
	{
		return direction;
	}

	public void setDirection(int direction) 
	{
		this.direction = direction;
	}
	
	public void draw(GraphicsContext c)
	{
		c.drawRotatedScaledBitmap(image, getCenterX(), getCenterY(),
				image.getWidth() * getScale(), image.getHeight() * getScale(), getDirection() * (float)Math.PI / 2.0f);
	}
}
