package cn.edu.sdu.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, 0).show();
	}
}
