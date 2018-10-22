[Markdown版本](https://github.com/baiqiantao/MyAndroidBlogs)
[我的GitHub](https://github.com/baiqiantao)


---------------
RxJava RxLifecycle 生命周期 内存泄漏  
Demo地址：https://github.com/baiqiantao/RxJavaDemo.git  
GitHub：https://github.com/trello/RxLifecycle  
***
目录
===
[TOC]


# 简介
另一个功能与此库类似、核心设计借鉴此库、此库作者也参与设计、此库作者认为比此库好、但是星星比较少、国内不流行、上手比较难的库：https://github.com/uber/AutoDispose  


## 添加依赖
```
implementation 'com.trello.rxlifecycle2:rxlifecycle-components:2.2.2'
```


完整依赖项：
```
implementation 'com.trello.rxlifecycle2:rxlifecycle:2.2.2' //基础库
implementation 'com.trello.rxlifecycle2:rxlifecycle-android:2.2.2' //Android中使用的库，内部引用了基础库，如果使用此库则无需再引用基础库
implementation 'com.trello.rxlifecycle2:rxlifecycle-components:2.2.2' //Android组件库，里面定义了例如RxActivity、RxFragment等Android组件，内部引用了基础库和Android库，如果使用此库则无需再重复引用
implementation 'com.trello.rxlifecycle2:rxlifecycle-components-preference:2.2.2' // Android使用的库，继承NaviActivity使用
implementation 'com.trello.rxlifecycle2:rxlifecycle-navi:2.2.2' //Android使用的库，继承LifecycleActivity使用，需要引入Google的仓库支持
implementation 'com.trello.rxlifecycle2:rxlifecycle-android-lifecycle:2.2.2' //使用AndroidLifecycle
implementation 'com.trello.rxlifecycle2:rxlifecycle-kotlin:2.2.2' // 支持Kotlin语法的RxLifecycle基础库
implementation 'com.trello.rxlifecycle2:rxlifecycle-android-lifecycle-kotlin:2.2.2' // 支持Kotlin语法的Android库
```


## 产生背景
随着 RxJava 及 RxAndroid 的逐渐推广，使用者越来越多，但是有一个问题，RxJava 的使用不当极有可能会导致内存泄漏，通常我们会有如下几种解决方案：
- 解决方案1：手动为 `RxJava` 的每一次订阅进行控制，在指定的时机进行取消订阅。这种情况在订阅比较少的情况下没问题，但是如果非常多，没有意义的脏代码会让你写到吐。
- 解决方案2：在 `BaseActivity` 中记录一个 `List<Subscription> 或 List<Disposable>`，每次使用后 add 进队列，在页面 `onDestory` 统一 `unsubscribe() 或 dispose()`。但是从一开始就发现这个方法实在是太搓了，脏代码并没有减少。
- 解决方案3：后来发现很多人用一个类 `CompositeSubscriptio`n，然而发现这个类仅仅是帮我们维护了一个 Set<Disposable> 而已，你妹，看起来高大上的样子，实际上屁都不是！


而 `RxLifecycle` 就是专门解决这一痛点的。一开始发现要让我们的 `Activity 或 Fragment` 继承自库中的类，就比较担心局限性，后来发现，这货这么做完全是为了帮我们省事呀，我们常用的一些比如 `FragmentActivity、AppCompatActivity` 等，基本都有相应的 `Rx***` 类，实际使用时，我们只需将我们的 `BaseActivity` 改为实现它就行了。
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180930215103733.png)


该库允许基于另一个生命周期流`[a second lifecycle stream]`自动完成序列`[automatically complete sequences]`。  
此功能在Android中很有用，因为在Android中，不完整的订阅`[incomplete subscriptions]`可能导致内存泄漏。


## 注意
注意：【RxLifecycle 的原理是不是基于自动调用 unsubscribe 的】   


RxLifecycle实际上并没有取消订阅`[unsubscribe]`序列。 相反，它终止`[terminates]`了序列。 它的表现方式因类型而异：
- Observable、Flowable 和 Maybe：发出 onCompleted()
- Single 和 Completable：发出onError(CancellationException)


如果序列需要 Subscription.unsubscribe() 行为，则建议您自己手动处理订阅并在适当时调用unsubscribe()。


# 使用方式 compose
您必须以表示生命周期流的 `Observable <T>` 开头。 然后使用 `RxLifecycle` 将序列绑定到该生命周期。  


您可以在生命周期发出任何内容时进行绑定：
```java
myObservable
    .compose(RxLifecycle.bind(lifecycle))
    .subscribe(...);
 ```


或者您可以在特定生命周期事件发生时绑定：
```java
myObservable
    .compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.DESTROY))
    .subscribe(...);
 ```
 
或者，您可以让 RxLifecycle 确定结束序列的适当时间：
```java
myObservable
    .compose(RxLifecycleAndroid.bindActivity(lifecycle))
    .subscribe(...);
 ```
它假定您想要在相反的`[opposing]`生命周期事件中结束序列 - 例如，如果在 START 期间订阅，它将在 STOP 时终止。如果您在 PAUSE 之后订阅，它将在下一个销毁事件`[destruction event]`中终止，例如，PAUSE 将在 STOP 中终止。


# RxLifecycle
生命周期来自哪里？ 通常，它们由适当的 `LifecycleProvider <T>` 提供。 但他们的实现`[implemented]`在哪里？


你有几个选择：
- 使用 rxlifecycle-components 中提供的 RxActivity，RxFragment 等类的子类
```java
public class MyActivity extends RxActivity {
    @Override
    public void onResume() {
        super.onResume();
        myObservable
            .compose(bindToLifecycle()) //使用内置的 bindToLifecycle() 或 bindUntilEvent() 方法即可
            .subscribe(...);
    }
}
```


- 使用 Navi + rxlifecycle-navi 生成：
```java
public class MyActivity extends NaviActivity {
    private final LifecycleProvider<ActivityEvent> provider = NaviLifecycle.createActivityLifecycleProvider(this);
    @Override
    public void onResume() {
        super.onResume();
        myObservable
            .compose(provider.bindToLifecycle())
            .subscribe();
    }
}
```


- 使用 Android 的 lifecycle + rxlifecycle-android-lifecycle 生成
```java
public class MyActivity extends LifecycleActivity {
    private final LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(this);
    @Override
    public void onResume() {
        super.onResume();
        myObservable
            .compose(provider.bindToLifecycle())
            .subscribe();
    }
}
```


- 自己编写实现


# 案例
```java
public class RxBindingActivity extends RxFragmentActivity {
    private Button btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxbinding);
        
        btn = findViewById(R.id.btn);
        RxView.clicks(btn) //点击获取验证码
                .doOnNext(o -> btn.setEnabled(false))
                .subscribe(o -> Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                        .take(10)
                        .compose(bindToLifecycle()) //绑定生命周期
                        .subscribe(aLong -> btn.setText(10 - aLong + " 秒"),
                                Throwable::printStackTrace,
                                () -> {
                                    btn.setEnabled(true);
                                    btn.setText("重新获取");
                                }));        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        RxView.clicks(btn)
                .buffer(1000, TimeUnit.MILLISECONDS, 5) //效果仅仅是，每隔1秒钟收集一下此1秒钟内的点击次数
                .compose(bindUntilEvent(ActivityEvent.STOP))//在 onStop 时取消
                .subscribe(list -> Log.i("【bqt】", "一秒钟内的点击次数：" + list.size()));
    }
}
```


2018-9-15