package com.nextev.download;

public class DownloadValues {

	/** 用于传递和获取Types动作标识值的主键 */
	public static final String	TYPE				= "type";

	/**
	 * 用于传递和获取 "下载速度|资源已下载大小|资源总共大小" 的主键
	 */
	public static final String	PROCESS_SPEED		= "process_speed";

	/** 用户传递和获取下载百分比的主键 */
	public static final String	PROCESS_PROGRESS	= "process_progress";

	/** 记录每次添加到下载队列的资源URL */
	//	public static final String	URL					= "url";

	public static final String	APPINFO				= "appInfo";

	public static final String	ERROR_CODE			= "error_code";
	public static final String	ERROR_INFO			= "error_info";
	public static final String	IS_PAUSED			= "is_paused";

	public class Types {
		/** 下载进度更新标识 */
		public static final int	PROCESS		= 0x9000;

		/** 下载成功标识 */
		public static final int	COMPLETE	= 0x9001;

		/**
		 * 重新继续下载队列中所有任务， 如应用重启后需唤醒所有下载任务
		 */
		public static final int	START		= 0x9002;

		/** 暂停下载某个下载任务 */
		public static final int	PAUSE		= 0x9003;

		/** 将一个下载任务从队列中移除 */
		public static final int	DELETE		= 0x9004;

		/** 暂停的下载任务重新唤醒标识 */
		public static final int	CONTINUE	= 0x9005;

		/** 添加新的下载任务标识 */
		public static final int	ADD			= 0x9006;

		/** 停止下载队列中所有任务 */
		public static final int	STOP		= 0x9007;

		/** 下载异常标识 */
		public static final int	ERROR		= 0x9008;
	}

	public class Actions {
		/** 启动下载服务的Intent Action */
		public static final String	DOWNLOAD_SERVICE_ACTION		= "com.nextev.services.IDownloadService";

		/** 下载广播发送的Intent Action */
		public static final String	BROADCAST_RECEIVER_ACTION	= "com.nextev.DownloadMgr";

	}
}
