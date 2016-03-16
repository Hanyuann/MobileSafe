package cn.edu.sdu.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.edu.sdu.mobilesafe.R;

public class SettingClickItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/cn.edu.sdu.mobilesafe";

	private TextView tv_title;
	private TextView tv_desc;

	public SettingClickItemView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SettingClickItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SettingClickItemView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		// 将定义好的布局文件设置给当前的SettingClickItemView
		View view = View.inflate(getContext(), R.layout.view_settings_click,
				this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);

	}

	public void setTitle(String text) {
		tv_title.setText(text);
	}

	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}
}