package com.nekokoneko.microlauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class ApplicationManager {
	class ApplicationListBuilder extends Thread implements Runnable {
		ProgressDialog nProgressDialog = null;
		
		public ApplicationListBuilder(ProgressDialog iProgressDialog) {
			nProgressDialog = iProgressDialog;
			nProgressDialog.show();
		}
		
		public void run() {
			Intent tIntent = new Intent(Intent.ACTION_MAIN, null);
			tIntent.addCategory(Intent.CATEGORY_LAUNCHER);

			List<ResolveInfo> tApplicationsList = nPackageManager.queryIntentActivities(tIntent, PackageManager.PERMISSION_GRANTED);
			
			int tCounter = 0;
			for (ResolveInfo ri: tApplicationsList) {
				tCounter++;
				
				final String 	tApplicationTitle 	= ri.loadLabel(nPackageManager).toString();
				final String 	tPackageName 		= ri.activityInfo.packageName;
				final Drawable 	tDrawable			= ri.loadIcon(nPackageManager);
				boolean			iIsSaveIcon			= true;
				
				if (nDatabase.isOpen()) {
					Cursor tC = nDatabase.query("applications", null, "apppackage='"+tPackageName+"'", null, null, null, "_id ASC");
					if(tC.getCount() > 0) {
						if (tC.moveToFirst()) {
							String tName = tC.getString(tC.getColumnIndex("apppackage")).replaceAll("\\.", "_") + ".png";
							boolean isSkin = new File(nIconsForSkin + tName).canRead();
							final String tIcon = ((isSkin) ? nIconsForSkin : nIconCache) + tName;
							File tF1 = new File(tIcon);
							if (tF1.canRead()) {
								iIsSaveIcon = false;
							}
						}
					} else {
						ContentValues tCV = new ContentValues();
						tCV.put("apptag", 0);
						tCV.put("appicon", tCounter);
						tCV.put("sortindex", 0);
						tCV.put("appname", tApplicationTitle);
						tCV.put("apppackage", tPackageName);
						
						nDatabase.insert("applications", null, tCV);
					}
				}
				
				if (iIsSaveIcon) {
					Bitmap tB = ResizeImages.DrawableToBitmap(tDrawable);
					FileOutputStream out = null;
					try {
						String tName = tPackageName.replaceAll("\\.", "_") + ".png";
						//boolean isSkin = new File(nIconsForSkin + tName).canRead();
						//if (isSkin) 
						//	out = new FileOutputStream(nIconsForSkin + tName); 
						//else
							out = new FileOutputStream(nIconCache + tName);

					       	tB.compress(Bitmap.CompressFormat.PNG, 90, out);
					       	tB.recycle();
					} catch (Exception e) {
					    e.printStackTrace();
					} finally {
					       try{
					    	   out.flush();
					           out.close();
					       } catch(Throwable ignore) {}
					}
				}
			}
			
			/* удаление из БД тех приложений, которых уже нет в системе */
			boolean n = true;
			ArrayList<String> all_1 = new ArrayList<String>();
			Cursor tC2 = nDatabase.query("applications", null, null, null, null, null, null);
			if(tC2.getCount() > 0) {
				if (tC2.moveToFirst()) {
					do {
						n = true;
						final String a = tC2.getString(tC2.getColumnIndex("apppackage"));
						for (ResolveInfo ri: tApplicationsList) {
							final String b = ri.activityInfo.packageName;
							if (a.equalsIgnoreCase(b)) {
								n = false;
								break;
							}
						}
						if (n) all_1.add(a);
					} while (tC2.moveToNext());
				}
				
				if (all_1.size() > 0) {
					for (String s : all_1) {
						nDatabase.delete("applications", "apppackage='"+s+"'", null);
					}
				}
			}
			

			if (nProgressDialog != null) nProgressDialog.hide();
		}
	}
	
	class ApplicationItem {
		private String 	nAppName 		= "",
				       	nAppPackage 	= "",
				       	nAppIconPath	= "";
		
		private int 	nSortIndex		= 0,
						nTag			= 0;
		
		private Bitmap  nIconCacheZ      = null;
		
		public ApplicationItem(String iAppName, String iAppPackage, int iSortIndex, int iTag) {
			nAppName 		= iAppName;
			nAppPackage 	= iAppPackage;
			String tName = nAppPackage.replaceAll("\\.", "_") + ".png";
			boolean isSkin = new File(nIconsForSkin + tName).canRead();
			
			nSortIndex 		= iSortIndex;
			nTag 			= iTag;
			nAppIconPath 	= ((isSkin) ? nIconsForSkin : nIconCache) + tName;
			
			if (new File(getAppIconPath()).canRead()) 
				nIconCacheZ = BitmapFactory.decodeFile(getAppIconPath());
		}
		
		public int 		getTag() 			{ return nTag; 				}
		public int 		getSortIndex() 		{ return nSortIndex; 		} 
		public String 	getAppName() 		{ return nAppName;		 	}
		public String   getAppPackage()		{ return nAppPackage;		}
		public String   getAppIconPath()	{ return nAppIconPath;		}
		public Bitmap   getBitmap() 		{ return nIconCacheZ;	 	}
	}
	
	private Context 			nContext 				= null;
	private PackageManager 	 	nPackageManager			= null;
	private SQLiteDatabase		nDatabase				= null;
	private Cursor				nCursor					= null;
	
	private String 				nRootPath 				= "",
								nIconCache 				= "",
								nIconsForSkin			= "",
								nDBPath 				= "";
	
	private int 				nTotalCount				= 0,
								nSavedTag				= 0;
	
	private ArrayList<ApplicationItem> nApplicationItems  = new ArrayList<ApplicationItem>();
	private ArrayList<ApplicationItem> nApplicationItemsF = new ArrayList<ApplicationItem>();
	private ArrayList<ApplicationItem> nApplicationItems5 = new ArrayList<ApplicationItem>();
	
	public PackageManager getPM() {
		return nPackageManager;
	}
	
	public ApplicationManager(Context iContext) {
		nContext 				= iContext;
		nPackageManager 		= nContext.getPackageManager();

		RebuildFSTree();

		try {
			nDatabase = nContext.openOrCreateDatabase(nDBPath + "appslist.db", Context.MODE_PRIVATE, null);
			if (nDatabase.isOpen()) {
				nDatabase.execSQL("CREATE TABLE if not exists applications(" +
						 "_id integer primary key autoincrement, " +
						 "appname text not null, " +
						 "apppackage text not null, " +
						 "sortindex integer not null, " +
						 "apptag integer not null, " +
					 	 "appicon integer not null);"
				);

				RebuildApplicationList();
			}
		} catch (android.database.sqlite.SQLiteException e) { }
	}
	
	public void SetTag(int index, String NewTag, String NewName) {
		boolean mainblock = true;
		if (index >= 9000) {
			index = index - 9000;
			mainblock = false;
		}
		if (nDatabase.isOpen()) {
			int tag = Integer.valueOf(NewTag).intValue();
			if ((tag == 9000) && (get5ApplicationsCount() > 4)) {
				return;
			}
			
			final String apppackage = (mainblock) ? getApplicationPackage(index) : get5ApplicationPackage(index);
			
			ContentValues tCV = new ContentValues();
			tCV.put("apptag", tag);
			tCV.put("appname", NewName);
			
			nDatabase.update("applications", tCV, "apppackage='"+apppackage+"'", null);
		}
	}
	
	public void ReloadLast() {
		ReloadApplicationList(nSavedTag);
	}
	
	public void Reload5ApplicationList() {
		nApplicationItems5.clear();
		Cursor nCursorZ = nDatabase.query("applications", null, "apptag=9000", null, null, null, "sortindex ASC");
		final int nCursorCount = nCursorZ.getCount();
		if(nCursorCount >= 0) {
			if (nCursorZ.moveToFirst()) {
				do {
					ApplicationItem ai = new ApplicationItem(nCursorZ.getString(nCursorZ.getColumnIndex("appname")), nCursorZ.getString(nCursorZ.getColumnIndex("apppackage")), 
							nCursorZ.getInt(nCursorZ.getColumnIndex("sortindex")), nCursorZ.getInt(nCursorZ.getColumnIndex("apptag")));
					nApplicationItems5.add(ai);
				} while (nCursorZ.moveToNext());
			}
		}
		
	}
	
	public void ReloadApplicationList(int iTag) {
		nApplicationItemsF.clear();
		nSavedTag = iTag;
		nCursor = nDatabase.query("applications", null, ((iTag == -1) ? null : "apptag="+iTag), null, null, null, "appname ASC");
		if (nDatabase.isOpen()) {
			final int nCursorCount = nCursor.getCount();
			nTotalCount = nCursorCount;
			if(nCursorCount >= 0) {
				if (nCursor.moveToFirst()) {
					do {
						ApplicationItem ai = new ApplicationItem(nCursor.getString(nCursor.getColumnIndex("appname")), nCursor.getString(nCursor.getColumnIndex("apppackage")), 
								nCursor.getInt(nCursor.getColumnIndex("sortindex")), nCursor.getInt(nCursor.getColumnIndex("apptag")));
						nApplicationItemsF.add(ai);
					} while (nCursor.moveToNext());
				}
			}
		}
	}
	
	public void RefreshApplicationList(int iTag, int iFrom, int iTo) {
		nApplicationItems.clear();
		if (nApplicationItemsF.size() <= 0) ReloadApplicationList(iTag);
		if (nApplicationItemsF.size() <= 0) return;
		
		if (iFrom < 0) iFrom = 0;
		if ((iTo >= nApplicationItemsF.size()) || (iTo < 0)) iTo = nApplicationItemsF.size();
		
		if (iFrom >= iTo) {
			nApplicationItems = nApplicationItemsF;
		} else {
			for (int i=iFrom; i<iTo; i++)
				nApplicationItems.add(nApplicationItemsF.get(i));
		}
	}
	
	public Bitmap	get5ApplicationIcon(int index) 		{ return nApplicationItems5.get(index).getBitmap(); 		}
	public String 	get5ApplicationPackage(int index) 	{ return nApplicationItems5.get(index).getAppPackage(); 	}
	public String 	get5ApplicationTitle(int index) 	{ return nApplicationItems5.get(index).getAppName(); 		}
	public int 		get5ApplicationTag(int index) 		{ return nApplicationItems5.get(index).getTag(); 			}
	public int 		get5ApplicationsCount()				{ return nApplicationItems5.size(); 						}
	
	public Bitmap	getApplicationIcon(int index) 		{ return nApplicationItems.get(index).getBitmap(); 			}
	public String 	getApplicationTitle(int index) 		{ return nApplicationItems.get(index).getAppName(); 		}
	public String 	getApplicationPackage(int index) 	{ return nApplicationItems.get(index).getAppPackage(); 		}
	public int 		getApplicationTag(int index) 		{ return nApplicationItems.get(index).getTag(); 			}
	public int 		getApplicationSI(int index) 		{ return nApplicationItems.get(index).getSortIndex(); 		}
	public int 		getApplicationsCount()				{ return nApplicationItems.size(); 							}
	public int 		getApplicationsTotalCount()			{ return nTotalCount; 										}

	public void RebuildFSTree() {
		File tBaseDir = Environment.getExternalStorageDirectory();
		nRootPath = tBaseDir.getAbsolutePath() + "/.MicroLauncherBeta/";

		nDBPath = nRootPath + "sql/";
		new File(nDBPath).mkdirs();
		
		nIconCache = nRootPath + "apps/icons/";
		new File(nIconCache).mkdirs();
		
		nIconsForSkin = nRootPath + "apps/fixed_icons/";
		new File(nIconsForSkin).mkdirs();
	}
	
	public void RebuildApplicationList() {
		ProgressDialog pd = new ProgressDialog(nContext);
	    pd.setTitle("Подождите");
	    pd.setMessage("Построение списка приложений...");
	    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
   
	    ApplicationListBuilder tALB = new ApplicationListBuilder(pd);
	    tALB.run();
	}
}