package com.jkgames.gta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class VehicleFactory 
{
	private Map<String, Bitmap> bitmapMap = new HashMap<String, Bitmap>();
	private ArrayList<String>  names = new ArrayList<String>();
	
	private void map(String s, int resource, Context c)
	{
		Bitmap b = BitmapFactory.decodeResource(c.getResources(), resource);
		bitmapMap.put(s, b);
		names.add(s);
	}
	
	public VehicleFactory(Context c)
	{
		map("ambulance",R.drawable.car_ambulance,c);
		map("bat",R.drawable.car_bat,c);
		map("beamer",R.drawable.car_beamer,c);
		map("beatle",R.drawable.car_beatle,c);
		map("bus",R.drawable.car_bus,c);
		map("classic",R.drawable.car_classic,c);
		map("cop",R.drawable.car_cop,c);
		map("fire_truck",R.drawable.car_fire_truck,c);
		map("home",R.drawable.car_home,c);
		map("hot_dog",R.drawable.car_hot_dog,c);
		map("mini",R.drawable.car_mini,c);
		map("mustang",R.drawable.car_mustang,c);
		map("roadster",R.drawable.car_roadster,c);
		map("speedster",R.drawable.car_speedster,c);
		map("sport",R.drawable.car_sport,c);
		map("tank",R.drawable.car_tank,c);
		map("tanker",R.drawable.car_tanker,c);
		map("taxi",R.drawable.car_taxi,c);
		map("truckbed",R.drawable.car_truckbed,c);
		map("viper",R.drawable.car_viper,c);
	}
	
	public Bitmap getRandomCarImage()
	{
		int r = JMath.randomRange(0, names.size() - 1);
		
		return getCarImage(names.get(r));
	}
	
	public Bitmap getCarImage(String s)
	{
		return bitmapMap.get(s);
	}
	
	public Vehicle generate(Bitmap b)
	{
		Vehicle v = new Vehicle();
		v.initialize(new Vector2D(b.getWidth() / 20.0f,b.getHeight() / 20.0f), 4.45f, b);
		v.setLocation(new Vector2D(0, 0), 0);
		
		return v;
	}
	
	public Vehicle generateRandom()
	{
		Bitmap b = getRandomCarImage();
		return generate(b);
	}
}
