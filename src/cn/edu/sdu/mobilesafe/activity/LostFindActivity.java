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
		boolean configed = mPrefs.getBoolean("configed", false);// 判断是否进入过设置向导，默认没有
		if (configed) {
			setContentView(R.layout.activity_lost_find);
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
}
