package com.nekokoneko.microlauncher;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

public class MainActivity extends Activity {
	private class CSurfaceView extends SurfaceView implements SurfaceHolder.Callback {	
		private SurfaceHolder 		nSurfaceHolder 		= null;
		private Context				nContext			= null;
		private Resources			nResources			= null;
		private Activity			nActivity 			= null;
		
		private AnimationThread		nAnimationThread    = null;
		
		private int					nScreenWidth		= 0,
									nScreenHeight		= 0;
		
		public CSurfaceView(Context context, Activity iActivity) {
			super(context);

			nSurfaceHolder  = this.getHolder();
			nContext		= context;
			nResources		= nContext.getResources();
			nActivity		= iActivity;
			
			this.setBackgroundColor(0);
			this.getHolder().addCallback(this);
			this.setZOrderOnTop(true);
			
			nSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		}
		
		@Override
        public boolean onTouchEvent(MotionEvent event) {
			if (nAnimationThread != null) {
				nAnimationThread.TouchEvent(event);
			}
			return true;
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
			nScreenWidth  = arg2;
			nScreenHeight = arg3;
			
			if (nContext != null) {
            	if (nAnimationThread == null) {
            		nAnimationThread = new AnimationThread(nActivity, nResources, nSurfaceHolder, nContext, nScreenWidth, nScreenHeight);
            		nAnimationThread.start();
            	}
    		}
		}

		public AnimationThread GetAnimationThread() {
			return nAnimationThread;
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder arg0) { }

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) { }
	}
	
	
	private CSurfaceView nSurfaceView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nSurfaceView = new CSurfaceView(this, this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(nSurfaceView);
	}

	@Override
    protected void onResume() {
		super.onResume();
		synchronized (nSurfaceView) {
			if (nSurfaceView != null)
				if (nSurfaceView.GetAnimationThread() != null) 
					nSurfaceView.GetAnimationThread().ResumeAnimation();
		}
	}
	
	@Override
    protected void onPause() {
		super.onPause();
		synchronized (nSurfaceView) {
			if (nSurfaceView != null)
				if (nSurfaceView.GetAnimationThread() != null) 
					nSurfaceView.GetAnimationThread().PauseAnimation();
		}
	}
	
	@Override
    public void onDestroy() {
		super.onDestroy();
		synchronized (nSurfaceView) {
			if (nSurfaceView != null)
				if (nSurfaceView.GetAnimationThread() != null) 
					nSurfaceView.GetAnimationThread().StopThread();
		}
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (nSurfaceView != null)
			if (nSurfaceView.GetAnimationThread() != null) 
				nSurfaceView.GetAnimationThread().onMenuButtonPress();
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_HOME) || (keyCode == KeyEvent.KEYCODE_BACK)) {
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();        
	}
}
