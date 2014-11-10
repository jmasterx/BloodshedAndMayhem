package com.jkgames.gta;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.provider.LiveFolders;

public class PedestrianManager 
{
	private ArrayList<Pedestrian> pedPool = new ArrayList<Pedestrian>();
	private ArrayList<Pedestrian> unusedPeds = new ArrayList<Pedestrian>();
	private ArrayList<Pedestrian> deadPeds = new ArrayList<Pedestrian>();
	private ArrayList<Pedestrian> alivePeds = new ArrayList<Pedestrian>();
	private GridWorld world;
	private PlayerManager playerMan;
	private Camera camera;
	private float timeSpent;
	private float timeSpentGenList;
	private boolean generatedLists = false;
	private boolean firstUpdate = true;
	
	public static final int MAX_PEDS = 150;
	public static final int MAX_LIVING_PEDS = 90;
	public static final int MAX_DEAD_PEDS = MAX_PEDS - MAX_LIVING_PEDS;
	public static final int PED_MOVE_DIST = 150;
	
	public PedestrianManager(GridWorld world, Camera camera,
			PlayerManager playerMan,
			Bitmap male, Bitmap female, Bitmap blood)
	{
		this.world = world;
		this.camera = camera;
		this.playerMan = playerMan;
		
		for(int i = 0; i < MAX_PEDS; ++i)
		{
			Pedestrian dude = null;
		       if(JMath.randomRange(0, 1)  == 0)
		       {
		    	   dude = new Pedestrian(female,blood);
		       }
		       else
		       {
		    	   dude = new Pedestrian(male,blood);
		       }
		       
		       pedPool.add(dude);
		}
	}
	
	public void generatePedLists()
	{
		unusedPeds.clear();
		deadPeds.clear();
		alivePeds.clear();
		
		Player player = playerMan.getPlayer();
		for(int i = 0; i < pedPool.size(); ++i)
		{
			Pedestrian p = pedPool.get(i);
			
			if(pedPool.get(i).getWorld() != null)
			{
				p.setDistanceFromPlayer(
						JMath.distance(p.getCenter(), player.getCenter()));
				
				if(p.isDead())
				{
					deadPeds.add(p);
				}
				else
				{
					alivePeds.add(p);
				}
			}
			else
			{
				unusedPeds.add(p);
			}
		}
	}
	
	public void positionPed(ArrayList<Region> regions, Pedestrian p)
	{
		int region = JMath.randomRange(0, regions.size() - 1);
		Region r = regions.get(region);
		float x = JMath.randomRange
				((int)r.getRect().left(), (int)r.getRect().right());
		float y = JMath.randomRange
				((int)r.getRect().top(), (int)r.getRect().bottom());
		
		float dirX =  x > camera.getCamRect().getCenter().x ? 
				PED_MOVE_DIST : -PED_MOVE_DIST;
		float dirY =  y > camera.getCamRect().getCenter().y ?
				PED_MOVE_DIST : -PED_MOVE_DIST;;
		while(camera.getCamRect().getBoundingRect().contains(x, y))
		{
			x += dirX;
			y += dirY;
		}
		p.setCenter(x, y);
	}
	
	public void updatePeds()
	{
		int pedsNeeded = MAX_LIVING_PEDS - alivePeds.size();
		pedsNeeded = (int)JMath.max(pedsNeeded, MAX_LIVING_PEDS / 3.25f);
		
		OBB2D rect = camera.getCamRect();
		ArrayList<Region> regions = world.determineNeighbourBorderRegions(rect);
		
		for(int i = 0; i < alivePeds.size() && pedsNeeded > 0; ++i)
		{
			Pedestrian p = alivePeds.get(i);
			
			if(!rect.overlaps(p.getRect()))
			{
				positionPed(regions, p);
			}
		
			pedsNeeded--;
		}
		
		
		for(int i = 0; i < deadPeds.size() && pedsNeeded > 0; ++i)
		{
			Pedestrian p = deadPeds.get(i);
			p.reset();
			positionPed(regions, p);
			pedsNeeded--;
		}
		
		for(int i = 0; i < unusedPeds.size() && pedsNeeded > 0; ++i)
		{
			Pedestrian p = unusedPeds.get(i);
			
			world.addStatic(p);
			positionPed(regions, p);
			pedsNeeded--;
		}
	}
	
	public void update(float timeStep)
	{
		if(firstUpdate)
		{
			firstUpdate = false;
			generatePedLists();
			updatePeds();
			
		}
		timeSpent += timeStep;
		timeSpentGenList += timeStep;
		if(timeSpentGenList > 2.75f && !generatedLists)
		{
			generatedLists = true;
			generatePedLists();
		}
		
		if(timeSpent > 3.5f)
		{
			timeSpent = 0.0f;
			timeSpentGenList = 0.0f;
			generatedLists = false;
			updatePeds();
		}
		
		for(int i = 0; i < pedPool.size(); ++i)
		{
			Pedestrian p = pedPool.get(i);
			
			if(p.getWorld() != null)
			{
				p.update(timeStep);
			}
		}
	}
	

}
