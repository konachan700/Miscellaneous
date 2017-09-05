package com.nekokoneko.microlauncher;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.net.TrafficStats;
import android.text.format.DateUtils;
import android.text.format.Time;

public class TimeDateWidget {
	private class NRect {
		public int x=0, y=0, w=0, h=0, fontsize=0;
		public NRect() {}
	}

	private Typeface /*nTypeface1 = null,*/ nTypeface2 = null;
	private int nScreenWidth, /*nScreenHeight,*/ nBatteryLevel = 0; //, nCachedFontSizeForTime = -1;
	private Context nContext = null;
	
	private static final int SPACE_SIZE_FOR_TIME = 4, 
							 SPACE_SIZE_FOR_INFO = 3,
							 FONT_SIZE_ON_START  = 12,
							 FONT_COLOR_ALPHA	 = 177,
							 FONT_VSPACE		 = 15,
							 FONT_HSPACE		 = 20/*, 
							 XX1_DEBUG			 = 1*/;
	
	private static final float SPACE_SIZE_FOR_NETSTAT = 2.5f;
	
	public TimeDateWidget(Context context, int w, int h) {
		//nTypeface1 = Typeface.createFromAsset(context.getAssets(), "PTM75F.ttf");
		nTypeface2 = Typeface.createFromAsset(context.getAssets(), "10.ttf");
		nScreenWidth = w;
		//nScreenHeight = h;
		nContext = context;
	}
	
	public void SetBatteryLevel(int iBatteryLevel) {
		nBatteryLevel = iBatteryLevel;
	}
	
	private Paint __GetPaint(Paint.Align p, Typeface t) {
		Paint tPaint = new Paint();
		tPaint.setAntiAlias(true);
		tPaint.setColor(0xffffffff); //0xffffddbb
		tPaint.setAlpha(FONT_COLOR_ALPHA);
		tPaint.setTextSize(FONT_SIZE_ON_START);
		tPaint.setTextAlign(p);
		tPaint.setTypeface(t);
		tPaint.setShadowLayer(5f, 0, 0, Color.BLACK);
		return tPaint;
	}

	@SuppressWarnings("unused")
	private Rect __DrawNRect(Canvas iCanvas, NRect nNRect) { /* Необходимо для отладки, ибо иначе границы текста трудновато понять =) */
		Rect tRect1 = new Rect();
		tRect1.set(nNRect.x, nNRect.y, nNRect.w + nNRect.x, nNRect.h + nNRect.y);
		//if (XX1_DEBUG == 1) {	
			Paint pp = new Paint();
			pp.setStyle(Style.STROKE);
			pp.setColor(0xFFFFFFFF);
			if (iCanvas != null) iCanvas.drawRect(tRect1, pp);
		//}
		return tRect1;
	}
	
	private NRect __GetTextBoundsX(Paint.Align p, Typeface t, String iText, int sw) {
		NRect nNRect = new NRect();
		Paint tPaint = __GetPaint(p, t);
		
		int tFontSize = 12;
		while (true) {
			if (tPaint.measureText(iText) >= sw) {
				Rect tRect1 = new Rect();
				tPaint.getTextBounds(iText, 0, iText.length(), tRect1);
				nNRect.x = 0;
				nNRect.y = 0;
				nNRect.w = tRect1.left + tRect1.width();
				nNRect.h = tRect1.bottom + tRect1.height();
				nNRect.fontsize = tFontSize;
				return nNRect;
			} else {
				tFontSize += 2;
				tPaint.setTextSize(tFontSize);
				if (tFontSize >= 250) {
					return null;
				}
			}
		}
	}
	
