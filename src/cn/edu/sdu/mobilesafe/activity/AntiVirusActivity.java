package cn.edu.sdu.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.db.dao.VirusDao;
import cn.edu.sdu.mobilesafe.utils.MD5Utils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AntiVirusActivity extends Activity {
	@ViewInject(R.id.iv_scanning)
	ImageView iv_scanning;
	@ViewInject(R.id.tv_init_virus)
	TextView tv_init_virus;
	@ViewInject(R.id.tv_result)
	TextView tv_result;
	@ViewInject(R.id.pb_scanning)
	ProgressBar pb_scanning;
	@ViewInject(R.id.ll_virus_content)
	LinearLayout ll_virus_content;
	@ViewInject(R.id.sv_scan)
	ScrollView sv_scan;

	// ɨ�迪ʼ
	protected static final int BEGINING = 1;
	// ɨ����
	protected static final int SCANNING = 2;
	// ɨ�����
	protected static final int FINISH = 3;

	private int count = 0;
	private int virusNum = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		initData();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BEGINING:
				tv_init_virus.setText("��ʼ���˺�ɱ������");
				break;
			case SCANNING:
				TextView textView = new TextView(AntiVirusActivity.this);
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				if (scanInfo.desc) {
					textView.setTextColor(Color.RED);
					textView.setText(scanInfo.appName + " �ж�");
				} else {
					textView.setTextColor(Color.BLACK);
					textView.setText(scanInfo.appName);
				}

				ll_virus_content.addView(textView);
				sv_scan.post(new Runnable() {
					@Override
					public void run() {
						sv_scan.fullScroll(sv_scan.FOCUS_DOWN);
					}
				});
				break;
			case FINISH:
				// ɨ����ɺ�ֹͣ����
				iv_scanning.clearAnimation();
				break;
			}
			tv_result.setText("�Ѳ�ɱ" + count + "����ֲ���" + virusNum + "��");
		};
	};

	private void initData() {
		new Thread() {
			private Message msg;

			public void run() {
				msg = Message.obtain();
				msg.what = BEGINING;
				handler.sendMessage(msg);
				ScanInfo scanInfo = new ScanInfo();

				PackageManager packageManager = getPackageManager();
				// ��ȡ�����а�װ��Ӧ�ó���
				List<PackageInfo> installedPackages = packageManager
						.getInstalledPackages(0);
				// �����ֻ����氲װ�˶��ٸ�Ӧ�ó���
				int size = installedPackages.size();
				// ���ý��������ֵ
				pb_scanning.setMax(size);
				int progress = 0;

				for (PackageInfo packageInfo : installedPackages) {
					// ��ȡ��Ӧ������
					String appName = packageInfo.applicationInfo.loadLabel(
							packageManager).toString();
					scanInfo.appName = appName;
					String packageName = packageInfo.applicationInfo.packageName;
					scanInfo.packageName = packageName;
					// ������Ҫ��ȡ��ÿ��Ӧ�ó����Ŀ¼
					String sourceDir = packageInfo.applicationInfo.sourceDir;
					String md5 = MD5Utils.getFileMd5(sourceDir);
					// �жϵ�ǰMD5�Ƿ��ڲ������ݿ���
					String desc = VirusDao.checkVirus(md5);
					if (desc == null) {
						scanInfo.desc = false;
					} else {
						scanInfo.desc = true;
						virusNum++;
					}

					count++;
					progress++;
					// SystemClock.sleep(30);
					pb_scanning.setProgress(progress);
					msg = Message.obtain();
					msg.what = SCANNING;
					msg.obj = scanInfo;
					handler.sendMessage(msg);
				}
				msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	static class ScanInfo {
		boolean desc;
		String appName;
		String packageName;
	}

	private void initUI() {
		setContentView(R.layout.activity_anti_virus);
		ViewUtils.inject(this);
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(5000);
		// ����ѭ��
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		iv_scanning.setAnimation(rotateAnimation);
	}
}
