package cn.edu.sdu.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//�����ز�ѯdao
public class AddressDao {
	private static final String PATH = "data/data/cn.edu.sdu.mobilesafe/files/address.db";// ע���·��������data/dataĿ¼,�������ݿ���ʲ���

	public static String getAddress(String number) {
		String address = "δ֪����";
		// ��ȡ���ݿ����
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);

		// ����������ʽƥ���ֻ���
		if (number.matches("^1[3-8]\\d{9}$")) {

			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		} else if (number.matches("^\\d+$")) {// ƥ������
			switch (number.length()) {
			case 3:
				address = "�����绰";
				break;
			case 4:
				address = "ģ����";
				break;
			case 5:
				address = "�ͷ��绰";
				break;
			case 7:
			case 8:
				address = "���ص绰";
				break;
			default:
				// ��;�绰
				if (number.startsWith("0") && number.length() > 10) {
					// ��Щ��������λ����Щ��������λ(����0)
					// ��ѯ��λ����
					Cursor cursor = database.rawQuery(
							"select location from data2 where area=?",
							new String[] { number.substring(1, 4) });
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					} else {
						cursor.close();
						// ��ѯ��λ����
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
		database.close();// �ر����ݿ�
		return address;
	}
}
