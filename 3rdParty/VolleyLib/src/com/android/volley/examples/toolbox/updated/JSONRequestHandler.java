
package com.android.volley.examples.toolbox.updated;

import java.io.File;

import org.json.JSONObject;

import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.examples.toolbox.MultipartRequest;
import com.android.volley.examples.toolbox.MyVolley;
import com.android.volley.toolbox.JsonObjectRequest;

public class JSONRequestHandler {

	private int reqCode;
	private IJSONParseListener listner;

	private boolean hasFile = false;
	private boolean isRetryPolicyUpdated = false;
	private String file_path = "", key = "";

	private boolean hasMultipleFiles = false;
	private Bundle fileBundle;

	/** The current timeout in milliseconds. */
	private int mCurrentTimeoutMs;

	/** The maximum number of attempts. */
	private int mMaxNumRetries;

	/** The backoff multiplier for for the policy. */
	private float mBackoffMultiplier;

	public void getResponse(String url, final int requestCode, IJSONParseListener mParseListener) {
		getResponse(url, requestCode, mParseListener, null);
	}

	public void getResponse(String url, final int requestCode, IJSONParseListener mParseListener,
			Bundle params) {
		this.listner = mParseListener;
		this.reqCode = requestCode;

		Response.Listener<JSONObject> sListener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (listner != null) {
					listner.SuccessResponse(response, reqCode);
				}
			}
		};

		Response.ErrorListener eListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (listner != null) {
					listner.ErrorResponse(error, reqCode);
				}
			}
		};

		if (!hasFile) {
			JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
					sListener, eListener);
			addToRequestQueue(jsObjRequest);
		} else {
			if (file_path != null) {
				File mFile = new File(file_path);
				MultipartRequest multipartRequest;
				if (hasMultipleFiles) {
					multipartRequest = new MultipartRequest(url, eListener, sListener, fileBundle,
							params);
				} else {
					multipartRequest = new MultipartRequest(url, eListener, sListener, key, mFile,
							params);
				}
				addToRequestQueue(multipartRequest);
			} else {
				throw new NullPointerException("File path is null");
			}
		}
	}

	void addToRequestQueue(Request<?> request) {
		if (isRetryPolicyUpdated) {
			request.setRetryPolicy(new DefaultRetryPolicy(mCurrentTimeoutMs, mMaxNumRetries,
					mBackoffMultiplier));
		}
		MyVolley.getRequestQueue().add(request);
	}

	/**
	 * Constructs a new retry policy.
	 * 
	 * @param initialTimeoutMs The initial timeout for the policy.
	 * @param maxNumRetries The maximum number of retries.
	 * @param backoffMultiplier Backoff multiplier for the policy.
	 */
	public void setRetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier) {
		mCurrentTimeoutMs = initialTimeoutMs;
		mMaxNumRetries = maxNumRetries;
		mBackoffMultiplier = backoffMultiplier;
		isRetryPolicyUpdated = true;
	}

	public void setIntialTimeOut(int initialTimeoutMs) {
		mCurrentTimeoutMs = initialTimeoutMs;
		mMaxNumRetries = DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
		mBackoffMultiplier = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
		isRetryPolicyUpdated = true;
	}

	/**
	 * @return hasFile whether file is set or not
	 */
	public boolean hasFile() {
		return hasFile;
	}

	/**
	 * @param isFile the File to set for uploading...
	 */
	public void setFile(String param, String path) {
		hasMultipleFiles = false;

		if (path != null && param != null) {
			key = param;
			file_path = path;
			hasFile = true;
		}
	}

	public void setFile(Bundle b) {
		hasMultipleFiles = true;
		fileBundle = b;
		hasFile = true;
	}

}
