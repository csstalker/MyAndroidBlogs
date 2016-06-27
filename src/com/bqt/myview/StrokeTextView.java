package com.bqt.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * 给文字加描边，实现方法是两个TextView叠加，只有描边的TextView放在底部，实体TextView叠加在上面
 * 当此控件被创建的同时创建另一个TextView，并将此TextView设为STROKE样式
 * 在测量onMeasure、绘制onDraw、布局onLayout自身时，使用同样的参数同样的方式处理此STROKE样式的TextView
 */
public class StrokeTextView extends TextView {
	/**描边的TextView*/
	private TextView strokeTextView = null;
	/**描边的TextView的颜色*/
	private int strokeColor = 0xffff0000;
	/**描边的TextView的宽度*/
	private float strokeWidth = 1.5f;

	/**
	 * 设置描边的颜色和宽度
	 */
	public void setStrokeColorAndWidth(int strokeColor, float strokeWidth) {
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
		init();//必须重新调用初始化方法
	}

	public StrokeTextView(Context context) {
		this(context, null);
	}

	public StrokeTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		strokeTextView = new TextView(context, attrs, defStyle);
		init();
	}

	public void init() {
		TextPaint textPaint = strokeTextView.getPaint();
		textPaint.setStrokeWidth(strokeWidth); //设置描边宽度
		strokeTextView.setTextColor(strokeColor); //设置描边颜色
		textPaint.setStyle(Style.STROKE); //对文字只描边
		invalidate();
	}

	//**************************************************************************************************************************
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		strokeTextView.setText(getText());
		strokeTextView.setGravity(getGravity());
		strokeTextView.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	//卧槽，为啥必须把super放在后面？
	protected void onDraw(Canvas canvas) {
		strokeTextView.draw(canvas);
		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		strokeTextView.layout(left, top, right, bottom);
	}

	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params) {
		super.setLayoutParams(params);
		strokeTextView.setLayoutParams(params);
	}

	@Override
	public void setBackgroundResource(int resid) {
		super.setBackgroundResource(resid);
		strokeTextView.setBackgroundResource(resid);
	}

	@Override
	public void setTextSize(float size) {
		super.setTextSize(size);
		strokeTextView.setTextSize(size);
	}

	@Override
	public void setTextSize(int unit, float size) {
		super.setTextSize(unit, size);
		strokeTextView.setTextSize(unit, size);
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		super.setPadding(left, top, right, bottom);
		strokeTextView.setPadding(left, top, right, bottom);
	}
}