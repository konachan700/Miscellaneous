package aniwp;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import com.aniwp.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class FlyingLights {
	private Resources nResources = null;
	private Canvas    nCanvas    = null;
	
	private class FlyingLight {
		public float x, y, a, scale, x_inc, y_inc, a_inc, x_min, x_max, y_min, y_max, a_min, a_max, f_rotate = 0;
		private Bitmap b;
		
		public FlyingLight(float tScale) {
			Bitmap bt = null;
			scale = tScale;
			
			final String fs = FSEngine.GetRootDirectory();
			if (fs != null) {
				final File f = new File(fs + "/fl.png");
				if (f.exists() == true) {
					bt = BitmapEngine.CreateBackgroundFromFile(fs + "/fl.png", 32, 32);
				} else {
					bt = BitmapFactory.decodeResource(nResources, R.drawable.fl_heart);
				}
			} else {
				bt = BitmapFactory.decodeResource(nResources, R.drawable.fl_heart);
			}

			if (bt == null) return;
			
			b = Bitmap.createScaledBitmap(bt, (int) scale, (int) scale, true);
			bt.recycle();
		}
	
		public void move() {
			Random r = new Random();
			
			x = x + x_inc + ((float)(r.nextInt(25)) * 0.01f);
			if (x > x_max) x = x_min;
			if (x < x_min) x = x_max;
			
			y = y - y_inc - ((float)(r.nextInt(25)) * 0.01f);
			if (y > y_max) y = y_min;
			if (y < y_min) y = y_max;
			
			
			a = a + a_inc;
			if (a >= a_max) a_inc = -a_inc;
			if (a <= a_min) a_inc = -a_inc; 
			
			f_rotate = 15 - r.nextInt(30);
			//f_rotate += (12f - r.nextInt(24));
			//if (f_rotate >= 360) f_rotate = 0;
		}
		
		public Bitmap getBitmap() {
			return b;
		}
	}
	
	private ArrayList<FlyingLight> nFlyingLights = new ArrayList<FlyingLight>();
	private Matrix tMatrix = new Matrix();
	private Paint tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public FlyingLights(int count, float sw, float sh, Resources rs) {
		Random r = new Random();
		nResources = rs;
		tPaint.setAntiAlias(true);
		
		for (int i=0; i<count; i++) {
			FlyingLight f = new FlyingLight(16 + r.nextInt(16));
			f.a = 185f;
			f.a_inc = 0.43f + (0.02f * r.nextInt(43));
			f.a_max = 240f;
			f.a_min = 180f;
			
			f.x = r.nextInt((int)(sw - f.scale));
			f.x_inc = 0.01f + (0.01f * r.nextInt(43));
			f.x_max = sw - f.scale;
			f.x_min = 0;
			
			f.y = r.nextInt((int)(sh - f.scale));
			f.y_inc = 0.43f + (0.015f * r.nextInt(43));
			f.y_max = sh - f.scale;
			f.y_min = 0;
			
			nFlyingLights.add(f);
		}
	}
	
	public void MoveAndDraw(Canvas c) {
		nCanvas = c;
		final int count = nFlyingLights.size();
		for (int i=0; i<count; i++) {
			FlyingLight f = nFlyingLights.get(i);
			f.move();
			nFlyingLights.set(i, f);
			tPaint.setAlpha((int)f.a);
			
			tMatrix = new Matrix();
			tMatrix.preTranslate(f.x, f.y);
			tMatrix.preRotate(f.f_rotate, f.getBitmap().getWidth() / 2, f.getBitmap().getHeight() / 2);
			
			if (nCanvas != null) nCanvas.drawBitmap(f.getBitmap(), tMatrix, tPaint);
		}
	}
}
