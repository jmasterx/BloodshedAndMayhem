//Class Name: PuzzleView.java
//Purpose: Main class for puzzle
//Created by Josh on 2012-09-04
package com.jkgames.gta;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameView extends SurfaceView implements Runnable, OnTouchListener
{
	private Game game;
	private boolean sizeHasChanged = false;
	private GraphicsContext graphicsContext = new GraphicsContext();
	private Camera camera = new Camera();

	private float startTouchX;
	private float startTouchY;
	
	private City city = null;
	
	private final static int 	MAX_FPS = 60;
	// maximum number of frames to be skipped
	private final static int	MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int	FRAME_PERIOD = 1000 / MAX_FPS;	
	
	private Thread	thread = null;	// thread that will run the animation
	private volatile boolean running; // = true if animation is running
	private SurfaceHolder holder;
	private InputHandler input = null;
	
	private Paint p = new Paint();
	public GameView(Context context) 
	{
		super(context);
		game = (Game)context;
		// in order to render to a SurfaceView from a different thread than the UI thread,
		// we need to acquire an instance of the SurfaceHolder class.
		holder = getHolder();
		setFocusable(true);
		setFocusableInTouchMode(true);
		running = false;
		camera.setScale(2.0f);
		graphicsContext.setCamera(camera);
		input = new InputHandler(game.getResources());
		
		Resources res = game.getResources();
		Bitmap mainRoad = BitmapFactory.decodeResource(res, R.drawable.main_road);
		Bitmap mainIntersection = BitmapFactory.decodeResource(res, R.drawable.road_intersection);
		
		ArrayList<Road> roads = new ArrayList<Road>();
		ArrayList<Intersection> intersections = new ArrayList<Intersection>();
				
		CityGenerator cityGen = new CityGenerator(30, 30, 512, 512);
		cityGen.generateIntersectionsAndRoads(intersections, roads,
				mainIntersection, mainRoad);
		
		city = new City(roads,intersections);
	}
	
	protected void onDrawTransformed(GraphicsContext g)
	{	
		city.draw(g);
	}
	
	protected void onDrawUntransformed(GraphicsContext g)
	{	
		input.draw(g);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
	   super.onSizeChanged(w, h, oldw, oldh);
	   
	   if(!sizeHasChanged)
	   {
		   sizeHasChanged = true;   
		   input.onResize(w, h);
	   }
	}
	
	public void touchBegin(MotionEvent e)
	{
		startTouchX = e.getX();
		startTouchY = e.getY();
	}
	
	public float getTouchDeltaX(MotionEvent e)
	{
		return e.getX() - startTouchX;
	}
	
	public float getTouchDeltaY(MotionEvent e)
	{
		return e.getY() - startTouchY;
	}
	
	public boolean onTouch(View v, MotionEvent e)
	{
		if(e.getAction() == MotionEvent.ACTION_DOWN)
		{
			touchBegin(e);
		}
		else if(e.getAction() == MotionEvent.ACTION_MOVE && e.getActionIndex() == 0)
		{
		}
		if(e.getAction() == MotionEvent.ACTION_UP && e.getActionIndex() == 0)
		{
		}
	
		return true;
	}
	
	public void resume()
	{
		// start the animation thread
		thread = new Thread (this);
		thread.start ();
		running = true;
	}

	public void pause()
	{
		// set the running flag to false. this will stop the animation loop
		// running was defined as volatile since it is used by two different
		// threads
		running = false;

		// we wait for the thread to die
		while (true)
		{
			try
			{
				thread.join ();
				break;
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	public void run() {
		Canvas canvas;

		long beginTime;		// the time when the cycle begun
		long timeDiff;		// the time it took for the cycle to execute
		int sleepTime;		// ms to sleep (<0 if we're behind)
		int framesSkipped;	// number of frames being skipped 

		sleepTime = 0;

		while (running) 
		{
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try 
			{
				
				// we have to make sure that the surface has been created
				// if not we wait until it gets created
				if (!holder.getSurface ().isValid())
					continue;
				canvas = this.holder.lockCanvas();
			synchronized (holder) 
			{
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;	// resetting the frames skipped
					// update game state
					update();
					// render state to the screen
					// draws the canvas on the panel
					display(canvas);
					// calculate how long did the cycle take
					timeDiff = System.currentTimeMillis() - beginTime;
					// calculate sleep time
					sleepTime = (int)(FRAME_PERIOD - timeDiff);

					if (sleepTime > 0) 
					{
						// if sleepTime > 0 we're OK
						try 
						{
							// send the thread to sleep for a short period
							// very useful for battery saving
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {}
					}

					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) 
					{
						// we need to catch up
						// update without rendering
						update();
						// add frame period to check if in next frame
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
				}
			} 
			finally 
			{
				// in case of an exception the surface is not left in
				// an inconsistent state
				if (canvas != null) 
				{
					holder.unlockCanvasAndPost(canvas);
				}
			}	// end finally
		}
	}

	private void update()
	{
	}

	private void display(Canvas canvas)
	{
		graphicsContext.setCanvas(canvas);
		graphicsContext.clear();
		camera.applyTransform(graphicsContext);
		onDrawTransformed(graphicsContext);
		graphicsContext.identityTransform();
		onDrawUntransformed(graphicsContext);
	}
}
