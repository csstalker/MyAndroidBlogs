| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
版本适配 sdk version MD  
***  
目录  
===  

- [Android 常见版本适配](#android-常见版本适配)
	- [Android 9.0 - P](#android-90---p)
		- [需要特别关注的变化](#需要特别关注的变化)
		- [[针对所有应用的行为变更](https://developer.android.google.cn/about/versions/pie/android-9.0-changes-all)](#针对所有应用的行为变更httpsdeveloperandroidgooglecnaboutversionspieandroid-90-changes-all)
		- [[针对 9.0 应用的行为变更](https://developer.android.google.cn/about/versions/pie/android-9.0-changes-28)](#针对-90-应用的行为变更httpsdeveloperandroidgooglecnaboutversionspieandroid-90-changes-28)
		- [[新功能和 API](https://developer.android.google.cn/about/versions/pie/android-9.0)](#新功能和-apihttpsdeveloperandroidgooglecnaboutversionspieandroid-90)
	- [Android 8.0 - O](#android-80---o)
		- [需要特别关注的变化](#需要特别关注的变化)
		- [[针对所有应用的行为变更](https://developer.android.google.cn/about/versions/oreo/android-8.0-changes#atap)](#针对所有应用的行为变更httpsdeveloperandroidgooglecnaboutversionsoreoandroid-80-changesatap)
		- [[针对 8.0 应用的行为变更](https://developer.android.google.cn/about/versions/oreo/android-8.0-changes#o-apps)](#针对-80-应用的行为变更httpsdeveloperandroidgooglecnaboutversionsoreoandroid-80-changeso-apps)
		- [[新功能和 API](https://developer.android.google.cn/about/versions/oreo/android-8.0)](#新功能和-apihttpsdeveloperandroidgooglecnaboutversionsoreoandroid-80)
	- [Android 7.0 - N](#android-70---n)
	- [Android 6.0 - M](#android-60---m)
	- [Android 5.0 - L](#android-50---l)
	- [Android 4.4 - K](#android-44---k)
- [各种 sdk version 的意义](#各种-sdk-version-的意义)
	- [buildToolsVersion](#buildtoolsversion)
	- [compileSdkVersion](#compilesdkversion)
	- [minSdkVersion](#minsdkversion)
	- [targetSdkVersion](#targetsdkversion)
  
# Android 常见版本适配  
如果要去适配某个版本，那么请将`targetSdkVersion`改为对应的版本号，点击 sync Now 即可。  
  
## Android 9.0 - P  
[官方文档](https://developer.android.google.cn/about/versions/pie/)  
[参考1](https://blog.csdn.net/chen_lian_/article/details/81516654)  
[参考2](https://www.jianshu.com/p/604b63d7efed)  
  
> Android 9 利用人工智能技术，让手机可以为您提供更多帮助。现在，手机变得更智能、更快，并且还可以随着您的使用进行调整。  
  
### 需要特别关注的变化  
- **对使用非 SDK 接口的限制 和 适配策略**：可以通过反射禁止弹窗或toast  
- 非Activity-Context启动Activity：必须添加`FLAG_ACTIVITY_NEW_TASK`  
- 刘海屏适配：DisplayCutout  
- 通知功能的增强  
- 前台服务需要`FOREGROUND_SERVICE`权限  
- 隐私权变更：后台对传感器的访问受限、限制访问通话记录和电话号码、限制访问 Wi-Fi 位置和连接信息  
- 多摄像头支持和摄像头更新  
- 电源管理：对第三方的后台功能是有影响的  
- 使用RTT API 进行更精准的室内定位  
- Apache HTTP 客户端弃用，影响采用非标准 ClassLoader 的应用  
- 多进程 webview 信息访问限制  
  
### [针对所有应用的行为变更](https://developer.android.google.cn/about/versions/pie/android-9.0-changes-all)  
Android 9（API 级别 28）向 Android 系统引入了多项变更。当应用在 Android 9 平台上运行时，以下行为变更将影响所有应用，无论这些应用以哪个 API 级别为目标。所有开发者都应查看这些变更，并修改其应用以正确支持这些变更（如果适用）。  
  
- [电源管理](https://developer.android.google.cn/about/versions/pie/power)：应用待机群组，省电模式改进  
- [对使用非 SDK 接口的限制](https://developer.android.google.cn/about/versions/pie/restrictions-non-sdk-interfaces)：对某些非 SDK 函数和字段的使用进行了限制  
- `隐私权变更`：后台对传感器的访问受限，限制访问通话记录和电话号码，限制访问 Wi-Fi 位置和连接信息  
- 安全行为变更：设备安全性变更，加密变更，不再支持 Android 安全加密文件  
- ICU 库更新  
- Android Test 变更  
- Java UTF 解码器  
- 使用证书的主机名验证  
- 网络地址查询可能会导致网络违规：StrictMode  
- 套接字标记  
- 报告的套接字中可用字节数  
- 更详尽的 VPN 网络功能报告：`NetworkCapabilities`  
- 应用不再能访问 xt_qtaguid 文件夹中的文件  
- `从非 Activity 环境中启动 Activity`：必须添加`FLAG_ACTIVITY_NEW_TASK`  
- `屏幕旋转变更`：可在自动屏幕旋转和纵向旋转模式之间切换  
- Apache HTTP 客户端弃用，影响采用非标准 ClassLoader 的应用  
- `枚举相机`：调用 getCameraIdList() 发现每个可用的摄像头  
  
### [针对 9.0 应用的行为变更](https://developer.android.google.cn/about/versions/pie/android-9.0-changes-28)  
Android 9（API 级别 28）向 Android 系统引入了多项变更。以下行为变更仅影响以 API 28 或更高级别为目标的应用。将 targetSdkVersion 设为 API 28 或更高级别的应用必须进行修改，以便正确支持这些行为（如果适用）。  
  
- 前台服务：`FOREGROUND_SERVICE`权限  
- 隐私权变更：构建序列号`Build.SERIAL`弃用、DNS 隐私  
- 框架安全性变更：默认情况下启用网络传输层安全协议 TLS、  
- 连接变更：连接数据计数和多路径、`Apache HTTP 客户端弃用`  
- 界面变更：`视图焦点`，CSS RGBA 十六进制值处理，文档(document)滚动标签，来自已暂停应用的通知  
  
### [新功能和 API](https://developer.android.google.cn/about/versions/pie/android-9.0)  
- 利用 Wi-Fi RTT 进行室内定位  
- 显示屏缺口支持：全面屏，DisplayCutout  
- 通知：提升短信体验，渠道设置、广播和请勿打扰  
- 多摄像头支持和摄像头更新  
- 适用于可绘制对象和位图的 ImageDecoder  
- 动画：AnimatedImageDrawable，绘制和显示 GIF 和 WebP 动画图像  
- HDR VP9 视频、HEIF 图像压缩和 Media API  
- JobScheduler 中的流量费用敏感度  
- Neural Networks API 1.1  
- 自动填充框架  
- 安全增强功能：Android Protected Confirmation，统一生物识别身份验证对话框，硬件安全性模块，保护对密钥库进行的密钥导入，具有密钥轮转的 APK 签名方案，只允许在未锁定设备上进行密钥解密的选项，旧版加密支持  
- Android 备份：客户端加密备份，定义备份所需的设备条件  
- 无障碍功能：导航语义，便捷操作，窗口变更详情  
- 旋转  
- 文本：DEX 文件的 ART 提前转换  
- 设备端系统跟踪  
  
## Android 8.0 - O  
[官方文档](https://developer.android.google.cn/about/versions/oreo)  
[参考1](https://www.jianshu.com/p/d9f5b0801c6b)  
[参考2](https://blog.csdn.net/qq_17766199/article/details/80965631)  
  
发布时间：2017年8月22日   
> 更智能、更快速、功能更强大。您喜爱的新 Android 版本以全球人都爱的一款曲奇饼为代号。  
  
### 需要特别关注的变化  
- 通知：通知渠道、通知标志、通知超时、背景颜色：必须适配，否则通知无法使用  
- 运行时权限管理策略：只是解决bug，正常使用不受影响  
- [后台执行限制](https://developer.android.google.cn/about/versions/oreo/background)：后台服务限制，广播限制  
- [后台位置限制](https://developer.android.google.cn/about/versions/oreo/background-location-limits)  
- 安装Apk权限：升级，`REQUEST_INSTALL_PACKAGES`，允许未知来源  
- 提醒窗口：悬浮窗权限  
- 自适应启动图标  
  
### [针对所有应用的行为变更](https://developer.android.google.cn/about/versions/oreo/android-8.0-changes#atap)  
这些行为变更适用于 在 Android 8.0 平台上运行的 所有应用，无论这些应用是针对哪个 API 级别构建。所有开发者都应查看这些变更，并修改其应用以正确支持这些变更（如果适用）。  
  
- `后台执行限制`：后台服务、前台服务、隐式广播  
- `后台位置限制`：降低了后台应用接收位置更新的频率  
- 应用快捷键： 对应用快捷方式做出了一些变更  
- 语言区域和国际化：默认语言区域  
- 提醒窗口：SYSTEM_ALERT_WINDOW  
- 输入和导航：键盘导航，焦点状态颜色  
- 网页表单自动填充：WebSettings、WebViewDatabase  
- 无障碍功能：可识别应用的 TextView 对象内部的所有 ClickableSpan 实例  
- 网络连接和 HTTP(S) 连接：Content-Length 标头，网址规范化，代理选择器，空白标签  
- 蓝牙：ScanRecord.getBytes()  
- 无缝连接：WLAN   
- 安全性：不再支持 SSLv3，TLS，SECCOMP  
- 隐私性：ANDROID_ID、net.hostname  
- 记录未捕获的异常：Thread.UncaughtExceptionHandler  
- 联系人提供程序使用情况统计方法的变更  
- 集合的处理：NullPointerException  
- Android 企业版  
  
### [针对 8.0 应用的行为变更](https://developer.android.google.cn/about/versions/oreo/android-8.0-changes#o-apps)  
这些行为变更专门应用于针对 O 平台或更高平台版本的应用。针对 Android 8.0 或更高平台版本进行编译，或将 `targetSdkVersion` 设为 Android 8.0 或更高版本的应用开发者必须修改其应用以正确支持这些行为（如果适用）。  
  
- 提醒窗口  
- 内容变更通知  
- 视图焦点  
- 安全性  
- 帐号访问和可检测性  
- 隐私性  
- 权限  
- 媒体  
- 原生库  
- 集合的处理  
- 类加载行为  
  
### [新功能和 API](https://developer.android.google.cn/about/versions/oreo/android-8.0)  
1、用户体验  
- `通知`：`通知渠道`，通知圆点(通知标志)，休眠，通知超时，通知设置，通知清除，背景颜色，消息样式  
- `自动填充框架`：登录和表单填写  
- 自适应图标：可在不同设备型号上显示为各种不同的形状  
- 画中画 (PIP) 模式：视频播放，多窗口模式，生命周期  
- 多显示器支持：可指定 Activity 应在哪个显示器上运行，生命周期  
- 可下载字体：无需将字体绑定到 APK 中，允许将字体作为res，而非只能是assent  
- 自动调整 TextView 的大小：根据 TextView 的大小自动设置文本展开或收缩的大小  
- 统一的布局外边距和内边距  
- 颜色管理：支持广色域色彩  
- 指针捕获：View 可以请求指针捕获将所有鼠标事件来处理捕获的指针事件  
- 应用类别：android:appCategory，声明应用所属的类别  
- WebView API：Version，Google SafeBrowsing，Termination Handle，Renderer Importance  
- 固定快捷方式和小部件功能  
- 最大屏幕纵横比：maxAspectRatio，以前最大为 1.86  
- Android TV 启动器  
- AnimatorSet：支持寻道和倒播功能  
- 输入和导航：键盘导航键区，视图默认焦点  
  
2、系统  
- 添加了三个新的 StrictMode 检测程序，帮助识别应用可能出现的错误  
- 缓存数据  
- 内容提供程序分页：加载大型数据集，Cursor  
- 内容刷新请求：ContentProvider 和 ContentResolver 类均包含 refresh() 函数  
- JobScheduler 改进  
- 自定义数据存储  
- `findViewById() 签名变更`  
  
3、媒体增强功能  
- VolumeShaper：执行简短的自动音量转换  
- 音频焦点增强功能：音频应用通过请求和舍弃音频焦点的方式在设备上共享音频输出  
- 媒体指标  
- MediaPlayer 新功能  
- 音频录制器  
- 音频播放控制  
- 增强的媒体文件访问功能  
  
4、连接  
- WLAN 感知：NAN 周边感知联网  
- 蓝牙：增强了平台对蓝牙的支持  
- 配套设备配对：允许自定义配对请求对话框  
  
5、共享  
- 智能共享：根据用户的个性化首选项自动学习  
- 智能文本选择：长按某个实体中可识别格式的单词时，系统会选中整个实体，并显示包含可以处理所选文本实体的应用  
  
6、无障碍功能  
- 无障碍功能按钮  
- 独立的音量调整  
- 指纹手势  
- 字词级突出显示  
- 标准化单端范围值  
- 提示文本  
- 连续的手势分派  
  
7、安全性与隐私  
- 权限：引入了多个与电话有关的新权限 `ANSWER_PHONE_CALLS`，`READ_PHONE_NUMBERS`  
- 新的帐号访问和 Discovery API  
- Google Safe Browsing API：增强网络浏览的安全性  
  
8、测试  
- 仪器测试  
- 用于测试的模拟 Intent  
  
9、运行时和工具  
- 平台优化：为平台引入了运行时优化和其他优化  
- 更新的 Java 支持：添加了对更多 OpenJDK Java API 的支持  
- 更新的 ICU4J Android Framework API  
  
10、Android 企业版  
  
## Android 7.0 - N  
[官方文档](https://developer.android.google.cn/about/versions/nougat)  
  
发布时间：2016年8月22日   
主要变化：  
- `私有文件访问权限更改：FileProvider`  
- `多窗口支持：分屏模式、画中画、自由形状`  
- 通知增强功能  
- 随时随地低电耗模式  
- 多语言区域支持，更多语言  
- 新增的表情符号  
- Chrome 和 WebView 配合使用  
- `APK signature scheme v2`  
  
## Android 6.0 - M  
[官方文档](https://developer.android.google.cn/about/versions/marshmallow)  
  
发布时间：2015年5月28日   
主要变化：  
- `运行时权限`  
- 增加低电耗模式和应用待机模式   
- 取消支持 Apache HTTP 客户端   
- 移除硬件标识符访问权   
- WLAN 和网络连接变更   
- 相机服务变更  
  
## Android 5.0 - L  
[官方文档](https://developer.android.google.cn/about/versions/lollipop)  
  
## Android 4.4 - K  
[官方文档](https://developer.android.google.cn/about/versions/kitkat)  
  
# 各种 sdk version 的意义  
[参考](https://blog.csdn.net/gaolh89/article/details/79809034)  
  
一般情况，应做如下设置：  
```c  
minSdkVersion <= 【targetSdkVersion】 <= compileSdkVersion == 当前Android发布的最新版本  
```  
  
即：用尽可能最低的 SDK 设置 minSdkVersion 来覆盖最大的人群，用尽可能最高的 SDK 设置 targetSdkVersion 和 compileVersion 来获得最好的外观和行为。  
  
## buildToolsVersion  
用于指定项目`构建工具的版本`，如果有更新的版本，Android Studio会进行提示。  
  
构建工具包括了打包工具aapt、dx、aidl等等，这个工具的目录位于`sdk_path/build-tools/XX.XX.XX`  
  
这个版本号一般是`API-LEVEL.x.x`。例如`28.0.3`  
  
可以用高版本的 build-tool 去构建一个低版本的 sdk 工程。  
![](index_files/9053f9e9-693e-42bd-899e-c298c48d9ee9.png)  
  
## compileSdkVersion  
`SDK编译版本`  
  
compileSdkVersion 告诉 `Gradle` 用哪个 Android SDK 版本`编译`你的应用。使用任何新添加的 API 就需要使用对应 Level 的 Android SDK。  
  
需要强调的是:修改 compileSdkVersion 不会改变运行时的行为。当你修改了 compileSdkVersion 的时候，可能会出现新的编译警告、编译错误，但新的 compileSdkVersion `不会被包含到 APK 中`：它纯粹`只是在编译的时候使用`。（你真的应该修复这些警告，他们的出现一定是有原因的）  
  
因此我们强烈推荐总是使用最新的 SDK 进行编译。在现有代码上使用新的`编译检查`可以获得很多好处，`避免新弃用的 API ，并且为使用新的 API 做好准备`。  
  
> 注意，如果使用 `Support Library` ，那么使用最新发布的 Support Library 就需要使用最新的 SDK 编译。  
例如，要使用 23.1.1 版本的 Support Library ，compileSdkVersion 就必需至少是 23（大版本号要一致！）。通常，新版的 Support Library 随着新的系统版本而发布，它`为系统新增加的 API 和新特性提供兼容性支持`。  
  
## minSdkVersion  
`最小支持的SDK版本`。  
  
minSdkVersion 是`应用可以运行的最低要求`，是各大Android应用商店用来判断用户设备`是否可以安装某个应用`的标志之一。  
  
在开发时 minSdkVersion 也起到一个重要角色：`lint` 默认会在项目中运行，它在你使用了高于 minSdkVersion 的 API 时会警告你，帮你避免调用不存在的 API 的运行时问题。如果只在较高版本的系统上才使用某些 API，通常使用`运行时检查系统版本`的方式解决。  
  
> 注意，你所使用的库可能有他们自己的 minSdkVersion，你的应用设置的 minSdkVersion 必需`大于等于`这些库的 minSdkVersion。例如有三个库，它们的 minSdkVersion 分别是 4, 7 和 9 ，那么你的 minSdkVersion 必需至少是 9 才能使用它们。  
在少数情况下，你仍然想用一个比你应用的 minSdkVersion 还高的库，你可以使用 `tools:overrideLibrary` 标记，但请做彻底的测试！  
  
## targetSdkVersion  
三个版本号中最有趣、最重要的就是 targetSdkVersion 了。  
  
targetSdkVersion 是 Android 提供`向前兼容`(兼容旧版本)的主要依据，`在应用的 targetSdkVersion 没有更新之前系统不会应用最新的行为变化`。这允许你`在适应新的行为变化之前就可以使用新的 API`（因为你已经更新了 compileSdkVersion 不是吗？）。  
  
targetSdkVersion 指定的值表示`你在该目标版本上已经做过了充分的测试`，系统将会为你的应用程序`启用一些最新的功能和特征`。比如，Android `6.0` 系统引用了`运行时权限`这个功能，如果你将 targetSdkVersion 指定为`23`或者更高，那么系统就会为你的程序启动运行时权限；如果你将targetSdkVersion 指定为22，那么就`说明你的程序最高只在Android 5.1系统上做过充分的测试`，Android6.0系统中`引入的新功能就不会启用了`。  
  
说通俗一点，比如你将 targetSdkVersion 设置为22，涉及某个(或某几个)权限，你直接在mainfest中配置权限即可，然后在java代码中进行获取权限后的逻辑处理即可。  
但如果你将 targetSdkVersion 设置为23或更高，你除了在mainfest中配置权限外，还需要在java代码中`判断用户是否同意权限`，如果同意，就可以执行具体的业务操作；如果不同意，提示某某权限被拒后某某功能无法正常使用。如果你只在mainfest中配置了权限，targetSdkVersion 又大于等于23，Java代码中不进行运行时权限的代码，你的应用程序就直接报错了。  
  
看完上面,你的想法是不是:既然这样,那我把 targetSdkVersion 设置低一点不就更保险了。  
  
这样做是可以的，但作为一位有良知且有追求的开发人员，不建议这么做，将 target 更新为最新的 SDK 是所有应用都应该优先处理的事情。  
  
但这不意味着你一定要使用所有新引入的功能，也不意味着你可以不做任何`测试`就盲目地更新 targetSdkVersion，请一定`在更新 targetSdkVersion 之前做测试`！尤其是国产手机各种厂商定制。  
  
总而言之，关于targetSdkVersion设置的值，需要自己`把握度`——既要为用户考虑,提升自己的能力，也要避免坑自己。  
  
2019-1-22  
