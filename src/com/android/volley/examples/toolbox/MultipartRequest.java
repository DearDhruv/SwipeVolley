
package com.android.volley.examples.toolbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.examples.toolbox.updated.FLog;
import com.android.volley.toolbox.HttpHeaderParser;

public class MultipartRequest extends Request<JSONObject>
{

	private MultipartEntity entity = new MultipartEntity();
	private static String FILE_PART_NAME = "";

	private final Response.Listener<JSONObject> mListener;
	private final File mFilePart;

	public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<JSONObject> listener,
			String key, File file, Bundle parameters)
	{
		super(Method.POST, url, errorListener);
		FILE_PART_NAME = key;
		mListener = listener;
		mFilePart = file;
		buildMultipartEntity(parameters);
	}

	private void buildMultipartEntity(Bundle parameters)
	{
		entity = encodePOSTUrl(entity, parameters);
		entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
	}

	public static MultipartEntity encodePOSTUrl(MultipartEntity mEntity, Bundle parameters)
	{
		if (parameters != null && parameters.size() > 0)
		{
			boolean first = true;
			for (String key : parameters.keySet())
			{
				if (key != null)
				{
					if (first)
					{
						first = false;
					}
					String value = "";
					Object object = parameters.get(key);
					if (object != null)
					{
						value = String.valueOf(object);
					}
					try
					{
						mEntity.addPart(key, new StringBody(value));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return mEntity;
	}

	@Override
	public String getBodyContentType()
	{
		return entity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			entity.writeTo(bos);
		}
		catch (IOException e)
		{
			FLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response)
	{
		try
		{
			try
			{
				String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
				return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
			}
			catch (UnsupportedEncodingException e)
			{
				return Response.error(new ParseError(e));
			}
			catch (JSONException je)
			{
				return Response.error(new ParseError(je));
			}

			// return Response.success(new JSONObject("{test:Uploaded}"),
			// getCacheEntry());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void deliverResponse(JSONObject response)
	{
		try
		{
			mListener.onResponse(response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
