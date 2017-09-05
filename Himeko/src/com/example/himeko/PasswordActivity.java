package com.example.himeko;

import com.example.himeko.R;

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
import android.widget.TextView;

public class PasswordActivity extends Activity {

	Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnOK, btnDEL;
	EditText password;
	TextView message;
	SharedPreferences sPref;
	Intent mainfrm;
	
	String xpassword = null, npassword = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password);
		
		btn1 	= (Button) findViewById(R.id.Button03);
		btn2 	= (Button) findViewById(R.id.Button02);
		btn3 	= (Button) findViewById(R.id.Button01);
		btn4 	= (Button) findViewById(R.id.Button04);
		btn5 	= (Button) findViewById(R.id.Button06);
		btn6 	= (Button) findViewById(R.id.Button05);
		btn7 	= (Button) findViewById(R.id.Button09);
		btn8 	= (Button) findViewById(R.id.Button08);
		btn9 	= (Button) findViewById(R.id.Button07);
		btn0 	= (Button) findViewById(R.id.Button11);
		btnOK 	= (Button) findViewById(R.id.Button10);
		btnDEL 	= (Button) findViewById(R.id.Button12);
		
		password = (EditText) findViewById(R.id.editText1);
		
		message = (TextView) findViewById(R.id.textView1);
		
		btn1.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("1"); }});
		btn2.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("2"); }});
		btn3.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("3"); }});
		btn4.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("4"); }});
		btn5.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("5"); }});
		btn6.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("6"); }});
		btn7.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("7"); }});
		btn8.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("8"); }});
		btn9.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("9"); }});
		btn0.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { password.append("0"); }});
		
		mainfrm = new Intent(this, HimekoMain.class);

		btnDEL.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { 
			String s = password.getText().toString();
			if (s.length() > 0) password.setText(s.substring(0, s.length()-1)); 
		}});
		
		btnOK.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { 
			if (xpassword == null) {
				if (npassword == null) {
					npassword = password.getText().toString();
					message.setText("Введите новый пароль еще раз");
				} else {
					if (npassword.equals(password.getText().toString())) {
						xpassword = password.getText().toString();
						Editor ed = sPref.edit();
						ed.putString("password", xpassword);
						ed.commit();
						/* code here */
						startActivity(mainfrm);
						finish();
					} else {
						npassword = null;
						message.setText("Ошибка ввода. Введите новый пароль.");
					}
				}
			} else {
				if (xpassword.equals(password.getText().toString())) {
					message.setText("Пароль введен верно");
					/* code here */
					startActivity(mainfrm);
					finish();
				} else {
					message.setText("Пароль введен неверно, попробуйте еще раз");
				}
			}
			password.setText("");
		}});
		
		sPref = getSharedPreferences("himeko_a", MODE_PRIVATE);
		xpassword = sPref.getString("password", null);
		if (xpassword == null) {
			message.setText("Введите новый пароль.");
		}
	}
	
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.password, menu);
		return true;
	}
*/
	
}
