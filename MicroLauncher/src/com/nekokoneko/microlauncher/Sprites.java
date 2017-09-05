package com.nekokoneko.microlauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nekokoneko.microlauncher.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class Sprites {
	class PreloadedSprite {
		private int 	npResID;
		private float 	npWidth, npHeight;
		private Bitmap 	npImage;
		private boolean nIsFile = false;
		
		public PreloadedSprite(Resources iResources, int iResID, float iWidth, float iHeight) {
			ResizeImages tResizeImages = new ResizeImages(iResources);
			npImage  = tResizeImages.ConvertSpriteFromResource(iResID, iWidth, iHeight);
			npWidth  = npImage.getWidth();
			npHeight = npImage.getHeight();
			npResID  = iResID;
			nIsFile = false;
		}
		
		public PreloadedSprite(Resources iResources, File link, float iWidth, float iHeight) {
			ResizeImages tResizeImages = new ResizeImages(iResources);
			if (link.canRead()) {
				Bitmap tImage  	= BitmapFactory.decodeFile(link.getAbsolutePath());
				npImage 		= tResizeImages.ConvertSpriteFromBitmap(tImage, iWidth, iHeight);
				npWidth  		= npImage.getWidth();
				npHeight 		= npImage.getHeight();
			} else {
				npImage  = null;
				npWidth  = -1;
				npHeight = -1;
			}

			nIsFile  = true;
		}
		
		public void Draw(Canvas iCanvas, float iScreenWidth, float iScreenHeight) {
			float x = (iScreenWidth > npWidth) ? ((iScreenWidth / 2) - (npWidth / 2)) : 0;
			float y = (iScreenHeight > npHeight) ? (iScreenHeight - npHeight) : 0;
			
			Paint tPaint = new Paint();
			tPaint.setAntiAlias(true);
			iCanvas.drawBitmap(npImage, x, y, tPaint);
		}
		
		public int getResID() {
			return npResID;
		}
		
		public float getWidth() {
			return npWidth;
		}
		
		public float getHeight() {
			return npHeight;
		}
	}
	
	class NonPreloadedSprite {
		private int 		npResID;
		private Resources 	nResources;
		
		public NonPreloadedSprite(Resources iResources, int iResID) {
			nResources = iResources;
			npResID = iResID;
		}
		
		public Bitmap Draw(Canvas iCanvas, float iScreenWidth, float iScreenHeight) {
			ResizeImages tResizeImages = new ResizeImages(nResources);
			Bitmap tBitmap = tResizeImages.ConvertSpriteFromResource(npResID, iScreenWidth, iScreenHeight);

			float w = ((float) tBitmap.getWidth());
			float h = ((float) tBitmap.getHeight());
			float x = (iScreenWidth > w)  ? ((iScreenWidth / 2) - (w / 2)) : 0;
			float y = (iScreenHeight > h) ? (iScreenHeight - h) : 0;
			
			Paint tPaint = new Paint();
			tPaint.setAntiAlias(true);
			iCanvas.drawBitmap(tBitmap, x, y, tPaint);

			return tBitmap;
		}
		
		public void DrawBitmap(Bitmap iBitmap, Canvas iCanvas, float iScreenWidth, float iScreenHeight) {
			float w = ((float) iBitmap.getWidth());
			float h = ((float) iBitmap.getHeight());
			float x = (iScreenWidth > w)  ? ((iScreenWidth / 2) - (w / 2)) : 0;
			float y = (iScreenHeight > h) ? (iScreenHeight - h) : 0;
			
			Paint tPaint = new Paint();
			tPaint.setAntiAlias(true);
			iCanvas.drawBitmap(iBitmap, x, y, tPaint);
		}
		
		public int getResID() {
			return npResID;
		}
	}
	
	private float 		nScreenWidth = 0, 
						nScreenHeight = 0;
	
	private String		nCacheName = "";
	
	private Resources 			nResources 				= null;
	private Canvas 				nCanvas 				= null;
	private Bitmap 				nCacheBitmap 			= null;
	private Activity			nActivity				= null;
	private SharedPreferences	nSharedPreferences		= null;
	
	private String 		nRootPath 				= "",
						nSpritesPath 			= "", 
						nOldSprite				= "";
	
	private Map<String, PreloadedSprite> 	nPreloadedSprites 		= new HashMap<String, PreloadedSprite>();
	private Map<String, NonPreloadedSprite> nNonPreloadedSprites 	= new HashMap<String, NonPreloadedSprite>();

	public Sprites(Activity iActivity, Resources res, int w, int h) { 
		nResources 			= res;
		nActivity 			= iActivity;
		nSharedPreferences	= PreferenceManager.getDefaultSharedPreferences(nActivity);
		
		RebuildFSTree();
		__LoadSprites(w, h);
	}
	
	public void RebuildFSTree() {
		File tBaseDir = Environment.getExternalStorageDirectory();
		nRootPath = tBaseDir.getAbsolutePath() + "/.MicroLauncherBeta/";

		nSpritesPath = nRootPath + "chars/";
		new File(nSpritesPath).mkdirs();
	}
	
	public static ArrayList<String> getCharsList() {
		File tBaseDir = Environment.getExternalStorageDirectory();
		String tCharsPath = tBaseDir.getAbsolutePath() + "/.MicroLauncherBeta/chars/";
		
		File f = new File(tCharsPath);
		String s[] = f.list();
		
		if (s.length > 0) {
			ArrayList<String> l = new ArrayList<String>();
			for (int i=0; i<s.length; i++) {
				if (new File(tCharsPath + s[i]).isDirectory()) {
					l.add(s[i]);
					Log.d("======", "s[i]="+tCharsPath+s[i]);
				}
			}
			return l;
		} else {
			f.mkdirs();
			return null;
		}
	}

	private void __LoadSprites(int w, int h) {
		if (nResources != null) {
			nScreenWidth  = w;
			nScreenHeight = h;
			
			nPreloadedSprites.clear();
			nNonPreloadedSprites.clear();
			
			nOldSprite = nSharedPreferences.getString("change_char_list", "default");
			if (nOldSprite.equalsIgnoreCase("default")) {
				nPreloadedSprites.put("normal", 			new PreloadedSprite(nResources, R.drawable.char_normal, 			w, h));
				nPreloadedSprites.put("eye_half_close", 	new PreloadedSprite(nResources, R.drawable.char_eye_half_close, 	w, h));
				nPreloadedSprites.put("eye_full_close", 	new PreloadedSprite(nResources, R.drawable.char_eye_full_close, 	w, h));
			} else {
				String tCurrentSprite = nSharedPreferences.getString("change_char_list", "default");
				nPreloadedSprites.put("normal", 			new PreloadedSprite(nResources, new File(nSpritesPath + tCurrentSprite + "/normal.png"), 			w, h));
				nPreloadedSprites.put("eye_half_close", 	new PreloadedSprite(nResources, new File(nSpritesPath + tCurrentSprite + "/eye_half_close.png"), 	w, h));
				nPreloadedSprites.put("eye_full_close", 	new PreloadedSprite(nResources, new File(nSpritesPath + tCurrentSprite + "/eye_full_close.png"), 	w, h));
			}
		}
	}
	
	public void Draw(Canvas cnv, String name) {
		nCanvas = cnv;
		
		if (name == null) 		return;
		if (nCanvas == null) 	return;
		if ((nScreenHeight == 0) || (nScreenWidth == 0)) return;
		
		String tCurrentSprite = nSharedPreferences.getString("change_char_list", "default");
		if (!tCurrentSprite.equalsIgnoreCase(nOldSprite)) {
			__LoadSprites(Math.round(nScreenWidth), Math.round(nScreenHeight));
			Log.d("Sprites", "All sprites reloaded!");
		}

		if (nPreloadedSprites.containsKey(name)) {
			PreloadedSprite tPreloadedSprite = nPreloadedSprites.get(name);
			if (tPreloadedSprite.getWidth() > 0) tPreloadedSprite.Draw(nCanvas, nScreenWidth, nScreenHeight);
		} else {
			if (nNonPreloadedSprites.containsKey(name)) {
				NonPreloadedSprite tNonPreloadedSprite = nNonPreloadedSprites.get(name);
				if ((name.contentEquals(nCacheName)) && (nCacheBitmap != null)) {
					tNonPreloadedSprite.DrawBitmap(nCacheBitmap, nCanvas, nScreenWidth, nScreenHeight);
				} else {
					nCacheBitmap = tNonPreloadedSprite.Draw(nCanvas, nScreenWidth, nScreenHeight);
					nCacheName = name;
				}
			}
		}
	}
}
