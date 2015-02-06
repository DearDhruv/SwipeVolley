/**
 * 
 */

package com.deardhruv.swipevolley.network;

import java.net.URLEncoder;

import android.os.Bundle;

/**
 * @author DearDhruv
 */
public class ServiceURL {

	public static final String mainURL = "https://raw.github.com/DearDhruv/SwipeVolley/master/json_res";

	// Image Upload URL and Params NOTE: URL is for testingpurpose only.
//	public static final String imgUploadURL = "http://laendleimmo.immoservice.mobi/api/ad/upload/pictures?";
	public static final String imgUploadURL = "https://api.cloudinary.com/v1_1/demo/image/upload?";
	
	public static final String AD_ID = "adId";
	public static final String TOKEN = "token";
	public static final String QQ_FILE = "qqfile";

	// Check Uploaded Images on below URL
	// http://laendleimmo.immoservice.mobi/immobilien/gewerbliche-immobilien/buro-ordination/vorarlberg/feldkirch/73793

	@SuppressWarnings("deprecation")
	public static String encodeGETUrl(Bundle parameters) {
		StringBuilder sb = new StringBuilder();

		if (parameters != null && parameters.size() > 0) {
			boolean first = true;
			for (String key : parameters.keySet()) {
				if (key != null) {

					if (first) {
						first = false;
					} else {
						sb.append("&");
					}
					String value = "";
					Object object = parameters.get(key);
					if (object != null) {
						value = String.valueOf(object);
					}

					try {
						sb.append(URLEncoder.encode(key, "UTF-8") + "="
								+ URLEncoder.encode(value, "UTF-8"));
					} catch (Exception e) {
						sb.append(URLEncoder.encode(key) + "=" + URLEncoder.encode(value));
					}
				}
			}
		}
		return sb.toString();
	}

	public static String encodeUrl(String url, Bundle mParams) {
		return url + encodeGETUrl(mParams);
	}

}
