package cn.edu.sdu.mobilesafe.utils;

import android.app.Activity;
import android.widget.Toast;

public class UIUtils {
	public static void showToast(final Activity context, final String text) {
		if("main".equals(Thread.currentThread().getName())){
		Toast.makeText(context, text, 0).show();
		}else{
			context.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					Toast.makeText(context, text, 0).show();
				}
			});
		}
	}
}
