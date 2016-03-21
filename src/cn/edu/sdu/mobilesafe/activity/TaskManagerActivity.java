package cn.edu.sdu.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.bean.TaskInfo;
import cn.edu.sdu.mobilesafe.engine.TaskInfoParser;
import cn.edu.sdu.mobilesafe.utils.SharedPreferencesUtils;
import cn.edu.sdu.mobilesafe.utils.SystemInfoUtils;
import cn.edu.sdu.mobilesafe.utils.UIUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class TaskManagerActivity extends Activity {
	@ViewInject(R.id.tv_task_count)
	TextView tv_task_count;
	@ViewInject(R.id.tv_task_memory)
	TextView tv_task_memory;
	@ViewInject(R.id.lv_task)
	ListView lv_task;
	@ViewInject(R.id.tv_task_num)
	TextView tv_task_num;

	private List<TaskInfo> taskInfos;
	private ArrayList<TaskInfo> userTaskInfos;
	private ArrayList<TaskInfo> systemTaskInfos;

	private TaskManagerAdapter adapter;

	private int totalTaskNum;
	private long availMem;
	private long totalMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
		initData();
	}

	private void initData() {
		new Thread() {
			public void run() {
				taskInfos = TaskInfoParser
						.getTaskInfos(TaskManagerActivity.this);
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.isUserProcess()) {
						userTaskInfos.add(taskInfo);
					} else {
						systemTaskInfos.add(taskInfo);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						adapter = new TaskManagerAdapter();
						lv_task.setAdapter(adapter);
					}
				});
			};
		}.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	private class TaskManagerAdapter extends BaseAdapter {
		private View view;
		private ViewHolder holder;

		@Override
		public int getCount() {
			boolean result = SharedPreferencesUtils.getBoolean(
					TaskManagerActivity.this, "show_system_process", true);
			if (result) {
				return userTaskInfos.size() + systemTaskInfos.size() + 2;
			} else {
				return userTaskInfos.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return null;
			} else if (position == userTaskInfos.size() + 1) {
				return null;
			}
			TaskInfo taskInfo;
			if (position < userTaskInfos.size() + 1) {
				// 把多出来的特殊的条目减掉
				taskInfo = userTaskInfos.get(position - 1);
			} else {
				int location = userTaskInfos.size() + 2;
				taskInfo = systemTaskInfos.get(position - location);
			}
			return taskInfo;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 如果当前的position等于0 表示应用程序
			if (position == 0) {
				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setText("用户程序(" + userTaskInfos.size() + ")个");
				return textView;
				// 表示系统程序
			} else if (position == userTaskInfos.size() + 1) {
				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setText("系统程序(" + systemTaskInfos.size() + ")个");
				return textView;
			}

			TaskInfo taskInfo;
			if (position < userTaskInfos.size() + 1) {
				// 把多出来的特殊的条目减掉
				taskInfo = userTaskInfos.get(position - 1);
			} else {
				int location = userTaskInfos.size() + 2;
				taskInfo = systemTaskInfos.get(position - location);
			}
			if (convertView != null && convertView instanceof LinearLayout) {
				view = convertView;
				holder = (ViewHolder) convertView.getTag();
			} else {
				view = View.inflate(TaskManagerActivity.this,
						R.layout.list_item_task_manager, null);
				holder = new ViewHolder();
				holder.iv_process_icon = (ImageView) view
						.findViewById(R.id.iv_process_icon);
				holder.tv_process_name = (TextView) view
						.findViewById(R.id.tv_process_name);
				holder.tv_process_memory_size = (TextView) view
						.findViewById(R.id.tv_process_memory_size);
				holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
				view.setTag(holder);
			}

			holder.iv_process_icon.setImageDrawable(taskInfo.getIcon());
			holder.tv_process_name.setText(taskInfo.getAppName());
			holder.tv_process_memory_size.setText("内存占用："
					+ Formatter.formatFileSize(TaskManagerActivity.this,
							taskInfo.getMemorySize()));
			if (taskInfo.getPackageName().equals(getPackageName())) {
				holder.cb_status.setVisibility(View.INVISIBLE);
			} else if (taskInfo.isChecked()) {
				holder.cb_status.setChecked(true);
			} else {
				holder.cb_status.setChecked(false);
			}

			return view;
		}
	}

	static class ViewHolder {
		ImageView iv_process_icon;
		TextView tv_process_name;
		TextView tv_process_memory_size;
		CheckBox cb_status;
	}

	private void initUI() {
		setContentView(R.layout.activity_task_manager);
		ViewUtils.inject(this);

		totalTaskNum = SystemInfoUtils.getProcessCount(this);
		tv_task_count.setText("运行中进程:" + totalTaskNum + "个");

		availMem = SystemInfoUtils.getAvailMem(this);
		totalMem = SystemInfoUtils.getTotalMem();

		tv_task_memory.setText("内存:"
				+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)
				+ "/"
				+ Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

		// 设置listview的滚动监听
		lv_task.setOnScrollListener(new AbsListView.OnScrollListener() {
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
				if (userTaskInfos != null && systemTaskInfos != null) {
					if (firstVisibleItem > (userTaskInfos.size() + 1)) {
						// 系统应用程序
						tv_task_num.setText("系统进程(" + systemTaskInfos.size()
								+ ")个");
					} else {
						// 用户应用程序
						tv_task_num.setText("用户进程(" + userTaskInfos.size()
								+ ")个");
					}
				}
			}
		});

		lv_task.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object object = lv_task.getItemAtPosition(position);
				if (object != null && object instanceof TaskInfo) {
					TaskInfo taskInfo = (TaskInfo) object;

					ViewHolder holder = (ViewHolder) view.getTag();
					if (taskInfo.getPackageName().equals(getPackageName())) {
						return;
					} else if (taskInfo.isChecked()) {
						taskInfo.setChecked(false);
						holder.cb_status.setChecked(false);
					} else {
						taskInfo.setChecked(true);
						holder.cb_status.setChecked(true);
					}
				}
			}
		});
	}

	public void selectAll(View v) {
		for (TaskInfo taskInfo : userTaskInfos) {
			// 判断当前的用户程序是不是自己
			if (taskInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			taskInfo.setChecked(true);
		}
		for (TaskInfo taskInfo : systemTaskInfos) {
			taskInfo.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	public void selectOppsite(View v) {
		for (TaskInfo taskInfo : userTaskInfos) {
			if (taskInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			taskInfo.setChecked(!taskInfo.isChecked());
		}
		for (TaskInfo taskInfo : systemTaskInfos) {
			taskInfo.setChecked(!taskInfo.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	public void kill(View v) {
		// 得到进程管理器
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// 将要被清理的进程的集合
		List<TaskInfo> killList = new ArrayList<TaskInfo>();

		int totalKilledNum = 0;
		long totalKilledMem = 0;
		for (TaskInfo taskInfo : userTaskInfos) {
			if (taskInfo.isChecked()) {
				killList.add(taskInfo);
				totalKilledNum++;
				totalKilledMem += taskInfo.getMemorySize();
			}
		}
		for (TaskInfo taskInfo : systemTaskInfos) {
			if (taskInfo.isChecked()) {
				killList.add(taskInfo);
				totalKilledNum++;
				totalKilledMem += taskInfo.getMemorySize();
			}
		}
		for (TaskInfo taskInfo : killList) {
			if (taskInfo.isUserProcess()) {
				userTaskInfos.remove(taskInfo);
				activityManager.killBackgroundProcesses(taskInfo
						.getPackageName());
			} else {
				systemTaskInfos.remove(taskInfo);
				activityManager.killBackgroundProcesses(taskInfo
						.getPackageName());
			}
		}

		UIUtils.showToast(this, "共清理了" + totalKilledNum + "个进程，释放内存"
				+ Formatter.formatFileSize(this, totalKilledMem));

		totalTaskNum -= totalKilledNum;
		availMem += totalKilledMem;
		tv_task_count.setText("运行中进程:" + totalTaskNum + "个");
		tv_task_memory.setText("内存:" + Formatter.formatFileSize(this, availMem)
				+ "/" + Formatter.formatFileSize(this, totalMem));
		adapter.notifyDataSetChanged();
	}

	public void setting(View v) {
		Intent intent = new Intent(this, TaskManagerSettingActivity.class);
		startActivity(intent);
	}

}
