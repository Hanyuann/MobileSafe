package cn.edu.sdu.mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {
	public interface BackUpCallBackSms {
		public void before(int count);

		public void onBackUpSms(int progress);
	}

	public static boolean backUp(Context context, BackUpCallBackSms callback) {

		// �ж��Ƿ���SD��
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			ContentResolver resolver = context.getContentResolver();
			// ��ȡ���ŵ�·��
			Uri uri = Uri.parse("content://sms/");
			Cursor cursor = resolver.query(uri, new String[] { "address",
					"date", "type", "body" }, null, null, null);
			// ��ȡ��ǰһ���ж��ٶ���
			int count = cursor.getCount();
			callback.before(count);
			// pd.setMax(count);
			// ����������
			int progress = 0;

			try {
				File file = new File(Environment.getExternalStorageDirectory(),
						"backup.xml");
				FileOutputStream fos = new FileOutputStream(file);
				XmlSerializer serializer = Xml.newSerializer();
				// �Ѷ������л���SD����Ȼ�����ñ����ʽ
				serializer.setOutput(fos, "utf-8");

				serializer.startDocument("utf-8", true);
				serializer.startTag(null, "smss");
				serializer.attribute(null, "size", String.valueOf(count));
				while (cursor.moveToNext()) {
					serializer.startTag(null, "sms");
					serializer.startTag(null, "address");
					serializer.text(cursor.getString(0));
					serializer.endTag(null, "address");
					serializer.startTag(null, "date");
					serializer.text(cursor.getString(1));
					serializer.endTag(null, "date");
					serializer.startTag(null, "type");
					serializer.text(cursor.getString(2));
					serializer.endTag(null, "type");
					serializer.startTag(null, "body");
					// ʹ������(��Կ)��������
					serializer.text(Crypto.encrypt("123", cursor.getString(3)));
					serializer.endTag(null, "body");
					serializer.endTag(null, "sms");
					// SystemClock.sleep(200);

					progress++;
					// pd.setProgress(progress);
					callback.onBackUpSms(progress);
					// SystemClock.sleep(200);
				}
				cursor.close();
				serializer.endTag(null, "smss");
				serializer.endDocument();
				fos.flush();
				fos.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
