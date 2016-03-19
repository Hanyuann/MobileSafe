package cn.edu.sdu.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.mobilesafe.R;
import cn.edu.sdu.mobilesafe.adapter.MyBaseAdapter;
import cn.edu.sdu.mobilesafe.bean.BlackNumberInfo;
import cn.edu.sdu.mobilesafe.db.dao.BlackNumberDao;
import cn.edu.sdu.mobilesafe.utils.ToastUtils;

public class CallSafeActivity2 extends Activity {
	private ListView lv_black;
	private List<BlackNumberInfo> blackNumberInfos;
	private LinearLayout ll_load;
	private BlackNumberDao dao;
	private CallSafeAdapter adapter;

	private int mCurrentPageNumber = 0;

	// ÿҳչʾ20������
	private int mPageSize = 8;
	private TextView tv_page_numbeer;
	// һ���ж���ҳ��
	private int totalPage;
	private EditText et_page_number;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ll_load.setVisibility(View.INVISIBLE);
			tv_page_numbeer.setText((mCurrentPageNumber + 1) + "/" + totalPage);
			adapter = new CallSafeAdapter(blackNumberInfos,
					CallSafeActivity2.this);
			lv_black.setAdapter(adapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_safe2);
		initUI();
		initData();
	}

	private void initData() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				dao = new BlackNumberDao(CallSafeActivity2.this);
				// ͨ���ܵļ�¼�� / ÿҳ����������
				totalPage = dao.getTotalNumber() / mPageSize;
				blackNumberInfos = dao.findPar(mCurrentPageNumber, mPageSize);
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void initUI() {
		ll_load = (LinearLayout) findViewById(R.id.ll_load);
		ll_load.setVisibility(View.VISIBLE);
		lv_black = (ListView) findViewById(R.id.lv_black);
		tv_page_numbeer = (TextView) findViewById(R.id.tv_page_numbeer);
		et_page_number = (EditText) findViewById(R.id.et_page_number);
	}

	private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {

		private CallSafeAdapter(List lists, Context mContext) {
			super(lists, mContext);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = View.inflate(CallSafeActivity2.this,
						R.layout.list_item_blacknumber, null);
				holder = new ViewHolder();
				holder.tv_number = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_number
					.setText(blackNumberInfos.get(position).getNumber());
			String mode = blackNumberInfos.get(position).getMode();
			if (mode.equals("1")) {
				holder.tv_mode.setText("�������� + �绰����");
			} else if (mode.equals("2")) {
				holder.tv_mode.setText("�绰����");
			} else if (mode.equals("3")) {
				holder.tv_mode.setText("��������");
			}

			final BlackNumberInfo info = lists.get(position);
			holder.iv_delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					boolean result = dao.delete(number);
					if (result) {
						ToastUtils.showToast(CallSafeActivity2.this, "ɾ���ɹ�");
						lists.remove(info);
						// ˢ�½���
						initData();
					} else {
						ToastUtils.showToast(CallSafeActivity2.this, "ɾ��ʧ��");
					}
				}
			});
			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}

	// ��һҳ
	public void prePage(View view) {

		if (mCurrentPageNumber <= 0) {
			Toast.makeText(this, "�Ѿ��ǵ�һҳ��", Toast.LENGTH_SHORT).show();
			return;
		}
		mCurrentPageNumber--;
		initData();
	}

	// ��һҳ
	public void nextPage(View view) {
		// �жϵ�ǰ��ҳ�벻�ܴ����ܵ�ҳ��
		if (mCurrentPageNumber >= (totalPage - 1)) {
			Toast.makeText(this, "�Ѿ������һҳ��", Toast.LENGTH_SHORT).show();
			return;
		}

		mCurrentPageNumber++;
		initData();
	}

	// ��ת
	public void jump(View view) {
		String str_page_number = et_page_number.getText().toString().trim();
		if (TextUtils.isEmpty(str_page_number)) {
			ToastUtils.showToast(this, "��������ȷ��ҳ��");
		} else {
			int number = Integer.parseInt(str_page_number);
			totalPage = dao.getTotalNumber() / mPageSize;
			if (number > 0 && number <= totalPage) {
				mCurrentPageNumber = number - 1;
				initData();
			} else {
				ToastUtils.showToast(this, "��������ȷ��ҳ��");
			}
		}
	}
}
