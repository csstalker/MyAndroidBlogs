| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
异常捕获 崩溃 Bugly ACRC 简介 总结  
***  
目录  
===  

- [腾讯 Bugly](#腾讯-Bugly)
	- [介绍](#介绍)
	- [最简单的接入配置](#最简单的接入配置)
	- [高级功能](#高级功能)
		- [上传开发者catch的异常](#上传开发者catch的异常)
		- [js 的异常捕获功能](#js-的异常捕获功能)
		- [配置用户策略 UserStrategy](#配置用户策略-UserStrategy)
		- [设置 Crash 回调](#设置-Crash-回调)
		- [使用 MultiDex 的注意事项](#使用-MultiDex-的注意事项)
		- [上报进程控制](#上报进程控制)
	- [其他设置](#其他设置)
		- [设置自定义标签](#设置自定义标签)
		- [设置自定义Map参数](#设置自定义Map参数)
		- [设置开发设备](#设置开发设备)
		- [设置用户ID](#设置用户ID)
		- [上传自定义日志](#上传自定义日志)
- [ACRA：安卓应用崩溃报告](#ACRA：安卓应用崩溃报告)
	- [介绍](#介绍)
	- [基本配置](#基本配置)
	- [初始化](#初始化)
		- [定义的注解](#定义的注解)
		- [可用的 Builder](#可用的-Builder)
	- [完整使用案例](#完整使用案例)
- [CustomActivityOnCrash：自定义崩溃界面](#CustomActivityOnCrash：自定义崩溃界面)
	- [基本使用](#基本使用)
	- [自定义配置](#自定义配置)
	- [CustomEventListener 监听](#CustomEventListener-监听)
	- [完全自定义错误 activity](#完全自定义错误-activity)
	- [测试代码](#测试代码)
		- [自定义崩溃Activity](#自定义崩溃Activity)
		- [设置](#设置)
	- [工作原理](#工作原理)
	- [不兼容性和免责声明](#不兼容性和免责声明)
  
# 腾讯 Bugly  
## 介绍  
[官网](https://bugly.qq.com/v2/)     
[文档](https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20180427191736)     
[SDK下载](https://bugly.qq.com/v2/sdkDownload)     
[产品列表](https://bugly.qq.com/v2/workbench/apps)  
  
腾讯Bugly，为移动开发者提供专业的异常上报和运营统计，帮助开发者快速发现并解决异常，同时掌握产品运营动态，及时跟进用户反馈。  
应用集成SDK后，即可在Web站点查看应用上报的崩溃数据和联网数据。  
  
功能  
- 异常概览：查看今日实时统计、崩溃趋势、崩溃排行和TOP20崩溃问题等信息  
- 崩溃分析/卡顿分析/错误分析：查看上报问题的列表  
- 问题详情：查看上报问题的详细信息  
- 高级搜索：通过各种条件快速查找需要定位分析的异常  
- 运营统计：查看应用的联网统计信息  
  
平台术语  
- 异常：App在运行过程中发生的崩溃、卡顿、ANR、错误，统称为异常。  
- 崩溃：用户在使用App过程中发生一次闪退，计为一次崩溃。  
- 卡顿：用户在使用App过程中发生卡顿现象，计为一次卡顿，卡顿时间默认是5s，也支持自定义时间。  
- ANR：用户在使用App过程中出现弹框，提示应用无响应，计为一次ANR，ANR仅用于Android平台应用。  
- 错误：主动上报的Exception、Error，或脚本(如C#、Lua、JS等)错误，统称为错误。  
- 发生次数：一个异常发生且被记录上报，计为一次异常发生。  
- 影响用户：一台设备发生异常，计为一个影响用户。 在指定时间范围内，若一个设备发生多次异常，只算一个影响用户。  
- 用户异常率：即影响用户/联网用户的比值，诸如用户崩溃率、用户卡顿率、用户ANR率、用户错误率等。  
- 次数异常率：即发生次数/联网次数的比值，诸如次数崩溃率、次数卡顿率、次数ANR率、次数错误率等。  
- 联网次数：即 启动次数+跨天联网次数。  
  
- 跨天联网：用户没有启动应用，只有应用进程在后台运行，且超过零点，计为一次跨天联网。  
  
- 启动次数：应用完全退出后重新启动，计为一次启动；应用被切换至后台后，30秒后被切换至前台，计为一次启动，若未超过30秒切换至前台，不算一次启动。  
- 联网用户：以设备为判断指标，每一个发生联网的设备，即为一个联网用户；在指定时间范围内，若一个设备重复发生联网行为，只算一个联网用户。  
  
## 最简单的接入配置  
[Bugly Android SDK 使用指南](https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20180521124306)  
  
配置依赖  
```  
implementation 'com.tencent.bugly:crashreport:2.6.6'  
```  
  
权限  
```xml  
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>  
<uses-permission android:name="android.permission.INTERNET"/>  
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>  
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>  
```  
  
避免混淆  
```  
-dontwarn com.tencent.bugly.**  
-keep public class com.tencent.bugly.**{*;}  
```  
  
初始化  
```java  
CrashReport.initCrashReport(this, "6c32268c59", true);//APPID、是否为调试模式  
```  
第三个参数为SDK调试模式开关，建议在测试阶段建议设置成true，发布时设置为false。  
调试模式的行为特性如下：  
- 输出详细的Bugly SDK的Log  
- 每一条Crash都会被立即上报  
- 自定义日志将会在Logcat中输出  
  
测试  
```java  
switch (position) {  
   case 0:  
      Log.i("bqt", "" + string.length());//空指针异常  
      break;  
   case 1:  
      CrashReport.testJavaCrash();//测试Java Crash  
      break;  
   case 2:  
      CrashReport.testANRCrash();//测试ANR Crash  
      break;  
   case 3:  
      CrashReport.testNativeCrash();//测试Native Crash  
      break;  
}  
```  
  
## 高级功能  
  
### 上传开发者catch的异常  
您可能会关注某些重要异常的Catch情况。我们提供了上报这类异常的接口。例：统计某个重要的数据库读写问题比例。  
```java  
try {  
    //...  
} catch (Throwable thr) {  
    CrashReport.postCatchedException(thr);  // bugly会将这个throwable上报  
}  
```  
  
### js 的异常捕获功能  
Bugly Android SDK 1.2.8及以上版本提供了Javascript的异常捕获和上报能力，以便开发者可以感知到 WebView 中发生的 Javascript  异常。  
在 WebChromeClient 的 onProgressChanged 函数中调用接口：  
```java  
webView.setWebChromeClient(new WebChromeClient() {  
   @Override  
   public void onProgressChanged(WebView webView, int progress) {  
      CrashReport.setJavascriptMonitor(webView, true);//设置Javascript的异常监控（自动注入方式）  
      super.onProgressChanged(webView, progress);  
   }  
});  
```  
参数：WebView webView 指定被监控的 webView  
参数：boolean autoInject 是否自动注入 Bugly.js 文件  
返回：true 设置成功；false 设置失败  
  
注意：  
- Bugly.js 文件在Bugly SDK包中，可以在HTML手动嵌入；  
- 如果使用自动集成SDK方式，可以使用自动注入和手动注入两种方式。  
- 由于Android 4.4以下版本存在反射漏洞，接口默认只对Android 4.4及以上版本有效；  
- 接口不会设置webView的WebViewClient和Listener；  
- 接口默认会开启webView的JS执行能力；  
  
### 配置用户策略 UserStrategy  
UserStrategy是Bugly的初始化扩展类，在这里您可以修改本次初始化Bugly数据的版本、渠道及部分初始化行为：  
```java  
CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this)//初始化时增加上报策略  
      .setAppChannel("myChannel")  //设置渠道，会覆盖 AndroidManifest 里面的配置  
      .setAppVersion("1.0.1")   //App的版本，会覆盖 AndroidManifest 里面的配置  
      .setAppPackageName("com.bqt.test")  //App的包名  
      .setAppReportDelay(20 * 1000)   //设置Bugly初始化延迟时间，Bugly默认会在启动10s后联网同步数据  
      .setUploadProcess(isMainProcess);//只在主进程下上报数据  
CrashReport.initCrashReport(this, "6c32268c59", true, strategy);// 初始化Bugly  
```  
  
### 设置 Crash 回调  
两个回调返回的数据将伴随Crash一起上报到Bugly平台，并展示在附件中。  
```java  
strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {  
   /**  
    * Crash处理.  
    *  
    * @param crashType 错误类型：CRASHTYPE_JAVA，CRASHTYPE_NATIVE，CRASHTYPE_U3D ,CRASHTYPE_ANR  
    * @param errorType 错误的类型名  
    * @param errorMessage 错误的消息  
    * @param errorStack 错误的堆栈  
    * @return 返回额外的自定义信息上报  
    */  
   @Override  
   public Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {  
      LinkedHashMap<String, String> map = new LinkedHashMap<>();  
      map.put("Key", "Value");  
      return map;  
   }  
     
   @Override  
   public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {  
      try {  
         return "Extra data.".getBytes("UTF-8");  
      } catch (Exception e) {  
         return null;  
      }  
   }  
     
});  
```  
  
崩溃类型：  
- public static final int CRASHTYPE_JAVA_CRASH = 0; // Java crash  
- public static final int CRASHTYPE_JAVA_CATCH = 1; // Java caught exception  
- public static final int CRASHTYPE_NATIVE = 2; // Native crash  
- public static final int CRASHTYPE_U3D = 3; // Unity error  
- public static final int CRASHTYPE_ANR = 4; // ANR  
- public static final int CRASHTYPE_COCOS2DX_JS = 5; // Cocos JS error  
- public static final int CRASHTYPE_COCOS2DX_LUA = 6; // Cocos Lua error  
  
注意，需要尽量保证回调的逻辑简单和稳定，绝对不能在回调中Kill掉进程，否则会影响Crash的上报。如果需要执行类似于`Crash之后Kill掉进程并重新拉起`的动作，建议自定义一个Crash handler，并在初始化Bugly之前注册。  
  
### 使用 MultiDex 的注意事项  
如果使用了MultiDex，建议通过 Gradle 的 multiDexKeepFile 配置等方式把Bugly的类放到主Dex，另外建议在Application类的 attachBaseContext 方法中主动加载非主dex：  
```java  
public class MyApplication extends SomeOtherApplication {  
  @Override  
  protected void attachBaseContext(Context base) {  
    super.attachBaseContext(context);  
    Multidex.install(this);  
  }  
}  
```  
  
### 上报进程控制  
如果App使用了多进程且各个进程都会初始化Bugly（例如在Application类onCreate()中初始化Bugly），那么每个进程下的Bugly都会进行数据上报，造成不必要的资源浪费。为了节省流量、内存等资源，建议初始化的时候对上报进程进行控制，只`在主进程下上报数据`。  
```java  
String packageName = getPackageName();// 获取当前包名  
String processName = getProcessName(android.os.Process.myPid());// 获取当前进程名  
boolean isMainProcess = processName == null || processName.equals(packageName);//通过进程名是否为包名来判断是否是主进程  
strategy.setUploadProcess(isMainProcess);//只在主进程下上报数据  
```  
  
获取进程号对应的进程名  
```java  
/**  
 * 获取进程号对应的进程名  
 *  
 * @param pid 进程号  
 * @return 进程名  
 */  
private static String getProcessName(int pid) {  
    BufferedReader reader = null;  
    try {  
        reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));  
        String processName = reader.readLine();  
        if (!TextUtils.isEmpty(processName)) {  
            processName = processName.trim();  
        }  
        return processName;  
    } catch (Throwable throwable) {  
        throwable.printStackTrace();  
    } finally {  
        try {  
            if (reader != null) {  
                reader.close();  
            }  
        } catch (IOException exception) {  
            exception.printStackTrace();  
        }  
    }  
    return null;  
}  
```  
  
## 其他设置  
### 设置自定义标签  
用于标明App的某个“场景”。在发生Crash时会显示该Crash所在的“场景”，以最后设置的标签为准，标签id需大于0。例：当用户进入界面A时，打上9527的标签：  
```java  
CrashReport.setUserSceneTag(context, 9527); // 上报后的Crash会显示该标签  
```  
打标签之前，需要在Bugly产品页配置中添加标签，取得标签ID后在代码中上报。  
  
### 设置自定义Map参数  
自定义Map参数可以保存发生Crash时的一些自定义的环境信息。在发生Crash时会随着异常信息一起上报并在页面展示。  
```java  
CrashReport.putUserData(context, "userkey", "uservalue");  
```  
最多可以有9对自定义的key-value（超过则添加失败）；  
key限长50字节，value限长200字节，过长截断；key必须匹配正则：[a-zA-Z[0-9]]+。  
  
### 设置开发设备  
在开发测试阶段，可以在初始化Bugly之前通过以下接口把调试设备设置成“开发设备”。  
```java  
CrashReport.setIsDevelopmentDevice(context, true);  
```  
ADT 17增加了BuildConfig特性，可以通过获取BuildConfig类的DEBUG变量来设置：  
```java  
CrashReport.setIsDevelopmentDevice(context, BuildConfig.DEBUG);  
```  
  
### 设置用户ID  
您可能会希望能精确定位到某个用户的异常，我们提供了用户ID记录接口。例：网游用户登录后，通过该接口记录用户ID，在页面上可以精确定位到每个用户发生Crash的情况。  
```java  
CrashReport.setUserId("9527");  //该用户本次启动后的异常日志用户ID都将是9527  
```  
  
### 上传自定义日志  
我们提供了自定义Log的接口，用于记录一些开发者关心的`调试日志`，可以更全面地反应App异常时的前后文环境。使用方式与 `android.util.Log` 一致。用户传入TAG和日志内容。该日志将在Logcat输出，并在发生异常时上报。  
```java  
BuglyLog.i(tag, log)  
```  
  
在使用`BuglyLog`接口时，为了减少磁盘IO次数，我们会先将日志缓存在`内存`中。当缓存大于一定阈值（默认10K），会将它持久化至`文件`。您可以通过 setCache 接口设置缓存大小，范围为0-30K。  
```java  
BuglyLog.setCache(12 * 1024) //将Cache设置为12K  
```  
  
PS：  
- 如果您没有使用 BuglyLog 接口，且初始化 Bugly 时 isDebug 参数设置为 false，该 Log 功能将不会有新的资源占用。  
- 为了方便开发者调试，当初始化 Bugly 的 isDebug 参数为 true 时，异常日志同时还会记录 Bugly 本身的日志。  
- 请在 App 发布时将其设置为false。  
- 上报Log最大30K。  
  
# ACRA：安卓应用崩溃报告  
## 介绍  
[Application Crash Report for Android](https://github.com/ACRA/acra)  
  
ACRA是一个库，使Android应用程序能够自动将崩溃报告发布到报表服务器。 它针对Android应用程序开发人员，帮助他们在崩溃或行为错误时从应用程序中获取数据。  
> ACRA is a library enabling Android Application to automatically post their crash reports to a report server. It is targeted to android applications developers to help them get data from their applications when they crash or behave erroneously.  
  
特点：  
- 开发者可配置的用户交互: silent reports, Toast notification, status bar notification + dialog or direct dialog  
- 可用于2.2以上的所有Android版本。  
- 有关运行应用程序的设备的详细崩溃报告  
- 您可以将自己的变量内容或调试跟踪[debug traces]添加到报告中  
- 即使应用程序没有崩溃，您也可以发送错误报告  
- 适用于任何应用程序，非常适用于Google Play不可用的设备/地区，测试版或企业私有应用  
- 如果没有网络覆盖，则保留报告并在稍后应用程序重新启动时发送  
- 可以与您自己的自托管报告接收器脚本一起使用  
  
ACRA的通知系统很干净。 如果发生崩溃，您的应用程序不会在现有系统的崩溃通知或报告功能上添加用户通知。 默认情况下，不再显示“强制关闭”对话框，启用它会将 allReportToAndroidFramework 设置为true。  
  
用户只会收到一次错误通知，您可以通过在 notifications/dialogs 中定义自己的文本来提高应用程序的感知质量。  
  
请不要犹豫，在问题跟踪器中打开缺陷/增强功能请求。  
  
## 基本配置  
要求 gradle 版本 3.0.0 或以上，java 8 或以上。  
  
统一版本管理  
```  
def acraVersion = '5.1.3'  
```  
  
选择报告目的地，更多信息参考 [Report Destinations](https://github.com/ACRA/acra/wiki/report-destinations)  
```  
implementation "ch.acra:acra-http:$acraVersion"//Http  
implementation "ch.acra:acra-mail:$acraVersion"//Email  
implementation "ch.acra:acra-core:$acraVersion"//Custom  
```  
  
选择交互方式，更多信息参考 [Interactions](https://github.com/ACRA/acra/wiki/interactions)  
```  
implementation "ch.acra:acra-dialog:$acraVersion"//Dialog  
implementation "ch.acra:acra-notification:$acraVersion"//Notification  
implementation "ch.acra:acra-toast:$acraVersion"//Toast  
//Silent:Add nothing.  
```  
  
限制器(可选)，限制acra从一个设备发送的报告数量  
```  
implementation "ch.acra:acra-limiter:$acraVersion"  
```  
  
高级调度程序(可选)，控制何时发送报告（例如仅在wifi时）并且可以在崩溃后重新启动应用程序  
```  
implementation "ch.acra:acra-advanced-scheduler:$acraVersion"  
```  
  
## 初始化  
```java  
@AcraCore(buildConfigClass = BuildConfig.class)  
public class MyApplication extends Application {  
    @Override  
    protected void attachBaseContext(Context base) {  
        super.attachBaseContext(base);  
        ACRA.init(this);  
    }  
}  
```  
  
### 定义的注解  
*   [@AcraHttpSender](https://www.faendir.com/wordpress/acra-javadoc/org/acra/annotation/AcraHttpSender.html)  
*   [@AcraMailSender](https://www.faendir.com/wordpress/acra-javadoc/org/acra/annotation/AcraMailSender.html)  
*   [@AcraDialog](https://www.faendir.com/wordpress/acra-javadoc/org/acra/annotation/AcraDialog.html)  
*   [@AcraNotification](https://www.faendir.com/wordpress/acra-javadoc/org/acra/annotation/AcraNotification.html)  
*   [@AcraToast](https://www.faendir.com/wordpress/acra-javadoc/org/acra/annotation/AcraToast.html)  
*   [@AcraLimiter](https://www.faendir.com/wordpress/acra-javadoc/org/acra/annotation/AcraLimiter.html)  
*   [@AcraScheduler](https://www.faendir.com/wordpress/acra-javadoc/org/acra/annotation/AcraScheduler.html)  
  
### 可用的 Builder  
*   [HttpSenderConfigurationBuilder](https://www.faendir.com/wordpress/acra-javadoc/org/acra/config/HttpSenderConfigurationBuilder.html)  
*   [MailSenderConfigurationBuilder](https://www.faendir.com/wordpress/acra-javadoc/org/acra/config/MailSenderConfigurationBuilder.html)  
*   [DialogConfigurationBuilder](https://www.faendir.com/wordpress/acra-javadoc/org/acra/config/DialogConfigurationBuilder.html)  
*   [NotificationConfigurationBuilder](https://www.faendir.com/wordpress/acra-javadoc/org/acra/config/NotificationConfigurationBuilder.html)  
*   [ToastConfigurationBuilder](https://www.faendir.com/wordpress/acra-javadoc/org/acra/config/ToastConfigurationBuilder.html)  
*   [LimiterConfigurationBuilder](https://www.faendir.com/wordpress/acra-javadoc/org/acra/config/LimiterConfigurationBuilder.html)  
*   [SchedulerConfigurationBuilder](https://www.faendir.com/wordpress/acra-javadoc/org/acra/config/SchedulerConfigurationBuilder.html)  
  
## 完整使用案例  
依赖  
```java  
def acraVersion = '5.1.3'  
implementation "ch.acra:acra-http:$acraVersion"//Http  
implementation "ch.acra:acra-mail:$acraVersion"//Email  
implementation "ch.acra:acra-core:$acraVersion"//Custom  
implementation "ch.acra:acra-dialog:$acraVersion"//Dialog  
implementation "ch.acra:acra-notification:$acraVersion"//Notification  
implementation "ch.acra:acra-toast:$acraVersion"//Toast  
```  
  
Application  
```java  
@AcraCore(buildConfigClass = BuildConfig.class)  
public class App extends Application {  
  
    @Override  
    protected void attachBaseContext(Context base) {  
        super.attachBaseContext(base);  
          
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this)  
                .setBuildConfigClass(BuildConfig.class)  
                .setReportFormat(StringFormat.JSON);  
          
        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class)//吐司  
                .setLength(Toast.LENGTH_SHORT)  
                .setResText(R.string.acra_crash_tips)//或者setText  
                .setEnabled(true);  
          
        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class)//弹窗  
                .setResIcon(R.drawable.icon)  
                .setResTheme(R.style.AppTheme)  
                .setTitle("标题")//setResTitle  
                .setText("内容\n\n呵呵呵呵呵呵呵呵")//setResText  
                .setCommentPrompt("CommentPrompt")//注释输入提示符的标签。setResCommentPrompt  
                .setEmailPrompt("EmailPrompt")//    setResEmailPrompt  
                .setNegativeButtonText("NegativeButtonText")//  setResNegativeButtonText  
                .setPositiveButtonText("PositiveButtonText")//  setResPositiveButtonText  
                //.setReportDialogClass(BaseCrashReportDialog.class)//自定义CrashReportDialog  
                .setEnabled(true);  
          
        builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class)//发邮件。测试失败  
                .setMailTo("0909082401@163.com")  
                .setSubject("主题(崩溃日志)")//或者setResSubject  
                .setReportAsFile(true)  
                .setReportFileName("附件名称")  
                .setEnabled(false);//是通过跳到系统邮箱中，在用户预览并确认后由用户发出去的，而不是在后台默默发送的  
          
        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class)//HTTP请求。不知道怎么测试  
                .setHttpMethod(HttpSender.Method.POST)  
                .setConnectionTimeout(30 * 1000)  
                .setSocketTimeout(30 * 1000)  
                .setHttpHeaders(new HashMap<>())  
                .setBasicAuthLogin("")//认证  
                .setBasicAuthPassword("")  
                .setCertificatePath("")//证书地址。或者setResCertificate  
                .setCertificateType("")//证书类型  
                .setDropReportsOnTimeout(false)//超时是否删除报告  
                .setKeyStoreFactoryClass(KeyStoreFactory.class)//该类创建了可以包含可信证书的密钥库  
                .setEnabled(false);  
          
        builder.getPluginConfigurationBuilder(NotificationConfigurationBuilder.class)//通知栏。测试失败  
                .setResIcon(R.drawable.icon)  
                .setTitle("标题")//setResTitle  
                .setText("内容\n\n呵呵呵呵呵呵呵呵")  
                .setResSendButtonIcon(R.drawable.icon)  
                .setResSendButtonText(R.string.acra_crash_tips)  
                .setEnabled(false);  
        ACRA.init(this, builder);  
    }  
}  
```  
  
# CustomActivityOnCrash：自定义崩溃界面  
[GitHub](https://github.com/Ereza/CustomActivityOnCrash)  
  
该库允许在应用程序崩溃时启动自定义activity，而不是显示讨厌的“Unfortunately, X has stopped”对话框。  
  
当然，您可以将此库与任何其他崩溃处理程序（如Crashlytics，ACRA或Firebase）结合使用，只需按照通常的方式进行设置即可。  
  
## 基本使用  
依赖：  
```java  
compile 'cat.ereza:customactivityoncrash:2.2.0'  
```  
  
在 Application 中初始化：  
```java  
@Override  
public void onCreate() {  
    super.onCreate();  
    CaocConfig.Builder.create()  
        .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM  
        .enabled(false) //default: true  
        .showErrorDetails(false) //default: true  
        .showRestartButton(false) //default: true  
        .logErrorOnRestart(false) //default: true  
        .trackActivities(true) //default: false  
        .minTimeBetweenCrashesMs(2000) //default: 3000  
        .errorDrawable(R.drawable.ic_custom_drawable) //default: bug image  
        .restartActivity(YourCustomActivity.class) //default: null (your app's launch activity)  
        .errorActivity(YourCustomErrorActivity.class) //default: null (default error activity)  
        .eventListener(new YourCustomEventListener()) //default: null  
        .apply();  
}  
```  
  
## 自定义配置  
以下是可以使用 CaocConfig.Builder 设置的每个选项的更详细说明：  
  
```java  
launchWhenInBackground(int);  
```  
This method defines if the error activity should be launched when the app crashes while on background. There are three modes:  
- CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM: launch the error activity even if the app is in background.  
- CaocConfig.BACKGROUND_MODE_CRASH: launch the default system error when the app is in background.  
- CaocConfig.BACKGROUND_MODE_SILENT: crash silently when the app is in background.  
The default is CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM.  
  
```java  
enabled(boolean);  
```  
默认true，设为false将阻止对崩溃的拦截，等于禁用了框架  
Defines if CustomActivityOnCrash crash interception mechanism拦截机制 is enabled. Set it to true if you want CustomActivityOnCrash to intercept crashes, false if you want them to be treated as if the library was not installed. This can be used to enable or disable the library depending on flavors or buildTypes. The default is true.  
  
```java  
showErrorDetails(boolean);  
```  
默认true，设为false将隐藏错误活动中的“错误详细信息”按钮，从而隐藏堆栈跟踪  
This method defines if the error activity must show a button with error details. If you set it to false, the button on the default error activity will disappear, thus这样的话 disabling the user from seeing the stack trace. The default is true.  
  
```java  
trackActivities(boolean);  
```  
默认false，设为true将会在错误页面中显示错误详细信息  
This method defines if the library must track the activities the user visits and their lifecycle calls. This is displayed on the default error activity as part of the error details. The default is false.  
  
```java  
showRestartButton(boolean);  
```  
默认true，是否显示重启页面按钮  
This method defines if the error activity must show a "Restart app" button or a "Close app" button. If you set it to false, the button on the default error activity will close the app instead of restarting. If you set it to true and your app has no launch activity, it will still display a "Close app" button! The default is true.  
  
```java  
logErrorOnRestart(boolean);  
```  
默认true，是否可以在 Logcat 中显示崩溃的堆栈轨迹  
This controls if the stack trace must be relogged必须重新记录堆栈跟踪 when the custom error activity is launched. This functionality exists because the Android Studio default Logcat view only shows the output for the current process. This makes it easier to see the stack trace of the crash崩溃的堆栈轨迹. You can disable it if you don't want an extra log. The default is true.  
  
```java  
minTimeBetweenCrashesMs(int);  
```  
默认3000。定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中。比如：在规定的时间内再次崩溃，框架将不处理，让系统处理！  
Defines the time that must pass between app crashes to determine that we are not in a crash loop. If a crash has occurred less that this time ago, the error activity will not be launched and the system crash screen will be invoked. The default is 3000.  
  
```java  
errorDrawable(int);  
```  
默认null ，崩溃页面显示的图标  
This method allows changing the default upside-down上下翻转 bug image with an image of your choice. You can pass a resource id for a drawable or a mipmap. The default is null (the bug image is used).  
  
```java  
restartActivity(Class<? extends Activity>);  
```  
默认null，重新启动后的页面  
This method sets the activity that must be launched by the error activity when the user presses the button to restart the app. If you don't set it (or set it to null), the library will use the first activity on your manifest that has an intent-filter with action cat.ereza.customactivityoncrash.RESTART, and if there is none, the default launchable activity on your app. If no launchable activity can be found and you didn't specify any, the "restart app" button will become a "close app" button, even if showRestartButton is set to true.  
  
```java  
errorActivity(Class<? extends Activity>);  
```  
默认null，程序崩溃后显示的页面  
This method allows you to set a custom error activity to be launched, instead of the default one. Use it if you need further customization进一步定制 that is not just不只是 strings, colors or themes (see below). If you don't set it (or set it to null), the library will use the first activity on your manifest that has an intent-filter with action cat.ereza.customactivityoncrash.ERROR, and if there is none, a default error activity from the library. If you use this, the activity must be declared in your AndroidManifest.xml, with process set to :error_activity.  
  
```java  
eventListener(EventListener);  
```  
默认null，设置监听  
This method allows you to specify an event listener in order to get notified when the library shows the error activity, restarts or closes the app. The EventListener you provide can not be an anonymous匿名 or non-static inner class, because it needs to be serialized by the library. The library will throw an exception if you try to set an invalid class. If you set it to null, no event listener will be invoked. The default is null.  
  
## CustomEventListener 监听  
监听程序崩溃/重启  
```java  
public interface EventListener extends Serializable {  
    void onLaunchErrorActivity();//程序崩溃回调  
    void onRestartAppFromErrorActivity();//重启程序时回调  
    void onCloseAppFromErrorActivity();//在崩溃提示页面关闭程序时回调  
}  
```  
  
## 完全自定义错误 activity  
如果您选择完全创建自定义的错误activity，则可以使用以下方法：  
  
```java  
CustomActivityOnCrash.getStackTraceFromIntent(getIntent());  
```  
Returns the stack trace堆栈跟踪信息 that caused the error as a string.  
  
```java  
CustomActivityOnCrash.getAllErrorDetailsFromIntent(getIntent());  
```  
Returns several error details including the stack trace that caused the error, as a string. This is used in the default error activity error details dialog.  
  
```java  
CustomActivityOnCrash.getConfigFromIntent(getIntent());  
```  
Returns the config of the library when the crash happened. Used to call some methods.  
  
```java  
CustomActivityOnCrash.restartApplication(activity, config);  
```  
Kills the current process and restarts the app again with a startActivity() to the passed intent. You MUST call this to restart the app, or you will end up having several Application class instances and experience multiprocess issues.否则你最终将拥有多个Application类实例并遇到多进程问题。  
  
```java  
CustomActivityOnCrash.restartApplicationWithIntent(activity, intent, config);  
```  
The same as CustomActivityOnCrash.restartApplication, but allows you to specify a custom intent.  
  
```java  
CustomActivityOnCrash.closeApplication(activity, eventListener);  
```  
Closes the app and kills the current process. You MUST call this to close the app, or you will end up having several Application class instances and experience multiprocess issues.否则你最终将拥有多个Application类实例并遇到多进程问题。  
  
## 测试代码  
### 自定义崩溃Activity  
```java  
public class CustomErrorActivity extends ListActivity {  
    private CaocConfig config;//配置对象  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Toast.makeText(this, "程序崩溃了！", Toast.LENGTH_SHORT).show();  
          
        String errorDetails = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent());// 获取所有的信息  
        String stackTrace = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());//获取堆栈跟踪信息  
        String activityLog = CustomActivityOnCrash.getActivityLogFromIntent(getIntent()); //获取错误报告的Log信息  
        config = CustomActivityOnCrash.getConfigFromIntent(getIntent());//获得配置信息  
        TextView textView = new TextView(this);  
        textView.setText("【errorDetails】\n" + errorDetails + "\n\n\n【stackTrace】\n" + stackTrace + "\n\n\n【activityLog】\n" + activityLog);  
        textView.setTextColor(Color.BLUE);  
        getListView().addFooterView(textView);  
          
        String[] array = {"重启程序", "关闭页面"};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array))));  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                if (config != null && config.getRestartActivityClass() != null) {  
                    CustomActivityOnCrash.restartApplication(this, config);  
                }  
                break;  
            case 1:  
                CustomActivityOnCrash.closeApplication(this, config);  
                break;  
        }  
    }  
}  
```  
  
### 设置  
```java  
public class SettingActivity extends ListActivity {  
    private int backgroundMode = CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM;  
    private boolean enabled = true;  
    private boolean showErrorDetails = true;  
    private boolean showRestartButton = true;  
    private boolean logErrorOnRestart = true;  
    private boolean trackActivities = false;  
    private int minTimeBetweenCrashesMs = 3000;  
    private Integer errorDrawable = null;  
    private Class<? extends Activity> errorActivityClass = null;  
    private Class<? extends Activity> restartActivityClass = null;  
    private CustomActivityOnCrash.EventListener eventListener = null;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"0、backgroundMode",  
                "1、enabled",  
                "2、showErrorDetails",  
                "3、showRestartButton",  
                "4、logErrorOnRestart",  
                "5、trackActivities",  
                "6、errorDrawable",  
                "7、errorActivityClass",  
                "8、restartActivityClass",  
                "9、eventListener",  
                "【自定义一个异常】",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array))));  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                backgroundMode = CaocConfig.BACKGROUND_MODE_CRASH;  
                break;  
            case 1:  
                enabled = !enabled;  
                break;  
            case 2:  
                showErrorDetails = !showErrorDetails;  
                break;  
            case 3:  
                showRestartButton = !showRestartButton;  
                break;  
            case 4:  
                logErrorOnRestart = !logErrorOnRestart;  
                break;  
            case 5:  
                trackActivities = !trackActivities;  
                break;  
            case 6:  
                errorDrawable = R.drawable.icon;  
                break;  
            case 7:  
                errorActivityClass = CustomErrorActivity.class;  
                break;  
            case 8:  
                restartActivityClass = MainActivity.class;  
                break;  
            case 9:  
                initListener();  
                break;  
            case 10:  
                throw new RuntimeException("自定义一个异常");  
        }  
        config();  
    }  
      
    private void config() {  
        CaocConfig.Builder.create()  
                .backgroundMode(backgroundMode)  
                .enabled(enabled) //这阻止了对崩溃的拦截,false表示阻止。用它来禁用customactivityoncrash框架  
                .showErrorDetails(showErrorDetails) //这将隐藏错误活动中的“错误详细信息”按钮，从而隐藏堆栈跟踪  
                .showRestartButton(showRestartButton) //是否可以重启页面  
                .trackActivities(trackActivities) //错误页面中显示错误详细信息  
                .logErrorOnRestart(logErrorOnRestart)  
                .minTimeBetweenCrashesMs(minTimeBetweenCrashesMs) //定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中  
                .errorDrawable(errorDrawable) //崩溃页面显示的图标  
                .restartActivity(restartActivityClass) //重新启动后的页面  
                .errorActivity(errorActivityClass) //这种程序崩溃后显示的页面  
                .eventListener(eventListener)//设置监听  
                .apply();  
    }  
      
    private void initListener() {  
        eventListener = new CustomActivityOnCrash.EventListener() {  
            @Override  
            public void onLaunchErrorActivity() {  
                Log.i("bqt", "【onLaunchErrorActivity程序崩溃】");  
            }  
              
            @Override  
            public void onRestartAppFromErrorActivity() {  
                Log.i("bqt", "【onRestartAppFromErrorActivity重启程序】");  
            }  
              
            @Override  
            public void onCloseAppFromErrorActivity() {  
                Log.i("bqt", "【onCloseAppFromErrorActivity在崩溃提示页面关闭程序】");  
            }  
        };  
    }  
}  
```  
  
## 工作原理  
This library relies on the Thread.setDefaultUncaughtExceptionHandler method. When an exception is caught by the library's UncaughtExceptionHandler it does the following:  
  
- Captures捕获 the stack trace that caused the crash  
- Launches a new intent to the error activity in a new process passing the crash info as an extra.  
- Kills the current process.  
The inner workings are based on ACRA's dialog reporting mode报告模式 with some minor tweaks小的调整. Look at the code if you need more detail about how it works.  
  
## 不兼容性和免责声明  
- CustomActivityOnCrash will not work in these cases:  
    - With any custom UncaughtExceptionHandler set after initializing the library, that does not call back to the original handler.  
    - With ACRA enabled and reporting mode set to TOAST or DIALOG.  
- If your app initialization or error activity crash, there is a possibility of entering an infinite restart loop无限重启循环 (this is checked by the library for the most common cases, but could happen in rarer罕见的 cases).  
- The library has not been tested with multidex enabled. It uses Class.forName() to load classes, so maybe that could cause some problem in API<21. If you test it with such configuration, please provide feedback反馈!  
- The library has not been tested with multiprocess apps. If you test it with such configuration, please provide feedback too!  
- Disclaimers  免责声明  
    - This will not avoid ANRs from happening.  
    - This will not catch native errors.  
    - There is no guarantee保证 that this will work on every device.  
    - This library will not make you toast for breakfast :)  
  
2018-6-6  
