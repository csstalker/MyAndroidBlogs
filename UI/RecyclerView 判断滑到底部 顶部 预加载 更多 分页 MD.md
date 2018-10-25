| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RecyclerView 判断滑到底部 顶部 预加载 更多 分页 MD    
***  
目录  
===  

- [项目中的案例【预加载】](# 项目中的案例【预加载】)
- [项目中的另一个案例](# 项目中的另一个案例)
- [利用 lastVisibleItemPosition 判断【不靠谱】](# 利用 lastvisibleitemposition 判断【不靠谱】)
- [利用 computeVerticalScrollRange 等判断【不靠谱】](# 利用 computeverticalscrollrange 等判断【不靠谱】)
- [利用 canScrollVertically(direction) 判断【比较靠谱】](# 利用 canscrollverticallydirection 判断【比较靠谱】)
- [利用 LinearLayoutManager 来判断【垃圾】](# 利用 linearlayoutmanager 来判断【垃圾】)
  
判断RecyclerView到达底部的几种方法  
  
# 项目中的案例【预加载】  
```java  
mRvChat.addOnScrollListener(new RecyclerView.OnScrollListener() {  
    private int minLeftItemCount=10;//剩余多少条时开始加载更多  
    @Override  
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {  
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {  
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();  
            int itemCount = layoutManager.getItemCount();  
            int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();  
            XLog.tag("bqt3").i("【总数】" + itemCount + "【位置】" + lastPosition);  
            if (lastPosition == layoutManager.getItemCount() - 1) {//容错处理，保证滑到最后一条时一定可以加载更多  
                this.onLoadMore();  
            } else {  
                if (itemCount > minLeftItemCount) {  
                    if (lastPosition == itemCount - minLeftItemCount) {  
                        //一定要意识到，onScrolled方法并不是一直被回调的，估计最多一秒钟几十次  
                        //所以当此条件满足时，可能并没有回调onScrolled方法，也就不会调用onLoadMore方法  
                        //所以一定要想办法弥补这隐藏的bug，最简单的方式就是当滑到最后一条时一定可以加载更多  
                        this.onLoadMore();  
                    }  
                } else {//（第一次进入时）如果总数特别少，直接加载更多  
                    this.onLoadMore();  
                }  
            }  
        }  
    }  
    private void onLoadMore() {  
        if (canLoadMore) {  
            canLoadMore = false;  
            ChatReqHelper.requestGroupHis(groupId, ((ChatModel) mGroupChats.get(mGroupChats.size() - 1)).getMsgId());  
            XLog.tag("bqt3").i("【加载更多数据】");  
        }  
    }  
});  
```  
  
# 项目中的另一个案例  
```java  
rv.addOnScrollListener(new RecyclerView.OnScrollListener() {  
    @Override  
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {  
        super.onScrolled(recyclerView, dx, dy);  
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) rv.getLayoutManager();  
        int[] positions = layoutManager.findLastVisibleItemPositions(null);  
        if (positions != null && positions.length > 0) {  
            for (int position : positions) {  
                if (position >= mDatas.size() - layoutManager.getSpanCount() || !rv.canScrollVertically(1)) {//滑到底部了  
                    if (canLoadMore) {  
                        XLog.tag("bqt_red").i("加载更多 "+position+" "+rv.canScrollVertically(1));  
                        canLoadMore = false;  
                        doRequest();  
                    }  
                    break;  
                }  
            }  
        }  
    }  
});  
```  
  
# 利用 lastVisibleItemPosition 判断【不靠谱】  
简单判断  
```java  
public static boolean isVisBottom(RecyclerView recyclerView){    
  LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();    
  int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition(); //屏幕中最后一个可见子项的position  
  int visibleItemCount = layoutManager.getChildCount(); //当前屏幕所看到的子项个数  
  int totalItemCount = layoutManager.getItemCount(); //当前RecyclerView的所有子项个数  
  int state = recyclerView.getScrollState(); //RecyclerView的滑动状态  
  if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE){     
     return true;   
  }else {     
     return false;    
  }  
}  
```  
当屏幕中最后一个子项 lastVisibleItemPosition 等于所有子项个数 totalItemCount - 1，那么RecyclerView就到达了底部。    
但是，如果 totalItemCount 等于1，并且这个item的高度比屏幕还要高时，我们可以发现这个item没完全显示出来就已经被判断为拉到底部。    
  
# 利用 computeVerticalScrollRange 等判断【不靠谱】  
这种方法原理其实很简单，而且也是View自带的方法  
```java  
public static boolean isSlideToBottom(RecyclerView recyclerView) {  
    return recyclerView != null  
            && recyclerView.computeVerticalScrollExtent()//当前屏幕显示区域的高度  
            + recyclerView.computeVerticalScrollOffset()//当前屏幕之前滑过的距离  
            >= recyclerView.computeVerticalScrollRange();//整个View控件的高度  
}  
```  
这种方法经过测试，暂时还没发现有bug，而且它用的是View自带的方法，所以个人觉得比较靠谱。  
  
# 利用 canScrollVertically(direction) 判断【比较靠谱】  
这种方法更简单，就通过简单的调用方法，就可以得到你想要的结果。  
这种方法与第二种方法其实是同一种方法，我们看看 canScrollVertically 的源码：  
```java  
/**  
 * Check if this view can be scrolled vertically in a certain direction.  
 *  
 * @param direction Negative to check scrolling up, positive to check scrolling down.  
 * @return true if this view can be scrolled in the specified direction, false otherwise.  
 */  
public boolean canScrollVertically(int direction) {  
    final int offset = computeVerticalScrollOffset();  
    final int range = computeVerticalScrollRange() - computeVerticalScrollExtent();  
    if (range == 0) return false;  
    if (direction < 0) {  
        return offset > 0;  
    } else {  
        return offset < range - 1;  
    }  
}  
```  
canScrollVertically(1)的值表示是否能向上滚动，true表示能滚动，false表示已经滚动到底部     
canScrollVertically(-1)的值表示是否能向下滚动，true表示能滚动，false表示已经滚动到顶部    
  
# 利用 LinearLayoutManager 来判断【垃圾】  
这种方法其实是比较呆板的，就是利用LinearLayoutManager的几个方法算出已经滑过的子项的距离、屏幕的高度、RecyclerView的总高度，最后将高度作比较。  
  
- 算出一个子项的高度  
```java  
public static int getItemHeight(RecyclerView recyclerView) {  
    int itemHeight = 0;  
    View child = null;  
    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();  
    int firstPos = layoutManager.findFirstCompletelyVisibleItemPosition();  
    int lastPos = layoutManager.findLastCompletelyVisibleItemPosition();  
    child = layoutManager.findViewByPosition(lastPos);  
    if (child != null) {  
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();  
        itemHeight = child.getHeight() + params.topMargin + params.bottomMargin;  
    }  
    return itemHeight;  
}  
```  
  
- 算出滑过的子项的总距离  
```java  
public static int getLinearTotalHeight(RecyclerView recyclerView) {  
    int totalHeight = 0;  
    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();  
    View child = layoutManager.findViewByPosition(layoutManager.findFirstVisibleItemPosition());  
    int headerCildHeight = getHeaderHeight(recyclerView);  
    if (child != null) {  
        int itemHeight = getItemHeight(recyclerView);  
        int childCount = layoutManager.getItemCount();  
        totalHeight = headerCildHeight + (childCount - 1) * itemHeight;  
    }  
    return totalHeight;  
}  
```  
  
- 算出所有子项的总高度  
```java  
public static boolean isLinearBottom(RecyclerView recyclerView) {  
    boolean isBottom = true;  
    int scrollY = getLinearScrollY(recyclerView);  
    int totalHeight = getLinearTotalHeight(recyclerView);  
    int height = recyclerView.getHeight();  
    // Log.e("height","scrollY " + scrollY + " totalHeight " + totalHeight + " recyclerHeight " + height);    
    if (scrollY + height < totalHeight) {  
        isBottom = false;  
    }  
    return isBottom;  
}  
```  
  
- 其他逻辑  
```java  
public static int getHeaderHeight(RecyclerView recyclerView) {  
    int headerCildHeight = 0;  
      
    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();  
    int firstHeaderPos = layoutManager.findFirstCompletelyVisibleItemPosition();  
    View headerCild = layoutManager.findViewByPosition(firstHeaderPos);  
      
    if (headerCild != null) {  
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) headerCild.getLayoutParams();  
        headerCildHeight = headerCild.getHeight() + params.topMargin + params.bottomMargin;  
    }  
    return headerCildHeight;  
}  
```  
```java  
public static int getLinearScrollY(RecyclerView recyclerView) {  
    int scrollY = 0;  
    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();  
    int headerCildHeight = getHeaderHeight(recyclerView);  
    int firstPos = layoutManager.findFirstVisibleItemPosition();  
    View child = layoutManager.findViewByPosition(firstPos);  
    int itemHeight = getItemHeight(recyclerView);  
    if (child != null) {  
        int firstItemBottom = layoutManager.getDecoratedBottom(child);  
        scrollY = headerCildHeight + itemHeight * firstPos - firstItemBottom;  
        if (scrollY < 0) {  
            scrollY = 0;  
        }  
    }  
      
    return scrollY;  
}  
```  
虽然这种方法看上去比较呆板的同时考虑不很周全，但这种方法可以对RecylerView的LinearLayoutManager有深一步的理解！  
  
2017.06.23  
