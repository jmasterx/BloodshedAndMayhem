package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

//our simulation object
class RigidBody extends Entity
{
  //linear
  private Vector2D velocity = new Vector2D();
  private Vector2D forces = new Vector2D();
  private float mass;
  private Vector2D v = new Vector2D();
  
  //angular
  private float angularVelocity;
  private float torque;
  private float inertia;

  //graphical
  private Vector2D halfSize = new Vector2D();
  private Bitmap image;
  

  private static float[] Vector2Ds = new float[2];
  private static Vector2D tangent = new Vector2D();
  private static Matrix mat = new Matrix();
  private static Vector2D worldRelVec = new Vector2D();
  private static Vector2D relWorldVec = new Vector2D();
  private static Vector2D pointVelVec = new Vector2D();
  private static Vector2D acceleration = new Vector2D();


  public RigidBody()
  {
      //set these defaults so we don't get divide by zeros
      mass = 1.0f;
      inertia = 1.0f;
      setLayer(LAYER_OBJECTS);
      setType(TYPE_RIGID_BODY);
  }

	protected void rectChanged()
	{
		if(getWorld() != null)
		{
			getWorld().updateDynamic(this);
		}
	}
	
  //intialize out parameters
  public void initialize(Vector2D halfSize, float mass, Bitmap bitmap)
  {
      //store physical parameters
      this.halfSize = halfSize;
      this.mass = mass;
      image = bitmap;
      inertia = (1.0f / 16.0f) * (halfSize.x * halfSize.x) *
    		  (halfSize.y * halfSize.y) * mass;

      RectF rect = new RectF();
      float scalar = 10.0f;
      rect.left = (int)-halfSize.x * scalar;
      rect.top = (int)-halfSize.y * scalar;
     
      rect.right = rect.left + (int)(halfSize.x * 2.0f * scalar);
      rect.bottom = rect.top + (int)(halfSize.y * 2.0f * scalar);
      setRect(rect);

      
  }

  public void setLocation(Vector2D position, float angle)
  {
      getRect().set(position.x,position.y, getWidth(), getHeight(), angle);
      rectChanged();
  }
  
  public Vector2D getPosition()
  {
      return getRect().getCenter();
  }
  
  @Override
  public void update(float timeStep)
  {
	  doUpdate(timeStep);
  }
  
  public void doUpdate(float timeStep)
  {
      //integrate physics
      //linear
      acceleration.x = forces.x / mass;
      acceleration.y = forces.y / mass;
	  velocity.x += (acceleration.x * timeStep);
	  velocity.y += (acceleration.y * timeStep);
    //velocity = Vector2D.add(velocity, Vector2D.scalarMultiply(acceleration, timeStep));
      Vector2D c = getRect().getCenter();
      v.x = getRect().getCenter().getX() + (velocity.x * timeStep);
      v.y = getRect().getCenter().getY() + (velocity.y * timeStep);
      setCenter(v.x, v.y);
      forces.x = 0; //clear forces
      forces.y = 0;
      
  

      //angular
      float angAcc = torque / inertia;

	  angularVelocity += angAcc * timeStep;
      setAngle(getAngle() +  angularVelocity * timeStep);  
      torque = 0; //clear torque
     
  }
  
  //take a relative Vector2D and make it a world Vector2D
  public Vector2D relativeToWorld(Vector2D relative)
  {
	  mat.reset();

      Vector2Ds[0] = relative.x;
      Vector2Ds[1] = relative.y;

      mat.postRotate(JMath.radToDeg(getAngle()));
      mat.mapVectors(Vector2Ds);

      relWorldVec.x = Vector2Ds[0];
      relWorldVec.y = Vector2Ds[1];
      return relWorldVec;
  }

  //take a world Vector2D and make it a relative Vector2D
  public Vector2D worldToRelative(Vector2D world)
  {
	  mat.reset();

      Vector2Ds[0] = world.x;
      Vector2Ds[1] = world.y;

      mat.postRotate(JMath.radToDeg(-getAngle()));
      mat.mapVectors(Vector2Ds);

      worldRelVec.x = Vector2Ds[0];
      worldRelVec.y = Vector2Ds[1];
      return worldRelVec;
  }

  //velocity of a point on body
  public Vector2D pointVelocity(Vector2D worldOffset)
  {
      tangent.x = -worldOffset.y;
      tangent.y = worldOffset.x;
      
      pointVelVec.x = (tangent.x * angularVelocity) + velocity.x;
      pointVelVec.y = (tangent.y * angularVelocity) + velocity.y;
      return pointVelVec;
  }

  public void applyForce(Vector2D worldForce, Vector2D worldOffset)
  {
      //add linear force
      forces.x += worldForce.x;
      forces.y += worldForce.y;
      //add associated torque
      torque += Vector2D.cross(worldOffset, worldForce);
  }
 
  public float getTorque()
  {
	return torque;
}

public void setTorque(float torque) {
	this.torque = torque;
}

@Override
  public void draw( GraphicsContext c)
  {
      c.drawRotatedScaledBitmap(image, getPosition().x, getPosition().y,
    		  getWidth() * 1.01f, getHeight() * 1.01f, getAngle());
  }

	public Vector2D getVelocity()
	{
		return velocity;
	}
	
	public void setVelocity(Vector2D velocity)
	{
		this.velocity = velocity;
	}
	
	public float getAngularVelocity()
	{
		return angularVelocity;
	}
	
	public void setAngularVelocity(float velocity)
	{
		this.angularVelocity = velocity;
	}
	
	public boolean isRigidBody()
	{
		return true;
	}
  
}
