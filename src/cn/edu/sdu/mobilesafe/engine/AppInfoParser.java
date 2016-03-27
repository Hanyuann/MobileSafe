package cn.edu.sdu.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import cn.edu.sdu.mobilesafe.bean.AppInfo;

public class AppInfoParser {
	public static List<AppInfo> getAppInfos(Context context) {

		ArrayList<AppInfo> packageAppInfos = new ArrayList<AppInfo>();

		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> installedPackages = packageManager
				.getInstalledPackages(0);

		for (PackageInfo installedPackage : installedPackages) {
			AppInfo appInfo = new AppInfo();

			// 获取到应用程序的图标
			Drawable icon = installedPackage.applicationInfo
					.loadIcon(packageManager);
			appInfo.setIcon(icon);
			// 应用程序名字
			String apkName = installedPackage.applicationInfo.loadLabel(
					packageManager).toString();
			appInfo.setApkName(apkName);
			// 获取到应用程序的包名
			String packageName = installedPackage.applicationInfo.packageName;
			appInfo.setApkPackageName(packageName);
			// 获取到apk资源的路径
			String sourDir = installedPackage.applicationInfo.sourceDir;

			File file = new File(sourDir);
			// apk的长度
			long apkSize = file.length();
			appInfo.setApkSize(apkSize);

			appInfo.setUid(installedPackage.applicationInfo.uid);

			// 获取到安装应用程序的标记
			int flags = installedPackage.applicationInfo.flags;
			if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				// 表示系统App
				appInfo.setUserApp(false);
			} else {
				// 表示用户App
				appInfo.setUserApp(true);
			}

			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
				// 表示存在SD卡
				appInfo.setRom(false);
			} else {
				// 表示存在手机内存
				appInfo.setRom(true);
			}

			packageAppInfos.add(appInfo);
		}
		return packageAppInfos;
	}
}
