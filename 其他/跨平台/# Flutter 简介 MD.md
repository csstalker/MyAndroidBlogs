| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Flutter 简介  
***  
目录  
===  

- [Flutter 1.0 正式版: Google 的便携 UI 工具包](#Flutter-10-正式版-Google-的便携-UI-工具包)
	- [Flutter 的 特点](#Flutter-的-特点)
  
# Flutter 1.0 正式版: Google 的便携 UI 工具包  
[原文](https://mp.weixin.qq.com/s/hCIN42OMmmc6HkOveegwWQ)  
  
文 / Tim Sneath，Google Dart & Flutter 产品组产品经理  
  
今天我们非常高兴的宣布，Flutter 的 `1.0 版本正式发布`！Flutter 是 Google 为您打造的 `UI 工具包`，帮助您`通过一套代码同时在 iOS 和 Android 上构建媲美原生体验的精美应用`！  
  
如今，移动开发者们在同时兼容 iOS 和 Android 时有两种选择：走原生开发路线，把界面和逻辑在不同平台分别实现；抑或用同一套代码兼容多个平台，但这往往意味着运行速度和产品保真度的损失。我们坚信，Flutter 为您提供了一套`两全其美`的解决方案——既能用原生 ARM 代码直接调用的方式来加速图形渲染和 UI 绘制，又能同时运行在两大主流移动操作系统上。  
  
## Flutter 的 特点  
Flutter 并`不是要替代原生应用开发模式`，您可以用 Flutter 开始一个`全新的应用`，也可以把 Flutter 理解为应用内置的一个引擎，把这个引擎`引入到现有的工程里`。  
  
让我来从以下四个特点为您介绍 Flutter:   
  
- Flutter 可以帮助您构建`界面精美`的应用。  
我们希望设计师们都能充分发挥自己的想象，尽情施展精美绝伦的创意，而`不受框架局限性的干扰`。通过 Flutter，您可以在屏幕上有`像素级的掌控`。`Flutter 强大的图像组合能力让您近乎可以不受限制的堆叠任何图形、视频、文本和控件`。Flutter 内置了一系列的 widgets (`在 Flutter 里，几乎所有内容都是 widget`)，这些 widgets 可以帮助您在 iOS 和 Android 上实现极致的视觉体验。Flutter 集成并有最大程度的实现了 `Material Design` —— 这是 Google 为数字体验所创作的开放的设计系统。  
  
- Flutter 的另一个特点：`速度快`。  
它基于 Skia 2D 硬件加速图形引擎，该引擎也同样用在了 Chrome 和 Android 平台。`媲美原生应用的速度`，这是我们在构建 Flutter 时的初心。Flutter 的代码基于 `Dart` 平台，它可以被编译成 iOS 和 Android 平台上 32 位和 64 位的 ARM 代码。  
  
- Flutter 非常`高效`。  
Flutter 引入了 `Stateful Hot Reload`（保持应用状态的热重载），这个革命性的新特性可以让移动开发者和设计师们`实时`迭代应用程序。通过 Stateful Hot Reload，无需重新启动应用，你就可以在程序运行的时候直接看到代码修改之后的效果，Stateful Hot Reload 改变了开发者们编写应用的方式。在我们的用户反馈中，开发者们表示该特性使得开发效率提升了三倍。  
  
- 最后，Flutter 是`开放`的。  
Flutter 是一个基于 BSD-style 许可的开源项目，全球数百位开发者在为其贡献代码。Flutter 的插件生态系统平台也充满活力。有数千款插件已经发布，避免了重复造轮子。由于 Flutter 应用程序使用标准的 Android 和 iOS 的编译打包工具 (build tools)，因此它的开放还体现在您可以使用原生开发资源。比如，您同样可以在 Android 上使用 `Kotlin` 或者 Java，在 iOS 上使用 Swift 或者 Objective-C 来写逻辑或者界面。  
  
结合以上这些，再加上您顺手的开发工具，比如 `Visual Studio Code`、`Android Studio`、`IntelliJ` 或其他您青睐的开发者编辑器，您就可以开始使用 Flutter 体验一套代码同时在 iOS 和 Android上实现原生应用效果的乐趣了。  
  
