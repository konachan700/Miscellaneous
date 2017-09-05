package com.nekokoneko.microlauncher;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class ScreenNotifyManager {
	private Context 			nContext 				= null;
	private SQLiteDatabase		nDatabase				= null;
	
	private String 				nRootPath 				= "",
								nDBPath 				= "";
	
	public ScreenNotifyManager(Context iContext) {
		nContext 				= iContext;
		RebuildFSTree();
		
	}
	
	public void RebuildFSTree() {
		File tBaseDir = Environment.getExternalStorageDirectory();
		nRootPath = tBaseDir.getAbsolutePath() + "/.MicroLauncherBeta/";

		nDBPath = nRootPath + "sql/";
		new File(nDBPath).mkdirs();
	}
	
}
