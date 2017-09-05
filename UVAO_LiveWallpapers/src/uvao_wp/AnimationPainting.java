package uvao_wp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.uvao_wp.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class AnimationPainting extends Thread implements Runnable {
	private final int  TOUCH_EVENT_HEAD 	= 1, 
					   TOUCH_EVENT_EARS 	= 2,
					   TOUCH_EVENT_HANDS 	= 3,
					   TOUCH_EVENT_FACE 	= 4,
					   TOUCH_EVENT_BOOBS 	= 5;
	
	private boolean 	PauseThread 					= false, 
						StopThread 						= false, 
						enable_lights 					= true, 
						flag_ears						= false;
	
	private int 		holder_width					= 0, 
						holder_height					= 0;
	
	private long 		LastEarUpDownTrigger1			= 0,
						LastEarUpDownTrigger2			= 0;
	
	private String 		CurrentSprite 					= "LS", 
						OldSprite 						= "LS",
						CurrentBackground 				= "DefaultBackground",
						CurrentBackgroundTopShadow 		= "TopLineShadow", 
						CurrentBackgroundBottomShadow 	= "BottomLineShadow";
	
	private volatile images_engine				XI = null;
	private volatile Resources       			XR = null;
	private volatile ArrayList<FlyingLights> 	FL = new ArrayList<FlyingLights>();
	private volatile SurfaceHolder   			XH = null;
	private volatile TouchMapEngine 			TM = null;
	
	private volatile Map<String, Bitmap> 		XG = new HashMap<String, Bitmap>();
	
	//private volatile Thread.UncaughtExceptionHandler oldHandler;
	
	private void _initLastEarUpDownTriggers() {
		LastEarUpDownTrigger1 = (new Date().getTime()) + ((long)(new Random().nextInt(2200) + 2200));
		LastEarUpDownTrigger2 = (new Date().getTime()) + 210;
	}
	
	@SuppressLint("NewApi")
	public AnimationPainting(SurfaceHolder surfaceHolder, Context context, SharedPreferences sp, int width, int height) {
		XH = surfaceHolder;
		XR = context.getResources();
		XI = new images_engine(XR);
		//oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		
		holder_width 	= width;
		holder_height 	= height;
		
    	XG.clear();
    	XG.put("TopLineShadow", 		XI.ConvertSpriteFromResource(R.drawable.bg_up, 		width, height));
    	XG.put("BottomLineShadow", 		XI.ConvertSpriteFromResource(R.drawable.bg_down, 	width, height));
    	
    	XG.put("UVAO-LS-EU", 			XI.ConvertSpriteFromResource(R.drawable.a01_result, width, height));
    	XG.put("UVAO-LS-ED", 			XI.ConvertSpriteFromResource(R.drawable.b01_result, width, height));
    	XG.put("UVAO-RS-EU", 			XI.ConvertSpriteFromResource(R.drawable.a02_result, width, height));
    	XG.put("UVAO-RS-ED", 			XI.ConvertSpriteFromResource(R.drawable.b02_result, width, height));
    	XG.put("UVAO-US-EU", 			XI.ConvertSpriteFromResource(R.drawable.c01_result, width, height));
    	XG.put("UVAO-US-ED", 			XI.ConvertSpriteFromResource(R.drawable.d01_result, width, height));
    	XG.put("UVAO-DS-EU", 			XI.ConvertSpriteFromResource(R.drawable.c02_result, width, height));
    	XG.put("UVAO-DS-ED", 			XI.ConvertSpriteFromResource(R.drawable.d02_result, width, height));
    	
    	XG.put("UVAO-TA-XX", 			XI.ConvertSpriteFromResource(R.drawable.e01_result, width, height));
    	XG.put("UVAO-TB-XX", 			XI.ConvertSpriteFromResource(R.drawable.e02_result, width, height));
    	
    	XG.put("TouchMap", 				XI.ConvertSpriteFromResource(R.drawable.touch_map,  width, height));    	
    	XG.put("DefaultBackground",		XI.ConvertBackgroundFromResource(R.drawable.defbg,  width, height));
    	
    	XG.put("Light",					BitmapFactory.decodeResource(XR, R.drawable.light));
    	XG.put("Light2",				BitmapFactory.decodeResource(XR, R.drawable.light2));
    	XG.put("Heart",				    BitmapFactory.decodeResource(XR, R.drawable.heart));
    	
    	TM = new TouchMapEngine(XG, "TouchMap", width, height);
    	TM.RegisterNewAction(TOUCH_EVENT_HEAD,  0xFFFF0000);
    	TM.RegisterNewAction(TOUCH_EVENT_EARS,  0xFF550000);
    	TM.RegisterNewAction(TOUCH_EVENT_HANDS, 0xFF330000);
    	TM.RegisterNewAction(TOUCH_EVENT_FACE,  0xFF880000);
    	TM.RegisterNewAction(TOUCH_EVENT_BOOBS, 0xFFAA0000);
    	
    	FlyingLights fl1 = new FlyingLights(XG, "Light", width, height, 50);
    	FlyingLights fl2 = new FlyingLights(XG, "Light2", width, height, 30);
    	
    	FL.add(fl1);
    	FL.add(fl2);
    	
    	final float HSH = (width / 2), HS5 = (width / 5), HSB = height - (height / 6);
    	FlyingLights fl3 = new FlyingLights(XG, "Heart", width, height, 5, HSH - HS5, HSH + HS5, HSB);
    	FL.add(fl3);
	}
	
	@SuppressLint("NewApi")
	public void TouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			switch (TM.GetCurrentAction(event)) {
			case TOUCH_EVENT_HEAD:
				if ((!CurrentSprite.contains("T")) && (!CurrentSprite.contains("X"))) OldSprite = CurrentSprite;
				_initLastEarUpDownTriggers();
				CurrentSprite = "TB";
				break;
			case TOUCH_EVENT_EARS:
				
				break;
			case TOUCH_EVENT_HANDS:
				
				break;
			case TOUCH_EVENT_FACE:
				
				break;
			case TOUCH_EVENT_BOOBS:
				if ((!CurrentSprite.contains("T")) && (!CurrentSprite.contains("X"))) OldSprite = CurrentSprite;
				_initLastEarUpDownTriggers();
				CurrentSprite = "TA";
				break;
			default:
				if (event.getY() < (holder_height / 3)) {
					CurrentSprite = "US";
				} else {
					if (event.getX() < (holder_width / 4)) {
						CurrentSprite = "LS";
					} else if (event.getX() > (holder_width - (holder_width / 4))) {
						CurrentSprite = "RS";
					} else {
						CurrentSprite = "DS";
					}
				}
				OldSprite = CurrentSprite;
				break;
			}
		}
	}
	
	public void run() {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionSaveEngine());
		while (true) {
			if (StopThread) {
				StopThread = false;
				return;
			}

			long DT = new Date().getTime();
			if (DT > LastEarUpDownTrigger1) {
				CurrentSprite = OldSprite;
				flag_ears = false;
				_initLastEarUpDownTriggers();
			} else {
				flag_ears = true;
				if (DT > LastEarUpDownTrigger2) {
					flag_ears = false;
				}
			}
	
			synchronized (XH) {		
				try {	
					if (XH != null) { 
						Canvas cv = XH.lockCanvas();
						if (CurrentSprite.contains("T") || CurrentSprite.contains("X")) { 
							XI.DrawSprite(cv, ((enable_lights) ? FL : null), XG, 
									"UVAO-" + CurrentSprite + "-XX", 
									CurrentBackground, CurrentBackgroundTopShadow, CurrentBackgroundBottomShadow, holder_width, holder_height);
						} else {
							XI.DrawSprite(cv, ((enable_lights) ? FL : null), XG, 
									"UVAO-" + CurrentSprite + ((flag_ears) ? "-ED" : "-EU"), 
									CurrentBackground, CurrentBackgroundTopShadow, CurrentBackgroundBottomShadow, holder_width, holder_height);
						}
						XH.unlockCanvasAndPost(cv);
					}
				} catch (IllegalArgumentException e) { return; } // ќтлавливает умерший контекст основоного потока при закрытии оного, падает при нажатии кнопки "установить обои".
				  catch (NullPointerException e) { return; } // „—’, обои устанавливаютс€, но вот ошибка глаз не радует. ѕридумал пиздец какой костыль, но щито поделаешь =^_^=
			} // Ќа самом деле тут надо делать как в играх, нормальную реализацию многопоточности. ќднако лень, обоина в первую очередь дл€ себ€, все пон€ли короче.

			try { Thread.sleep(30); } catch (InterruptedException e) { }
			
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
		PauseThread = true;
        synchronized(this) {
                this.notify();
        }
	}
	
	public void ResumeAnimation() {
		PauseThread = false;
		synchronized(this) {
            this.notify();
		}
	}
}
