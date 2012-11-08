package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.opengl.GLES20;

public class Entity implements IDrawable
{
	OBB2D rect = new OBB2D(new Vector2D(0.0f,0.0f),1.0f,1.0f,0.0f);

	public float getAngle() 
	{
		return rect.getAngle();
	}
	
	public void setAngle(float angle)
	{
		rect.setAngle(angle);
	}

	public OBB2D getRect() 
	{
		return rect;
	}

	public void setRect(RectF rect) 
	{
		this.rect.set(rect);
	}
	
	public float getWidth()
	{
		return getRect().getWidth();
	}
	
	public float getHeight()
	{
		return getRect().getHeight();
	}
	
	public float getCenterX()
	{
		return getRect().getCenter().x;
	}
	
	public float getCenterY()
	{
		return getRect().getCenter().y;
	}
	
	public void setCenter(float x, float y)
	{
		getRect().moveTo(new Vector2D(x,y));
	}
	
	public void setSize(float w, float h)
	{
		rect.setSize(w, h);
	}

	public void setRadius(float radius)
	{
		setSize(radius * 2.0f, radius * 2.0f);
	}
	
	public float getRadius()
	{
		return getWidth() / 2.0f;
	}

	public void draw(GraphicsContext c)
	{
		
	}
	
	public void update(float timeStep)
	{
		
	}
	

}
