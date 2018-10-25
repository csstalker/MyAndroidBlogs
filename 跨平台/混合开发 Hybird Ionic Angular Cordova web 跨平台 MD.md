| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
混合开发 Hybird Ionic Angular Cordova web 跨平台 MD  
***  
目录  
===  

- [ Ionic](# ionic)
	- [ Ionic 简介](# ionic 简介)
	- [ Ionic 和 Cordova/PhoneGap 的关系](# ionic 和 cordovaphonegap 的关系)
- [ 零基础案例](# 零基础案例)
	- [ 环境配置](# 环境配置)
		- [ 下载](# 下载)
		- [ 淘宝镜像 cnpm](# 淘宝镜像 cnpm)
		- [ 安装 ionic](# 安装 ionic)
		- [ 查看 ionic 可以创建的模板](# 查看 ionic 可以创建的模板)
	- [ 项目编写](# 项目编写)
		- [ 创建 ionic 应用](# 创建 ionic 应用)
		- [ 添加Android平台：](# 添加android平台：)
		- [ 构建应用](# 构建应用)
		- [ 首页源码](# 首页源码)
	- [ 应用的目录结构](# 应用的目录结构)
	- [ Ionic Lab](# ionic lab)
  
# Ionic  
[官网](https://ionicframework.com/)    
[文档](https://ionicframework.com/docs/)    
[API](https://ionicframework.com/docs/api/)  
[GitHub](https://github.com/ionic-team/ionic)    
  
## Ionic 简介  
- Build amazing apps in one codebase, for any platform, with the web. One app running on everything  
- Ionic让Web开发人员比以往更轻松地`构建，测试，部署和监控`跨平台应用程序。  
- Ionic是开源移动应用程序开发框架，可以使用`Web技术`轻松构建高质量的原生和渐进式[progressive]的`Web应用程序`。  
- Ionic基于Web组件，与过去的版本相比，具有许多重要的性能、可用性和功能等方面的改进。  
  
ionic是一个用来开发混合手机应用的、开源的、免费的`代码库`。可以优化html、css和js的性能，构建高效的应用程序，而且还可以用于构建Sass和AngularJS的优化。这个框架的目的是从web的角度开发手机应用，基于`PhoneGap`的编译平台，可以实现编译成各个平台的应用程序。  
  
使用 Ionic 需要掌握的技术：HTML、CSS、Javascript、`Angular`  
  
- ionic 是一个强大的 HTML5 应用程序开发框架(HTML5 Hybrid Mobile App Framework)。 可以帮助您使用 Web 技术，比如 HTML、CSS 和 Javascript 构建`接近原生体验`的移动应用程序。  
- ionic 主要`关注外观和体验，以及和你的应用程序的 UI 交互`，特别适合用于基于 Hybird 模式的 HTML5 移动应用程序开发。  
- ionic是一个轻量的`手机UI库`，具有速度快，界面现代化、美观等特点。为了解决其他一些UI库在手机上运行缓慢的问题，它直接放弃了`IOS6和Android4.1`以下的版本支持，来获取更好的使用体验。  
  
**Ionic主要工作内容:**  
- 在Angular的基础上提供了更适合移动开发的一系列组件(menu,nav,card等)。  
- 在cordova的基础上提供了cordova插件的Typescipt封装，使得调用cordova插件更容易。  
- 提供了一组图标和主题，是的生成的移动应用更美观。  
  
**特点：**  
- ionic 基于Angular语法，简单易学。  
- ionic 是一个轻量级框架。  
- ionic 完美的融合下一代移动框架，支持 Angularjs 的特性， MVVM ，代码易维护。  
- ionic 提供了漂亮的设计，通过 SASS 构建应用程序，它提供了很多 `UI 组件`来帮助开发者开发强大的应用。  
- ionic 专注原生，让你看不出混合应用和原生的区别  
- ionic 提供了强大的命令行工具。  
- ionic 性能优越，运行速度快。  
- ionic提供很多`css组件`和`javascript UI库`。  
- ionic可以支持定制android和ios的插件，也支持服务端REST的敏捷开发。  
  
## Ionic 和 Cordova/PhoneGap 的关系  
Ionic是一个用来开发混合手机应用的、开源的、免费的`代码库`，这个框架的目的是从web的角度开发手机应用，`基于PhoneGap的编译平台`，可以实现`编译成各个平台的应用程序`。  
  
PhoneGap是一个采用`HTML，CSS和JavaScript`的技术，创建`移动跨平台`移动应用程序的快速开发平台。它使开发者能够在网页中调用`IOS，Android`等智能手机的核心功能，包括地理定位，加速器，联系人，声音和振动等，此外PhoneGap拥有丰富的`插件`可以调用。  
  
ionic是一个用来解决开发跨平台应用的方案。他是建立在Cordova的基础之上的，内部实现跨平台是由Cordova来实现的。相对于Cordova而言，他多了一些东西，例如的他的样式，例如AngularJS。  
  
通俗的讲：  
- Ionic是一款`H5混合移动app开发框架`(HTML5 Hybrid Mobile App Framework)  
- Phonegap是一款可以`打包`并且可以`让JS调用原生功能`的移动app框架  
  
至于为什么Ionic也可以打包，这是因为Ionic的打包插件是基于Phonegap/Cordova的。  
  
关于他们之间的关系，首先我们需要明确以下概念：  
- 即使我们将移动端web页面做得和原生应用及其相似，在我们的页面中也无法像原生应用那样调用原生的能力，当然通过输入框触发键盘、图库、拍照等操作不在这里“调用原生能力”的范畴。  
- 单纯的web页面不能提交到应用商店被用户使用。  
  
**Ionic和Angular**  
首先要明确的是`Ionic是Angular的衍生品`，Angular是单独的JS库，和jQuery一样能够独立用于开发应用，而Ionic只是对Angular进行了扩展，`利用Angular实现了很多符合移动端应用的组件`，并`搭建了很完善的样式库`，是对Angular最成功的应用样例。即使不使用Ionic，Angular也可与任意样式库，如Bootstrap、Foundation等搭配使用，得到想要的页面效果。  
  
**Ionic/Angular和Cordova**  
可能会有人被问道：Cordova比Ionic/Angular好吗？这就很尴尬了，根本是毫无意义的问题。`它们在混合开发中扮演的是不同的角`色：`Ionic/Angular负责页面的实现，而Cordova负责将实现的页面包装成原生应用（Android:apk；iOS:ipa`）。包装完成之后我们的页面才有可能调用设备的原生能力，最后才能上传到应用商店被用户使用。  
  
**Ionic/Angular和Cordova插件**  
- Cordova插件的作用是提供一个桥梁供页面和原生通信，因为我们的页面不能直接调用设备能力，所以需要与能够调用设备能力的原生代码（android:Java；ios:OC）通信，此时就需要Cordova插件了。  
- Cordova插件能够在任何Cordova工程中使用，和使用什么前端框架（如Ionic）无关。  
- Ionic2 中封装了`Ionic Native`，方便了Cordova插件的使用，但在 Ionic2 中仍然可以像 Ionic1 中一样使用Cordova插件，Ionic Native 不是必须的。  
- 即使在 Ionic2 中使用了 Ionic Native，也首先需要手动添加插件，如：`cordova plugin add cordova-plugin-pluginName`。  
  
# 零基础案例  
## 环境配置  
### 下载  
ionic 下载地址：https://ionicframework.com/docs/v1/overview/#download  
[ionic-v1.3.3.zip](https://code.ionicframework.com/1.3.3/ionic-v1.3.3.zip)  
  
下载后解压压缩包，包含以下目录：  
  
|  目录  |  作用  |  
| ------------ | ------------ |  
| css  | 样式文件目录  |  
| fonts  |  字体文件目录 |  
| js |  Javascript文件目录 |  
| version.json |  当前版本更新说明文件 |  
  
### 淘宝镜像 cnpm  
如果由于GFW导致插件下载不下来，可以用淘宝镜像来解决这个问题。  
  
方式一：使用cnpm(China npm)代替npm：  
- 首先安装 cnpm：`npm install -g cnpm --registry=https://registry.npm.taobao.org`  
- 查看cnpm版本信息：`cnpm -v`  
- 安装完成后，以后就可以使用cnpm这个命令来安装插件：`cnpm install -g ionic`  
  
方式一：通过更改访问地址：  
- 更改前的原始源地址为: `npm config set registry https://registry.npmjs.org/`  
- 更改为镜像淘宝源地址：`npm config set registry https://registry.npm.taobao.org/`  
- 查证自己所切换的源路径：`npm config get registry`  
  
### 安装 ionic  
- 查看安装的 ionic 版本信息：`ionic -v`  
- 如果找不到，执行以下命令安装 ionic：`npm install -g ionic`  
- 如果你已经安装，可以执行以下命令来更新版本:`npm update -g ionic`  
- 卸载ionic：`npm uninstall -g ionic`  
- 清除缓存：`npm cache clean --force`  
  
### 查看 ionic 可以创建的模板  
查看 ionic 可以创建的模板：`ionic start --list`  
  
|  name  |  project type  | description | 功能  
| ------------ | ------------ | ------------ | ------------ |  
|  blank    | angular       | A blank starter project  |空白项目  
|  blank    | ionic-angular | A blank starter project  |空白项目  
|  blank    | ionic1        | A blank starter project for Ionic  |空白项目  
|  tabs     | angular       | A starting project with a simple tabbed interface  |包含底部分页  
|  tabs     | ionic-angular | A starting project with a simple tabbed interface  |包含底部分页  
|  tabs     | ionic1        | A starting project for Ionic using a simple tabbed interface  |包含底部分页  
|  sidemenu | angular       | A starting project with a side menu with navigation in the content area  |包含滑动菜单  
|  sidemenu | ionic-angular | A starting project with a side menu with navigation in the content area  |包含滑动菜单  
|  sidemenu | ionic1        | A starting project for Ionic using a side menu with navigation in the content area  |包含滑动菜单  
|  super    | ionic-angular | A starting project complete with pre-built pages, providers and best practices for Ionic development.  |包含推荐的开发实践的完整项目(页面,服务划分等)  
|  tutorial | ionic-angular | A tutorial based project that goes along with the Ionic documentation  |教程项目  
|  aws      | ionic-angular | AWS Mobile Hub Starter  |Amazon 移动应用  
|  maps     | ionic1        | An Ionic starter project using Google Maps and a side menu  |包含地图  
  
tabs 工程：这是一个包含3个页面的应用程序，每个页面有标题、内容。  
  
## 项目编写  
### 创建 ionic 应用  
使用 ionic 提供的模板创建一个应用：`ionic start projectName [templateName]`  
例如：`ionic start ionicTest tabs`  
  
> 使用此命令时会提示让你用 ionic4 创建应用：  
You are about to create an Ionic 3 app. Would you like to try Ionic 4 (beta)?  
Ionic 4 uses the power of the modern Web and embraces拥抱 the Angular CLI and Angular Router to bring you the best version of Ionic ever.  
  
然后会提示你是否集成 ** ，选择 Y 之后会下载大量的东西，如果不使用淘宝镜像，根本不可能下载成功：  
```  
? Integrate your new app with Cordova to target native iOS and Android?  
Yes  
  
> ionic integrations enable cordova --quiet  
[INFO] Downloading integration cordova  
[INFO] Copying integrations files to project  
[OK] Integration cordova added!  
  
Installing dependencies may take several minutes.  
     *   IONIC  DEVAPP   *  
 Speed up development with the Ionic DevApp, our fast, on-device testing mobile app  
  -     Test on iOS and Android without Native SDKs  
  -     LiveReload for instant style and JS updates  
  
 -->    Install DevApp: https://bit.ly/ionic-dev-app    <--  
────────────────────────────────────────────────────────────────────────────────────  
> npm i  
npm WARN deprecated browserslist@2.11.3: Browserslist 2 could fail on reading Browserslist >3.0 config used in other tools.  
npm WARN deprecated hoek@2.16.3: The major version is no longer supported. Please update to 4.x or newer  
  
> node-sass@4.9.0 install C:\_Web\node.js\_workplace\testAndroid\ionicTest\node_modules\node-sass  
> node scripts/install.js  
  
Downloading binary from https://github.com/sass/node-sass/releases/download/v4.9.0/win32-x64-64_binding.node  
Download complete  ] - :  
Binary saved to C:\_Web\node.js\_workplace\testAndroid\ionicTest\node_modules\node-sass\vendor\win32-x64-64\binding.node  
Caching binary to C:\_Web\node.js\node_cache\node-sass\4.9.0\win32-x64-64_binding.node  
  
> uglifyjs-webpack-plugin@0.4.6 postinstall C:\_Web\node.js\_workplace\testAndroid\ionicTest\node_modules\uglifyjs-webpack-plugin  
> node lib/post_install.js  
  
> node-sass@4.9.0 postinstall C:\_Web\node.js\_workplace\testAndroid\ionicTest\node_modules\node-sass  
> node scripts/build.js  
  
Binary found at C:\_Web\node.js\_workplace\testAndroid\ionicTest\node_modules\node-sass\vendor\win32-x64-64\binding.node  
Testing binary  
Binary is fine  
npm notice created a lockfile as package-lock.json. You should commit this file.  
npm WARN optional SKIPPING OPTIONAL DEPENDENCY: fsevents@1.2.4 (node_modules\fsevents):  
npm WARN notsup SKIPPING OPTIONAL DEPENDENCY: Unsupported platform for fsevents@1.2.4: wanted {"os":"darwin","arch":"any"} (current: {"os":"win32","arch":"x64"})  
  
added 709 packages from 634 contributors in 426.888s  
> git init  
Initialized empty Git repository in C:/_Web/node.js/_workplace/testAndroid/ionicTest/.git/  
     *   IONIC  PRO   *  
 Supercharge your Ionic development with the Ionic Pro SDK  
  -     Track runtime errors in real-time, back to your original TypeScript  
  -    Push remote updates and skip the app store queue  
  
 Learn more about Ionic Pro: https://ionicframework.com/pro  
────────────────────────────────────────────────────────────────────────────────────  
```  
  
废了九牛二虎之力才下载完，下的东西基本都在项目的 node_modules 文件夹中！  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181020/AIEdeChfA2.png?imageslim)  
  
### 添加Android平台：  
添加Android平台：`ionic cordova platform add android`  
卧槽，输入命令后又是一顿猛操作，噼里啪啦的给我下载了一堆东西，耗时也至少有5分钟。  
```html  
√ Creating .\www directory for you - done!  
cordova platform add android --save  
Using cordova-fetch for cordova-android@~7.1.1  
Adding android project...  
Creating Cordova project for the Android platform:  
        Path: platforms\android  
        Package: io.ionic.starter  
        Name: ionicTest  
        Activity: MainActivity  
        Android target: android-27  
Android project created with cordova-android@7.1.1  
Android Studio project detected  
Android Studio project detected  
Discovered plugin "cordova-plugin-whitelist" in config.xml. Adding it to the project  
Installing "cordova-plugin-whitelist" for android  
  
This plugin is only applicable for versions of cordova-android greater than 4.0. If you have a previous platform version, you do *not* need this plugin since the whitelist will be built in.  
  
Adding cordova-plugin-whitelist to package.json  
Saved plugin info for "cordova-plugin-whitelist" to config.xml  
Discovered plugin "cordova-plugin-statusbar" in config.xml. Adding it to the project  
Installing "cordova-plugin-statusbar" for android  
Adding cordova-plugin-statusbar to package.json  
Saved plugin info for "cordova-plugin-statusbar" to config.xml  
Discovered plugin "cordova-plugin-device" in config.xml. Adding it to the project  
Installing "cordova-plugin-device" for android  
Adding cordova-plugin-device to package.json  
Saved plugin info for "cordova-plugin-device" to config.xml  
Discovered plugin "cordova-plugin-splashscreen" in config.xml. Adding it to the project  
Installing "cordova-plugin-splashscreen" for android  
```  
  
又是废了九牛二虎之力才下载完。  
  
### 构建应用  
构建Android项目：`ionic cordova build android`  
构建后直接运行：`ionic cordova run android`  
> 应该之前 cordova 的命令他也都能用  
  
这玩意可真是费劲，构建一次应用竟然也要一分钟！  
```html  
BUILD SUCCESSFUL in 55s  
48 actionable tasks: 48 executed  
Built the following apk(s):...\ionicTest\platforms\android\app\build\outputs\apk\debug\app-debug.apk  
```  
  
### 首页源码  
注意，和使用cordova创建的项目不同，ionic项目的www目录并不是源码目录，而是运行时候生成的目录，源码是在src目录下的，要修改index.html，只有修改src目录下的index.html才有效，修改www目录下的index.html没有任何意义。  
  
```html  
<!DOCTYPE html>  
<html lang="en" dir="ltr">  
  
    <head>  
        <script data-ionic="inject">  
            (function(w) {  
                var i = w.Ionic = w.Ionic || {};  
                i.version = '3.9.2';  
                i.angular = '5.2.11';  
                i.staticDir = 'build/';  
            })(window);  
        </script>  
        <meta charset="UTF-8">  
        <title>Ionic App</title>  
        <meta name="viewport" content="viewport-fit=cover, width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">  
        <meta name="format-detection" content="telephone=no">  
        <meta name="msapplication-tap-highlight" content="no">  
  
        <link rel="icon" type="image/x-icon" href="assets/icon/favicon.ico">  
        <link rel="manifest" href="manifest.json">  
        <meta name="theme-color" content="#4e8ef7">  
        <meta name="apple-mobile-web-app-capable" content="yes">  
        <meta name="apple-mobile-web-app-status-bar-style" content="black">  
  
        <script src="cordova.js"></script>  
        <link href="build/main.css" rel="stylesheet">  
    </head>  
  
    <body>  
        <ion-app></ion-app>  
        <script src="build/polyfills.js"></script>  
        <script src="build/vendor.js"></script>  
        <script src="build/main.js"></script>  
    </body>  
  
</html>  
```  
  
## 应用的目录结构  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181021/5fDda0CI3d.png?imageslim)  
  
```html  
|-- resources/                          * 资源文件,分为Android和Ios  
|  
|-- src/  
|    |-- app/  
|    |    |── app.component.ts          * 入口组件  
|    |    |── app.module.ts             * 主模块  
|    |    |── app.html                  * 主组件页面布局  
|    |    |── app.scss                  * 全局Sass  
|    |    |── main.ts                   * 主程序     
|    |  
|    |-- assets/  
|    |    |── icon/  
|    |    |    |── favicon.ico          * 页面图标  
|    |    |  
|    |    |── imgs/  
|    |         |── logo.png             * 程序logo  
|    |  
|    |-- pages/                          * 包含所有的页面  
|    |    |── about/                     * 关于页面 page   
|    |    |    |── about.html            * 关于页面 template  
|    |    |    |── about.ts              * 关于页面 code  
|    |    |    |── about.scss            * 关于页面 stylesheet  
|    |    |  
|    |    |── contact/                   * 联系人页面 page  
|    |    |    |── contact.html          * 联系人页面 template  
|    |    |    |── contact.ts            * 联系人页面 code  
|    |    |    |── contact.scss          * 联系人页面stylesheet  
|    |    |  
|    |    |── home/                     * 主页面 page  
|    |    |    |── home.html            * 主页面 template  
|    |    |    |── home.ts              * 主页面 code  
|    |    |    |── home.scss            * 主页面 stylesheet  
|    |    |  
|    |    |  
|    |    |── tabs/                      * 分页 page  
|    |    |    |── tabs.html             * 分页 template  
|    |    |    |── tabs.ts               * 分页 code  
|    |  
|    |── providers/                      * 包含所有的可注入服务  
|    |  
|    |── theme/                          * 应用主题文件  
|    |     |── variables.scss            * 应用scss变量  
|    |  
|    |-- index.html  
|  
|-- typings/                             * JavaScript 类型声明  
|    |── cordova-typings.d.ts  
|  
|-- www/                                 * ionic serve 运行时候生成站点目录  
|    |── assets/  
|    |    |── data/  
|    |    |  
|    |    |── fonts/  
|    |    |  
|    |    |── img/  
|    |  
|    |── build/  
|    |  
|    |── index.html  
|    |  
|    |── manifest.json  
|    |  
|    |── service-worker.js  
|  
|── .editorconfig                       * 代码风格  
|── .gitignore                          * git忽略文件  
|── LICENSE                             * License 文件  
|── README.md                           * Readme  
|── config.xml                          * Cordova 配置文件  
|── ionic.config.json                   * Ionic 配置文件  
|── package.json                        * Javascript 工程文件  
|── tsconfig.json                       * typescript 编译配置文件  
|── tslint.json                         * TypeScript 书写规范规则文件  
```  
  
## Ionic Lab  
Ionic Lab 是桌面版的开发环境，如果你不喜欢使用命令行操作，Ionic Lab 将会满足你的需求。  
Ionic Lab 为开发者提供了一个更简单的方法来开始，编译，运行，和模拟运行Ionic的应用程序。  
[下载地址](http://lab.ionic.io/)，貌似已经关闭了，无法访问了，可以通过这里 [直接下载](http://dx2.pc0359.cn/soft/i/ioniclabes.rar)。  
  
通过以上界面你可以完成以下操作：  
- 创建应用  
- 预览应用  
- 编译应用  
- 运行应用  
- 上传应用  
- 运行日志查看  
  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181021/4fmlACAccB.png?imageslim)  
> Ionic Lab 目前已停止更新。  
  
2018-10-21  