	private NRect __DrawTimeA(Canvas iCanvas) {
		Time nTime = new Time(Time.getCurrentTimezone());
		nTime.setToNow();
		final String tTimeText = nTime.format("%H:%M:%S");
		
		NRect nNRect = __GetTextBoundsX(Align.LEFT, nTypeface2, "00:00:00", (nScreenWidth - (nScreenWidth / SPACE_SIZE_FOR_TIME)));

		Paint tPaint = __GetPaint(Align.LEFT, nTypeface2);
		tPaint.setTextSize(nNRect.fontsize);
		
		nNRect.x = (nScreenWidth / 2) - (nNRect.w / 2);
		nNRect.y = nNRect.y + nNRect.h + (nScreenWidth / 3);

		if (iCanvas != null) iCanvas.drawText(tTimeText, nNRect.x, nNRect.y + nNRect.h, tPaint);
		//__DrawNRect(iCanvas, nNRect);
		return nNRect;
	}
	
	private NRect __DrawBatteryNetstatA(Canvas iCanvas, NRect iBaseRect) {
		final long rxl = TrafficStats.getMobileRxBytes();
		final long txl = TrafficStats.getMobileTxBytes();
		
		final String tx = (txl > 1024) ? 
					 		(txl > (1024 * 1024)) ? 
					 				(txl > (1024 * 1024 * 1024)) ? (txl / (1024 * 1024 * 1024)) + "ГБ" : (txl / (1024 * 1024)) + "МБ" 
					 				: 
					 				(txl / 1024) + "КБ"
							: 
							txl + "Б";
		
		final String rx = (rxl > 1024) ? 
		 		(rxl > (1024 * 1024)) ? 
		 				(rxl > (1024 * 1024 * 1024)) ? (rxl / (1024 * 1024 * 1024)) + "ГБ" : (rxl / (1024 * 1024)) + "МБ" 
		 				: 
		 				(rxl / 1024) + "КБ"
				: 
				rxl + "Б";
	
		
		
		final String tText = "" + nBatteryLevel + "%  ↑" + tx + "  ↓" + rx;
		NRect nNRect = __GetTextBoundsX(Align.LEFT, nTypeface2, tText, (int)(nScreenWidth - (nScreenWidth / SPACE_SIZE_FOR_NETSTAT)));
		
		nNRect.x = FONT_HSPACE;
		nNRect.y = iBaseRect.y - FONT_VSPACE - nNRect.h;
		
		Paint tPaint = __GetPaint(Align.LEFT, nTypeface2);
		tPaint.setTextSize(nNRect.fontsize);
		
		if (iCanvas != null) iCanvas.drawText(tText, nNRect.x, nNRect.y + nNRect.h, tPaint);
		//__DrawNRect(iCanvas, nNRect);
		return nNRect;
	}
	
	private NRect __DrawDate(Canvas iCanvas, NRect iBaseRect) {
		Calendar tCalendar = new GregorianCalendar();
		tCalendar.setTime(new Date());
		String tDate = DateUtils.formatDateTime(nContext, tCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY);
		
		NRect nNRect = __GetTextBoundsX(Align.RIGHT, nTypeface2, tDate, (nScreenWidth - (nScreenWidth / SPACE_SIZE_FOR_INFO)));
		nNRect.x =  nScreenWidth - FONT_HSPACE;// - nNRect.w - FONT_HSPACE;
		nNRect.y = iBaseRect.y + iBaseRect.h;
		
		Paint tPaint = __GetPaint(Align.RIGHT, nTypeface2);
		tPaint.setTextSize(nNRect.fontsize);
		
		if (iCanvas != null) iCanvas.drawText(tDate, nNRect.x, nNRect.y + nNRect.h, tPaint);
		//__DrawNRect(iCanvas, nNRect);
		return nNRect;
	}

	@SuppressWarnings("unused")
	public void Draw(Canvas iCanvas) {
		NRect tRect1 = __DrawTimeA(iCanvas);
		NRect tRect2 = __DrawBatteryNetstatA(iCanvas, tRect1);
		NRect tRect3 = __DrawDate(iCanvas, tRect1);
	}
}
