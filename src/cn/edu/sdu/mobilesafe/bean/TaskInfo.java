package cn.edu.sdu.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private Drawable icon;
	private String appName;
	private String packageName;
	private long memorySize;
	private boolean isUserProcess;
	private boolean isChecked;

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public long getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize;
	}

	public boolean isUserProcess() {
		return isUserProcess;
	}

	public void setUserProcess(boolean isUserProcess) {
		this.isUserProcess = isUserProcess;
	}

}
