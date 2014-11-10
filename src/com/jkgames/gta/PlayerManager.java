package com.jkgames.gta;

import java.util.ArrayList;

public class PlayerManager implements InputListener
{

	private Player player;
	private InputHandler input;
	private GridWorld world;
	private Vehicle playerVehicle = null;
	private Camera camera;
	private ArrayList<Vehicle> oldCars = new ArrayList<Vehicle>();
	private int screenWidth;
	private int screenHeight;
	private OBB2D queryRect = new OBB2D(0,0,0,0);
	private SoundManager sound;
	private int walkSoundId;
	private int engineIdleSoundId;
	private int carMovingSoundId;
	private int carMovingLoopSoundId;
	private float timeToChange = 0.0f;
	private boolean needsToLoopEngine;
	private boolean needsToActivateRadio;
	private float radioActivationTime;
	
	private int slideTrackingFrames;
	
	private static final int MAX_SLIDE_FRAMES = 60;
	private static final float SOUND_CHANGE_TIME = 2.6f;
	private static final float RADIO_CHANGE_TIME = 1.5f;
	private static final float EXIT_CAR_DIST = 4.0f;
	

	
	public PlayerManager(Player player, InputHandler handler, 
			GridWorld world, Camera camera, SoundManager sound)
	{
		this.player = player;
		this.input = handler;
		this.world = world; 
		this.camera = camera;
		this.sound = sound;
	}
	
	public void onResize(int w, int h)
	{
		screenWidth = w;
		screenHeight = h;
	}
	
	public void handleCarTheft()
	{
		final float multiplier = 4.0f;
		
		queryRect.set(player.getCenterX(),player.getCenterY(),
				player.getWidth() * multiplier,player.getHeight() * multiplier,0.0f);
		
		ArrayList<Entity> ents = world.queryRect(queryRect);
		
		for(int i = 0; i < ents.size(); ++i)
		{
			if(ents.get(i).getType() == Entity.TYPE_VEHICLE)
			{
				playerVehicle = (Vehicle)ents.get(i);
				for(int x = oldCars.size() - 1; x >= 0; x--)
				{
				    if(oldCars.get(x) == playerVehicle)
				    {
				    	world.removeSolid(oldCars.get(x));
				        oldCars.remove(x);
				    }
				}
				
				world.updateSolid(playerVehicle);
				world.removeStatic(player);
				sound.stop(walkSoundId);
				sound.play("car_start");
				engineIdleSoundId = sound.play("engine_idle",true);
				input.layoutInCar();
				needsToActivateRadio = true;
				radioActivationTime = 0.0f;
				sound.pauseAmbiance();
				break;
			}
		}
		
	}
	
	public void handleExitCar()
	{
		oldCars.add(playerVehicle);
		world.addStatic(player);
	
		Vector2D midPoint = playerVehicle.getRect().midpoint(1, 2);
		player.setCenter(midPoint.x, midPoint.y);
		OBB2D.collisionResponse(playerVehicle.getRect(), player.getRect(),midPoint);
		player.setCenter(player.getCenterX() + (EXIT_CAR_DIST * midPoint.x),
				(EXIT_CAR_DIST * midPoint.y) + player.getCenterY());
		
		
		playerVehicle = null;
		sound.stop(engineIdleSoundId);
		sound.stop(carMovingSoundId);
		sound.stop(carMovingLoopSoundId);
		needsToLoopEngine = false;
		input.layoutOnFoot();
		sound.pauseRadio();
		sound.resumeAmbiance();
		needsToActivateRadio = false;
	}
	
	public void onButtonPressed(ControlButton button, int id)
	{
		if(id == ControlButton.BUTTON_STEAL_CAR)
		{
			if(!isInCar())
			{
				handleCarTheft();
			}
			else
			{
				handleExitCar();
			}
		}
		
		if(id == ControlButton.BUTTON_GAS)
		{
			if(isInCar())
			{
				sound.stop(engineIdleSoundId);
				carMovingSoundId = sound.play("car_moving");
				needsToLoopEngine = true;
				timeToChange = 0.0f;
			}
		}
		
		if(id == ControlButton.BUTTON_BRAKE && isInCar() && playerVehicle.getVelocity().length() > 50.0f)
		{
			int skid = sound.play("car_skid");
			sound.changePitch(skid, JMath.randomRange(70, 120) / 100.0f);
		}
	}

