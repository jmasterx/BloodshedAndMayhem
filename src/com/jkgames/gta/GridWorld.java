package com.jkgames.gta;

import java.util.ArrayList;

import android.util.Log;

public class GridWorld
{
	private Region[][] regions;
	private float startX;
	private float startY;
	private float regionW;
	private float regionH;
	private int regionsX;
	private int regionsY;
	private ArrayList<Region> determinedRegions = new ArrayList<Region>();
	private ArrayList<Region> determinedOuterRegions = new ArrayList<Region>();
	private ArrayList<Region> determinedNeighbourRegions = new ArrayList<Region>();
	private ArrayList<Entity> neighbourQueryObjs = new ArrayList<Entity>();
	private ArrayList<Entity> allQueryObjs = new ArrayList<Entity>();
	private ArrayList<RigidBody> neighbourQueryObjsDyn = new ArrayList<RigidBody>();
	
	
	public GridWorld(int xRegions, int yRegions, 
			float regionWidth, float regionHeight, float startX, float startY)
	{
		this.startX = startX;
		this.startY = startY;
		regionW = regionWidth;
		regionH = regionHeight;
		regionsX = xRegions;
		regionsY = yRegions;

		regions = new Region[xRegions][yRegions];
		
		for(int i = 0; i < regions.length; ++i)
		{
			for(int j = 0; j < regions[i].length; ++j)
			{
				regions[i][j] = 
						new Region(startX + (i * regionWidth),
								startY + (j * regionHeight),regionWidth,regionHeight,i,j);
			}
		}
		
		for(int i = 0; i < 4; ++i)
		{
			determinedRegions.add(null);
		}
		
	}
	
	public void addStatic(Entity entity)
	{
		entity.setWorld(this);
		updateStatic(entity);
		
		if((entity.getType() == Entity.TYPE_PED ||
				entity.getType() == Entity.TYPE_PLAYER) &&
				entity.isSolid() && !allQueryObjs.contains(entity))
		{
			allQueryObjs.add(entity);
		}
		
	}
	
	public void addDynamic(RigidBody body)
	{
		body.setWorld(this);
		updateDynamic(body);
		
		if(body.isSolid() && !allQueryObjs.contains(body))
		{
			allQueryObjs.add(body);
		}
	}
	
	public void removeStatic(Entity entity)
	{
		ArrayList<Region> objregions = entity.getRegions();
	
		//remove the old regions
		for(int i = 0; i < objregions.size(); ++i)
		{
			Region r = objregions.get(i);
			
			if(r != null)
			{
				r.removeStatic(entity);
			}
			
			objregions.set(i, null);
		}
		
		entity.setRegionCount(0);
		entity.setWorld(null);
		
		
		if(entity.isSolid())
		{
			allQueryObjs.remove(entity);
		}
	}
	
	public void removeDynamic(RigidBody entity)
	{
		ArrayList<Region> objregions = entity.getRegions();
		
		//remove the old regions
		for(int i = 0; i < objregions.size(); ++i)
		{
			Region r = objregions.get(i);
			if(r != null)
			{
				r.removeDynamic(entity);
			}
			
			objregions.set(i, null);
		}
		
		entity.setRegionCount(0);
		entity.setWorld(null);
		
		if(entity.isSolid())
		{
			allQueryObjs.remove(entity);
		}
	}
	
	public void updateStatic(Entity entity)
	{
		ArrayList<Region> determinedRegions = determineRegions(entity.getRect());
		ArrayList<Region> objregions = entity.getRegions();
		

		//remove the old regions
		for(int i = 0; i < objregions.size(); ++i)
		{
			if(objregions.get(i) != null && objregions.get(i) != determinedRegions.get(i))
			{
				objregions.get(i).removeStatic(entity);
				objregions.set(i, null);
			}
			
			
		}
		
		//add the regions
		int count = 0;
		for(int i = 0; i < determinedRegions.size(); ++i)
		{
			Region r = determinedRegions.get(i);
			
			
			if(r != null)
			{
				count++;
				if( objregions.get(i) == determinedRegions.get(i))
				{
					continue;
				}
					r.addStatic(entity);
					objregions.set(i, r);
					
			}
		
		}
		entity.setRegionCount(count);
	}
	
