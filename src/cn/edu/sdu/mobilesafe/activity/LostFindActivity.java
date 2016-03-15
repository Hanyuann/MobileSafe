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
		boolean configed = mPrefs.getBoolean("configed", false);// �ж��Ƿ����������򵼣�Ĭ��û��
		if (configed) {
			setContentView(R.layout.activity_lost_find);
			// ����sp���°�ȫ����
			tv_safe_phone = (TextView) findViewById(R.id.tv_safe_phone);
			String safePhone = mPrefs.getString("safe_phone", "");
			tv_safe_phone.setText(safePhone);
			// ����sp���±�����
			iv_protect = (ImageView) findViewById(R.id.iv_protect);
			boolean protect = mPrefs.getBoolean("protect", false);
			btn_active = (Button) findViewById(R.id.btn_active);

			if (protect) {
				iv_protect.setImageResource(R.drawable.lock);
			} else {
				iv_protect.setImageResource(R.drawable.unlock);
			}

			mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);// �豸�������
			if (mDPM.isAdminActive(mDeviceAdminSample)) {
				btn_active.setText("��������Ч");
				btn_active.setClickable(false);
			}
		} else {
			// ��ת������ҳ
			startActivity(new Intent(this, Setup1Activity.class));
			finish();
		}

	}

	// ���½�����
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
					"������ ���������˳����豸����������ţ�ƣ�");
			startActivityForResult(intent, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			btn_active.setText("��������Ч");
			btn_active.setClickable(false);
		}
	}
}
