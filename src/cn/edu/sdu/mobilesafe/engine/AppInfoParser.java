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

			// ��ȡ��Ӧ�ó����ͼ��
			Drawable icon = installedPackage.applicationInfo
					.loadIcon(packageManager);
			appInfo.setIcon(icon);
			// Ӧ�ó�������
			String apkName = installedPackage.applicationInfo.loadLabel(
					packageManager).toString();
			appInfo.setApkName(apkName);
			// ��ȡ��Ӧ�ó���İ���
			String packageName = installedPackage.applicationInfo.packageName;
			appInfo.setApkPackageName(packageName);
			// ��ȡ��apk��Դ��·��
			String sourDir = installedPackage.applicationInfo.sourceDir;

			File file = new File(sourDir);
			// apk�ĳ���
			long apkSize = file.length();
			appInfo.setApkSize(apkSize);

			appInfo.setUid(installedPackage.applicationInfo.uid);

			// ��ȡ����װӦ�ó���ı��
			int flags = installedPackage.applicationInfo.flags;
			if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				// ��ʾϵͳApp
				appInfo.setUserApp(false);
			} else {
				// ��ʾ�û�App
				appInfo.setUserApp(true);
			}

			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
				// ��ʾ����SD��
				appInfo.setRom(false);
			} else {
				// ��ʾ�����ֻ��ڴ�
				appInfo.setRom(true);
			}

			packageAppInfos.add(appInfo);
		}
		return packageAppInfos;
	}
}
