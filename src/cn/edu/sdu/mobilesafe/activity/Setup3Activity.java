package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.utils.UIUtils;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		et_phone = (EditText) findViewById(R.id.et_phone);

		String safePhone = mPrefs.getString("safe_phone", "");
		et_phone.setText(safePhone);
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();

		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	@Override
	public void showNextPage() {
		String phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			UIUtils.showToast(this, "安全号码不能为空！");
			return;
		}

		mPrefs.edit().putString("safe_phone", phone).commit();// 保存安全号码

		startActivity(new Intent(this, Setup4Activity.class));
		finish();

		overridePendingTransition(R.anim.anim_in, R.anim.tran_out);
	}

	public void selectContact(View v) {
		Intent intent = new Intent(this, ContactActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String phone = data.getExtras().getString("phone");
			phone = phone.replaceAll("-", "").replaceAll(" ", "");
			et_phone.setText(phone);// 把电话号码设置给输入框
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}