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
	private OBB2D camRect = new OBB2D(new Vector2D(),1.0f,1.0f,0.0f);
	
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
	
	public OBB2D getCamRect(int width, int height)
	{

		float h = ((float)height * (1.0f / getScale()));
		float w = ((float)width * (1.0f / getScale()));
		float cy = getPositionY() * 1.0f / getScale();
		cy += h / 2;
		float cx = getPositionX() * 1.0f / getScale();
		cx += w / 2;
		
		camRect.set(new Vector2D(cx,cy), w, h, 0.0f);
		
		return camRect;
	}
}
