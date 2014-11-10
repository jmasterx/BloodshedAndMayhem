package com.jkgames.gta;

import android.graphics.Bitmap;

public class ControlButton extends Entity
{
	private Bitmap controlImg;
	private boolean pressed = false;
	private int id;
	private boolean visible = true;
	
	public static final int BUTTON_STEAL_CAR = 0;
	public static final int BUTTON_CHANGE_WEAPON = 1;
	public static final int BUTTON_FIRE_WEAPON = 2;
	public static final int BUTTON_GAS = 3;
	public static final int BUTTON_BRAKE = 4;
		
	public ControlButton(Bitmap buttonImg, float radius, int id)
	{
		controlImg = buttonImg;
		setRadius(radius);
		setId(id);
	}
	
	public void draw(GraphicsContext c)
	{
		if(!isPressed())
		c.drawRotatedScaledBitmap(controlImg, getCenterX(), 
				getCenterY(), getWidth(), getHeight(), getAngle());
	}

	public boolean isPressed() 
	{
		return pressed;
	}

	public void setPressed(boolean pressed) 
	{
		this.pressed = pressed;
	}

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public boolean isVisible() 
	{
		return visible;
	}

	public void setVisible(boolean visible) 
	{
		this.visible = visible;
	}	
	
	public boolean pointInside(float x, float y)
	{
		return JMath.distance(getCenterX(), getCenterY(), x, y) < getRadius();
	}
}
