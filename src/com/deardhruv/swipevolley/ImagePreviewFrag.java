
package com.deardhruv.swipevolley;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.examples.toolbox.MyVolley;
import com.android.volley.toolbox.NetworkImageView;
import com.deardhruv.swipevolley.MainActivity.IUpdateImageView;

@SuppressLint("ValidFragment")
public class ImagePreviewFrag extends Fragment implements IUpdateImageView {
	View fragView;
	NetworkImageView imagePreview;
	TextView txtName;
	ItemDetail sharedItemArg;

	public ImagePreviewFrag() {
	}

	public ImagePreviewFrag(IUpdateImageView mCallback) {
		// TODO Auto-generated constructor stub
		mCallback = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
			sharedItemArg = (ItemDetail) getArguments().get("item");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragView = inflater.inflate(R.layout.image_preview_layout, container, false);
		imagePreview = (NetworkImageView) fragView.findViewById(R.id.imagePreview);
		txtName = (TextView) fragView.findViewById(R.id.txtName);
		return fragView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (sharedItemArg != null) {
			updateImagePreview(sharedItemArg);
		}
	}

	@Override
	public void updateImagePreview(ItemDetail sharedItem) {
		if (sharedItem != null) {
			imagePreview.setImageUrl(sharedItem.getImgUrl(), MyVolley.getImageLoader());
			txtName.setText(sharedItem.getName());

		}
	}

}
