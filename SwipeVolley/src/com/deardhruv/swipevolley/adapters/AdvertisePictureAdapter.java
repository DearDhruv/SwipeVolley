
package com.deardhruv.swipevolley.adapters;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.deardhruv.swipevolley.R;
import com.deardhruv.swipevolley.activities.ImageUploadActivity.AddPictureEvent;
import com.deardhruv.swipevolley.activities.ImageUploadActivity.RemovePictureEvent;
import com.deardhruv.swipevolley.utils.AnimateImageListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

public class AdvertisePictureAdapter extends PagerAdapter {

	private final List<String> mImageFileList;
	private final Context mContext;

	private final AnimateImageListener mAnimateImageListener = new AnimateImageListener();
	private final DisplayImageOptions mImageOptions;
	private final ImageLoader mImageLoader;

	private EventBus mEventBus;

	// private int mDefaultPicture = 0;

	public AdvertisePictureAdapter(final Context context, final List<String> fileList) {
		if (context == null) {
			throw new IllegalArgumentException("context shouldn't be null!");
		}

		if (fileList == null) {
			throw new IllegalArgumentException("fileList shouldn't be null!");
		}

		mEventBus = EventBus.getDefault();

		mImageFileList = fileList;
		mContext = context;

		mImageLoader = ImageLoader.getInstance();
		mImageLoader.clearMemoryCache();

		mImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(R.drawable.ic_launcher).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		if (mImageFileList.size() == 0) {
			return 0;
		}
		if (mImageFileList.size() < 8) {
			return mImageFileList.size() + 1;
		}
		return mImageFileList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == ((View) obj);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		final LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View page;

		if (mImageFileList.size() == position) {
			page = inflater.inflate(R.layout.row_advertise_add_item, container, false);

			View addButton = page.findViewById(R.id.fragment_advertise_btn_add_photo);
			addButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mEventBus.post(new AddPictureEvent());
				}
			});
			container.addView(page);
			return page;
		}

		page = inflater.inflate(R.layout.row_advertise_image_item, null);
		container.addView(page);

		final ImageView iv = (ImageView) page.findViewById(R.id.row_advertise_image_item_image);

		final ImageButton deleteBtn = (ImageButton) page
				.findViewById(R.id.row_advertise_image_btn_delete);
		deleteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final RemovePictureEvent event = new RemovePictureEvent();
				final Integer selection = (Integer) v.getTag();
				event.position = selection;
				mEventBus.post(event);
			}
		});

		deleteBtn.setTag(position);
		mImageLoader.displayImage(mImageFileList.get(position), iv, mImageOptions,
				mAnimateImageListener);

		return page;
	}

	@Override
	public void destroyItem(View view, int index, Object obj) {
		((ViewPager) view).removeView((View) obj);
	}

	@Override
	public float getPageWidth(int position) {
		if (mImageFileList.size() == 1) {
			return 0.5f;
		}
		return 0.34f;
	}

	public List<String> getFileList() {
		return mImageFileList;
	}

	// public int getDefaultPicture() {
	// return mDefaultPicture;
	// }
	//
	// public void setDefaultPicture(final int defaultPic) {
	// mDefaultPicture = defaultPic;
	// }
}
