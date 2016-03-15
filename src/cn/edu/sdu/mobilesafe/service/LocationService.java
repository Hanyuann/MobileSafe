package cn.edu.sdu.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {

	private LocationManager lm;
	private MyLocationListener listener;
	private SharedPreferences mPrefs;

	@Override
	public void onCreate() {
		super.onCreate();

		mPrefs = getSharedPreferences("config", MODE_PRIVATE);

		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);// �Ƿ�������,����ʹ��3G���綨λ
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = lm.getBestProvider(criteria, true);// ��ȡ��ѵ�λ���ṩ��

		listener = new MyLocationListener();
		// ������λ���ṩ��,��̸���ʱ��,��̸��¾���
		lm.requestLocationUpdates(bestProvider, 0, 0, listener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// ����ȡ�ľ�γ�ȱ�����sp��
			mPrefs.edit()
					.putString(
							"location",
							"longitude:" + location.getLongitude()
									+ ";latitude:" + location.getLatitude())
					.commit();
			String safePhone = mPrefs.getString("safe_phone", "");

			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(safePhone, null, mPrefs.getString("location", ""), null, null);
			stopSelf();// ͣ��service
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);
	}

}
