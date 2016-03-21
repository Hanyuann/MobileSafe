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
		// �õ����̹�����
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// ��ȡ����ǰ�ֻ��ϱ����е����н���
		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		// ��ȡ������
		return runningAppProcesses.size();
	}

	public static long getAvailMem(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		// ��ȡ�ڴ�Ļ�����Ϣ
		activityManager.getMemoryInfo(memoryInfo);
		// ��ȡ��ʣ���ڴ�
		return memoryInfo.availMem;
	}

	public static long getTotalMem() {
		// ��ȡ�����ڴ�
		// �Ͱ汾�����ô˷���,��ͨ��proc/meminfo�ҵ��ڴ���Ϣ
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

	// �ж�һ�������Ƿ�������״̬
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
