package com.jkgames.gta;

import java.util.ArrayList;

import android.graphics.RectF;

public class Entity implements IDrawable
{
	private OBB2D rect = new OBB2D(0.0f,0.0f,1.0f,1.0f,0.0f);
	private GridWorld world = null;
	private ArrayList<Region> regions = new ArrayList<Region>();
	private boolean solid = true;
	private int layer = LAYER_ROAD;
	private int regionCount;
	
	public static final int LAYER_ROAD = 0;
	public static final int LAYER_INTERSECTION = 1;
	public static final int LAYER_PEDESTRIAN = 2;
	public static final int LAYER_OBJECTS = 3;
	
	public static final int MAX_LAYERS = 4;

	public Entity()
	{
		for(int i = 0; i < 4; ++i)
		{
			regions.add(null);
		}
	}
	
	protected void rectChanged()
	{
		if(getWorld() != null)
		{
			getWorld().updateStatic(this);
		}
	}
	
	public ArrayList<Region> getRegions()
	{
		return regions;
	}
	
	
	public GridWorld getWorld() 
	{
		return world;
	}

	public void setWorld(GridWorld world) 
	{
		this.world = world;
	}
	
	public float getAngle() 
	{
		return rect.getAngle();
	}
	
	public void setAngle(float angle)
	{
		rect.setAngle(angle);
		rectChanged();
	}

	public OBB2D getRect() 
	{
		return rect;
	}

	public void setRect(RectF rect) 
	{
		this.rect.set(rect);
		rectChanged();
	}
	
	public int getRegionCount()
	{
		return regionCount;
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
		getRect().moveTo(x,y);
		rectChanged();
	}
	
	public void setSize(float w, float h)
	{
		rect.setSize(w, h);
		rectChanged();
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

	public boolean isSolid() 
	{
		return solid;
	}

	public void setSolid(boolean solid)
	{
		this.solid = solid;
	}

	public int getLayer() 
	{
		return layer;
	}

	public void setLayer(int layer) 
	{
		this.layer = layer;
	}

	public void setRegionCount(int regionCount) 
	{
		this.regionCount = regionCount;
	}
	

}
