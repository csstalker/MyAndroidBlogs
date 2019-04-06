| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RxJava【过滤】操作符 filter distinct throttle take skip first MD    
[demo地址](https://github.com/baiqiantao/RxJavaDemo.git)    
[参考](https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Filtering-Observables.html)   
***   
目录  
===  

- [常用的过滤操作符](#常用的过滤操作符)
	- [filter ofType](#filter-ofType)
	- [distinct distinctUntilChanged](#distinct-distinctUntilChanged)
	- [ignoreElements](#ignoreElements)
	- [throttle sample](#throttle-sample)
		- [throttleFirst](#throttleFirst)
		- [throttleLast](#throttleLast)
		- [sample](#sample)
		- [throttleLatest](#throttleLatest)
	- [debounce throttleWithTimeout](#debounce-throttleWithTimeout)
	- [timeout](#timeout)
		- [timeout(long,TimeUnit)](#timeoutlongTimeUnit)
		- [timeout(long,TimeUnit,Observable)](#timeoutlongTimeUnitObservable)
		- [timeout(Function)](#timeoutFunction)
	- [take takelast takeUntil takeWhile](#take-takelast-takeUntil-takeWhile)
		- [take](#take)
		- [takeLast](#takeLast)
		- [takeUntil takeWhile](#takeUntil-takeWhile)
	- [skip skipLast skipUntil skipWhile](#skip-skipLast-skipUntil-skipWhile)
	- [获取指定位置的元素](#获取指定位置的元素)
		- [first*](#first)
		- [last*](#last)
		- [elementAt elementAtOrError](#elementAt-elementAtOrError)
	- [single*](#single)
  
# 常用的过滤操作符   
根据指定【条件】过滤事件   
- filter：过滤掉没有通过谓词测试的数据项，只发射通过测试的   
- ofType：仅发出指定类型的项目   
- distinct：过滤掉重复数据项   
- distinctUntilChanged：过滤掉连续重复的数据   
- ignoreElements：丢弃所有的正常数据，只发射 onError 或 onCompleted 通知   
- single、singleElement、singleOrError：如果只发射一个数据则发射这个数据，否则发射默认数据或异常   
  
根据指定【时间】过滤事件   
- throttleFirst：定期发射Observable发射的第一项数据   
- throttleLast、sample：定期发射Observable最近的数据，等于是数据抽样   
- throttleLatest：定期发射Observable最近的数据，类似于 throttleFirst + throttleLast   
- debounce、throttleWithTimeout：去抖动，只有当Observable在指定的时间后还没有发射数据时，才发射一个数据   
- timeout：如果在一个指定的时间段后还没发射数据，就发射一个异常   
  
根据【位置】保留或跳过事件   
- take、takelast：只保留前面(后面)的N项数据   
- takeUntil、takeWhile：当满足指定的条件时开始发射或停止发射   
- skip、skipLast：跳过前面(后面)的N项数据   
- skipUntil、skipWhile：当满足指定的条件时开始发射或停止发射   
  
获取【指定位置】的事件   
- first、firstElement、firstOrError、last、lastElement、lastOrError：只发射第一项(最后一项)数据   
- elementAt、elementAtOrError：只发发射第N项数据   
  
## filter ofType   
filter：过滤掉没有通过谓词测试的数据项，只发射通过测试的数据项   
```Java   
Observable.range(1, 10)  
    .filter(i -> i % 3 == 0)  
    .subscribe(i -> log("" + i));//3,6,9   
```   
  
ofType：仅发出指定类型的项目   
```Java   
Observable.just(1, true, "包青天", 2, "哈哈")   
    .ofType(String.class)   
    .subscribe(i -> log("" + i));//包青天，哈哈   
```   
  
## distinct distinctUntilChanged   
过滤掉重复数据项   
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180930115333134.png)  
  
` distinct` ：过滤掉重复数据项(只允许还没有发射过的数据项通过)   
```Java   
Observable.just(1, 2, 2, 3, 4, 3, 5)   
    .distinct()   
    .subscribe(i -> log("" + i)); //1, 2, 3, 4, 5   
```   
  
可以指定一个函数，这个函数根据原始Observable发射的数据项产生一个Key，然后，比较这些Key而不是数据本身，来判定两个数据是否是不同的。   
```Java   
Observable.just("a", "bcd", "e", "fg", "h")   
      .distinct(String::length) //通过比较函数的返回值而不是数据本身来判定两个数据是否是不同的   
      .subscribe(i -> log("" + i)); //a，bcd，fg   
```   
  
` distinctUntilChanged `：过滤掉连续重复的数据，同样可以指定一个判断是否是相同数据的函数   
```Java   
Observable.just(1, 2, 2, 3, 4, 3, 5)   
    .distinctUntilChanged()   
    .subscribe(i -> log("" + i)); //1, 2, 3, 4, 3, 5   
```   
  
## ignoreElements   
丢弃所有的正常数据，只发射 onError 或 onCompleted 通知   
```Java   
Observable.create(emitter -> {   
   emitter.onNext(1);   
   emitter.onError(new Throwable("异常"));   
}).ignoreElements()   
      .subscribe(() -> log("onComplete"), e -> log(e.getMessage())); // 异常   
```   
  
## throttle sample   
> throttle  [ˈθrɑ:tl]  n.节流阀; 喉咙，气管; [机] 风门;  vt.扼杀，压制;勒死，使窒息;使节流;调节;   vi.节流，减速; 窒息;   
  
在某段时间内，只发送该段时间内第1次事件 / 最后1次事件   
> 注意：如果自上次采样以来，原始Observable没有发射任何数据，则返回的Observable在那段时间内也不会发射任何数据。   
  
### throttleFirst   
在每个采样周期内，throttleFirst 总是只发射原始Observable的第一项数据。周期内可能没有发射任何数据。   
```Java   
RxView.clicks(v)   
      .map(o -> format.format(new Date()))   
      .doOnEach(notification -> log("点击事件的时间为：" + notification.getValue()))   
      .throttleFirst(1, TimeUnit.SECONDS)   
      .subscribe(s -> log("防止按钮重复点击，值为：" + s));   
```   
```   
点击事件的时间为：22:31:08 304   
防止按钮重复点击，值为：22:31:08 304   
点击事件的时间为：22:31:08 516   
点击事件的时间为：22:31:08 764   
点击事件的时间为：22:31:09 247   
点击事件的时间为：22:31:09 590   
防止按钮重复点击，值为：22:31:09 590   
```   
  
### throttleLast   
定期发射Observable最近的数据，等于是定时对数据进行抽样。   
sample 和 throttleLast 操作符定时查看一个Observable，然后发射自上次采样以来它最近发射的数据。   
```Java   
RxView.clicks(v)   
      .map(o -> format.format(new Date()))   
      .doOnEach(notification -> log("点击事件的时间为：" + notification.getValue()))   
      .throttleLast(1, TimeUnit.SECONDS)   
      .subscribe(s -> log("定时对数据进行抽样，值为：" + s));   
```   
```   
点击事件的时间为：22:33:03 809   
点击事件的时间为：22:33:04 371   
点击事件的时间为：22:33:04 552   
定时对数据进行抽样，值为：22:33:04 552   
点击事件的时间为：22:33:04 943   
点击事件的时间为：22:33:05 463   
定时对数据进行抽样，值为：22:33:05 463   
```   
  
### sample   
相同参数的 sample 的效果和 throttleLast 完全一致：   
```Java   
public final Observable<T> throttleLast(long intervalDuration, TimeUnit unit) {   
    return sample(intervalDuration, unit);   
}   
```   
不过 sample 有一个带 boolean 类型参数的变体   
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180930115421539.png)   
参数 emitLast：   
- 如果为true且上游完成[upstream completes]且仍有未采样的项目可用，则该项目在完成之前发送到下游。   
- 如果为false，则忽略未采样的最后一项。   
  
### throttleLatest   
感觉就是 throttleFirst + throttleLast 的组合效果，即：第一个数据项立即发射出去，后续则只发射周期内最后一个数据项。   
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180930115442260.png)   
  
和 sample 一样，其也有一个带 boolean 类型参数的变体   
```Java   
RxView.clicks(v)   
      .map(o -> format.format(new Date()))   
      .doOnEach(notification -> log("点击事件的时间为：" + notification.getValue()))   
      .throttleLatest(1, TimeUnit.SECONDS)   
      .subscribe(s -> log("获取最接近采样点的值，值为：" + s));   
```   
```   
点击事件的时间为：22:37:36 202   
获取最接近采样点的值，值为：22:37:36 202   
点击事件的时间为：22:37:36 526   
点击事件的时间为：22:37:36 853   
点击事件的时间为：22:37:37 204   
获取最接近采样点的值，值为：22:37:37 204   
点击事件的时间为：22:37:37 540   
点击事件的时间为：22:37:38 045   
获取最接近采样点的值，值为：22:37:38 045   
```   
  
## debounce throttleWithTimeout   
仅在过了一段指定的时间还没发射数据时才发射一个数据，Debounce操作符会过滤掉发射速率过快的数据项。   
去抖动，只有当Observable在指定的时间后还没有发射数据时，才发射一个数据   
  
> 注意：这个操作符会会接着最后一项数据发射原始Observable的onCompleted通知，即使这个通知发生在你指定的时间窗口内(从最后一项数据的发射算起)。也就是说，onCompleted通知不会触发限流。   
  
debounce操作符的一个变体通过对原始Observable的每一项应用一个函数进行限流，这个函数返回一个Observable。如果原始Observable在这个新生成的Observable终止之前发射了另一个数据，debounce会抑制(suppress)这个数据项。   
  
```Java   
RxTextView.textChanges(et)   
      .doOnEach(notification -> log("值为：" + notification.getValue()))   
      .debounce(2000, TimeUnit.MILLISECONDS) //防抖动，去除发送频率过快的项   
      .subscribe(s -> log("2秒钟内没有发生变化才发送，值为：" + s), e -> log("异常"), () -> log("完成"));   
```   
```   
18:57:20.799: 值为：1   
18:57:21.539: 值为：12   
18:57:22.413: 值为：123   
18:57:23.245: 值为：1234   
18:57:25.179: 值为：12345   
18:57:26.916: 值为：123456   
18:57:28.918: 2秒钟内没有发生变化才发送，值为：123456   
18:57:30.205: 值为：1234567   
18:57:32.206: 2秒钟内没有发生变化才发送，值为：1234567   
```   
  
## timeout   
如果原始Observable过了指定的一段时长没有发射任何数据，Timeout操作符会以一个onError通知终止这个Observable。   
```Java   
timeout(Function itemTimeoutIndicator)   
timeout(Function itemTimeoutIndicator, ObservableSource other)   
timeout(long timeout, TimeUnit timeUnit)   
timeout(long timeout, TimeUnit timeUnit, ObservableSource other)   
timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler)   
timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler, ObservableSource other)   
timeout(ObservableSource firstTimeoutIndicator, Function itemTimeoutIndicator)   
timeout(ObservableSource firstTimeoutIndicator, Function itemTimeoutIndicator, ObservableSource other)   
```   
  
以下案例均用到了如下代码：   
```Java   
Observable<Integer> observable = Observable.create(emitter -> {   
   emitter.onNext(1);   
   SystemClock.sleep(150L + 100L * new Random().nextInt(2));   
   emitter.onNext(2);   
   if (new Random().nextBoolean()) emitter.onComplete();   
});   
```   
  
### timeout(long,TimeUnit)   
每当原始Observable发射了一项数据，timeout就启动一个计时器，如果计时器超过了指定指定的时长而原始Observable没有发射另一项数据，timeout就抛出TimeoutException，以一个错误通知终止Observable。   
```Java   
observable.timeout(200, TimeUnit.MILLISECONDS)   
      .subscribe(i -> log("" + i), e -> log("超时了:" + e.getClass().getSimpleName()), () -> log("完成"));   
```   
可能的结果有：   
> 【1，2，完成】【1，2，超时了:TimeoutException】【1，超时了:TimeoutException】   
  
### timeout(long,TimeUnit,Observable)   
可以在超时时切换到一个指定的Observable，而不是发错误通知。   
```Java   
timeout(long timeout, TimeUnit timeUnit, ObservableSource other)   
```   
  
案例：   
```Java   
observable.timeout(200, TimeUnit.MILLISECONDS, observer -> observer.onNext(3))   
      .subscribe(i -> log("" + i), e -> log("超时了:" + e.getClass().getSimpleName()), () -> log("完成"));   
```   
可能的结果有：   
> 【1，2，完成】【1，2，3】【1，3】   
  
### timeout(Function)   
可以使用一个函数针对原始Observable的每一项返回一个新的Observable，如果当这个新的Observable终止时原始Observable还没有发射另一项数据，就会认为是超时了，timeout就抛出TimeoutException，以一个错误通知终止Observable。    
  
还可以同时指定超时时切换到的Observable，还可以单独给第一项设置一个超时时切换到的Observable。   
```Java   
timeout(Function itemTimeoutIndicator)   
timeout(Function itemTimeoutIndicator, ObservableSource other)   
timeout(ObservableSource firstTimeoutIndicator, Function itemTimeoutIndicator)   
timeout(ObservableSource firstTimeoutIndicator, Function itemTimeoutIndicator, ObservableSource other)   
```   
案例：   
```Java   
observable.timeout(i -> Observable.just("这里并不会发射数据给订阅者" + i).delay(200, TimeUnit.MILLISECONDS))   
      .subscribe(i -> log("" + i), e -> log("超时了:" + e.getClass().getSimpleName()), () -> log("完成"));   
```   
可能的结果有：   
> 【1，2，完成】【1，2，超时了:TimeoutException】【1，超时了:TimeoutException】   
  
## take takelast takeUntil takeWhile   
只发射前面或后面的N项数据，或当满足指定的条件时开始发射或停止发射   
  
### take   
只发射前面的N项数据   
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180930115527230.png)   
  
使用Take操作符让你可以修改Observable的行为，只返回前面的N项数据，然后发射完成通知，忽略剩余的数据。    
如果 Observable 发射的数据少于 N 项，那么take操作生成的Observable不会抛异常或发射onError通知，在完成前它只会发射相同的少量数据。   
  
还可以指定一个时长，它会只发射在原始Observable的最开始一段时间内发射的数据。   
```Java   
Observable.range(1, 5)   
      .take(2)   
      .subscribe(i -> log("" + i), e -> log("异常"), () -> log("完成")); //1, 2,完成   
Observable.range(11, 1)   
      .take(2)   
      .subscribe(i -> log("" + i), e -> log("异常"), () -> log("完成")); //11,完成   
Observable.create(emitter -> {   
   emitter.onNext(21);   
   emitter.onNext(22);   
   SystemClock.sleep(150L + 100L * new Random().nextInt(2));   
   emitter.onNext(23);   
   emitter.onComplete();   
}).take(200, TimeUnit.MILLISECONDS)   
      .subscribe(i -> log("" + i), e -> log("异常"), () -> log("完成")); //21, 22,完成 或 21, 22,23,完成   
```   
  
### takeLast   
发射Observable发射的最后N项数据    
使用TakeLast操作符修改原始Observable，你可以只发射Observable发射的后N项数据，忽略前面的数据。   
> 注意：takeLast 操作符会延迟原始 Observable 发射的任何数据项，直到它全部完成。   
  
上述案例在使用 takeLast 操作符时的打印结果为【4,5,完成】【11,完成】【23,完成  或  21, 22,23,完成】   
  
takeLast 还可以添加一些额外的参数，如可以同时指定发射的元素的最大个数以及时长等：   
```Java   
takeLast(long count, long time, TimeUnit unit, Scheduler scheduler, boolean delayError, int bufferSize)   
```   
- long count：the maximum number of items to emit   
- boolean delayError：如果为true，则由当前Observable发出的 exception 通知会被延迟，直到常规元素[regular elements]被下游[downstream]消耗为止；如果为false，则立即发射异常通知，并且所有常规元素会被丢弃[dropped]。不指定时都是用的 false 这种情况。   
- int bufferSize：the hint about how many elements to expect to be last。不指定时，默认值为【Math.max(1, Integer.getInteger("rx2.buffer-size", 128))】   
  
### takeUntil takeWhile   
takeUntil 有两个重载方法：   
- 返回一个Observable，它发出源Observable发出的项，其会检查每个项的指定谓词[predicate]，如果条件满足则完成。   
- 返回一个Observable，它发出源Observable发出的项，直到指定的Observable发出一个项为止。   
```Java   
takeUntil(ObservableSource other) //Returns an Observable that emits the items emitted by the source Observable until a second ObservableSource emits an item.   
takeUntil(Predicate stopPredicate) //Returns an Observable that emits items emitted by the source Observable, checks the specified predicate for each item, and then completes when the condition is satisfied.   
```   
  
案例：   
```Java   
Observable.range(10, 5)   
      .flatMap(Observable::just)   
      .takeUntil(l -> l >= 12) //当满足条件时原始的Observable会停止发射   
      .subscribe(i -> log("" + i), e -> log("异常1"), () -> log("完成1")); //10, 11, 12, 完成   
Observable.interval(200, TimeUnit.MILLISECONDS)   
      .takeUntil(Observable.just("开始发射数据时原始的Observable会停止发射").delay(500, TimeUnit.MILLISECONDS))   
      .subscribe(i -> log("" + i), e -> log("异常2"), () -> log("完成2")); //0, 1,完成   
```   
  
takeWhile 仅有一个带 Predicate 参数的方法，其是当不满足条件时原始的Observable停止发射，例如同样的案例：   
```Java   
Observable.range(20, 5)   
      .flatMap(Observable::just)   
      .takeWhile(l -> l <= 22) //当不满足条件时原始的Observable会停止发射   
      .subscribe(i -> log("" + i), e -> log("异常3"), () -> log("完成3")); //20, 21, 22, 完成   
```   
  
## skip skipLast skipUntil skipWhile   
跳过前面(后面)的N项数据，基本上和 take 的结构一致   
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180930115639119.png)   
  
> 注意：skipLast 的机制是这样实现的：延迟原始Observable发射的任何数据项，直到它发射了N项数据(或直到自这次发射之后过了给定的时长)。   
  
案例：   
```Java   
Observable.range(0, 5).skip(2).subscribe(i -> log("" + i)); //2.3.4   
Observable.range(10, 5).skipLast(2).subscribe(i -> log("" + i)); //10,11,12   
Observable.intervalRange(20, 5, 200, 200, TimeUnit.MILLISECONDS)   
      .skip(500, TimeUnit.MILLISECONDS).subscribe(i -> log("" + i)); //22,23,24   
Observable.intervalRange(30, 5, 500, 200, TimeUnit.MILLISECONDS)   
      .skipLast(500, TimeUnit.MILLISECONDS).subscribe(i -> log("" + i)); //30,31   
Observable.intervalRange(40, 5, 200, 200, TimeUnit.MILLISECONDS)   
      .skipUntil(Observable.just("开始发射数据时源Observable发射的数据才不会被丢掉").delay(500, TimeUnit.MILLISECONDS))   
      .subscribe(i -> log("" + i)); //42,43,44   
Observable.range(50, 5)   
      .delay(1000, TimeUnit.MILLISECONDS)   
      .flatMap(Observable::just)   
      .skipWhile(l -> l <= 52) //当满足条件时原始的Observable发射的数据会被丢掉   
      .subscribe(i -> log("" + i)); //53,54   
```   
  
## 获取指定位置的元素   
### first*   
只发射满足条件的第一条数据   
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-2018093011565938.png)   
- first：返回仅发出源ObservableSource发出的第一个item的Single，如果源ObservableSource不发出任何item，则发出默认item   
- firstOrError：返回仅发出源ObservableSource发出的第一个item的Single，如果此Observable为empty则发出NoSuchElementException事件   
- firstElement：返回仅发出源ObservableSource发出的第一个item的Maybe，如果源ObservableSource为空则发出complete事件   
  
```Java   
Observable.range(0, 5)   
      .doOnEach(notification -> log("first1发射的事件为：" + notification.getValue())) //只有一个 0   
      .first(100) //此时的结果和使用 firstOrError、firstElement 的效果完全一样   
      .subscribe(i -> log("first1:" + i), e -> log("first1异常"));  //0   
Observable.empty()   
      .doOnEach(notification -> log("first2发射的事件为：" + notification.getValue())) //null   
      .first(100)  //此时的结果和使用 firstOrError、firstElement 的效果仅仅是结果不一样   
      .subscribe(o -> log("first2:" + o), e -> log("first2异常")); //100、NoSuchElementException、onComplete   
```   
  
### last*   
只发射满足条件的最后一条数据   
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180930115711369.png)   
  
如果将以上代码改为 last* 时，则对于【Observable.empty()】，其结果和 first* 完全一致；    
而对于【Observable.range(0, 5)】，其结果和 first* 相比有一点区别，那就是 doOnEach 中一定会打印一项内容【发射的事件为：null】   
```Java   
Observable.range(10, 5)   
      .doOnEach(notification -> log("last发射的事件为：" + notification.getValue())) //有六个：10,11,12,13,14,null   
      .last(100)   
      .subscribe(i -> log("last:" + i), e -> log("last异常"));  //14   
```   
  
### elementAt elementAtOrError   
只发射第N项数据，和 first、last 基本一样   
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180930115725438.png)  
  
first* 方法其实调用的就是 elementAt 方法：   
```Java   
public final Single<T> first(T defaultItem) {   
    return elementAt(0L, defaultItem);   
}   
public final Single<T> firstOrError() {   
    return elementAtOrError(0L);   
}   
public final Maybe<T> firstElement() {   
    return elementAt(0L);   
}   
```   
其行为基本上是 first + last 的效果：   
- 如果源ObservableSource存在第 N 项数据，则源 ObservableSource 从第一项数据发布到第N项数据后就停止发射了，然后新的 ObservableSource 把此第N项数据发射出去；   
- 如果源ObservableSource发射的数据个数小于 N，则还会在源 ObservableSource 发布完所有数据后再发射一个【值为null】的对象；如果指定了默认数据，则在最最后还会发射默认数据   
  
```Java   
Observable.range(0, 5)   
      .doOnEach(notification -> log("1发射的事件为：" + notification.getValue())) //0,1   
      .elementAt(1)   
      .subscribe(i -> log("1:" + i), e -> log("1异常"), () -> log("完成"));  //1   
Observable.range(10, 5)   
      .doOnEach(notification -> log("2发射的事件为：" + notification.getValue())) //10,11,12,13,14,null   
      .elementAt(6)   
      .subscribe(i -> log("2:" + i), e -> log("2异常"), () -> log("完成"));  //0,完成   
Observable.range(20, 5)   
      .doOnEach(notification -> log("3发射的事件为：" + notification.getValue())) //20,21,22,23,24,null   
      .elementAt(6, 100)   
      .subscribe(i -> log("3:" + i), e -> log("3异常"));  //100   
Observable.empty()   
      .doOnEach(notification -> log("4发射的事件为：" + notification.getValue())) //null   
      .elementAtOrError(6)   
      .subscribe(i -> log("4:" + i), e -> log("4异常"));  //4异常   
```   
  
## single*   
如果只发射一个则发射这个值，如果不是则发射默认数据或异常   
- Maybe<T> singleElement()：如果此Observable为空或发出单个item，则返回Maybe并完成，如果此Observable发出多个item，则会发出 IllegalArgumentException 消息。   
- Single<T> single(T defaultItem)：如果此Observable仅发出单个item，则返回发出此Observable发出的单个item的Single；如果源ObservableSource未发出任何item，则返回发出默认item的Single；如果源ObservableSource发出多个item，则会发出 IllegalArgumentException 消息。   
- Single<T> singleOrError()：如果此Observable仅发出单个item，则返回发出此Observable发出的单个item的Single；如果此Observable完成而不发出任何item或发出多个item，则将分别[respectively]发出 NoSuchElementException 或 IllegalArgumentException 消息。   
  
```Java   
Observable.range(0, 5)   
      .doOnEach(notification -> log("发射的事件为：" + notification.getValue())) //0,1   
      .singleElement()  //此时的结果和使用 single、singleOrError 的效果完全一样   
      .subscribe(i -> log("" + i), e -> log("异常"), () -> log("完成"));  //IllegalArgumentException   
Observable.empty().delay(100, TimeUnit.MILLISECONDS)   
      .doOnEach(notification -> log("发射的事件为：" + notification.getValue())) //null   
      .single(100)  //此时的结果和使用 singleElement、singleOrError 的效果仅仅是结果不一样   
      .subscribe(i -> log("" + i), e -> log("异常"));  //100、完成、NoSuchElementException   
Observable.just(20).delay(200, TimeUnit.MILLISECONDS)   
      .doOnEach(notification -> log("发射的事件为：" + notification.getValue())) //20,null   
      .singleOrError()  //此时的结果和使用 single、singleElement 的效果完全一样   
      .subscribe(i -> log("" + i), e -> log("异常"));  //20   
```   
  
2018-9-26  
  
