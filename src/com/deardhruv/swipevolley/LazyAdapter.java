package com.deardhruv.swipevolley;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.examples.toolbox.MyVolley;
import com.android.volley.toolbox.NetworkImageView;

public class LazyAdapter extends BaseAdapter {
	
	private Activity				activity;
	private static LayoutInflater	inflater	= null;
	private ArrayList<ItemDetail>	mList;
	
	public LazyAdapter(Activity a, List<ItemDetail> list) {
		activity = a;
		mList = new ArrayList<ItemDetail>(list);
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return mList.size();
	}
	
	public Object getItem(int position) {
		return mList.get(position);
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_listview_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.image = (NetworkImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.image.setDefaultImageResId(R.drawable.ic_launcher);
		holder.image.setImageUrl(mList.get(position).getImgUrl(), MyVolley.getImageLoader());
		holder.text.setText("" + mList.get(position).getName());
		
		// convertView.setTag(holder);
		return convertView;
	}
	
	class ViewHolder {
		TextView			text;
		NetworkImageView	image;
	}
}
