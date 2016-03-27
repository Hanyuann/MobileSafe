package cn.edu.sdu.mobilesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VirusDao {
	private static final String PATH = "data/data/cn.edu.sdu.mobilesafe/files/antivirus.db";

	// 检查当前的MD5值是否在病毒库中
	public static String checkVirus(String md5) {
		String desc = null;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select desc from datable where md5=?",
				new String[] { md5 });
		if (cursor.moveToNext()) {
			desc = cursor.getString(0);
		}
		cursor.close();
		return desc;
	}

	// 添加病毒特征码到数据库中
	public static void addVirus(String md5, String desc) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("type", 6);
		values.put("desc", desc);
		values.put("name", "Android.Troj.AirAD.a");
		db.insert("datable", null, values);
		db.close();
	}
}
