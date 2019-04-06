| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [----------  创建型模式 - 6个  ----------](#------------创建型模式---6个------------)
	- [- 单例模式](#--单例模式)
	- [- 建造者模式](#--建造者模式)
	- [- 原型模式](#--原型模式)
	- [- 工厂模式3个](#--工厂模式3个)
- [----------  行为型模式 - 11个  ----------](#------------行为型模式---11个------------)
	- [- 策略模式](#--策略模式)
	- [- 观察者模式](#--观察者模式)
	- [- 模板方法模式](#--模板方法模式)
	- [- 责任链模式](#--责任链模式)
	- [- 备忘录模式](#--备忘录模式)
	- [- 命令模式](#--命令模式)
	- [解释器模式](#解释器模式)
	- [迭代器模式](#迭代器模式)
	- [状态模式](#状态模式)
	- [中介者模式](#中介者模式)
	- [访问者模式](#访问者模式)
- [----------  结构型模式 - 7个  ----------](#------------结构型模式---7个------------)
	- [- 装饰器模式](#--装饰器模式)
	- [- 组合模式](#--组合模式)
	- [- 代理模式](#--代理模式)
	- [- 适配器模式](#--适配器模式)
	- [- 门面模式](#--门面模式)
	- [- 亨元模式](#--亨元模式)
	- [桥接模式](#桥接模式)
  
值得收藏的一些文章  
- [一篇超实用博客：Android开发中常见的设计模式](https://www.cnblogs.com/android-blogs/p/5530239.html)  
- [四月葡萄的系列文章](https://www.jianshu.com/p/a3e844619ed2)  
- [xxq2dream的系列文章](https://www.jianshu.com/p/1b3710bee2ef)  
- [Android设计模式之23种设计模式一览](https://blog.csdn.net/happy_horse/article/details/50908439)  
- [Android设计模式简单理解](https://www.jianshu.com/p/7ef9092977fb)  
- [android源码中的设计模式](https://blog.csdn.net/ruizhenggang/article/details/78837183)  
  
# ----------  创建型模式 - 6个  ----------  
  
## - 单例模式  
懒汉式、饿汉式、静态内部类、双重锁检查、枚举  
  
## - 建造者模式  
最明显的标志就是使用Build类  
AlterDialog、Notification  
各大开源框架广泛使用 Gson、Okhttp  
  
## - 原型模式  
更快的获取到一个相同属性的对象  
Intent、OkHttpClient  
  
## - 工厂模式3个  
简单工厂(静态工厂)、工厂方法、抽象工厂  
BitmapFactory、MediaPlayerFactory、Executors线程池工厂类  
  
# ----------  行为型模式 - 11个  ----------  
  
## - 策略模式  
封装算法  
出行策略：公交车、的士、自驾策略下的费用、时间计算等  
插值器、估值器、缓存策略，增删改查策略  
  
## - 观察者模式  
监听器、点击事件、滚动事件  
rxjava、eventbus、广播  
架构组件Livedata  
  
## - 模板方法模式  
定义算法的骨架、结构、流程，细节由子类实现  
Activity生命周期  
BaseFragment里面定义的getLayout、initView、懒加载onLazyLoad  
  
## - 责任链模式  
需要指定处理链，屏蔽了请求的处理过程，客户端不需要知道请求会被哪些对象处理  
事件分发机制、有序广播、审批流程  
ClassLoader的双亲委托模型  
  
## - 备忘录模式  
捕获一个对象的内部状态，并在该对象之外保存，以便需要时恢复到原先保存的状态  
Activity的onSaveInstanceState，保存Bundle类型的savedInstanceState，当Activity重建时可以恢复状态  
  
## - 命令模式  
将来自客户端的请求封装为一个独立的对象，将行为请求者与行为实现者解耦，可以实现对请求进行排队、撤消、记录请求日志等  
Handler机制，Runnable、Callable等接口的设计  
广播、eventbus  
  
## 解释器模式  
正则表达式Pattern  
时间格式化Format、Html格式化、Integer.parseInt解析、SQL解析  
解释加减乘除、或与非  
  
## 迭代器模式  
把对容器中包含对象的访问委让给外部类，使用Iterator按顺序进行遍历访问  
比如至少提供hasNext()和next()方法  
所有的集合都有 Iterator iterator() 方法  
  
## 状态模式  
根据内部的状态动态的选择行为；状态改变的时候会改变其行为；状态改变的时候其行为也随之改变  
把所研究的对象的行为包装在不同的状态对象里  
登录状态、未登录状态下用户的行为  
  
## 中介者模式  
房屋中介  
将网状结构转化成星型结构，可以避免同事类之间的过度耦合  
同事对象只知道中介者而不知道其他同事对象，同事类之间必须通过中介者才能进行消息传递  
  
## 访问者模式  
相对比较复杂的一个，项目中可能见得非常少。  
把数据结构和作用于结构上的操作解耦合，适用于数据结构稳定、算法易变的系统  
案例：领导(CEO、CTO、Leader)对员工(程序员、产品、测试)进行考核  
  
# ----------  结构型模式 - 7个  ----------  
  
## - 装饰器模式  
扩展功能，相比于继承基类来扩展功能，使用装饰器模式更加的灵活  
解决类膨胀、类爆炸、继承体系臃肿的问题  
Context和它的包装类ContextWrapper(其实并没有增强功能)  
IO流中的BufferReader  
  
## - 组合模式  
View和ViewGroup的组合、文件结构File  
集合List的add和addAll  
树、递归、叶子结点  
  
## - 代理模式  
AIDL、Binder  
静态代理、动态代理  
Proxy、Cglib  
ActivityManagerProxy代理ActivityManagerService  
  
## - 适配器模式  
listview、recycleview、ViewPage  
流类型转换InputStreamReader、OutputStreamWriter  
集合和数组类型转换 Arrays.asList()  
  
## - 门面模式  
医院的接待、功能的封装(工具类)  
Context与ContextImpl  
  
## - 亨元模式  
String常量池、Integer、Message常量池  
享元对象能做到共享的关键是区分内部状态和外部状态  
一般需要用到单例模式、工厂模式、HashMap  
降低内存中对象的数量，节省内存，但使系统将变得复杂，且在读取外部状态时耗时增加  
  
## 桥接模式  
车的轮胎，人的衣服  
业务抽象角色引用业务实现角色，业务抽象角色的部分实现是由业务实现角色完成的  
将一个系统的抽象部分和实现部分分离，使它们都可以独立地进行变化  
Window 与 WindowManager、应用层与Native层之间的交互  
  
2019-4-1  
