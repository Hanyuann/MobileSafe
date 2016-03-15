package cn.edu.sdu.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.service.LocationService;

public class SmsReceiver extends BroadcastReceiver {
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objects) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
			String incomingNumber = message.getOriginatingAddress();
			String messageBody = message.getMessageBody();
			
			mDPM = (DevicePolicyManager) context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
			mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);// 设备管理组件

			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			String safePhone = sp.getString("safe_phone", "");

			if (incomingNumber.equals(safePhone)) {
				if ("#*alarm*#".equals(messageBody)) {
					// 播放报警音乐,即使调为静音也能播放音乐,因为使用的还是媒体声音的通道,和铃声无关
					MediaPlayer player = MediaPlayer.create(context,
							R.raw.areyouok);
					player.setVolume(1f, 1f);
					player.setLooping(true);
					player.start();
					abortBroadcast();// 中断短信的传递,从而系统app就收不到短信内容了
				} else if ("#*location*#".equals(messageBody)) {
					// 获取经纬度
					context.startService(new Intent(context,
							LocationService.class));// 开启定位服务

					abortBroadcast();
				} else if ("#*wipedata*#".equals(messageBody)) {
					if (mDPM.isAdminActive(mDeviceAdminSample)) {
						mDPM.wipeData(0);// 清除数据，恢复出厂设置
					}
					abortBroadcast();
				} else if ("#*lockscreen*#".equals(messageBody)) {
					if (mDPM.isAdminActive(mDeviceAdminSample)) {
						mDPM.resetPassword("123456", 0);
						mDPM.lockNow();
					}
					abortBroadcast();
				}
			}
		}
	}

}
