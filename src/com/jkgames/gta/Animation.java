//Class Name: Animation.java
//Purpose: Represents a single simple linearly interpolated animation.
//Created by Josh on 2012-09-21
package com.jkgames.gta;

import android.graphics.RectF;

public class Animation 
{
	private float interval;
	private PositionComponent target;
	private PositionComponent targetCopy;
	private PositionComponent result;
	private float length;
	private int id;
	private long startTime;
	
	public Animation(PositionComponent target, PositionComponent result, float length)
	{
		setId(id);
		setTarget(target);
		targetCopy = new PositionComponent(target);
		this.result = result;
		setLength(length);
		resetTime();
	}
	
	public int getId() 
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	//called each time the object is to be updated
	public void update()
	{
		updateInterval();
		
		RectF initialRect = targetCopy.getRect();
		RectF finalRect = result.getRect();
		float topA = initialRect.top;
		float topB = finalRect.top;
		float leftA = initialRect.left;
		float leftB = finalRect.left;
		float bottomA = initialRect.bottom;
		float bottomB = finalRect.bottom;
		float rightA = initialRect.right;
		float rightB = finalRect.right;
		
		//calculate the coordinates using linear interpolation 
		//over domain of interval[0,1]
		target.getRect().top = JMath.valueAt(topA, topB, getInterval());
		target.getRect().left = JMath.valueAt(leftA, leftB, getInterval());
		target.getRect().bottom = JMath.valueAt(bottomA, bottomB, getInterval());
		target.getRect().right = JMath.valueAt(rightA, rightB, getInterval());
	}
	
	public boolean isFinished()
	{
		return getInterval() >= 1.0f;
	}

	public float getInterval() 
	{
		return interval;
	}

	private void updateInterval() 
	{
		//multiply by 1000 to convert seconds to milliseconds
		interval = (System.currentTimeMillis () - startTime) / (getLength() * 1000.0f);
		
		if(interval > 1.0f)
		{
			interval = 1.0f;
		}
	}

	public PositionComponent getTarget() 
	{
		return target;
	}
	
	public PositionComponent getTargetCopy() 
	{
		return targetCopy;
	}
	
	//the result is what target will be when interval is 1.0f
	public PositionComponent getResult() 
	{
		return result;
	}
	
	//the target reference that gets modified
	private void setTarget(PositionComponent target)
	{
		this.target = target;
	}

	public float getLength() 
	{
		return length;
	}

	//length of animation in seconds
	public void setLength(float length) 
	{
		this.length = length;
	}
	
	public void resetTime()
	{
		startTime = System.currentTimeMillis();
	}
	
	//called when an animation starts
	public void begin()
	{
		resetTime();
		targetCopy = new PositionComponent(target);
	}
	
}
