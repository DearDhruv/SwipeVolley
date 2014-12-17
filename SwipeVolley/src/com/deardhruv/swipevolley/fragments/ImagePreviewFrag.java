
package com.deardhruv.swipevolley.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.examples.toolbox.MyVolley;
import com.android.volley.toolbox.NetworkImageTouchView;
import com.deardhruv.swipevolley.R;
import com.deardhruv.swipevolley.activities.MainActivity.IUpdateImageView;
import com.deardhruv.swipevolley.model.ImageItem;

@SuppressLint("ValidFragment")
public class ImagePreviewFrag extends Fragment implements IUpdateImageView {
	View fragView;
	// NetworkImageView imagePreview;
	NetworkImageTouchView imagePreview;
	TextView txtName;
	ImageItem sharedItemArg;

	public ImagePreviewFrag() {
	}

	public ImagePreviewFrag(IUpdateImageView mCallback) {
		mCallback = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
			sharedItemArg = (ImageItem) getArguments().get("item");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragView = inflater.inflate(R.layout.image_preview_layout, container, false);
		imagePreview = (NetworkImageTouchView) fragView.findViewById(R.id.imagePreview);
		txtName = (TextView) fragView.findViewById(R.id.txtName);
		return fragView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (sharedItemArg != null) {
			updateImagePreview(sharedItemArg);
		}
	}

	@Override
	public void updateImagePreview(ImageItem sharedItem) {
		if (sharedItem != null) {
			// Replace all spaces from URL.
			String str = sharedItem.getImgUrl().replaceAll("[ ]", "%20");
			imagePreview.setImageUrl(str, MyVolley.getImageLoader());
			txtName.setText(sharedItem.getName());

		}
	}

}
