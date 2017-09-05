package com.remotecamera;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AppSettings extends Activity {
	EditText ip1, ip2, ip3, ip4, svcport, camport, cmdln;
	Button save;
	SharedPreferences sPref;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        sPref = getSharedPreferences("RemoteCamera", MODE_PRIVATE);
        
        ip1 = (EditText) findViewById(R.id.editText1);
        ip2 = (EditText) findViewById(R.id.EditText01);
        ip3 = (EditText) findViewById(R.id.EditText02);
        ip4 = (EditText) findViewById(R.id.EditText03);
        
        camport = (EditText) findViewById(R.id.EditText04);
        svcport = (EditText) findViewById(R.id.EditText06);
        
        cmdln = (EditText) findViewById(R.id.EditText05);
        
        ip1.setText(sPref.getString("cam_ip_1", "127"));
        ip2.setText(sPref.getString("cam_ip_2", "0"));
        ip3.setText(sPref.getString("cam_ip_3", "0"));
        ip4.setText(sPref.getString("cam_ip_4", "1"));
        
        camport.setText(sPref.getString("cam_port", "2999"));
        svcport.setText(sPref.getString("svc_port", "2998"));
        
        cmdln.setText(sPref.getString("command_line", "/jpg/headers/"));
        
        save = (Button) findViewById(R.id.button1);
        save.setOnClickListener(
        		new OnClickListener() {
      		       @Override
      		       public void onClick(View v) {
      		    	   Editor ed = sPref.edit();
      		    	   ed.putString("cam_ip_1", ip1.getText().toString());
      		    	   ed.putString("cam_ip_2", ip2.getText().toString());
      		    	   ed.putString("cam_ip_3", ip3.getText().toString());
      		    	   ed.putString("cam_ip_4", ip4.getText().toString());
      		    	   
      		    	   ed.putString("cam_port", camport.getText().toString());
      		    	   ed.putString("svc_port", svcport.getText().toString());
      		    	   
      		    	   ed.putString("command_line", cmdln.getText().toString());
      		    	   
      		    	   ed.commit();
      		       }
         		}
         	);
	}
}
