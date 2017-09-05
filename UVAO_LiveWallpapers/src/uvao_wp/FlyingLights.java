package uvao_wp;

import java.util.ArrayList;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class FlyingLights {
	ArrayList<FlyingLight> Lights = new ArrayList<FlyingLight>();
	
	public FlyingLights(Map<String, Bitmap> _XG, String spriteID, float HolderWidth, float HolderHeight, int LightsCount) {
		for (int i=0; i<LightsCount; i++) {
			FlyingLight fl = new FlyingLight(HolderWidth, HolderHeight);
			fl.CreateBitmap(_XG, spriteID);
			Lights.add(fl);
		}
	}
	
	public FlyingLights(Map<String, Bitmap> _XG, String spriteID, float HolderWidth, float HolderHeight, int LightsCount, 
			float StartPositionX, float StopPositionX, float StartPositionY) {
		for (int i=0; i<LightsCount; i++) {
			FlyingLight fl = new FlyingLight(HolderWidth, HolderHeight, StartPositionX, StopPositionX, StartPositionY);
			fl.CreateBitmap(_XG, spriteID);
			Lights.add(fl);
		}
	}
	
	public void FlyingLightsMove() {
		for (int i=0; i<Lights.size(); i++) {
			FlyingLight fl = Lights.get(i);
			fl.Move();
			Lights.set(i, fl);
		}
	}
	
	public void FlyingLightsDraw(Canvas c) throws IllegalArgumentException, NullPointerException   {
		for (int i=0; i<Lights.size(); i++) {
			FlyingLight fl = Lights.get(i);
			Paint p = new Paint();
			p.setAlpha(fl.GetAlpha());
			if (c != null)  c.drawBitmap(fl.GetBitmap(), fl.GetX(), fl.GetY(), p);
		}
	}
}
