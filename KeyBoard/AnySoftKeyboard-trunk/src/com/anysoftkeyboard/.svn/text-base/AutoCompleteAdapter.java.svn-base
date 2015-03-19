package com.anysoftkeyboard;

import java.io.FileInputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.menny.android.anysoftkeyboard.R;
public class AutoCompleteAdapter extends ArrayAdapter<Model> {
	ArrayList<Model> array;
    private int index=0;
	public AutoCompleteAdapter(Context context, int layout) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.itemlayout);
	}

	public AutoCompleteAdapter(Context context, int layout,
			ArrayList<Model> values) {
		super(context, layout, values);
		this.array = values;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array.size();
	}

	@Override
	public Model getItem(int position) {
		// TODO Auto-generated method stub
		return array.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;
		ViewHolder holder = null;
	final	Model item = array.get(position);
        index=position;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.itemlayout, parent, false);

			holder = new ViewHolder();
			holder.title = (TextView) row.findViewById(R.id.txtTitle);
			holder.url = (TextView) row.findViewById(R.id.txtUrl);
            holder.bt=(Button)row.findViewById(R.id.bt1); 
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		holder.title.setText(item.getTitle());
		holder.url.setText(item.getUrl());
		holder.bt.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Bundle bund=new Bundle();
				bund.putString("url", item.getUrl());
				Intent send=new Intent("lab411.eeg.selecturl_1");
				send.putExtras(bund);
				getContext().sendBroadcast(send);
					
			}
		});
		return (row);
	}

	public static class ViewHolder {
		TextView title, url;
		Button bt;
	}
	
}
