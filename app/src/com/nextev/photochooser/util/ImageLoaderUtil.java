package com.nextev.photochooser.util;

import android.content.Context;

import com.nextev.photochooser.PhotoApp;

import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.ImageHolder;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.display.DefaultImageDisplayer;
import me.xiaopan.sketch.process.CircleImageProcessor;
import me.xiaopan.sketch.process.RoundedCornerImageProcessor;


public class ImageLoaderUtil {

	private static ImageLoaderUtil instance;

	private ImageLoaderUtil(Context context){
	}
	
	public static ImageLoaderUtil getInstance(Context context){
		if(instance == null){
			synchronized (ImageLoaderUtil.class) {
				if(instance == null){
					instance = new ImageLoaderUtil(context);
				}
			}
		}
		return instance;
	}

	/**
	 * ???????DisplayOptions??
	 * @param defaultImg ??????ID
	 * @param roundDip ????,???dip
	 * @return
	 */
	public DisplayOptions createRoundedOptions(int defaultImg, int roundDip){
		RoundedCornerImageProcessor rounded_05 = new RoundedCornerImageProcessor(Utils_Pix.dip2px(PhotoApp.getContext(), roundDip));
		DisplayOptions options = new DisplayOptions()//
				.setLoadingImage(new ImageHolder(defaultImg).setImageProcessor(rounded_05))//
				.setFailureImage(new ImageHolder(defaultImg).setImageProcessor(rounded_05))//
				.setPauseDownloadImage(new ImageHolder(defaultImg).setImageProcessor(rounded_05))//
				.setDecodeGifImage(false)//
				.setResizeByFixedSize(false)//
				.setForceUseResize(false)//
				.setImageDisplayer(new DefaultImageDisplayer())//
				.setImageProcessor(rounded_05);
		return options;
	}

	/**
	 * ????????DisplayOptions??
	 * @param defaultImg ??????ID
	 * @return
	 */
	public DisplayOptions createNoRoundedOptions(int defaultImg){
		DisplayOptions options = new DisplayOptions()//
				.setLoadingImage(new ImageHolder(defaultImg))//
				.setFailureImage(new ImageHolder(defaultImg))//
				.setPauseDownloadImage(new ImageHolder(defaultImg))//
				.setDecodeGifImage(false)//
				.setResizeByFixedSize(false)//
				.setForceUseResize(false)//
				.setImageDisplayer(new DefaultImageDisplayer());//
		return options;
	}

	/**
	 * ?????DisplayOptions??
	 *
	 * @param defaultImg
	 *            ??????ID
	 * @return
	 */
	public DisplayOptions createCircleOptions(int defaultImg) {
		DisplayOptions options = new DisplayOptions()//
				.setLoadingImage(new ImageHolder(defaultImg).setImageProcessor(CircleImageProcessor.getInstance()))//
				.setFailureImage(new ImageHolder(defaultImg).setImageProcessor(CircleImageProcessor.getInstance()))//
				.setPauseDownloadImage(new ImageHolder(defaultImg).setImageProcessor(CircleImageProcessor.getInstance()))//
				.setDecodeGifImage(false)//
				.setResizeByFixedSize(false)//
				.setForceUseResize(false)//
				.setImageDisplayer(new DefaultImageDisplayer()).setImageProcessor(CircleImageProcessor.getInstance());//
		return options;
	}

	/**
	 * ??????TAG?????Adapter???????
	 * @param view
	 * 			??View
	 * @param url
	 * 			??????
	 * @param optionsName
	 * 			UIL??Option
	 */
	public void imgDisplay(SketchImageView view, String url, Enum<?> optionsName, SketchImageView.ImageShape imageShape) {

		if(url == null){
			url = "";
		}

		if (view.getTag() == null || !view.getTag().equals(url)) {
			view.setTag(url);

			view.setDisplayOptions(optionsName);
			view.setImageShape(imageShape);
			view.displayImage(url);

		}
	}

	public void imgDisplay(SketchImageView view, String url, DisplayOptions displayOptions, SketchImageView.ImageShape imageShape) {

		if(url == null){
			url = "";
		}

		if (view.getTag() == null || !view.getTag().equals(url)) {
			view.setTag(url);

			view.setDisplayOptions(displayOptions);
			view.setImageShape(imageShape);
			view.displayImage(url);
		}
	}
}
