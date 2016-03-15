package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.db.dao.AddressDao;

public class AddressActivity extends Activity {
	private EditText et_number;
	private TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);

		et_number = (EditText) findViewById(R.id.et_number);
		tv_result = (TextView) findViewById(R.id.tv_result);

		// 监听文本内容的变化
		et_number.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String result = AddressDao.getAddress(s.toString());
				tv_result.setText(result);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public void query(View v) {
		String number = et_number.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			String result = AddressDao.getAddress(number);
			tv_result.setText(result);
		} else {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			// shake.setInterpolator(new Interpolator() {
			//
			// @Override
			// public float getInterpolation(float input) {
			// // y=ax+b
			// int y = 0;
			// return y;
			// }
			// });
			et_number.startAnimation(shake);
			vibrate();
		}
	}

	// 手机震动，需要权限
	public void vibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(2000);
		// 先震动一秒，再等待一秒，再震动一秒，再等待一秒，不重复
		// vibrator.vibrate(new long[]{1000,1000,1000,1000}, -1);
		// 取消震动
		// vibrator.cancel();
	}
}
