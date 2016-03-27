package cn.edu.sdu.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	// md5加密
	public static String encode(String password) {
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");
			byte[] digest = instance.digest(password.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);

				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	// 获取到文件的MD5值
	public static String getFileMd5(String sourceDir) {
		File file = new File(sourceDir);
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			MessageDigest messageDigest = MessageDigest.getInstance("md5");
			while ((len = fis.read(buffer)) != -1) {
				messageDigest.update(buffer, 0, len);
			}
			byte[] result = messageDigest.digest();

			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);

				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				sb.append(hexString);
			}
			// System.out.println(sourceDir + "---" + sb.toString());
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
