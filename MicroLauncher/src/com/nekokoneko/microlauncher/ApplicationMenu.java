package com.nekokoneko.microlauncher;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.Window;

public class ApplicationMenu extends Activity{
	@SuppressLint("ValidFragment")
	class GlobalOptions extends PreferenceFragment {
		ListPreference tTagLP;
		EditTextPreference tTextLP;
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.options_screen_01);
	        
	        tTagLP = (ListPreference) getPreferenceManager().findPreference("change_char_list");
	        ArrayList<String> list = Sprites.getCharsList();
	        if (list == null) list = new ArrayList<String>();
	        
	        list.add("Default");
	        tTagLP.setEntries(list.toArray(new String[list.size()])); 
	        tTagLP.setEntryValues(list.toArray(new String[list.size()]));
	        
	        Preference p = (Preference) this.getPreferenceManager().findPreference("change_wallpapers");
	        p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@SuppressLint("ServiceCast")
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					final Intent 		nIntent1 			= new Intent(Intent.ACTION_SET_WALLPAPER);
					Intent 				nIntent2 			= Intent.createChooser(nIntent1, "");
					WallpaperManager 	nWallpaperManager 	= (WallpaperManager) getSystemService(Context.WALLPAPER_SERVICE);
					WallpaperInfo 		nWallpaperInfo 		= nWallpaperManager.getWallpaperInfo();

			        if (nWallpaperInfo != null && nWallpaperInfo.getSettingsActivity() != null) {
			            LabeledIntent nLabeledIntent = new LabeledIntent(getPackageName(), "Configure...", 0);
			            nLabeledIntent.setClassName(nWallpaperInfo.getPackageName(), nWallpaperInfo.getSettingsActivity());
			            nIntent2.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { nLabeledIntent });
			        }
			        startActivity(nIntent2);
					
					return false;
				}
	        });
		}
	}
	
	@SuppressLint("ValidFragment")
	class ApplicationOptions extends PreferenceFragment {
		ListPreference tTagLP;
		EditTextPreference tTextLP;
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.apps_options);

	        tTagLP = (ListPreference) getPreferenceManager().findPreference("application_tag");
	        tTagLP.setDialogTitle("");
	        tTagLP.setEnabled(true);
	        tTagLP.setTitle("Категория приложения");
	        tTagLP.setEntries(new String[] {"Топ-5", "Избранное", "Общее", "Малоиспользуемое"});
	        tTagLP.setEntryValues(new String[] {"9000", "43", "0", "1"});
	        
	        tTextLP = (EditTextPreference) getPreferenceManager().findPreference("application_rename");
	        tTextLP.setTitle("Отображаемое имя");
	        tTextLP.setSummary(tTextLP.getText());
		}
	}

	private int nDialogType = 0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        nDialogType  = this.getIntent().getIntExtra("DialogType", 0);
        
        switch (nDialogType) {
        case 0:
        	getFragmentManager().beginTransaction().replace(android.R.id.content, new ApplicationOptions()).commit();
        	break;
        case 1:
        	getFragmentManager().beginTransaction().replace(android.R.id.content, new GlobalOptions()).commit();
        	break;
        }
    }

	@Override
    protected void onResume() {
		super.onResume();
	}
	
	@Override
    protected void onPause() {
		super.onPause();
	}
	
	@Override
    public void onDestroy() {
		super.onDestroy();
	}
}
