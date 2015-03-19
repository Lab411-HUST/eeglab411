package com.anysoftkeyboard;

import java.util.ArrayList;

import com.anysoftkeyboard.keyboards.views.AnyKeyboardView;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UrlService extends Service {
	//tao giao dien
	WindowManager mWindowManager;
	WindowManager.LayoutParams mParams;
	ListView mListView;
	Button mButton;
	FrameLayout  mLayout;
	//
	private String mKey="";
	String word="";
	BroadcastReceiver rce;
	Cursor result = null;
	ArrayList<Model> model = new ArrayList<Model>();
	private int sizeArry=0;
	private int i=0;
	private int count=0;
	private int index=0;
	private Boolean mExitLoop=false;
	private Boolean state=false;
	//
	private ArrayList<String > ad=new ArrayList<String>();
	private AutoCompleteAdapter adapter=null;
	Model Mode;
	Uri Contract=Uri.parse("content://browser/bookmarks");
    String[] projection=new String[]{"title","url"}; // xac dinh so cot
	//
	Handler process1 =new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			try
			{
				
			if(state==true)
			{
			i=count/8;
			
			if(index==0)
			{
				if(model.size()>0)
				((View) getViewByPosition(i,mListView)).setBackgroundColor(Color.parseColor("#FF8C00"));	
				//mListView.getChildAt(i).setBackgroundColor(Color.parseColor("#FF8C00"));
				if (i==0)
				mListView.setSelection(i);
				else
				mListView.setSelection(i-1);
				index=1;
			}
			else
			{
				if(model.size()>0)
				((View) getViewByPosition(i, mListView))
					.setBackgroundColor(Color.WHITE);	
				if (i==0)
				mListView.setSelection(i);
				else
				mListView.setSelection(i-1);
				index=0;
			}
              count++;
			
			if(count==(sizeArry*8)) //size*8
			{ 
				//a=count;
				count=0;
			}
			}
			}
			catch(Exception e)
			{
				System.out.println("DEBUG:"+e.toString());
			}
		}
		
	};
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		//createWindow();
		runThread();
		//Toast.makeText(getApplicationContext(), "creat1",Toast.LENGTH_LONG).show();
		super.onCreate();
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
		
		while(mExitLoop==false)
		{
			mExitLoop=true;
		}
		if(state)
		mWindowManager.removeView(mLayout);
		super.onDestroy();
	}
	public void process()
    {
    	rce=new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if(intent.getAction().equals("lab411.eeg.key"))
				{
				String key="";
				Bundle minh=intent.getExtras();
				mKey=minh.getString("primaryCode");
				key=minh.getString("key");
				int b=Integer.parseInt(mKey);
				if(b==-5)
				{
					if(word.length()>1)
					word=word.substring(0,word.length()-1);
					else
					word="";
				}
				else
				word=word+key;
				//Toast.makeText(getApplicationContext(),word, 0).show();
								
				try{
					if(((b>=32)&&(b<=137))||(b==-5))
					{
						
						
						Cursor d=null;
				       // d=getContentResolver().query(Contract, projection," title LIKE "+"'%" + word + "%'",null, " title ASC limit 3");
						d=getContentResolver().query(Contract, projection," title LIKE "+"'%" + word + "%'",null, null);
						if(word.equals(""))
				        	d=null;
				        if(model.size()>0)
				        	model.removeAll(model);
				       
				        if(d!=null)
				        { 
				        	
				        					        	
				        	while(d.moveToNext())
				        	{
				        		
				        		Mode=new Model();
				        		Mode.setTitle(d.getString(0));
				        		Mode.setUrl(d.getString(1));
				        		model.add(Mode);
				        	}
				           d.close();
				        }
				        if(ad.size()>0)
				        ad.removeAll(ad);
				        if(!word.equals(""))
				        {
				        Mode=new Model();
		        		Mode.setTitle(word);
		        		Mode.setUrl("Tim kiem google");
		        		model.add(Mode);
				        }
				        if(model.size()>0)
				        {
				        	
							count=0;
							index=0;
							for (Model res : model)
							{
							
								ad.add(res.getUrl());
							}	
														
				        }
				        if(model.size()>0)
				        {
				        	if(!state)
				        	{
				        		createWindow();
				        		state=true;
				        	}
				        }
				        else
				        {
				        	if(state)
				        	{
				        		mWindowManager.removeView(mLayout);
				        		state=false;
				        	}
				        }
				        adapter = new AutoCompleteAdapter(getBaseContext(),R.layout.itemlayout,model);
						sizeArry=ad.size();// tra sua
						mListView.setAdapter(adapter);
						
										
				}
				}
				catch(Exception e)
				{
					
					
				}
				
				}
				if(intent.getAction().equals("lab411.eeg.selecturl"))
				{
													
					String c=ad.get(i);
					Bundle bund=new Bundle();
					//Intent write=new Intent("ghivao");
					Intent write=new Intent("lab411.eeg.sendkey");
					bund.putString("url",c);
					write.putExtras(bund);
					sendBroadcast(write);
									
					
				}
				
				
			}
		};
		
		registerReceiver(rce, new IntentFilter("lab411.eeg.key"));//report
		registerReceiver(rce, new IntentFilter("lab411.eeg.selecturl"));
		
    }
	public void createWindow()
	{  
			 
		mLayout = new FrameLayout(this);
		mLayout.setBackgroundColor(Color.WHITE);
		mListView=new ListView(this);
		mButton=new Button(this);
		mButton.setBackgroundColor(Color.parseColor("#00000000"));
  		mParams = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ALERT,
				LayoutParams.FLAG_NOT_FOCUSABLE|LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.TOP | Gravity.LEFT;// tra sua

		mParams.x =30;// 180 cho tab, 30 note
		mParams.y =120;//80 note ,120 note
        mParams.width=1000;
        mParams.height=500;// 300 tab ,500 note
        mLayout.addView(mListView);
		mLayout.addView(mButton);
		try
		{
		mWindowManager.addView(mLayout, mParams);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		mButton.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				stopSelf();
				AnySoftKeyboard.mCheckStateWedService=false;
				return false;
			}
		});
				
	}
    public void runThread()
	{
    	
		 Thread Proces2=new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				while(true)
				{
					if(mExitLoop==true)
						break;
				try {
					Thread.sleep(500);
					process1.sendMessage(process1.obtainMessage());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
		});
		 Proces2.start();
	}
     public View getViewByPosition(int position, ListView lv) {
		final int firstListItemPosition = lv.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition
				+ lv.getChildCount() - 1;

		if (position < firstListItemPosition || position > lastListItemPosition) {
			return lv.getAdapter().getView(position, null, lv);
		} else {
			final int childIndex = position - firstListItemPosition;
			return lv.getChildAt(childIndex);
		}
	 }


}
