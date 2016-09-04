package com.nextev.download;

import android.os.Parcel;
import android.os.Parcelable;

public class DLFileInfo implements Parcelable {
	private final String	TAG						= DLFileInfo.class.getSimpleName();
	/**下载初始化状态*/
	public static final int	STATUS_NULL				= 0;
	/**正在下载中*/
	public static final int	STATUS_DOWNLOADING		= 1;
	/**下载失败*/
	public static final int	STATUS_DOWNLOAD_FAIL	= 2;
	/**下载停止，人为操作*/
	public static final int	STATUS_DOWNLOAD_STOP	= 3;
	/**下载完成*/
	public static final int	STATUS_DOWNLOAD_END		= 4;

	//文件唯一标示ID
	private int				fileId;
	//文件大小
	private String			fileSize;
	//文件网络地址
	private String			fileUrl;
	//文件本地存储路径，只是文件夹路径
	private String			filePath;
	//文件名
	private String			fileName;
	//文件类型
	private String			fileType;
	//包名
	private String			pkgName;
	//待扩展字段
	private String			extraVal;
	//次序
	private int				sequence;
	//下载进度
	private int				progress				= 0;
	//文件下载状态
	private int				status					= STATUS_NULL;
	//当前文件下载是否有焦点
	private boolean			hasFocus;

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getExtraVal() {
		return extraVal;
	}

	public void setExtraVal(String extraVal) {
		this.extraVal = extraVal;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isHasFocus() {
		return hasFocus;
	}

	public void setHasFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}

	public DLFileInfo() {}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(fileId);
		dest.writeString(fileSize);
		dest.writeString(fileUrl);
		dest.writeString(filePath);
		dest.writeString(fileName);
		dest.writeString(fileType);
		dest.writeString(pkgName);
		dest.writeString(extraVal);
		dest.writeInt(sequence);
		dest.writeInt(progress);
		dest.writeInt(status);
		dest.writeByte((byte) (hasFocus ? 1 : 0));//if hasFocus == true, byte == 1
	}

	public DLFileInfo(Parcel source) {
		this.fileId = source.readInt();
		this.fileSize = source.readString();
		this.fileUrl = source.readString();
		this.filePath = source.readString();
		this.fileName = source.readString();
		this.fileType = source.readString();
		this.pkgName = source.readString();
		this.extraVal = source.readString();
		this.sequence = source.readInt();
		this.progress = source.readInt();
		this.status = source.readInt();
		this.hasFocus = source.readByte() != 0;//hasFocus == true if byte != 0
	}

	public static final Creator<DLFileInfo>	CREATOR	= new Creator<DLFileInfo>() {

																	@Override
																	public DLFileInfo createFromParcel(Parcel source) {
																		return new DLFileInfo(source);
																	}

																	@Override
																	public DLFileInfo[] newArray(int size) {
																		return new DLFileInfo[size];
																	}

																};
}