	public void updateDynamic(RigidBody entity)
	{
		ArrayList<Region> determinedRegions = determineRegions(entity.getRect());
		ArrayList<Region> objregions = entity.getRegions();
		

		//remove the old regions
		for(int i = 0; i < objregions.size(); ++i)
		{
			if(objregions.get(i) != null && objregions.get(i) != determinedRegions.get(i))
			{
				objregions.get(i).removeDynamic(entity);
				objregions.set(i, null);
			}
			
			
		}
		
		//add the regions
		int count = 0;
		for(int i = 0; i < determinedRegions.size(); ++i)
		{
			Region r = determinedRegions.get(i);
			
			
			if(r != null)
			{
				count++;
				if( objregions.get(i) == determinedRegions.get(i))
				{
					continue;
				}
					r.addDynamic(entity);
					objregions.set(i, r);
					
			}
		
		}
		entity.setRegionCount(count);
	}
	
	public ArrayList<Region> determineRegions(OBB2D rect)
	{
		Region lt = determineRegion(rect.left(), rect.top());
		if(lt != null)
			determinedRegions.set(0,lt);
		else
			determinedRegions.set(0, null);
		
		Region lb = determineRegion(rect.left(), rect.bottom());
		if(lb != null && lb != lt)
			determinedRegions.set(1,lb);
		else
			determinedRegions.set(1, null);
		
		Region rt = determineRegion(rect.right(), rect.top());
		if(rt != null && rt != lt && rt != lb)
			determinedRegions.set(2,rt);
		else
			determinedRegions.set(2, null);
		
		Region rb = determineRegion(rect.right(), rect.bottom());
		if(rb != null && rb != lt && rb != lb && rb != rt)
			determinedRegions.set(3,rb);
		else
			determinedRegions.set(3, null);
		
		return determinedRegions;
	}
	
	public ArrayList<Region> determineNeighbourRegions(OBB2D rect)
	{
		determinedNeighbourRegions.clear();
		Region lt = determineClampedRegion(rect.left(), rect.top());
		

		Region rb = determineClampedRegion(rect.right(), rect.bottom());
		
		int startX = lt.getIndexX();
		int endX = rb.getIndexX();
		int startY = lt.getIndexY();
		int endY = rb.getIndexY();
		
		for(int i = startX; i <= endX; ++i)
		{
			for(int j = startY; j <= endY; ++j)
			{
				determinedNeighbourRegions.add(regions[i][j]);
			}
		}
		
		
		return determinedNeighbourRegions;
	}
	
	public boolean isInBound(int x, int y)
	{
		return x>= 0 && y >= 0 && x < regionsX && y < regionsY;
	}
	
	public ArrayList<Region> determineNeighbourBorderRegions(OBB2D rect)
	{
		determinedOuterRegions.clear();
		Region lt = determineClampedRegion(rect.left(), rect.top());
		

		Region rb = determineClampedRegion(rect.right(), rect.bottom());
		
		int startX = lt.getIndexX() ;
		int endX = rb.getIndexX() ;
		int startY = lt.getIndexY() ;
		int endY = rb.getIndexY() ;
		
		for(int i = startX; i <= endX; ++i)
		{
			if(isInBound(i, startY))
			{
				determinedOuterRegions.add(regions[i][startY]);
			}
			
			if(isInBound(i, endY))
			{
				determinedOuterRegions.add(regions[i][endY]);
			}
		}
		
		for(int i = startY + 1; i < endY; ++i)
		{
			if(isInBound(startX, i))
			{
				determinedOuterRegions.add(regions[startX][i]);
			}
			
			if(isInBound(endX, i))
			{
				determinedOuterRegions.add(regions[endX][i]);
			}
		}
		
		return determinedOuterRegions;
	}
	
	public Region determineRegion(float x, float y)
	{
		int row = JMath.rowFromPos(startY, regionH, y);
		int col = JMath.colFromPos(startX, regionW, x);

		if(row >= 0 && row < regionsY  && col >= 0 && col < regionsX)
		{
			return regions[col][row];
		}
		
		return null;
	}
	
	public Region determineClampedRegion(float x, float y)
	{
		int row = JMath.rowFromPos(startY, regionH, y);
		int col = JMath.colFromPos(startX, regionW, x);
		
		col = (int)JMath.clamp(0, regionsX - 1, col);
		row = (int)JMath.clamp(0, regionsY - 1, row);
		return regions[col][row];
	}

	public Region[][] getRegions()
	{
		return regions;
	}

	public float getStartX()
	{
		return startX;
	}

	public float getStartY()
	{
		return startY;
	}

	public float getRegionW()
	{
		return regionW;
	}

	public float getRegionH() 
	{
		return regionH;
	}

	public int getRegionsX()
	{
		return regionsX;
	}

	public int getRegionsY()
	{
		return regionsY;
	}
	
