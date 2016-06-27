package com.bqt.myview;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flowlayout);

		FlowLayout flow_layout = (FlowLayout) findViewById(R.id.flow_layout);
		//一定要注意，我们自定义的FlowLayout中使用的是MarginLayoutParams，所以这里也只能用MarginLayoutParams，不然报ClassCastException
		MarginLayoutParams marginLayoutParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int margins = (int) (2 * getResources().getDisplayMetrics().density + 0.5f);
		marginLayoutParams.setMargins(margins, margins, margins, margins);
		TextView tv1 = new TextView(new ContextThemeWrapper(this, R.style.text_style3), null, 0);//这是代码中设置style的方法！
		TextView tv2 = new TextView(new ContextThemeWrapper(this, R.style.text_style2), null, 0);
		TextView tv3 = new TextView(new ContextThemeWrapper(this, R.style.text_style1), null, 0);
		TextView tv4 = new TextView(new ContextThemeWrapper(this, R.style.text_style2), null, 0);
		tv1.setText("代码中添加View");
		tv2.setText("并设置style");
		tv3.setText("并设置margins");
		tv4.setText("博客：http://www.cnblogs.com/baiqiantao/，如果TextView内容特别长会是这种效果");
		tv1.setLayoutParams(marginLayoutParams);
		tv2.setLayoutParams(marginLayoutParams);
		tv3.setLayoutParams(marginLayoutParams);
		tv4.setLayoutParams(marginLayoutParams);
		flow_layout.addView(tv1);
		flow_layout.addView(tv2);
		flow_layout.addView(tv3);
		flow_layout.addView(tv4);
	}
}