package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.service.AddressService;
import cn.edu.sdu.mobilesafe.service.CallSafeService;
import cn.edu.sdu.mobilesafe.utils.ServiceStatusUtils;
import cn.edu.sdu.mobilesafe.view.SettingClickItemView;
import cn.edu.sdu.mobilesafe.view.SettingItemView;

public class SettingsActivity extends Activity {
	private SettingItemView siv_update;
	private SettingItemView siv_address;
	private SettingItemView siv_callsafe;
	private SettingClickItemView sciv_address_style;
	private SettingClickItemView sciv_address_location;

	private SharedPreferences mPrefs;
	private String[] items = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		mPrefs = getSharedPreferences("config", MODE_PRIVATE);

		initUpdateView();
		initAddressView();
		initAddressStyle();
		initAddressLocation();
		initBlackView();
	}

	// 初始化自动更新开关
	private void initUpdateView() {
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// siv_update.setTitle("自动更新设置");

		boolean autoUpdate = mPrefs.getBoolean("auto_update", true);
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
					mPrefs.edit().putBoolean("auto_update", false).commit();
				} else {
					siv_update.setChecked(true);
					// siv_update.setDesc("自动更新已开启");
					mPrefs.edit().putBoolean("auto_update", true).commit();
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

	// 修改提示框的显示风格
	private void initAddressStyle() {
		sciv_address_style = (SettingClickItemView) findViewById(R.id.sciv_address_style);
		sciv_address_style.setTitle("归属地提示框风格");

		final int style = mPrefs.getInt("address_style", 0);
		sciv_address_style.setDesc(items[style]);

		sciv_address_style.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSingleChooseDialog(style);
			}
		});
	}

	protected void showSingleChooseDialog(int style) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("归属地提示框风格");

		builder.setSingleChoiceItems(items, style,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPrefs.edit().putInt("address_style", which).commit();// 保存选择的风格
						dialog.dismiss();

						sciv_address_style.setDesc(items[which]);// 更新描述
					}
				});

		builder.setNegativeButton("取消", null);
		builder.show();
	}

	// 修改归属地显示位置
	private void initAddressLocation() {
		sciv_address_location = (SettingClickItemView) findViewById(R.id.sciv_address_location);
		sciv_address_location.setTitle("归属地提示框显示位置");
		sciv_address_location.setDesc("设置归属地提示框的显示位置");
		sciv_address_location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingsActivity.this,
						DragViewActivity.class));
			}
		});
	}

	// 初始化黑名单
	private void initBlackView() {
		siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);

		// 根据归属地服务是否运行来更新checkbox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"cn.edu.sdu.mobilesafe.service.CallSafeService");

		if (serviceRunning) {
			siv_callsafe.setChecked(true);
		} else {
			siv_callsafe.setChecked(false);
		}

		siv_callsafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsafe.isChecked()) {
					siv_callsafe.setChecked(false);
					stopService(new Intent(SettingsActivity.this,
							CallSafeService.class));// 停止黑名单服务
				} else {
					siv_callsafe.setChecked(true);
					startService(new Intent(SettingsActivity.this,
							CallSafeService.class));// 开启黑名单服务
				}
			}
		});
	}
}
