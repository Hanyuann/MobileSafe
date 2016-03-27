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
				// appInfos��� �û�����ļ��� + ϵͳ����ļ���

				// �û�����ļ���
				userAppInfos = new ArrayList<AppInfo>();
				// ϵͳ����ļ���
				systemAppInfos = new ArrayList<AppInfo>();

				for (AppInfo appInfo : appInfos) {
					// �û�����
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
		// ��ȡ��rom�ڴ����е�ʣ��ռ�
		long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
		// ��ȡ��sd����ʣ��ռ�
		long sd_freeSpace = Environment.getExternalStorageDirectory()
				.getFreeSpace();
		// ��ʽ����С
		tv_rom.setText("�ڴ����" + Formatter.formatFileSize(this, rom_freeSpace));
		tv_sdcard.setText("SD������"
				+ Formatter.formatFileSize(this, sd_freeSpace));

		receiver = new UninstallReceiver();
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addDataScheme("package");
		registerReceiver(receiver, intentFilter);

		// ����listview�Ĺ�������
		lv_software.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			/**
			 * 
			 * @param view
			 * @param firstVisibleItem
			 *            ��һ���ɼ�������λ��
			 * @param visibleItemCount
			 *            һҳ����չʾ���ٸ���Ŀ
			 * @param totalItemCount
			 *            �ܹ���item�ĸ���
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				popupWindowDismiss();

				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > (userAppInfos.size() + 1)) {
						// ϵͳӦ�ó���
						tv_app_num.setText("ϵͳ����(" + systemAppInfos.size()
								+ ")��");
					} else {
						// �û�Ӧ�ó���
						tv_app_num.setText("�û�����(" + userAppInfos.size() + ")��");
					}
				}
			}
		});

		lv_software
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// ��ȡ����ǰ�����item����
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
							// -2��ʾ��������
							popupWindow = new PopupWindow(contentView, -2, -2);
							// ��Ҫע�⣺ʹ��PopupWindow �������ñ�������Ȼû�ж���
							popupWindow
									.setBackgroundDrawable(new ColorDrawable(
											Color.TRANSPARENT));

							int[] location = new int[2];
							// ��ȡviewչʾ�����������λ��
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
				// �Ѷ�������������Ŀ����
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
			// �����ǰ��position����0 ��ʾӦ�ó���
			if (position == 0) {
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setText("�û�����(" + userAppInfos.size() + ")");
				return textView;
				// ��ʾϵͳ����
			} else if (position == userAppInfos.size() + 1) {
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setText("ϵͳ����(" + systemAppInfos.size() + ")");
				return textView;
			}

			AppInfo appInfo;
			if (position < userAppInfos.size() + 1) {
				// �Ѷ�������������Ŀ����
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
				holder.tv_source_dir.setText("�ֻ��ڴ�");
			} else {
				holder.tv_source_dir.setText("�ⲿ�洢");
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
		// ����
		case R.id.ll_share:

			Intent share_localIntent = new Intent("android.intent.action.SEND");
			share_localIntent.setType("text/plain");
			share_localIntent.putExtra("android.intent.extra.SUBJECT", "f����");
			share_localIntent.putExtra("android.intent.extra.TEXT",
					"Hi���Ƽ���ʹ�������" + clickAppInfo.getApkName() + "���ص�ַ:"
							+ "https://play.google.com/store/apps/details?id="
							+ clickAppInfo.getApkPackageName());
			this.startActivity(Intent.createChooser(share_localIntent, "����"));
			popupWindowDismiss();

			break;

		// ����
		case R.id.ll_start:

			Intent start_localIntent = this.getPackageManager()
					.getLaunchIntentForPackage

					(clickAppInfo.getApkPackageName());
			this.startActivity(start_localIntent);
			popupWindowDismiss();
			break;
		// ж��
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
			UIUtils.showToast(AppManagerActivity.this, "ж�����");
		}
	}

}
