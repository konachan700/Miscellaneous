package uvao_wp;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;

public class images_engine {
	private Resources XR;

	public images_engine(Resources res) {
		XR = res;
	}

	/* Спрайты подогнаны по меньшим сторонам, но не обрезаны, потому их необходимо позиционировать относительно размеров экрана. */
	public int DrawSprite (
									Canvas cnv, 
									ArrayList<FlyingLights>	_FL, 
									Map<String, Bitmap> 	_XG, 
									String 					spriteID, 
									String 					backgroundID, 
									String 					backgroundTopShadowID, 
									String 					backgroundBottomShadowID, 
									float 					holder_width, 
									float 					holder_height
								 ) throws IllegalArgumentException, NullPointerException  {
		
		final Bitmap b = _XG.get(spriteID);
		if (b == null) {
			return 0;
		}
		
		final float SpriteWidth  = (float) b.getWidth();
		final float SpriteHeight = (float) b.getHeight();
		
		float x = 0;
		if (SpriteWidth < holder_width) {
			x = (holder_width / 2) - (SpriteWidth / 2);
		}
		
		float y = 0;
		if (SpriteHeight < holder_height) {
			y = holder_height - SpriteHeight;
		}

		Paint pnt = new Paint();
		pnt.setAntiAlias(true);
		
		final Bitmap bg = _XG.get(backgroundID);
		if (bg == null) {
			return 0;
		}

		cnv.drawBitmap(bg, 0, 0, null);		
		
		if (_FL != null) {
			for (int i=0; i<_FL.size(); i++) {
				FlyingLights fl2 = _FL.get(i);
				fl2.FlyingLightsMove();
				fl2.FlyingLightsDraw(cnv);
				_FL.set(i, fl2);
			}
		}

		cnv.drawBitmap(b, x, y, pnt);
		
		if (backgroundTopShadowID != null) {
			final Bitmap bgtop = _XG.get(backgroundTopShadowID);
			if (bgtop != null) cnv.drawBitmap(bgtop, 0, 0, null);
		}
		
		if (backgroundBottomShadowID != null) {
			final Bitmap bgbottom = _XG.get(backgroundBottomShadowID);
			if (bgbottom != null) cnv.drawBitmap(bgbottom, 0, (int)(holder_height - bgbottom.getHeight()), null);
		}

		return 1;
	}
	
	
	
	private String __GetCacheDirectory() {
		File f = Environment.getExternalStorageDirectory();
		if (f.canWrite()) {
			final String p1 = f.getAbsolutePath() + "/.uvao-livewallpaper/cache";
			File f1 = new File(p1);
			if (f1.canWrite()) {
				return p1;
			} else {
				if (f1.mkdirs()) {
					return p1;
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}
	
	public int CreateLocalCachePNG(Bitmap ImageToExport) {
		final String CachePath = __GetCacheDirectory();
		if (CachePath != null) {
			
			
			
			return 0;
		} else {
			return -1;
		}
	}
	
	public Bitmap ConvertSpriteFromResource(int resID, float holder_width, float holder_height) {
		float propXY = holder_width / holder_height;
		Bitmap bmp = BitmapFactory.decodeResource(XR, resID);

		float propBXY = ((float)bmp.getWidth()) / ((float)bmp.getHeight());
		float nW, nH;
		
		if (propXY > propBXY) {
			nW = holder_height * propBXY;
			nH = holder_height;
		} else {
			nW = holder_width;
			nH = holder_width / propBXY;
		}
		
		Bitmap bmp_scaled = Bitmap.createScaledBitmap(bmp, (int)nW, (int)nH, true);
		return bmp_scaled;
	}
	
	public Bitmap ConvertBackgroundFromResource(int resID, float holder_width, float holder_height) { 
		final Bitmap B = BitmapFactory.decodeResource(XR, resID);
		Bitmap outBMP = ResizeBitmapWithCut(B, holder_width, holder_height);
		return outBMP;
	}

	private Bitmap __ResizeBitmapWithCut1(Bitmap InputImage, float DisplayWidth, float DisplayHeight) {
		final float W = InputImage.getWidth(), H = InputImage.getHeight();
		final float KI = W / H;
		float IH = DisplayHeight;
		float IW = DisplayHeight * KI;
		float X = 0, Y = 0;
		if (IW > DisplayWidth) {
			X = (IW - DisplayWidth) / 2;
		} else if (IW < DisplayWidth) {
			final float KC = DisplayWidth - IW;
			IW = DisplayWidth;
			IH = IH + (KC / KI);
			Y = (IH - DisplayHeight) / 2;
		}
		final Bitmap B = Bitmap.createScaledBitmap(InputImage, (int) IW, (int) IH, true);
		Bitmap OutputImage = Bitmap.createBitmap(B, (int) X, (int) Y, (int) DisplayWidth, (int) DisplayHeight);
		return OutputImage;
	}
	
	private Bitmap __ResizeBitmapWithCut2(Bitmap InputImage, float DisplayWidth, float DisplayHeight) {
		final float W = InputImage.getWidth(), H = InputImage.getHeight();
		final float KI = H / W;
		float IW = DisplayWidth;
		float IH = DisplayWidth * KI;
		float X = 0, Y = 0;
		if (IH < DisplayHeight) {
			final float KC = DisplayHeight - IH;
			IH = DisplayHeight;
			IW = IW + (KC / KI);
			X = (IW - DisplayWidth) / 2;
		} else if (IH > DisplayHeight) {
			Y = (IH - DisplayHeight) / 2;
		}
		final Bitmap B = Bitmap.createScaledBitmap(InputImage, (int) IW, (int) IH, true);
		Bitmap OutputImage = Bitmap.createBitmap(B, (int) X, (int) Y, (int) DisplayWidth, (int) DisplayHeight);
		return OutputImage;
	}

	public Bitmap ResizeBitmapWithCut(Bitmap InputImage, float DisplayWidth, float DisplayHeight) {
		if (DisplayWidth <= DisplayHeight) {
			return __ResizeBitmapWithCut1(InputImage, DisplayWidth, DisplayHeight);
		} else {
			return __ResizeBitmapWithCut2(InputImage, DisplayWidth, DisplayHeight);
		}	
	}	
}
