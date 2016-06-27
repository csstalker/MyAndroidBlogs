package com.bqt.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;

public class CustomVolumControlBar extends View implements OnClickListener {
	/**第一圈的颜色，底色*/
	private int mFirstColor = Color.GRAY;
	/**第二圈的颜色，覆盖在上面的颜色*/
	private int mSecondColor = Color.RED;
	/**圈的厚度*/
	private int mCircleWidth = 10;
	/**块块的个数*/
	private int mCount = 6;
	/**块块间的间隙*/
	private int mSplitSize = 20;
	/**当前进度（其实应该先判断mCount是否大于mCurrentCount的）*/
	private int mCurrentCount = 4;
	/**从哪开始算*/
	private int mStartCount = 0;
	/**中间的图片*/
	private Bitmap mImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	private final Paint mPaint = new Paint();//画笔
	private final Rect mRect = new Rect();

	public CustomVolumControlBar(Context context) {
		this(context, null);
	}

	public CustomVolumControlBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomVolumControlBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnClickListener(this);//必须给自己设置点击事件
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomVolumControlBar, defStyle, 0);
		for (int i = 0; i < a.getIndexCount(); i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.CustomVolumControlBar_firstColor:
				mFirstColor = a.getColor(attr, Color.GRAY);
				break;
			case R.styleable.CustomVolumControlBar_secondColor:
				mSecondColor = a.getColor(attr, Color.RED);
				break;
			case R.styleable.CustomVolumControlBar_cb_bg:
				mImage = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, R.drawable.ic_launcher));
				break;
			case R.styleable.CustomVolumControlBar_circleWidth:
				mCircleWidth = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 10, getResources().getDisplayMetrics()));
				break;
			case R.styleable.CustomVolumControlBar_dotCount:
				mCount = a.getInt(attr, 6);
				break;
			case R.styleable.CustomVolumControlBar_splitSize:
				mSplitSize = a.getInt(attr, 20);
				break;
			}
		}
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.setAntiAlias(true); // 消除锯齿
		mPaint.setStrokeWidth(mCircleWidth); // 设置圆环的宽度
		mPaint.setStrokeCap(Paint.Cap.ROUND); // 定义线段断点处形状为圆头
		mPaint.setAntiAlias(true); // 消除锯齿
		mPaint.setStyle(Paint.Style.STROKE); // 设置空心
		int centre = getWidth() / 2; // 获取圆心的x坐标
		int radius = centre - mCircleWidth / 2;// 半径
		//画块块去
		drawOval(canvas, centre, radius);
		//计算内切正方形的位置
		int relRadius = radius - mCircleWidth / 2;// 获得内圆的半径
		// 内切正方形的距离顶部 = mCircleWidth + relRadius - √2 / 2
		mRect.left = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
		//内切正方形的距离左边 = mCircleWidth + relRadius - √2 / 2
		mRect.top = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
		mRect.bottom = (int) (mRect.left + Math.sqrt(2) * relRadius);
		mRect.right = (int) (mRect.left + Math.sqrt(2) * relRadius);
		// 如果图片比较小，那么根据图片的尺寸放置到正中心
		if (mImage.getWidth() < Math.sqrt(2) * relRadius) {
			mRect.left = (int) (mRect.left + Math.sqrt(2) * relRadius * 1.0f / 2 - mImage.getWidth() * 1.0f / 2);
			mRect.top = (int) (mRect.top + Math.sqrt(2) * relRadius * 1.0f / 2 - mImage.getHeight() * 1.0f / 2);
			mRect.right = (int) (mRect.left + mImage.getWidth());
			mRect.bottom = (int) (mRect.top + mImage.getHeight());
		}
		// 绘图
		canvas.drawBitmap(mImage, null, mRect, mPaint);
	}

	//根据参数画出每个小块
	private void drawOval(Canvas canvas, int centre, int radius) {
		float itemSize = (360 * 1.0f - mCount * mSplitSize) / mCount;//根据需要画的个数以及间隙计算每个块块所占的比例*360
		RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
		mPaint.setColor(mFirstColor); // 设置圆环的颜色
		for (int i = 0; i < mCount; i++) {
			canvas.drawArc(oval, i * (itemSize + mSplitSize), itemSize, false, mPaint); // 根据进度画圆弧
		}
		mPaint.setColor(mSecondColor); // 设置圆环的颜色
		for (int i = 0; i < mCurrentCount; i++) {
			canvas.drawArc(oval, (i - mStartCount) * (itemSize + mSplitSize), itemSize, false, mPaint); // 根据进度画圆弧
		}
	}

	@Override
	public void onClick(View v) {
		if (mCurrentCount < mCount) mCurrentCount++;
		else mCurrentCount = 1;
		invalidate();
	}
}