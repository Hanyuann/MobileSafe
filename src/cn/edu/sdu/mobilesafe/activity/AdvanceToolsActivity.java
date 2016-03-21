package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.utils.SmsUtils;
import cn.edu.sdu.mobilesafe.utils.SmsUtils.BackUpCallBackSms;
import cn.edu.sdu.mobilesafe.utils.UIUtils;

//�߼�����
public class AdvanceToolsActivity extends Activity {
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	// �����ز�ѯ
	public void numberAddressQuery(View v) {
		startActivity(new Intent(this, AddressActivity.class));
	}

	// ���ű���
	public void backUpSms(View v) {
		pd = new ProgressDialog(AdvanceToolsActivity.this);
		pd.setTitle("��ʾ");
		pd.setMessage("�԰����꣬���ڱ���");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread() {
			public void run() {
				boolean result = SmsUtils.backUp(AdvanceToolsActivity.this,
						new BackUpCallBackSms() {
							@Override
							public void onBackUpSms(int progress) {
								pd.setProgress(progress);
							}

							@Override
							public void before(int count) {
								pd.setMax(count);
							}
						});
				if (result) {
					UIUtils.showToast(AdvanceToolsActivity.this, "���ݳɹ�");
				} else {
					UIUtils.showToast(AdvanceToolsActivity.this, "����ʧ��");
				}
				pd.dismiss();
			};
		}.start();
	}

	public void createShortcut(View v) {
		Intent intent = new Intent(this, ContactActivity.class);
		startActivityForResult(intent, 0);
	}

	public void createShortcutForMobileSafe(View v) {
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// ���������ظ���ݷ�ʽ
		intent.putExtra("duplicate", false);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Android������");
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("aaa.bbb");
		shortcutIntent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		UIUtils.showToast(this, "�ɹ�������ݷ�ʽ");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			Intent intent = new Intent();
			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
					.decodeResource(getResources(), R.drawable.ic_launcher));
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					data.getStringExtra("name"));
			Intent shortcutIntent = new Intent();
			shortcutIntent.setAction(Intent.ACTION_CALL);
			shortcutIntent.setData(Uri.parse("tel://"
					+ data.getStringExtra("phone")));
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			sendBroadcast(intent);
			UIUtils.showToast(this, "�ɹ�������ݷ�ʽ");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
