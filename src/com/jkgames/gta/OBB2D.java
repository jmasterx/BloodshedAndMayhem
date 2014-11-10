package com.jkgames.gta;

import java.net.NoRouteToHostException;
import java.util.ArrayList;

import android.graphics.RectF;

public class OBB2D
{
   private Vector2D projVec = new Vector2D();
   private static Vector2D rotationVec = new Vector2D();
   private static Vector2D projAVec = new Vector2D();
   private static Vector2D projBVec = new Vector2D();
   private static Vector2D tempNormal = new Vector2D();
   private Vector2D deltaVec = new Vector2D();
   private static Vector2D midpoint = new Vector2D();
   private ArrayList<Vector2D> collisionPoints = new ArrayList<Vector2D>();


// Corners of the box, where 0 is the lower left.
   private  Vector2D corner[] = new Vector2D[4];
   
   private Vector2D center = new Vector2D();
   private Vector2D extents = new Vector2D();
   
   private RectF boundingRect = new RectF();
   private float angle;

    //Two edges of the box extended away from corner[0]. 
   private  Vector2D axis[] = new Vector2D[2];

   private double origin[] = new double[2];

   public OBB2D(float centerx, float centery, float w, float h, float angle)
   	{
	   for(int i = 0; i < corner.length; ++i)
	   {
		   corner[i] = new Vector2D();
	   }
	   for(int i = 0; i < axis.length; ++i)
	   {
		   axis[i] = new Vector2D();
	   }
	   set(centerx,centery,w,h,angle);
    }
   
   public OBB2D(float left, float top, float width, float height)
  {
	   for(int i = 0; i < corner.length; ++i)
	   {
		   corner[i] = new Vector2D();
	   }
	   for(int i = 0; i < axis.length; ++i)
	   {
		   axis[i] = new Vector2D();
	   }
	   set(left + (width / 2), top + (height / 2),width,height,0.0f);
   }
   
   public void set(float left, float top, float width, float height)
   {
	   set(left + (width / 2), top + (height / 2),width,height,0.0f);
   }
   
   public void set(float centerx,float centery,float w, float h,float angle)
   {
	   float vxx = (float)Math.cos(angle);
	   float vxy = (float)Math.sin(angle);
	   float vyx = (float)-Math.sin(angle);
	   float vyy = (float)Math.cos(angle);

	       vxx *= w / 2;
	       vxy *= (w / 2);
	       vyx *= (h / 2);
	       vyy *= (h / 2);

	       corner[0].x = centerx - vxx - vyx;
	       corner[0].y = centery - vxy - vyy;
	       corner[1].x = centerx + vxx - vyx;
	       corner[1].y = centery + vxy - vyy;
	       corner[2].x = centerx + vxx + vyx;
	       corner[2].y = centery + vxy + vyy;
	       corner[3].x = centerx - vxx + vyx;
	       corner[3].y = centery - vxy + vyy;

	       this.center.x = centerx;
	       this.center.y = centery;
	       this.angle = angle;
	       computeAxes();
	       extents.x = w / 2;
	       extents.y = h / 2;

	       computeBoundingRect();
   }
   

   //Updates the axes after the corners move.  Assumes the
   //corners actually form a rectangle.
   private void computeAxes()
   {
       axis[0].x = corner[1].x - corner[0].x;
       axis[0].y = corner[1].y - corner[0].y;
       axis[1].x = corner[3].x - corner[0].x;
       axis[1].y = corner[3].y - corner[0].y;
       

       // Make the length of each axis 1/edge length so we know any
       // dot product must be less than 1 to fall within the edge.

       for (int a = 0; a < axis.length; ++a) 
       {
       	float l = axis[a].length();
       	float ll = l * l;
       	axis[a].x = axis[a].x / ll;
       	axis[a].y = axis[a].y / ll;
           origin[a] = corner[0].dot(axis[a]);
       }
   }
   

   
   public void computeBoundingRect()
   {
	   boundingRect.left = JMath.min(JMath.min(corner[0].x, corner[3].x), JMath.min(corner[1].x, corner[2].x));
       boundingRect.top = JMath.min(JMath.min(corner[0].y, corner[1].y),JMath.min(corner[2].y, corner[3].y));
       boundingRect.right = JMath.max(JMath.max(corner[1].x, corner[2].x), JMath.max(corner[0].x, corner[3].x));
       boundingRect.bottom = JMath.max(JMath.max(corner[2].y, corner[3].y),JMath.max(corner[0].y, corner[1].y)); 
   }
   
