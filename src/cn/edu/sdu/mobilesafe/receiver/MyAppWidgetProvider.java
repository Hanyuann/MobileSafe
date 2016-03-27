package cn.edu.sdu.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import cn.edu.sdu.mobilesafe.service.KillProcessWidgetService;

/**
 * 
 * 创建桌面小部件的步骤： 1 需要在清单文件里面配置元数据 2 需要配置当前元数据里面要用到xml res/xml 3 需要配置一个广播接受者 4
 * 实现一个桌面小部件的xml (根据需求。桌面小控件涨什么样子。就实现什么样子)
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		// System.out.println("onReceive");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		// System.out.println("onUpdate");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		// System.out.println("onDeleted");
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		// System.out.println("onEnabled");
		Intent intent = new Intent(context, KillProcessWidgetService.class);
		context.startService(intent);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		// System.out.println("onDisabled");
		Intent intent = new Intent(context, KillProcessWidgetService.class);
		context.stopService(intent);
	}

}
