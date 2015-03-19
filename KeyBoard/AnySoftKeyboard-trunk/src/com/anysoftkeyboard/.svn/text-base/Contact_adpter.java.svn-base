package com.anysoftkeyboard;

import java.util.ArrayList;
import java.util.List;

import com.menny.android.anysoftkeyboard.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Contact_adpter extends ArrayAdapter<Contract_Model>{
    ArrayList<Contract_Model> arr; 
	public Contact_adpter(Context context, int resource,
			 ArrayList<Contract_Model> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub\
		arr=objects;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr.size();
	}
	@Override
	public Contract_Model getItem(int position) {
		// TODO Auto-generated method stub
		return arr.get(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row=convertView;
		if(row==null)
		{
			LayoutInflater lay=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row=lay.inflate(R.layout.itemlayout, parent, false);
			
		}
		Contract_Model model=arr.get(position);
		TextView tx1=(TextView)row.findViewById(R.id.txtTitle);
		TextView tx2=(TextView)row.findViewById(R.id.txtUrl);
		tx1.setText(model.get_Name());
		tx2.setText(model.get_Phonenumber());
		
		return row;
	}
	
	
	
	
}
