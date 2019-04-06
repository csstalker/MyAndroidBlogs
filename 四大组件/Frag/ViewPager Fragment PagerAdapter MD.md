| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [简介](#简介)
	- [PagerAdapter](#PagerAdapter)
	- [FragmentPagerAdapter](#FragmentPagerAdapter)
	- [FragmentStatePagerAdapter](#FragmentStatePagerAdapter)
	- [ViewPager 的预加载](#ViewPager-的预加载)
	- [getCount 的返回值](#getCount-的返回值)
	- [如何自定义指示器](#如何自定义指示器)
- [代码](#代码)
	- [Activity](#Activity)
	- [Fragment](#Fragment)
	- [Activity 的布局](#Activity-的布局)
	- [Fragment 的布局](#Fragment-的布局)
- [测试](#测试)
	- [启动后](#启动后)
	- [滑到第二个页面](#滑到第二个页面)
	- [滑到第一个页面](#滑到第一个页面)
	- [最小化后再回到前台](#最小化后再回到前台)
	- [再次滑到第二个页面](#再次滑到第二个页面)
	- [在第二个页面横竖屏切换](#在第二个页面横竖屏切换)
	- [在第一个页面横竖屏切换](#在第一个页面横竖屏切换)
  
# 简介  
  
PagerAdapter 是android.support.v4包中的类，它的子类有FragmentPagerAdapter, FragmentStatePagerAdapter，相比通用的 PagerAdapter，后两个adapter 专注于每一页均为 Fragment 的情况。  
  
```java  
android.support.v4.app.Fragment;  
android.support.v4.app.FragmentActivity;  
android.support.v4.app.FragmentManager;  
android.support.v4.app.FragmentPagerAdapter;  
android.support.v4.app.FragmentStatePagerAdapter;  
android.support.v4.view.PagerAdapter;  
android.support.v4.view.ViewPager;  
android.support.v4.view.ViewPager.OnPageChangeListener;  
```  
  
`FragmentPagerAdapter` 类内的每一个生成的 Fragment 都将保存在内存之中，因此适用于那些相对静态、数量也比较少的那种。比如说一个有3-5个tab标签的fragment滑动界面，FragmentPagerAdapter 会`对我们浏览过所有Fragment进行缓存`，保存这些界面的临时状态，这样当我们左右滑动的时候，界面切换更加的流畅。但是，这样也会增加程序占用的内存。  
  
> 注意，上面所说的FragmentPagerAdapter只是会把【Fragment本身】保存在内存中，而不会把【Fragment中的View】也保存在内存中。实际上，除了当前页面及左右相邻两个页面中的Fragment中的【View】会被保存到内存中外，其余Fragment中的【View】都会被销毁。也即，所有浏览过的Fragment的【onDestroy】方法正常情况下都不会被调用，但是除了当前页面及左右相邻两个页面外，Fragment的【onDestroyView】方法都会被调用。  
  
如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，应该使用 `FragmentStatePagerAdapter`。  
  
FragmentStatePagerAdapter也是PagerAdapter的子类，这个适配器对实现多个Fragment界面的滑动是非常有用的。它的工作方式和listview是非常相似的，当Fragment对用户不可见的时候，`整个Fragment会被销毁`(当然，为了保证滑动时流畅，和所有PagerAdapter一样，左右两个页面的Fragment会被保留)，而不像FragmentPagerAdapter那样，只是销毁Fragment中的View。  
  
## PagerAdapter  
  
PageAdapter 是 ViewPager 的支持者，ViewPager 将调用它来取得所需显示的页，而 PageAdapter 也会在数据变化时通知 ViewPager。这个类也是 FragmentPagerAdapter 以及 FragmentStatePagerAdapter 的基类。如果继承自该类，至少需要实现 instantiateItem(), destroyItem(), getCount() 以及 isViewFromObject()。  
  
**getItemPosition()**  
- 该函数用以返回给定对象的位置，给定对象是由 instantiateItem() 的返回值。  
- 在 ViewPager.dataSetChanged() 中将对该函数的返回值进行判断，以决定是否最终触发 PagerAdapter.instantiateItem() 函数。  
- 在 PagerAdapter 中的实现是直接返回 POSITION_UNCHANGED，如果该函数不被重载，则会一直返回 POSITION_UNCHANGED，从而导致 ViewPager.dataSetChanged() 被调用时，认为不必触发 PagerAdapter.instantiateItem()。很多人因为没有重载该函数，而导致调用 PagerAdapter.notifyDataSetChanged() 后什么都没有发生。  
  
```java  
Called when the host view is attempting to determine确定 if an item's position has changed. Returns POSITION_UNCHANGED if the position of the given item has not changed or POSITION_NONE if the item is no longer present存在 in the adapter.  
  
The default implementation assumes that items will never change position and always returns POSITION_UNCHANGED.  
  
@param object Object representing an item, previously returned by a call to instantiateItem(View, int).  
@return object's new position index from [0, getCount()-1], POSITION_UNCHANGED if the object's position has not changed, or POSITION_NONE if the item is no longer present.  
  
public int getItemPosition(@NonNull Object object) {  
    return POSITION_UNCHANGED;  
}  
```  
  
**instantiateItem()**  
在每次 ViewPager 需要一个用以显示的 Object 的时候，该函数都会被 ViewPager.addNewItem() 调用。  
这个方法虽然不是抽象方法，但是必须重写，否则默认实现是抛出异常。  
  
```java  
Create the page for the given position.    
The adapter is responsible负责 for adding the view to the container given here, although it only must ensure this is done by the time it returns from finishUpdate(ViewGroup).  
  
@param container The containing View in which the page will be shown.  
@param position The page position to be instantiated.  
@return Returns an Object representing the new page. This does not need to be a View, but can be some other container of the page.  
  
public Object instantiateItem(ViewGroup container, int position) {  
    return instantiateItem((View) container, position);  
}  
public Object instantiateItem(View container, int position) {  
    throw new UnsupportedOperationException("Required method instantiateItem was not overridden");  
}  
```  
  
**destroyItem**  
用于销毁一个 page。  
这个方法虽然不是抽象方法，但是必须重写，否则默认实现是抛出异常。  
  
```java  
Remove a page for the given position.   
The adapter is responsible负责 for removing the view from its container, although it only must ensure this is done by the time it returns from finishUpdate(ViewGroup).  
  
@param container The containing View from which the page will be removed.  
@param position The page position to be removed.  
@param object The same object that was returned by instantiateItem(View, int).  
  
public void destroyItem(ViewGroup container, int position, Object object) {  
    destroyItem((View) container, position, object);  
}  
  
public void destroyItem(View container, int position, Object object) {  
    throw new UnsupportedOperationException("Required method destroyItem was not overridden");  
}  
```  
  
**notifyDataSetChanged()**  
在数据集发生变化的时候，一般 Activity 会调用 PagerAdapter.notifyDataSetChanged()，以通知 PagerAdapter，而 PagerAdapter 则会通知在自己这里注册过的所有 DataSetObserver，其中之一就是在 ViewPager.setAdapter() 中注册过的 PageObserver。PageObserver 则进而调用 ViewPager.dataSetChanged()，从而导致 ViewPager 开始触发更新其内含 View 的操作。  
  
## FragmentPagerAdapter  
FragmentPagerAdapter 继承自 PagerAdapter。相比通用的 PagerAdapter，该类更专注于每一页均为 Fragment 的情况。如文档所述， 该类内的每一个生成的 Fragment 都将保存在内存之中，因此适用于那些相对静态的页，数量也比较少的那种；如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，应该使用FragmentStatePagerAdapter。FragmentPagerAdapter 重载实现了几个必须的函数，因此来自 PagerAdapter 的函数，我们只需要实现 getCount() 即可。且由于 FragmentPagerAdapter.instantiateItem() 的实现中，调用了一个新增的虚函数 getItem()，因此，我们还至少需要实现一个 getItem()。因此，总体上来说，相对于继承自 PagerAdapter，更方便一些。  
  
**getItem()**  
该类中新增的一个`虚函数`，函数的目的为生成新的 Fragment 对象，重载该函数时需要注意这一点。在需要时，该函数将被 instantiateItem() 所调用。  
  
> 虚函数是C++中的概念，简单地说，那些被virtual关键字修饰的成员函数，就是虚函数。虚函数的作用，用专业术语来解释就是实现多态性(将接口与实现进行分离)；用形象的语言来解释就是实现以共同的方法，但因个体差异而采用不同的策略。  
  
如果需要向 Fragment 对象传递相对静态的数据时，我们一般通过 Fragment.setArguments() 来进行，这部分代码应当放到 getItem()，它们`只会在新生成 Fragment 对象时执行一遍`。  
  
> 如果需要在`生成 Fragment 对象后`，将数据集里面一些动态的数据传递给该 Fragment，那么，这部分代码不适合放到 getItem() 中。因为当数据集发生变化时，往往对应的 Fragment 已经生成，如果传递数据部分代码放到了 getItem() 中，`这部分代码将不会被调用`。这也是为什么很多人发现调用 PagerAdapter.notifyDataSetChanged() 后，getItem() 没有被调用的一个原因。  
  
```java  
//Return the Fragment associated with a specified position.  
public abstract Fragment getItem(int position);  
```  
  
**instantiateItem()**  
函数中判断一下要生成的 Fragment 是否已经生成过了，如果生成过了，就使用旧的，旧的将被 attach；如果没有，就调用 getItem() 生成一个新的，新的对象将被 FragmentTransation.add()。  
  
FragmentPagerAdapter 会将所有生成的 Fragment 对象`通过 FragmentManager 保存起来`备用，以后需要该 Fragment 时，都会`从 FragmentManager 读取，而不会再次调用 getItem() 方法`。  
  
> 如果需要在生成 Fragment 对象后，将数据集中的一些数据传递给该 Fragment，这部分代码应该放到这个函数的重载里。在我们继承的子类中，重载该函数，并调用 FragmentPagerAdapter.instantiateItem() 取得该函数返回 Fragment 对象，然后，我们通过该 Fragment 对象中对应的方法，将数据传递过去，然后返回该对象。否则，如果将这部分传递数据的代码放到 getItem()中，在 PagerAdapter.notifyDataSetChanged() 后，这部分数据设置代码将不会被调用。  
  
```java  
public Object instantiateItem(ViewGroup container, int position) {  
    if (mCurTransaction == null) mCurTransaction = mFragmentManager.beginTransaction();  
    final long itemId = getItemId(position);  
  
    // Do we already have this fragment?  
    String name = makeFragmentName(container.getId(), itemId);  
    Fragment fragment = mFragmentManager.findFragmentByTag(name);  
    if (fragment != null) {  
        mCurTransaction.attach(fragment); //非首次初始化为attach  
    } else {  
        fragment = getItem(position); //首次初始化为add  
        mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), itemId));  
    }  
    if (fragment != mCurrentPrimaryItem) {  
        fragment.setMenuVisibility(false);  
        FragmentCompat.setUserVisibleHint(fragment, false); //非常重要的 setUserVisibleHint 冒出来了  
    }  
    return fragment;  
}  
```  
  
**destroyItem()**  
该函数被调用后，会对 Fragment 进行 FragmentTransaction.detach()。这里`不是 remove()，只是 detach()`，因此 Fragment 还在 FragmentManager 管理中，Fragment 所占用的资源不会被释放。  
  
```java  
public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {  
    if (this.mCurTransaction == null) {  
        this.mCurTransaction = this.mFragmentManager.beginTransaction();  
    }  
    this.mCurTransaction.detach((Fragment)object); //为毛要调用detach？  
}  
```  
  
## FragmentStatePagerAdapter  
  
FragmentStatePagerAdapter 和前面的 FragmentPagerAdapter 一样，是继承自 PagerAdapter。但是，和 FragmentPagerAdapter 不一样的是，正如其类名中的 'State' 所表明的含义一样，该 PagerAdapter 的实现将只保留当前页面，`当页面离开视线后，就会被消除，释放其资源`；而在页面需要显示时，生成新的页面(就像 ListView 的实现一样)。这么实现的好处就是当拥有大量的页面时，不必在内存中占用大量的内存。  
  
**getItem()**  
一个该类中新增的虚函数，函数的目的为生成新的 Fragment 对象。Fragment.setArguments() 这种只会在新建 Fragment 时执行一次的参数传递代码，可以放在这里。  
  
由于 FragmentStatePagerAdapter.instantiateItem() 在大多数情况下，都将调用 getItem() 来生成新的对象，因此如果在该函数中放置与数据集相关的 setter 代码，基本上都可以在 instantiateItem() 被调用时执行，但这和设计意图不符。毕竟还有部分可能是不会调用 getItem() 的。因此这部分代码应该放到 instantiateItem() 中。  
  
**instantiateItem()**  
除非碰到 FragmentManager 刚好从 SavedState 中恢复了对应的 Fragment 的情况外，该函数将会调用 getItem() 函数，生成新的 Fragment 对象。新的对象将被 FragmentTransaction.add()。  
FragmentStatePagerAdapter 就是通过这种方式，每次都创建一个新的 Fragment，而在不用后就立刻释放其资源，来达到节省内存占用的目的的。  
  
**destroyItem()**  
将 Fragment 移除，即调用 FragmentTransaction.`remove()`，并释放其资源。  
  
```java  
public void destroyItem(ViewGroup container, int position, Object object) {  
    Fragment fragment = (Fragment) object;  
    if (mCurTransaction == null) mCurTransaction = mFragmentManager.beginTransaction();  
    while (mSavedState.size() <= position) {  
        mSavedState.add(null);  
    }  
    mSavedState.set(position, fragment.isAdded() ? mFragmentManager.saveFragmentInstanceState(fragment) : null);  
    mFragments.set(position, null);  
    mCurTransaction.remove(fragment); //移除  
}  
```  
  
## ViewPager 的预加载  
  
ViewPager里面定义了一个`mOffscreenPageLimit`：  
```java  
private int mOffscreenPageLimit = DEFAULT_OFFSCREEN_PAGES;  
```  
  
默认值是1，这表示你的预加载的页面数量是1。在使用ViewPager与Fragment的时候，这个值的效果是，ViewPager会自动缓存1页内的数据，当我们当前处在页面2的时候，页面1和页面3的View实际上已经创建好了，所以在我们拖动的时候是可以看见他们的界面的。但是当我们的页面处在1的时候，页面3实际上就已经销毁了(**这句话一定要正确理解**)，直到跳转到页面2的时候，页面3才会创建(**这句话也一定要正确理解**)。  
  
如果想预加载更多页面，可以通过`setOffscreenPageLimit`设置你要预加载页面的数量。特别对于应用的首页来说，我们为了更好的用户体验，可以设置 mOffscreenPageLimit 的值为 `tab数量-1` 以实现预加载全部tab的效果()。  
  
```java  
public void setOffscreenPageLimit(int limit) {  
    if (limit < 1) {  
        Log.w("ViewPager", "Requested offscreen page limit " + limit + " too small; defaulting to " + 1);  
        limit = 1;  
    }  
    if (limit != this.mOffscreenPageLimit) {  
        this.mOffscreenPageLimit = limit;  
        this.populate();  
    }  
}  
```  
  
## getCount 的返回值  
**返回值意义**  
- 若getCount()返回为0，则viewpage上不会显示任何内容(不会报错)，也不能滑动(因为没有Item)  
- 若getCount()返回为1，则viewpage上只显示第一个Item，也不能滑动(因为只有一个Item)  
- 若getCount()返回为2，则viewpage上只显示前两个Item，且只能在两者间滑动  
  
**怎么无限循环**  
正常情况下，滑动到最后一个Item时是不能再向前划到第一个Item的，当然在第一个Item时也不能向后划到最后一个Item，而要想首尾可以无限制的前后滑动，需满足三个条件  
- getCount()的返回值要足够大(否则滑动到一定次数后向前就划不动了)  
- 要保证首先加载的那个Item的position足够大，可使用`viewpage.setCurrentItem(3000000)`设置  
- 当然也要保证首先加载的是第一个Item，使用`position=position % mList.size()`替换所有用到的position参数的值  
  
**如何一次滑动多页**  
![](index_files/741e1aba-9c5e-4ad1-b1b1-643f36617583.jpg)  
  
```java  
class ViewPagerAdapter extends PagerAdapter {  
    @Override  
    public int getCount() {  
        int size = anchorList.size();  
        return size % 3 == 0 ? size / 3 : (size / 3 + 1);  
    }  
    @Override  
    public int getItemPosition(Object object) {  
        return POSITION_NONE;  // -2   
    }  
    @Override  
    public void destroyItem(ViewGroup container, int position, Object object) {  
        container.removeView((View) object);  
    }  
    @Override  
    public Object instantiateItem(ViewGroup container, int position) {  
        LinearLayout linearLayout;  
        firstBean = anchorList.get(3 * position + 0); //第一个子View的数据  
        secondBean = anchorList.get(3 * position + 1); //第二个子View的数据  
        thirdBean = anchorList.get(3 * position + 2); //第三个子View的数据  
  
        container.addView(linearLayout);  
        return linearLayout;  
    }  
    @Override  
    public boolean isViewFromObject(View view, Object object) {  
        return view == object;  
    }  
}  
```  
  
## 如何自定义指示器  
根据需求，要么重写onPageScrolled方法，要么重写onPageSelected方法。  
  
重写onPageScrolled可以实现跟随手指滑动  
```java  
@Override  
public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {  
    //参数为：当前条目位置，当前条目移动的百分比(0-100%)，移动距离(0-item的width)  
    int leftMargin; //小红点动态移动的距离  
    //最后一个条目时，不再继续向后移动  
    if (position == mList.size() - 1) {  
        leftMargin = position * mPointWidth + MARGIN_BETWEEN_POINTS;  
    } else {  
        leftMargin = (int) (mPointWidth * positionOffset) + position * mPointWidth + MARGIN_BETWEEN_POINTS;  
    }  
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view_red_point.getLayoutParams();  
    params.leftMargin = leftMargin;  
    view_red_point.setLayoutParams(params);  
}  
```  
  
重写onPageSelected可以实现切换后平滑的滑动到目标位置  
```java  
@Override  
public void onPageSelected(int position) {  
    tv_right.setTextColor(Color.GRAY);//根据位置设置字体颜色  
    tv_left.setTextColor(Color.RED);  
  
    Animation animation = new TranslateAnimation(currIndex * one, position * one, 0, 0);  
    currIndex = position; //将当前位置设置为目标位置  
    animation.setFillAfter(true);//停留在动画结束时的状态  
    animation.setDuration(100);//动画切换时间  
    iv_red.startAnimation(animation); //根据当前位置和将要移动到的位置创建动画  
}  
```  
  
# 代码  
  
## Activity  
```java  
public class MainActivity extends FragmentActivity {  
    private ViewPager mViewPager;  
    private TextView tvTitle, tvWeixin, tvFriend, tvContact, tvSetting;  
    private String[] titles = {"微信", "通讯录", "发现", "我"};  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        initView();  
          
        mViewPager.setAdapter(getPagerAdapter(2));  
        mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());  
        //mViewPager.setOffscreenPageLimit(titles.length - 1); //这个方法的杀伤力巨大，岂会导致所有缓存的Fragment都不会被销毁掉  
        reset(tvWeixin, 0); // 使用 mViewPager.setCurrentItem(0); 并不会回调 onPageSelected  
    }  
      
    private PagerAdapter getPagerAdapter(int type) {  
        switch (type) {  
            case 0:  
                return new MyPagerAdapter();  
            case 1:  
                return new MyFragmentPagerAdapter(getSupportFragmentManager());  
            case 2:  
                return new MyFragmentStatePagerAdapter(getSupportFragmentManager());  
            default:  
                return new MyPagerAdapter();  
        }  
    }  
      
    private void initView() {  
        mViewPager = findViewById(R.id.id_viewpager);  
        tvTitle = findViewById(R.id.tv_title);  
        tvWeixin = findViewById(R.id.tv_tab_bottom_weixin);  
        tvFriend = findViewById(R.id.tv_tab_bottom_friend);  
        tvContact = findViewById(R.id.tv_tab_bottom_contact);  
        tvSetting = findViewById(R.id.tv_tab_bottom_setting);  
          
        tvTitle.setOnClickListener(v -> {  
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {  
                Log.i("bqt", "【Fragment】" + ((SimpleTabFragment) fragment).getName());  
            }  
        });  
        tvWeixin.setOnClickListener(v -> reset(v, 0));  
        tvFriend.setOnClickListener(v -> reset(v, 1));  
        tvContact.setOnClickListener(v -> reset(v, 2));  
        tvSetting.setOnClickListener(v -> reset(v, 3));  
    }  
      
    private void reset(View v, int position) {  
        tvWeixin.setSelected(false);  
        tvFriend.setSelected(false);  
        tvContact.setSelected(false);  
        tvSetting.setSelected(false);  
        v.setSelected(true);  
        mViewPager.setCurrentItem(position);  
        tvTitle.setText(titles[position]);  
    }  
      
    class MyPagerAdapter extends PagerAdapter {  
        @Override  
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {  
            Log.i("bqt", "【isViewFromObject】");  
            return view == object;  
        }  
          
        @Override  
        public int getCount() {  
            return titles.length;  
        }  
          
        @NonNull  
        public Object instantiateItem(@NonNull ViewGroup container, int position) {  
            Log.i("bqt", "【PagerAdapter-instantiateItem】" + position); //这个并非抽象方法  
            TextView tv = new TextView(container.getContext());  
            tv.setText(titles[position] + "：" + new SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault()).format(new Date()));  
            tv.setBackgroundColor(0xFF000000 + new Random().nextInt(0xFFFFFF));  
            container.addView(tv);  
            return tv;  
        }  
          
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {  
            Log.i("bqt", "【destroyItem】" + position); //这个并非抽象方法  
            container.removeView((View) object);  
        }  
    }  
      
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {  
          
        MyFragmentPagerAdapter(FragmentManager fm) {  
            super(fm);  
        }  
          
        @Override  
        public Fragment getItem(int position) {  
            Log.i("bqt", "【----------------FragmentPagerAdapter-getItem:" + position + "----------------】");  
            return SimpleTabFragment.newInstance(titles[position]);  
        }  
          
        @Override  
        public int getCount() {  
            return titles.length;  
        }  
    }  
      
    class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {  
        MyFragmentStatePagerAdapter(FragmentManager fm) {  
            super(fm);  
        }  
          
        @Override  
        public int getCount() {  
            return titles.length;  
        }  
          
        @Override  
        public Fragment getItem(int position) {  
            Log.i("bqt", "【----------------FragmentStatePagerAdapter-getItem:" + position + "----------------】");  
            return SimpleTabFragment.newInstance(titles[position]);  
        }  
    }  
      
    class MyOnPageChangeListener implements OnPageChangeListener {  
          
        @Override  
        public void onPageScrolled(int i, float v, int i1) {  
        }  
          
        @Override  
        public void onPageSelected(int position) {  
            Log.i("bqt", "【onPageSelected】" + position);  
            switch (position) {  
                case 0:  
                    reset(tvWeixin, position);  
                    break;  
                case 1:  
                    reset(tvFriend, position);  
                    break;  
                case 2:  
                    reset(tvContact, position);  
                    break;  
                case 3:  
                    reset(tvSetting, position);  
                    break;  
                default:  
                    break;  
            }  
        }  
          
        @Override  
        public void onPageScrollStateChanged(int i) {  
        }  
    }  
}  
```  
  
## Fragment  
```java  
public class SimpleTabFragment extends Fragment {  
    private String name;  
      
    public static final String ARGUMENT = "name";  
      
    public static SimpleTabFragment newInstance(String name) {  
        SimpleTabFragment fragment = new SimpleTabFragment();  
        Bundle bundle = new Bundle();  
        bundle.putString(ARGUMENT, name);  
        fragment.setArguments(bundle);  
        return fragment;  
    }  
      
    public SimpleTabFragment() {  
        Log.i("bqt", "【----------------构造方法" +hashCode() +"----------------】");  
    }  
      
    @Override  
    public void onAttach(Context context) {  
        super.onAttach(context);  
        Log.i("bqt", "【--------onAttach--------】");  
    }  
      
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Bundle bundle = getArguments();  
        name = bundle != null ? bundle.getString(ARGUMENT) : "unknown";  
        Log.i("bqt", "【--------onCreate:" + name + "--------】");  
    }  
      
    @Override  
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        Log.i("bqt", "【----onCreateView" + name + "----】");  
        View view = inflater.inflate(R.layout.fragment_tab, container, false);  
        TextView tv = view.findViewById(R.id.tv);  
        tv.setText(name + "：" + new SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault()).format(new Date()));  
        tv.setBackgroundColor(0xFF000000 + new Random().nextInt(0xFFFFFF));  
        return view;  
    }  
      
    @Override  
    public void onResume() {  
        super.onResume();  
        Log.i("bqt", "【onResume:" + name + "】");  
    }  
      
    @Override  
    public void onDestroyView() {  
        super.onDestroyView();  
        Log.i("bqt", "【----onDestroyView:" + name + "------】");  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.i("bqt", "【--------onDestroy:" + name + "--------】");  
    }  
      
    @Override  
    public void onDetach() {  
        super.onDetach();  
        Log.i("bqt", "【--------onDetach:" + name + "--------】");  
    }  
      
    public void setName(String name) {  
        this.name = name;  
    }  
      
    public String getName() {  
        return name;  
    }  
}  
```  
  
## Activity 的布局  
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
  
    <android.support.v4.view.ViewPager  
        android:id="@+id/id_viewpager"  
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
  
## Fragment 的布局  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"  
              android:layout_width="fill_parent"  
              android:layout_height="fill_parent"  
              android:orientation="vertical">  
  
    <TextView  
        android:id="@+id/tv"  
        android:layout_width="fill_parent"  
        android:layout_height="fill_parent"  
        android:gravity="center"  
        android:text="第一个页面"  
        android:textColor="#000"  
        android:textSize="20sp"/>  
  
</LinearLayout>  
```  
  
# 测试  
  
16个短杠`----------------`的有`getItem`和`构造方法`方法，代表的是Fragment对象的创建，是其他所有声明周期的开始  
8个短杠`--------`的有`onAttach`、`onCreate`和`onDestroy`、`onDetach`方法，代表的是Fragment声明周期的开始  
4个短杠`----`的有`onCreateView`和`onDestroyView`方法，代表的是Fragment中View的创建和销毁  
  
![](index_files/d21f73b1-a05b-445c-b01a-400a7d732dc8.png)  
  
## 启动后  
  
**FragmentPagerAdapter**  
```  
【----------------FragmentPagerAdapter-getItem:0----------------】  
【----------------构造方法215908580----------------】  
【----------------FragmentPagerAdapter-getItem:1----------------】  
【----------------构造方法173705549----------------】  
  
【--------onAttach--------】  
【--------onCreate:微信--------】  
【--------onAttach--------】  
【--------onCreate:通讯录--------】  
  
【----onCreateView微信----】  
【onResume:微信】  
【----onCreateView通讯录----】  
【onResume:通讯录】  
```  
  
> 首先是构造了两个Fragment，然后两个Fragment被绑定到Activity上，然后通过onCreateView创建Fragment的View，最后都onResume  
> 注意，虽然此时第二个Fragment对用户是不可见的，但仍会回调其onResume方法  
  
**FragmentStatePagerAdapter**  
> 和FragmentPagerAdapter行为完全一致  
  
## 滑到第二个页面  
```  
【onPageSelected】1  
【----------------FragmentPagerAdapter-getItem:2----------------】  
【----------------构造方法88702934----------------】  
【--------onAttach--------】  
【--------onCreate:发现--------】  
【----onCreateView发现----】  
【onResume:发现】  
```  
  
> 此时会完整的创建第三个Fragment，同时第一个Fragment会保留  
  
**FragmentStatePagerAdapter**  
> 和FragmentPagerAdapter行为完全一致  
  
## 滑到第一个页面  
```  
【onPageSelected】0  
【----onDestroyView:发现------】  
```  
  
> 此时第三个Fragment的View会被销毁  
> 注意，此时第三个Fragment并没有回调`onDestroy`、`onDetach`方法，所以第三个Fragment并没有被销毁  
  
**FragmentStatePagerAdapter**  
```java  
【onPageSelected】0  
【----onDestroyView:发现------】  
【--------onDestroy:发现--------】  
【--------onDetach:发现--------】  
```  
  
> 重大区别来了，除了会回调`onDestroyView`销毁View外，FragmentStatePagerAdapter还会回调`onDestroy`、`onDetach`方法销毁Fragment  
  
## 最小化后再回到前台  
```  
【onResume:微信】  
【onResume:通讯录】  
```  
  
> 可以发现，前两个Fragment都回调了onResume  
> 注意：虽然第三个Fragment没有被销毁，但因为其View被销毁了，所以不可能再回调其`onCreateView`之后的声明周期方法了，这其中当然包括onResume方法。  
  
**FragmentStatePagerAdapter**  
> 和FragmentPagerAdapter行为完全一致  
  
## 再次滑到第二个页面  
```  
【onPageSelected】1  
【----onCreateView发现----】  
【onResume:发现】  
```  
  
> 可以发现，并没有重建第三个Fragment，而只是通过`onCreateView`重建了Fragment的View  
  
**FragmentStatePagerAdapter**  
```java  
【onPageSelected】1  
【----------------FragmentStatePagerAdapter-getItem:2----------------】  
【----------------构造方法62405875----------------】  
  
【--------onAttach--------】  
【--------onCreate:发现--------】  
  
【----onCreateView发现----】  
【onResume:发现】  
```  
  
> 和前面的重大区别对应，这里会完全重建第三个Fragment  
  
## 在第二个页面横竖屏切换  
  
来个大招，横竖屏切换：  
```  
【----onDestroyView:微信------】  
【--------onDestroy:微信--------】  
【--------onDetach:微信--------】  
【----onDestroyView:通讯录------】  
【--------onDestroy:通讯录--------】  
【--------onDetach:通讯录--------】  
【----onDestroyView:发现------】  
【--------onDestroy:发现--------】  
【--------onDetach:发现--------】  
  
【----------------构造方法207117498----------------】  
【----------------构造方法264001387----------------】  
【----------------构造方法227934920----------------】  
  
【--------onAttach--------】  
【--------onCreate:微信--------】  
【--------onAttach--------】  
【--------onCreate:通讯录--------】  
【--------onAttach--------】  
【--------onCreate:发现--------】  
  
【----onCreateView微信----】  
【----onCreateView通讯录----】  
【----onCreateView发现----】  
  
【onPageSelected】1  
【onResume:微信】  
【onResume:通讯录】  
【onResume:发现】  
```  
  
> 可以发现，完全销毁了之前的所有Fragment，并且重建了所有Fragment  
  
**FragmentStatePagerAdapter**  
> 和FragmentPagerAdapter行为完全一致  
  
## 在第一个页面横竖屏切换  
```  
【----onDestroyView:微信------】  
【--------onDestroy:微信--------】  
【--------onDetach:微信--------】  
【----onDestroyView:通讯录------】  
【--------onDestroy:通讯录--------】  
【--------onDetach:通讯录--------】  
//注意，这里并不会调用第三个Fragment的onDestroyView，因为这个Fragment的View早就被销毁了  
【--------onDestroy:发现--------】  
【--------onDetach:发现--------】  
  
【----------------构造方法262171070----------------】  
【----------------构造方法242817567----------------】  
【----------------构造方法41927020----------------】  
  
【--------onAttach--------】  
【--------onCreate:微信--------】  
【--------onAttach--------】  
【--------onCreate:通讯录--------】  
【--------onAttach--------】  
【--------onCreate:发现--------】  
  
【----onCreateView微信----】  
【----onCreateView通讯录----】  
//同样，这里并不会调用第三个Fragment的onCreateView，因为这个Fragment的View目前并不需要创建  
  
【onResume:微信】  
【onResume:通讯录】  
```  
  
> 和上一种情况没什么区别，唯一的区别就是，并没有调用第三个Fragment的`onDestroyView`销毁View，或调用其`onCreateView`重建View  
  
**FragmentStatePagerAdapter**  
```java  
【----onDestroyView:微信------】  
【--------onDestroy:微信--------】  
【--------onDetach:微信--------】  
【----onDestroyView:通讯录------】  
【--------onDestroy:通讯录--------】  
【--------onDetach:通讯录--------】  
//这里完全没有调用第三个Fragment的任何回调方法，因为在切换到第一个Fragment时，第三个Fragment已经完全销毁了  
  
【----------------构造方法41927020----------------】  
【----------------构造方法48001845----------------】  
//这里也不会重新创建第三个Fragment  
  
【--------onAttach--------】  
【--------onCreate:微信--------】  
【--------onAttach--------】  
【--------onCreate:通讯录--------】  
  
【----onCreateView微信----】  
【----onCreateView通讯录----】  
  
【onResume:微信】  
【onResume:通讯录】  
```  
  
> 和前面的重大区别对应，因为在切换到第一个Fragment时，第三个Fragment已经完全销毁了，所以这里不会回调第三个Fragment的任何回调方法，同样，这里也不会重新创建第三个Fragment。  
  
2018-2-11  
