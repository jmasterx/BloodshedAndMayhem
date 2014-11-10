package com.jkgames.gta;

import java.util.ArrayList;

public class VehiclePlotter
{
	public static final int CAR_LENGTH = 150;
	public static final int MIN_CAR_DIST = 50;
	public static final int MAX_CAR_DIST = 300;
	
	public static ArrayList<Vehicle> plotVehicles(
			GridWorld world, VehicleFactory vf, ArrayList<Road> roads)
	{
		ArrayList<Vehicle> cars = new ArrayList<Vehicle>();
		for(Road r : roads)
		{
			if(r.isTopBottom())
			{
				float y = r.getRect().top();
				while(true)
				{
					Vehicle v = vf.generateRandom();
					v.setCenter(r.getRect().left(), y + (v.getHeight() / 2));
					
					y += v.getHeight();
					
					if(y > r.getRect().bottom())
					{
						break;
					}
					else
					{
						cars.add(v);
						world.addStatic(v);
						
						y += JMath.randomRange(MIN_CAR_DIST, MAX_CAR_DIST);
					}
				}
				
				y = r.getRect().top();
				while(true)
				{
					Vehicle v = vf.generateRandom();
					v.setCenter(r.getRect().right(), y + (v.getHeight() / 2));
					
					y += v.getHeight();
					
					if(y > r.getRect().bottom())
					{
						break;
					}
					else
					{
						v.setAngle((float)Math.PI);
						cars.add(v);
						world.addStatic(v);
						
						y += JMath.randomRange(MIN_CAR_DIST, MAX_CAR_DIST);
					}
				}
				
			}
			else
			{
				float x = r.getRect().left() + CAR_LENGTH;
				while(true)
				{
					Vehicle v = vf.generateRandom();
					v.setAngle((float)Math.PI / 2.0f);
					v.setCenter(x + (v.getWidth() / 2), r.getRect().top());
					
					x += v.getWidth();
					
					if(x > r.getRect().right() - CAR_LENGTH)
					{
						break;
					}
					else
					{
						cars.add(v);
						world.addStatic(v);
						
						x += JMath.randomRange(MIN_CAR_DIST, MAX_CAR_DIST);
					}
				}
				
				x = r.getRect().left() + CAR_LENGTH;
				while(true)
				{
					Vehicle v = vf.generateRandom();
					v.setAngle((float)Math.PI / 2.0f);
					v.setCenter(x + (v.getWidth() / 2),
							r.getRect().bottom());
					
					x += v.getWidth();
					
					if(x > r.getRect().right() - CAR_LENGTH)
					{
						break;
					}
					else
					{
						v.setAngle(v.getAngle() + (float)Math.PI);
						cars.add(v);
						world.addStatic(v);
						
						x += JMath.randomRange(MIN_CAR_DIST, MAX_CAR_DIST);
					}
				}
			}
		}
		
		return cars;
	}
}
