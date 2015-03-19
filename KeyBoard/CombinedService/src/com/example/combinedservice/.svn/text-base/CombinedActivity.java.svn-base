package com.example.combinedservice;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CombinedActivity extends Activity implements OnClickListener {
	Button btnOk,btnCancel;
	RadioGroup rg;
	RadioButton rb1,rb2;
	

	public static final String ANY = "com.menny.android.keyboard";
	public static final String COADAPT = "com.htd.hust.coadapt300";
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combined);
		
		rg=(RadioGroup) findViewById(R.id.group);
		rb1=(RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		btnOk=(Button) findViewById(R.id.btnOK);
		btnCancel=(Button) findViewById(R.id.btnCancel);
		
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
	}


	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btnOK)
		{
			switch (rg.getCheckedRadioButtonId()) {
			case R.id.rb1:
				//Start AnyKeyboard
				startActivity(new Intent(ANY));
				break;

			case R.id.rb2:
				//Start CoAdapt
				startActivity(new Intent(COADAPT));
				break;
			default:
				break;
			}
		}
		if (v.getId() == R.id.btnCancel)
		{
			finish();
		}
		
	}
	

}
