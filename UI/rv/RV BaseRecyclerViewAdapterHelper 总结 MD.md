| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RV BaseRecyclerViewAdapterHelper 总结 MD  
***  
目录  
===  

- [介绍](#介绍)
	- [添加依赖](#添加依赖)
	- [功能一览](#功能一览)
	- [优化Adapter代码](#优化adapter代码)
	- [Item的点击事件](#item的点击事件)
	- [添加列表加载动画](#添加列表加载动画)
	- [添加头部、尾部](#添加头部、尾部)
	- [加载更多](#加载更多)
	- [自定义加载布局](#自定义加载布局)
	- [添加分组](#添加分组)
	- [自定义不同的item类型](#自定义不同的item类型)
	- [设置空布局](#设置空布局)
	- [添加拖拽、滑动删除](#添加拖拽、滑动删除)
	- [分组的伸缩栏](#分组的伸缩栏)
	- [自定义BaseViewHolder](#自定义baseviewholder)
	- [案例](#案例)
  
# 介绍  
[项目地址](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)  
[文档](https://www.jianshu.com/p/b343fcff51b0)  
[网站](http://www.recyclerview.org/)  
  
## 添加依赖  
```groovy  
allprojects {  
    repositories {  
        ...  
        maven { url "https://jitpack.io" }  
    }  
}  
```  
```groovy  
compile 'com.github.CymChad:BaseRecyclerViewAdapterHelper:VERSION_CODE'  
```  
用 [这里](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/releases) 的真实发行版本号, 替换 VERSION_CODE（2017-6最新版本号：2.9.18）。  
  
## 功能一览  
*   [优化Adapter代码](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E4%BC%98%E5%8C%96Adapter%E4%BB%A3%E7%A0%81)：和原始的adapter相对，减少70%的代码量。  
*   [添加Item事件](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E6%B7%BB%E5%8A%A0Item%E4%BA%8B%E4%BB%B6)：Item的点击/长按事件，Item子控件的点击/长按事件  
*   [添加列表加载动画](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E6%B7%BB%E5%8A%A0%E5%88%97%E8%A1%A8%E5%8A%A0%E8%BD%BD%E5%8A%A8%E7%94%BB)： 一行代码轻松切换5种默认动画。  
*   [添加头部、尾部](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E6%B7%BB%E5%8A%A0%E5%A4%B4%E9%83%A8%E3%80%81%E5%B0%BE%E9%83%A8)： 一行代码搞定，感觉又回到ListView时代。  
*   [自动加载](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E4%B8%8A%E6%8B%89%E5%8A%A0%E8%BD%BD)：上拉加载无需监听滑动事件,可自定义加载布局，显示异常提示，自定义异常提示。  
*   [添加分组](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E6%B7%BB%E5%8A%A0%E5%88%86%E7%BB%84)：随心定义分组头部。  
*   [自定义不同的item类型](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E8%87%AA%E5%AE%9A%E4%B9%89%E4%B8%8D%E5%90%8C%E7%9A%84item%E7%B1%BB%E5%9E%8B)：简单配置、无需重写额外方法。  
*   [设置空布局](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E8%AE%BE%E7%BD%AE%E7%A9%BA%E5%B8%83%E5%B1%80)：比Listview的setEmptyView还要好用。  
*   [添加拖拽、滑动删除](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E6%B7%BB%E5%8A%A0%E6%8B%96%E6%8B%BD%E3%80%81%E6%BB%91%E5%8A%A8%E5%88%A0%E9%99%A4)：开启，监听即可，就是这么简单。  
*   [分组的伸缩栏](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E5%88%86%E7%BB%84%E7%9A%84%E4%BC%B8%E7%BC%A9%E6%A0%8F)：比ExpandableListView还要强大，支持两级。  
*   [自定义ViewHolder](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E8%87%AA%E5%AE%9A%E4%B9%89BaseViewHolder)：支持自定义ViewHolder，让开发者随心所欲。  
  
## 优化Adapter代码  
使用代码  
  
```java  
public class QuickAdapter extends BaseQuickAdapter<Status, BaseViewHolder> {  
    public QuickAdapter(List data) {  
        super(R.layout.tweet, data);  
    }  
  
    @Override  
  protected void convert(BaseViewHolder viewHolder, Status item) {  
        viewHolder.setText(R.id.tweetName, item.getUserName())  
                .setVisible(R.id.tweetRT, item.isRetweet())  
                .linkify(R.id.tweetText);  
                 Glide.with(mContext).load(item.getUserAvatar()).crossFade().into((ImageView) helper.getView(R.id.iv));  
    }  
}  
```  
  
- 继承BaseQuickAdapter基类， 泛型为List的数据类型  
- 重写convert方法，通过viewHolder.点出各种常用方法，如果有自定义控件或者viewHolder没有提供的方法，就可以通过getView方法来实现获取控件，然后进行的操作。  
- 如果需要获取当前position可以通过viewHolder.getLayoutPosition()来获取  
  
## Item的点击事件  
```java  
adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {  
    @Override  
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {  
        Toast.makeText(ItemClickActivity.this, "onItemClick" + position, Toast.LENGTH_SHORT).show();  
    }  
});  
```  
```java  
adapter.setOnItemLongClickListener(BaseQuickAdapter.OnItemLongClickListener);  
```  
```java  
adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {  
    @Override  
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {  
        Toast.makeText(ItemClickActivity.this, "onItemChildClick" + position, Toast.LENGTH_SHORT).show();  
    }  
});  
```  
```java  
mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {  
    @Override  
    public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {  
        Toast.makeText(ItemClickActivity.this, "onSimpleItemClick" + position, Toast.LENGTH_LONG).show();  
    }  
});  
```  
  
## 添加列表加载动画  
默认渐显效果：quickAdapter.openLoadAnimation();  
渐显、缩放、从下到上，从左到右、从右到左：quickAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);  
默认动画每个item只执行一次，如果想重复执行动画可以调用一下方法：mQuickAdapter.isFirstOnly(false);  
  
自定义动画  
```java  
quickAdapter.openLoadAnimation(new BaseAnimation() {  
    @Override  
    public Animator[] getAnimators(View view) {  
        return new Animator[]{  
                ObjectAnimator.ofFloat(view, "scaleY", 1, 1.1f, 1),  
                ObjectAnimator.ofFloat(view, "scaleX", 1, 1.1f, 1)  
        };  
    }  
});  
```  
  
## 添加头部、尾部  
- 使用代码 添加头部、尾部  mQuickAdapter.addHeaderView(getView());  
- 删除指定头部、尾部  mQuickAdapter.removeHeaderView(getView);  
- 删除所有头部、尾部  mQuickAdapter.removeAllHeaderView();  
- 扩展方法  setHeaderAndEmpty  
- 设置头部、尾部不占一行（使用场景：gv的头部添加一个"+"号） setHeaderViewAsFlow  
  
## 加载更多  
- 加载更多：setOnLoadMoreListener() //滑动最后一个Item的时候回调  
- 加载完成：mQuickAdapter.loadMoreComplete();  
- 加载失败：mQuickAdapter.loadMoreFail();  
- 没有更多数据：mQuickAdapter.loadMoreEnd();  
- 打开或关闭加载：mQuickAdapter.setEnableLoadMore(boolean);  
- 预加载：mQuickAdapter.setAutoLoadMoreSize(int);// 当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法  
- 检查是否满一屏，如果不满足关闭loadMore：mQuickAdapter.disableLoadMoreIfNotFullPage();  
- 设置自定义加载布局：mQuickAdapter.setLoadMoreView(new LoadMoreView());  
  
```java  
mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {  
    @Override   
    public void onLoadMoreRequested() {  
        mRecyclerView.postDelayed(new Runnable() {  
            @Override  
            public void run() {  
                if (mCurrentCounter >= TOTAL_COUNTER) {  
                    //数据全部加载完毕  
                    mQuickAdapter.loadMoreEnd();  
                } else {  
                    if (isErr) {  
                        //成功获取更多数据  
                        mQuickAdapter.addData(DataServer.getSampleData(PAGE_SIZE));  
                        mCurrentCounter = mQuickAdapter.getData().size();  
                        mQuickAdapter.loadMoreComplete();  
                    } else {  
                        //获取更多数据失败  
                        isErr = true;  
                        mQuickAdapter.loadMoreFail();  
                    }  
                }  
            }  
        }, delayMillis);  
    }  
}, mReyclerView);  
```  
注意：如果上拉结束后，下拉刷新需要再次开启上拉监听，需要使用setNewData方法填充数据。  
  
## 自定义加载布局  
设置自定义加载布局：mQuickAdapter.setLoadMoreView(new LoadMoreView());  
```java  
public final class CustomLoadMoreView extends LoadMoreView {  
  
    @Override   
    public int getLayoutId() {  
        return R.layout.view_load_more;  
    }  
  
    /**  
     * 如果返回true，数据全部加载完毕后会隐藏加载更多  
     * 如果返回false，数据全部加载完毕后会显示getLoadEndViewId()布局  
     */  
    @Override   
    public boolean isLoadEndGone() {  
        return true;  
    }  
  
    @Override protected int getLoadingViewId() {  
        return R.id.load_more_loading_view;  
    }  
  
    @Override   
    protected int getLoadFailViewId() {  
        return R.id.load_more_load_fail_view;  
    }  
  
    /**  
     * isLoadEndGone()为true，可以返回0，为false，不能返回0  
     */  
    @Override   
    protected int getLoadEndViewId() {  
        return 0;  
    }  
}  
```  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<FrameLayout  
    xmlns:android="http://schemas.android.com/apk/res/android"  
    android:layout_width="match_parent"  
    android:layout_height="@dimen/dp_40">  
    <LinearLayout  
        android:id="@+id/load_more_loading_view"  
        android:layout_width="match_parent"  
        android:layout_height="match_parent"  
        android:gravity="center"  
        android:orientation="horizontal">  
        <ProgressBar  
            android:id="@+id/loading_progress"  
            android:layout_width="wrap_content"  
            android:layout_height="wrap_content"  
            style="?android:attr/progressBarStyleSmall"  
            android:layout_marginRight="@dimen/dp_4"  
            android:indeterminateDrawable="@drawable/sample_footer_loading_progress"/>  
        <TextView  
            android:id="@+id/loading_text"  
            android:layout_width="wrap_content"  
            android:layout_height="wrap_content"  
            android:layout_marginLeft="@dimen/dp_4"  
            android:text="@string/loading"  
            android:textColor="#0dddb8"  
            android:textSize="@dimen/sp_14"/>  
    </LinearLayout>  
    <FrameLayout  
        android:id="@+id/load_more_load_fail_view"  
        android:layout_width="match_parent"  
        android:layout_height="match_parent"  
        android:visibility="gone">  
        <TextView  
            android:id="@+id/tv_prompt"  
            android:layout_width="wrap_content"  
            android:layout_height="wrap_content"  
            android:layout_gravity="center"  
            android:textColor="#0dddb8"  
            android:text="@string/load_failed"/>  
    </FrameLayout>  
</FrameLayout>  
```  
  
## 添加分组  
实体类必须继承SectionEntity  
```java  
public class MySection extends SectionEntity<Video> {  
    private boolean isMore;  
    public MySection(boolean isHeader, String header) {  
        super(isHeader, header);  
    }  
    public MySection(Video t) {  
        super(t);  
    }  
}  
```  
adapter构造需要传入两个布局id，第一个是item的，第二个是head的，在convert方法里面加载item数据，在convertHead方法里面加载head数据  
  
```java  
public class SectionAdapter extends BaseSectionQuickAdapter<MySection> {  
  public SectionAdapter(int layoutResId, int sectionHeadResId, List data) {  
        super(layoutResId, sectionHeadResId, data);  
    }  
  @Override  
  protected void convert(BaseViewHolder helper, MySection item) {  
        helper.setImageUrl(R.id.iv, (String) item.t);  
    }  
  @Override  
  protected void convertHead(BaseViewHolder helper,final MySection item) {  
        helper.setText(R.id.header, item.header);  
        helper.setOnClickListener(R.id.more, new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                Toast.makeText(context,item.header+"more..",Toast.LENGTH_LONG).show();  
            }  
        });  
    }  
}  
```  
  
## 自定义不同的item类型  
实体类必须实现MultiItemEntity，在设置数据的时候，需要给每一个数据设置itemType  
```java  
public class MultipleItem implements MultiItemEntity {  
    public static final int TEXT = 1;  
    public static final int IMG = 2;  
    private int itemType;  
  
    public MultipleItem(int itemType) {  
        this.itemType = itemType;  
    }  
    @Override  
  public int getItemType() {  
        return itemType;  
    }  
}  
```  
在构造里面addItemType绑定type和layout的关系  
```java  
public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder> {  
  
    public MultipleItemQuickAdapter(List data) {  
        super(data);  
        addItemType(MultipleItem.TEXT, R.layout.text_view);  
        addItemType(MultipleItem.IMG, R.layout.image_view);  
    }  
  
    @Override  
    protected void convert(BaseViewHolder helper, MultipleItem item) {  
        switch (helper.getItemViewType()) {  
            case MultipleItem.TEXT:  
                helper.setImageUrl(R.id.tv, item.getContent());  
                break;  
            case MultipleItem.IMG:  
                helper.setImageUrl(R.id.iv, item.getContent());  
                break;  
        }  
    }  
  
}  
```  
如有多布局有两行布局的item都是一样的，这个时候item是可以被复用的，我们可以使用GridLayoutManager  
```java  
multipleItemAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {  
   @Override  
    public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {  
        return data.get(position).getSpanSize();  
    }  
});  
```  
  
通过重写他的SapnSize来重用ImageView。 假设GridLayoutManager的spanCount是3，那么第一个、第二个、item的spanSize就是3，因为要占据一行（和权重差不多的意思）,然后第三个、第四个、第五个、item的spanSize就是1。  
  
## 设置空布局  
```java  
mQuickAdapter.setEmptyView(getView());  
```  
  
## 添加拖拽、滑动删除  
 拖拽和滑动删除的回调方法  
  
```java  
OnItemDragListener onItemDragListener = new OnItemDragListener() {  
    @Override  
    public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos){}  
    @Override  
    public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {}  
    @Override  
    public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {}  
}  
```  
  
```java  
OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {  
    @Override  
    public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {}  
    @Override  
    public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}  
    @Override  
  public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {}  
};  
```  
adapter需要继承BaseItemDraggableAdapter  
```java  
public class ItemDragAdapter extends BaseItemDraggableAdapter<String, BaseViewHolder> {  
    public ItemDragAdapter(List data) {  
        super(R.layout.item_draggable_view, data);  
    }  
  
    @Override  
  protected void convert(BaseViewHolder helper, String item) {  
        helper.setText(R.id.tv, item);  
    }  
}  
```  
Activity使用代码  
```java  
mAdapter = new ItemDragAdapter(mData);  
  
ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemDragAndSwipeCallback(mAdapter));  
itemTouchHelper.attachToRecyclerView(mRecyclerView);  
// 开启拖拽  
mAdapter.enableDragItem(itemTouchHelper, R.id.textView, true);  
mAdapter.setOnItemDragListener(onItemDragListener);  
// 开启滑动删除  
mAdapter.enableSwipeItem();  
mAdapter.setOnItemSwipeListener(onItemSwipeListener);  
```  
默认不支持多个不同的 ViewType 之间进行拖拽，如果开发者有所需求，重写ItemDragAndSwipeCallback里的onMove()方法，return true即可  
  
## 分组的伸缩栏  
代码  
```java  
// if you don't want to extent a class, you can also use the interface IExpandable. AbstractExpandableItem is just a helper class.  
public class Level0Item extends AbstractExpandableItem<Level1Item> {...}  
public class Level1Item extends AbstractExpandableItem<Person> {...}  
public class Person {...}  
```  
adapter需要继承BaseMultiItemQuickAdapter  
```java  
public class ExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {   
    public ExpandableItemAdapter(List<MultiItemEntity> data) {      
        super(data);  
        addItemType(TYPE_LEVEL_0, R.layout.item_expandable_lv0);     
        addItemType(TYPE_LEVEL_1, R.layout.item_expandable_lv1);      
        addItemType(TYPE_PERSON, R.layout.item_text_view);  
    }  
    @Override  
  protected void convert(final BaseViewHolder holder, final MultiItemEntity item) {  
        switch (holder.getItemViewType()) {  
        case TYPE_LEVEL_0:  
            ....  
      //set view content  
           holder.itemView.setOnClickListener(new View.OnClickListener() {  
               @Override  
        public void onClick(View v) {  
                   int pos = holder.getAdapterPosition();  
                   if (lv0.isExpanded())  collapse(pos);  
                   else expand(pos);  
           }});  
           break;  
        case TYPE_LEVEL_1: // similar with level 0  
      break;  
        case TYPE_PERSON: //just set the content  
      break;  
    }  
}  
```  
Activity使用代码  
```java  
public class ExpandableUseActivity extends Activity {  
    @Override  
  protected  void onCreate(Bundle savedInstanceState) {  
        ...  
    ArrayList<MultiItemEntity> list = generateData();  
        ExpandableItemAdapter adapter = new ExpandableItemAdapter(list);  
        mRecyclerView.setAdapter(adapter);  
    }  
  
    private ArrayList<MultiItemEntity> generateData() {  
        ArrayList<MultiItemEntity> res = new ArrayList<>();  
        for (int i = 0; i < lv0Count; i++) {  
            Level0Item lv0 = new Level0Item(...);  
            for (int j = 0; j < lv1Count; j++) {  
                Level1Item lv1 = new Level1Item(...);  
                for (int k = 0; k < personCount; k++) {  
                    lv1.addSubItem(new Person());  
                }  
                lv0.addSubItem(lv1);  
            }  
            res.add(lv0);  
        }  
        return res;  
    }  
}  
```  
  
## 自定义BaseViewHolder  
之前的写法：public class QuickAdapter extends BaseQuickAdapter<Status>  
现在的写法：public class QuickAdapter extends BaseQuickAdapter<Status, BaseViewHolder>  
注意：需要单独建一个外部类继承BaseViewHolder，否则部分机型会出现ClassCastException  
  
## 案例  
```java  
public class BRVAHActivity extends Activity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {  
    private List<String> mDatas;  
    private RecyclerView mRecyclerView;  
    private PullToRefreshAdapter mAdapter;  
    private SwipeRefreshLayout mSwipeRefreshLayout;  
​  
    private static final int TOTAL_COUNTER = 18;  
    private static final int PAGE_SIZE = 6;  
    private static final int DELAY_MILLIS = 1000;  
    private int mCurrentCounter = 0;  
​  
    private boolean isErr;  
    private boolean mLoadMoreEndGone = false;  
​  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
​  
        setContentView(R.layout.activity_main);  
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);  
        mSwipeRefreshLayout.setOnRefreshListener(this);  
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));  
​  
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);  
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  
​  
        initDate();  
        initAdapter();  
​  
        ImageView headView = new ImageView(this);  
        headView.setImageResource(R.mipmap.ic_launcher);  
        mAdapter.addHeaderView(headView);  
    }  
​  
    private void initDate() {  
        mDatas = new ArrayList<String>();  
        for (int i = 'A'; i < 'z'; i++) {  
            mDatas.add("" + (char) i);  
        }  
    }  
​  
    private void initAdapter() {  
        mAdapter = new PullToRefreshAdapter();  
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);  
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);//动画  
        mAdapter.setPreLoadNumber(3);//预加载  
        mRecyclerView.setAdapter(mAdapter);  
        mCurrentCounter = mAdapter.getData().size();  
​  
        //item点击事件  
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {  
            @Override  
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {  
                Toast.makeText(BRVAHActivity.this, "onItemClick" + position, Toast.LENGTH_SHORT).show();  
            }  
        });  
        //item中View的点击事件  
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {  
            @Override  
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {  
                Toast.makeText(BRVAHActivity.this, "onItemChildClick" + position, Toast.LENGTH_SHORT).show();  
            }  
        });  
    }  
​  
    @Override  
    public void onLoadMoreRequested() {  
        mSwipeRefreshLayout.setEnabled(false);  
        if (mAdapter.getData().size() < PAGE_SIZE) {  
            mAdapter.loadMoreEnd(true);  
        } else {  
            if (mCurrentCounter >= TOTAL_COUNTER) {  
                //pullToRefreshAdapter.loadMoreEnd();//default visible  
                mAdapter.loadMoreEnd(mLoadMoreEndGone);//true is gone,false is visible  
            } else {  
                if (isErr) {  
                    mAdapter.addData("新数据");  
                    mCurrentCounter = mAdapter.getData().size();  
                    mAdapter.loadMoreComplete();  
                } else {  
                    isErr = true;  
                    Toast.makeText(BRVAHActivity.this, "错误", Toast.LENGTH_LONG).show();  
                    mAdapter.loadMoreFail();  
                }  
            }  
            mSwipeRefreshLayout.setEnabled(true);  
        }  
    }  
​  
    @Override  
    public void onRefresh() {  
        mAdapter.setEnableLoadMore(false);//禁止加载  
        new Handler().postDelayed(new Runnable() {  
            @Override  
            public void run() {  
                mAdapter.setNewData(mDatas);  
                isErr = false;  
                mCurrentCounter = PAGE_SIZE;  
                mSwipeRefreshLayout.setRefreshing(false);  
                mAdapter.setEnableLoadMore(true);//启用加载  
            }  
        }, DELAY_MILLIS);  
    }  
​  
    class PullToRefreshAdapter extends BaseQuickAdapter<String, BaseViewHolder> {  
        public PullToRefreshAdapter() {  
            super(R.layout.item2, mDatas);  
        }  
​  
        @Override  
        protected void convert(BaseViewHolder helper, String item) {  
            helper.setText(R.id.id_num, item)  
                    .setImageResource(R.id.iv, R.drawable.actionbar_add_icon)  
                    .addOnClickListener(R.id.iv);  
        }  
    }  
}  
```  
2017-5-31  
