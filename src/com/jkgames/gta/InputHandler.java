package com.jkgames.gta;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

public class InputHandler implements IDrawable
{
	private class Input
	{
		private int id;
		private ControlButton button;
		private AnalogStick stick;
		private float startX;
		private float startY;
		private float curX;
		private float curY;
		
		public Input(int id, ControlButton button, AnalogStick stick,float startX, float startY)
		{
			this.id = id;
			this.button = button;
			this.stick = stick;
			this.startX = startX;
			this.startY = startY;
		}
		
		public float getStartX() 
		{
			return startX;
		}
		public float getStartY()
		{
			return startY;
		}

		public int getId() 
		{
			return id;
		}
		public ControlButton getButton() 
		{
			return button;
		}
		
		public AnalogStick getStick() 
		{
			return stick;
		}

		public float getCurX() 
		{
			return curX;
		}

		public void setCurX(float curX) 
		{
			this.curX = curX;
		}

		public float getCurY()
		{
			return curY;
		}

		public void setCurY(float curY)
		{
			this.curY = curY;
		}
		
		public float getDeltaX()
		{
			return getCurX() - getStartX();
		}
		
		public float getDeltaY()
		{
			return getCurY() - getStartY();
		}
	}
	
	private ArrayList<ControlButton> buttons = new ArrayList<ControlButton>();
	private ArrayList<Input> inputEvents = new ArrayList<Input>();
	private AnalogStick analogStick;		
	private List<InputListener> inputListeners = new ArrayList<InputListener>();
	
	private int screenWidth;
	private int screenHeight;

	public InputHandler(Resources res)
	{
		Bitmap tempButton = BitmapFactory.decodeResource(res, R.drawable.temp_button);
		Bitmap stickImg = BitmapFactory.decodeResource(res, R.drawable.analog_stick);
		Bitmap subStickImg = BitmapFactory.decodeResource(res, R.drawable.sub_analog_stick);
		
		buttons.add(new ControlButton(tempButton,50,ControlButton.BUTTON_STEAL_CAR));
		buttons.add(new ControlButton(tempButton,50,ControlButton.BUTTON_CHANGE_WEAPON));
		buttons.add(new ControlButton(tempButton,50,ControlButton.BUTTON_FIRE_WEAPON));
		buttons.add(new ControlButton(tempButton,50,ControlButton.BUTTON_GAS));
		buttons.add(new ControlButton(tempButton,50,ControlButton.BUTTON_BRAKE));
		
		analogStick = new AnalogStick(stickImg, subStickImg, 65);
	}

	public AnalogStick getAnalogStick()
	{
		return analogStick;
	}
	
	public ControlButton getButton(int id)
	{
		for (ControlButton button : buttons)
		{
			if (button.getId() == id)
			{
				return button;
			}
		}
		
		return null;
	}
	
