| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Intent 简介 结构 传递数据 常见Action  
***  
目录  
===  

- [简介](#简介)
- [Intent 的组成结构](#Intent-的组成结构)
	- [Component name](#Component-name)
	- [Action](#Action)
	- [Data](#Data)
	- [Category](#Category)
	- [Extras](#Extras)
	- [Flags](#Flags)
- [与 Intent 相关的 API](#与-Intent-相关的-API)
- [通过 Intent 传递数据](#通过-Intent-传递数据)
	- [可以传递的数据类型](#可以传递的数据类型)
	- [Bundle](#Bundle)
	- [小技巧：通过 Intent 传递接口](#小技巧：通过-Intent-传递接口)
- [Intent 中定义的常量](#Intent-中定义的常量)
	- [Category](#Category)
	- [Extra](#Extra)
	- [Flag](#Flag)
	- [Activity 相关的 Action](#Activity-相关的-Action)
	- [广播相关的 Action](#广播相关的-Action)
- [Settings 中定义的 Action 常量](#Settings-中定义的-Action-常量)
	- [常用设置](#常用设置)
	- [测试代码](#测试代码)
  
# 简介  
Intent 是 Android 程序中各个`组件之间`进行交互的重要方式之一，它既可以在不同 Activity 中指定想要完成的动作，还可以在不同组件间进行数据传递。  
Intent作为联系各Activity之间的纽带，其作用并不仅仅限于简单的数据传递。通过其自带的属性，可以方便的完成很多较为复杂的操作。例如调用拨号功能、自动调用合适的程序打开不同类型的文件等等。诸如此类，都可以通过 Intent 来完成。  
  
官方说明：  
> An intent is an abstract description of an operation to be performed. It can be used with startActivity to launch an Activity, broadcastIntent to send it to any interested BroadcastReceiver components, and startService(Intent) or bindService(Intent, ServiceConnection, int) to communicate with a background Service.  
  
> An Intent provides a facility工具 for performing late runtime binding between the code in different applications. Its most significant重要的 use is in the launching of activities, where it can be thought of as the glue胶合 between activities. It is basically a passive被动的 data structure holding an abstract description of an action to be performed.  
  
显式意图：必须指定要激活的组件的`类名和完整包名`，一般激活`自己应用的组件`的时候采用显示意图  
隐式意图：只需要指定要`动作和数据`就可以，激活`其他应用的组件`时候使用， 不需要关心对方的包名和类名  
  
# Intent 的组成结构  
![](http://phz2kt37i.bkt.clouddn.com/blog/181115/54dd5Jc038.png?imageslim)  
Intent 由以下六个部分组成：  
  
## Component name  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181117/FF7F0e58df.jpg?imageslim)  
- 即组件名称，是要处理这个Intent对象的组件名称。  
- 组件名称对象由`ComponentName`类来封装，组件名称包含`包名称和类名称`，被声明在`AndroidManifest.xml`文件中。  
- 组件名称通过 `setComponent()、setClass()、setClassName()`设置，通过`getComponent()`获取。  
- 组件名称是一个可选项，如果被设置，那么Intent对象就显式指定了要转向的组件；如果没有被设置，则Intent对象需要根据其他信息进行筛选查找。  
  
## Action  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181117/fJ7Hgh1BK8.jpg?imageslim)  
- Action是指Intent要完成的动作，是一个字符串常量。使用 `setAction()` 和 `getAction()` 来设置和读取Action属性。  
- 注意：一个 Intent 只能指定一个 action，但是一 个Activity(或广播等组件)可以设置(监听、匹配)多个 action(即intent-filter中可以设置多个action属性)，这是两个不同的概念  
  
## Data  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181117/DBcjl377Jk.jpg?imageslim)  
- Data属性用来执行执行动作的`Uri和mimeType`，不同的动作有不同的数据规格。  
- 当组件能够匹配intent时，知道mimeType是很重要的。比如，一个展示图像的组件不应该被叫去播放一个音频。  
- 很多情况下，从Uri可以看出数据类型，比如`content:`表示数据是在设备上，但是是由`content provider`控制。  
- 数据类型也可以显式指定，比如使用`setData(Uri)`指定数据为Uri，使用`setType(String type) `指定mimeType，使用 `setDataAndType()` 同时指定Uri和MIMEType。读取的时候URI用`getData()`，mimeType用`getType()`。  
  
## Category  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181117/4dDAH32K0G.jpg?imageslim)  
- Category是一个字符串，提供了额外的信息，有关于能够处理这个Intent对象的组件种类，一般在隐式地启动activity时需要用到。  
- 与category相应的方法有添加`addCategory()`、移除`removeCategory()` 和获取`getCategories()`。  
- Intent的 Category 默认为`CATEGORY_DEFAULT`，为防止出bug，建议`intent-filter`中都添加一个 category 属性  
  
## Extras  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181117/7diI05cdDK.jpg?imageslim)  
- 传递给Intent的额外数据，以`Bundle`的形式定义，就是一些`键值对`。  
- 数据可以被作为一个Bundle对象被使用，利用 `putExtras()` 和 `getExtras()` 方法。  
  
## Flags  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181117/EifEjKcLjd.jpg?imageslim)  
- 各种类型的Flag。很多是用来指定Android系统`如何启动activity`，还有启动了activity后`如何对待它`。  
- 所有用到的值都定义在Intent类中  
  
# 与 Intent 相关的 API  
**Activity中的相关方法**  
```java  
public Intent getIntent()  
protected void onActivityResult(int requestCode, int resultCode, Intent data)  
public final void setResult(int resultCode, Intent data)  
public ComponentName startService(Intent service)   
public boolean stopService(Intent name)  
public boolean bindService(Intent service, ServiceConnection conn, int flags)  
public void unbindService(ServiceConnection conn)  
public void sendBroadcast(Intent intent, String receiverPermission)   
```  
  
**Intent的构造方法**  
```java  
Intent()  
Intent(Context packageContext, Class<?> cls)  
Intent(Intent o)  
Intent(String action)  
Intent(String action, Uri uri)//Uri即为通过setData设置的数据  
Intent(String action, Uri uri, Context packageContext, Class<?> cls)  
```  
  
**Intent 中的方法**  
```java  
public Intent addCategory(String category)  为一个intent对象增加一个category  
public Set<String> getCategories()  
public Intent putExtra(String name, Bundle value)//可以添加很多类型的数据  
public Bundle getExtras()  
public Intent setAction(String action)  
public Intent setClass(Context packageContext, Class<?> cls)  
public Intent setData(Uri data)  
public Intent setDataAndType(Uri data, String type)//Set an explicit MIME data type.  
```  
  
# 通过 Intent 传递数据  
## 可以传递的数据类型  
- 八大`基本类型` 以及 `String`、`CharSequence` 类型及其对于的`数组`类型  
  
- 可序列化对象及其数组，也可将对象序列化为Json字符串后传递：`new Gson().toJson(obj)`  
```java  
public Intent putExtra(String name, Serializable value) //Serializable 数组的类型仍然是 Serializable  
public Intent putExtra(String name, Parcelable value) //BitMap默认实现了Parcelable接口  
public Intent putExtra(String name, Parcelable[] value)  
```  
  
- 特定泛型的一些`ArrayList`(而非List)：`ArrayList<Integer>`、`ArrayList<String>`、`ArrayList<CharSequence>`、`ArrayList<? extends Parcelable>`  
```java  
public Intent putIntegerArrayListExtra(String name, ArrayList<Integer> value)  
public Intent putStringArrayListExtra(String name, ArrayList<String> value)  
public Intent putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value)  
public Intent putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value)  
```  
  
- 传递 Bundle 对象或其他 Intent 对象  
```java  
public Intent putExtra(String name, Bundle value)  
public Intent putExtras(Bundle extras)  
public Intent putExtras(Intent src)  
```  
  
## Bundle  
本质上，以上所有类型的数据都是通过 Bundle 传递过去的，Bundle 中有所有以上类型相对应的方法：  
```java  
private Bundle mExtras;    
public Intent putExtra(String name, int value) {  
    if (mExtras == null) mExtras = new Bundle();  
    mExtras.putInt(name, value); //Bundle 中的方法名更精准，传递什么类型数据就用相对应的哪个方法  
    return this;  
}    
public Intent putExtra(String name, int[] value) {  
    if (mExtras == null) mExtras = new Bundle();  
    mExtras.putIntArray(name, value);  
    return this;  
}    
```  
  
## 小技巧：通过 Intent 传递接口  
定义了一个接口(回调类)：  
```java  
public interface A {  
    void a(String text);  
}  
```  
  
根据需求定义了一个实现类：  
```java  
public class B implements A, Serializable {  
    @Override  
    public void a(String text) {  
        Log.i("bqt", "回调:" + text);  
    }  
}  
```  
  
通过 Intent 传递这个实现类：  
```java  
Intent intent = new Intent(this, SecondActivity.class);  
intent.putExtra("a", new B());  
startActivity(intent);  
```  
  
然后就可以在需要时回调：  
```java  
A a = (A) getIntent().getSerializableExtra("a"); //强转给接口即可  
a.a("SecondActivity");  
```  
  
# Intent 中定义的常量  
## Category  
- `CATEGORY_DEFAULT`  //Android系统中默认的执行方式，按照普通Activity的执行方式执行。  
- `CATEGORY_BROWSABLE`  //能够被浏览器安全调用的activity必须支持这个category。设置该组件可以使用浏览器启动  
- `CATEGORY_ALTERNATIVE`  //设置这个activity是否可以被认为是用户正在浏览的数据的一个可选择的action    
- `CATEGORY_SELECTED_ALTERNATIVE`  //设置这个activity是否可以被认为是用户当前选择的数据的一个可选择的action    
- `CATEGORY_TAB`  //想要在已有的TabActivity内部作为一个Tab使用    
- `CATEGORY_LAUNCHER`  //设置该组件为在当前应用程序启动器中优先级最高的Activity，通常为入口ACTION_MAIN配合使用。　  
- `CATEGORY_INFO`  //  
- `CATEGORY_HOME`  //主activity，当应用程序启动时，它是第一个显示的activity  
- `CATEGORY_PREFERENCE`  //这个activity是一个选项卡  
- `CATEGORY_DEVELOPMENT_PREFERENCE`  //  
- `CATEGORY_EMBED`  //可以运行在父activity容器内  
- `CATEGORY_APP_MARKET`  //这个activity允许用户浏览和下载新的应用程序    
- `CATEGORY_MONKEY`  //这个activity可能被monkey或者其他的自动测试工具执行    
- `CATEGORY_TEST`  //供测试使用（一般情况不使用）    
- `CATEGORY_UNIT_TEST`  //联合测试使用    
- `CATEGORY_SAMPLE_CODE`  //作为一个简单的代码示例使用（一般情况下不使用）    
- `CATEGORY_OPENABLE`  //用来指示一个GET_CONTENT意图只希望ContentResolver.openInputStream能够打开URI    
- `CATEGORY_CAR_DOCK`  //  
- `CATEGORY_DESK_DOCK`  //  
- `CATEGORY_LE_DESK_DOCK`  //  
- `CATEGORY_HE_DESK_DOCK`  //  
- `CATEGORY_CAR_MODE`  //  
- `CATEGORY_APP_BROWSER`  //和ACTION_MAIN一起使用，用来启动浏览器应用程序    
- `CATEGORY_APP_CALCULATOR`  //和ACTION_MAIN一起使用，用来启动计算器应用程序    
- `CATEGORY_APP_CALENDAR`  //和ACTION_MAIN一起使用，用来启动日历应用程序    
- `CATEGORY_APP_CONTACTS`  //和ACTION_MAIN一起使用，用来启动联系人应用程序    
- `CATEGORY_APP_EMAIL`  //和ACTION_MAIN一起使用，用来启动邮件应用程序    
- `CATEGORY_APP_GALLERY`  //和ACTION_MAIN一起使用，用来启动图库应用程序    
- `CATEGORY_APP_MAPS`  //和ACTION_MAIN一起使用，用来启动地图应用程序    
- `CATEGORY_APP_MESSAGING`  //和ACTION_MAIN一起使用，用来启动短信应用程序    
- `CATEGORY_APP_MUSIC`  //和ACTION_MAIN一起使用，用来启动音乐应用程序    
  
## Extra  
- `EXTRA_ALARM_COUNT`   
- `EXTRA_BCC` 存放邮件密送人地址的字符串数组  
- `EXTRA_CC` 存放邮件抄送人地址的字符串数组  
- `EXTRA_CHANGED_COMPONENT_NAME`   
- `EXTRA_DATA_REMOVED`   
- `EXTRA_DOCK_STATE`   
- `EXTRA_DOCK_STATE_HE_DESK`   
- `EXTRA_DOCK_STATE_LE_DESK`   
- `EXTRA_DOCK_STATE_CAR`   
- `EXTRA_DOCK_STATE_DESK`   
- `EXTRA_DOCK_STATE_UNDOCKED`   
- `EXTRA_DONT_KILL_APP`   
- `EXTRA_EMAIL` 存放邮件地址的字符串数组  
- `EXTRA_INITIAL_INTENTS`   
- `EXTRA_INTENT`   
- `EXTRA_KEY_EVENT` 以KeyEvent对象方式存放触发Intent的按键  
- `EXTRA_ORIGINATING_URI`   
- `EXTRA_PHONE_NUMBER` 存放调用ACTION_CALL时的电话号码  
- `EXTRA_REFERRER`   
- `EXTRA_REMOTE_INTENT_TOKEN`   
- `EXTRA_REPLACING`   
- `EXTRA_SHORTCUT_ICON`   
- `EXTRA_SHORTCUT_ICON_RESOURCE`   
- `EXTRA_SHORTCUT_INTENT`   
- `EXTRA_STREAM`   
- `EXTRA_SHORTCUT_NAME`   
- `EXTRA_SUBJECT` 存放邮件主题字符串  
- `EXTRA_TEMPLATE`   
- `EXTRA_TEXT` 存放邮件内容  
- `EXTRA_TITLE`   
- `EXTRA_UID`   
  
## Flag  
- `FLAG_GRANT_READ_URI_PERMISSION`  //  
- `FLAG_GRANT_WRITE_URI_PERMISSION`  //  
- `FLAG_FROM_BACKGROUND`  //  
- `FLAG_DEBUG_LOG_RESOLUTION`  //  
- `FLAG_EXCLUDE_STOPPED_PACKAGES`  //  
- `FLAG_INCLUDE_STOPPED_PACKAGES`  //  
- `FLAG_ACTIVITY_NO_HISTORY`  //退出后不会存在于任务栈中  
- `FLAG_ACTIVITY_SINGLE_TOP`  //单一顶部  
- `FLAG_ACTIVITY_NEW_TASK`  //在新任务栈中打开  
- `FLAG_ACTIVITY_MULTIPLE_TASK`  //  
- `FLAG_ACTIVITY_CLEAR_TOP`  //若此Activity已存在于任务栈中，再次启动时弹出它所有顶部的Activity  
- `FLAG_ACTIVITY_FORWARD_RESULT`  //  
- `FLAG_ACTIVITY_PREVIOUS_IS_TOP`  //  
- `FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS`  //  
- `FLAG_ACTIVITY_BROUGHT_TO_FRONT`  //  
- `FLAG_ACTIVITY_RESET_TASK_IF_NEEDED`  //  
- `FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY`  //  
- `FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET`  //  
- `FLAG_ACTIVITY_NO_USER_ACTION`  //  
- `FLAG_ACTIVITY_REORDER_TO_FRONT`  //  
- `FLAG_ACTIVITY_NO_ANIMATION`  //  
- `FLAG_ACTIVITY_CLEAR_TASK`  //  
- `FLAG_ACTIVITY_TASK_ON_HOME`  //  
- `FLAG_RECEIVER_REGISTERED_ONLY`  //  
- `FLAG_RECEIVER_REPLACE_PENDING`  //  
- `FLAG_RECEIVER_FOREGROUND`  //  
- `FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT`  //  
- `FLAG_RECEIVER_BOOT_UPGRADE`  //  
  
## Activity 相关的 Action  
- `ACTION_MAIN` //Android Application的入口，每个Android应用必须且只能包含一个此类型的Action声明  
- `ACTION_VIEW` //系统根据不同的Data类型，通过已注册的对应Application显示数据。  
- `ACTION_ATTACH_DATA` //  
- `ACTION_EDIT` //系统根据不同的Data类型，通过已注册的对应Application编辑示数据。  
- `ACTION_PICK` //从图库获取照片  
- `ACTION_CHOOSER` //  
- `ACTION_GET_CONTENT` //  
- `ACTION_DIAL` //打开系统默认的拨号程序，如果Data中设置了电话号码，则自动在拨号程序中输入此号码。  
- `ACTION_CALL` //直接呼叫Data中所带的号码  
- `ACTION_SEND` //由用户指定发送方式进行数据发送操作  
- `ACTION_SENDTO` //系统根据不同的Data类型，通过已注册的对应Application进行数据发送操作  
- `ACTION_ANSWER` //接听来电  
- `ACTION_INSERT` //  
- `ACTION_DELETE` //卸载应用程序  
- `ACTION_RUN` //  
- `ACTION_SYNC` //同步手机与数据服务器上的数据  
- `ACTION_PICK_ACTIVITY` //  
- `ACTION_SEARCH` //  
- `ACTION_WEB_SEARCH` //  
- `ACTION_FACTORY_TEST` //  
  
## 广播相关的 Action  
- `Intent.ACTION_AIRPLANE_MODE_CHANGED`;//关闭或打开飞行模式时的广播  
- `Intent.ACTION_BATTERY_CHANGED`;//充电状态，或者电池的电量发生变化  
- `Intent.ACTION_BATTERY_LOW`;//表示电池电量低  
- `Intent.ACTION_BATTERY_OKAY`;//表示电池电量充足，即从电池电量低变化到饱满时会发出广播  
- `Intent.ACTION_BOOT_COMPLETED`;//在系统启动完成后，这个动作被广播一次（只有一次）。  
- `Intent.ACTION_CAMERA_BUTTON`;//按下照相时的拍照按键(硬件按键)时发出的广播  
- `Intent.ACTION_CLOSE_SYSTEM_DIALOGS`;//当屏幕超时进行锁屏时  
- `Intent.ACTION_CONFIGURATION_CHANGED`;//设备当前设置被改变时发出的广播  
- `Intent.ACTION_DATE_CHANGED`;//设备日期发生改变时会发出此广播  
- `Intent.ACTION_DEVICE_STORAGE_LOW`;//设备内存不足时发出的广播,此广播只能由系统使用，其它APP不可用  
- `Intent.ACTION_DEVICE_STORAGE_OK`;//设备内存从不足到充足时发出的广播,此广播只能由系统使用，其它APP不可用  
- `Intent.ACTION_DOCK_EVENT`;//  
- `Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE`;//移动APP完成之后，发出的广播(移动是指:APP2SD)  
- `Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE`;//正在移动APP时，发出的广播(移动是指:APP2SD)  
- `Intent.ACTION_GTALK_SERVICE_CONNECTED`;//Gtalk已建立连接时发出的广播  
- `Intent.ACTION_GTALK_SERVICE_DISCONNECTED`;//Gtalk已断开连接时发出的广播  
- `Intent.ACTION_HEADSET_PLUG`;//在耳机口上插入耳机时发出的广播  
- `Intent.ACTION_INPUT_METHOD_CHANGED`;//改变输入法时发出的广播  
- `Intent.ACTION_LOCALE_CHANGED`;//设备当前区域设置已更改时发出的广播  
- `Intent.ACTION_MANAGE_PACKAGE_STORAGE`;//  
- `Intent.ACTION_MEDIA_BAD_REMOVAL`;//未正确移除SD卡但已把SD卡取出来时发出的广播  
- `Intent.ACTION_MEDIA_BUTTON`;//按下"Media Button" 按键时发出的广播,假如有"Media Button" 按键的话(硬件按键)  
- `Intent.ACTION_MEDIA_CHECKING`;//插入外部储存装置，比如SD卡时，系统会检验SD卡，此时发出的广播  
- `Intent.ACTION_MEDIA_EJECT`;//已拔掉外部大容量储存设备发出的广播  
- `Intent.ACTION_MEDIA_MOUNTED`;//插入SD卡并且已正确安装（识别）时发出的广播。广播：扩展介质被插入，而且已经被挂载  
- `Intent.ACTION_MEDIA_NOFS`;//  
- `Intent.ACTION_MEDIA_REMOVED`;//外部储存设备已被移除，不管有没正确卸载,都会发出此广播？。广播：扩展介质被移除  
- `Intent.ACTION_MEDIA_SCANNER_FINISHED`;//广播：已经扫描完介质的一个目录  
- `Intent.ACTION_MEDIA_SCANNER_SCAN_FILE`;//  
- `Intent.ACTION_MEDIA_SCANNER_STARTED`;//开始扫描介质的一个目录  
- `Intent.ACTION_MEDIA_SHARED`;//扩展介质的挂载被解除 (unmount)，因为它已经作为 USB 大容量存储被共享  
- `Intent.ACTION_MEDIA_UNMOUNTABLE`;//  
- `Intent.ACTION_MEDIA_UNMOUNTED`;//扩展介质存在，但是还没有被挂载 (mount)。  
- `Intent.ACTION_NEW_OUTGOING_CALL`;//拨号时  
- `Intent.ACTION_PACKAGE_ADDED`;//成功的安装APK之后的广播  
- `Intent.ACTION_PACKAGE_CHANGED`;//一个已存在的应用程序包已经改变，包括包名  
- `Intent.ACTION_PACKAGE_DATA_CLEARED`;//清除一个应用程序的数据时发出的广播  
- `Intent.ACTION_PACKAGE_INSTALL`;//触发一个下载并且完成安装时发出的广播，比如在电子市场里下载应用  
- `Intent.ACTION_PACKAGE_REMOVED`;//成功的删除某个APK之后发出的广播  
- `Intent.ACTION_PACKAGE_REPLACED`;//替换一个现有的安装包时发出的广播  
- `Intent.ACTION_PACKAGE_RESTARTED`;//用户重新开始一个包，包的所有进程将被杀死  
- `Intent.ACTION_POWER_CONNECTED`;//插上外部电源时发出的广播  
- `Intent.ACTION_POWER_DISCONNECTED`;//已断开外部电源连接时发出的广播  
- `Intent.ACTION_PROVIDER_CHANGED`;//  
- `Intent.ACTION_REBOOT`;//重启设备时的广播  
- `Intent.ACTION_SCREEN_OFF`;//屏幕被关闭之后的广播  
- `Intent.ACTION_SCREEN_ON`;//屏幕被打开之后的广播  
- `Intent.ACTION_SHUTDOWN`;//关闭系统时发出的广播  
- `Intent.ACTION_TIMEZONE_CHANGED`;//时区发生改变时发出的广播  
- `Intent.ACTION_TIME_CHANGED`;//时间被设置时发出的广播  
- `Intent.ACTION_TIME_TICK`;//当前时间已经变化  
- `Intent.ACTION_UID_REMOVED`;//一个用户ID已经从系统中移除发出的广播  
- `Intent.ACTION_UMS_CONNECTED`;//设备已进入USB大容量储存状态时发出的广播  
- `Intent.ACTION_UMS_DISCONNECTED`;//设备已从USB大容量储存状态转为正常状态时发出的广播  
- `Intent.ACTION_USER_PRESENT`;//  
- `Intent.ACTION_WALLPAPER_CHANGED`;//设备墙纸已改变时发出的广播  
  
# Settings 中定义的 Action 常量  
## 常用设置  
- `ACTION_ACCESSIBILITY_SETTINGS`   辅助功能模块的显示设置  
- `ACTION_ADD_ACCOUNT`   显示屏幕上创建一个新帐户添加帐户  
- `ACTION_AIRPLANE_MODE_SETTINGS`   显示设置，以允许进入/退出飞行模式  
- `ACTION_APN_SETTINGS`   显示设置，以允许配置APN  
- `ACTION_APPLICATION_DETAILS_SETTINGS`   有关特定应用程序的详细信息的显示屏幕  
- `ACTION_APPLICATION_DEVELOPMENT_SETTINGS`   显示设置，以允许应用程序开发相关的设置配置  
- `ACTION_APPLICATION_SETTINGS`   显示设置，以允许应用程序相关的设置配置  
- `ACTION_BLUETOOTH_SETTINGS`   显示设置，以允许蓝牙配置  
- `ACTION_DATA_ROAMING_SETTINGS`   选择of2G/3G显示设置  
- `ACTION_DATE_SETTINGS`   显示日期和时间设置，以允许配置  
- `ACTION_DEVICE_INFO_SETTINGS`   显示一般的设备信息设置（序列号，软件版本，电话号码，等）  
- `ACTION_DISPLAY_SETTINGS`   显示设置，以允许配置显示  
- `ACTION_INPUT_METHOD_SETTINGS`   特别配置的输入方法，允许用户启用输入法的显示设置  
- `ACTION_INPUT_METHOD_SUBTYPE_SETTINGS`   显示设置来启用/禁用输入法亚型  
- `ACTION_INTERNAL_STORAGE_SETTINGS`   内部存储的显示设置  
- `ACTION_LOCALE_SETTINGS`   显示设置，以允许配置的语言环境  
- `ACTION_LOCATION_SOURCE_SETTINGS`   显示设置，以允许当前位置源的配置  
- `ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS`   显示设置来管理所有的应用程序  
- `ACTION_MANAGE_APPLICATIONS_SETTINGS`   显示设置来管理安装的应用程序  
- `ACTION_MEMORY_CARD_SETTINGS`   显示设置为存储卡存储  
- `ACTION_NETWORK_OPERATOR_SETTINGS`   选择网络运营商的显示设置  
- `ACTION_PRIVACY_SETTINGS`   显示设置，以允许配置隐私选项  
- `ACTION_QUICK_LAUNCH_SETTINGS`   显示设置，以允许快速启动快捷键的配置  
- `ACTION_SEARCH_SETTINGS`   全局搜索显示设置  
- `ACTION_SECURITY_SETTINGS`   显示设置，以允许配置的安全性和位置隐私  
- `ACTION_SETTINGS`   显示系统设置  
- `ACTION_SOUND_SETTINGS`   显示设置，以允许配置声音和音量  
- `ACTION_SYNC_SETTINGS`   显示设置，以允许配置同步设置  
- `ACTION_USER_DICTIONARY_SETTINGS`   显示设置来管理用户输入字典  
- `ACTION_WIFI_IP_SETTINGS`   显示设置，以允许配置一个静态IP地址的Wi – Fi  
- `ACTION_WIFI_SETTINGS`   显示设置，以允许Wi – Fi配置  
- `ACTION_WIRELESS_SETTINGS`   显示设置，以允许配置，如Wi – Fi，蓝牙和移动网络的无线控制  
  
## 测试代码  
```java  
public class MainActivity extends ListActivity {  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"【设置界面相关Action】", "辅助功能(无障碍)", "添加帐户", "无线和网络-1", "APN(接入点名称)", "设置中指定应用程序的详细信息页面","显示所有安装的应用-1", "显示所有安装的应用-2", "蓝牙", "移动网络设置设置，2G/3G/4G", "日期和时间", "关于手机－设备详细配置信息","显示相关设置，如字体、亮度等", "语言和输入法", "启用/禁用输入法亚型", "存储-1", "语言", "位置信息－定位", "显示所有安装的应用-3","显示所有安装的应用-4", "存储-2", "可用网络", "备份和重置", "快捷键配置－可能无效", "全局搜索显示设置－崩溃", "安全", "系统设置主页面","声音(和振动)", "账户同步设置", "个人字典", "IP设置(WLAN高级设置)", "WLAN设置", "无线和网络-2"};  
        for (int i = 1; i < array.length; i++) {  
            array[i] = i + "、" + array[i];  
        }  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
    }  
      
    @SuppressLint("SdCardPath")  
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        switch (position) {  
            case 0:  
                return;  
            case 1:  
                intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);//辅助功能(无障碍)模块的显示设置。  
                break;  
            case 2:  
                intent.setAction(Settings.ACTION_ADD_ACCOUNT);//显示屏幕上创建一个新帐户添加帐户。  
                break;  
            case 3:  
                intent.setAction(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//进入/退出飞行模式。  
                break;  
            case 4:  
                intent.setAction(Settings.ACTION_APN_SETTINGS);//配置的APN。  
                break;  
            case 5:  
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);//有关特定应用程序的详细信息的显示屏幕。  
                intent.setData(Uri.fromParts("package", getPackageName(), null));  
                break;  
            case 6:  
                intent.setAction(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);//显示设置，以允许应用程序开发相关的设置配置  
            case 7:  
                intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);//应用程序相关的设置配置  
                break;  
            case 8:  
                intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);//蓝牙配置  
                break;  
            case 9:  
                intent.setAction(Settings.ACTION_DATA_ROAMING_SETTINGS);//   选择of2G/3G显示设置  
                break;  
            case 10:  
                intent.setAction(Settings.ACTION_DATE_SETTINGS);//显示日期和时间设置，以允许配置  
                break;  
            case 11:  
                intent.setAction(Settings.ACTION_DEVICE_INFO_SETTINGS);//显示一般的设备信息设置（序列号，软件版本，电话号码，等）  
                break;  
            case 12:  
                intent.setAction(Settings.ACTION_DISPLAY_SETTINGS);//显示设置，以允许配置显示  
                break;  
            case 13:  
                intent.setAction(Settings.ACTION_INPUT_METHOD_SETTINGS);//   特别配置的输入方法，允许用户启用输入法的显示设置  
                break;  
            case 14:  
                intent.setAction(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS);//   显示设置来启用/禁用输入法亚型  
                break;  
            case 15:  
                intent.setAction(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);//内部存储的显示设置  
                break;  
            case 16:  
                intent.setAction(Settings.ACTION_LOCALE_SETTINGS);//显示设置，以允许配置的语言环境  
                break;  
            case 17:  
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);//显示设置，以允许当前位置源的配置  
                break;  
            case 18:  
                intent.setAction(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);//显示设置来管理所有的应用程序  
                break;  
            case 19:  
                intent.setAction(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);//显示设置来管理安装的应用程序  
                break;  
            case 20:  
                intent.setAction(Settings.ACTION_MEMORY_CARD_SETTINGS);//显示设置为存储卡存储  
                break;  
            case 21:  
                intent.setAction(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);//选择网络运营商的显示设置  
                break;  
            case 22:  
                intent.setAction(Settings.ACTION_PRIVACY_SETTINGS);//显示设置，以允许配置隐私选项  
                break;  
            case 23:  
                intent.setAction(Settings.ACTION_QUICK_LAUNCH_SETTINGS);//显示设置，以允许快速启动快捷键的配置  
                break;  
            case 24:  
                intent.setAction(Settings.ACTION_SEARCH_SETTINGS);//全局搜索显示设置  
                break;  
            case 25:  
                intent.setAction(Settings.ACTION_SECURITY_SETTINGS);//显示设置，以允许配置的安全性和位置隐私  
                break;  
            case 26:  
                intent.setAction(Settings.ACTION_SETTINGS);//显示系统设置  
                break;  
            case 27:  
                intent.setAction(Settings.ACTION_SOUND_SETTINGS);//显示设置，以允许配置声音和音量  
                break;  
            case 28:  
                intent.setAction(Settings.ACTION_SYNC_SETTINGS);//显示设置，以允许配置同步设置  
                break;  
            case 29:  
                intent.setAction(Settings.ACTION_USER_DICTIONARY_SETTINGS);//显示设置来管理用户输入字典  
                break;  
            case 30:  
                intent.setAction(Settings.ACTION_WIFI_IP_SETTINGS);//显示设置，以允许配置一个静态IP地址的Wi – Fi  
                break;  
            case 31:  
                intent.setAction(Settings.ACTION_WIFI_SETTINGS);//显示设置，以允许Wi – Fi配置  
                break;  
            case 32:  
                intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);//显示设置，以允许配置，如Wi – Fi，蓝牙和移动网络的无线控制  
                break;  
        }  
        startActivity(intent);  
    }  
}  
```  
  
  
2018-11-17  
