package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.service.AddressService;
import cn.edu.sdu.mobilesafe.service.CallSafeService;
import cn.edu.sdu.mobilesafe.service.WatchDogService;
import cn.edu.sdu.mobilesafe.utils.MD5Utils;
import cn.edu.sdu.mobilesafe.utils.ServiceStatusUtils;
import cn.edu.sdu.mobilesafe.utils.UIUtils;
import cn.edu.sdu.mobilesafe.view.SettingClickItemView;
import cn.edu.sdu.mobilesafe.view.SettingItemView;

public class SettingsActivity extends Activity {

	private SettingItemView siv_update;
	private SettingItemView siv_address;
	private SettingItemView siv_callsafe;
	private SettingItemView siv_watch_dog;
	private SettingClickItemView sciv_address_style;
	private SettingClickItemView sciv_address_location;

	private SharedPreferences mPrefs;
	private String[] items = new String[] { "��͸��", "������", "��ʿ��", "������", "ƻ����" };;

	private Intent watchDogIntent;

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
		initWatchDog();
	}

	// ��ʼ���Զ����¿���
	private void initUpdateView() {
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// siv_update.setTitle("�Զ���������");

		boolean autoUpdate = mPrefs.getBoolean("auto_update", true);
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
					mPrefs.edit().putBoolean("auto_update", false).commit();
				} else {
					siv_update.setChecked(true);
					// siv_update.setDesc("�Զ������ѿ���");
					mPrefs.edit().putBoolean("auto_update", true).commit();
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

	// �޸���ʾ�����ʾ���
	private void initAddressStyle() {
		sciv_address_style = (SettingClickItemView) findViewById(R.id.sciv_address_style);
		sciv_address_style.setTitle("��������ʾ����");

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
		builder.setTitle("��������ʾ����");

		builder.setSingleChoiceItems(items, style,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPrefs.edit().putInt("address_style", which).commit();// ����ѡ��ķ��
						dialog.dismiss();

						sciv_address_style.setDesc(items[which]);// ��������
					}
				});

		builder.setNegativeButton("ȡ��", null);
		builder.show();
	}

	// �޸Ĺ�������ʾλ��
	private void initAddressLocation() {
		sciv_address_location = (SettingClickItemView) findViewById(R.id.sciv_address_location);
		sciv_address_location.setTitle("��������ʾ����ʾλ��");
		sciv_address_location.setDesc("���ù�������ʾ�����ʾλ��");
		sciv_address_location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingsActivity.this,
						DragViewActivity.class));
			}
		});
	}

	// ��ʼ��������
	private void initBlackView() {
		siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);

		// ���ݹ����ط����Ƿ�����������checkbox
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
							CallSafeService.class));// ֹͣ����������
				} else {
					siv_callsafe.setChecked(true);
					startService(new Intent(SettingsActivity.this,
							CallSafeService.class));// ��������������
				}
			}
		});
	}

	// ��ʼ�����Ź�
	private void initWatchDog() {
		siv_watch_dog = (SettingItemView) findViewById(R.id.siv_watch_dog);
		// ���ݹ����ط����Ƿ�����������checkbox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"cn.edu.sdu.mobilesafe.service.WatchDogService");

		if (serviceRunning) {
			siv_watch_dog.setChecked(true);
		} else {
			siv_watch_dog.setChecked(false);
		}

		watchDogIntent = new Intent(this, WatchDogService.class);
		siv_watch_dog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_watch_dog.isChecked()) {
					siv_watch_dog.setChecked(false);
					// ֹͣ����������
					stopService(watchDogIntent);
				} else {
					String lockNum = mPrefs.getString("locknum", null);
					if (TextUtils.isEmpty(lockNum)) {
						showPasswordSetDialog();
					} else {
						siv_watch_dog.setChecked(true);
						startService(watchDogIntent);
					}
				}
			}
		});
	}

	// ��������ĵ���
	private void showPasswordSetDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_set_password, null);
		// dialog.setView(view);// ���Զ���Ĳ����ļ����ø�Dialog
		dialog.setView(view, 0, 0, 0, 0);// ���ñ߾�Ϊ0����֤��2.x�İ汾������û����

		final EditText et_password = (EditText) view
				.findViewById(R.id.et_password);
		final EditText et_password_confirm = (EditText) view
				.findViewById(R.id.et_password_confirm);

		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString();
				String password_confirm = et_password_confirm.getText()
						.toString();

				if (!TextUtils.isEmpty(password)
						&& !TextUtils.isEmpty(password_confirm)) {
					if (password.equals(password_confirm)) {
						mPrefs.edit()
								.putString("locknum", MD5Utils.encode(password))
								.commit();
						siv_watch_dog.setChecked(true);
						startService(watchDogIntent);
						dialog.dismiss();
					} else {
						UIUtils.showToast(SettingsActivity.this, "�������벻һ��");
					}
				} else {
					UIUtils.showToast(SettingsActivity.this, "�������Ϊ��");
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				siv_watch_dog.setChecked(false);
				dialog.dismiss();
			}
		});

		dialog.show();
	}

}
