package com.jkgames.gta;

import android.graphics.RectF;

public class Camera
{
	private float positionX;
	private float positionY;
	private float beginPosX;
	private float beginPosY;
	private float beginScale;
	private float scale;
	private RectF camRect = new RectF();
	
	public Camera()
	{
		setScale(1.0f);
	}
	
	public void setPosition( float x, float y )
	{
		positionX = x;
		positionY = y;
	}

	public void applyTransform(GraphicsContext c)
	{
		c.getCanvas().translate(-getPositionX(), -getPositionY());
		c.getCanvas().scale(getScale(), getScale());
	}


	public void move( float x, float y )
	{
		positionX += x;
		positionY += y;
	}

	public float getPositionX() 
	{
		return positionX;
	}
	
	public float getPositionY() 
	{
		return positionY;
	}

	public void setScale( float scale )
	{
		this.scale = scale;
	}

	public void zoom( float quantity )
	{
		this.scale += quantity;
	}

	public float getScale()
	{
		return scale;
	}
	
	public void beginDeltaTracking()
	{
		beginPosX = positionX;
		beginPosY = positionY;
		beginScale = scale;
	}
	
	public void deltaMove(float dx, float dy)
	{
		positionX = beginPosX + dx;
		positionY = beginPosY + dy;
	}
	
	public void deltaScale(float dScale)
	{
		scale = beginScale + dScale;
	}
	
	public RectF getCamRect(int width, int height)
	{
		camRect.top = getPositionY() * 1.0f / getScale();
		camRect.left = getPositionX() * 1.0f / getScale();
		camRect.bottom = camRect.top + ((float)height * (1.0f / getScale()));
		camRect.right = camRect.left + ((float)width * (1.0f / getScale()));
		
		return camRect;
	}
}
