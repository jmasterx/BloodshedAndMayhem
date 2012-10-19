package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;

//our simulation object
class RigidBody extends Entity
{
  //linear
  private Vector2D position = new Vector2D();
  private Vector2D velocity = new Vector2D();
  private Vector2D forces = new Vector2D();
  private Bitmap image;
  private float mass;

  //angular
  private float angularVelocity;
  private float torque;
  private float inertia;

  //graphical
  private Vector2D halfSize = new Vector2D();

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

      rect.left = (int)-halfSize.x;
      rect.top = (int)-halfSize.y;
     
      rect.right = rect.left + (int)(halfSize.x * 2.0f);
      rect.bottom = rect.top + (int)(halfSize.y * 2.0f);
  }

  public void setLocation(Vector2D position, float angle)
  {
      this.position = position;
      setAngle(angle);
  }

  public Vector2D getPosition()
  {
      return position;
  }

  @Override
  public void update(float timeStep)
  {
      //integrate physics
      //linear
      Vector2D acceleration = Vector2D.scalarDivide(forces, mass);
      velocity = Vector2D.add(velocity, Vector2D.scalarMultiply(acceleration, timeStep));
      position = Vector2D.add(position, Vector2D.scalarMultiply(velocity , timeStep));
      forces = new Vector2D(0,0); //clear forces

      //angular
      float angAcc = torque / inertia;
      angularVelocity += angAcc * timeStep;
      setAngle(getAngle() +  angularVelocity * timeStep);
      torque = 0; //clear torque
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
    		  halfSize.x * 20, halfSize.y * 20, getAngle());
  }

}
