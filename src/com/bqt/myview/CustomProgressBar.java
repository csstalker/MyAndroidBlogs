package com.bqt.myview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CustomProgressBar extends View {
	/**第一圈的颜色*/
	private int mFirstColor = Color.GREEN;
	/**第二圈的颜色*/
	private int mSecondColor = Color.RED;
	/**圈的宽度*/
	private int mCircleWidth = 40;
	/**当前进度*/
	private int mProgress = 0;
	/**速度。实际代表的是休眠时间，这里只是为了方便演示*/
	private int mSpeed = 30;
	/**进度的百分比形式*/
	private String percent = "0%";
	/**绘制时文本绘制的范围*/
	private Rect mRect = null;
	/**绘制时文本的大小*/
	private int sTextSize = 40;
	/**绘制时文本的颜色*/
	private int sTextColor = Color.BLACK;
	/**画笔*/
	private Paint mPaint = null;
	/**是否应该开始下一个*/
	private boolean isToNext = false;
	/**用于定义圆弧的形状和大小的界限*/
	private RectF oval;
	private OnComompleteListener mListener;

	public CustomProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomProgressBar(Context context) {
		this(context, null);
	}

	public CustomProgressBar(final Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mRect = new Rect();
		mPaint = new Paint();
		oval = new RectF();
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyle, 0);
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			int attr = typedArray.getIndex(i);
			switch (attr) {
			case R.styleable.CustomProgressBar_firstColor:
				mFirstColor = typedArray.getColor(attr, Color.GREEN);
				break;
			case R.styleable.CustomProgressBar_secondColor:
				mSecondColor = typedArray.getColor(attr, Color.RED);
				break;
			case R.styleable.CustomProgressBar_circleWidth:
				mCircleWidth = typedArray.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
				break;
			case R.styleable.CustomProgressBar_speed:
				mSpeed = typedArray.getInt(attr, 20);
				break;
			}
		}
		typedArray.recycle();

		// 绘图线程
		new Thread() {
			public void run() {
				while (true) {
					mProgress++;
					if (mProgress == 360) {
						if (mListener != null && context instanceof Activity) {
							((Activity) context).runOnUiThread(new Runnable() {//Activity拿到回调时，不能直接在子线程中更新UI
										@Override
										public void run() {
											mListener.onComplete(CustomProgressBar.this, isToNext);
										}
									});
						}
						mProgress = 0;
						percent = "100%";
						if (!isToNext) {//如果不继续转的话就停止
							mPaint.getTextBounds(percent, 0, percent.length(), mRect);
							postInvalidate();//要刷新一下，否则最后的状态可能没绘制上
							return;
						}
					} else if (mProgress % 10 == 0) {//每10个进度刷新一次，目的是防止频繁的更新进度。实际项目中不需要考虑！
						percent = mProgress * 100 / 360 + "%";
					}
					mPaint.getTextBounds(percent, 0, percent.length(), mRect);//将初始文本的边界值封装到矩形mRect中
					postInvalidate();
					try {
						Thread.sleep(mSpeed);
					} catch (InterruptedException e) {
					}
				}
			};
		}.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.setAntiAlias(true); // 消除锯齿
		mPaint.setStyle(Paint.Style.STROKE); //空心
		mPaint.setStrokeWidth(mCircleWidth); // 设置圆环的宽度
		int centre = getWidth() / 2; // 获取圆心的x坐标
		int radius = centre - mCircleWidth / 2;// 半径
		oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限

		if (!isToNext) {// 第一颜色的圈完整，第二颜色跑
			mPaint.setColor(mFirstColor); // 设置圆环的颜色
			canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
			mPaint.setColor(mSecondColor); // 设置圆环的颜色
			canvas.drawArc(oval, -90, mProgress, false, mPaint); // 根据进度画圆弧
		} else {
			mPaint.setColor(mSecondColor); // 设置圆环的颜色
			canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
			mPaint.setColor(mFirstColor); // 设置圆环的颜色
			canvas.drawArc(oval, -90, mProgress, false, mPaint); // 根据进度画圆弧
		}
		//再绘制一个知识当前进度的文字，先包装文字的范围，再根据此大小调整文字的位置
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextSize(sTextSize);
		mPaint.setColor(sTextColor);
		canvas.drawText(percent, centre - mRect.width() / 2, centre + mRect.height() / 2, mPaint);
	}

	public boolean isToNext() {
		return isToNext;
	}

	public void setToNext(boolean isToNext) {
		this.isToNext = isToNext;
	}

	//自定义的回调
	public interface OnComompleteListener {
		public void onComplete(CustomProgressBar pb, boolean isToNext);
	}

	public void setOnComompleteListener(OnComompleteListener listener) {
		mListener = listener;
	}
}