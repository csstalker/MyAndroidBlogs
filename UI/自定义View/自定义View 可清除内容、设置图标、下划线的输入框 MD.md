| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
自定义View 可清除内容、设置图标、下划线的输入框 MD    
***  
目录  
===  

- [案例](# 案例)
- [ClearEditText](# clearedittext)
- [下划线](# 下划线)
  
# 案例  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181013/04Ehg00F6c.png?imageslim)  
  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181013/LL6a8H35IA.png?imageslim)  
  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181013/926Kleaf06.png?imageslim)  
  
```xml  
<com.bqt.lock.ClearEditText  
    android:layout_width="match_parent"  
    android:layout_height="50dp"  
    android:background="@null"  
    android:drawableBottom="@drawable/shape_line"  
    android:drawableLeft="@drawable/icon"  
    android:maxLines="2"  
    android:textSize="14sp"/>  
  
<com.bqt.lock.ClearEditText  
    android:layout_width="match_parent"  
    android:layout_height="30dp"  
    android:layout_marginTop="10dp"  
    android:background="#00f"  
    android:drawableBottom="@drawable/shape_line"  
    android:drawableLeft="@drawable/icon"  
    android:drawablePadding="10dp"  
    android:maxLength="10"  
    android:maxLines="1"  
    android:textSize="13sp"/>  
```  
  
# ClearEditText  
```java  
public class ClearEditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {  
      
    private Drawable leftIconDrawable;//左侧的一般为输入内容的指示图标  
    private Drawable bottomLineDrawable;//右侧为清空内容的图标  
    private Drawable rightClearDrawable;//下侧为下划线  
    private int size;  
      
    public ClearEditText(Context context) {  
        this(context, null);  
    }  
      
    public ClearEditText(Context context, AttributeSet attrs) {  
        this(context, attrs, android.R.attr.editTextStyle);  
    }  
      
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init(context);  
    }  
      
    private void init(Context context) {  
        // 获取EditText的DrawableRight，假如没有设置我们就使用默认的图片  
        rightClearDrawable = getCompoundDrawables()[2] == null ? getResources().getDrawable(R.drawable.icon_clean) : getCompoundDrawables()[2];  
        leftIconDrawable = getCompoundDrawables()[0];  
        bottomLineDrawable = getCompoundDrawables()[3];  
        setOnFocusChangeListener(this);  
        addTextChangedListener(this);  
    }  
      
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
        //如果设置了【drawableBottom】，则【drawableRight】的最大高度为【控件的高度 - drawableBottom的高度 - drawablePadding】  
        int usedHeight = bottomLineDrawable != null ? bottomLineDrawable.getIntrinsicHeight() * 2 + getCompoundDrawablePadding() : 0;  
        size = getMeasuredHeight() - usedHeight;  
        Log.i("bqt", "【Drawable的宽高为】" + size);  
        rightClearDrawable.setBounds(0, 0, size, size);  
        if (leftIconDrawable != null) {  
            leftIconDrawable.setBounds(0, 0, size, size);  
        }  
        setClearIconVisible(getText().length() > 0 && hasFocus());  
    }  
      
    /**  
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -  
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑  
     */  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        if (getCompoundDrawables()[2] != null  
                && event.getAction() == MotionEvent.ACTION_UP  
                && event.getX() >= (getWidth() - getPaddingRight() - size)  
                && (event.getX() <= (getWidth() - getPaddingRight()))) {  
            this.setText("");  
        }  
        return super.onTouchEvent(event);  
    }  
      
    /**  
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏  
     */  
    @Override  
    public void onFocusChange(View v, boolean hasFocus) {  
        if (hasFocus) {  
            setClearIconVisible(getText().length() > 0);  
        } else {  
            setClearIconVisible(false);  
        }  
    }  
      
    /**  
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去  
     */  
    protected void setClearIconVisible(boolean visible) {  
        setCompoundDrawables(leftIconDrawable, getCompoundDrawables()[1], visible ? rightClearDrawable : null, bottomLineDrawable);  
    }  
      
    @Override  
    public void onTextChanged(CharSequence s, int start, int count, int after) {  
      
    }  
      
    @Override  
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
      
    }  
      
    @Override  
    public void afterTextChanged(Editable s) {  
        setClearIconVisible(s.length() > 0 && hasFocus());  
    }  
}  
```  
  
# 下划线  
这种方式比单独加一个控件显示分割线更优雅一些  
  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<shape xmlns:android="http://schemas.android.com/apk/res/android">  
    <solid android:color="#D4D4D4"/>  
    <size  
        android:width="1000dp"  
        android:height="1dp"/>  
</shape>  
```  
  
2018-10-13  
