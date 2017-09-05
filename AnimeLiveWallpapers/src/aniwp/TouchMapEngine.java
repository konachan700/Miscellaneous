package aniwp;

import java.util.Properties;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.MotionEvent;

class TouchMapEngine {
	private Bitmap nTouchMap;
	private boolean nDisableEngine = false;
	private float nHolderWidth, nHolderHeight;
	private Properties prop;
	private SpritesEngine nSpritesEngine;
	
	public TouchMapEngine(SpritesEngine se, float HolderWidth, float HolderHeight) {
		if (se == null) {
			nDisableEngine = true;
			return;
		}
		
		nSpritesEngine = se;
		
		nTouchMap = se.GetSprite("touchmap");
		if (nTouchMap == null) { 
			nDisableEngine = true;
			return;
		}
		
		prop = FSEngine.GetTouchMapConfig();
		if (prop == null) {
			prop = new Properties();
			prop.put("b8b8b8", "top");
			prop.put("0000ff", "smile");
			prop.put("008a00", "left");
			prop.put("ff0000", "rage");
			prop.put("00ff00", "right");
			prop.put("ffff00", "bottom");
		}
		
		nHolderHeight = HolderHeight;
		nHolderWidth = HolderWidth;
	}
	
	@SuppressLint("DefaultLocale")
	public String GetEventString(MotionEvent event) {
		if (nDisableEngine == true) return null;
		
		final float x 		= event.getX(),
				    y 		= event.getY(),
				    map_w 	= nTouchMap.getWidth(),
				    map_h 	= nTouchMap.getHeight();
		
		final float xc 		= x - ((nHolderWidth - map_w) / 2),
				    yc		= y - (nHolderHeight - map_h);
		
		if (prop.containsValue("top") == true) {
			if (nSpritesEngine.GetSprite("top") != null)
				if (yc <= 0) return "top";
		}
		
		if (prop.containsValue("left") == true) {
			if (nSpritesEngine.GetSprite("left") != null)
				if (xc <= 0) return "left";
		}
		
		if (prop.containsValue("right") == true) {
			if (nSpritesEngine.GetSprite("right") != null)
				if (xc >= map_w) return "right";
		}
		
		if ((xc <= 0) || (yc <= 0)) return null;
		if ((xc >= map_w) || (yc >= map_h)) return null;
		
		final int 		tColor 	= nTouchMap.getPixel((int) xc, (int) yc);
		final int 		naColor = tColor & (0xFFFFFF);
		final String 	hexStrT	= "Z00000000" + Integer.toHexString(naColor).toLowerCase();
		final String 	hexStr  = hexStrT.substring(hexStrT.length() - 6, hexStrT.length());
		
		System.err.println("TM RAW Event:" + hexStr);
		final String 	ret 	= prop.getProperty(hexStr, null);
			
		return ret;
	}
}

