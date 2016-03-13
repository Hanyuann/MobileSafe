package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.edu.sdu.mobilesafe.R;

public class Setup2Activity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
	}

	// 下一页
	public void next(View v) {
		startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
		finish();

		overridePendingTransition(R.anim.anim_in, R.anim.tran_out);
	}

	// 上一页
	public void previous(View v) {
		startActivity(new Intent(Setup2Activity.this, Setup1Activity.class));
		finish();

		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}
}