	public void onButtonReleased(ControlButton button, int id) 
	{
		if(id == ControlButton.BUTTON_GAS)
		{
			if(isInCar())
			{
				sound.stop(carMovingSoundId);
				sound.stop(carMovingLoopSoundId);
				needsToLoopEngine = false;
				engineIdleSoundId = sound.play("engine_idle",true);
			}
		}
	}

	public Player getPlayer() 
	{
		return player;
	}
	
	public void hidePlayer()
	{
		
	}
	
	public void engineUpdate(float timeStep)
	{
		timeToChange += timeStep;
		if(timeToChange >= SOUND_CHANGE_TIME && needsToLoopEngine)
		{
			needsToLoopEngine = false;
			sound.stop(carMovingSoundId);
			carMovingLoopSoundId = sound.play("car_move_loop",true);
		}
	}
	
	public void slideUpdate()
	{
		slideTrackingFrames--;
		if(slideTrackingFrames < 0)
		{
			slideTrackingFrames = 0;
		}
		
		if((float)Math.abs(playerVehicle.getAngularVelocity()) > 1.8f
				&& slideTrackingFrames == 0)
		{
			int skid = sound.play("car_skid");
			sound.changePitch(skid, JMath.randomRange(70, 120) / 100.0f);
			
			slideTrackingFrames = MAX_SLIDE_FRAMES;
		}
		
	}
	
	public void handleRadioActivation(float timeStep)
	{
		radioActivationTime += timeStep;
		if(needsToActivateRadio && radioActivationTime > RADIO_CHANGE_TIME)
		{
			needsToActivateRadio = false;
			sound.resumeRadio();
		}
	}
	
	public void handleOldCars(float timeStep)
	{
		for(int i = oldCars.size() - 1; i >= 0; i--)
		{
		    if(oldCars.get(i).getVelocity().length() > 0.0f)
		    {
		    	if(oldCars.get(i).getSpeed() > 0.1f)
				{
		    		oldCars.get(i).setBrakes(1.0f);
				}
		        oldCars.get(i).update(timeStep);
		    }
		    else
		    {
		    	world.removeSolid(oldCars.get(i));
		        oldCars.remove(i);
		    }
		}
	}
	
	public void update(float timeStep)
	{
		if(isInCar())
		{
			inCarUpdate(timeStep);
			engineUpdate(timeStep);
			slideUpdate();
		handleRadioActivation(timeStep);
		}
		else
		{
			onFootUpdate(timeStep);
		}
		
		handleOldCars(timeStep);
	}
	
	public void onFootUpdate(float timeStep)
	{
		if(input.getAnalogStick().getStickValueX() == 0.0f
				&& input.getAnalogStick().getStickValueY() == 0.0f)
		{
			if(player.getState() != Character.CHARACTER_STATE_STAND)
			{
				player.setState(Character.CHARACTER_STATE_STAND);
				sound.stop(walkSoundId);
			}
			
		}
		else 
		{
			if(player.getState() != Character.CHARACTER_STATE_WALKING)
			{
				player.setState(Character.CHARACTER_STATE_WALKING);
				walkSoundId = sound.play("walk",true);
			}
			
			player.setAngle(input.getAnalogStick().getAngle());
		}
		
		player.update(timeStep);
		camera.setPosition((player.getCenterX() *
				camera.getScale()) - ((getWidth() ) / 2.0f),
				(player.getCenterY() *
						camera.getScale()) - ((getHeight() ) / 2.0f));
	}
	
	public void inCarUpdate(float timeStep)
	{
		
		if(input.isPressed(ControlButton.BUTTON_GAS))
		{
			playerVehicle.setThrottle(1.0f, false);
		}
		
		if(input.isPressed(ControlButton.BUTTON_BRAKE))
		{
			if(playerVehicle.getSpeed() > 0.1f)
			{
				playerVehicle.setBrakes(1.0f);
			}
			else
			{
				
				playerVehicle.setThrottle(-0.5f, false);
			}
			
		}

		playerVehicle.setSteering(
				input.getAnalogStick().getStickValueX() * JMath.clamp(0.005f, 0.03f, timeStep));
		playerVehicle.update(timeStep);
		
		camera.setPosition((playerVehicle.getPosition().x *
				camera.getScale()) - ((getWidth() ) / 2.0f),
				(playerVehicle.getPosition().y *
						camera.getScale()) - ((getHeight() ) / 2.0f));
	}
	
	public boolean isInCar()
	{
		return playerVehicle != null;
	}
	
	public int getWidth()
	{
		return screenWidth;
	}
	
	public int getHeight()
	{
		return screenHeight;
	}

}
