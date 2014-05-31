package com.deardhruv.swipevolley;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.android.volley.examples.toolbox.updated.IParseListener;
import com.android.volley.examples.toolbox.updated.JSONRequestResponse;

/**
 * A fragment which displays the List of the images.
 */
public class ImageListFrag extends Fragment implements IParseListener, OnItemClickListener {
	private static final int	CODE_IMG_LIST	= 101;
	
	ProgressDialog				pd;
	ListView					list;
	LazyAdapter					adapter;
	View						fragView;
	
	public ImageListFrag() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
		list = (ListView) fragView.findViewById(R.id.listView1);
		return fragView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getImages(getActivity());
	}
	
	@Override
	public void onDestroy() {
		list.setAdapter(null);
		super.onDestroy();
	}
	
	boolean	isImageChanged	= false;
	
	void getImages(Context mContext) {
		pd = ProgressDialog.show(mContext, "Please wait", "getting images...");
		if (!pd.isShowing()) {
			pd.show();
		}
		
		Bundle parms = new Bundle();
		// change here 07-FEB
		// parms.putString(ServiceURL.INDENT, "");
		
		JSONRequestResponse mResponse = new JSONRequestResponse(mContext);
		if (isImageChanged) {
			// if (mImagePath != null) {
			// mResponse.setFile(ServiceURL.IMAGE_URL, mImagePath);
			// }
			// mResponse.getResponse(ServiceURL.mainURL, CODE_IMG_LIST, this,
			// parms);
		} else {
			mResponse.getResponse(ServiceURL.encodeUrl(ServiceURL.mainURL, parms), CODE_IMG_LIST, this);
		}
	}
	
	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		if (pd.isShowing()) {
			pd.dismiss();
		}
		if (requestCode == CODE_IMG_LIST) {
			Log.e("error", "" + error.toString());
		}
	}
	
	ArrayList<ItemDetail>	mList	= null;
	
	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		if (pd.isShowing()) {
			pd.dismiss();
		}
		
		if (requestCode == CODE_IMG_LIST) {
			// Log.d("reponse", "" + response.toString());
			try {
				JSONArray mJsonArray = new JSONArray(response.get("result").toString());
				if (mJsonArray.length() > 0) {
					mList = new ArrayList<ItemDetail>();
					for (int i = 0; i < mJsonArray.length(); i++) {
						ItemDetail itemDetail = new ItemDetail();
						
						itemDetail.setImgUrl(mJsonArray.getJSONObject(i).getString("img"));
						itemDetail.setName(mJsonArray.getJSONObject(i).getString("name"));
						
						mList.add(itemDetail);
						
						if (i == 10) {
							break;
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			adapter = new LazyAdapter(getActivity(), mList);
			list.setAdapter(adapter);
			list.setOnItemClickListener(ImageListFrag.this);
		}
	}
	
	public interface ShareViewItem {
		// Interface method you will call from this fragment
		public void shareItem(ItemDetail viewItem);
	}
	
	ShareViewItem	mCallback	= null;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mCallback = (ShareViewItem) activity;
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void changeImagePreview(ItemDetail itemDetail) {
		
		// Then use the interface callback to tell activity item is shared
		if (mCallback != null) {
			mCallback.shareItem(itemDetail);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		if (mList != null) {
			changeImagePreview(mList.get(pos));
		}
	}
	
}
