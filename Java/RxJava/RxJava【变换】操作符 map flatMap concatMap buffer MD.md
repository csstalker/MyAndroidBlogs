| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RxJava【变换】操作符 map flatMap concatMap buffer MD    
[demo地址](https://github.com/baiqiantao/RxJavaDemo.git)     
[参考](https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Transforming-Observables.html)     
***   
目录  
===  

- [变换操作符](#变换操作符)
	- [map cast](#map-cast)
	- [flatMap concatMap switchMap flatMapIterable](#flatMap-concatMap-switchMap-flatMapIterable)
		- [使用 flatMap 化解循环嵌套](#使用-flatMap-化解循环嵌套)
		- [concatMap](#concatMap)
		- [switchMap](#switchMap)
		- [使用 flatMap 化解接口嵌套](#使用-flatMap-化解接口嵌套)
		- [flatMapIterable](#flatMapIterable)
	- [buffer](#buffer)
		- [buffer(count)](#buffercount)
		- [buffer(count, skip)](#buffercount-skip)
		- [buffer(timespan, unit)](#buffertimespan-unit)
		- [buffer(timespan, unit, count)](#buffertimespan-unit-count)
	- [scan](#scan)
	- [groupBy](#groupBy)
	- [window](#window)
		- [window(count)](#windowcount)
		- [window(count, skip)](#windowcount-skip)
		- [window(timespan, unit)](#windowtimespan-unit)
		- [window(timespan, unit, count)](#windowtimespan-unit-count)
  
# 变换操作符   
常用的变换操作符   
- map、cast：【数据类型转换】将Observable发送的事件转换为另一种类型的事件   
- flatMap、concatMap、switchMap、flatMapIterable：扁平化   
    - flatMap：【化解循环嵌套和接口嵌套】将Observable发送的事件序列进行拆分 & 转换 后合并成一个新的事件序列，最后再进行发送   
    - concatMap：【有序】与 flatMap 的 区别在于，拆分 & 重新合并生成的事件序列 的顺序与被观察者旧序列生产的顺序一致   
    - switchMap：当原始Observable发射一个新的数据时，它将取消订阅并停止监视产生执之前那个数据的Observable，只监视当前这一个   
    - flatMapIterable：相当于对 flatMap 的数据进行了二次扁平化   
- buffer：【打包】定期从Observable发送的事件中获取一定数量的事件并放到缓存区中，然后把这些数据集合打包发射   
- scan：【连续】对Observable发射的每一项数据应用一个函数，然后按顺序依次发射每一个值   
- groupBy：【分组】将一个Observable分拆为一些Observables集合，它们中的每一个发射原始Observable的一个子序列   
- window：定期将来自Observable的数据分拆成一些Observable窗口，然后发射这些窗口，而不是每次发射一项   
  
## map cast   
Map操作符对Observable发射的每一项数据应用一个函数，执行变换操作   
  
Map操作符对原始Observable发射的每一项数据应用一个你选择的函数，然后返回一个发射这些结果的Observable。   
```Java  
Observable.just(new Date()) // Date 类型   
      .map(Date::getTime) // long 类型   
      .map(time -> time + 1000 * 60 * 60)// 改变 long 类型时间的值   
      .map(time -> new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(time))) //String 类型   
      .subscribe(this::log);   
```  
  
cast操作符将原始Observable发射的每一项数据都强制转换为一个指定的类型，然后再发射数据，它是map的一个特殊版本。  
```Java   
Observable.just(28)  
    .cast(Number.class)  
    .subscribe(number -> log(number.getClass().getSimpleName())); //Integer  
```  
  
## flatMap concatMap switchMap flatMapIterable   
flatMap 将一个发射数据的Observable变换为多个Observables，然后将它们发射的数据合并后放进一个单独的Observable   
  
flatMap 操作符使用一个指定的函数对原始Observable发射的每一项数据执行变换操作，这个函数返回一个本身也发射数据的Observable，然后FlatMap合并这些Observables发射的数据，最后将合并后的结果当做它自己的数据序列发射。   
  
> 注意：flatMap 对这些Observables发射的数据做的是合并(merge)操作，因此它们可能是交错的。   
  
flatMap 有很多个重载方法。   
```Java  
flatMap(Function mapper)   
flatMap(Function mapper, boolean delayErrors)   
flatMap(Function mapper, boolean delayErrors, int maxConcurrency)   
flatMap(Function mapper, boolean delayErrors, int maxConcurrency, int bufferSize)   
flatMap(Function onNextMapper, Function onErrorMapper, Callable onCompleteSupplier)   
flatMap(Function onNextMapper, Function onErrorMapper, Callable onCompleteSupplier, int maxConcurrency)   
flatMap(Function mapper, int maxConcurrency)   
flatMap(Function  mapper, BiFunction resultSelector)   
flatMap(Function mapper, BiFunction combiner, boolean delayErrors)   
flatMap(Function mapper, BiFunction combiner, boolean delayErrors, int maxConcurrency)   
flatMap(Function mapper, BiFunction combiner, boolean delayErrors, int maxConcurrency, int bufferSize)   
flatMap(Function mapper, BiFunction combiner, int maxConcurrency)   
```  
  
### 使用 flatMap 化解循环嵌套   
```Java  
Observable.just(new Person(Arrays.asList("篮球", "足球", "排球")), new Person(Arrays.asList("画画", "跳舞")))   
      .map(person -> person.loves)   
      .flatMap(Observable::fromIterable) //fromIterable：逐个发送集合中的元素   
      .subscribe(this::log);   
```  
```  
篮球，22:56:43 009，true   
足球，22:56:43 010，true   
排球，22:56:43 010，true   
画画，22:56:43 011，true   
跳舞，22:56:43 012，true   
```  
  
> 注意：如果任何一个通过这个 flatMap 操作产生的单独的 Observable 调用 onError 异常终止了，这个 Observable 自身会立即调用 onError 并终止。例如：  
```  
Observable.just(new Person(Arrays.asList("篮球", null, "排球")), new Person(Arrays.asList("画画", "跳舞"))) ...  
篮球，00:20:14 762，true   
onError:The iterator returned a null value，00:20:14 767，true   
```  
  
### concatMap   
concatMap 操作符的功能和 flatMap 非常相似，只不过经过 flatMap 操作变换后，最后输出的序列有可能是交错的(flatMap最后合并结果采用的是 merge 操作符)，而 concatMap 最终输出的数据序列和原数据序列是一致的。   
  
flatMap：   
```Java  
long start = System.currentTimeMillis();   
Observable.just(Arrays.asList(1, 2, 3), Arrays.asList(4, 5))   
      .flatMap(list -> Observable.fromIterable(list).delay(list.size(), TimeUnit.SECONDS))//flatMap是无序的   
      .subscribe((i -> log("f:" + i)), e -> log("f"), () -> log("f耗时" + (System.currentTimeMillis() - start))); //3秒   
```  
```  
f:4，23:21:07 944，false   //flatMap后，订阅者首先接收到的事件是【4】而不是【1】   
f:5，23:21:07 945，false   
f:1，23:21:08 942，false   
f:2，23:21:08 943，false   
f:3，23:21:08 943，false   
f耗时3025，23:21:08 945，false    //flatMap耗时3秒   
```  
  
concatMap：   
```Java  
Observable.just(Arrays.asList(1, 2, 3), Arrays.asList(4, 5))   
      .concatMap(list -> Observable.fromIterable(list).delay(list.size(), TimeUnit.SECONDS))//concatMap是有序的   
      .subscribe(i -> log("c:" + i), e -> log("c"), () -> log("c耗时" + (System.currentTimeMillis() - start))); //5秒   
```  
```  
c:1，14:27:27 450，false   //concatMap后，订阅者首先接收到的事件是【1】   
c:2，14:27:27 451，false   
c:3，14:27:27 451，false   
c:4，14:27:29 454，false   
c:5，14:27:29 455，false   
c耗时5021，14:27:29 455，false    //concatMap耗时5秒   
```  
  
### switchMap   
switchMap 和 flatMap 很像，除了一点：当源Observable发射一个新的数据项时，如果旧数据项订阅还未完成，就取消旧订阅数据和停止监视那个数据项产生的Observable，开始监视新的数据项。   
> It behaves much like flatMap, except that whenever a new item is emitted by the source Observable, it will unsubscribe to and stop mirroring镜像 the Observable that was generated from the previously-emitted item, and begin only mirroring the current one.   
  
如果是在同一线程产生数据，因为当第二个数据项来临时，第一个已经完成了，此时其和 concatMap 是完全一致的。    
如果是并发产生数据项，当第二个数据项来临时，如果前一个任务尚未执行结束，就会被后一个任务给取消。   
  
```Java  
Observable.just(Arrays.asList(1, 2, 3), Arrays.asList(4, 5))   
     .switchMap(Observable::fromIterable)   
     .subscribeOn(Schedulers.newThread()) //与这里的线程无关   
     .subscribe(i -> log("s:" + i)); //1, 2, 3,4, 5   
  
Observable.just(Arrays.asList(1, 2, 3), Arrays.asList(4, 5))   
     .switchMap(list -> Observable.fromIterable(list).subscribeOn(Schedulers.newThread()))  //只与这里的线程有关   
     .subscribeOn(AndroidSchedulers.mainThread()) //与这里的线程无关   
     .observeOn(AndroidSchedulers.mainThread()) //与这里的线程无关   
     .subscribe(i -> log("s:" + i)); //4, 5   
  
Observable.range(1, 8)   
     .switchMap(i -> Observable.just(i).subscribeOn(Schedulers.newThread()))  //只与这里的线程有关   
     .subscribe(i -> log("s:" + i)); //8   
```  
```  
s:1，15:05:23 537，false   
s:2，15:05:23 537，false   
s:3，15:05:23 538，false   
s:4，15:05:23 538，false   
s:5，15:05:23 539，false   
  
s:4，15:05:21 013，true   
s:5，15:05:21 014，true   
  
s:8，15:05:22 669，false   
```  
  
### 使用 flatMap 化解接口嵌套   
可以利用 flatMap 操作符实现网络请求依次依赖，即：第一个接口的返回值包含第二个接口请求需要用到的数据。   
  
首先是两个请求网络的操作：   
```Java  
private Observable<String> firstRequest(String parameter) {   
   return Observable.create(emitter -> {   
      SystemClock.sleep(2000);//模拟网络请求   
      emitter.onNext(parameter + "，第一次修改：" + FORMAT.format(new Date(System.currentTimeMillis())));   
      emitter.onComplete();   
   });   
}   
private Observable<String> secondRequest(String parameter) {   
   return Observable.create(emitter -> {   
      SystemClock.sleep(3000);//模拟网络请求   
      emitter.onNext(parameter + "，第二次修改：" + FORMAT.format(new Date(System.currentTimeMillis())));   
      emitter.onComplete();   
   });   
}   
```  
  
然后可以通过 flatMap 将两者串联起来：   
```Java  
firstRequest("原始值：" + FORMAT.format(new Date(System.currentTimeMillis())))   
      .subscribeOn(Schedulers.io()) // 在io线程进行网络请求   
      .observeOn(AndroidSchedulers.mainThread()) // 在主线程处理请求结果   
      .doOnNext(response -> log("【第一个网络请求结束，响应为】" + response))//true   
      .observeOn(Schedulers.io()) // 回到 io 线程去处理下一个网络请求   
      .flatMap(this::secondRequest)//实现多个网络请求依次依赖   
      .observeOn(AndroidSchedulers.mainThread()) // 在主线程处理请求结果   
      .subscribe(string -> log("【第二个网络请求结束，响应为】" + string));//true，5 秒   
```  
打印结果为：   
> 【第一个网络请求结束，响应为】原始值：23:58:11 220，第一次修改：23:58:13 245，true   
> 【第二个网络请求结束，响应为】原始值：23:58:11 220，第一次修改：23:58:13 245，第二次修改：23:58:16 256，true   
  
简化形式的Demo代码为：   
```Java  
Observable.just("包青天").delay(1000, TimeUnit.MILLISECONDS) //第一个网络请求，返回姓名   
      .flatMap(s -> Observable.just(s + "，男").delay(1000, TimeUnit.MILLISECONDS)) //第二个网络请求，返回性别   
      .flatMap(s -> Observable.just(s + "，28岁").delay(1000, TimeUnit.MILLISECONDS)) //第三个网络请求，返回年龄   
      .subscribeOn(Schedulers.io())   
      .observeOn(AndroidSchedulers.mainThread())   
      .subscribe(this::log); //包青天，男，28岁，耗时:3058毫秒，true   
```  
  
当然这种情况下使用 concatMap 的效果也是完全一样的，然而因为 concatMap 的核心是用来保证在合并时"有序"的，而这两种情况根本就没涉及到合并，所以这些情况下使用 concatMap是没有任何意义的。   
  
### flatMapIterable   
flatMapIterable这个变体成对的打包数据，然后生成Iterable而不是原始数据和生成的Observables，但是处理方式是相同的。   
  
flatMapIterable 返回一个Observable，它将源ObservableSource发出的每个项目与 the values in an Iterable corresponding to that item that is generated by a selector 合并[merges]。   
```Java  
public final <U> Observable<U> flatMapIterable(final Function<? super T, ? extends Iterable<? extends U>> mapper)   
```  
> 参数 mapper：一个在源 ObservableSource 发出指定项时，返回一个 Iterable 值序列的函数。   
  
案例1：   
```Java  
Observable.just(Arrays.asList("篮球1", "足球1"))   
      .flatMap(Observable::fromIterable) //返回一个 Observable   
      .subscribe(string -> log("" + string));   
  
Observable.just(Arrays.asList("篮球2", "足球2"))   
      .flatMapIterable(list -> list) //返回一个 Iterable 而不是另一个 Observable   
      .subscribe(string -> log("" + string));   
  
Observable.fromIterable(Arrays.asList("篮球3", "足球3")) //和上面两种方式的结果一样   
      .subscribe(string -> log("" + string));   
```  
```  
篮球1，01:00:39 493，true   
足球1，01:00:39 494，true   
篮球2，01:00:39 496，true   
足球2，01:00:39 496，true   
篮球3，01:00:39 499，true   
足球3，01:00:39 499，true   
```  
  
案例2：   
```Java  
Observable.just(new Person(Arrays.asList("包青天", "哈哈")), new Person(Arrays.asList("白乾涛", "你好")))   
      .map(person -> person.loves)   
      .flatMap(Observable::fromIterable) //返回一个 Observable   
      .flatMap(string -> Observable.fromArray(string.toCharArray())) //返回一个 Observable   
      .subscribe(array -> log(Arrays.toString(array)));   
  
Observable.just(new Person(Arrays.asList("广州", "上海")), new Person(Arrays.asList("武汉", "长沙")))   
      .map(person -> person.loves)   
      .flatMap(Observable::fromIterable) //返回一个 Observable   
      .flatMapIterable(string -> Arrays.asList(string.toCharArray())) //返回一个 Iterable 而不是另一个 Observable   
      .subscribe(array -> log(Arrays.toString(array)));   
  
Observable.just(new Person(Arrays.asList("你妹", "泥煤")), new Person(Arrays.asList("你美", "你没")))   
      .map(person -> person.loves)   
      .flatMapIterable(list -> {   
               List<char[]> charList = new ArrayList<>();   
               for (String string : list) {   
                  charList.add(string.toCharArray());   
               }   
               return charList; //返回一个 Iterable 而不是另一个 Observable   
            }   
      ).subscribe(array -> log(Arrays.toString(array)));   
```  
```  
[包, 青, 天]，21:48:37 917，true   
[哈, 哈]，21:48:37 919，true   
[白, 乾, 涛]，21:48:37 921，true   
[你, 好]，21:48:37 921，true   
  
[广, 州]，21:48:37 925，true   
[上, 海]，21:48:37 926，true   
[武, 汉]，21:48:37 926，true   
[长, 沙]，21:48:37 927，true   
  
[你, 妹]，21:48:37 929，true   
[泥, 煤]，21:48:37 929，true   
[你, 美]，21:48:37 930，true   
[你, 没]，21:48:37 931，true   
```  
  
## buffer   
定期收集Observable的数据放进一个数据包裹，然后发射这些数据包裹，而不是一次发射一个值。   
Buffer操作符将一个Observable变换为另一个，原来的Observable正常发射数据，变换产生的Observable发射这些数据的缓存集合。   
  
> 注意：如果原来的Observable发射了一个onError通知，Buffer会立即传递这个通知，而不是首先发射缓存的数据，即使在这之前缓存中包含了原始Observable发射的数据[without first emitting the buffer it is in the process of assembling]。   
  
Window操作符与Buffer类似，但是它在发射之前把收集到的数据放进单独的Observable，而不是放进一个数据结构。   
  
![buffer](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180928200106645.png)  
在RxJava中有许多Buffer的变体：   
```Java  
buffer(ObservableSource openingIndicator, Function closingIndicator)   
buffer(ObservableSource openingIndicator, Function closingIndicator, Callable bufferSupplier)   
```  
  
### buffer(count)   
buffer(count)以列表(List)的形式发射非重叠的缓存，每一个缓存至多包含来自原始Observable的count项数据(最后发射的列表数据可能少于count项)。   
  
每接收到 count 个数据包裹，将这 count 个包裹打包，发送给订阅者   
  
一次订阅2个：   
```Java  
Observable.range(1, 5)   
    .buffer(2) //缓存区大小，步长==缓存区大小，等价于buffer(count, count)   
    .subscribe(list -> log(list.toString()), t -> log(""), () -> log("完成")); //[1, 2]，[3, 4]，[5]，完成   
```  
  
一次全部订阅(将所有元素组装到集合中的效果)：  
```Java  
Observable.range(1, 10)  
    .buffer(10)  
    .subscribe(list -> log(list.toString())); //[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  
```  
  
### buffer(count, skip)   
buffer(count, skip)从原始Observable的第一项数据开始创建新的缓存，此后每当收到skip项数据，用count项数据填充缓存：开头的一项和后续的count-1项，它以列表(List)的形式发射缓存，取决于count和skip的值，这些缓存可能会有重叠部分（比如skip < count时），也可能会有间隙（比如skip > count时）。   
  
生成的ObservableSource每隔 skip 项就会 emits buffers，每个 buffers 都包含 count 个 items。   
  
队列效果(先进先出)：   
```Java  
Observable.range(1, 5)   
      .buffer(3, 1) // 缓存区大小，步长(每次获取新事件的数量)   
      .subscribe(list -> log(list.toString()));//[1, 2, 3]，[2, 3, 4]，[3, 4, 5]，[4, 5]，[5]   
```  
   
每次剔除一个效果：   
```Java  
Observable.range(1, 5).buffer(5, 1)   
    .subscribe(list -> log(list.toString()));//[1, 2, 3, 4, 5]，[2, 3, 4, 5]，[3, 4, 5]，[4, 5]，[5]   
```  
  
只取奇数个效果：  
```Java  
Observable.range(1, 5)  
    .buffer(1, 2)  
    .subscribe(list -> log(list.toString()));//[1]，[3]，[5]   
```  
  
### buffer(timespan, unit)   
buffer(timespan, unit)定期以List的形式发射新的数据，每个时间段，收集来自原始Observable的数据(从前面一个数据包裹之后，或者如果是第一个数据包裹，从有观察者订阅原来的Observale之后开始)。   
  
持续收集直到指定的每隔时间后，然后发射一次并清空缓存区。   
  
周期性订阅多个结果：   
```Java  
Observable.intervalRange(0, 8, 100, 100, TimeUnit.MILLISECONDS) //从0开始发射8个   
    .buffer(250, TimeUnit.MICROSECONDS) //等价于 count = Integer.MAX_VALUE   
    .subscribe(list -> log("缓存区中事件：" + list.toString())); //[0, 1]，[2, 3]，[4, 5, 6]，[7]   
```  
  
### buffer(timespan, unit, count)   
每当收到来自原始Observable的count项数据，或者每过了一段指定的时间后，buffer(timespan, unit, count)就以List的形式发射这期间的数据，即使数据项少于count项。   
  
当达到指定时间【或】缓冲区中达到指定数量时发射   
```Java  
Observable.intervalRange(0, 8, 100, 100, TimeUnit.MILLISECONDS) //从0开始发射8个   
    .buffer(250, TimeUnit.MICROSECONDS, 2) //可以指定工作所在的线程   
    .subscribe(list -> log("缓存区中事件：" + list.toString())); //[0, 1]，[]，[2, 3]，[]，[4, 5]，[6]，[7]   
```  
  
## scan   
连续地对数据序列的每一项应用一个函数，然后连续发射结果   
![scan](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180928200149748.png)  
  
Scan操作符对原始Observable发射的第一项数据应用一个函数，然后将那个函数的结果作为自己的第一项数据发射。它将函数的结果同第二项数据一起填充给这个函数来产生它自己的第二项数据。它持续进行这个过程来产生剩余的数据序列。这个操作符在某些情况下被叫做accumulator。   
  
```Java  
Observable.range(1, 10)   
      .scan((i1, i2) -> i1 + i2)   
      .subscribe(sum -> log("" + sum)); //1,3,6,10,15,21...   
Observable.just("包青天", "你好", "我是泥煤")   
      .scan((s1, s2) -> s1 + "," + s2)   
      .subscribe(s -> log("值为：" + s)); //包青天,你好,我是泥煤   
```  
  
## groupBy   
GroupBy操作符将原始Observable分拆为一些Observables集合，它们中的每一个发射原始Observable数据序列的一个子序列。    
哪个数据项由哪一个Observable发射是由一个函数判定的，这个函数给每一项指定一个Key，Key相同的数据会被同一个Observable发射。    
  
注意：groupBy将原始Observable分解为一个发射多个GroupedObservable的Observable，一旦有订阅，每个GroupedObservable就开始缓存数据。因此，如果你忽略这些GroupedObservable中的任何一个，这个缓存可能形成一个潜在的内存泄露。因此，如果你不想观察，也不要忽略GroupedObservable。你应该使用像take(0)这样会丢弃自己的缓存的操作符。   
  
如果你取消订阅一个GroupedObservable，那个Observable将会终止。如果之后原始的Observable又发射了一个与这个Observable的Key匹配的数据，groupBy将会为这个Key创建一个新的GroupedObservable。   
  
有一个版本的groupBy允许你传递一个变换函数，这样它可以在发射结果GroupedObservable之前改变数据项。   
```Java  
groupBy(Function keySelector)   
groupBy(Function keySelector, boolean delayError)   
groupBy(Function keySelector, Function valueSelector)   
groupBy(Function keySelector, Function valueSelector, boolean delayError)   
groupBy(Function keySelector, Function valueSelector, boolean delayError, int bufferSize)   
```  
  
案例：   
```Java  
Observable.range(1, 5)   
      .groupBy(i -> "包青天" + i % 2) //返回值决定组名   
      .subscribe(groupedObservable ->   
            groupedObservable.subscribe(i -> log("组名为：" + groupedObservable.getKey() + "，值为：" + i)));   
```  
```  
组名为：包青天1，值为：1，16:34:47 561，true   
组名为：包青天0，值为：2，16:34:47 562，true   
组名为：包青天1，值为：3，16:34:47 564，true   
组名为：包青天0，值为：4，16:34:47 565，true   
组名为：包青天1，值为：5，16:34:47 566，true   
```  
  
## window   
定期将来自原始Observable的数据分解为一个Observable窗口，发射这些窗口，而不是每次发射一项数据   
  
Window和Buffer类似，但不是发射来自原始Observable的数据包，它发射的是Observables，这些Observables中的每一个都发射原始Observable数据的一个子集，最后发射一个onCompleted通知。   
  
和Buffer一样，Window有很多变体，每一种都以自己的方式将原始Observable分解为多个作为结果的Observable，每一个都包含一个映射原始数据的window。用Window操作符的术语描述就是，当一个窗口打开(when a window "opens")意味着一个新的Observable已经发射了，而且这个Observable开始发射来自原始Observable的数据；当一个窗口关闭(when a window "closes")意味着发射的Observable停止发射原始Observable的数据，并且发射终止通知onCompleted给它的观察者们。   
  
如果从原始Observable收到了onError或onCompleted通知它也会关闭当前窗口。   
  
和Buffer一样，Window有很多变体：  
![window](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20180928200213778.png)  
```Java  
window(ObservableSource openingIndicator, Function closingIndicator)   
window(ObservableSource openingIndicator, Function closingIndicator, int bufferSize)   
```  
  
以下案例中均用到以下代码：   
```Java  
private Consumer<Observable<Integer>> consumer = observable -> {   
   SystemClock.sleep(100);   
   String name = new SimpleDateFormat("SSS", Locale.getDefault()).format(new Date());   
   log("打开了一个新的窗口 " + name); //每当当前窗口发射了count项数据，它就关闭当前窗口并打开一个新窗口   
   observable.subscribe(i -> {   
      SystemClock.sleep(100);   
      log("窗口 " + name + " 发射了数据：" + i);   
      SystemClock.sleep(100);   
   }, e -> log("窗口 " + name + " 异常了"), () -> log("窗口 " + name + " 关闭了"));   
};   
```  
  
### window(count)   
这个window的变体立即打开它的第一个窗口。每当当前窗口发射了count项数据，它就关闭当前窗口并打开一个新窗口。    
这种window变体发射一系列不重叠的窗口，这些窗口的数据集合与原始Observable发射的数据是一一对应的。   
```Java  
Observable.range(1, 5)   
      .window(2)   
      .subscribe(consumer);   
```  
```  
打开了一个新的窗口 418   
窗口 418 发射了数据：1   
窗口 418 发射了数据：2   
窗口 418 关闭了   
  
打开了一个新的窗口 926   
窗口 926 发射了数据：3   
窗口 926 发射了数据：4   
窗口 926 关闭了   
  
打开了一个新的窗口 435   
窗口 435 发射了数据：5   
窗口 435 关闭了   
```  
  
### window(count, skip)   
这个window的变体立即打开它的第一个窗口。原始Observable每发射skip项数据它就打开一个新窗口(例如，如果skip等于3，每到第三项数据，它会打开一个新窗口)。每当当前窗口发射了count项数据，它就关闭当前窗口并打开一个新窗口。   
  
如果skip > count，在两个窗口之间会有skip - count项数据被丢弃。  
```Java  
Observable.range(20, 5)  
    .window(1, 2)  
    .subscribe(consumer);  //每发射2项就打开一个新窗口，每当当前窗口发射了1项就关闭当前窗口并打开一个新窗口  
```  
```  
打开了一个新的窗口 714   
窗口 714 发射了数据：1   
窗口 714 关闭了   
打开了一个新的窗口 020   
窗口 020 发射了数据：3   
窗口 020 关闭了   
打开了一个新的窗口 326   
窗口 326 发射了数据：5   
窗口 326 关闭了   
```  
  
如果skip=count，它的行为与window(count)相同   
```Java  
Observable.range(20, 5)  
    .window(2, 2)  
    .subscribe(consumer);  //每发射2项就打开一个新窗口，每当当前窗口发射了2项就关闭当前窗口并打开一个新窗口 `  
```  
```  
打开了一个新的窗口 867   
窗口 867 发射了数据：10   
窗口 867 发射了数据：11   
窗口 867 关闭了   
  
打开了一个新的窗口 376   
窗口 376 发射了数据：12   
窗口 376 发射了数据：13   
窗口 376 关闭了   
  
打开了一个新的窗口 885   
窗口 885 发射了数据：14   
窗口 885 关闭了   
```  
  
如果skip < count，窗口可会有count - skip 个重叠的数据；   
```Java  
Observable.range(20, 5)  
    .window(2, 1)  
    .subscribe(consumer);  //每发射1项就打开一个新窗口，每当当前窗口发射了2项就关闭当前窗口并打开一个新窗口 `  
```  
```  
打开了一个新的窗口 005   
窗口 005 发射了数据：20   
打开了一个新的窗口 310   
窗口 005 发射了数据：21   
窗口 310 发射了数据：21   
窗口 005 关闭了   
打开了一个新的窗口 818   
窗口 310 发射了数据：22   
窗口 818 发射了数据：22   
窗口 310 关闭了   
打开了一个新的窗口 327   
窗口 818 发射了数据：23   
窗口 327 发射了数据：23   
窗口 818 关闭了   
打开了一个新的窗口 836   
窗口 327 发射了数据：24   
窗口 836 发射了数据：24   
窗口 327 关闭了   
窗口 836 关闭了   
```  
  
### window(timespan, unit)   
  
这个window的变体立即打开它的第一个窗口。每当过了timespan这么长的时间它就关闭当前窗口并打开一个新窗口。    
这种window变体发射一系列不重叠的窗口，这些窗口的数据集合与原始Observable发射的数据也是一一对应的。   
```Java  
Observable.range(1, 6)  
    .window(500, TimeUnit.MILLISECONDS)  
    .subscribe(consumer); `  
```  
```  
打开了一个新的窗口 195   
窗口 195 发射了数据：1   
窗口 195 发射了数据：2   
窗口 195 发射了数据：3   
窗口 195 关闭了   
  
打开了一个新的窗口 908   
窗口 908 发射了数据：4   
窗口 908 发射了数据：5   
窗口 908 关闭了   
  
打开了一个新的窗口 416   
窗口 416 发射了数据：6   
窗口 416 关闭了   
```  
  
### window(timespan, unit, count)   
这个window的变体立即打开它的第一个窗口。这个变体是window(count)和window(timespan, unit)的结合，每当过了timespan的时长或者当前窗口收到了count项数据，它就关闭当前窗口并打开另一个。    
这种window变体发射一系列不重叠的窗口，这些窗口的数据集合与原始Observable发射的数据也是一一对应的。   
```Java  
Observable.range(10, 4)  
    .window(1000, TimeUnit.MILLISECONDS, 2)  
    .subscribe(consumer);  
```  
```  
打开了一个新的窗口 725   
窗口 725 发射了数据：10   
窗口 725 发射了数据：11   
窗口 725 关闭了   
  
打开了一个新的窗口 233   
窗口 233 发射了数据：12   
窗口 233 发射了数据：13   
窗口 233 关闭了   
  
打开了一个新的窗口 250   
窗口 250 关闭了   
```  
  
案例2：   
```Java  
Observable.range(10, 3)  
    .window(200, TimeUnit.MILLISECONDS, 2)  
    .subscribe(consumer); `  
```  
```  
打开了一个新的窗口 696   
窗口 696 发射了数据：10   
窗口 696 关闭了   
  
打开了一个新的窗口 001   
窗口 001 发射了数据：11   
窗口 001 关闭了   
  
打开了一个新的窗口 306   
窗口 306 关闭了   
  
打开了一个新的窗口 408   
窗口 408 发射了数据：12   
窗口 408 关闭了   
  
打开了一个新的窗口 712   
窗口 712 关闭了   
  
打开了一个新的窗口 815   
窗口 815 关闭了   
```  
2018-9-18  
  
