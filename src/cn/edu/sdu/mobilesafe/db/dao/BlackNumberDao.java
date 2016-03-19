package cn.edu.sdu.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import cn.edu.sdu.mobilesafe.bean.BlackNumberInfo;

public class BlackNumberDao {
	private BlackNumberOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new BlackNumberOpenHelper(context);
	}

	public boolean add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long rowid = db.insert("blacknumber", null, values);
		if (rowid == -1) {
			return false;
		} else {
			return true;
		}
	}

	public boolean delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int rowNumber = db.delete("blacknumber", "number=?",
				new String[] { number });
		if (rowNumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean changeNumberMode(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		int rowNumber = db.update("blacknumber", values, "number=?",
				new String[] { number });
		if (rowNumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	public String findNumber(String number) {
		String mode = "";
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[] { "mode" },
				"number=?", new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			mode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return mode;
	}

	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db
				.query("blacknumber", new String[] { "number", "mode" }, null,
						null, null, null, null);
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setNumber(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getString(1));
			blackNumberInfos.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		SystemClock.sleep(3000);
		return blackNumberInfos;
	}

	/**
	 * ��ҳ��������
	 * 
	 * @param pageNumber
	 *            ��ʾ��ǰ����һҳ
	 * @param pageSize
	 *            ��ʾÿһҳ�ж���������
	 * @return limit ��ʾ���Ƶ�ǰ�ж������� offset ��ʾ���� �ӵڼ�����ʼ
	 */
	public List<BlackNumberInfo> findPar(int pageNumber, int pageSize) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber limit ? offset ?",
				new String[] { String.valueOf(pageSize),
						String.valueOf(pageSize * pageNumber) });
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setMode(cursor.getString(1));
			blackNumberInfo.setNumber(cursor.getString(0));
			blackNumberInfos.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberInfos;
	}

	/**
	 * ������������
	 * 
	 * @param startIndex
	 *            ��ʼ��λ��
	 * @param maxCount
	 *            ÿҳչʾ��������Ŀ
	 * @return
	 */
	public List<BlackNumberInfo> findPar2(int startIndex, int maxCount) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber limit ? offset ?",
				new String[] { String.valueOf(maxCount),
						String.valueOf(startIndex) });
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setMode(cursor.getString(1));
			blackNumberInfo.setNumber(cursor.getString(0));
			blackNumberInfos.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberInfos;
	}

	// ��ȡ�ܵļ�¼��
	public int getTotalNumber() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		return count;
	}
}
