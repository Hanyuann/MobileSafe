package cn.edu.sdu.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.bean.TaskInfo;

public class TaskInfoParser {
	public static List<TaskInfo> getTaskInfos(Context context) {
		PackageManager packageManager = context.getPackageManager();

		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			TaskInfo taskInfo = new TaskInfo();
			// ��ȡ����������
			String processName = runningAppProcessInfo.processName;
			taskInfo.setPackageName(processName);
			try {
				// �õ��ڴ�Ļ�����Ϣ
				MemoryInfo[] memoryInfo = activityManager
						.getProcessMemoryInfo(new int[] { runningAppProcessInfo.pid });
				long totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty() * 1024;
				taskInfo.setMemorySize(totalPrivateDirty);

				PackageInfo packageInfo = packageManager.getPackageInfo(
						processName, 0);
				Drawable icon = packageInfo.applicationInfo
						.loadIcon(packageManager);
				taskInfo.setIcon(icon);
				String appName = packageInfo.applicationInfo.loadLabel(
						packageManager).toString();
				taskInfo.setAppName(appName);

				// ��ȡ����ǰӦ�ó���ı��
				int flags = packageInfo.applicationInfo.flags;
				if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					taskInfo.setUserProcess(false);
				} else {
					taskInfo.setUserProcess(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// ��Щϵͳ����û��ͼ��
				taskInfo.setAppName("ϵͳ����");
				taskInfo.setIcon(context.getResources().getDrawable(
						R.drawable.ic_launcher));
			}
			taskInfos.add(taskInfo);
		}
		return taskInfos;
	}
}
