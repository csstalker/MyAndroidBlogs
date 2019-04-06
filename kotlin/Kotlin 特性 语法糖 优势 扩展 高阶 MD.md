| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Kotlin 特性 语法糖 优势 扩展 高阶 MD  
***  
目录  
===  

- [用 Kotlin 开发 Android 项目的优势](#用-Kotlin-开发-Android-项目的优势)
	- [有了空安全，再也不怕服务端返回空对象了](#有了空安全，再也不怕服务端返回空对象了)
	- [转型与智能转换，省力又省心](#转型与智能转换，省力又省心)
	- [比 switch 更强大的 when](#比-switch-更强大的-when)
	- [容器的操作符](#容器的操作符)
	- [线程切换，so easy](#线程切换，so-easy)
	- [一个关键字实现单例](#一个关键字实现单例)
	- [自动 getter、setter 及 class 简洁声明](#自动-getter、setter-及-class-简洁声明)
	- [DSL 式编程](#DSL-式编程)
	- [委托/代理，SharedPreference 不再麻烦](#委托代理，SharedPreference-不再麻烦)
	- [扩展，和工具类说拜拜](#扩展，和工具类说拜拜)
	- [向 findViewById 说 NO](#向-findViewById-说-NO)
	- [简单粗暴的 startActivity](#简单粗暴的-startActivity)
	- [玲珑小巧的 toast](#玲珑小巧的-toast)
	- [用 apply 方法进行数据组合](#用-apply-方法进行数据组合)
	- [利用高阶函数搞事情](#利用高阶函数搞事情)
	- [用扩展方法替代工具类](#用扩展方法替代工具类)
	- [自动 getter、setter 使得代码更精简](#自动-getter、setter-使得代码更精简)
  
# 用 Kotlin 开发 Android 项目的优势  
[参考1](http://www.jianshu.com/p/8a1fce6fa93a)   
[参考2](http://www.jianshu.com/p/b444aea1b038)  
  
## 有了空安全，再也不怕服务端返回空对象了  
简单一点的例子，那就是 String 和 `String?`是两种不同的类型。String 已经确定是不会为空，一定有值；而 `String?`则是未知的，也许有值，也许是空。在使用对象的属性和方法的时候，String 类型的对象可以毫无顾忌的直接使用，而 `String?`类型需要你先做非空判断。  
```java  
fun main(args: Array<String>) {  
    var string1: String = "string1"  
    var string2: String? = null  
  
    println(string1.length) //7  
    //string1 = null; //不能赋空值  
    println(string2?.length) //null，既不会报错，也不会崩溃  
    string2 = "string2";  
    println(string2?.length) //7  
}  
```  
  
尽管 string2 是一个空对象，也并没有因为我调用了它的属性/方法就报空指针。而你所需要做的，仅仅是加一个"?"。  
  
如果说这样还体现不出空安全的好处，那么看下面的例子：  
```java  
val a: A? = A()  
println(a?.b?.c)  
```  
  
试想一下当每一级的属性皆有可能为空的时候，JAVA 中我们需要怎么处理？  
  
## 转型与智能转换，省力又省心  
我写过这样子的 JAVA 代码  
```java  
if(view instanceof TextView) {  
    TextView textView = (TextView) view;  
    textView.setText("text");  
}  
```  
  
而在 Kotlin 中的写法则有所不同  
```java  
if (view is TextView) {  
    var textView: TextView = view as TextView  
    textView.setText("text")  
}  
```  
  
缩减代码之后对比更加明显  
```java  
if(view instanceof TextView) {  
    ((TextView) view).setText("text");  
}  
```  
```java  
if(view is TextView) {  
    (view as TextView).setText("text")  
}  
```  
  
相比于 JAVA 在对象前加 `(Class)` 这样子的写法，Kotlin 是在对象之后添加 `as Class` 来实现转型。至少我个人而言，在习惯了 as Class 顺畅的写法之后，是再难以忍受 JAVA 中前置的写法，哪怕有 cast 快捷键的存在，仍然很容易打断我写代码的顺序和思路  
  
事实上，Kotlin 此处可以更简单：  
```java  
if(view is TextView) {  
    view.setText("text")  
}  
```  
因为当前上下文已经判明 view 就是 TextView，所以在当前代码块中 view 不再是 View 类，而是 TextView 类。这就是 Kotlin 的**智能转换**。  
  
接着上面的空安全来举个例子，常规思路下，既然 String 和 `String?` 是不同的类型，是不是我有可能会写出这样的代码？  
```java  
val a: A? = A()  
if (a != null) {  
    println(a?.b)  
}  
```  
这样子写，Kotlin 反而会给你显示一个高亮的警告，说这是一个不必要的 safe call。至于为什么，因为你前面已经写了 `a != null` 了啊，于是 a 在这个代码块里不再是 A? 类型, 而是 A 类型。  
```java  
val a: A? = A()  
if (a != null) {  
    println(a.b)  
}  
```  
  
智能转换还有一个经常出现的场景，那就是 `switch case` 语句中。在 Kotlin 中，则是 `when` 语法。  
```java  
fun testWhen(obj: Any) {  
    when(obj) {  
        is Int -> {  
            println("obj is a int")  
            println(obj + 1)  
        }  
  
        is String -> {  
            println("obj is a string")  
            println(obj.length)  
        }  
  
        else -> {  
            println("obj is something i don't care")  
        }  
    }  
}  
```  
  
可以看出在已经判断出是 String 的条件下，原本是一个 `Any` 类的 obj 对象，我可以直接使用属于 `String` 类的 `.length` 属性。  
  
Kotlin 的智能程度远不止如此，即便是现在，在编写代码的时候还会偶尔蹦一个高亮警告出来，这时候我才知道原来我的写法是多余的，Kotlin 已经帮我处理了好了。  
  
## 比 switch 更强大的 when  
通过上面智能转化的例子，已经展示了一部分 when 的功能。但相对于 JAVA 的 switch，Kotlin 的 when 带给我的惊喜远远不止这么一点。  
例如：  
```java  
fun testWhen(int: Int) {  
    when(int) {  
        in 10 .. Int.MAX_VALUE -> println("${int} 太大了我懒得算")  
        2, 3, 5, 7 -> println("${int} 是质数")  
        else -> println("${int} 不是质数")  
    }  
}  
```  
```java  
fun main(args: Array<String>) {  
    (0..10).forEach { testWhen(it) }  
}  
```  
  
和 JAVA 中死板的 switch-case 语句不同，在 when 中，我既可以用参数去匹配 10 到 Int.MAX_VALUE 的区间，也可以去匹配 `2, 3, 5, 7` 这一组值，当然我这里没有列举所有特性。when 的灵活、简洁，使得我在使用它的时候变得相当开心  
  
## 容器的操作符  
自从迷上 RxJava 之后，我实在很难再回到从前，这其中就有 RxJava 中许多方便的操作符。而 Kotlin 中，容器自身带有一系列的操作符，可以非常简洁的去实现一些逻辑。  
例如:  
```java  
var container = LinearLayout(this)  
(0 until container.childCount)  
        .map { container.getChildAt(it) }  
        .filter { it.visibility == View.GONE }  
        .forEach { it.visibility = View.VISIBLE }  
```  
  
上述代码首先创建了一个 0 到 `container.childCount - 1` 的区间；再用 `map` 操作符配合取出 child 的代码将这个 Int 的集合转化为了 childView 的集合；然后在用 `filter` 操作符对集合做筛选，选出 childView 中所有可见性为 GONE 的作为一个新的集合；最终 `forEach` 遍历把所有的 childView 都设置为 VISIBLE。  
这里再贴上 JAVA 的代码作为对比。  
```java  
for(int i = 0; i < container.childCount - 1;  i++) {  
    View childView = container.getChildAt(i);  
    if(childView.getVisibility() == View.GONE) {  
        childView.setVisibility(View.VISIBLE);  
    }  
}  
```  
  
## 线程切换，so easy  
既然上面提到了 RxJava，不得不想起 RxJava 的另一个优点——`线程调度`。Kotlin 中有一个专为 Android 开发量身打造的库，名为 `anko`，其中包含了许多可以简化开发的代码，其中就对线程进行了简化。  
```java  
async {  
    val response = URL("https://www.baidu.com").readText()  
    uiThread {  
        textView.text = response  
    }  
}  
```  
  
上面的代码很简单，通过 `async` 方法将代码实现在一个`异步线程`中，在读取到 http 请求的响应了之后，再通过 `uiThread` 方法切换回 `ui 线程`将 response 显示在 textView 上。  
  
抛开内部的实现，你再也不需要为了一个简简单单的异步任务去写一大堆的无效代码。  
  
## 一个关键字实现单例  
没错，就是一个关键字就可以实现单例：  
```java  
object Log {  
    fun i(string: String) {  
        println(string)  
    }  
}  
```  
```java  
fun main(args: Array<String>) {  
    Log.i("test")  
}  
```  
  
再见，单例模式  
  
## 自动 getter、setter 及 class 简洁声明  
```java  
class Person(var name: String)  
```  
```java  
val person = Person("张三");  
```  
  
还可以添加默认值：  
```java  
class Person(var name: String = "张三")  
```  
```java  
val person = Person()  
```  
  
再附上我项目中一个比较复杂的数据类：  
```java  
data class Column(  
        var subId: String?,  
        var subTitle: String?,  
        var subImg: String?,  
        var subCreatetime: String?,  
        var subUpdatetime: String?,  
        var subFocusnum: Int?,  
        var lastId: String?,  
        var lastMsg: String?,  
        var lastType: String?,  
        var lastMember: String?,  
        var lastTIme: String?,  
        var focus: String?,  
        var subDesc: String?,  
        var subLikenum: Int?,  
        var subContentnum: Int?,  
        var pushSet: String?  
)  
```  
  
一眼望去，没有多余代码。这是为什么我认为 Kotlin 代码比 JAVA 代码要更容易写得干净的原因之一。  
  
## DSL 式编程  
说起 dsl ，Android 开发者接触的最多的或许就是 gradle 了  
那么在 Android 项目的代码中使用 DSL 是一种什么样的感觉呢？  
```java  
override fun onCreate(savedInstanceState: Bundle?) {  
    super.onCreate(savedInstanceState)  
  
    val homeFragment = HomeFragment()  
    val columnFragment = ColumnFragment()  
    val mineFragment = MineFragment()  
  
    setContentView(  
            tabPages {  
                backgroundColor = R.color.white  
                dividerColor = R.color.colorPrimary  
                behavior = ByeBurgerBottomBehavior(context, null)  
  
                tabFragment {  
                    icon = R.drawable.selector_tab_home  
                    body = homeFragment  
                    onSelect { toast("home selected") }  
                }  
  
                tabFragment {  
                    icon = R.drawable.selector_tab_search  
                    body = columnFragment  
                }  
  
                tabImage {  
                    imageResource = R.drawable.selector_tab_photo  
                    onClick { showSheet() }  
                }  
  
                tabFragment {  
                    icon = R.drawable.selector_tab_mine  
                    body = mineFragment  
                }  
            }  
    )  
}  
```  
  
效果图  
![](index_files/95835c49-0799-4c0b-be57-8f82d93b927e.jpg)  
  
没错，上面的代码就是用来构建这个主界面的 `viewPager + fragments + tabBar` 的。以 tabPages 作为开始，设置背景色，分割线等属性；再用 `tabFrament` 添加 `fragment + tabButton`，`tabImage` 方法则只添加 `tabButton`。所见的代码都是在做配置，而具体的实现则被封装了起来。  
  
前面提到过 anko 这个库，其实也可以用来替代 xml 做布局用：  
```java  
override fun onCreate(savedInstanceState: Bundle?) {  
    super.onCreate(savedInstanceState)  
  
    verticalLayout {  
        textView {  
            text = "这是标题"  
        }.lparams {  
            width = matchParent  
            height = dip(44)  
        }  
  
        textView {  
            text = "这是内容"  
            gravity = Gravity.CENTER  
        }.lparams {  
            width = matchParent  
            height = matchParent  
        }  
    }  
}  
```  
  
相比于用 JAVA 代码做布局，这种 DSL 的方式也是在做配置，把布局的实现代码封装在了背后，和 xml 布局很接近。  
  
## 委托/代理，SharedPreference 不再麻烦  
通过 Kotlin 中的委托功能，我们能轻易的写出一个 SharedPreference 的代理类  
```java  
class Preference<T>(val context: Context, val name: String?, val default: T) : ReadWriteProperty<Any?, T> {  
    val prefs by lazy {  
        context.getSharedPreferences("xxxx", Context.MODE_PRIVATE)  
    }  
  
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = with(prefs) {  
        val res: Any = when (default) {  
            is Long -> getLong(name, 0)  
            is String -> getString(name, default)  
            is Float -> getFloat(name, default)  
            is Int -> getInt(name, default)  
            is Boolean -> getBoolean(name, default)  
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")  
        }  
        res as T  
    }  
  
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = with(prefs.edit()) {  
        when (value) {  
            is Long -> putLong(name, value)  
            is String -> putString(name, value)  
            is Float -> putFloat(name, value)  
            is Int -> putInt(name, value)  
            is Boolean -> putBoolean(name, value)  
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")  
        }.apply()  
    }  
}  
```  
  
暂且跳过原理，我们去看怎么使用  
```java  
class EntranceActivity : BaseActivity() {  
    private var userId: String by Preference(this, "userId", "")  
  
    override fun onCreate(savedInstanceState: Bundle?) {  
        testUserId()  
    }  
  
    fun testUserId() {  
        if (userId.isEmpty()) {  
            println("userId is empty")  
            userId = "default userId"  
        } else {  
            println("userId is $userId")  
        }  
    }  
}  
```  
  
重复启动 app 输出结果：  
```  
userId is empty  
userId is default userId  
userId is default userId  
```  
  
第一次启动 app 的时候从 SharedPreference 中取出来的 userId 是空的，可是后面却不为空。由此可见，`userId = "default userId"` 这句代码成功的将 SharedPreference 中的值修改成功了。  
也就是说，在这个 Preference 代理的帮助下，SharedPreference 存取操作变得和普通的对象调用、赋值一样的简单。  
  
## 扩展，和工具类说拜拜  
很久很久以前，有人和我说过，工具类本身就是一种违反`面向对象思想`的东西。可是当时我就想了，你不让我用工具类，那有些代码我该怎么写呢？直到我知道了扩展这个概念，我才豁然开朗。  
```java  
fun ImageView.displayUrl(url: String?) {  
    if (url == null || url.isEmpty() || url == "url") {  
        imageResource = R.mipmap.ic_launcher  
    } else {  
        Glide.with(context)  
                .load(ColumnServer.SERVER_URL + url)  
                .into(this)  
    }  
}  
```  
```java  
imageView.displayUrl(url)  
```  
  
上述代码可理解为：  
我给 ImageView 这个类扩展了一个名为 displayUrl 的方法，这个方法接收一个名为 url 的 `String?`类对象。如不出意外，会通过 Glide 加载这个 url 的图片，显示在当前的 imageView 上；  
  
我在另一个地方通过 findViewById 拿到了一个 ImageView 类的实例，然后调用这个 imageView 的displayUrl 方法，试图加载我传入的 url  
  
通过扩展来为 ImageView 添加方法，相比于通过继承 ImageView 来写一个 CustomImageView，再添加方法而言，侵入性更低，不需要在代码中全写 CustomImageView，也不需要在 xml 布局中将包名写死，造成移植的麻烦。  
  
这事用工具类当然也可以做，比如做成 ImageUtil.displayUrl(imageView, url)，但是工具类阅读起来并没有扩展出来的方法读起来更自然更流畅。  
扩展是 Kotlin 相比于 JAVA 的一大杀器  
  
## 向 findViewById 说 NO  
不同于 JAVA 中，在 Kotlin 中 findViewById 本身就简化了很多，这得益于 Kotlin 的类型推断以及转型语法后置：  
```java  
val onlyTv = findViewById(R.id.onlyTv) as TextView  
```  
  
很简洁，但若仅仅是这样，想必大家会喷死我：就这么点差距也拿出来搞事？  
当然不是。在官方库 anko 的支持下，这事又有了很多变化。  
例如  
```java  
val onlyTv: TextView = find(R.id.onlyTv)  
```  
  
肯定有人会问：find 是个什么鬼？  
让我们点过去看看 find 的源码：  
```java  
inline fun <reified T : View> Activity.find(id: Int): T = findViewById(id) as T  
```  
  
忽略掉其他细节，原来和我们上面第一种写法没差别嘛，不就是用一个扩展方法给 Activity 加了这么一个方法，帮我们写了 findViewById，再帮我们转型了一下嘛。  
  
其实 Kotlin 中还有很多令人乍舌的实现其实都是在一些`基础特性的组合`之上实现的  
  
然而，上面这些都不是终极武器！  
在 `anko` 的帮助下，你只需要根据布局的 id 写一句 `import` 代码，然后你就可以把布局中的 id 作为 view 对象的名称直接使用。不仅 activity 中可以这样玩，你甚至可以 `viewA.viewB.viewC`，所以大可不必担心 adapter 中应当怎么写。  
```java  
import kotlinx.android.synthetic.main.activity_main.*  
onlyTv.text="不需要声明直接可以用"  
```  
  
没有 findViewById，也就减少了空指针；没有 cast，则几乎不会有类型转换异常。  
  
也许有的朋友会发现这和 Google 出品的 databinding 实在是有异曲同工之妙，那如果我告诉你，databinding 库本身就有对 kotlin 的依赖呢？  
  
## 简单粗暴的 startActivity  
我们原本大都是这样子来做 Activity 跳转的：  
```java  
Intent intent = new Intent(LoginActivity.this, MainActivity.class);  
startActivity(intent);  
```  
  
为了 startActivity，我不得不 new 一个 Intent 出来，特别是当我要传递参数的时候：  
```java  
Intent intent = new Intent(LoginActivity.this, MainActivity.class);  
intent.putExtra("name", "张三");  
intent.putExtra("age", 27);  
startActivity(intent);  
```  
  
不知道大家有木有累觉不爱？  
在 anko 的帮助下，startActivity 是这样子的：  
```java  
startActivity<MainActivity>()  
startActivity<MainActivity>("name" to "张三", "age" to 27)  
startActivityForResult<MainActivity>(101, "name" to "张三", "age" to 27)  
```  
  
无参情况下，只需要在调用 startActivity 的时候`加一个 Activity 的 Class 泛型来告知要到哪去`。有参也好说，这个方法支持你传入 `vararg params: Pair<String, Any>`  
  
有没有觉得代码写起来、读起来流畅了许多？  
  
## 玲珑小巧的 toast  
JAVA 中写一个 toast 大概是这样子的：  
```java  
Toast.makeText(context, "this is a toast", Toast.LENGTH_SHORT).show();  
```  
  
不得不说真的是又臭又长，虽然确实是有很多考量在里面，但是对于使用来说实在是太不便利了，而且还很容易忘记最后一个 show()。我敢说没有任何一个一年以上的 Android 开发者会不去封装一个 ToastUtil 的。  
  
封装之后大概会是这样：  
```java  
ToastUtil.showShort(context, "this is a toast");  
```  
  
如果处理一下 context 的问题，可以缩短成这样：  
```java  
ToastUtil.showShort("this is a toast");  
```  
  
有那么一点极简的味道了对吧？  
  
好了，是时候让我们看看 anko 是怎么做的了：  
```java  
context.toast("this is a toast")  
```  
  
如果当前已经是在 context 上下文中（比如 activity）:  
```java  
toast("this is a toast")  
```  
  
如果你是想要一个长时间的 toast：  
```java  
longToast("this is a toast")  
```  
  
没错，就是`给 Context 类扩展了 toast 和 longToast 方法`，用屁股想都知道里面干了什么。只是这样一来比任何工具类都来得更简洁更直观。  
  
## 用 apply 方法进行数据组合  
假设有如下 A、B、C 三个 class：  
```java  
class A(val b: B)  
class B(val c: C)  
class C(val content: String)  
```  
  
可以看到，A 中有 B，B 中有 C。在实际开发的时候，我们有的时候难免会遇到比这个更复杂的数据，嵌套层级很深。这种时候，用 JAVA 初始化一个 A 类数据会变成一件非常痛苦的事情。例如：  
```java  
C c = new C("content");  
// 设置 c 的属性  
B b = new B(c);  
// 设置 b 的属性  
A a = new A(b);  
// 设置 a 的属性  
```  
  
这还是 A、B、C 的关系很单纯的情况下，如果有大量数据进行组合，那么我们会需要初始化大量的对象进行赋值、修改等操作。如果我描述的不够清楚的话，大家不妨想一想用 JAVA 代码布局是一种什么样的感觉？  
  
当然，在 JAVA 中也是有解决方案的，比如 Android 中常用的 Dialog，就用了 `Builder` 模式来进行相应配置。（说到这里，其实用 Builder 模式基本上也可以说是 JAVA 语言的 DSL）。但是在更为复杂的情况下，即便是有设计模式的帮助，也很难保证代码的可读性。那么 Kotlin 有什么好方法，或者说小技巧来解决这个问题吗？  
  
Kotlin 中有一个名为 `apply` 的方法，它的源码是这样子的：  
```java  
@kotlin.internal.InlineOnly  
public inline fun <T> T.apply(block: T.() -> Unit): T { block(); return this }  
```  
  
没有 Kotlin 基础的小伙伴看到这里一定会有点晕。我们先忽略一部分细节，把关键的信息提取出来，再改改格式看看：  
```java  
public fun <T> T.apply(block: T.() -> Unit): T {  
    block()  
    return this  
}  
```  
  
1、首先，我们可以看出 T 是一个泛型，而且后面没有给 T 增加约束条件，那么这里的 T 可以理解为：我这是在`给所有类扩展一个名为 apply 的方法`；  
2、第一行最后的: T 表明，我`最终是要返回一个 T 类`。我们也可以看到方法内部最后的 `return this` 也能说明，其实最后我就是要`返回调用方法的这个对象自身`；  
3、在 return this 之前，我执行了一句 `block()`，这意味着 block 本身一定是一个方法。我们可以看到，apply 方法接收的 block 参数的类型有点特殊，不是 String 也不是其他什么明确的类型，而是 `T.() -> Unit` ；  
4、`T.() -> Unit` 表示的意思是：这是一个 `上下文在 T 对象中`、`返回一个 Unit 类对象`的方法。由于 Unit 和 JAVA 中的 Void 一致，所以可以理解为`不需要返回值`。那么这里的 block 的意义就清晰起来了：一个执行在 T，即调用 apply 方法的对象自身当中，又不需要返回值的方法。  
  
有了上面的解析，我们再来看一下这句代码：  
```java  
val textView = TextView(context).apply {  
    text = "这是文本内容"  
    textSize = 16f  
}  
```  
这句代码就是初始化了一个 TextView，并且在将它赋值给 textView 之前，将自己的文本、字体大小修改了。  
  
或许你会觉得这和 JAVA 比起来并没有什么优势。别着急，我们慢慢来：  
```java  
layout.addView(TextView(context).apply {  
    text = "这是文本内容"  
    textSize = 16f  
})  
```  
  
这样又如何呢？  
我并`不需要声明一个变量或者常量来持有这个对象才能去做修改操作`(类似匿名内部类)！  
  
上面的A、B、C 问题用 Kotlin 来实现是可以这么写的：  
```java  
val a = A().apply {  
    b = B().apply {  
        c = C("content")  
    }  
}  
```  
  
我只声明了一个 a 对象，然后初始化了一个 A，在这个初始化的对象中先给 B 赋值，然后再提交给了 a。B 中的 C 也是如此。  
  
说到底，这个小技巧也就是 `扩展方法 + 高阶函数` 两个特性组合在一起实现的效果。  
  
## 利用高阶函数搞事情  
先看代码  
```java  
inline fun debug(code: () -> Unit) {  
    if (BuildConfig.DEBUG) {  
        code()  
    }  
}  
```  
  
```java  
// Application 中  
debug {  
    Timber.plant(Timber.DebugTree())  
}  
```  
  
上述代码是先定义了一个`全局的名为 debug 的方法`，这个方法接收一个方法作为参数，命名为 code。然后在方法体内部，我先判断当前是不是 DEBUG 版本，如果是，再调用传入的 code 方法。  
而后我们在 Application 中，`debug方法` 就成为了依据条件执行代码的`关键字`。仅当 DEBUG 版本的时候，我才初始化 Timber 这个日志库。  
  
如果这还不够体现有点的话，那么可以再看看下面一段：  
```java  
supportsLollipop {  
    window.statusBarColor = Color.TRANSPARENT  
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE  
}  
```  
  
当系统版本在 Lollipop 之上时才去做沉浸式状态栏。系统 api 经常会有版本的限制，相对于一个 supportsLollipop `关键字`， 我想一定不是所有人都希望每次都去写：  
```java  
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  
    // do something  
}  
```  
  
诸如此类的场景和可以自创的 `关键字/代码块` 还有很多。  
例如：  
```java  
inline fun handleException(code : () -> Unit) {  
    try {  
        code()  
    } catch (e : Exception) {  
        e.printStackTrace()  
    }  
}  
```  
  
```java  
handleException {  
     println(Integer.parseInt("这明显不是数字"))  
}  
```  
  
虽然大都可以用 `if(xxxxUtil.isxxxx())` 来凑合，但是既然有了更好的方案，那还何必凑合呢？  
  
## 用扩展方法替代工具类  
曾几何时，我做字符串判断的时候一定会写一个工具类，在这个工具类里充斥着各种各样的判断方法。而在 Kotlin 中，可以用扩展方法来替代。下面是我项目中 `String 扩展方法`的一部分：  
```java  
fun String.isPassword(): Boolean {  
    return length in 6..12  
}  
  
fun String.isNumber(): Boolean {  
    val regEx = "^-?[0-9]+$"  
    val pat = Pattern.compile(regEx)  
    val mat = pat.matcher(this)  
  
    return mat.find()  
}  
```  
```java  
println("123abc".isPassword())  
println("123456".isNumber())  
```  
  
## 自动 getter、setter 使得代码更精简  
以 TextView 举例，JAVA 代码中获取文本、设置文本的代码分别为：  
```java  
String text = textView.getText().toString();  
textView.setText("new text");  
```  
  
Kotlin 中是这样写的:  
```java  
val text = textView.text  
textView.text = "new text"  
```  
  
如果 TextView 是一个原生的 Kotlin class，那么是没有 getText 和 setText 两个方法的，而是一个 text 属性。尽管此处的TextView 是 JAVA class，源码中有getText 和 setText 两个方法，Kotlin 也做了类似映射的处理。当这个 text 属性在等号右边的时候，就是在提取 text 属性（此处映射为 getText）;当在等号左边的时候，就是在赋值（setText）。  
  
说到这里我又想起了上一篇文章中提到的 Preference 代理，其实也有一定关联，那就是当一个属性在等号左边和右边的时候，不同于 JAVA 中一定是赋值操作，在 Kotlin 中则有可能会触发一些别的。  
  
2018-12-31  
