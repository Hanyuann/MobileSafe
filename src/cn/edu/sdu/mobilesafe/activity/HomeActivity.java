package cn.edu.sdu.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.utils.MD5Utils;

public class HomeActivity extends Activity {
	private SharedPreferences mPref;

	private GridView gv_home;

	private String[] mItems = new String[] { "�ֻ�����", "ͨѶ��ʿ", "�������", "���̹���",
			"����ͳ��", "�ֻ�ɱ��", "��������", "�߼�����", "��������" };
	private int[] mPics = new int[] { R.drawable.home_safe,
			R.drawable.home_callmsgsafe, R.drawable.home_apps,
			R.drawable.home_taskmanager, R.drawable.home_netmanager,
			R.drawable.home_trojan, R.drawable.home_sysoptimize,
			R.drawable.home_tools, R.drawable.home_settings };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new HomeAdapter());
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			// ���ü���
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					// �ֻ�����
					showPasswordDialog();
					break;
				case 8:
					// ��������
					startActivity(new Intent(HomeActivity.this,
							SettingsActivity.class));
					break;
				default:
					break;
				}
			}
		});
	}

	// ��ʾ���뵯��
	protected void showPasswordDialog() {
		// �ж��Ƿ�������
		String savedPassword = mPref.getString("password", null);
		if (!TextUtils.isEmpty(savedPassword)) {
			showPasswordInputDialog();
		} else {
			// ���û�����ù���������������ĵ���
			showPasswordSetDialog();
		}
	}

	// ��������ĵ���
	private void showPasswordInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_input_password, null);
		// dialog.setView(view);// ���Զ���Ĳ����ļ����ø�Dialog
		dialog.setView(view, 0, 0, 0, 0);// ���ñ߾�Ϊ0����֤��2.x�İ汾������û����

		final EditText et_password = (EditText) view
				.findViewById(R.id.et_password);

		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString();
				if (!TextUtils.isEmpty(password)) {
					String savedPassword = mPref.getString("password", null);
					if (MD5Utils.encode(password).equals(savedPassword)) {
						dialog.dismiss();
						// ��ת���ֻ�����ҳ
						startActivity(new Intent(HomeActivity.this,
								LostFindActivity.class));
					} else {
						Toast.makeText(HomeActivity.this, "�������",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(HomeActivity.this, "�������Ϊ�գ�",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	// ��������ĵ���
	private void showPasswordSetDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_set_password, null);
		// dialog.setView(view);// ���Զ���Ĳ����ļ����ø�Dialog
		dialog.setView(view, 0, 0, 0, 0);// ���ñ߾�Ϊ0����֤��2.x�İ汾������û����

		final EditText et_password = (EditText) view
				.findViewById(R.id.et_password);
		final EditText et_password_confirm = (EditText) view
				.findViewById(R.id.et_password_confirm);

		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString();
				String password_confirm = et_password_confirm.getText()
						.toString();

				if (!TextUtils.isEmpty(password)
						&& !TextUtils.isEmpty(password_confirm)) {
					if (password.equals(password_confirm)) {
						mPref.edit()
								.putString("password",
										MD5Utils.encode(password)).commit();
						dialog.dismiss();
						startActivity(new Intent(HomeActivity.this,
								LostFindActivity.class));
					} else {
						Toast.makeText(HomeActivity.this, "�������벻һ�£�",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(HomeActivity.this, "�������Ϊ�գ�",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	class HomeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mItems.length;
		}

		@Override
		public Object getItem(int position) {
			return mItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this,
					R.layout.home_list_item, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

			tv_item.setText(mItems[position]);
			iv_item.setImageResource(mPics[position]);
			return view;
		}

	}
}
