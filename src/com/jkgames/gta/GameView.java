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
	private Bitmap sideWalk;
	
	private Vehicle vehicle = new Vehicle();

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
		camera.setScale(1.75f);
		graphicsContext.setCamera(camera);
		input = new InputHandler(game.getResources());
				
		CityGenerator cityGen = new CityGenerator(30, 30, 1100, 1100);
		
		city = cityGen.generateCity(game.getResources());
		
		sideWalk = BitmapFactory.decodeResource(game.getResources(), R.drawable.side_walk);
		
		Bitmap taxi = BitmapFactory.decodeResource(game.getResources(), R.drawable.taxi);
		vehicle.initialize(new Vector2D(taxi.getWidth() / 20.0f,taxi.getHeight() / 20.0f), 4.45f, taxi);
		vehicle.setLocation(new Vector2D(0, 0), 0);
	
		camera.setPosition(-100, -100);
	}
	
	protected void onDrawTransformed(GraphicsContext g)
	{	
		RectF view = g.getCamera().getCamRect(getWidth(), getHeight());
		float w = sideWalk.getWidth();
		float h = sideWalk.getHeight();
		int numX = (int)Math.ceil(view.width() / (float)w) + 2;
		int numY = (int)Math.ceil(view.height() / (float)h) + 2;
		
		float sx = (float)((int)view.left % (int)w) + w;
		float sy = (float)((int)view.top % (int)h) + h;
	
		for(int i = 0; i < numX; ++i)
		{
			for(int j = 0; j < numY; ++j)
			{
				g.drawRotatedScaledBitmap(sideWalk, view.left - sx + 
						(i * sideWalk.getWidth()),
						view.top - sy + (j * sideWalk.getHeight()),
						w + 0.02f,h + 0.02f,0.0f);
			}
		}
		city.draw(g);
		vehicle.draw(g);
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
	
	public boolean onTouch(View v, MotionEvent e)
	{
		input.onTouch(e);
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
		camera.setPosition((vehicle.getPosition().x * camera.getScale()) - ((getWidth() ) / 2.0f),
				(vehicle.getPosition().y * camera.getScale()) - ((getHeight() ) / 2.0f));
		if(input.isPressed(ControlButton.BUTTON_GAS))
		{
			vehicle.setThrottle(1.0f, false);
		}
		
		if(input.isPressed(ControlButton.BUTTON_BRAKE))
		{
			vehicle.setBrakes(1.0f);
		}

		vehicle.setSteering(input.getAnalogStick().getStickValueX());
		

		vehicle.update(16.6666f / 1000.0f);
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
