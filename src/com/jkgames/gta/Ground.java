package com.jkgames.gta;

import android.graphics.Bitmap;

public class Ground implements  IDrawable
{
	private Bitmap tile;
	
	public Ground(Bitmap tile)
	{
		this.tile = tile;
	}

	public void draw(GraphicsContext c) 
	{
		OBB2D view = c.getCamera().getCamRect();
		
		float w = tile.getWidth();
		float h = tile.getHeight();
		int numX = (int)Math.ceil(view.getWidth() / (float)w) + 3;
		int numY = (int)Math.ceil(view.getHeight() / (float)h) + 3;
		
		float sx = (float)((int)view.left() % (int)w) + w;
		float sy = (float)((int)view.top() % (int)h) + h;
	
		for(int i = 0; i < numX; ++i)
		{
			for(int j = 0; j < numY; ++j)
			{
				c.drawRotatedScaledBitmap(tile, view.left() - sx + 
						(i * tile.getWidth()),
						view.top() - sy + (j * tile.getHeight()),
						w + 0.025f,h + 0.025f,0.0f);
			}
		}
		
	}
}
