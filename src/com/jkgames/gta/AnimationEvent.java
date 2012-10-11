//Class Name: AnimationEvent.java
//Purpose: Manages a queue of animations.
//Created by Josh on 2012-09-21
package com.jkgames.gta;

import java.util.LinkedList;
import java.util.Queue;

import android.R.anim;

public class AnimationEvent
{
	private int id;
	private boolean firstUpdate = true;
	private Queue<Animation> animations = new LinkedList<Animation>();

	AnimationEvent(int id)
	{
		setId(id);
	}
	
	public void add(Animation animation)
	{
		animations.add(animation);
	}
	
	public Animation getCurrentAnimation()
	{
		if(!isFinished())
		{
			return animations.peek();
		}
		
		return null;
	}
	
	private void destroyCurrentAnimation()
	{
		animations.poll();
	}
	
	public void update()
	{
		if(!isFinished())
		{
			//update the current animation
			getCurrentAnimation().update();
			if(getCurrentAnimation().isFinished())
			{
				destroyCurrentAnimation();
				
				//there is at least 1 animation left
				if(!isFinished())
				{
					getCurrentAnimation().begin();
				}
			}
			
		}
		
	}
	
	public boolean isFinished()
	{
		return animations.isEmpty();
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}

	//called to know if the first animation has started
	public boolean isFirstUpdate()
	{
		return firstUpdate;
	}

	public void setFirstUpdate(boolean firstUpdate) 
	{
		this.firstUpdate = firstUpdate;
	}
}
