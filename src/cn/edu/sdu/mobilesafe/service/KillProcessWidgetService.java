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
		// ÿ��5���Ӹ���һ������
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
				// ������findViewById
				views.setTextViewText(R.id.process_count,
						"�������� �����:" + String.valueOf(processCount));
				views.setTextViewText(
						R.id.process_memory,
						"�����ڴ�"
								+ Formatter.formatFileSize(
										getApplicationContext(), availMem));
				Intent intent = new Intent();
				//����һ����ʽ��ͼ
				intent.setAction("cn.edu.sdu.mobilesafe");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent, 0);
				// ���õ���¼�
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				// �ڶ�����������ǰ����һ���㲥ȥ��������С�ؼ�
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyAppWidgetProvider.class);
				// ���¿ؼ�
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