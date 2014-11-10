package com.jkgames.gta;

import android.graphics.Rect;

public class TileAnimation
{
	private Rect coordRect = new Rect();


	private int cols;
	private int rows;
	private int tileWidth;
	private int tileHeight;
	private int startFrame;
	private int curFrame;
	private int endFrame;
	private float time;
	private float rate;
	private boolean running = false;
	
	public static final float UPDATE_INTERVAL = 1.0f / 60.0f;
	
	public TileAnimation(int bmpW, int bmpH,int tileW, 
			int tileH, int startFrame, int numFrames, int frameRate)
	{
		cols = bmpW / tileW;
		rows = bmpH / tileH;
		tileWidth = tileW;
		tileHeight = tileH;
		this.startFrame  = startFrame;
		this.endFrame = startFrame + numFrames;
		rate = 1.0f / frameRate;
		
		reset();
		time = rate;
	}
	
	public void reset()
	{
		curFrame = startFrame;
		time = 0.0f;
	}
	
	public void start()
	{
		reset();
		running = true;
	}
	
	public void stop()
	{
		running = false;
	}
	
	public void computeFrame(int frame)
	{
		int ix = (frame % cols);
		int iy = (int)Math.floor(frame / cols);
		coordRect.left = ix * tileWidth;
		coordRect.top = iy * tileHeight;
		coordRect.right = coordRect.left + tileWidth;
		coordRect.bottom = coordRect.top + tileHeight;
	}
	
	public void update()
	{
		if(running)
		{
			time += UPDATE_INTERVAL;
			if(time >= rate)
			{
				time = 0.0f;
				computeFrame(curFrame);
				curFrame++;
			
				if(curFrame >= endFrame)
				{
					curFrame = startFrame;
				}
			}
		}

	}
	
	public Rect getCoordRect()
	{
		return coordRect;
	}

	public int getCols() 
	{
		return cols;
	}

	public int getRows()
	{
		return rows;
	}

	public int getTileWidth() 
	{
		return tileWidth;
	}

	public int getTileHeight()
	{
		return tileHeight;
	}

	public int getCurFrame()
	{
		return curFrame;
	}
}
