package cn.edu.sdu.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import cn.edu.sdu.mobilesafe.R;

public class Setup1Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void showPreviousPage() {
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();

		overridePendingTransition(R.anim.anim_in, R.anim.tran_out);
	}

}
