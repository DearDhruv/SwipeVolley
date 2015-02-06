
package com.deardhruv.swipevolley;

import android.app.Application;
import android.content.Context;

import com.android.volley.examples.toolbox.MyVolley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class BaseApplication extends Application {

	private static Context mContext = null;
	private static final int DISK_IMAGE_CACHE_SIZE = 50 * 1024 * 1024; // 50 MB

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();

		MyVolley.init(getApplicationContext());
		initImageLoader();
	}

	private void initImageLoader() {
		// UnlimitedDiscCache is used by default. Only the size has to be set.
		// Memory cache is set by default.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
				.diskCacheSize(DISK_IMAGE_CACHE_SIZE).build();

		ImageLoader.getInstance().init(config);
	}

	public static Context getAppContext() {
		return mContext;
	}

}
