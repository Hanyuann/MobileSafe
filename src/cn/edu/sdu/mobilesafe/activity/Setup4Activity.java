package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import cn.edu.sdu.mobilesafe.R;

public class Setup4Activity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
	}

	// ��һҳ
	public void next(View v) {
		startActivity(new Intent(Setup4Activity.this, LostFindActivity.class));
		finish();

		overridePendingTransition(R.anim.anim_in, R.anim.tran_out);

		SharedPreferences mPrefs = getSharedPreferences("config", MODE_PRIVATE);
		mPrefs.edit().putBoolean("configed", true).commit();// �Ѿ�չʾ�������򵼣��´ν�����չʾ��
	}

	// ��һҳ
	public void previous(View v) {
		startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
		finish();

		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}
}
