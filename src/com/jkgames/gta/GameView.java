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
	private int gameDifficulty;
	private boolean sizeHasChanged = false;
	private GraphicsContext graphicsContext = new GraphicsContext();
	private Camera camera = new Camera();
	private ArrayList<Road> roads = new ArrayList<Road>();
	private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	private float startTouchX;
	private float startTouchY;
	
	// constants for the animation
	private static final float	FRAME_RATE	= 1.0f / 60.0f; // NTSC 60 FPS
	private Thread	thread = null;	// thread that will run the animation
	private volatile boolean running; // = true if animation is running
	private SurfaceHolder holder;
	private CityGenerator city = new CityGenerator(40, 35, 400, 500);
	
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
		gameDifficulty = game.getIntent().getIntExtra("game_difficulty", 0);
		running = false;
		camera.setScale(0.1f);
		
		Resources res = game.getResources();
		Bitmap mainRoad = BitmapFactory.decodeResource(res, R.drawable.main_road);
		Bitmap mainIntersection = BitmapFactory.decodeResource(res, R.drawable.road_intersection);
		city.generateIntersectionsAndRoads(intersections, roads, mainIntersection, mainRoad);
		
		p.setColor(Color.RED);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{	
		graphicsContext.setCanvas(canvas);
		graphicsContext.clear();
		camera.applyTransform(graphicsContext);
		RectF screen = camera.getCamRect(getWidth(), getHeight());
		for(Road r : roads)
		{
			if(RectF.intersects(screen, r.getRect()) || screen.contains(r.getRect()))
			{
				r.draw(graphicsContext);
			}
		}
		
		for(Intersection i : intersections)
		{
			if(RectF.intersects(screen, i.getRect()) || screen.contains(i.getRect()))
			{
				i.draw(graphicsContext);
			}
		}
		
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
	   super.onSizeChanged(w, h, oldw, oldh);
	   
	   if(!sizeHasChanged)
	   {
		   sizeHasChanged = true;   
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
			camera.beginDeltaTracking();
		}
		else if(e.getAction() == MotionEvent.ACTION_MOVE && e.getActionIndex() == 0)
		{
			camera.deltaMove(-getTouchDeltaX(e), -getTouchDeltaY(e));
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
	
	public void run()
	{
		// this is the method that gets called when the thread is started.

		// first we get the current time before the loop starts
		long startTime = System.currentTimeMillis();

		// start the animation loop
		while (running)
		{
			// we have to make sure that the surface has been created
			// if not we wait until it gets created
			if (!holder.getSurface ().isValid())
				continue;

			// get the time elapsed since the loop was started
			// this is important to achieve frame rate-independent movement,
			// otherwise on faster processors the animation will go too fast
			float timeElapsed = (System.currentTimeMillis () - startTime);

			// is it time to display the next frame?
			if (timeElapsed > FRAME_RATE)
			{
				// compute the next step in the animation
				update();

				// display the new frame
				display();

				// reset the start time
				startTime = System.currentTimeMillis();
			}
		}

		// run is over: thread dies
	}
	

	private void update()
	{
		
	}

	private void display()
	{
		// we lock the surface for rendering and get a Canvas instance we can use
		Canvas canvas = holder.lockCanvas();
		onDraw(canvas);
		// we unlock the surface and make sure that what we've drawn via the Canvas gets
		// displayed on the screen
		holder.unlockCanvasAndPost(canvas);
	}
}
