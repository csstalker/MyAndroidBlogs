| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [Fragment 知识点总结](#Fragment-知识点总结)
	- [FragmentActivity 简介](#FragmentActivity-简介)
	- [Fragment 和 Activity 的通讯](#Fragment-和-Activity-的通讯)
	- [使用静态 Fragment](#使用静态-Fragment)
	- [FragmentManager简介](#FragmentManager简介)
	- [带 RecyclerView 的 Fragment 模板代码](#带-RecyclerView-的-Fragment-模板代码)
	- [子 Fragment 的 onActivityResult 方法不被调用问题的解决方法](#子-Fragment-的-onActivityResult-方法不被调用问题的解决方法)
	- [通过动态添加子 Fragment 的方式简化复杂的 Activity 或 Fragment](#通过动态添加子-Fragment-的方式简化复杂的-Activity-或-Fragment)
	- [DialogFragment 使用简介](#DialogFragment-使用简介)
	- [如何正确的判断当前的Fragment是否对用户可见](#如何正确的判断当前的Fragment是否对用户可见)
  
# Fragment 知识点总结  
  
## FragmentActivity 简介  
  
Fragment是3.0以后的东西，为了在低版本中使用Fragment就要用到android-support-v4兼容包，而Fragmentactivity就是这个兼容包里面的，它提供了操作Fragment的一些方法，其功能跟3.0及以后的版本的Activity的功能一样。  
  
下面是API中的原话：  
> FragmentActivity is a special activity provided in the Support Library to handle fragments on system versions older than API level 11. If the lowest system version you support is API level 11 or higher, then you can use a regular Activity.  
  
一些区别  
- FragmentActivity继承自Activity，用来解决android3.0之前没有fragment的问题，所以在3.0前使用Fragment时需要导入support包，同时让Activity继承FragmentActivity，这样就能在Activity中使用Fragment了。  
- 在3.0之后你直接继承自Activity就可以使用android.app.Fragment，但目前Google推荐使用support包中的Fragment。  
- 两者获得FragmentManager的方式不同，3.0之前使用FragmentActivity时：getSupportFragmentManager()，3.0以后使用Activity时：getFragmentManager()   
- 一定要保证你的Activity中和你的Fragment中导的包是一致的！  
  
## Fragment 和 Activity 的通讯  
因为Fragment都是依附于Activity的，所以两者通信并不复杂，大概归纳为：  
- 如果你Activity中包含此Fragment的引用，可以通过此引用直接访问Fragment所有的public方法  
- 如果Activity中不保存此Fragment的引用，由于每个Fragment都有唯一的一个TAG或者ID,可以通过findFragmentByTag或者findFragmentById获得任何Fragment实例  
- 在Fragment中可以通过getActivity得到当前绑定的Activity的实例  
  
如果在Fragment中需要Context，可以通过调用getContext()或getActivity()或onAttach中的参数context获取，如果该Context需要在Activity被销毁后还存在，则使用getActivity().getApplicationContext()。  
```java  
@Override  
public void onAttach(Context context) {  
    super.onAttach(context);  
    Log.i("bqt", "【onAttach】" + (getContext() == getActivity()) + "-" + (context == getActivity())); //true-true  
}  
```  
  
## 使用静态 Fragment  
虽然简单，但有两点必须满足：  
- 要通过`android:name`属性指定所引用的fragment的`全类名`  
- 必须给fragment设置`android:id`，即使完全用不到  
  
```xml  
<fragment  
    android:id="@+id/fragment"  
    android:name="com.bqt.fragment.MyFragment"  
    android:layout_width="match_parent"  
    android:layout_height="45dp" />  
```  
  
## FragmentManager简介  
  
FragmentManager 可以做的事情：   
- 使用 findFragmentById 或 findFragmentByTag 获取 activity 中存在的 fragment  
- 使用 popBackStack() 将 fragment 从后台堆栈中弹出 (模拟用户按下BACK 命令)  
- 使用 addOnBackStackChangeListener() 注册一个监听后台堆栈变化的 listener  
- 开启一个事务 fm.benginTransatcion()  
  
> Fragment嵌套Fragment时，里面的Fragment要用`getChildFragmentManager`获取到的FragmentManager  
  
## 带 RecyclerView 的 Fragment 模板代码  
  
```java  
public class MainBeaAppStoreFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {  
    private Context mContext;  
      
    private RecyclerView mRecyclerView;  
    private BeaAppStoreAdapter mAdapter;  
    private ArrayList<BeaAppItemInfo> list;  
    private static int REQUESTCODE_LOGIN_TONEWPAGE = 20094;  
      
    public static MainBeaAppStoreFragment newInstance(ArrayList<BeaAppItemInfo> list) {  
        MainBeaAppStoreFragment officeSimpleFragment = new MainBeaAppStoreFragment();  
        Bundle args = new Bundle();  
        args.putParcelableArrayList("list", list);  
        officeSimpleFragment.setArguments(args);  
          
        return officeSimpleFragment;  
    }  
      
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        mContext = getActivity();  
        list = getArguments() != null ? getArguments().getParcelableArrayList("list") : new ArrayList<>();  
    }  
      
    @Override  
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        super.onCreateView(inflater, container, savedInstanceState);  
        View view = inflater.inflate(R.layout.bea_fragment_main_appstore, container, false);  
        mRecyclerView = view.findViewById(R.id.rv_group);  
        return view;  
    }  
      
    @Override  
    public void onViewCreated(View view, Bundle savedInstanceState) {  
        super.onViewCreated(view, savedInstanceState);  
          
        int spanCount = list.size() >= 4 ? 4 : list.size();  
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));  
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));  
        mRecyclerView.addItemDecoration(new GridItemDecoration.Builder()  
                .spanCount(spanCount)//行数或列数  
                .spaceSize(SystemUtil.dp2px(15f))//行列间距大小  
                .mDivider(new ColorDrawable(Color.TRANSPARENT))  
                .includeLREdge(false).includeTBEdge(false)//是否包含上下左右边界  
                .drawLREdge(false).drawTBEdge(false)//是否绘制上下左右边界  
                .build());  
        mAdapter = new BeaAppStoreAdapter(list);  
        mRecyclerView.setAdapter(mAdapter);  
        mAdapter.setOnItemClickListener(this);  
    }  
      
    @Override  
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {  
    }  
      
    @Override  
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        CherryCodeLogUtil.i("bqt", "【onActivityResult】" + requestCode);  
        //登录成功进行跳转  
        if (requestCode == REQUESTCODE_LOGIN_TONEWPAGE && resultCode == Activity.RESULT_OK) {  
            WebviewActivity.launche(mContext, true, "index.html", toNewPageSuffixUrl);  
        }  
    }  
}  
```  
  
## 子 Fragment 的 onActivityResult 方法不被调用问题的解决方法  
出现此问题的原因：程序bug，没有处理嵌套Fragment的情况，也就是说回调只到第一级Fragment，就没有继续分发。  
我们可以实现一个自己的FragmentActiviy，来实现继续分发，如下：  
```java  
@Override  
protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
   super.onActivityResult(requestCode, resultCode, data);  
   CherryCodeLogUtil.i("bqt", "【onActivityResult】" + requestCode);  
   List<Fragment> fragments = getSupportFragmentManager().getFragments();  
   if (fragments != null && fragments.size() > 0) {  
      for (Fragment fragment : fragments) {  
         handleChildFragmentResult(fragment, requestCode, resultCode, data);  
      }  
   }  
}  
  
/**  
 * 递归调用，对所有的子Fragment生效  
 */  
private void handleChildFragmentResult(Fragment fragment, int requestCode, int resultCode, Intent data) {  
   fragment.onActivityResult(requestCode, resultCode, data);//调用每个Fragment的onActivityResult  
   List<Fragment> childFragments = fragment.getChildFragmentManager().getFragments(); //找到第二层Fragment  
   if (childFragments != null && childFragments.size() > 0) {  
      for (Fragment childFragment : childFragments) {  
         handleChildFragmentResult(childFragment, requestCode, resultCode, data);  
      }  
   }  
}  
```  
  
## 通过动态添加子 Fragment 的方式简化复杂的 Activity 或 Fragment  
根据情况使用【getFragmentManager】【getSupportFragmentManager】【getChildFragmentManager】  
```java  
private void addFragment() {  
   Fragment fragment1 = fragmentManager.findFragmentById(R.id.fl_search);  
   Fragment fragment2 = fragmentManager.findFragmentById(R.id.fl_appstore);  
   Fragment fragment3 = fragmentManager.findFragmentById(R.id.fl_pic);  
     
   FragmentTransaction transaction = fragmentManager.beginTransaction();  
   if (fragment1 == null) transaction.add(R.id.fl_search, SearchFragment.newInstance(name));  
   if (fragment2 == null) transaction.add(R.id.fl_appstore, AppStoreFragment.newInstance());  
   if (fragment3 == null) transaction.add(R.id.fl_pic, PicFragment.newInstance(picList));  
   transaction.commit();  
}  
```  
  
## DialogFragment 使用简介  
  
在 DialogFragment 产生之前，我们创建对话框一般采用 AlertDialog 和 Dialog，DialogFragment 在 android 3.0 时被引入，是一种特殊的**Fragment**，用于在 Activity 的内容之上展示一个模态的对话框。  
  
```java  
public class DialogFragment extends Fragment implements OnCancelListener, OnDismissListener {}  
interface OnCancelListener { void onCancel(DialogInterface dialog); }  
interface OnDismissListener { void onDismiss(DialogInterface dialog); }  
```  
  
传统的 AlertDialog 在屏幕旋转时，既不会保存用户输入的值，对话框也不会重建，还可能会报异常。而使用 DialogFragment 来管理对话框，当旋转屏幕和按下后退键时可以更好的管理其生命周期(自动重建)，它和 Fragment 有着基本一致的生命周期。  
  
使用 DialogFragment 需要实现`onCreateView或者onCreateDIalog`方法，onCreateView即使用自定义的 xml 布局文件展示 Dialog，onCreateDialog 则可利用 AlertDialog 或者 Dialog 创建出 Dialog。  
  
DialogFragment 也允许开发者把 Dialog 作为`内嵌的组件`进行重用，类似 Fragment 可以在大屏幕和小屏幕显示出不同的效果(需要向Fragment那样，在onCreateView中返回Fragment的视图，而不应在onCreateDialog中创建Dialog的视图)。  
  
**DialogFragment 使用案例**  
  
调用方式：  
```java  
MyDialogFragment.newInstance(position).show(fragmentManager, titles[position]);  
fragmentManager.beginTransaction().add(R.id.id_container, MyDialogFragment.newInstance(position), titles[position]).commit();  
```  
  
DialogFragment实现类：  
```java  
public class MyDialogFragment extends DialogFragment {  
    public static final String ARGUMENT = "returnType";  
    private int returnType = 0;  
      
    public static MyDialogFragment newInstance(int returnType) {  
        MyDialogFragment fragment = new MyDialogFragment();  
        Bundle bundle = new Bundle();  
        bundle.putInt(ARGUMENT, returnType);  
        fragment.setArguments(bundle);  
        return fragment;  
    }  
      
    @Override  
    public void onCreate(@Nullable Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Bundle bundle = getArguments();  
        returnType = bundle != null ? bundle.getInt(ARGUMENT) : 0;  
        Log.i("bqt", "【onCreate】" + returnType);  
    }  
      
    @NonNull  
    @Override  
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {  
        Log.i("bqt", "【*********************onCreateDialog*********************】");//在 onCreateView 之前回调  
        if (returnType == 0) {  
            return super.onCreateDialog(savedInstanceState);  
        } else {  
            View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_tab, null);  
            TextView tv = rootView.findViewById(R.id.tv);  
            tv.setText(returnType + "：" + new SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault()).format(new Date()));  
            tv.setBackgroundColor(0xFF000000 + new Random().nextInt(0xFFFFFF));  
              
            if (returnType == 1) {  
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).create();  
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//设置AlertDialog背景透明效果  
                return alertDialog;  
            } else {  
                Dialog dialog = new Dialog(getContext());  
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//设置Dialog没有标题。需在setContentView之前设置，在之后设置会报错  
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//设置Dialog背景透明效果  
                dialog.setContentView(rootView);  
                return dialog;  
            }  
        }  
    }  
      
    @Nullable  
    @Override  
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {  
        Log.i("bqt", "【onCreateView】Dialog是否为null：" + (getDialog() == null));  
        if (returnType == 0) {  
            View rootView = inflater.inflate(R.layout.fragment_tab, container, false);  
            TextView tv = rootView.findViewById(R.id.tv);  
            tv.setText(returnType + "：" + new SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault()).format(new Date()));  
            tv.setBackgroundColor(0xFF000000 + new Random().nextInt(0xFFFFFF));  
            return rootView;  
        } else {  
            return null;  
        }  
    }  
      
    @Override  
    public void onCancel(DialogInterface dialog) {  
        super.onCancel(dialog);  
        Log.i("bqt", "【onCancel】");//在 onDismiss 之前回调  
    }  
      
    @Override  
    public void onDismiss(DialogInterface dialog) {  
        super.onDismiss(dialog);  
        Log.i("bqt", "【*********************onDismiss*********************】"); //在 onPause 之前回调  
    }  
}  
```  
  
## 如何正确的判断当前的Fragment是否对用户可见  
[如何正确的判断当前的Fragment是否对用户可见](https://blog.csdn.net/woshizisezise/article/details/88622357)  
  
- onResume：适用于单一Activity嵌套单一Fragment的场景，跟随载体Activity的生命周期，能得出正确的判断  
- onHiddenChanged：适用于通过FragmentManager添加多个Fragment并且在点击切换Fragment的场景，通过FragmentManager的hide和show方法触发，能得出正确的判断  
- setUserVisibleHint：适用于ViewPager中嵌套Fragment的场景，在PagerAdapter中左右滑动时通过设置当前显示的Fragment会触发该方法，能得出正确的判断  
  
