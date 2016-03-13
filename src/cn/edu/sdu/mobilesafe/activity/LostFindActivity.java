package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import cn.edu.sdu.mobilesafe.R;

public class LostFindActivity extends Activity {
	private SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = mPrefs.getBoolean("configed", false);// �ж��Ƿ����������򵼣�Ĭ��û��
		if (configed) {
			setContentView(R.layout.activity_lost_find);
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
}
