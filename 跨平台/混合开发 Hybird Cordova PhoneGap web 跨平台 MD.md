| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |
| :------------: | :------------: | :------------: | :------------: | :------------: |
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |

[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs
[GitHub]:https://github.com/baiqiantao
[博客]:http://www.cnblogs.com/baiqiantao/

混合开发 Hybird Cordova PhoneGap web 跨平台 MD
***
目录
===
[TOC]

# Cordova 简介
[官网](https://cordova.apache.org/)  
[中文网](http://cordova.axuer.com/)  
[W3C](https://www.w3cschool.cn/cordova/)  
[cordova-android](https://github.com/apache/cordova-android)  

## 官网介绍
关键词：
- 使用HTML, CSS & JS进行移动App开发
- 多平台(Android、iOS、wp)共用一套代码，Target multiple platforms with one code base
- 免费开源，Free and open source
- 跨平台重用代码，Reusable code across platforms
- 支持离线场景应用，Support for offline scenarios
- 访问设备原生API，Access native device APIs

Cordova包装你的HTML/JavaScript app到原生app容器[container]中，可以让你访问每个平台设备的功能。这些功能通过统一的[unified] JavaScript API 提供，让你轻松的编写一组代码运行在几乎市面上的所有手机和平板上，并可以发布到相应的app商城中。

Apache Cordova是一个开源移动开发框架，它允许您使用标准的Web技术，如HTML5，CSS3和JavaScript进行跨平台[cross-platform]开发。应用程序在针对每个平台的包装[wrappers]内执行，并依赖符合标准的API绑定来访问每个设备的功能[capabilities]，如传感器，数据，网络状态等。

适用`Apache Cordova`的人群:
- 移动应用开发者，想扩展[extend]一个应用的使用平台，而不通过每个平台的语言和工具集重新实现。
- web开发者，想包装部署[deploy]自己的`web App`将其分发[distribution]到各个应用商店门户。
- 移动应用开发者，有兴趣通过混合[mixing]原生应用组件和WebView接触设备级API，或者你想开发一个原生和WebView组件之间的插件接口。  

## W3C上的介绍
Cordova 是用于使用`HTML，CSS和JS`构建`移动应用`的平台。我们可以认为Cordova是一个`容器`，用于将我们的`网络应用程序`与`本机移动功能`连接。默认情况下，Web应用程序不能使用本机移动功能。这就是Cordova进来的地方。它为网络应用和移动设备之间的连接提供了`桥梁`。 通过使用cordova，我们可以使混合移动应用程序，可以使用摄像头，地理位置，文件系统和其他本地移动功能。  

适用人群：希望了解移动开发的`HTML，CSS和JavaScript开发人员`。  
学习前提：需要熟悉`HTML，CSS和JavaScript`。  

## 特点、优缺点
时下流行的移动Web应用可分为三种：原生应用、Web应用和混合型应用。
- 原生应用：通过各种应用市场安装，采用平台特定语言开发。
- Web应用：通过浏览器访问，采用Web技术开发。
- 混合型应用：通过各种应用市场安装，但采用Web技术开发。它虽然看上去是一个原生应用，但里面访问的实际上是一个Web应用。

Cordova 就是混合型应用。

优点：
- 跨平台，开发成本低(相同的代码只需针对不同平台进行编译就能实现在多平台的分发)
- 开发速度快，学习成本低(可以让众多Web开发人员几乎零成本地转型成移动应用开发者)(其实也不是很快，手机上的页面调整会很坑)
- 插件多，可自定义插件(可通过包装好的接口调用大部分常用的系统API，一般还是要原生开发人员写插件)
- 发展早，社区资源丰富(Cordova是混合型框架中的佼佼者)
- 迭代更新容易(基于Web)
- 支持离线模式

缺点(就一个字：卡)：
- 无法突破WebView性能瓶颈，用户体验差
- 手机浏览器的渲染能力有限，导致APP会卡
- 间隔的层次比较多，调用硬件的速度会慢一些
- 调试不方便，既不像原生那么好调试，也不像纯web那种调试
- 跨浏览器兼容性可能会产生很多问题，测试和优化可能需要很多时间，因为我们需要覆盖大量的设备和操作系统。
- 某些插件与不同的设备和平台存在兼容性问题，还有一些Cordova尚不支持的本机API。

你需要了解的：
- `phonegap`和`cordova`的区别：他们之间没有区别，在被收购之前叫`phonegap`，被收购后叫`cordova`
- `phonegap`能做什么：它是一个桥梁，能让网页可以调用手机的硬件，没有别的用处了。
- 有什么东西`phonegap`做不了只能原生做：网页上用JS调用`phonegap`，`phonegap`调用原生代码，原生代码调用手机硬件，所以理论上说，原生可以做的`phonegap`都可以做。可以用原生的写插件，用`phonegap`调用。

总结：其实phonegap不是想象中的那么好用，做一些简单的增删改查APP还是可以的，如果你的公司刚起步或者不想请高价开发人员，而APP又比较简单，那你就选它吧。

## 体系结构
cordova应用程序有几个组件。下图显示了cordova应用程序体系结构的high-level视图。  
![架构图](https://cordova.apache.org/static/img/guide/cordovaapparchitecture.png)

从图中，我们可以看到它提供了`Web APP、WebView、Cordova Plugins`。

**Web APP**  
这是存放[resides]应用程序代码的部分。应用程序的实现是通过web页面，默认的本地文件名称是`index.html`，这个本地文件引用 CSS,JavaScript,images，media files和其他运行需要的资源。应用程序执行在原生应用包装的WebView中，这个原生应用是你分发到 app stores 中的。  

此容器有一个非常关键的`config.xml`文件，该文件提供有关应用程序的信息，并指定影响其工作的参数，例如它是否响应方向转换[orientation shifts]。  

**WebView**  
Cordova启用的WebView可以给应用提供完整用户访问界面[entire user interface]。在一些平台中，他也可以作为一个组件给大的、混合[hybrid]应用，这些应用混合[mixes] Webview 和原生的应用组件。  

**Cordova Plugins**  
插件是Cordova生态[ecosystem]的重要组成部分。他提供了Cordova和原生组件相互通信的接口，并绑定到了标准的设备API上，这使你能够通过JavaScript调用原生代码。  

Apache Cordova项目维护了一组名为`Core Plugins`的插件。这些核心插件为您的应用程序提供访问设备功能，如电池，相机，联系人等。  

除了核心插件之外，还有一些第三方插件可以提供额外的绑定(不一定所有平台上都提供的这些功能)。您可以使用插件搜索或npm搜索Cordova插件。您还可以开发自己的插件，例如，可能需要插件才能在Cordova和自定义native组件之间进行通信。  

> 注意：创建Cordova项目时，它没有任何插件。这是新的默认行为。您需要的任何插件，甚至是核心插件，都必须明确添加。
> 注意：Cordova不提供任何 `UI widgets` 或 `MV*` 框架。Cordova仅提供可以执行的 runtime。如果您希望使用 `UI widgets` 或 `MV*` 框架，则需要选择它们并将它们包含在您的应用程序中。

## 基本原理
[参考](https://segmentfault.com/q/1010000004526369/a-1020000004526852)  

iOS：【webView:`shouldStartLoadWithRequest`:`navigationType`:】  
Android：【public void `addJavascriptInterface` (Object object, String name)】  

第一个是 iOS 上 UIWebView 将要开始跳转地址的时候被调用，进而根据传入的地址作出反应。  
第二个是 Android 上用于`使一个 Java 对象可以在 JS 中被访问，并调用其方法`。  
这就开启了两个平台上 `JS 和原生代码之间的沟通窗口`，这就是原理。 

`Cordova` 在这个基础上构建了完善的一套体系，让我们可以以一种简单标准的流程写 `Hybird` 应用，它来负责这个 JS 与原生代码的沟通工作。

到这看得出，其实`原生代码是避不开的`，想要利用系统的各项功能必须要写对应不同系统支持的不同语言的原生代码。但有很多写 `Cordova` 的程序员不懂这些也能写出东西来，靠的就是`丰富的插件`。

随便找一个 `Cordova` 插件，目录结构打开，大致是这样：  
```java
xxx@xxx:~/.../cordova-plugin-device
> tree

├── README.md
├── package.json
├── plugin.xml
├── src
│ ├── android
│ │ └── Device.java
│ ├── ios
│ │ ├── CDVDevice.h
│ │ └── CDVDevice.m
│ ├── ...
│ └── wp
│ └── Device.cs
└── www
    └── device.js
```

看到 `src` 文件夹底下的 `ios、android、wp` 这些文件夹了么，里面装的就是`各个平台上的原生代码`。用打包工具 build 的时候，就会对应的帮你复制到各个平台的项目文件夹去，并做好配置。

比如我写一个调用摄像头拍照片的插件，支持 android 与 iOS 两个平台，我就要针对这两个平台编写两份完成同样功能的`原生代码`，然后给一个`统一的 JS 接口`，由 Cordova 把这个接口暴露给写 Cordova 应用的人。他们就可以只用 JS 完成我写的插件承诺能够做到的功能，也就是拍一张照片。

也就是说 Cordova 写的应用`理论上可以做到任何原生应用能做到的功能`，而不是很多人误解的“局限很大”，确实是有局限，但不是局限在`可能性`上。

虽然只使用用上面提到的两个接口也可以让你做到这里说的`使用 JS 调用原生平台功能`，但 Cordova 把这个过程简化、标准化，甚至生态化了。`丰富的插件`、活跃的社区还有详尽的文档，这些都极大方便了 `Hybird` 应用的开发过程。就好像只用 `1010` 可以构建整个互联网，但我们仍然需要`操作系统`一样。

# PhoneGap 简介
[官网](https://phonegap.com/)  
[GitHub](https://github.com/phonegap/)  

## PhoneGap 简介
PhoneGap是一个采用`HTML，CSS和JavaScript`的技术，创建`移动跨平台`移动应用程序的快速开发平台。它使开发者能够在网页中调用`IOS，Android`等智能手机的核心功能——包括地理定位，加速器，联系人，声音和振动等，此外PhoneGap拥有丰富的`插件`可以调用。

PhoneGap原本由Nitobi公司开发，现在由Adobe拥有。  

PhoneGap目前支持的操作系统包含:苹果的iOS，谷歌的Android，RIM的Blackberry，惠普的WebOS，微软的Windows Phone，塞班公司的Symbian以及三星的bada。

- Build amazing mobile apps powered by open web tech.
- 重用现有的Web开发技能，快速制作使用HTML，CSS和JavaScript构建的混合[hybrid]应用程序。 使用单一代码库为多个平台创建应用，因此无论您的用户使用的是什么设备，您都可以覆盖到他们。
- PhoneGap Build消除了编译PhoneGap应用程序的痛苦。直接获取可以发布到应用商店的应用程序，而无需头痛的去维护原生SDK。我们的PhoneGap Build服务通过在云中进行编译来为您完成工作。

## PhoneGap 和 Cordova 的关系
Cordova是`PhoneGap`贡献给`Apache`后的开源项目，是从PhoneGap中抽离出的核心代码，是驱动PhoneGap的核心引擎。有点类似`Webkit`和`Google Chrome`的关系。

渊源就是：早在`2011年10月`，`Adobe`收购了`Nitobi Software`和它的`PhoneGap`产品，然后宣布这个`移动Web开发框架`将会继续开源，并把它提交到`Apache Incubator`，以便完全接受`Apache Software Foundation`的管治。当然，由于Adobe拥有了PhoneGap商标，所以开源组织的这个`PhoneGap v2.0`版产品就更名为`Apache Cordova`。

> Cordova名字的由来：
> PhoneGap 由一个叫 `Nitobi` 的公司发起 ，曾经改名为 `Callback`，接着又改名为 `Cordova`，因为 `Nitobi` 的办公地点曾设在在一条叫 `Cordova` 的街道。

## PhoneGap 和 Cordova 如何选
就目前来看，`cordova`是一个`移动应用开发框架`，你基于这个东西可以用网页代码做出APP。

`Phonegap Build`是一个`在线打包工具`，你把使用`cordova`写好的项目给Phonegap Build，Phonegap Build 就会在线打包成App。

目前大家所说的`Phonegap`，其实指的都是`cordova`，而那个真正被`Adobe`收购了的`Phonegap`，现在是`Phonegap Build` 和 `cordova` 的合体。

简单来说，就是看你需不需要`Phonegap Build`的功能，或者说需不需要在线打包，需要就是用`Phonegap`，否则直接使用`cordova`即可。
> `PhoneGap build` is a Build Service allows you to build and package you app in cloud。

# 零基础案例
## Cordova 环境搭建
### 环境检查
**JDK**
打开cmd，输入`java -version`和`javac -version`，可以看到打印的Java和Javac版本信息。如果没有，请配置java环境变量。

**Git**
打开cmd，输入`git --version`，可以看到打印的Git版本信息。如果没有，请配置Git环境变量。

**adb**
打开cmd，输入`adb --version`，可以看到打印的adb版本信息。如果没有，请配置adb环境变量。

**Node.js**
打开cmd，输入`node -v`和`npm -v`，可以看到打印的Node和npm版本信息。如果没有，请配置Node环境变量。

### 配置 Node.js 环境
**下载、安装**
- 下载 [Node.js](http://nodejs.cn/download/)，你可以根据不同平台系统选择你需要的Node.js安装包。
- 选择Windows 安装包，例如 [node-v10.11.0-x64](http://cdn.npm.taobao.org/dist/node/v10.11.0/node-v10.11.0-x64.msi)  
- 安装一路下一步即可，其中在下图的地方，点击树形图标来选择你需要的安装模式 ,默认以下四项都会安装：
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/ljhcm92JeD.png?imageslim)  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/1mkADfL7hD.png?imageslim)  

> 新版的Node.js已自带npm，安装Node.js时会一起安装，npm的作用就是对Node.js依赖的包进行管理，也可以理解为用来安装/卸载Node.js需要装的东西。

**检测环境是否成功**
- 检测PATH环境变量是否配置了Node.js：查看【path】中是否包含Node所在的路径
- 查看node的版本：【node -v】或【node --version】
- 查看自带的npm的版本：【npm -v】或【npm --version】

**设置默认路径**
默认在执行类似：`npm install express [-g] `的安装语句时，会将安装的模块安装到【C:\Users\用户名\AppData\Roaming\npm】路径中，
修改安装模块所在路径和缓存路径

    npm config set prefix "C:\_Web\node.js\node_global"
    npm config set cache "C:\_Web\node.js\node_cache"

**设置系统变量和用户变量**
在【系统变量】下新建【NODE_PATH】，输入【C:\_Web\node.js\node_global\node_modules】，将【用户变量】下的【Path】的【C:\Users\baiqi\AppData\Roaming\npm】修改为【C:\_Web\node.js\node_global】

配置完后，安装个module测试下，我们就安装最常用的express模块：【npm install express -g】  
-g是全局安装的意思，安装后发现express模块安装在刚才配置的全局模块路径【C:\_Web\node.js\node_global\node_modules\express】中，如果不加    参数 -g ，则会安装在当前路径下。
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/Kl39JIGm9f.png?imageslim)  

### 安装 Cordova
全局安装 Cordova

    npm install cordova –g

![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/k33Il7heC6.png?imageslim)  

中途若会出现“Error: shasum check failed for” 错误，多数是因为网络问题导致下载插件包失败，可以重试几次。

## 项目编写
### 创建项目
通过 `cd/d` 命令定位到要创建的cordova项目所在的目录，执行以下命令：`cordova create 文件夹名 包名 应用名`，例如：`cordova create hello com.bq.test helloWorld`
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/KIfAkihgg7.png?imageslim)  

目录简介：
- hooks：自定义扩展功能，存放自定义cordova命令的脚本文件。每个project命令都可以定义before和after的Hook，比如：before_build、after_build。
- platforms：平台目录，各自的平台代码就放在这里，可以放平台专属的代码。
- plugins：插件目录，安装的插件会放在这里。
- www：最重要的目录，存放项目主题的HTML5和JS代码的目录。app一开始打开的就是这个目录中`index.html`文件。
- config.xml：Cordova的核心配置信息，比如：项目使用了哪些插件、应用图标icon和启动页面SplashScreen，修改app的版本，名字等信息，还有平台的配置。

### 添加或移除平台
- 通过 `cd` 命令定位到项目目录，执行`cordova platforms ls`命令检查你电脑支持的平台。  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/0i06hE678d.png?imageslim)  

- 如果包含你要添加的平台，则可以通过执行`cordova platform add android`命令添加需要的平台。  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/F9f42e38Kl.png?imageslim)  

- 可以通过输入`cordova platforms rm android`移除对相应平台的支持。  
- 可以通过@版本号，来添加不同版本的android平台，如`cordova platforms add android@6.0`

### 添加和删除插件
- 先到这里去 [搜索插件](http://cordova.apache.org/plugins/)，记录下插件的名称
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/KJ8f0bLh4L.png?imageslim)  

- 然后添加插件：`cordova plugin add cordova-plugin-camera`
![](index_files/7bfed9c3-ccde-40e1-8806-f1fdf5fbcd4c.png)

- 删除插件：`cordova plugin rm cordova-plugin-camera`，使用rm和remove都可以
- 查看当前安装了哪些插件：`cordova plugin list`
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/2KhGKjA4dK.png?imageslim)  

这里的列表应该与文件目录的列表相同：
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/mB545Kb33H.png?imageslim)  

### 编写web页面
一个简单的主页是这样的：
```html
<!DOCTYPE html>
<html>

    <head>
        <title>Capture Photo</title>
        <meta http-equiv="Content-type" content="text/html; charset=utf-8">
        <script type="text/javascript" charset="utf-8" src="cordova.js"></script>
        <script type="text/javascript" charset="utf-8">
            var destinationType;
            document.addEventListener("deviceready", onDeviceReady, false);
            //Cordova加载完成会触发
            function onDeviceReady() {
                destinationType = navigator.camera.DestinationType;
            }
            //拍照
            function getPhoto() {
                //拍照并获取Base64编码的图像（quality : 存储图像的质量，范围是[0,100]）
                navigator.camera.getPicture(onPhotoDataSuccess, onFail, {
                    quality: 50,
                    destinationType: destinationType.DATA_URL
                });
            }
            //拍照成功
            function onPhotoDataSuccess(imageData) {
                console.log(imageData);
                var image = document.getElementById('image');
                image.style.display = 'block';
                image.src = "data:image/jpeg;base64," + imageData;
            }
            //拍照失败
            function onFail(message) {
                alert('拍照失败: ' + message);
            }
        </script>
    </head>

    <body style="margin:0;padding:0">
        <button style="font-size:23px;" onclick="getPhoto();">getPhoto</button> <br>
        <img style="display:none;width:100%;height:100%;" id="image" src="" />
    </body>

</html>
```

里面有一个按钮和一个显示图片的控件，点击按钮后可以通过相机拍照并获取图片，在返回时将图片显示在控件上。

### 编译调试
在命令窗口执行编译调指定平台的应用：`cordova build android`
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/JFDDiH1igm.png?imageslim)  

![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181016/bJlfFcc6LB.png?imageslim)  

完成后就在 `\platforms\android\app\build\outputs\apk\debug` 目录下生成了我们需要的Android安装包。
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181017/cHB8BL1HF5.png?imageslim)  

### cordova 常用命令
常用的几个命令：
- cordova create <工程路径> <包名> <工程名> //创建cordova工程
- cordova install android //将编译好的应用程序安装到模拟器上
- cordova emulate android //在模拟器上运行（前提是创建好AVD）
- cordova serve android //在浏览器运行
- cordova build android //打包cordova项目到android平台
- cordova run android //编译和运行项目，连接手机并打开usb调试时会在build后自动安装到真机
- cordova plugin add <插件完全限定名> //给项目添加插件
- cordova plugin remove <插件完全限定名> //删除插件。
- cordova plugin list //查看插件列表。
- cordova platforms add android //添加平台支持。

## 源码结构简要分析
### 文件目录结构
我们发现，通过 cordova 创建的Android项目和我们通过AndroidStudio创建的项目基本一样，比如文件目录结构：
![](index_files/4978b071-4613-4156-8578-9283cd285ed4.png)  

我们甚至可以直接使用 AndroidStudio 打开并构建此项目。
> 当然由于一些配置的原因可能无法直接运行，但是稍微改一点点东西就可以运行了。我就改了两个地方：
- 删除 \platforms\android\app\src\main\res\xml
- 按照AndroidStudio的提示更新了gradle版本

这个项目我放在了这个[GitHub仓库](https://github.com/baiqiantao/CordovaTest.git) 中。

![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181017/4km3ab9cDl.png?imageslim)  

### 核心代码
```java
public class MainActivity extends CordovaActivity
public class CordovaActivity extends Activity

protected CordovaWebView appView = new CordovaWebViewImpl(makeWebViewEngine());

onCreate() ->  loadUrl(launchUrl);
onDestroy() -> appView.handleDestroy();

setContentView(appView.getView());
```

### 清单文件
```xml
<?xml version='1.0' encoding='utf-8'?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.bq.test"【包名】
          android:hardwareAccelerated="true"【硬件加速】
          android:versionCode="10000"【版本号，版本码】
          android:versionName="1.0.0">

    【支持的屏幕】
    <supports-screens
        android:anyDensity="true"
        android:largeScreens="true"
        android:normalScreens="true"
        android:resizeable="true"
        android:smallScreens="true"
        android:xlargeScreens="true"/>

    【权限：网络、写SD卡】
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    【支持的SDK版本】
    <uses-sdk
        android:minSdkVersion="19"
        android:targetSdkVersion="27"/>

    【application】
    <application
        android:hardwareAccelerated="true"【硬件加速】
        android:icon="@mipmap/icon"
        android:label="@string/app_name"
        android:supportsRtl="true">
        
        【入口activity】
        <activity
            android:name="MainActivity"
            android:configChanges="orientation|keyboardHidden|keyboard|screenSize|locale"
            android:label="@string/activity_name"
            android:launchMode="singleTop"
            android:theme="@android:style/Theme.DeviceDefault.NoActionBar"
            android:windowSoftInputMode="adjustResize">
            <intent-filter android:label="@string/launcher_name">
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

        【适配7.0的文件权限】
        <provider
            android:name="org.apache.cordova.camera.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/camera_provider_paths"/>
        </provider>
    </application>

</manifest>
```

### 其他相关源码
其配置的 FileProvider
```xml
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
</paths>
```

settings.gradle：
```
include ":"
include ":CordovaLib"
include ":app"
```

app 模块的 build.gradle 的部分代码：
```Groovy
apply plugin: 'com.android.application'

compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}

dependencies {
    implementation fileTree(dir: 'libs', include: '*.jar')
    implementation(project(path: ":CordovaLib"))
    compile "com.android.support:support-v4:24.1.1+"
}
```

CordovaLib 的 build.gradle 的部分代码：
```groovy
apply plugin: 'com.android.library'
apply plugin: 'com.github.dcendents.android-maven'
apply plugin: 'com.jfrog.bintray'
```

至此，通过 Cordova 的一个完整的演示成功完成了。

2018-10-15