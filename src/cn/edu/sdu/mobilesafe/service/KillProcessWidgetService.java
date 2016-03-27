package cn.edu.sdu.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.receiver.MyAppWidgetProvider;
import cn.edu.sdu.mobilesafe.utils.SystemInfoUtils;

public class KillProcessWidgetService extends Service {

	private AppWidgetManager widgetManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		widgetManager = AppWidgetManager.getInstance(this);
		// 每隔5秒钟更新一次桌面
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				int processCount = SystemInfoUtils
						.getProcessCount(getApplicationContext());
				long availMem = SystemInfoUtils
						.getAvailMem(getApplicationContext());
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				// 并不是findViewById
				views.setTextViewText(R.id.process_count,
						"正在运行 的软件:" + String.valueOf(processCount));
				views.setTextViewText(
						R.id.process_memory,
						"可用内存"
								+ Formatter.formatFileSize(
										getApplicationContext(), availMem));
				Intent intent = new Intent();
				//发送一个隐式意图
				intent.setAction("cn.edu.sdu.mobilesafe");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent, 0);
				// 设置点击事件
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				// 第二个参数代表当前由哪一个广播去处理桌面小控件
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyAppWidgetProvider.class);
				// 更新控件
				widgetManager.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 5000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}