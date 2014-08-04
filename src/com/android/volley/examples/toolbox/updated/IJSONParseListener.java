
package com.android.volley.examples.toolbox.updated;

import org.json.JSONObject;

import com.android.volley.VolleyError;

/**
 * Interface for responses
 * 
 * @author DearDhruv
 */
public interface IJSONParseListener {

	// Interface methods for Responses

	// void ErrorResponse(VolleyError error, int requestCode, int errorcode);

	/**
	 * Invoked when any network failure or JSON parsing failure.
	 * 
	 * @param error
	 * @param requestCode
	 */
	void ErrorResponse(VolleyError error, int requestCode);

	/**
	 * Invoked when successful response and successful JSON parsing completed.
	 * 
	 * @param response
	 * @param requestCode
	 */
	void SuccessResponse(JSONObject response, int requestCode);
}
