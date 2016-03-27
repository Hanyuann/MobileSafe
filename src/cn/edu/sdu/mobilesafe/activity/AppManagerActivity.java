package cn.edu.sdu.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.bean.AppInfo;
import cn.edu.sdu.mobilesafe.engine.AppInfoParser;
import cn.edu.sdu.mobilesafe.utils.UIUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AppManagerActivity extends Activity implements
		View.OnClickListener {

	@ViewInject(R.id.lv_software)
	private ListView lv_software;
	@ViewInject(R.id.tv_rom)
	private TextView tv_rom;
	@ViewInject(R.id.tv_sdcard)
	private TextView tv_sdcard;
	@ViewInject(R.id.tv_app_num)
	private TextView tv_app_num;

	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;

	private PopupWindow popupWindow;
	private AppInfo clickAppInfo;

	private AppManagerAdapter adapter;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			adapter = new AppManagerAdapter();
			lv_software.setAdapter(adapter);
		};
	};
	private UninstallReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
		initData();
	}

	private void initData() {
		new Thread() {
			public void run() {
				appInfos = AppInfoParser.getAppInfos(AppManagerActivity.this);
				// appInfos拆成 用户程序的集合 + 系统程序的集合

				// 用户程序的集合
				userAppInfos = new ArrayList<AppInfo>();
				// 系统程序的集合
				systemAppInfos = new ArrayList<AppInfo>();

				for (AppInfo appInfo : appInfos) {
					// 用户程序
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		setContentView(R.layout.activity_app_manager);
		ViewUtils.inject(this);
		// 获取到rom内存运行的剩余空间
		long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
		// 获取到sd卡的剩余空间
		long sd_freeSpace = Environment.getExternalStorageDirectory()
				.getFreeSpace();
		// 格式化大小
		tv_rom.setText("内存可用" + Formatter.formatFileSize(this, rom_freeSpace));
		tv_sdcard.setText("SD卡可用"
				+ Formatter.formatFileSize(this, sd_freeSpace));

		receiver = new UninstallReceiver();
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addDataScheme("package");
		registerReceiver(receiver, intentFilter);

		// 设置listview的滚动监听
		lv_software.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			/**
			 * 
			 * @param view
			 * @param firstVisibleItem
			 *            第一个可见的条的位置
			 * @param visibleItemCount
			 *            一页可以展示多少个条目
			 * @param totalItemCount
			 *            总共的item的个数
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				popupWindowDismiss();

				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > (userAppInfos.size() + 1)) {
						// 系统应用程序
						tv_app_num.setText("系统程序(" + systemAppInfos.size()
								+ ")个");
					} else {
						// 用户应用程序
						tv_app_num.setText("用户程序(" + userAppInfos.size() + ")个");
					}
				}
			}
		});

		lv_software
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 获取到当前点击的item对象
						Object obj = lv_software.getItemAtPosition(position);

						if (obj != null && obj instanceof AppInfo) {
							clickAppInfo = (AppInfo) obj;

							View contentView = View.inflate(
									AppManagerActivity.this,
									R.layout.item_popup, null);
							LinearLayout ll_uninstall = (LinearLayout) contentView
									.findViewById(R.id.ll_uninstall);
							LinearLayout ll_share = (LinearLayout) contentView
									.findViewById(R.id.ll_share);
							LinearLayout ll_start = (LinearLayout) contentView
									.findViewById(R.id.ll_start);

							ll_uninstall
									.setOnClickListener(AppManagerActivity.this);
							ll_share.setOnClickListener(AppManagerActivity.this);
							ll_start.setOnClickListener(AppManagerActivity.this);

							popupWindowDismiss();
							// -2表示包裹内容
							popupWindow = new PopupWindow(contentView, -2, -2);
							// 需要注意：使用PopupWindow 必须设置背景。不然没有动画
							popupWindow
									.setBackgroundDrawable(new ColorDrawable(
											Color.TRANSPARENT));

							int[] location = new int[2];
							// 获取view展示到窗体上面的位置
							view.getLocationInWindow(location);
							popupWindow.showAtLocation(parent, Gravity.LEFT
									+ Gravity.TOP, 70, location[1]);

							ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f,
									0.5f, 1.0f, Animation.RELATIVE_TO_SELF,
									0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
							sa.setDuration(100);

							contentView.startAnimation(sa);
						}
					}
				});
	}

	private void popupWindowDismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	private class AppManagerAdapter extends BaseAdapter {

		private View view;
		private ViewHolder holder;

		@Override
		public int getCount() {
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return null;
			} else if (position == userAppInfos.size() + 1) {
				return null;
			}
			AppInfo appInfo;
			if (position < userAppInfos.size() + 1) {
				// 把多出来的特殊的条目减掉
				appInfo = userAppInfos.get(position - 1);
			} else {
				int location = userAppInfos.size() + 2;
				appInfo = systemAppInfos.get(position - location);
			}
			return appInfo;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 如果当前的position等于0 表示应用程序
			if (position == 0) {
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setText("用户程序(" + userAppInfos.size() + ")");
				return textView;
				// 表示系统程序
			} else if (position == userAppInfos.size() + 1) {
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setText("系统程序(" + systemAppInfos.size() + ")");
				return textView;
			}

			AppInfo appInfo;
			if (position < userAppInfos.size() + 1) {
				// 把多出来的特殊的条目减掉
				appInfo = userAppInfos.get(position - 1);
			} else {
				int location = userAppInfos.size() + 2;
				appInfo = systemAppInfos.get(position - location);
			}

			if (convertView != null && convertView instanceof LinearLayout) {
				view = convertView;
				holder = (ViewHolder) convertView.getTag();
			} else {
				view = View.inflate(AppManagerActivity.this,
						R.layout.list_item_app_manager, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_apk_icon);
				holder.tv_apk_size = (TextView) view
						.findViewById(R.id.tv_apk_size);
				holder.tv_source_dir = (TextView) view
						.findViewById(R.id.tv_source_dir);
				holder.tv_apk_name = (TextView) view
						.findViewById(R.id.tv_apk_name);

				view.setTag(holder);
			}

			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_apk_size.setText(Formatter.formatFileSize(
					AppManagerActivity.this, appInfo.getApkSize()));
			holder.tv_apk_name.setText(appInfo.getApkName());
			if (appInfo.isRom()) {
				holder.tv_source_dir.setText("手机内存");
			} else {
				holder.tv_source_dir.setText("外部存储");
			}
			return view;
		}
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_apk_size;
		TextView tv_source_dir;
		TextView tv_apk_name;
	}

	@Override
	protected void onDestroy() {
		popupWindowDismiss();
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 分享
		case R.id.ll_share:

			Intent share_localIntent = new Intent("android.intent.action.SEND");
			share_localIntent.setType("text/plain");
			share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
			share_localIntent.putExtra("android.intent.extra.TEXT",
					"Hi！推荐您使用软件：" + clickAppInfo.getApkName() + "下载地址:"
							+ "https://play.google.com/store/apps/details?id="
							+ clickAppInfo.getApkPackageName());
			this.startActivity(Intent.createChooser(share_localIntent, "分享"));
			popupWindowDismiss();

			break;

		// 运行
		case R.id.ll_start:

			Intent start_localIntent = this.getPackageManager()
					.getLaunchIntentForPackage

					(clickAppInfo.getApkPackageName());
			this.startActivity(start_localIntent);
			popupWindowDismiss();
			break;
		// 卸载
		case R.id.ll_uninstall:

			Intent uninstall_localIntent = new Intent(
					"android.intent.action.DELETE", Uri.parse("package:" +

					clickAppInfo.getApkPackageName()));

			startActivityForResult(uninstall_localIntent, 0);
			popupWindowDismiss();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
		}else{
			if (clickAppInfo.isUserApp()) {
				userAppInfos.remove(clickAppInfo);
			} else {
				systemAppInfos.remove(clickAppInfo);
			}
		}
		adapter.notifyDataSetChanged();
	}

	private class UninstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			UIUtils.showToast(AppManagerActivity.this, "卸载完成");
		}
	}

}
