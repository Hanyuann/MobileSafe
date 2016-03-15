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

	// 初始化自动更新开关
	private void initUpdateView() {
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// siv_update.setTitle("自动更新设置");

		boolean autoUpdate = sharedPreferences.getBoolean("auto_update", true);
		if (autoUpdate) {
			siv_update.setChecked(true);
			// siv_update.setDesc("自动更新已开启");
		} else {
			siv_update.setChecked(false);
			// siv_update.setDesc("自动更新已关闭");
		}

		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断当前的勾选状态
				if (siv_update.isChecked()) {
					// 关闭勾选
					siv_update.setChecked(false);
					// siv_update.setDesc("自动更新已关闭");
					// 更新sp
					sharedPreferences.edit().putBoolean("auto_update", false)
							.commit();
				} else {
					siv_update.setChecked(true);
					// siv_update.setDesc("自动更新已开启");
					sharedPreferences.edit().putBoolean("auto_update", true)
							.commit();
				}
			}
		});
	}

	// 初始化归属地开关
	private void initAddressView() {
		siv_address = (SettingItemView) findViewById(R.id.siv_address);

		// 根据归属地服务是否运行来更新checkbox
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
							AddressService.class));// 停止归属地服务
				} else {
					siv_address.setChecked(true);
					startService(new Intent(SettingsActivity.this,
							AddressService.class));// 开始归属地服务
				}
			}
		});
	}
}
