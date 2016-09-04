package com.nextev.download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;

import com.nextev.photochooser.util.DebugLog;
import com.google.gson.Gson;


public class DownloadUtil {
	private static final String				TAG						= DownloadUtil.class.getSimpleName();

	/** ----------------------------- 初始化环境参数 --------------------------------- */
	public static String					FILE_PATH				= null;
	

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		}
		else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED
							|| info[i].getState() == NetworkInfo.State.CONNECTING) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void InitPath(Context ctx) {
		File temFile = Environment.getExternalStorageDirectory();

		if (temFile != null && temFile.canWrite() && AppFileManager.getInstance(ctx).getAvailableExternalMemorySize() > 0) {
			FILE_PATH = AppFileManager.getInstance(ctx).getAppDir().getAbsolutePath() + "/Downloads/";
		}
		else { 
			FILE_PATH = AppFileManager.getInstance(ctx).getAppDir().getAbsolutePath() + File.separator;
		}
		
		new File(FILE_PATH).mkdirs();
		
		//AppFileManager.getInstance(ctx).createFileDir("Downloads");
	}
	
	/** --------------------------------- 下载数据 --------------------------------- */
	public static final String	DL_PREFERENCE_NAME	= "com.qw.download";

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(DL_PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public static void cleanAllData(Context context){
		getPreferences(context).edit().clear().commit();
	}

	public static HashMap<String, String> getAll(Context context) {
		SharedPreferences preferences = getPreferences(context);

		HashMap<String, String> allDlInfo = new HashMap<String, String>();
		Map<String, ?> allContent = preferences.getAll();
		for (Map.Entry<String, ?> entry : allContent.entrySet()) {
			String key = entry.getKey();
			DebugLog.d(TAG, "key -->  " + key);

			if (entry.getValue() instanceof String) {
				allDlInfo.put(entry.getKey(), (String) entry.getValue());
			}
		}
		return allDlInfo;
	}

	public static String getString(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getString(key, "");
		else
			return "";
	}

	public static void setString(Context context, String key, String value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	public static void clearString(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.remove(key);
			editor.commit();
		}
	}

	public static final int		URL_COUNT	= DownloadTask.MAX_DOWNLOAD_THREAD_COUNT;
	public static final String	KEY_URL		= "url";

	public static void storeURL(Context context, String index, DLFileInfo dLFileInfo) {
		String s = new Gson().toJson(dLFileInfo);
		setString(context, KEY_URL + index, s);
	}

	public static void clearURL(Context context, String index) {
		//setString(context, KEY_URL + index, "");
		clearString(context, KEY_URL + index);
	}

	public static String getURL(Context context, String index) {
		return getString(context, KEY_URL + index);
	}

	public static List<DLFileInfo> getURLArray(Context context) {
		List<DLFileInfo> urlList = new ArrayList<DLFileInfo>();

		HashMap<String, String> allDlInfo = getAll(context);
		for (Map.Entry<String, String> entry : allDlInfo.entrySet()) {

			String key = entry.getKey();
			String value = entry.getValue();
			DebugLog.d(TAG, "getURLArray() --> key = " + key);
			DebugLog.d(TAG, "getURLArray() --> value = " + value);

			if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
				urlList.add(new Gson().fromJson(value, DLFileInfo.class));
			}
		}

		return urlList;
	}
}
