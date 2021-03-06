package cn.edu.sdu.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.bean.Virus;
import cn.edu.sdu.mobilesafe.db.dao.VirusDao;
import cn.edu.sdu.mobilesafe.utils.StreamUtils;
import cn.edu.sdu.mobilesafe.utils.UIUtils;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class SplashActivity extends Activity {

	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROR = 3;
	protected static final int CODE_ENTER_HOME = 4;

	private RelativeLayout rl_root;
	private TextView tv_version;
	private TextView tv_progress;

	private String mVersionName;// 版本名
	private int mVersionCode;// 版本号
	private String mDesc;// 更新描述
	private String mDownloadUrl;// 下载连接

	private SharedPreferences mPref;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case CODE_URL_ERROR:
				UIUtils.showToast(SplashActivity.this, "url错误");
				enterHome();
				break;
			case CODE_NET_ERROR:
				UIUtils.showToast(SplashActivity.this, "网络错误");
				enterHome();
				break;
			case CODE_JSON_ERROR:
				UIUtils.showToast(SplashActivity.this, "数据解析错误");
				enterHome();
				break;
			case CODE_ENTER_HOME:
				enterHome();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// 设置有米广告
		AdManager.getInstance(this).init("b21e6d4d676ad3b3",
				"650dcbb136e035c1", false);

		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText("版本号:" + getVersionName());
		tv_progress = (TextView) findViewById(R.id.tv_progress);

		copyDB("address.db");// 拷贝归属地查询数据库
		copyDB("antivirus.db");// 拷贝病毒数据库
		// 更新病毒数据库
		// updateVirus();

		// 检查更新配置,判断是否需要自动更新
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		boolean autoUpdate = mPref.getBoolean("auto_update", true);
		if (autoUpdate) {
			checkVersion();
		} else {
			// 延时两秒后发送消息
			mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
		}

		// 渐变的动画效果
		AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
		anim.setDuration(2000);
		rl_root.startAnimation(anim);
	}

	// 更新病毒库
	private void updateVirus() {
		final VirusDao dao = new VirusDao();

		// 联网从服务器获取到最新的MD5特征码
		HttpUtils httpUtils = new HttpUtils();
		String url = "http://10.0.2.2:8080/virus.json";
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				try {
					// JSONObject jsonObject = new JSONObject(arg0.result);
					// String md5 = jsonObject.getString("md5");
					// String desc = jsonObject.getString("desc");
					Gson gson = new Gson();
					Virus virus = gson.fromJson(arg0.result, Virus.class);
					dao.addVirus(virus.md5, virus.desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

	private String getVersionName() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);// 获取包的信息
			String versionName = packageInfo.versionName;

			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	private int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);// 获取包的信息
			int versionCode = packageInfo.versionCode;

			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 从服务器获取版本信息进行校验
	private void checkVersion() {
		final long startTime = System.currentTimeMillis();
		new Thread() {
			Message msg = Message.obtain();
			HttpURLConnection conn = null;

			@Override
			public void run() {
				super.run();
				try {
					// 虚拟机加载本机地址10.0.2.2
					URL url = new URL("http://10.0.2.2:8080/update.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);// 设置连接超时
					conn.setReadTimeout(5000);// 设置响应超时，已经连接上了，但是服务器迟迟不给响应
					conn.getContent();

					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						InputStream in = conn.getInputStream();

						String result = StreamUtils.readFromStream(in);

						// 解析json
						JSONObject jo = new JSONObject(result);
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDesc = jo.getString("description");
						mDownloadUrl = jo.getString("downloadUrl");

						if (mVersionCode > getVersionCode()) {
							// 判断是否有更新,若有更新则弹出升级对话框
							msg.what = CODE_UPDATE_DIALOG;
						} else {
							msg.what = CODE_ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					// url错误异常
					msg.what = CODE_URL_ERROR;
				} catch (IOException e) {
					// 网络错误异常
					msg.what = CODE_NET_ERROR;
				} catch (JSONException e) {
					// json解析失败
					msg.what = CODE_JSON_ERROR;
				} finally {
					long endTime = System.currentTimeMillis();
					long timeUsed = endTime - startTime;// 访问网络花费的时间
					if (timeUsed < 2000) {
						// 强制休眠一段时间，保证闪屏页2秒钟
						try {
							sleep(2000 - timeUsed);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					mHandler.sendMessage(msg);
					if (conn != null) {
						conn.disconnect();
					}
				}
			}
		}.start();
	}

	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("最新版本：" + mVersionName);
		builder.setMessage(mDesc);
		// builder.setCancelable(false);// 不让用户取消对话框，用户体验太差，尽量不要用
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				download();
			}
		});

		builder.setNegativeButton("以后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		});

		builder.setOnCancelListener(new OnCancelListener() {
			// 设置取消的监听，用户点击返回键触发
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		builder.show();
	}

	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	// 拷贝数据库
	public void copyDB(String dbName) {
		File destFile = new File(getFilesDir(), dbName);

		if (destFile.exists()) {

			return;
		}

		FileOutputStream out = null;
		InputStream in = null;
		try {
			in = getAssets().open(dbName);
			out = new FileOutputStream(destFile);

			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	protected void download() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			tv_progress.setVisibility(View.VISIBLE);

			String target = Environment.getExternalStorageDirectory()
					+ "/update.apk";
			// XUtils
			HttpUtils utils = new HttpUtils();
			utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

				// 下载进度
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					tv_progress.setText("下载进度：" + current * 100 / total + "%");
				}

				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					// 跳转到系统安装界面
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					// startActivity(intent);
					startActivityForResult(intent, 0);// 如果用户取消安装会返回结果
				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					UIUtils.showToast(SplashActivity.this, "下载失败！");
				}
			});
		} else {
			UIUtils.showToast(SplashActivity.this, "没有找到SDcard！");
		}
	}

	// 用户安装更新时取消调用
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}
}