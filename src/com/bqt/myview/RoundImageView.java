package com.bqt.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

public class RoundImageView extends ImageView {
	public static final int TYPE_CIRCLE = 0;
	public static final int TYPE_ROUND = 1;

	/**图片的类型，圆形or圆角，值请参考TYPE_CIRCLE或TYPE_ROUND*/
	private int viewType;
	/**圆角的大小*/
	private int roundRadiu;
	/**圆形的半径*/
	private int circleRadiu;

	/**记录圆角矩形的边界*/
	private RectF mRoundRect;
	private Paint mBitmapPaint;

	public RoundImageView(Context context) {
		this(context, null);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
		roundRadiu = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_borderRadius, dp2px(10));//默认为10dp
		viewType = typedArray.getInt(R.styleable.RoundImageView_type, TYPE_CIRCLE);//默认为圆形
		//circleRadiu的大小需要在onMeasure根据测量到的宽高确定
		typedArray.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//因为我们是直接继承ImageView(而不是继承自View)，所以直接调用super方法就能正确得到实际的宽高(按ImageView默认的规则)
		if (viewType == TYPE_CIRCLE) {
			int mSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
			circleRadiu = mSize / 2;
			setMeasuredDimension(mSize, mSize);//实际的大小
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable == null) return;

		//1、根据Bitmap要裁剪的类型，以及bitmap和view的宽高，计算Bitmap需要缩放的比例
		Bitmap bitmap = drawableToBitamp(drawable);
		float scale = calculateBitmapScale(bitmap);

		//2、为BitmapShader定义一个变换矩阵Matrix，通过Matrix对Bitmap进行缩放
		Matrix mMatrix = new Matrix();
		mMatrix.setScale(scale, scale);

		//3、通过Matrix将缩放后的Bitmap移动到View的中心位置
		float dx = getMeasuredWidth() - bitmap.getWidth() * scale;
		float dy = getMeasuredHeight() - bitmap.getHeight() * scale;
		mMatrix.postTranslate(dx / 2, dy / 2);//注意只能用一个set方法，其他的要用post或pre方法

		//4、通过BitmapShader对Bitmap进行渲染，
		BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.MIRROR, TileMode.REPEAT);//后两个参数：填充模式
		bitmapShader.setLocalMatrix(mMatrix);//由于我们已经让Bitmap的宽高一定【大于】view的宽高，所以上面的填充模式是没有任何效果的。
		mBitmapPaint.setShader(bitmapShader);

		//5、最后画出所需要的图形
		if (viewType == TYPE_ROUND) {
			// 【一】可以在这里通过getWidth、getHeight获取View的大小
			mRoundRect = new RectF(0, 0, getWidth(), getHeight());
			canvas.drawRoundRect(mRoundRect, roundRadiu, roundRadiu, mBitmapPaint);//在哪个矩形区域绘制，圆角大小
		} else {
			canvas.drawCircle(circleRadiu, circleRadiu, circleRadiu, mBitmapPaint);//圆心坐标，半径
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 【二】也可以在这里直接使用系统帮我们测量的准确的View的大小
		//	if (viewType == TYPE_ROUND) mRoundRect = new RectF(0, 0, w, h);
		Log.i("bqt", "w=" + w + "--h=" + h);
	}

	/**drawable转bitmap*/
	private Bitmap drawableToBitamp(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			return bitmapDrawable.getBitmap();
		}
		int drawableWidth = drawable.getIntrinsicWidth();
		int drawableHeight = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(drawableWidth, drawableHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawableWidth, drawableHeight);
		drawable.draw(canvas);
		return bitmap;
	}

	/**计算Bitmap需要缩放的比例*/
	private float calculateBitmapScale(Bitmap bitmap) {
		float scale = 1.0f;
		if (viewType == TYPE_CIRCLE) {
			int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
			scale = circleRadiu * 2.0f / bSize;
		} else if (viewType == TYPE_ROUND) {
			// 如果Bitmap的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的Bitmap的宽高，一定要【大于】view的宽高
			if (bitmap.getWidth() != getWidth() || bitmap.getHeight() != getHeight()) {
				float scaleWidth = getWidth() * 1.0f / bitmap.getWidth();
				float scaleHeight = getHeight() * 1.0f / bitmap.getHeight();
				scale = Math.max(scaleWidth, scaleHeight);
			}
		}
		Log.i("bqt", "缩放比例" + scale);
		return scale;
	}

	/**根据屏幕规格，将dp值转为px值*/
	public int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
	}

	//对外公布两个方法，用于动态修改圆角大小和type
	public void setBorderRadius(int borderRadius) {
		int pxVal = dp2px(borderRadius);
		if (roundRadiu != pxVal) {
			roundRadiu = pxVal;
			invalidate();
		}
	}

	public void setType(int type) {
		if (type != TYPE_ROUND && type != TYPE_CIRCLE) {
			type = TYPE_CIRCLE;
		}
		if (viewType != type) {
			viewType = type;
			requestLayout();
		}
	}
}