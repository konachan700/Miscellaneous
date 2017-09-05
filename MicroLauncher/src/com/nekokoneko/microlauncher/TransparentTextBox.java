package com.nekokoneko.microlauncher;

import com.nekokoneko.microlauncher.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class TransparentTextBox {
	private Resources 	nResources 		= null;
	private Canvas 		nCanvas 		= null;
	private Bitmap 		nCacheBitmap 	= null;
	//private Rect        nTextRect       = null;
	//private Context		nContext		= null;
	
	private int 		nx, ny, nw, nh/*, tFontSize = 16;*/;
	private float		nImageW, nImageH, nScreenW, nScreenH;
	//private boolean 	nDrawTimeState  = false;
	
	public TransparentTextBox(Resources res, int w, int h) {
		nResources = res;
		__LoadTTB(w, h);
	}

	public int getXForText() { return nx; }
	public int getYForText() { return ny; }
	public int getWForText() { return nw; }
	public int getHForText() { return nh; }
	
	private void __LoadTTB(float w, float h) {
		ResizeImages tResizeImages = new ResizeImages(nResources);
		nCacheBitmap = tResizeImages.ConvertSpriteFromResource(R.drawable.preloaded_voice_1, w, h);
		
		nImageW = nCacheBitmap.getWidth();
		nImageH = nCacheBitmap.getHeight();
		nScreenW = w;
		nScreenH = h;

		/* Таки да, магические числа как они есть. Ловим координаты в гимпе, считаем в калькуляторе пропорции, суем сюда... 
		   Можно было бы и функцию сделать для пересчета, но зачем, если считать надо всего один раз при изготовлении спрайта? */
		
		nx = (int)(nImageW / 3.20f);
		ny = (int)(nImageH / 2.55f);
		nw = (int)(nImageW / 2.00f);
		nh = (int)(nImageH / 2.10f);
		
		//System.err.printf("nw=%d, nh=%d, nx=%d, ny=%d; nImageW=%d, nImageH=%d \n", nw, nh, nx, ny, (int)nImageW, (int)nImageH);
	}
	
	public void Draw(Canvas cnv, String text, String charName) {
		nCanvas = cnv;
		
		if (nCanvas == null) return;
		
		float ix = (nScreenW > nImageW) ? ((nScreenW / 2) - (nImageW / 2)) : 0;
		float iy = (nScreenH > nImageH) ? (nScreenH - nImageH) : 0;
		
		Paint tPaint = new Paint();
		tPaint.setAntiAlias(true);
		if (cnv != null) nCanvas.drawBitmap(nCacheBitmap, ix, iy, tPaint);

		if (charName == null) {
			DrawMultilineText(nCanvas, text, (int)nw, (int)nh, (int)(ix+nx), (int)(iy+ny), 12, 18);
		} else {
			DrawMultilineText(nCanvas, charName, (int)nw, 28, (int)(ix+nx), (int)(iy+ny), 12, 28);
			DrawMultilineText(nCanvas, text, (int)nw, (int)nh - 33, (int)(ix+nx-17), (int)(iy+ny+28), 12, 18);
		}
		
		
		
	}
	
	public void Draw(Canvas cnv, String text) {
		this.Draw(cnv, text, null);
	}

	
	private static int __DrawMultilineText(Canvas cnv, String txt, int w, int h, int x, int y, int vspace, int tsize, int posY) {
		return __DrawMultilineText(cnv, txt, w, h, x, y, vspace, tsize, posY, null);
	}
	
	private static int __DrawMultilineText(Canvas cnv, String txt, int w, int h, int x, int y, int vspace, int tsize, int posY, Paint pT) {
		String ta[] = txt.trim().split(" ");
		StringBuilder sb = new StringBuilder();

		Paint pntT;
		if (pT == null) {
			pntT = new Paint();
			pntT.setAntiAlias(true);
			pntT.setTextSize(tsize);
			pntT.setColor(Color.BLACK);
			pntT.setShadowLayer(8f, 0, 0, Color.WHITE);
			pntT.setAlpha(200);
		} else {
			pntT = pT;
		}

		Rect rectT = new Rect();
		
		if (ta.length == 0) {
			return 0;
		}
		
		if (ta.length == 1) {
			pntT.getTextBounds(txt, 0, txt.length()-1, rectT);
			if (cnv != null) cnv.drawText(txt, x, y, pntT);
			return (rectT.height() + vspace);
		}

		int pos = posY; /* смещение текста по вертикали *///, 
		for (int i=0; i<ta.length; i++) {
			if (sb.length() > 1) {
				final String sx = sb.substring(0) + " " + ta[i];
				pntT.getTextBounds(sx, 0, sx.length()-1, rectT);
				
				if (rectT.width() > w) {
					if (cnv != null) cnv.drawText(sb.substring(0), x, y + pos, pntT);
					sb = new StringBuilder();
					pos = pos + rectT.height() + vspace;
					if ((rectT.height() + pos) > h) {
						break;
					}
				}
			}
			sb.append(ta[i] + " ");
			if (i == (ta.length - 1)) {
				if (i == 0) pos = pos + rectT.height() + vspace;
				if (cnv != null) cnv.drawText(sb.substring(0), x, y + pos, pntT);
				return pos + rectT.height() + vspace;
			}
		}
		return pos;
	}
	
	public static void DrawMultilineText(Canvas cnv, String txt, int w, int h, int x, int y, int vspace, int tsize) {
		String ta[] = txt.trim().split("\n");
		if (ta.length < 1) return;
		int posT = 0, posE = 0;
		for (int i=0; i<ta.length; i++) {
			if (ta[i].length() > 0) {
				posE = __DrawMultilineText(cnv, ta[i], w, h, x, y, vspace, tsize, posT);
				posT = posE;
			}
		}
	}
	
	public static void DrawMultilineText(Canvas cnv, String txt, int w, int h, int x, int y, int vspace, Paint pT) {
		String tz[] = txt.trim().split(" ");
		StringBuilder sba = new StringBuilder();
		for (int i=0; i<tz.length; i++) {
			final int ln = Math.round(pT.measureText(tz[i]));
			if (ln > Math.round((w+(w*0.3f)))) { /* АХТУНГ! Странная временная магическая цифра, необходимо переделать! */
				sba.append(tz[i].substring(0, tz[i].length()/2) + " " + tz[i].substring(tz[i].length()/2, tz[i].length()) + " ");
			} else {
				sba.append(tz[i] + " ");
			}
		}
		txt = sba.substring(0);
		
		String ta[] = txt.trim().split("\n");
		if (ta.length < 1) return;
		int posT = 0, posE = 0;
		for (int i=0; i<ta.length; i++) {
			if (ta[i].length() > 0) {
				posE = __DrawMultilineText(cnv, ta[i], w, h, x, y, vspace, 0, posT, pT);
				posT = posE;
			}
		}
	}
}
