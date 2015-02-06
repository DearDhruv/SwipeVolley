
package com.deardhruv.swipevolley.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.examples.toolbox.updated.IJSONParseListener;
import com.android.volley.examples.toolbox.updated.JSONRequestResponse;
import com.deardhruv.swipevolley.R;
import com.deardhruv.swipevolley.adapters.AdvertisePictureAdapter;
import com.deardhruv.swipevolley.network.ServiceURL;
import com.deardhruv.swipevolley.utils.ImageValidator;
import com.deardhruv.swipevolley.utils.StoreImageHelper;

import de.greenrobot.event.EventBus;

public class ImageUploadActivity extends Activity implements OnClickListener, IJSONParseListener {
	private static final String LOGTAG = ImageUploadActivity.class.getSimpleName();

	private static final int PHOTO_PICKER_CODE = 2001;
	private static final int REQUEST_IMAGE_UPLOAD_CODE = 2011;

	private EventBus mEventBus;

	private StoreImageHelper mStoreImageHelper;
	private File mTmpPictureFile;
	private AdvertisePictureAdapter mPictureAdapter;

	private ProgressDialog pd;
	private ImageButton mAddInitial;
	private Button btnImageUpload;
	private ViewPager mViewPager;
	private TextView txtResponse;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_upload_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initUI();
		initListeners();
		mStoreImageHelper = new StoreImageHelper(ImageUploadActivity.this);

	}

	private void initUI() {
		btnImageUpload = (Button) findViewById(R.id.btnImageUpload);
		mAddInitial = (ImageButton) findViewById(R.id.addInitial);
		txtResponse = (TextView) findViewById(R.id.txtResponse);

		mViewPager = (ViewPager) findViewById(R.id.fragment_advertise_viewpager_pics);
		mPictureAdapter = new AdvertisePictureAdapter(ImageUploadActivity.this,
				new ArrayList<String>());
	}

	private void initListeners() {
		btnImageUpload.setOnClickListener(this);
		mAddInitial.setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mEventBus != null) {
			mEventBus.unregister(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mEventBus = EventBus.getDefault();
		mEventBus.register(this);
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private void showAddPhotoDialog() {

		Intent getContentIntent = new Intent();
		getContentIntent.setAction(Intent.ACTION_GET_CONTENT);
		getContentIntent.setType("image/*");

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			mTmpPictureFile = null;
			mTmpPictureFile = mStoreImageHelper.createImageFile();
			takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mTmpPictureFile));
		} catch (IOException e) {
			Log.e(LOGTAG, e.getMessage());
		}

		Intent[] additionalIntents = new Intent[] {
			takePictureIntent
		};

		Intent chooserIntent = Intent.createChooser(getContentIntent, "Pick your choice");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, additionalIntents);
		startActivityForResult(chooserIntent, PHOTO_PICKER_CODE);
	}

	private void removeImageFromGallery(final int pos) {
		// int defaultPic = mPictureAdapter.getDefaultPicture() == pos ? 0 :
		// mPictureAdapter
		// .getDefaultPicture();
		final List<String> fileList = mPictureAdapter.getFileList();
		fileList.remove(pos);

		mPictureAdapter = new AdvertisePictureAdapter(ImageUploadActivity.this, fileList);

		// if (pos < defaultPic) {
		// defaultPic--;
		// }

		if (fileList.size() == 0) {
			mAddInitial.setVisibility(View.VISIBLE);
		}

		// mPictureAdapter.setDefaultPicture(defaultPic);
		mViewPager.setAdapter(mPictureAdapter);
		mViewPager.requestLayout();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case PHOTO_PICKER_CODE:
				if (data == null) {
					// The camera picture does not come with the data. It is set
					// via Extra
					// android.provider.MediaStore.EXTRA_OUTPUT when the camera
					// intent is started.
					handleTakePictureResult(resultCode);
				} else {
					handlePhotoPickerResult(resultCode, data);
				}
				break;
			default:
				break;
		}

	}

	/**
	 * Handles the result which is returned when the user picked one or more
	 * photos from the multi picture chooser or another source.
	 * 
	 * @param resultCode
	 * @param data
	 */
	private void handlePhotoPickerResult(int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && data != null) {
			// The image comes from another source.
			String imagePath = ImageValidator.getPath(ImageUploadActivity.this, data.getData());
			validateAndAddtoGallery(imagePath, data.getData());
		}
	}

	/**
	 * Check if the image exists and is valid. Add it to the gallery adapter.
	 * 
	 * @param imagePath
	 * @param uri
	 */
	private void validateAndAddtoGallery(String imagePath, Uri uri) {
		if (imagePath != null && imagePath.length() > 0) {
			if (mPictureAdapter.getFileList().size() < 8) {
				if (imagePath.startsWith("file://")) {
					imagePath.replace("file://", "");
				}

				final File file = new File(imagePath);

				if (file.exists()) {
					if (!ImageValidator.isPictureValidForUpload(file.getAbsolutePath())) {
						Toast.makeText(ImageUploadActivity.this, "Creating image failed.",
								Toast.LENGTH_LONG).show();
					} else {
						addImageToGallery(file);
					}
				} else {
					Log.e(LOGTAG, "Photo picker: File does not exist!");
					Toast.makeText(ImageUploadActivity.this, "Image is not supported.",
							Toast.LENGTH_LONG).show();
				}
			}
		} else {
			Toast.makeText(ImageUploadActivity.this, "Creating image failed.", Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * Handles the result which is returned when the user took a picture with
	 * the camera.
	 * 
	 * @param resultCode
	 */
	private void handleTakePictureResult(int resultCode) {
		if (resultCode == Activity.RESULT_OK) {
			if (mTmpPictureFile == null) {
				Toast.makeText(ImageUploadActivity.this, "Creating image failed.",
						Toast.LENGTH_LONG).show();
			} else {
				saveImageAndUpdateGallery();
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	private void saveImageAndUpdateGallery() {
		File dir = new File(Environment.getExternalStorageDirectory() + "/SwipeVolley/");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file_save = new File(dir.getAbsoluteFile(), System.currentTimeMillis() + ".jpg");

		try {
			InputStream in = new FileInputStream(mTmpPictureFile);
			OutputStream out = new FileOutputStream(file_save);
			// Copy tshe bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;

			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			Log.e(LOGTAG, "FileNotFoundException while saving image. Message: " + e.getMessage());
		} catch (IOException e) {
			Log.e(LOGTAG, "IOException while saving image. Message: " + e.getMessage());
		}

		// Tell the MediaScanner to scan the newly created image to make it
		// available to the user.
		// http://developer.android.com/reference/android/os/Environment.html#getExternalStoragePublicDirectory%28java.lang.String%29
		String[] filePaths = new String[] {
			file_save.toString()
		};
		MediaScannerConnection.scanFile(ImageUploadActivity.this, filePaths, null, null);

		addImageToGallery(mTmpPictureFile);
	}

	private void addImageToGallery(final File image) {
		if (image == null) {
			throw new IllegalArgumentException("image cannot be null");
		}

		final List<String> fileList = mPictureAdapter.getFileList();
		fileList.add(mStoreImageHelper.getImagePath(image));

		mAddInitial.setVisibility(View.GONE);
		mPictureAdapter = new AdvertisePictureAdapter(ImageUploadActivity.this, fileList);
		mViewPager.setAdapter(mPictureAdapter);
		mViewPager.requestLayout();
		mViewPager.setCurrentItem(mPictureAdapter.getCount() - 1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.addInitial:
				showAddPhotoDialog();
				break;
			case R.id.btnImageUpload:
				prepareInsertAd();
				break;

			default:
				break;
		}
	}

	private void prepareInsertAd() {
		if (isInputValid()) {
			startUploading();
		}
	}

	private void startUploading() {

		pd = ProgressDialog.show(ImageUploadActivity.this, "Please wait", "Uploading images...");

		Bundle params = new Bundle();
		// params.putString(ServiceURL.AD_ID, "73793");
		// params.putString(ServiceURL.TOKEN, "deardhruvletarutoken");

		// params.putString("Content-Type","application/x-www-form-urlencoded");
		// JSONRequestHandler mResponse = new JSONRequestHandler();

		// Bundle fileBundle = new Bundle();
		// uploading only single file.
		// fileBundle.putString(ServiceURL.QQ_FILE,
		// mPictureAdapter.getFileList().get(0));
		// mResponse.setFile(fileBundle);

		JSONRequestResponse mResponse = new JSONRequestResponse(ImageUploadActivity.this);

		if (new File(mPictureAdapter.getFileList().get(0).replace("file://", "")).exists()) {
			// mResponse.setFile(ServiceURL.QQ_FILE,
			// mPictureAdapter.getFileList().get(0).replace("file://", ""));
			mResponse.setFile("file", "/storage/emulated/0/SwipeVolley/1423222649274.jpg");
			mResponse.getResponse(ServiceURL.encodeUrl(ServiceURL.imgUploadURL, params),
					REQUEST_IMAGE_UPLOAD_CODE, this);
		}
		System.gc();
	}

	private boolean isInputValid() {
		if (!isAllPicturesValid()) {
			Toast.makeText(ImageUploadActivity.this, "Selected image is not valid!!!",
					Toast.LENGTH_LONG).show();
			return false;
		}

		if (mPictureAdapter.getCount() == 0) {
			Toast.makeText(ImageUploadActivity.this, "Please select atleast one image to upload.",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private boolean isAllPicturesValid() {
		final List<String> pictures = new ArrayList<String>(mPictureAdapter.getFileList());

		for (String filepath : pictures) {
			if (!ImageValidator.isPictureValidForUpload(filepath)) {
				return false;
			}
		}
		return true;
	}

	public static class RemovePictureEvent {
		public int position;
	}

	public static class AddPictureEvent {
		// nothing
	}

	public void onEventMainThread(final AddPictureEvent event) {
		showAddPhotoDialog();
	}

	public void onEventMainThread(final RemovePictureEvent event) {
		if (event != null) {
			removeImageFromGallery(event.position);
		}
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		if (pd != null & pd.isShowing()) {
			pd.dismiss();
		}
		Log.e(LOGTAG, error.toString());
		txtResponse.setText(error.toString());
		txtResponse.setTextColor(Color.RED);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		if (pd != null & pd.isShowing()) {
			pd.dismiss();
		}
		Log.e(LOGTAG, response.toString());
		txtResponse.setText(response.toString());
		txtResponse.setTextColor(Color.BLACK);
	}

}
