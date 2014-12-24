
package com.deardhruv.swipevolley.adapters;

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
import com.deardhruv.swipevolley.R;
import com.deardhruv.swipevolley.model.ImageItem;

public class ItemDetailAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	private ArrayList<ImageItem> mList;

	public ItemDetailAdapter(Activity a, List<ImageItem> list) {
		activity = a;
		mList = new ArrayList<ImageItem>(list);
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
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
		holder.image.setErrorImageResId(R.drawable.ic_image_err);

		// Replace all spaces from URL.
		String str = mList.get(position).getImgUrl().replaceAll("[ ]", "%20");
		holder.image.setImageUrl(str, MyVolley.getImageLoader());
		holder.text.setText("" + mList.get(position).getName());

		// convertView.setTag(holder);
		return convertView;
	}

	class ViewHolder {
		TextView text;
		NetworkImageView image;
	}
}
