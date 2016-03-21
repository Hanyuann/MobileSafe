package cn.edu.sdu.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
	public static final String SP_NAME = "config";

	public static void saveBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context context, String key,
			boolean defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}
}
