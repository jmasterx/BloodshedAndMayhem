package com.jkgames.gta;

import android.R.anim;
import android.graphics.Bitmap;
import android.graphics.Rect;

public class Character extends Entity
{
	private TileAnimation animation = null;
	private Bitmap animationImg;
	private Bitmap bloodImg;
	private float velocity;
	private Vector2D direction = new Vector2D();
	private int state = -1;
	private boolean readyToDie;
	private int deathFrame;
	private float bloodMultiplier = 0.0f;
	
	private static final int DEATH_FRAMES = 16;
	private static final float MAX_BLOOD_MULTIPLIER = 2.0f;
	private static final float BLEED_RATE = 0.01f;
	
	public static final int CHARACTER_STATE_STAND = 0;
	public static final int CHARACTER_STATE_WALKING = 1;
	public static final int CHARACTER_STATE_DEAD = 2;
	
	public static final float CHARACTER_SIZE = 25.0f;
	public static final float CHARACTER_WALK_SPEED = 0.75f;
	
	public Character(Bitmap animation, Bitmap blood)
	{
		this.animationImg = animation;
		this.bloodImg = blood;
		this.animation = new TileAnimation(animationImg.getWidth(),
				animationImg.getHeight(), 
				64, 64, 2, 10,8);
		setLayer(LAYER_PEDESTRIAN);
		setAngle(0.0f);
		setSize(CHARACTER_SIZE, CHARACTER_SIZE);
		setVelocity(CHARACTER_WALK_SPEED);
		setState(CHARACTER_STATE_WALKING);
		
		
	}
	
	public void update(float timeStep)
	{
		animation.update();
		
		if(state == CHARACTER_STATE_WALKING)
		{
			setCenter(getCenterX() + (getVelocity() * getDirection().x),
					getCenterY() + (getVelocity() * getDirection().y));
		}
		
		if(state == CHARACTER_STATE_DEAD)
		{
			if(bloodMultiplier < MAX_BLOOD_MULTIPLIER)
			{
				bloodMultiplier += BLEED_RATE;
			}
		}
		
		if(readyToDie)
		{
			deathFrame++;
			if(deathFrame == DEATH_FRAMES / 2)
			{
				setSolid(false);
			}
			if(deathFrame == DEATH_FRAMES)
			{
				setState(CHARACTER_STATE_DEAD);
				deathFrame = 0;
				readyToDie = false;
			}
		}
	}
	
	public boolean isReadyToDie()
	{
		return readyToDie;
	}

	public void draw(GraphicsContext c)
	{
		Rect rc = animation.getCoordRect();
		
		c.drawRotatedScaledBitmap(animationImg, getCenterX(), getCenterY(),
				getWidth() * 2.0f, getHeight() * 2.0f, 
				rc.left, rc.top, rc.width(), rc.height(), getAngle());
		
		if(getState() == CHARACTER_STATE_DEAD)
		{
			c.drawRotatedScaledBitmap(bloodImg,
					getCenterX(),getCenterY(),getWidth() * bloodMultiplier,
					getHeight() * bloodMultiplier,getAngle());
		}
	}
	
	public void setState(int state)
	{
		if(this.state != state)
		{
			this.state = state;
			animation.stop();
			switch(state)
			{
			case CHARACTER_STATE_STAND:
				animation.computeFrame(1);
				break;
			case CHARACTER_STATE_DEAD:
				animation.computeFrame(0);
				bloodMultiplier = 0.0f;
				setLayer(LAYER_DEAD_PEDESTRIAN);
				break;
			case CHARACTER_STATE_WALKING:
				animation.start();
				break;
			}
			
			rectChanged();
		}
		
	
	}

	public float getVelocity() 
	{
		return velocity;
	}

	public void setVelocity(float velocity) 
	{
		this.velocity = velocity;
	}
	
	public void setAngle(float theta)
	{
		theta += Math.PI / 2;
	    float x = (float)Math.cos(theta);
	    float y = (float)Math.sin(theta);
		super.setAngle((float)Math.atan2(y, x) - (float)(Math.PI / 2));
	    
	    setDirection(x, y);
	}

	public Vector2D getDirection() 
	{
		return direction;
	}

	public void setDirection(Vector2D direction) 
	{
		this.direction.set(direction);
	}
	
	public void setDirection(float x, float y) 
	{
		this.direction.x = x;
		this.direction.y = y;
	}

	public int getState() 
	{
		return state;
	}
	
	public void slowDeath()
	{
		readyToDie = true;
	}
	
	public boolean isDead()
	{
		return getState() == CHARACTER_STATE_DEAD;
	}
	
	public void reset()
	{
		setState(CHARACTER_STATE_WALKING);
		readyToDie = false;
		deathFrame = 0;
		bloodMultiplier = 0.0f;
		setLayer(LAYER_PEDESTRIAN);
		setSolid(true);
		
	}
}
