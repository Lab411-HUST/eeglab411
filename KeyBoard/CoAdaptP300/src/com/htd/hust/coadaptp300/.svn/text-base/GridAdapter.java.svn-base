package com.htd.hust.coadaptp300;

import com.htd.hust.coadaptp300.MainActivity.Train;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GridAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;

	public GridAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	static class ViewHolder {
		public TextView tv;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.textview_simple, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.tv = (TextView) view.findViewById(R.id.textview);
			view.setTag(viewHolder);

			System.out.println("Position: " + position + ", Text: "
					+ getItem(position));
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag();
		viewHolder.tv.setText(getItem(position));

		viewHolder.tv.setGravity(Gravity.CENTER);

		return view;
	}
}
