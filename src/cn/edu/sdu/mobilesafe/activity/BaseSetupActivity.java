package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import cn.edu.sdu.mobilesafe.utils.ToastUtils;

//��������ҳ����,����Ҫ���嵥�ļ���ע��,��Ϊ����Ҫ����չʾ
public abstract class BaseSetupActivity extends Activity {
	private GestureDetector mDetector;
	public SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("config", MODE_PRIVATE);

		// �������ƻ����¼�
		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			// e1:������ʼ��;e2:����������;velocityX:X�����ٶ�;velocityY:Y�����ٶ�
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// �ж����򻬶������Ƿ����,����Ļ��������л�����
				if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
					ToastUtils.showToast(BaseSetupActivity.this, "����������Ŷ��");
					return true;
				}
				if (Math.abs(velocityX) < 150) {
					ToastUtils.showToast(BaseSetupActivity.this, "����̫���ˣ�");
					return true;
				}

				// ���һ�,��һҳ
				if (e2.getRawX() - e1.getRawX() > 200) {
					showPreviousPage();
				}
				// ����,��һҳ
				if (e1.getRawX() - e2.getRawX() > 200) {
					showNextPage();
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	// ��һҳ
	public abstract void showPreviousPage();

	// ��һҳ
	public abstract void showNextPage();

	// �������һҳ��ť
	public void next(View v) {
		showNextPage();
	}

	// �������һҳ��ť
	public void previous(View v) {
		showPreviousPage();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);// ί������ʶ�����������¼�
		return super.onTouchEvent(event);
	}
}
