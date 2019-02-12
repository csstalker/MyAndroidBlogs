| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
适配 通知 Notification 通知渠道 前台服务 MD  
***  
目录  
===  

- [直接上代码](#直接上代码)
- [通知的基本知识](#通知的基本知识)
	- [创建通知](#创建通知)
	- [通知优先级](#通知优先级)
	- [更新和删除通知](#更新和删除通知)
	- [在通知中显示进度](#在通知中显示进度)
	- [扩展布局 setStyle](#扩展布局-setstyle)
	- [设定提示响应](#设定提示响应)
	- [浮动通知](#浮动通知)
	- [自定义通知 RemoteViews](#自定义通知-remoteviews)
	- [PendingIntent](#pendingintent)
- [8.0 通知的适配：通知渠道](#80-通知的适配：通知渠道)
	- [为什么要进行通知栏适配？](#为什么要进行通知栏适配？)
	- [8.0 系统的通知栏适配](#80-系统的通知栏适配)
	- [我一定要适配吗？](#我一定要适配吗？)
	- [创建通知渠道](#创建通知渠道)
	- [通知的变更](#通知的变更)
	- [管理通知渠道](#管理通知渠道)
	- [显示未读角标](#显示未读角标)
- [前台服务](#前台服务)
	- [什么是前台服务](#什么是前台服务)
	- [为什么要使用前台服务](#为什么要使用前台服务)
	- [如何创建一个前台服务](#如何创建一个前台服务)
	- [测试代码](#测试代码)
  
[官方文档](https://developer.android.google.cn/guide/topics/ui/notifiers/notifications#ManageChannels)  
  
# 直接上代码  
```java  
public class MainActivity extends ListActivity {  
    public static final String CHANNEL_ID = "channelId";//渠道id  
    public static final String BROADCAST_ACTION = "android.intent.action.BROADCAST_ACTION";  
    private static final int ID = 1111;  
    private NotificationManager manager;  
      
    private DynamicBroadcast broadcast;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"创建通知渠道",  
            "删除通知渠道",  
            "打开通知渠道设置",  
            "查看通知渠道设置信息",  
            "创建通知",  
            "更新通知",  
            "自定义通知",  
            "创建前台服务",  
            "删除通知",  
            "删除所有通知",  
            "关闭前台服务",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
          
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
          
        broadcast = new DynamicBroadcast();  
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);  
        registerReceiver(broadcast, intentFilter);//动态注册广播  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        if (broadcast != null) {  
            unregisterReceiver(broadcast);  
        }  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //26，8.0  
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "【内幕】", NotificationManager.IMPORTANCE_DEFAULT);//渠道id、渠道名称、渠道重要性级别  
                    channel.setDescription("【渠道描述】"); //设置渠道描述，这个描述在系统设置中可以看到  
                    channel.enableLights(true);//是否显示通知指示灯  
                    channel.enableVibration(true);//是否振动  
                    channel.setShowBadge(true); //是否允许这个渠道下的通知显示角标，默认会显示角标  
                    manager.createNotificationChannel(channel);//创建通知渠道  
                }  
                break;  
            case 1:  
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
                    manager.deleteNotificationChannel(CHANNEL_ID); //会在通知设置界面显示所有被删除的通知渠道数量  
                }  
                break;  
            case 2:  
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);  
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());  
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_ID);//渠道id必须是我们之前注册的  
                    startActivity(intent);  
                }  
                break;  
            case 3:  
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
                    NotificationChannel channel = manager.getNotificationChannel(CHANNEL_ID);  
                    if (channel != null) {//防止渠道被删除  
                        Toast.makeText(this, "通知等级:" + channel.getImportance(), Toast.LENGTH_SHORT).show();  
                        Log.i("bqt", "通知渠道完整信息：" + channel.toString());  
                    } else {  
                        Log.i("bqt", "通知渠道不存在");  
                    }  
                }  
                break;  
            case 4:  
                manager.notify(ID, getNotificationBuilder("内容").build());  
                break;  
            case 5:  
                manager.notify(ID, getNotificationBuilder("更新的内容").build());  
                break;  
            case 6:  
                manager.notify(ID, getNotificationBuilder("更新的内容").setContent(getRemoteViews()).build());  
                break;  
            case 7:  
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
                    //即使应用在后台运行，系统也允许其调用startForegroundService()函数将启动一个前台服务  
                    //但应用必须在创建服务后的5秒内调用该服务的startForegroun()函数，否则将报ANR错误  
                    startForegroundService(new Intent(this, ForegroundService.class));  
                } else {  
                    startService(new Intent(this, ForegroundService.class));  
                }  
                break;  
            case 8:  
                manager.cancel(ID);//根据id删除通知  
                break;  
            case 9:  
                manager.cancelAll();//删除所有通知，不能删除前台服务  
                break;  
            case 10:  
                stopService(new Intent(this, ForegroundService.class));  
                break;  
        }  
    }  
      
    private NotificationCompat.Builder getNotificationBuilder(String content) {  
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)  
            .setSmallIcon(R.drawable.icon)  
            .setContentTitle("标题")  
            .setContentText(content)  
            .setAutoCancel(true)  
              
            .setPriority(NotificationCompat.PRIORITY_HIGH)  
            .setWhen(System.currentTimeMillis())//设置事件发生的时间，面板中的通知按此时间排序  
            .setProgress(100, 50, false) //设置此通知表示的进度(ProgressBar)，为true时不可被用户取消  
            .setOngoing(false)//正在进行的通知，在通知面板中的常规通知上方进行排序，没有“X”关闭按钮，不受全部清除按钮的影响  
            //通常是用来表示一个后台任务或以某种方式正在等待，如播放音乐、文件下载、同步操作、获取定位、主动网络连接  
              
            //以下几个不常用  
            .setTicker("【TickerText】您有新短消息，请注意查收！")//设置通知首次到达时状态栏中显示的文本  
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))//设置ticker和通知中显示的大图标  
            .setStyle(getStyle())//大视图风格，有三个实现类，NotificationCompat.BigPictureStyle、BigTextStyle、InboxStyle  
            .setDefaults(Notification.DEFAULT_ALL) //设置将使用的默认通知选项  
            .setNumber(3);//未读通知数量，小米等定制系统无效  
          
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { //16  
            mBuilder.setPriority(Notification.PRIORITY_DEFAULT); //设置该通知优先级，优先级表示此通知应consumed多少用户的attention  
        }  
          
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //21，5.0  
            mBuilder.setFullScreenIntent(getPendingIntent(), true);//显示悬挂式通知  
        } else {  
            mBuilder.setContentIntent(getPendingIntent()); //普通通知  
        }  
          
        return mBuilder;  
    }  
      
    private NotificationCompat.InboxStyle getStyle() {  
        return new NotificationCompat.InboxStyle()  
            .addLine("第1行")  
            .addLine("第2行")  
            .addLine("第3行")  
            .addLine("第4行")  
            .setBigContentTitle("我是标题")  
            .setSummaryText("总结：我是包青天");  
    }  
      
    private PendingIntent getPendingIntent() {  
        Intent intent = new Intent(BROADCAST_ACTION);  
        intent.putExtra("data", "【你好，包青天】");  
        //PendingIntent.getActivity(this, 0, intent, 0); //跳转到Activity：上下文，请求码，意图，int flags  
        return PendingIntent.getBroadcast(this, ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
    }  
      
    private RemoteViews getRemoteViews() {  
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_custom_notification);  
        remoteViews.setImageViewResource(R.id.imageview, R.drawable.icon);  
        remoteViews.setTextViewText(R.id.tv_title, "这是标题");  
        remoteViews.setTextViewText(R.id.tv_content, "这是内容");  
        return remoteViews;  
    }  
      
    class DynamicBroadcast extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String data = intent.getStringExtra("data");  
            Toast.makeText(context, data, Toast.LENGTH_SHORT).show();  
        }  
    }  
}  
```  
  
# 通知的基本知识  
[官方文档](https://developer.android.google.cn/guide/topics/ui/notifiers/notifications#ManageChannels)  
  
通知是您可以在应用的常规 UI 外部向用户显示的消息。当您告知系统发出通知时，它将先以图标的形式显示在通知区域中。用户可以打开抽屉式通知栏查看通知的详细信息。 通知区域和抽屉式通知栏均是由系统控制的区域，用户可以随时查看。  
  
## 创建通知  
通知是通过 `NotificationManager` 来发送一个 Notification 对象来完成，NotificationManager 是一个重要的系统级服务，该对象位于应用程序的框架层(framework)中，应用程序可以通过它向系统发送全局的通知。  
  
您可以在 [NotificationCompat.Builder](https://developer.android.google.cn/reference/android/support/v4/app/NotificationCompat.Builder) 对象中为通知指定 UI 信息和操作。要创建通知，请调用 NotificationCompat.Builder.build()，它将返回包含您的具体规范的 Notification 对象。要发出通知，请通过调用 NotificationManager.notify() 将 Notification 对象传递给系统。  
  
虽然API中提供了各种属性的设置，但是一个通知对象，有几个属性是**必须**要设置的，其他的属性均是可选的，必须设置的属性如下：  
- 标题：setContentTitle()  
- 文本内容：setContentText()  
- 小图标：setSamllIcon()  
  
尽管通知操作都是可选的，但是您至少应向通知添加一个操作，一般都会定义一个当用户点击通知时会触发的操作。  
  
## 通知优先级  
您可以根据需要设置通知的优先级。优先级充当一个提示，提醒设备 UI 应该如何显示通知。 要设置通知的优先级，请调用 NotificationCompat.Builder.setPriority() 并传入一个 NotificationCompat 优先级常量。如果未设置，则优先级默认为 PRIORITY_DEFAULT。  
  
有五个优先级别：  
- `NotificationCompat.PRIORITY_MIN` = -2  
- `NotificationCompat.PRIORITY_LOW` = -1  
- `NotificationCompat.PRIORITY_DEFAULT` = 0  
- `NotificationCompat.PRIORITY_HIGH` = 1  
- `NotificationCompat.PRIORITY_MAX` = 2  
  
有关设置适当优先级别的信息，请参阅 [通知](https://developer.android.google.cn/design/patterns/notifications.html) 设计指南中的“正确设置和管理通知优先级”。  
  
  
## 更新和删除通知  
当您需要为同一类型的事件多次发出同一通知时，应避免创建全新的通知，而是应考虑通过更改之前通知的某些值和/或为其添加某些值来更新通知。  
  
**更新通知**  
要将通知设置为能够更新，请通过调用 NotificationManager.notify() 发出带有通知 ID 的通知。 要在发出之后更新此通知，请更新或创建 NotificationCompat.Builder 对象，从该对象构建 Notification 对象，并发出与之前所用 ID 相同的 Notification。如果之前的通知仍然可见，则系统会根据 Notification 对象的内容更新该通知。相反，如果之前的通知已被清除，系统则会创建一个新通知。  
  
**删除通知**  
除非发生以下情况之一，否则通知仍然可见：  
- 用户单独或通过使用“全部清除”清除了该通知（如果通知可以清除）。  
- 用户点击通知，且您在创建通知时调用了 setAutoCancel()。  
- 您针对特定的通知 ID 调用了 cancel()。此方法还会删除当前通知。  
- 您调用了 cancelAll() 方法，该方法将删除之前发出的所有通知。  
  
## 在通知中显示进度  
通知可能包括动画形式的进度指示器，向用户显示正在进行的操作状态。如果您可以估计操作所需的时间以及任意时刻的完成进度，则使用“限定”形式的指示器（进度栏）。如果无法估计操作的时长，则使用“非限定”形式的指示器（Activity 指示器）。  
  
要在 Android 4.0 及更高版本的平台上使用进度指示器，需调用 `setProgress()`。对于早期版本，您必须创建包括 `ProgressBar` 视图的自定义通知布局。  
  
**显示持续时间固定的进度指示器**  
要显示限定形式的进度栏，请通过调用 `setProgress(max, progress, false)` 将进度栏添加到通知，然后发出通知。随着操作继续进行，递增 progress 并更新通知。操作结束时， progress 应该等于 max。调用 setProgress() 的常见方法是将 max 设置为 100，然后将 progress 作为操作的“完成百分比”值递增。  
  
您可以在操作完成后仍保留显示进度栏，也可以将其删除。无论哪种情况，都请记住更新通知文本以显示操作已完成。要删除进度栏，请调用 `setProgress(0, 0, false)`。例如：  
  
**显示持续 Activity 指示器**  
要显示非限定形式的 Activity 指示器，请使用 `setProgress(0, 0, true)` 将其添加到通知（忽略前两个参数），然后发出通知。这样一来，指示器的样式就与进度栏相同，只是其动画还在继续。  
  
在操作开始之际发出通知。除非您修改通知，否则动画将一直运行。 操作完成后，调用 `setProgress(0, 0, false)`，然后更新通知以删除 Activity 指示器。 请务必这样做；否则，即使操作完成，动画仍将运行。同时，请记得更改通知文本，以表明操作已完成。  
  
## 扩展布局 setStyle  
Notification有两种视觉风格，一种是标准视图、一种是大视图。标准视图在Android中各版本是通用的，但是对于大视图而言，仅支持 Android4.1+ 的版本。  
  
从官方文档了解到，一个标准视图显示的大小要保持在 64dp 高，宽度为屏幕标准。标准视图的通知主体内容有以下几个：通知标题、大图标、通知内容、通知消息、小图标、通知的时间。  
  
而对于大视图而言，它的细节区域只能显示 256dp 高度的内容，并且只对 Android4.1+ 之后的设备才支持，它相比标准视图不一样的地方，均需要使用 `setStyle()` 方法设定。  
  
setStyle() 传递一个 NotificationCompat.Style 对象，它是一个抽象类，系统为我们提供了三个实现类，用于显示不同的场景。分别是：  
- `NotificationCompat.BigPictureStyle`：在细节部分显示一个256dp高度的位图  
- `NotificationCompat.BigTextStyle`：在细节部分显示一个大的文本块  
- `NotificationCompat.InboxStyle`：在细节部分显示一段行文本  
  
如果仅仅显示一个图片，使用 BigPictureStyle 是最方便的；如果需要显示一个富文本信息，则可以使用 BigTextStyle；如果仅仅用于显示一个文本的信息，那么使用 InboxStyle 即可。  
  
## 设定提示响应  
对于有些通知，需要调用一些设备的资源，使用户能更快的发现有新通知，一般可设定的响应有：`铃声、闪光灯、震动`。对于这三个属性，NotificationCompat.Builder 提供了三个方法设定：  
- setSound(Uri sound)：设定一个铃声，用于在通知的时候响应。传递一个Uri的参数，格式为“file:///mnt/sdcard/Xxx.mp3”  
- setLights(int argb, int onMs, int offMs)：设定前置LED灯的闪烁速率，持续毫秒数，停顿毫秒数  
- setVibrate(long[] pattern)：设定震动的模式，以一个long数组保存毫秒级间隔的震动  
  
大多数时候，我们并不需要设定一个特定的响应效果，只需要遵照用户设备上系统通知的效果即可，那么可以使用`setDefaults(int)`方法设定默认响应参数。  
  
在Notification中，已对它的参数使用常量定义了，我们只需使用即可：  
- `Notification.DEFAULT_ALL`  铃声、闪光、震动均系统默认  
- `Notification.DEFAULT_SOUND`  系统默认铃声  
- `Notification.DEFAULT_LIGHTS`  系统默认闪光  
- `Notification.DEFAULT_VIBRATE`  系统默认震动  
  
注意，如果需要访问硬件设备的话，是需要对其进行授权的，所以需要在清单文件 AndroidManifest.xml 中增加两个授权，分别授予访问振动器与闪光灯的权限：  
```xml  
<!-- 闪光灯权限 -->  
<uses-permission android:name="android.permission.FLASHLIGHT"/>  
<!-- 振动器权限 -->  
<uses-permission android:name="android.permission.VIBRATE"/>  
```  
  
## 浮动通知  
对于 Android 5.0（API 级别 21），当设备处于活动状态时（即，设备未锁定且其屏幕已打开），通知可以显示在小型浮动窗口中（也称为“浮动通知”）。 这些通知看上去类似于精简版的通知，只是浮动通知还显示操作按钮。 用户可以在不离开当前应用的情况下处理或清除浮动通知。  
  
可能触发浮动通知的条件示例包括：  
- 用户的 Activity 处于全屏模式中（应用使用 fullScreenIntent），或者  
- 通知具有较高的优先级并使用铃声或振动  
  
>   
  
## 自定义通知 RemoteViews  
通知也可以使用自定义的XML来自定义样式，但是对于通知而言，因为它的全局性，并不能简单的通过 inflate 出一个View，因为可能触发通知的时候，响应的 App 已经关闭，无法获取所指定的 XML 布局文件。为此，系统设计要使用单独的一个 RemoteViews 类来操作。  
  
RemoteViews，描述了一个视图层次的结构，可以显示在另一个进程中。层次结构也是从布局文件中 inflate 出一个视图，这个类提供了一些基本的操作修改其 inflate 的内容。  
  
RemoteViews提供了多个构造函数，一般使用 `RemoteViews(String packageName,int layoutId)`  
  
当获取到 RemoteViews 对象之后，可以使用它的一系列 `setXxx()` 方法通过控件的Id设置控件的属性。最后使用 `.setContent(RemoteViews)` 方法设置它到一个 Notification 中。  
  
## PendingIntent  
PendingIntent 可以看做是对Intent的包装，通过名称可以看出PendingIntent用于处理即将发生的意图，而Intent用来处理马上发生的意图，使用pendingIntent的目的在于它所包含的Intent的操作的执行是需要满足某些条件的。  
  
PendingIntent主要持有的信息是它所包装的Intent和当前Application的Context。由于它保存了触发App的Context，所以使得外部App可以像当前App一样执行PendingIntent里的Intent，就算执行时触发通知的App已经不存在了，也能通过存在PendingIntent里的Context正常执行Intent，并且还可以处理Intent所带来的额外的信息。  
  
PendingIntent提供了多个静态方法，用于获得适用于不同场景的PendingIntent对象  
![](index_files/41860b48-d4d5-429f-a0d4-a1927eb257fe.png)  
  
`getActivity()、getBroadcast()、getService()` 分别对应着Intent的3个行为：跳转到一个activity组件、打开一个广播组件和打开一个服务组件。主要的使用的地方：Notificatio的发送，SmsManager的发送、警报器AlarmManager的执行等。  
  
PendingIntent一般作为参数传给某个实例，在该实例完成某个操作后自动执行PendingIntent上的Action；也可以通过PendingIntent的send函数手动执行，并可以在send函数中设置OnFinished表示send成功后执行的动作。  
  
```java  
public static PendingIntent getActivity(Context context, int requestCode, Intent intent, int flags, Bundle options)  
```  
第四个参数 int flags 系统提供的几个值的含义：  
- `FLAG_ONE_SHOT` = 1<<30：表明这里构建的PendingIntent只能使用一次。  
- `FLAG_UPDATE_CURRENT` = 1<<27：如果构建的PendingIntent已经存在，则替换它。  
- `FLAG_CANCEL_CURRENT` = 1<<28：如果构建的PendingIntent已经存在，则取消前一个，重新构建一个。  
- `FLAG_NO_CREATE` = 1<<29：如果前一个PendingIntent已经不存在了，将不再构建它，否则替换它。  
  
Intent和PendingIntent的一些区别：  
- Intent是立即使用的，而PendingIntent可以等到事件发生后触发，PendingIntent可以cancel  
- Intent在程序结束后即终止，而PendingIntent在程序结束后依然有效  
- Intent需要在某个Context内运行，而PendingIntent自带Context  
- Intent在原task中运行，PendingIntent在新的task中运行  
  
# 8.0 通知的适配：通知渠道  
[参考郭神的文章](https://blog.csdn.net/guolin_blog/article/details/79854070)  
  
## 为什么要进行通知栏适配？  
不得不说，通知栏真是一个让人又爱又恨的东西。  
  
通知栏是Android系统原创的一个功能，虽说乔布斯一直认为Android系统是彻彻底底抄袭iOS的一个产品，但是通知栏确实是Android系统原创的，反而苹果在iOS 5之后也加入了类似的通知栏功能。  
  
通知栏的设计确实非常巧妙，它默认情况下不占用任何空间，只有当用户需要的时候用手指在状态栏上向下滑动，通知栏的内容才会显示出来，这在智能手机发展的初期极大地解决了手机屏幕过小，内容展示区域不足的问题。  
  
可是随着智能手机发展的逐渐成熟，通知栏却变得越来越不讨人喜欢了。各个App都希望能抢占通知栏的空间，来尽可能地宣传和推广自己的产品。现在经常是早上一觉醒来拿起手机一看，通知栏上全是各种APP的推送，不胜其烦。  
  
我个人虽然是Android应用开发者，但同时也是Android手机的资深用户。我已经使用了8年的Android手机，目前我对于通知栏的这种垃圾推送是零容忍的。现在每当我安装一个新的App时，我都会先到设置里面去找一找有没有推送开关，如果有的话我会第一时间把它关掉。而如果一个App经常给我推送垃圾信息却又无法关闭时，我会直接将它的通知总开关给关掉，如果还不是什么重要的App的话，那么我可能就直接将它卸载掉了。  
  
为什么一个很好的通知栏功能现在却变得这么遭用户讨厌？很大一部分原因都是因为开发者没有节制地使用导致的。就好像App保活一样，直到今天还是不断有人问我该如何保活App，试想如何每个人都能保活自己的App，那么最终受害的人是谁？还不是使用Android手机的用户。大家的手机只会越来越卡，最后只想把手机丢掉，变成iPhone用户了。也是因为开发者没节制地使用，Android现在的每个版本都会不断收缩后台权限。  
  
回到通知栏上也是一样，每个开发者都只想着尽可能地去宣传自己的App，最后用户的手机就乱得跟鸡窝一样了。但是通知栏又还是有用处的，比如我们收到微信、短信等消息的时候，确实需要通知栏给我们提醒。因此分析下来，通知栏目前最大的问题就是，无法让用户对感兴趣和不感兴趣的消息进行区分。就比如说，我希望淘宝向我推送卖家发货和物流的相关消息，但是我不想收到那些打折促销或者是让我去买衣服的这类消息。那么就目前来说，是没有办法对这些消息做区分的，我要么同意接受所有消息，要么就屏蔽所有消息，这是当前通知栏的痛点。  
  
那么在Android 8.0系统中，Google也是从这个痛点开始下手的。   
  
## 8.0 系统的通知栏适配  
从Android 8.0系统开始，Google引入了通知渠道这个概念。  
  
什么是通知渠道呢？顾名思义，就是每条通知都要属于一个对应的渠道。每个App都可以自由地创建当前App拥有哪些通知渠道，但是这些通知渠道的控制权都是掌握在用户手上的。用户可以自由地选择这些通知渠道的重要程度，是否响铃、是否振动、或者是否要关闭这个渠道的通知。  
  
拥有了这些控制权之后，用户就再也不用害怕那些垃圾推送消息的打扰了，因为用户可以自主地选择自己关心哪些通知、不关心哪些通知。举个具体的例子，我希望可以即时收到支付宝的收款信息，因为我不想错过任何一笔收益，但是我又不想收到支付宝给我推荐的周围美食，因为我没钱只吃得起公司食堂。这种情况，支付宝就可以创建两种通知渠道，一个收支，一个推荐，而我作为用户对推荐类的通知不感兴趣，那么我就可以直接将推荐通知渠道关闭，这样既不影响我关心的通知，又不会让那些我不关心的通知来打扰我了。  
  
对于每个App来说，通知渠道的划分是非常需要仔细考究的，因为通知渠道一旦创建之后就不能再修改了，因此开发者需要仔细分析自己的App一共有哪些类型的通知，然后再去创建相应的通知渠道。  
  
我们可以参考Twitter的通知渠道划分，Twitter就是根据自己的通知类型，对通知渠道进行了非常详细的划分，这样用户的自主选择性就比较高了，也就大大降低了用户不堪其垃圾通知的骚扰而将App卸载的概率。  
  
## 我一定要适配吗？  
Google这次对于8.0系统通知渠道的推广态度还是比较强硬的。  
  
首先，如果你升级了appcompat库，那么所有使用appcompat库来构建通知的地方全部都会进行废弃方法提示，如下所示：  
![](index_files/b280b490-6ef9-46e2-811a-0933f0ded4e7.png)  
  
上图告诉我们，此方法已废弃，需要使用带有通知渠道的方法才行。  
  
当然，Google也并没有完全做绝，即使方法标为了废弃，但还是可以正常使用的。可是如果你将项目中的targetSdkVersion指定到了26或者更高，那么Android系统就会认为你的App已经做好了8.0系统的适配工作，当然包括了通知栏的适配。这个时候如果还不使用通知渠道的话，那么你的App的通知将完全无法弹出。因此这里给大家的建议就是，一定要适配。  
  
## 创建通知渠道  
首先确保我们的targetSdkVersion已经指定到了26或者更高  
```java  
targetSdkVersion 26  
```  
  
然后就可以创建渠道了，但在创建渠道时要判断当前手机的系统版本必须是否为Android 8.0系统或者更高，因为低版本的手机系统并没有通知渠道这个功能，不做系统版本检查的话会在低版本手机上造成崩溃。  
```java  
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //26，8.0  
    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "【内幕】", NotificationManager.IMPORTANCE_DEFAULT);//渠道id、渠道名称、渠道重要性级别  
    channel.setDescription("【渠道描述】"); //设置渠道描述，这个描述在系统设置中可以看到  
    channel.enableLights(true);//是否显示通知指示灯  
    channel.enableVibration(true);//是否振动  
    channel.setShowBadge(true); //是否允许这个渠道下的通知显示角标，默认会显示角标  
    manager.createNotificationChannel(channel);//创建通知渠道  
}  
```  
  
创建一个通知渠道的方式非常简单，创建一个通知渠道至少需要渠道ID、渠道名称以及重要等级这三个参数。其中渠道ID可以随便定义，只要保证全局唯一性就可以；渠道名称是给用户看的，需要能够表达清楚这个渠道的用途；重要等级的不同则会决定通知的不同行为，当然这里只是初始状态下的重要等级，用户可以随时手动更改某个渠道的重要等级，甚至是可以完全关闭该渠道的通知，这些都是App无法干预的。  
  
系统提供的重要等级主要有七种层次：  
- `IMPORTANCE_DEFAULT`: 默认notification importance,可以在任何地方显示，有声音。  
- `IMPORTANCE_HIGH`:可以在任何地方显示，有声音.  
- `IMPORTANCE_LOW`:可以在任何地方显示,没有声音.  
- `IMPORTANCE_MAX`:重要程度最高，可以在任何地方显示，有声音，可以在用户当前屏幕上显示通知,可以使用full screen intents.比如来电。  
- `IMPORTANCE_MIN`:无声音，不会出现在状态栏中。  
- `IMPORTANCE_NONE`:在任何地方都不会显示，被阻塞。  
- `IMPORTANCE_UNSPECIFIED`:表示用户没有表示重要性的值。这个值是为了持久的首选项，并且永远不应该与实际的通知相关联  
  
至于创建通知渠道的这部分代码，你可以写在MainActivity中，也可以写在Application中，实际上可以写在程序的任何位置，只需要保证在通知弹出之前调用就可以了。并且创建通知渠道的代码只在第一次执行的时候才会创建，以后每次执行创建代码系统会检测到该通知渠道已经存在了，因此不会重复创建，也并不会影响任何效率。  
  
## 通知的变更  
触发通知的代码和之前版本基本是没有任何区别的，只是在构建通知对象的时候，需要多传入一个通知渠道ID，表示这条通知是属于哪个渠道的。  
  
接下来我们看一看Android 8.0系统中通知栏特有的功能。  
  
对于一条通知，我们可以通过快速向左或者向右滑动来关闭一条通知，但在Android 8.0中，如果你缓慢地向左或者向右滑动，就会看到这样两个按钮(小米等国产定制系统可能只有设置哪一个按钮)：  
![](https://img-blog.csdn.net/20180413205907731)  
  
其中，左边那个时钟图标的按钮可以让通知延迟显示。比方说这是一条比较重要的通知，但是我暂时没时间看，也不想让它一直显示在状态栏里打扰我，我就可以让它延迟一段后时间再显示，这样我就暂时能够先将精力放在专注的事情上，等过会有时间了这条通知会再次显示出来，我不会错过任何信息。  
  
而右边那个设置图标的按钮就可以用来对通知渠道进行屏蔽和配置了，用户对每一个App的每一个通知渠道都有绝对的控制权，可以根据自身的喜好来进行配置和修改。比如说我觉得某一渠道的消息老是向我推荐广告，实在是太烦了，我就可以将此渠道消息的通知渠道关闭掉。这样我以后就不会再收到这个通知渠道下的任何消息，而其他渠道的消息却不会受到影响，这就是8.0系统通知渠道最大的特色。  
  
另外，点击上图中的所有类别就可以进入到当前应用程序通知的完整设置界面。  
  
## 管理通知渠道  
在前面的内容中我们已经了解到，通知渠道一旦创建之后就不能再通过代码修改了。既然不能修改的话那还怎么管理呢？为此，Android赋予了开发者`读取`通知渠道配置的权限，如果我们的某个功能是必须按照指定要求来配置通知渠道才能使用的，那么就可以提示用户去手动更改通知渠道配置。  
```java  
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
    NotificationChannel channel = manager.getNotificationChannel(CHANNEL_ID);  
    if (channel != null) {//防止渠道被删除  
        Toast.makeText(this, "通知等级:" + channel.getImportance(), Toast.LENGTH_SHORT).show();  
        Log.i("bqt", "通知渠道完整信息：" + channel.toString());  
    } else {  
        Log.i("bqt", "通知渠道不存在");  
    }  
}  
```  
  
除了以上管理通知渠道的方式之外，Android 8.0还赋予了我们删除通知渠道的功能：  
```java  
manager.deleteNotificationChannel(CHANNEL_ID); //会在通知设置界面显示所有被删除的通知渠道数量  
```  
  
但是这个功能非常不建议大家使用。因为Google为了防止应用程序随意地创建垃圾通知渠道，会在通知设置界面显示所有被删除的通知渠道数量。这样是非常不美观的，所以对于开发者来说最好的做法就是仔细规划好通知渠道，而不要轻易地使用删除功能。  
  
## 显示未读角标  
前面我们提到过，苹果是从iOS 5开始才引入了通知栏功能，那么在iOS 5之前，iPhone都是怎么进行消息通知的呢？使用的就是未读角标功能，效果如下所示：  
![](https://img-blog.csdn.net/20180413211856236)  
  
实际上Android系统之前是从未提供过这种类似于iOS的角标功能的，但是由于很多国产手机厂商都喜欢跟风iOS，因此各种国产手机ROM都纷纷推出了自己的角标功能。  
  
可是国产手机厂商虽然可以订制ROM，但是却没有制定`API`的能力，因此长期以来都没有一个标准的API来实现角标功能，很多都是要通过向系统发送广播来实现的，而各个手机厂商的广播标准又不一致，经常导致代码变得极其混杂。  
  
值得高兴的是，从8.0系统开始，Google制定了Android系统上的角标规范，也提供了标准的API，长期让开发者头疼的这个问题现在终于可以得到解决了。  
  
我们只需要修改了两个地方，第一是在创建通知渠道的时候，调用 NotificationChannel 的 `setShowBadge(true)` 方法，表示允许这个渠道下的通知显示角标；第二是在创建通知的时候，调用 NotificationCompat.Builder 的 `setNumber()` 方法，并传入未读消息的数量。  
  
需要注意的是，即使我们不调用 setShowBadge(true) 方法，Android系统默认也是会显示角标的，但是如果你想禁用角标功能，那么记得一定要调用 setShowBadge(false) 方法。  
  
但是你如上设置后会发现：  
1、在谷歌原生系统上，只是在图标的右上角有个绿色的角标，未读数量并没有显示出来呢。这是因为这个功能还需要我们对着图标进行长按才行，效果如下图所示：  
![](https://img-blog.csdn.net/20180414170822618)  
  
2、在小米等国产定制系统上，连个卵用都没有。可能是因为他们还没来得及适配。  
  
可能有些朋友习惯了iOS上的那种未读角标，觉得Android上这种还要长按的方式很麻烦。这个没有办法，因为这毕竟是Android原生系统，Google没有办法像国内手机厂商那样可以肆无忌惮地模仿iOS，要不然可能会吃官司的。但是我相信国内手机厂商肯定会将这部分功能进行定制，风格应该会类似于iOS。不过这都不重要，对于我们开发者来说，最好的福音就是有了统一的API标准，不管国内手机厂商以后怎么定制ROM，都会按照这个API的标准来定制，我们只需要使用这个API来进行编程就可以了。  
  
# 前台服务  
## 什么是前台服务  
前台服务是那些被认为用户知道（用户所认可的）且在系统内存不足的时候不允许系统杀死的服务。前台服务必须给状态栏提供一个通知，它被放到正在运行(Ongoing)标题之下，这就意味着通知只有在这个服务被终止或从前台主动移除通知后才能被解除。  
  
官方描述：  
> A foreground service（前台服务） is a service that's considered to be（被用户所认可的） something the user is actively aware of and thus not a candidate for（而不是一个候选的，可以在内存不足时，被系统杀死的） the system to kill when low on memory. A foreground service must provide a notification for the status bar（前台服务必须提供一个显示通知）, which is placed under the "Ongoing" heading（它是不可以忽略的）, which means that the notification cannot be dismissed unless the service is either stopped or removed from the foreground.（意思是通知信息不能被忽略，除非服务停止或主动移除，否则将一直显示。）  
  
总结：  
- 所谓的前台Service就是状态栏显示的Notification  
- 前台Service比后台Service的系统优先级更高、更不易被系统杀死  
- 前台Service会一直有一个正在运行的图标在系统的状态栏显示，下拉状态栏后可以看到更加详细的信息  
- 前台服务只有在服务被终止或主动移除通知后才能被移除  
- 一般用于音乐播放器相关的功能  
  
## 为什么要使用前台服务  
在一般情况下，Service几乎都是在后台运行，一直默默地做着辛苦的工作。但这种情况下，后台运行的Service系统优先级相对较低，当系统内存不足时，在后台运行的Service就有可能被回收。  
  
那么，如果我们希望Service可以一直保持运行状态且不会在内存不足的情况下被回收时，可以选择将需要保持运行的Service设置为前台服务。  
  
## 如何创建一个前台服务  
- 调用 startForegroundService(8.0添加) 或 startService 启动一个 Service  
- 在 Service 的 onCreate 或 onStartCommand 中构建 Notification  
- 然后调用 startForeground 方法即让 Service 以前台服务方式运行  
- 在 Service 的 onDestory 中调用 stopForeground 方法停止正在运行的前台服务  
  
## 测试代码  
启动一个前台服务：  
```java  
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
    //即使应用在后台运行，系统也允许其调用startForegroundService()函数将启动一个前台服务  
    //但应用必须在创建服务后的5秒内调用该服务的startForegroun()函数，否则将报ANR错误  
    startForegroundService(new Intent(this, ForegroundService.class));  
} else {  
    startService(new Intent(this, ForegroundService.class));  
}  
```  
  
前台服务的定义：  
```java  
public class ForegroundService extends Service {  
    private static final int ID = 1112;  
      
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
      
    @Override  
    public void onCreate() {  
        super.onCreate();  
          
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {  
            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);  
            intent.putExtra("data", "【我是前台服务】");  
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ID, intent, 0);  
              
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)  
                .setSmallIcon(R.drawable.icon)  
                .setContentTitle("标题")  
                .setContentText("我是前台服务")  
                .setContentIntent(pendingIntent);  
            startForeground(ID, mBuilder.build());  
        }  
    }  
      
    @Override  
    public void onDestroy() {  
        //从前台状态中移除此服务，如果需要更多内存，则允许它被杀死。但这不会stop服务运行，只是 takes it out of 前台状态  
        stopForeground(true);  
        super.onDestroy();  
    }  
}  
```  
  
2019-1-24  
