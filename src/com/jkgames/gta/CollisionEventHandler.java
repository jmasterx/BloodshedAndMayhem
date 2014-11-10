package com.jkgames.gta;

public class CollisionEventHandler implements CollisionListener 
{
	private SoundManager sound;
	private ScoreManager score;
	private int collisionCount;
	private int collisionStartCount;
	private long lastTimeMs;
	
	public static final int PITCH_RANGE_START = 90;
	public static final int PITCH_RANGE_END = 110;
	public static final int MIN_SCORE_INCREASE = 10;
	public static final int MAX_SCORE_INCREASE = 15;
	public static final float MIN_VELOCITY = 100.0f;
	
	
	public CollisionEventHandler(SoundManager sound, ScoreManager scoreMan)
	{
		this.sound = sound;
		this.score = scoreMan;
	}
	
	public void onCollisionContact(Entity a, Entity b) 
	{
		long deltaTime = System.currentTimeMillis() - lastTimeMs;
		
		//wait at least 1 second
		if(deltaTime > 1000 && a.isRigidBody() && 
				(b.getType() == Entity.TYPE_DECORE ||
				b.getType() == Entity.TYPE_VEHICLE))
		{
			lastTimeMs = System.currentTimeMillis();
			int sid = sound.play("car_crash_wall");
				sound.changePitch(sid, JMath.randomRange(MIN_SCORE_INCREASE,
						MAX_SCORE_INCREASE) / 100.0f);
		}
	}

	public void onCollisionDetected(Entity a, Entity b) 
	{
		collisionCount++;
		if(collisionCount > 10000)
		{
			collisionCount = 0;
		}

		if(a.isRigidBody() && b.getType() == Entity.TYPE_PED)
		{
			RigidBody r = (RigidBody)a;
			if(r.getVelocity().length() > MIN_VELOCITY)
			{
					Pedestrian p = (Pedestrian)b;
					if(!p.isReadyToDie())
					{
						p.slowDeath();
						score.increaseScore(JMath.randomRange(
								MIN_SCORE_INCREASE, MAX_SCORE_INCREASE));
						int sid = sound.play("run_over");
						sound.changePitch(sid, 
								JMath.randomRange(
										PITCH_RANGE_START, PITCH_RANGE_END) / 100.0f);
					}
			}
		}
		
		if(a.isRigidBody() && (b.getType() == Entity.TYPE_DECORE || 
				b.getType() == Entity.TYPE_VEHICLE) && collisionCount % 3 == 0)
		{		
				int sid = sound.play("car_scrape");
				sound.changePitch(sid, 
						JMath.randomRange(PITCH_RANGE_START, PITCH_RANGE_END) / 100.0f);
		}
	}
	

}
