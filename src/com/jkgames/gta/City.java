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
		
		OBB2D screen = c.getViewRect();
		for(Road r : roads)
		{
			if(screen.overlaps(r.getRect()))
			{
				r.draw(c);
			}
		}
		
		for(Intersection i : intersections)
		{
			if(screen.overlaps(i.getRect()))
			{
				i.draw(c);
			}
		}
		
		/*
		for(Building b : buildings)
		{
			if(screen.overlaps(b.getRect()))
			{
				b.draw(c);
			}
		}
		*/
	}
	
	ArrayList<Building> getBuildings()
	{
		return buildings;
	}
}
