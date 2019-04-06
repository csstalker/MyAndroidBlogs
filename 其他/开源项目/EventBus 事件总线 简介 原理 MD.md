| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
EventBus 事件总线 简介 原理 MD  
***  
目录  
===  

- [EventBus](#EventBus)
	- [简介](#简介)
	- [常用API](#常用API)
	- [更多介绍](#更多介绍)
	- [3.0 版本的重大变更](#30-版本的重大变更)
- [原理](#原理)
	- [注册 register](#注册-register)
		- [findSubscriberMethods](#findSubscriberMethods)
		- [suscribe 方法](#suscribe-方法)
	- [发布 post](#发布-post)
	- [粘性事件 sticky](#粘性事件-sticky)
  
# EventBus  
## 简介  
[EventBus](https://github.com/greenrobot/EventBus)  
  
EventBus is a `publish/subscribe` event bus for `Android and Java`.  
![](https://github.com/greenrobot/EventBus/raw/master/EventBus-Publish-Subscribe.png)  
  
```  
implementation 'org.greenrobot:eventbus:3.1.1'  
```  
  
**可能遇到的收不到消息的一些原因**  
- 方法名不对。旧版本要求必须以onEvent开头，新版本要求使用`@Subscribe`注解  
- 参数不对。参数只能有一个，且不能是基本类型  
- 可能没有注册。发送不需要注册，接收需要注册。父类如果注册的话，子类调用super方法后不能再注册  
- 注册的位置可能不对。试一下改为在onCreate方法中注册，在onDestroy中取消  
- 可能你的onEvent方法运行时有错误，比如空指针，此时EventBus不会提示任何异常，这会让你误以为是没有回调到  
- 如果当前在BActivity，那么在BActivity中发了一条消息，在AActivity中收到消息后是不能显示吐司的  
  
## 常用API  
```java  
public void register(Object subscriber)  
public synchronized void unregister(Object subscriber)  
  
public synchronized boolean isRegistered(Object subscriber)  
public boolean hasSubscriberForEvent(Class<?> eventClass)  
  
public void post(Object event)  
public void postSticky(Object event)  
  
public void removeAllStickyEvents()  
public <T> T removeStickyEvent(Class<T> eventType)  
public boolean removeStickyEvent(Object event)  
public <T> T getStickyEvent(Class<T> eventType)  
  
@Subscribe(threadMode = ThreadMode.MAIN)   
public void onMessageEvent(MessageEvent event) {/* Do something */};  
```  
  
注意：Eventbus的发送消息和消息处理是和Eventbus实例有关的, 是无法跨进程传递消息的。  
  
PS：新版本的Eventbus在使用层面有比较大的改动，但是基本原理是完全一样的。  
  
## 更多介绍  
**组成部分**  
包含4个成分：发布者，订阅者，事件，总线。  
关系：订阅者订阅事件到总线，发送者发布事件；订阅者可以订阅多个事件，发送者可以发布任何事件，发布者同时也可以是订阅者。  
注册订阅者：`EventBus.getDefault().register(this);`这个方法通常在onCreate方法中进行注册。  
解绑订阅者：`EventBus.getDefault().unregister(this);`这个方法通常在onDestroy方法中进行解绑。  
发布者是不需要注册的，只有订阅者才需要注册。  
  
**约定的收到事件要执行的方法(已过时)**  
要求：方法名必须以【onEvent】开头，必须为非静态、【public】权限、有且仅有一个参数  
- `onEvent`：如果使用onEvent作为订阅函数，那么该事件在哪个线程发布出来的，onEvent就会在这个线程中运行，也就是说发布事件和接收事件线程在同一个线程。使用这个方法时，在onEvent方法中不能执行耗时操作，如果执行耗时操作容易导致事件分发延迟。  
- `onEventMainThread`：如果使用onEventMainThread作为订阅函数，那么不论事件是在哪个线程中发布出来的，onEventMainThread都会在UI线程中执行，接收事件就会在UI线程中运行。这个在Android中是非常有用的，因为在Android中只能在UI线程中更新UI，所以在onEvnetMainThread方法中是不能执行耗时操作的。  
- `onEventBackground`：如果使用onEventBackgrond作为订阅函数，那么如果事件是在UI线程中发布出来的，那么onEventBackground就会在子线程中运行，如果事件本来就是子线程中发布出来的，那么onEventBackground函数直接在该子线程中执行。  
- `onEventAsync`：使用这个函数作为订阅函数，那么无论事件在哪个线程发布，都会创建新的子线程在执行onEventAsync。  
  
注意post方法的参数，EventBus是根据这四个方法的参数来决定哪个类接收事件的，发布者的参数和某个订阅者这四个方法的参数一样，则执行这个订阅者的这个方法。  
  
当订阅者收到事件后，就会自动执行上面这四个方法，如果写了多个，则都会执行。  
  
**粘性事件**  
若收不到事件，比如在Activity的oncreat中发送事件，在Fragment中的oncreat中接收事件，可能是因为发送事件时，接收事件的Fragment还未注册EventBus。  
这种情况可以采用发送`Sticky Event`粘性事件 来解决  
这样注册之前发送的sticky事件的最近的一个会保存在内存中，错过这个事件的发送的情况下，也可以通过getStickyEvent收到。  
获取时使用：`StickyEventBusBeans bean = EventBus.getDefault().getStickyEvent(StickyEventBusBeans.class);`  
还可以移除：`EventBus.getDefault().removeStickyEvent(bean);`  
  
**EventBus带来的好处和引入的问题**  
好处比较明显，就是独立出一个发布订阅模块，调用者可以通过使用这个模块，屏蔽一些线程切换问题，简单地实现发布订阅功能。  
坏处可能比较隐晦，但这些需要足够引起我们的重视  
- 大量的滥用，将导致逻辑的分散，`出现问题后很难定位`  
- 没办法实现强类型，在编译的时候就发现问题，（Otto实现了这个，但性能有问题）  
- 在实现上通过一个很`弱的协议`，比如onEventXXX（XXX表示ThreadModel），来实现线程的切换，代码可读性有些问题，IDE无法识别这些协议，`对IDE不友好`  
  
**EventBus 、 Otto 、 BroadcastReceiver三者比较**  
- 单从使用上看，`EventBus > Otto > BroadcastReceiver`，当然BroadcastReceiver作为系统内置组件，有一些前两者没有的功能  
- EventBus最简洁，Otto最符合Guava EventBus的设计思路， BroadcastReceiver最难使用。  
- EventBus规定onEvent方法固定作为订阅者接受事件的方法，应该是参考了`约定优于配置`的思想。  
- Otto使用注解定义订阅/发布者的角色，`@Subscribe`为订阅者，`@Produce`为发布者，方法名称可以自定义。  
- Otto为了性能，代码意图清晰，要求`@Subscribe`，`@Produce`方法必须定义在直接的作用类上，而不能定义在基类而被继承。  
- Otto要求发布者也需要register和unregister，而EventBus的发布者是不需要的。  
  
## 3.0 版本的重大变更  
**V3.0.0 (2016-02-04) Annotations**  
- Breaking change: switching from `subscriber method name` conventions to `annotations`  
- Using annotations, each `subscriber method` can set `sticky` behavior and `priority` individually  
- Annotation processor indexes annotation information for efficient subscriber registration on Android  
- Renamed package and artifact id to allow co-existence with libs using EventBus 2 internally  
  
Note: This is a breaking change release: there is no inter-op between EventBus versions 2 and 3; they can run in parallel though.  
  
突破性变化：从订阅者方法名称约定切换到注释  
使用注释，每个订阅者方法可以单独设置粘性行为和优先级  
注释处理器索引注释信息，以便在Android上进行有效的订户注册  
重命名包和工件ID以允许在内部使用EventBus 2与lib共存  
  
注意：这是一个重大变化版本：EventBus版本2和3之间没有互操作; 它们可以并行运行。  
  
# 原理  
调用【register】方法后，EventBus会把当前类中【onEvent开头的方法】，存入一个【单例】内部维持着一个【map】中，KEY就是【方法中的参数】，Value就是此【onEvent开头的方法】；而后在post的时候，EventBus就会根据我们传入的参数的类型去map中查找匹配的方法(可能有很多个)，然后逐个【反射】调用这些方法。  
  
PS：新版本的Eventbus在使用层面有比较大的改动，但是基本原理是完全一样的。  
  
## 注册 register  
简单说，`EventBus.getDefault().register(this)`所做的事情就是：在当前类中遍历所有的方法，找到`onEvent`开头的然后进行存储。  
  
EventBus.getDefault()其实就是个单例，register公布给我们使用的有4个，本质上调用的是同一个：  
```java  
public void register(Object subscriber) { register(subscriber, DEFAULT_METHOD_NAME, false, 0); }  
public void register(Object subscriber, int priority) { register(subscriber, DEFAULT_METHOD_NAME, false, priority); }  
public void registerSticky(Object subscriber) { register(subscriber, DEFAULT_METHOD_NAME, true, 0);    }  
public void registerSticky(Object subscriber, int priority) { register(subscriber, DEFAULT_METHOD_NAME, true, priority); }  
```  
  
```java  
private synchronized void register(Object subscriber, String methodName, boolean sticky, int priority) {  
    List<SubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriber.getClass(), methodName);  
    for (SubscriberMethod subscriberMethod : subscriberMethods) {  
        subscribe(subscriber, subscriberMethod, sticky, priority);  
    }  
}  
```  
  
四个参数  
- subscriber：需要扫描的类，也就是我们代码中常见的this  
- methodName：用于确定扫描什么开头的方法，这个是写死的"onEvent"，可见我们的类中都是以这个开头  
- sticky：是否是粘性事件，最后面再单独说  
- priority：优先级，优先级越高，在调用的时候会越先调用  
  
### findSubscriberMethods  
首先会调用findSubscriberMethods方法，实际是去`遍历该类内部所有方法`，然后根据methodName去匹配，匹配成功的就封装成`SubscriberMethod`对象，最后存储到一个`List`并返回。  
  
```java  
List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass, String eventMethodName) {  
    String key = subscriberClass.getName() + '.' + eventMethodName;  
    List<SubscriberMethod> subscriberMethods;  
    synchronized (methodCache) {  
        subscriberMethods = methodCache.get(key);  
    }  
    if (subscriberMethods != null) {  
        return subscriberMethods;  
    }  
    subscriberMethods = new ArrayList<SubscriberMethod>();  
    Class<?> clazz = subscriberClass;  
    HashSet<String> eventTypesFound = new HashSet<String>();  
    StringBuilder methodKeyBuilder = new StringBuilder();  
    while (clazz != null) {  
        String name = clazz.getName();  
        if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {  
            // Skip system classes, this just degrades performance  降低性能  
            break;  
        }  
        // Starting with EventBus 2.2 we enforced 强制 methods to be public (might change with annotations again)    
        Method[] methods = clazz.getMethods();//去得到所有的方法  
        for (Method method : methods) {//开始遍历每一个方法，去匹配封装  
            String methodName = method.getName();  
            if (methodName.startsWith(eventMethodName)) {//分别判断了是否以onEvent开头，是否是public且非static和abstract方法，是否是一个参数。如果都符合，才进入封装的部分。  
                int modifiers = method.getModifiers();  
                if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {  
                    Class<?>[] parameterTypes = method.getParameterTypes();  
                    if (parameterTypes.length == 1) {  
                        String modifierString = methodName.substring(eventMethodName.length());  
                        ThreadMode threadMode;  
                        if (modifierString.length() == 0) {//根据方法的后缀，来确定threadMode，threadMode是个枚举类型  
                            threadMode = ThreadMode.PostThread;  
                        } else if (modifierString.equals("MainThread")) {  
                            threadMode = ThreadMode.MainThread;  
                        } else if (modifierString.equals("BackgroundThread")) {  
                            threadMode = ThreadMode.BackgroundThread;  
                        } else if (modifierString.equals("Async")) {  
                            threadMode = ThreadMode.Async;  
                        } else {  
                            if (skipMethodVerificationForClasses.containsKey(clazz)) {  
                                continue;  
                            } else {  
                                throw new EventBusException("Illegal onEvent method, check for typos: " + method);  
                            }  
                        }  
                        Class<?> eventType = parameterTypes[0];  
                        methodKeyBuilder.setLength(0);  
                        methodKeyBuilder.append(methodName);  
                        methodKeyBuilder.append('>').append(eventType.getName());  
                        String methodKey = methodKeyBuilder.toString();  
                        if (eventTypesFound.add(methodKey)) {  
                            // Only add if not already found in a sub class    
                            subscriberMethods.add(new SubscriberMethod(method, threadMode, eventType));//将method, threadMode, eventType传入构造SubscriberMethod对象，添加到List  
                        }  
                    }  
                } else if (!skipMethodVerificationForClasses.containsKey(clazz)) {  
                    Log.d(EventBus.TAG, "Skipping method (not public, static or abstract): " + clazz + "." + methodName);  
                }  
            }  
        }  
        clazz = clazz.getSuperclass();//会扫描所有的父类，不仅仅是当前类  
    }  
    if (subscriberMethods.isEmpty()) {  
        throw new EventBusException("Subscriber " + subscriberClass + " has no public methods called " + eventMethodName);//最常见的这个异常是在这里抛出来的  
    } else {  
        synchronized (methodCache) {  
            methodCache.put(key, subscriberMethods);  
        }  
        return subscriberMethods;  
    }  
}  
```  
  
### suscribe 方法  
然后for循环扫描到的方法，然后调用suscribe方法  
```java  
// Must be called in synchronized block    
private void subscribe(Object subscriber, SubscriberMethod subscriberMethod, boolean sticky, int priority) {  
    subscribed = true;  
    Class<?> eventType = subscriberMethod.eventType; //eventType是我们方法参数的Class，是Map的key  
    //根据subscriberMethod.eventType，去subscriptionsByEventType查找一个CopyOnWriteArrayList<Subscription> ，如果没有则创建。  
    CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType); // Map的value，这里的subscriptionsByEventType是个Map，这个Map其实就是EventBus存储方法的地方  
    Subscription newSubscription = new Subscription(subscriber, subscriberMethod, priority);//Map的value中保存的是Subscription  
    if (subscriptions == null) {  
        subscriptions = new CopyOnWriteArrayList<Subscription>();  
        subscriptionsByEventType.put(eventType, subscriptions);  
    } else {  
        for (Subscription subscription : subscriptions) {  
            if (subscription.equals(newSubscription)) {  
                throw new EventBusException("Subscriber " + subscriber.getClass() + " already registered to event " + eventType);  
            }  
        }  
    }  
    // Starting with EventBus 2.2 we enforced methods to be public (might change with annotations again)    
    // subscriberMethod.method.setAccessible(true);    
    int size = subscriptions.size();  
    for (int i = 0; i <= size; i++) { //按照优先级添加newSubscription，优先级越高，会插到在当前List的前面。  
        if (i == size || newSubscription.priority > subscriptions.get(i).priority) {  
            subscriptions.add(i, newSubscription);  
            break;  
        }  
    }  
    List<Class<?>> subscribedEvents = typesBySubscriber.get(subscriber); //根据subscriber存储它所有的eventType  
    if (subscribedEvents == null) {  
        subscribedEvents = new ArrayList<Class<?>>();  
        typesBySubscriber.put(subscriber, subscribedEvents);  
    }  
    subscribedEvents.add(eventType);  
    if (sticky) { //判断sticky；如果为true，从stickyEvents中根据eventType去查找有没有stickyEvent，如果有则立即发布去执行  
        Object stickyEvent;  
        synchronized (stickyEvents) {  
            stickyEvent = stickyEvents.get(eventType);  
        }  
        if (stickyEvent != null) {  
            // If the subscriber is trying to abort the event, it will fail (event is not tracked in posting state)    
            // --> Strange corner case, which we don't take care of here.    
            postToSubscription(newSubscription, stickyEvent, Looper.getMainLooper() == Looper.myLooper());  
        }  
    }  
}  
```  
  
你只要记得一件事：扫描了所有的方法，把匹配的方法最终保存在`subscriptionsByEventType(Map，key：eventType ； value：CopyOnWriteArrayList<Subscription> )`中；  
eventType是我们方法参数的Class，Subscription中则保存着subscriber, subscriberMethod, priority；包含了执行改方法所需的一切。  
  
## 发布 post  
调用很简单，一句话，你也可以叫发布，只要把这个param发布出去，EventBus会`在它内部存储的方法中，进行扫描，找到参数匹配的，就使用反射进行调用`。  
```java  
/** Posts the given event to the event bus. */  
public void post(Object event) {  
    PostingThreadState postingState = currentPostingThreadState.get();  
    List<Object> eventQueue = postingState.eventQueue;  
    eventQueue.add(event);  
    if (postingState.isPosting) {//防止每次post都会去调用整个队列  
        return;  
    } else {  
        postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper();//判断当前是否是UI线程（我去，竟然是通过这种方式判断的！）  
        postingState.isPosting = true;  
        if (postingState.canceled) {  
            throw new EventBusException("Internal error. Abort state was not reset");  
        }  
        try {  
            while (!eventQueue.isEmpty()) {//遍历队列中的所有的event，调用postSingleEvent方法。  
                postSingleEvent(eventQueue.remove(0), postingState);  
            }  
        } finally {  
            postingState.isPosting = false;  
            postingState.isMainThread = false;  
        }  
    }  
}  
```  
  
```java  
private void postSingleEvent(Object event, PostingThreadState postingState) throws Error {  
    //得到event当前对象的Class，以及父类和接口的Class类型；主要用于匹配，比如你传入Dog extends Animal，他会把Animal也装到该List中。  
    Class<? extends Object> eventClass = event.getClass();  
    List<Class<?>> eventTypes = findEventTypes(eventClass);  
    boolean subscriptionFound = false;  
    int countTypes = eventTypes.size();  
    for (int h = 0; h < countTypes; h++) { //遍历所有的Class，到subscriptionsByEventType去查找subscriptions，register时我们就是把方法存在了这个Map里  
        Class<?> clazz = eventTypes.get(h);  
        CopyOnWriteArrayList<Subscription> subscriptions;  
        synchronized (this) {  
            subscriptions = subscriptionsByEventType.get(clazz);  
        }  
        if (subscriptions != null && !subscriptions.isEmpty()) {  
            for (Subscription subscription : subscriptions) {  
                postingState.event = event;  
                postingState.subscription = subscription;  
                boolean aborted = false;  
                try {  
                    postToSubscription(subscription, event, postingState.isMainThread); //反射执行方法  
                    aborted = postingState.canceled;  
                } finally {  
                    postingState.event = null;  
                    postingState.subscription = null;  
                    postingState.canceled = false;  
                }  
                if (aborted) {  
                    break;  
                }  
            }  
            subscriptionFound = true;  
        }  
    }  
    if (!subscriptionFound) {  
        Log.d(TAG, "No subscribers registered for event " + eventClass);  
        if (eventClass != NoSubscriberEvent.class && eventClass != SubscriberExceptionEvent.class) {  
            post(new NoSubscriberEvent(this, event));  
        }  
    }  
}  
```  
  
```java  
private void postToSubscription(Subscription subscription, Object event, boolean isMainThread) {  
    switch (subscription.subscriberMethod.threadMode) { //根据threadMode去判断应该在哪个线程去执行该方法  
    case PostThread:  
        invokeSubscriber(subscription, event); //直接【反射】调用；也就是说在当前的线程直接调用该方法  
        break;  
    case MainThread:  
        if (isMainThread) {//如果是UI线程，则直接调用  
            invokeSubscriber(subscription, event);  
        } else {//否则把当前的方法加入到队列，然后直接通过【handler】去发送一个消息，并在handler的handleMessage中去执行我们的方法  
            mainThreadPoster.enqueue(subscription, event);  
        }  
        break;  
    case BackgroundThread:   
        if (isMainThread) {//如果是UI线程，则将任务加入到后台的一个【队列】，最终由Eventbus中的一个【线程池】去调用executorService = Executors.newCachedThreadPool();  
            backgroundPoster.enqueue(subscription, event);  
        } else {//如果当前非UI线程，则直接调用  
            invokeSubscriber(subscription, event);  
        }  
        break;  
    case Async: //将任务加入到后台的一个【队列】，最终由Eventbus中的一个【线程池】去调用；线程池与BackgroundThread用的是【同一个】  
        asyncPoster.enqueue(subscription, event); //BackgroundThread中的任务，【一个接着一个去调用】，中间使用了一个布尔型变量进行的控制。Async则会【动态控制并发】  
        break;  
    default:  
        throw new IllegalStateException("Unknown thread mode: " + subscription.subscriberMethod.threadMode);  
    }  
}  
```  
  
## 粘性事件 sticky  
介绍了register和post；大家获取还能想到一个词sticky，在register中，如果sticky为true，会去stickyEvents去查找事件，然后立即去post；  
那么这个stickyEvents何时进行保存事件呢？  
其实evevntbus中，除了post发布事件，还有一个方法也可以：  
```java  
public void postSticky(Object event) {  
    synchronized (stickyEvents) {  
        stickyEvents.put(event.getClass(), event);  
    }  
    // Should be posted after it is putted, in case the subscriber wants to remove immediately    
    post(event);  
}  
```  
  
和post功能类似，但是会把方法存储到`stickyEvents`中去；  
大家再去看看EventBus中所有的public方法，无非都是一些状态判断，获取事件，移除事件的方法；没什么好介绍的，基本见名知意。  
  
2016-9-7  
