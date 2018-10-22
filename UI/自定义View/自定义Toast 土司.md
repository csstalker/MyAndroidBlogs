[Markdown版本](https://github.com/baiqiantao/MyAndroidBlogs)
[我的GitHub](https://github.com/baiqiantao)


---------------


自定义Toast 土司
***
目录
===
[TOC]

# 自定义Toast
```java
public class CToast {
    //这里只是一个案例，你可以随意定制自己喜欢的样式
    public static CToast makeText(Context context, CharSequence text, int duration) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(dp2px(context, 20));//边角
        drawable.setGradientType(GradientDrawable.RECTANGLE);//矩形
        drawable.setColor(Color.BLACK);//填充色
        drawable.setStroke(1, Color.WHITE);//描边
        
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(13);
        tv.setPadding(dp2px(context, 20), dp2px(context, 7), dp2px(context, 20), dp2px(context, 7));
        tv.setGravity(Gravity.CENTER);
        
        LinearLayout mLayout = new LinearLayout(context);
        mLayout.setBackground(drawable);
        mLayout.addView(tv);
        
        CToast toast = new CToast(context);
        toast.mNextView = mLayout;
        toast.mDuration = duration;
        return toast;
    }
    
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
    
    public static final int LENGTH_SHORT = 2000;
    public static final int LENGTH_LONG = 3500;
    
    private final Handler mHandler = new Handler();
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private WindowManager mWM;
    
    private int mDuration = LENGTH_SHORT;
    private int mGravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    private float mHorizontalMargin = 0, mVerticalMargin = 0;
    private int mX = 0, mY;
    
    private View mView, mNextView;
    
    public CToast(Context context) {
        init(context);
    }
    
    public void setView(View view) {
        mNextView = view;
    }
    
    public View getView() {
        return mNextView;
    }
    
    public void setDuration(int duration) {
        mDuration = duration;
    }
    
    public int getDuration() {
        return mDuration;
    }
    
    public void setMargin(float horizontalMargin, float verticalMargin) {
        mHorizontalMargin = horizontalMargin;
        mVerticalMargin = verticalMargin;
    }
    
    public float getHorizontalMargin() {
        return mHorizontalMargin;
    }
    
    public float getVerticalMargin() {
        return mVerticalMargin;
    }
    
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mGravity = gravity;
        mX = xOffset;
        mY = yOffset;
    }
    
    public int getGravity() {
        return mGravity;
    }
    
    public int getXOffset() {
        return mX;
    }
    
    public int getYOffset() {
        return mY;
    }
    
    public void show() {
        mHandler.post(mShow);
        if (mDuration > 0) {
            mHandler.postDelayed(mHide, mDuration);
        }
    }
    
    public void hide() {
        mHandler.post(mHide);
    }
    
    private final Runnable mShow = this::handleShow;
    private final Runnable mHide = this::handleHide;
    
    private void init(Context context) {
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = android.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        mWM = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mY = dp2px(context, 30);
    }
    
    private void handleShow() {
        if (mView != mNextView) {
            handleHide();// remove the old view if necessary
            mView = mNextView;
            final int gravity = mGravity;
            mParams.gravity = gravity;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f;
            }
            mParams.x = mX;
            mParams.y = mY;
            mParams.verticalMargin = mVerticalMargin;
            mParams.horizontalMargin = mHorizontalMargin;
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
            mWM.addView(mView, mParams);
        }
    }
    
    private void handleHide() {
        if (mView != null) {
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
            mView = null;
        }
    }
}
```

2018-10-13