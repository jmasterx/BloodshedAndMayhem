package com.jkgames.gta;

import android.graphics.Color;
import android.graphics.Paint;

public class ScoreManager implements IDrawable
{
	private Paint paint = new Paint();
	private Paint stroke = new Paint();
	private int score;
	private String scoreStr;
	private int w;
	private int h;
	
	public ScoreManager()
	{
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(1.5f);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		stroke.setColor(Color.BLACK);
		stroke.setStrokeWidth(1.5f);
		stroke.setStyle(Paint.Style.STROKE);
		
		increaseScore(0);
	}
	
	public void onResize(int w, int h)
	{
		this.w = w;
		this.h = h;
		stroke.setTextSize(w * 0.045f);
		paint.setTextSize(w * 0.045f);
	}
	
	public void increaseScore(int amount)
	{
		score += amount;
		scoreStr = "$" + String.valueOf(score);
	}

	public void draw(GraphicsContext c) 
	{
		c.getCanvas().drawText(scoreStr, (0.01f * w), paint.getTextSize() + (0.01f * w), paint);
		c.getCanvas().drawText(scoreStr, (0.01f * w), paint.getTextSize() + (0.01f * w), stroke);
		
	}
	
	
}
