| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RxJava【创建】操作符 create just from defer timer interval MD    
[demo地址](https://github.com/baiqiantao/RxJavaDemo.git)    
[参考](https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Creating-Observables.html)    
***  
目录  
===  

- [常用的创建操作符](# 常用的创建操作符)
	- [create](# create)
	- [just](# just)
	- [from](# from)
	- [fromArray 和 fromIterable](# fromarray 和 fromiterable)
	- [empty、error、never](# empty、error、never)
	- [defer](# defer)
	- [timer](# timer)
	- [interval、intervalRange](# interval、intervalrange)
	- [range、rangeLong](# range、rangelong)
	- [repeat repeatUntil repeatWhen](# repeat repeatuntil repeatwhen)
		- [repeat](# repeat)
		- [repeatUntil](# repeatuntil)
		- [repeatWhen](# repeatwhen)
  
# 常用的创建操作符    
基础创建    
- create：通过调用观察者的方法从头创建一个Observable，创建被观察者对象最基本的操作符    
  
快速创建    
- just：快速创建被观察者对象并直接发送传入的事件，最多发送10个事件(因为其是基于方法重载而非可变参数)    
- fromArray：和 just 类似，区别是，fromArray是基于可变参数的，所以其可接受的参数数量是没有限制的    
- fromIterable：快速创建被观察者对象并逐个发送集合中的元素    
- empty、error、never：直接发送Complete事件，或onError事件，或不发送任何事件    
  
延迟创建    
- defer：在观察者订阅之前不创建这个observable，订阅后才会调用defer创建被观察者对象    
- timer：延迟指定时间后，发送1个值为0的Long类型对象    
- interval、intervalRange：每隔指定时间就发送一个Long类型的对象(从0开始每次递增1)    
- range、rangeLong：连续发送一个int(long)类型的序列，作用类似于 intervalRange，默认在主线程上    
  
重复发送 `不是创建一个Observable，而是重复发射原始Observable的数据序列`    
- repeat、repeatUntil：创建一个重复发射指定数据或数据序列的Observable    
- repeatWhen：创建一个重复发射指定数据或数据序列的Observable，它依赖于另一个Observable发射的数据    
  
## create    
使用一个函数从头开始创建一个Observable    
  
你可以使用Create操作符从头开始创建一个Observable，给这个操作符传递一个接受观察者作为参数的函数，编写这个函数让它的行为表现为一个Observable--恰当的调用观察者的onNext，onError和onCompleted方法。    
  
一个形式正确的有限Observable必须尝试调用观察者的onCompleted正好一次或者它的onError正好一次，而且此后不能再调用观察者的任何其它方法。    
建议你在传递给create方法的函数中检查观察者的isDisposed状态，以便在没有观察者的时候，让你的Observable停止发射数据或者做昂贵的运算。    
```Java    
Observable.create(emitter -> {    
   emitter.onNext(1); //打印 onNext:1    
   emitter.onNext(2); //打印 onNext:2    
   emitter.onNext(3); //打印 onNext:3    
   emitter.onComplete(); //打印 onComplete    
}).subscribe(s -> log("onNext:" + s), t -> log("onError"), () -> log("onComplete"));    
```    
  
## just    
创建一个发射指定值的Observable    
  
Just将单个数据转换为发射那个数据的Observable，它接受一至九个参数，返回一个按参数列表顺序发射这些数据的Observable。    
  
> 注意：Just只是简单的原样发射，传入数组或Iterable时其会将数组或Iterable当做单个数据发射。    
> 注意：如果你传递null给Just，它会返回一个发射null值的Observable。不要误认为它会返回一个空Observable(完全不发射任何数据的Observable)，如果需要空Observable你应该使用Empty操作符。    
  
```Java    
just(T item1, T item2, T item3, T item4, T item5, T item6, T item7, T item8, T item9, T item10);     
```  
```Java    
Observable.just(1, 2, 3)  
    .subscribe(s -> log("onNext:" + s), t -> log("onError"), () -> log("onComplete"));//和上面一样    
Observable.just(new int[]{7, 8, 9})  
    .subscribe(arr -> log("onNext:" + Arrays.toString(arr)));//发送一个对象，onNext:[7, 8, 9]    
Observable.just(Arrays.asList(4, 5, 6))  
    .subscribe(list -> log("onNext:" + list.toString()));//发送一个对象，onNext:[4, 5, 6]    
```    
  
## from  
将其它种类的对象和数据类型转换为Observable    
  
在RxJava中，from操作符可以转换Future、Iterable和数组。对于Iterable和数组，产生的Observable会发射Iterable或数组的每一项数据。    
![from](http://pfpk8ixun.bkt.clouddn.com/blog/180927/1G6FDLDF75.png?imageslim)    
对于Future，它会发射Future.get()方法返回的单个数据。且可以指定超时时长和时间单位，也可以指定调度器。如果过了指定的时长Future还没有返回一个值，这个Observable会发射错误通知并终止。    
  
## fromArray 和 fromIterable    
> 注意，可变参数本身就是指一个数组，例如 Observable.fromArray(1, 2, 3) 其中Array指的就是 [1,2,3] ，我们最终发出的也是 1 和 2 和 3，所以我们才说：fromArray 和 fromIterable 一样是发射数组或集合的每一项数据。    
  
fromArray：    
```Java    
public static <T> Observable<T> fromArray(T... items)    
```  
```Java    
Observable.fromArray(1, 2, 3)  
    .subscribe(i -> log("*" + i));//整个发送传入的对象，*1 *2 *3    
Observable.fromArray(new int[]{4, 5, 6})  
    .subscribe(arr -> log(Arrays.toString(arr)));//整个发送传入的对象，[4, 5, 6]    
Observable.fromArray(new int[]{7, 8}, new int[]{9, 10})  
    .subscribe(arr -> log(Arrays.toString(arr)));//[7, 8] [9, 10]    
```    
  
fromIterable：    
```Java    
Observable.fromIterable(Arrays.asList(1, 2, 3))  
    .subscribe(i -> log("*" + i));//逐个发送对象，*1 *2 *3    
```    
  
## empty、error、never    
- empty：创建一个不发射任何数据但是正常终止的Observable    
- error：创建一个不发射数据以一个错误终止的Observable    
- never：创建一个不发射数据也不终止的Observable    
  
这三个操作符生成的Observable行为非常特殊和受限。测试的时候很有用，有时候也用于结合其它的Observables，或者作为其它需要Observable的操作符的参数。    
```Java    
Observable.empty().subscribe(o -> log("onNext"),    
      t -> log("onError"), () -> log("onComplete"), d -> log("onSubscribe"));//onSubscribe onComplete    
Observable.error(new Throwable("")).subscribe(o -> log("onNext"),    
      t -> log("onError"), () -> log("onComplete"), d -> log("onSubscribe"));//onSubscribe onError    
Observable.never().subscribe(o -> log("onNext"),    
      t -> log("onError"), () -> log("onComplete"), d -> log("onSubscribe"));//onSubscribe    
```    
  
## defer    
直到有观察者订阅时才创建Observable，并且为每个观察者创建一个新的Observable    
  
Defer操作符会一直等待直到有观察者订阅它，然后它使用Observable工厂方法生成一个Observable。它对每个观察者都这样做，因此尽管每个订阅者都以为自己订阅的是同一个Observable，事实上每个订阅者获取的是它们自己的单独的数据序列。    
  
在某些情况下，等待直到最后一刻(就是知道订阅发生时)才生成Observable可以确保Observable包含最新的数据。    
```Java    
Format format = new SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault());//订阅前不创建    
Observable<String> observable = Observable.defer(() -> Observable.just(format.format(new Date())));    
log("当前时间：" + format.format(new Date())); //当前时间：21:19:12 523    
SystemClock.sleep(1000);    
  
observable.subscribe(l -> log("发送对象：" + l));//订阅后才会调用defer创建被观察者对象：21:19:13 028    
```    
  
## timer    
创建一个Observable，它在一个给定的延迟后发射一个特殊的值。    
  
timer返回一个Observable，它在延迟一段给定的时间后发射一个简单的数字0。    
```Java    
Observable.timer(1000, TimeUnit.MILLISECONDS) //延迟指定时间后，发送1个值为0的Long类型对象，默认在子线程上    
      .doOnSubscribe(s -> log2("doOnSubscribe1")) //doOnSubscribe1，21:33:02 109，true    
      .subscribe(l -> log2("onNext1：" + l)); //onNext1：0，21:33:03 112，false    
Observable.timer(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()) //指定订阅者接收事件所在线程  
      .doOnSubscribe(s -> log2("doOnSubscribe2")) //doOnSubscribe2，21:33:02 111，true    
      .subscribe(l -> log2("onNext2：" + l)); //onNext2：0，21:33:03 113，true    
```    
  
## interval、intervalRange    
创建一个按固定时间间隔发射整数序列的Observable    
  
Interval操作符返回一个Observable，它按固定的时间间隔发射一个无限递增的整数序列。    
可以传递一个可选的Scheduler参数来指定调度器。    
![interval](http://pfpk8ixun.bkt.clouddn.com/blog/180927/jgm6CkmemF.png?imageslim)    
```Java    
log2("开始时间"); //每次递增1，默认在子线程上，可指定线程调度器    
Observable.interval(5000, 1000, TimeUnit.MILLISECONDS)//首次延迟时间、间隔时间，    
      .subscribe(l -> log2("接收的对象2：" + l)); //不会自动结束的    
Observable.intervalRange(100, 3, 0, 1, TimeUnit.SECONDS) //起始值，发送总数量    
      .subscribe(l -> log2("接收的对象1：" + l), t -> log2("onError"), () -> log2("接收的对象1：onComplete"));    
```    
```    
开始时间，21:53:29 350，true    
接收的对象1：100，21:53:29 365，false    
接收的对象1：101，21:53:30 366，false    
接收的对象1：102，21:53:31 366，false    
接收的对象1：onComplete，21:53:31 366，false    
   
接收的对象2：0，21:53:34 363，false    
接收的对象2：1，21:53:35 364，false    
```    
  
## range、rangeLong    
创建一个发射特定整数序列的Observable    
  
Range操作符发射一个范围内的有序整数序列，你可以指定范围的起始和长度。    
可以通过可选参数指定Scheduler。    
```Java    
Observable.range(10, 3)  
    .subscribe(i -> log2("" + i), t -> log2(""), () -> log2("onComplete1"));    
Observable.rangeLong(20, 2)  
    .subscribe(i -> log2("" + i), t -> log2(""), () -> log2("onComplete2"));    
```    
  
## repeat repeatUntil repeatWhen    
创建一个发射特定数据重复多次的Observable    
  
它不是创建一个Observable，而是重复发射原始Observable的数据序列    
  
### repeat    
可以是无限的，也可以通过repeat(n)指定重复次数。    
```Java    
Observable.just("无限个")    
      .delay(1000, TimeUnit.MILLISECONDS) //放在repeat之前则每次发送均会延迟1秒    
      .repeat()    
      .subscribe(i -> log2("" + i), t -> log2(""), () -> log2("onComplete1"));    
Observable.just("指定个数")    
      .repeat(5)    
      .delay(1000, TimeUnit.MILLISECONDS) //放在repeat之后则只在第一次发送会延迟1秒    
      .subscribe(i -> log2("" + i), t -> log2(""), () -> log2("onComplete2"));    
```    
```    
无限个，22:11:03 006，false    
指定个数，22:11:03 008，false    
指定个数，22:11:03 008，false    
指定个数，22:11:03 008，false    
指定个数，22:11:03 008，false    
指定个数，22:11:03 009，false    
onComplete1，22:11:03 009，false    
   
无限个，22:11:04 008，false    
无限个，22:11:05 011，false    
```    
  
### repeatUntil    
可以指定重复结束的条件    
```Java    
long startTime = System.currentTimeMillis();    
Observable.just("repeatUntil")    
      .delay(1000, TimeUnit.MILLISECONDS) //放在repeat之前则每次发送均会延迟1秒    
      .repeatUntil(() -> System.currentTimeMillis() - startTime > 1000 * 3) //3秒后结束    
      .subscribe(i -> log2("" + i), t -> log2(""), () -> log2("onComplete1"));    
```   
```   
repeatUntil，22:28:38 790，false    
repeatUntil，22:28:39 794，false    
repeatUntil，22:28:40 798，false    
onComplete1，22:28:40 800，false    
```    
  
### repeatWhen    
它不是缓存和重放原始Observable的数据序列，而是有条件的重新订阅和发射原来的Observable。    
  
将原始Observable的终止通知(完成或错误)当做一个void数据传递给一个通知处理器，它以此来决定是否要重新订阅和发射原来的Observable。这个通知处理器就像一个Observable操作符，接受一个发射void通知的Observable为输入，返回一个发射void数据(意思是，重新订阅和发射原始Observable)或者直接终止(意思是，使用repeatWhen终止发射数据)的Observable。    
  
可以通过可选参数指定Scheduler。    
```Java    
Observable.just("repeatWhen")    
      .repeatWhen(s -> s.delay(1000, TimeUnit.MILLISECONDS))    
      .subscribe(i -> log2("" + i), t -> log2(""), () -> log2("onComplete2"));    
```    
```    
repeatWhen，22:28:37 797，true    
repeatWhen，22:28:38 799，false    
repeatWhen，22:28:39 802，false    
repeatWhen，22:28:40 804，false    
```    
  
2018-9-18    