	public boolean isPressed(int id)
	{
		for (ControlButton button : buttons)
		{
			if (button.getId() == id)
			{
				if (button.isPressed())
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void setPressed(int id , boolean pressed)
	{
		ControlButton tempButton = getButton(id);
		
		if (tempButton == null)
		{
			return;
		}
		
		if (tempButton.isPressed() != pressed)
		{
			tempButton.setPressed(pressed);
			
			if (pressed)
			{
				for (InputListener listener : inputListeners)
				{
					listener.onButtonPressed(tempButton, tempButton.getId()); 
				}
			}
			else
			{
				for (InputListener listener : inputListeners)
				{
					listener.onButtonReleased(tempButton, tempButton.getId()); 
				}
			}
		}
	}
	
	public void addInputListener(InputListener listener)
	{
		inputListeners.add(listener);
	}
	
	public void removeInputListener(InputListener listener)
	{
		inputListeners.remove(listener);
	}
	
	public void onResize(int w, int h)
	{
		screenWidth = w;
		screenHeight = h;
		
		for(int i = 0; i < buttons.size(); ++i)
		{
			buttons.get(i).setCenter(w * 0.85f,20 + i * 120);
		}
	}
	
	private boolean containsInputEvent(int id)
	{
		for(Input i : inputEvents)
		{
			if(i.getId() == id)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private Input getInputEvent(int id)
	{
		for(Input i : inputEvents)
		{
			if(i.getId() == id)
			{
				return i;
			}
		}
		
		return null;
	}
	
	private Input getAnalogStickInput()
	{
		for(Input i : inputEvents)
		{
			if(i.getStick() != null)
			{
				return i;
			}
		}
		
		return null;
	}
	
	private Input getButtonInput(ControlButton button)
	{
		for(Input i : inputEvents)
		{
			if(i.getButton() == button)
			{
				return i;
			}
		}
		
		return null;
	}
	
	public ControlButton getButtonAt(float x, float y)
	{
		for(ControlButton b : buttons)
		{
			if(b.pointInside(x, y))
			{
				return b;
			}
		}
		
		return null;
	}
	
	private void createInput(int id, float x, float y)
	{
		if(!containsInputEvent(id))
		{
			if(getAnalogStickInput() == null  && x < (getScreenWidth() * 0.25f))
			{
				inputEvents.add(new Input(id,null,analogStick,x,y));
				analogStick.setCenter(x, y);
				if(x < analogStick.getRadius())
				{
					analogStick.setCenter(analogStick.getRadius(), y);
				}
			}
			else
			{
				ControlButton b = getButtonAt(x, y);
				if(b != null && getButtonInput(b) == null)
				{
					inputEvents.add(new Input(id,b,null,x,y));
					setPressed(b.getId(), true);
				}
			}
		}
	
	}
	
	private void destroyInput(int id, float x, float y)
	{
		Input evt = getInputEvent(id);
		if(evt != null)
		{
			if(evt.getStick() != null)
			{
				evt.getStick().clearStickValue();
			}
			else if(evt.getButton() != null)
			{
				setPressed(evt.getButton().getId(), false);
			}
			
			inputEvents.remove(evt);
		}
	}
	
	private void inputMove(int id, float x, float y)
	{
		Input evt = getInputEvent(id);
		if(evt != null)
		{
			evt.setCurX(x);
			evt.setCurY(y);
			
			if(evt.getStick() != null)
			{
				evt.getStick().calcStickVector(x, y);
			}
		}
	}
	
	private void onTouch(int finger, int action, float x, float y)
	{
		if(action == MotionEvent.ACTION_DOWN || (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN)
		{
			createInput(finger, x, y);
		}
		else if(action == MotionEvent.ACTION_MOVE)
		{
			inputMove(finger, x, y);
		}
		else if(action == MotionEvent.ACTION_UP  || (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP)
		{
			destroyInput(finger, x, y);
		}
	}
	
	public void onTouch(MotionEvent ev)
	 {
	     final int pointerCount = ev.getPointerCount();
	     if(ev.getAction() == MotionEvent.ACTION_MOVE)
	     {
	    	  for (int p = 0; p < pointerCount; p++)
	    	  {
	    		  onTouch(ev.getPointerId(p), ev.getAction(), ev.getX(p), ev.getY(p));
	    	  }
	     }
	     else
	     {
	    	    final int p = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
	                    >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

	    	 onTouch(ev.getPointerId(p), ev.getAction(), ev.getX(p), ev.getY(p));
	     }
	    
	 }


	public void draw(GraphicsContext c) 
	{
		for (ControlButton button : buttons)
		{
			if (button.isVisible())
			{
				button.draw(c);
			}
		}
		
		if (analogStick.isVisible())
		{
			analogStick.draw(c);
		}
	}

	public int getScreenWidth() 
	{
		return screenWidth;
	}

	public int getScreenHeight()
	{
		return screenHeight;
	}
	
}
