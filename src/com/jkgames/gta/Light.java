package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;

public class Light extends Entity
{
	private Bitmap image;
	private Paint paint = new Paint();
	public Light(Bitmap light, RectF rect, int color)
	{
		image = light;
		setRect(rect);
	
		paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
	}
	
	public void draw(GraphicsContext g)
	{
		g.additiveBlend();
		g.getCanvas().drawBitmap(image, null, getRect(), paint);
		g.restoreBlend();
	}
}
