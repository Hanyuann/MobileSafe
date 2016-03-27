package cn.edu.sdu.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.bean.AppInfo;
import cn.edu.sdu.mobilesafe.engine.AppInfoParser;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class TrafficManagerActivity extends Activity {
	@ViewInject(R.id.tv_totalRx)
	private TextView tv_totalRx;
	@ViewInject(R.id.tv_totalTx)
	private TextView tv_totalTx;
	@ViewInject(R.id.lv_traffic)
	private ListView lv_traffic;

	private List<AppInfo> appInfos = new ArrayList<AppInfo>();
	private TrafficManagerAdapter adapter;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			adapter = new TrafficManagerAdapter();
			lv_traffic.setAdapter(adapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		initData();
	}

	private void initData() {
		new Thread() {
			public void run() {
				appInfos = AppInfoParser
						.getAppInfos(TrafficManagerActivity.this);
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		setContentView(R.layout.activity_traffic_manager);
		ViewUtils.inject(this);

		// 获取到手机的总下载流量
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		// 获取到手机的总上传流量
		long totalTxBytes = TrafficStats.getTotalTxBytes();
		tv_totalRx.setText("已下载流量:"
				+ Formatter.formatFileSize(this, totalRxBytes));
		tv_totalTx.setText("已上传流量:"
				+ Formatter.formatFileSize(this, totalTxBytes));
	}

	private class TrafficManagerAdapter extends BaseAdapter {

		private View view;
		private ViewHolder holder;

		@Override
		public int getCount() {
			return appInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				view = View.inflate(TrafficManagerActivity.this,
						R.layout.list_item_traffic_manager, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_apk_icon);
				holder.tv_apk_name = (TextView) view
						.findViewById(R.id.tv_apk_name);
				holder.tv_tx = (TextView) view.findViewById(R.id.tv_tx);
				holder.tv_rx = (TextView) view.findViewById(R.id.tv_rx);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) convertView.getTag();
			}

			AppInfo appInfo = appInfos.get(position);
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_apk_name.setText(appInfo.getApkName());
			holder.tv_tx.setText("已上传流量:"
					+ Formatter.formatFileSize(TrafficManagerActivity.this,
							TrafficStats.getUidTxBytes(appInfo.getUid())));
			holder.tv_rx.setText("已下载流量:"
					+ Formatter.formatFileSize(TrafficManagerActivity.this,
							TrafficStats.getUidRxBytes(appInfo.getUid())));
			return view;
		}
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_apk_name;
		TextView tv_tx;
		TextView tv_rx;
	}
}
