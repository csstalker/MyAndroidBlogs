| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
自定义View 水印布局 WaterMark 前景色 MD  
***  
目录  
===  

- [第一种实现方式](#第一种实现方式)
	- [项目中的使用案例](#项目中的使用案例)
	- [水印布局 MarkFrameLayout](#水印布局-MarkFrameLayout)
	- [自定义属性](#自定义属性)
- [第二种实现方式](#第二种实现方式)
	- [使用案例](#使用案例)
	- [自定义 Drawable](#自定义-Drawable)
  
# 第一种实现方式  
## 项目中的使用案例  
项目中要求在所有页面都添加水印，这种情况下可以在BaseActivity中将水印布局设为根布局  
  
前景色样式：    
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181013/I5KiemLdmE.png?imageslim)    
背景色样式：    
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181013/lL07B0JGJd.png?imageslim)    
  
布局：    
```xml  
<com.bqt.lock.MarkFrameLayout  
    xmlns:android="http://schemas.android.com/apk/res/android"  
    xmlns:app="http://schemas.android.com/apk/res-auto"  
    android:id="@+id/mark_layout"  
    android:layout_width="match_parent"  
    android:layout_height="match_parent"  
    app:mark_is_foreground="false"  
    app:mark_show_value="包青天"  
    app:mark_textcolor="#fff">  
  
    <TextView  
        android:id="@+id/tv"  
        android:layout_width="match_parent"  
        android:layout_height="100dp"  
        android:background="#f00"  
        android:gravity="center"/>  
  
    <ImageView  
        android:layout_width="match_parent"  
        android:layout_height="100dp"  
        android:layout_marginTop="200dp"  
        android:scaleType="centerCrop"  
        android:src="@drawable/icon"/>  
  
</com.bqt.lock.MarkFrameLayout>  
```  
  
## 水印布局 MarkFrameLayout  
绘制水印时，可以选择在`onDrawForeground`上绘制前景色(盖在所有View的上面)，也可以选择在`onDraw`上绘制背景色(会被所有View的背景遮盖)。    
如果需要用到继承自其他其他 `Layout` 的水印布局，则只需将继承的类改为`RelativeLayout`或`LinearLayout`即可，其他什么都不需要更改。    
  
```java  
public class MarkFrameLayout extends FrameLayout {  
      
    private static final int DEFAULT_DEGRESES = -15;//水印倾斜角度  
    private static final int DEFAULT_MARK_PAINT_COLOR = Color.parseColor("#FFCCCCCC");//水印颜色  
    private static final int DEFAULT_ALPHA = (int) (0.5 * 255);//水印透明度  
    private static final String DEFAULT_MARK_SHOW_VALUE = "[水印]";//水印内容  
      
    private boolean showMark = true;  
    private float mMarkTextSize;  
    private int mMarkTextColor;  
    private boolean mMarkLayerIsForeground; //水印绘制在控件背景上，还是前景色上  
    private float mDegrees;  
    private int mVerticalSpacing;  
    private int mHorizontalSpacing;  
    private int mMarkPainAlpha;  
    private String mMarkValue;  
    private TextPaint mMarkPaint;  
    private Bitmap mMarkBitmap;  
      
    public MarkFrameLayout(@NonNull Context context) {  
        this(context, null);  
    }  
      
    public MarkFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {  
        super(context, attrs);  
        if (showMark) {  
            int defaultMarkTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());  
            int defaultSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());  
              
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MarkFrameLayout);  
            mDegrees = a.getInteger(R.styleable.MarkFrameLayout_mark_rotate_degrees, DEFAULT_DEGRESES);  
            mMarkTextColor = a.getColor(R.styleable.MarkFrameLayout_mark_textcolor, DEFAULT_MARK_PAINT_COLOR);  
            mMarkTextSize = a.getDimension(R.styleable.MarkFrameLayout_mark_textsize, defaultMarkTextSize);  
            mMarkPainAlpha = a.getInt(R.styleable.MarkFrameLayout_mark_alpha, DEFAULT_ALPHA);  
            mMarkLayerIsForeground = a.getBoolean(R.styleable.MarkFrameLayout_mark_is_foreground, true);//默认绘制在前景色上  
            mHorizontalSpacing = (int) a.getDimension(R.styleable.MarkFrameLayout_mark_hor_spacing, defaultSpacing);  
            mVerticalSpacing = (int) a.getDimension(R.styleable.MarkFrameLayout_mark_ver_spacing, defaultSpacing);  
            mMarkValue = a.getString(R.styleable.MarkFrameLayout_mark_show_value);  
            mMarkValue = TextUtils.isEmpty(mMarkValue) ? DEFAULT_MARK_SHOW_VALUE : mMarkValue;  
              
            a.recycle();  
            initWaterPaint();  
            setForeground(new ColorDrawable(Color.TRANSPARENT)); //重置前景色透明  
        }  
    }  
      
    @Override  
    public void onDrawForeground(Canvas canvas) {  
        super.onDrawForeground(canvas);  
        if (showMark && mMarkLayerIsForeground) {  
            drawMark(canvas); //绘制前景色  
        }  
    }  
      
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        if (showMark && !mMarkLayerIsForeground) {  
            drawMark(canvas); //绘制被景色  
        }  
    }  
      
    private void initWaterPaint() {  
        //初始化Mark的Paint  
        mMarkPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG); //mMarkPaint.setAntiAlias(true)  
        mMarkPaint.setColor(mMarkTextColor);  
        mMarkPaint.setAlpha(mMarkPainAlpha);  
        mMarkPaint.setTextSize(mMarkTextSize);  
        //初始化MarkBitmap  
        Paint.FontMetrics fontMetrics = mMarkPaint.getFontMetrics();  
        int textHeight = (int) (fontMetrics.bottom - fontMetrics.top);  
        int textLength = (int) mMarkPaint.measureText(mMarkValue);  
        mMarkBitmap = Bitmap.createBitmap(textLength + 2 * mHorizontalSpacing,  
                textHeight + mVerticalSpacing * 2, Bitmap.Config.ARGB_8888);  
        Canvas canvas = new Canvas(mMarkBitmap);  
        canvas.drawText(mMarkValue, mHorizontalSpacing, mVerticalSpacing, mMarkPaint);  
    }  
      
    private void drawMark(Canvas canvas) {  
        int maxSize = Math.max(getMeasuredWidth(), getMeasuredHeight());  
        mMarkPaint.setShader(new BitmapShader(mMarkBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));  
        canvas.save();  
        canvas.translate(-(maxSize - getMeasuredWidth()) / 2, 0);  
        canvas.rotate(mDegrees, maxSize / 2, maxSize / 2);  
        canvas.drawRect(new RectF(0, 0, maxSize, maxSize), mMarkPaint);  
        canvas.restore();  
    }  
      
    public void setShowMark(boolean showMark) {  
        this.showMark = showMark;  
        invalidate();  
    }  
}  
```  
  
## 自定义属性  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<resources>  
    <declare-styleable name="MarkFrameLayout">  
        <attr name="mark_rotate_degrees" format="integer"/>  
        <attr name="mark_textcolor" format="color|reference"/>  
        <attr name="mark_textsize" format="dimension"/>  
        <attr name="mark_alpha" format="integer"/>  
        <attr name="mark_is_foreground" format="boolean"/>  
        <attr name="mark_hor_spacing" format="dimension"/>  
        <attr name="mark_ver_spacing" format="dimension"/>  
        <attr name="mark_show_value" format="string"/>  
    </declare-styleable>  
  
</resources>  
```  
  
# 第二种实现方式  
[参考](https://github.com/fulushan/watermark-android)  
  
## 使用案例  
```java  
FrameLayout rootView = findViewById(R.id.layout);  
rootView.setForeground(new WaterMarkBg(this, labels, -10, 12));  
```  
  
## 自定义 Drawable  
```java  
public class WaterMarkBg extends Drawable {  
      
    private Paint paint = new Paint();  
    private List<String> labels;  
    private Context context;  
    private int degress;//角度  
    private int fontSize;//字体大小 单位sp  
      
    /**  
     * 初始化构造  
     *  
     * @param context  上下文  
     * @param labels   水印文字列表 多行显示支持  
     * @param degress  水印角度  
     * @param fontSize 水印文字大小  
     */  
    public WaterMarkBg(Context context, List<String> labels, int degress, int fontSize) {  
        this.labels = labels;  
        this.context = context;  
        this.degress = degress;  
        this.fontSize = fontSize;  
    }  
      
    @Override  
    public void draw(@NonNull Canvas canvas) {  
        int width = getBounds().right;  
        int height = getBounds().bottom;  
          
        canvas.drawColor(Color.TRANSPARENT);  
        paint.setColor(Color.GRAY);  
        paint.setAlpha((int) (0.5 * 255));  
        paint.setAntiAlias(true);  
        paint.setTextSize(sp2px(context, fontSize));  
        canvas.save();  
        canvas.rotate(degress);  
        float textWidth = paint.measureText(labels.get(0));  
        int index = 0;  
        for (int positionY = height / 10; positionY <= height; positionY += height / 10 + 80) {  
            float fromX = -width + (index++ % 2) * textWidth;  
            for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {  
                int spacing = 0;//间距  
                for (String label : labels) {  
                    canvas.drawText(label, positionX, positionY + spacing, paint);  
                    spacing = spacing + 50;  
                }  
                  
            }  
        }  
        canvas.restore();  
    }  
      
    @Override  
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {  
      
    }  
      
    @Override  
    public void setColorFilter(@Nullable ColorFilter colorFilter) {  
      
    }  
      
    @Override  
    public int getOpacity() {  
        return PixelFormat.UNKNOWN;  
    }  
      
    private static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  
}  
```  
  
2018-10-13 11:59:36 星期六  
