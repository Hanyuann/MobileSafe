package cn.edu.sdu.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/cn.edu.sdu.mobilesafe";

	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_status;
	private String mDescOff;
	private String mDescOn;
	private String mTitle;

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mTitle = attrs.getAttributeValue(NAMESPACE, "title");// �����������ƻ�ȡ���Ե�ֵ
		mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
		mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
		initView();

		// int attributeCount = attrs.getAttributeCount();
		// for (int i = 0; i < attributeCount; i++) {
		// String attributeName = attrs.getAttributeName(i);
		// String attributeValue = attrs.getAttributeValue(i);
		//
		// }
	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		// ������õĲ����ļ����ø���ǰ��SettingItemImageView
		View view = View.inflate(getContext(), R.layout.view_settings_item,
				this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		cb_status = (CheckBox) findViewById(R.id.cb_status);

		setTitle(mTitle);
	}

	public void setTitle(String text) {
		tv_title.setText(text);
	}

	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}

	public boolean isChecked() {
		return cb_status.isChecked();
	}

	public void setChecked(boolean check) {
		cb_status.setChecked(check);

		// ����ѡ���״̬�����ı�����
		if (check) {
			setDesc(mDescOn);
		} else {
			setDesc(mDescOff);
		}
	}
}