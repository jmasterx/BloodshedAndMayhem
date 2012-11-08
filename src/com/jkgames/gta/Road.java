package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class Road extends Entity
{
	private Bitmap image = null;
	private Intersection startIntersection;
	private Intersection endIntersection;
	private boolean topBottom;

	public Road(RectF rect, Intersection start, Intersection end,
			Bitmap image, boolean topBottom)
	{
		setRect(rect);
		setStartIntersection(start);
		setEndIntersection(end);
		setImage(image);
		setTopBottom(topBottom);
	}
	
	@Override
	public void draw(GraphicsContext c)
	{
		//Rect clipRect = c.getCanvas().getClipBounds();
		//c.getCanvas().clipRect(getRect());
		OBB2D view = c.getViewRect();
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
				
				if(view.overlaps(x,y,x +sizeW, y + sizeH))
				{
						c.drawRotatedScaledBitmap(
						image,
						x + (sizeW / 2.0f),
						y + (sizeH / 2.0f), 
						sizeW, sizeH, 0.0f);
				}
			
			}
			else
			{
				float x = getRect().left() +  (sizeW  * i);
				float y = getRect().top();
				if(view.overlaps(x,y,x + sizeW,y + sizeH))
				{
					c.drawRotatedScaledBitmap(
							image,
							x + (sizeH / 2.0f),
							y + (sizeH / 2.0f), 
							sizeW, sizeH, (float)Math.PI / 2.0f);
				}
			}
			
		}
		
	//	c.getCanvas().clipRect(clipRect);
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
