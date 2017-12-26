package com.nextev.photoview;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nextev.photochooser.R;

@SuppressLint("NewApi")
public class PhotoPreviewWithDel extends Dialog {

	public static final String	PHOTO_DEL_REFIX	= "PhotoDelRefix_";
	private Context				context;
	private RelativeLayout		preview_image_header;
	private TextView			header_back;
	private TextView			tv_del;
	private PhotoView			iv_preview;

	/** 当前图片预览 */
	String						photoImg;

	public PhotoPreviewWithDel(Context context, String photoImg) {
		super(context, R.style.PhotoViewDialog);
		this.context = context;
		this.photoImg = photoImg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化View
		LayoutInflater factory = LayoutInflater.from(context);
		View view = factory.inflate(R.layout.photo_preview_with_del, null);
		setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		preview_image_header = (RelativeLayout) findViewById(R.id.preview_image_header);
		header_back = (TextView) findViewById(R.id.header_back);
		tv_del = (TextView) findViewById(R.id.tv_del);
		iv_preview = (PhotoView) view.findViewById(R.id.iv_preview);

		header_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PhotoPreviewWithDel.this.dismiss();
			}

		});

		tv_del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//EventBus.getDefault().post(PHOTO_DEL_REFIX + photoImg);
				PhotoPreviewWithDel.this.dismiss();
			}

		});

		/*iv_preview.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
		
			@Override
			public void onPhotoTap(View view, float x, float y) {
				handleHearderOrBottom();
			}
		
			@Override
			public void onOutsidePhotoTap() {
		
			}
		});
		
		String imgpath = photoImg;
		//获取网络图片显示
		if (imgpath.startsWith("http")) {
			ImageLoader.getInstance().displayImage(imgpath, iv_preview, ImageLoaderUtil.getInstance(context).createNoRoundedOptions(R.drawable.image_loading_default));
		}
		//显示本地图片
		else {
			if (!imgpath.startsWith("file")) {
				imgpath = ImageDownloader.Scheme.FILE.wrap(imgpath);
			}
			ImageLoader.getInstance().displayImage(imgpath, iv_preview, ImageLoaderUtil.getInstance(context).createNoRoundedOptions(R.drawable.image_loading_default));
		}*/

		Glide.with(context).load(photoImg).into(iv_preview);

	}

	@Override
	public void show() {
		super.show();

		//设置Dialog为全屏铺开
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(PhotoPreviewWithDel.this.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		PhotoPreviewWithDel.this.getWindow().setAttributes(lp);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	int		mHeaderHeight;
	int		mShortAnimTime;
	boolean	visible	= true;

	public void handleHearderOrBottom() {

		//DebugLog.d("visible = " + visible);
		if (mHeaderHeight == 0) {
			mHeaderHeight = preview_image_header.getHeight();
		}
		if (mShortAnimTime == 0) {
			mShortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
		}
		preview_image_header.animate().translationY(visible ? -mHeaderHeight : 0).setDuration(mShortAnimTime);
		visible = !visible;
	}
}
