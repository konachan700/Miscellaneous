package uvao_wp;

import java.util.Map;
import java.util.Random;

import android.graphics.Bitmap;

public class FlyingLight {
	private float ModX, ModY, wCanvasMax, hCanvasMax, PositionX, PositionY, Alpha, Size, MaxX, MinX; 
	private Bitmap light;
	private boolean MoveMod = true;
	
	private final int Y_START_POS_RANDOM_RANGE = 99; /* разброс огоньков на старте по Y-координате, нужно, чтобы они изначально плыли неравномерно */

	public FlyingLight(float CanvasWidth, float CanvasHeight) {
		PositionX = ((float)(new Random().nextInt((int)CanvasWidth)));
		PositionY = CanvasHeight - ((float)(new Random().nextInt(Y_START_POS_RANDOM_RANGE)));
		ModX = 0.023f * ((float)( new Random().nextInt(5) - 10));
		ModY = 0.15f * ((float)(new Random().nextInt(20)));
		Alpha = 0;
		wCanvasMax = CanvasWidth; 
		hCanvasMax = CanvasHeight;
		MaxX = CanvasWidth;
		MinX = 1;
		MoveMod = (new Random().nextInt(10) > 5) ? true : false;
	}
	
	public FlyingLight(float CanvasWidth, float CanvasHeight, float StartPositionX, float StopPositionX, float StartPositionY) {
		PositionX = StartPositionX + ((float)new Random().nextInt((int)(StopPositionX - StartPositionX)));
		PositionY = StartPositionY;
		ModX = 0.023f * ((float)( new Random().nextInt(10) - 20));
		ModY = 0.15f * ((float)(new Random().nextInt(40)));
		Alpha = 0;
		wCanvasMax = CanvasWidth; 
		hCanvasMax = CanvasHeight;
		MaxX = (StopPositionX >= CanvasWidth) ? CanvasWidth : StopPositionX;
		MoveMod = (new Random().nextInt(10) > 5) ? true : false;
		MinX = StartPositionX;
	}
	
	public void CreateBitmap(Map<String, Bitmap> _XG, String spriteID) {
		Size = 32 - (new Random().nextInt(20));
		Bitmap Bo = _XG.get(spriteID);
		light = Bitmap.createScaledBitmap(Bo, (int) Size, (int) Size, true);
		
		PositionY = PositionY - Size;
		PositionX = (PositionX >= (wCanvasMax - Size)) ? (PositionX - Size - 1) : PositionX;
	}
	
	public void SetBounds(float w, float h) {
		wCanvasMax = w;
		hCanvasMax = h;
	}
	
	public void Move() {
		PositionX = PositionX + (ModX * ((MoveMod) ? 1 : -1));
		if (PositionX <= MinX) {
			//PositionX = wCanvasMax - Size - 1;
			MoveMod = !MoveMod;
		}
		else if ((PositionX + 1 + Size) >= MaxX) {
			//PositionX = 1;
			MoveMod = !MoveMod;
		}
		
		PositionY = PositionY - ModY;
		if (PositionY <= 1) { 
			PositionY = hCanvasMax - Size - 1 - ((float)(new Random().nextInt(Y_START_POS_RANDOM_RANGE)));
			Alpha = 0;
		}

		if (Alpha < 180) Alpha = Alpha + ((Alpha < 64) ? 2 : 1);
	}

	public Bitmap 	GetBitmap() { return light; 			}
	public int 		GetX() 		{ return (int) PositionX;	}
	public int 		GetY() 		{ return (int) PositionY; 	}
	public int	 	GetAlpha() 	{ return (int) Alpha; 		}
}
