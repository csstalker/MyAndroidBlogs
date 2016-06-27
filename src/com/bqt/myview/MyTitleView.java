package com.bqt.myview;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MyTitleView extends View implements OnClickListener {
	/**初始文本内容，在定义时赋初值，当在布局中没有定义时就是用默认值*/
	private String mTitleText = "8888";
	/**文本的颜色*/
	private int mTitleTextColor = Color.RED;
	/**文本字体大小*/
	private int mTitleTextSize;
	/**背景色*/
	private int mTitleBackgroundColor = Color.YELLOW;
	/**圆角大小*/
	private int mTitleRoundSize;
	/**绘制时文本绘制的范围*/
	private Rect mRect;
	private Paint mPaint;//画笔
	private Context mContext;

	//在【代码】里面创建对象的时候使用此构造方法
	public MyTitleView(Context context) {
		this(context, null);
	}

	//在【布局】文件中使用时，系统默认会调用两个参数的构造方法
	public MyTitleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initAttrs(context, attrs, defStyle);
		initRect();
		mTitleRoundSize = dp2px(5);
		setOnClickListener(this);
	}

	//初始化属性集
	private void initAttrs(Context context, AttributeSet attrs, int defStyle) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyTitleView, defStyle, 0);//获取我们在【属性集】中【定义】的自定义属性的集合
		int count = typedArray.getIndexCount();//获取我们在【布局】文件中【设置】的自定义属性的个数，若没有设置任何自定义的属性，则此值为0
		for (int i = 0; i < count; i++) {
			int attrIndex = typedArray.getIndex(i);//获取此属性的编号，此值是由R文件自动生成的，代表【属性集】中定义的属性的位置，第一个属性对应的编号为0
			switch (attrIndex) {
			case R.styleable.MyTitleView_titleText://=0
				mTitleText = typedArray.getString(attrIndex);// 获取【布局】文件中设置的值
				break;
			case R.styleable.MyTitleView_titleTextColor://=1
				mTitleTextColor = typedArray.getColor(attrIndex, Color.RED);//defValue：Value to return if the attribute is not defined or not a resource 
				// 注意：布局中没设置此属性时代码根本执行不到这里，所以不能在此设置默认值，默认值建议直接在构造方法中初始化。
				break;
			case R.styleable.MyTitleView_titleBackground://=2
				mTitleBackgroundColor = typedArray.getColor(attrIndex, Color.YELLOW);
				break;
			case R.styleable.MyTitleView_titleTextSize://=3
				mTitleTextSize = typedArray.getDimensionPixelSize(attrIndex, 30);//能识别初代码中单位是sp(或dp)还是px
				break;
			}
		}
		typedArray.recycle();//Give back a previously retrieved array, for later re-use.
	}

	//获取文本需要占用的空间大小
	private void initRect() {
		mPaint = new Paint();
		//如果有自定义大小，就用自定义的，否则设置一个默认的。因为定义成员时Context还不存在，所以放在这里赋初值。
		if (mTitleTextSize == 0) mTitleTextSize = dp2px(30);
		mPaint.setTextSize(mTitleTextSize);//单位为px
		mRect = new Rect();
		mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mRect);//将初始文本的边界值封装到矩形mRect中
	}

	@Override
	public void onClick(View v) {
		mTitleText = getRandomText();
		invalidate();//调用此方法主动刷新UI。If the view is visible, onDraw will be called at some point in the future。
	}

	@Override
	//测量view尺寸时的回调方法
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//设置宽度
		int width = 0;
		int width_size = MeasureSpec.getSize(widthMeasureSpec);//测量的值
		int width_mode = MeasureSpec.getMode(widthMeasureSpec);//测量模式
		switch (width_mode) {
		case MeasureSpec.EXACTLY:// 设置了具体的值或者是【MATCH_PARENT】时，可以直接使用测量的值
			width = getPaddingLeft() + getPaddingRight() + width_size;//Padding值+测量值
			break;
		case MeasureSpec.AT_MOST:// 当我们设置为【WARP_CONTENT】时，测量的值实际为【MATCH_PARENT】，不能使用，所以我们重新计算
			width = getPaddingLeft() + getPaddingRight() + mRect.width();//Padding值+文本实际占用空间
			break;
		}
		//设置高度
		int height = 0;
		int height_size = MeasureSpec.getSize(heightMeasureSpec);
		int height_mode = MeasureSpec.getMode(heightMeasureSpec);
		switch (height_mode) {
		case MeasureSpec.EXACTLY:
			height = getPaddingTop() + getPaddingBottom() + height_size;
			break;
		case MeasureSpec.AT_MOST:
			height = getPaddingTop() + getPaddingBottom() + mRect.height();
			break;
		}
		//设置控件实际大小
		setMeasuredDimension(width, height);
	}

	@Override
	//在onDraw中绘制【圆角矩形】的背景和随机生成的文字。只要图形稍有改变，此方法在就会调用
	protected void onDraw(Canvas canvas) {
		Log.i("bqt", "getMeasuredWidth()=" + getMeasuredWidth() + "，getMeasuredHeight()=" + getMeasuredHeight());//点击一次调用一次
		mPaint.setColor(mTitleBackgroundColor);
		canvas.drawRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), mTitleRoundSize, mTitleRoundSize, mPaint);//绘制背景
		mPaint.setColor(mTitleTextColor);
		canvas.drawText(mTitleText, getWidth() / 2 - mRect.width() / 2, getHeight() / 2 + mRect.height() / 2, mPaint);//居中绘制文字
	}

	/** 
	  * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	  */
	public int dp2px(float dpValue) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 获取一个四位数字的随机数
	 */
	public String getRandomText() {
		Random random = new Random();
		Set<Integer> set = new HashSet<Integer>();
		while (set.size() < 4) {
			int randomInt = random.nextInt(10);
			set.add(randomInt);
		}
		StringBuffer sb = new StringBuffer();
		for (Integer i : set) {
			sb.append("" + i);
		}
		return sb.toString();
	}
}