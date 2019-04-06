| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [TabLayout 简介](#TabLayout-简介)
	- [文档简介](#文档简介)
	- [内部类和内部接口](#内部类和内部接口)
	- [XML attributes](#XML-attributes)
	- [公共方法](#公共方法)
	- [两种模式](#两种模式)
	- [默认样式](#默认样式)
- [测试代码](#测试代码)
	- [Activity](#Activity)
	- [XML布局](#XML布局)
	- [定义的样式](#定义的样式)
  
# TabLayout 简介  
  
## 文档简介  
  
[文档位置](https://developer.android.google.cn/reference/android/support/design/widget/TabLayout.html)  
  
依赖  
```groovy  
implementation 'com.android.support:design:28.0.0'  
```  
  
继承体系  
```java  
java.lang.Object  
   ↳    android.view.View  
       ↳    android.view.ViewGroup  
           ↳    android.widget.FrameLayout  
               ↳    android.widget.HorizontalScrollView  
                   ↳    android.support.design.widget.TabLayout  
```  
  
TabLayout provides a horizontal layout to display tabs.  
> TabLayout提供水平布局以显示选项卡。  
  
Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods.   
> 要显示的选项卡的填充是通过TabLayout.Tab实例完成的。 您可以通过`newTab()`创建选项卡。 从那里，您可以分别通过`setText(int)`和`setIcon(int)`更改选项卡的标签或图标。 要显示选项卡，需要通过`addTab(Tab)`方法之一将其添加到布局中。  
  
You should set a listener via setOnTabSelectedListener(OnTabSelectedListener) to be notified when any tab's selection state has been changed.  
> 您应该通过`setOnTabSelectedListener`设置一个侦听器，以便在任何选项卡的选择状态发生更改时得到通知。  
  
You can also add items to TabLayout in your layout through the use of TabItem.   
> 您还可以通过使用`TabItem`将项目添加到布局中的TabLayout。  
  
If you're using a ViewPager together with this layout, you can call setupWithViewPager(ViewPager) to link the two together. This layout will be automatically populated from the PagerAdapter's page titles.  
> 如果您将ViewPager与此布局一起使用，则可以调用`setupWithViewPager(ViewPager)`将两者链接在一起。 **此布局将自动从PagerAdapter的页面标题中填充**。  
  
This view also supports being used as part of a ViewPager's decor, and can be added directly to the ViewPager in a layout resource file like so:  
> 此视图还支持用作ViewPager的装饰的一部分，并且可以直接添加到布局资源文件中的ViewPager，如下所示：  
  
```xml  
 <android.support.v4.view.ViewPager  
     android:layout_width="match_parent"  
     android:layout_height="match_parent">  
  
     <android.support.design.widget.TabLayout  
         android:layout_width="match_parent"  
         android:layout_height="wrap_content"  
         android:layout_gravity="top" />  
  
 </android.support.v4.view.ViewPager>  
```  
  
> 注，和ViewPager联合使用时，TabLayout 可以作为 ViewPager 的子元素，也可以单独让在任何地方。  
  
通过调用TabLayout的setupWithViewPager方法便可实现TabLayout与ViewPager的联动。看源码可以知道，其是通ViewPager的OnPageChangeListener监听将两者连接起来的。  
  
## 内部类和内部接口  
**唯一的一个内部接口：TabLayout.OnTabSelectedListener**  
Callback interface invoked when a tab's selection state changes. Some applications may use this action to return to the top level of a category类型.  
- onTabReselected(TabLayout.Tab tab)   Called when a tab that is already selected is chosen again by the user.  
- onTabSelected(TabLayout.Tab tab)   Called when a tab enters the selected state.  
- onTabUnselected(TabLayout.Tab tab)   Called when a tab exits the selected state.  
  
**内部类：TabLayout.ViewPagerOnTabSelectedListener**  
  
A `TabLayout.OnTabSelectedListener` class which contains the necessary calls back to the provided ViewPager so that the tab position is kept in sync.  
  
```java  
public static class TabLayout.ViewPagerOnTabSelectedListener extends Object implements TabLayout.OnTabSelectedListener  
```  
  
**内部类：TabLayout.TabLayoutOnPageChangeListener**  
A `ViewPager.OnPageChangeListener` class which contains the necessary calls back to the provided TabLayout so that the tab position is kept in sync.  
  
```java  
public static class TabLayout.TabLayoutOnPageChangeListener extends Object implements ViewPager.OnPageChangeListener  
```  
  
This class stores the provided TabLayout `weakly`, meaning that you can use `addOnPageChangeListener(OnPageChangeListener)` without removing the listener and not cause a leak.  
  
**内部类：TabLayout.Tab**  
A tab in this layout. Instances can be created via newTab().  
- CharSequence   `getContentDescription()`   Gets a brief description of this tab's content for use in accessibility support.  
- View   `getCustomView()`   Returns the custom view used for this tab.  
- Drawable   `getIcon()`   Return the icon associated with this tab.  
- int   `getPosition()`   Return the current position of this tab in the action bar.  
- Object   `getTag()`   CharSequence   getText()   Return the text of this tab.  
- boolean   `isSelected()`   Returns true if this tab is currently selected.  
- void   `select()`   Select this tab.  
- TabLayout.Tab   `setContentDescription(int resId 或 CharSequence contentDesc)`   Set a description of this tab's content for use in accessibility support.  
- TabLayout.Tab   `setCustomView(int resId 或 View view)`   Set a custom view to be used for this tab.  
- TabLayout.Tab   `setIcon(int resId 或 Drawable icon)`   Set the icon displayed on this tab.  
- TabLayout.Tab   `setText(int resId 或 CharSequence text)`   Set the text displayed on this tab.  
- TabLayout.Tab   `setTag(Object tag)`   Give this Tab an arbitrary object to hold for later use.  
  
## XML attributes  
特有的属性：  
- android.support.design:`tabBackground`    Reference to a background to be applied to tabs.   
- android.support.design:`tabContentStart`    Position in the Y axis from the starting edge that tabs should be positioned from.   
- android.support.design:`tabGravity`    Gravity constant for tabs.   
- android.support.design:`tabIndicatorColor` / tabIndicatorHeight    Color of the indicator used to show the currently selected tab.   
- android.support.design:`tabMaxWidth` / tabMinWidth    The maximum width for tabs.   
- android.support.design:`tabMode`    The behavior mode for the Tabs in this layout  
- android.support.design:`tabPadding` / tabPaddingBottom / ...    The preferred padding along all edges of tabs.   
- android.support.design:`tabSelectedTextColor` / tabTextColor    The text color to be applied to the currently selected tab.   
- android.support.design:`tabTextAppearance`    A reference to a TextAppearance style to be applied to tabs.   
  
## 公共方法  
**添加移除OnTabSelectedListener**  
- void    `addOnTabSelectedListener`(TabLayout.OnTabSelectedListener listener)   Add a TabLayout.OnTabSelectedListener that will be invoked when tab selection changes.  
- void    `removeOnTabSelectedListener`(TabLayout.OnTabSelectedListener listener)   Remove the given TabLayout.OnTabSelectedListener that was previously added via addOnTabSelectedListener(OnTabSelectedListener).  
- void    `clearOnTabSelectedListeners`()   Remove all previously added TabLayout.OnTabSelectedListeners.  
- ~~void    `setOnTabSelectedListener`(TabLayout.OnTabSelectedListener listener)   This method was deprecated in API level 24.0.0.~~  
  
**添加移除Tab**  
- void    `addTab(TabLayout.Tab tab, boolean setSelected)`   Add a tab to this layout. The tab will be added at the end of the list.  
- void    `addTab(TabLayout.Tab tab, int position)`   Add a tab to this layout. The tab will be inserted at position. If this is the first tab to be added it will become the selected tab.  
- void    `addTab(TabLayout.Tab tab)`   Add a tab to this layout. The tab will be added at the end of the list. If this is the first tab to be added it will become the selected tab.  
- void    `addTab(TabLayout.Tab tab, int position, boolean setSelected)`  
- void    `removeAllTabs()`   Remove all tabs from the action bar and deselect the current tab.   
- void    `removeTabAt(int position)`   Remove a tab from the layout. If the removed tab was selected it will be deselected and another tab will be selected if present.  
- void    `removeTab(TabLayout.Tab tab)`   Remove a tab from the layout.  
  
**获取、创建Tab**  
- TabLayout.Tab    `getTabAt(int index)`   Returns the tab at the specified index.  
- TabLayout.Tab    `newTab()`   Create and return a new TabLayout.Tab.  
  
**获取Tab位置、数量**  
- int    `getSelectedTabPosition()`   Returns the position of the current selected tab.  
- int    `getTabCount()`   Returns the number of tabs currently registered with the action bar.  
  
**设置Tab样式**  
这些样式全可以在xml中设置，有很多样式只能在xml中设置  
- void    `setSelectedTabIndicatorColor(int color)`   Sets the tab indicator's color for the currently selected tab.  
- void    `setSelectedTabIndicatorHeight(int height)`   Sets the tab indicator's height for the currently selected tab.  
- void    `setTabGravity(int gravity) / getTabGravity`  Set the gravity to use when laying out the tabs.  
- void    `setTabMode(int mode) / getTabMode`  Set the behavior mode for the Tabs in this layout.  
- void    `setTabTextColors(int normalColor, int selectedColor)`   Sets the text colors for the different states (normal, selected) used for the tabs.  
- void    `setTabTextColors(ColorStateList textColor) / getTabTextColors`  Sets the text colors for the different states (normal, selected) used for the tabs.  
  
**添加子View**  
- void    `addView(View child, int index)`   Adds a child view. If no layout parameters are already set on the child, the default parameters for this ViewGroup are set on the child.  
- void    `addView(View child)`   
- void    `addView(View child, ViewGroup.LayoutParams params)`   Adds a child view with the specified layout parameters.  
- void    `addView(View child, int index, ViewGroup.LayoutParams params)`   Adds a child view with the specified layout parameters.  
  
**与ViewPager联动**  
- void    `setupWithViewPager(ViewPager viewPager)`   The one-stop shop for setting up this TabLayout with a ViewPager.  
- void    `setupWithViewPager(ViewPager viewPager, boolean autoRefresh)`  
- void    `setTabsFromPagerAdapter(PagerAdapter adapter)`   This method was deprecated in API level 23.2.0.   
  
**其他**  
- FrameLayout.LayoutParams    `generateLayoutParams(AttributeSet attrs)`   Returns a new set of layout parameters based on the supplied attributes set.  
- void    `setScrollPosition(int position, float positionOffset, boolean updateSelectedText)`   Set the scroll position of the tabs. This is useful for when the tabs are being displayed as part of a scrolling container such as ViewPager .Calling this method does not update the selected tab, it is only used for drawing purposes.  
- boolean    `shouldDelayChildPressedState()`   Return true if the pressed state should be delayed for children or descendants of this ViewGroup.  
  
## 两种模式  
**MODE_FIXED**  
  
Fixed tabs display `all tabs` concurrently and are best used with content that benefits from quick pivots between tabs. The maximum number of tabs is `limited by the view’s width`. Fixed tabs have `equal width`, based on the `widest tab label`.  
> 固定选项卡同时显示所有选项卡，最适用于受益于选项卡之间快速枢轴的内容。 选项卡的最大数量受视图宽度的限制。 固定标签的宽度相等，基于最宽的标签标签。  
  
**MODE_SCROLLABLE**  
  
Scrollable tabs display a `subset of tabs` at any given moment, and can contain `longer tab labels` and `a larger number of tab`s. They are best used for browsing contexts in touch interfaces when users don’t need to directly `compare` the tab labels.  
> 可滚动选项卡在任何给定时刻显示选项卡的子集，并且可以包含更长的选项卡标签和更多数量的选项卡。 当用户不需要直接比较标签时，它们最适合用于浏览触摸界面中的内容。  
  
## 默认样式  
**TabLayout的默认样式**  
```xml  
app:theme="@style/Widget.Design.TabLayout"  
```  
  
```xml  
<style name="Widget.Design.TabLayout" parent="Base.Widget.Design.TabLayout">  
    <item name="tabGravity">fill</item>  
    <item name="tabMode">fixed</item>  
</style>  
```  
  
```xml  
<style name="Base.Widget.Design.TabLayout" parent="android:Widget">  
    <item name="tabMaxWidth">@dimen/design_tab_max_width</item>【264dp】  
    <item name="tabIndicatorColor">?attr/colorAccent</item>  
    <item name="tabIndicatorHeight">2dp</item>  
    <item name="tabPaddingStart">12dp</item>  
    <item name="tabPaddingEnd">12dp</item>  
    <item name="tabBackground">?attr/selectableItemBackground</item>  
    <item name="tabTextAppearance">@style/TextAppearance.Design.Tab</item>  
    <item name="tabSelectedTextColor">?android:textColorPrimary</item>  
</style>  
```  
  
我们可以改变TabLayout对应的系统样式的属性值来适配我们自己的需求。  
  
**Tab文本的默认样式**  
```xml  
<style name="TextAppearance.Design.Tab" parent="TextAppearance.AppCompat.Button">  
    <item name="android:textSize">@dimen/design_tab_text_size</item>  
    <item name="android:textColor">?android:textColorSecondary</item>  
    <item name="textAllCaps">true</item>【1、全部大写显示；2、可能导致无法显示某些内容】  
</style>  
```  
  
# 测试代码  
  
[完整Demo地址](https://github.com/baiqiantao/TabLayoutTest.git)  
  
## Activity  
```java  
public class SimpleTestActivity extends FragmentActivity {  
    private String[] titles = {"包青天", "白", "bqt注意字母的大小写", "白乾涛", "1", "广州玖拾玖度信息科技有限公司广州东百科技有限公司", "bqt"};  
    ViewPager viewPager, viewPager2;  
    TabLayout tabLayout, tabLayout2;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
          
        viewPager = findViewById(R.id.view_pager);  
        viewPager2 = findViewById(R.id.view_pager2);  
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));  
        viewPager2.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));  
          
        //在代码中设置属性，也可以在XML中设置(推荐)，不多大多都没什么卵用  
        tabLayout = findViewById(R.id.tablayout);  
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//滚动模式，根据内容自动调整宽度，不管内容多长都全部显示且不换行  
        tabLayout.setTabGravity(Gravity.TOP);//没卵用！  
        tabLayout.setTabTextColors(Color.RED, Color.GREEN); //正常、选中的颜色  
        tabLayout.setSelectedTabIndicatorColor(Color.BLUE); //下划线颜色  
        tabLayout.setSelectedTabIndicatorHeight(15);  //下划线  
        tabLayout.setBackgroundColor(Color.YELLOW); //背景色，设置选择器不起效果！  
        tabLayout.setupWithViewPager(viewPager);  
          
        //在XML中设置Tab样式  
        tabLayout2 = findViewById(R.id.tablayout2);  
        tabLayout2.setTabMode(TabLayout.MODE_FIXED);//固定模式，会将每个tab都显示出来，tab的宽度一样，内容太长会换行及显示...  
        tabLayout2.setupWithViewPager(viewPager2);  
    }  
      
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {  
          
        MyFragmentPagerAdapter(FragmentManager fm) {  
            super(fm);  
        }  
          
        @Override  
        public Fragment getItem(int position) {  
            return MyFragment.newInstance(titles[position]);  
        }  
          
        @Override  
        public int getCount() {  
            return titles.length;  
        }  
          
        @Override  
        public CharSequence getPageTitle(int position) {  
            return titles[position];//虽然不是抽象方法，但是必须重写，不重写的话会导致TabLayout没有标题  
        }  
    }  
}  
```  
  
## XML布局  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<RelativeLayout  
    xmlns:android="http://schemas.android.com/apk/res/android"  
    xmlns:app="http://schemas.android.com/apk/res-auto"  
    android:layout_width="match_parent"  
    android:layout_height="match_parent"  
    android:orientation="vertical">  
  
    <android.support.design.widget.TabLayout  
        android:id="@+id/tablayout"  
        android:layout_width="match_parent"  
        android:layout_height="wrap_content"  
        app:theme="@style/Widget.Design.TabLayout"/>  
  
    <android.support.v4.view.ViewPager  
        android:id="@+id/view_pager"  
        android:layout_width="match_parent"  
        android:layout_height="200dp"  
        android:layout_below="@id/tablayout"/>  
  
    <android.support.v4.view.ViewPager  
        android:id="@+id/view_pager2"  
        android:layout_width="match_parent"  
        android:layout_height="match_parent"  
        android:layout_above="@id/tablayout2"  
        android:layout_below="@id/view_pager"/>  
  
    <android.support.design.widget.TabLayout  
        android:id="@+id/tablayout2"  
        style="@style/MyTabStyle"  
        android:layout_width="match_parent"  
        android:layout_height="wrap_content"  
        android:layout_alignParentBottom="true"  
        app:tabTextAppearance="@style/MyTabTextAppearance"/>  
  
</RelativeLayout>  
```  
  
## 定义的样式  
Tab样式  
```xml  
<style name="MyTabStyle" parent="Widget.Design.TabLayout">  
    <item name="android:background">@android:color/holo_green_dark</item><!--此背景会被tabBackground覆盖掉-->  
    <item name="tabBackground">@android:color/holo_orange_dark</item><!--"通常"和background的效果完全一样-->  
    <item name="tabContentStart">20dp</item><!--此值导致左边一块区域属于background，但不属于tabBackground-->  
    <item name="tabGravity">fill</item><!--只能是fill或center，貌似没有卵用啊-->  
    <item name="tabIndicatorHeight">5dp</item>  
    <item name="tabIndicatorColor">#f00</item>  
    <item name="tabMaxWidth">120dp</item>  
    <item name="tabMinWidth">20dp</item>  
    <item name="tabPadding">0dp</item>  
    <item name="tabMode">scrollable</item>  
    <item name="tabSelectedTextColor">#f00</item>  
    <!--以下属性貌似都无效！-->  
    <item name="android:ellipsize">end</item>  
    <item name="android:maxLines">1</item>  
    <item name="android:gravity">top</item>  
    <item name="android:textColor">#fff</item>  
</style>  
```  
  
Tab文字样式  
```xml  
<style name="MyTabTextAppearance" parent="TextAppearance.Design.Tab"><!--必须继承自TextAppearance.Design.Tab -->  
    <!--只支持以下三个属性-->  
    <item name="android:textSize">15sp</item>  
    <item name="android:textAllCaps">false</item>  
    <!--选择器无效！除非根据tab的选中状态手动设置-->  
    <item name="android:textColor">@drawable/sel_tab_textcolor</item>  
</style>  
```  
  
2017-6-28  
