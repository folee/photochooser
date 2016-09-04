package com.nextev.photochooser.adapter;

import java.io.File;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;

import com.nextev.photochooser.R;
import com.nextev.photochooser.PhotoChooseActivity;
import com.nextev.photochooser.PhotoChooseMgr;
import com.nextev.photochooser.adapter.vo.ImageItem;
import com.nextev.photochooser.util.DebugLog;
import com.nextev.photochooser.util.Utils;

import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.display.DefaultImageDisplayer;

public class PictureAdapter extends BaseAdapter {
	private int							itemSize;
	private LayoutInflater				inflater;
	private View.OnClickListener		listener;
	private Context						context;
	private RelativeLayout.LayoutParams	params;
	private PhotoChooseMgr				loaderManager;
	private ContentResolver				resolver;
	//private DisplayImageOptions			displayOptions;
	private DisplayOptions				displayOptions;

	public PictureAdapter(Activity activity) {
		inflater = activity.getLayoutInflater();
		context = activity;
		resolver = context.getContentResolver();
		loaderManager = PhotoChooseMgr.getInstance(context);

		// 计算每个项的高度：高度=宽度
		int[] point = new int[2];
		Utils.GetScreenSize(context, point);
		int spaceSize = context.getResources().getDimensionPixelSize(R.dimen.qw_dip_01) * 2;
		itemSize = (point[0] - spaceSize) / 3;

		listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				if (!holder.checkBox.isChecked()) {
					holder.checkBox.setChecked(false);
					loaderManager.removeSelect(holder.imageItem);
				}
				else {
					if (!loaderManager.addSelect(holder.imageItem)) {
						holder.checkBox.setChecked(false);
						Toast.makeText(context, context.getString(R.string.max_pic, loaderManager.getMaxSelectSize()), Toast.LENGTH_LONG).show();
					}
					else {
						holder.checkBox.setChecked(true);
					}
				}
				((PhotoChooseActivity) context).changeSelectedCount();
			}
		};

		displayOptions = new DisplayOptions().setLoadingImage(R.drawable.image_loading_default).setFailureImage(R.drawable.image_loading_default)
				.setPauseDownloadImage(R.drawable.image_loading_default).setDecodeGifImage(false).setImageDisplayer(new DefaultImageDisplayer());

		//displayOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.image_loading_default).showImageOnFail(R.drawable.image_loading_default)
		//		.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true).cacheOnDisk(false).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		int size = PhotoChooseMgr.getInstance(context).getAllImageList().size();
		if (PhotoChooseMgr.getInstance(context).isTakePhoto()) {
			size += 1;
		}
		return size;
	}

	@Override
	public ImageItem getItem(int position) {

		if (PhotoChooseMgr.getInstance(context).isTakePhoto()) {
			position = position - 1;
		}
		return PhotoChooseMgr.getInstance(context).getAllImageList().get(position);
	}

	@Override
	public long getItemId(int position) {
		ImageItem item = PhotoChooseMgr.getInstance(context).getAllImageList().get(position);
		if (item == null)
			return 0;
		return item.id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_picture, null);
			holder = new ViewHolder();
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.picture_checkbox);
			holder.imageView = (SketchImageView) convertView.findViewById(R.id.picture_imageview);
			holder.textView = (TextView) convertView.findViewById(R.id.text);
			holder.checkBox.setOnClickListener(listener);
			params = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
			params.height = itemSize;
			holder.imageView.setLayoutParams(params);

			holder.checkBox.setTag(holder);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (PhotoChooseMgr.getInstance(context).getAllImageList() != null) {
			if (position == 0 && PhotoChooseMgr.getInstance(context).isTakePhoto()) {
				holder.checkBox.setChecked(false);
				holder.checkBox.setVisibility(View.GONE);
				holder.imageView.setImageResource(R.drawable.take_photo);
			}
			else {
				ImageItem item = getItem(position);
				holder.imageItem = item;
				holder.textView.setText(item.name);
				holder.checkBox.setChecked(loaderManager.getImageItem(item.id) != null && loaderManager.getSelectCount() <= loaderManager.getMaxSelectSize());
				holder.checkBox.setVisibility(View.VISIBLE);
				//PhotoChooseMgr.getInstance(context).dispalyImage(item.realPath, holder.imageView);
				//ImageAware imageAware = new ImageViewAware(holder.imageView, false);
				//ImageLoader.getInstance().displayImage(resolver, ImageDownloader.Scheme.FILE.wrap(item.realPath), item.id, imageAware, displayOptions);

				holder.imageView.setDisplayOptions(displayOptions);
				holder.imageView.setImageShape(SketchImageView.ImageShape.ROUNDED_RECT);
				holder.imageView.displayImage(item.realPath);
			}
		}
		return convertView;
	}

	/**
	 * 根据传入的Cursor，初始化Adapter数据
	 *
	 * @param loadCursor
	 */
	public void setLoadCursor(Cursor loadCursor) {
		PhotoChooseMgr.getInstance(context).getAllImageList().clear();
		if (loadCursor != null) {
			DebugLog.d("loadCursor Size = " + loadCursor.getCount());
			for (int i = 0, count = loadCursor.getCount(); i < count; i++) {
				loadCursor.moveToPosition(i);
				ImageItem item = new ImageItem();
				item.id = loadCursor.getInt(loadCursor.getColumnIndex(MediaStore.Images.Media._ID));
				item.name = loadCursor.getString(loadCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
				item.realPath = loadCursor.getString(loadCursor.getColumnIndex(MediaStore.Images.Media.DATA));
				item.albumId = loadCursor.getInt(loadCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
				try {
					//判断相片是否存在，只添加存在的，抛弃无用的
					if (new File(item.realPath).exists()) {
						PhotoChooseMgr.getInstance(context).getAllImageList().add(item);
					}
					else {
						DebugLog.e(item.realPath + " not exist, drop it; ");
						Utils.delErrorImgURI(context, item.id);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			DebugLog.d("AllImageList Size = " + PhotoChooseMgr.getInstance(context).getAllImageList().size());
			notifyDataSetChanged();
		}
	}

	class ViewHolder {
		ImageItem		imageItem;
		SketchImageView	imageView;
		CheckBox		checkBox;
		TextView		textView;
	}
}