   public void set(RectF rect)
   {
	   set(rect.centerX(),rect.centerY(),rect.width(),rect.height(),0.0f);
   }
   
    // Returns true if other overlaps one dimension of this.
    private boolean overlaps1Way(OBB2D other)
    {
        for (int a = 0; a < axis.length; ++a) {

            double t = other.corner[0].dot(axis[a]);

            // Find the extent of box 2 on axis a
            double tMin = t;
            double tMax = t;

            for (int c = 1; c < corner.length; ++c) {
                t = other.corner[c].dot(axis[a]);

                if (t < tMin) {
                    tMin = t;
                } else if (t > tMax) {
                    tMax = t;
                }
            }

            // We have to subtract off the origin

            // See if [tMin, tMax] intersects [0, 1]
            if ((tMin > 1 + origin[a]) || (tMax < origin[a])) {
                // There was no intersection along this dimension;
                // the boxes cannot possibly overlap.
                return false;
            }
        }

        // There was no dimension along which there is no intersection.
        // Therefore the boxes overlap.
        return true;
    }



    public void moveTo(float centerx, float centery) 
    {
    	float cx,cy;
    	
    	cx = center.x;
    	cy = center.y;

        deltaVec.x = centerx - cx;
    	deltaVec.y  = centery - cy;
    	

        for (int c = 0; c < 4; ++c)
        {
            corner[c].x += deltaVec.x;
            corner[c].y += deltaVec.y;
        }
        
        boundingRect.left += deltaVec.x;
        boundingRect.top += deltaVec.y;
        boundingRect.right += deltaVec.x;
        boundingRect.bottom += deltaVec.y;
        

        this.center.x = centerx;
        this.center.y = centery;
        computeAxes();
    }

    // Returns true if the intersection of the boxes is non-empty.
    public boolean overlaps(OBB2D other)
    {
		if(right() < other.left())
		{
			return false;
		}
		
		if(left() > other.right())
		{
			return false;
		}
		
		if(bottom() < other.top())
		{
			return false;
		}
		
		
		if(top() > other.bottom())
		{
			return false;
		}
		
		
    	if(other.getAngle() == 0.0f && getAngle() == 0.0f)
    	{
    		return true;
    	}
    	
        return overlaps1Way(other) && other.overlaps1Way(this);
    }
    
    public Vector2D getCenter()
    {
    	return center;
    }
    
    public float getWidth()
    {
    	return extents.x * 2;
    }
    
    public float getHeight() 
    {
    	return extents.y * 2;
    }
    
    public void setAngle(float angle)
    {
    	set(center.x,center.y,getWidth(),getHeight(),angle);
    }
    
    public float getAngle()
    {
    	return angle;
    }
    
    public void setSize(float w,float h)
    {
    	set(center.x,center.y,w,h,angle);
    }
    
    public float left()
    {
    	return boundingRect.left;
    }
    
    public float right()
    {
    	return boundingRect.right;
    }
    
    public float bottom()
    {
    	return boundingRect.bottom;
    }
    
    public float top()
    {
    	return boundingRect.top;
    }
    
    public RectF getBoundingRect()
    {
    	return boundingRect;
    }
    
    public boolean overlaps(float left, float top, float right, float bottom)
    {
    	if(right() < left)
		{
			return false;
		}
		
		if(bottom() < top)
		{
			return false;
		}
		
		if(left() > right)
		{
			return false;
		}
		
		if(top() > bottom)
		{
			return false;
		}
		
		return true;
    }
    
    public static float distance(float ax, float ay,float bx, float by)
    {
      if (ax < bx)
        return bx - ay;
      else
        return ax - by;
    }
    

    public Vector2D project(float ax, float ay)
    {
    	projVec.x = Float.MAX_VALUE;
    	projVec.y = Float.MIN_VALUE;
    	
      for (int i = 0; i < corner.length; ++i)
      {
        float dot = Vector2D.dot(corner[i].x,corner[i].y,ax,ay);

        projVec.x = JMath.min(dot, projVec.x);
        projVec.y = JMath.max(dot, projVec.y);
      }

      return projVec;
    }
    
