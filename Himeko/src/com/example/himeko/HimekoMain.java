package com.example.himeko;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class HimekoMain extends Activity {
	
	Button b1, b2;
	EditText et1, et2;
	SharedPreferences sPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_himeko_main);
		
		b1 = (Button) findViewById(R.id.okbtnx);
		b2 = (Button) findViewById(R.id.resetbtn);
		
		et1 = (EditText) findViewById(R.id.qstn_et);
		et2 = (EditText) findViewById(R.id.logs_t);
		
		sPref = getSharedPreferences("himeko_a", MODE_PRIVATE);
		et2.setText(sPref.getString("logs", ""));

		b2.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { et1.setText(""); }}); 
		b1.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { 
			if (et1.getText().toString().length() > 2) {
				et1.append("?");
				File f = new File("/dev/urandom");
				if (f.canRead()) {
					try {
						FileInputStream fis = new FileInputStream("/dev/urandom");
						int b = 0;
						for (int i=0; i<43437; i++) {
							int a = fis.read();
							if (a < 1) {
								b = a;
							} else {
								b = (b + a) / 2;
							}
						}
						fis.close();
						
						if (b >= 127) {
							et2.setText(et1.getText().toString() + " [Ответ оракула: ДА]\r\n***\r\n" + et2.getText().toString());
						} else {
							et2.setText(et1.getText().toString() + " [Ответ оракула: НЕТ]\r\n***\r\n" + et2.getText().toString());
						}
						
						if (et2.getText().toString().length() > 1) {
							String x[] = et2.getText().toString().split("\\*\\*\\*\r\n");
							if (x.length > 25) {
								et2.setText("");
								for (int i=0; i<25; i++) {
									et2.append(x[i]+ "***\r\n");
								}
							}
						}
						
						Editor ed = sPref.edit();
						ed.putString("logs", et2.getText().toString());
						ed.commit();
					} catch (FileNotFoundException e) { } 
					  catch (IOException e) { }
				} else {
					et2.append("Не могу получить доступ к устройству /dev/urandom\r\n***");
				}
				et1.setText("");
			}
		}});
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.himeko_main, menu);
		return true;
	}
	*/
	
	@Override
    protected void onPause() {
		super.onPause();
		finish();
	}
}
