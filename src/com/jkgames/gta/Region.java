package com.jkgames.gta;

import java.util.ArrayList;

import android.util.Log;

public class Region
{
	private ArrayList<ArrayList<Entity>> staticObjects = new ArrayList<ArrayList<Entity>>();
	private ArrayList<ArrayList<RigidBody>> dynamicObjects = new ArrayList<ArrayList<RigidBody>>();
	private OBB2D rect;
	private int indexX;
	private int indexY;
	
	public Region(float left, float top, float width,
			float height, int indexX, int indexY)
	{
		rect = new OBB2D(left,top,width,height);
		this.indexX = indexX;
		this.indexY = indexY;
		
		for(int i = 0; i < Entity.MAX_LAYERS; ++i)
		{
			staticObjects.add(new ArrayList<Entity>());
			dynamicObjects.add(new ArrayList<RigidBody>());
		}
	}
	
	public OBB2D getRect() 
	{
		return rect;
	}

	public int getIndexX() 
	{
		return indexX;
	}

	public int getIndexY()
	{
		return indexY;
	}

	public void addStatic(Entity e)
	{	
		staticObjects.get(e.getLayer()).add(e);
	}
	
	public void addDynamic(RigidBody e)
	{
		dynamicObjects.get(e.getLayer()).add(e);
	}
	
	public void removeStatic(Entity e)
	{
		staticObjects.get(e.getLayer()).remove(e);
	}
	
	public void removeDynamic(RigidBody e)
	{
		dynamicObjects.get(e.getLayer()).remove(e);
	}
	
	ArrayList<Entity> getStatic(int layer)
	{
		return staticObjects.get(layer);
	}
	
	ArrayList<RigidBody> getDynamic(int layer)
	{
		return dynamicObjects.get(layer);
	}
	
	public int getLayerCount()
	{
		return Entity.MAX_LAYERS;
	}
	
}
