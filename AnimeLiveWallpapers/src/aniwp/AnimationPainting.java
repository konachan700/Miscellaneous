package aniwp;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import com.aniwp.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class AnimationPainting extends Thread implements Runnable {
	private boolean 	PauseThread 					= false, 
						StopThread 						= false,
						EyeLock							= false;
	
	private long		EyeLockTimer					= 0;
	
	private int			nScreenWidth					= 0,
						nScreenHeight					= 0,
						nWallpapersChangeTime			= 10,
						nFlyingStarsCount				= 43,
						nFlyingStarsEnable				= 1,
						nShadowDisplay					= 1;
	
	private volatile SurfaceHolder   			nSurfaceHolder 		= null;
	private volatile Resources       			nResources 			= null;
	private volatile TouchMapEngine 			nTouchMapEngine 	= null;
	private volatile SpritesEngine 				nSpritesEngine 		= null;
	private volatile BackgroundsEngine 			nBackgroundsEngine 	= null;
	private volatile Random 					nRandom 			= new Random();
	private volatile Bitmap						nCurrentSprite		= null,
												nCurrentBackground  = null;
	private volatile Paint 						nDefaultPaint 		= new Paint();
	private volatile Properties 				nProperties1 		= null;
	private volatile FlyingLights 				nFlyingLights1 		= null;
	
	private volatile Paint						nGeneralShadowTop	= new Paint(Paint.ANTI_ALIAS_FLAG),
												nGeneralShadowLeft  = new Paint(Paint.ANTI_ALIAS_FLAG),
												nGeneralShadowRight = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	@SuppressLint("NewApi")
	public AnimationPainting(SurfaceHolder surfaceHolder, Context context, SharedPreferences sp, int width, int height) {
		nScreenWidth 		= width;
		nScreenHeight 		= height;
		nSurfaceHolder 		= surfaceHolder;
		nResources 			= context.getResources();
		
		if (nSurfaceHolder != null) { 
			Canvas tCanvas = nSurfaceHolder.lockCanvas();
			final Bitmap b = BitmapFactory.decodeResource(nResources, R.drawable.please_wait);
			BitmapEngine.DrawBitmapOnCenter(b, nDefaultPaint, tCanvas, nScreenWidth, nScreenHeight);
			nSurfaceHolder.unlockCanvasAndPost(tCanvas);
		}
		
		nProperties1 = FSEngine.GetWallpapersConfig();
		if (nProperties1 != null) {
			nWallpapersChangeTime 	= Integer.valueOf(nProperties1.getProperty("bgchange", "10"));
			nFlyingStarsCount 		= Integer.valueOf(nProperties1.getProperty("flcount", "43"));
			nFlyingStarsEnable 		= Integer.valueOf(nProperties1.getProperty("fl", "1"));
			nShadowDisplay			= Integer.valueOf(nProperties1.getProperty("shadow", "1"));
		}
		
		LinearGradient tLinearGradient1 = new LinearGradient(0, 0, 0, 200, 0xFF000000, 0x00000000, TileMode.CLAMP);
		nGeneralShadowTop.setStyle(Paint.Style.FILL);
		nGeneralShadowTop.setShader(tLinearGradient1);
		
		LinearGradient tLinearGradient2 = new LinearGradient(0, 0, 75, 0, 0xFF000000, 0x00000000, TileMode.CLAMP);
		nGeneralShadowLeft.setStyle(Paint.Style.FILL);
		nGeneralShadowLeft.setShader(tLinearGradient2);
		
		LinearGradient tLinearGradient3 = new LinearGradient(nSurfaceHolder.getSurfaceFrame().right-75, 0, nSurfaceHolder.getSurfaceFrame().right, 0, 0x00000000, 0xFF000000, TileMode.CLAMP);
		nGeneralShadowRight.setStyle(Paint.Style.FILL);
		nGeneralShadowRight.setShader(tLinearGradient3);
		
		nSpritesEngine 		= new SpritesEngine(nResources, width, height);
		nTouchMapEngine 	= new TouchMapEngine(nSpritesEngine, width, height);
		nBackgroundsEngine 	= new BackgroundsEngine(nResources, width, height);
		if (nFlyingStarsEnable == 1)
			nFlyingLights1	= new FlyingLights(nFlyingStarsCount, width, height, nResources);
	}
	
	@SuppressLint("NewApi")
	public void TouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			final String ev = nTouchMapEngine.GetEventString(event);
			if (ev != null) {
				EyeLock = true;
				EyeLockTimer = new Date().getTime() + (nRandom.nextInt(1200) + 1000);
				nCurrentSprite = nSpritesEngine.GetSprite(ev);
			}
		}
	}
	
	public void run() {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionSaveEngine());
		long tCurrentDate = 0, tEyeTrigger1 = 0, tBGTrigger = 0;
		
		while (true) {
			if (StopThread) {
				StopThread = false;
				return;
			}

			tCurrentDate = new Date().getTime();
			if (EyeLock == false) {
				if (tCurrentDate > tEyeTrigger1) {
					EyeLock = true;
					EyeLockTimer = new Date().getTime() + (nRandom.nextInt(90) + 90);
					nCurrentSprite = nSpritesEngine.GetSprite("eye_close");
				} else {
					nCurrentSprite = nSpritesEngine.GetSprite("normal");
				}
			} else {
				if (tCurrentDate > EyeLockTimer) {
					EyeLock = false;
					nCurrentSprite = nSpritesEngine.GetSprite("normal");
					tEyeTrigger1 = tCurrentDate + (nRandom.nextInt(2500) + 1900);
				}
			}
			
			if (tCurrentDate > tBGTrigger) {
				nCurrentBackground = nBackgroundsEngine.GetBackground();
				nBackgroundsEngine.NextBackground();
				tBGTrigger = tCurrentDate + (1000 * 60 * 60 * ((nWallpapersChangeTime <= 0) ? 10 : nWallpapersChangeTime));
			}
			
			synchronized (nSurfaceHolder) {		
				try {	
					if (nSurfaceHolder != null) { 
						Canvas tCanvas = nSurfaceHolder.lockCanvas();
						
						BitmapEngine.DrawBackground(nCurrentBackground, nDefaultPaint, tCanvas);
						if (nFlyingLights1 != null) nFlyingLights1.MoveAndDraw(tCanvas);
						BitmapEngine.DrawSprite(nCurrentSprite, nDefaultPaint, tCanvas, nScreenWidth, nScreenHeight);
						
						if (nShadowDisplay > 0) {
							if (tCanvas != null) tCanvas.drawRect(0, 0, 75, tCanvas.getHeight(), nGeneralShadowLeft);
							if (tCanvas != null) tCanvas.drawRect(0, 0, tCanvas.getWidth(), 200, nGeneralShadowTop);
							if (tCanvas != null) tCanvas.drawRect(tCanvas.getWidth()-75, 0, tCanvas.getWidth(), tCanvas.getHeight(), nGeneralShadowRight);
						}
						
						nSurfaceHolder.unlockCanvasAndPost(tCanvas);
					}
				} catch (IllegalArgumentException e) { return; } // Отлавливает умерший контекст основоного потока при закрытии оного, падает при нажатии кнопки "установить обои".
				  catch (NullPointerException e) { return; } // ЧСХ, обои устанавливаются, но вот ошибка глаз не радует. Придумал пиздец какой костыль, но щито поделаешь =^_^=
			} // На самом деле тут надо делать как в играх, но западло, :3 и так сойдет.

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
