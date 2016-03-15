package cn.edu.sdu.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);

		// 只有在防盗保护开启的前提下才进行SIM卡判断
		boolean protect = sp.getBoolean("protect", false);
		if (protect) {
			String sim = sp.getString("sim", null);
			if (!TextUtils.isEmpty(sim)) {
				// 获取当前手机的SIM卡
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				String currentSim = tm.getSimSerialNumber();// 模拟SIM卡变化测试用 +
															// "111";
				if (sim.equals(currentSim)) {
					// System.out.println("手机安全");
				} else {
					String safePhone = sp.getString("safe_phone", "");
					// 发送短信给安全号码
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(safePhone, null,
							"SIM card changed!", null, null);
				}
			}
		}
	}

}
