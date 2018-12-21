| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
RV 多样式 MultiType 聊天界面 消息类型 MD  
[Demo](https://github.com/baiqiantao/MultiTypeTest.git)  
***  
目录  
===  

- [纯原生实现多样式](#纯原生实现多样式)
	- [addHeaderView 方式](#addheaderview-方式)
		- [Activity](#activity)
		- [Adapter](#adapter)
		- [Model](#model)
	- [getItemViewType 方式](#getitemviewtype-方式)
		- [Activity](#activity)
		- [Adapter](#adapter)
		- [Model](#model)
- [MultiType 简介](#multitype-简介)
	- [特性](#特性)
	- [基础用法](#基础用法)
	- [设计思想](#设计思想)
	- [使用插件自动生成代码](#使用插件自动生成代码)
	- [一个类型对应多个 ItemViewBinder](#一个类型对应多个-itemviewbinder)
	- [添加HeaderView、FooterView](#添加headerview、footerview)
	- [使用断言以方便调试](#使用断言以方便调试)
	- [聊天界面模板代码](#聊天界面模板代码)
		- [聊天界面 ChatActivity](#聊天界面-chatactivity)
		- [消息模型的父类 ContentModel](#消息模型的父类-contentmodel)
		- [VH的父类 ContentHolder](#vh的父类-contentholder)
		- [IVB的父类 ChatFrameBinder](#ivb的父类-chatframebinder)
		- [消息模型的子类 SimpleImage](#消息模型的子类-simpleimage)
		- [SimpleImageViewBinder](#simpleimageviewbinder)
		- [消息模型的特殊子类 BigImage](#消息模型的特殊子类-bigimage)
		- [BigImageViewBinder](#bigimageviewbinder)
		- [左框架布局 item_frame_left](#左框架布局-item_frame_left)
		- [左图片 item_simple_image_left](#左图片-item_simple_image_left)
  
# 纯原生实现多样式  
## addHeaderView 方式  
### Activity   
```java  
public class Activity1 extends Activity {  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        ListView listView = new ListView(this);  
  
        List<Model1> mList = new ArrayList<>();  
        for (int i = 0; i < 100; i++) {  
            mList.add(new Model1(("包青天" + i), R.drawable.icon));  
        }  
  
        //给ListView添加头尾  
        TextView mTextView = new TextView(this);  
        mTextView.setText("我是头部\n必须在listview.setAdapter前添加");  
        mTextView.setBackgroundColor(Color.YELLOW);  
        listView.addHeaderView(mTextView);//必须在listview.setAdapter前添加。添加以后，listView的position=0的View是此View  
  
        ImageView mImageView = new ImageView(this);  
        mImageView.setImageResource(R.drawable.icon);  
        mImageView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 300));  
        listView.addHeaderView(mImageView);  
        listView.setHeaderDividersEnabled(new Random().nextBoolean());//控制头部是否显示分割线。默认为true  
  
        View footerView = new View(this);  
        footerView.setBackgroundColor(Color.GREEN);  
        footerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 50));  
        listView.addFooterView(footerView);  
        listView.setFooterDividersEnabled(new Random().nextBoolean());  
  
        listView.setAdapter(new MyAdapter1(this, mList));//addHeaderView要放在setAdapter之前，而addFooterView放在前后都可以  
        listView.setDivider(new ColorDrawable(Color.RED));  
        listView.setDividerHeight(2);//如果调用了setDivider，也需调用setDividerHeight才行  
        listView.setOnItemClickListener((parent, view, p, id) -> Toast.makeText(this, "p=" + p, Toast.LENGTH_SHORT).show());  
  
        setContentView(listView);  
    }  
}  
```  
  
### Adapter  
```java  
public class MyAdapter1 extends BaseAdapter {  
    private Context mContext;  
    private List<Model1> mList;  
  
    public MyAdapter1(Context context, List<Model1> list) {  
        this.mContext = context;  
        this.mList = list;  
    }  
  
    @Override  
    public int getCount() {  
        return mList.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return mList.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder mViewHolder;  
        if (convertView != null) {  
            mViewHolder = (ViewHolder) convertView.getTag();  
        } else {  
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);  
            mViewHolder = new ViewHolder();  
            mViewHolder.iv_head = (ImageView) convertView.findViewById(R.id.holder2_iv);  
            mViewHolder.tv_name = (TextView) convertView.findViewById(R.id.holder2_title);  
            convertView.setTag(mViewHolder);  
        }  
        Model1 mBean = mList.get(position);  
        mViewHolder.iv_head.setImageResource(mBean.resId);  
        mViewHolder.tv_name.setText(mBean.name + "  position=" + position);  
        return convertView;  
    }  
  
    public static class ViewHolder {  
        public ImageView iv_head;// 头像  
        public TextView tv_name;// 名字  
    }  
}  
```  
  
### Model  
```java  
public class Model1 {  
    public String name;  
    public int resId;  
  
    public Model1(String name, int resId) {  
        this.name = name;  
        this.resId = resId;  
    }  
}  
```  
  
## getItemViewType 方式  
### Activity   
```java  
public class Activity2 extends Activity {  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        ListView listView = new ListView(this);  
        List<Model2> mList = new ArrayList<>();  
        for (int i = 0; i < 100; i++) {  
            if (new Random().nextBoolean()) mList.add(new Model2(Model2.ITEM_FIRST, "第一种样式 " + i));  
            else mList.add(new Model2(Model2.ITEM_SECOND, "第二种样式 " + i, R.drawable.icon));  
        }  
  
        listView.setAdapter(new MyAdapter2(this, mList));  
        listView.setOnItemClickListener((parent, view, p, id) -> Toast.makeText(this, "p=" + p, Toast.LENGTH_SHORT).show());  
        setContentView(listView);  
    }  
}  
```  
  
### Adapter  
```java  
public class MyAdapter2 extends BaseAdapter {  
      
    private Context context;  
    private List<Model2> mList;  
      
    public MyAdapter2(Context context, List<Model2> list) {  
        this.context = context;  
        mList = list;  
    }  
      
    //**************************************************************************************************************************  
    @Override  
    public int getCount() {  
        return mList.size();  
    }  
      
    @Override  
    public Object getItem(int position) {  
        return mList.get(position);  
    }  
      
    //重写方法一：返回值代表的是某一个样式的 Type（是一个需要我们自己定义的，用于区分不同样式的int类型的值）  
    @Override  
    public int getItemViewType(int position) {  
        return mList.get(position).type;  
    }  
      
    //重写方法一：返回的是你有几种类型的样式  
    @Override  
    public int getViewTypeCount() {  
        return 2;  
    }  
      
    @Override  
    public long getItemId(int paramInt) {  
        return paramInt;  
    }  
      
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        int type = getItemViewType(position);  
        //要使用不同类型的ViewHolder  
        Holder1 holder1 = null;  
        Holder2 holder2 = null;  
  
        //************************************************初始化和复用******************************************  
        if (convertView != null) {  
            switch (type) {  
                case Model2.ITEM_FIRST:  
                    holder1 = (Holder1) convertView.getTag();  
                    Log.i("bqt", position + "  复用  " + type);  
                    break;  
                case Model2.ITEM_SECOND:  
                    holder2 = (Holder2) convertView.getTag();  
                    Log.i("bqt", position + "  复用  " + type);  
                    break;  
            }  
        } else {  
            switch (type) {  
                case Model2.ITEM_FIRST:  
                    convertView = View.inflate(context, R.layout.head, null);  
                    holder1 = new Holder1();  
                    holder1.holder1_title = (TextView) convertView.findViewById(R.id.holder1_title);  
                    holder1.holder1_time = (TextView) convertView.findViewById(R.id.holder1_time);  
                    convertView.setTag(holder1);  
                    Log.i("bqt", position + "  初始化  " + type);  
                    break;  
                case Model2.ITEM_SECOND:  
                    convertView = View.inflate(context, R.layout.item, null);  
                    holder2 = new Holder2();  
                    holder2.holder2_title = (TextView) convertView.findViewById(R.id.holder2_title);  
                    holder2.holder2_iv = (ImageView) convertView.findViewById(R.id.holder2_iv);  
                    convertView.setTag(holder2);  
                    Log.i("bqt", position + "  初始化  " + type);  
                    break;  
            }  
        }  
  
        //*************************************************填充数据*****************************************  
        switch (type) {  
            case Model2.ITEM_FIRST:  
                if (holder1 != null) {  
                    holder1.holder1_title.setText(mList.get(position).title);  
                    holder1.holder1_time.setText(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date()));  
                }  
                break;  
            case Model2.ITEM_SECOND:  
                if (holder2 != null) {  
                    holder2.holder2_title.setText(mList.get(position).title);  
                    holder2.holder2_iv.setImageResource(mList.get(position).resId);  
                }  
                break;  
        }  
        return convertView;  
    }  
      
    //**************************************************************************************************************************  
    private class Holder1 {  
        TextView holder1_title;  
        TextView holder1_time;  
    }  
      
    private class Holder2 {  
        TextView holder2_title;  
        ImageView holder2_iv;  
    }  
}  
```  
  
### Model  
```java  
public class Model2 {  
    public static final int ITEM_FIRST = 0;//第一个样式  
    public static final int ITEM_SECOND = 1;//第二个样式  
    public int type;//记录是哪种样式  
    public String title;//标题  
    public int resId;//图片，仅第二个样式可以获取图片  
  
    public Model2(int type, String str) {  
        this.type = type;  
        title = str;  
    }  
  
    public Model2(int type, String str, int resId) {  
        this.type = type;  
        title = str;  
        this.resId = resId;  
    }  
}  
```  
  
# MultiType 简介  
[GitHub](https://github.com/drakeet/MultiType)  
  
```  
compile 'me.drakeet.multitype:multitype:3.1.0'  
```  
  
在开发我的 TimeMachine 时，我有一个复杂的聊天页面，于是我设计了我的类型池系统，它是完全解耦的，因此我能够轻松将它抽离出来分享，并给它取名为 MultiType.  
  
从前，比如我们写一个类似微博列表页面，这样的列表是十分复杂的：有纯文本的、带转发原文的、带图片的、带视频的、带文章的等等，甚至穿插一条可以横向滑动的好友推荐条目。不同的 item 类型众多，而且随着业务发展，还会更多。如果我们使用传统的开发方式，经常要做一些繁琐的工作，代码可能都堆积在一个 Adapter 中：我们需要覆写 RecyclerView.Adapter 的 getItemViewType 方法，罗列一些 type 整型常量，并且 ViewHolder 转型、绑定数据也比较麻烦。一旦产品需求有变，或者产品设计说需要增加一种新的 item 类型，我们需要去代码堆里找到原来的逻辑去修改，或找到正确的位置去增加代码。这些过程都比较繁琐，侵入较强，需要小心翼翼，以免改错影响到其他地方。  
  
现在好了，我们有了 MultiType，简单来说，MultiType 就是一个多类型列表视图的中间分发框架，它能帮助你快速并且清晰地开发一些复杂的列表页面。 它本是为聊天页面开发的，聊天页面的消息类型也是有大量不同种类，且新增频繁，而 MultiType 能够轻松胜任。  
  
MultiType 以灵活直观为第一宗旨进行设计，它内建了 类型 - View 的复用池系统，支持 RecyclerView，随时可拓展新的类型进入列表当中，使用简单，令代码清晰、模块化、灵活可变。  
  
因此，我写了这篇文章，目的有几个：一是以作者的角度对 MultiType 进行入门和进阶详解。二是传递我开发过程中的思想、设计理念，这些偏细腻的内容，即使不使用 MultiType，想必也能带来很多启发。最后就是把自我觉得不错的东西分享给大家，试想如果你制造的东西很多人在用，即使没有带来任何收益，也是一件很自豪的事情。  
  
## 特性  
- 轻盈，整个类库只有 14 个类文件，aar 或 jar 包大小只有 13 KB  
- 周到，支持 data type <--> item view binder 之间 一对一 和 一对多 的关系绑定  
- 灵活，几乎所有的部件(类)都可被替换、可继承定制，面向接口 / 抽象编程  
- 纯粹，只负责本分工作，专注多类型的列表视图 类型分发，绝不会去影响 views 的内容或行为  
- 高效，没有性能损失，内存友好，最大限度发挥 RecyclerView 的复用性  
- 可读，代码清晰干净、设计精巧，极力避免复杂化，可读性很好，为拓展和自行解决问题提供了基础  
  
## 基础用法  
```java  
compile 'me.drakeet.multitype:multitype:3.1.0'  
```  
注：MultiType 内部引用了 recyclerview-v7:25.3.1，如果你不想使用这个版本，可以使用 exclude 将它排除掉，再自行引入你选择的版本。示例如下：  
```java  
dependencies {  
    compile('me.drakeet.multitype:multitype:3.1.0', {  
       exclude group: 'com.android.support'  
    })  
    compile 'com.android.support:recyclerview-v7:你选择的版本'  
}  
```  
  
1、创建一个类，它将是你的数据类型或 Java bean / model. 对这个类的内容没有任何限制（建议这些model统一继承于一个父类）  
```java  
public class SimpleImage extends ContentModel {  
    public int resId;  
​  
    public SimpleImage(int resId) {  
        super(ContentModel.TYPE_SIMPLE_IMAGE);  
        this.resId = resId;  
    }  
}  
```  
  
2、创建一个类 继承 ItemViewBinder  
ItemViewBinder 是个抽象类，其中 onCreateViewHolder 方法用于生产你的 Item View Holder，onBindViewHolder 用于绑定数据到 Views。  
一般一个 ItemViewBinder 类在内存中只会有一个实例对象，MultiType 内部将复用这个 binder 对象来生产所有相关的 item views 和绑定数据。  
```java  
public class SimpleImageViewBinder extends ItemViewBinder<SimpleImage, SimpleImageViewBinder.ViewHolder> {  
​  
    @NonNull  
    @Override  
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {  
        View contentView = inflater.inflate(R.layout.item_weibo_simple_image, parent, false);  
        return new ViewHolder(contentView);  
    }  
​  
    @Override  
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull SimpleImage item) {  
        holder.simpleImage.setImageResource(item.resId);  
    }  
​  
    static class ViewHolder extends RecyclerView.ViewHolder {  
        private ImageView simpleImage;  
        ViewHolder(View itemView) {  
            super(itemView);  
            simpleImage = (ImageView) itemView.findViewById(R.id.simple_image);  
        }  
    }  
}  
```  
  
3、为RecyclerView指定所使用的MultiTypeAdapter，注册你的类型，绑定数据。完毕。  
```java  
MultiTypeAdapter adapter = new MultiTypeAdapter();  
adapter.register(SimpleImage.class, new SimpleImageViewBinder());  
adapter.register(SimpleText.class, new SimpleTextViewBinder());  
recyclerView.setAdapter(adapter);  
​  
List<Simple_Content> items = new ArrayList<>();  
adapter.setItems(items);  
```  
  
## 设计思想  
MultiType 设计伊始，我给它定了几个原则：  
  
1、要简单，便于他人阅读代码  
因此我极力避免将它复杂化，避免加入许多不相干的内容。我想写人人可读的代码，使用简单的方式，去实现复杂的需求。过多不相干、没必要的代码，将会使项目变得令人晕头转向，难以阅读，遇到需要定制、解决问题的时候，无从下手。  
  
2、要灵活，便于拓展和适应各种需求  
很多人会得意地告诉我，他们把 MultiType 源码精简成三四个类，甚至一个类，以为代码越少就是越好，这我不能赞同。MultiType 考虑得更远，这是一个提供给大众使用的类库，过度的精简只会使得大幅失去灵活性。它或许不是使用起来最简单的，但很可能是使用起来最灵活的。 在我看来，"直观"、"灵活"优先级大于"简单"。因此，MultiType 以接口或抽象进行连接，这意味着它的角色、组件都可以被替换，或者被拓展和继承。如果你觉得它使用起来还不够简单，完全可以通过继承封装出更具体符合你使用需求的方法。它已经暴露了足够丰富、周到的接口以供拓展，我们不应该直接去修改源码，这会导致一旦后续发现你的精简版满足不了你的需求时，已经没有回头路了。  
  
3、要直观，使用起来能令项目代码更清晰可读，一目了然  
MultiType 提供的 ItemViewBinder 沿袭了 RecyclerView Adapter 的接口命名，使用起来更加舒适，符合习惯。另外，MultiType 很多地方放弃使用反射而是让用户显式指明一些关系，如：MultiTypeAdapter#register 方法，需要传递一个数据模型 class 和 ItemViewBinder 对象，虽然有很多方法可以把它精简成单一参数方法，但我们认为显式声明数据模型类与对应关系，更具直观。  
  
## 使用插件自动生成代码  
MultiType 提供了 Android Studio 插件 [MultiTypeTemplates](https://github.com/drakeet/MultiTypeTemplates) 来自动生成代码，源码也是开源的 。  
  
这个插件不仅提供了一键生成 item 类文件和 ItemViewBinder，而且是一个很好的利用代码模版自动生成代码的示例。其中使用到了官方提供的代码模版 API，也用到了我自己发明的更灵活修改模版内容的方法，有兴趣做这方面插件的可以看看。  
  
使用方式：右键点击你的 package，选择 New -> MultiType Item，然后输入你的 item 名字，它就会自动生成 item 模型类 和 ItemViewBinder 文件和代码。特别方便，相信你会很喜欢它。未来这个插件也将会支持自动生成布局文件，这是目前欠缺的，但不要紧，其实 AS 在这方面已经很方便了，对布局 R.layout.item_category 使用 alt + enter 快捷键即可自动生成布局文件。  
  
## 一个类型对应多个 ItemViewBinder  
MultiType 天然支持一个类型对应多个 ItemViewBinder，注册方式也很简单，如下：  
```java  
adapter.register(Data.class).to(  
    new DataType1ViewBinder(),  
    new DataType2ViewBinder()  
).withClassLinker(new ClassLinker<Data>() {  
    @NonNull @Override  
    public Class<? extends ItemViewBinder<Data, ?>> index(@NonNull Data data) {  
        if (data.type == Data.TYPE_2) return DataType2ViewBinder.class;  
        else return DataType1ViewBinder.class;  
    }  
});  
```  
或者：  
```java  
adapter.register(Data.class).to(  
    new DataType1ViewBinder(),  
    new DataType2ViewBinder()  
).withLinker(new Linker<Data>() {  
    @Override  
    public int index(@NonNull Data data) {  
        if (data.type == Data.TYPE_2) return 1;  
        else return 0;  
    }  
});  
```  
如上示例代码，对于一对多，我们需要使用 MultiType#register(class) 方法，它会返回一个 OneToManyFlow 让你紧接着绑定多个 ItemViewBinder 实例，最后再调用 OneToManyEndpoint#withLinker 或 OneToManyEndpoint#withClassLinker 操作符方法类设置 linker. 所谓 linker，是负责动态连接这个 "一" 对应 "多" 中哪一个 binder 的角色。  
  
这个方案具有很好的性能表现，而且可谓十分直观。另外，我使用了 @CheckResult 注解来让编译器督促开发者一定要完整调用方法链才不至于出错。  
  
## 添加HeaderView、FooterView  
MultiType 其实本身就支持 HeaderView、FooterView，只要创建一个 Header.class - HeaderViewBinder 和 Footer.class - FooterViewBinder 即可，然后把 new Header() 添加到 items 第一个位置，把 new Footer() 添加到 items 最后一个位置。  
需要注意的是，如果使用了 Footer View，在底部插入数据的时候，需要添加到 最后位置 - 1，即倒二个位置，或者把 Footer remove 掉，再添加数据，最后再插入一个新的 Footer.  
PS：听他这么说，这哪叫"支持"啊，HeaderView、FooterView完全就是item中的【普通一员】了。不过话又说回来，它本来不就是吗？  
  
## 使用断言以方便调试  
众所周知，如果一个传统的 RecyclerView Adapter 内部有异常导致崩溃，它的异常栈是不会指向到你的 Activity，这给我们开发调试过程中带来了麻烦。如果我们的 Adapter 是复用的，就不知道是哪一个页面崩溃。而对于 MultiTypeAdapter，我们显然要用于多个地方，而且可能出现开发者忘记注册类型等等问题。为了便于调试，开发期快速定位失败，MultiType 提供了很方便的断言 API: MultiTypeAsserts，使用方式如下：  
assertHasTheSameAdapter(recyclerView, adapter);//断言 recyclerView 使用的是正确的 adapter，必须在setAdapter(adapter) 之后调用  
assertAllRegistered(adapter, items);//断言所有使用的类型都已注册，需要在加载或更新数据之后调用  
这两个API都是可选择性使用的。  
这样做以后，MultiTypeAdapter 相关的异常都会报到你的 Activity，并且会详细注明出错的原因，而如果符合断言，断言代码不会有任何副作用或影响你的代码逻辑，这时你可以把它当作废话。关于这个类的源代码是很简单的，有兴趣可以直接看看源码：MultiTypeAsserts.java。  
  
## 聊天界面模板代码  
### 聊天界面 ChatActivity   
```java  
public class ChatActivity extends Activity {  
    private static final String TEXT = "不懂左右逢源，不喜趋炎附势，不会随波逐流，不狡辩，不恭维，不把妹";  
    private static final String PATH1 = "http://img.mmjpg.com/2015/74/33.jpg";  
    private static final String PATH2 = "http://img.mmjpg.com/2015/74/35.jpg";  
​  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        RecyclerView recyclerView = new RecyclerView(this);  
        MultiTypeAdapter adapter = new MultiTypeAdapter();  
​  
        //一对多，都有相同的父框架结构（头像、昵称、时间……等）  
        adapter.register(ContentModel.class).to(  
                new SimpleTextViewBinder(ContentModel.SEND_TYPE_OTHERS),//左边的布局（别人发的消息）  
                new SimpleTextViewBinder(ContentModel.SEND_TYPE_YOURSELF),//右边的布局（自己发的消息）  
                new SimpleImageViewBinder(ContentModel.SEND_TYPE_OTHERS),  
                new SimpleImageViewBinder(ContentModel.SEND_TYPE_YOURSELF),  
                new SimpleVoiceViewBinder(ContentModel.SEND_TYPE_OTHERS),  
                new SimpleVoiceViewBinder(ContentModel.SEND_TYPE_YOURSELF)  
        ).withLinker(model -> {  
            if (model.msgType == ContentModel.MSG_TYPE_SIMPLE_TEXT  
                    && model.sendType == ContentModel.SEND_TYPE_OTHERS) return 0;//左边的布局（别人发的消息）  
            else if (model.msgType == ContentModel.MSG_TYPE_SIMPLE_TEXT  
                    && model.sendType == ContentModel.SEND_TYPE_YOURSELF) return 1;//右边的布局（自己发的消息）  
            else if (model.msgType == ContentModel.MSG_TYPE_SIMPLE_IMAGE  
                    && model.sendType == ContentModel.SEND_TYPE_OTHERS) return 2;  
            else if (model.msgType == ContentModel.MSG_TYPE_SIMPLE_IMAGE  
                    && model.sendType == ContentModel.SEND_TYPE_YOURSELF) return 3;  
            else if (model.msgType == ContentModel.MSG_TYPE_SIMPLE_VOICE  
                    && model.sendType == ContentModel.SEND_TYPE_OTHERS) return 4;  
            else if (model.msgType == ContentModel.MSG_TYPE_SIMPLE_VOICE  
                    && model.sendType == ContentModel.SEND_TYPE_YOURSELF) return 5;  
            return 0;  
        });  
        //一个独立的结构，没有父框架结构  
        adapter.register(BigImage.class, new BigImageViewBinder());  
​  
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//new GridLayoutManager(this,2)  
        recyclerView.setAdapter(adapter);  
        assertHasTheSameAdapter(recyclerView, adapter);//断言 recyclerView 使用的是正确的 adapter，可选择性使用  
​  
        User other = new User("other", PATH1);  
        User yourself = new User("包青天", PATH2);  
        List<ContentModel> items = new ArrayList<>();  
        for (int i = 0; i < 50; i++) {  
            int sendType = new Random().nextBoolean() ? ContentModel.SEND_TYPE_OTHERS : ContentModel.SEND_TYPE_YOURSELF;  
            User user = sendType == ContentModel.SEND_TYPE_OTHERS ? other : yourself;  
            String path = sendType == ContentModel.SEND_TYPE_OTHERS ? PATH1 : PATH2;  
​  
            int random = new Random().nextInt(4);  
            if (random == 0) items.add(new SimpleText(user, sendType, i + "、" + TEXT));  
            else if (random == 1) items.add(new SimpleImage(user, sendType, path));  
            else items.add(new SimpleVoice(user, sendType, path, new Random().nextInt(60)));  
        }  
        items.add(new BigImage(other, ContentModel.SEND_TYPE_OTHERS, PATH1));  
        items.add(new BigImage(yourself, ContentModel.SEND_TYPE_YOURSELF, PATH2));  
​  
        adapter.setItems(items);  
        adapter.notifyDataSetChanged();  
        assertAllRegistered(adapter, items);//断言所有使用的类型都已注册，需要在加载或更新数据之后调用，可选择性使用  
​  
        setContentView(recyclerView);  
    }  
}  
```  
  
### 消息模型的父类 ContentModel   
```java  
/**  
 * 各种消息类型的基类  
 */  
public abstract class ContentModel {  
    //消息类型  
    public static final int MSG_TYPE_SIMPLE_TEXT = 0;  
    public static final int MSG_TYPE_SIMPLE_IMAGE = 1;  
    public static final int MSG_TYPE_SIMPLE_VOICE = 2;  
    public static final int MSG_TYPE_BIG_IMAGE = 3;  
​  
    //消息是谁发的  
    public static final int SEND_TYPE_OTHERS = 0;  
    public static final int SEND_TYPE_YOURSELF = 1;  
    public static final int SEND_TYPE_YSTEM = 2;  
​  
    public int msgType;  
    public int sendType;  
    public String createTime;  
    /**  
     * 所有信息都可以封装到user中  
     */  
    public User user;  
​  
    protected ContentModel(User user, int msgType, int sendType) {  
        this.user = user;  
        this.msgType = msgType;  
        this.sendType = sendType;  
        this.createTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS E", Locale.getDefault()).format(new Date());  
    }  
}  
```  
  
### VH的父类 ContentHolder   
```java  
public class ContentHolder {  
​  
    public ChatFrameBinder.FrameHolder frameHolder;  
​  
    public final View itemView;  
​  
    public ContentHolder(final View itemView) {  
        this.itemView = itemView;  
    }  
​  
    public ChatFrameBinder.FrameHolder getParent() {  
        return frameHolder;  
    }  
​  
    public final int getAdapterPosition() {  
        return getParent().getAdapterPosition();  
    }  
​  
    public final int getLayoutPosition() {  
        return getParent().getLayoutPosition();  
    }  
​  
    public final int getOldPosition() {  
        return getParent().getOldPosition();  
    }  
​  
    public final boolean isRecyclable() {  
        return getParent().isRecyclable();  
    }  
​  
    public final void setIsRecyclable(boolean recyclable) {  
        getParent().setIsRecyclable(recyclable);  
    }  
}  
```  
  
### IVB的父类 ChatFrameBinder  
```java  
/**  
 * 此种方式非常适合聊天页面。  
 * 对于聊天页面，left和right的元素基本是完全相同的，唯一（会最大）的不同就是元素放置的位置不同  
 */  
public abstract class ChatFrameBinder<T extends ContentModel, H extends ContentHolder>  
        extends ItemViewBinder<ContentModel, ChatFrameBinder.FrameHolder> {  
    protected int sendType;  
​  
    public ChatFrameBinder(int sendType) {  
        super();  
        this.sendType = sendType;  
    }  
​  
    protected abstract ContentHolder onCreateContentViewHolder(LayoutInflater inflater, ViewGroup parent);  
​  
    protected abstract void onBindContentViewHolder(H holder, T content);  
​  
    @NonNull  
    @Override  
    protected FrameHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {  
        View root;  
        if (sendType == ContentModel.SEND_TYPE_OTHERS) root = inflater.inflate(R.layout.item_frame_left, parent, false);  
        else root = inflater.inflate(R.layout.item_frame_right, parent, false);  
        ContentHolder subViewHolder = onCreateContentViewHolder(inflater, parent);  
        return new FrameHolder(root, subViewHolder);  
    }  
​  
    @Override  
    @SuppressWarnings("unchecked")  
    protected void onBindViewHolder(@NonNull FrameHolder holder, @NonNull ContentModel model) {  
        Glide.with(holder.avatar.getContext()).load(model.user.avatar).into(holder.avatar);  
        holder.username.setText(model.user.name);  
        holder.createTime.setText(model.createTime);  
        onBindContentViewHolder((H) holder.subViewHolder, (T) model);  
    }  
​  
    public static class FrameHolder extends RecyclerView.ViewHolder {  
​  
        private ImageView avatar;  
        private TextView username;  
        private FrameLayout container;  
        private TextView createTime;  
        private ContentHolder subViewHolder;  
​  
        FrameHolder(View itemView, final ContentHolder subViewHolder) {  
            super(itemView);  
            avatar = (ImageView) itemView.findViewById(R.id.avatar);  
            username = (TextView) itemView.findViewById(R.id.username);  
            container = (FrameLayout) itemView.findViewById(R.id.container);  
            createTime = (TextView) itemView.findViewById(R.id.create_time);  
​  
            container.addView(subViewHolder.itemView);  
            this.subViewHolder = subViewHolder;  
            this.subViewHolder.frameHolder = this;  
​  
            itemView.setOnClickListener(v -> Toast.makeText(v.getContext(), "Position=" + getAdapterPosition(), LENGTH_SHORT).show());  
        }  
    }  
}  
```  
  
### 消息模型的子类 SimpleImage   
```java  
public class SimpleImage extends ContentModel {  
​  
    public String imagePath;  
​  
    public SimpleImage(User user, int sendType, String imagePath) {  
        super(user, ContentModel.MSG_TYPE_SIMPLE_IMAGE, sendType);  
        this.imagePath = imagePath;  
    }  
}  
```  
  
### SimpleImageViewBinder   
```java  
public class SimpleImageViewBinder extends ChatFrameBinder<SimpleImage, SimpleImageViewBinder.ViewHolder> {  
​  
    public SimpleImageViewBinder(int sendType) {  
        super(sendType);  
    }  
​  
    @Override  
    protected ContentHolder onCreateContentViewHolder(LayoutInflater inflater, ViewGroup parent) {  
        View root;  
        if (sendType == ContentModel.SEND_TYPE_OTHERS) root = inflater.inflate(R.layout.item_simple_image_left, parent, false);  
        else root = inflater.inflate(R.layout.item_simple_image_right, parent, false);  
        return new SimpleImageViewBinder.ViewHolder(root);  
    }  
​  
    @Override  
    protected void onBindContentViewHolder(ViewHolder holder, SimpleImage simpleImage) {  
        Glide.with(holder.simpleImage.getContext()).load(simpleImage.imagePath).into(holder.simpleImage);  
    }  
​  
    static class ViewHolder extends ContentHolder {  
​  
        private ImageView simpleImage;  
​  
        ViewHolder(View itemView) {  
            super(itemView);  
            simpleImage = (ImageView) itemView.findViewById(R.id.simple_image);  
        }  
    }  
}  
```  
  
### 消息模型的特殊子类 BigImage   
```java  
public class BigImage extends ContentModel {  
​  
    public String imagePath;  
​  
    public BigImage(User user, int sendType, String imagePath) {  
        super(user, ContentModel.MSG_TYPE_BIG_IMAGE, sendType);  
        this.imagePath = imagePath;  
    }  
}  
```  
  
### BigImageViewBinder   
```java  
public class BigImageViewBinder extends ItemViewBinder<BigImage, BigImageViewBinder.ViewHolder> {  
      
    @NonNull  
    @Override  
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {  
        View root = inflater.inflate(R.layout.item_big_image, parent, false);  
        return new ViewHolder(root);  
    }  
      
    @Override  
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull BigImage bigImage) {  
        Glide.with(holder.iv_pic.getContext()).load(bigImage.imagePath).into(holder.iv_pic);  
        holder.tv_path.setText(bigImage.imagePath);  
    }  
      
    static class ViewHolder extends RecyclerView.ViewHolder {  
        private ImageView iv_pic;  
        private TextView tv_path;  
​  
        ViewHolder(View itemView) {  
            super(itemView);  
            iv_pic = (ImageView) itemView.findViewById(R.id.iv_pic);  
            tv_path = (TextView) itemView.findViewById(R.id.tv_path);  
        }  
    }  
}  
```  
  
### 左框架布局 item_frame_left  
```java  
<?xml version="1.0" encoding="utf-8"?>  
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"  
                xmlns:tools="http://schemas.android.com/tools"  
                style="@style/Weibo.Frame"  
                tools:ignore="UnusedAttribute, RtlHardcoded, ContentDescription">  
​  
    <ImageView  
        android:id="@+id/avatar"  
        style="@style/Weibo.Avatar"  
        android:layout_marginRight="16dp"  
        android:src="@drawable/icon"/>  
​  
    <TextView  
        android:id="@+id/username"  
        style="@style/Weibo.Username"  
        android:layout_alignTop="@id/avatar"  
        android:layout_toRightOf="@id/avatar"  
        tools:text="drakeet"/>  
​  
    <FrameLayout  
        android:id="@+id/container"  
        style="@style/Weibo.SubView"  
        android:layout_alignLeft="@id/username"  
        android:layout_below="@id/username"  
        tools:background="@android:color/darker_gray"  
        tools:layout_height="72dp"/>  
​  
    <TextView  
        android:id="@+id/create_time"  
        style="@style/Weibo.CreateTime"  
        android:layout_alignLeft="@id/username"  
        android:layout_below="@id/container"  
        tools:text="2017-7-18 11:53:59 星期二"/>  
​  
</RelativeLayout>  
```  
  
### 左图片 item_simple_image_left  
```java  
<?xml version="1.0" encoding="utf-8"?>  
<RelativeLayout  
    xmlns:android="http://schemas.android.com/apk/res/android"  
    xmlns:tools="http://schemas.android.com/tools"  
    android:layout_width="match_parent"  
    android:layout_height="wrap_content">  
​  
​  
    <ImageView  
        android:id="@+id/simple_image"  
        style="@style/WeiboContent.SimpleImage"  
        tools:src="@drawable/icon"/>  
</RelativeLayout>  
```  
2017-7-18  
