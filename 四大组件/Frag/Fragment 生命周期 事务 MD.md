| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [生命周期](#生命周期)
	- [标准的生命周期](#标准的生命周期)
	- [与事务相关的生命周期](#与事务相关的生命周期)
	- [为什么 add 之前需要判断是否为null](#为什么-add-之前需要判断是否为null)
	- [测试事务相关的生命周期的案例](#测试事务相关的生命周期的案例)
- [FragmentTransaction](#FragmentTransaction)
	- [全部方法](#全部方法)
	- [几个常用API的区别](#几个常用API的区别)
	- [添加移除显示隐藏方法](#添加移除显示隐藏方法)
		- [add](#add)
		- [remove](#remove)
		- [replace](#replace)
		- [show 和 hide](#show-和-hide)
		- [attach 和 detach](#attach-和-detach)
	- [addToBackStack](#addToBackStack)
	- [commit 系列方法](#commit-系列方法)
	- [setCustomAnimations](#setCustomAnimations)
	- [addSharedElement](#addSharedElement)
- [主界面 Tab + Fragment 实现案例](#主界面-Tab--Fragment-实现案例)
	- [Activity](#Activity)
	- [Fragment](#Fragment)
	- [布局](#布局)
  
# 生命周期  
  
## 标准的生命周期  
  
当创建、显示Fragment的时候都是先执行Activity的方法，当停止、销毁Fragment的时候都是先执行Fragment的方法。  
> 皮之不存，毛将焉附！  
  
```java  
Activity【onCreate】//Activity的onCreate是一切的起点  
【onAttach】null  
【onCreate】微信  
【onCreateView】微信  
【onViewCreated】  
【onActivityCreated】微信  
  
【onStart】微信  
Activity【onStart】  
Activity【onResume】  
【onResume】微信  
  
【onPause】微信  
Activity【onPause】  
【onStop】微信  
Activity【onStop】  
  
【onDestroyView】微信  
【onDestroy】微信  
【onDetach】微信  
Activity【onDestroy】//Activity的onDestroy是一切的终点  
```  
  
**创建时的逻辑**  
- 先调用onAttach，表明将Fragment添加到了Activity；onAttach会回调给我们一个Context对象，一般此Context就是此Fragment所依附的那个Activity；不过一般我们不重写此方法而是重写onCreate方法  
- 再调用onCreate，表明开始创建Fragment了，注意此方法只会调用一次，类似于Activity的onCreate只会调用一次；同样类似于Activity的onCreate方法，此方法会回调给我们一个 Bundle savedInstanceState 对象，用于状态的恢复，我们一般在这里做初始化操作(而不是在onAttach中)  
- 再调用onCreateView，表明开始创建Fragment中的View了，此方法是Fragment中最常操作的一个方法，因为我们必须在此方法中返回Fragment所需的View，通常我们直接在这里执行 inflateView 及 findView 甚至包括 initView 操作  
- 再调用onViewCreated，表明Fragment中的View已经创建完成了，此时可以根据参数view初始化View了  
- 最后调用onActivityCreated，表明Activity的onCreate方法执行完毕了，只有在这及之后，Fragment才能和Activity进行交互  
  
**销毁时的逻辑**  
- 先调用onDestroyView，把Fragment的View销毁掉，此方法和onCreateView是相对应的  
- 再调用onDestroy，把Fragment的自身销毁掉，此方法只会调用一次；此方法类似于Activity的onDestroy方法，一般在这里做内存释放、资源的回收等操作  
- 最后调用onDetach，把Fragment从Activity中移除  
  
![](index_files/2f86b8b4-b0dd-4315-978a-ff07afe480de.png)  
  
## 与事务相关的生命周期  
  
**hide、show方法**  
- 如果通过`hide`方法将此Fragment隐藏掉，即使此时Fragment对用户不可见，但是因为Fragment是依附于Activity的，当Activity通过前后台切换等方式回调`onStart、onResume、onPause、onStop`等方法时，仍会回调Fragment的`onStart、onResume、onPause、onStop`方法。并且如果FragmentManager管理有多个Fragment，那么所有这些Fragment都会回调`onStart、onResume、onPause、onStop`方法。  
  
- 同样，如果Activity没有回调onStart、onResume、onPause、onStop等方法，即使你通过`hide`方法将此Fragment隐藏了，或者通过`show`方法将此Fragment显示了，虽然Fragment的可见状态改变了，但是所有这些Fragment都仍然不会回调`onStart、onResume、onPause、onStop`方法。  
  
> 简单来说就是，使用show或hide显示或隐藏Fragment时，Fragment的生命周期不会发生任何变化。  
  
**add、remove方法**  
- 上面说的只是`hide`和`show`方法，如果你用的是`add`、`remove`方法，则Fragment的生命周期是会相应回调的。  
  
- 比如你在Activity的中通过点击一个按钮将一个Fragment通过add方式添加到Activity中，其会依次回调`onAttach、onCreate、onCreateView、onViewCreated、onActivityCreated、onStart、onResume`方法。此时你再通过remove方法将其移除时，则会依次回调`onPause、onStop、onDestroyView、onDestroy、onDetach`方法。  
  
> 简单来说就是，使用add或remove添加或移除Fragment时，Fragment的生命周期会发生任何变化。  
  
**replace方式显示Fragment，不添加到回退栈**  
- 当我们通过replace()方式首次展示FragA时，它的生命周期展示方式是同在布局文件中静态设置的表现一模一样的。  
- 在我们正常展示FragA且没有addToBackStack后，通过replace方式用另一个FragmentB替换掉FragA时，FragA调用顺序为onPause, onStop, onDestroyView, onDestroy, onDetach。这说明，FragA已经被FragmentManager完全抛弃了，取而代之的是FragB的完全展现。  
- 如果此时按返回键的话，FragB的生命周期也将是onPause, onStop, onDestroyView, onDestroy, onDetach。这说明，在添加FragmentA时，如果没有调用addToBackStack的话，当FragmentManager以【replace】方式更换掉此FragmentA时，是不会保存被更换掉的FragmentA的状态的。  
  
**replace方式显示Fragment，添加到回退栈**  
- 我们首先通过replace()方式正常添加FragA，并addToBackStack，同样，它的生命周期展示方式是同在布局文件中静态设置的表现一模一样的。  
- 然后我们用FragB来替换FragA，并addToBackStack，此时FragA生命周期方法只是调用到了`onDestroyView`，而onDestroy和onDetach则没有被调用。这说明FragA的View被销毁了，但是FragmentManager并没有完全销毁FragA，FragA的状态被保存在FragmentManager里面。  
- 然后再使用FragA来替换当前显示的FragB，此时FragB的生命方法调用顺序是跟FragB替换FragA时FragA的调用顺序一致的。而此时FragA的调用则值得注意。此时FragA直接从`onCreateView`调起，也就是说只是重新创建了视图，而依然使用上次被替换时的Fragment状态。  
- 此时(每次替换时都addToBackStack)按BACK返回时，界面是由`FragA-->FragB`，即销毁FragA的【视图】，重建FragB的【视图】  
- 再按BACK返回时，界面由`FragB-->FragA`，由于回退栈中没有FragB了，所以这时会完全【销毁FragB】，并重建FragA的【视图】  
- 再按BACK返回时，Fragment界面消失，但Activity不消失，由于回退栈中也没有FragA了，所以这时会完全【销毁FragA】  
- 再按BACK返回时，【Activity】界面才会完全退出。  
  
## 为什么 add 之前需要判断是否为null  
因为当【Activity】因为配置发生改变(如屏幕旋转)或者因内存不足被系统杀死，造成被重新创建时，我们的【Fragment】会被保存下来  
而当【Activity】被重建时【FragmentManager】会自动获取保存下来的【Fragment】队列并重建Fragment队列，从而可以恢复之前的状态  
所以如果不判断指定id的Fragment是否为null就add，会造成在同一id下有多个Fragment的情况（多个Fragment会重叠在一起）  
  
需要注意的是，因为FragmentManager重建【Fragment】时调用的是Fragment的无参的构造方法，如果不存在无参的构造方法(比如定义了有参的构造方法后没有再显示的定义无参的构造方法)，那么会导致异常，这时整个Activity就崩溃了。  
  
同时还需要注意的是，如果定义了有参的构造方法后又定义了无参的构造方法，那么由于重建Fragment时，调用的是Fragment的无参的构造方法，所以此Fragment会由于缺少必要的参数而导致显示数据异常。  
  
所以，强烈不建议给Fragment定义有参的构造方法，如果需要传数据过来，就通过setArguments传递。  
  
## 测试事务相关的生命周期的案例  
```java  
public class MainActivity2 extends FragmentActivity {  
    private static final String TAG1 = "normal_mode", TAG2 = "replace_mode";  
    private TextView tvTitle, tvWeixin, tvFriend, tvContact, tvSetting;  
    private String[] titles = {"add", "hide", "show", "remove"};  
    private FragmentManager fragmentManager;  
    private Fragment fragment1, fragment2;  
      
    boolean replaceMode = false;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        initView();  
          
        fragmentManager = getSupportFragmentManager();  
        fragment1 = fragmentManager.findFragmentByTag(TAG1);//这一步是必须要的，否则横竖屏切换时会重复创建Fragment  
        fragment2 = fragmentManager.findFragmentByTag(TAG2);  
        if (fragment1 == null) fragment1 = MyFragment.newInstance("第一个");  
        if (fragment2 == null) fragment2 = MyFragment.newInstance("第二个");  
    }  
      
    private void initView() {  
        tvWeixin = findViewById(R.id.tv_tab_bottom_weixin);  
        tvFriend = findViewById(R.id.tv_tab_bottom_friend);  
        tvContact = findViewById(R.id.tv_tab_bottom_contact);  
        tvSetting = findViewById(R.id.tv_tab_bottom_setting);  
          
        tvWeixin.setTag(0);  
        tvFriend.setTag(1);  
        tvContact.setTag(2);  
        tvSetting.setTag(3);  
          
        tvWeixin.setOnClickListener(v -> reset((Integer) v.getTag()));  
        tvFriend.setOnClickListener(v -> reset((Integer) v.getTag()));  
        tvContact.setOnClickListener(v -> reset((Integer) v.getTag()));  
        tvSetting.setOnClickListener(v -> reset((Integer) v.getTag()));  
          
        tvTitle = findViewById(R.id.tv_title);  
        tvTitle.setText(replaceMode ? "replace模式" : "非replace模式");  
        tvTitle.setOnClickListener(v -> {  
            replaceMode = !replaceMode;  
            tvTitle.setText(replaceMode ? "replace模式" : "非replace模式");  
            Log.i("bqt", "【Fragment数量】" + fragmentManager.getFragments().size());  
        });  
    }  
      
    private void reset(int position) {  
        tvWeixin.setSelected(position == 0);  
        tvFriend.setSelected(position == 1);  
        tvContact.setSelected(position == 2);  
        tvSetting.setSelected(position == 3);  
          
        switch (position) {  
            case 0:  
                if (fragmentManager.findFragmentById(R.id.id_container) == null) {  
                    //在一个activity上重复add同一个Fragment会崩溃，然而可以在一个id上add多个不同的Fragment，但这样后面的会把前面的盖住  
                    fragmentManager.beginTransaction().add(R.id.id_container, fragment1, TAG1).commit();  
                }  
                break;  
            case 1:  
                //没有add或在remove后调用hide、show方法不会异常，且只会在第一次调用时回调onAttach和onCreate方法  
                if (!replaceMode) fragmentManager.beginTransaction().hide(fragment1).commit();  
                else fragmentManager.beginTransaction().replace(R.id.id_container, fragment1, TAG1).addToBackStack("name").commit();  
                break;  
            case 2:  
                if (!replaceMode) fragmentManager.beginTransaction().show(fragment1).commit();  
                else fragmentManager.beginTransaction().replace(R.id.id_container, fragment2, TAG2).commit();  
                break;  
            case 3:  
                //没有add就调用remove方法不会异常，且不会回调任何方法，且再调用hide、show方法也不会回调任何方法  
                if (!replaceMode) fragmentManager.beginTransaction().remove(fragment1).commit();  
                else fragmentManager.beginTransaction().replace(R.id.id_container, fragment2, TAG2).addToBackStack("name").commit();  
                break;  
            default:  
                break;  
        }  
    }  
}  
```  
  
# FragmentTransaction  
  
## 全部方法  
FragmentTransaction 的全部方法（API 24）：  
- add(Fragment fragment, String tag) //  调用add(int, Fragment, String),填入为0的containerViewId.  
- add(int containerViewId, Fragment fragment) // 调用add(int, Fragment, String),填入为null的tag.  
- add(int containerViewId, Fragment fragment, String tag) // 向Activity中添加一个Fragment.  
- addSharedElement(View sharedElement, String name) // 添加共享元素  
- addToBackStack(String name) // 将事务添加到回退栈  
- attach(Fragment fragment) // 重新关联Fragment（当Fragment被detach时）  
- commit() // 提交事务  
- commitAllowingStateLoss() // 类似commit()，但允许在Activity状态保存之后提交（即允许状态丢失）。  
- commitNow() // 同步提交事务  
- commitNowAllowingStateLoss() // 类似commitNow()，但允许在Activity状态保存之后提交（即允许状态丢失）。  
- detach(Fragment fragment) // 将fragment保存的界面从UI中移除  
- disallowAddToBackStack() // 不允许调用addToBackStack(String)操作  
- hide(Fragment fragment) // 隐藏已存在的Fragment  
- isAddToBackStackAllowed() // 是否允许添加到回退栈  
- isEmpty() // 事务是否未包含的任何操作  
- remove(Fragment fragment) // 移除一个已存在的Fragment  
- replace(int containerViewId, Fragment fragment) // 调用replace(int, Fragment, String)填入为null的tag.  
- replace(int containerViewId, Fragment fragment, String tag) // 替换已存在的Fragment  
- setBreadCrumbShortTitle(int res) // 为事务设置一个BreadCrumb短标题  
- setBreadCrumbShortTitle(CharSequence text) // 为事务设置一个BreadCrumb短标题，将会被FragmentBreadCrumbs使用  
- setBreadCrumbTitle(int res) // 为事务设置一个BreadCrumb全标题，将会被FragmentBreadCrumbs使用  
- setBreadCrumbTitle(CharSequence text) // 为事务设置一个BreadCrumb全标题  
- setCustomAnimations(int enter, int exit, int popEnter, int popExit) // 自定义事务进入/退出以及入栈/出栈的动画效果  
- setCustomAnimations(int enter, int exit) // 自定义事务进入/退出的动画效果  
- setTransition(int transit) // 为事务设置一个标准动画  
- setTransitionStyle(int styleRes) // 为事务标准动画设置自定义样式  
- show(Fragment fragment) // 显示一个被隐藏的Fragment  
  
## 几个常用API的区别  
  
比如，我在FragmentA中的EditText填了一些数据，当切换到FragmentB后又切换回FragmentA时：  
- 如果希望回到A时还能看到以前数据，则适合你的就是hide和show  
- 如果你希望回到A时看到的是全新的数据，则可以优先使用detach和attach，这样会只重建视图而不重建Fragment  
- 而如果希望回到A时是重新创建的全新的Fragment，则你可以使用replace(即remove+ add)，在Fragment比较多时这种方式是合理的，因为不需要的Fragment都被销毁了  
  
## 添加移除显示隐藏方法  
### add  
  
往Activity中添加一个Fragment  
  
**API文档**  
```java  
add(Fragment fragment, String tag);  
add(int containerViewId, Fragment fragment)  
add(int containerViewId, Fragment fragment, String tag)  
```  
Add a fragment to the activity state.  This fragment may optionally also have its view (if Fragment.onCreateView returns non-null) inserted into a container view of the activity.  
  
参数：  
- containerViewId：Optional identifier of the container this fragment is to be placed in.  If 0, it will not be placed in a container.  
- fragment：The fragment to be added.  This fragment **must not** already be added to the activity.  
- tag Optional tag name for the fragment, to later retrieve the fragment with FragmentManager.findFragmentByTag(String).  
  
> 参数containerViewId一般会传Activity中某个视图容器的id，如果containerViewId传0，则这个Fragment不会被放置在一个容器中，但不要认为Fragment没添加进来，只是我们添加了一个`没有视图的Fragment`，这个Fragment可以用来做一些类似于service的后台工作。  
> 在一个activity上重复add同一个Fragment会崩溃，然而可以在一个id上add多个不同的Fragment，但这样后面的会把前面的盖住  
  
**Fragment被重复添加的问题**  
下面看一个使用add方法添加Fragment的坑：  
```java  
public class MainActivity extends AppCompatActivity {  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        fragmentManager.beginTransaction().add(R.id.fl_main, new MyFragment(), "tag").commit();  
        findViewById(R.id.fl_main).setOnClickListener(v -> Log.i("bqt", "【Fragment数量】" + fragmentManager.getFragments().size()));  
}  
```  
  
接下来运行一下程序，我们进行几次横竖屏切换，并且每次切换都点击下布局容器，我们发现Activity里的Fragment数量在不断的增加。  
  
其实，当我们Activty被异常销毁时，Activity会对自身状态进行保存，这里面包含了我们添加的Fragment。在Activity被重新创建时，又对我们之前保存的Fragment进行了恢复，但是我们在onCreate时却又添加了1次Fragment，所以我们的Fragment数量在不断增加...  
  
**添加Fragment的正确方式**  
  
```java  
public class MainActivity extends AppCompatActivity {  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
  
        if(fragmentManager.findFragmentById(R.id.fl_main) == null) { //也可以用 findFragmentByTag  
            fragmentManager.beginTransaction().add(R.id.fl_main, new MyFragment(), "tag").commit();  
        }  
    }  
}  
```  
  
### remove  
  
从Activity中移除一个Fragment，如果被移除的Fragment没有添加到回退栈，这个Fragment实例将会被销毁。  
  
```java  
remove(Fragment fragment)  
```  
Remove an existing fragment.  If it was added to a container, its view is also removed from that container.  
  
> Fragment被remove后，Fragment的生命周期会一直执行完onDetach，之后Fragment的实例也会从FragmentManager中移除。  
  
### replace  
  
使用另一个Fragment替换当前的Fragment，实际上就是 remove(所有旧的) + add(一个新的)  
  
```java  
replace(int containerViewId, Fragment fragment)  
replace(int containerViewId, Fragment fragment, String tag)  
```  
Replace an existing fragment that was added to a container. This is essentially the same as calling remove(Fragment) for **all** currently added fragments that were added with the same containerViewId and then add(int, Fragment, String) with the same arguments given here.  
  
### show 和 hide  
  
显示一个以前被隐藏过的Fragment，或隐藏一个存在的Fragment  
  
Shows a previously以前 hidden fragment. This is only relevant适用于 for fragments whose views have been added to a container, as this will cause the view to be shown.  
```java  
show(Fragment fragment) //显示之前隐藏的Fragment  
```  
  
Hides an existing fragment. This is only relevant for fragments whose views have been added to a container, as this will cause the view to be hidden.  
```java  
hide(Fragment fragment) //隐藏当前的Fragment，仅仅是设为不可见，并不会销毁  
```  
  
Fragment被 hide/show，仅仅是隐藏/显示Fragment的视图，不会有任何生命周期方法的调用。  
在Fragment中重写onHiddenChanged方法可以对Fragment的hide和show状态进行监听。  
  
### attach 和 detach  
  
重建Fragment的view视图，或将Fragment的view视图从UI中移除  
  
  
Re-attach关联 a fragment after it had previously been detached from the UI with detach(Fragment). This causes its view hierarchy to be re-created, attached to the UI, and displayed.  
```java  
attach(Fragment fragment)  
```  
  
Detach the given fragment from the UI. This is the same state as when it is put on the **back stack**: the fragment is removed from the UI, however its **state** is still being actively managed by the fragment manager.  When going into this state its view hierarchy is destroyed.  
```java  
detach(Fragment fragment) //和remove()不同，detach后fragment的状态依然由FragmentManager维护  
```  
  
当Fragment被detach后，Fragment的生命周期执行完`onDestroyView`就终止了，这意味着Fragment的实例并没有被销毁，只是UI界面被移除了（注意和remove是有区别的）。  
  
当Fragment被detach后，执行attach操作，会让Fragment从`onCreateView`开始执行，一直执行到onResume。  
  
attach无法像add一样单独使用，单独使用会抛异常。方法存在的意义是对detach后的Fragment进行界面恢复。  
  
## addToBackStack  
  
记录已提交的事务，可用于回退操作。  
  
```java  
addToBackStack(String name) //An optional name for this back stack state, or null.  
```  
Add this transaction to the back stack. This means that the transaction will be remembered after it is committed, and will reverse its operation when later popped off the stack.  
将此事务添加到后台堆栈。这意味着该事务被提交后将会被记住后，回退操作后，事务会从堆栈中弹出。  
  
addToBackStack适用于remove、hide、show、attach、detach等所有操作。  
  
Fragment回退栈类似于Android系统为Activity维护的任务栈，如果你将Fragment添加到回退栈中，当用户点击后退按钮时，将看到上一次的保存的Fragment。一旦Fragment完全从后退栈中弹出，用户再次点击后退键时，才退出当前Activity。  
  
> add并addToBackStack后，第1次按下手机的back键，程序没有退出而是变成了白板(其实是回退了之前的add操作，相当于没有添加Fragment)，第2次按下手机的back键，因为当前回退栈已空，程序就直接退出了。  
  
## commit 系列方法  
  
提交一个事务  
  
```java  
commit() //安排一个事务的提交，一定要在Activity.onSaveInstance()之前调用  
commitAllowingStateLoss() //和commit一样，但是允许Activity的状态保存之后提交  
commitNow() //同步的提交这个事务，API_24添加  
commitNowAllowingStateLoss() //和commitNow()一样，但是允许Activity的状态保存之后提交，API_24添加的  
```  
  
commit()：安排一个针对该事务的提交。提交并没有立刻发生，会安排到在主线程下次准备好的时候来执行。  
commitNow()：同步的提交这个事务。任何被添加的Fragment都将会被初始化，并将他们完全带入他们的生命周期状态。  
  
使用commitNow()类似于先调用commit()方法，之后调通FragmentManager的 executePendingTransactions() 方法，但commitNow()比后者的这种操作更好，后者在尝试提交当前事务时会有副作用（具体的副作用是什么，API文档没有给出说明）。  
  
使用commitNow()时不能进行添加回退栈的操作，否则会抛出一个 IllegalStateException的异常。  
  
> 如果调用 Transaction 的 `commit` 方法时抛出**Can not perform this action after onSaveInstanceState**异常，可以改成使用 `commitAllowingStateLoss` 方法：  
  
[选择正确的 commitXXX 函数](https://www.open-open.com/lib/view/open1472628695314.html)  
  
## setCustomAnimations  
  
为Fragment的进入/退出设置指定的动画资源。  
  
```java  
setCustomAnimations(int enter, int exit)  
setCustomAnimations(int enter, int exit, int popEnter, int popExit)  
```  
  
该方法用于给Fragment设置自定义切换动画。四个参数方法的后两个参数也是传入指定动画资源，在某个事务被addToBackStack或回退时起动画效果。  
  
getSupportFragmentManager不支持属性动画，仅支持补间动画。getFragmentManager支持属性动画。  
  
使用setCustomAnimations一定要放在add、remove...等操作之前。  
  
## addSharedElement  
  
用于将View从一个被删除的或隐藏的Fragment映射到另一个显示或添加的Fragment上。  
  
```java  
addSharedElement(View sharedElement, String name)  
```  
  
方法作用：设置共享元素（或着说共享View），第二个参数name是这个共享元素的标识。  
[使用SharedElement切换Fragment页面](https://www.jianshu.com/p/e9f63ead8bf5)  
  
# 主界面 Tab + Fragment 实现案例  
  
实现效果类似于 ViewPage + Fragment 的组合，主要用到了Transaction的`add、show、hide`等方法。  
  
## Activity  
```java  
public class MainActivity extends FragmentActivity {  
    private TextView tvTitle, tvWeixin, tvFriend, tvContact, tvSetting;  
    private String[] titles = {"微信", "通讯录", "发现", "我"};  
    private FragmentManager fragmentManager;  
    private Fragment weixinFragment, friendFragment, contactFragment, settingFragment;  
    private int selectPosition = -1;//默认值不能设为0、1、2、3  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        initView();  
        reset(0);  
    }  
  
    private void initView() {  
        tvWeixin = findViewById(R.id.tv_tab_bottom_weixin);  
        tvFriend = findViewById(R.id.tv_tab_bottom_friend);  
        tvContact = findViewById(R.id.tv_tab_bottom_contact);  
        tvSetting = findViewById(R.id.tv_tab_bottom_setting);  
          
        tvWeixin.setTag(0);  
        tvFriend.setTag(1);  
        tvContact.setTag(2);  
        tvSetting.setTag(3);  
          
        tvWeixin.setOnClickListener(v -> reset((Integer) v.getTag()));  
        tvFriend.setOnClickListener(v -> reset((Integer) v.getTag()));  
        tvContact.setOnClickListener(v -> reset((Integer) v.getTag()));  
        tvSetting.setOnClickListener(v -> reset((Integer) v.getTag()));  
          
        tvTitle = findViewById(R.id.tv_title);  
        tvTitle.setOnClickListener(v -> Log.i("bqt", "【Fragment数量】" + fragmentManager.getFragments().size()));  
          
        fragmentManager = getSupportFragmentManager();  
        weixinFragment = fragmentManager.findFragmentByTag(titles[0]);//这一步是必须要的，否则横竖屏切换时会重复创建Fragment  
        friendFragment = fragmentManager.findFragmentByTag(titles[1]);  
        contactFragment = fragmentManager.findFragmentByTag(titles[2]);  
        settingFragment = fragmentManager.findFragmentByTag(titles[3]);  
    }  
      
    private void reset(int position) {  
        tvWeixin.setSelected(position == 0);  
        tvFriend.setSelected(position == 1);  
        tvContact.setSelected(position == 2);  
        tvSetting.setSelected(position == 3);  
          
        if (position != selectPosition) {  
            tvTitle.setText(titles[position]);  
            hideFragment();//隐藏掉选中之前正在显示的Fragment  
            showFragment(position);//显示当前选中的Fragment  
            selectPosition = position;//更新当前选中的Fragment  
        }  
    }  
      
    private void showFragment(int position) {  
        switch (position) {  
            case 0:  
                if (weixinFragment == null) {  
                    weixinFragment = MyFragment.newInstance(titles[position]);  
                    fragmentManager.beginTransaction().add(R.id.id_container, weixinFragment, titles[position]).commit();//设置tag，方便获取  
                } else {  
                    fragmentManager.beginTransaction().show(weixinFragment).commit();//使用之前要保证不为空并且目前是否隐藏状态  
                }  
                break;  
            case 1:  
                if (friendFragment == null) {  
                    friendFragment = MyFragment.newInstance(titles[position]);  
                    fragmentManager.beginTransaction().add(R.id.id_container, friendFragment, titles[position]).commit();  
                } else {  
                    fragmentManager.beginTransaction().show(friendFragment).commit();  
                }  
                break;  
            case 2:  
                if (contactFragment == null) {  
                    contactFragment = MyFragment.newInstance(titles[position]);  
                    fragmentManager.beginTransaction().add(R.id.id_container, contactFragment, titles[position]).commit();  
                } else {  
                    fragmentManager.beginTransaction().show(contactFragment).commit();  
                }  
                break;  
            case 3:  
                if (settingFragment == null) {  
                    settingFragment = MyFragment.newInstance(titles[position]);  
                    fragmentManager.beginTransaction().add(R.id.id_container, settingFragment, titles[position]).commit();  
                } else {  
                    fragmentManager.beginTransaction().show(settingFragment).commit();  
                }  
                break;  
            default:  
                break;  
        }  
    }  
      
    private void hideFragment() {  
        switch (selectPosition) {  
            case 0:  
                fragmentManager.beginTransaction().hide(weixinFragment).commit();  
                break;  
            case 1:  
                fragmentManager.beginTransaction().hide(friendFragment).commit();  
                break;  
            case 2:  
                fragmentManager.beginTransaction().hide(contactFragment).commit();  
                break;  
            case 3:  
                fragmentManager.beginTransaction().hide(settingFragment).commit();  
                break;  
            default:  
                break;  
        }  
    }  
}  
```  
  
## Fragment  
```java  
public class MyFragment extends Fragment {  
    private String name;  
    public static final String ARGUMENT = "name";  
      
    public static MyFragment newInstance(String name) {  
        MyFragment fragment = new MyFragment();  
        Bundle bundle = new Bundle();  
        bundle.putString(ARGUMENT, name);  
        fragment.setArguments(bundle);  
        return fragment;  
    }  
      
    @Override  
    public void onAttach(Context context) {  
        super.onAttach(context);  
        Log.i("bqt", "【onAttach】" + name);//在这里获取Bundle中的数据也是可以的，不过都在onCreate做初始化操作  
    }  
      
    @Override  
    public void onCreate(@Nullable Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Bundle bundle = getArguments();  
        name = bundle != null ? bundle.getString(ARGUMENT) : "unknown";  
        Log.i("bqt", "【onCreate】" + name);  
    }  
      
    @Nullable  
    @Override  
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {  
        Log.i("bqt", "【onCreateView】" + name);  
        View rootView = inflater.inflate(R.layout.fragment_tab, container, false);  
        TextView tv = rootView.findViewById(R.id.tv);  
        tv.setText(name + "：" + new SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault()).format(new Date()));  
        tv.setBackgroundColor(0xFF000000 + new Random().nextInt(0xFFFFFF));  
        return rootView;  
    }  
      
    @Override  
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {  
        super.onViewCreated(view, savedInstanceState);  
        Log.i("bqt", "【onViewCreated】");  
    }  
      
    @Override  
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
        Log.i("bqt", "【onActivityCreated】" + name);  
    }  
      
    @Override  
    public void onStart() {  
        super.onStart();  
        Log.i("bqt", "【onStart】" + name);  
    }  
      
    @Override  
    public void onResume() {  
        super.onResume();  
        Log.i("bqt", "【onResume】" + name);  
    }  
      
    @Override  
    public void onPause() {  
        super.onPause();  
        Log.i("bqt", "【onPause】" + name);  
    }  
      
    @Override  
    public void onStop() {  
        super.onStop();  
        Log.i("bqt", "【onStop】" + name);  
    }  
      
    @Override  
    public void onDestroyView() {  
        super.onDestroyView();  
        Log.i("bqt", "【onDestroyView】" + name);  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.i("bqt", "【onDestroy】" + name);  
    }  
      
    @Override  
    public void onDetach() {  
        super.onDetach();  
        Log.i("bqt", "【onDetach】" + name);  
    }  
}  
```  
  
## 布局  
```xml  
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"  
              android:layout_width="match_parent"  
              android:layout_height="match_parent"  
              android:orientation="vertical">  
  
    <TextView  
        android:id="@+id/tv_title"  
        android:layout_width="fill_parent"  
        android:layout_height="45dp"  
        android:gravity="center"  
        android:text="微信"  
        android:textColor="#000"  
        android:textSize="20sp"  
        android:textStyle="bold"/>  
  
    <View  
        android:layout_width="match_parent"  
        android:layout_height="1px"  
        android:background="#dbdbdb"/>  
  
    <FrameLayout  
        android:id="@+id/id_container"  
        android:layout_width="fill_parent"  
        android:layout_height="0dp"  
        android:layout_weight="2016"/>  
  
    <View  
        android:layout_width="match_parent"  
        android:layout_height="1px"  
        android:background="#dbdbdb"/>  
  
    <LinearLayout  
        android:id="@+id/ly_main_tab_bottom"  
        android:layout_width="fill_parent"  
        android:layout_height="55dp">  
  
        <TextView  
            android:id="@+id/tv_tab_bottom_weixin"  
            android:layout_width="0dp"  
            android:layout_height="fill_parent"  
            android:layout_weight="1"  
            android:gravity="center"  
            android:text="微信"  
            android:textColor="@drawable/text_color_sel"/>  
  
        <TextView  
            android:id="@+id/tv_tab_bottom_friend"  
            android:layout_width="0dp"  
            android:layout_height="fill_parent"  
            android:layout_weight="1"  
            android:gravity="center"  
            android:text="通讯录"  
            android:textColor="@drawable/text_color_sel"/>  
  
        <TextView  
            android:id="@+id/tv_tab_bottom_contact"  
            android:layout_width="0dp"  
            android:layout_height="fill_parent"  
            android:layout_weight="1"  
            android:gravity="center"  
            android:text="发现"  
            android:textColor="@drawable/text_color_sel"/>  
  
        <TextView  
            android:id="@+id/tv_tab_bottom_setting"  
            android:layout_width="0dp"  
            android:layout_height="fill_parent"  
            android:layout_weight="1"  
            android:gravity="center"  
            android:text="我"  
            android:textColor="@drawable/text_color_sel"/>  
    </LinearLayout>  
  
</LinearLayout>  
```  
  
2019-3-28  
