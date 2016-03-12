package cn.edu.sdu.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

//��ȡ�����TextView
public class FocusedTextView extends TextView {

	// ��style��ʽ�Ļ����ߴ˷���
	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// �����ԵĻ����ߴ˷���
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// �ô���new����ʱ�ߴ˷���
	public FocusedTextView(Context context) {
		super(context);
	}

	// ��ʾ��û�л�ȡ����
	// �����Ҫ���У����ȵ��ô˺����ж��Ƿ��н��㣬��true�Ļ�������Ʋ���Ч
	@Override
	public boolean isFocused() {
		return true;
	}

}
