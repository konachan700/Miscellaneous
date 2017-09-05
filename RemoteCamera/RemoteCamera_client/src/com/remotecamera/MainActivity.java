package com.remotecamera;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends Activity {
	ImageView 			iv;
	Button 				btnStart, btnOpts;
	TextView 			NoCam, logTxt;
	SharedPreferences 	sPref;
	Intent              optsIntent;
	TabHost             tabs;
	
	boolean 			is_started = false;
	String 				cameraURL = "";
	
	
    private class StartVideo extends AsyncTask<String, Drawable, Void> {
    	private String UrlPart = "";
    	
    	@Override
    	protected Void doInBackground(String... args) { 
    		if (is_started == false) {
    			return null;
    		}
    		
    		while (true) {
    			InputStream is;
    			try {
    				is = (InputStream) new URL(UrlPart).getContent();
    				Drawable img = Drawable.createFromStream(is, "src");
    				if (is_started == true) { publishProgress(img); } else { publishProgress(); }
    				is.close();
    			} catch (MalformedURLException e) {
    				publishProgress();
    			} catch (IOException e) {
    				publishProgress();
    			}
    			
    			if (is_started == false) {
    				publishProgress();
        			return null;
        		}
    		}
    	}
    	
    	@Override
    	protected void onProgressUpdate(Drawable... d) {
    		try { 
    			iv.setImageDrawable(d[0]); 
    			NoCam.setVisibility(4);
	    		logTxt.setVisibility(4);
    		} catch (ArrayIndexOutOfBoundsException e) {
    			iv.setImageDrawable(null);
    			iv.setBackgroundColor(getResources().getColor(R.color.DummyDarkRed)); 
    			NoCam.setVisibility(0);
    			logTxt.setVisibility(0);
    		}
    	}

    	@Override
    	protected void onPreExecute() {
    		UrlPart = cameraURL;
    	}
    	
    	@Override 
    	protected void onPostExecute(Void args) {
    		
    	}
    }
    
    void __get_settings() {
        sPref = getSharedPreferences("RemoteCamera", MODE_PRIVATE);
        String cam_ip = sPref.getString("cam_ip_1", "127") + "." +  sPref.getString("cam_ip_2", "0") + "." + 
        		        sPref.getString("cam_ip_3", "0")   + "." +  sPref.getString("cam_ip_4", "1");
        String cam_port = sPref.getString("cam_port", "2999");
        //String svc_port = sPref.getString("svc_port", "2998");
        String command_line = sPref.getString("command_line", "/jpg/headers/");
        cameraURL = "http://" + cam_ip + ":" + cam_port + command_line;
        logTxt.setText(cameraURL);
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
        
        btnStart = (Button) findViewById(R.id.button1);
        btnOpts  = (Button) findViewById(R.id.button2);
        iv = (ImageView) findViewById(R.id.imageView1); 
        NoCam = (TextView) findViewById(R.id.textView1);
        logTxt = (TextView) findViewById(R.id.textView2);
        tabs = (TabHost) findViewById(android.R.id.tabhost);
        
        tabs.setup();
        TabHost.TabSpec spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
		spec.setIndicator(getResources().getText(R.string.tabMain));
		tabs.addTab(spec);
		spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
		spec.setIndicator(getResources().getText(R.string.tabCamera));
		tabs.addTab(spec);
		
		
		
        
        __get_settings();
        
        optsIntent = new Intent(this, AppSettings.class);

        btnStart.setOnClickListener(
        		new OnClickListener() {
        		       @Override
        		       public void onClick(View v) {
        		    	   if (is_started == false) {
        		    		   __get_settings();
        		    		   is_started = true;
        		    		   btnStart.setText(getResources().getText(R.string.Stop));
        		    		   new StartVideo().execute(""); 
        		    	   } else {
        		    		   btnStart.setText(getResources().getText(R.string.Start));
        		    		   is_started = false;
        		    		   try { Thread.sleep(500); } catch (InterruptedException e) {}
        		    		   iv.setImageDrawable(null);
        		    		   iv.setBackgroundColor(getResources().getColor(R.color.DummyDarkRed)); 
        		    		   NoCam.setVisibility(0);
        		    		   logTxt.setVisibility(0);
        		    	   }
        		       }
        		     }
        		);
        
        btnOpts.setOnClickListener(
        		new OnClickListener() {
     		       @Override
     		       public void onClick(View v) {
     		    	   startActivity(optsIntent);
     		       }
        		}
        	);
    }

    @Override
    protected void onPause() {
      super.onPause();
      is_started = false;
      btnStart.setText(getResources().getText(R.string.Start));
    }
    
    @Override
    protected void onStop() {
      super.onStop();
      is_started = false;
      btnStart.setText(getResources().getText(R.string.Start));
    }
    
    @Override
    protected void onDestroy() {
      super.onStop();
      is_started = false;
      btnStart.setText(getResources().getText(R.string.Start));
    }
    
    //@Override
   // public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.main, menu);
    //    return true;
    //}
}
