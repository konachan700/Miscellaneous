package com.nekokoneko.microlauncher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Shader.TileMode;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class LauncherButtons implements GestureDetector.OnGestureListener {
	class ButtonArea {
		private int x1=0, y1=0, x2=0, y2=0, ID=0;
		public ButtonArea() {}
		public ButtonArea(int x_1, int y_1, int x_2, int y_2, int iID) {
			x1 = x_1;
			x2 = x_2;
			y1 = y_1;
			y2 = y_2;
			ID = iID;
		}

		public int getWidth() 							{ return (x2 - x1); 						}
		public int getHeight() 							{ return (y2 - y1); 						}
		public int getX()								{ return x1; 								}
		public int getID()								{ return ID; 								}
		public int getY()								{ return y1; 								}
		public int getCenterX()							{ return ((x2 / 2) - (x1 / 2) + x1); 		}
		public int getCenterY()							{ return ((y2 / 2) - (y1 / 2) + y1); 		}
		public int getCenterXForObject(int w)			{ return (((x2 - x1) / 2) - (w / 2)) + x1; 	}
		public int getCenterYForObject(int h)			{ return (((y2 - y1) / 2) - (h / 2)) + y1; 	}

		public Rect getRect(int x, int y) {
			Rect tRect = new Rect();
			tRect.set(x1+x, y1+y, x2+x, y2+y);
			return tRect;
		}
		
		public RectF getRectF(int x, int y) {
			RectF tRectF = new RectF();
			tRectF.set(x1+x, y1+y, x2+x, y2+y);
			return tRectF;
		}
		
		public ButtonArea getAreaForText(int x, int y) {
			if (this.getWidth() < this.getHeight()) {
				return new ButtonArea(x1+x, (y1 + this.getWidth())+y, x2+x, y2+y, ID);
			} else if (this.getWidth() > this.getHeight()) {
				return new ButtonArea((x1 + this.getHeight())+x, y1+y, x2+x, y2+y, ID);
			} else {
				return null;
			}
		}
		
		public ButtonArea getArea(int x, int y) {
			ButtonArea ba = new ButtonArea(x1+x, y1+y, x2+x, y2+y, ID);
			return ba;
		}
		
		public ButtonArea getAreaForIcon(int x, int y) {
			if (this.getWidth() < this.getHeight()) {
				return new ButtonArea(x1+x, y1+y, x2+x, (this.getWidth() + y1)+y, ID);
			} else if (this.getWidth() > this.getHeight()) {
				return new ButtonArea(x1+x, y1+y, (this.getHeight() + x1)+x, y2+y, ID);
			} else {
				return new ButtonArea(x1+x, y1+y, x2+x, y2+y, ID);
			}
		}

		public boolean isPointInThisArea(int x, int y) {
			//Log.d("isPointInThisArea", "x="+x+" y="+y+" x1="+x1+" y1="+y1+" x2="+x2+" y2="+y2);
			if ( ((x1 <= x) && (x2 >= x)) && ((y1 <= y) && (y2 >= y)) ) {
				return true;
			}
			return false;
		}
		
		public void setID(int id) {
			ID = id;
		}
	}
	
	class ButtonAreaList {
		private ArrayList<ButtonArea> nButtonAreas = new ArrayList<ButtonArea>();
		
		private int   nFrameWidth 	= 0,
				      nFrameHeight  = 0,
				      nButtonWidth  = 0,
				      nButtonHeight = 0, 
				      nSpacerX		= 0,
				      nSpacerY		= 0,
				      nRowsCount	= 0,
				      nColsCount	= 0;
		
		public ButtonAreaList(int iFrameWidth, int iFrameHeight, int iRowsCount, int iColsCount/*, int iSpacerX, int iSpacerY*/) {
			nFrameWidth 	= iFrameWidth;
			nFrameHeight 	= iFrameHeight;
			nRowsCount 		= iRowsCount;
			nColsCount 		= iColsCount;
			
			nButtonWidth    = Math.round((nFrameWidth / nColsCount) * 1.0f);  // Какого черта тут получается 1.15f я не знаю, но именно так оно работает.
			nButtonHeight   = Math.round((nFrameHeight / nRowsCount) * 1.0f); // по идее оно должно вылезти за границы экрана, чего в реальности не происходит
			nSpacerX 		= Math.round(((nFrameWidth  * 0.15f)  / nColsCount));
			nSpacerY 		= Math.round(((nFrameHeight * 0.15f)  / nRowsCount));
			
			Log.d("ButtonAreaList init", "nFrameWidth="+nFrameWidth+" nFrameHeight="+nFrameHeight+ " nButtonWidth="+nButtonWidth+" nButtonHeight="+nButtonHeight+" nSpacerX="+nSpacerX+" nSpacerY="+nSpacerY);

			int tTotalCounter = 0;

			for (int r=0; r<nRowsCount; r++) {
				for (int c=0; c<nColsCount; c++) {
					ButtonArea ba = new ButtonArea((c * nButtonWidth) + nSpacerX, (r * nButtonHeight) + nSpacerY, (c + 1) * nButtonWidth, (r + 1) * nButtonHeight, tTotalCounter);
					nButtonAreas.add(ba);
					tTotalCounter++;
				}
			}
		}
		
		public int getCellsCount() 	{ return nRowsCount * nColsCount; 	}
		public int getRowsCount() 	{ return nRowsCount; 				}
		public int getColsCount() 	{ return nColsCount; 				}
		public int getWidth()		{ return nFrameWidth; 				}
		public int getHeight()		{ return nFrameHeight; 				}
		public int getCellWidth()	{ return nButtonWidth; 				}
		public int getCellHeight()	{ return nButtonHeight; 			}
		public int getIconWidth()	{ return (nButtonWidth > nButtonHeight) ? nButtonHeight : nButtonWidth; }
		public int getIconHeight()	{ return (nButtonWidth < nButtonHeight) ? nButtonWidth : nButtonHeight; }
		
		public ButtonArea getButtonArea(int index) {
			if (index > (nButtonAreas.size() - 1)) return null;
			return nButtonAreas.get(index);
		}

		public int getIndexFromXY(int x, int y) {
			for (ButtonArea ba : nButtonAreas) {
				if (ba.isPointInThisArea(x, y)) return ba.getID();
			}
			return -1;
		}
	}
	
	class ButtonIcon {
		private Bitmap nImage = null;
		private String nText  = "", nAppPackage = "";
		private Paint  nPaintForText, nPaintForIcon, nPaintForIconBGFill, nPaintForIconBGStroke;
		
		private int nH, nW, nFontSize = 16, nDisableBg = 0;
		float nScaleValue = 0.70f;
		
		private void _CreatePaintObjects() {
			nPaintForIconBGFill = new Paint(Paint.ANTI_ALIAS_FLAG);
			//nPaintForIconBGFill.setStyle(Paint.Style.FILL);
			//nPaintForIconBGFill.setColor(0x77FFFFFF);
			nPaintForIconBGFill.setShadowLayer(3f, 2, 2, 0xFFFFFFFF);
					
			nPaintForIconBGStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
			nPaintForIconBGStroke.setStyle(Paint.Style.STROKE);
			nPaintForIconBGStroke.setColor(0x77997755); // 0x99999999
			nPaintForIconBGStroke.setStrokeWidth(1.2f);
			//nPaintForIconBGStroke.setShadowLayer(3f, 3, 3, 0xAA000000);
			
			ColorMatrix tColorMatrix1 = new ColorMatrix();

			int sat = Integer.valueOf(nSharedPreferences.getString("opt_icon_saturation", "20")).intValue();
			tColorMatrix1.setSaturation(sat / 100f); //(0.2f);
			//if (sat == 0) {
			//	ColorMatrix tColorMatrix2 = new ColorMatrix();
			//	tColorMatrix2.setScale(1f, 1f, 0.80f, 0.60f);
			//	tColorMatrix1.setConcat(tColorMatrix2, tColorMatrix1);
			//}
			
			boolean is_contrast = nSharedPreferences.getString("opt_change_icon_contrast", "OK").equalsIgnoreCase("OK");
			if (is_contrast) ResizeImages.SetContrast(tColorMatrix1, 1.15f, -4.99f);
			ColorMatrixColorFilter tColorMatrixColorFilter = new ColorMatrixColorFilter(tColorMatrix1);
			
			nPaintForIcon = new Paint(Paint.ANTI_ALIAS_FLAG);
			//nPaintForIcon.setShadowLayer(2.5f, 2f, 2f, 0xAA000000);
			nPaintForIcon.setColorFilter(tColorMatrixColorFilter);
			
			nPaintForText = new Paint(Paint.ANTI_ALIAS_FLAG);
			nPaintForText.setColor(Color.WHITE);
			nPaintForText.setTextSize(16);
			nPaintForText.setTextAlign(Align.CENTER);
			nPaintForText.setShadowLayer(2f, 1, 1, Color.BLACK);
		}
		
		public ButtonIcon(Bitmap b, String iText, String tAppPackage, int iw, int ih) {
			nAppPackage = tAppPackage;
			
			boolean is_draw_bg = nSharedPreferences.getString("opt_draw_bg_for_icons", "OK").equalsIgnoreCase("OK");
			if (!is_draw_bg) nScaleValue = 0.80f;
			
			nW = Math.round(iw*nScaleValue);
			nH = Math.round(ih*nScaleValue);
			nImage = ResizeImages.ScaleBitmap(b, nW, nH);
			nText = iText;
			_CreatePaintObjects();
		}
		
		public ButtonIcon(Drawable d, String iText, String tAppPackage, int iw, int ih) {
			nAppPackage = tAppPackage;
			nW = Math.round(iw*nScaleValue);
			nH = Math.round(ih*nScaleValue);
			nImage = ResizeImages.DrawableToBitmap(d, nW, nH);
			nText = iText;
			_CreatePaintObjects();
		}
		
		public void DisableBackground() {
			nDisableBg = 1;
		}
		
		public void Draw(ButtonArea iMainButtonArea, Canvas c, int x, int y) {
			ButtonArea ba = iMainButtonArea.getAreaForIcon(x, y);

			if (nDisableBg == 0) {
				LinearGradient tLinearGradient = new LinearGradient(ba.getX(), ba.getY(), ba.getX() + ba.getWidth(), ba.getY() + ba.getHeight(),
						0x77012a2a, 0x334f8e8f, TileMode.CLAMP); //444444  AAAAAA
				nPaintForIconBGFill.setShader(tLinearGradient);
				
				//nPaintForIconBGFill.setShadowLayer(3f, 2, 2, 0xAA000000); // WTF? Цвет тени такой же, как у градиента... 
				// тут даже нашел аналогичный случай http://stackoverflow.com/questions/7330941/textview-adding-gradient-and-shadow
				// обошелся дизайнерским костылем, поменяв цвета местами =)
				boolean is_draw_bg = nSharedPreferences.getString("opt_draw_bg_for_icons", "OK").equalsIgnoreCase("OK");
				if (is_draw_bg) {
					if (c != null) c.drawRoundRect(ba.getRectF(0, 0), 9f, 9f, nPaintForIconBGFill);
					if (c != null) c.drawRoundRect(ba.getRectF(0, 0), 9f, 9f, nPaintForIconBGStroke);
				}
			} else {
				nPaintForIcon.setAlpha(190);
			}
			
			if (!nImage.isRecycled()) if (c != null) c.drawBitmap(nImage, ba.getCenterXForObject(nW), ba.getCenterYForObject(nH), nPaintForIcon);

			ButtonArea bt = iMainButtonArea.getAreaForText(x, y);
			if (bt != null) TransparentTextBox.DrawMultilineText(c, nText, bt.getWidth(), (nFontSize*3), bt.getCenterX(), bt.getY()+ nFontSize, (nFontSize / 2), nPaintForText);
		}
		
		public int getWidth()	{ return nW; }
		public int getHeight()	{ return nH; }
		public String getTitle() { return nText; }
		public String getAppPackage() { return nAppPackage; }
	}
	
	class ButtonList {
		private CopyOnWriteArrayList<ButtonIcon> 	nButtonIcons 		= new CopyOnWriteArrayList<ButtonIcon>();
		private ButtonAreaList 						nButtonAreaList 	= null;
		
		private int   nFrameWidth 	= 0,
				      nFrameHeight  = 0,
				      nFrameX		= 0,
				      nFrameY		= 0,
				      nFrameBorder  = 10, 
				      nRowsCount    = 0, 
				      nColsCount	= 0,
				      nButtonWidth  = 0,
				      nButtonHeight = 0,
				      nBorderWidth  = 8, 
				      nDisableBg	= 0;
		
		private Paint nPaintForIconBGFill, nPaintForIconBGStroke;
		
		public ButtonList(int iFrameX, int iFrameY, int iFrameWidth, int iFrameHeight, int iRowsCount, int iColsCount/*, int iSpacerX, int iSpacerY*/) {
			nFrameWidth 	= iFrameWidth;
			nFrameHeight 	= iFrameHeight;
			nFrameX 		= iFrameX;
			nFrameY 		= iFrameY;
			nColsCount		= iColsCount;
			nRowsCount      = iRowsCount;
			
			nButtonAreaList = new ButtonAreaList(nFrameWidth - (nFrameBorder * 2), nFrameHeight - (nFrameBorder * 2), iRowsCount, iColsCount/*, iSpacerX, iSpacerY*/);
			
			nButtonWidth    = nButtonAreaList.getIconWidth();
			nButtonHeight   = nButtonAreaList.getIconWidth();
			
			nPaintForIconBGFill = new Paint(Paint.ANTI_ALIAS_FLAG);
			nPaintForIconBGFill.setStyle(Paint.Style.FILL);
			nPaintForIconBGFill.setColor(0x88000000); //88FFFFFF
			
			nPaintForIconBGStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
			nPaintForIconBGStroke.setStyle(Paint.Style.STROKE);
			nPaintForIconBGStroke.setColor(0xAA000000);
			nPaintForIconBGStroke.setStrokeWidth(nBorderWidth);
		}
		
		public void DisableBackground() {
			nDisableBg = 1;
		}
		
		public void Draw(Canvas c) {
			if (nDisableBg == 0) {
			    if (c != null) c.drawRect(nFrameX, nFrameY, nFrameX + nFrameWidth, nFrameY + nFrameHeight - nBorderWidth, nPaintForIconBGFill);
			    if (c != null) c.drawRect(nFrameX - nBorderWidth, nFrameY - (nBorderWidth / 2), nFrameX + nFrameWidth + nBorderWidth * 2, nFrameY + nFrameHeight - (nBorderWidth / 2), nPaintForIconBGStroke);
			}
			
		    if (!nDrawLocked) {
		    	int tCounter = 0;
			    for (ButtonIcon bi : nButtonIcons) {
			    	if (!nDrawLocked) bi.Draw(nButtonAreaList.getButtonArea(tCounter), c, nFrameX, nFrameY);
			    	tCounter++;
			    }
		    }
		}
		
		public int getTouchIndex(int x, int y) {
			final int tCount = nButtonAreaList.getCellsCount();
			for (int i=0; i<tCount; i++) {
				ButtonArea ba = nButtonAreaList.getButtonArea(i).getArea(nFrameX, nFrameY); 
				if (ba.isPointInThisArea(x, y)) {
					return i;
				}
			}
			
			return -1;
		}

		public int getFrameX() 			{ return nFrameX; 			}
		public int getFrameY() 			{ return nFrameY; 			}
		public int getFrameWidth() 		{ return nFrameWidth; 		}
		public int getFrameHeight() 	{ return nFrameHeight; 		}
		
		
		public int getButtonHeight() {
			return nButtonHeight;
		}
		
		public int getButtonWidth() {
			return nButtonWidth;
		}
		
		public void AddButton(ButtonIcon bi) {
			if (bi != null)
				if (nButtonIcons.size() < (nColsCount * nRowsCount)) nButtonIcons.add(bi);
		}
		
		public void AddButton(Bitmap b, String iText, String iAppPackage) {
			ButtonIcon bi = new ButtonIcon(b, iText, iAppPackage, nButtonWidth, nButtonHeight);
			if (nButtonIcons.size() < (nColsCount * nRowsCount)) nButtonIcons.add(bi);
		}
		
		public void AddButton(Drawable d, String iText, String iAppPackage) {
			ButtonIcon bi = new ButtonIcon(d, iText, iAppPackage, nButtonWidth, nButtonHeight);
			if (nButtonIcons.size() < (nColsCount * nRowsCount)) nButtonIcons.add(bi);
		}
		
		public void RemoveButton(int index) {
			if ((index >= 0) && (index < nButtonIcons.size())) nButtonIcons.remove(index);
		}
		
		public void RemoveAllButtons() {
			nButtonIcons.clear();
		}
		
		public int getCount() {
			return nButtonIcons.size();
		}
		
		public ButtonIcon getButton(int index) {
			if ((index < 0) || (index >= nButtonIcons.size())) return null;
			return nButtonIcons.get(index);
		}
	}
	
	class ButtonsForAppGroups {
		private ButtonAreaList 		nButtonAreaList 	= null;
		private ResizeImages		nResizeImages       = null;
		
		private int 				nW 					= 0, 
									nH 					= 0, 
									nCols 				= 10, 
									nRows 				= 1;
		
		private ArrayList<Bitmap> 	iconR 				= new ArrayList<Bitmap>(), 
									iconL 				= new ArrayList<Bitmap>();
		
		private Paint 				nPaintForIcon 			= null;

		public ButtonsForAppGroups(int sw, int sh, Resources r) {
			nW    = sw;
			nH    = Math.round(sw * 0.15f);
			nCols = Math.round(nW / nH);

			nResizeImages    = new ResizeImages(r);
			nButtonAreaList  = new ButtonAreaList(nW, nH, nRows, nCols);
			
			iconL.add(nResizeImages.ConvertSpriteFromResource(R.drawable.icon_favourite, nH, nH));
			iconL.add(nResizeImages.ConvertSpriteFromResource(R.drawable.icon_allapps, nH, nH));
			iconL.add(nResizeImages.ConvertSpriteFromResource(R.drawable.icon_trash, nH, nH));
			
			iconR.add(nResizeImages.ConvertSpriteFromResource(R.drawable.icon_close_back, nH, nH));
			
			nPaintForIcon = new Paint(Paint.ANTI_ALIAS_FLAG);
			nPaintForIcon.setAlpha(180);
		}
		
		public void Draw(Canvas c, int x, int y) {
			int tLRCounter = 0;
			final int tBALCount = nButtonAreaList.getCellsCount();
			for (int i=0; i<tBALCount; i++) {
				final ButtonArea ba = nButtonAreaList.getButtonArea(i).getArea(x, y);
				final int xt = ba.getX(),
						  yt = ba.getY();
				
				if (c != null) c.drawBitmap(iconL.get(tLRCounter), xt, yt, nPaintForIcon);

				tLRCounter++;
				if (tLRCounter >= iconL.size()) break;
			}
			
			tLRCounter = 0;
			for (int i=(tBALCount-1); i>=0; i--) {
				final ButtonArea ba = nButtonAreaList.getButtonArea(i).getArea(x, y);
				final int xt = ba.getX(),
						  yt = ba.getY();
				
				if (c != null) c.drawBitmap(iconR.get(tLRCounter), xt, yt, nPaintForIcon);

				tLRCounter++;
				if (tLRCounter >= iconR.size()) break;
			}
		}
		
		public int getTouchIndex(int x, int y, int xpos, int ypos) {
			final int tBALCount = nButtonAreaList.getCellsCount();
			for (int i=0; i<tBALCount; i++) {
				ButtonArea ba = nButtonAreaList.getButtonArea(i).getArea(xpos, ypos);
				if (ba.isPointInThisArea(x, y)) return (i+1);
			}
			return -1;
		}
		
		public int getMaxTouchIndex() {
			return nButtonAreaList.getCellsCount();
		}
	}
	
	class NotOpenButtonsBlock {
		private ButtonAreaList 		nButtonAreaList 		= null;
		private ResizeImages		nResizeImages       	= null;
		
		private int 				nW 						= 0, 
									nH 						= 0, 
									nSW						= 0,
									nSH						= 0,
									nCols 					= 1, 
									nRows 					= 4,
									nX						= 0,
									nY						= 0;
		
		private ArrayList<Bitmap> 	nIcons 					= new ArrayList<Bitmap>();
		
		private Paint 				nPaintForIcon 			= null;
		
		
		public NotOpenButtonsBlock(int sw, int sh, Resources r) {
			nW    = Math.round(sw * 0.14f);
			nH    = Math.round((nW * 1.5f) * nRows);
			nSW   = sw;
			nSH	  = sh;
			nX 	  = Math.round(nSW * 0.06f);
			nY    = nSH - nH - Math.round(nSH * 0.085f);

			nResizeImages    = new ResizeImages(r);
			nButtonAreaList  = new ButtonAreaList(nW, nH, nRows, nCols);
			
			nPaintForIcon = new Paint(Paint.ANTI_ALIAS_FLAG);
			nPaintForIcon.setAlpha(210);
			
			nIcons.add(nResizeImages.ConvertSpriteFromResource(R.drawable.icon_apps, nW, nW));
			nIcons.add(nResizeImages.ConvertSpriteFromResource(R.drawable.icon_lights, nW, nW));
			nIcons.add(nResizeImages.ConvertSpriteFromResource(R.drawable.icon_wifiap, nW, nW));
		}
		
		public void Draw(Canvas c) {
			int tCounter=0;
			for (int i=(nButtonAreaList.getCellsCount()-1); i>=0; i--) {
				ButtonArea ba = nButtonAreaList.getButtonArea(i).getAreaForIcon(nX, nY); 
				if (c != null) c.drawBitmap(nIcons.get(tCounter), ba.getX(), ba.getY(), nPaintForIcon); 
				
				tCounter++;
				if (tCounter >= nIcons.size()) {
					break;
				}
			}
		}
		
		public int getTouchIndex(int x, int y) {
			final int tBALCount = nButtonAreaList.getCellsCount();
			for (int i=0; i<tBALCount; i++) {
				ButtonArea ba = nButtonAreaList.getButtonArea(i).getArea(nX, nY);
				if (ba.isPointInThisArea(x, y)) return (tBALCount - i);
			}
			return -1;
		}
	}
	
	public class DummySurfaceView extends SurfaceView {
		public DummySurfaceView(Context context) {
			super(context);
			this.setBottom(101);
			this.setTop(100);
			this.setLeft(100);
			this.setRight(101);
			this.setVisibility(1);
		}
	}
	
	private ButtonsForAppGroups				nButtonsForAppGroups	= null;
	private ButtonList						nCurrentButtonList      = null;
	private ButtonList						nTopButtonList      	= null;
	private NotOpenButtonsBlock				nNotOpenButtonsBlock	= null;
	private Context 			  			nContext 				= null;
	private ApplicationManager    			nApplicationManager   	= null;
	private GestureDetectorCompat 			nDetector				= null; 
	private Vibrator						nVibrator				= null;
	private Resources						nResources				= null;
	private Activity						nActivity				= null;
	private SharedPreferences				nSharedPreferences		= null;
	private ResizeImages					nResizeImagesX			= null;
	private Camera 							nCamera					= null;

	private int 							nW, 
											nH,
											nCols 					= 5,
											nRows					= 3,
											nPage					= 0,
											nMyYPosition			= 0,
											nActiveTag				= 0,
											nCurrentAppIndex		= 0;
	
	private boolean							nPageChanged            = true,
											nIsOpened				= false,
											nDrawLocked				= false,
											nLigtsOn				= false;
	
	public LauncherButtons(Activity iActivity, Context iContext, int w, int h) {
		nW 				= w;
		nH 				= h;
		nMyYPosition 	= Math.round((nH * 0.5f) - (nH * 0.1f));

		nContext 				= iContext;
		nActivity				= iActivity;
		nResources				= nContext.getResources();
		nApplicationManager 	= new ApplicationManager(nContext);
		nCurrentButtonList 		= new ButtonList(0, nH / 2, nW, nH / 2, 3, 5);
		nTopButtonList			= new ButtonList(0, 30, nW, nH / 7, 1, 5);
		nButtonsForAppGroups    = new ButtonsForAppGroups(w, h, nResources);
		nNotOpenButtonsBlock	= new NotOpenButtonsBlock(w, h, nResources);
		nVibrator 				= (Vibrator) nContext.getSystemService(Context.VIBRATOR_SERVICE);
		nDetector 				= new GestureDetectorCompat(nContext, this);
		nSharedPreferences		= PreferenceManager.getDefaultSharedPreferences(nActivity);
		nResizeImagesX			= new ResizeImages(nResources);
		
		nTopButtonList.DisableBackground();
		__generate5Icons();
	}
	
	public void onPause() {
		if (nCamera != null) {
			nCamera.stopPreview();
			nCamera.release();
			nCamera = null;
			nLigtsOn = false;
		}
	}
	
	public void onResume() {
		final int mod = nSharedPreferences.getInt("modification", 0);
		
		if (nCurrentAppIndex < 9000) {
			final int appCount1 = nApplicationManager.getApplicationsTotalCount();
			
			if (mod == 1) {
				nApplicationManager.SetTag(nCurrentAppIndex, nSharedPreferences.getString("application_tag", "0"), nSharedPreferences.getString("application_rename", "0"));
				nApplicationManager.ReloadLast();
			}
			
			final int appCount2 = nApplicationManager.getApplicationsTotalCount();
			if (appCount1 != appCount2) {
				final int pc = appCount2 / (nCols * nRows);
				final int rv = (nCols * nRows) * pc;
				if (appCount2 == rv) {
					nPage--;
				}
			}
		} else {
			if (mod == 1) {
				nApplicationManager.SetTag(nCurrentAppIndex, nSharedPreferences.getString("application_tag", "0"), nSharedPreferences.getString("application_rename", "0"));
				nApplicationManager.ReloadLast();
			}
		}

		Editor e = nSharedPreferences.edit();
		e.putInt("modification", 0);
		e.commit();

		nApplicationManager.RefreshApplicationList(nActiveTag, nPage * nCols * nRows, (nPage + 1) * nCols * nRows);

		GenerateApplicationPage();
		__generate5Icons();
	}
	
	private void __generate5Icons() {
		nApplicationManager.Reload5ApplicationList();
		nTopButtonList.RemoveAllButtons();
		for (int i=0; i<5; i++) {
			if ((i < nApplicationManager.get5ApplicationsCount()) && (nApplicationManager.get5ApplicationsCount() > 0)) {
				ButtonIcon bi = new ButtonIcon(nApplicationManager.get5ApplicationIcon(i), "", nApplicationManager.get5ApplicationPackage(i), nTopButtonList.getButtonWidth(), nTopButtonList.getButtonHeight());
				bi.DisableBackground();
				nTopButtonList.AddButton(bi); 
			} else {
				nTopButtonList.AddButton(nResizeImagesX.ConvertSpriteFromResource(R.drawable.icon_noapp, nTopButtonList.getButtonWidth(), nTopButtonList.getButtonHeight()), "", "empty");
			}
		}
	}
	
	public void GenerateApplicationPage() {
		nCurrentButtonList.RemoveAllButtons();
		if (nApplicationManager.getApplicationsCount() <= 0) return;
		
		for (int i=0; i<(nCols * nRows); i++) {
			ButtonIcon bi = new ButtonIcon(nApplicationManager.getApplicationIcon(i), nApplicationManager.getApplicationTitle(i), 
					nApplicationManager.getApplicationPackage(i), nCurrentButtonList.getButtonWidth(), nCurrentButtonList.getButtonHeight()); 
			nCurrentButtonList.AddButton(bi);

			if (i >= (nApplicationManager.getApplicationsCount()-1)) break;
		}
	}
	
	public int getCurrentPage() {
		return nPage;
	}
	
	public int getPagesCount() {
		int pc = nApplicationManager.getApplicationsTotalCount() / (nCols * nRows);
		int rev = (nCols * nRows) * pc;
		if (nApplicationManager.getApplicationsTotalCount() == rev) pc--; /* сам не до конца понял смысл сего участка кода, но без него зависает скролл */

		return pc;
	}
	
	public void Draw(Canvas c) {
		nCurrentButtonList.Draw(c);
		nButtonsForAppGroups.Draw(c, 0, nMyYPosition);
	}
	
	public void DrawOnNotOpened(Canvas c) {
		nNotOpenButtonsBlock.Draw(c);
	}
	
	public void DrawNonClosable(Canvas c) {
		nTopButtonList.Draw(c);
	}
	
	public void Vibro(int arr[]) {
		boolean pause = false;
		for (int i=0; i<arr.length; i++) {
			if (pause) {
				try { Thread.sleep(arr[i]); } catch (InterruptedException e) { }
			} else {
				nVibrator.vibrate(arr[i]); 
			}
			pause = !pause;
		}
	}
	
	public boolean IsOpened() {
		return nIsOpened;
	}
	
	public void Close() {
		nIsOpened = false;
	}
	
	public void Open() {
		nIsOpened = true;
	}
	
	public void onTouchEvent(MotionEvent event) {
		nDetector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			nPageChanged = true;
		}
	}
	
	public void onMenuButtonPress() {
		Intent tIntent = new Intent(nContext, ApplicationMenu.class);
		tIntent.putExtra("DialogType", 1);
		nActivity.startActivity(tIntent);
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		final int tTopButtonsAction = nTopButtonList.getTouchIndex(Math.round(arg0.getX()), Math.round(arg0.getY()));
		if (tTopButtonsAction >= 0) {
			Vibro(new int[] {23, 88, 43});
			if (tTopButtonsAction >= nTopButtonList.getCount()) return;
			Intent tIntent = new Intent(nContext, ApplicationMenu.class);
			nCurrentAppIndex = 9000 + tTopButtonsAction;
			
			Editor e = nSharedPreferences.edit();
			e.putInt("modification", 1);
			e.putString("application_tag", nApplicationManager.get5ApplicationTag(tTopButtonsAction) + "");
			e.putString("application_rename", nApplicationManager.get5ApplicationTitle(tTopButtonsAction));
			e.commit();
			
			nActivity.startActivity(tIntent);
		}
		
		if (nIsOpened) {
			Vibro(new int[] {23, 88, 43});
			final int tActionIndex = nCurrentButtonList.getTouchIndex(Math.round(arg0.getX()), Math.round(arg0.getY())); 
			if (tActionIndex >= 0) {
				//Log.d("onLongPress", "getApplicationTitle="+nApplicationManager.getApplicationTitle(tActionIndex)+ " nCurrentAppIndex="+nCurrentAppIndex);
				if (tActionIndex >= nCurrentButtonList.getCount()) return;
				nCurrentAppIndex = tActionIndex;
	
				Intent tIntent = new Intent(nContext, ApplicationMenu.class);

				Editor e = nSharedPreferences.edit();
				e.putInt("modification", 1);
				e.putString("application_tag", nApplicationManager.getApplicationTag(tActionIndex) + "");
				e.putString("application_rename", nApplicationManager.getApplicationTitle(tActionIndex));
				e.commit();
				
				nActivity.startActivity(tIntent);
			}
		}
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		if (nPageChanged) {
			if (arg0.getY() < (nH / 2)) return false;
			if (!nIsOpened) return false;
				
			if (arg0.getX() < arg1.getX()) {
				nPage--;
				if (nPage < 0) {
					nPage = 0;
				} else {
					Vibro(new int[] {25, 50, 25, 50, 25});
					nApplicationManager.RefreshApplicationList(nActiveTag, nPage * nCols * nRows, (nPage + 1) * nCols * nRows);
					GenerateApplicationPage();
				}
			} else if (arg0.getX() > arg1.getX()) {
				nPage++;
				if (nPage > getPagesCount()) {
					nPage = getPagesCount();
				} else {
					Vibro(new int[] {25, 50, 25, 50, 25});
					nApplicationManager.RefreshApplicationList(nActiveTag, nPage * nCols * nRows, (nPage + 1) * nCols * nRows);
					GenerateApplicationPage();
				}
			}
			
			nPageChanged = false;
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent z) {
		Log.d("onSingleTapUp", "X="+Math.round(z.getX())+" Y="+ Math.round(z.getY()));
		
		final int tTopButtonsAction = nTopButtonList.getTouchIndex(Math.round(z.getX()), Math.round(z.getY()));
		if (tTopButtonsAction >= 0) {
			Intent tIntent = nApplicationManager.getPM().getLaunchIntentForPackage(nApplicationManager.get5ApplicationPackage(tTopButtonsAction)); 
			nContext.startActivity(tIntent);
		}
		
		if (nIsOpened) {
			final int tActionIndex = nCurrentButtonList.getTouchIndex(Math.round(z.getX()), Math.round(z.getY())); 
			if (tActionIndex >= 0) {
				Intent tIntent = nApplicationManager.getPM().getLaunchIntentForPackage(nApplicationManager.getApplicationPackage(tActionIndex)); 
				nContext.startActivity(tIntent);
			}
			
			final int tBFAEvent = nButtonsForAppGroups.getTouchIndex(Math.round(z.getX()), Math.round(z.getY()), 0, nMyYPosition);
			if (tBFAEvent >= 0) {
				if (tBFAEvent == 1) {
					Vibro(new int[] {43, 88, 43});
					nActiveTag = 43;
					nPage = 0;
					nApplicationManager.ReloadApplicationList(nActiveTag);
					nApplicationManager.RefreshApplicationList(nActiveTag, nPage * nCols * nRows, (nPage + 1) * nCols * nRows);
					GenerateApplicationPage();
				}
				
				if (tBFAEvent == 2) {
					Vibro(new int[] {43, 88, 43});
					nActiveTag = 0;
					nPage = 0;
					nApplicationManager.ReloadApplicationList(nActiveTag);
					nApplicationManager.RefreshApplicationList(nActiveTag, nPage * nCols * nRows, (nPage + 1) * nCols * nRows);
					GenerateApplicationPage();
				}
				
				if (tBFAEvent == 3) {
					Vibro(new int[] {43, 88, 43});
					nActiveTag = 1;
					nPage = 0;
					nApplicationManager.ReloadApplicationList(nActiveTag);
					nApplicationManager.RefreshApplicationList(nActiveTag, nPage * nCols * nRows, (nPage + 1) * nCols * nRows);
					GenerateApplicationPage();
				}
				
				if (tBFAEvent == nButtonsForAppGroups.getMaxTouchIndex()) {
					Vibro(new int[] {43, 88, 43});
					Close();
				}
			}
		} else {
			final int tNOEvent = nNotOpenButtonsBlock.getTouchIndex(Math.round(z.getX()), Math.round(z.getY()));
			if (tNOEvent >= 0) {
				if (tNOEvent == 1) {
					nApplicationManager.RefreshApplicationList(nActiveTag, nPage * nCols * nRows, (nPage + 1) * nCols * nRows);
					GenerateApplicationPage();
					Vibro(new int[] {43, 88, 43});
					Open();
				}
				
				if (tNOEvent == 2) {
					if (!nLigtsOn) {
						Vibro(new int[] {23, 88, 23});
						nCamera = Camera.open();
 		    			Camera.Parameters cp = nCamera.getParameters();
 		    			nLigtsOn = true;
 		    			cp.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
 		    			nCamera.setParameters(cp);
 		    			try {
							nCamera.setPreviewTexture(new SurfaceTexture(0));
						} catch (IOException e) { e.printStackTrace(); }
 		    			nCamera.startPreview();
					} else {
						Vibro(new int[] {23, 88, 23});
						nCamera.stopPreview();
						nCamera.release();
						nCamera = null;
						nLigtsOn = false;
					}
				}
				
				if (tNOEvent == 3) {
					__runApp("com.android.settings", "com.android.settings.TetherSettings");
				}
				
			}
		}

		return false;
	}
	
	public boolean isLigtsOn() {
		return nLigtsOn;
	}
	
	private void __runApp(String pkgName, String activityName) {
		Intent nIntent = new Intent();
		nIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		nIntent.setClassName(pkgName, activityName);
		nContext.startActivity(nIntent);
	}
}
