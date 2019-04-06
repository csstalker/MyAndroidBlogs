| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
插件化 VirtualAPK 简介 体验 MD  
***  
目录  
===  

- [简介](#简介)
- [基本使用](#基本使用)
	- [宿主项目的配置](#宿主项目的配置)
	- [插件项目的配置](#插件项目的配置)
	- [构建插件时可能出现的问题](#构建插件时可能出现的问题)
- [相关知识](#相关知识)
	- [VirtualAPK 的特性](#VirtualAPK-的特性)
	- [主流插件化框架的对比](#主流插件化框架的对比)
	- [为什么选择 VirtualAPK](#为什么选择-VirtualAPK)
	- [如何选择适合的插件化框架](#如何选择适合的插件化框架)
	- [VirtualAPK 原理简介](#VirtualAPK-原理简介)
- [已知约束](#已知约束)
	- [目前暂不支持的特性](#目前暂不支持的特性)
	- [Activity](#Activity)
	- [Service](#Service)
	- [BroadcastReceiver](#BroadcastReceiver)
	- [ContentProvider](#ContentProvider)
	- [Fragment](#Fragment)
	- [so文件的加载](#so文件的加载)
- [FQA](#FQA)
  
# 简介  
[GitHub](https://github.com/didi/VirtualAP)  
[Release note](https://github.com/didi/VirtualAPK/blob/master/RELEASE-NOTES.md)  
  
[VirtualAPK 框架接入](http://www.jianshu.com/p/013510c19391)  
[VirtualAPK四大组件源码分析](https://blog.csdn.net/lmj623565791/article/details/75000580)  
[VirtualAPK 资源加载机制分析](https://www.notion.so/VirtualAPK-1fce1a910c424937acde9528d2acd537)  
  
**个人使用体验：功能强大，遍地是坑！**  
  
VirtualAPK是`滴滴出行`自研的一款优秀的`插件化框架`。  
  
VirtualAPK is a `powerful` yet `lightweight` plugin framework for Android. It can `dynamically` load and run an APK file (we call it `LoadedPlugin`) seamlessly as an installed application. Developers can use any Class, Resources, Activity, Service, Receiver and Provider in `LoadedPlugin` as if they are registered in app's manifest file.  
  
**Supported Features**  
  
| Feature | Detail |  
|:-------------|:-------------:|  
| Supported components | Activity, Service, Receiver and Provider |  
| Manually register components in AndroidManifest.xml | No need |  
| Access host app classes and resources | Supported |  
| PendingIntent | Supported |  
| Supported Android features | Almost all features |  
| Compatibility | Almost all devices |  
| Building system | Gradle plugin |  
| Supported Android versions | API Level 15+ |  
  
**基本原理**  
- Activity：在宿主apk中提前占几个坑，然后通过“欺上瞒下”的方式启动插件apk的Activity；因为要支持不同的launchMode以及一些特殊的属性，所以需要占多个坑。  
- BroadcastReceiver：将静态注册的广播改为动态注册。  
- Service：通过代理Service的方式去分发；主进程和其他进程，VirtualAPK使用了两个代理Service。  
- ContentProvider：通过一个代理Provider进行分发。  
  
# 基本使用  
VirtualAPK 对插件没有额外的约束，原生的apk即可作为插件。插件工程编译生成apk后，即可通过宿主App加载，每个插件apk被加载后，都会在宿主中创建一个单独的`LoadedPlugin`对象。通过这些LoadedPlugin对象，VirtualAPK就可以`管理插件`并`赋予插件新的意义`，使其可以像手机中安装过的App一样运行。  
  
## 宿主项目的配置  
1、在 project 的`build.gradle`中添加依赖：  
```groovy  
classpath 'com.android.tools.build:gradle:3.1.4' //这个版本不能修改，否则同步时就会失败  
classpath 'com.didi.virtualapk:gradle:0.9.8.6' //2019-1-14最新版本  
```  
  
2、在 app 模块的`build.gradle`中使用插件：  
```groovy  
apply plugin: 'com.didi.virtualapk.host'  
```  
  
3、在 app 模块的`build.gradle`中添加依赖：  
```groovy  
implementation 'com.didi.virtualapk:core:0.9.8'  
//注意，宿主项目中需要包含所有插件项目中的support依赖，否则插件编译不通过(会提示要在宿主中添加依赖)  
//但对于其他依赖则没有此要求，例如可以在插件中依赖gson，而无需在宿主中依赖gson  
```  
  
4、在 Application 中初始化插件引擎：  
```java  
@Override  
protected void attachBaseContext(Context context) {  
    super.attachBaseContext(context);  
    PluginManager.getInstance(context).init();  
}  
```  
  
5、在合适的时机加载插件(APP退出后下次使用前仍需要加载)：  
```java  
PluginManager.getInstance(context).loadPlugin(apkFile);  
//当插件入口被调用后，插件的后续逻辑均不需要宿主干预，均走原生的Android流程。  
```  
  
6、判断是否已加载插件  
```java  
LoadedPlugin loadedPlugin = PluginManager.getInstance(this).getLoadedPlugin(PKG); //包名  
if (loadedPlugin == null) Toast.makeText(this, "尚未加载 " + PKG, Toast.LENGTH_SHORT).show();  
else Toast.makeText(this, "已加载 " + loadedPlugin.getPackageName(), Toast.LENGTH_SHORT).show();  
```  
  
7、跳转到插件的Activity中  
```java  
Intent intent = new Intent();  
intent.setClassName(this, "com.didi.virtualapk.demo.aidl.BookManagerActivity");  
intent.putExtra("name","包青天");  
startActivity(intent);  
```  
  
注意，如果遇到如下提示，可以不必关心，因为并没有什么影响：  
```  
Configuration on demand is not supported by the current version of the Android Gradle plugin since you are using Gradle version 4.6 or above.  
Suggestion: disable configuration on demand by setting org.gradle.configureondemand=false in your gradle.properties file or use a Gradle version less than 4.6.  
```  
  
## 插件项目的配置  
在VirtualAPK中，插件开发等同于原生Android开发，因此开发插件就和开发APP一样。  
如果有使用`nativeActivity`需要的用户请更新使用`fix_native_activity`分支并修改依赖为`CoreLibrary`，未来会合入主线。  
构建环境建议直接使用Demo中的配置，`插件构建强依赖构建环境`，请不要轻易尝试修改。  
  
1、在 project 的`build.gradle`中添加依赖：  
```groovy  
classpath 'com.android.tools.build:gradle:3.1.4' //这个版本不能修改，否则同步时就会失败  
classpath 'com.didi.virtualapk:gradle:0.9.8.6'  //2019-1-14最新版本，和宿主中用的是同一个依赖  
```  
  
2、在 app 模块的`gradle.properties`中(如没有请创建)添加如下配置：  
```java  
android.useDexArchive=false  
```  
  
3、在 app 模块的`build.gradle`中使用插件：  
```groovy  
apply plugin: 'com.didi.virtualapk.plugin'  
virtualApk {  
    packageId = 0x6f // 插件资源表中的packageId，需要确保不同插件有不同的packageId.  
    targetHost = 'D:/code/PluginDemo/app' // 宿主工程application模块的路径，插件的构建需要依赖这个路径  
    applyHostMapping = true //默认为true，如果插件有引用宿主的类，那么这个选项可以使得插件和宿主保持混淆一致  
}  
```  
  
4、构建插件  
请通过`gradle assemblePlugin`来构建插件，`assemblePlugin`依赖于`assembleRelease`，这意味着：  
- 插件包均是`release`包，不支持`debug`模式的插件包  
- 如果存在多个`productFlavors`，那么将会构建出多个插件包  
- 插件包位于`build`目录下  
  
![](index_files/16af8aa1-fe40-4c7d-8504-8664491909a6.jpg)  
  
打出来的包是非常小的  
![](index_files/d311547f-7239-4654-845e-5e5f2778bd0a.png)  
  
以下是正常打的包  
![](index_files/d159583c-c024-4433-96b2-ef57b6750503.png)  
  
其实主要区别在于：插件包是不包含宿主中`已经存在`的`aar依赖库`和`res资源`的内容的，因为这些内容最终是用的宿主包中的。  
  
> 一定要给插件设置一个资源别名`resourcePrefix`，以防止插件中误用到了宿主中已经存在的资源名，导致解析出错。  
> 最典型的是默认的`activity_main.xml`，如果插件和宿主中都有这个布局文件，那么打包后会删除插件中定义的`activity_main.xml`，所以在运行时使用的是宿主中的`activity_main.xml`，那么就很可能会导致调用findViewBuId时崩溃！  
> 宿主如果更改后最好先build一次，因为生成插件包时需要用到宿主构建时生成的文件。  
  
## 构建插件时可能出现的问题  
我通过AS创建了一个最最纯净的项目(默认包含kotlin)，结果运行时发现一堆问题。  
  
1、提示设置在app模块中的`gradle.properties`中添加`android.useDexArchive=false`  
```java  
A problem occurred configuring project ':app'.  
> Failed to notify project evaluation listener.  
   > Can't using incremental dexing mode, please add 'android.useDexArchive=false' in gradle.properties of :app.  
   > Cannot invoke method onProjectAfterEvaluate() on null object  
```  
我们按照上述提示修改即可。  
  
2、修改后再运行出现如下提示：  
```java  
Failed to notify task execution listener.  
> The dependencies   
[  
   com.android.support.constraint:constraint-layout:1.1.3,  
   com.android.support:support-fragment:28.0.0,  
   //后面省略二十个  
]  
that will be used in the current plugin must be included in the host app first. Please add it in the host app as well.  
```  
意思是说，在插件项目中包含的库也必须在宿主项目存在。可以发现全部是 `support` 库，我们只需统一宿主和插件的`support`库版本就可以了，比如都用如下最新的设置：  
```  
implementation 'com.android.support:appcompat-v7:28.0.0'  
implementation 'com.android.support.constraint:constraint-layout:1.1.3'  
```  
  
3、沃日，配置为宿主的依赖后便开始出现各种问题，clean不行、build不行、手动删除build目录也不行，重启AS也不行  
```  
AAPT2 error: check logs for details  
```  
查看报错详细信息，说什么资源文件找不到什么问题，完全是莫名其妙嘛，为什么会有这个错呢？  
网上搜了一通，找不到解决方案，只找到一种委曲求全的扯淡方案，那就是在project中的`gradle.properties`中添加`android.enableAapt2=false`。  
  
4、添加完之后clean了一下，结果那个问题没有了，又出另一个莫名其妙的错误：  
```  
Process 'command 'D:\software\android_sdk\build-tools\26.0.2\aapt.exe'' finished with non-zero exit value 1  
```  
![](index_files/6bd0cf7c-b768-42a2-a4e5-34a92dabd519.png)  
  
报错的原因可能和我们上面的操作有关，因为看到有这么两行信息：  
```java  
Deprecated Gradle features were used in this build, making it incompatible with Gradle 5.0.  
See https://docs.gradle.org/4.6/userguide/command_line_interface.html#sec:command_line_warnings  
```  
  
5、把所有设置都还原吧，完全没法搞嘛！  
我猜测可能与插件中采用了kotlin而宿主没有采用有关，于是在宿主中添加了kotlin相关的依赖，结果这货同步时又报一个错：  
```  
A problem occurred evaluating project ':CoreLibrary'.  
> Failed to apply plugin [id 'com.android.library']  
   > Configuration on demand is not supported by the current version of the Android Gradle plugin since you are using Gradle version 4.6 or above. Suggestion: disable configuration on demand by setting org.gradle.configureondemand=false in your gradle.properties file or use a Gradle version less than 4.6.  
```  
意思是说当前版本的Gradle插件不支持按需配置，日了狗了，什么鬼呀，搜索了一下，说可以这样禁用按需配置：  
- 在你的`Project`和报错的`CoreLibrary`模块中的`gradle.properties` 文件中设置 `org.gradle.configureondemand=false`。  
- 在AS的设置中禁用按需配置  
![](index_files/9747d1a5-8390-48e9-8fb8-589d4a9243f2.png)  
  
配置完成后同步一下发现成功了。  
  
6、继续构建插件  
```java  
> Failed to notify project evaluation listener.  
   > Can't find C:\Users\baiqi\Desktop\VirtualAPK-master\app\build\VAHost\versions.txt, please check up your host application  
       need apply com.didi.virtualapk.host in build.gradle of host application  
   > Cannot invoke method onProjectAfterEvaluate() on null object  
```  
  
这个错误提示就比较好处理了因为提示找不到`versions.txt`，而这个文件是构建后由 VirtualAPK 产生的，我们要先构建一次宿主app，才可以构建plugin(因为插件构建需要宿主的mapping以及其他信息)，可以尝试使用`build -> build apk(s)`直接构建宿主apk。  
  
7、然后处理之后继续构建插件又遇到了最初遇到的问题，也就是提示我添加一堆 `support` 库，干脆我把插件中所有用到的 `support` 库全部去掉得了，看你还报不报错！  
  
果不其然，又一个错误出来了：  
```  
Cannot get property 'id' on null object  
```  
这又是什么鬼？  
网上搜了半天，有人说，这个问题是因为插件中布局文件没有id，在插件主activity的布局文件中增加一个view，声明一个id就可以了。  
然而我按照上述方式设置之后并没有任何卵用！  
  
我通过以下指令  
```  
gradle assemblePlugin --stacktrace  
```  
拿到了如下错误信息：  
```java  
* Exception is:  
java.lang.NullPointerException: Cannot get property 'id' on null object  
        at com.didi.virtualapk.aapt.ArscEditor.slice(ArscEditor.groovy:66)  
        ...  
```  
然后又去查看了`ArscEditor.groovy`中相应的源码：  
![](index_files/83fbb564-170a-4ff2-8528-ddf7579ec76b.png)  
这意思大致是说，要确保有一个'attr'，否则就会报异常(垃圾代码都不判空的吗？)  
  
但是这是什么垃圾东西呢？我搞了老半天，这个问题始终解决不了！  
  
8、坑实在是太多了，填不完了，重新开始集成吧。  
这次我决定将demo中的配置全部迁移过来，然后再一点一点的更新到新版本，或添加新功能，看看到哪一步时会失败！  
然而理想很丰满现实很骨感，仍旧是遍地错误！  
  
9、重新开始集成！  
这次我在网上仔细搜了一遍，发现很多人反映，Gradle的 build tools 版本问题会导致失败，即使使用 demo 中的配置也不行，所以这一次我使用网上说的版本吧。  
```  
classpath 'com.android.tools.build:gradle:2.1.3'  
```  
gradle-wrapper  
```  
distributionUrl=https\://services.gradle.org/distributions/gradle-2.14.1-all.zip  
```  
  
然而发现这TMD全是扯淡，可能旧版本需要这么配置，然而新版本并不需要这么配置。  
  
10、重新开始集成！  
这一次决定在原demo基础上修改，这次终于成功了。具体配置就是上面所述的。  
  
# 相关知识  
## VirtualAPK 的特性  
**1、功能完备**  
- 支持几乎所有的Android特性；  
- 四大组件均`不需要在宿主manifest中预注册`，每个组件都有完整的生命周期。  
    - Activity：支持显示和隐式调用，支持Activity的`theme`和`LaunchMode`，支持透明主题；  
    - Service：支持显示和隐式调用，支持Service的`start`、`stop`、`bind`和`unbind`，并支持跨进程bind插件中的Service；  
    - Receiver：支持静态注册和动态注册的Receiver；  
    - ContentProvider：支持provider的所有操作，包括`CRUD`和`call`方法等，支持跨进程访问插件中的Provider。  
- 支持自定义View，支持自定义属性和`style`，支持动画；  
- 支持`PendingIntent`以及和其相关的`Alarm`、`Notification`和`AppWidget`；  
- 支持插件`Application`以及插件manifest中的`meta-data`；  
- 支持插件中的`so`；  
  
**2、优秀的兼容性**  
- 兼容市面上几乎所有的Android手机(兼容性问题没法保证，从它文档中遍地的"升级提示"便可以看出来)；  
- 资源方面适配`小米、Vivo、Nubia`等，对未知机型采用自适应适配方案(意思就是说，只象征性的进行了一些适配)；  
- 极少的Binder Hook，目前仅仅hook了两个Binder：`AMS`和`IContentProvider`，hook过程做了充分的兼容性适配；  
- 插件运行逻辑和宿主隔离，确保框架的任何问题都不会影响宿主的正常运行(扯淡，一上手就遇到几个框架导致的崩溃)。  
  
**3、入侵性极低**  
- 插件开发等同于原生开发，四大组件无需继承特定的基类；  
- 精简的插件包，插件可以依赖宿主中的代码和资源，也可以不依赖；  
- 插件的构建过程简单，`通过Gradle插件来完成插件的构建`，整个过程对开发者透明。  
  
## 主流插件化框架的对比  
如下是VirtualAPK和主流的插件化框架之间的对比。  
  
|特性|DynamicLoadApk|DynamicAPK|Small|DroidPlugin|VirtualAPK  
|-------------|:-------------:|:-----:|:-----:|:-----:|:-----:|  
| 支持四大组件 | 只支持Activity | 只支持Activity |只支持Activity|全支持|全支持  
| 组件无需在宿主中预注册| √ |×|√|√|√  
| 插件可以依赖宿主| √ | √|√|×|√  
| 支持PendingIntent| × | ×|×|√|√  
| Android特性支持| 大部分 | 大部分|大部分|几乎全部|几乎全部  
| 兼容性适配| 一般 | 一般|中等|高|高  
| 插件构建| 无 |部署aapt |Gradle插件|无|Gradle插件  
  
## 为什么选择 VirtualAPK  
已经有那么多优秀的开源的插件化框架，滴滴为什么要重新造一个轮子呢？  
  
- 大部分开源框架所支持的功能还不够全面  
除了DroidPlugin，大部分都只支持Activity。  
  
- 兼容性问题严重，大部分开源方案不够健壮  
由于国内Rom尝试深度定制Android系统，这导致插件框架的兼容性问题特别多，而目前已有的开源方案中，除了DroidPlugin，其他方案对兼容性问题的适配程度是不足的。  
  
- 已有的开源方案不适合滴滴的业务场景  
    虽然说DroidPlugin从功能的完整性和兼容性上来看，是一款非常完善的插件框架，然而它的使用场景和滴滴的业务不符。  
    DroidPlugin侧重于加载第三方独立插件，并且`插件不能访问宿主的代码和资源`。而在滴滴打车中，其他业务模块均需要宿主提供的订单、定位、账号等数据，因此插件不可能和宿主没有交互。  
    其实在大部分产品中，一个业务模块实际上并不能轻而易举地独立出来，它们往往都会和宿主有交互，在这种情况下，DroidPlugin就有点力不从心了。  
  
基于上述几点，我们只能重新造一个轮子，它不但功能全面、兼容性好，还必须能够`适用于有耦合的业务插件`，这就是VirtualAPK存在的意义。  
  
## 如何选择适合的插件化框架  
在加载`耦合插件`方面，VirtualAPK是开源方案的首选，推荐大家使用。  
  
**抽象地说**  
- 如果你要加载一个插件，并且这个插件无需和宿主有任何耦合，也无需和宿主进行通信，并且你也不想对这个插件重新打包，那么推荐选择DroidPlugin；  
- 除此之外，在同类的开源中，推荐大家选择VirtualAPK。  
  
**通俗易懂地说**  
- 如果你是要加载微信、支付宝等第三方APP，那么推荐选择DroidPlugin；  
- 如果你是要加载一个内部业务模块，并且这个业务模块很难从主工程中解耦，那么VirtualAPK是最好的选择。  
  
## VirtualAPK 原理简介  
**基本原理**  
- `合并宿主和插件的ClassLoader`。需要注意的是，插件中的类不可以和宿主重复  
- `合并插件和宿主的资源`。重设插件资源的packageId，将插件资源和宿主资源合并  
- `去除插件包对宿主的引用`。构建时通过Gradle插件去除插件对宿主的代码以及资源的引用  
  
**四大组件的实现原理**  
- **Activity**  
采用宿主manifest中占坑的方式来绕过系统校验，然后再加载真正的activity；  
- **Service**  
动态代理`AMS`，拦截service相关的请求，将其中转给`Service Runtime`去处理，`Service Runtime`会接管系统的所有操作；  
- **Receiver**  
将插件中静态注册的receiver重新注册一遍；  
- **ContentProvider**  
动态代理`IContentProvider`，拦截provider相关的请求，将其中转给`Provider Runtime`去处理，`Provider Runtime`会接管系统的所有操作。  
  
如下是VirtualAPK的整体架构图，更详细的内容请大家阅读源码。  
  
![VirtualAPK](https://github.com/didi/VirtualAPK/blob/master/imgs/va.png?raw=true)  
  
**插件如何和宿主交互**  
通过compile相同aar的方式来交互。  
比如，宿主工程中compile了如下aar：  
```groovy  
compile 'com.didi.foundation:sdk:1.2.0'  
compile 'com.didi.virtualapk:core:[newest version]'  
compile 'com.android.support:appcompat-v7:22.2.0'  
```  
  
但是插件工程需要访问宿主sdk中的类和资源，那么可以在插件工程中同样compile sdk的aar，如下：  
```groovy  
compile 'com.didi.foundation:sdk:1.2.0'  
```  
  
这样一来，插件工程就可以正常地引用sdk了。并且，`插件构建的时候会自动将这个aar从apk中剔除`。  
  
上述就是VirtualAPK中插件和宿主通信的基本方式。  
  
然而，VirtualAPK仍然有一些小小的约束，如下注意事项，请务必仔细阅读。  
  
# 已知约束  
  
## 目前暂不支持的特性  
- 暂不支持Activity的一些不常用特性，比如`process`、`configChanges`等属性，但是支持`theme`、`launchMode`和`screenOrientation`属性  
- `overridePendingTransition(int enterAnim, int exitAnim)`这种形式的转场动画，动画资源不能使用插件的(可以使用宿主或系统的)  
- 插件中弹通知，需要统一处理，走宿主的逻辑，通知中的资源文件不能使用插件的(可以使用宿主或系统的)  
- 插件的Activity中不支持`动态申请权限`  
  
## Activity  
支持LaunchMode和theme  
- 透明Activity不能有启动模式，并且主题中必须含有`android:windowIsTranslucent`属性；  
  
```xml  
<style name="AppTheme.Transparent">  
    <item name="android:windowBackground">@android:color/transparent</item>  
    <item name="android:windowIsTranslucent">true</item>  
</style>  
```  
  
- 插件中调用宿主的四大组件，请注意Intent中的`包名`。  
  
VirtualAPK对Intent的处理遵循Android规范，插件之间乃至插件和宿主之间，包名是区分它们的唯一标识。  
  
为了兼容宿主与插件之间的activity互调的场景，我们`弱化了插件的包名`，在插件中通过`context.getPackageName()`取到的仍然是宿主的包名。因此在下面的例子中，假如宿主的包名是`com.didi.virtualapk`，然后在插件中启动一个宿主Activity，仍然可正确的调用：  
```Java  
// 兼容方式  
Intent intent = new Intent(this, HostActivity.class);  
startActivity(intent);  
  
// 显式指定包名的方式  
Intent intent = new Intent();  
intent.setClassName("com.didi.virtualapk", "com.didi.virtualapk.HostActivity");  
startActivity(intent);  
```  
  
如果想`在插件中去访问插件的四大组件`，那么就没有任何要求了，下面的代码会在插件的一个Activity中尝试启动插件中的另一个Activity：  
```Java  
// 正确的用法，因为此时intent中的包名是插件的包名  
Intent intent = new Intent(this, PluginActivity.class);  
startActivity(intent);  
```  
  
## Service  
支持跨进程bind service  
无约束  
  
## BroadcastReceiver  
- `静态Receiver将被动态注册`，当宿主停止运行时，外部广播将无法唤醒宿主；  
- 由于动态注册的缘故，插件中的Receiver必须通过`隐式调用`来唤起。  
  
## ContentProvider  
支持跨进程访问ContentProvider  
- 分情况，插件调用自己的ContentProvider，如果需要用到`call`方法，那么需要将`provider`的`uri`放到`bundle`中，否则调用不生效；  
  
```Java  
Uri bookUri = Uri.parse("content://com.didi.virtualapk.demo.book.provider/book");  
Bundle bundle = PluginContentResolver.getBundleForCall(bookUri);  
getContentResolver().call(bookUri, "testCall", null, bundle);  
```  
  
- 插件调用宿主和外部的ContentProvider，无约束；  
  
- 宿主调用插件的ContentProvider，需要将`provider`的`uri`包装一下，通过`PluginContentResolver.wrapperUri`方法，如果涉及到`call`方法，参考上面所描述的;  
  
```java  
String pkg = "com.didi.virtualapk.demo";  
LoadedPlugin plugin = PluginManager.getInstance(this).getLoadedPlugin(pkg);  
Uri bookUri = Uri.parse("content://com.didi.virtualapk.demo.book.provider/book");  
bookUri = PluginContentResolver.wrapperUri(plugin, bookUri);  
Cursor bookCursor = getContentResolver().query(bookUri, new String[]{"_id", "name"}, null, null, null);  
```  
  
## Fragment  
推荐大家在Application启动的时候去加载插件，不然的话，请注意插件的加载时机。  
  
考虑一种情况，如果在一个较晚的时机去加载插件并且去访问插件中的资源，请注意当前的Context。比如在宿主Activity(MainActivity)中去加载插件，接着在MainActivity去访问插件中的资源（比如Fragment），需要做一下显示的hook，否则部分4.x的手机会出现资源找不到的情况。  
  
```java  
String pkg = "com.didi.virtualapk.demo";  
PluginUtil.hookActivityResources(MainActivity.this, pkg);  
```  
  
## so文件的加载  
为了提升性能，VirtualAPK 在加载一个插件时并不会主动去释放插件中的so，除非你在插件apk的manifest中显式地指定`VA_IS_HAVE_LIB`为true，如下所示：  
```xml  
<meta-data  
    android:name="VA_IS_HAVE_LIB"  
    android:value="true" />  
```  
  
为了通用性，在armeabi路径下放置对应的so文件即可满足需求。如果考虑性能请做好各种so文件的适配。  
  
# FQA  
**The directory of host application doesn't exist!**  
错误分析：宿主工程的application模块的路径不存在，一般是指路径配错了  
解决方式：检测`targetHost`这个路径是否正确，相对路径或者绝对路径都行  
  
**java.lang.ArrayIndexOutOfBoundsException: 2**  
错误分析：请检查`dependencies`中aar的依赖方式  
解决方式：按如下建议修改  
```groovy  
dependencies {  
    √ compile 'com.didi.virtualapk:core:0.9.0'  
    √ compile project (":CoreLibrary")  
  
    // group和version字段必须有  
    √ compile(group:'test', name:'CoreLibrary-release', version:'0.1', ext:'aar')  
  
    × releaseCompile 'com.didi.virtualapk:core:0.9.0'  
    × compile(name:'CoreLibrary-release', ext:'aar')  
}  
```  
  
**编译插件时空指针：Cannot invoke method getAt() on null object**  
解决方式：请确保插件中至少有一个自己的资源  
  
**插件的activity能正常打开，但是插件中的资源读取失败**  
解决方式：依次检查：  
- 先检查 `packageId` 的取值范围(在下面)是否正确  
- 再检查插件依赖的所有`com.android.support`包在宿主都有显式依赖，并且版本和宿主保持一致  
- 读取失败的资源`id`是否和宿主的资源重名，重名资源会在构建插件包时被自动剔除  
  
**Failed to notify project evaluation listener**  
解决方式：修改`Gradle`和`build tools`的版本  
构建环境建议：  
> Gradle 2.14.1  
com.android.tools.build  2.1.3  
  
**java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity**  
解决方式：构建插件请使用`gradle assemblePlugin`，而不能直接通过AndroidStudio run出来一个插件apk。  
  
**关于Android M及以上版本动态申请权限问题**  
从Android 6.0开始，系统采用了新的权限机制，为了保证插件的加载，请保证APP具有SD卡的访问权限。如果你的app没有在android 6.0上做足够的测试，请不要设置targetSdk为23。  
注意：**目前暂时不支持在插件中动态申请权限**。  
  
**插件的gradle文件中对于packageID设置有什么范围吗？**  
- 采用正常的android资源命名方式，PPTTNNNN：PackageId + TypeId + EntryId。  
- 运行时获取资源需要通过packageId来映射apk中的资源文件，`不同apk的packageId值不能相同`，所以插件的packageId范围是介于系统应用(0x01,0x02,...具体占用多少值视系统而定)和宿主(0x7F)之间。  
- 多个插件的packageId和packageName一样，在宿主中需要确保是唯一的。  
  
**生成的插件apk中会发现有些png图片是黑色的，大小为0，这是怎么回事？**  
为了减小包的大小对于那些没有引用的资源进行压缩了，在gradle中配置`shrinkResources true`即可，位置和`minifyEnabled true`一起。  
  
**关于Activity的configchanges**  
因为`configChanges`的选项组合太多，坑位比较多，这个暂时不准备支持，因为在日常使用的时候就横竖常用。  
  
**iR是什么意思？**  
install Release，gradle中的一种`小驼峰`命名的缩写方式。如果发现冲突，可以通过assembleRelease来实现构建宿主工程。  
  
**0.9.1版本的VirtualAPK构建插件在构建插件的时候assets目录下的文件会被删除**  
这是0.9.1版本的bug，更高版本已经修复，请更新版本。  
  
**宿主和插件同时依赖公共的本地jar文件或library module，支持在构建插件时自动剔除吗？**  
不支持。  
构建插件的依赖自动剔除功能仅支持`内容稳定不变，路径稳定的资源`，而`本地的jar`或其它资源的路径和内容都是可变更的，因此无法直接自动剔除，如果需要剔除，请将资源打包导出部署到`maven`或其它依赖管理服务器。如果资源不可公开发布，可在内网部署`私有maven服务`。  
  
2019-1-13  
