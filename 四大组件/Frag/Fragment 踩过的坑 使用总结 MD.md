| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [Fragment 那些年踩过的坑](#Fragment-那些年踩过的坑)
	- [getActivity()空指针](#getActivity空指针)
	- [commit 异常：Can not perform this action after onSaveInstanceState](#commit-异常：Can-not-perform-this-action-after-onSaveInstanceState)
	- [Fragment重叠异常](#Fragment重叠异常)
	- [Fragment嵌套的那些坑](#Fragment嵌套的那些坑)
	- [未必靠谱的出栈方法remove()](#未必靠谱的出栈方法remove)
	- [多个Fragment同时出栈的深坑BUG](#多个Fragment同时出栈的深坑BUG)
	- [深坑 Fragment 转场动画](#深坑-Fragment-转场动画)
	- [总结](#总结)
- [Fragment 正确的使用姿势](#Fragment-正确的使用姿势)
	- [一些使用建议](#一些使用建议)
	- [add(), show(), hide(), replace()的那点事](#add-show-hide-replace的那点事)
	- [关于FragmentManager你需要知道的](#关于FragmentManager你需要知道的)
	- [使用FragmentPagerAdapter+ViewPager的注意事项](#使用FragmentPagerAdapterViewPager的注意事项)
	- [使用单Activity＋多Fragment的架构，还是多模块Activity＋多Fragment的架构？](#使用单Activity＋多Fragment的架构，还是多模块Activity＋多Fragment的架构？)
  
# Fragment 那些年踩过的坑  
  
本篇主要介绍一些最常见的Fragment的坑以及官方Fragment库的那些自身的BUG，并给出解决方案。  
  
Fragment是可以让你的app纵享丝滑的设计，如果你的app想在现在基础上**性能大幅度提高**，并且**占用内存降低**，同样的界面Activity占用内存比Fragment要多，响应速度Fragment比Activty在中低端手机上快了很多，甚至能达到好几倍！如果你的app当前或以后有**移植**平板等平台时，可以让你节省大量时间和精力。  
  
但是！Fragment相比较Activity要难用很多，在多Fragment以及嵌套Fragment的情况下更是如此。更重要的是Fragment的坑真的太多了，看Square公司的这篇文章吧，[Square：从今天开始抛弃Fragment吧！](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0605/2996.html)  
  
当然，不能说不再用Fragment，Fragment的这些坑都是有解决办法的，官方也在逐步修复一些BUG。  
  
下面罗列一些，有常见的，也有极度隐蔽的一些坑，也是我在用单Activity多Fragment时遇到的坑。  
  
在这之前为了方便后面文章的介绍，先规定一个“术语”，安卓app有一种特殊情况，就是 app运行在后台的时候，系统资源紧张的时候导致把app的资源全部回收（杀死app的进程），这时把app再从后台返回到前台时，app会重启。这种情况下文简称为：**“内存重启”**。  
> 屏幕旋转等配置变化也会造成当前Activity重启，本质与“内存重启”类似。  
  
在系统要把app回收之前，系统会把Activity的状态保存下来，Activity的FragmentManager负责把Activity中的Fragment保存起来。在“内存重启”后，Activity的恢复是从栈顶逐步恢复，Fragment会在宿主Activity的`onCreate`方法调用后紧接着恢复（`从onAttach`生命周期开始）。  
  
## getActivity()空指针  
  
可能你遇到过getActivity()返回null，或者平时运行完好的代码，在“内存重启”之后，调用getActivity()的地方却返回null，报了空指针异常。  
  
大多数情况下的原因：你在调用了getActivity()时，当前的Fragment已经`onDetach()`了宿主Activity。  
  
比如：你在pop了Fragment之后，该Fragment的`异步任务仍然在执行`，并且在执行完成后调用了getActivity()方法，这样就会空指针。  
  
**更"安全"的解决方法**  
对于Fragment已经onDetach这种情况，我们应该避免在这之后再去调用宿主Activity对象，比如取消这些异步任务，但我们的团队可能会有粗心大意的情况，所以下面给出的这个方案会保证安全。  
  
在Fragment基类里设置一个mActivity的成员变量，在`onAttach(Activity activity)`里赋值，以后使用mActivity代替`getActivity()`，保证Fragment即使在`onDetach`后，仍持有Activity的引用：  
```java  
protected Activity mActivity;  
@Override  
public void onAttach(Activity activity) {  
    super.onAttach(activity);  
    this.mActivity = activity;  
}  
```  
  
这种方式有引起内存泄露的风险，但是异步任务没停止的情况下，本身就可能已内存泄漏，相比Crash，这种做法“安全”些。  
  
如果你用了support 23的库，上面的方法会提示过时，有强迫症的小伙伴，可以用下面的方法代替：  
```java  
@Override  
public void onAttach(Context context) {  
    super.onAttach(context);  
    this.mActivity = (Activity)context;  
}  
```  
  
## commit 异常：Can not perform this action after onSaveInstanceState  
  
有很多小伙伴遇到这个异常，这个异常产生的原因是：  
  
在你离开当前Activity等情况下，系统会调用`onSaveInstanceState()`帮你保存当前Activity的状态、数据等，**直到再回到该Activity之前（`onResume()`之前），你执行Fragment事务，就会抛出该异常！**（一般是其他Activity的回调让当前页面执行事务的情况，会引发该问题）  
  
**解决方法**  
1、该事务使用`commitAllowingStateLoss()`方法提交，但是有可能导致该次提交无效！  
  
> 对于`popBackStack()`没有对应的`popBackStackAllowingStateLoss()`方法，所以可以在下次可见时提交事务，参考2  
  
2、利用`onActivityForResult()`/`onNewIntent()`，可以做到事务的完整性，不会丢失事务。  
  
ReceiverActivity 或 其子Fragment:  
```java  
startActivityForResult(new Intent(this, SenderActivity.class), 100);  
  
@Override  
protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
     super.onActivityResult(requestCode, resultCode, data);  
     if (requestCode == 100 && resultCode == 100) {  
         // 执行Fragment事务  
     }  
 }  
```  
  
SenderActivity 或 其子Fragment:  
```java  
setResult(100);// 操作ReceiverActivity（或其子Fragment）执行事务  
finish();  
```  
  
## Fragment重叠异常  
  
在Activity的`onCreate()`的方法中加载Fragment，并且没有判断`saveInstanceState==null`或`if(findFragmentByTag(mFragmentTag) == null)`，导致重复加载了同一个Fragment导致重叠。  
  
> PS：`replace`情况下，如果没有加入回退栈，则不判断也不会造成重叠，但建议还是统一判断下  
  
```java  
@Override   
protected void onCreate(@Nullable Bundle savedInstanceState) {  
// 在页面重启时，Fragment会被保存恢复，而此时再加载Fragment会重复加载，导致重叠 ;  
    if(saveInstanceState == null){  
    // 或者 if(findFragmentByTag(mFragmentTag) == null)  
    // 正常情况下去 加载根Fragment   
    }   
}  
```  
  
详细原因：[从源码角度分析，为什么会发生Fragment重叠？](https://www.jianshu.com/p/78ec81b42f92)  
  
如果你`add()`了几个Fragment，使用`show()、hide()`方法控制，比如微信、QQ的底部tab等情景，如果你什么都不做的话，在“内存重启”后回到前台，app的这几个Fragment界面会重叠。  
原因是FragmentManager帮我们管理Fragment，当发生“内存重启”，他会从栈底向栈顶的顺序一次性恢复Fragment；但是因为官方没有保存Fragment的mHidden属性，默认为false，即show状态，所以所有Fragment都是以show的形式恢复，我们看到了界面重叠。  
  
> 如果是`replace`，恢复形式和Activity一致，只有当你pop之后上一个Fragment才开始重新恢复，所以使用`replace`不会造成重叠现象  
  
> 注意：v4-24.0.0+ 开始，官方修复了上述没有保存mHidden的问题，所以如果你在使用24.0.0+的v4包，下面分析的2个解决方案可以自行跳过...  
  
这里给出2个解决方案：  
  
1、是大家比较熟悉的 `findFragmentByTag`  
  
即在`add()`或者`replace()`时绑定一个tag，一般我们是用fragment的类名作为tag，然后在发生“内存重启”时，通过`findFragmentByTag`找到对应的Fragment，并`hide()`需要隐藏的fragment。  
  
下面是个标准恢复写法：  
```java  
@Override  
protected void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);  
    setContentView(R.layout.activity);  
  
    TargetFragment targetFragment;  
    HideFragment hideFragment;  
  
    if (savedInstanceState != null) {  // “内存重启”时调用  
        targetFragment = getSupportFragmentManager().findFragmentByTag(TargetFragment.class.getName);  
        hideFragment = getSupportFragmentManager().findFragmentByTag(HideFragment.class.getName);  
        getFragmentManager().beginTransaction()// 解决重叠问题  
                .show(targetFragment)  
                .hide(hideFragment)   
                .commit();  
    }else{  // 正常时  
        targetFragment = TargetFragment.newInstance();  
        hideFragment = HideFragment.newInstance();  
        getFragmentManager().beginTransaction()  
                .add(R.id.container, targetFragment, targetFragment.getClass().getName())  
                .add(R.id,container,hideFragment,hideFragment.getClass().getName())  
                .hide(hideFragment)  
                .commit();  
    }  
}  
```  
  
如果你想恢复到用户离开时的那个Fragment的界面，你还需要在`onSaveInstanceState(Bundle outState)`里保存离开时的那个可见的tag或下标，在`onCreate`“内存重启”代码块中，取出tag或下标，进行恢复。  
  
2、我的解决方案，9行代码解决所有情况的Fragment重叠：[传送门](https://www.jianshu.com/p/c12a98a36b2b)  
  
## Fragment嵌套的那些坑  
  
其实一些小伙伴遇到的很多嵌套的坑，大部分都是由于对嵌套的栈视图产生混乱，只要理清栈视图关系，做好恢复相关工作以及正确选择是使用`getFragmentManager()`还是`getChildFragmentManager()`就可以避免这些问题。  
  
> 附：startActivityForResult接收返回问题在support 23.2.0以下的支持库中，对于在嵌套子Fragment的`startActivityForResult ()`，会发现无论如何都不能在`onActivityResult()`中接收到返回值，只有最顶层的父Fragment才能接收到，这是一个support v4库的一个BUG，不过在前两天发布的support 23.2.0库中，已经修复了该问题，嵌套的子Fragment也能正常接收到返回数据了!  
  
## 未必靠谱的出栈方法remove()  
  
如果你想让某一个Fragment出栈，使用`remove()`在加入回退栈时并不靠谱。  
  
如果你在add的同时将Fragment加入回退栈：addToBackStack(name)的情况下，它并不能真正将Fragment从栈内移除，如果你在2秒后（确保Fragment事务已经完成）打印`getSupportFragmentManager().getFragments()`，会发现该Fragment依然存在，并且依然可以返回到被remove的Fragment，而且是空白页面。  
  
如果你没有将Fragment加入回退栈，remove方法可以正常出栈。  
  
如果你加入了回退栈，`popBackStack()`系列方法才能真正出栈，这也就引入下一个深坑，`popBackStack`等系列方法的BUG。  
  
## 多个Fragment同时出栈的深坑BUG  
  
> 6月17日更新： 在support-25.4.0版本，google意识到下面的问题，并修复了。 如果你使用25.4.0及以上版本，下面的方法不要再使用，google移除了mAvailIndices属性  
  
在Fragment库中如下4个方法是可能产生BUG的：  
- popBackStack(String tag,int flags)  
- popBackStack(int id,int flags)  
- popBackStackImmediate(String tag,int flags)  
- popBackStackImmediate(int id,int flags)  
  
上面4个方法作用是，出栈到tag/id的fragment，即一次多个Fragment被出栈。  
  
**1、FragmentManager栈中管理fragment下标位置的数组的BUG**  
  
下面的方法FragmentManagerImpl类方法，产生BUG的罪魁祸首是管理Fragment栈下标的`mAvailIndeices`属性：  
  
```  
void makeActive(Fragment f) {  
      if (f.mIndex >= 0) {  
         return;  
      }   
      if (mAvailIndices == null || mAvailIndices.size() <= 0) {  
           if (mActive == null) {  
              mActive = new ArrayList<Fragment>();  
           }   
           f.setIndex(mActive.size(), mParent);   
           mActive.add(f);  
       } else {  
           f.setIndex(mAvailIndices.remove(mAvailIndices.size()-1), mParent);  
           mActive.set(f.mIndex, f);  
       }   
      if (DEBUG) Log.v(TAG, "Allocated fragment index " + f);  
 }  
```  
  
上面代码最终导致了栈内顺序不正确的问题，如下图：  
![](index_files/0d48de53-a2e3-4c21-9a76-d86c80a325a0.jpg)  
  
上面的这个情况，会一次异常，一次正常。带来的问题就是“内存重启”后，各种异常甚至Crash。  
  
发现这BUG的时候，我一脸懵比，幸好，stackoverflow上有大神给出了[解决方案](http%3A%2F%2Fstackoverflow.com%2Fquestions%2F25520705%2Fandroid-cant-retain-fragments-that-are-nested-in-other-fragments)！hack `FragmentManagerImpl`的`mAvailIndices`，对其进行一次`Collections.reverseOrder()`降序排序，保证栈内Fragment的index的正确。  
  
```  
public class FragmentTransactionBugFixHack {  
  
  public static void reorderIndices(FragmentManager fragmentManager) {  
    if (!(fragmentManager instanceof FragmentManagerImpl))  
      return;  
    FragmentManagerImpl fragmentManagerImpl = (FragmentManagerImpl) fragmentManager;  
    if (fragmentManagerImpl.mAvailIndices != null && fragmentManagerImpl.mAvailIndices.size() > 1) {  
      Collections.sort(fragmentManagerImpl.mAvailIndices, Collections.reverseOrder());  
    }  
  }  
}  
```  
  
使用方法就是通过`popBackStackImmediate(tag/id)`多个Fragment后，调用  
  
```  
hanler.post(new Runnable(){  
    @Override  
     public void run() {  
         FragmentTransactionBugFixHack.reorderIndices(fragmentManager));  
     }  
});  
```  
  
**2、popBackStack的坑**  
`popBackStack`和`popBackStackImmediate`的区别在于前者是加入到主线队列的末尾，等其它任务完成后才开始出栈，后者是队列内的任务立即执行，再将出栈任务放到队列尾（可以理解为立即出栈）。  
  
如果你`popBackStack`多个Fragment后，紧接着`beginTransaction()` add新的一个Fragment，接着发生了“内存重启”后，你再执行`popBackStack()`，app就会Crash，解决方案是postDelay出栈动画时间再执行其它事务，但是根据我的观察不是很稳定。  
  
我的建议是：如果你想出栈多个Fragment，你应尽量使用`popBackStackImmediate(tag/id)`，而不是`popBackStack(tag/id)`，如果你想在出栈后，立刻`beginTransaction()`开始一项事务，你应该把事务的代码post/postDelay到主线程的消息队列里，下一篇有详细描述。  
  
## 深坑 Fragment 转场动画  
  
如果你的Fragment没有转场动画，或者使用`setCustomAnimations(enter, exit)`的话，那么上面的那些坑解决后，你可以愉快的玩耍了。  
  
```  
getFragmentManager().beginTransaction()  
         .setCustomAnimations(enter, exit)  
        // 如果你有通过tag/id同时出栈多个Fragment的情况时，  
        // 请谨慎使用.setCustomAnimations(enter, exit, popEnter, popExit)    
        // 在support-25.4.0之前出栈多Fragment时，伴随出栈动画，会在某些情况下发生异常  
        // 你需要搭配Fragment的onCreateAnimation()临时取消出栈动画，或者延迟一个动画时间再执行一次上面提到的Hack方法，排序  
```  
  
> 注意：如果你想给下一个Fragment设置进栈动画和出栈动画，.setCustomAnimations(enter, exit)只能设置进栈动画，第二个参数并不是设置出栈动画；请使用.setCustomAnimations(enter, exit, popEnter, popExit)，这个方法的第1个参数对应进栈动画，第4个参数对应出栈动画，所以是.setCustomAnimations(进栈动画, exit, popEnter, 出栈动画)  
  
总结起来就是Fragment没有出栈动画的话，可以避免很多坑。  
  
如果想让出栈动画运作正常的话，需要使用Fragment的`onCreateAnimation`中控制动画。  
  
```  
@Override  
public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {  
    // 此处设置动画  
}  
```  
  
但是用代价也是有的，你需要解决出栈动画带来的几个坑。  
  
**1、pop多个Fragment时转场动画带来的问题**  
  
> 6月17日更新： 在support-25.4.0版本，google意识到下面动画引起的问题，并修复了。  
  
在使用 `pop(tag/id)`出栈多个Fragment的这种情况下，将转场动画临时取消或者延迟一个动画的时间再去执行其他事务；  
  
原因在于这种情景下，可能会导致栈内顺序错乱（上文有提到），同时如果发生“内存重启”后，因为Fragment转场动画没结束时再执行其他方法，会导致Fragment状态不会被FragmentManager正常保存下来。  
  
**2、进入新的Fragment并立刻关闭当前Fragment 时的一些问题**  
如果你想从当前Fragment进入一个新的Fragment，并且同时要关闭当前Fragment。由于数据结构是栈，所以正确做法是先`pop`，再`add`，但是转场动画会有覆盖的不正常现象，你需要特殊处理，不然会闪屏！  
  
> Tip：如果你遇到Fragment的mNextAnim空指针的异常（通常是在你的Fragment被重启的情况下），那么你首先需要检查是否操作的Fragment是否为null；其次在你的Fragment转场动画还没结束时，你是否就执行了其他事务等方法；解决思路就是延迟一个动画时间再执行事务，或者临时将该Fragment设为无动画  
  
## 总结  
  
看了上面的介绍，你可能会觉得Fragment有点可怕。  
  
但是我想说，如果你只是浅度使用，比如一个Activity容器包含列表Fragment＋详情Fragment这种简单情景下，不涉及到`popBackStack/Immediate(tag/id)`这些的方法，还是比较轻松使用的，出现的问题，网上都可以找到解决方案。  
  
但是如果你的Fragment逻辑比较复杂，有特殊需求，或者你的app架构是仅有一个Activity + 多个Fragment，上面说的这些坑，你都应该全部解决。  
  
# Fragment 正确的使用姿势  
  
## 一些使用建议  
  
1、对Fragment传递数据，建议使用`setArguments(Bundle args)`，而后在`onCreate`中使用`getArguments()`取出，在 “内存重启”前，系统会帮你保存数据，不会造成数据的丢失。和Activity的Intent恢复机制类似。  
  
2、使用`newInstance(参数)` 创建Fragment对象，优点是调用者只需要关系传递的哪些数据，而无需关心传递数据的Key是什么。  
  
3、如果你需要在Fragment中用到宿主Activity对象，建议在你的基类Fragment定义一个Activity的全局变量，在`onAttach`中初始化，这不是最好的解决办法，但这可以有效避免一些意外Crash。  
```  
protected Activity mActivity;  
@Override  
public void onAttach(Activity activity) {  
    super.onAttach(activity);  
    this.mActivity = activity;  
}  
```  
  
## add(), show(), hide(), replace()的那点事  
  
**1、区别**  
- `show()`，`hide()`最终是让Fragment的View `setVisibility`(true还是false)，不会调用生命周期；  
- `replace()`的话会销毁视图，即调用onDestoryView、onCreateView等一系列生命周期；  
- `add()`和 `replace()`不要在同一个阶级的FragmentManager里混搭使用。  
  
**2、使用场景**  
如果你有一个很高的概率会再次使用当前的Fragment，建议使用`show()`，`hide()`，可以提高性能。  
在我使用Fragment过程中，大部分情况下都是用`show()`，`hide()`，而不是`replace()`。  
注意：如果你的app有大量图片，这时更好的方式可能是replace，配合你的图片框架在Fragment视图销毁时，回收其图片所占的内存。  
  
**3、onHiddenChanged的回调时机**  
当使用`add()`+`show()，hide()`跳转新的Fragment时，旧的Fragment回调`onHiddenChanged()`，不会回调`onStop()`等生命周期方法，而新的Fragment在创建时是不会回调`onHiddenChanged()`，这点要切记。  
  
**4、Fragment重叠问题**  
使用`show()`，`hide()`带来的一个问题就是，如果你不做任何额外处理，在“内存重启”后，Fragment会重叠；（该BUG在support-v4 24.0.0+以上 官方已修复）  
  
有些小伙伴可能就是为了避免Fragment重叠问题，而选择使用`replace()`，但是使用`show()`，`hide()`时，重叠问题很简单解决的：  
*   如果你在用24.0.0+的版本，不需要特殊处理，官方已经修复该BUG；  
*   如果你在使用小于24.0.0以下的v4包，可以参考[9行代码让你App内的Fragment对重叠说再见](https://www.jianshu.com/p/c12a98a36b2b)  
  
## 关于FragmentManager你需要知道的  
  
**FragmentManager栈视图:**  
1、每个Fragment以及宿主Activity(继承自FragmentActivity)都会在创建时，初始化一个FragmentManager对象，处理好Fragment嵌套问题的关键，就是理清这些不同阶级的栈视图。  
  
下面给出一个简要的**关系图**：  
  
![](https://upload-images.jianshu.io/upload_images/937851-6e0b034db7df7199.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/582/format/webp)  
  
2、对于宿主Activity，`getSupportFragmentManager()`获取的FragmentActivity的FragmentManager对象;  
  
对于Fragment，`getFragmentManager()`是获取的是父Fragment(如果没有，则是FragmentActivity)的FragmentManager对象，而`getChildFragmentManager()`是获取自己的FragmentManager对象。  
  
## 使用FragmentPagerAdapter+ViewPager的注意事项  
  
*   使用FragmentPagerAdapter+ViewPager时，切换回上一个Fragment页面时（已经初始化完毕），不会回调任何生命周期方法以及`onHiddenChanged()`，只有`setUserVisibleHint(boolean isVisibleToUser)`会被回调，所以如果你想进行一些懒加载，需要在这里处理。  
*   在给ViewPager绑定FragmentPagerAdapter时，`new FragmentPagerAdapter(fragmentManager)`的FragmentManager，一定要保证正确，如果ViewPager是Activity内的控件，则传递`getSupportFragmentManager()`，如果是Fragment的控件中，则应该传递`getChildFragmentManager()`。只要记住ViewPager内的Fragments是当前组件的子Fragment这个原则即可。  
*   你不需要考虑在“内存重启”的情况下，去恢复Fragments的问题，因为FragmentPagerAdapter已经帮我们处理啦。  
  
## 使用单Activity＋多Fragment的架构，还是多模块Activity＋多Fragment的架构？  
  
**单Activity＋多Fragment：**  
一个app仅有一个Activity，界面皆是Frament，Activity作为app容器使用。  
优点：性能高，速度最快。参考：新版知乎 、google系app  
缺点：逻辑比较复杂，尤其当Fragment之间联动较多或者嵌套较深时，比较复杂。  
  
**多模块Activity＋多Fragment：**  
一个模块用一个Activity，比如  
- 登录注册流程：LoginActivity + 登录Fragment + 注册Fragment + 填写信息Fragment ＋ 忘记密码Fragment  
- 或者常见的数据展示流程：DataActivity + 数据列表Fragment + 数据详情Fragment ＋ ...  
  
优点：速度快，相比较单Activity+多Fragment，更易维护。  
  
**我的观点：**  
权衡利弊。**Fragment只是官方提供的灵活组件，请优先遵从你的项目设计！**真的特别复杂的界面，或者单个Activity就可以完成一个流程的界面，使用Activity可能是更好的方案。  
  
2019-3-31  
