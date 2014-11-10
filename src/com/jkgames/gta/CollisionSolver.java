package com.jkgames.gta;

import java.util.ArrayList;
import java.util.List;

public class CollisionSolver
{
	private Vector2D outNorm = new Vector2D();
	private GridWorld world;
	private InputHandler input;
	private ArrayList<CollisionListener> listeners = new ArrayList<CollisionListener>();
	private int skip;
	
	public static final float RIGID_BODY_VELOCITY_FACTOR = 0.75f;
	public static final float RIGID_BODY_ANGULAR_FACTOR = 0.94f;
	public static final float RIGID_BODY_ANGULAR_VEL_SCALAR = 0.7f;

	public CollisionSolver(GridWorld world, InputHandler input)
	{
		this.world = world;
		this.input = input;
	}
	
	public void solveRigidBody(RigidBody a, Entity e)
	{
		int trys = 0;
		while(a.getRect().overlaps(e.getRect()) && trys < 1000)
		{
			trys++;
			OBB2D.collisionResponse(a.getRect(), e.getRect(), outNorm);
			a.setCenter(a.getCenterX() + outNorm.x , a.getCenterY() + outNorm.y );
		  a.setVelocity(a.getVelocity().multiply(RIGID_BODY_VELOCITY_FACTOR));
		}
		
	    Vector2D normVel = new Vector2D();
        normVel.set(a.getVelocity());
        normVel.normalize();
        float dot = normVel.dot(outNorm);
        dot = -dot;
        Vector2D alongWall=new Vector2D(outNorm.y,-outNorm.x); //get perpendicular vector 
        float cw=alongWall.dot(normVel);
        if(cw<0)//check which direction we are coming from
            dot=-dot; //coming from the right, so reverse direction of rotation

     
        if(input.getAnalogStick().getStickValueX() == 0.0f 
        		&& Math.abs(dot) < RIGID_BODY_ANGULAR_FACTOR)
        { 	  
        	a.setAngularVelocity(a.getAngularVelocity() +
                (dot * RIGID_BODY_ANGULAR_VEL_SCALAR));
        }
        
        if(Math.abs(dot) >= RIGID_BODY_ANGULAR_FACTOR && e.getType() != Entity.TYPE_PED)
        { 	  
        	a.setVelocity(a.getVelocity().multiply(0.0f));
        }
        	
	}
	
	public void solveStaticBody(Entity a, Entity e)
	{
		int trys = 0;
		while(a.getRect().overlaps(e.getRect()) && trys < 1000)
		{
			trys++;
			OBB2D.collisionResponse(a.getRect(), e.getRect(), outNorm);
			a.setCenter(a.getCenterX() + outNorm.x , a.getCenterY() + outNorm.y );
		}
			
	}
	
	public void update(float timeStep)
	{
		skip++;
		List<Entity> ents = world.querySolid();
		for(int g = 0; g < ents.size(); ++g)
		{
			Entity a = ents.get(g);
			//lets cheat and only update peds a quarter the time!
			if(a.getType() == Entity.TYPE_PED && skip % 4 != 0)
			{
				continue;
			}
			
			List<Entity> collids = world.querySolid(a,a.getRect());
			Entity e = null; 
			if(collids.size() > 0)
			{
				for(int i = 0; i < collids.size(); ++i)
				{
					e = collids.get(i);
					
				
					
					if(a.getType() == Entity.TYPE_PLAYER || 
							a.getType() == Entity.TYPE_PED)
					{
						solveStaticBody(a, e);
						if( a.getType() == Entity.TYPE_PED)
						a.setAngle((float)Math.atan2(
								a.getRect().getMoveDeltaVec().y,
								a.getRect().getMoveDeltaVec().x ));
					}
					
					if(a.isRigidBody() && (e.getType() == Entity.TYPE_PLAYER ||
							e.getType() == Entity.TYPE_PED))
					{
						solveStaticBody(e, a);
					}	
					
					if(a.isRigidBody())
					{
						solveRigidBody((RigidBody)a, e);
					}
					
				}
				
				if(a.getLastColliders().contains(e))
				{
					for(int x = 0; x < listeners.size(); ++x)
					{
						listeners.get(x).onCollisionContact(a, e);
					}
				}
				
				for(int x = 0; x < listeners.size(); ++x)
				{
					listeners.get(x).onCollisionDetected(a, e);
				}
				
				if(skip % 4 == 0)
				{
					a.getLastColliders().clear();
					for(int i = 0; i < collids.size(); ++i)
					{
						a.getLastColliders().add(collids.get(i));
					}
				}
				
			}
			else
			{
				a.getLastColliders().clear();
			}
			
			a.getRect().invalidateDeltaVec();
			
		}
	}
	
	public void addListener(CollisionListener c)
	{
		listeners.add(c);
	}
	
	public void removeListener(CollisionListener c)
	{
		listeners.remove(c);
	}
}
