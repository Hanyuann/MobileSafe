package cn.edu.sdu.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.edu.sdu.mobilesafe.db.dao.AddressDao;

//监听去电的广播接收者,需要权限android.permission.PROCESS_OUTGOING_CALLS
public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String number = getResultData();
		String address = AddressDao.getAddress(number);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();
	}

}
