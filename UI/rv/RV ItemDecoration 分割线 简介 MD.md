| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RV ItemDecoration 分割线 简介 MD  
[Demo地址](https://github.com/baiqiantao/RecyclerViewDemo)  
[RecyclerView-FlexibleDivider 库](https://github.com/yqritc/RecyclerView-FlexibleDivider)  
***  
目录  
===  

- [一个通用分割线](#一个通用分割线)
	- [ItemDecoration 分割线](#itemdecoration-分割线)
	- [测试代码](#测试代码)
	- [普通 Adapter](#普通-adapter)
	- [item 布局](#item-布局)
- [RecyclerView.ItemDecoration 简介](#recyclerviewitemdecoration-简介)
	- [API](#api)
	- [官方案例 DividerItemDecoration](#官方案例-divideritemdecoration)
		- [getItemOffsets 方法解析](#getitemoffsets-方法解析)
		- [onDraw 方法解析](#ondraw-方法解析)
		- [总结](#总结)
  
# 一个通用分割线  
一个通用分割线，适用于GridView，当然也适用于普通的RecyclerView。  
支持在item之间设置任何类型的间距，支持控制是否显示上下左右间隔及是否绘制上下左右背景  
  
## ItemDecoration 分割线  
```java  
/**  
 * Desc：适用于GridView，当然也适用于普通的RecyclerView  
 * 支持在item之间设置任何类型的间距，支持控制是否显示上下左右间隔及是否绘制上下左右背景  
 */  
public class GridItemDecoration extends RecyclerView.ItemDecoration {  
      
    private Drawable verticalDivider;  
    private Drawable horizontalDivider;  
    private int spanCount;  
    private int verticalSpaceSize;  
    private int horizontalSpaceSize;  
    private boolean includeVerticalEdge;//是否绘制左右边界，注意不绘制并不表示没有预留边界空间  
    private boolean includeHorizontalEdge;//是否绘制上下边界，注意不绘制并不表示没有预留边界空间  
      
    private GridItemDecoration(Builder builder) {  
        verticalDivider = builder.verticalDivider;  
        horizontalDivider = builder.horizontalDivider;  
        spanCount = builder.spanCount;  
        verticalSpaceSize = builder.verticalSpaceSize;  
        horizontalSpaceSize = builder.horizontalSpaceSize;  
        includeVerticalEdge = builder.includeVerticalEdge;  
        includeHorizontalEdge = builder.includeHorizontalEdge;  
    }  
      
    public static Builder newBuilder() {  
        return new Builder();  
    }  
      
    @Override  
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {  
        int position = parent.getChildAdapterPosition(view); // item position  
        int column = position % spanCount; // item column  
        if (verticalSpaceSize > 0) {  
            outRect.left = verticalSpaceSize - column * verticalSpaceSize / spanCount;  
            outRect.right = (column + 1) * verticalSpaceSize / spanCount;  
        } else {  
            outRect.left = column * verticalSpaceSize / spanCount;  
            outRect.right = verticalSpaceSize - (column + 1) * verticalSpaceSize / spanCount;  
        }  
          
        if (horizontalSpaceSize > 0) {  
            if (position < spanCount) outRect.top = horizontalSpaceSize; // top edge  
            outRect.bottom = horizontalSpaceSize; // item bottom  
        } else {  
            if (position >= spanCount) outRect.top = horizontalSpaceSize; // item top  
        }  
          
        log("确定边界" + outRect.left + "  " + outRect.right + "  " + outRect.top + "  " + outRect.bottom);  
    }  
      
    @Override  
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {  
        log(toString());  
        //在边界上绘制图形  
        if (horizontalDivider != null && horizontalSpaceSize > 0) {  
            drawHorizontalLineAtItemTop(c, parent);//在每一个item的上面绘制水平分割线  
            drawLineAtTopAndBottom(c, parent);//绘制上下边界，包括倒数第二行中的部分item  
        }  
        if (verticalDivider != null && verticalSpaceSize > 0) {  
            drawVerticalLineAtItemLeft(c, parent);//绘制竖直分割线  
            if (includeVerticalEdge) drawLR(c, parent);//绘制左右边界  
        }  
    }  
      
    //在每一个item的上面绘制水平分割线  
    private void drawHorizontalLineAtItemTop(Canvas c, RecyclerView parent) {  
        int childCount = parent.getChildCount();  
        for (int position = 0; position < childCount; position++) {  
            if (position >= spanCount) {//第一行不绘制  
                View child = parent.getChildAt(position);  
                int left = child.getLeft();  
                int right = (position % spanCount == spanCount - 1) ? child.getRight() : (child.getRight() + verticalSpaceSize);  
                int top = child.getTop() - horizontalSpaceSize;  
                int bottom = top + horizontalSpaceSize;  
                log("绘制水平分割线" + left + "  " + right + "  " + top + "  " + bottom);  
                horizontalDivider.setBounds(left, top, right, bottom);  
                horizontalDivider.draw(c);  
            }  
        }  
    }  
      
    //绘制竖直分割线  
    private void drawVerticalLineAtItemLeft(Canvas c, RecyclerView parent) {  
        int childCount = parent.getChildCount();  
        for (int position = 0; position < childCount; position++) {//第一列不绘制  
            if (position % spanCount != 0) {  
                View child = parent.getChildAt(position);  
                int left = child.getLeft() - verticalSpaceSize;  
                int right = left + verticalSpaceSize;  
                int top = child.getTop() /*- horizontalSpaceSize*/;  
                int bottom = child.getBottom();  
                log("绘制竖直分割线" + left + "  " + right + "  " + top + "  " + bottom);  
                verticalDivider.setBounds(left, top, right, bottom);  
                verticalDivider.draw(c);  
            }  
        }  
    }  
      
    //绘制左右边界  
    private void drawLR(Canvas c, RecyclerView parent) {  
        int childCount = parent.getChildCount();  
        for (int position = 0; position < childCount; position++) {  
            View child = parent.getChildAt(position);  
              
            int maxLines = (childCount % spanCount == 0) ? childCount / spanCount : (childCount / spanCount + 1);//一共有多少行  
            boolean isLastLineItem = position / spanCount == maxLines - 1;//倒数第一行的元素  
            //最左边那条线  
            if (position % spanCount == 0) {  
                int left = child.getLeft() - verticalSpaceSize;  
                int right = left + verticalSpaceSize;  
                int top = child.getTop() - horizontalSpaceSize;  
                int bottom;  
                if (isLastLineItem) {//【左下角】  
                    bottom = child.getBottom() + horizontalSpaceSize;  
                } else {  
                    bottom = child.getBottom();  
                }  
                verticalDivider.setBounds(left, top, right, bottom);  
                verticalDivider.draw(c);  
                log("绘制最左边那条线" + left + "  " + right + "  " + top + "  " + bottom);  
            }  
              
            //最右边那条线  
            if ((position + 1) % spanCount == 0) {  
                int left = child.getRight();  
                int right = left + verticalSpaceSize;  
                int top = child.getTop() - horizontalSpaceSize;  
                  
                boolean isLastSecondLineItem = (position / spanCount == maxLines - 2) && (position + spanCount >= childCount);//倒数第二行的部分元素  
                int bottom;  
                if (isLastLineItem || isLastSecondLineItem) {  
                    bottom = child.getBottom() + horizontalSpaceSize;//【右下角】  
                } else {  
                    bottom = child.getBottom();  
                }  
                verticalDivider.setBounds(left, top, right, bottom);  
                verticalDivider.draw(c);  
                log("绘制最右边那条线" + left + "  " + right + "  " + top + "  " + bottom);  
            }  
        }  
    }  
      
    //绘制上下边界，包括倒数第二行中的部分item  
    private void drawLineAtTopAndBottom(Canvas c, RecyclerView parent) {  
        int childCount = parent.getChildCount();  
        for (int position = 0; position < childCount; position++) {  
            View child = parent.getChildAt(position);  
              
            //最上边那条线  
            if (includeHorizontalEdge && position < spanCount) {  
                int top = child.getTop() - horizontalSpaceSize;  
                int bottom = top + horizontalSpaceSize;  
                int left = child.getLeft();  
                  
                int right;  
                boolean isRightEdgeItem = (position + 1) % spanCount == 0;//最右侧的元素  
                boolean isFirstLineItem = childCount < spanCount && (position == childCount - 1);//只有一行的元素  
                if (isRightEdgeItem || isFirstLineItem) {//【右上角】  
                    right = child.getRight();  
                } else {  
                    right = child.getRight() + verticalSpaceSize;//如果同时有竖直分割线，则需要包含竖直分割线的宽度  
                }  
                  
                horizontalDivider.setBounds(left, top, right, bottom);  
                horizontalDivider.draw(c);  
                log("绘制最上边那条线" + left + "  " + right + "  " + top + "  " + bottom);  
            }  
              
            //最下边那条线  
            int maxLines = (childCount % spanCount == 0) ? childCount / spanCount : (childCount / spanCount + 1);//一共有多少行  
            boolean isLastLineItem = position / spanCount == maxLines - 1;//倒数第一行的元素  
            boolean isLastSecondLineItem = (position / spanCount == maxLines - 2) && (position + spanCount >= childCount);//倒数第二行的部分元素  
            if ((includeHorizontalEdge && isLastLineItem) || isLastSecondLineItem) {  
                int top = child.getBottom();  
                int bottom = top + horizontalSpaceSize;  
                int left = child.getLeft();  
                int right;  
                  
                boolean isLastSecondLineEdgeItem = isLastSecondLineItem && ((position + 1) % spanCount == 0);//倒数第二行最后一个元素  
                if (position == childCount - 1 || isLastSecondLineEdgeItem) {//【右下角】  
                    right = child.getRight();  
                } else {  
                    right = child.getRight() + verticalSpaceSize;//如果同时有竖直分割线，则需要包含竖直分割线的宽度  
                }  
                  
                horizontalDivider.setBounds(left, top, right, bottom);  
                horizontalDivider.draw(c);  
                log("绘制最下边那条线" + left + "  " + right + "  " + top + "  " + bottom);  
            }  
        }  
    }  
      
    public static final class Builder {  
        private Drawable horizontalDivider;  
        private Drawable verticalDivider;  
        private int spanCount;  
        private int horizontalSpaceSize;  
        private int verticalSpaceSize;  
        private boolean includeHorizontalEdge;  
        private boolean includeVerticalEdge;  
          
        private Builder() {  
        }  
          
        public Builder horizontalDivider(Drawable horizontalDivider, int horizontalSpaceSize, boolean includeHorizontalEdge) {  
            this.horizontalDivider = horizontalDivider;  
            this.horizontalSpaceSize = horizontalSpaceSize;  
            this.includeHorizontalEdge = includeHorizontalEdge;  
            return this;  
        }  
          
        public Builder verticalDivider(Drawable verticalDivider, int verticalSpaceSize, boolean includeVerticalEdge) {  
            this.verticalDivider = verticalDivider;  
            this.verticalSpaceSize = verticalSpaceSize;  
            this.includeVerticalEdge = includeVerticalEdge;  
            return this;  
        }  
          
        public Builder spanCount(int val) {  
            spanCount = val;  
            return this;  
        }  
          
        public GridItemDecoration build() {  
            return new GridItemDecoration(this);  
        }  
    }  
      
    private void log(String msg) {  
        Log.i("bqt", "【】" + msg);  
    }  
  
}  
```  
  
## 测试代码  
```java  
public class MainActivity extends Activity implements RecyclerAdapter.MyOnItemClickLitener {  
    private RecyclerView mRecyclerView;  
    private int spanCount;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"0、水平分割线，包含上下边界",  
                "1、水平分割线，不包含上下边界",  
                "2、竖直分割线，包含左右边界",  
                "3、竖直分割线，不包含左右边界",  
                  
                "4、水平分割线+透明竖直分割线，包含边界",  
                "5、水平分割线+透明竖直分割线，不包含边界",  
                "6、透明水平分割线+竖直分割线，包含边界",  
                "7、透明水平分割线+竖直分割线，不包含边界",  
                  
                "8、【水平分割线+竖直分割线，包含边界】",  
                "9、【水平分割线+竖直分割线，不包含边界】",  
                "10、图片分割线+竖直分割线，包含边界",  
                "11、图片分割线+竖直分割线，不包含边界",  
                  
                "12、重置",  
                "13、重置",  
                "14、重置",};  
        spanCount = 1 + new Random().nextInt(5);  
        RecyclerAdapter mRecyclerViewAdapter = new RecyclerAdapter(this, Arrays.asList(array));  
        mRecyclerViewAdapter.setOnItemClickLitener(this);  
          
        mRecyclerView = new RecyclerView(this);  
        mRecyclerView.setBackgroundColor(Color.BLACK);  
        mRecyclerView.setAdapter(mRecyclerViewAdapter);//设置adapter  
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));//设置布局管理器  
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画  
        setContentView(mRecyclerView);  
    }  
      
    @Override  
    public void onItemClick(View view, int position) {  
        GridItemDecoration decoration = null;  
        switch (position) {  
            case 0:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(new ColorDrawable(0x88ff0000), 60, true)  
                        .build();  
                break;  
            case 1:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(new ColorDrawable(0x88ff0000), 60, false)  
                        .build();  
                break;  
            case 2:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .verticalDivider(new ColorDrawable(0x88ff0000), 60, true)  
                        .build();  
                break;  
            case 3:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .verticalDivider(new ColorDrawable(0x88ff0000), 60, false)  
                        .build();  
                break;  
            //******************************************************************************************  
            case 4:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(new ColorDrawable(0x88ff0000), 20, true)  
                        .verticalDivider(new ColorDrawable(Color.TRANSPARENT), 60, true)  
                        .build();  
                break;  
            case 5:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(new ColorDrawable(0x88ff0000), 20, false)  
                        .verticalDivider(new ColorDrawable(Color.TRANSPARENT), 60, false)  
                        .build();  
                break;  
            case 6:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(new ColorDrawable(Color.TRANSPARENT), 20, true)  
                        .verticalDivider(new ColorDrawable(0x88ff0000), 60, true)  
                        .build();  
                break;  
            case 7:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(new ColorDrawable(Color.TRANSPARENT), 20, false)  
                        .verticalDivider(new ColorDrawable(0x88ff0000), 60, false)  
                        .build();  
                break;  
            //******************************************************************************************  
            case 8:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(new ColorDrawable(0x88ff0000), 20, true)  
                        .verticalDivider(new ColorDrawable(0x88ff0000), 60, true)  
                        .build();  
                break;  
            case 9:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(new ColorDrawable(0x88ff0000), 20, false)  
                        .verticalDivider(new ColorDrawable(0x88ff0000), 60, false)  
                        .build();  
                break;  
            case 10:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(getResources().getDrawable(R.drawable.icon2), 20, true)  
                        .verticalDivider(new ColorDrawable(0x88ff0000), 60, true)  
                        .build();  
                break;  
            case 11:  
                decoration = GridItemDecoration.newBuilder().spanCount(spanCount)  
                        .horizontalDivider(getResources().getDrawable(R.drawable.icon2), 20, false)  
                        .verticalDivider(new ColorDrawable(0x88ff0000), 60, false)  
                        .build();  
                break;  
            case 12:  
            case 13:  
            case 14:  
                recreate();  
                return;  
        }  
        mRecyclerView.addItemDecoration(decoration);  
    }  
      
    @Override  
    public void onItemLongClick(View view, int position) {  
        recreate();  
    }  
}  
```  
  
## 普通 Adapter  
```java  
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {  
    private Context context;  
    private List<String> mDatas;  
    private MyOnItemClickLitener mOnItemClickLitener;  
      
    public RecyclerAdapter(Context context, List<String> mDatas) {  
        this.context = context;  
        this.mDatas = mDatas;  
    }  
      
    @NonNull  
    @Override  
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));  
    }  
      
    @Override  
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {  
        holder.tv.setText(mDatas.get(position));  
          
        // 如果设置了回调，则设置点击事件  
        holder.itemView.setOnClickListener(v -> {  
            if (mOnItemClickLitener != null) mOnItemClickLitener.onItemClick(holder.itemView, holder.getAdapterPosition());  
        });  
        holder.itemView.setOnLongClickListener(v -> {  
            if (mOnItemClickLitener != null) mOnItemClickLitener.onItemLongClick(holder.itemView, holder.getAdapterPosition());  
            return false;  
        });  
    }  
      
    @Override  
    public int getItemCount() {  
        return mDatas.size();  
    }  
      
    /**  
     * 添加并更新数据，同时具有动画效果  
     */  
    public void addDataAt(int position, String data) {  
        mDatas.add(position, data);  
        notifyItemInserted(position);//更新数据集，注意如果用adapter.notifyDataSetChanged()将没有动画效果  
    }  
      
    /**  
     * 移除并更新数据，同时具有动画效果  
     */  
    public void removeDataAt(int position) {  
        mDatas.remove(position);  
        notifyItemRemoved(position);  
    }  
      
    public void setOnItemClickLitener(MyOnItemClickLitener mOnItemClickLitener) {  
        this.mOnItemClickLitener = mOnItemClickLitener;  
    }  
      
    class MyViewHolder extends RecyclerView.ViewHolder {  
        TextView tv;  
          
        public MyViewHolder(View view) {  
            super(view);  
            tv = view.findViewById(R.id.tv_name);  
        }  
    }  
      
    public interface MyOnItemClickLitener {  
        void onItemClick(View view, int position);  
          
        void onItemLongClick(View view, int position);  
    }  
}  
```  
  
## item 布局  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<TextView  
    xmlns:android="http://schemas.android.com/apk/res/android"  
    android:id="@+id/tv_name"  
    android:layout_width="match_parent"  
    android:layout_height="80dp"  
    android:background="#00f"  
    android:gravity="center"  
    android:text="包青天"  
    android:textColor="#fff"  
    android:textSize="12sp"></TextView>  
```  
  
# RecyclerView.ItemDecoration 简介  
RecyclerView 不支持`divider`这样的属性，虽然你可以通过给Item的布局设置 margin、background 等方式来间接添加分割线，但这种方式不够优雅，也不好控制。使用RecyclerView时，我们的分割线可以在代码中通过`.addItemDecoration(RecyclerView.ItemDecoration)`添加。  
  
作用：控制Item间的间隔  
已知子类：`DividerItemDecoration`,ItemTouchHelper  
ItemDecoration允许应用程序向来自 adapter 的数据集中的特定项目 views 添加特殊 drawing 和布局 offset。这可用于在项目之间绘制分隔符、highlights、视觉分组边界等。  
  
All ItemDecorations are drawn in the order they were added, before the item views (在`onDraw()`方法中) and after the items (在`onDrawOver(Canvas, RecyclerView, RecyclerView.State)`方法中).  
  
## API  
**getItemOffsets**  
```java  
void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)  
```  
Retrieve any offsets for the given item.检索给定项目的任何偏移量。   
- Each field of outRect specifies the number of pixels that the item view should be inset by, similar to padding or margin. The default implementation sets the bounds of outRect to 0 and returns.  
    outRect的每个字段指定项目视图应该插入的像素数，类似于填充或边距。默认实现将outRect的范围设置为0并返回。  
- If this ItemDecoration does not affect the positioning of item views, it should set all four fields of outRect (left, top, right, bottom) to zero before returning.  
    如果此ItemDecoration不影响项目视图的定位，则应将outRect（左，上，右，下）的所有四个字段设置为零，然后返回。  
- If you need to access Adapter for additional data, you can call getChildAdapterPosition(View) to get the adapter position of the View.  
    如果您需要访问适配器以获取其他数据，可以调用getChildAdapterPosition获取视图的适配器位置。  
  
参数：  
- outRec: Rect to receive the output.  
- view: The child view to decorate装饰  
- parent: RecyclerView this ItemDecoration is decorating装饰  
- state: The current state of RecyclerView.  
  
**onDraw**  
```java  
void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)  
```  
- Draw any appropriate decorations into the Canvas supplied to the RecyclerView.  
    在提供给RecyclerView的Canvas中绘制任何适当的装饰。  
- Any content drawn by this method will be drawn before the item views are drawn, and will thus appear underneath the views.  
    在绘制项目视图之前，将绘制通过此方法绘制的任何内容，因此将显示在视图下。  
  
**onDrawOver**  
```java  
void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)  
```  
- Draw any appropriate decorations into the Canvas supplied to the RecyclerView.  
    在提供给RecyclerView的Canvas中绘制任何适当的装饰。  
- Any content drawn by this method will be drawn after the item views are drawn and will thus appear over the views.  
    在绘制项目视图之后，将绘制通过此方法绘制的任何内容，并将出现在视图上。  
  
**废弃方法**  
- void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent)   
    This method was deprecated in API level 22.0.0. Use getItemOffsets(Rect, View, RecyclerView, State)  
- void onDraw(Canvas c, RecyclerView parent)   
    This method was deprecated in API level 22.0.0. Override onDraw(Canvas, RecyclerView, RecyclerView.State)  
- void onDrawOver(Canvas c, RecyclerView parent)  
    This method was deprecated in API level 22.0.0. Override onDrawOver(Canvas, RecyclerView, RecyclerView.State)  
  
## 官方案例 DividerItemDecoration  
适合用在 LinearLayoutManager 上，不适合 GridLayoutManager，因为如果一行有多个 Item，则间隔线会多次绘制，并且如果 Item 为最后一列则右边无间隔线，如果 Item 为最后一行则底部无分割线。  
  
> DividerItemDecoration is a RecyclerView.ItemDecoration that can be used as a divider between items of a LinearLayoutManager. It supports both HORIZONTAL and VERTICAL orientations.  
  
```java  
mRecyclerView.addItemDecoration(new DividerItemDecoration(context, mLayoutManager.getOrientation()));  
```  
### getItemOffsets 方法解析  
getItemOffsets 主要用于绘制Decorator，可以通过`outRect.set()`为每个Item设置一定的偏移量。  
DividerItemDecoration中getItemOffsets方法是这样实现的：  
```java  
@Override  
public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {  
    if (mOrientation == VERTICAL) {  
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());  
    } else {  
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);  
    }  
}  
```  
  
这个outRect设置的四个值是什么意思呢？先来看看它是在哪里调用的，它在RecyclerView中唯一被调用的地方就是 getItemDecorInsetsForChild(View child) 函数。  
```java  
Rect getItemDecorInsetsForChild(View child) {  
    final LayoutParams lp = (LayoutParams) child.getLayoutParams();  
    if (!lp.mInsetsDirty) {  
        return lp.mDecorInsets;  
    }  
    if (mState.isPreLayout() && (lp.isItemChanged() || lp.isViewInvalid())) {  
        // changed/invalid items should not be updated until they are rebound.  
        return lp.mDecorInsets;  
    }  
    final Rect insets = lp.mDecorInsets;  
    insets.set(0, 0, 0, 0);  
    final int decorCount = mItemDecorations.size();  
    for (int i = 0; i < decorCount; i++) {  
        mTempRect.set(0, 0, 0, 0);  
        mItemDecorations.get(i).getItemOffsets(mTempRect, child, this, mState);  
        insets.left += mTempRect.left;  
        insets.top += mTempRect.top;  
        insets.right += mTempRect.right;  
        insets.bottom += mTempRect.bottom;  
    }  
    lp.mInsetsDirty = false;  
    return insets;  
}  
```  
可以看到，getItemDecorInsetsForChild 函数中会重置 insets 的值，并重新计算，计算方式就是把所有 ItemDecoration 的 getItemOffsets 中设置的值累加起来，而这个 insets 实际上是 RecyclerView 的 child 的 LayoutParams 中的一个属性，它会在 getTopDecorationHeight, getBottomDecorationHeight 等函数中被返回，那么这个 insets 的意义就很明显了，它记录的是所有 ItemDecoration 所需要的尺寸的总和。  
  
而在 RecyclerView 的 measureChild函数中，调用了 getItemDecorInsetsForChild，并把它算在了 child view 的 padding 中。  
```java  
public void measureChild(View child, int widthUsed, int heightUsed) {  
    final LayoutParams lp = (LayoutParams) child.getLayoutParams();  
    final Rect insets = mRecyclerView.getItemDecorInsetsForChild(child);  
    widthUsed += insets.left + insets.right;  
    heightUsed += insets.top + insets.bottom;  
    final int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(),  
            getPaddingLeft() + getPaddingRight() + widthUsed, lp.width, canScrollHorizontally());  
    final int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(),  
            getPaddingTop() + getPaddingBottom() + heightUsed, lp.height, canScrollVertically());  
    if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {  
        child.measure(widthSpec, heightSpec);  
    }  
}  
```  
上面这段代码中调用的 getChildMeasureSpec 函数的第三个参数就是 child view 的 padding，而这个参数就把 insets 的值算进去了。  
  
那么现在就可以确认了：getItemOffsets 中为 outRect 设置的4个方向的值，将被计算进所有 decoration 的尺寸中，而这个尺寸，被计入了 RecyclerView 每个 item view 的 padding 中。  
  
### onDraw 方法解析  
DividerItemDecoration 中 onDraw 方法是这样实现的：  
```java  
@Override  
public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {  
    if (parent.getLayoutManager() == null) return;  
    if (mOrientation == VERTICAL) drawVertical(c, parent);  
    else drawHorizontal(c, parent);  
}  
```  
  
drawVertical 是为纵向的 RecyclerView 绘制 divider，我们看一下 drawVertical 的具体实现：  
```java  
public void drawVertical(Canvas c, RecyclerView parent) {  
    final int left = parent.getPaddingLeft();  
    final int right = parent.getWidth() - parent.getPaddingRight();  
    final int childCount = parent.getChildCount();  
    for (int i = 0; i < childCount; i++) {  
        final View child = parent.getChildAt(i);  
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();  
        final int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));  
        final int bottom = top + mDivider.getIntrinsicHeight();  
        mDivider.setBounds(left, top, right, bottom);  
        mDivider.draw(c);  
    }  
}  
```  
  
简单说就是：遍历每个 child view ，把 divider 绘制到 canvas 上，而 mDivider.setBounds 则设置了 divider 的绘制范围。  
其中：  
- left 设置为 parent.getPaddingLeft()，也就是左边是 parent 也就是 RecyclerView 的左边界加上 paddingLeft 之后的位置  
- right 设置为了 RecyclerView 的右边界减去 paddingRight 之后的位置，那这里左右边界就是 RecyclerView 的内容区域了  
- top 设置为了 child 的 bottom 加上 marginBottom 再加上 translationY，这其实就是 child view 的下边界  
- bottom 就是简单地 top 加上 divider 的高度  
  
在 onDraw 为 divider 设置绘制范围，并绘制到 canvas 上，而这个绘制范围可以超出在 getItemOffsets 中设置的范围，但由于 decoration 是绘制在 child view 的底下，所以并不可见，但是会存在 overdraw。  
  
有一点需要注意：decoration 的 onDraw，child view 的 onDraw，decoration 的 onDrawOver，这三者是依次发生的。而由于 onDrawOver 是绘制在最上层的，所以它的绘制位置并不受限制（当然，decoration 的 onDraw 绘制范围也不受限制，只不过不可见），所以利用 onDrawOver 可以做很多事情，例如为 RecyclerView 整体顶部绘制一个蒙层，或者为特定的 item view 绘制蒙层。一般我们选择复写其中一个即可。  
  
### 总结  
- getItemOffsets 中为 outRect 设置的4个方向的值，将被计算进所有 decoration 的尺寸中，而这个尺寸，被计入了 RecyclerView 每个 item view 的 padding 中  
- decoration 的 onDraw，child view 的 onDraw，decoration 的 onDrawOver，这三者是依次发生的  
- 在 onDraw 为 divider 设置绘制范围，并绘制到 canvas 上。虽然这个绘制范围可以超出在 getItemOffsets 中设置的范围，但由于onDraw 方法是在drawChildren之前调用的， 所以 decoration 是绘制在 Children 的底下，所以超出在 getItemOffsets 中设置的范围的部分并不可见（被Children覆盖了），这就造成了 overdraw(过度绘制)的问题，影响性能。  
- onDrawOver 是在drawChildren之后绘制的，是绘制在最上层的，所以不会被Children覆盖，所以它的绘制位置并不受限制  
  
2018-5-18  
