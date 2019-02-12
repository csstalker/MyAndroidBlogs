| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
AppWidgetProvider 桌面插件 Widget 广播  
***  
目录  
===  

- [简介](#简介)
- [AppWidget 框架类](#appwidget-框架类)
- [AppWidgetManger 常用 API](#appwidgetmanger-常用-api)
- [开发步骤](#开发步骤)
	- [定义小工具界面](#定义小工具界面)
	- [定义小工具配置信息](#定义小工具配置信息)
	- [定义小工具类](#定义小工具类)
	- [声明小工具类](#声明小工具类)
	- [与 Activity 的交互](#与-activity-的交互)
	- [工具类](#工具类)
  
# 简介  
[Demo](https://github.com/baiqiantao/AppWidgetProvider.git)  
效果：  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181116/93Jd3AJBe3.png?imageslim)  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181116/LaEkbm0aCE.png?imageslim)  
  
AppWidgetProvider是android中提供的用于实现`桌面小工具`的类，其本质是一个广播，即`BroadcastReceiver`。  
```java  
public class AppWidgetProvider extends BroadcastReceiver   
```  
所以，在实际的使用中，把AppWidgetProvider当成一个BroadcastReceiver就可以了。  
  
App Widgets是一个显示在别的application中（比如显示在桌面程序）的微型application views，并且定期接受更新。这个views在用户界面被叫Widgets，你可以发布一个自己应用的Widget。Widget的application称为App Widget host。  
> App Widgets are `miniature` application views that can be `embedded` in other applications (such as the Home screen) and receive periodic updates. These views are referred to as `Widgets` in the user interface, and you can publish one with an App Widget provider. An application component that is able to hold other App Widgets is called an App Widget host.   
  
# AppWidget 框架类  
- `AppWidgetProvider` ：继承自 BroadcastRecevier ， 在 AppWidget 应用 update、enable、disable 和 delete 时接收通知。其中，onUpdate、onReceive 是最常用到的方法，它们接收更新通知。  
- `AppWidgetProvderInfo`：描述 AppWidget 的大小、更新频率和初始界面等信息，以XML 文件形式存在于应用的 res/xml/目录下。  
- `AppWidgetManger` ：负责管理 AppWidget ，向 AppwidgetProvider 发送通知。  
- `RemoteViews` ：一个可以在其他应用进程中运行的类，向 AppWidgetProvider 发送通知。  
  
AppWidgetProvider中的几个回调方法：onEnabled,onDisabled,onDeleted,onUpdated会自动被其onReceive方法在合适的时间调用，确切来说是，当广播到来以后，AppWidgetProvider会自动根据广播的action通过onReceive方法来自动派发广播，也就是调用上述几个方法。  
  
# AppWidgetManger 常用 API  
- `bindAppWidgetId`(int appWidgetId, ComponentName provider)：通过给定的 ComponentName 绑定appWidgetId  
- `getAppWidgetIds`(ComponentName provider)：通过给定的 ComponentName 获取AppWidgetId  
- `getAppWidgetInfo`(int appWidgetId)：通过 AppWidgetId 获取 AppWidget 信息  
- `getInstalledProviders`()：返回一个 `List<AppWidgetProviderInfo>` 的信息  
- `getInstance`(Context context)：获取 AppWidgetManger 实例(静态方法)  
- `updateAppWidget`(int[] appWidgetIds, RemoteViews views)：通过 appWidgetId 对传进来的 RemoteView 进行修改，并重新刷新vAppWidget 组件  
- `updateAppWidget`(ComponentName provider, RemoteViews views)：通过 ComponentName 对传进来的 RemoeteView 进行修改，并重新刷新AppWidget 组件  
- `updateAppWidget`(int appWidgetId, RemoteViews views)：通过 appWidgetId 对传进来的 RemoteView 进行修改，并重新刷新AppWidget 组件  
  
# 开发步骤  
## 定义小工具界面  
在`res/layout/`下新建一个xml文件，在里面设计小工具要做成什么样子。  
```xml  
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"  
                android:layout_width="match_parent"  
                android:layout_height="wrap_content"  
                android:background="#300f">  
  
    <ImageView  
        android:id="@+id/iv_icon"  
        android:layout_width="70dp"  
        android:layout_height="match_parent"  
        android:layout_centerVertical="true"  
        android:src="@drawable/icon"/>  
  
    <TextView  
        android:id="@+id/tv_data"  
        android:layout_width="wrap_content"  
        android:layout_height="wrap_content"  
        android:layout_alignParentBottom="true"  
        android:layout_alignParentRight="true"  
        android:gravity="center"  
        android:padding="3dp"  
        android:singleLine="true"  
        android:text="时间 2018.11.16 13.40.49"  
        android:textColor="#a000"  
        android:textSize="11sp"/>  
  
    <TextView  
        android:id="@+id/tv_text"  
        android:layout_width="match_parent"  
        android:layout_height="match_parent"  
        android:layout_above="@+id/tv_data"  
        android:layout_toRightOf="@+id/iv_icon"  
        android:gravity="center"  
        android:text="内容"  
        android:textColor="#fff"  
        android:textSize="15sp"/>  
  
</RelativeLayout>  
```  
  
## 定义小工具配置信息  
在`res/xml/`下新建`my_app_widget_info.xml`，名称随意，添加如下内容：  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"  
                    android:initialKeyguardLayout="@layout/my_app_widget"  
                    android:initialLayout="@layout/my_app_widget"  
                    android:minHeight="40dp"  
                    android:minWidth="110dp"  
                    android:previewImage="@drawable/icon"  
                    android:resizeMode="horizontal|vertical"  
                    android:updatePeriodMillis="1800000"  
                    android:widgetCategory="home_screen|keyguard"/>  
```  
配置项：  
- initialKeyguardLayout：锁屏时插件使用的布局  
- initialLayout：桌面时插件使用的布局  
- minWidth、minHeight：小部件的最小宽高，计算公式为：`(70*N)-30`，N为打算在屏幕上占几格  
- previewImage：小部件的列表中的预览图  
- resizeMode：调整size模式，即是否可以在水平、竖直方向伸缩  
- updatePeriodMillis：更新的周期，单位为毫秒，0表示没有更新周期；系统为了省电，默认是`30分钟`更新一次，如果你设置的值比30分钟小，系统也是30分钟才会更新一次。如果需要频繁更新 Widget，需要自己起一个service进行更新。  
- widgetCategory：插件模式(可添加的位置)，锁屏或(和)桌面  
  
## 定义小工具类  
继承AppWidgetProvider，重写所需要的方法  
```java  
public class MyWidget extends AppWidgetProvider {  
    public static final String ACTION_MYWIDGET_ONCLICK = "com.bqt.test.mywidget.onclick";  
      
    @SuppressLint("UnsafeProtectedBroadcastReceiver")  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        super.onReceive(context, intent);  
        Log.i("bqt", "【onReceive，其他所有回调方法都是由它调用的】");  
        //这里判断是自己的action，做自己的事情，比如小工具被点击了要干啥  
        if (ACTION_MYWIDGET_ONCLICK.equals(intent.getAction())) {  
            Toast.makeText(context, "什么是最重要的呢？\n         时间！争分夺秒！", Toast.LENGTH_LONG).show();  
        }  
    }  
      
    @Override  
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {  
        //根据 updatePeriodMillis 定义的时间定期调用该函数，此外当用户添加 Widget 时也会调用该函数，可以在这里进行必要的初始化操作。  
        Log.i("bqt", "【onUpdate，当插件内容更新函数时调用，最重要的方法】" + Arrays.toString(appWidgetIds));  
        for (int appWidgetId : appWidgetIds) {  
            String text = context.getSharedPreferences("MyWidget", Context.MODE_PRIVATE).getString("MyWidgetText", "");  
            RemoteViews remoteViews = Utils.getRemoteViews(context, text);  
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);  
        }  
    }  
      
    @Override  
    public void onEnabled(Context context) {  
        Log.i("bqt", "【onEnabled，当 Widget 第一次被添加时调用】");  
        //例如用户添加了两个你的 Widget，那么只有在添加第一个 Widget 时该方法会被调用，该方法适合执行你所有 Widgets 只需进行一次的操作  
    }  
      
    @Override  
    public void onDisabled(Context context) {  
        Log.i("bqt", "【onDisabled，当你的最后一个 Widget 被删除时调用】");//该方法适合用来清理之前在 onEnabled() 中进行的操作  
    }  
      
    @Override  
    public void onDeleted(Context context, int[] appWidgetIds) {  
        super.onDeleted(context, appWidgetIds);  
        Log.i("bqt", "【onDeleted，当 Widget 被删除时调用】" + Arrays.toString(appWidgetIds));  
    }  
      
    @Override  
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {  
        super.onRestored(context, oldWidgetIds, newWidgetIds);  
        Log.i("bqt", "【onRestored，被还原是调用】旧" + Arrays.toString(oldWidgetIds) + "，新" + Arrays.toString(newWidgetIds));  
    }  
      
    @Override  
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {  
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);  
        Log.i("bqt", "【onAppWidgetOptionsChanged，当 Widget 第一次被添加或者大小发生变化时调用】");  
    }  
}  
```  
  
## 声明小工具类  
```xml  
<receiver android:name=".MyWidget">  
    <intent-filter>  
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE"/><!--必须添加的action，否则不会出现在小部件的列表中-->  
        <action android:name="com.bqt.test.mywidget.onclick"/><!--自定义的action，用于在点击小部件上时发送的广播Action-->  
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE"/><!--下面这几个都是不必须的-->  
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE_OPTIONS"/>  
        <action android:name="android.appwidget.action.APPWIDGET_RESTORED"/>  
        <action android:name="android.appwidget.action.APPWIDGET_DELETED"/>  
    </intent-filter>  
  
    <meta-data  
        android:name="android.appwidget.provider"  
        android:resource="@xml/my_app_widget_info"/>  
</receiver>  
```  
  
## 与 Activity 的交互  
```java  
public class EditActivity extends Activity {  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        LinearLayout linearLayout = getContentView();  
        setContentView(linearLayout);  
        new Handler().postDelayed(this::showSoftInput, 100);  
    }  
      
    @NonNull  
    private LinearLayout getContentView() {  
        LinearLayout linearLayout = new LinearLayout(this);  
        linearLayout.setOrientation(LinearLayout.VERTICAL);  
          
        EditText editText = new EditText(this);  
        editText.setHint("请输入小工具中显示的内容");  
        editText.setText(getIntent() != null ? getIntent().getStringExtra("text") : "");  
        editText.setLines(3);  
        editText.setGravity(Gravity.CENTER);  
        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));  
        linearLayout.addView(editText);  
          
        Button button = new Button(this);  
        button.setText("保存");  
        button.setOnClickListener(v -> {  
            updateAppWidget(editText.getText().toString());  
            finish();  
        });  
        linearLayout.addView(button);  
          
        return linearLayout;  
    }  
      
    private void updateAppWidget(String text) {  
        ComponentName componentName = new ComponentName(this, MyWidget.class);  
        getSharedPreferences("MyWidget", Context.MODE_PRIVATE).edit().putString("MyWidgetText", text).apply();  
        RemoteViews remoteViews = Utils.getRemoteViews(this, text);  
        AppWidgetManager.getInstance(this).updateAppWidget(componentName, remoteViews);  
    }  
      
    private void showSoftInput() {  
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
        if (imm != null) {  
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);  
        }  
    }  
}  
```  
  
## 工具类  
```java  
public class Utils {  
      
    public static RemoteViews getRemoteViews(Context context, String text) {  
        SpannableString textSpannableString = Utils.getSpannableString(context, text, Color.WHITE, 15);  
        String date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date());  
        SpannableString dateSpannableString = Utils.getSpannableString(context, date, Color.DKGRAY, 12);  
          
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);  
        remoteViews.setTextViewText(R.id.tv_data, dateSpannableString);//时间  
        remoteViews.setTextViewText(R.id.tv_text, textSpannableString);//内容  
          
        Intent intent = new Intent(context, EditActivity.class);  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);  
        intent.putExtra("text", text);  
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
        remoteViews.setOnClickPendingIntent(R.id.iv_icon, pendingIntent);  
          
        Intent actionIntent = new Intent(context, MyWidget.class);//显示意图  
        actionIntent.setAction(MyWidget.ACTION_MYWIDGET_ONCLICK);  
        //actionIntent.setPackage(context.getPackageName());//隐式意图必须设置Package，实际测试发现，如果使用隐式意图，在应用被杀掉时不响应广播  
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
        remoteViews.setOnClickPendingIntent(R.id.tv_text, pIntent);  
          
        return remoteViews;  
    }  
      
    public static SpannableString getSpannableString(Context context, String source, int color, int size) {  
        SpannableString mSpannableString = new SpannableString(source);  
        int dpValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());  
        String firstLine = source.contains("\n") ? source.substring(0, source.indexOf("\n")) : source;  
          
        //第一行的样式  
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);//颜色  
        mSpannableString.setSpan(colorSpan, 0, firstLine.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(dpValue);//大小  
        mSpannableString.setSpan(absoluteSizeSpan, 0, firstLine.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
          
        //其他行的样式  
        if (source.contains("\n")) {  
            String otherLine = source.substring(source.indexOf("\n"));  
            if (otherLine.length() > 0) {  
                ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.YELLOW);//颜色  
                mSpannableString.setSpan(colorSpan2, firstLine.length(), source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
                AbsoluteSizeSpan absoluteSizeSpan2 = new AbsoluteSizeSpan((int) (0.8f * dpValue));//大小  
                mSpannableString.setSpan(absoluteSizeSpan2, firstLine.length(), source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
            }  
        }  
        return mSpannableString;  
    }  
}  
```  
  
2018-11-15  
