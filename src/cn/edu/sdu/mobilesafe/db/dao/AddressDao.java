package cn.edu.sdu.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//归属地查询dao
public class AddressDao {
	private static final String PATH = "data/data/cn.edu.sdu.mobilesafe/files/address.db";// 注意该路径必须是data/data目录,否则数据库访问不到

	public static String getAddress(String number) {
		String address = "未知号码";
		// 获取数据库对象
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);

		// 根据正则表达式匹配手机号
		if (number.matches("^1[3-8]\\d{9}$")) {

			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		} else if (number.matches("^\\d+$")) {// 匹配数字
			switch (number.length()) {
			case 3:
				address = "报警电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "客服电话";
				break;
			case 7:
			case 8:
				address = "本地电话";
				break;
			default:
				// 长途电话
				if (number.startsWith("0") && number.length() > 10) {
					// 有些区号是四位，有些区号是三位(包括0)
					// 查询四位区号
					Cursor cursor = database.rawQuery(
							"select location from data2 where area=?",
							new String[] { number.substring(1, 4) });
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					} else {
						cursor.close();
						// 查询三位区号
						cursor = database.rawQuery(
								"select location from data2 where area=?",
								new String[] { number.substring(1, 3) });
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
					}
				}
				break;
			}
		}
		database.close();// 关闭数据库
		return address;
	}
}
