package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.view.SettingItemView;

public class SettingsActivity extends Activity {
	private SettingItemView siv_update;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

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
}
