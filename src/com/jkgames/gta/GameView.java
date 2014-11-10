//Class Name: PuzzleView.java
//Purpose: Main class for puzzle
//Created by Josh on 2012-09-04
package com.jkgames.gta;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
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
	private final static int	MAX_FRAME_SKIPS = 4;
	// the frame period
	private final static int	FRAME_PERIOD = 1000 / MAX_FPS;	
	
	private Thread	thread = null;	// thread that will run the animation
	private volatile boolean running; // = true if animation is running
	private SurfaceHolder holder;
	private InputHandler input = null;
	private Bitmap sideWalk;
	private ArrayList<Pedestrian> dudes = new ArrayList<Pedestrian>();
	
	private GridWorld world = null;
	private Ground ground;
	private CollisionSolver solver;
	private CollisionEventHandler colHandler;
	private Player holyPlayer;
	private PlayerManager playerManager;
	private SoundManager soundManager;
	private ScoreManager scoreManager = new ScoreManager();
	private VehicleFactory vehicleFactory = null;
	private PedestrianManager pedMan = null;
	private long lastTimeMs;
	
	public static final float CAMERA_SCALE = 1.7f;

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
		camera.setScale(CAMERA_SCALE);
		graphicsContext.setCamera(camera);
		input = new InputHandler(game.getResources());
				
		CityGenerator cityGen = new CityGenerator(35, 35, 1000, 1000);
		
		world = new GridWorld(80, 80, 500, 500,-2000, -2000);
		
		
		city = cityGen.generateCity(game.getResources());
		
		sideWalk = BitmapFactory.decodeResource(game.getResources(), R.drawable.side_walk);
		ground = new Ground(sideWalk);
		
		vehicleFactory = new VehicleFactory(context);
		
		VehiclePlotter.plotVehicles(world, vehicleFactory, city.getRoads());
		
		for(Building b : city.getBuildings())
		{
			world.addStatic(b);
		}
		
		for(Intersection i : city.getIntersections())
		{
			world.addStatic(i);
		}
		
		for(Road r : city.getRoads())
		{
			for(RoadSection rs : r.getSubRoads())
			world.addStatic(rs);
		}
		
		Bitmap girl = BitmapFactory.decodeResource(game.getResources(), R.drawable.lady_sheet);
		Bitmap guy = BitmapFactory.decodeResource(game.getResources(), R.drawable.man_sheet);
		Bitmap holy = BitmapFactory.decodeResource(game.getResources(), R.drawable.holy_sheet);
		Bitmap blood = BitmapFactory.decodeResource(game.getResources(), R.drawable.blood);
		
		soundManager = new SoundManager(context);
		colHandler = new CollisionEventHandler(soundManager,scoreManager);
		solver = new CollisionSolver(world, input);
		solver.addListener(colHandler);
		
		holyPlayer = new Player(holy, blood);
		int randomRoad = JMath.randomRange(0,city.getRoads().size() - 1);
		Road road = city.getRoads().get(randomRoad);
		holyPlayer.setCenter(road.getRect().left(), road.getRect().top());
		
		world.addStatic(holyPlayer);
		playerManager = new PlayerManager(holyPlayer, input, 
				world, camera,soundManager);
		pedMan = new PedestrianManager(world, camera, playerManager, guy, girl, blood);
		input.addInputListener(playerManager);
	}
	
	protected void onDrawTransformed(GraphicsContext g)
	{	
		OBB2D view = g.getCamera().getCamRect(getWidth(), getHeight());
		ground.draw(g);
		ArrayList<Entity> ents = world.queryRect(view);
		for(int i = 0; i < ents.size(); ++i)
		{
			ents.get(i).draw(g);
		}

	}
	
	protected void onDrawUntransformed(GraphicsContext g)
	{	
		input.draw(g);
		scoreManager.draw(g);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
	   super.onSizeChanged(w, h, oldw, oldh);
	   
	   if(!sizeHasChanged)
	   {
		   sizeHasChanged = true;   
		   input.onResize(w, h);
		   playerManager.onResize(w, h);
		   scoreManager.onResize(w, h);
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
		soundManager.halt();

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
		Canvas canvas;

		long beginTime;		// the time when the cycle begun
		long timeDiff;		// the time it took for the cycle to execute
		int sleepTime;		// ms to sleep (<0 if we're behind)
		int framesSkipped;	// number of frames being skipped 
		lastTimeMs = System.currentTimeMillis();
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
		long deltaTime = System.currentTimeMillis() - lastTimeMs;
		lastTimeMs = System.currentTimeMillis();
		float timeSpent = deltaTime / 1000.0f;
		input.update(timeSpent);
		playerManager.update(timeSpent);
		pedMan.update(timeSpent);
		solver.update(timeSpent);
		soundManager.update(timeSpent);
	}

	private void display(Canvas canvas)
	{
		graphicsContext.setCanvas(canvas);
		graphicsContext.generateViewRect();
		graphicsContext.clear();
		camera.applyTransform(graphicsContext);
		onDrawTransformed(graphicsContext);
		graphicsContext.identityTransform();
		onDrawUntransformed(graphicsContext);
	}
}
