package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class AnalogStick extends Entity
{
	private Bitmap mainStickImg;
	private Bitmap innerStickImg;
	private RectF innerStickRect = new RectF();
	private Vector2D stickVector = new Vector2D();
	private float stickDistance = 0.0f;
	public static final float INNER_STICK_PROPORTION = 0.5f;
	public boolean visible = true;
	
	public AnalogStick(Bitmap mainStick, Bitmap innerStick, float radius)
	{
		mainStickImg = mainStick;
		innerStickImg = innerStick;
		setRadius(radius);
	}
	
	public float getInnerRadius()
	{
		return getRadius() * INNER_STICK_PROPORTION;
	}
	
	public void calcStickVector(float x, float y)
	{
		float cx = getCenterX();
		float cy = getCenterY();
		float r = getRadius();
		float angle = (float)Math.atan2(y - cy, x - cx);
		stickVector.x = (float)Math.cos(angle);
		stickVector.y = (float)Math.sin(angle);
		stickDistance = JMath.distance(x,y,cx,cy);
		stickDistance = JMath.clamp(-r, r, stickDistance);
	}
	
	public float getStickValueX()
	{
		return (stickDistance / getRadius()) * getStickVector().x;
	}
	
	public float getStickValueY()
	{
		return (stickDistance / getRadius()) * getStickVector().y;
	}
	
	public RectF getInnerStickRect()
	{
		float r = getInnerRadius();
		float cx = getCenterX() + (stickDistance * getStickVector().x);
		float cy = getCenterY() + (stickDistance * getStickVector().y);
		
		innerStickRect.left = cx - r;
		innerStickRect.top = cy - r;
		innerStickRect.right = cx + r;
		innerStickRect.bottom = cy + r;
		
		return innerStickRect;
	}
	
	public void draw(GraphicsContext c)
	{
		c.getCanvas().drawBitmap(mainStickImg, null, getRect(), null);
		c.getCanvas().drawBitmap(innerStickImg, null, getInnerStickRect(), null);
	}

	public Vector2D getStickVector()
	{
		return stickVector;
	}

	public void setStickVector(Vector2D stickVector) 
	{
		this.stickVector.x = stickVector.x;
		this.stickVector.y = stickVector.y;
	}
	
	public void clearStickValue()
	{
		stickVector.x = 0.0f;
		stickVector.y = 0.0f;
		stickDistance = 0.0f;
	}

	public boolean isVisible() 
	{
		return visible;
	}

	public void setVisible(boolean visible) 
	{
		this.visible = visible;
	}
}
