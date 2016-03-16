package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;

//修改归属地显示位置
public class DragViewActivity extends Activity {
	private ImageView iv_drag;
	private TextView tv_bottom;
	private TextView tv_top;

	private int startX;
	private int startY;

	private SharedPreferences mPrefs;

	long[] mHits = new long[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);
		mPrefs = getSharedPreferences("config", MODE_PRIVATE);

		tv_top = (TextView) findViewById(R.id.tv_top);
		tv_bottom = (TextView) findViewById(R.id.tv_bottom);
		iv_drag = (ImageView) findViewById(R.id.iv_drag);

		int lastX = mPrefs.getInt("lastX", 0);
		int lastY = mPrefs.getInt("lastY", 0);
		// iv_drag.layout(lastX, lastY, lastX + iv_drag.getWidth(), lastY
		// + iv_drag.getHeight());// 不能用这个方法，因为还没有测量完成就不能安放位置

		// 获取屏幕宽高
		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
		final int winHeight = getWindowManager().getDefaultDisplay()
				.getHeight();

		// 避免文本与提示框重叠
		if (lastY > winHeight / 2) {
			tv_top.setVisibility(View.VISIBLE);
			tv_bottom.setVisibility(View.INVISIBLE);
		} else {
			tv_top.setVisibility(View.INVISIBLE);
			tv_bottom.setVisibility(View.VISIBLE);
		}

		RelativeLayout.LayoutParams layoutParams = (LayoutParams) iv_drag
				.getLayoutParams();// 拿到布局参数对象
		layoutParams.leftMargin = lastX;
		layoutParams.topMargin = lastY;
		iv_drag.setLayoutParams(layoutParams);// 重新设置位置

		// 双击居中事件
		iv_drag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// 居中
					iv_drag.layout(winWidth / 2 - iv_drag.getWidth() / 2,
							iv_drag.getTop(), winWidth / 2 + iv_drag.getWidth()
									/ 2, iv_drag.getBottom());
				}
			}
		});

		// 设置触摸监听
		iv_drag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 计算移动偏移量
					int dx = endX - startX;
					int dy = endY - startY;

					// 计算坐标
					int l = iv_drag.getLeft() + dx;
					int r = iv_drag.getRight() + dx;
					int t = iv_drag.getTop() + dy;
					int b = iv_drag.getBottom() + dy;

					// 判断是否超出屏幕边界，注意状态栏的高度
					if (l < 0 || r > winWidth || t < 0 || b > winHeight - 20) {
						break;
					}

					// 避免文本与提示框重叠
					if (t > winHeight / 2) {
						tv_top.setVisibility(View.VISIBLE);
						tv_bottom.setVisibility(View.INVISIBLE);
					} else {
						tv_top.setVisibility(View.INVISIBLE);
						tv_bottom.setVisibility(View.VISIBLE);
					}

					// 更新界面
					iv_drag.layout(l, t, r, b);

					// 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Editor editor = mPrefs.edit();
					editor.putInt("lastX", iv_drag.getLeft());
					editor.putInt("lastY", iv_drag.getTop());
					editor.commit();
					break;

				default:
					break;
				}
				return false;// 事件要往下传递，让双击事件可以相应
			}
		});
	}
}
