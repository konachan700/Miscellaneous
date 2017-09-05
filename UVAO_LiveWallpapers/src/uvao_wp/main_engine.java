package uvao_wp;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class main_engine extends WallpaperService {
	UVAOEngine ue1 = null;
	
	@Override
	public Engine onCreateEngine() {
		if (ue1 != null) {
			ue1.AP.StopThread();
			ue1 = null;
		}
		ue1 = new UVAOEngine();
		return ue1;
	}
	
	@Override
    public void onCreate() {
            super.onCreate();
    }

    @Override
    public void onDestroy() {
            super.onDestroy();
    }
	
    public class UVAOEngine extends Engine {
    	private AnimationPainting 	AP = null;
    	private Context 			CN = null;
    	//private SurfaceHolder		SH = null;
    	private SharedPreferences   SP = null;
    	
    	private int flag_loaded = 0; /* флаг первоначальной загрузки и конвертации всех изображений, устанавливается в 1, когда все завершено и готово */
    	private int KDISP;
    	
    	public UVAOEngine() {
    		//SH = getSurfaceHolder();
    		CN = getApplicationContext(); 
    		SP = getSharedPreferences("uvao-wpx", MODE_PRIVATE);
    		Thread.setDefaultUncaughtExceptionHandler(new ExceptionSaveEngine()); 
    	}
    	
    	@Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
    	}
    	
    	@Override
        public void onDestroy() {
            super.onDestroy();
            boolean retry = true;
            AP.StopThread();
            while (retry) {
                try {
                    AP.join();
                    retry = false;
                } catch (InterruptedException e) {}
            }
        }
    	
    	@Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            int CURR_OR = CN.getResources().getConfiguration().orientation;
            
            if (flag_loaded == 0) {
            	AP = new AnimationPainting(holder, CN, SP, width, height);
            	AP.start();
            	flag_loaded = 1;
            	KDISP = CN.getResources().getConfiguration().orientation;
            }
            
            if (KDISP != CURR_OR) {
            	AP.StopThread();
            	boolean retry = true;
            	while (retry) {
                    try {
                        AP.join();
                        retry = false;
                    } catch (InterruptedException e) {}
                }
            	AP = null;
            	AP = new AnimationPainting(holder, CN, SP, width, height);
            	AP.start();
            	KDISP = CN.getResources().getConfiguration().orientation;
            }
    	}
    	
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }
        
        @Override
        public void onVisibilityChanged(boolean visible) {
	        if (visible) {
	        	if (AP != null) AP.ResumeAnimation();
	        } else {
	        	if (AP != null) AP.PauseAnimation();
	        }
        }
    	
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            boolean retry = true;
            AP.StopThread();
            while (retry) {
                try {
                    AP.join();
                    retry = false;
                } catch (InterruptedException e) {}
            }
            //try { Thread.sleep(500); } catch (InterruptedException e) { }
        }
    	
        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            if (AP != null) AP.TouchEvent(event);
        }
    }
}
  