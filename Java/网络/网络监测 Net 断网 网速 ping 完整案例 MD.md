| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
***  
目录  
===  

- [监测网络](#监测网络)
	- [Activity](#Activity)
	- [布局](#布局)
	- [工具方法](#工具方法)
	- [自定义View](#自定义View)
	- [自定义View2](#自定义View2)
  
# 监测网络  
## Activity  
```java  
public class NetActivity extends Activity {  
      
    private static final String URL_DOWNLOAD = "https://qmuiteam.com/download/android/qmui_1.2.0.apk";  
      
    public static final int MESSAGE_WHAT_REFUSH_CURRENT_SPEED = 1;//刷新当前网速  
    public static final int MESSAGE_WHAT_REFUSH_AVE_SPEED = 2;//刷新平均网速  
    public static final int MESSAGE_WHAT_REFUSH_RESET = 3;//重置  
      
    private static final int DURATION_REFUSH_CURRENT_SPEED = 150;//过多久时间后刷新一次实时网速  
    private static final int DURATION_REFUSH_AVE_SPEED = 300;//过多久时间后刷新一次平均网速  
    private static final int DURATION_MAXCHECK = 5 * 1000;//整个测速过程允许的最大时间  
      
    private TextView tv_type, tv_now_speed, tv_ave_speed;  
    private DashboardView mDashboardView;  
    private Button btn;  
    private boolean flag = false;  
    private Handler handler = new StaticUiHandler(this);  
    private Disposable pingDisposable;  
      
    private long lastTotalRxBytes = 0;  
    private long lastTimeStamp = 0;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_net);  
          
        tv_type = findViewById(R.id.connection_type);  
        tv_now_speed = findViewById(R.id.now_speed);  
        tv_ave_speed = findViewById(R.id.ave_speed);  
        btn = findViewById(R.id.start_btn);  
        mDashboardView = findViewById(R.id.dashboard_view);  
          
        btn.setOnClickListener(arg0 -> checkNetSpeed());  
        checkNetSpeed();  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        flag = false;  
        handler.removeCallbacksAndMessages(null);  
        if (pingDisposable != null) {  
            pingDisposable.dispose();  
        }  
    }  
      
    /**  
     * 监测网速  
     */  
    private void checkNetSpeed() {  
        flag = true;  
        handler.postDelayed(this::reset, DURATION_MAXCHECK);  
        checkNetType();  
          
        btn.setText("正在ping百度ip...");  
        btn.setEnabled(false);  
    }  
      
    /**  
     * 监测网络类型  
     */  
    private void checkNetType() {  
        //先ping一下百度看能不能通  
        pingDisposable = Observable.create(emitter -> emitter.onNext(PingUtils.pingIpAddress(PingUtils.BAIDU_IP, 2)))  
            .subscribeOn(Schedulers.io())  
            .observeOn(AndroidSchedulers.mainThread())  
            .subscribe(isSuccess -> {  
                //通的话再检查是什么网，或者网络不可用  
                btn.setText("正在检查网络...");  
                if (isSuccess) {  
                    ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);  
                    if (manager != null) {  
                        NetworkInfo networkInfo = manager.getActiveNetworkInfo();  
                        if (networkInfo != null) {  
                            tv_type.setText(networkInfo.getTypeName());//网络类型  
                            btn.postDelayed(() -> btn.setText("正在测试网速..."), 500);  
                            new DownloadThread(this).start();  
                        } else {  
                            onFailed("网络不可用");  
                        }  
                    } else {  
                        onFailed("网络不可用");  
                    }  
                    //不同的话说明没网  
                } else {  
                    onFailed("无网络");  
                }  
            });  
    }  
      
    private void onFailed(String tips) {  
        tv_type.setText(tips);  
        Toast.makeText(NetActivity.this, tips, Toast.LENGTH_SHORT).show();  
        reset();  
    }  
      
    /**  
     * 复原到初始状态  
     */  
    private void reset() {  
        if (flag) {  
            flag = false;  
            btn.setText("重新测试");  
            btn.setEnabled(true);  
            handler.removeCallbacksAndMessages(null);  
        }  
    }  
      
    private void showCurrentNetSpeed() {  
        long nowTimeStamp = System.currentTimeMillis();  
        long totalRxBytes = TrafficStats.getTotalRxBytes();  
        if (TrafficStats.getUidRxBytes(getApplicationInfo().uid) != TrafficStats.UNSUPPORTED  
            && totalRxBytes > lastTotalRxBytes && nowTimeStamp - lastTimeStamp > 0) {  
            int speed = (int) ((totalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换成秒  
            lastTimeStamp = nowTimeStamp;  
            lastTotalRxBytes = TrafficStats.getTotalRxBytes();  
              
            tv_now_speed.setText(PingUtils.formatData(speed) + "/S");  
            Log.i("bqt", "当前网速：" + PingUtils.formatData(speed) + "/S");  
              
            mDashboardView.setRealTimeValue(speed * 1.0f / 1024 / 1024);  
        }  
    }  
      
    //region  Handler  
      
    static class StaticUiHandler extends Handler {  
        private SoftReference<NetActivity> mSoftReference;  
          
        StaticUiHandler(NetActivity activity) {  
            mSoftReference = new SoftReference<>(activity);  
        }  
          
        @SuppressLint("SetTextI18n")  
        @Override  
        public void handleMessage(Message msg) {  
            NetActivity activity = mSoftReference.get();  
            if (activity != null && msg != null) {  
                switch (msg.what) {  
                    case MESSAGE_WHAT_REFUSH_CURRENT_SPEED:  
                        activity.showCurrentNetSpeed();  
                        break;  
                    case MESSAGE_WHAT_REFUSH_AVE_SPEED:  
                        activity.tv_ave_speed.setText(PingUtils.formatData((int) msg.obj) + "/S");  
                        break;  
                    case MESSAGE_WHAT_REFUSH_RESET:  
                        activity.reset();  
                        break;  
                }  
            }  
        }  
    }  
    //endregion  
      
    //region  子线程  
      
    /**  
     * 下载资源，下载过程中，根据已下载长度、总长度、时间计算实时网速  
     */  
    static class DownloadThread extends Thread {  
        private SoftReference<NetActivity> mSoftReference;  
          
        DownloadThread(NetActivity activity) {  
            mSoftReference = new SoftReference<>(activity);  
        }  
          
        @Override  
        public void run() {  
            try {  
                URLConnection connection = new URL(URL_DOWNLOAD).openConnection();  
                InputStream inputStream = connection.getInputStream();  
                Log.i("bqt", "总长度：" + PingUtils.formatData(connection.getContentLength()));  
                  
                long startTime = System.currentTimeMillis();//开始时间  
                long usedTime;//已经使用的时长  
                long tempTime1 = 0, tempTime2 = 0;  
                  
                int aveSpeed;//当前网速和平均网速  
                int temLen, downloadLen = 0;//已下载的长度  
                byte[] buf = new byte[1024];  
                  
                NetActivity activity = mSoftReference.get();  
                while ((temLen = inputStream.read(buf)) != -1 && activity != null && activity.flag) {  
                    usedTime = System.currentTimeMillis() - startTime;//毫秒  
                    downloadLen += temLen;  
                      
                    //刷新当前网速  
                    if (System.currentTimeMillis() - tempTime1 > DURATION_REFUSH_CURRENT_SPEED) {  
                        tempTime1 = System.currentTimeMillis();  
                        activity.handler.sendMessage(Message.obtain(activity.handler, MESSAGE_WHAT_REFUSH_CURRENT_SPEED, 0));  
                    }  
                      
                    //刷新平均网速  
                    if (System.currentTimeMillis() - tempTime2 > DURATION_REFUSH_AVE_SPEED) {  
                        if (usedTime > 0) {//防止分母为零时报ArithmeticException  
                            tempTime2 = System.currentTimeMillis();  
                            aveSpeed = (int) (downloadLen / usedTime) * 1000;//平均网速，单位秒  
                            activity.handler.sendMessage(Message.obtain(activity.handler, MESSAGE_WHAT_REFUSH_AVE_SPEED, aveSpeed));  
                            Log.i("bqt", "平均网速：" + PingUtils.formatData(aveSpeed) + "/S   已下载长度：" + PingUtils.formatData(downloadLen));  
                        }  
                    }  
                }  
                  
                //重置  
                if (activity != null) {  
                    activity.handler.sendMessage(Message.obtain(activity.handler, MESSAGE_WHAT_REFUSH_RESET));  
                }  
                inputStream.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    //endregion  
}  
```  
  
## 布局  
```java  
<?xml version="1.0" encoding="utf-8"?>  
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"  
              xmlns:tools="http://schemas.android.com/tools"  
              android:layout_width="match_parent"  
              android:layout_height="match_parent"  
              android:background="#800F"  
              android:orientation="vertical">  
  
    <LinearLayout  
        android:layout_width="match_parent"  
        android:layout_height="wrap_content"  
        android:background="#000"  
        android:orientation="horizontal"  
        android:paddingTop="10dp"  
        android:paddingBottom="10dp">  
  
        <LinearLayout  
            android:layout_width="0dp"  
            android:layout_height="wrap_content"  
            android:layout_weight="1"  
            android:gravity="center"  
            android:orientation="vertical">  
  
            <TextView  
                android:layout_width="wrap_content"  
                android:layout_height="wrap_content"  
                android:singleLine="true"  
                android:text="连接方式"  
                android:textColor="#ffffff"/>  
  
            <TextView  
                android:id="@+id/connection_type"  
                android:layout_width="wrap_content"  
                android:layout_height="wrap_content"  
                android:layout_marginTop="5dp"  
                android:singleLine="true"  
                android:textColor="#fff"  
                tools:text="2M/S"/>  
        </LinearLayout>  
  
        <View  
            android:layout_width="1dp"  
            android:layout_height="match_parent"  
            android:background="#dbdbdb"/>  
  
        <LinearLayout  
            android:layout_width="0dp"  
            android:layout_height="wrap_content"  
            android:layout_weight="1"  
            android:gravity="center"  
            android:orientation="vertical">  
  
            <TextView  
                android:layout_width="wrap_content"  
                android:layout_height="wrap_content"  
                android:text="当前速度"  
                android:textColor="#ffffff"/>  
  
            <TextView  
                android:id="@+id/now_speed"  
                android:layout_width="wrap_content"  
                android:layout_height="wrap_content"  
                android:layout_marginTop="5dp"  
                android:textColor="#fff"  
                tools:text="2M/S"/>  
        </LinearLayout>  
  
        <View  
            android:layout_width="1dp"  
            android:layout_height="match_parent"  
            android:background="#dbdbdb"/>  
  
        <LinearLayout  
            android:layout_width="0dp"  
            android:layout_height="wrap_content"  
            android:layout_weight="1"  
            android:gravity="center"  
            android:orientation="vertical">  
  
            <TextView  
                android:layout_width="wrap_content"  
                android:layout_height="wrap_content"  
                android:text="平均速度"  
                android:textColor="#ffffff"/>  
  
            <TextView  
                android:id="@+id/ave_speed"  
                android:layout_width="wrap_content"  
                android:layout_height="wrap_content"  
                android:layout_marginTop="5dp"  
                android:textColor="#fff"  
                tools:text="2M/S"/>  
        </LinearLayout>  
    </LinearLayout>  
  
    <rx.test.bqt.com.rxjavademo.net.DashboardView  
        android:id="@+id/dashboard_view"  
        android:layout_width="match_parent"  
        android:layout_height="wrap_content"  
        android:layout_gravity="center_horizontal"  
        android:layout_marginTop="20dp"/>  
  
    <Button  
        android:id="@+id/start_btn"  
        android:layout_width="match_parent"  
        android:layout_height="wrap_content"  
        android:layout_gravity="center_horizontal"  
        android:text="开始测试"/>  
</LinearLayout>  
```  
  
## 工具方法  
```java  
public class PingUtils {  
    public static final String BAIDU_IP = "119.75.217.109";  
    public static final String APPLE_IP = "http://captive.apple.com";  
      
    /**  
     * 格式化文件大小  
     *  
     * @param size 文件大小  
     */  
    public static String formatData(long size) {  
        DecimalFormat formater = new DecimalFormat("####.00");  
        if (size < 1024) return size + "B";  
        else if (size < Math.pow(1024, 2)) return formater.format(size * Math.pow(1024, -1)) + "KB";  
        else if (size < Math.pow(1024, 3)) return formater.format(size * Math.pow(1024, -2)) + "MB";  
        else if (size < Math.pow(1024, 4)) return formater.format(size * Math.pow(1024, -3)) + "GB";  
        else return "";  
    }  
      
    /**  
     * ping返回true表示ping通，false表示没有ping通  
     * 所谓没有ping通是指数据包没有返回，也就是客户端没有及时收到ping的返回包因此返回false  
     * 但是要是网络不可用则ping的时候也会返回true，因为ping指定有成功结束，只是ping的返回包是失败的数据包而不是成功的数据包  
     * 所以准确的说返回true表示ping指定返回完成，false表示没收到ping的返回数据  
     * 以上方法是阻塞的，android系统默认等待ping的超时是10s，可以自定义超时时间  
     * 也不用担心方法一直被阻塞，如果ping超时就会自动返回，不必担心方法被阻塞导致无法运行下面的代码  
     * 网上的一些ping的实现说方法会被一直阻塞，实际上是他们ping的命令没写好，以及使用io被阻塞了  
     */  
    public static boolean ping(String host, int pingCount) {  
        Process process = null;  
        BufferedReader successReader = null;  
        //String command = "/system/bin/ping -c " + pingCount + " -w 5 " + host;//-c 是指ping的次数，-w是指超时时间，单位为s。  
        String command = "ping -c " + pingCount + " " + host;  
        boolean isSuccess = false;  
        try {  
            Log.i("bqt", "【start ping，command：" + command + "】");  
            process = Runtime.getRuntime().exec(command);  
            if (process == null) {  
                Log.i("bqt", "【ping fail：process is null.】");  
                return false;  
            }  
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));  
            String line;  
            while ((line = successReader.readLine()) != null) {  
                Log.i("bqt", line);  
            }  
            int status = process.waitFor();  
            if (status == 0) {  
                Log.i("bqt", "【exec cmd success】");  
                isSuccess = true;  
            } else {  
                Log.i("bqt", "【exec cmd fail】");  
                isSuccess = false;  
            }  
            Log.i("bqt", "【exec finished】");  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        } finally {  
            Log.i("bqt", "【ping exit】");  
            if (process != null) {  
                process.destroy();  
            }  
            if (successReader != null) {  
                try {  
                    successReader.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return isSuccess;  
    }  
      
    /**  
     * 简易版  
     */  
    public static boolean pingIpAddress(String host, int pingCount) {  
        try {  
            String command = "ping -c " + pingCount + " -w 5 " + host;//-c 是指ping的次数，-w是指超时时间，单位为s。  
            Log.i("bqt", "【start ping，command：" + command + "】");  
            Process process = Runtime.getRuntime().exec(command);  
            //其中参数-c 1是指ping的次数为1次，-w是指超时时间单位为s。  
            boolean isSuccess = process != null && process.waitFor() == 0;//status 等于0的时候表示网络可用，status等于2时表示当前网络不可用  
            Log.i("bqt", "【end ping，isSuccess：" + isSuccess + "】");  
            return isSuccess;  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
        return false;  
    }  
}  
```  
  
## 自定义View  
[dashboardviewdemo](https://github.com/woxingxiao/DashboardView/tree/master/app/src/main/java/com/xw/sample/dashboardviewdemo)  
  
```java  
public class DashboardView extends View {  
      
    private static final long ANIMATION_DURATION = 130; //动画时长，注意最好不要在一个动画周期内设置多个动画  
    private static final int M_START_ANGLE = 135; // 起始角度  
    private static final int M_SWEEP_ANGLE = 270; // 绘制角度  
    private static final float mMin = 0; // 最小值  
    private static final float mMax = 5; // 最大值，对应5MB/S  
    private static final int M_SECTION = 10; // 值域（mMax-mMin）等分份数  
    private static final int M_PORTION = 10; // 一个mSection等分份数  
    private static final String M_HEADER_TEXT = "MB/S"; // 表头  
    private static final int SWEEP_ANGLE_COLOR = 0x880000ff;//圆弧颜色  
    private static final int REAL_TIME_VALUE_COLOR = 0xffff0000;//实时读数的颜色  
    private static final boolean IS_SHOW_VALUE = true; // 是否显示实时读数  
    private int mRadius; // 扇形半径  
    private float realTimeValue = mMin; // 实时读数  
    private int mStrokeWidth; // 画笔宽度  
    private int mLength1; // 长刻度的相对圆弧的长度  
    private int mLength2; // 刻度读数顶部的相对圆弧的长度  
    private int mPLRadius; // 指针长半径  
    private int mPSRadius; // 指针短半径  
      
    private int mPadding;  
    private float mCenterX, mCenterY; // 圆心坐标  
    private Paint mPaint;  
    private RectF mRectFArc;  
    private Path mPath;  
    private RectF mRectFInnerArc;  
    private Rect mRectText;  
    private String[] mTexts;  
      
    public DashboardView(Context context) {  
        this(context, null);  
    }  
      
    public DashboardView(Context context, AttributeSet attrs) {  
        this(context, attrs, 0);  
    }  
      
    public DashboardView(Context context, AttributeSet attrs, int defStyleAttr) {  
        super(context, attrs, defStyleAttr);  
          
        init();  
    }  
      
    private void init() {  
        mStrokeWidth = dp2px(1);  
        mLength1 = dp2px(8) + mStrokeWidth;  
        mLength2 = mLength1 + dp2px(2);  
        mPSRadius = dp2px(10);  
          
        mPaint = new Paint();  
        mPaint.setAntiAlias(true);  
        mPaint.setStrokeCap(Paint.Cap.ROUND);  
          
        mRectFArc = new RectF();  
        mPath = new Path();  
        mRectFInnerArc = new RectF();  
        mRectText = new Rect();  
          
        mTexts = new String[M_SECTION + 1]; // 需要显示mSection + 1个刻度读数  
        for (int i = 0; i < mTexts.length; i++) {  
            float n = (mMax - mMin) / M_SECTION;  
            mTexts[i] = String.valueOf(mMin + i * n);  
        }  
    }  
      
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
          
        mPadding = Math.max(  
                Math.max(getPaddingLeft(), getPaddingTop()),  
                Math.max(getPaddingRight(), getPaddingBottom())  
        );  
        setPadding(mPadding, mPadding, mPadding, mPadding);  
          
        int width = resolveSize(dp2px(200), widthMeasureSpec);  
        mRadius = (width - mPadding * 2 - mStrokeWidth * 2) / 2;  
          
        mPaint.setTextSize(sp2px(16));  
        if (IS_SHOW_VALUE) { // 显示实时读数，View高度增加字体高度3倍  
            mPaint.getTextBounds("0", 0, "0".length(), mRectText);  
        } else {  
            mPaint.getTextBounds("0", 0, 0, mRectText);  
        }  
        // 由半径+指针短半径+实时读数文字高度确定的高度  
        int height1 = mRadius + mStrokeWidth * 2 + mPSRadius + mRectText.height() * 3;  
        // 由起始角度确定的高度  
        float[] point1 = getCoordinatePoint(mRadius, M_START_ANGLE);  
        // 由结束角度确定的高度  
        float[] point2 = getCoordinatePoint(mRadius, M_START_ANGLE + M_SWEEP_ANGLE);  
        // 取最大值  
        int max = (int) Math.max(height1, Math.max(point1[1] + mRadius + mStrokeWidth * 2, point2[1] + mRadius + mStrokeWidth * 2));  
        setMeasuredDimension(width, max + getPaddingTop() + getPaddingBottom());  
          
        mCenterX = mCenterY = getMeasuredWidth() / 2f;  
        mRectFArc.set(getPaddingLeft() + mStrokeWidth,  
                getPaddingTop() + mStrokeWidth,  
                getMeasuredWidth() - getPaddingRight() - mStrokeWidth,  
                getMeasuredWidth() - getPaddingBottom() - mStrokeWidth);  
          
        mPaint.setTextSize(sp2px(10));  
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);  
        mRectFInnerArc.set(getPaddingLeft() + mLength2 + mRectText.height(),  
                getPaddingTop() + mLength2 + mRectText.height(),  
                getMeasuredWidth() - getPaddingRight() - mLength2 - mRectText.height(),  
                getMeasuredWidth() - getPaddingBottom() - mLength2 - mRectText.height());  
          
        mPLRadius = mRadius - (mLength2 + mRectText.height() + dp2px(5));  
    }  
      
    private float tempRealTimeValue;  
      
    private int dp2px(int dp) {  
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,  
                Resources.getSystem().getDisplayMetrics());  
    }  
      
    private int sp2px(int sp) {  
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,  
                Resources.getSystem().getDisplayMetrics());  
    }  
      
    public float[] getCoordinatePoint(int radius, float angle) {  
        float[] point = new float[2];  
          
        double arcAngle = Math.toRadians(angle); //将角度转换为弧度  
        if (angle < 90) {  
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);  
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);  
        } else if (angle == 90) {  
            point[0] = mCenterX;  
            point[1] = mCenterY + radius;  
        } else if (angle > 90 && angle < 180) {  
            arcAngle = Math.PI * (180 - angle) / 180.0;  
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);  
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);  
        } else if (angle == 180) {  
            point[0] = mCenterX - radius;  
            point[1] = mCenterY;  
        } else if (angle > 180 && angle < 270) {  
            arcAngle = Math.PI * (angle - 180) / 180.0;  
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);  
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);  
        } else if (angle == 270) {  
            point[0] = mCenterX;  
            point[1] = mCenterY - radius;  
        } else {  
            arcAngle = Math.PI * (360 - angle) / 180.0;  
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);  
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);  
        }  
          
        return point;  
    }  
      
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
          
        //画圆弧  
        mPaint.setStyle(Paint.Style.STROKE);  
        mPaint.setStrokeWidth(mStrokeWidth);  
        mPaint.setColor(SWEEP_ANGLE_COLOR);  
        canvas.drawArc(mRectFArc, M_START_ANGLE, M_SWEEP_ANGLE, false, mPaint);  
          
        //画长刻度。画好起始角度的一条刻度后通过canvas绕着原点旋转来画剩下的长刻度  
        double cos = Math.cos(Math.toRadians(M_START_ANGLE - 180));  
        double sin = Math.sin(Math.toRadians(M_START_ANGLE - 180));  
        float x0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - cos));  
        float y0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - sin));  
        float x1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * cos);  
        float y1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * sin);  
          
        canvas.save();  
        canvas.drawLine(x0, y0, x1, y1, mPaint);  
        float angle = M_SWEEP_ANGLE * 1f / M_SECTION;  
        for (int i = 0; i < M_SECTION; i++) {  
            canvas.rotate(angle, mCenterX, mCenterY);  
            canvas.drawLine(x0, y0, x1, y1, mPaint);  
        }  
        canvas.restore();  
          
        //画短刻度。同样采用canvas的旋转原理  
        canvas.save();  
        mPaint.setStrokeWidth(1);  
        float x2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1 / 2f) * cos);  
        float y2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1 / 2f) * sin);  
        canvas.drawLine(x0, y0, x2, y2, mPaint);  
        angle = M_SWEEP_ANGLE * 1f / (M_SECTION * M_PORTION);  
        for (int i = 1; i < M_SECTION * M_PORTION; i++) {  
            canvas.rotate(angle, mCenterX, mCenterY);  
            if (i % M_PORTION == 0) { // 避免与长刻度画重合  
                continue;  
            }  
            canvas.drawLine(x0, y0, x2, y2, mPaint);  
        }  
        canvas.restore();  
          
        //画长刻度读数。添加一个圆弧path，文字沿着path绘制  
        mPaint.setTextSize(sp2px(10));  
        mPaint.setTextAlign(Paint.Align.LEFT);  
        mPaint.setStyle(Paint.Style.FILL);  
        for (int i = 0; i < mTexts.length; i++) {  
            mPaint.getTextBounds(mTexts[i], 0, mTexts[i].length(), mRectText);  
            // 粗略把文字的宽度视为圆心角2*θ对应的弧长，利用弧长公式得到θ，下面用于修正角度  
            float degree = (float) (180 * mRectText.width() / 2 /  
                    (Math.PI * (mRadius - mLength2 - mRectText.height())));  
              
            mPath.reset();  
            mPath.addArc(mRectFInnerArc,  
                    M_START_ANGLE + i * (M_SWEEP_ANGLE / M_SECTION) - degree, // 正起始角度减去θ使文字居中对准长刻度  
                    M_SWEEP_ANGLE);  
            canvas.drawTextOnPath(mTexts[i], mPath, 0, 0, mPaint);  
        }  
          
        //画表头。没有表头就不画  
        if (!TextUtils.isEmpty(M_HEADER_TEXT)) {  
            mPaint.setTextSize(sp2px(14));  
            mPaint.setTextAlign(Paint.Align.CENTER);  
            mPaint.getTextBounds(M_HEADER_TEXT, 0, M_HEADER_TEXT.length(), mRectText);  
            canvas.drawText(M_HEADER_TEXT, mCenterX, mCenterY / 2f + mRectText.height(), mPaint);  
        }  
          
        //画指针  
        float degree = M_START_ANGLE + M_SWEEP_ANGLE * (realTimeValue - mMin) / (mMax - mMin); // 指针与水平线夹角  
        int d = dp2px(5); // 指针由两个等腰三角形构成，d为共底边长的一半  
        mPath.reset();  
        float[] p1 = getCoordinatePoint(d, degree - 90);  
        mPath.moveTo(p1[0], p1[1]);  
        float[] p2 = getCoordinatePoint(mPLRadius, degree);  
        mPath.lineTo(p2[0], p2[1]);  
        float[] p3 = getCoordinatePoint(d, degree + 90);  
        mPath.lineTo(p3[0], p3[1]);  
        float[] p4 = getCoordinatePoint(mPSRadius, degree - 180);  
        mPath.lineTo(p4[0], p4[1]);  
        mPath.close();  
        canvas.drawPath(mPath, mPaint);  
          
        //画指针围绕的镂空圆心  
        mPaint.setColor(Color.WHITE);  
        canvas.drawCircle(mCenterX, mCenterY, dp2px(2), mPaint);  
          
        //画实时度数值  
        if (IS_SHOW_VALUE) {  
            mPaint.setTextSize(sp2px(18));  
            mPaint.setTextAlign(Paint.Align.CENTER);  
            mPaint.setColor(REAL_TIME_VALUE_COLOR);  
            String value = String.format(Locale.getDefault(), "%.2f", realTimeValue) /*+ " MB/S"*/;  
            mPaint.getTextBounds(value, 0, value.length(), mRectText);  
            canvas.drawText(value, mCenterX, mCenterY + mPSRadius + mRectText.height() * 2, mPaint);  
        }  
    }  
      
    public float getRealTimeValue() {  
        return realTimeValue;  
    }  
      
    public void setRealTimeValue(final float value) {  
        if (this.realTimeValue == value || value < mMin || value > mMax) {  
            return;  
        }  
        this.tempRealTimeValue = this.realTimeValue;  
          
        Animation anim = new Animation() {  
            @Override  
            protected void applyTransformation(float interpolatedTime, Transformation t) {  
                //开始动画以后applyTransformation函数会自动调用，这里的interpolatedTime是 0-1 区间的变量，反映动画的进度  
                super.applyTransformation(interpolatedTime, t);  
                realTimeValue = tempRealTimeValue + interpolatedTime * (value - tempRealTimeValue);  
                Log.i("bqt", "interpolatedTime=" + interpolatedTime + "   realTimeValue=" + realTimeValue);  
                postInvalidate();  
            }  
        };  
        anim.setDuration(ANIMATION_DURATION);  
        this.startAnimation(anim);  
          
        //不知为什么不能用下面这种动画，感觉应该也是可以的  
        /*ObjectAnimator anim = ObjectAnimator.ofFloat(this, "realTimeValue", value)  
                .setDuration(ANIMATION_DURATION);  
        anim.addUpdateListener(animation -> {  
            realTimeValue = (Float) animation.getAnimatedValue();  
            Log.i("bqt", "realTimeValue=" + realTimeValue);  
            postInvalidate();  
        });  
        anim.start();*/  
    }  
}  
```  
  
## 自定义View2  
```java  
public class DashboardView2 extends View {  
      
    private static final long ANIMATION_DURATION = 130; //动画时长，注意最好不要在一个动画周期内设置多个动画  
      
    private static final int M_START_ANGLE = 135; // 起始角度  
    private static final int M_SWEEP_ANGLE = 270; // 绘制角度  
      
    private static final float M_MIN = 0; // 最小值  
    private static final float M_MAX = 5; // 最大值，对应5MB/S  
    private static final int M_SECTION = 10; // 值域（M_MAX-M_MIN）等分份数，对应1-5M/S  
    private static final int M_PORTION = 5; // 一个mSection等分份数  
      
    private static final int DEFAULT_ANGLE_COLOR = 0x22000000;//默认圆弧颜色  
    private static final int CURRENT_ANGLE_COLOR = 0x8800ffff;//已达到部分圆弧颜色  
    private static final int DEFAULT_TEXT_COLOR = 0xaa000000;//文字颜色  
      
    private static final int STROKE_TEXT_WIDTH_SP1 = 12;//刻度读数文字大小，sp单位  
    private String mHeaderText = ""; // 表头文字  
    private static final int STROKE_TEXT_WIDTH_SP2 = 16;//表头文字大小，sp单位  
    private static final boolean IS_SHOW_VALUE = true; // 是否显示实时读数  
    private static final int STROKE_TEXT_WIDTH_SP3 = 13;//实时度数值文字大小，sp单位  
      
    private static final int STROKE_WIDTH_DP = 2;//圆弧画笔宽度，刻度宽，dp单位  
    private static final int M_LENGTH_1_DP = 22; // 短刻度的相对圆弧的长度，dp单位  
    private static final int M_LENGTH_2_DP = 30; // 长刻度的相对圆弧的长度，dp单位  
    private static final int M_LENGTH_3_DP = 42; // 刻度读数顶部的相对圆弧的长度  
      
    private int mRadius; // 扇形半径  
    private float realTimeValue = 0; // 实时读数  
      
    private int mPadding;  
    private float mCenterX, mCenterY; // 圆心坐标  
    private Paint mPaint;  
    private RectF mRectFArc;  
    private Path mPath;  
    private RectF mRectFInnerArc;  
    private Rect mRectText;  
    private String[] mTexts;  
      
    public DashboardView2(Context context) {  
        this(context, null);  
    }  
      
    public DashboardView2(Context context, AttributeSet attrs) {  
        this(context, attrs, 0);  
    }  
      
    public DashboardView2(Context context, AttributeSet attrs, int defStyleAttr) {  
        super(context, attrs, defStyleAttr);  
          
        init();  
    }  
      
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        int mStrokeWidth = dp2px(STROKE_WIDTH_DP);  
        mPaint.setStyle(Paint.Style.STROKE);  
        mPaint.setStrokeWidth(mStrokeWidth);  
        mPaint.setColor(CURRENT_ANGLE_COLOR);  
        float degree = M_SWEEP_ANGLE * (realTimeValue - M_MIN) / (M_MAX - M_MIN); // 指针与水平线夹角  
          
        //画圆弧  
        /*canvas.drawArc(mRectFArc, M_START_ANGLE, M_SWEEP_ANGLE, false, mPaint);*/  
          
        //画长刻度。画好起始角度的一条刻度后通过canvas绕着原点旋转来画剩下的长刻度  
        double cos = Math.cos(Math.toRadians(M_START_ANGLE - 180));  
        double sin = Math.sin(Math.toRadians(M_START_ANGLE - 180));  
        float x0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - cos));  
        float y0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - sin));  
        float x1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - dp2px(M_LENGTH_2_DP)) * cos);  
        float y1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - dp2px(M_LENGTH_2_DP)) * sin);  
          
        canvas.save();  
        canvas.drawLine(x0, y0, x1, y1, mPaint);  
        float angle = M_SWEEP_ANGLE * 1f / M_SECTION;  
        //int tem = (int) (degree / M_SECTION);  
        int tem = (int) (degree / angle);  
        Log.i("bqt", "degree=" + degree + "   tem=" + tem);  
        for (int i = 0; i < M_SECTION; i++) {  
            if (i < tem) {  
                mPaint.setColor(CURRENT_ANGLE_COLOR);  
            } else {  
                mPaint.setColor(DEFAULT_ANGLE_COLOR);  
            }  
            canvas.rotate(angle, mCenterX, mCenterY);  
            canvas.drawLine(x0, y0, x1, y1, mPaint);  
        }  
        canvas.restore();  
          
        //画短刻度。同样采用canvas的旋转原理  
        canvas.save();  
        float x2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - dp2px(M_LENGTH_1_DP)) * cos);  
        float y2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - dp2px(M_LENGTH_1_DP)) * sin);  
        //canvas.drawLine(x0, y0, x2, y2, mPaint);  
        angle = M_SWEEP_ANGLE * 1f / (M_SECTION * M_PORTION);  
        tem = (int) (degree / angle);  
        for (int i = 1; i < M_SECTION * M_PORTION; i++) {  
            canvas.rotate(angle, mCenterX, mCenterY);  
            if (i % M_PORTION == 0) { // 避免与长刻度画重合  
                continue;  
            }  
            if (i < tem) {  
                mPaint.setColor(CURRENT_ANGLE_COLOR);  
            } else {  
                mPaint.setColor(DEFAULT_ANGLE_COLOR);  
            }  
            canvas.drawLine(x0, y0, x2, y2, mPaint);  
        }  
        canvas.restore();  
          
        //画长刻度读数。添加一个圆弧path，文字沿着path绘制  
        mPaint.setColor(DEFAULT_TEXT_COLOR);  
        mPaint.setTextSize(sp2px(STROKE_TEXT_WIDTH_SP1));  
        mPaint.setTextAlign(Paint.Align.LEFT);  
        mPaint.setStyle(Paint.Style.FILL);  
        for (int i = 0; i < mTexts.length; i++) {  
            mPaint.getTextBounds(mTexts[i], 0, mTexts[i].length(), mRectText);  
            // 粗略把文字的宽度视为圆心角2*θ对应的弧长，利用弧长公式得到θ，下面用于修正角度  
            float tempDegree = (float) (180 * mRectText.width() / 2 /  
                    (Math.PI * (mRadius - dp2px(M_LENGTH_3_DP) - mRectText.height())));  
              
            mPath.reset();  
            mPath.addArc(mRectFInnerArc,  
                    M_START_ANGLE + i * (M_SWEEP_ANGLE / M_SECTION) - tempDegree, // 正起始角度减去θ使文字居中对准长刻度  
                    M_SWEEP_ANGLE);  
            canvas.drawTextOnPath(mTexts[i], mPath, 0, 0, mPaint);  
        }  
          
        //画表头。没有表头就不画  
        if (!TextUtils.isEmpty(mHeaderText)) {  
            mPaint.setTextSize(sp2px(STROKE_TEXT_WIDTH_SP2));  
            mPaint.setTextAlign(Paint.Align.CENTER);  
            mPaint.getTextBounds(mHeaderText, 0, mHeaderText.length(), mRectText);  
            canvas.drawText(mHeaderText, mCenterX, mCenterY, mPaint);  
        }  
          
        //画实时度数值  
        if (IS_SHOW_VALUE) {  
            mPaint.setTextSize(sp2px(STROKE_TEXT_WIDTH_SP3));  
            mPaint.setTextAlign(Paint.Align.CENTER);  
            String value = String.format(Locale.getDefault(), "%.2f", realTimeValue) + " MB/S";  
            mPaint.getTextBounds(value, 0, value.length(), mRectText);  
            canvas.drawText(value, mCenterX, mCenterY + mRectText.height() * 2, mPaint);  
        }  
    }  
      
    private void init() {  
        mPaint = new Paint();  
        mPaint.setAntiAlias(true);  
        mPaint.setStrokeCap(Paint.Cap.ROUND);  
          
        mRectFArc = new RectF();  
        mPath = new Path();  
        mRectFInnerArc = new RectF();  
        mRectText = new Rect();  
          
        mTexts = new String[M_SECTION + 1]; // 需要显示mSection + 1个刻度读数  
        for (int i = 0; i < mTexts.length; i++) {  
            float n = (M_MAX - M_MIN) / M_SECTION;  
            mTexts[i] = String.valueOf(M_MIN + i * n);  
        }  
    }  
      
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
        int mStrokeWidth = dp2px(STROKE_WIDTH_DP);  
          
        mPadding = Math.max(Math.max(getPaddingLeft(), getPaddingTop()),  
                Math.max(getPaddingRight(), getPaddingBottom()));  
        setPadding(mPadding, mPadding, mPadding, mPadding);  
          
        int width = resolveSize(dp2px(200), widthMeasureSpec);  
          
        mRadius = (width - mPadding * 2 - mStrokeWidth * 2) / 2;  
          
        mPaint.setTextSize(sp2px(16));  
        if (IS_SHOW_VALUE) { // 显示实时读数，View高度增加字体高度3倍  
            mPaint.getTextBounds("0", 0, "0".length(), mRectText);  
        } else {  
            mPaint.getTextBounds("0", 0, 0, mRectText);  
        }  
        // 由半径+指针短半径+实时读数文字高度确定的高度  
        int height1 = mRadius + mStrokeWidth * 2 + mRectText.height() * 3;  
        // 由起始角度确定的高度  
        float[] point1 = getCoordinatePoint(mRadius, M_START_ANGLE);  
        // 由结束角度确定的高度  
        float[] point2 = getCoordinatePoint(mRadius, M_START_ANGLE + M_SWEEP_ANGLE);  
        // 取最大值  
        int max = (int) Math.max(height1, Math.max(point1[1] + mRadius + mStrokeWidth * 2, point2[1] + mRadius + mStrokeWidth * 2));  
        setMeasuredDimension(width, max + getPaddingTop() + getPaddingBottom());  
          
        mCenterX = mCenterY = getMeasuredWidth() / 2f;  
        mRectFArc.set(getPaddingLeft() + mStrokeWidth,  
                getPaddingTop() + mStrokeWidth,  
                getMeasuredWidth() - getPaddingRight() - mStrokeWidth,  
                getMeasuredWidth() - getPaddingBottom() - mStrokeWidth);  
          
        mPaint.setTextSize(sp2px(10));  
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);  
        mRectFInnerArc.set(getPaddingLeft() + dp2px(M_LENGTH_3_DP) + mRectText.height(),  
                getPaddingTop() + dp2px(M_LENGTH_3_DP) + mRectText.height(),  
                getMeasuredWidth() - getPaddingRight() - dp2px(M_LENGTH_3_DP) - mRectText.height(),  
                getMeasuredWidth() - getPaddingBottom() - dp2px(M_LENGTH_3_DP) - mRectText.height());  
    }  
      
    private float tempRealTimeValue;  
      
    private int dp2px(int dp) {  
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());  
    }  
      
    private int sp2px(int sp) {  
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());  
    }  
      
    public float[] getCoordinatePoint(int radius, float angle) {  
        float[] point = new float[2];  
          
        double arcAngle = Math.toRadians(angle); //将角度转换为弧度  
        if (angle < 90) {  
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);  
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);  
        } else if (angle == 90) {  
            point[0] = mCenterX;  
            point[1] = mCenterY + radius;  
        } else if (angle > 90 && angle < 180) {  
            arcAngle = Math.PI * (180 - angle) / 180.0;  
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);  
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);  
        } else if (angle == 180) {  
            point[0] = mCenterX - radius;  
            point[1] = mCenterY;  
        } else if (angle > 180 && angle < 270) {  
            arcAngle = Math.PI * (angle - 180) / 180.0;  
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);  
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);  
        } else if (angle == 270) {  
            point[0] = mCenterX;  
            point[1] = mCenterY - radius;  
        } else {  
            arcAngle = Math.PI * (360 - angle) / 180.0;  
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);  
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);  
        }  
          
        return point;  
    }  
      
    public void setHeaderText(String mHeaderText) {  
        this.mHeaderText = mHeaderText;  
    }  
      
    public void setRealTimeValue(final float value) {  
        this.realTimeValue = value;  
        postInvalidate();  
    }  
      
    public void setRealTimeValueWithAnimation(final float value) {  
        if (this.realTimeValue == value || value < M_MIN || value > M_MAX) {  
            return;  
        }  
        this.tempRealTimeValue = this.realTimeValue;  
          
        Animation anim = new Animation() {  
            @Override  
            protected void applyTransformation(float interpolatedTime, Transformation t) {  
                //开始动画以后applyTransformation函数会自动调用，这里的interpolatedTime是 0-1 区间的变量，反映动画的进度  
                super.applyTransformation(interpolatedTime, t);  
                realTimeValue = tempRealTimeValue + interpolatedTime * (value - tempRealTimeValue);  
                //Log.i("bqt", "interpolatedTime=" + interpolatedTime + "   realTimeValue=" + realTimeValue);  
                postInvalidate();  
            }  
        };  
        anim.setDuration(ANIMATION_DURATION);  
        this.startAnimation(anim);  
    }  
}  
```  
  
2018-4-8  
