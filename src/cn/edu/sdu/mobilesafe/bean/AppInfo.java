package cn.edu.sdu.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private Drawable icon;
	private String apkName;
	private long apkSize;
	private boolean isUserApp;
	private boolean isRom;// ´æ´¢µÄÎ»ÖÃ
	private String apkPackageName;
	private int uid;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public long getApkSize() {
		return apkSize;
	}

	public void setApkSize(long apkSize) {
		this.apkSize = apkSize;
	}

	public boolean isUserApp() {
		return isUserApp;
	}

	public void setUserApp(boolean isUserApp) {
		this.isUserApp = isUserApp;
	}

	public boolean isRom() {
		return isRom;
	}

	public void setRom(boolean isRom) {
		this.isRom = isRom;
	}

	public String getApkPackageName() {
		return apkPackageName;
	}

	public void setApkPackageName(String apkPackageName) {
		this.apkPackageName = apkPackageName;
	}

	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", apkName=" + apkName + ", apkSize="
				+ apkSize + ", isUserApp=" + isUserApp + ", isRom=" + isRom
				+ ", apkPackageName=" + apkPackageName + ", uid=" + uid + "]";
	}

}
