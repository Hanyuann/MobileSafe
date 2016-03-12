package cn.edu.sdu.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

//获取焦点的TextView
public class FocusedTextView extends TextView {

	// 有style样式的话会走此方法
	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 有属性的话会走此方法
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 用代码new对象时走此方法
	public FocusedTextView(Context context) {
		super(context);
	}

	// 表示有没有获取焦点
	// 跑马灯要运行，首先调用此函数判断是否有焦点，是true的话，跑马灯才有效
	@Override
	public boolean isFocused() {
		return true;
	}

}
