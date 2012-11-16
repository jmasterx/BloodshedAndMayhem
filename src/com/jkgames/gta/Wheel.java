package com.jkgames.gta;

import android.graphics.Matrix;

public class Wheel
{
    private Vector2D forwardVec = new Vector2D();
    private Vector2D sideVec = new Vector2D();
    private static Vector2D sideVel = new Vector2D();
    private static Vector2D forwardVel = new Vector2D();
    
    private float wheelTorque;
    private float wheelSpeed;
    private float wheelInertia;
    private float wheelRadius;
    private Vector2D position = new Vector2D();
    private static float forwardMag;
    private static Vector2D responseForce = new Vector2D();
    private static float []vecArray = new float[4];
    private static Matrix mat = new Matrix();
    

    public Wheel(Vector2D position, float radius)
    {
        this.position = position;
        setSteeringAngle(0);
        wheelSpeed = 0;
        wheelRadius = radius;
        wheelInertia = (radius * radius) * 1.1f;
    }

    public void setSteeringAngle(float newAngle)
    {
    		mat.reset();
            //forward Vector
            vecArray[0] = 0;
            vecArray[1] = 1;
            //side Vector
            vecArray[2] = -1;
            vecArray[3] = 0;

            mat.postRotate(newAngle / (float)Math.PI * 180.0f);
            mat.mapVectors(vecArray);

            forwardVec.x = vecArray[0];
            forwardVec.y = vecArray[1];
            sideVec.x = vecArray[2];
            sideVec.y = vecArray[3];
    }

    public void addTransmissionTorque(float newValue)
    {
        wheelTorque += newValue;
    }

    public float getWheelSpeed()
    {
        return wheelSpeed;
    }

    public Vector2D getAnchorPoint()
    {
        return position;
    }

    public Vector2D calculateForce(Vector2D relativeGroundSpeed, float timeStep)
    {
        //calculate speed of tire patch at ground
    	float px,py;
    	px = (-forwardVec.x * wheelSpeed) * wheelRadius;
    	py = (-forwardVec.y * wheelSpeed) * wheelRadius;
  
       
        //get velocity difference between ground and patch
    	
        float velDifferenceX = relativeGroundSpeed.x + px;
        float velDifferenceY = relativeGroundSpeed.y + py;

        //project ground speed onto side axis
      
        
       Vector2D.project(velDifferenceX,velDifferenceY,sideVec.x,sideVec.y,sideVel);
        forwardMag = Vector2D.project(velDifferenceX,velDifferenceY,
        		forwardVec.x,forwardVec.y,forwardVel);

        //calculate super fake friction forces
        //calculate response force
        responseForce.x = -sideVel.x * 2.0f;
        responseForce.y = -sideVel.y * 2.0f;
        responseForce.x -= forwardVel.x;
        responseForce.y -= forwardVel.y;

        float topSpeed = 500.0f;
        
        //calculate torque on wheel
        wheelTorque += forwardMag * wheelRadius;

        //integrate total torque into wheel
        wheelSpeed += wheelTorque / wheelInertia * timeStep;
        
        //top speed limit (kind of a hack)
        if(wheelSpeed > topSpeed)
        {
        	wheelSpeed = topSpeed;
        }

        //clear our transmission torque accumulator
        wheelTorque = 0;

        //return force acting on body
        return responseForce;
    }
    
    public void setTransmissionTorque(float newValue)
    {
    	wheelTorque = newValue;
    }
    
    public float getTransmissionTourque()
    {
    	return wheelTorque;
    }
    
    public void setWheelSpeed(float speed)
    {
    	wheelSpeed = speed;
    }
}
