package com.nextev.photochooser.adapter;

import java.util.HashMap;
import java.util.Map;

import com.nextev.photochooser.R;
import com.nextev.photochooser.adapter.vo.AlbumItem;
import com.nextev.photochooser.util.DebugLog;
import com.nextev.photochooser.util.LoadeImageConsts;
import com.nextev.photochooser.util.Utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.display.DefaultImageDisplayer;

/**
 * 相册选择Adapter
 */
public class AlbumAdapter extends BaseAdapter {
	private Context					context;
	private Map<Integer, AlbumItem>	albumMap;
	private int						currAlumbId;
	private DisplayOptions			displayOptions;

	public AlbumAdapter(Context context) {
		this.context = context;
		this.albumMap = new HashMap<Integer, AlbumItem>();

		DefaultImageDisplayer transitionImageDisplayer = new DefaultImageDisplayer();
		displayOptions = new DisplayOptions()
				.setLoadingImage(R.drawable.image_loading_default)
				.setFailureImage(R.drawable.image_loading_default)
				.setPauseDownloadImage(R.drawable.image_loading_default)
				.setDecodeGifImage(false)
				.setImageDisplayer(transitionImageDisplayer);
	}

	@Override
	public int getCount() {
		if (albumMap == null) {
			return 0;
		}
		return albumMap.size();
	}

	@Override
	public AlbumItem getItem(int position) {
		return albumMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		AlbumItem albumItem = albumMap.get(position);
		return albumItem == null ? LoadeImageConsts.LOADER_IMAGE_CURSOR : albumItem.id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_album, null);
			holder.currFlagView = (ImageView) convertView.findViewById(R.id.alumb_curr_flag);
			holder.countView = (TextView) convertView.findViewById(R.id.alumb_count);
			holder.nameView = (TextView) convertView.findViewById(R.id.alubm_name);
			holder.imageView = (SketchImageView) convertView.findViewById(R.id.alumb_picture);

			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		//DebugLog.d("position = " + position);
		AlbumItem albumItem = getItem(position);
		if (albumItem != null) {
			holder.nameView.setText(albumItem.albumName);
			holder.countView.setText(context.getString(R.string.zhang, albumItem.imageCount));
			holder.currFlagView.setVisibility(albumItem.id == this.currAlumbId ? View.VISIBLE : View.GONE);
			//PhotoChooseMgr.getInstance(context).dispalyImage(albumItem.firstImagePath, holder.imageView, albumItem.firstImageId);

			holder.imageView.setDisplayOptions(displayOptions);
			holder.imageView.setImageShape(SketchImageView.ImageShape.ROUNDED_RECT);
			holder.imageView.displayImage(albumItem.firstImagePath);
		}

		return convertView;
	}

	public void setCurrAlumbId(int currAlumbId) {
		this.currAlumbId = currAlumbId;
		notifyDataSetChanged();
	}

	/**
	 * 根据Cursor帅选Adapter数据
	 * 
	 * @param albumCursor
	 */
	public void setAlbumCursor(Cursor albumCursor) {
		albumMap.clear();
		if (albumCursor != null) {
			DebugLog.d("loadCursor Size = " + albumCursor.getCount());
			for (int i = 0, count = albumCursor.getCount(); i < count; i++) {
				albumCursor.moveToPosition(i);
				AlbumItem albumItem = new AlbumItem();
				albumItem.firstImageId = albumCursor.getInt(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
				albumItem.firstImagePath = Utils.getImagePath(context, albumItem.firstImageId);
				albumItem.albumName = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
				albumItem.id = albumCursor.getInt(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
				albumItem.imageCount = albumCursor.getInt(albumCursor.getColumnIndex("album_count"));

				albumMap.put(i + 1, albumItem);
			}

			AlbumItem firstItem = new AlbumItem();
			firstItem.id = LoadeImageConsts.LOADER_IMAGE_CURSOR;
			firstItem.albumName = context.getString(R.string.all_photos);
			int amount = 0;
			for (Map.Entry<Integer, AlbumItem> entry : albumMap.entrySet()) {
				Integer key = entry.getKey();
				AlbumItem value = entry.getValue();
				amount += value.imageCount;
				if (key == 1) {
					firstItem.firstImageId = value.firstImageId;
					firstItem.firstImagePath = value.firstImagePath;
				}
			}
			firstItem.imageCount = +amount;
			setFirstItem(false, firstItem);
			DebugLog.d("albumMap Size = " + albumMap.size());
			notifyDataSetChanged();
		}
	}

	public void setFirstItem(boolean isreal, AlbumItem item) {
		AlbumItem firstItem = albumMap.get(0);
		if (firstItem == null || isreal) {
			albumMap.put(0, item);
		}
		setCurrAlumbId(item.id);
	}

	class ViewHolder {
		TextView	nameView;
		TextView	countView;
		ImageView	currFlagView;
		SketchImageView	imageView;
	}
}
