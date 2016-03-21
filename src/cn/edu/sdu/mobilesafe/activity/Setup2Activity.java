package cn.edu.sdu.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.utils.UIUtils;
import cn.edu.sdu.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv_sim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		siv_sim = (SettingItemView) findViewById(R.id.siv_sim);

		String sim = mPrefs.getString("sim", null);
		if (!TextUtils.isEmpty(sim)) {
			siv_sim.setChecked(true);
		} else {
			siv_sim.setChecked(false);
		}

		siv_sim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_sim.isChecked()) {
					siv_sim.setChecked(false);
					// 删除已绑定的SIM卡信息
					mPrefs.edit().remove("sim").commit();
				} else {
					siv_sim.setChecked(true);
					// 保存SIM卡信息
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tm.getSimSerialNumber();
					mPrefs.edit().putString("sim", simSerialNumber).commit();
				}
			}
		});
	}

	// 上一页
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();

		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	// 下一页
	public void showNextPage() {
		// 如果SIM卡没有绑定,就不允许进入下一页
		String sim = mPrefs.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			UIUtils.showToast(this, "必须绑定SIM卡！");
			return;
		}
		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		overridePendingTransition(R.anim.anim_in, R.anim.tran_out);
	}
}
