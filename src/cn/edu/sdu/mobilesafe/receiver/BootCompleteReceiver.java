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

		// ֻ���ڷ�������������ǰ���²Ž���SIM���ж�
		boolean protect = sp.getBoolean("protect", false);
		if (protect) {
			String sim = sp.getString("sim", null);
			if (!TextUtils.isEmpty(sim)) {
				// ��ȡ��ǰ�ֻ���SIM��
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				String currentSim = tm.getSimSerialNumber();// ģ��SIM���仯������ +
															// "111";
				if (sim.equals(currentSim)) {
					// System.out.println("�ֻ���ȫ");
				} else {
					String safePhone = sp.getString("safe_phone", "");
					// ���Ͷ��Ÿ���ȫ����
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(safePhone, null,
							"SIM card changed!", null, null);
				}
			}
		}
	}

}
