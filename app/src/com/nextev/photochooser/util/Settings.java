package com.nextev.photochooser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class Settings {
    private static final String PREFERENCE_SCROLLING_PAUSE_LOAD = "PREFERENCE_SCROLLING_PAUSE_LOAD";
    private static final String PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS = "PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS";
    private static final String PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD = "PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD";
    private static final String PREFERENCE_SHOW_IMAGE_FROM_FLAG = "PREFERENCE_SHOW_IMAGE_FROM_FLAG";
    private static final String PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD = "PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD";
    private static final String PREFERENCE_CLICK_DISPLAY_ON_FAILED = "PREFERENCE_CLICK_DISPLAY_ON_FAILED";
    private static final String PREFERENCE_CLICK_SHOW_PRESSED_STATUS = "PREFERENCE_CLICK_SHOW_PRESSED_STATUS";
    private static final String PREFERENCE_CACHE_IN_MEMORY = "PREFERENCE_CACHE_IN_MEMORY";
    private static final String PREFERENCE_CACHE_IN_DISK = "PREFERENCE_CACHE_IN_DISK";
    private static final String PREFERENCE_IMAGES_OF_LOW_QUALITY = "PREFERENCE_IMAGES_OF_LOW_QUALITY";

    private static Settings settingsInstance;

    private boolean scrollingPauseLoad;
    private boolean showImageDownloadProgress;
    private boolean mobileNetworkPauseDownload;
    private boolean showImageFromFlag;
    private boolean clickDisplayOnPauseDownload;
    private boolean clickDisplayOnFailed;
    private boolean showPressedStatus;
    private boolean cacheInMemory;
    private boolean cacheInDisk;
    private boolean lowQualityImage;

    private SharedPreferences.Editor editor;

    private Settings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = preferences.edit();

        this.scrollingPauseLoad = preferences.getBoolean(PREFERENCE_SCROLLING_PAUSE_LOAD, false);
        this.showImageDownloadProgress = preferences.getBoolean(PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS, false);
        this.mobileNetworkPauseDownload = preferences.getBoolean(PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD, false);
        this.showImageFromFlag = preferences.getBoolean(PREFERENCE_SHOW_IMAGE_FROM_FLAG, false);
        this.clickDisplayOnPauseDownload = preferences.getBoolean(PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD, true);
        this.clickDisplayOnFailed = preferences.getBoolean(PREFERENCE_CLICK_DISPLAY_ON_FAILED, true);
        this.showPressedStatus = preferences.getBoolean(PREFERENCE_CLICK_SHOW_PRESSED_STATUS, true);
        this.cacheInMemory = preferences.getBoolean(PREFERENCE_CACHE_IN_MEMORY, true);
        this.cacheInDisk = preferences.getBoolean(PREFERENCE_CACHE_IN_DISK, true);
        this.lowQualityImage = preferences.getBoolean(PREFERENCE_IMAGES_OF_LOW_QUALITY, false);
    }

    public static Settings with(Context context){
        if(settingsInstance == null){
            synchronized (Settings.class){
                if(settingsInstance == null){
                    settingsInstance = new Settings(context);
                }
            }
        }
        return settingsInstance;
    }

    private void apply(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            editor.apply();
        }else{
            editor.commit();
        }
    }

    /**
     * ??????????????
     * @return ??????????????
     */
    public boolean isScrollingPauseLoad(){
        return scrollingPauseLoad;
    }

    /**
     * ????????????????
     * @param scrollingPauseLoad ??????????????
     */
    public void setScrollingPauseLoad(boolean scrollingPauseLoad){
        this.scrollingPauseLoad = scrollingPauseLoad;
        editor.putBoolean(PREFERENCE_SCROLLING_PAUSE_LOAD, scrollingPauseLoad);
        apply();
    }

    /**
     * ??????????
     * @return ??????????
     */
    public boolean isShowImageDownloadProgress() {
        return showImageDownloadProgress;
    }

    /**
     * ????????????
     * @param showImageDownloadProgress ??????????
     */
    public void setShowImageDownloadProgress(boolean showImageDownloadProgress) {
        this.showImageDownloadProgress = showImageDownloadProgress;
        editor.putBoolean(PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS, showImageDownloadProgress);
        apply();
    }

    /**
     * ??????????????
     * @return ??????????????
     */
    public boolean isMobileNetworkPauseDownload() {
        return mobileNetworkPauseDownload;
    }

    /**
     * ????????????????
     * @param mobileNetworkPauseDownload ??????????????
     */
    public void setMobileNetworkPauseDownload(boolean mobileNetworkPauseDownload) {
        this.mobileNetworkPauseDownload = mobileNetworkPauseDownload;
        editor.putBoolean(PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD, mobileNetworkPauseDownload);
        apply();
    }

    /**
     * ??????????
     * @return ????????
     */
    public boolean isShowImageFromFlag() {
        return showImageFromFlag;
    }

    /**
     * ??????????
     * @param showImageFromFlag ????????
     */
    public void setShowImageFromFlag(boolean showImageFromFlag) {
        this.showImageFromFlag = showImageFromFlag;
        editor.putBoolean(PREFERENCE_SHOW_IMAGE_FROM_FLAG, showImageFromFlag);
        apply();
    }

    public boolean isClickDisplayOnFailed() {
        return clickDisplayOnFailed;
    }

    public void setClickDisplayOnFailed(boolean clickDisplayOnFailed) {
        this.clickDisplayOnFailed = clickDisplayOnFailed;
        editor.putBoolean(PREFERENCE_CLICK_DISPLAY_ON_FAILED, clickDisplayOnFailed);
        apply();
    }

    public boolean isClickDisplayOnPauseDownload() {
        return clickDisplayOnPauseDownload;
    }

    public void setClickDisplayOnPauseDownload(boolean clickDisplayOnPauseDownload) {
        this.clickDisplayOnPauseDownload = clickDisplayOnPauseDownload;
        editor.putBoolean(PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD, clickDisplayOnPauseDownload);
        apply();
    }

    public boolean isShowPressedStatus() {
        return showPressedStatus;
    }

    public void setShowPressedStatus(boolean showPressedStatus) {
        this.showPressedStatus = showPressedStatus;
        editor.putBoolean(PREFERENCE_CLICK_SHOW_PRESSED_STATUS, showPressedStatus);
        apply();
    }

    public boolean isCacheInDisk() {
        return cacheInDisk;
    }

    public void setCacheInDisk(boolean cacheInDisk) {
        this.cacheInDisk = cacheInDisk;
        editor.putBoolean(PREFERENCE_CACHE_IN_DISK, cacheInDisk);
        apply();
    }

    public boolean isCacheInMemory() {
        return cacheInMemory;
    }

    public void setCacheInMemory(boolean cacheInMemory) {
        this.cacheInMemory = cacheInMemory;
        editor.putBoolean(PREFERENCE_CACHE_IN_MEMORY, cacheInMemory);
        apply();
    }

    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    public void setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        editor.putBoolean(PREFERENCE_IMAGES_OF_LOW_QUALITY, lowQualityImage);
        apply();
    }
}
