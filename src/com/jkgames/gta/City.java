package com.jkgames.gta;

import java.util.ArrayList;

import android.graphics.RectF;


public class City implements IDrawable
{
	private ArrayList<Road> roads = new ArrayList<Road>();
	private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	private ArrayList<Building> buildings = new ArrayList<Building>();

	
	public City(ArrayList<Road> roads, ArrayList<Intersection> intersections, ArrayList<Building> buildings)
	{
		this.roads = roads;
		this.intersections = intersections;
		this.buildings = buildings;
		
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
		
		for(Building b : buildings)
		{
			if(RectF.intersects(screen, b.getRect()) || screen.contains(b.getRect()))
			{
				b.draw(c);
			}
		}
	}
}
