| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
***  
目录  
===  

- [LiveData](#LiveData)
	- [LiveData 简介](#LiveData-简介)
	- [LiveData 功能概览](#LiveData-功能概览)
	- [LiveData 的优点](#LiveData-的优点)
	- [LiveData 使用步骤](#LiveData-使用步骤)
	- [Transformations](#Transformations)
		- [map](#map)
		- [switchMap](#switchMap)
		- [MediatorLiveData](#MediatorLiveData)
- [ViewModel](#ViewModel)
	- [基本介绍](#基本介绍)
	- [适用场景](#适用场景)
	- [ViewModel 的 onCleared 调用时机](#ViewModel-的-onCleared-调用时机)
	- [Fragment 的 setRetainInstance 方法](#Fragment-的-setRetainInstance-方法)
- [使用案例](#使用案例)
	- [MutableLiveData 简单使用案例](#MutableLiveData-简单使用案例)
	- [扩展 LiveData](#扩展-LiveData)
		- [LiveData](#LiveData)
		- [Manager](#Manager)
		- [案例说明](#案例说明)
	- [MediatorLiveData 案例](#MediatorLiveData-案例)
	- [ViewModel 案例](#ViewModel-案例)
  
# LiveData  
  
[官方文档](https://developer.android.google.cn/topic/libraries/architecture/livedata)  
官方公众号介绍文章：[正式发布 Android 架构组件 1.0 稳定版 | 附带中文介绍视频](https://mp.weixin.qq.com/s/9rC_5GhdAA_EMEbWKJT5vQ?)    
  
## LiveData 简介  
  
LiveData 是一个`可被观察的数据持有者`类。与常规的Observable不同，LiveData能意识到应用程序组件的`生命周期变化`，这意味着它能遵守Activity、Fragment、Service等组件的生命周期。这种意识确保LiveData只更新处于`活跃状态`的应用程序组件Observer。  
  
如果一个Observer的生命周期处于STARTED或RESUMED状态，那么LiveData将认为这个Observer处于活跃状态。 LiveData仅通知活跃的Observer去更新UI。 非活跃状态的Observer，即使订阅了LiveData，也不会收到更新的通知。  
  
结合一个实现了`LifecycleOwner`接口的对象，你能注册一个Observer。这种结合关系使得当`具有生命周期的对象`的状态变为DESTROYED时，Observer将被取消订阅。 这对于Activity和Fragment尤其有用，因为它们可以安全地订阅LiveData对象，而不必担心内存泄漏 - 当Activity和Fragment生命周期为DESTROYED时，它们立即会被取消订阅。  
  
简单地说，LiveData是一个数据持有类。它具有以下特点：  
- 数据可以被观察者订阅；  
- 能够感知组件（Fragment、Activity、Service）的生命周期；  
- 只有在组件出于激活状态（STARTED、RESUMED）才会通知观察者有数据更新；  
  
## LiveData 功能概览  
  
LiveData 也是一个观察者模型，但是它是一个与 `Lifecycle` 绑定了的 Subject，也就是说，只有当 UI 组件处于 ACTIVE 状态时，它的 Observer 才能收到消息，否则会自动切断订阅关系，不用再像 RxJava 那样通过 `CompositeDisposable` 来手动处理。  
  
LiveData 的数据类似 EventBus 的 `sticky event`，不会被消费掉，只要有数据，它的 observer 就会收到通知。如果我们要把 LiveData 用作事件总线，还需要做一些定制，Github 上搜 `SingleLiveEvent` 可以找到源码实现。  
  
我们没法直接修改 LiveData 的 value，因为它是不可变的（immutable），可变（mutable）版本是 `MutableLiveData`，通过调用 `setValue`（主线程）或 `postValue`（非主线程）可以修改它的 value。如果我们对外暴露一个 LiveData，但是不希望外部可以改变它的值，可以用如下技巧实现：  
  
> 内部用 `MutableLiveData` ，可以修改值，对外暴露成 `LiveData` 类型，只能获取值，不能修改值。  
  
LiveData 有一个实现了`中介者模式`的子类 —— `MediatorLiveData`，它可以把多个 LiveData 整合成一个，只要任何一个 LiveData 有数据变化，它的观察者就会收到消息。  
  
综上，我们汇总一下 LiveData 的使用场景：  
*   **LiveData** - immutable 版本  
*   **MutableLiveData** - mutable 版本  
*   **MediatorLiveData** - 可汇总多个数据源  
*   **SingleLiveEvent** - 事件总线  
  
LiveData 只存储最新的数据，虽然用法类似 RxJava2 的 Flowable，但是它不支持背压（backpressure），所以不是一个流（stream），利用 `LiveDataReactiveStreams` 我们可以实现 Flowable 和 LiveData 的互换。  
  
如果把异步获取到的数据封装成 Flowable，通过 `toLiveData()` 方法转换成 LiveData，既利用了 RxJava 的线程模型，还消除了 Flowable 与 UI Controller 生命周期的耦合关系，借助 `DataBinding` 再把 LiveData 绑定到 xml UI 元素上，数据驱动 UI，妥妥的响应式。于是一幅如下模样的数据流向图就被勾勒了出来：  
  
![](index_files/f9cd2566-368b-4e98-816b-81b8cc4cb5ea.png)  
  
> 图中右上角的 Local Data 是 AAC 提供的另一个强大武器 —— ORM 框架 Room。  
  
## LiveData 的优点  
  
在项目中使用LiveData，会有以下优点:  
- `确保UI符合数据状态`：LiveData遵循观察者模式。 当生命周期状态改变时，LiveData会向Observer发出通知。 您可以把更新UI的代码合并在这些Observer对象中。不必去考虑导致数据变化的各个时机，每次数据有变化，Observer都会去更新UI。  
- `没有内存泄漏`：Observer会绑定具有生命周期的对象，并在这个绑定的对象被销毁后自行清理。  
- `不会因停止Activity而发生崩溃`：如果Observer的生命周期处于非活跃状态，例如在后退堆栈中的Activity，就不会收到任何LiveData事件的通知。  
- `不需要手动处理生命周期`：UI组件只需要去观察相关数据，不需要手动去停止或恢复观察。LiveData会进行自动管理这些事情，因为在观察时，它会感知到相应组件的生命周期变化。  
- `始终保持最新的数据`：如果一个对象的生命周期变到非活跃状态，它将在再次变为活跃状态时接收最新的数据。 例如，后台Activity在返回到前台后立即收到最新数据。  
- `正确应对配置更改`：如果一个Activity或Fragment由于配置更改（如设备旋转）而重新创建，它会立即收到最新的可用数据。  
- `共享资源`：您可以使用单例模式扩展LiveData对象并包装成系统服务，以便在应用程序中进行共享。LiveData对象一旦连接到系统服务，任何需要该资源的Observer都只需观察这个LiveData对象。  
  
## LiveData 使用步骤  
  
按照以下步骤使用LiveData对象：  
- 创建一个LiveData的实例来保存特定类型的数据，这通常在ViewModel类中完成。  
- 创建一个定义了`onChanged()`方法的`Observer`对象，当LiveData对象保存的数据发生变化时，onChanged()方法可以进行相应的处理。您通常在UI控制器（如Activity或Fragment）中创建Observer对象。  
- 使用`observe()`方法将Observer对象注册到LiveData对象。observe()方法还需要一个`LifecycleOwner`对象作为参数。Observer对象订阅了LiveData对象，便会在数据发生变化时发出通知。您通常需要UI控制器（如Activity或Fragment）中注册Observer对象。  
  
> Note:您可以使用`observeForever(Observer)`方法注册一个没有关联LifecycleOwner对象的Observer。在这种情况下，Observer被认为始终处于活动状态，因此当有数据变化时总是会被通知。您可以调用`removeObserver(Observer)`方法移除这些Observer。  
  
当你更新LiveData对象中存储的数据时，所有注册了的Observer，只要`所绑定的LifecycleOwner处于活动状态`，就会被触发通知。  
  
**创建LiveData对象**  
LiveData是一个`包装器`，可用于任何数据，包括实现Collections的对象，如List。一个LiveData对象通常存储在`ViewModel`对象中，并通过getter方法访问。  
  
> 确保在ViewModel而不是Activity或Fragment中保存用来更新UI的LiveData对象，原因如下：  
> - 避免臃肿的Activity和Fragment。这些UI控制器`负责显示数据而不是保存数据状态`。  
> - 将LiveData实例与特定Activity或Fragment实例分离，这将使得LiveData对象在`配置更改`后仍然存活。  
  
**观察LiveData对象**  
在大多数情况下，出于以下原因，应用程序组件的`onCreate()`方法是开始观察LiveData对象的最佳位置：  
- 确保系统不会从Activity或Fragment的onResume()方法中进行多余的调用。  
- 确保Activity或Fragment一旦变为活动状态时，就有可展示的数据。当应用程序组件处于STARTED状态，它就需从它所观察的LiveData对象中接收到最新的值。所以我们需要在一开始就设置好观察。  
  
> 通常情况下，LiveData只在数据有变化时，给活跃的Observer进行通知。此行为的一个例外是，Observer在`从非活跃状态变为活跃状态`时也会收到通知。并且，如果Observer第二次从非活跃状态变为活跃状态，则只有`在自上一次变为活跃状态以来该数据发生变化时`才会接收到更新。  
  
**更新LiveData对象**  
LiveData没有公用的方法来更新存储的数据，`MutableLiveData`类暴露公用的setValue(T)和postValue(T)方法，如果需要编辑存储在LiveData对象中的值，必须使用这两个方法。  
  
通常在ViewModel中使用MutableLiveData，然后ViewModel仅向Observer公开不可变的LiveData对象。  
  
> Note: 必须要从主线程调用setValue(T) 方法来更新LiveData 对象，如果代码在工作线程中执行, 你可以使用postValue(T) 方法来更新LiveData对象.  
  
## Transformations  
您可能希望先转换存储在 LiveData 对象中的值，然后再将其分派给 Observer，或者您可能需要根据一个 LiveData 实例的值返回不同的 LiveData 实例。Lifecycle 包提供了 Transformations 类，提供了支持这些使用场景的方法。  
  
要实现自己的转换，您可以使用MediatorLiveData类，该类监听其他LiveData对象并处理它们发出的事件。MediatorLiveData将其状态正确地传播到源LiveData对象。  
  
### map  
  
map()使用一个函数来转换存储在LiveData对象中的值，并向下传递转换后的值。  
  
**源码**  
```java  
@MainThread  
public static <X, Y> LiveData<Y> map(LiveData<X> source, final Function<X, Y> func) {  
    final MediatorLiveData<Y> result = new MediatorLiveData<>(); //创建了一个中介者  
    result.addSource(source, x -> result.setValue(func.apply(x))); //当源LiveData数据改变时通知中介，中介更改数据后通知观察者  
    return result; //返回的就是中介  
}  
```  
  
Applies the given function on the main thread to each value emitted by *source* LiveData and returns LiveData, which emits resulting values.  
> 将主线程上的给定函数应用于源LiveData发出的每个值，并返回发送结果值的LiveData。  
  
The given function *func* will be executed on the main thread.  
> 给定的函数func将在主线程上执行。  
  
**使用案例**  
```java  
Transformations.map(MyLiveData.get(), s -> 100 + s.length()) //使用map进行数据转换  
    .observe(this, i -> Log.i("bqt", "【观察到的值】" + i));  
```  
  
### switchMap  
  
switchMap()与map()类似，不同的是，其要求传递给switchMap()的函数必须`返回一个LiveData对象`。  
  
switchMap主要会进行`重新绑定`的操作，如果从switchMapFunction中获得的LiveData发生了变化，则会重新进行observe，从而避免上文提到的重复observe的问题  
  
**源码**  
```java  
@MainThread  
public static <X, Y> LiveData<Y> switchMap(LiveData<X> trigger, final Function<X, LiveData<Y>> func) {  
    final MediatorLiveData<Y> result = new MediatorLiveData<>();  
    result.addSource(trigger, new Observer<X>() {  
        LiveData<Y> mSource;  
  
        @Override  
        public void onChanged(@Nullable X x) {  
            LiveData<Y> newLiveData = func.apply(x); //转换为新的源  
            if (mSource == newLiveData) return; //和旧的源是同一个源，没必要移除然后添加  
            if (mSource != null) result.removeSource(mSource); //移除旧的源  
            mSource = newLiveData;  
            if (mSource != null) result.addSource(mSource, result::setValue); //添加新的源  
        }  
    });  
    return result;  
}  
```  
  
此机制允许较低级别的应用程序创建按需延迟计算的LiveData对象。ViewModel对象可以很容易地获得对LiveData对象的引用，然后在其上定义转换规则。  
  
**案例**  
```java  
Transformations.switchMap(MyLiveData.get(), s -> {  
    MutableLiveData<Integer> liveData = ViewModelProviders.of(this).get(MyViewModel.class).getMutableLiveData();  
    liveData.observe(this, i -> Log.i("bqt", "【观察到的值1】" + i));  
    liveData.setValue(1000 + new Random().nextInt(100));  
    return liveData;  
})  
    .observe(this, i -> Log.i("bqt", "【观察到的值2】" + i));  
```  
  
### MediatorLiveData  
  
我们从源码中可以看到两个方法都使用了`MediatorLiveData<T>`这个类型。这个类和`MutableLiveData<T>`一样都继承自`LiveData<T>`，主要是帮助我们解决这样一种case：我们有多个LiveData实例，这些LiveData的value发生变更时都需要通知某一个相同的LiveData。  
  
我们还可以在监听value变化的过程中根据需要取消对某个LiveData的observe  
  
> LiveData subclass which may observe other LiveData objects and react on做出反应 `OnChanged events` from them. This class correctly正确地 propagates传播 its active/inactive states down to source LiveData objects.  
  
# ViewModel  
  
[官方文档](https://developer.android.google.cn/topic/libraries/architecture/viewmodel.html)  
https://blog.csdn.net/qq_24442769/article/details/79426609  
  
ViewModel类是被设计用来`以可感知生命周期的方式存储和管理 UI 相关数据`，ViewModel中数据会一直存活即使 activity configuration发生变化，比如横竖屏切换的时候。  
  
```groovy  
implementation "android.arch.lifecycle:extensions:1.1.1" //ViewModelProviders 依赖  
annotationProcessor "android.arch.lifecycle:compiler:1.1.1"  
```  
  
## 基本介绍  
  
ViewModel类的设计目的是以一种关注生命周期的方式存储和管理与UI相关的数据。  
  
LiveData 是一个可以感知 Activity 、Fragment生命周期的数据容器。当 LiveData 所持有的数据改变时，它会通知相应的界面代码进行更新。同时，LiveData 持有界面代码 Lifecycle 的引用，这意味着它会在界面代码（LifecycleOwner）的生命周期处于 started 或 resumed 时作出相应更新，而在 LifecycleOwner 被销毁时停止更新。  
  
> 不用手动控制生命周期，不用担心内存泄露，数据变化时会收到通知。  
  
ViewModel 将`视图的数据和逻辑`从具有生命周期特性的实体（如 Activity 和 Fragment）中剥离开来，直到关联的 Activity 或 Fragment 完全销毁时，ViewModel 才会随之消失，也就是说，即使在旋转屏幕导致 Fragment 被重新创建等事件中，视图数据依旧会被保留。ViewModels 不仅消除了常见的生命周期问题，而且可以帮助构建更为模块化、更方便测试的用户界面。  
  
> 为Activity 、Fragment存储数据，在当前Activity的Fragment之间实现数据共享  
  
ViewModel职责是为Activity或Fragment管理、请求数据，`具体数据请求逻辑不应该写在ViewModel中`，否则ViewModel的职责会变得太重，此处需要一个引入一个`Repository`，负责数据请求相关工作。  
  
![](index_files/2bf9825a-b309-4552-8cfc-9e46538b0c72.png)  
![](index_files/eb233143-04b1-4c76-ab9b-6bdb5620e40b.png)  
![](index_files/5316c3bb-c22d-4da9-b978-dfb9c688f402.png)  
  
## 适用场景  
  
- ViewModel可以用于Activity内不同Fragment的交互，也可以用作Fragment之间一种解耦方式。  
- ViewModel也可以负责处理部分Activity/Fragment与应用其他模块的交互。  
- ViewModel生命周期（以Activity为例）起始于Activity第一次onCreate()，结束于Activity最终finish时。  
  
**数据持久化**  
  
我们知道在屏幕旋转的时候会经历 activity 的销毁与重新创建，这里就涉及到数据保存的问题，显然重新请求或加载数据是不友好的。在 ViewModel 出现之前我们可以用 activity 的onSaveInstanceState()机制保存和恢复数据，但缺点很明显，onSaveInstanceState只适合保存少量的可以被序列化、反序列化的数据，假如我们需要保存是一个比较大的 bitmap list ，这种机制明显不合适。  
  
使用ViewModel的话，ViewModel会自动保留之前的数据并给新的Activity或Fragment使用，`直到当前Activity被系统销毁时`，Framework会调用ViewModel的`onCleared()`方法，我们可以在onCleared()方法中做一些资源清理操作。  
  
![](index_files/b9e043c9-ae06-4956-9afb-30254a20aaea.jpg)  
  
由图可知，ViewModel 生命周期是贯穿整个 activity 生命周期，包括 Activity 因旋转造成的重创建，直到 Activity 真正意义上销毁后才会结束。既然如此，用来存放数据再好不过了。  
  
**异步回调问题**  
  
通常我们 app 需要频繁异步请求数据，比如调接口请求服务器数据。当然这些请求的回调都是相当耗时的，之前我们在 Activity 或 fragment里接收这些回调。所以不得不考虑潜在的内存泄漏情况，比如 Activity 被销毁后接口请求才返回。处理这些问题，会给我们增添好多复杂的工作。  
  
但现在我们利用 ViewModel 处理数据回调，可以完美的解决此痛点。  
  
**分担 UI controller负担**  
  
从最早的 MVC 到目前流行的 MVP、MVVM，目的无非是 明确职责，分离 UI controller 负担。  
  
UI controller 比如 Activity 、Fragment 是设计用来渲染展示数据、响应用户行为、处理系统的某些交互。如果再要求他去负责加载网络或数据库数据，会让其显得臃肿和难以管理。所以为了简洁、清爽、丝滑，我们可以分离出数据操作的职责给 ViewModel。  
  
**Fragments 间共享数据**  
  
比如在一个 Activity 里有多个fragment，这fragment 之间需要做某些交互。我之前的做法是接口回调，需要统一在 Activity 里管理，并且不可避免的 fragment 之间还得互相持有对方的引用。仔细想想就知道这是很翔的一件事，耦合度高不说，还需要大量的容错判断（比如对方的 fragment 是否还活着）。  
  
使用ViewModel后会有如下好处：  
- Activity 不需要做任何事，甚至不知道这次交互，完美解耦。  
- Fragment 只需要与ViewModel交互，不需要知道对方 Fragment 的状态甚至是否存在，更不需要持有其引用。所有当对方 Fragment 销毁时，不影响本身任何工作。  
- Fragment 生命周期互不影响，甚至 fragment 替换成其他的 也不影响这个系统的运作。  
  
## ViewModel 的 onCleared 调用时机  
  
ViewModel 有一个onCleared方法，那么他是在何时调用的呢?  
  
```java  
public abstract class ViewModel {  
    /**  
     * This method will be called when this ViewModel is no longer used and will be destroyed.  
     * It is useful when ViewModel observes some data and you need to clear this subscription to prevent a leak of this ViewModel.  
     */  
    @SuppressWarnings("WeakerAccess")  
    protected void onCleared() {  
    }  
}  
```  
  
ViewModel 创建的时候提到过实例化了一个 `HolderFragment` 。并且实例化的时候通过上面 createHolderFragment 方法将其`fragmentManager.beginTransaction().add(holder, HOLDER_TAG).commitAllowingStateLoss();`，在 commit 之后，fragment 将会获得生命周期。  
  
我们看看其 onDestroy 方法：  
```java  
@Override  
public void onDestroy() {  
    super.onDestroy();  
    mViewModelStore.clear();  
}  
```  
  
在HolderFragment的onDestroy()方法中调用了ViewModelStore.clear()方法，在该方法中会调用ViewModel的onCleared()方法：  
```java  
/**  
 *  Clears internal storage and notifies ViewModels that they are no longer used.  
 */  
public final void clear() {  
    for (ViewModel vm : mMap.values()) {  
        vm.onCleared(); //调用ViewMode的onCleared方法  
    }  
    mMap.clear();  
}  
```  
  
## Fragment 的 setRetainInstance 方法  
  
Fragment具有属性retainInstance，默认值为false，当设备旋转时，fragment会随托管activity一起销毁并重建。  
  
调用setRetainInstance(true)方法可保留fragment，已保留的fragment不会随着activity一起被销毁，相反，它会一直保留(进程不消亡的前提下)，并在需要时原封不动地传递给新的Activity。  
  
**保留Fragment的原理**  
  
当设备配置发生变化时，FragmentManager首先销毁队列中fragment的视图（因为可能有更合适的匹配资源），紧接着，FragmentManager将检查每个fragment的retainInstance属性值。  
- 如果retainInstance属性值为false，FragmentManager会立即销毁该fragment实例，随后，为适应新的设备配置，新的Activity的新的FragmentManager会创建一个新的fragment及其视图。  
- 如果retainInstance属性值为true，则该fragment本身不会被销毁。为适应新的设备配置，当新的Activity创建后，新的FragmentManager会找到被保留的fragment，并重新创建其视图(而不会重建fragment本身)。  
  
虽然保留的fragment没有被销毁，但它已脱离消亡中的activity并处于保留状态。尽管此时的fragment仍然存在，但已经没有任何activity托管它。   
  
需要说明的是，只有调用了fragment的setRetainInstance(true)方法，并且因设备配置改变，托管Activity正在被销毁的条件下，fragment才会短暂的处于保留状态。如果activity是因操作系统需要回收内存而被销毁，则所有的fragment也会随之销毁。  
  
**那么问题来了，为什么横竖屏切换 ViewModel 不会回调 onCleared？**  
看 HolderFragment 的构造方法里有个`setRetainInstance(true);`  
```java  
public HolderFragment() {  
    setRetainInstance(true);  
}  
```  
  
setRetainInstance(boolean) 是Fragment中的一个方法。将这个方法设置为true就可以使当前Fragment在Activity重建时存活下来。在setRetainInstance(boolean)为true的 Fragment 中放一个专门用于存储ViewModel的Map, 自然Map中所有的ViewModel都会幸免于Activity重建，让Activity, Fragment都绑定一个这样的Fragment, 将ViewModel存放到这个 Fragment 的 Map 中, ViewModel 组件就这样实现了。  
  
# 使用案例  
  
## MutableLiveData 简单使用案例  
  
LiveData which publicly exposes公开暴露 setValue(T) and postValue(T) method.  
将 LiveData 的 setValue 和 postValue 访问权限修改为了 public  
  
```java  
public class MutableLiveData<T> extends LiveData<T>   
```  
  
**案例一：直接在Activity中定义LiveData**  
这种方式不推荐  
```java  
MutableLiveData<String> liveData = new MutableLiveData<>(); //定义LiveData  
liveData.observe(this, s -> tvTitle.setText("值为：" + s)); //将Observer对象注册到LiveData对象  
liveData.setValue(value);//修改LiveData存储的值会立即触发onChanged()回调  
new Thread(() -> liveData.postValue(value)).start(); //在子线程必须使用 postValue  
```  
  
调用observe()之后，将立即回调onChanged()，提供存储在LiveData中的最新值。如果LiveData对象存储的值并未设置，则不调用onChanged()。  
当通过`setValue`修改LiveData存储的值时，会立即回调onChanged()方法。  
在主线程可以使用setValue也可以使用postValue，但在子线程必须使用`postValue`，否则报llegalStateException: Cannot invoke   
  
**案例二：和ViewModel结合使用**  
定义ViewModel：  
```java  
public class MyViewModel extends ViewModel {  
    private MutableLiveData<String> mCurrentName;  
      
    public MutableLiveData<String> getCurrentName() {  
        if (mCurrentName == null) {  
            mCurrentName = new MutableLiveData<>();  
        }  
        return mCurrentName;  
    }  
}  
```  
```java  
MyViewModel mModel = ViewModelProviders.of(this).get(MyViewModel.class); //Google推荐的方式  
mModel.getCurrentName().observe(this, s -> tvTitle.setText("值为：" + s));  
mModel.getCurrentName().setValue(value);  
```  
  
**案例三：和单例模式结合使用**  
定义LiveData：  
```java  
public class MyMutableLiveData extends MutableLiveData<String> {  
      
    private MyMutableLiveData() {  
    }  
      
    public static MyMutableLiveData get() {  
        return SingleHolder.INSTANCE;  
    }  
      
    private static class SingleHolder {  
        private static final MyMutableLiveData INSTANCE = new MyMutableLiveData();  
    }  
}  
```  
```java  
MyMutableLiveData.get().observe(this, s -> tvTitle.setText("值为：" + s));  
MyMutableLiveData.get().setValue(value);  
```  
  
## 扩展 LiveData  
  
### LiveData  
```java  
public class MyLiveData extends LiveData<String> {  
    private Manager manager;  
    private OnChangeListener listener = time -> postValue("回调值：" + time); //因为在子线程中，所以一定要用postValue  
      
    private MyLiveData() {  
        manager = new Manager();  
    }  
      
    public static MyLiveData get() {  
        return SingleHolder.INSTANCE;  
    }  
      
    private static class SingleHolder {  
        private static final MyLiveData INSTANCE = new MyLiveData();  
    }  
      
    @Override  
    protected void onActive() {  
        super.onActive();  
        Log.i("bqt", "【onActive】");  
        manager.addListerner(listener);  
    }  
      
    @Override  
    protected void onInactive() {  
        super.onInactive();  
        Log.i("bqt", "【onInactive】");  
        manager.removeListerner(listener);  
    }  
}  
```  
  
### Manager  
```java  
public class Manager {  
    private boolean stop = false;  
    private Set<OnChangeListener> listeners = new HashSet<>();  
      
    public Manager() {  
        new Thread(() -> {  
            while (!stop) {  
                SystemClock.sleep(2000);  
                for (OnChangeListener listener : listeners) {  
                    String time = new SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault()).format(new Date());  
                    listener.onChanged(time);  
                }  
            }  
        }).start();  
    }  
      
    public void addListerner(OnChangeListener listener) {  
        listeners.add(listener);  
    }  
      
    public void removeListerner(OnChangeListener listener) {  
        listeners.remove(listener);  
    }  
      
    public void stop() {  
        stop = true;  
    }  
}  
```  
  
### 案例说明  
回调接口  
```java  
public interface OnChangeListener {  
    void onChanged(String time);  
}  
```  
  
本例中LiveData的实现包括以下重要的方法：  
- 当LiveData对象有一个活跃的Observer时，onActive()方法被调用，这意味着你需要从这个方法开始观察Manager的更新。  
- 当LiveData对象没有任何活跃的Observer时，onInactive()方法被调用，由于没有Observer在监听，所以没有理由继续保持与Manager的连接。  
- setValue(T)方法更新LiveData实例的值，并通知活动观察者有关更改。  
  
  
```java  
MyLiveData.get().observe(this, s -> Log.i("bqt", "【观察到的值】" + s));  
```  
  
observe()方法第一个参数是作为LifecycleOwner实例的Fragment或FragmentActivity，这样做表示此Observer绑定了Lifecycle对象的生命周期，即：  
- 如果Lifecycle对象不处于活动状态，则即使值发生更改，也不会调用Observer。  
- Lifecycle对象被销毁后，Observer被自动删除。  
- LiveData对象具有感知生命周期的能力，意味着您可以在多个Activity，Fragment和service之间共享它们。  
  
## MediatorLiveData 案例  
  
```java  
public class MyMediatorLiveData extends MediatorLiveData<String> {  
    @Override  
    protected void onActive() {  
        super.onActive();  
        Log.i("bqt", "【onActive】");  
    }  
      
    @Override  
    protected void onInactive() {  
        super.onInactive();  
        Log.i("bqt", "【onInactive】");  
    }  
}  
```  
  
添加源  
```java  
MyMediatorLiveData mediatorLiveData = new MyMediatorLiveData();  
mediatorLiveData.addSource(liveData1, s -> mediatorLiveData.setValue("liveData1-" + s));  
mediatorLiveData.addSource(liveData2, s -> mediatorLiveData.setValue("liveData2-" + s));  
mediatorLiveData.observe(this, s -> tvTitle.setText("值为：" + s));  
```  
  
在源LiveData数据变更后MediatorLiveData注册的观察者就会收到通知。  
```java  
liveData1.postValue(value1);  
liveData2.setValue(value2);  
```  
  
## ViewModel 案例  
  
定义 ViewModel  
```java  
public class MyViewModel extends ViewModel {  
    private MutableLiveData<String> mutableLiveData;  
      
    public MutableLiveData<String> getMutableLiveData() {  
        if (mutableLiveData == null) {  
            mutableLiveData = new MutableLiveData<>();  
        }  
        return mutableLiveData;  
    }  
}  
```  
  
通过ViewModelProviders获取 ViewModel 实例  
```java  
MyViewModel model = ViewModelProviders.of(activity).get(MyViewModel.class);  
MyViewModel model = ViewModelProviders.of(fragment).get(MyViewModel.class);   
MyViewModel model = ViewModelProviders.of(activity，factory).get(MyViewModel.class);  
```  
  
> You usually request a ViewModel the first time the system calls an activity object's `onCreate()` method.  
  
在Activity中使用  
```java  
private MyViewModel mModel;  
mModel = ViewModelProviders.of(this).get(MyViewModel.class); //获取ViewModel实例，注意这里的【this】  
mModel.getMutableLiveData().observe(this, s -> tvTitle.setText("Activity获取到的值为：" + s)); //注册观察者  
mModel.getMutableLiveData().setValue(value); //修改LiveData中存储的值  
```  
  
在Fragment中使用  
```java  
private MyViewModel mModel;  
mModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class); //注意这里一定要用【getActivity()】  
mModel.getMutableLiveData().observe(this, s -> tv.setText("Fragment获取到的值为：" + s));  
mModel.getMutableLiveData().setValue(value);  
```  
  
2019-3-29  
