
package com.deardhruv.swipevolley.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class StoreImageHelper {

	private static final String LOGTAG = StoreImageHelper.class.getSimpleName();
	private static final String IMAGES_PATH = "imgs";

	private final File mImageFolder;

	public StoreImageHelper(final Context ctx) {
		mImageFolder = getImageFolder(ctx);

		if (!mImageFolder.exists()) {
			mImageFolder.mkdir();
		}
	}

	public static File getImageFolder(final Context ctx) {
		return new File(StorageUtils.getCacheDirectory(ctx), IMAGES_PATH);
	}

	public void saveImage(final String urlparam, final String adnumber) throws IOException {
		final File bitmap = new File(mImageFolder, adnumber);

		if (bitmap.exists()) {
			// NOTHIND TO DO
		} else if (bitmap.createNewFile()) {
			final URL url = new URL(urlparam);
			final URLConnection urlConnect = url.openConnection();
			final OutputStream output = new FileOutputStream(bitmap);
			final InputStream is = urlConnect.getInputStream();

			try {
				byte[] buffer = new byte[512];
				int bytesRead = 0;
				while ((bytesRead = is.read(buffer, 0, buffer.length)) >= 0) {
					output.write(buffer, 0, bytesRead);
				}
			} catch (MalformedURLException e) {
				Log.e(LOGTAG, e.getMessage());
			} finally {
				is.close();
				output.close();
			}
		} else {
			// FILE NOT CREATED
		}
	}

	public String getImagePath(final File file) {
		if (file.exists()) {
			return "file://" + file.getAbsolutePath();
		} else {
			return null;
		}
	}

	public String getImagePath(final String adnumber) {
		if (adnumber == null) {
			throw new IllegalArgumentException("adnumber cannot be null");
		}

		final File bitmap = new File(mImageFolder, adnumber);

		if (bitmap.exists()) {
			return "file://" + bitmap.getAbsolutePath();
		} else {
			return null;
		}
	}

	public void removeImage(final String adnumber) {
		final File bitmap = new File(mImageFolder, adnumber);

		if (bitmap.exists()) {
			bitmap.delete();
		}
	}

	@SuppressLint("SimpleDateFormat")
	public File createImageFile() throws IOException {
		// Create an image file name
		final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		final String imageFileName = "j" + timeStamp + "_";
		final File image = File.createTempFile(imageFileName, ".jpg", mImageFolder);
		return image;
	}
}
