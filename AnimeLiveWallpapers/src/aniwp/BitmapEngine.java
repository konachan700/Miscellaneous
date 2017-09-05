package aniwp;

import java.io.File;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BitmapEngine {
	public BitmapEngine() {}
	
	public static void DrawBackground(Bitmap iBitmap, Paint iPaint, Canvas iCanvas) {
		if ((iCanvas != null) && (iBitmap != null)) iCanvas.drawBitmap(iBitmap, 0, 0, iPaint);
	}
	
	public static void DrawBitmapOnCenter(Bitmap iBitmap, Paint iPaint, Canvas iCanvas, float iScreenWidth, float iScreenHeight) {
		final float w = ((float) iBitmap.getWidth());
		final float h = ((float) iBitmap.getHeight());
		final float x = (iScreenWidth  > w) ? ((iScreenWidth  / 2) - (w / 2)) : 0;
		final float y = (iScreenHeight > h) ? ((iScreenHeight / 2) - (h / 2)) : 0;
		
		if ((iCanvas != null) && (iBitmap != null)) iCanvas.drawBitmap(iBitmap, x, y, iPaint);
	}
	
	public static void DrawSprite(Bitmap iBitmap, Paint iPaint, Canvas iCanvas, float iScreenWidth, float iScreenHeight) {
		final float w = ((float) iBitmap.getWidth());
		final float h = ((float) iBitmap.getHeight());
		final float x = (iScreenWidth > w)  ? ((iScreenWidth / 2) - (w / 2)) : 0;
		final float y = (iScreenHeight > h) ? (iScreenHeight - h) : 0;
		
		if ((iCanvas != null) && (iBitmap != null)) iCanvas.drawBitmap(iBitmap, x, y, iPaint);
	}
	
	private static Bitmap __ResizeBitmapWithCut1(Bitmap InputImage, float DisplayWidth, float DisplayHeight) {
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
	
	private static Bitmap __ResizeBitmapWithCut2(Bitmap InputImage, float DisplayWidth, float DisplayHeight) {
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

	public static Bitmap ResizeBitmapWithCut(Bitmap InputImage, float DisplayWidth, float DisplayHeight) {
		if (DisplayWidth <= DisplayHeight) {
			return __ResizeBitmapWithCut1(InputImage, DisplayWidth, DisplayHeight);
		} else {
			return __ResizeBitmapWithCut2(InputImage, DisplayWidth, DisplayHeight);
		}	
	}
	
	public static Bitmap CreateBackgroundFromResources(Resources r, int resID, float DisplayWidth, float DisplayHeight) {
		final Bitmap B = BitmapFactory.decodeResource(r, resID);
		return ResizeBitmapWithCut(B, DisplayWidth, DisplayHeight);
	}
	
	public static Bitmap CreateBackgroundFromFile(String fName, float DisplayWidth, float DisplayHeight) {
		File f = new File(fName);
		if (f.canRead() == false) return null;
		Bitmap B = BitmapFactory.decodeFile(f.getAbsolutePath());
		return ResizeBitmapWithCut(B, DisplayWidth, DisplayHeight);
	}
	
	private static Bitmap __CreateSpriteFromBitmap(Bitmap bmp, float holder_width, float holder_height) {
		float propXY = holder_width / holder_height;

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
		bmp.recycle();
		return bmp_scaled;
	}
	
	public static Bitmap CreateSpriteFromResources(Resources r, int resID, float DisplayWidth, float DisplayHeight) {
		final Bitmap B = BitmapFactory.decodeResource(r, resID);
		return __CreateSpriteFromBitmap(B, DisplayWidth, DisplayHeight);
	}
	
	public static Bitmap CreateSpriteFromFile(String fName, float DisplayWidth, float DisplayHeight) {
		File f = new File(fName);
		if (f.canRead() == false) return null;
		Bitmap B = BitmapFactory.decodeFile(f.getAbsolutePath());
		return __CreateSpriteFromBitmap(B, DisplayWidth, DisplayHeight);
	}
}
