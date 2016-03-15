package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.receiver.AdminReceiver;

public class LostFindActivity extends Activity {
	private SharedPreferences mPrefs;
	private TextView tv_safe_phone;
	private ImageView iv_protect;
	private Button btn_active;

	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = mPrefs.getBoolean("configed", false);// 判断是否进入过设置向导，默认没有
		if (configed) {
			setContentView(R.layout.activity_lost_find);
			// 根据sp更新安全号码
			tv_safe_phone = (TextView) findViewById(R.id.tv_safe_phone);
			String safePhone = mPrefs.getString("safe_phone", "");
			tv_safe_phone.setText(safePhone);
			// 根据sp更新保护锁
			iv_protect = (ImageView) findViewById(R.id.iv_protect);
			boolean protect = mPrefs.getBoolean("protect", false);
			btn_active = (Button) findViewById(R.id.btn_active);

			if (protect) {
				iv_protect.setImageResource(R.drawable.lock);
			} else {
				iv_protect.setImageResource(R.drawable.unlock);
			}

			mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);// 设备管理组件
			if (mDPM.isAdminActive(mDeviceAdminSample)) {
				btn_active.setText("设置已生效");
				btn_active.setClickable(false);
			}
		} else {
			// 跳转设置向导页
			startActivity(new Intent(this, Setup1Activity.class));
			finish();
		}

	}

	// 重新进入向导
	public void reEnter(View v) {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}

	public void active(View v) {
		if (!mDPM.isAdminActive(mDeviceAdminSample)) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					mDeviceAdminSample);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"哈哈哈 ，我们有了超级设备管理器，好牛逼！");
			startActivityForResult(intent, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			btn_active.setText("设置已生效");
			btn_active.setClickable(false);
		}
	}
}
