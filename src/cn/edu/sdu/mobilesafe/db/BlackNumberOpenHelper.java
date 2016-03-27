package cn.edu.sdu.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberOpenHelper extends SQLiteOpenHelper {

	public BlackNumberOpenHelper(Context context) {
		super(context, "safe.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blacknumber(_id integer primary key autoincrement,number varchar(20),mode varchar(2))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
