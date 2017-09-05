package aniwp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.os.Environment;

public class FSEngine {
	private static final String rootDir 	= "/ALW/",
								charsDir 	= "sprites/",
								bgDir 		= "backgrounds/";
	
	public FSEngine() {}
	
	public static int InitFileTree() {
		final String s = GetRootDirectory();
		final File f0 = new File(s); 				f0.mkdirs();
		final File f1 = new File(s + charsDir); 	f1.mkdirs();
		final File f2 = new File(s + bgDir); 		f2.mkdirs();
		
		if (f0.canRead() && f1.canRead() && f2.canRead()) return 0; else return 1;
	}
	
	public static Properties GetWallpapersConfig() {
		final String rd = GetRootDirectory();
		if (rd == null) return null;
		
		FileInputStream fis = null;
		Properties p = new Properties();
		
		try {
			fis = new FileInputStream(rd + "wallpapers.conf");
			p.load(fis);
			fis.close();
			return p;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static Properties GetTouchMapConfig() {
		final String rd = GetRootDirectory();
		if (rd == null) return null;
		
		FileInputStream fis = null;
		Properties p = new Properties();
		
		try {
			fis = new FileInputStream(rd + "touchmap.conf");
			p.load(fis);
			fis.close();
			return p;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static String[] GetFoldersList(String path) {
		ArrayList<String> al = new ArrayList<String>();
		
		final File f = new File(path);
		if (f.isDirectory() == false) return null;
		
		String s[] = f.list();
		if (s == null) return null;
		if (s.length <= 0) return null;
		
		for (String sa : s) {
			File fa = new File(f.getAbsoluteFile() + "/" + sa + "/");
			if (fa.isDirectory() == true) al.add(sa);
		}
		
		String ret[] = new String[]{};
		ret = al.toArray(ret);
		return ret;
	}
	
	public static String GetRootDirectory() {
		File f = Environment.getExternalStorageDirectory();
		if (f.canRead() == true) return (f.getAbsolutePath() + rootDir); else return null;
	}
	
	public static String GetSpritesDirectory() {
		final String s = GetRootDirectory();
		if (s != null) return (s + charsDir); else return null;
	}
	
	public static String GetBackgroundsDirectory() {
		final String s = GetRootDirectory();
		if (s != null) return (s + bgDir); else return null;
	}
}
