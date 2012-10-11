package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class Intersection extends Entity
{
	Road topRoad;
	Road leftRoad;
	Road bottomRoad;
	Road rightRoad;
	Bitmap image;
	
	public Bitmap getImage() 
	{
		return image;
	}

	public void setImage(Bitmap image)
	{
		this.image = image;
	}

	public Intersection(RectF rect, Bitmap image)
	{
		setRect(rect);
		setImage(image);
	}
	
	public Road getTopRoad() 
	{
		return topRoad;
	}

	public void setTopRoad(Road topRoad)
	{
		this.topRoad = topRoad;
	}

	public Road getLeftRoad()
	{
		return leftRoad;
	}

	public void setLeftRoad(Road leftRoad)
	{
		this.leftRoad = leftRoad;
	}

	public Road getBottomRoad() 
	{
		return bottomRoad;
	}

	public void setBottomRoad(Road bottomRoad)
	{
		this.bottomRoad = bottomRoad;
	}

	public Road getRightRoad()
	{
		return rightRoad;
	}

	public void setRightRoad(Road rightRoad) 
	{
		this.rightRoad = rightRoad;
	}
	
	@Override
	public void draw(GraphicsContext c)
	{
		c.drawRotatedScaledBitmap(image, getCenterX(), getCenterY(), getWidth(), getHeight(), 0.0f);
	}
	
}
