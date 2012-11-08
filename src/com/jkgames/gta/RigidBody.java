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
  private OBB2D predictionRect = new OBB2D(new Vector2D(), 1.0f, 1.0f, 0.0f);
  private float mass;
  private Vector2D deltaVec;

  //angular
  private float angularVelocity;
  private float torque;
  private float inertia;

  //graphical
  private Vector2D halfSize = new Vector2D();
  private Bitmap image;

  public RigidBody()
  {
      //set these defaults so we don't get divide by zeros
      mass = 1.0f;
      inertia = 1.0f;
  }

  //intialize out parameters
  public void initialize(Vector2D halfSize, float mass, Bitmap bitmap)
  {
      //store physical parameters
      this.halfSize = halfSize;
      this.mass = mass;
      image = bitmap;
      inertia = (1.0f / 20.0f) * (halfSize.x * halfSize.x) * (halfSize.y * halfSize.y) * mass;

      RectF rect = new RectF();
      float scalar = 10.0f;
      rect.left = (int)-halfSize.x * scalar;
      rect.top = (int)-halfSize.y * scalar;
     
      rect.right = rect.left + (int)(halfSize.x * 2.0f * scalar);
      rect.bottom = rect.top + (int)(halfSize.y * 2.0f * scalar);
      setRect(rect);
      predictionRect.set(rect);
      
  }

  public void setLocation(Vector2D position, float angle)
  {
      getRect().set(position, getWidth(), getHeight(), angle);
  }
  
  public void setPredictionLocation(Vector2D position, float angle)
  {
      getPredictionRect().set(position, getWidth(), getHeight(), angle);
  }
  
  public void setPredictionCenter(Vector2D center)
  {
      getPredictionRect().moveTo(center);
  }
  
  public void setPredictionAngle(float angle)
  {
	  predictionRect.setAngle(angle);
  }

  public Vector2D getPosition()
  {
      return getRect().getCenter();
  }
  
  public OBB2D getPredictionRect()
  {
	  return predictionRect;
  }

  @Override
  public void update(float timeStep)
  {
	  doUpdate(false,timeStep);
  }
  
  public void doUpdate(boolean prediction, float timeStep)
  {
      //integrate physics
      //linear
      Vector2D acceleration = Vector2D.scalarDivide(forces, mass);
      if(prediction)
      {
    	   Vector2D velocity = Vector2D.add(this.velocity, Vector2D.scalarMultiply(acceleration, timeStep));
    	      Vector2D c = getRect().getCenter();
    	      c = Vector2D.add(getRect().getCenter(), Vector2D.scalarMultiply(velocity , timeStep));
    	      setPredictionCenter(c);
    	      //forces = new Vector2D(0,0); //clear forces
      }
      else
      {
    	    velocity = Vector2D.add(velocity, Vector2D.scalarMultiply(acceleration, timeStep));
    	      Vector2D c = getRect().getCenter();
    	      Vector2D v = Vector2D.add(getRect().getCenter(), Vector2D.scalarMultiply(velocity , timeStep));
    	      deltaVec = v.subtract(c);
    	      deltaVec.normalize();
    	      setCenter(v.x, v.y);
    	      forces = new Vector2D(0,0); //clear forces
      }
  

      //angular
      float angAcc = torque / inertia;
      if(prediction)
      {
    	 float angularVelocity = this.angularVelocity + angAcc * timeStep;
         setPredictionAngle(getAngle() +  angularVelocity * timeStep);  
         //torque = 0; //clear torque
      }
      else
      {
    	  angularVelocity += angAcc * timeStep;
          setAngle(getAngle() +  angularVelocity * timeStep);  
          torque = 0; //clear torque
      }
     
   
  }
  
  public void updatePrediction(float timeStep)
  {
	  doUpdate(true, timeStep);
  }

  //take a relative Vector2D and make it a world Vector2D
  public Vector2D relativeToWorld(Vector2D relative)
  {
      Matrix mat = new Matrix();
      float[] Vector2Ds = new float[2];

      Vector2Ds[0] = relative.x;
      Vector2Ds[1] = relative.y;

      mat.postRotate(JMath.radToDeg(getAngle()));
      mat.mapVectors(Vector2Ds);

      return new Vector2D(Vector2Ds[0], Vector2Ds[1]);
  }

  //take a world Vector2D and make it a relative Vector2D
  public Vector2D worldToRelative(Vector2D world)
  {
      Matrix mat = new Matrix();
      float[] Vectors = new float[2];

      Vectors[0] = world.x;
      Vectors[1] = world.y;

      mat.postRotate(JMath.radToDeg(-getAngle()));
      mat.mapVectors(Vectors);

      return new Vector2D(Vectors[0], Vectors[1]);
  }

  //velocity of a point on body
  public Vector2D pointVelocity(Vector2D worldOffset)
  {
      Vector2D tangent = new Vector2D(-worldOffset.y, worldOffset.x);
      return Vector2D.add( Vector2D.scalarMultiply(tangent, angularVelocity) , velocity);
  }

  public void applyForce(Vector2D worldForce, Vector2D worldOffset)
  {
      //add linear force
      forces = Vector2D.add(forces ,worldForce);
      //add associated torque
      torque += Vector2D.cross(worldOffset, worldForce);
  }
 
  @Override
  public void draw( GraphicsContext c)
  {
      c.drawRotatedScaledBitmap(image, getPosition().x, getPosition().y,
    		  getWidth(), getHeight(), getAngle());
  }

	public Vector2D getVelocity()
	{
		return velocity;
	}
	
	public void setVelocity(Vector2D velocity)
	{
		this.velocity = velocity;
	}
	
	public Vector2D getDeltaVec()
	{
		return deltaVec;
	}
  
}
