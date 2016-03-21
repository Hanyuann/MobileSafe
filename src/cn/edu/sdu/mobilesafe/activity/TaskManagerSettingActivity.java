package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.service.KillProcessService;
import cn.edu.sdu.mobilesafe.utils.SharedPreferencesUtils;
import cn.edu.sdu.mobilesafe.utils.SystemInfoUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class TaskManagerSettingActivity extends Activity {
	@ViewInject(R.id.cb_show_system_process)
	private CheckBox cb_show_system_process;
	@ViewInject(R.id.cb_kill_process_intime)
	private CheckBox cb_kill_process_intime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
	}

	private void initUI() {
		setContentView(R.layout.activity_task_manager_setting);
		ViewUtils.inject(this);

		cb_show_system_process.setChecked(SharedPreferencesUtils.getBoolean(
				this, "show_system_process", true));
		cb_show_system_process
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPreferencesUtils.saveBoolean(
								TaskManagerSettingActivity.this,
								"show_system_process", isChecked);
					}
				});

		// 定时清理进程
		final Intent intent = new Intent(this, KillProcessService.class);

		cb_kill_process_intime
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							startService(intent);
						} else {
							stopService(intent);
						}
					}
				});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (SystemInfoUtils.isServiceRunning(TaskManagerSettingActivity.this,
				"cn.edu.sdu.mobilesafe.service.KillProcessService")) {
			cb_kill_process_intime.setChecked(true);
		} else {
			cb_kill_process_intime.setChecked(false);
		}
	}
}
