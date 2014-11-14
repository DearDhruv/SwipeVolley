
package com.deardhruv.swipevolley.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.deardhruv.swipevolley.R;
import com.deardhruv.swipevolley.utils.ImageValidator;
import com.deardhruv.swipevolley.utils.StoreImageHelper;

public class ImageUploadActivity extends Activity implements OnClickListener {
	private static final String LOGTAG = ImageUploadActivity.class.getSimpleName();

	private static final int REQUESTCODE_PHOTO_PICKER = 2001;

	private Button btnImageUpload;

	private File mTmpPictureFile;
	private StoreImageHelper mStoreImageHelper;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_upload_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initUI();
		initListeners();
	}

	private void initUI() {
		btnImageUpload = (Button) findViewById(R.id.btnImageUpload);
	}

	private void initListeners() {
		btnImageUpload.setOnClickListener(this);
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

		Intent chooserIntent = Intent.createChooser(getContentIntent, "Pick your choise");

		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, takePictureIntent);
		startActivityForResult(chooserIntent, REQUESTCODE_PHOTO_PICKER);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case REQUESTCODE_PHOTO_PICKER:
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
			// if (mPictureAdapter.getFileList().size() < 8) {
			// if (imagePath.startsWith("file://")) {
			// imagePath.replace("file://", "");
			// }
			//
			// final File file = new File(imagePath);
			//
			// if (file.exists()) {
			// if
			// (!ImageValidator.isPictureValidForUpload(file.getAbsolutePath()))
			// {
			// Toast.makeText(ImageUploadActivity.this,
			// "Creating image failed.",
			// Toast.LENGTH_LONG).show();
			// } else {
			// addImageToGallery(file);
			// }
			// } else {
			// Log.e(LOGTAG, "Photo picker: File does not exist!");
			// Toast.makeText(ImageUploadActivity.this,
			// "Image is not supported.",
			// Toast.LENGTH_LONG).show();
			// }
			// }
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

		File file_save = new File(dir.getAbsoluteFile(), System.currentTimeMillis() + ".png");

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
		// if (image == null) {
		// throw new IllegalArgumentException("image cannot be null");
		// }
		//
		// final List<String> fileList = mPictureAdapter.getFileList();
		// fileList.add(mStoreImageHelper.getImagePath(image));
		//
		// mAddInitial.setVisibility(View.GONE);
		// mPictureAdapter = new AdvertisePictureAdapter(getActivity(),
		// fileList);
		// mViewPager.setAdapter(mPictureAdapter);
		// mViewPager.requestLayout();
		// mViewPager.setCurrentItem(mPictureAdapter.getCount() - 1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnImageUpload:

				Toast.makeText(ImageUploadActivity.this, "Clicked", Toast.LENGTH_LONG).show();

				break;

			default:
				break;
		}
	}

}
