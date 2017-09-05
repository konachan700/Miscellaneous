package uvao_wp;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
//import android.util.Log;
import android.view.MotionEvent;

public class TouchMapEngine {	
	private Bitmap 					touchMapBitmap		= null;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Integer> 	Actions 			= new HashMap<Integer, Integer>();
	
	private float 					HolderWidth			= 0, 
									HolderHeight		= 0, 
									MapWidth			= 0,
									MapHeight			= 0;

	public TouchMapEngine(Map<String, Bitmap> _XG, String mapID, float _HolderWidth, float _HolderHeight) {
		touchMapBitmap 	= _XG.get(mapID);
		MapWidth		= touchMapBitmap.getWidth();
		MapHeight		= touchMapBitmap.getHeight();
		HolderWidth 	= _HolderWidth;
		HolderHeight 	= _HolderHeight;
	}
	
	public void RegisterNewAction(int index, int color) {
		Actions.put(color, index);
	}
	
	public void UnregisterAction(String name) {
		Actions.remove(name);
	}
	
	public int GetCurrentAction(MotionEvent event) {
		final float X 		= event.getX(), 
					Y 		= event.getY();
		final float corrX 	= X - ((float)((HolderWidth - MapWidth)) / 2),
					corrY	= Y - (HolderHeight - MapHeight);
		      
		if ((corrX <= 0) || (corrY <= 0)) return 0;
		if (corrX >= (touchMapBitmap.getWidth())) return 0;
		if (corrY >= (touchMapBitmap.getHeight())) return 0;

		//Log.d("GetCurrentAction", "X=" + X + "; Y=" + Y + "; cX=" + corrX + "; cY=" + corrY + ";");
		//Log.d("GetCurrentAction", "HolderWidth=" + HolderWidth + "; MapWidth=" + MapWidth + "; HolderHeight=" + HolderHeight + "; MapHeight=" + MapHeight + ";");
		
		final int 	C 		= touchMapBitmap.getPixel((int)corrX, (int)corrY);
		final int   RetVal	= (Actions.get(C) != null) ? Actions.get(C) : 0;
		
		return RetVal;
	}
}
