package com.nextev.photoview;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nextev.photochooser.R;
import com.nextev.download.DLFileInfo;
import com.nextev.download.DownloadHelper;
import com.nextev.download.DownloadManager;
import com.nextev.download.DownloadTask;
import com.nextev.download.DownloadUtil;
import com.nextev.download.DownloadValues;
import com.nextev.photochooser.util.DebugLog;
import com.nextev.photochooser.util.DepthPageTransformer;
import com.nextev.photochooser.util.FixedScroller;

import java.lang.reflect.Field;
import java.util.List;


@SuppressLint("NewApi")
public class PhotoPreview extends Dialog {
	private Context				context;
	private ViewPager			vp_preview;
	private TextView			header_back;
	private TextView			tv_del;
	private RelativeLayout		preview_image_header;

	PhotoPreviewAdapter			mAdapter;
	/** 当前图片预览List */
	List<String>				photoImgList;
	/** 预览偏移量 */
	int							offset;
	/** 是否带有删除功能 */
	boolean						withDel			= false;
	/** 当前预览图片位置 */
	int							mPosition		= -1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			DownloadTask task = null;
			if (msg.obj instanceof DownloadTask) {
				task = (DownloadTask) msg.obj;
			}

			switch (msg.what) {
				case DownloadValues.Types.COMPLETE:
					handleCompletedAction(task.getDLFileInfo());
					break;
				case DownloadValues.Types.ERROR:
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 下载成功
	 */
	private void handleCompletedAction(DLFileInfo info) {
		DebugLog.d("Download Completed ---> " + info.getFileName());
		if(info.getFileType().equalsIgnoreCase("jpg") || info.getFileType().equalsIgnoreCase("png") ){
			Toast.makeText(context, context.getString(R.string.pic_save_success), Toast.LENGTH_SHORT);
		}
	}

	public PhotoPreview(Context context, List<String> list, int offset) {
		super(context, R.style.PhotoViewDialog);
		this.context = context;
		this.photoImgList = list;
		this.offset = offset;
	}

	public PhotoPreview(Context context, List<String> list, int offset, boolean withDel) {
		super(context, R.style.PhotoViewDialog);
		this.context = context;
		this.photoImgList = list;
		this.offset = offset;
		this.withDel = withDel;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化View
		LayoutInflater factory = LayoutInflater.from(context);
		View view = factory.inflate(R.layout.photo_preview, null);
		setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		vp_preview = (ViewPager) findViewById(R.id.vp_preview);
		header_back = (TextView) findViewById(R.id.header_back);
		tv_del = (TextView) findViewById(R.id.tv_del);
		preview_image_header = (RelativeLayout) findViewById(R.id.preview_image_header);

		preview_image_header.setBackgroundResource(R.color.color_04);

		if (withDel) {
			preview_image_header.setVisibility(View.VISIBLE);
		}
		else {
			preview_image_header.setVisibility(View.GONE);
		}

		header_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PhotoPreview.this.dismiss();
			}

		});

		tv_del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPosition != -1 && mPosition + 1 <= photoImgList.size()) {
					//EventBus.getDefault().post(new ET_ChoosedPicDeleted(photoImgList.get(mPosition)));
					photoImgList.remove(mPosition);
					mAdapter.notifyDataSetChanged();
				}

				if (photoImgList.size() <= 0) {
					PhotoPreview.this.dismiss();
				}
			}
		});

		vp_preview.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				mPosition = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		initAdapter();
	}

	public void initAdapter() {

		mAdapter = new PhotoPreviewAdapter(this, photoImgList, 0);
		vp_preview.setAdapter(mAdapter);
		vp_preview.setCurrentItem(offset);
		mPosition = offset;

		//将当前类的Handler传入下载管理类，在页面销毁是移除
		DownloadManager.getInstance(context).addHandler(handler);

		//替换Viewpager默认切换动画
		initVPAnim();
	}

	@Override
	protected void onStop() {
		super.onStop();
		//在页面销毁时移除
		DownloadManager.getInstance(context).clearHandler(handler);
	}

	@Override
	public void show() {
		super.show();

		//设置Dialog为全屏铺开
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(PhotoPreview.this.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		PhotoPreview.this.getWindow().setAttributes(lp);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	int		mHeaderHeight;
	int		mShortAnimTime;
	boolean	visible	= true;

	public void handleHearderOrBottom() {
		PhotoPreview.this.dismiss();

		/*
		DebugLog.d("visible = " + visible);
		if (mHeaderHeight == 0) {
			mHeaderHeight = preview_image_header.getHeight();
		}
		if (mShortAnimTime == 0) {
			mShortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
		}
		preview_image_header.animate().translationY(visible ? -mHeaderHeight : 0).setDuration(mShortAnimTime);

		visible = !visible;
		*/
	}

	/**
	 * 设置Viewpager切换动画，和动画时间
	 */
	private void initVPAnim() {
		try {
			Field mScroller;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
			FixedScroller scroller = new FixedScroller(vp_preview.getContext(), sInterpolator);
			mScroller.set(vp_preview, scroller);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//vp_preview.setPageTransformer(true, new ZoomOutPageTransformer());
		vp_preview.setPageTransformer(true, new DepthPageTransformer());

	}


	// 下载文件方法
	public void addDLFile(String fileUrl, String fileType) {
		DownloadUtil.InitPath(context);
		DLFileInfo dLFileInfo = new DLFileInfo();
		dLFileInfo.setFilePath(DownloadUtil.FILE_PATH);
		dLFileInfo.setFileUrl(fileUrl);
		dLFileInfo.setFileType(fileType);
		dLFileInfo.setFileName(getFileName(fileUrl, fileType));

		DebugLog.d("Add DL File() --> = " + dLFileInfo.getFilePath() + dLFileInfo.getFileName());
		DownloadHelper.addNewTask(context, dLFileInfo, new DownloadHelper.PreDownloadStatusListener() {

			@Override
			public void sdCardCannotWriteOrRead(int errorCode, String errorInfo) {
				Toast.makeText(context, "不能读写SD卡", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void notFoundSDCard(int errorCode, String errorInfo) {
				Toast.makeText(context, "没有SD卡", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void moreTaskCount(int errorCode, String errorInfo) {
				Toast.makeText(context, "任务列表已满", Toast.LENGTH_SHORT).show();
			}
		});
	}

	// 文件名格式
	public String getFileName(String url, String fileType) {
		return Integer.toHexString(url.hashCode()) + "." + fileType;
	}
}
