package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;


public class GraphicsContext
{
	private Canvas canvas;
	private Matrix matrix = new Matrix();
	private Paint paint = new Paint();

	public GraphicsContext()
	{
		paint.setColor(Color.BLACK);
	}
	
	public Canvas getCanvas() 
	{
		return canvas;
	}

	public void setCanvas(Canvas canvas) 
	{
		this.canvas = canvas;
	}
	
	public void drawRotatedScaledBitmap(Bitmap b, 
			float centerX, float centerY, float width, float height, float angle)
	{
		float scaleX = width / b.getWidth();
		float scaleY = height / b.getHeight();
		centerX -= (b.getWidth() * scaleX) / 2.0f;
		centerY -= (b.getHeight() * scaleY) / 2.0f;
		matrix.reset();
		matrix.setTranslate(centerX, centerY);
		matrix.postRotate(angle * (180.0f / (float)(Math.PI)),
				centerX + (b.getWidth() * scaleX) / 2.0f,
				centerY + (b.getHeight() * scaleY) / 2.0f); 
		matrix.preScale(scaleX,scaleY);
		canvas.drawBitmap(b, matrix, null);
	}
	
	public void drawRotatedBitmap(Bitmap b, 
			float centerX, float centerY, float angle)
	{
		drawRotatedScaledBitmap(b, centerX, centerY,
				b.getWidth(), b.getHeight(), angle);
	}
	
	public void setBackgroundColor(int c)
	{
		paint.setColor(c);
	}
	
	public void clear()
	{
		getCanvas().drawRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight(), paint);
	}
	
	
}
