package cn.edu.sdu.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.edu.sdu.mobilesafe.R;

public class ContactActivity extends Activity {

	private ListView lv_list;
	private ArrayList<HashMap<String, String>> readContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		lv_list = (ListView) findViewById(R.id.lv_list);
		readContact = readContact();

		lv_list.setAdapter(new SimpleAdapter(this, readContact,
				R.layout.list_item_contact, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));

		lv_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = readContact.get(position).get("phone");// 读取当前Item的电话号码
				String name = readContact.get(position).get("name");// 读取当前Item的电话号码
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				intent.putExtra("name", name);
				setResult(Activity.RESULT_OK, intent);
				finish();// 将数据放在intent中返回给上一个页面
			}
		});
	}

	private ArrayList<HashMap<String, String>> readContact() {
		// 首先从raw_contacts中读取联系人的id("contact_id")
		// 然后根据mimetype来区分哪个是联系人哪个是电话号码
		Uri rawContactsUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		Cursor rawContactCursor = getContentResolver().query(rawContactsUri,
				new String[] { "contact_id" }, null, null, null);
		if (rawContactCursor != null) {
			while (rawContactCursor.moveToNext()) {
				String contactId = rawContactCursor.getString(0);
				// 其次,根据cantact_id从data表中查询出相应的电话号码和联系人名称
				Cursor dataCursor = getContentResolver().query(dataUri,
						new String[] { "data1", "mimetype" }, "contact_id=?",
						new String[] { contactId }, null);
				if (dataCursor != null) {
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimetype)) {
							map.put("name", data1);
						}
					}
					list.add(map);
					dataCursor.close();
				}
			}
			rawContactCursor.close();
		}
		return list;
	}
}
