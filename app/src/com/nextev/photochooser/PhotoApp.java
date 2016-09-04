package com.nextev.photochooser;

import android.app.Application;
import android.content.Context;

import com.nextev.photochooser.util.SketchManager;

public class PhotoApp extends Application {

    private static Context context;
    
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		//ImageLoaderUtil.getInstance(this);
		SketchManager sketchManager = new SketchManager(getBaseContext());
		sketchManager.initConfig();
		sketchManager.initDisplayOptions();
	}
	
	public static Context getContext(){
	    return context;
	}
}
