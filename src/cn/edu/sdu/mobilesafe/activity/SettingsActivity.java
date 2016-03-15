package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.service.AddressService;
import cn.edu.sdu.mobilesafe.utils.ServiceStatusUtils;
import cn.edu.sdu.mobilesafe.view.SettingItemView;

public class SettingsActivity extends Activity {
	private SettingItemView siv_update;
	private SharedPreferences sharedPreferences;
	private SettingItemView siv_address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

		initUpdateView();
		initAddressView();
	}

	// ��ʼ���Զ����¿���
	private void initUpdateView() {
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// siv_update.setTitle("�Զ���������");

		boolean autoUpdate = sharedPreferences.getBoolean("auto_update", true);
		if (autoUpdate) {
			siv_update.setChecked(true);
			// siv_update.setDesc("�Զ������ѿ���");
		} else {
			siv_update.setChecked(false);
			// siv_update.setDesc("�Զ������ѹر�");
		}

		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �жϵ�ǰ�Ĺ�ѡ״̬
				if (siv_update.isChecked()) {
					// �رչ�ѡ
					siv_update.setChecked(false);
					// siv_update.setDesc("�Զ������ѹر�");
					// ����sp
					sharedPreferences.edit().putBoolean("auto_update", false)
							.commit();
				} else {
					siv_update.setChecked(true);
					// siv_update.setDesc("�Զ������ѿ���");
					sharedPreferences.edit().putBoolean("auto_update", true)
							.commit();
				}
			}
		});
	}

	// ��ʼ�������ؿ���
	private void initAddressView() {
		siv_address = (SettingItemView) findViewById(R.id.siv_address);

		// ���ݹ����ط����Ƿ�����������checkbox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"cn.edu.sdu.mobilesafe.service.AddressService");
		if (serviceRunning) {
			siv_address.setChecked(true);
		} else {
			siv_address.setChecked(false);
		}

		siv_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_address.isChecked()) {
					siv_address.setChecked(false);
					stopService(new Intent(SettingsActivity.this,
							AddressService.class));// ֹͣ�����ط���
				} else {
					siv_address.setChecked(true);
					startService(new Intent(SettingsActivity.this,
							AddressService.class));// ��ʼ�����ط���
				}
			}
		});
	}
}
