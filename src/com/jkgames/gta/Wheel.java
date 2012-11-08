package com.jkgames.gta;

import android.graphics.Matrix;

public class Wheel
{
    private Vector2D forwardVec;
    private Vector2D sideVec;
    private float wheelTorque;
    private float wheelSpeed;
    private float wheelInertia;
    private float wheelRadius;
    private Vector2D position = new Vector2D();

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
            Matrix mat = new Matrix();

            float []vecArray = new float[4];
            //forward Vector
            vecArray[0] = 0;
            vecArray[1] = 1;
            //side Vector
            vecArray[2] = -1;
            vecArray[3] = 0;

            mat.postRotate(newAngle / (float)Math.PI * 180.0f);
            mat.mapVectors(vecArray);

            forwardVec = new Vector2D(vecArray[0], vecArray[1]);
            sideVec = new Vector2D(vecArray[2], vecArray[3]);
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

    public Vector2D calculateForce(Vector2D relativeGroundSpeed, float timeStep, boolean prediction)
    {
        //calculate speed of tire patch at ground
        Vector2D patchSpeed = Vector2D.scalarMultiply(Vector2D.scalarMultiply(
        		Vector2D.negative(forwardVec), wheelSpeed), wheelRadius);
       
        //get velocity difference between ground and patch
        Vector2D velDifference = Vector2D.add(relativeGroundSpeed , patchSpeed);

        //project ground speed onto side axis
        Float forwardMag = new Float(0.0f);
        Vector2D sideVel = velDifference.project(sideVec);
        Vector2D forwardVel = velDifference.project(forwardVec, forwardMag);

        //calculate super fake friction forces
        //calculate response force
        Vector2D responseForce = Vector2D.scalarMultiply(Vector2D.negative(sideVel), 2.0f);
        responseForce =  Vector2D.subtract(responseForce, forwardVel);

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
