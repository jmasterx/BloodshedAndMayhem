package com.jkgames.gta;

import java.util.ArrayList;

import android.graphics.RectF;


public class City
{
	private ArrayList<Road> roads = new ArrayList<Road>();
	private ArrayList<Intersection> intersections = 
			new ArrayList<Intersection>();
	private ArrayList<Building> buildings = new ArrayList<Building>();

	
	public City(ArrayList<Road> roads,
			ArrayList<Intersection> intersections,
			ArrayList<Building> buildings)
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

	ArrayList<Building> getBuildings()
	{
		return buildings;
	}
}
