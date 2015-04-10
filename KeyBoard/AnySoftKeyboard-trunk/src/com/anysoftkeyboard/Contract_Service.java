package com.anysoftkeyboard;

import java.util.ArrayList;

import com.menny.android.anysoftkeyboard.R;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Contract_Service extends Service{

	WindowManager windowManager;
	LinearLayout layout;
	LayoutParams params;
	ListView list_view;
	ArrayList<Contract_Model> arr=new ArrayList<Contract_Model>();
	Cursor cur;
	Contract_Model model;
	ArrayList<String> phone_name=new ArrayList<String>();
	BroadcastReceiver rce;
	String word="";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		creat_window();
		get_All();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		process();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(rce);
		windowManager.removeView(layout);
		super.onDestroy();
	}

	public void creat_window()
	{
		    windowManager=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
		    layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setBackgroundColor(Color.parseColor("#DEB887"));
			list_view=new ListView(this);
	  		params = new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, LayoutParams.TYPE_SYSTEM_ALERT,
					LayoutParams.FLAG_NOT_FOCUSABLE|LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
					PixelFormat.TRANSLUCENT);
			params.gravity = Gravity.CENTER | Gravity.TOP;
		    params.y=80;
			params.height=150;
			params.width=300;
			layout.addView(list_view);
			windowManager.addView(layout, params);
	}
	public void process()
	{
	  rce=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("report"))
			{ 
				
				String keygoiy="";
				Bundle minh=intent.getExtras();
				String key=minh.getString("key");
				keygoiy=minh.getString("keygoiy");
							
				
				int b=Integer.parseInt(key);
				if(b==-5)
				{
					if(word.length()>1)
					word=word.substring(0,word.length()-1);
					else
					word="";	
				}
				else
				word=word+keygoiy;
				Log.d("report", word);
				if(((b>=32)&&(b<=137))||(b==-5))
				{
					get_word(word);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			if(intent.getAction().equals("lab411.eeg.selecturl"))
			{
                Toast.makeText(getApplicationContext(), "CAll", Toast.LENGTH_SHORT).show();
				Bundle bun=intent.getExtras();
				if(bun!=null)
				{
					
					int so=bun.getInt("ind");
					
					int a=phone_name.size();
					a=a-1;
					if(so<=a)
					{
					Contract_Model mo=new Contract_Model();
					mo=arr.get(so);
					so=so+1;
					String num=mo.get_id();
					Intent write=new Intent(Intent.ACTION_VIEW,Uri.parse("content://contacts/people/"+num));
					write.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(write);
					//Toast.makeText(getApplicationContext(), "ban chon hang "+so, Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	};
	registerReceiver(rce, new IntentFilter("lab411.eeg.selecturl"));
	registerReceiver(rce, new IntentFilter("report"));
	}
    public void get_All()
    {
    	String[] projection=new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
    			                         ContactsContract.CommonDataKinds.Phone.CONTACT_ID,ContactsContract.CommonDataKinds.Phone.NUMBER};
    	cur=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
    			                       ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +" LIKE ?", 
    			                                           new String[]{"%t%"}, null);
    	if(cur!=null)
    	{
    		while(cur.moveToNext())
    		{
    			model=new Contract_Model();
    			model.set_id(cur.getString(1));
    			model.set_Name(cur.getString(0));
    			model.set_Phonenumber(cur.getString(2));
    			arr.add(model);
    		}
    		cur.close();
    	}
    	for (Contract_Model res : arr) {
			phone_name.add(res.get_Name());
    	}
    	Contact_adpter adapter=new Contact_adpter(getApplicationContext(), R.layout.itemlayout, arr);
    	list_view.setAdapter(adapter);
    }
    public void get_word( String word)
    {
    	String[] projection=new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
    			                         ContactsContract.CommonDataKinds.Phone.NUMBER};
         cur=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +" LIKE ?", 
                                  new String[]{"%"+word+"%"}, null);
             if(cur!=null)
              {
            	  if(arr.size()>0)
            	  arr.removeAll(arr);
                 while(cur.moveToNext())
                    {
                      model=new Contract_Model();
                      model.set_id(cur.getString(1));
                      model.set_Name(cur.getString(0));
                      model.set_Phonenumber(cur.getString(2));
                      arr.add(model);
                    }
                 cur.close();
             }
             if(phone_name.size()>0)
            	 phone_name.removeAll(phone_name);
           for (Contract_Model res : arr) {
                phone_name.add(res.get_Name());
              }
           Contact_adpter adapter=new Contact_adpter(getApplicationContext(), R.layout.itemlayout,arr);
           list_view.setAdapter(adapter);
    }
}
