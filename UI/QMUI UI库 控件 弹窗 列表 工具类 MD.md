| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
QMUI UI库 控件 弹窗 列表 工具类  
***  
目录  
===  

- [简介](#简介)
- [使用步骤](#使用步骤)
- [基本组成](#基本组成)
	- [弹窗相关组件](#弹窗相关组件)
	- [列表相关](#列表相关)
	- [顶部栏相关](#顶部栏相关)
	- [TextView相关](#textview相关)
	- [Span相关](#span相关)
	- [其他](#其他)
	- [工具类](#工具类)
- [演示案例](#演示案例)
	- [QMUIDialog](#qmuidialog)
	- [QMUIBottomSheet 和 QMUITipDialog 和 QMUIPopup](#qmuibottomsheet-和-qmuitipdialog-和-qmuipopup)
	- [QMUITabSegment](#qmuitabsegment)
  
# 简介  
[学习Demo](https://github.com/baiqiantao/QMUIDemo.git)   
[官网](http://qmuiteam.com/android)  
[GitHub](https://github.com/QMUI/QMUI_Android)  
[功能列表](http://qmuiteam.com/android/documents/)  
  
Android UI 解决方案  
  
QMUI Android 的设计目的是用于辅助快速搭建一个具备基本`设计还原效果`的 Android 项目，同时利用自身提供的丰富控件及兼容处理，让开发者能专注于业务需求而无需耗费精力在基础代码的设计上。  
  
不管是新项目的创建，或是已有项目的维护，均可使开发效率和项目质量得到大幅度提升。  
  
特点  
- `全局 UI 配置`：只需要修改一份`配置表`就可以调整 App 的`全局样式`，包括`组件颜色、导航栏、对话框、列表`等。一处修改，全局生效。  
- `丰富的 UI 控件`：提供丰富常用的 UI 控件，例如 BottomSheet、Tab、圆角ImageView、下拉刷新等，使用方便灵活，并且支持自定义控件的样式。  
- `高效的工具方法`：提供高效的工具方法，包括设备信息、屏幕信息、键盘管理、状态栏管理等，可以解决各种常见场景并大幅度提升开发效率。  
  
# 使用步骤  
**引入库**  
最新的库会上传到 JCenter 仓库上，请确保配置了 JCenter 仓库源，然后直接引用：  
```groovy  
compile 'com.qmuiteam:qmui:1.0.6'  
```  
  
**配置主题**  
把项目的 theme 的 parent 指向 `QMUI.Compat` 或 `QMUI`，至此，QMUI 可以正常工作。  
  
**覆盖组件的默认表现**  
你可以通过在项目中的 theme 中用 `<item name="(name)">(value)</item>` 的形式来覆盖 QMUI 组件的默认表现。具体可指定的属性名请参考 `@style/QMUI.Compat` 或 `@style/QMUI` 中的属性。  
  
# 基本组成  
## 弹窗相关组件  
**QMUIDialog**  
提供了一系列常用的对话框，解决了使用系统默认对话框时在不同 Android 版本上的表现不一致的问题。使用不同的 Builder 来构建不同类型的对话框，这些 Builder 都拥有设置 title 和添加底部按钮的功能，不同的 Builder 特有的作用如下：  
- `MessageDialogBuilder`：消息类型的对话框 Builder。通过它可以生成一个带标题、文本消息、按钮的对话框。  
- `ConfirmMessageDialogBuilder`：带 Checkbox 的消息确认框 Builder。  
- `EditTextDialogBuilder`：带输入框的对话框 Builder。  
- `MenuDialogBuilder`：菜单对话框 Builder。  
- `CheckableDialogBuilder`：单选类型的对话框 Builder。  
- `MultiCheckableDialogBuilder`：多选类型的对话框 Builder。  
- `CustomDialogBuilder`：自定义对话框内容区域的 Builder。  
- `AutoResizeDialogBuilder`：随键盘升降自动调整 Dialog 高度的 Builder  
  
**QMUITipDialog**  
提供一个浮层展示在屏幕中间，提供了以下两种样式：  
- 使用 `QMUITipDialog.Builder` 生成，提供了一个图标和一行文字的样式, 其中图标有 Loading、成功、失败等类型可选。  
- 使用 `QMUITipDialog.CustomBuilder` 生成，支持传入自定义的 layoutResId。  
  
**QMUIBottomSheet**  
在 Dialog 的基础上重新定制了 show() 和 hide() 时的动画效果, 使 Dialog 在界面底部升起和降下。提供了以下两个面板样式：  
- 列表样式：使用 `QMUIBottomSheet.BottomListSheetBuilder` 生成。  
- 宫格类型：使用 `QMUIBottomSheet.BottomGridSheetBuilder` 生成。  
  
**QMUIPopup**  
提供一个浮层，支持自定义浮层的内容，支持在指定 View 的任一方向旁边展示该浮层，支持自定义浮层出现/消失的动画。  
  
**QMUIListPopup**  
继承自 QMUIPopup，在 QMUIPopup 的基础上，支持显示一个列表。  
  
**QMUIProgressBar**  
一个进度条控件，通过颜色变化显示进度，支持环形和矩形两种形式，主要特性如下：  
- 支持在进度条中以文字形式显示进度，支持修改文字的颜色和大小。  
- 可以通过 xml 属性修改进度背景色，当前进度颜色，进度条尺寸。  
- 支持限制进度的最大值。  
  
## 列表相关  
**QMUIGroupListView**  
通用的列表，常用于 App 的设置界面，注意其父类不是 ListView 而是 LinearLayout，所以一般要配合 ScrollView 使用。提供了 Section 的概念，用来将列表分块。 配合 `QMUIGroupListView.Section`, `QMUICommonListItemView` 和 `QMUIGroupListSectionHeaderFooterView` 使用。  
  
**QMUIWrapContentListView**  
支持高度值为 wrap_content 的 ListView，解决原生 ListView 在设置高度为 wrap_content 时高度计算错误的 bug。  
  
**QMUIAnimationListView**  
使 ListView 支持添加/删除 Item 的动画，支持自定义动画效果。  
  
**QMUICommonListItemView**  
用作通用列表 QMUIGroupListView 里的 Item，也可单独使用。支持以下样式：  
- 展示一行文字。  
- 在右侧或下方增加一行说明文字。  
- 在 item 右侧显示一个开关或箭头或自定义的View  
  
**QMUIGroupListSectionHeaderFooterView**  
用作通用列表 QMUIGroupListView 里每个 Section 的头部或尾部，也可单独使用。  
  
**QMUIItemViewsAdapter**  
一个带 cache 功能的“列表型数据-View”的适配器，适用于自定义 View 需要显示重复单元 ListView 的情景，cache 功能主要是保证在需要多次刷新数据或布局的情况下（ListView 或 RecycleView 的 itemView）复用已存在的 View。QMUI 用于 QMUITabSegment 中 Tab 与数据的适配。  
  
**QMUIPullRefreshLayout**  
下拉刷新控件。支持自定义 RefreshView（表示正在刷新的 View），触发刷新的位置等特性。  
  
**QMUIEmptyView**  
通用的空界面控件，支持显示 loading、主标题和副标题、图片。  
  
**QMUILoadingView**  
用于显示 Loading 的 View，支持颜色和大小的设置。  
  
**QMUIObservableScrollView**  
可以监听滚动事件的 ScrollView，并能在滚动回调中获取每次滚动前后的偏移量。  
  
## 顶部栏相关  
**QMUITopBar**  
通用的顶部 Bar。提供了以下功能：  
- 在左侧/右侧添加图片按钮/文字按钮/自定义View。  
- 设置标题/副标题，且支持设置标题/副标题的水平对齐方式。  
  
**QMUITopBarLayout**  
对 QMUITopBar 的包裹类，并代理了 QMUITopBar 的方法。配合 QMUIWindowInsetLayout 使用，可使 QMUITopBar 在支持沉浸式状态栏的界面中顶部延伸到状态栏。  
  
**QMUIWindowInsetLayout**  
配合沉浸式状态栏使用，用于协调子 View 的 fitSystemWindows。  
  
**QMUITabSegment**  
用于横向多个 Tab 的布局，包含多个特性：  
- 可以用 xml 或 QMUITabSegment 提供的 set 方法统一配置文字颜色、icon 位置、是否要下划线等。  
- 每个 Tab 都可以非常灵活的配置，内容上支持文字和 icon 的显示，icon 支持选中态，支持内容的排版对齐方向设置，支持显示红点，支持插入自定义的 View，支持监听双击事件等。  
- 可以通过 setupWithViewPager(ViewPager) 方法与 ViewPager 绑定。  
  
## TextView相关  
**QMUILinkTextView**  
使 TextView 能自动识别 URL、电话、邮箱地址，相比 TextView 有以下特点：  
- 可以设置链接的样式。  
- 可以设置链接的点击事件。  
  
**QMUIFontFitTextView**  
使 TextView 在宽度固定的情况下，文字多到一行放不下时能缩小文字大小来自适应。  
  
**QMUISpanTouchFixTextView**  
相比 TextView，修正了两个常见问题：  
- 修正了 TextView 与 ClickableSpan 一起使用时，点击 ClickableSpan 也会触发 TextView 的事件的问题。  
- 修正了 TextView 默认情况下如果添加了 ClickableSpan 之后就无法把点击事件传递给 TextView 的 Parent 的问题。  
  
**QMUIVerticalTextView**  
在 TextView 的基础上支持文字竖排。  
  
## Span相关  
**QMUITouchableSpan**  
继承自 ClickableSpan，支持 normal 态和 press 态时有不同的背景颜色以及字体颜色。建议配合 QMUISpanTouchFixTextView 或其子类使用，便于事件传递的协调。  
  
**QMUIBlockSpaceSpan**  
通过在段落之间设置该 span，实现段间距的效果。  
  
**QMUICustomTypefaceSpan**  
支持以 Typeface 的方式设置 span 的字体，实现自定义字体的效果。  
  
**QMUIAlignMiddleImageSpan**  
继承自 ImageSpan，在此基础上实现让 span 垂直居中的效果。  
  
**QMUIMarginImageSpan**  
继承自 QMUIMarginImageSpan，在此基础上支持设置图片的左右间距。  
  
**QMUITextSizeSpan**  
支持调整字体大小的 span。AbsoluteSizeSpan 可以调整字体大小，但在中英文混排下由于decent的不同，无法根据具体需求进行底部对齐或者顶部对齐。而 QMUITextSizeSpan 则可以多传一个参数，让你可以根据具体情况来决定偏移值。  
  
## 其他  
**QMUIKeyboardHelper**  
提供更加便捷的方式针对给定的 EditText 显示/隐藏软键盘，并且提供了工具方法判断键盘是否当前可见。  
  
**QMUIFloatLayout**  
类似 CSS 里 float: left 的浮动布局，从左到右排列子 View 并自动换行。支持以下特性：  
- 控制子 View 之间的垂直/水平间距。  
- 控制子 View 的水平对齐方向（左对齐/居中/右对齐）。  
- 限制子 View 的个数或行数。  
  
**QMUIRadiusImageView**  
提供为图片添加圆角、边框、剪裁到圆形或其他形状等功能。  
  
**QMUIRoundButton**  
对 Button 提供圆角功能，支持以下特性：  
- 指定圆角的大小。  
- 分别指定不同方向的圆角大小。  
- 指定圆角的大小为高度的一半，并跟随高度变化自适应圆角大小。  
- 支持分别指定背景色和边框色，指定颜色时支持使用 color 或 ColorStateList。  
  
**QMUIRoundButtonDrawable**  
使用该 Drawable 可以方便地生成圆角矩形/圆形 Drawable，提供设置背景色、描边大小和颜色、圆角自适应 View 高度等特性。  
  
**QMUIQQFaceView**  
支持显示表情的伪 TextView（继续自定义 View，而不是真正的 TextView)，实现了 TextView 的 maxLine、ellipsize、textSize、textColor 等基本功能。支持与 QMUITouchableSpan 配合使用实现内容可点击。  
- `QMUIQQFaceCompiler`：QMUIQQFaceView 的内容解析器，将文本内容解析成 QMUIQQFaceView 想要的数据格式。  
- `IQMUIQQFaceManager`：QMUIQQFaceView 资源管理接口，使用 QMUIQQFaceView 必须实现这个接口以提供表情资源。  
  
## 工具类  
**QMUIColorHelper**  
颜色处理工具类，按照功能类型来划分，总共包含以下几个特性：  
- 为一个颜色设置透明度。  
- 根据指定比例，在两个颜色值之间计算出一个颜色值。  
- 将颜色值转换为字符串。  
  
**QMUIDeviceHelper**  
获取设备信息的工具类，按照功能类型来划分，总共包含以下几个特性：  
- 判断设备为手机/平板。  
- 判断设备是否为魅族手机。  
- 判断当前系统是否为 Flyme 系统。  
- 判断当前系统是否为 MIUI 系统。  
- 判断当前是否拥有悬浮窗权限。  
  
**QMUIDisplayHelper**  
屏幕相关的工具类，按照功能类型来划分，总共包含以下几个特性：  
- 方便地获取一个 DisplayMetrics 实例。  
- 获取屏幕信息，包括屏幕密度、屏幕宽度和高度、状态栏高度、ActionBar 高度等。  
- 获取设备硬件信息，包括是否有可用摄像头、是否有硬件菜单、是否有网络、SD Card 是否可用、当前选择的国家语言等。  
- 判断当前是否处于全屏状态，控制进入/退出全屏状态。  
- dp 与 px 数值的相互转化。  
  
**QMUIDrawableHelper**  
- 快速绘制一张指定大小、颜色、边框的图片，支持形状为圆角矩形和圆形。  
- 快速绘制一张带上分隔线或下分隔线的图片。  
- 快速绘制一张可带圆角的渐变图片。  
- 将当前图片的颜色换成另一个颜色。  
- 将两张图片叠加后生成一张新的图片。  
- 对某个 View 截图生成图片。  
  
**QMUIPackageHelper**  
提供简便的方式获取 App 的版本信息，可以单独获取主版本号、次版本号以及修正版本号。  
  
**QMUIResHelper**  
封装了更加便捷的方法，用于获取当前 Theme 下的 Attr 值，支持 Float、Color、ColorStateList、Drawable 和 Dimen 类型的 Attr。  
  
**QMUISpanHelper**  
提供了方法使得 QMUIMarginImageSpan 能被更便捷地使用。  
  
**QMUIStatusBarHelper**  
状态栏相关的工具类，按照功能类型来划分，总共包含以下几个特性：  
- 快速实现沉浸式状态栏（支持 4.4 以上版本的 MIUI 和 Flyme，以及 5.0 以上版本的其他 Android）。  
- 快速设置状态栏为黑色或白色字体图标（支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android）。  
- 提供多个常用的工具方法，如获取状态栏高度、判断当前是否全屏等等。  
  
**QMUIViewHelper**  
View 工具类，按照功能类型来划分，总共包含以下几个特性：  
- 对 ImageView 进行处理，可以按比例缩放图片。  
- 对 View 做背景颜色变化动画，支持多个动画参数。  
- 对 View 做进退场动画，支持透明度变化和上下位移两种方式。  
- 提供多个常用的 View 相关工具方法，如对 View 设置单个方向的 padding、从 ViewStub 中获取一个 View、判断 ListView 是否已经滚动到底部等等。  
  
# 演示案例  
## QMUIDialog  
```java  
public class QMUIDialogActivity extends ListActivity {  
    private Context mcContext;  
    private int mCurrentDialogStyle = R.style.QMUI_Dialog;  
    private boolean b;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        QMUIStatusBarHelper.setStatusBarLightMode(this);  
        mcContext = this;  
        String[] array = new String[]{  
                "消息类型对话框（蓝色按钮）",  
                "消息类型对话框（红色按钮）",  
                "消息类型对话框 (很长文案)",  
                "菜单类型对话框",  
                "带 Checkbox 的消息确认框",  
                "单选菜单类型对话框",  
                "多选菜单类型对话框",  
                "多选菜单类型对话框(item 数量很多)",  
                "带输入框的对话框",  
                "高度适应键盘升降的对话框"  
        };  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array))));  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        mCurrentDialogStyle = b ? com.qmuiteam.qmui.R.style.QMUI_Dialog : R.style.DialogTheme2;  
          
        b = !b;  
        switch (position) {  
            case 0:  
                showMessagePositiveDialog();  
                break;  
            case 1:  
                showMessageNegativeDialog();  
                break;  
            case 2:  
                showLongMessageDialog();  
                break;  
            case 3:  
                showMenuDialog();  
                break;  
            case 4:  
                showConfirmMessageDialog();  
                break;  
            case 5:  
                showSingleChoiceDialog();  
                break;  
            case 6:  
                showMultiChoiceDialog();  
                break;  
            case 7:  
                showNumerousMultiChoiceDialog();  
                break;  
            case 8:  
                showEditTextDialog();  
                break;  
            case 9:  
                showAutoDialog();  
                break;  
              
        }  
    }  
      
    private void showMessagePositiveDialog() {  
        new QMUIDialog.MessageDialogBuilder(mcContext)  
                .setTitle("标题")  
                .setMessage("确定要发送吗？")  
                .addAction("取消", (dialog, index) -> dialog.dismiss())  
                .addAction("确定", (dialog, index) -> {  
                    dialog.dismiss();  
                    Toast.makeText(mcContext, "发送成功", Toast.LENGTH_SHORT).show();  
                })  
                .create(mCurrentDialogStyle).show();  
    }  
      
    private void showMessageNegativeDialog() {  
        new QMUIDialog.MessageDialogBuilder(mcContext)  
                .setTitle("标题")  
                .setMessage("确定要删除吗？")  
                .addAction("取消", (dialog, index) -> dialog.dismiss())  
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, (dialog, index) -> {  
                    Toast.makeText(mcContext, "删除成功", Toast.LENGTH_SHORT).show();  
                    dialog.dismiss();  
                })  
                .create(mCurrentDialogStyle).show();  
    }  
      
    private void showLongMessageDialog() {  
        new QMUIDialog.MessageDialogBuilder(mcContext)  
                .setTitle("标题")  
                .setMessage("这是一段很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +  
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很" +  
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很" +  
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很" +  
                        "长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +  
                        "很长很长很长很长很很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +  
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +  
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +  
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +  
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +  
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长长很长的文案")  
                .addAction("取消", (dialog, index) -> dialog.dismiss())  
                .create(mCurrentDialogStyle).show();  
    }  
      
    private void showConfirmMessageDialog() {  
        new QMUIDialog.CheckBoxMessageDialogBuilder(mcContext)  
                .setTitle("退出后是否删除账号信息?")  
                .setMessage("删除账号信息")  
                .setChecked(true)  
                .addAction("取消", (dialog, index) -> dialog.dismiss())  
                .addAction("退出", (dialog, index) -> dialog.dismiss())  
                .create(mCurrentDialogStyle).show();  
    }  
      
    private void showMenuDialog() {  
        final String[] items = new String[]{"选项1", "选项2", "选项3"};  
        new QMUIDialog.MenuDialogBuilder(mcContext)  
                .addItems(items, (dialog, which) -> {  
                    Toast.makeText(mcContext, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();  
                    dialog.dismiss();  
                })  
                .create(mCurrentDialogStyle).show();  
    }  
      
    private void showSingleChoiceDialog() {  
        final String[] items = new String[]{"选项1", "选项2", "选项3"};  
        final int checkedIndex = 1;  
        new QMUIDialog.CheckableDialogBuilder(mcContext)  
                .setCheckedIndex(checkedIndex)  
                .addItems(items, (dialog, which) -> {  
                    Toast.makeText(mcContext, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();  
                    dialog.dismiss();  
                })  
                .create(mCurrentDialogStyle).show();  
    }  
      
    private void showMultiChoiceDialog() {  
        final String[] items = new String[]{"选项1", "选项2", "选项3", "选项4", "选项5", "选项6"};  
        final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(mcContext)  
                .setCheckedItems(new int[]{1, 3})  
                .addItems(items, (dialog, which) -> {  
                });  
        builder.addAction("取消", (dialog, index) -> dialog.dismiss());  
        builder.addAction("提交", (dialog, index) -> {  
            StringBuilder result = new StringBuilder("你选择了 ");  
            for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {  
                result.append("").append(builder.getCheckedItemIndexes()[i]).append("; ");  
            }  
            Toast.makeText(mcContext, result.toString(), Toast.LENGTH_SHORT).show();  
            dialog.dismiss();  
        });  
        builder.create(mCurrentDialogStyle).show();  
    }  
      
    private void showNumerousMultiChoiceDialog() {  
        final String[] items = new String[]{  
                "选项1", "选项2", "选项3", "选项4", "选项5", "选项6",  
                "选项7", "选项8", "选项9", "选项10", "选项11", "选项12",  
                "选项13", "选项14", "选项15", "选项16", "选项17", "选项18"  
        };  
        final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(mcContext)  
                .setCheckedItems(new int[]{1, 3})  
                .addItems(items, (dialog, which) -> {  
                });  
        builder.addAction("取消", (dialog, index) -> dialog.dismiss());  
        builder.addAction("提交", (dialog, index) -> {  
            StringBuilder result = new StringBuilder("你选择了 ");  
            for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {  
                result.append("").append(builder.getCheckedItemIndexes()[i]).append("; ");  
            }  
            Toast.makeText(mcContext, result.toString(), Toast.LENGTH_SHORT).show();  
            dialog.dismiss();  
        });  
        builder.create(mCurrentDialogStyle).show();  
    }  
      
    private void showEditTextDialog() {  
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(mcContext);  
        builder.setTitle("标题")  
                .setPlaceholder("在此输入您的昵称")  
                .setInputType(InputType.TYPE_CLASS_TEXT)  
                .addAction("取消", (dialog, index) -> dialog.dismiss())  
                .addAction("确定", (dialog, index) -> {  
                    CharSequence text = builder.getEditText().getText();  
                    if (text != null && text.length() > 0) {  
                        Toast.makeText(mcContext, "您的昵称: " + text, Toast.LENGTH_SHORT).show();  
                        dialog.dismiss();  
                    } else {  
                        Toast.makeText(mcContext, "请填入昵称", Toast.LENGTH_SHORT).show();  
                    }  
                })  
                .create(mCurrentDialogStyle).show();  
    }  
      
    private void showAutoDialog() {  
        QMAutoTestDialogBuilder autoTestDialogBuilder = (QMAutoTestDialogBuilder) new QMAutoTestDialogBuilder(mcContext)  
                .addAction("取消", (dialog, index) -> dialog.dismiss())  
                .addAction("确定", (dialog, index) -> {  
                    Toast.makeText(mcContext, "你点了确定", Toast.LENGTH_SHORT).show();  
                    dialog.dismiss();  
                });  
        autoTestDialogBuilder.create(mCurrentDialogStyle).show();  
        QMUIKeyboardHelper.showKeyboard(autoTestDialogBuilder.getEditText(), true);  
    }  
      
    class QMAutoTestDialogBuilder extends QMUIDialog.AutoResizeDialogBuilder {  
        private Context mContext;  
        private EditText mEditText;  
          
        public QMAutoTestDialogBuilder(Context context) {  
            super(context);  
            mContext = context;  
        }  
          
        public EditText getEditText() {  
            return mEditText;  
        }  
          
        @Override  
        public View onBuildContent(QMUIDialog dialog, ScrollView parent) {  
            LinearLayout layout = new LinearLayout(mContext);  
            layout.setOrientation(LinearLayout.VERTICAL);  
            layout.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));  
            int padding = QMUIDisplayHelper.dp2px(mContext, 20);  
            layout.setPadding(padding, padding, padding, padding);  
            mEditText = new EditText(mContext);  
            QMUIViewHelper.setBackgroundKeepingPadding(mEditText, QMUIResHelper.getAttrDrawable(mContext, R.attr.qmui_list_item_bg_with_border_bottom));  
            mEditText.setHint("输入框");  
            LinearLayout.LayoutParams editTextLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, QMUIDisplayHelper.dpToPx(50));  
            editTextLP.bottomMargin = QMUIDisplayHelper.dp2px(mcContext, 15);  
            mEditText.setLayoutParams(editTextLP);  
            layout.addView(mEditText);  
            TextView textView = new TextView(mContext);  
            textView.setLineSpacing(QMUIDisplayHelper.dp2px(mcContext, 4), 1.0f);  
            textView.setText("观察聚焦输入框后，键盘升起降下时 dialog 的高度自适应变化。\n\n" +  
                    "QMUI Android 的设计目的是用于辅助快速搭建一个具备基本设计还原效果的 Android 项目，" +  
                    "同时利用自身提供的丰富控件及兼容处理，让开发者能专注于业务需求而无需耗费精力在基础代码的设计上。" +  
                    "不管是新项目的创建，或是已有项目的维护，均可使开发效率和项目质量得到大幅度提升。");  
            textView.setTextColor(ContextCompat.getColor(mcContext, R.color.app_color_description));  
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));  
            layout.addView(textView);  
            return layout;  
        }  
    }  
      
}  
```  
  
## QMUIBottomSheet 和 QMUITipDialog 和 QMUIPopup  
```java  
public class BottomSheet_TipDialog_PopupActivity extends ListActivity {  
    private Context mContext;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        mContext = this;  
        String[] array = {"BottomSheet List",  
                "BottomSheet Grid",  
                "Loading 类型提示框",  
                "成功提示类型提示框",  
                "失败提示类型提示框",  
                "信息提示类型提示框",  
                "单独图片类型提示框",  
                "单独文字类型提示框",  
                "自定义内容提示框",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array))));  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                showSimpleBottomSheetList();  
                break;  
            case 1:  
                showSimpleBottomSheetGrid();  
                break;  
            default:  
                dealTipDialog(v, position - 2);  
                break;  
        }  
    }  
      
    private void dealTipDialog(View v, int position) {  
        if (position % 2 == 0) {  
            showListPopup(v, new Random().nextInt(3));  
        } else {  
            showNormalPopup(v, new Random().nextInt(3));  
        }  
        final QMUITipDialog tipDialog;  
        switch (position) {  
            case 0:  
                tipDialog = new QMUITipDialog.Builder(mContext)  
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)  
                        .setTipWord("正在加载")  
                        .create();  
                break;  
            case 1:  
                tipDialog = new QMUITipDialog.Builder(mContext)  
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)  
                        .setTipWord("发送成功")  
                        .create();  
                break;  
            case 2:  
                tipDialog = new QMUITipDialog.Builder(mContext)  
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)  
                        .setTipWord("发送失败")  
                        .create();  
                break;  
            case 3:  
                tipDialog = new QMUITipDialog.Builder(mContext)  
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)  
                        .setTipWord("请勿重复操作")  
                        .create();  
                break;  
            case 4:  
                tipDialog = new QMUITipDialog.Builder(mContext)  
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)  
                        .create();  
                break;  
            case 5:  
                tipDialog = new QMUITipDialog.Builder(mContext)  
                        .setTipWord("请勿重复操作")  
                        .create();  
                break;  
            case 6:  
                tipDialog = new QMUITipDialog.CustomBuilder(mContext)  
                        .setContent(R.layout.qmui_bottom_sheet_grid)  
                        .create();  
                break;  
            default:  
                tipDialog = new QMUITipDialog.Builder(mContext)  
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)  
                        .setTipWord("正在加载")  
                        .create();  
        }  
        tipDialog.show();  
        new Handler().postDelayed(tipDialog::dismiss, 1500);  
    }  
      
    private void showSimpleBottomSheetList() {  
        new QMUIBottomSheet.BottomListSheetBuilder(mContext)  
                .addItem("Item 1")  
                .addItem("Item 2")  
                .addItem("Item 3")  
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {  
                    dialog.dismiss();  
                    Toast.makeText(mContext, "Item " + (position + 1), Toast.LENGTH_SHORT).show();  
                })  
                .build()  
                .show();  
    }  
      
    private void showSimpleBottomSheetGrid() {  
        final int TAG_SHARE_WECHAT_FRIEND = 0;  
        final int TAG_SHARE_WECHAT_MOMENT = 1;  
        final int TAG_SHARE_WEIBO = 2;  
        final int TAG_SHARE_CHAT = 3;  
        final int TAG_SHARE_LOCAL = 4;  
        new QMUIBottomSheet.BottomGridSheetBuilder(mContext)  
                .addItem(R.mipmap.ic_launcher, "分享到微信", TAG_SHARE_WECHAT_FRIEND, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)  
                .addItem(R.mipmap.ic_launcher, "分享到朋友圈", TAG_SHARE_WECHAT_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)  
                .addItem(R.mipmap.ic_launcher, "分享到微博", TAG_SHARE_WEIBO, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)  
                .addItem(R.mipmap.ic_launcher, "分享到私信", TAG_SHARE_CHAT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)  
                .addItem(R.mipmap.ic_launcher, "保存到本地", TAG_SHARE_LOCAL, QMUIBottomSheet.BottomGridSheetBuilder.SECOND_LINE)  
                .setOnSheetItemClickListener((dialog, itemView) -> {  
                    dialog.dismiss();  
                    int tag = (int) itemView.getTag();  
                    switch (tag) {  
                        case TAG_SHARE_WECHAT_FRIEND:  
                            Toast.makeText(mContext, "分享到微信", Toast.LENGTH_SHORT).show();  
                            break;  
                        case TAG_SHARE_WECHAT_MOMENT:  
                            Toast.makeText(mContext, "分享到朋友圈", Toast.LENGTH_SHORT).show();  
                            break;  
                        case TAG_SHARE_WEIBO:  
                            Toast.makeText(mContext, "分享到微博", Toast.LENGTH_SHORT).show();  
                            break;  
                        case TAG_SHARE_CHAT:  
                            Toast.makeText(mContext, "分享到私信", Toast.LENGTH_SHORT).show();  
                            break;  
                        case TAG_SHARE_LOCAL:  
                            Toast.makeText(mContext, "保存到本地", Toast.LENGTH_SHORT).show();  
                            break;  
                    }  
                })  
                .build().show();  
    }  
      
    private void showNormalPopup(View v, int preferredDirection) {  
        QMUIPopup mNormalPopup = new QMUIPopup(mContext, QMUIPopup.DIRECTION_NONE);  
        TextView textView = new TextView(mContext);  
        textView.setLayoutParams(mNormalPopup.generateLayoutParam(QMUIDisplayHelper.dp2px(mContext, 250),  
                ViewGroup.LayoutParams.WRAP_CONTENT));  
        textView.setLineSpacing(QMUIDisplayHelper.dp2px(mContext, 4), 1.0f);  
        int padding = QMUIDisplayHelper.dp2px(mContext, 20);  
        textView.setPadding(padding, padding, padding, padding);  
        textView.setText("Popup 可以设置其位置以及显示和隐藏的动画");  
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.app_color_description));  
        mNormalPopup.setContentView(textView);  
        mNormalPopup.setOnDismissListener(() -> Toast.makeText(mContext, "onDismiss", Toast.LENGTH_SHORT).show());  
          
        mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);  
        mNormalPopup.setPreferredDirection(preferredDirection);//QMUIPopup.DIRECTION_TOP、DIRECTION_BOTTOM、DIRECTION_NONE  
        mNormalPopup.show(v);  
    }  
      
    private void showListPopup(View v, int preferredDirection) {  
        String[] array = new String[]{  
                "Item 1",  
                "Item 2",  
                "Item 3",  
                "Item 4",  
                "Item 5",  
        };  
        ArrayAdapter adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array)));  
        QMUIListPopup mListPopup = new QMUIListPopup(mContext, QMUIPopup.DIRECTION_NONE, adapter);  
        mListPopup.create(QMUIDisplayHelper.dp2px(mContext, 250), QMUIDisplayHelper.dp2px(mContext, 200),  
                (adapterView, view, i, l) -> {  
                    Toast.makeText(mContext, "Item " + (i + 1), Toast.LENGTH_SHORT).show();  
                    mListPopup.dismiss();  
                });  
        mListPopup.setOnDismissListener(() -> Toast.makeText(mContext, "onDismiss", Toast.LENGTH_SHORT).show());  
          
        mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);  
        mListPopup.setPreferredDirection(preferredDirection);  
        mListPopup.show(v);  
    }  
}  
```  
  
## QMUITabSegment  
```java  
public class QMUITabSegmentActivity extends Activity {  
    private Context context;  
    private QMUITopBar topBar;  
    private QMUITabSegment tabSegment;  
    private ViewPager viewPager;  
    private Drawable drawable, drawable2;  
      
    @Override  
    protected void onCreate(@Nullable Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.fragment_tab_viewpager_layout);  
        context = this;  
        drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher);  
        drawable2 = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round);  
          
        topBar = findViewById(R.id.topbar);  
        tabSegment = findViewById(R.id.tabSegment);  
        viewPager = findViewById(R.id.contentViewPager);  
          
        initView();  
    }  
      
    private void initView() {  
        topBar.addLeftBackImageButton().setOnClickListener(v -> finish());  
        topBar.setTitle("QMUITabSegment");  
        topBar.addRightImageButton(R.mipmap.ic_launcher_round, R.id.topbar_right).setOnClickListener(v -> showBottomSheetList());  
          
        viewPager.setAdapter(new PagerAdapter() {  
            @Override  
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {  
                return view == object;  
            }  
              
            @Override  
            public int getCount() {  
                return 2;  
            }  
              
            @NonNull  
            @Override  
            public Object instantiateItem(@NonNull final ViewGroup container, int position) {  
                TextView textView = new TextView(context);  
                textView.setGravity(Gravity.CENTER);  
                textView.setBackgroundColor(position == 0 ? Color.YELLOW : Color.CYAN);  
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);  
                textView.setText(position == 0 ? "白乾涛" : "包青天  " + position);  
                container.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));  
                return textView;  
            }  
              
            @Override  
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {  
                container.removeView((View) object);  
            }  
        });  
        viewPager.setCurrentItem(0, false);  
          
        tabSegment.addTab(new QMUITabSegment.Tab("白乾涛"));  
        tabSegment.addTab(new QMUITabSegment.Tab("包青天"));  
        tabSegment.setupWithViewPager(viewPager, false);  
        tabSegment.setMode(QMUITabSegment.MODE_FIXED);  
        /*tabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);  
        tabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(this, 16));*/  
          
        tabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {  
            @Override  
            public void onTabSelected(int index) {//当某个 Tab 被选中时会触发  
                Log.i("bqt", "【onTabSelected】" + index);  
                tabSegment.hideSignCountView(index);//根据 index 在对应的 Tab 上隐藏红点  
            }  
              
            @Override  
            public void onTabReselected(int index) {//当某个 Tab 处于被选中状态下再次被点击时会触发  
                Log.i("bqt", "【onTabReselected】" + index);  
                tabSegment.hideSignCountView(index);//根据 index 在对应的 Tab 上隐藏红点  
            }  
              
            @Override  
            public void onTabUnselected(int index) {//当某个 Tab 被取消选中时会触发  
                Log.i("bqt", "【onTabUnselected】" + index);  
            }  
              
            @Override  
            public void onDoubleTap(int index) {//当某个 Tab 被双击时会触发  
                Log.i("bqt", "【onDoubleTap】" + index);  
            }  
        });  
    }  
      
    private void showBottomSheetList() {  
        new QMUIBottomSheet.BottomListSheetBuilder(this)  
                .addItem("简单文字")  
                .addItem("文字 + 底部")  
                .addItem("文字 + 顶部 indicator")  
                .addItem("文字 + indicator 长度不要跟随内容长度")  
                .addItem("文字 + icon(支持四个方向) + 自动着色选中态 icon")  
                .addItem("显示红点")  
                .addItem("选中态更换 icon")  
                .addItem("不同 item，不同文字(icon)颜色")  
                .addItem("根据 index 更新 tab 文案")  
                .addItem("根据 index 完全替换 tab")  
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {  
                    dialog.dismiss();  
                    switch (position) {  
                        case 0:  
                            tabSegment.reset();//1、清空已经存在的 Tab。  
                            tabSegment.setHasIndicator(false);//设置是否需要显示 indicator  
                            tabSegment.addTab(new QMUITabSegment.Tab("白乾涛"));// 2、重新 addTab添加新的 Tab  
                            tabSegment.addTab(new QMUITabSegment.Tab("包青天"));  
                            tabSegment.notifyDataChanged();//3、通过 notifyDataChanged() 通知变动  
                            break;  
                        case 1:  
                            tabSegment.reset();  
                            tabSegment.setHasIndicator(true);  
                            tabSegment.setIndicatorPosition(false);//true 时表示 indicator 位置在 Tab 的上方, false 时表示在下方  
                            tabSegment.setIndicatorWidthAdjustContent(true);//设置 indicator的宽度是否随内容宽度变化  
                            tabSegment.addTab(new QMUITabSegment.Tab("白乾涛"));  
                            tabSegment.addTab(new QMUITabSegment.Tab("包青天"));  
                            tabSegment.notifyDataChanged();  
                            break;  
                        case 2:  
                            tabSegment.reset();  
                            tabSegment.setHasIndicator(true);  
                            tabSegment.setIndicatorPosition(true);  
                            tabSegment.setIndicatorWidthAdjustContent(true);  
                            tabSegment.addTab(new QMUITabSegment.Tab("白乾涛"));  
                            tabSegment.addTab(new QMUITabSegment.Tab("包青天"));  
                            tabSegment.notifyDataChanged();  
                            break;  
                        case 3:  
                            tabSegment.reset();  
                            tabSegment.setHasIndicator(true);  
                            tabSegment.setIndicatorPosition(false);  
                            tabSegment.setIndicatorWidthAdjustContent(false);  
                            tabSegment.addTab(new QMUITabSegment.Tab("白乾涛"));  
                            tabSegment.addTab(new QMUITabSegment.Tab("包青天"));  
                            tabSegment.notifyDataChanged();  
                            break;  
                        case 4:  
                            tabSegment.reset();  
                            tabSegment.setHasIndicator(false);  
                            tabSegment.addTab(new QMUITabSegment.Tab(drawable, null, "白乾涛", true));  
                            tabSegment.addTab(new QMUITabSegment.Tab(drawable2, null, "包青天", true));  
                            tabSegment.notifyDataChanged();  
                            break;  
                        case 5:  
                            QMUITabSegment.Tab tab = tabSegment.getTab(0);  
                            tab.setSignCountMargin(0, -QMUIDisplayHelper.dp2px(context, 4));  
                            tab.showSignCountView(context, 10086);//显示 Tab 上的未读数或红点  
                            tabSegment.notifyDataChanged();  
                            break;  
                        case 6:  
                            tabSegment.reset();  
                            tabSegment.setHasIndicator(false);  
                            tabSegment.addTab(new QMUITabSegment.Tab(drawable, drawable2, "白乾涛", false));  
                            tabSegment.addTab(new QMUITabSegment.Tab(drawable, drawable2, "包青天", false));  
                            tabSegment.notifyDataChanged();  
                            break;  
                        case 7:  
                            tabSegment.reset();  
                            tabSegment.setHasIndicator(true);  
                            tabSegment.setIndicatorWidthAdjustContent(true);  
                            tabSegment.setIndicatorPosition(false);  
                            QMUITabSegment.Tab tab1 = new QMUITabSegment.Tab(drawable, null, "白乾涛", true);  
                            tab1.setTextColor(Color.GREEN, Color.RED);  
                            QMUITabSegment.Tab tab2 = new QMUITabSegment.Tab(drawable2, null, "包青天", true);  
                            tab2.setTextColor(Color.GREEN, Color.RED);  
                            tabSegment.addTab(tab1);  
                            tabSegment.addTab(tab2);  
                            tabSegment.notifyDataChanged();  
                            break;  
                        case 8:  
                            tabSegment.updateTabText(0, "动态更新文案");//改变 Tab 的文案  
                            tabSegment.notifyDataChanged();  
                            break;  
                        case 9:  
                            QMUITabSegment.Tab replaceTab = new QMUITabSegment.Tab(drawable, null, "动态更新", true);  
                            tabSegment.replaceTab(0, replaceTab);//整个 Tab 替换  
                            tabSegment.notifyDataChanged();  
                            break;  
                        default:  
                            break;  
                    }  
                })  
                .build()  
                .show();  
    }  
}  
```  
2018-4-21  