	public ArrayList<Entity> queryRect(OBB2D rect)
	{
		neighbourQueryObjs.clear();
		ArrayList<Region> reg = determineNeighbourRegions(rect);
		for(int i = 0; i < Entity.MAX_LAYERS; ++i)
		{
			for(int j = 0; j < reg.size(); ++j)
			{
				Region r = reg.get(j);
				if(r == null)
				{
					continue;
				}
				
				for(int k = 0; k < r.getStatic(i).size(); ++k)
				{
					Entity e = r.getStatic(i).get(k);
					
					if(!neighbourQueryObjs.contains(e) && e.getRect().overlaps(rect))
					{
						neighbourQueryObjs.add(e);
					}
				}
				
				for(int k = 0; k < r.getDynamic(i).size(); ++k)
				{
					Entity e = r.getDynamic(i).get(k);
					if(!neighbourQueryObjs.contains(e) && e.getRect().overlaps(rect))
					{
						neighbourQueryObjs.add(e);
					}
				}
			}
		}
		
		return neighbourQueryObjs;
		
	}
	
	public ArrayList<Entity> queryStaticSolid(Entity ent, OBB2D rect)
	{
		neighbourQueryObjs.clear();
		ArrayList<Region> reg = ent.getRegions();
		for(int i = 0; i < Entity.MAX_LAYERS; ++i)
		{
			for(int j = 0; j < reg.size(); ++j)
			{
				Region r = reg.get(j);
				if(r == null)
				{
					continue;
				}
				
				for(int k = 0; k < r.getStatic(i).size(); ++k)
				{
					Entity e = r.getStatic(i).get(k);
					if(e != ent && !neighbourQueryObjs.contains(e) && e.isSolid() &&
							e.getRect().overlaps(rect))
					{
						neighbourQueryObjs.add(e);
					}
				}
				
			}
		}
		
		return neighbourQueryObjs;
		
	}
	
	public ArrayList<RigidBody> queryDynamicSolid(Entity ent, OBB2D rect)
	{
		neighbourQueryObjsDyn.clear();
		ArrayList<Region> reg = ent.getRegions();
		for(int i = 0; i < Entity.MAX_LAYERS; ++i)
		{
			for(int j = 0; j < reg.size(); ++j)
			{
				Region r = reg.get(j);
				if(r == null)
				{
					continue;
				}
				
				for(int k = 0; k < r.getDynamic(i).size(); ++k)
				{
					RigidBody e = r.getDynamic(i).get(k);
					if(e != ent && !neighbourQueryObjsDyn.contains(e) && e.isSolid() &&
							e.getRect().overlaps(rect))
					{
						neighbourQueryObjsDyn.add(e);
					}
				}
				
			}
		}
		
		return neighbourQueryObjsDyn;
		
	}
	
	public ArrayList<Entity> querySolid(Entity ent, OBB2D rect)
	{
		neighbourQueryObjs.clear();
		ArrayList<Region> reg = ent.getRegions();
		for(int i = 0; i < Entity.MAX_LAYERS; ++i)
		{
			final int szreg = reg.size();
			for(int j = 0; j < szreg; ++j)
			{
				Region r = reg.get(j);
				if(r == null)
				{
					continue;
				}
				
				ArrayList<Entity> re = r.getStatic(i);
				final int szre = re.size();
				for(int k = 0; k < szre; ++k)
				{
					Entity e = re.get(k);
					if(e != ent 
							&& e.isSolid() && !neighbourQueryObjs.contains(e)
							&& e.getRect().overlaps(rect) 
							)
					{
						neighbourQueryObjs.add(e);
					}
				}
				
			ArrayList<RigidBody> rr = r.getDynamic(i);
			final int szrr = rr.size();
				for(int k = 0; k < szrr; ++k)
				{
					Entity e = rr.get(k);
					if(e != ent 
							&& e.isSolid() 
							&& !neighbourQueryObjs.contains(e) &&
							e.getRect().overlaps(rect) 
							)
					{
						neighbourQueryObjs.add(e);
					}
				}
				
			}
		}
		
		return neighbourQueryObjs;
	}
	
	public void updateSolid(Entity e)
	{
		if(e.isSolid() && !allQueryObjs.contains(e))
		{
			allQueryObjs.add(e);
		}
		else if(!e.isSolid())
		{
			allQueryObjs.remove(e);
		}
	}
	
	public void removeSolid(Entity e)
	{
		allQueryObjs.remove(e);
	}
	
	public ArrayList<Entity> querySolid()
	{		
		return allQueryObjs;
	}
}
