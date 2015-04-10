package com.anysoftkeyboard;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UrlService extends Service {
	//windown for caching
	WindowManager mWindowManager;
	WindowManager.LayoutParams mParams;
	ListView mListView;
	Button mButton;
	FrameLayout  mLayout;
	//Frame for Dic
	WindowManager mWindowDic;
	WindowManager.LayoutParams mDicPrama;
	LinearLayout mDicLayout;
	 
	TextView tx1,tx2,tx3;
	ArrayList<String> data1;
	int mDicIndex=1;
	//
	private String mKey="";
	String word="";
	BroadcastReceiver rce;
	Cursor result = null;
	ArrayList<Model> model = new ArrayList<Model>();
	private int sizeArry=0;
	private int i=0;
	
	private Boolean mExitLoop=false;
	private Boolean state=false;
	private int newIndex=-1;
	private int oldIndex=-1;
	Handler hanler=new Handler();
	boolean mStateChose=false;
	//
	private ArrayList<String > ad=new ArrayList<String>();
	private AutoCompleteAdapter adapter=null;
	Model Mode;
	Uri Contract=Uri.parse("content://browser/bookmarks");
    String[] projection=new String[]{"title","url"}; 
	
	Handler process1 =new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			 oldIndex=newIndex;
			 newIndex++; 
			 if(newIndex==sizeArry) {
				 newIndex=0;
				 oldIndex=sizeArry-1;
				
			 }
				 
			 if(model.size()>0) {
			 ((View) getViewByPosition(newIndex,mListView)).setBackgroundColor(Color.parseColor("#FF8C00"));
			  if((newIndex!=oldIndex) && (oldIndex!=-1))
			 ((View) getViewByPosition(oldIndex,mListView)).setBackgroundColor(Color.WHITE);
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
		mWindowDic=(WindowManager)getSystemService(WINDOW_SERVICE);
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
		newIndex=-1;
		oldIndex=-1;
		unregisterReceiver(rce);
		mStateChose=false;
		while(mExitLoop==false)
		{
			mExitLoop=true;
		}
		if(state) {
		mWindowManager.removeView(mLayout);
		mWindowDic.removeView(mDicLayout);
		}
		super.onDestroy();
	}
	public int getSpaceIndex(String word) {
		int lenght=word.length()-1;
		int index=0;
		for(int i=lenght;i>=0;i--) {
			if(word.charAt(i)==' '){
				index=i;
				break;
			}
		}
		return index;
	}
	public void process()
    {
    	rce=new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if(intent.getAction().equals("lab411.eeg.Word")){
					data1=intent.getStringArrayListExtra("data");
					int n=data1.size();
					String st="";
					mDicIndex=0;
					if(n>0) {
						st=data1.get(0)+" | ";
						tx1.setText(st);
						tx1.setTextColor(Color.RED);
						tx2.setTextColor(Color.BLACK);
						tx3.setTextColor(Color.BLACK);
					}
					if(n>1) {
						st=data1.get(1)+" | ";
						tx2.setText(st);
					}
					if(n>2) {
						st=data1.get(2)+" | ";
						tx3.setText(st);
					}
				}
				if(intent.getAction().equals("lab411.eeg.gaze")) {
					
					int gaze=intent.getExtras().getInt("data");
					switch(gaze) {
					case 4:
						if(model.size()>0)
						process1.sendMessage(process1.obtainMessage());
						break;
					case 3:
						if(model.size()>0){
						if(mStateChose) {
							int size1=data1.size();
							if(size1>0) {
							Intent inDic=new Intent("lab411.eeg.Dictionary");
							inDic.putExtra("data",mDicIndex);
							sendBroadcast(inDic);
							String mWord1="";
							mWord1=data1.get(mDicIndex);
							Log.d("BroadcastReceiver", mWord1+"");
							processWord(mWord1);
							mStateChose=false;
							word+=" ";
							}
						}else {
							String c="";
							if(newIndex>=0)
							c=ad.get(newIndex);
							Bundle bund=new Bundle();
							Intent write=new Intent("lab411.eeg.sendkey");
							bund.putString("url",c);
							write.putExtras(bund);
							sendBroadcast(write);
						}
						}
						break;
					case 1:
						if(model.size()>0){
						if(newIndex>=0)	
						((View) getViewByPosition(newIndex,mListView)).setBackgroundColor(Color.WHITE);
						}
						break;
                   case 2:
                	   if(data1!=null) {
                	    mStateChose=true;
						mDicIndex++;
						int lenght2=data1.size();
						if(mDicIndex==3||mDicIndex==lenght2)
							mDicIndex=0;
						
						switch (mDicIndex) {
						case 0:
							
							tx1.setTextColor(Color.RED);
							tx2.setTextColor(Color.BLACK);
							tx3.setTextColor(Color.BLACK);
							
							break;
						case 1:
							
							tx1.setTextColor(Color.BLACK);
							tx2.setTextColor(Color.RED);
							tx3.setTextColor(Color.BLACK);
							
							break;
						case 2:
							
							tx1.setTextColor(Color.BLACK);
							tx2.setTextColor(Color.BLACK);
							tx3.setTextColor(Color.RED);
							
							break;

						default:
							break;
						}
						
						break;
					}
					}
					
				}
				if(intent.getAction().equals("lab411.eeg.key"))
				{
				mDicIndex=0;
				newIndex=-1;
				oldIndex=-1;
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
				        d=getContentResolver().query(Contract, projection," title LIKE "+"'%" + word + "%'",null, " title ASC limit 3");
						//d=getContentResolver().query(Contract, projection," title LIKE "+"'%" + word + "%'",null, null);
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
				        	
							//count=0;
							//index=0;
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
				        		creatDic();
				        		state=true;
				        	}
				        }
				        else
				        {
				        	if(state)
				        	{
				        		mWindowManager.removeView(mLayout);
				        		mWindowDic.removeView(mDicLayout);
				        		state=false;
				        	}
				        }
				        adapter = new AutoCompleteAdapter(getBaseContext(),R.layout.itemlayout,model);
						sizeArry=ad.size();
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
					Intent write=new Intent("lab411.eeg.sendkey");
					bund.putString("url",c);
					write.putExtras(bund);
					sendBroadcast(write);
									
					
				}
				
				
			}
		};
		
		registerReceiver(rce, new IntentFilter("lab411.eeg.Word"));
		registerReceiver(rce, new IntentFilter("lab411.eeg.gaze"));
		registerReceiver(rce, new IntentFilter("lab411.eeg.key"));
		registerReceiver(rce, new IntentFilter("lab411.eeg.selecturl"));
		
    }
	public void processWord(String mWord) {
		newIndex=oldIndex=-1;
		int n=word.length();
		
		if(n==1){
			word=mWord+" ";
		}
		if(n>1) {
			
			int a=getSpaceIndex(word);
			word=word.substring(0,a)+mWord;
		}
		//
		
		try {
			Cursor d=null;
	        d=getContentResolver().query(Contract, projection," title LIKE "+"'%" + word + "%'",null, " title ASC limit 3");
			//d=getContentResolver().query(Contract, projection," title LIKE "+"'%" + word + "%'",null, null);
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
	        	
				//count=0;
				//index=0;
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
	        		creatDic();
	        		state=true;
	        	}
	        }
	        else
	        {
	        	if(state)
	        	{
	        		mWindowManager.removeView(mLayout);
	        		mWindowDic.removeView(mDicLayout);
	        		state=false;
	        	}
	        }
	        adapter = new AutoCompleteAdapter(getBaseContext(),R.layout.itemlayout,model);
			sizeArry=ad.size();
			mListView.setAdapter(adapter);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public void creatDic(){
		mDicLayout=new LinearLayout(this);
		mDicLayout.setBackgroundColor(Color.WHITE);
		mDicLayout.setOrientation(LinearLayout.HORIZONTAL);
		tx1=new TextView(this);
		tx1.setTextSize(25);
		tx1.setTextColor(Color.BLACK);
		
		tx2=new TextView(this);
		tx2.setTextSize(25);
		tx2.setTextColor(Color.RED);
		
		tx3=new TextView(this);
		tx3.setTextSize(25);
		tx3.setTextColor(Color.BLACK);
		
		mDicPrama=new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ALERT,
				LayoutParams.FLAG_NOT_FOCUSABLE|LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		mDicPrama.gravity = Gravity.BOTTOM;
		mDicPrama.y =500;
		mDicPrama.width=1080;
		mDicPrama.height=144;
		mDicLayout.addView(tx1);
		mDicLayout.addView(tx2);
		mDicLayout.addView(tx3);
		try
		{
			mWindowDic.addView(mDicLayout, mDicPrama);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
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
		mParams.gravity = Gravity.TOP | Gravity.LEFT;

		mParams.x =30;
		mParams.y =120;
        mParams.width=1000;
        mParams.height=500;
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
