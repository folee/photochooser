package com.nextev.photochooser.adapter;

import java.util.List;

import com.nextev.photochooser.R;
import com.nextev.photochooser.adapter.vo.ImageItem;
import com.nextev.photochooser.fragment.PreviewFragment;
import com.nextev.photoview.PhotoViewAttacher;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.xiaopan.sketch.CancelCause;
import me.xiaopan.sketch.DisplayListener;
import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.FailCause;
import me.xiaopan.sketch.ImageFrom;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.display.DefaultImageDisplayer;

public class PreviewAdapter extends PagerAdapter {

	private Fragment fragment;
	private Context				mContext;
	private List<ImageItem>		data;
	private int					offset	= 0;
	private PhotoViewAttacher photoViewAttacher;
	private DisplayOptions displayOptions;

	public PreviewAdapter(Fragment fragment, List<ImageItem> data, int offset) {
		this.mContext = fragment.getActivity();
		this.fragment = fragment;
		this.data = data;
		this.offset = offset;

		displayOptions = new DisplayOptions().setLoadingImage(R.drawable.image_loading_default).setFailureImage(R.drawable.image_loading_default)
				.setPauseDownloadImage(R.drawable.image_loading_default).setDecodeGifImage(false).setImageDisplayer(new DefaultImageDisplayer());
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.item_preview, null);
		final SketchImageView iv_preview = (SketchImageView) view.findViewById(R.id.iv_preview);

		position = position + offset;
		if (position < 0) {
			position += getCount();
		}
		else {
			position = position % getCount();
		}
		String imgpath = getCount() > 0 ? data.get(position).realPath : "";

		/*iv_preview.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				((PreviewFragment)fragment).handleHearderOrBottom();
				return true;
			}
		});

		iv_preview.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View view, float x, float y) {
				((PreviewFragment)fragment).handleHearderOrBottom();
			}
		});*/

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((PreviewFragment) fragment).handleHearderOrBottom();
			}
		});

		if (iv_preview.getTag() == null || !iv_preview.getTag().equals(imgpath + view.getId())) {
			iv_preview.setTag(imgpath + view.getId());
			iv_preview.setDisplayOptions(displayOptions);
			iv_preview.setDisplayListener(new DisplayListener() {
				@Override
				public void onStarted() {
				}

				@Override
				public void onCompleted(ImageFrom imageFrom, String mimeType) {
					photoViewAttacher = new PhotoViewAttacher(iv_preview);
					photoViewAttacher.update();
				}

				@Override
				public void onFailed(FailCause failCause) {
				}

				@Override
				public void onCanceled(CancelCause cancelCause) {
				}
			});
			iv_preview.displayImage(imgpath);
		}

		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = (View) object;
		container.removeView(view);
	}
}
