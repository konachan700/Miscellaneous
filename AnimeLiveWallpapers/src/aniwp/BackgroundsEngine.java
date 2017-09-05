package aniwp;

import java.io.File;
import java.util.ArrayList;
import com.aniwp.R;
import android.content.res.Resources;
import android.graphics.Bitmap;

public class BackgroundsEngine {
	private float nHolderWidth, nHolderHeight;
	private int defBgCounter = 1,
			    customBgCounter = 0;
	
	private Bitmap nCurrentBitmap;
	private Resources nResources;
	private ArrayList<String> fileList = new ArrayList<String>();
	
	public BackgroundsEngine(Resources r, float HolderWidth, float HolderHeight) {
		nResources = r;
		nHolderWidth = HolderWidth;
		nHolderHeight = HolderHeight;
		
		final String bgPath = FSEngine.GetBackgroundsDirectory();
		if (bgPath != null) {
			File f1 = new File(bgPath);
			final String fs[] = f1.list();
			if (fs == null) {
				__NextDefaultBackground();
				return;
			}
			
			for (String sd : fs) {
				File f2 = new File(f1.getAbsolutePath() + "/" + sd);
				if (f2.canRead() == true) {
					if (sd.contains(".jpg")) {
						fileList.add(f1.getAbsolutePath() + "/" + sd);
					}
				}
			}
			
			if (fileList.size() <= 0) {
				__NextDefaultBackground();
				return;
			}
			
			__NextCustomBackground();
		} else {
			__NextDefaultBackground();
		}
	}
	
	public void NextBackground() {
		if (fileList.size() <= 0) {
			__NextDefaultBackground();
		} else {
			__NextCustomBackground();
		}
	}
	
	private void __NextCustomBackground() {
		final String fPath = fileList.get(customBgCounter);
		nCurrentBitmap = BitmapEngine.CreateBackgroundFromFile(fPath, nHolderWidth, nHolderHeight);
		customBgCounter++;
		if (customBgCounter >= fileList.size()) customBgCounter = 0;
	}
	
	private void __NextDefaultBackground() {
		switch(defBgCounter) {
		case 1:
			nCurrentBitmap = BitmapEngine.CreateBackgroundFromResources(nResources, R.drawable.default_bg1, nHolderWidth, nHolderHeight);
			break;
		case 2:
			nCurrentBitmap = BitmapEngine.CreateBackgroundFromResources(nResources, R.drawable.default_bg2, nHolderWidth, nHolderHeight);
			break;
		case 3:
			nCurrentBitmap = BitmapEngine.CreateBackgroundFromResources(nResources, R.drawable.default_bg3, nHolderWidth, nHolderHeight);
			break;
		}
		
		defBgCounter++;
		if (defBgCounter > 3) defBgCounter = 1;
	}
	
	public Bitmap GetBackground() {
		return nCurrentBitmap;
	}
}
