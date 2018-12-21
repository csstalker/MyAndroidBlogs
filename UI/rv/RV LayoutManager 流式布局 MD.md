| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RV LayoutManager 流式布局 MD  
***  
目录  
===  

- [流式布局](#流式布局)
	- [使用](#使用)
	- [测试代码](#测试代码)
	- [FlowLayoutManager](#flowlayoutmanager)
  
# 流式布局  
## 使用  
```java  
.setLayoutManager(new FlowLayoutManager());  
```  
![](index_files/3e61af42-a638-42f5-aab4-3e92e71b7dd6.png)  
  
## 测试代码  
```java  
public static List<TemplateTag> getTemplateTagList() {  
    List<TemplateTag> list = new ArrayList<>();  
    int colors[] = new int[10];  
      
    for (int i = 0, j = colors.length; i < j; i++) {  
        colors[i] = 0xFF000000 + new Random().nextInt(0xFFFFFF);  
        //注意，Integer.MAX_VALUE= 0x7fffffff，超过这个值就是负数了，所以通过随机数获取int值时，传入的值要小于0x7fffffff  
    }  
    list.add(new TemplateTag("白乾涛", colors[0]));  
    list.add(new TemplateTag("tag", colors[1]));  
    list.add(new TemplateTag("标签 tag", colors[2]));  
    list.add(new TemplateTag("1", colors[3]));  
    list.add(new TemplateTag("颜色 " + colors[4], colors[4]));  
    list.add(new TemplateTag("包青天", colors[5]));  
    list.add(new TemplateTag("包青天包青天", colors[6]));  
    list.add(new TemplateTag("包青天包青天包青天包青天包青天包青天包青天", colors[7]));  
    list.add(new TemplateTag("包青天天包青天", colors[8]));  
    list.add(new TemplateTag("包青天123456789123456789123456789123456789123456789", colors[9]));  
    return list;  
}  
```  
  
根据颜色设置背景  
```java  
tvContent.setBackground(getDrawable(mTemplateTag.color));  
```  
  
```java  
private GradientDrawable getDrawable(int bgColor) {  
    GradientDrawable gradientDrawable = new GradientDrawable();  
    gradientDrawable.setColor(bgColor);  
    gradientDrawable.setCornerRadius(DisplayUtil.dip2px(ApplicationContext.getContext(), 4));  
    return gradientDrawable;  
}  
```  
  
## FlowLayoutManager  
```java  
/**  
 * 描述：一种流式布局的LayoutManager<p>  
 * 添加时间：2017/12/26 <p>  
 * 开发者：<a href="http://www.cnblogs.com/baiqiantao">白乾涛</a><p>  
 */  
public class FlowLayoutManager extends RecyclerView.LayoutManager {  
      
    private static final String TAG = FlowLayoutManager.class.getSimpleName();  
    final FlowLayoutManager self = this;  
      
    private int width, height;  
    private int left, top, right;  
    //最大容器的宽度  
    private int usedMaxWidth;  
    //竖直方向上的偏移量  
    private int verticalScrollOffset = 0;  
      
    public int getTotalHeight() {  
        return totalHeight;  
    }  
      
    //计算显示的内容的高度  
    private int totalHeight = 0;  
    private Row row = new Row();  
    private List<Row> lineRows = new ArrayList<>();  
      
    //保存所有的Item的上下左右的偏移量信息  
    private SparseArray<Rect> allItemFrames = new SparseArray<>();  
      
    public FlowLayoutManager() {  
        //设置主动测量规则,适应recyclerView高度为wrap_content  
        setAutoMeasureEnabled(true);  
    }  
      
    //每个item的定义  
    public class Item {  
        int useHeight;  
        View view;  
          
        public void setRect(Rect rect) {  
            this.rect = rect;  
        }  
          
        Rect rect;  
          
        public Item(int useHeight, View view, Rect rect) {  
            this.useHeight = useHeight;  
            this.view = view;  
            this.rect = rect;  
        }  
    }  
      
    //行信息的定义  
    public class Row {  
        public void setCuTop(float cuTop) {  
            this.cuTop = cuTop;  
        }  
          
        public void setMaxHeight(float maxHeight) {  
            this.maxHeight = maxHeight;  
        }  
          
        //每一行的头部坐标  
        float cuTop;  
        //每一行需要占据的最大高度  
        float maxHeight;  
        //每一行存储的item  
        List<Item> views = new ArrayList<>();  
          
        public void addViews(Item view) {  
            views.add(view);  
        }  
    }  
      
    @Override  
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {  
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);  
    }  
      
    //该方法主要用来获取每一个item在屏幕上占据的位置  
    @Override  
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {  
        Log.d(TAG, "onLayoutChildren");  
        if (getItemCount() == 0) {  
            detachAndScrapAttachedViews(recycler);  
            verticalScrollOffset = 0;  
            return;  
        }  
        if (getChildCount() == 0 && state.isPreLayout()) {  
            return;  
        }  
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍  
        detachAndScrapAttachedViews(recycler);  
        if (getChildCount() == 0) {  
            width = getWidth();  
            height = getHeight();  
            left = getPaddingLeft();  
            right = getPaddingRight();  
            top = getPaddingTop();  
            usedMaxWidth = width - left - right;  
        }  
        totalHeight = 0;  
        int cuLineTop = top;  
        //当前行使用的宽度  
        int cuLineWidth = 0;  
        int itemLeft;  
        int itemTop;  
        int maxHeightItem = 0;  
        row = new Row();  
        lineRows.clear();  
        allItemFrames.clear();  
        removeAllViews();  
        for (int i = 0; i < getItemCount(); i++) {  
            Log.d(TAG, "index:" + i);  
            View childAt = recycler.getViewForPosition(i);  
            if (View.GONE == childAt.getVisibility()) {  
                continue;  
            }  
            measureChildWithMargins(childAt, 0, 0);  
            int childWidth = getDecoratedMeasuredWidth(childAt);  
            int childHeight = getDecoratedMeasuredHeight(childAt);  
            int childUseWidth = childWidth;  
            int childUseHeight = childHeight;  
            //如果加上当前的item还小于最大的宽度的话  
            if (cuLineWidth + childUseWidth <= usedMaxWidth) {  
                itemLeft = left + cuLineWidth;  
                itemTop = cuLineTop;  
                Rect frame = allItemFrames.get(i);  
                if (frame == null) {  
                    frame = new Rect();  
                }  
                frame.set(itemLeft, itemTop, itemLeft + childWidth, itemTop + childHeight);  
                allItemFrames.put(i, frame);  
                cuLineWidth += childUseWidth;  
                maxHeightItem = Math.max(maxHeightItem, childUseHeight);  
                row.addViews(new Item(childUseHeight, childAt, frame));  
                row.setCuTop(cuLineTop);  
                row.setMaxHeight(maxHeightItem);  
            } else {  
                //换行  
                formatAboveRow();  
                cuLineTop += maxHeightItem;  
                totalHeight += maxHeightItem;  
                itemTop = cuLineTop;  
                itemLeft = left;  
                Rect frame = allItemFrames.get(i);  
                if (frame == null) {  
                    frame = new Rect();  
                }  
                frame.set(itemLeft, itemTop, itemLeft + childWidth, itemTop + childHeight);  
                allItemFrames.put(i, frame);  
                cuLineWidth = childUseWidth;  
                maxHeightItem = childUseHeight;  
                row.addViews(new Item(childUseHeight, childAt, frame));  
                row.setCuTop(cuLineTop);  
                row.setMaxHeight(maxHeightItem);  
            }  
            //不要忘了最后一行进行刷新下布局  
            if (i == getItemCount() - 1) {  
                formatAboveRow();  
                totalHeight += maxHeightItem;  
            }  
              
        }  
        totalHeight = Math.max(totalHeight, getVerticalSpace());  
        fillLayout(recycler, state);  
    }  
      
    //对出现在屏幕上的item进行展示，超出屏幕的item回收到缓存中  
    private void fillLayout(RecyclerView.Recycler recycler, RecyclerView.State state) {  
        if (state.isPreLayout()) { // 跳过preLayout，preLayout主要用于支持动画  
            return;  
        }  
          
        // 当前scroll offset状态下的显示区域  
        Rect displayFrame = new Rect(getPaddingLeft(), getPaddingTop() + verticalScrollOffset,  
                getWidth() - getPaddingRight(), verticalScrollOffset + (getHeight() - getPaddingBottom()));  
          
        //对所有的行信息进行遍历  
        for (int j = 0; j < lineRows.size(); j++) {  
            Row row = lineRows.get(j);  
            float lineTop = row.cuTop;  
            float lineBottom = lineTop + row.maxHeight;  
            //如果该行在屏幕中，进行放置item  
            if (lineTop < displayFrame.bottom && displayFrame.top < lineBottom) {  
                List<Item> views = row.views;  
                for (int i = 0; i < views.size(); i++) {  
                    View scrap = views.get(i).view;  
                    measureChildWithMargins(scrap, 0, 0);  
                    addView(scrap);  
                    Rect frame = views.get(i).rect;  
                    //将这个item布局出来  
                    layoutDecoratedWithMargins(scrap,  
                            frame.left,  
                            frame.top - verticalScrollOffset,  
                            frame.right,  
                            frame.bottom - verticalScrollOffset);  
                }  
            } else {  
                //将不在屏幕中的item放到缓存中  
                List<Item> views = row.views;  
                for (int i = 0; i < views.size(); i++) {  
                    View scrap = views.get(i).view;  
                    removeAndRecycleView(scrap, recycler);  
                }  
            }  
        }  
    }  
      
    /**  
     * 计算每一行没有居中的viewgroup，让居中显示  
     */  
    private void formatAboveRow() {  
        List<Item> views = row.views;  
        for (int i = 0; i < views.size(); i++) {  
            Item item = views.get(i);  
            View view = item.view;  
            int position = getPosition(view);  
            //如果该item的位置不在该行中间位置的话，进行重新放置  
            if (allItemFrames.get(position).top < row.cuTop + (row.maxHeight - views.get(i).useHeight) / 2) {  
                Rect frame = allItemFrames.get(position);  
                if (frame == null) {  
                    frame = new Rect();  
                }  
                frame.set(allItemFrames.get(position).left, (int) (row.cuTop + (row.maxHeight - views.get(i).useHeight) / 2),  
                        allItemFrames.get(position).right, (int) (row.cuTop + (row.maxHeight - views.get(i).useHeight) / 2 + getDecoratedMeasuredHeight(view)));  
                allItemFrames.put(position, frame);  
                item.setRect(frame);  
                views.set(i, item);  
            }  
        }  
        row.views = views;  
        lineRows.add(row);  
        row = new Row();  
    }  
      
    /**  
     * 竖直方向需要滑动的条件  
     *  
     * @return  
     */  
    @Override  
    public boolean canScrollVertically() {  
        return true;  
    }  
      
    //监听竖直方向滑动的偏移量  
    @Override  
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler,  
                                  RecyclerView.State state) {  
          
        Log.d("TAG", "totalHeight:" + totalHeight);  
        //实际要滑动的距离  
        int travel = dy;  
          
        //如果滑动到最顶部  
        if (verticalScrollOffset + dy < 0) {//限制滑动到顶部之后，不让继续向上滑动了  
            travel = -verticalScrollOffset;//verticalScrollOffset=0  
        } else if (verticalScrollOffset + dy > totalHeight - getVerticalSpace()) {//如果滑动到最底部  
            travel = totalHeight - getVerticalSpace() - verticalScrollOffset;//verticalScrollOffset=totalHeight - getVerticalSpace()  
        }  
          
        //将竖直方向的偏移量+travel  
        verticalScrollOffset += travel;  
          
        // 平移容器内的item  
        offsetChildrenVertical(-travel);  
        fillLayout(recycler, state);  
        return travel;  
    }  
      
    private int getVerticalSpace() {  
        return self.getHeight() - self.getPaddingBottom() - self.getPaddingTop();  
    }  
      
    public int getHorizontalSpace() {  
        return self.getWidth() - self.getPaddingLeft() - self.getPaddingRight();  
    }  
      
}  
```  
  
2017-12-27  
