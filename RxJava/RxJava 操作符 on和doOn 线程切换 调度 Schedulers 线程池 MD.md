| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RxJava 操作符 on和doOn 线程切换 调度 Schedulers 线程池 MD  
***  
目录  
===  

- [RxJava 线程池](#RxJava 线程池)
- [正常的流程](#正常的流程)
- [切换线程对 on** 方法的影响](#切换线程对 on** 方法的影响)
	- [指定被观察者发布事件的线程](#指定被观察者发布事件的线程)
	- [指定订阅者(观察者)接收事件的线程](#指定订阅者(观察者)接收事件的线程)
	- [线程切换](#线程切换)
- [切换线程对 doOn** 的影响](#切换线程对 doOn** 的影响)
	- [切换线程的操作放在最上面](#切换线程的操作放在最上面)
	- [向下移动1下](#向下移动1下)
	- [向下移动2下](#向下移动2下)
	- [向下移动3下](#向下移动3下)
	- [别人总结的规律](#别人总结的规律)
  
# RxJava 线程池  
[RxJava 线程池](https://www.jianshu.com/p/73c207844bb4)    
  
RxJava中的多线程操作主要是由强大的`Scheduler`集合提供的。在RxJava中，我们无法直接访问或操作线程。如果想要使用线程的话，必须要通过内置的Scheduler来实现。如果你需要在特定的线程中执行任务的话，我们就需要此选择恰当的Scheduler，Scheduler接下来会从它的池中获取一个可用的线程，并基于该线程执行任务。  
  
在RxJava框架中有多种类型的Scheduler，但是这里比较有技巧的一点就是`为合适的工作选择恰当的Scheduler`。如果你没有选择恰当的Scheduler的话，那么任务就无法最优地运行，所以接下来，我们尝试理解每一个Scheduler。  
  
- Schedulers.io()     
    > 这是由`无边界线程池`作为支撑的一个Scheduler，它适用于`非CPU密集的I/O工作`，比如`访问文件系统、执行网络调用、访问数据库`等等。    
    这个Scheduler是没有限制的，它的线程池可以按需一直增长。    
    注意：在使用无边界线程池支撑的Scheduler时，我们要特别小心，因为它有可能会导致线程池无限增长，使系统中出现大量的线程。    
- Schedulers.computation()  
    > 这个Scheduler用于执行`CPU密集`的工作，比如`处理大规模的数据集、图像处理`等等。它由一个`有界的线程池`作为支撑，线程的最大数量就是`可用的处理器数量`。    
    因为这个Scheduler只适用于CPU密集的任务，我们希望限制线程的数量，这样的话，它们不会彼此抢占CPU时间或出现`线程饿死`的现象。  
- Schedulers.newThread()  
    > 这个Scheduler 每次都会创建一个`全新的线程`来完成一组工作。它不会从任何线程池中受益，`线程的创建和销毁都是很昂贵的`，所以你需要非常小心，不要衍生出太多的线程，导致服务器系统变慢或出现内存溢出的错误。    
    理想情况下，你应该很少使用这个Scheduler，它大多用于在一个完全分离的线程中开始一项长时间运行、隔离的一组任务。  
- Schedulers.single()  
    > 这个Scheduler是RxJava 2新引入的，它的背后只有一个线程作为支撑，只能按照有序的方式执行任务。如果你有一组后台任务要在App的不同地方执行，但是同时只能承受一个任务执行的话，那么这个Scheduler就可以派上用场了。  
- Schedulers.from(Executor executor)  
    > 我们可以使用它创建自定义的Scheduler，它是由我们自己的`Executor`作为支撑的。在有些场景下，我们希望创建自定义的Scheduler为App执行特定的任务，这些任务可能需要自定义的线程逻辑。    
    假设，我们想要限制App中并行网络请求的数量，那么我们就可以创建一个自定义的Scheduler，使其具有一个固定线程池大小的Executor：`Scheduler.from(Executors.newFixedThreadPool(n))`，然后将其应用到代码中所有网络相关的Observable上。  
- AndroidSchedulers.mainThread()  
    > 这是一个特殊的Scheduler，它无法在核心RxJava库中使用，要使用它，必须要借助`RxAndroid`扩展库。这个Scheduler对Android App特别有用，它能够在应用的主线程中执行基于UI的任务。    
    默认情况下，它会在应用`主线程关联的looper`中进行任务排队，但是它有一个其他的`变种`，允许我们以API的形式使用任意的Looper：`AndroidSchedulers.from(Looper looper)`。  
  
# 正常的流程  
正常的流程为：  
> doOnSubscribe、onSubscribe、【create】、doOnNext、onNext、doOnComplete、onComplete  
  
例如最基础的形式：  
```java  
Observable.create(emitter -> {  
    log("【开始create】");  
    emitter.onNext("救命啊");  
    emitter.onComplete();  
    log("【结束create】");  
})  
        .doOnNext(s -> log("【doOnNext】"))  
        .doOnError(e -> log("【doOnError】"))  
        .doOnComplete(() -> log("【doOnComplete】"))  
        .doOnSubscribe(disposable -> log("【doOnSubscribe】"))  
        .subscribe(o -> log("【onNext】"), e -> log("【onError】"), () -> log("【onComplete】"), d -> log("【onSubscribe】"));  
```  
  
打印方法为：  
```java  
private void log(String s) {  
    String date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault()).format(new Date());  
    Log.i("bqt", s + date + "，" + (Looper.myLooper() == Looper.getMainLooper()));  
}  
```  
  
打印结果为：  
```  
【doOnSubscribe】2018.09.15 13:38:01 163，true  
【onSubscribe】2018.09.15 13:38:01 165，true  
  
【开始create】2018.09.15 13:38:01 166，true  
  
【doOnNext】2018.09.15 13:38:01 167，true  
【onNext】2018.09.15 13:38:01 168，true  
  
【doOnComplete】2018.09.15 13:38:01 171，true  
【onComplete】2018.09.15 13:38:01 172，true  
  
【结束create】2018.09.15 13:38:01 173，true  
```  
  
将 Observable 换成下面的几种形式后，效果都和上面基本是一样的：  
```java  
Observable.just("救命啊") //相应的只是没有打印【开始create】和【结束create】  
```  
```java  
Observable.create(emitter -> {  
    log("【开始create】");  
    emitter.onNext("救命啊"); //相应的只是没有【doOnComplete】和【onComplete】  
    log("【结束create】");  
})  
```  
```java  
Observable.create(emitter -> {  
    log("【开始create】");  
    emitter.onNext("救命啊");  
    emitter.onNext("HELP");  //相应的有两组 **Next，顺序为【doOnNext】【onNext】【doOnNext】【onNext】  
    emitter.onComplete();  
    log("【结束create】");  
})  
```  
```java  
Observable.create(emitter -> {  
    log("【开始create】");  
    emitter.onNext("救命啊");  
    emitter.onError(new RuntimeException("game over")); //相应的是【doOnError】和【onError】  
    log("【结束create】");  
})  
```  
  
# 切换线程对 on** 方法的影响  
通过上面的打印结果，我们可以知道，如果没有添加切换线程的操作，则以上所有方法默认都执行在主线程中。  
  
先说一个极端情况，如果我们整个在一个子线程中执行以上逻辑，例如：  
```java  
new Thread(() -> ... ).start(); //子线程  
```  
则以上所有方法都是在子线程中执行的。  
  
下面我们考虑切换线程对【on**】方法的影响。  
  
## 指定被观察者发布事件的线程  
```java  
Observable.create(...)  
    .subscribeOn(Schedulers.io()) //指定被观察者发布事件的线程：  
    //.observeOn(Schedulers.io()) //如果订阅者接收事件的线程和被观察者发布事件的线程相同，则可以不必指定接收事件的线程  
    .subscribe(...);  
```  
  
打印结果为：  
```  
【onSubscribe】2018.09.15 14:36:44 010，true  
【开始create】2018.09.15 14:36:44 011，false  
【onNext】2018.09.15 14:36:44 012，false  
【onComplete】2018.09.15 14:36:44 015，false  
【结束create】2018.09.15 14:36:44 015，false  
```  
  
这种情况下，其线程效果等价于：  
```java  
Observable.create(emitter -> new Thread(() -> {...}).start()) //将 create 中的所有操作放到了子线程  
    .subscribe(...);  
```  
  
## 指定订阅者(观察者)接收事件的线程  
```java  
Observable.create(...)  
    //.subscribeOn(AndroidSchedulers.mainThread()) //因为默认就在主线程，所以可以不指定  
    .observeOn(Schedulers.io()) //指定订阅者(观察者)接收事件的线程  
    .subscribe(...);  
```  
  
打印结果为：  
```  
【onSubscribe】2018.09.15 14:25:58 682，true  
【开始create】2018.09.15 14:25:58 697，true  
【结束create】2018.09.15 14:25:58 699，true //此案例中，由于【create】和后面几个回调不在一个线程中，因为存在线程竞争的情况，所以【结束create】的回调时机是不确定的，有可能在【onNext】前也可能在【onNext】后，甚至有可能在【onComplete】之后  
【onNext】2018.09.15 14:25:58 700，false  
【onComplete】2018.09.15 14:25:58 701，false  
```  
  
这种情况下，其线程效果等价于：  
```java  
Observable.create(emitter -> {  
    log("【开始create】"); //主线程  
    new Thread(() -> {  
        emitter.onNext("救命啊"); //子线程  
        emitter.onComplete(); //子线程  
    }).start();  
    log("【结束create】"); //主线程  
})  
    .subscribe(...);  
```  
  
## 线程切换  
在被观察者发布事件时将线程切换到一个线程，在订阅者接收事件时将线程切换到另一个线程  
```java  
Observable.create(...)  
    .subscribeOn(Schedulers.io()) //指定被观察者发布事件的线程：  
    .observeOn(AndroidSchedulers.mainThread()) //指定订阅者(观察者)接收事件的线程  
    .subscribe(...);  
```  
  
打印结果为：  
```  
【onSubscribe】2018.09.15 14:12:11 711，true  
【开始create】2018.09.15 14:12:11 713，false  
【结束create】2018.09.15 14:12:11 714，false //此案例中，由于【create】和后面几个回调不在一个线程中，因为存在线程竞争的情况，所以【结束create】的回调时机是不确定的，有可能在【onNext】前也可能在【onNext】后，甚至有可能在【onComplete】之后  
【onNext】2018.09.15 14:12:11 723，true  
【onComplete】2018.09.15 14:12:11 724，true  
```  
  
这种情况下，其线程效果等价于：  
```java  
Observable.create(emitter -> new Thread(() -> {  
    log("【开始create】"); //子线程  
    runOnUiThread(() -> {  
        emitter.onNext("救命啊"); //主线程  
        emitter.onComplete(); //主线程  
    });  
    log("【结束create】"); //子线程  
}).start())  
    .subscribe(...);  
```  
  
# 切换线程对 doOn** 的影响  
> 注意：下面这些没必要去记，也根本不可能记住，只需要知道，这几个 doOn 方法的执行时机和对应的 on 方法相比并不稳定，且这几个 doOn 方法执行时所在线程也并不稳定即可。  
  
## 切换线程的操作放在最上面  
```java  
Observable.create(...)  
    .subscribeOn(Schedulers.io())//指定 subscribe 时所发生的线程，发射事件的线程  
    .observeOn(AndroidSchedulers.mainThread()) //指定下游 Observer 回调发生的线程，订阅者接收事件的线程  
    .doOnNext(s -> Log.i("bqt", "【doOnNext】" + currentData() + "，" + isMainThread()))  
    .doOnComplete(() -> Log.i("bqt", "【doOnComplete】 " + currentData() + "，" + isMainThread()))  
    .doOnSubscribe(disposable -> Log.i("bqt", "【doOnSubscribe】" + currentData() + "，" + isMainThread()))  
    .subscribe(...);  
```  
  
打印结果为：  
```  
【doOnSubscribe】2018.08.26 18:05:23 799，true  
【onSubscribe】2018.08.26 18:05:23 800，true //【没有改变过】  
​  
【create】2018.08.26 18:05:23 803，false //【没有改变过】  
​  
【doOnNext】2018.08.26 18:05:23 806，true  
【onNext】2018.08.26 18:05:23 807，true //【没有改变过】  
​  
【doOnComplete】 2018.08.26 18:05:23 807，true  
【onComplete】2018.08.26 18:05:23 807，true //【没有改变过】  
```  
  
## 向下移动1下  
```java  
Observable.create(...)  
    .doOnNext(s -> Log.i("bqt", "【doOnNext】" + currentData() + "，" + isMainThread()))  
    .subscribeOn(Schedulers.io())//指定 subscribe 时所发生的线程，发射事件的线程  
    .observeOn(AndroidSchedulers.mainThread()) //指定下游 Observer 回调发生的线程，订阅者接收事件的线程  
    .doOnComplete(() -> Log.i("bqt", "【doOnComplete】 " + currentData() + "，" + isMainThread()))  
    .doOnSubscribe(disposable -> Log.i("bqt", "【doOnSubscribe】" + currentData() + "，" + isMainThread()))  
    .subscribe(...);  
```  
  
打印结果为：  
```  
【doOnSubscribe】2018.08.26 18:50:32 149，true  
【onSubscribe】2018.08.26 18:50:32 150，true //【没有改变过】  
​  
【create】2018.08.26 18:50:32 158，false //【没有改变过】  
​  
【doOnNext】2018.08.26 18:50:32 158，false  
【onNext】2018.08.26 18:50:32 172，true //【没有改变过】  
​  
【doOnComplete】 2018.08.26 18:50:32 172，true  
【onComplete】2018.08.26 18:50:32 172，true //【没有改变过】  
```  
  
## 向下移动2下  
```java  
Observable.create(...)  
    .doOnNext(s -> Log.i("bqt", "【doOnNext】" + currentData() + "，" + isMainThread()))  
    .doOnComplete(() -> Log.i("bqt", "【doOnComplete】 " + currentData() + "，" + isMainThread()))  
    .subscribeOn(Schedulers.io())//指定 subscribe 时所发生的线程，发射事件的线程  
    .observeOn(AndroidSchedulers.mainThread()) //指定下游 Observer 回调发生的线程，订阅者接收事件的线程  
    .doOnSubscribe(disposable -> Log.i("bqt", "【doOnSubscribe】" + currentData() + "，" + isMainThread()))  
    .subscribe(...);  
```  
  
打印结果为：  
```  
【doOnSubscribe】2018.08.26 18:58:01 329，true  
【onSubscribe】2018.08.26 18:58:01 349，true //【没有改变过】  
​  
【create】2018.08.26 18:58:01 359，false //【没有改变过】  
​  
【doOnNext】2018.08.26 18:58:01 359，false  
【doOnComplete】 2018.08.26 18:58:01 360，false //注意顺序已经和之前的不一样了  
【onNext】2018.08.26 18:58:01 371，true //【没有改变过】  
【onComplete】2018.08.26 18:58:01 372，true //【没有改变过】  
```  
  
## 向下移动3下  
```java  
Observable.create(...)  
    .doOnNext(s -> Log.i("bqt", "【doOnNext】" + currentData() + "，" + isMainThread()))  
    .doOnComplete(() -> Log.i("bqt", "【doOnComplete】 " + currentData() + "，" + isMainThread()))  
    .doOnSubscribe(disposable -> Log.i("bqt", "【doOnSubscribe】" + currentData() + "，" + isMainThread()))  
    .subscribeOn(Schedulers.io())//指定 subscribe 时所发生的线程，发射事件的线程  
    .observeOn(AndroidSchedulers.mainThread()) //指定下游 Observer 回调发生的线程，订阅者接收事件的线程  
    .subscribe(...);  
```  
  
打印结果为：  
```  
【onSubscribe】2018.08.26 19:01:51 196，true //【没有改变过】  
【doOnSubscribe】2018.08.26 19:01:51 200，false//注意顺序已经和之前的不一样了  
​  
【create】2018.08.26 19:01:51 201，false //【没有改变过】  
​  
【doOnNext】2018.08.26 19:01:51 201，false  
【doOnComplete】 2018.08.26 19:01:51 201，false//注意顺序已经和之前的不一样了  
【onNext】2018.08.26 19:01:51 227，true //【没有改变过】  
【onComplete】2018.08.26 19:01:51 228，true //【没有改变过】  
```  
  
## 别人总结的规律  
   - doOnSubscribe()与onStart()相似，均在代码调用时就会回调。但doOnSubscribe()能够通过subscribeOn()操作符改变运行的线程且越在后面运行越早；  
   - doOnSubscribe()后面紧跟subscribeOn()，那么doOnSubscribe()将于subscribeOn()指定的线程保持一致。假设doOnSubscribe()在subscribeOn()之后，他的运行线程得再看情况分析；  
   - doOnSubscribe()假设在observeOn()后（注意：observeon()后没有紧接着再调用subcribeon()方法）。那么doOnSubscribe的运行线程就是main线程，与observeon()指定的线程没有关系。  
   - 假设在observeOn()之前没有调用过subcribeOn()方法，observeOn()之后subscribe面()方法之前调用subcribeOn()方法，那么他会改变整个代码流程中全部调用doOnSubscribe()方法所在的线程。同一时候也会改变observeOn()方法之前全部操作符所在的线程（有个重要前提：不满足第2点的条件，也就是doOnSubscribe()后面没有调用subscribeOn()方法）。  
   - 假设在observeOn()前后都没有调用过subcribeOn()方法，那么整个代码流程中的doOnSubscribe()运行在main线程，与observeOn()指定的线程无关。同一时候observeOn()之前的操作符也将运行在main线程，observeOn()之后的操作符与observeOn()指定的线程保持一致。  
  
> 再次强调：上面这些没必要去记，也根本不可能记住，只需要知道，这几个 doOn** 方法的执行时机和对应的 on** 方法相比并不稳定，且这几个 doOn** 方法执行时所在线程也并不稳定即可。  
  
2018-9-15  
