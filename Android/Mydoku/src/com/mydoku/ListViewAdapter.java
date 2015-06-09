package com.mydoku;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Class used to put data in the ListView
 * 
 * @author Christer
 * @version 1.0
 * @since 2014-08-21
 */
public class ListViewAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<String> list;

	/**
	 * Consctructor
	 * 
	 * @param activity
	 * @param list
	 */
	public ListViewAdapter(Activity activity, ArrayList<String> list) {
		super();
		this.activity = activity;
		this.list = list;
	}

	/**
	 * Gets the size of the list.
	 */
	@Override
	public int getCount() {
		return list.size();
	}

	/**
	 * Gets an item at position
	 */
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	/**
	 * Gets itemid.
	 */
	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	/**
	 * Inner class containing the textview to put text in.
	 * 
	 * @author Christer
	 * 
	 */
	private class ViewHolder {
		TextView txtFirst;
		TextView txtSecond;
		TextView txtThird;
		TextView txtFourth;
		TextView txtFifth;
	}

	/**
	 * Creates a view for the row and returns it.
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_row, null);
			holder = new ViewHolder();
		
			holder.txtFirst = (TextView) convertView
					.findViewById(R.id.FirstText);
			
			holder.txtSecond = (TextView) convertView
					.findViewById(R.id.SecondText);
			
			holder.txtThird = (TextView) convertView
					.findViewById(R.id.ThirdText);

			holder.txtFourth = (TextView) convertView
					.findViewById(R.id.FourthText);

			holder.txtFifth = (TextView) convertView
					.findViewById(R.id.FifthText);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		List<String> profile = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(list.get(position));
		while (st.hasMoreTokens()) {
			profile.add(st.nextToken());			
		}

		
		holder.txtFirst.setText(profile.get(0));
		holder.txtSecond.setText(profile.get(1));
		holder.txtThird.setText(profile.get(2));
		holder.txtFourth.setText(profile.get(3));
		holder.txtFifth.setText(profile.get(4));

		return convertView;
	}
}
