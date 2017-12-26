package com.nextev.photochooser.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.nextev.photochooser.R;
import com.nextev.photochooser.adapter.vo.ImageItem;
import com.nextev.photochooser.fragment.PreviewFragment;
import com.nextev.photoview.PhotoView;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PreviewAdapter extends PagerAdapter {

	private Fragment		fragment;
	private Context			mContext;
	private List<ImageItem>	data;
	private int				offset	= 0;

	public PreviewAdapter(Fragment fragment, List<ImageItem> data, int offset) {
		this.mContext = fragment.getActivity();
		this.fragment = fragment;
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
		View view = inflater.inflate(R.layout.item_preview, null);
		final PhotoView iv_preview = (PhotoView) view.findViewById(R.id.iv_preview);

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

		Glide.with(mContext).load(imgpath).into(iv_preview);
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = (View) object;
		container.removeView(view);
	}
}
