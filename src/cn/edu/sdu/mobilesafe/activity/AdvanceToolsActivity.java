package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.edu.sdu.mobilesafe.R;

public class AdvanceToolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	// πÈ Ùµÿ≤È—Ø
	public void numberAddressQuery(View v) {
		startActivity(new Intent(this, AddressActivity.class));
	}
}
