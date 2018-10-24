| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
RxJava 设计理念 观察者模式 Observable lambdas MD    
[demo地址](https://github.com/baiqiantao/RxJavaDemo.git)    
***    
目录    
===    
[TOC]    
  
  
# 首先写一个最简单的观察者模式    
```Java    
interface Watcher {    
    void update(String str);    
}    
```    
```Java    
interface Watched {    
    void addWatcher(Watcher watcher);    
    void removeWatcher(Watcher watcher);    
    void notifyWatchers(String str);    
}    
```    
```Java    
class ConcreteWatcher implements Watcher {    
    @Override    
    public void update(String str) {    
        Log.i("bqt", "观察者收到被观察者的消息：" + str);    
    }    
}    
```    
```Java    
class ConcreteWatched implements Watched {    
    private List<Watcher> list = new ArrayList<>();    
  
    @Override    
    public void addWatcher(Watcher watcher) {    
        if (list.contains(watcher)) {    
            Log.i("bqt", "失败，被观察者已经注册过此观察者");    
        } else {    
            Log.i("bqt", "成功，被观察者成功注册了此观察者");    
            list.add(watcher);    
        }    
    }    
  
    @Override    
    public void removeWatcher(Watcher watcher) {    
        if (list.contains(watcher)) {    
            boolean success = list.remove(watcher);    
            Log.i("bqt", "此观察者解除注册观察者结果：" + (success ? "成功" : "失败"));    
        } else {    
            Log.i("bqt", "失败，此观察者并未注册到被观察者");    
        }    
    }    
  
    @Override    
    public void notifyWatchers(String str) {    
        for (Watcher watcher : list) {    
            watcher.update(str);    
        }    
    }    
}    
```    
  
演示案例：    
```Java    
private Watcher watcher;    
private Watched watched;    
...    
watcher = new ConcreteWatcher();    
watched = new ConcreteWatched();    
...    
watched.addWatcher(watcher);    
watched.notifyWatchers("救命啊");    
watched.removeWatcher(watcher);    
```    
  
# 我们再对比下用 rx 写的观察者模式    
首先，我们创建一个基本的被观察者Observable：    
```Java    
Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {    
    @Override    
    public void subscribe(ObservableEmitter<String> emitter) {    
        emitter.onNext("救命啊-------isDisposed=" + emitter.isDisposed());//false    
        if (new Random().nextBoolean()) emitter.onComplete(); //发送onComplete    
        else emitter.onError(new Throwable("发送onError")); //发送onComplete或onError事件后就取消注册了    
        Log.i("bqt", "调用 onComplete 或 onError 后 isDisposed=" + emitter.isDisposed());//true    
    }    
});    
```    
  
接着我们创建Observer来消费这个数据：    
```Java    
Observer<String> observer = new Observer<String>() {    
    @Override    
    public void onSubscribe(Disposable d) {    
        Log.i("bqt", "成功订阅-------isDisposed=" + d.isDisposed());//false    
    }    
  
    @Override    
    public void onNext(String s) {    
        Log.i("bqt", "【观察者接收到了事件】onNext:" + s);    
    }    
  
    @Override    
    public void onError(Throwable e) {    
        Log.i("bqt", "对Error事件作出响应:" + e.getMessage());    
    }    
  
    @Override    
    public void onComplete() {    
        Log.i("bqt", "对Complete事件作出响应");    
    }    
};    
```    
  
现在我们已经有了Observable和Observer，那么就可以通过subscribe()函数把两者关联起来了：    
```Java    
observable.subscribe(observer);//被观察者注册观察者    
```    
  
# 简化代码    
在以下这个例子中，Observable.just()发送一个消息然后完成，功能类似上面的代码。    
```Java    
Observable<String> observable = Observable.just("救命啊");    
```    
  
接下来，让我们处理Observer不必要的样板代码。如果我们不关心onSubscribe、onCompleted、onError的话，那么可以使用一个更简单的类来定义onNext()要完成什么功能：    
```Java    
new Consumer<String>() {    
   @Override    
   public void accept(String s) throws Exception {    
      Log.i("bqt", "【观察者收到被观察者的消息】onNext:" + s);    
   }    
};    
```    
  
可以用Consumer或Action来定义Observer的每一个部分，Observable.subscribe()函数能够处理一到四个参数，分别表示onNext()，onError()和onComplete()和onSubscribe函数。    
```Java    
public final Disposable subscribe(Consumer<? super T> onNext)    
public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError)    
public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete)    
```    
  
因此，上述代码可简化为：    
```Java    
Observable.just("救命啊").subscribe(new Consumer<String>() {    
   @Override    
   public void accept(String s) throws Exception {    
      Log.i("bqt", "【观察者收到被观察者的消息】onNext:" + s);    
   }    
});    
```    
  
使用lambdas表达式去掉丑陋的Consumer代码：    
```Java    
Observable.just("救命啊")    
   .subscribe(s -> Log.i("bqt", "【观察者收到被观察者的消息】onNext:" + s));    
```    
  
不仅如此，因为 just 方法可以接受多个不同类型的数据，所以我们可以对一系列数据同时进行处理：    
```Java    
Observable.just("救命啊", 1, true, 1.5f)    
   .subscribe(s -> "【观察者收到被观察者的消息】onNext:" + s));    
```    
  
# 介绍一个操作符：map    
让我们来点刺激的。假如我想把我的签名拼接到"Hello, world!"的输出中，一个可行的办法是改变Observable：    
```Java    
Observable.just("Hello, world! -包青天")  
   .subscribe(s -> System.out.println(s));    
```    
  
这么做存在两个问题：    
- 当你有权限控制Observable时这么做是可行的，但不能保证每次都可以这样，因为如果你使用的是别人的函数库呢？    
- 如果项目中在多个地方使用此Observable，但只在某一个地方需要增加签名，这时怎么办？    
  
你可能会想到，我们可以单独修改此Subscriber：    
```Java    
Observable.just("Hello, world!")    
   .subscribe(s -> System.out.println(s + " -包青天"));    
```    
  
用这个解决方案的话，虽然上面的问题解决了，但同样也存在一个重要的问题：    
> 因为我希望Subscribers应尽可能的轻量级，而不要耦合任何的业务逻辑(比如对Observable发送的数据做再处理)。    
从更概念化的层面上讲，` Subscribers只是用于被动响应的，而不是主动发送或处理消息的。`      
  
接下来我们将介绍如何解决消息转换的难题：` 使用Operators。`     
Operators在消息发送者Observable(被观察者)和消息消费者Subscriber(观察者)之间起到操纵消息的作用。RxJava拥有大量的opetators，map()这个operator可以用来将已被发送的消息转换成另外一种形式：    
```Java    
Observable.just("Hello, world!")    
      .map(new Function<String, String>() {    
         @Override    
         public String apply(String s) throws Exception {    
            return s + " -包青天";    
         }    
      })    
      .subscribe(s -> System.out.println(s));    
```    
  
使用lambdas表达式后：    
```Java    
Observable.just("Hello, world!")    
    .map(s -> s + " -包青天")    
    .subscribe(s -> System.out.println(s));    
```    
很酷吧？这里的map()操作符本质上是一个用于转换消息对象的Observable。我们可以级联调用任意多个的map()函数，一层一层地将初始消息转换成Subscriber需要的数据形式。    
  
# 对于 map 的更多用法    
map()函数有趣的一点是：它不需要发送和原始的Observable一样的数据类型。    
假如我的Subscriber不想直接输出原始的数据，而是想输出原始数据的hash值，可以这样写：    
```Java    
Observable.just("Hello, world!")    
    .map(s -> s.hashCode())    
    .subscribe(i -> System.out.println(Integer.toString(i)));    
```    
这样，我们的原始输入是字符串，但Subscriber最终收到的是Integer类型。    
  
正如说过的，我们希望Subscriber做尽量少的工作，所以我们还可以把hash值转换成字符串的操作移动到一个新的map()函数中：    
```Java    
Observable.just("Hello, world!")    
    .map(s -> s.hashCode())    
    .map(i -> Integer.toString(i))    
    .subscribe(s -> System.out.println(s));    
```    
给力吧！我们的Observable和Subscriber完全还是原来的代码，我们只是根据不同的需求在中间添加了一些变换的操作。    
  
类似的案例：    
```Java    
Observable.just(System.currentTimeMillis())    
        .map(time -> time + 1000 * 60 * 60)//改变时间的值    
        .map(time -> new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(time)))    
        .subscribe(string -> Log.i("bqt", string));    
```    
  
# 对 rx 设计理念的理解    
上面只是一个简单的例子，但其中有几个思想你需要理解：    
  
第一：通过上面的Observable和Subscriber，你能完成任何你想做的事情，也就是说这是一个可以处理任何事情的通用的框架。    
- 你的Observable可以是一个数据库查询，Subscriber获得查询结果然后将其显示在屏幕上。    
- 你的Observable可以是屏幕上的一个点击，Subscriber响应该事件。    
- 你的Observable可以从网络上读取一个字节流，Subscriber将其写入本地磁盘中。    
  
第二：Observable和Subscriber与它们之间的一系列转换步骤是相互独立的。    
- 我们可以在消息发送者Observable和消息消费者Subscriber之间加入任意个你想要的map()函数，这个系统是高度可组合的，它很容易对数据进行操纵。    
- 只要operators符合输入输出的数据类型，那么我可以得到一个无穷尽的调用链。    
  
结合这两个概念，你可以看到一个有极大潜能的系统。并且我们还只是介绍了RxJava中的其中一个operator，实际上RxJava中还定义了大量的其他的operators，在你深入了解后，你会深深感叹：卧槽，你牛逼。    
  
2018-9-18  
