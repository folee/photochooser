package com.nextev.photochooser;

import java.io.File;

import com.nextev.photochooser.adapter.PictureAdapter;
import com.nextev.photochooser.adapter.vo.AlbumItem;
import com.nextev.photochooser.adapter.vo.ImageItem;
import com.nextev.photochooser.fragment.PreviewFragment;
import com.nextev.photochooser.fragment.SelectAlbumFragment;
import com.nextev.photochooser.util.DebugLog;
import com.nextev.photochooser.util.LoadeImageConsts;
import com.nextev.photochooser.util.Utils;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;


/**
 * 相册主界面，需要在使用工程的manifest注册该类
 */
public class PhotoChooseActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String[]	LOADING_COLUMN	= { MediaStore.Images.ImageColumns._ID,//
			MediaStore.Images.Media.DATA, //
			MediaStore.Images.ImageColumns.DISPLAY_NAME,//
			MediaStore.Images.Media.BUCKET_ID,		};
	private Integer					albumId;

	private TextView				header_back;
	private TextView				header_title;
	private TextView				header_right_button;
	//本机无图片提示
	private TextView				tv_no_photo;
	//图片展示GridView
	private GridView				gridView;
	private PictureAdapter			adapter;

	//相册选择Fragment
	private SelectAlbumFragment		albumFragment;
	//图片预览Fragment
	private PreviewFragment			previewFragment;
	//根据相册ID检索出来的Cursor
	private Cursor					cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_image);
		initHeader();
		tv_no_photo = (TextView) findViewById(R.id.tv_no_photo);
		gridView = (GridView) findViewById(R.id.choose_image_gridview);
		adapter = new PictureAdapter(this);
		albumFragment = (SelectAlbumFragment) getSupportFragmentManager().findFragmentById(R.id.choose_image_album);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && PhotoChooseMgr.getInstance(getApplication()).isTakePhoto()) {
					Toast.makeText(PhotoChooseActivity.this, getString(R.string.take_photo), Toast.LENGTH_LONG).show();
				}
				else {
					ImageItem item = adapter.getItem(position);
					previewFragment = new PreviewFragment();
					Bundle args = new Bundle();
					args.putInt("offset", PhotoChooseMgr.getInstance(getApplication()).isTakePhoto() ? position - 1 : position);
					args.putBoolean("show_all", true);
					args.putBoolean("change_title_color", isChangeTitleColor);
					args.putSerializable("ImageItem", item);
					previewFragment.setArguments(args);

					showPreviewFragment(previewFragment);
				}
			}
		});

		refreshGridViewByAlbumId(LoadeImageConsts.LOADER_IMAGE_CURSOR, getString(R.string.all_photos));
	}

	@Override
	protected void onResume() {
		super.onResume();
		PhotoChooseMgr.getInstance(getApplication()).initSelected();
		changeSelectedCount();
	}

	private int		curAlbumID;
	private String	curAlbumName;
	private int 	mLoadTimes = 0;

	/**
	 * 根据相册ID刷新相册
	 *
	 * @param id
	 * @param albumName
	 */
	public void refreshGridViewByAlbumId(int id, String albumName) {
		DebugLog.d("album id = " + id);
		header_back.setText(albumName);
		mLoadTimes = 0;
		curAlbumID = id;
		curAlbumName = albumName;

		if (id == LoadeImageConsts.LOADER_IMAGE_CURSOR) {
			PhotoChooseMgr.getInstance(getApplication()).setTakePhoto(PhotoChooseMgr.getInstance(getApplication()).isTakePhoto());
		}
		else {
			PhotoChooseMgr.getInstance(getApplication()).setTakePhoto(false);
		}
		this.albumId = id;
		getSupportLoaderManager().initLoader(id, null, this);
	}

	private boolean	isChangeTitleColor;

	/**
	 * 初始化标题栏
	 */
	private void initHeader() {
		isChangeTitleColor = getIntent().getBooleanExtra("change_title_color", false);
		header_back = (TextView) findViewById(R.id.header_back);
		header_title = (TextView) findViewById(R.id.header_title);
		findViewById(R.id.choose_image_header).setBackgroundResource(R.color.color_04);
		header_back.setTextColor(getResources().getColor(R.color.color_01));
		header_title.setTextColor(getResources().getColor(R.color.color_01));
		header_title.setText(getString(R.string.pic_pick));

		header_right_button = (TextView) findViewById(R.id.header_right_button);

		if (PhotoChooseMgr.getInstance(getApplication()).getSelectCount() > 0) {
			header_right_button.setEnabled(true);
			header_right_button.setTextColor(getResources().getColor(R.color.color_01));
		}
		else {
			header_right_button.setEnabled(false);
			header_right_button.setTextColor(Color.GRAY);
		}
		header_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//PhotoChooseMgr.getInstance(getApplication()).clearSelect();
				onBackPressed();
			}
		});
		header_right_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	/**
	 * 设置已选中图片的数量
	 */
	public void changeSelectedCount() {

		PhotoChooseMgr imageLoaderMgr = PhotoChooseMgr.getInstance(getApplication());
		int selectedCount = imageLoaderMgr.getSelectCount();
		//有选择图片，完成按钮可点击
		if (selectedCount > 0) {
			header_right_button.setEnabled(true);
			header_right_button.setTextColor(getResources().getColor(R.color.color_01));
			header_right_button.setText(getString(R.string.select_done, selectedCount, imageLoaderMgr.getMaxSelectSize()));
		}
		//没有选择图片，完成按钮不可点击
		else {
			header_right_button.setEnabled(false);
			header_right_button.setTextColor(Color.GRAY);
			header_right_button.setText(getString(R.string.complete));
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		DebugLog.d("album id = " + id);
		String selection = null;
		String[] selectionArgs = null;
		if (albumId != null && albumId != 1) {
			selection = "bucket_id=?";
			selectionArgs = new String[] { "" + id };
		}
		String orderBy = MediaStore.Images.Media._ID + " DESC";//MediaStore.Images.Media._ID + " ASC";MediaStore.Images.Media.DATE_TAKEN + " DESC";
		return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, LOADING_COLUMN, selection, selectionArgs, orderBy);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {

		// 第一次默认取全部图片，所以相册第一项为所有图片
		if (/*loader.getId() == LoadeImageConsts.LOADER_IMAGE_CURSOR &&*/ mLoadTimes<2) {
			//initFirstAlbum(loader, cursor);
			adapter.setLoadCursor(cursor);
		}
		/*else {
			adapter.setLoadCursor(cursor);
		}*/
		this.cursor = cursor;

		new Thread(new Runnable() {
			@Override
			public void run() {
				checkErrorImg(cursor);
			}
		}).run();

		mLoadTimes++;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (this.cursor != null && !this.cursor.isClosed()) {
			this.cursor.close();
		}
	}

	/**
	 * 检测该相册内所有的图片，并删除无效图片URI
	 */
	private boolean checkErrorImg(Cursor cursor) {
		cursor.moveToPosition(0);
		boolean hasErrorImg = false;
		while (cursor.moveToNext()) {
			AlbumItem item = new AlbumItem();
			item.firstImageId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
			item.firstImagePath = Utils.getImagePath(getApplication(), item.firstImageId);
			try {
				//判断图片是否存在
				if (!new File(item.firstImagePath).exists()) {
					DebugLog.e(item.firstImagePath + " not exist, drop it; ");
					hasErrorImg = true;
					Utils.delErrorImgURI(getApplication(), item.firstImageId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return hasErrorImg;
	}

	/**
	 * 初始化第一个相册 --> 所有图片
	 */
	private void initFirstAlbum(Loader<Cursor> loader, Cursor cursor) {
		AlbumItem item = new AlbumItem();
		cursor.moveToPosition(0);
		int imgCount = cursor.getCount();

		if (imgCount <= 0) {
			tv_no_photo.setVisibility(View.VISIBLE);
			gridView.setVisibility(View.GONE);
		}
		else {
			gridView.setVisibility(View.VISIBLE);
			tv_no_photo.setVisibility(View.GONE);

		}

		while (cursor.moveToNext()) {
			item.firstImageId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
			item.firstImagePath = Utils.getImagePath(getApplication(), item.firstImageId);
			try {
				//判断图片是否存在
				if (new File(item.firstImagePath).exists()) {
					item.id = loader.getId();
					item.imageCount = imgCount;
					item.albumName = getString(R.string.all_photos);
					albumFragment.setFirstItem(item);
					break;
				}
				else {
					--imgCount;
					DebugLog.e(item.firstImagePath + " not exist, drop it; ");
					getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?", new String[] { item.firstImageId + "" });
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		//点击返回按钮，首先判断预览Fragment是否存在，若存在，先关闭预览Fragment
		if (previewFragment != null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
			ft.remove(previewFragment).commit();
			previewFragment = null;
			adapter.notifyDataSetChanged();
			changeSelectedCount();
			return;
		}
		PhotoChooseMgr.getInstance(getApplication()).backAction();
		super.onBackPressed();
	}

	@Override
	public void finish() {
		super.finish();

		if (/*PhotoChooseMgr.getInstance(getApplication()).getMaxSelectSize() == 1 && */
				PhotoChooseMgr.getInstance(getApplication()).getSeletectList().size() == 1 //
				) {
			EventBus.getDefault().post(PhotoChooseMgr.getInstance(getApplication()).getSeletectList().get(0));
		}
	}

	private void showPreviewFragment(PreviewFragment previewFragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
		ft.replace(R.id.preview, previewFragment).commit();
	}

	/**
	 * 显示预览
	 */
	public void showPreview() {
		if (PhotoChooseMgr.getInstance(getApplication()).getSeletectList().size() <= 0) {
			Toast.makeText(PhotoChooseActivity.this, getString(R.string.have_no_chosen), Toast.LENGTH_LONG).show();
		}
		else {
			previewFragment = new PreviewFragment();
			Bundle b = new Bundle();
			b.putBoolean("change_title_color", isChangeTitleColor);
			previewFragment.setArguments(b);
			showPreviewFragment(previewFragment);
		}
	}

}
