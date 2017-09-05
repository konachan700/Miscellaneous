package com.nekokoneko.microlauncher;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.drawable.Drawable;

public class ResizeImages {
	private Resources XR;

	public ResizeImages(Resources res) {
		XR = res;
	}
	
	public static void SetContrast(ColorMatrix cm, float contrast, float brightness) {
        float a[] = new float[] { 	contrast, 		0, 0, 0, 			brightness, 	0,
        							contrast, 		0, 0, brightness, 	0, 				0, contrast, 0,
        							brightness, 	0, 0, 0, 			1, 				0 
        				   		};
        cm.postConcat(new ColorMatrix(a));
	}
	
	
	public static Bitmap ScaleBitmap(Bitmap bmp, int w, int h) {
		float propXY = w / h;
		float propBXY = ((float)bmp.getWidth()) / ((float)bmp.getHeight());
		float nW, nH;
		
		if (propXY > propBXY) {
			nW = h * propBXY;
			nH = h;
		} else {
			nW = w;
			nH = w / propBXY;
		}
		
		return Bitmap.createScaledBitmap(bmp, (int)nW, (int)nH, true);
	}
	
	public static Bitmap DrawableToBitmap (Drawable drawable, int w, int h) {
	   // if (drawable instanceof BitmapDrawable) {
	    //    return ScaleBitmap(((BitmapDrawable)drawable).getBitmap(), w, h);
	   // }

	    int width = drawable.getIntrinsicWidth();
	    width = width > 0 ? width : 1;
	    int height = drawable.getIntrinsicHeight();
	    height = height > 0 ? height : 1;

	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return ScaleBitmap(bitmap, w, h);
	}
	
	public static Bitmap DrawableToBitmap (Drawable drawable) {
	    //if (drawable instanceof BitmapDrawable) {
	    //    return ((BitmapDrawable)drawable).getBitmap();
	    //}

	    int width = drawable.getIntrinsicWidth();
	    width = width > 0 ? width : 1;
	    int height = drawable.getIntrinsicHeight();
	    height = height > 0 ? height : 1;

	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}

	public Bitmap ConvertSpriteFromBitmap(Bitmap bmp, float holder_width, float holder_height) {
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
		bmp.recycle();
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
		InputImage.recycle();
		Bitmap OutputImage = Bitmap.createBitmap(B, (int) X, (int) Y, (int) DisplayWidth, (int) DisplayHeight);
		B.recycle();
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
		InputImage.recycle();
		Bitmap OutputImage = Bitmap.createBitmap(B, (int) X, (int) Y, (int) DisplayWidth, (int) DisplayHeight);
		B.recycle();
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
