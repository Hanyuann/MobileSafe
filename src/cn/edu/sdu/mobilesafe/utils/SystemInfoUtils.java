package cn.edu.sdu.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class SystemInfoUtils {
	public static int getProcessCount(Context context) {
		// 得到进程管理者
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取到当前手机上边运行的所有进程
		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		// 获取进程数
		return runningAppProcesses.size();
	}

	public static long getAvailMem(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		// 获取内存的基本信息
		activityManager.getMemoryInfo(memoryInfo);
		// 获取到剩余内存
		return memoryInfo.availMem;
	}

	public static long getTotalMem() {
		// 获取到总内存
		// 低版本不适用此方法,可通过proc/meminfo找到内存信息
		// long totalMem = memoryInfo.totalMem;
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File("/proc/meminfo"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			String readLine = reader.readLine();
			StringBuffer sb = new StringBuffer();
			for (char c : readLine.toCharArray()) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString()) * 1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 判断一个服务是否处于运行状态
	public static boolean isServiceRunning(Context context, String className) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(200);
		for (RunningServiceInfo info : infos) {
			String serviceClassName = info.service.getClassName();
			if (className.equals(serviceClassName)) {
				return true;
			}
		}
		return false;
	}
}