    public Vector2D getCorner(int c)
    {
    	return corner[c];
    }
    
    public int getNumCorners()
    {
    	return corner.length;
    }
    
    public static float collisionResponse(OBB2D a, OBB2D b,  Vector2D outNormal) 
    {
    	
        float depth = Float.MAX_VALUE;

        
        for (int i = 0; i < a.getNumCorners() + b.getNumCorners(); ++i)
        {
        	Vector2D edgeA;
        	Vector2D edgeB;
        	if(i >= a.getNumCorners())
        	{
        		edgeA = b.getCorner((i + b.getNumCorners() - 1) % b.getNumCorners());
        		edgeB = b.getCorner(i % b.getNumCorners());
        	}
        	else
        	{
        		edgeA = a.getCorner((i + a.getNumCorners() - 1) % a.getNumCorners());
        		edgeB = a.getCorner(i % a.getNumCorners());
        	}
        
       	     tempNormal.x = edgeB.x -edgeA.x;
		     tempNormal.y = edgeB.y - edgeA.y; 
        		
     
          tempNormal.normalize();

          
          projAVec.set(a.project(tempNormal.x,tempNormal.y));
          projBVec.set(b.project(tempNormal.x,tempNormal.y));

          float distance = OBB2D.distance(projAVec.x, projAVec.y,projBVec.x,projBVec.y);

          if (distance > 0.0f)
          {
            return 0.0f;
          }
          else
          {
            float d = Math.abs(distance);

            if (d < depth)
            {
              depth = d;
              outNormal.set(tempNormal);
            }
          }
        }
        
        float dx,dy;
        dx = b.getCenter().x - a.getCenter().x;
        dy = b.getCenter().y - a.getCenter().y;
        float dot = Vector2D.dot(dx,dy,outNormal.x,outNormal.y);
        if(dot > 0)
        {
        	outNormal.x = -outNormal.x;
        	outNormal.y = -outNormal.y;
        }

        return depth;
    }
    
    public Vector2D getMoveDeltaVec()
    {
    	return deltaVec;
    }
    
    public static Vector2D rotateAboutPoint(float x,float y,float centerX,float centerY,float theta)
    {
        float radius = (float) (Math.pow(Math.sqrt(centerX-x),2) + Math.pow(centerY-y, 2)); 
        float currentAngle = (float) Math.atan2(y-centerY, x-centerX);
        float newAngle = currentAngle + theta;
        float newX = (float) (centerX + radius*Math.cos(newAngle));
        float newY = (float) (centerY + radius*Math.sin(newAngle));
        rotationVec.x = newX;
        rotationVec.y = newY;
         return rotationVec;
     }

    public boolean pointInside(float x, float y)
    {
    	 Vector2D v = rotateAboutPoint(x,y,center.x, center.y, -angle);
    	 float newX = v.x;
    	 float newY = v.y;
	    return (
	        newY  > center.y - (getHeight() / 2) &&
	        newY  < center.y + (getHeight() / 2) &&
	        newX  > center.x - (getWidth() / 2) &&
	        newX  < center.x + (getWidth() / 2)
	    );
    }
    
    public boolean pointInside(Vector2D v)
    {
    	return pointInside(v.x,v.y);
    }
    
    public ArrayList<Vector2D> getCollsionPoints(OBB2D b)
    {
    	
    	collisionPoints.clear();
    	for(int i = 0; i < corner.length; ++i)
    	{
    		if(b.pointInside(corner[i]))
    		{
    			collisionPoints.add(corner[i]);
    		}
    	}
    	
    	for(int i = 0; i < b.corner.length; ++i)
    	{
    		if(pointInside(b.corner[i]))
    		{
    			collisionPoints.add(b.corner[i]);
    		}
    	}
    	return collisionPoints;
    }
    
    public void invalidateDeltaVec()
    {
    	deltaVec.x = 0.0f;
    	deltaVec.y = 0.0f;
    }
    
    public Vector2D midpoint(int cornerA, int cornerB)
    {
    	midpoint.x = (corner[cornerA].x + corner[cornerB].x) / 2.0f;
    	midpoint.y = (corner[cornerB].y + corner[cornerB].y) / 2.0f;
    	return midpoint;
    }
};
 
