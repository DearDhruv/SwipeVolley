
package com.android.volley.toolbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

public class CustomNetworkImageView extends NetworkImageView {

	private Bitmap mLocalBitmap;

	private boolean mShowLocal;

	public void setLocalImageBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			mShowLocal = true;
		}
		this.mLocalBitmap = bitmap;
		requestLayout();
	}

	@Override
	public void setImageUrl(String url, ImageLoader imageLoader) {
		mShowLocal = false;
		super.setImageUrl(url, imageLoader);
	}

	public CustomNetworkImageView(Context context) {
		this(context, null);
	}

	public CustomNetworkImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

		super.onLayout(changed, left, top, right, bottom);
		if (mShowLocal) {
			setImageBitmap(mLocalBitmap);
		}
	}

}
