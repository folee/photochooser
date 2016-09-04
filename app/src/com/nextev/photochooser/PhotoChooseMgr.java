package com.nextev.photochooser;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.Toast;


import com.nextev.photochooser.R;
import com.nextev.photochooser.adapter.vo.ImageItem;
import com.nextev.photochooser.util.DebugLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoChooseMgr {

	private static volatile PhotoChooseMgr	instance;
	private Context							context;
	/**在进入相册前纪录上次已选择图片List*/
	private List<ImageItem> tempSelectedList;
	/**已选中图片List*/
	private List<ImageItem>					selectedList;
	/**所有图片List*/
	private List<ImageItem>					allImageList;
	/**相册最大可选择图片的数量*/
	private int								maxSelectSize								= 6;
	/**相册是否有拍照功能*/
	private boolean							isTakePhoto;

	/**启动Activity的Request Code*/
	public static final int					CAPTURE_IMAGE_REQUEST_CODE_CONTENT_RESOLVER	= 0x101;

	/**存放拍照图片的Content Resolver 地址*/
	private Uri								mPhotoUri;

	/**为了解决红米手机拍照Crash问题，将拍照uri存入 sp，以备不时之需*/
	private SharedPreferences				sp;

	private PhotoChooseMgr(Context context) {
		this.context = context;
		this.selectedList = new ArrayList<ImageItem>();
		this.tempSelectedList = new ArrayList<ImageItem>();
		this.allImageList = new ArrayList<ImageItem>();

		sp = context.getSharedPreferences("ConstantParams", Context.MODE_PRIVATE);
	}

	public static PhotoChooseMgr getInstance(Context context) {
		if (instance == null) {
			synchronized (PhotoChooseMgr.class) {
				if (instance == null) {
					instance = new PhotoChooseMgr(context);
				}
			}
		}
		return instance;
	}

	/**
	 * 获取最大可选择图片数量
	 * @return
	 */
	public int getMaxSelectSize() {
		return maxSelectSize;
	}

	/**
	 * 设置最大可选择图片数量
	 * @param maxSelectSize
	 */
	public void setMaxSelectSize(int maxSelectSize) {
		this.maxSelectSize = maxSelectSize;
	}

	/**
	 * 获取当前已选中图片数量
	 * @return
	 */
	public int getSelectCount() {
		return selectedList.size();
	}

	/**
	 * 获取所有图片List，包括未选中图片
	 * @return
	 */
	public List<ImageItem> getAllImageList() {
		return allImageList;
	}

	/**
	 * 获取已选择图片List
	 * @return
	 */
	public List<ImageItem> getSeletectList() {
		return selectedList;
	}

	/**
	 * 每次进入相册先将之前已选中的图片放到临时List
	 */
	public void initSelected(){

		for (int i = 0; i < selectedList.size(); i++) {
			ImageItem it = selectedList.get(i);
			if (!(new File(it.realPath).exists())) {
				selectedList.remove(i);
			}
		}
		tempSelectedList.clear();
		tempSelectedList.addAll(selectedList);
	}

	/**
	 * 若点击返回，将已选中图片列表还原成最初进入时的数据
	 */
	public void backAction(){
		selectedList.clear();
		selectedList.addAll(tempSelectedList);
	}

	/**
	 * 添加选中图片
	 *
	 * @param item
	 * @return 是否添加成功
	 */
	public boolean addSelect(ImageItem item) {
		boolean isContained = false;
		for (int i = 0, size = selectedList.size(); i < size; i++) {
			ImageItem it = selectedList.get(i);
			if (item.id == it.id) {
				isContained = true;
				break;
			}
		}

		if (isContained) {
			return true;
		}
		else if (selectedList.size() >= maxSelectSize) {
			return false;
		}
		else {
			selectedList.add(item);
			//tempSelectedList.add(item);
			DebugLog.d("imgPath = " + item.realPath);
			return true;
		}
	}

	/**
	 * 清除已选中图片
	 * @param item
	 * @return 是否成功
	 */
	public boolean removeSelect(ImageItem item) {
		for (int i = 0; i < selectedList.size(); i++) {
			ImageItem it = selectedList.get(i);
			if (item.id == it.id) {
				selectedList.remove(i);
			}
		}
		return true;
	}

	/**
	 * 清空所有已选中图片
	 */
	public void clearSelect() {
		//selectedItemMap.clear();
		selectedList.clear();
	}

	/**
	 * 根据id获取已选中的某一张图片
	 * @param key
	 * @return
	 */
	public ImageItem getImageItem(int key) {

		for (ImageItem it : selectedList) {
			if (key == it.id) {
				return it;
			}
		}

		return null;
		//return selectedItemMap.get(key);
	}

	/**
	 * 相册是否包含拍照功能
	 * @return
	 */
	public boolean isTakePhoto() {
		return isTakePhoto;
	}

	/**
	 * 设置相册显示拍照功能
	 * @param isTakePhoto
	 */
	public void setTakePhoto(boolean isTakePhoto) {
		this.isTakePhoto = isTakePhoto;
	}

	/**
	 * 通过把照片存储在Content Provide的方式获取 启动Activity的Request Code为
	 * {@link #CAPTURE_IMAGE_REQUEST_CODE_CONTENT_RESOLVER}
	 *
	 * @param fragment
	 */
	public void takePhotoViaCP(Fragment fragment) {
		mPhotoUri = fragment.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
		String uri_str = mPhotoUri.toString();
		sp.edit().putString("photo_uri_str", uri_str).commit();

		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
			fragment.startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE_CONTENT_RESOLVER);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, context.getString(R.string.camera_disable), Toast.LENGTH_SHORT);
			e.printStackTrace();
		} catch (Exception e) {
			Toast.makeText(context, context.getString(R.string.camera_error), Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
	}

	/**
	 * 首先调用{@link #takePhotoViaCP(Fragment)}方法拍照，而后根据Uri查询图片地址<br>
	 *
	 * @param activity
	 * @param photoUri
	 *            查询图片需要的Uri
	 * @return 如果没有查询到，可能返回null，请做好为空判断
	 */
	public ImageItem getPhotoViaCP(Activity activity, Uri photoUri) {
		// Image saved to a generated MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		String[] projection = { MediaStore.MediaColumns._ID,//
				MediaStore.Images.Media.DATA, //
				MediaStore.Images.ImageColumns.DISPLAY_NAME,//
				MediaStore.Images.Media.BUCKET_ID, };
		/*
		String[] projection = {
				MediaStore.MediaColumns._ID,
				MediaStore.Images.ImageColumns.ORIENTATION,
				MediaStore.Images.Media.DATA
		};*/

		//首先赋值存放的变量
		if (photoUri == null)
			photoUri = mPhotoUri;

		//上一次赋值不成功，从SP获取并赋值
		if (photoUri == null) {
			String photo_uri_str = sp.getString("photo_uri_str", "");
			photoUri = Uri.parse(photo_uri_str);
		}

		if (photoUri != null) {
			Cursor c = activity.getContentResolver().query(photoUri, projection, null, null, null);
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				ImageItem item = new ImageItem();
				item.id = c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID));
				item.name = c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
				item.realPath = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
				item.albumId = c.getInt(c.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
				return item;
			}
		}
		else {
			DebugLog.e("--------------photoUri is NULL --------------");
		}

		return null;
	}
}
