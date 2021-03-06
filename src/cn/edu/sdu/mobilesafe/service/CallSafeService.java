package cn.edu.sdu.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import cn.edu.sdu.mobilesafe.db.dao.BlackNumberDao;

import com.android.internal.telephony.ITelephony;

public class CallSafeService extends Service {

	private BlackNumberDao dao;
	private InnerReceiver innerReceiver;

	public CallSafeService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		super.onCreate();

		dao = new BlackNumberDao(this);

		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		MyPhoneStateListener listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		innerReceiver = new InnerReceiver();
		IntentFilter intentFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(Integer.MAX_VALUE);
		registerReceiver(innerReceiver, intentFilter);
	}

	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String mode = dao.findNumber(incomingNumber);
				if (mode.equals("1") || mode.equals("2")) {
					// 挂断电话
					endCall();
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver()
							.registerContentObserver(
									uri,
									true,
									new MyContentObserver(new Handler(),
											incomingNumber));
				}
				break;
			}
		}
	}

	private class MyContentObserver extends ContentObserver {
		String incomingNumber;

		public MyContentObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		// 当数据改变的时候调用的方法
		@Override
		public void onChange(boolean selfChange) {
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
		}
	}

	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Object[] objects = (Object[]) intent.getExtras().get("pdus");

			for (Object object : objects) {// 短信最多140字节,
				// 超出的话,会分为多条短信发送,所以是一个数组,因为我们的短信指令很短,所以for循环只执行一次
				SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
				String originatingAddress = message.getOriginatingAddress();// 短信来源号码
				String messageBody = message.getMessageBody();// 短信内容
				// 通过短信的电话号码查询拦截的模式
				String mode = dao.findNumber(originatingAddress);
				/**
				 * 黑名单拦截模式 1 全部拦截 电话拦截 + 短信拦截 2 电话拦截 3 短信拦截
				 */
				if (mode.equals("1")) {
					abortBroadcast();
				} else if (mode.equals("3")) {
					abortBroadcast();
				}
				// 智能拦截模式 发票 你的头发漂亮 分词
				if (messageBody.contains("fapiao")) {
					abortBroadcast();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(innerReceiver);
	}

	// 删除通话记录
	public void deleteCallLog(String incomingNumber) {
		Uri uri = Uri.parse("content://call_log/calls");
		getContentResolver().delete(uri, "number=?",
				new String[] { incomingNumber });
	}

	// 挂断电话
	public void endCall() {
		try {
			// 通过类加载器加载ServiceManager类
			Class<?> clazz = getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			iTelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
