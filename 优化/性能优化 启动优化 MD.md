| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
启动优化 MD  
***  
目录  
===  

- [启动优化](#启动优化)
- [性能优化之启动优化](#性能优化之启动优化)
	- [启动时间分析](#启动时间分析)
	- [启动时间优化](#启动时间优化)
- [性能优化之启动加速35%](#性能优化之启动加速35%)
	- [初识启动加速](#初识启动加速)
	- [主题切换](#主题切换)
	- [Avoid Heavy App Initialization](#Avoid-Heavy-App-Initialization)
	- [Diagnosing The Problem](#Diagnosing-The-Problem)
	- [问题](#问题)
  
# 启动优化  
[启动优化官方文档](https://developer.android.com/topic/performance/launch-time.html)  
[启动优化官方视频教程](https://www.youtube.com/watch?v=Vw1G1s73DsY&index=74&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE)  
  
# 性能优化之启动优化  
[参考文章](https://github.com/sucese/android-open-source-project-analysis/blob/master/doc/Android应用开发实践篇/Android应用优化/02Android应用优化：启动优化.md)  
  
> 冷启动、暖启动、热启动  
计算冷启动时间，Displayed Time 日志；通过ADB命令测量启动时间，录屏法，不保留后台进程；  
延迟初始化，后台任务(getWindow().getDecorView().post，IntentService)  
懒加载(ViewPager+Fragment懒加载)，界面预加载(闪屏页，弹窗形式的广告页，windowIsFloating)  
TraceView和Systrace  
白屏(windowBackground，背景透明、快速显示背景)  
  
应用按照启动场景的不同可以分为三种启动方式：  
- 冷启动：冷启动指应用进程还没有创建，应用`从头开始启动`。  
- 暖启动：暖启动指的是`应用进程已经创建`，但是当前页面的Activity被销毁或者还未创建，需要重新创建。  
- 热启动：热启动指的是应用进程已经创建，当前页面的Activity也已经创建，驻留在内存中，只需要将来重新`带到前台`来即可。  
  
当用户点击一个桌面上的图标，启动一个未经启动过的应用，其冷启动的流程如下图所示：  
https://github.com/guoxiaoxing/android-open-source-project-analysis/raw/master/art/practice/performance/app_start_structure.png  
  
- 加载和启动应用程序。  
- 启动后立即显示应用程序的空白开始窗口。  
- 创建应用程序进程。  
- 创建应用程序对象。  
- 启动主线程。  
- 创建MainActivity。  
- 加载布局文件。  
- 铺设屏幕。  
- 执行初始绘制。  
  
当应用完成第一次绘制以后，系统就会将当前显示的背景窗口替换为MainActivity，用户就看到了应用的首页，一个应用的冷启动流程也就算完成了。  
  
通常来说应用的启动优化的重点就在于冷启动的优化上，暖启动和热启动它们的优化涉及到具体的页面，在做这些页面时做好缓存，减少不必要的创建工作和网络请求都是常用的优化手段，我们重点来看看冷启动的优化。  
  
首先，要学会如何`计算冷启动时间`，找出冷启动的性能瓶颈。  
  
## 启动时间分析  
  
从上面启动图可以看出启动时间分为两个部分，`系统创建进程的时间`和`应用进程启动的时间`，前者是由系统自行完成的，一般都会很快，我们也干预不了，我们能做的就是去优化应用进程启动，具体说来就是`从Application的onCreate()执行开始到MainActivity的onCreate()执行结束`这一段时间。  
  
那么如何计算这个启动时间呢，API 19 之后系统会给一个启动时间`Displayed Time`的Log，如下所示：  
```  
Displayed com.guoxiaoxing.android.framework.demo/.MainActivity: +557ms  
```  
  
这个Displayed Time的Log只是`布局加载完成`显示的时间，如果我们在MainActivity做了一些数据的加载，时间就不止这么多了，API 19 以后系统为我们提供了一个自定义上报时间的工具，如下所示：  
  
```java  
try{  
    Thread.sleep(2000); // 模拟数据加载  
    reportFullyDrawn(); //上报启动时间，这个方法在API 19 以下不支持，会crash  
}catch(Exception e){  
    e.printStackTrace();  
}  
```  
  
然后你就可以看到一条这样的Log，如下所示：  
```  
Fully drawn com.guoxiaoxing.android.framework.demo/.MainActivity: +2s557ms  
```  
  
此外，我们还可以`通过ADB命令测量启动时间`：  
> 启动首页MainActivity  
```  
adb shell am start -W com.guoxiaoxing.android.framework.demo/.MainActivity  
```  
  
该命令会打印以下Log：  
```  
Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.guoxiaoxing.android.framework.demo/.MainActivity }  
Status: ok  
Activity: com.guoxiaoxing.android.framework.demo/.MainActivity  
ThisTime: 2658  
TotalTime: 2658  
WaitTime: 2697  
Complete  
```  
  
以上三个时间的概念如下所示：  
- ThisTime: 最后一个Activity的启动耗时。  
- TotalTime: 自己所有Activity的启动耗时。  
- WaitTime: ActivityManagerService启动Activity的总耗时（包括当前Activity的onPause()和自己Activity的启动时间）  
  
除了上述两种方法外，还有一个录屏法，如下所示：  
```  
adb shell screenrecord --bugreport /sdcard/test.mp4  
```  
  
我们可以借助播放器的满放功能，利用右上角的时间戳逐帧的去分析整个启动流程，这样我们就可以知道哪个环节出现了性能问题。  
  
另外，在调试冷启动时间的时候，还可以把开发者选项里的`不保留后台进程`打开  
  
## 启动时间优化  
  
前面我们说过启动耗时的地方其实主要有个两个地方：  
  
- Application的onCreate()方法  
- MainActivity的onCreate()方法  
  
优化的手段也无非三种：  
- 延迟初始化  
- 后台任务  
- 界面预加载  
  
**延迟初始化**  
  
延迟初始化主要指的是在Application的onCreate()方法里执行的组件库的初始化工作，这里可以仔细梳理一遍，哪些是一定要当前初始化完成的，哪些是可以放在后台任务里懒加载的。  
  
**后台任务**  
  
后台任务指的是`将一些耗时操作添加到后台执行`，减少对界面的阻塞，后台任务的实现也有很多种，如下所示：  
  
方式一：通过view的`post`方式可以将一些工作推迟到`布局完成之后`进行。  
```java  
getWindow().getDecorView().post(() -> /* TODO do some work */ );  
```  
  
方式二：通过`IntentService`执行后台任务  
  
**界面预加载**  
  
界面预加载主要指的是MainActivity的预加载问题，现在的应用一般都会有个闪屏页，`闪屏页`一般有个2s左右的广告时间，这个就是可以利用的黄金时间，可以利用它做MainActivity的预加载。  
  
利用`TraceView`和`Systrace`做好耗时组件和方法的分析与优化。  
  
如果主页是通过ViewPager实现的，要做好ViewPager的懒加载。  
  
另外，还可以给MainActivity设置一个`windowBackground`，免得MainActivity在加载的时候一直白屏。  
  
  
# 性能优化之启动加速35%  
[参考文章](https://www.jianshu.com/p/f5514b1a826c)  
[官方文档：Launch-Time Performance](https://developer.android.com/topic/performance/launch-time.html)  
  
随着项目版本的迭代，App的性能问题会逐渐暴露出来，而好的用户体验与性能表现紧密相关，从本篇文章开始，我将开启一个Android应用性能优化的专题，从理论到实战，从入门到深挖，手把手将性能优化实践到项目中，欢迎持续关注！  
  
那么第一篇文章我就从应用的启动优化开始，根据实际案例，打造闪电般的App启动速度。  
  
## 初识启动加速  
  
来看一下Google官方文档[《Launch-Time Performance》](https://developer.android.com/topic/performance/launch-time.html)对应用启动优化的概述；  
  
应用的启动分为`冷启动、热启动、温启动`，而启动最慢、挑战最大的就是冷启动：系统和App本身都有更多的工作要从头开始！  
  
应用在冷启动之前，要执行三个任务：  
- 加载启动App；  
- App启动之后立即展示出一个空白的Window；  
- 创建App的进程；  
  
而这三个任务执行完毕之后会马上执行以下任务：  
- 创建App对象；  
- 启动Main Thread；  
- 创建启动的Activity对象；  
- 加载View；  
- 布置屏幕；  
- 进行第一次绘制；  
  
而一旦App进程完成了第一次绘制，系统进程就会用Main Activity替换已经展示的Background Window，此时用户就可以使用App了。  
  
![](https://upload-images.jianshu.io/upload_images/4056837-36c808285a70cf1b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)  
  
应用冷启动流程图  
  
作为普通应用，App进程的创建等环节我们是无法主动控制的，可以优化的也就是Application、Activity创建以及回调等过程。  
  
同样，Google也给出了启动加速的方向：  
- 利用提前展示出来的Window，`快速展示出来一个界面，给用户快速反馈的体验`；  
- 避免在启动时做密集沉重的初始化（Heavy app initialization）；  
- 定位问题：避免I/O操作、反序列化、网络操作、布局嵌套等。  
  
备注：方向1属于治标不治本，只是表面上快；方向2、3可以真实的加快启动速度。  
  
接下来我们就在项目中实际应用。  
  
## 主题切换  
  
按照官方文档的说明：使用Activity的`windowBackground`主题属性来为启动的Activity提供一个简单的drawable。  
  
//...  
  
这样在启动的时候，会先展示一个界面，这个界面就是Manifest中设置的`Style`，`等Activity加载完毕后，再去加载Activity的界面`，而在Activity的界面中，我们将主题重新设置为正常的主题，从而产生一种快的感觉。  
  
不过如上文总结，这种方式其实并没有真正的加速启动过程，而是通过交互体验来优化了展示的效果。  
  
## Avoid Heavy App Initialization  
  
通过代码分析，我们可以得到App启动的业务工作流程图：  
![](https://upload-images.jianshu.io/upload_images/4056837-c5cc3971de4b3547.png?imageMogr2/auto-orient/)  
  
这一章节我们重点关注初始化的部分：在Application以及首屏Activity中我们主要做了：  
- MultiDex以及Tinker的初始化，最先执行  
- Application中主要做了各种三方组件的初始化  
  
项目中除听云之外其余所有三方组件都抢占先机，在Application主线程初始化，这样的初始化方式肯定是过重的:  
- 考虑异步初始化三方组件，不阻塞主线程  
- 延迟部分三方组件的初始化；实际上我们粗粒度的把所有三方组件都放到异步任务里，`可能会出现WorkThread中尚未初始化完毕但MainThread中已经使用的错误`，因此这种情况建议延迟到使用前再去初始化；  
- 而如何开启WorkThread同样也有讲究，这个话题在下文详谈。  
  
**项目修改：**  
  
- 将友盟、Bugly、听云、GrowingIO、BlockCanary等组件放在WorkThread中初始化；  
- 延迟地图定位、ImageLoader、自有统计等组件的初始化：  
    - 地图及自有统计延迟4秒，此时应用已经打开  
    - ImageLoader因为调用关系不能异步以及过久延迟，初始化从Application延迟到SplashActivity  
    - EventBus因为在Activity中使用所以必须在Application中初始化  
  
注意：闪屏页的2秒停留可以利用，把耗时操作延迟到这个时间间隔里。  
  
## Diagnosing The Problem  
  
本节我们实际定位耗时的操作，在开发阶段我们一般使用 **BlockCanary** 或者 **ANRWatchDog** 找耗时操作，简单明了，但是 **无法得到每一个方法的执行时间以及更详细的对比信息**。我们可以通过 **Method Tracing** 或者 **DDMS** 来获得更全面详细的信息。  
  
启动应用，点击 Start Method Tracing，应用启动后再次点击，会自动打开刚才操作所记录下的`.trace`文件，建议使用`DDMS`来查看，功能更加方便全面。  
  
![](https://upload-images.jianshu.io/upload_images/4056837-6d9ea37ee381b29e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)  
  
![](https://upload-images.jianshu.io/upload_images/4056837-ab34c441b40d9df9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)  
  
左侧为发生的具体线程，右侧为发生的时间轴，下面是发生的具体方法信息。注意两列：Real Time/Call（实际发生时间），Calls+RecurCalls/Total（发生次数）；  
  
上图我们可以得到以下信息：  
- 可以直观看到MainThread的时间轴很长，说明大多数任务都是在MainThread中执行；  
- 通过 Real Time/Call 降序排列可以看到程序中的部分代码确实非常耗时；  
- 在下一页可以看出来部分三方SDK也比较耗时；  
  
即便是耗时操作，但是只要正确发生在WorkThread就没问题。因此我们需要确认这些方法执行的线程以及发生的时机。这些操作如果发生在主线程，可能不构成ANR的发生条件，但是卡顿是再算难免的！  
  
结合上章节图App冷启动业务工作流程图中业务操作以及分析图，再次查看代码我们可以看到：部分耗时操作例如IO读取等确实发生在主线程。事实上在traceview里点击执行函数的名称不仅可以跟踪到父类及子类的方法耗时，也可以在方法执行时间轴中看到具体在哪个线程以及耗时的界面闪动。  
  
分析到部分耗时操作发生在主线程，那我们把耗时操作都改到子线程是不是就万事大吉了？非也！！  
  
- 卡顿不能都靠异步来解决，`错误的使用工作线程不仅不能改善卡顿，反而可能加剧卡顿`。是否需要开启工作线程需要根据具体的性能瓶颈根源具体分析，对症下药，不可一概而论；  
- 而如何开启线程同样也有学问：`Thread、ThreadPoolExecutor、AsyncTask、HandlerThread、IntentService`等都各有利弊；例如通常情况下`ThreadPoolExecutor`比Thread更加高效、优势明显，但是特定场景下单个时间点的表现Thread会比ThreadPoolExecutor好：同样的创建对象，ThreadPoolExecutor的开销明显比Thread大；  
- 正确的开启线程也不能包治百病，例如执行网络请求会创建线程池，而在Application中正确的创建线程池势必也会降低启动速度；因此延迟操作也必不可少。  
  
通过对traceview的详细跟踪以及代码的详细比对，我发现卡顿发生在：  
- 部分数据库及IO的操作发生在首屏Activity主线程；  
- `Application中创建了线程池`；  
- 首屏Activity网络请求密集；  
- `工作线程使用未设置优先级`；  
- `信息未缓存，重复获取同样信息`；  
- 流程问题：例如闪屏图每次下载，当次使用；  
  
以及其它细节问题：  
- 执行无用老代码；  
- 执行开发阶段使用的代码；  
- 执行重复逻辑；  
- 调用三方SDK里或者Demo里的多余代码；  
  
**项目修改：**  
- 数据库及IO操作都移到工作线程，并且设置线程优先级为`THREAD_PRIORITY_BACKGROUND`，这样工作线程最多能获取到10%的时间片，优先保证主线程执行。  
- 流程梳理，延后执行；实际上，这一步对项目启动加速最有效果。通过流程梳理发现部分流程调用时机偏早、失误等，例如：  
    - 更新等操作无需在首屏尚未展示就调用，造成资源竞争；  
    - 调用了IOS为了规避审核而做的开关，造成网络请求密集；  
    - 自有统计在Application的调用里创建数量固定为5的线程池，造成资源竞争，在上图traceview功能说明图中最后一行可以看到编号12执行5次，耗时排名前列；此处线程池的创建是必要但可以延后的。  
    - 修改广告闪屏逻辑为下次生效。  
  
**其它优化**  
- 去掉无用但被执行的老代码；  
- 去掉开发阶段使用但线上被执行的代码；  
- 去掉重复逻辑执行代码；  
- 去掉调用三方SDK里或者Demo里的多余代码；  
- `信息缓存，常用信息只在第一次获取，之后从缓存中取`；  
- 项目是多进程架构，`只在主进程执行Application的onCreate()`；  
  
![](https://upload-images.jianshu.io/upload_images/4056837-1c99837a19270fff.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)  
  
通过以上三步及三方组件的优化：Application以及首屏Activity回调期间主线程就没有耗时、争抢资源等情况了。此外还涉及布局优化、内存优化等部分技术，因对于应用冷启动一般不是瓶颈点，这里不展开详谈，可根据实际项目实际处理。  
  
**对比效果**  
可通过ADB命令统计应用的启动时间：  
```  
adb shell am start -W 首屏Activity  
```  
  
## 问题  
将启动速度加快了35%不代表之前的代码都是问题，从业务角度上将，代码并没有错误，实现了业务需求。但是在启动时这个注重速度的阶段，忽略的细节就会导致性能的瓶颈。**  
  
开发过程中，对核心模块与应用阶段如启动时，使用`TraceView`进行分析，尽早发现瓶颈。  
  
**还可以继续优化的方向？**  
- 项目里使用Retrofit网络请求库，FastConverterFactory做Json解析器，`TraceView`中看到FastConverterFactory在创建过程中也比较耗时，考虑将其换为GsonConverterFactory。但是因为类的继承关系短时间内无法直接替换，作为优化点暂时遗留；  
- 可以考虑根据实际情况`将启动时部分接口合并为一，减少网络请求次数，降低频率`；  
- `相同功能的组件只保留一个`，例如：友盟、GrowingIO、自有统计等功能重复；  
- 使用 [ReDex](https://github.com/facebook/redex) 进行优化；实验Redex发现Apk体积确实是小了一点，但是启动速度没有变化，或许需要继续研究。  
  
**异步、延迟初始化及操作的依据？**  
注意一点：`并不是每一个组件的初始化以及操作都可以异步或延迟`；是否可以取决组件的调用关系以及自己项目具体业务的需要。保证一个准则：`可以异步的都异步，不可以异步的尽量延迟`。让应用先启动，再操作。  
  
**通用应用启动加速套路？**  
- 利用主题快速显示界面；  
- 异步初始化组件；  
- 梳理业务逻辑，延迟初始化组件、操作；  
- 正确使用线程；  
- 去掉无用代码、重复逻辑等。  
  
2019-3-1  
