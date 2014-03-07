package com.deardhruv.swipevolley;

import android.app.Application;

import com.android.volley.examples.toolbox.MyVolley;

public class BaseApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		MyVolley.init(getApplicationContext());
	}
}
