package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class RoadSection extends Entity 
{
	private Road parent;
	private Bitmap image;

	public RoadSection(Road parent, Bitmap image,
			float centerX,float centerY,float width, float height, float angle) 
	{
		this.parent = parent;
		this.image = image;
		
		setSolid(false);
		setType(TYPE_GROUND);
		getRect().set(centerX, centerY,width,height,angle);
	}

	public Road getParent() 
	{
		return parent;
	}

	public Bitmap getImage()
	{
		return image;
	}
	
	@Override
	public void draw(GraphicsContext g)
	{
		g.drawRotatedScaledBitmap(image, getCenterX(), getCenterY(), 
				getWidth(), getHeight(), getAngle());
	}
}
