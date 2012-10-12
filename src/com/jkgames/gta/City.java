package com.jkgames.gta;

import java.util.ArrayList;

import android.graphics.RectF;


public class City implements IDrawable
{
	private ArrayList<Road> roads = new ArrayList<Road>();
	private ArrayList<Intersection> intersections = new ArrayList<Intersection>();

	
	public City(ArrayList<Road> roads, ArrayList<Intersection> intersections)
	{
		this.roads = roads;
		this.intersections = intersections;
		
	}
	
	public ArrayList<Road> getRoads() 
	{
		return roads;
	}

	public ArrayList<Intersection> getIntersections() 
	{
		return intersections;
	}
	
	public void draw(GraphicsContext c)
	{
		RectF screen = c.getViewRect();
		for(Road r : roads)
		{
			if(RectF.intersects(screen, r.getRect()) || screen.contains(r.getRect()))
			{
				r.draw(c);
			}
		}
		
		for(Intersection i : intersections)
		{
			if(RectF.intersects(screen, i.getRect()) || screen.contains(i.getRect()))
			{
				i.draw(c);
			}
		}
	}
}
