package com.nextev.photoview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


import com.nextev.photochooser.R;

import java.util.List;

import me.xiaopan.sketch.CancelCause;
import me.xiaopan.sketch.DisplayListener;
import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.FailCause;
import me.xiaopan.sketch.ImageFrom;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.display.DefaultImageDisplayer;

public class PhotoPreviewAdapter extends PagerAdapter implements PhotoViewAttacher.OnViewTapListener {

	private PhotoPreview		photoPreview;
	private Context				mContext;
	private List<String>		data;
	private int					offset			= 0;
	private DisplayOptions		displayOptions;
	private PhotoViewAttacher	photoViewAttacher;

	//private NiftyDialogBuilder	savePicDialog	= null;

	public PhotoPreviewAdapter(PhotoPreview photoPreview, List<String> data, int offset) {
		this.mContext = photoPreview.getContext();
		this.photoPreview = photoPreview;
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
		View view = inflater.inflate(R.layout.item_photo_preview, null);
		final SketchImageView iv_preview = (SketchImageView) view.findViewById(R.id.iv_preview);

		position = position + offset;
		if (position < 0) {
			position += getCount();
		}
		else {
			position = position % getCount();
		}
		final String imgpath = getCount() > 0 ? data.get(position) : "";


		if (iv_preview.getTag() == null || !iv_preview.getTag().equals(imgpath + view.getId())) {
			iv_preview.setTag(imgpath + view.getId());
			//获取网络图片显示
			iv_preview.setDisplayOptions(displayOptions);
			iv_preview.setDisplayListener(new DisplayListener() {
				@Override
				public void onStarted() {
				}

				@Override
				public void onCompleted(ImageFrom imageFrom, String mimeType) {
					photoViewAttacher = new PhotoViewAttacher(iv_preview);
					photoViewAttacher.setOnViewTapListener(PhotoPreviewAdapter.this);
					photoViewAttacher.update();

					photoViewAttacher.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							savePic(imgpath);
							return false;
						}
					});
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

	private void savePic(final String imgpath) {
		/*View view = LayoutInflater.from(mContext).inflate(R.layout.save_to_local, null);
		View tvSave = view.findViewById(R.id.save_to_local_tv);
		tvSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				savePicDialog.dismiss();
				photoPreview.addDLFile(imgpath, "png");
			}
		});
		savePicDialog = Utils_CustomDialog.getInstance(mContext).createDialog(null, null, null, null, null, view, null, null);
		savePicDialog.show();*/
	}

	@Override
	public void onViewTap(View view, float x, float y) {
		this.photoPreview.dismiss();
	}
}
