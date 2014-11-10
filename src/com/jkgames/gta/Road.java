package com.jkgames.gta;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class Road extends Entity
{
	private Bitmap image = null;
	private Intersection startIntersection;
	private Intersection endIntersection;
	private boolean topBottom;
	private ArrayList<RoadSection> sections = new ArrayList<RoadSection>(); 

	public Road(RectF rect, Intersection start, Intersection end,
			Bitmap image, boolean topBottom)
	{
		setRect(rect);
		setStartIntersection(start);
		setEndIntersection(end);
		setImage(image);
		setTopBottom(topBottom);
		setSolid(false);
		generateSubRoads();
		setLayer(LAYER_ROAD);
		setType(TYPE_GROUND);
	}
	
	private void generateSubRoads()
	{
		float sizeW;
		float sizeH;
		if(isTopBottom())
		{
			sizeW = getWidth();
			sizeH = (sizeW / image.getWidth()) * image.getHeight();
		}
		else
		{
			sizeW = getHeight();
			sizeH = (sizeW / image.getWidth()) * image.getHeight();
			
		}
		
		int numTiles = isTopBottom() ? (int)Math.ceil(getHeight() / sizeH) :
			(int)Math.ceil(getWidth() / sizeW);
		
		for(int i = 0; i < numTiles; ++i)
		{
			if(isTopBottom())
			{
				float x = getRect().left();
				float y = getRect().top() + (sizeH  * i);
				
				sections.add(new RoadSection(this, image,
						x + (sizeW / 2.0f), 
						y + (sizeH / 2.0f), sizeW, sizeH, 0.0f));
			}
			else
			{
				float x = getRect().left() +  (sizeW  * i);
				float y = getRect().top();
				sections.add(new RoadSection(this, image,
						x + (sizeW / 2.0f), 
						y + (sizeH / 2.0f), sizeW, sizeH,  (float)Math.PI / 2.0f));
			}
			
		}
	}
	
	public ArrayList<RoadSection> getSubRoads()
	{
		return sections;
	}
	
	@Override
	public void draw(GraphicsContext c)
	{
		for(RoadSection s : sections)
		{
			s.draw(c);
		}
	}
	
	public Bitmap getImage() 
	{
		return image;
	}
	
	public void setImage(Bitmap image) 
	{
		this.image = image;
	}
	
	public Intersection getStartIntersection()
	{
		return startIntersection;
	}
	
	public void setStartIntersection(Intersection startIntersection) 
	{
		this.startIntersection = startIntersection;
	}
	
	public Intersection getEndIntersection()
	{
		return endIntersection;
	}

	public void setEndIntersection(Intersection endIntersection) 
	{
		this.endIntersection = endIntersection;
	}

	public boolean isTopBottom()
	{
		return topBottom;
	}

	public void setTopBottom(boolean topBottom) 
	{
		this.topBottom = topBottom;
	}
}
