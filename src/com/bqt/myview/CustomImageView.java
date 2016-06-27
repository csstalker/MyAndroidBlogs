package com.bqt.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义View，将普通图形截取成圆角，圆形或带描边等效果
 */
public class CustomImageView extends View {
	/**圆形*/
	public static final int TYPE_CIRCLE = 0;
	/**矩形*/
	public static final int TYPE_RECTF = 1;
	/**带描边的圆形*/
	public static final int TYPE_CIRCLE_WITH_STROKE = 2;
	/**类型，默认为圆形*/
	private int type = TYPE_CIRCLE;
	/**代码中使用需指定宽高，否则是全屏的*/
	private Bitmap mSrc = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
	/**圆角的大小*/
	private int mRadius;
	/**描边的颜色*/
	private int strokeColor = 0xffff0000;
	/**描边的宽度*/
	private int strokeWidth;
	/**控件的宽度*/
	private int mWidth;
	/**控件的高度*/
	private int mHeight;
	private Context mContext;

	public CustomImageView(Context context) {
		this(context, null);
	}

	public CustomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		mRadius = dp2px(40);
		strokeWidth = dp2px(2);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyle, 0);
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			int attr = typedArray.getIndex(i);
			switch (attr) {
			case R.styleable.CustomImageView_src:
				mSrc = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(attr, 0));
				break;
			case R.styleable.CustomImageView_types://类型
				type = typedArray.getInt(attr, 0);
				break;
			case R.styleable.CustomImageView_radius://圆角大小
				mRadius = typedArray.getDimensionPixelSize(attr, dp2px(5));
				break;
			}
		}
		typedArray.recycle();
	}

	//**************************************************************************************************************************
	// 计算控件的高度和宽度
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//设置宽度
		int width_size = MeasureSpec.getSize(widthMeasureSpec);
		int width_mode = MeasureSpec.getMode(widthMeasureSpec);
		if (width_mode == MeasureSpec.EXACTLY) mWidth = width_size;
		else {
			// 由图片决定的宽
			int desireByImg = getPaddingLeft() + getPaddingRight() + mSrc.getWidth();
			if (width_mode == MeasureSpec.AT_MOST) mWidth = Math.min(desireByImg, width_size);
			else mWidth = desireByImg;
		}
		//设置高度
		int height_size = MeasureSpec.getSize(heightMeasureSpec);
		int height_mode = MeasureSpec.getMode(heightMeasureSpec);
		if (height_mode == MeasureSpec.EXACTLY) mHeight = height_size;
		else {
			int desire = getPaddingTop() + getPaddingBottom() + mSrc.getHeight();
			if (height_mode == MeasureSpec.AT_MOST) mHeight = Math.min(desire, height_size);
			else mHeight = desire;
		}
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	//绘制图形
	protected void onDraw(Canvas canvas) {
		switch (type) {
		case TYPE_CIRCLE:
			int min = Math.min(mWidth, mHeight);//长度如果不一致，按小的值进行压缩
			mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
			canvas.drawBitmap(getCircleImage(mSrc, min), 0, 0, null);
			break;
		case TYPE_RECTF:
			canvas.drawBitmap(getRoundConerImage(mSrc), 0, 0, null);
			break;
		case TYPE_CIRCLE_WITH_STROKE:
			int min2 = Math.min(mWidth, mHeight);
			mSrc = Bitmap.createScaledBitmap(mSrc, min2, min2, false);
			canvas.drawBitmap(getCircleImageWithStroke(mSrc, min2), 0, 0, null);
			break;
		}
	}

	//**************************************************************************************************************************
	/**
	 * 绘制【圆形】图片
	 * @param min 要生成的图形大小
	 */
	private Bitmap getCircleImage(Bitmap source, int min) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);//产生一个同样大小的画布
		//SRC_IN模式，取【交集】展现【后】者
		canvas.drawCircle(min / 2, min / 2, min / 2, paint); //先绘制圆形
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//使用SRC_IN
		canvas.drawBitmap(source, 0, 0, paint);//后绘制图片
		return target;
	}

	/**
	 * 绘制【圆角矩形】图片
	 */
	private Bitmap getRoundConerImage(Bitmap source) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());//指定圆角矩形的边界
		//疑问：采用DST_IN模式（取交集展现前图）并改变绘制顺序为何不对？
		canvas.drawRoundRect(rect, mRadius, mRadius, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	/**
	 * 绘制【带描边】的圆形图片
	 */
	private Bitmap getCircleImageWithStroke(Bitmap source, int min) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		//先裁剪成一个圆形图片
		canvas.drawCircle(min / 2, min / 2, min / 2, paint); //先绘制圆形
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//使用SRC_IN
		canvas.drawBitmap(source, 0, 0, paint);//后绘制图片
		//再画一个带颜色的空心的圈
		paint.setColor(strokeColor);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Style.STROKE);
		//疑问：为什么不是采用DST_ATOP模式（取前图非交集部分与后图交集部分）
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		return target;
	}

	/** 
	  * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	  */
	public int dp2px(float dpValue) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}