package com.jkgames.gta;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class InputHandler implements IDrawable
{
	private ArrayList<ControlButton> buttons = new ArrayList<ControlButton>();
	private AnalogStick analogStick;		
	private List<InputListener> inputListeners = new ArrayList<InputListener>();
	
	public InputHandler(Resources res)
	{
		Bitmap tempButton = BitmapFactory.decodeResource(res, R.drawable.temp_button);
		Bitmap stickImg = BitmapFactory.decodeResource(res, R.drawable.analog_stick);
		Bitmap subStickImg = BitmapFactory.decodeResource(res, R.drawable.sub_analog_stick);
		
		buttons.add(new ControlButton(tempButton,40,ControlButton.BUTTON_STEAL_CAR));
		buttons.add(new ControlButton(tempButton,40,ControlButton.BUTTON_CHANGE_WEAPON));
		buttons.add(new ControlButton(tempButton,40,ControlButton.BUTTON_FIRE_WEAPON));
		buttons.add(new ControlButton(tempButton,40,ControlButton.BUTTON_GAS));
		buttons.add(new ControlButton(tempButton,40,ControlButton.BUTTON_BREAK));
		
		analogStick = new AnalogStick(stickImg, subStickImg, 40);
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
		analogStick.setCenter(w * 0.2f, h * 0.8f);
		for(int i = 0; i < buttons.size(); ++i)
		{
			buttons.get(i).setCenter(w * 0.75f,i * 100);
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
	
	
}
