package com.jkgames.gta;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BuildingGenerator
{
	private Resources res;
	private ArrayList<Road> roads;
	
	public static final int SPACE_X = 70;
	
	BuildingGenerator(Resources res, ArrayList<Road> roads)
	{
		this.res = res;
		this.roads = roads;
	}
	
	public ArrayList<Building> generateBuildings()
	{
		Bitmap buildingImg = BitmapFactory.decodeResource(res, R.drawable.building);
		ArrayList<Building> buildings = new ArrayList<Building>();
		
		float newBuildH = 0.0f;
		for(Road road : roads)
		{
			final float scalar = 1.0f;
			final float marginW = road.getHeight() / 2;
			final float marginH = road.getWidth() / 3;
			final float buildW = buildingImg.getWidth() * scalar;
			final float buildH = buildingImg.getWidth() * scalar;
	
			for(int i = 0; i < 2; ++i)
			{
				if(!road.isTopBottom())
				{	
					float posX = road.getRect().left() + marginW;
					float posY = road.getRect().top();
					float maxX = road.getRect().right() - marginW - posX;
			
					float totalBuildingW = SPACE_X + buildW;
					int numBuildings = (int)Math.floor(maxX / totalBuildingW);
					//margin - 1
					
					float spaceSize = SPACE_X * (numBuildings);
					float remainder = maxX - spaceSize;
					float newBuildingW = remainder / numBuildings;
					float newScalar = newBuildingW / buildW;
					float newBuildingH = buildingImg.getHeight() * newScalar;
					if(newBuildingH > newBuildH)
					{
							newBuildH = newBuildingH;
					}
				
					
					
					
					for(int j = 0; j < numBuildings; ++j)
					{
							buildings.add(new Building(buildingImg, 
									Building.DIRECTION_TOP, newScalar,
									posX + (newBuildingW / 2),
									posY - marginW - (newBuildingH / 2)));
							
							buildings.add(new Building(buildingImg, 
									Building.DIRECTION_BOTTOM, newScalar,
									posX + (newBuildingW / 2),
									posY + road.getHeight() + marginW + (newBuildingH / 2)));
							
							posX += newBuildingW + SPACE_X;
					}
					
				}
				else
				{
					float posX = road.getRect().left();
					float posY = road.getRect().top()  + marginH + newBuildH;
					float maxX = road.getRect().bottom() - marginH - newBuildH - posY;
		
					float totalBuildingW = SPACE_X + buildW;
					int numBuildings = (int)Math.ceil(maxX / totalBuildingW);
					//margin - 1
					
					float spaceSize = SPACE_X * (numBuildings);
					float remainder = maxX - spaceSize;
					float newBuildingW = remainder / numBuildings;
					float newScalar = newBuildingW / buildW;
					float newBuildingH = buildingImg.getHeight() * newScalar;
					
					
					
					for(int j = 0; j < numBuildings; ++j)
					{
							buildings.add(new Building(buildingImg, 
									Building.DIRECTION_RIGHT, newScalar,
									posX - (newBuildingH / 2) - marginH,
									posY + (newBuildingW / 2)));
							
							buildings.add(new Building(buildingImg, 
									Building.DIRECTION_LEFT, newScalar,
									posX + (newBuildingH / 2) + marginH + road.getWidth(),
									posY + (newBuildingW / 2)));
							
							posY += newBuildingW + SPACE_X;
					}
				}
			}
		}
		
		
		return buildings;
	}
}
