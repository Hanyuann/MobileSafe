package cn.edu.sdu.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.utils.UIUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CleanCacheActivity extends Activity {

	public static final int ADD_CACHE_ITEM = 1;
	public static final int NO_CACHE = 2;

	private PackageManager packageManager;
	private List<CacheInfo> cacheList;

	@ViewInject(R.id.lv_cache)
	ListView lv_cache;
	@ViewInject(R.id.ll_loading)
	LinearLayout ll_loading;
	@ViewInject(R.id.pb_mypb)
	ProgressBar pb_mypb;
	@ViewInject(R.id.tv_info)
	TextView tv_info;

	int appCount;
	boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initUI();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == ADD_CACHE_ITEM) {
				CacheAdapter adapter = new CacheAdapter();
				lv_cache.setAdapter(adapter);
				ll_loading.setVisibility(View.GONE);
			} else if (msg.what == NO_CACHE) {
				pb_mypb.setVisibility(View.GONE);
				tv_info.setText("暂无缓存，系统很干净哟");
			}
		};
	};

	private void initUI() {
		setContentView(R.layout.activity_clean_cache);
		ViewUtils.inject(this);

		packageManager = getPackageManager();
		ll_loading.setVisibility(View.VISIBLE);

		new Thread() {

			public void run() {
				cacheList = new ArrayList<CacheInfo>();
				List<PackageInfo> installedPackages = packageManager
						.getInstalledPackages(0);
				appCount = installedPackages.size();

				for (PackageInfo packageInfo : installedPackages) {
					getCacheSize(packageInfo);
				}
			};
		}.start();
	}

	private void getCacheSize(PackageInfo packageInfo) {
		try {
			appCount--;
			// Class<?> clazz = getClassLoader().loadClass(
			// "android.content.pm.PackageManager");
			// 通过反射获取到当前的方法
			Method method = PackageManager.class.getDeclaredMethod(
					"getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			method.invoke(packageManager,
					packageInfo.applicationInfo.packageName,
					new MyIPackageStatsObserber(packageInfo));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class MyIPackageStatsObserber extends IPackageStatsObserver.Stub {
		private PackageInfo packageInfo;

		public MyIPackageStatsObserber(PackageInfo packageInfo) {
			this.packageInfo = packageInfo;
		}

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cacheSize = pStats.cacheSize;
			Message msg = new Message();
			// 如果缓存大小大于0，说明有缓存
			if (cacheSize > 0) {
				flag = false;
				CacheInfo cacheInfo = new CacheInfo();
				cacheInfo.icon = packageInfo.applicationInfo
						.loadIcon(packageManager);
				cacheInfo.appName = packageInfo.applicationInfo.loadLabel(
						packageManager).toString();
				cacheInfo.cacheSize = cacheSize;
				cacheInfo.packageName = packageInfo.packageName;
				cacheList.add(cacheInfo);
				msg.what = ADD_CACHE_ITEM;
				handler.sendMessage(msg);
			} else if (flag && (appCount == 0)) {
				msg.what = NO_CACHE;
				handler.sendMessageDelayed(msg, 1000);
			}
		}
	}

	static class CacheInfo {
		Drawable icon;
		long cacheSize;
		String appName;
		String packageName;
	}

	private class CacheAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return cacheList.size();
		}

		@Override
		public Object getItem(int position) {
			return cacheList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(CleanCacheActivity.this,
						R.layout.list_item_cache, null);
				holder = new ViewHolder();
				holder.iv_cache_icon = (ImageView) view
						.findViewById(R.id.iv_cache_icon);
				holder.tv_cache_name = (TextView) view
						.findViewById(R.id.tv_cache_name);
				holder.tv_cache_size = (TextView) view
						.findViewById(R.id.tv_cache_size);
				holder.iv_clean_cache = (ImageView) view
						.findViewById(R.id.iv_clean_cache);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.iv_cache_icon.setImageDrawable(cacheList.get(position).icon);
			holder.tv_cache_name.setText(cacheList.get(position).appName);
			holder.tv_cache_size.setText("缓存大小："
					+ Formatter.formatFileSize(CleanCacheActivity.this,
							cacheList.get(position).cacheSize));
			holder.iv_clean_cache.setOnClickListener(new MyOnClickListener(
					position) {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:"
							+ cacheList.get(position).packageName));
					startActivityForResult(intent, 0);
				}
			});

			return view;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		flag = true;
		initUI();
	}

	abstract class MyOnClickListener implements OnClickListener {
		int position;

		public MyOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public abstract void onClick(View v);
	}

	static class ViewHolder {
		ImageView iv_cache_icon;
		ImageView iv_clean_cache;
		TextView tv_cache_name;
		TextView tv_cache_size;
	}

	// 清除全部缓存
	public void clearAll(View v) {
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if (method.getName().equals("freeStorageAndNotify")) {
				try {
					method.invoke(packageManager, Integer.MAX_VALUE,
							new MyIPackageDataObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}
				flag = true;
				initUI();
				UIUtils.showToast(CleanCacheActivity.this, "清除成功");
			}
		}
	}

	private class MyIPackageDataObserver extends IPackageDataObserver.Stub {

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {

		}

	}
}
