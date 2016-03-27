package cn.edu.sdu.mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.edu.sdu.mobilesafe.utils.UIUtils;

public class KillAllProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
			// 杀死所有的进程
			activityManager
					.killBackgroundProcesses(runningAppProcess.processName);
		}
		Toast.makeText(context, "清理完毕", 0).show();
	}
}
