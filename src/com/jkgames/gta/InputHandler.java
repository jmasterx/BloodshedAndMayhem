package com.jkgames.gta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

public class InputHandler implements IDrawable
{
	private class Pressed
	{
		private int id;
		private boolean pressed;
		
		public Pressed(int id, boolean pressed)
		{
			setId(id);
			setPressed(pressed);
		}

		public boolean isPressed() 
		{
			return pressed;
		}

		public void setPressed(boolean pressed) 
		{
			this.pressed = pressed;
		}

		public int getId()
		{
			return id;
		}

		public void setId(int id) 
		{
			this.id = id;
		}
	}
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
	private Queue<Pressed> pressed = new LinkedList<Pressed>();
	
	private int screenWidth;
	private int screenHeight;

	public InputHandler(Resources res)
	{
		Bitmap stickImg = BitmapFactory.decodeResource(res, R.drawable.analog_stick);
		Bitmap subStickImg = BitmapFactory.decodeResource(res, R.drawable.sub_analog_stick);
		
		buttons.add(new ControlButton(BitmapFactory.decodeResource(res, R.drawable.door),
				50,ControlButton.BUTTON_STEAL_CAR));
		buttons.add(new ControlButton(BitmapFactory.decodeResource(res, R.drawable.shoot),
				50,ControlButton.BUTTON_CHANGE_WEAPON));
		buttons.add(new ControlButton(BitmapFactory.decodeResource(res, R.drawable.fist),
				50,ControlButton.BUTTON_FIRE_WEAPON));
		buttons.add(new ControlButton(BitmapFactory.decodeResource(res, R.drawable.gas),
				50,ControlButton.BUTTON_GAS));
		buttons.add(new ControlButton(BitmapFactory.decodeResource(res, R.drawable.brake),
				50,ControlButton.BUTTON_BRAKE));
		
		analogStick = new AnalogStick(stickImg, subStickImg, 65);
	}

	public AnalogStick getAnalogStick()
	{
		return analogStick;
	}
	
	public ControlButton getButton(int id)
	{
		for (int i = 0; i < buttons.size(); ++i)
		{
			ControlButton button = buttons.get(i);
			if (button.getId() == id)
			{
				return button;
			}
		}
		
		return null;
	}
	
	public boolean isPressed(int id)
	{
		for (int i = 0; i < buttons.size(); ++i)
		{
			ControlButton button = buttons.get(i);
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
			this.pressed.add(new Pressed(id, pressed));
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
		layoutInCar();
	}
	
	public void layoutInCar()
	{
		float gap = screenWidth * 0.022f;
		float btnSize = screenWidth * 0.05f;
		for(int i = 0; i  < buttons.size(); ++i)
		{
			buttons.get(i).setRadius(btnSize);
			buttons.get(i).setVisible(false);
		}
		getButton(ControlButton.BUTTON_GAS).setVisible(true);
		getButton(ControlButton.BUTTON_BRAKE).setVisible(true);
		getButton(ControlButton.BUTTON_STEAL_CAR).setVisible(true);
		getButton(ControlButton.BUTTON_STEAL_CAR).setRadius(btnSize * 0.8f);
		getButton(ControlButton.BUTTON_GAS).
		setCenter(screenWidth - getButton(ControlButton.BUTTON_GAS).getWidth() - gap, 
				screenHeight - getButton(ControlButton.BUTTON_GAS).getHeight() - gap);
		
		getButton(ControlButton.BUTTON_STEAL_CAR).
		setCenter(screenWidth - (getButton(ControlButton.BUTTON_GAS).getWidth() * 1.5f) - (gap * 1.5f), 
				screenHeight - (getButton(ControlButton.BUTTON_GAS).getHeight() * 2.0f) - (gap * 2.0f));
		
		getButton(ControlButton.BUTTON_BRAKE).
		setCenter(screenWidth - 
				(getButton(ControlButton.BUTTON_BRAKE).getWidth() * 2) - gap - (gap * 0.5f), 
				screenHeight - getButton(ControlButton.BUTTON_BRAKE).getHeight() - gap);
	}
	
	public void layoutOnFoot()
	{
		float gap = screenWidth * 0.022f;
		float btnSize = screenWidth * 0.05f;
		for(int i = 0; i  < buttons.size(); ++i)
		{
			buttons.get(i).setRadius(btnSize);
			buttons.get(i).setVisible(false);
		}
		//getButton(ControlButton.BUTTON_GAS).setVisible(true);
		//getButton(ControlButton.BUTTON_BRAKE).setVisible(true);
		getButton(ControlButton.BUTTON_STEAL_CAR).setVisible(true);
		getButton(ControlButton.BUTTON_STEAL_CAR).setRadius(btnSize * 0.8f);
		getButton(ControlButton.BUTTON_GAS).
		setCenter(screenWidth - getButton(ControlButton.BUTTON_GAS).getWidth() - gap, 
				screenHeight - getButton(ControlButton.BUTTON_GAS).getHeight() - gap);
		
		getButton(ControlButton.BUTTON_STEAL_CAR).
		setCenter(screenWidth - (getButton(ControlButton.BUTTON_GAS).getWidth() * 1.5f) - (gap * 1.5f), 
				screenHeight - (getButton(ControlButton.BUTTON_GAS).getHeight() * 2.0f) - (gap * 2.0f));
		
		getButton(ControlButton.BUTTON_BRAKE).
		setCenter(screenWidth - 
				(getButton(ControlButton.BUTTON_BRAKE).getWidth() * 2) - gap - (gap * 0.5f), 
				screenHeight - getButton(ControlButton.BUTTON_BRAKE).getHeight() - gap);
	}
	
	private boolean containsInputEvent(int id)
	{
		for(int i = 0; i < inputEvents.size(); ++i)
		{
			Input in = inputEvents.get(i);
			if(in.getId() == id)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private Input getInputEvent(int id)
	{
		for(int i = 0; i < inputEvents.size(); ++i)
		{
			Input in = inputEvents.get(i);
			if(in.getId() == id)
			{
				return in;
			}
		}
		
		return null;
	}
	
	private Input getAnalogStickInput()
	{
		for(int i = 0; i < inputEvents.size(); ++i)
		{
			Input in = inputEvents.get(i);
			if(in.getStick() != null)
			{
				return in;
			}
		}
		
		return null;
	}
	
	private Input getButtonInput(ControlButton button)
	{
		for(int i = 0; i < inputEvents.size(); ++i)
		{
			Input in = inputEvents.get(i);
			if(in.getButton() == button)
			{
				return in;
			}
		}
		
		return null;
	}
	
	public ControlButton getButtonAt(float x, float y)
	{
		for(int i = 0; i < buttons.size(); ++i)
		{
			ControlButton b = buttons.get(i);
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
		for (int i = 0; i < buttons.size(); ++i)
		{
			ControlButton button = buttons.get(i);
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
	
	public void update(float timeElapsed)
	{
		while(!pressed.isEmpty())
		{
			Pressed p = pressed.poll();
			ControlButton tempButton = getButton(p.getId());
			if (p.isPressed())
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
	
}
