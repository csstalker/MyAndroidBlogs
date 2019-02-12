| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
事件分发机制 时间拦截 滑动冲突 MD  
***  
目录  
===  

- [事件分发机制分析案例](#事件分发机制分析案例)
	- [默认行为](#默认行为)
		- [试验 0](#试验-0)
		- [结论](#结论)
	- [dispatchTouchEvent 返回 true](#dispatchtouchevent-返回-true)
		- [试验 1](#试验-1)
		- [试验 2](#试验-2)
		- [结论](#结论)
	- [onInterceptTouchEvent 返回 true](#onintercepttouchevent-返回-true)
		- [试验 3](#试验-3)
		- [结论](#结论)
	- [onTouchEvent 返回 true](#ontouchevent-返回-true)
		- [试验 4](#试验-4)
		- [结论](#结论)
		- [试验 5](#试验-5)
	- [测试代码](#测试代码)
		- [Activity](#activity)
		- [自定义 LinearLayout](#自定义-linearlayout)
		- [自定义 TextView](#自定义-textview)
		- [工具类](#工具类)
- [一些面试题](#一些面试题)
- [onTouch 和 onTouchEvent 的区别](#ontouch-和-ontouchevent-的区别)
  
# 事件分发机制分析案例  
![](index_files/bc6f8abf-6e9b-4828-9574-91b63848f499.png)  
  
## 默认行为  
触摸事件由`Action_Down`==0、`Aciton_UP`==1、`Action_Move`==2组成，其中一次完整的触摸事件中，Down只有一个、Up有一个或0个、Move有若干个(包括0个)，一旦UP发生，就表明此次触摸事件已经结束了。  
  
### 试验 0  
1、当触摸根部的`LinearLayout`时：  
```c  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
onTouch--root--按下  
onTouchEvent--root--按下  
```  
  
2、当触摸`子TextView`时：  
```c  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
  
dispatchTouchEvent--TextView--tv--按下  
onTouch--tv--按下  
onTouchEvent--tv--按下  
  
onTouch--root--按下  
onTouchEvent--root--按下  
```  
  
3、当触摸`子LinearLayout`：  
```c  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
  
dispatchTouchEvent--ll_child--按下  
onInterceptTouchEvent--ll_child--按下  
onTouch--ll_child--按下  
onTouchEvent--ll_child--按下  
  
onTouch--root--按下  
onTouchEvent--root--按下  
```  
  
4、当触摸`孙子TextView`时：  
```c  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
  
dispatchTouchEvent--ll_child--按下  
onInterceptTouchEvent--ll_child--按下  
  
dispatchTouchEvent--TextView--tv_child--按下  
onTouch--tv_child--按下  
onTouchEvent--tv_child--按下  
  
onTouch--ll_child--按下  
onTouchEvent--ll_child--按下  
  
onTouch--root--按下  
onTouchEvent--root--按下  
```  
  
5、当触摸一个具有点击事件的`普通的子TextView`时：  
```java  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
dispatchTouchEvent--TextView--null--按下  
onTouchEvent--null--按下  
  
dispatchTouchEvent--root--移动  
onInterceptTouchEvent--root--移动  
dispatchTouchEvent--TextView--null--移动  
onTouchEvent--null--移动  
//...后续所有Move事件都会收到  
  
dispatchTouchEvent--root--松开  
onInterceptTouchEvent--root--松开  
dispatchTouchEvent--TextView--null--松开  
onTouchEvent--null--松开  
```  
  
### 结论  
在`不进行如何干涉`的情况下:  
  
> 即要满足以下所有条件：  
- 触摸点区域没有一个View的`dispatchTouchEvent`方法的返回值为true  
- 触摸点区域没有一个View的`onTouchEvent`方法和`onTouch`方法的返回值为true  
- 触摸点区域没有一个View有设置`点击事件、长点击事件`等会消耗触摸事件的方法  
  
两个基本规律：  
- View的`onInterceptTouchEvent`方法是在`dispatchTouchEvent`方法中被调用的(如果`dispatchTouchEvent`方法返回true，则不会调用`onInterceptTouchEvent`方法)，所以可以把`dispatchTouchEvent`方法和`onInterceptTouchEvent`方法看做一对方法。  
- View的`onTouchEvent`方法是在监听器回调方法`onTouch`之后被调用的，所以也可以把`onTouch`方法和`onTouchEvent`方法看做一对方法。  
  
这么一划分的话，事件的分发过程就可以简化为对两对方法进行分析了(事实上，这种划分是合理的)。  
  
**对于 Down 事件**  
- Down事件首先会从root逐级【下发】到最底层的View，下发的事件是通过`dispatchTouchEvent`方法传递的(当然，因为`dispatchTouchEvent`方法没有返回true，所以还会将Down事件传给自己的`onInterceptTouchEvent`方法，下同)。  
- 当Down事件从root逐级下发到最底层的那个View后，最底层的那个View的`dispatchTouchEvent`会将Down事件传给自己的 `onTouch`方法(当然因为`onTouch`没有返回true，所以还会将Down事件传给自己的`onTouchEvent`方法，下同)。  
- 由此便开始了Down事件的逐级【上传】过程，即Down事件将从最底层的那个View开始，逐级【上传】到root的 `onTouch`(和 `onTouchEvent`方法)。  
  
> Down事件的传递过程：  
dTE父(包含-->oIT) --> dTE子 --> ... --> dTE底 --> `onTouch底(包含-->onTE)` --> ... --> onTouch子 --> onTouch父。  
  
**对于后续的 Move、UP 事件**  
- 在分发完 Down 事件后( Down 事件一定会分发)，其余的 Move、UP 事件将不再分发。  
- 也即任何 View 都不会收到后续的 Move、UP 事件，因为没有任何 View 需要处理本次触摸事件。  
  
> 注意，上述案例中的第五种情况不满足上述条件，因为那个TextView具有点击事件，所以会导致能收到后续的触摸事件  
  
## dispatchTouchEvent 返回 true  
### 试验 1  
我们将`子LinearLayout`的 `dispatchTouchEvent` 方法返回`true`，当触摸`子LinearLayout`、`孙子TextView`时：  
```java  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
dispatchTouchEvent--ll_child--按下  
  
dispatchTouchEvent--root--移动  
onInterceptTouchEvent--root--移动  
dispatchTouchEvent--ll_child--移动  
//...后续所有Move事件都会收到  
  
dispatchTouchEvent--root--松开  
onInterceptTouchEvent--root--松开  
dispatchTouchEvent--ll_child--松开  
```  
  
### 试验 2  
我们将`孙子TextView`的 `dispatchTouchEvent` 方法返回`true`，当触摸`孙子TextView`时：  
```java  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
dispatchTouchEvent--ll_child--按下  
onInterceptTouchEvent--ll_child--按下  
dispatchTouchEvent--TextView--tv_child--按下  
  
dispatchTouchEvent--root--移动  
onInterceptTouchEvent--root--移动  
dispatchTouchEvent--ll_child--移动  
onInterceptTouchEvent--ll_child--移动  
dispatchTouchEvent--TextView--tv_child--移动  
//...后续所有Move事件都会收到  
  
dispatchTouchEvent--root--松开  
onInterceptTouchEvent--root--松开  
dispatchTouchEvent--ll_child--松开  
onInterceptTouchEvent--ll_child--松开  
dispatchTouchEvent--TextView--tv_child--松开  
```  
  
### 结论  
- 如果某个View的`dispatchTouchEvent`方法返回值为true(不管是普通View还是ViewGroup)，则表明 **这个具体的事件** 的分发过程到此结束了。  
- 所以此View的所有`子View`都不会获取到**这一个具体的事件**。  
- 但是此View的所有`父View`可以获取到此View能获取到的所有事件，因为此View获取的任何事件都是由根View逐级下发过来的。  
- 注意，分发结束只是针对当前**这个具体的MotionEvent事件**而言的，而不是针对整个**事件链**而言的，所以到后续所有事件仍会正常进行分发。  
- 所以，即使你在`dispatchTouchEvent`中没做任何判断就直接返回了true，后续所有的事件仍会正常进行分发。  
- 鉴于`dispatchTouchEvent`方法只是终止了**某一具体事件**的分发，而不是终止本次完整事件的分发，所以如果想在某种情况下终止接收后续的事件，用这个方法是不适合的。  
  
## onInterceptTouchEvent 返回 true  
> PS:只有 ViewGroup 才有 onInterceptTouchEvent 方法  
  
### 试验 3  
我们将`子LinearLayout`的 `onInterceptTouchEvent` 方法返回`true`，当触摸`子LinearLayout`、`孙子TextView`时：  
```java  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
dispatchTouchEvent--ll_child--按下  
onInterceptTouchEvent--ll_child--按下  
  
onTouch--ll_child--按下  
onTouchEvent--ll_child--按下  
onTouch--root--按下  
onTouchEvent--root--按下  
```  
  
### 结论  
- 如果一个 `ViewGroup` 的`onInterceptTouchEvent`方法返回 true，则代表此 ViewGroup 会拦截**后续所有事件**，因此，其`子View`后续将收不到`任何Touch事件`。  
- 注意，`ViewGroup`拦截事件后会将当前事件传递给自己的 `onTouch` 和 `onTouchEvent` 方法，而**不会终止事件的分发**。  
- 因此，我们可以认为：如果一个 `ViewGroup` 的`onInterceptTouchEvent`方法返回 true，则可以等价的认为这个`ViewGroup`隐藏了其所有`子View`(或者认为其没有任何子View)，而其他过程和正常的事件分发过程完全一致。  
- 鉴于此，我们经常是在`onInterceptTouchEvent`方法中拦截`子View`获取事件(目的往往是为了让自己能够处理后续事件)。  
  
`onInterceptTouchEvent` 和 `dispatchTouchEvent` 方法的注意区别：  
- 返回 true 后，`onInterceptTouchEvent` 则会拦截后续所有事件的分发，而 `dispatchTouchEvent` 只是终止当前这一具体事件的分发。  
- `onInterceptTouchEvent` 会继续正常分发这一具体的事件，而 `dispatchTouchEvent`会直接结束分发这一具体的事件。  
  
## onTouchEvent 返回 true  
  
### 试验 4  
我们将`子LinearLayout`的`onTouchEvent`方法返回true  
  
当触摸`子LinearLayout`时：  
```java  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
dispatchTouchEvent--ll_child--按下  
onInterceptTouchEvent--ll_child--按下  
onTouch--ll_child--按下  
onTouchEvent--ll_child--按下  
  
dispatchTouchEvent--root--移动  
onInterceptTouchEvent--root--移动  
dispatchTouchEvent--ll_child--移动  
onTouch--ll_child--移动  
onTouchEvent--ll_child--移动  
//...后续所有Move事件都会收到  
  
dispatchTouchEvent--root--松开  
onInterceptTouchEvent--root--松开  
dispatchTouchEvent--ll_child--松开  
onTouch--ll_child--松开  
onTouchEvent--ll_child--松开  
```  
  
当触摸`孙子TextView`时：  
```java  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
dispatchTouchEvent--ll_child--按下  
onInterceptTouchEvent--ll_child--按下  
dispatchTouchEvent--TextView--tv_child--按下  
onTouch--tv_child--按下  
onTouchEvent--tv_child--按下  
onTouch--ll_child--按下  
onTouchEvent--ll_child--按下  
  
dispatchTouchEvent--root--移动  
onInterceptTouchEvent--root--移动  
dispatchTouchEvent--ll_child--移动  
onTouch--ll_child--移动  
onTouchEvent--ll_child--移动  
//...后续所有Move事件都会收到  
  
dispatchTouchEvent--root--松开  
onInterceptTouchEvent--root--松开  
dispatchTouchEvent--ll_child--松开  
onTouch--ll_child--松开  
onTouchEvent--ll_child--松开  
```  
  
### 结论  
首先可以发现，当触摸`子LinearLayout`和`孙子TextView`时，两者的过程基本都是一致的，所以下面的关键是分析对比着两者的区别。  
  
**对于DOWN事件**  
- 【下发过程】：当触摸`孙子TextView`时，虽然`子LinearLayout`的`onTouchEvent`方法返回了true，但是`孙子TextView`的`dispatchTouchEvent`方法仍会被调用，所以，View的`onTouchEvent`方法返回true并不会影响`DOWN`事件的正常下发。  
- 【上传过程】：首先`DOWN`事件会从最底层的`孙子TextView`的`onTouchEvent`方法上传给`子LinearLayout`的`onTouchEvent`方法，然后`DOWN`事件的上传过程就结束了，而不会继续上传给`父View`。  
  
结论：  
- `onTouchEvent`方法的返回值并不会影响`DOWN`事件的正常`下发`过程。  
- 如果一个View的`onTouchEvent`方法返回true，那么`DOWN`事件的`上传`过程将会在这里停止，所以此View所有`父View`的`onTouch`方法和`onTouchEvent`方法都将不会被调用。  
- 换句话说，如果一个View的`onTouchEvent`方法返回true，那么`DOWN`事件将会在传递到View的`onTouchEvent`方法后被完全消耗掉。  
  
**对于后续的 MOVE、UP 事件**  
- 所触摸的那个View`及`所有`父View`的`dispatchTouchEvent`方法和`onInterceptTouchEvent`方法【都会】被调用。  
- 所触摸的那个View的所有`子View`的`dispatchTouchEvent`方法和`onInterceptTouchEvent`方法【都不会】被调用。  
- 仅所触摸的那个View的`onTouch`方法和`onTouchEvent`方法会被调用，换句话说就是，仅`onTouchEvent`返回true的那个View的`onTouchEvent`方法能够处理`MOVE、UP`事件。  
- 所以，`onTouchEvent`方法最核心的作用是用来告诉View树：后续的`MOVE、UP`事件到底应该从根View【传递】到哪个View去处理(PS："传递"这个词用的非常好)。  
  
**完整的流程为**  
- 一旦某个View的【onTouchEvent】返回true，当【DOWN】事件按照【正常的分发流程】逐级【下传】到【最底层的View】的【dispatchTouchEvent】后，【最底层的View】将通过其【onTouchEvent】逐级【上传】，直到上传到【此View】的【onTouchEvent】方法之后，DOWN事件将会被消耗掉；  
- 然而，此后的【MOVE、UP】事件在逐级【下传】到【此View】后将直接【结束】分发(而不会下传到最底层的View)，并且在调用【此View】的onTouchEvent方法后被完全【消耗】掉。  
  
### 试验 5  
我们将`孙子TextView`的`onTouchEvent`方法返回true  
  
当触摸`孙子TextView`时：  
```java  
dispatchTouchEvent--root--按下  
onInterceptTouchEvent--root--按下  
dispatchTouchEvent--ll_child--按下  
onInterceptTouchEvent--ll_child--按下  
dispatchTouchEvent--TextView--tv_child--按下  
onTouch--tv_child--按下  
onTouchEvent--tv_child--按下  
  
dispatchTouchEvent--root--移动  
onInterceptTouchEvent--root--移动  
dispatchTouchEvent--ll_child--移动  
onInterceptTouchEvent--ll_child--移动  
dispatchTouchEvent--TextView--tv_child--移动  
onTouch--tv_child--移动  
onTouchEvent--tv_child--移动  
  
dispatchTouchEvent--root--松开  
onInterceptTouchEvent--root--松开  
dispatchTouchEvent--ll_child--松开  
onInterceptTouchEvent--ll_child--松开  
dispatchTouchEvent--TextView--tv_child--松开  
onTouch--tv_child--松开  
onTouchEvent--tv_child--松开  
```  
  
可以发现和上面的情况完全一致，这里之所以分开，是因为上面的篇幅太长了，放在一起的话会让人比较难把握住重点信息。  
  
## 测试代码  
### Activity  
```java  
public class TouchEventActivity extends Activity implements OnTouchListener {  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.layout);  
          
        findViewById(R.id.root).setTag("root");  
        findViewById(R.id.ll_child).setTag("ll_child");  
        findViewById(R.id.tv).setTag("tv");  
        findViewById(R.id.tv_child).setTag("tv_child");  
          
        findViewById(R.id.root).setOnTouchListener(this);  
        findViewById(R.id.ll_child).setOnTouchListener(this);  
        findViewById(R.id.tv).setOnTouchListener(this);  
        findViewById(R.id.tv_child).setOnTouchListener(this);  
          
        findViewById(R.id.tv_alert).setOnClickListener(v -> {  
            Utils.TYPE = (Utils.TYPE + 1) % 6;  
            Toast.makeText(this, "值为 " + Utils.TYPE, Toast.LENGTH_SHORT).show();  
        });  
    }  
      
    @Override  
    protected void onResume() {  
        super.onResume();  
        Toast.makeText(this, "值为 " + Utils.TYPE, Toast.LENGTH_SHORT).show();  
    }  
      
    @Override  
    public boolean onTouch(View v, MotionEvent event) {  
        Log.i("【bqt】", "onTouch--" + v.getTag() + "--" + Utils.getActionName(event));  
        return false;  
    }  
}  
```  
  
### 自定义 LinearLayout  
```java  
public class MyLinearLayout extends LinearLayout {  
      
    public MyLinearLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
      
    @Override  
    public boolean dispatchTouchEvent(MotionEvent event) {  
        Log.i("【bqt】", "dispatchTouchEvent--" + getTag() + "--" + Utils.getActionName(event));  
        return (getId() == R.id.ll_child && Utils.TYPE == 1) || super.dispatchTouchEvent(event);  
    }  
      
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent event) {  
        Log.i("【bqt】", "onInterceptTouchEvent--" + getTag() + "--" + Utils.getActionName(event));  
        return (getId() == R.id.ll_child && Utils.TYPE == 3) || super.onInterceptTouchEvent(event);  
    }  
      
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        Log.i("【bqt】", "onTouchEvent--" + getTag() + "--" + Utils.getActionName(event));  
        return (getId() == R.id.ll_child && Utils.TYPE == 4) || super.onTouchEvent(event);  
    }  
}  
```  
  
### 自定义 TextView  
```java  
public class MyTextView extends AppCompatTextView {  
      
    public MyTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
      
    @Override  
    public boolean dispatchTouchEvent(MotionEvent event) {  
        Log.i("【bqt】", "dispatchTouchEvent--TextView--" + getTag() + "--" + Utils.getActionName(event));  
        return (getId() == R.id.tv_child && Utils.TYPE == 2) || super.dispatchTouchEvent(event);  
    }  
      
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        Log.i("【bqt】", "onTouchEvent--" + getTag() + "--" + Utils.getActionName(event));  
        return (getId() == R.id.tv_child && Utils.TYPE == 5) || super.onTouchEvent(event);  
    }  
}  
```  
  
### 工具类  
```java  
public class Utils {  
    public static int TYPE = 0;  
      
    public static String getActionName(MotionEvent event) {  
        switch (event.getAction()) {  
            case ACTION_DOWN:  
                return "按下";  
            case ACTION_UP:  
                return "松开";  
            case ACTION_MOVE:  
                return "移动";  
            default:  
                return "未知";  
        }  
    }  
}  
```  
  
# 一些面试题  
**View的onTouchEvent方法，OnClickListerner的OnClick方法，OnTouchListener的onTouch方法，这三者的优先级如何？**  
onTouch ＞ onTouchEvent  ＞ OnClick  
  
**如果某个view(不包括VG)处理事件的时候没有消耗down事件，会有什么结果？**  
假如一个view，在down事件来的时候他的`onTouchEvent`返回false， 那么这个down事件所属的`事件序列`，就是他`后续的move和up`都不会给他处理了。  
  
> 注意：之所以不包括VG绝不是因为对于`后续的move和up`，此view的父View的onTouchEvent会回调，而只是因为任何view所接收到的事件都是通过VG传递过来的。  
  
**一旦有事件传递给view(不包括VG)，view的onTouchEvent一定会被调用吗？**  
一定会(一定要三思，否则多半会回答"不一定会")。  
因为view本身没有`onInterceptTouchEvent`方法，所以只要事件来到view这里就一定会走`onTouchEvent`方法，并且默认都是返回true的，除非这个view是不可点击的。  
  
> PS:所谓不可点击就是`clickable`和`longClickable`同时为fale。  
 Button的clickable就是true，但是textview是false。  
  
**enable是否影响view的onTouchEvent返回值？**  
不影响  
只要`clickable和longClickable`有一个为真，那么onTouchEvent就返回true  
  
**requestDisallowInterceptTouchEvent 的作用？**  
可以`在子元素中干扰父元素的事件分发`  
但是down事件干扰不了。  
  
**onTouchEvent和GestureDetector在什么时候用哪个比较好？**  
只有滑动需求的时候就用onTouchEvent  
如果有双击、抛掷等行为的时候就用GestureDetector  
  
**滑动冲突问题如何解决？**  
要解决滑动冲突，其实主要的就是一个核心思想：`你到底想让哪个 view 来响应你的滑动？`  
  
比如，从上到下滑，是哪个view来处理这个事件，从左到右呢？  
  
业务需求想明白以后，解决的方法就是2个：`外部拦截(父亲拦截)和内部拦截`，基本上所有的滑动冲突都是这2种的变种，而且核心代码思想都一样。  
- 外部拦截法：思路就是`重写父容器的onInterceptTouchEvent方法`，子元素一般不需要做额外的处理。我们通常是对父View的`onInterceptTouchEvent`接收到的Move事件做一个`过滤`，当Move事件满足适当条件时(如持续若干时间或移动若干距离)会拦截掉，并返回子View一个`Action_Cancel`事件。这种方式比较简单且很容易让人理解。  
- 内部拦截法：思路就是父容器不管，在子View中调用`getParent().requestDisallowInterceptTouchEvent(true)`，作用是告诉父view，这个触摸事件由我来处理，不要阻碍我。  
  
# onTouch 和 onTouchEvent 的区别  
对于View，我们可以通过重写`onTouchEvent`方法来处理Touch事件，也可以通过实现`OnTouchListener`的接口，然后在`onTouch`方法中达到同样的目的，这两种监听有什么区别呢？  
  
这两个方法都是在View的`dispatchTouchEvent`中调用的，`onTouch`优先于`onTouchEvent`执行。如果在`onTouch`方法中通过返回true将事件消费掉，`onTouchEvent`将不会再执行。  
  
另外需要注意的是，`onTouch`能够得到执行需要两个前提条件，第一`mOnTouchListener`的值不能为空，第二当前点击的控件必须是`enable`的。因此如果你有一个控件是非`enable`的，那么给它注册`onTouch`事件将永远得不到执行。对于这一类控件，如果我们想要监听它的touch事件，就必须通过在该控件中重写`onTouchEvent`方法来实现。  
  
总结  
- 1、`onTouchListener`的`onTouch`方法优先级比`onTouchEvent`方法高，会先触发。  
- 2、假如`onTouch`方法返回false会接着触发`onTouchEvent`方法，反之`onTouchEvent`方法不会被调用。  
- 3、内置诸如click事件的实现等等都基于`onTouchEvent`，假如`onTouch`返回true，这些事件将不会被触发。  
  
2017-10-14  
