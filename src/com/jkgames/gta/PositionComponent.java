//Class Name: PositionComponent.java
//Purpose: Simple base class for anything that has a size and position.
//Created by Josh on 2012-09-21
package com.jkgames.gta;

import android.graphics.RectF;

public class PositionComponent
{
	private RectF rect;
	
	public PositionComponent()
	{
		rect = new RectF();
	}
	
	public PositionComponent(RectF rect)
	{
		this.rect = new RectF(rect);
	}
	
	public PositionComponent(PositionComponent p)
	{
		this.rect = new RectF(p.getRect());
	}
	
	public RectF getRect() 
	{
		return rect;
	}
	
	public void setRect(RectF rect)
	{
		this.rect = rect;
	}
}
