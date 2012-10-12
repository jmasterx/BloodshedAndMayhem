package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.opengl.GLES20;

public class Entity implements IDrawable
{
	RectF rect = new RectF();
	float angle = 0.0f;

	public float getAngle() 
	{
		return angle;
	}
	
	public void setAngle(float angle)
	{
		this.angle = angle;
	}

	public RectF getRect() 
	{
		return rect;
	}

	public void setRect(RectF rect) 
	{
		this.rect.top = rect.top;
		this.rect.left = rect.left;
		this.rect.bottom = rect.bottom;
		this.rect.right = rect.right;
	}
	
	public float getWidth()
	{
		return getRect().width();
	}
	
	public float getHeight()
	{
		return getRect().height();
	}
	
	public float getCenterX()
	{
		return getRect().centerX();
	}
	
	public float getCenterY()
	{
		return getRect().centerY();
	}
	
	public void setCenter(float x, float y)
	{
		float w = getWidth();
		float h = getHeight();
		
		getRect().left = x - (w / 2.0f);
		getRect().top = y - (h / 2.0f);
		getRect().right = x + (w / 2.0f);
		getRect().bottom = y + (h / 2.0f);
	}
	
	public void setSize(float w, float h)
	{	
		float x = getCenterX();
		float y = getCenterY();
		getRect().left = x - (w / 2.0f);
		getRect().top = y - (h / 2.0f);
		getRect().right = x + (w / 2.0f);
		getRect().bottom = y + (h / 2.0f);
	}


	public void draw(GraphicsContext c)
	{
		
	}
	

}
