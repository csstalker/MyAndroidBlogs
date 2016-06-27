package com.bqt.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class SwitchButton extends View implements OnClickListener, OnTouchListener {
	private Context mContext;
	private Bitmap mSwitchBottom, mSwitchThumb, mSwitchFrame, mSwitchMask;
	private float mCurrentX = 0;
	/**开关状态*/
	private boolean mSwitchOn = true;
	/**最大移动距离*/
	private int mMoveLength;
	/**第一次按下的有效区域*/
	private float mLastX = 0;
	/**绘制的目标区域大小*/
	private Rect mDest = null;
	/**截取源图片的大小*/
	private Rect mSrc = null;
	/**移动的偏移量*/
	private int mDeltX = 0;
	private Paint mPaint = null;
	private OnChangeListener mListener = null;
	private boolean mFlag = false;

	public SwitchButton(Context context) {
		this(context, null);
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		mSwitchBottom = BitmapFactory.decodeResource(getResources(), R.drawable.switch_bottom);
		mSwitchThumb = BitmapFactory.decodeResource(getResources(), R.drawable.switch_btn_pressed);
		mSwitchFrame = BitmapFactory.decodeResource(getResources(), R.drawable.switch_frame);
		mSwitchMask = ((BitmapDrawable) getResources().getDrawable(R.drawable.switch_mask)).getBitmap();//两种方式都行

		mMoveLength = mSwitchBottom.getWidth() - mSwitchFrame.getWidth();
		mSrc = new Rect();
		mDest = new Rect(0, 0, mSwitchFrame.getWidth(), mSwitchFrame.getHeight());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setAlpha(255);
		mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		setOnClickListener(this);
		setOnTouchListener(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//这里的大小是写死的，所以布局中任何宽高的设置都是无效的！
		setMeasuredDimension(mSwitchFrame.getWidth(), mSwitchFrame.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mDeltX > 0 || mDeltX == 0 && mSwitchOn) {
			if (mSrc != null) mSrc.set(mMoveLength - mDeltX, 0, mSwitchBottom.getWidth() - mDeltX, mSwitchFrame.getHeight());
		} else if (mDeltX < 0 || mDeltX == 0 && !mSwitchOn) {
			if (mSrc != null) mSrc.set(-mDeltX, 0, mSwitchFrame.getWidth() - mDeltX, mSwitchFrame.getHeight());
		}
		//这儿是离屏缓冲，自己感觉类似双缓冲机制吧
		int count = canvas.saveLayer(new RectF(mDest), null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
				| Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
		canvas.drawBitmap(mSwitchBottom, mSrc, mDest, null);
		canvas.drawBitmap(mSwitchThumb, mSrc, mDest, null);
		canvas.drawBitmap(mSwitchFrame, 0, 0, null);
		canvas.drawBitmap(mSwitchMask, 0, 0, mPaint);
		canvas.restoreToCount(count);
	}

	@Override
	//onTouchListener的onTouch优先级比onTouchEvent高，会先触发。若onTouch返回false会接着触发onTouchEvent，反之onTouchEvent方法不会被调用！
	public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
		return false;//内置诸如click事件的实现等等都基于onTouchEvent，假如onTouch返回true，这些事件将不会被触发
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			mCurrentX = event.getX();
			mDeltX = (int) (mCurrentX - mLastX);
			// 如果开关开着向左滑动，或者开关关着向右滑动（这时候是不需要处理的）
			if ((mSwitchOn && mDeltX < 0) || (!mSwitchOn && mDeltX > 0)) {
				mFlag = true;
				mDeltX = 0;
			}
			if (Math.abs(mDeltX) > mMoveLength) mDeltX = mDeltX > 0 ? mMoveLength : -mMoveLength;
			invalidate();
			return true;
		case MotionEvent.ACTION_UP:
			if (Math.abs(mDeltX) > 0 && Math.abs(mDeltX) < mMoveLength / 2) {
				mDeltX = 0;
				invalidate();
				return true;
			} else if (Math.abs(mDeltX) > mMoveLength / 2 && Math.abs(mDeltX) <= mMoveLength) {
				mDeltX = mDeltX > 0 ? mMoveLength : -mMoveLength;
				mSwitchOn = !mSwitchOn;
				if (mListener != null) mListener.onChange(this, mSwitchOn);
				invalidate();
				mDeltX = 0;
				return true;
			} else if (mDeltX == 0 && mFlag) {
				//这时候得到的是不需要进行处理的，因为已经move过了
				mDeltX = 0;
				mFlag = false;
				return true;
			}
			return super.onTouchEvent(event);
		default:
			break;
		}
		invalidate();
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		mDeltX = mSwitchOn ? mMoveLength : -mMoveLength;
		mSwitchOn = !mSwitchOn;
		if (mListener != null) mListener.onChange(this, mSwitchOn);
		invalidate();
		mDeltX = 0;
	}

	//自定义的回调
	public interface OnChangeListener {
		public void onChange(SwitchButton sb, boolean state);
	}

	public void setOnChangeListener(OnChangeListener listener) {
		mListener = listener;
	}
}