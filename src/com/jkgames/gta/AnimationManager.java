//Class Name: AnimationManager.java
//Purpose: Manages Animation Events and event listeners.
//Created by Josh on 2012-09-21
package com.jkgames.gta;

import java.util.ArrayList;
import java.util.List;

public class AnimationManager 
{
	private List<AnimationEvent> animations = new ArrayList<AnimationEvent>();
	private List<AnimationListener> animationListeners = new ArrayList<AnimationListener>();
	private List<AnimationEvent> animationsToRemove = new ArrayList<AnimationEvent>();
	
	public AnimationManager()
	{
	}
	
	public void addAnimationListener(AnimationListener listener)
	{
		animationListeners.add(listener);
	}
	
	public void removeAnimationListener(AnimationListener listener)
	{
		animationListeners.remove(listener);
	}
	
	public void add(AnimationEvent animation)
	{
		animations.add(animation);	
	}
	
	public void remove(AnimationEvent animation)
	{
		if(animations.contains(animation))
		{
			for(AnimationListener listener : animationListeners)
			{
				listener.onAnimationCanceled(animation);
			}
		}
		
		animations.remove(animation);
	}
	
	public void update()
	{
		for(AnimationEvent animation : animations)
		{
			if(animation.isFirstUpdate())
			{
				for(AnimationListener listener : animationListeners)
				{
					listener.onAnimationStarted(animation);
				}
				
				animation.setFirstUpdate(false);
			}
			animation.update();
			if(animation.isFinished())
			{
				animationsToRemove.add(animation);
				for(AnimationListener listener : animationListeners)
				{
					listener.onAnimationFinished(animation);
				}	
			}
		}
		
		if(animationsToRemove.size() > 0)
		{
			for(AnimationEvent animation : animationsToRemove)
			{
				animations.remove(animation);
			}
		}
	
		animationsToRemove.clear();	
	}
}
