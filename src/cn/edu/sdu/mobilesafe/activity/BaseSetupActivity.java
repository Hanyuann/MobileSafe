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

//设置引导页父类,不需要在清单文件中注册,因为不需要界面展示
public abstract class BaseSetupActivity extends Activity {
	private GestureDetector mDetector;
	public SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("config", MODE_PRIVATE);

		// 监听手势滑动事件
		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			// e1:滑动起始点;e2:滑动结束点;velocityX:X方向速度;velocityY:Y方向速度
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// 判断纵向滑动幅度是否过大,过大的话不允许切换界面
				if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
					ToastUtils.showToast(BaseSetupActivity.this, "不能这样滑哦！");
					return true;
				}
				if (Math.abs(velocityX) < 150) {
					ToastUtils.showToast(BaseSetupActivity.this, "滑的太慢了！");
					return true;
				}

				// 向右滑,上一页
				if (e2.getRawX() - e1.getRawX() > 200) {
					showPreviousPage();
				}
				// 向左滑,下一页
				if (e1.getRawX() - e2.getRawX() > 200) {
					showNextPage();
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	// 上一页
	public abstract void showPreviousPage();

	// 下一页
	public abstract void showNextPage();

	// 鼠标点击下一页按钮
	public void next(View v) {
		showNextPage();
	}

	// 鼠标点击上一页按钮
	public void previous(View v) {
		showPreviousPage();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);// 委托手势识别器处理触摸事件
		return super.onTouchEvent(event);
	}
}
