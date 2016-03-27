package cn.edu.sdu.mobilesafe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.fragment.LockFragment;
import cn.edu.sdu.mobilesafe.fragment.UnlockFragment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AppLockActivity extends FragmentActivity implements
		OnClickListener {
	@ViewInject(R.id.tv_tab_lock)
	TextView tv_tab_lock;
	@ViewInject(R.id.tv_tab_unlock)
	TextView tv_tab_unlock;
	@ViewInject(R.id.fl_lock_content)
	FrameLayout fl_lock_content;
	private FragmentManager fragmentManager;
	private UnlockFragment unlockFragment;
	private LockFragment lockFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.activity_app_lock);
		ViewUtils.inject(this);
		tv_tab_lock.setOnClickListener(this);
		tv_tab_unlock.setOnClickListener(this);

		// 获取到Fragment的管理者
		fragmentManager = getSupportFragmentManager();
		unlockFragment = new UnlockFragment();
		lockFragment = new LockFragment();
		// 开启事务
		FragmentTransaction mTransaction = fragmentManager.beginTransaction();
		mTransaction.replace(R.id.fl_lock_content, unlockFragment).commit();
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		switch (v.getId()) {
		case R.id.tv_tab_lock:
			tv_tab_unlock.setBackgroundResource(R.drawable.tab_left_default);
			tv_tab_lock.setBackgroundResource(R.drawable.tab_right_pressed);
			ft.replace(R.id.fl_lock_content, lockFragment);
			break;
		case R.id.tv_tab_unlock:
			tv_tab_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
			tv_tab_lock.setBackgroundResource(R.drawable.tab_right_default);
			ft.replace(R.id.fl_lock_content, unlockFragment);
			break;
		}
		ft.commit();
	}
}
