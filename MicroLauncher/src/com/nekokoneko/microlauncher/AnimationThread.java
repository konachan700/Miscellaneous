package com.nekokoneko.microlauncher;

import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class AnimationThread extends Thread implements Runnable {
	private BroadcastReceiver nBatteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent iIntent) {
			String tAction = iIntent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(tAction)) {
				int tLevel = iIntent.getIntExtra("level", 0);
				int tScale = iIntent.getIntExtra("scale", 100);
				int tBatteryLevel = ((tLevel * 100) / tScale);
				setBatteryLevel(tBatteryLevel);
			}
		}
	};
	
	private class SensorsReader implements SensorEventListener {
		public SensorsReader() {}
		
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			
		}

		@Override
		public void onSensorChanged(SensorEvent arg0) {
			if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				final double acc = (Math.sqrt(arg0.values[0]*arg0.values[0] + arg0.values[1]*arg0.values[1] + arg0.values[2]*arg0.values[2]) - SensorManager.GRAVITY_EARTH);
				
				nMotionArray[nMotionArrayCounter] = (acc < 0) ? -acc : acc;
				nMotionArrayCounter++;
				if (nMotionArrayCounter >= 10) {
					nMotionArrayCounter = 0;
					double acc2 = 0;
					for (int i=0; i<10; i++) {
						acc2 += nMotionArray[i];
					}
					nMotion = acc2 / 10;
				}
			}
		}
	}
	
	private boolean 			StopThread 			= false, 
								PauseThread 		= false;
	
	private SurfaceHolder 		nSurfaceHolder 		= null;
	private Context				nContext			= null;
	private Resources			nResources          = null;
	private TimeDateWidget		nTimeDateWidget		= null;
	private Sprites				nSprites            = null;
	private TransparentTextBox  nTransparentTextBox = null;
	private SensorsReader		nSensorsReader		= new SensorsReader();
	private SensorManager		nSensorManager      = null;
	private Sensor				nSensor1			= null;
	private LauncherButtons		nLauncherButtons 	= null;
	private Activity			nActivity			= null;
	private SharedPreferences	nSharedPreferences	= null;
	
	private int					nBatteryLevel		= 0,
			      				nMotionArrayCounter = 0,
			      				nEyeTriggerState 	= 0;
	
	private long				nEyeTrigger			= 0;

	private double				nMotionArray[]		= new double[10],
								nMotion				= 0;
	
	public AnimationThread(Activity iActivity, Resources iResources, SurfaceHolder surfaceHolder, Context context, int width, int height) {
		nSurfaceHolder 			= surfaceHolder;
		nContext				= context;
		nResources      		= iResources;
		nActivity				= iActivity;
		nSprites				= new Sprites(nActivity, nResources, width, height);
		nTransparentTextBox 	= new TransparentTextBox(nResources, width, height);
		nTimeDateWidget 		= new TimeDateWidget(context, width, height);
		nSharedPreferences		= PreferenceManager.getDefaultSharedPreferences(nActivity);
		
		nContext.registerReceiver(nBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		nSensorManager		 	= (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		nSensor1				=  nSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		nSensorManager.registerListener(nSensorsReader, nSensor1, SensorManager.SENSOR_DELAY_UI);
		
		nLauncherButtons		= new LauncherButtons(nActivity, nContext, width, height);
	}
	
	public void setBatteryLevel(int iBL) {
		nBatteryLevel = iBL;
	}
	
	public void TouchEvent(MotionEvent event) {
		if (nLauncherButtons != null) nLauncherButtons.onTouchEvent(event);
	
	}
	
	public void run() {
		StringBuilder tTextForTTB = new StringBuilder();
		String tSpriteName = "normal";
		
		while (true) {
			if (StopThread) {
				StopThread = false;
				return;
			}
			
			synchronized (nSurfaceHolder) {
				Canvas tCanvas = nSurfaceHolder.lockCanvas();
				if (tCanvas != null) tCanvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
			
				if (nLauncherButtons.IsOpened()) {
					nLauncherButtons.Draw(tCanvas);
				} else {
					long tTime = new Date().getTime();
					switch (nEyeTriggerState) {
					case 0:
						if (nEyeTrigger < tTime) {
							nEyeTrigger = new Date().getTime() + new Random().nextInt(100) + 50;
							tSpriteName = "eye_half_close";
							nEyeTriggerState = 1;
						}
						break;
					case 1:
						if (nEyeTrigger < tTime) {
							nEyeTrigger = new Date().getTime() + new Random().nextInt(200) + 50;
							tSpriteName = "eye_full_close";
							nEyeTriggerState = 2;
						}
						break;
					case 2:
						if (nEyeTrigger < tTime) {
							nEyeTrigger = new Date().getTime() + new Random().nextInt(3000) + 1500;
							tSpriteName = "normal";
							nEyeTriggerState = 0;
						}
						break;
					}

					tTextForTTB.delete(0, tTextForTTB.length());

					if (nLauncherButtons.isLigtsOn()) {
						tTextForTTB.append("Фонарик включен. ");
					} else {
						if (nMotion > 1.2) {
							double tM = Math.round(nMotion * Math.pow(10, 2)) / Math.pow(10, 2);
							tTextForTTB.append("Уровень тряски телефона " + tM + ". Руки дрожат?");
						}
					}

					nSprites.Draw(tCanvas, tSpriteName);
					if (tTextForTTB.length() > 1) {
						String tOldSprite = nSharedPreferences.getString("change_char_list", "belita");
						nTransparentTextBox.Draw(tCanvas, tTextForTTB.substring(0), tOldSprite.substring(0, 1).toUpperCase() + tOldSprite.substring(1).toLowerCase());
					}
					
					nLauncherButtons.DrawOnNotOpened(tCanvas);
				}
				
				nLauncherButtons.DrawNonClosable(tCanvas);
				
				nTimeDateWidget.SetBatteryLevel(nBatteryLevel);
				nTimeDateWidget.Draw(tCanvas);
				
				if (tCanvas != null) nSurfaceHolder.unlockCanvasAndPost(tCanvas);
			}
			
			try { Thread.sleep(75); } catch (InterruptedException e) { }
			
			synchronized (this) {
                if (PauseThread) {
                	try { wait(); } catch (Exception e) {}
                }
			}
		}
	}
	
	public void StopThread() {
		StopThread = true;
		
		synchronized(this) {
            this.notify();
		}
	}
	
	public void PauseAnimation() {
		nContext.unregisterReceiver(nBatteryInfoReceiver);
		nSensorManager.unregisterListener(nSensorsReader);
		nLauncherButtons.onPause();
		PauseThread = true;
        synchronized(this) {
                this.notify();
        }
	}
	
	public void ResumeAnimation() {
		nContext.registerReceiver(nBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		nSensorManager.registerListener(nSensorsReader, nSensor1, SensorManager.SENSOR_DELAY_NORMAL);
		nLauncherButtons.onResume();
		PauseThread = false;
		synchronized(this) {
            this.notify();
		}
	}
	
	public void onMenuButtonPress() {
		nLauncherButtons.onMenuButtonPress();
	}
}
