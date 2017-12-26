package com.nextev.photoview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.nextev.photochooser.R;

import java.util.List;

public class PhotoPreviewAdapter extends PagerAdapter implements PhotoViewAttacher.OnViewTapListener {

	private PhotoPreview	photoPreview;
	private Context			mContext;
	private List<String>	data;
	private int				offset	= 0;
	//private PhotoViewAttacher	photoViewAttacher;

	//private NiftyDialogBuilder	savePicDialog	= null;

	public PhotoPreviewAdapter(PhotoPreview photoPreview, List<String> data, int offset) {
		this.mContext = photoPreview.getContext();
		this.photoPreview = photoPreview;
		this.data = data;
		this.offset = offset;
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
		final PhotoView iv_preview = (PhotoView) view.findViewById(R.id.iv_preview);

		position = position + offset;
		if (position < 0) {
			position += getCount();
		}
		else {
			position = position % getCount();
		}
		final String imgpath = getCount() > 0 ? data.get(position) : "";

		//获取网络图片显示
		Glide.with(mContext).load(imgpath).into(iv_preview);
		iv_preview.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				savePic(imgpath);
				return false;
			}
		});

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
