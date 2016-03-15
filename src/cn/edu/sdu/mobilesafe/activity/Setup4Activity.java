package cn.edu.sdu.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.edu.sdu.mobilesafe.R;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cb_protect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		cb_protect = (CheckBox) findViewById(R.id.cb_protect);
		boolean protect = mPrefs.getBoolean("protect", false);
		if (protect) {
			cb_protect.setText("防盗保护已经开启！");
			cb_protect.setChecked(true);
		} else {
			cb_protect.setText("防盗保护没有开启！");
			cb_protect.setChecked(false);
		}
		// 当checkbox发生变化时
		cb_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cb_protect.setText("防盗保护已经开启！");
					mPrefs.edit().putBoolean("protect", true).commit();
				} else {
					cb_protect.setText("防盗保护没有开启！");
					mPrefs.edit().putBoolean("protect", false).commit();
				}
			}
		});
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
		finish();

		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(Setup4Activity.this, LostFindActivity.class));
		finish();

		overridePendingTransition(R.anim.anim_in, R.anim.tran_out);

		mPrefs.edit().putBoolean("configed", true).commit();// 已经展示过设置向导，下次进来不展示了
	}
}
