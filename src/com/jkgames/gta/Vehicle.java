package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;

//our vehicle object
public class Vehicle extends RigidBody
{
    
    private Wheel [] wheels = new Wheel[4];
    private boolean throttled = false;
    
    private static Vector2D worldWheelOffset = new Vector2D();
    private static Vector2D worldGroundVel = new Vector2D();
    private static Vector2D relativeGroundSpeed = new Vector2D();
    private static Vector2D relativeResponseForce = new Vector2D();
    private static Vector2D worldResponseForce = new Vector2D();
    
    

    public void initialize(Vector2D halfSize, float mass, Bitmap bitmap)
    {
        //front wheels
        wheels[0] = new Wheel(new Vector2D(halfSize.x, halfSize.y), 0.45f);
        wheels[1] = new Wheel(new Vector2D(-halfSize.x, halfSize.y), 0.45f);

        //rear wheels
        wheels[2] = new Wheel(new Vector2D(halfSize.x, -halfSize.y), 0.75f);
        wheels[3] = new Wheel(new Vector2D(-halfSize.x, -halfSize.y), 0.75f);

        super.initialize(halfSize, mass, bitmap);
    }

    public void setSteering(float steering)
    {
        float steeringLock = 0.13f;

        //apply steering angle to front wheels
        wheels[0].setSteeringAngle(steering * steeringLock);
        wheels[1].setSteeringAngle(steering * steeringLock);
        
    }

    public void setThrottle(float throttle, boolean allWheel)
    {
        float torque = 85.0f;
        throttled = true;

        //apply transmission torque to back wheels
        if (allWheel)
        {
            wheels[0].addTransmissionTorque(throttle * torque);
            wheels[1].addTransmissionTorque(throttle * torque);
        }

        wheels[2].addTransmissionTorque(throttle * torque);
        wheels[3].addTransmissionTorque(throttle * torque);
    }


    public void setBrakes(float brakes)
    {
        float brakeTorque = 15.0f;

        //apply brake torque opposing wheel vel
        for (Wheel wheel : wheels)
        {
            float wheelVel = wheel.getWheelSpeed();
            wheel.addTransmissionTorque(-wheelVel * brakeTorque * brakes);
        }
    }

    public void doVehicleUpdate(float timeStep)
    {
        for (Wheel wheel : wheels)
        {
        	float wheelVel = wheel.getWheelSpeed();
         
        	//apply negative force to naturally slow down car
        	if(!throttled)
        	wheel.addTransmissionTorque(-wheelVel * 0.13f);
            
            worldWheelOffset.equals(relativeToWorld(wheel.getAnchorPoint()));
            worldGroundVel.equals(pointVelocity(worldWheelOffset));
            relativeGroundSpeed.equals(worldToRelative(worldGroundVel));
            relativeResponseForce.equals(wheel.calculateForce(relativeGroundSpeed, timeStep));
            worldResponseForce.equals(relativeToWorld(relativeResponseForce));

            applyForce(worldResponseForce, worldWheelOffset);
        }
        
        //no throttling yet this frame
        throttled = false;

     
        super.update(timeStep);
        
    }
    
    @Override
    public void update(float timeStep)
    {
    	doVehicleUpdate(timeStep);
    }
    
    public float getSpeed()
    {
    	return wheels[0].getWheelSpeed();
    }
    
}
