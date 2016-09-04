package com.nextev.photochooser.util;


import android.content.Context;

import com.nextev.photochooser.R;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.LoadOptions;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.DefaultImageDisplayer;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;

public class SketchManager {
	private Context	context;

	public SketchManager(Context context) {
		this.context = context;
	}

	public void initConfig() {
		Sketch.setDebugMode(DebugLog.isDebuggable());
		Settings settings = Settings.with(context);
		Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
		sketchConfiguration.setMobileNetworkPauseDownload(settings.isMobileNetworkPauseDownload());
		sketchConfiguration.setLowQualityImage(settings.isLowQualityImage());
		sketchConfiguration.setCacheInDisk(settings.isCacheInDisk());
		sketchConfiguration.setCacheInMemory(settings.isCacheInMemory());
	}

	public void initDisplayOptions() {
		//TransitionImageDisplayer transitionImageDisplayer = new TransitionImageDisplayer();
		DefaultImageDisplayer transitionImageDisplayer = new DefaultImageDisplayer();
		Sketch.putOptions(OptionsType.NORMAL_RECT, new DisplayOptions()//
				.setLoadingImage(R.drawable.image_loading)//
				.setFailureImage(R.drawable.image_failure)//
				.setPauseDownloadImage(R.drawable.image_pause_download)//
				.setDecodeGifImage(false)//
				.setImageDisplayer(transitionImageDisplayer));

		Sketch.putOptions(OptionsType.DETAIL, new DisplayOptions().setImageDisplayer(transitionImageDisplayer));


		Sketch.putOptions(OptionsType.WINDOW_BACKGROUND, new LoadOptions()//
				.setImageProcessor(new GaussianBlurImageProcessor(true))//
				.setDecodeGifImage(false));

	}
}
