package com.jkgames.gta;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.provider.MediaStore.Audio.Media;

public class SoundManager
{
	private SoundPool pool = null;
	private MediaPlayer radio;
	private MediaPlayer ambiance;
	private Map<String, Integer> soundMap = new HashMap<String, Integer>();
	private int curRadioTime;
	
	private void mapSound(String s, int resource, Context c)
	{
		int id = pool.load(c, resource,0);
		soundMap.put(s, id);
	}
	
	private int seekRadioToRandomPos()
	{
		int l = JMath.randomRange(0, getRadioLength());
		curRadioTime = l;
		radio.seekTo(l);
		return l;
	}
	public SoundManager(Context c)
	{
		pool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
		radio = MediaPlayer.create(c, R.raw.radio);
		ambiance = MediaPlayer.create(c, R.raw.ambiance);
		radio.start();
		seekRadioToRandomPos();
		radio.pause();
		radio.setVolume(0.65f, 0.65f);
		radio.setLooping(true);
		ambiance.setVolume(0.65f, 0.65f);
		ambiance.setLooping(true);
		ambiance.start();
		ambiance.seekTo(JMath.randomRange(0, ambiance.getDuration()));
		
		mapSound("walk", R.raw.walk, c);
		mapSound("car_start", R.raw.car_start, c);
		mapSound("engine_idle", R.raw.engine_idle, c);
		mapSound("car_moving", R.raw.car_moving, c);
		mapSound("car_move_loop", R.raw.car_move_loop, c);
		mapSound("run_over", R.raw.run_over, c);
		mapSound("car_skid", R.raw.car_skid, c);
		mapSound("car_crash_wall", R.raw.car_crash_wall, c);
		mapSound("car_scrape", R.raw.car_scrape, c);
	}
	
	private int getRadioLength()
	{
		return radio.getDuration();
	}
	
	public int play(String sound, boolean loop)
	{
		int id = soundMap.get(sound);
		return pool.play(id, 1.0f,1.0f, 0, loop ? -1 : 0, 1.0f);
	}
	
	public int play(String sound)
	{
		return play(sound,false);
	}
	
	public void stop(int soundId)
	{
		pool.stop(soundId);
	}
	
	public void changePitch(int soundId, float pitch)
	{
		pool.setRate(soundId, pitch);
	}
	
	public void pauseRadio()
	{
		radio.pause();
		curRadioTime = radio.getCurrentPosition();
	}
	
	public void resumeRadio()
	{
		radio.start();
		radio.seekTo(curRadioTime);
	}
	
	public void pauseAmbiance()
	{
		ambiance.pause();
	}
	
	public void resumeAmbiance()
	{
		ambiance.start();
	}
	
	public void update(float timeElapsed)
	{
		int time = (int)(timeElapsed * 1000);
		if(!radio.isPlaying())
		{
			curRadioTime += time;
			if(curRadioTime > getRadioLength())
			{
				curRadioTime = 0;
			}
		}
	}
	
	public void halt()
	{
		pool.autoPause();
		radio.pause();
		ambiance.pause();
	}
	
}
