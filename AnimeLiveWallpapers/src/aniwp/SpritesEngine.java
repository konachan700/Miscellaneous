package aniwp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.aniwp.R;
import android.content.res.Resources;
import android.graphics.Bitmap;

public class SpritesEngine {
	private Resources nResources;
	private float nHolderWidth, nHolderHeight;
	
	private Map<String, Bitmap> nSpriteArray = new HashMap<String, Bitmap>();
	
	public SpritesEngine(Resources r, float HolderWidth, float HolderHeight) {
		nResources = r;
		nHolderWidth = HolderWidth;
		nHolderHeight = HolderHeight;
		
		final int retval = LoadUserSprites();
		if (retval > 0) {
			nSpriteArray.clear();
			LoadDefaultSprites();
		}
	}
	
	public void LoadDefaultSprites() {
		nSpriteArray.put("normal", 		BitmapEngine.CreateSpriteFromResources(nResources, R.drawable.lena_normal, 		nHolderWidth, nHolderHeight));
		nSpriteArray.put("top",    		BitmapEngine.CreateSpriteFromResources(nResources, R.drawable.lena_top,    		nHolderWidth, nHolderHeight));
		nSpriteArray.put("smile",  		BitmapEngine.CreateSpriteFromResources(nResources, R.drawable.lena_smile,  		nHolderWidth, nHolderHeight));
		nSpriteArray.put("right",  		BitmapEngine.CreateSpriteFromResources(nResources, R.drawable.lena_right,  		nHolderWidth, nHolderHeight));
		nSpriteArray.put("rage",   		BitmapEngine.CreateSpriteFromResources(nResources, R.drawable.lena_rage,   		nHolderWidth, nHolderHeight));
		nSpriteArray.put("left",   		BitmapEngine.CreateSpriteFromResources(nResources, R.drawable.lena_left,   		nHolderWidth, nHolderHeight));
		nSpriteArray.put("bottom", 		BitmapEngine.CreateSpriteFromResources(nResources, R.drawable.lena_bottom, 		nHolderWidth, nHolderHeight));
		nSpriteArray.put("eye_close", 	BitmapEngine.CreateSpriteFromResources(nResources, R.drawable.lena_eye_close, 	nHolderWidth, nHolderHeight));
		nSpriteArray.put("touchmap", 	BitmapEngine.CreateSpriteFromResources(nResources, R.drawable.lena_touchmap, 	nHolderWidth, nHolderHeight));
	}
	
	public int LoadUserSprites() {
		final String sdir = FSEngine.GetSpritesDirectory();
		if (sdir == null) return 2;
		
		final File f1 = new File(sdir);
		if (f1.isDirectory() == false) return 1;
		
		final String rs[] = f1.list();
		if (rs == null) return 3;
		
		for (String s : rs) {
			File f2 = new File(f1.getAbsolutePath() + "/" + s);
			if ((f2.exists() == true) && (f2.canRead() == true) && (s.contains(".png"))) { 
				nSpriteArray.put(s.replace(".png", ""), BitmapEngine.CreateSpriteFromFile(f2.getAbsolutePath(), nHolderWidth, nHolderHeight));
			}
		}
		if (nSpriteArray.size() <= 0) return 4;
		
		return 0;
	}
	
	public Bitmap GetSprite(String name) {
		return nSpriteArray.get(name);
	}
}
