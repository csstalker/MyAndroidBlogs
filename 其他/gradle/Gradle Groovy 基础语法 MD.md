| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Gradle Groovy 基础语法 MD  
***  
目录  
===  

- [Groovy 基础](#groovy-基础)
	- [为何要学 Groovy](#为何要学-groovy)
	- [为何要使用 Groovy](#为何要使用-groovy)
	- [如何编译运行 Groovy](#如何编译运行-groovy)
	- [最基本的语法](#最基本的语法)
	- [支持的数据类型](#支持的数据类型)
		- [String](#string)
		- [闭包](#闭包)
		- [List和Map](#list和map)
		- [闭包的参数](#闭包的参数)
		- [加强的IO](#加强的io)
		- [访问xml文件](#访问xml文件)
	- [其他的一些语法特性](#其他的一些语法特性)
		- [Getter和Setter](#getter和setter)
		- [构造器](#构造器)
		- [Class类型](#class类型)
		- [使用with()操作符](#使用with操作符)
		- [判断是否为真](#判断是否为真)
		- [简洁的三元表达式](#简洁的三元表达式)
		- [捕获任何异常](#捕获任何异常)
		- [简洁的非空判断](#简洁的非空判断)
		- [使用断言](#使用断言)
		- [==和equals](#和equals)
		- [switch方法](#switch方法)
		- [字符串分行](#字符串分行)
		- [Import 别名](#import-别名)
  
# Groovy 基础  
[groovy 文档](http://docs.groovy-lang.org/latest/html/groovy-jdk/index-all.html)  
[groovy api 文档](http://www.groovy-lang.org/api.html)  
[参考1](https://blog.csdn.net/singwhatiwanna/article/details/76084580)  
[参考2](https://www.jianshu.com/p/ba55dc163dfd)  
  
## 为何要学 Groovy  
Gradle 是目前Android主流的构建工具，不管你是通过命令行还是通过AndroidStudio来build，最终都是通过Gradle来实现的。所以学习Gradle非常重要。  
  
目前国内对Android领域的探索已经越来越深，不少技术领域如`插件化、热修复、构建系统`等都对Gradle有迫切的需求，不懂Gradle将无法完成上述事情。所以Gradle必须要学习。  
  
Gradle不单单是一个配置脚本，它的背后是几门语言：  
- Groovy Language  
- Gradle DSL  
- Android DSL  
  
> DSL的全称是`Domain Specific Language`，即领域特定语言，或者直接翻译成“特定领域的语言”，再直接点，其实就是这个语言不通用，只能用于特定的某个领域，俗称“小语言”。因此DSL也是语言。  
  
实际上，Gradle脚本大多都是使用groovy语言编写的。  
  
Groovy是一门jvm语言，功能比较强大，细节也很多，全部学习的话比较耗时，对我们来说收益较小，并且玩转Gradle并不需要学习Groovy的全部细节，所以其实我们只需要学一些Groovy基础语法与API即可。  
  
## 为何要使用 Groovy  
Groovy是一种基于JVM的敏捷开发语言，它结合了众多脚本语言的强大的特性，由于同时又能与Java代码很好的结合。一句话：既有面向对象的特性又有纯粹的脚本语言的特性。  
  
由于Groovy运行在JVM上，因此也可以使用Java语言编写的组建。  
  
简单来说，Groovy提供了`更加灵活简单的语法`，`大量的语法糖`以及`闭包`特性可以让你`用更少的代码来实现和Java同样的功能`。  
  
## 如何编译运行 Groovy  
Groovy是一门jvm语言，它最终是要编译成class文件然后在jvm上执行，所以`Java语言的特性Groovy都支持`，Groovy支持99%的java语法，我们完全可以在Groovy代码中直接粘贴java代码。  
  
可以安装`Groovy sdk`来编译和运行。但是我并不想搞那么麻烦，毕竟我们的最终目的只是学习Gradle。  
  
推荐大家通过这种方式来编译和运行Groovy。  
  
在当面目录下创建`build.gradle`文件，在里面创建一个`task`，然后在task中编写Groovy代码即可，如下所示：  
```groovy  
task(testGroovy).doLast {  
   println "开始运行自定义task"  
   test()  
}  
  
def test() {  
   println "执行Groovy语法的代码"  
   System.out.println("执行Java语法的代码!");  
}  
```  
  
然后在命令行终端中执行如下命令即可：  
```  
gradle testGroovy  
```  
```  
> Configure project :app   
  
> Task :app:testGroovy   
开始运行自定义task  
执行Groovy语法的代码  
执行Java语法的代码!  
  
BUILD SUCCESSFUL in 3s  
1 actionable task: 1 executed  
```  
  
我们知道，在Android项目中，我们只要更改`build.gradle`文件一点内容，AS就会提示我们同步：  
![](index_files/3baaa02c-addf-4772-949e-739cf8c80649.png)  
但是在我们测试 Groovy 时中，我们更改`build.gradle`文件后可以不必去同步，执行命令时会自动执行你修改后的最新逻辑。  
  
## 最基本的语法  
Groovy中的类和方法默认都是public权限的，所以我们可以省略public关键字，除非我们想使用private。  
Groovy中的类型是弱化的，所有的`类型都可以动态推断`，但是Groovy仍然是`强类型的语言`，类型不匹配仍然会报错。  
Groovy中通过 `def` 关键字来声明`变量和方法`。  
Groovy中很多东西都是可以省略的，比如  
- 语句后面的分号是可以省略的  
- def 和 变量的类型 中的其中之一是可以省略的(不能全部省略)  
- def 和 方法的返回值类型 中的其中之一是可以省略的(不能全部省略)  
- 方法调用时的圆括号是可以省略的  
- 方法声明中的参数类型是可以省略的  
- 方法的最后一句表达式可作为返回值返回，而不需要return关键字  
> 省略return关键字并不是一个好的习惯，就如同 if else while 后面只有一行语句时可以省略大括号一样，以后如果添加了其他语句，很有可能会导致逻辑错误  
  
```groovy  
def int a = 1; //如果 def 和 类型同时存在，IDE 会提示你"def是不需要的(is unnecessary)"  
def String b = "hello world" //省略分号，存在分号时也会提示你 unnecessary  
def c = 1 //省略类型  
  
def hello() {  //省略方法声明中的返回值类型  
   println ("hello world");  
   println "hello groovy" //省略方法调用时的圆括号  
   return 1;  
}  
  
def hello(String msg) {  
   println "hello" + msg //省略方法调用时的圆括号  
   1;                    //省略return  
}  
  
int hello(msg) { //省略方法声明中的参数类型  
   println msg  
   return 1 // 这个return不能省略  
   println "done" //这一行代码是执行不到的，IDE 会提示你 Unreachable statement，但语法没错  
}  
```  
  
## 支持的数据类型  
在Groovy中，数据类型有：  
- Java中的基本数据类型  
- Java中的对象  
- `Closure（闭包）`  
- 加强的List、Map等集合类型  
- 加强的File、Stream等IO类型  
  
类型可以显示声明，也可以用 def 来声明，用 def 声明的类型Groovy将会进行类型推断。  
基本数据类型和对象和Java中的一致，只不过在Gradle中，`对象默认的修饰符为public`。  
  
### String  
String的特色在于字符串的拼接，比如  
```groovy  
def a = 1  
def b = "hello"  
def c = "a=${a}, b=${b}"  
println c //a=1, b=hello  
```  
  
### 闭包  
Groovy中有一种特殊的类型，叫做`Closure`，翻译过来就是闭包，这是一种类似于C语言中`函数指针`的东西。  
闭包用起来非常方便，在Groovy中，闭包作为一种特殊的`数据类型`而存在，`闭包可以作为方法的参数和返回值，也可以作为一个变量而存在`。  
闭包可以有`返回值和参数`，当然也可以没有。下面是几个具体的例子：  
```groovy  
def test() {  
    def closure = { String parameters -> //闭包的基本格式  
        println parameters  
    }  
    def closure2 = { a, b -> // 省略了闭包的参数类型  
        println "a=${a}, b=${b}"  
    }  
    def closure3 = { a ->  
        a + 1 //省略了return  
    }  
    def closure4 = { // 省略了闭包的参数声明  
        println "参数为 ${it}" //如果闭包不指定参数，那么它会有一个隐含的参数 it  
    }  
  
    closure("包青天") //包青天  
    closure2 10086, "包青天" //a=10086, b=包青天  
    println closure3(1) //2  
    //println closure3 2 //不允许省略圆括号，会提示：Cannot get property '1' on null object  
    closure4() //参数为 null  
    closure4 //不允许省略圆括号，但是并不会报错  
    closure4 10086 //参数为 10086  
}  
```  
  
闭包的一个难题是如何确定闭包的参数(包括参数的个数、参数的类型、参数的意义)，尤其当我们调用Groovy的API时，这个时候没有其他办法，只有查询Groovy的文档才能知道。  
  
### List和Map  
Groovy加强了Java中的集合类，比如List、Map、Set等。  
  
基本使用如下：  
```groovy  
def emptyList = []  
def list = [10086, "hello", true]  
list[1] = "world"  
assert list[1] == "world"  
println list[0] //10086  
list << 5 //相当于 add()  
assert 5 in list // 调用包含方法  
println list //[10086, world, true, 5]  
```  
```groovy  
def range = 1..5  
assert 2 in range  
println range //1..5  
println range.size() //5  
```  
```groovy  
def emptyMap = [:]  
def map = ["id": 1, "name": "包青天"]  
map << [age: 29] //添加元素  
map["id"] = 10086 //访问元素方式一  
map.name = "哈哈" //访问元素方式二，这种方式最简单  
println map //{id=10086, name=哈哈, age=29}  
```  
  
可以看到，通过Groovy来操作List和Map显然比Java简单的多。  
  
上面有一个看起来很奇怪的操作符`<<`，其实这并没有什么大不了，`<<`表示`向List中添加新元素`的意思，这一点从 [List文档](http://docs.groovy-lang.org/latest/html/groovy-jdk/java/util/List.html) 当也能查到。  
  
```groovy  
public List leftShift(Object value)  
```  
- Overloads the left shift operator to provide an easy way to append objects to a List. 重载`左移位运算符`，以提供将对象`append`到List的简单方法。  
- Parameters: value - an Object to be added to the List.  
- Returns: same List, after the value was added to it.  
  
实际上，这个运算符是大量使用的，并且当你用 `leftShift` 方法时 IDE 也会提示你让你使用`左移位`运算符`<<`替换：  
![](index_files/39791b5e-c17b-44b8-b7c7-d1d377798533.png)  
  
```groovy  
def list = [1, 2]  
list.leftShift 3  
assert list == [1, 2, 3]  
list << 4  
println list //[1, 2, 3, 4]  
```  
  
### 闭包的参数  
这里借助Map再讲述下如何确定闭包的参数。比如我们想遍历一个Map，我们想采用Groovy的方式，通过查看文档，发现它有如下两个方法，看起来和遍历有关：  
- `Map each(Closure closure)`：Allows a Map to be iterated through using a closure.  
- `Map eachWithIndex(Closure closure)`：Allows a Map to be iterated through using a closure.  
  
可以发现，这两个each方法的参数都是一个闭包，那么我们如何知道闭包的参数呢？当然不能靠猜，还是要查文档。  
```groovy  
public Map each(Closure closure)  
```  
- Allows a Map to be iterated through using a closure. If the closure takes `one parameter` then it will be passed the `Map.Entry` otherwise if the closure takes `two parameters` then it will be passed `the key and the value`.  
- In general, the order in which the map contents are processed cannot be guaranteed(通常无法保证处理元素的顺序). In practise(在实践中), specialized forms of Map(特殊形式的Map), e.g. a `TreeMap` will have its contents processed according to the natural ordering(自然顺序) of the map.  
  
```groovy  
def result = ""  
[a:1, b:3].each { key, value -> result += "$key$value" } //两个参数  
assert result == "a1b3"  
```  
```groovy  
def result = ""  
[a:1, b:3].each { entry -> result += entry } //一个参数  
assert result == "a=1b=3"  
  
[a: 1, b: 3].each { println "[${it.key} : ${it.value}]" } //一个隐含的参数 it，key 和 value 是属性名  
```  
  
试想一下，如果你不知道查文档，你又怎么知道each方法如何使用呢？光靠从网上搜，API文档中那么多接口，搜的过来吗？记得住吗？  
  
### 加强的IO  
在Groovy中，文件访问要比Java简单的多，不管是普通文件还是xml文件。怎么使用呢？查来 [File文档](http://docs.groovy-lang.org/latest/html/groovy-jdk/java/io/File.html)。  
  
```groovy  
public Object eachLine(Closure closure)  
```  
- Iterates through this file line by line. Each line is passed to the given 1 or 2 arg closure. The file is read using a reader which is closed before this method returns.  
- Parameters: closure - a closure (arg 1 is line, optional arg 2 is line number starting at line 1)  
- Returns: the last value returned by the closure  
  
可以看到，eachLine方法也是支持1个或2个参数的，这两个参数分别是什么意思，就需要我们学会读文档了，一味地从网上搜例子，多累啊，而且很难彻底掌握：  
```groovy  
def file = new File("a.txt")  
file.eachLine { line, lineNo ->  
    println "${lineNo} ${line}" //行号，内容  
}  
  
file.eachLine { line ->  
    println "${line}" //内容  
}  
```  
  
除了eachLine，File还提供了很多Java所没有的方法，大家需要浏览下大概有哪些方法，然后需要用的时候再去查就行了，这就是学习Groovy的正道。  
  
### 访问xml文件  
Groovy访问xml有两个类：`XmlParser`和`XmlSlurper`，二者几乎一样，在性能上有细微的差别，不过这对于本文不重要。  
`groovy.util.XmlParser`的 [API文档](http://docs.groovy-lang.org/docs/latest/html/api/)  
  
文档中的案例：  
```groovy  
def xml = '<root><one a1="uno!"/><two>Some text!</two></root>'  
//或者 def xml = new XmlParser().parse(new File("filePath.xml"))  
def rootNode = new XmlParser().parseText(xml) //根节点  
assert rootNode.name() == 'root' //根节点的名称  
assert rootNode.one[0].@a1 == 'uno!' //根节点中的子节点 one 的 a1 属性的值  
assert rootNode.two.text() == 'Some text!'  //根节点中的子节点 two 的内容  
rootNode.children().each { assert it.name() in ['one','two'] }  
```  
更多的细节查文档即可。  
  
## 其他的一些语法特性  
### Getter和Setter  
当你在Groovy中创建一个beans的时候，通常我们称为POGOS(Plain Old Groovy Objects)，Groovy会自动帮我们创建`getter/setter`方法。  
当你对`getter/setter`方法有特殊要求，你尽可提供自己的方法，Groovy默认的`getter/setter`方法会被替换。  
  
### 构造器  
有一个bean  
```groovy  
class Server {  
    String name  
    Cluster cluster  
}  
```  
初始化一个实例的时候你可能会这样写：  
```groovy  
def server = new Server()  
server.name = "Obelix"  
server.cluster = aCluster  
```  
  
其实你可以用带命名的参数的默认构造器，会大大减少代码量：  
```groovy  
def server = new Server(name: "Obelix", cluster: aCluster)  
```  
### Class类型  
在Groovy中Class类型的`.class`后缀不是必须的，比如：  
```groovy  
def func(Class clazz) {  
    println clazz  
}  
func(File.class) //class java.io.File  
func(File) //class java.io.File  
```  
  
### 使用with()操作符  
当更新一个实例的时候，你可以使用with()来省略相同的前缀，比如：  
```groovy  
Book book = new Book()   
book.with {  
   id = 1 //等价于 book.id = 1  
   name = "包青天"  
   start(10086)  
   stop("包青天")  
}  
```  
  
### 判断是否为真  
所有类型都能转成布尔值，比如`null`和`void`相当于`0`或者相当于`false`，其他则相当于`true`，所以：  
```groovy  
if (name) {}  
//等价于  
if (name != null && name.length > 0) {}  
```  
在Groovy中可以在类中添加`asBoolean()`方法来`自定义是否为真`。  
  
### 简洁的三元表达式  
在Groovy中，三元表达式可以更加简洁，比如：  
```groovy  
def result = name ?: ""  
//等价于  
def result = name != null ? name : ""  
```  
  
### 捕获任何异常  
如果你实在不想关心`try`块里抛出何种异常，你可以简单的捕获所有异常，并且可以省略异常类型：  
```groovy  
try {  
    // ...  
} catch (any) { //可以省略异常类型  
    // something bad happens  
}  
```  
这里的any并不包括`Throwable`，如果你真想捕获everything，你必须明确的标明你想捕获Throwable  
  
### 简洁的非空判断  
在java中，你要获取某个对象的值必须要检查是否为null，这就造成了大量的`if`语句；在Groovy中，非空判断可以用`?.`表达式，比如：  
```groovy  
println order?.customer?.address  
//等价于  
if (order != null) {  
   if (order.getCustomer() != null) {  
       if (order.getCustomer().getAddress() != null) {  
           System.out.println(order.getCustomer().getAddress());  
       }  
   }  
}  
```  
  
### 使用断言  
在Groovy中，可以使用assert来设置断言，`当断言的条件为false时，程序将会抛出异常`：  
```groovy  
def check(String name) {  
   assert name // 检查方法传入的参数是否为空，name non-null and non-empty according to Groovy Truth  
   assert name?.size() > 3  
}  
```  
  
### ==和equals  
Groovy里的`is()`方法等同于Java里的`==`。  
Groovy中的`==`是更智能的`equals()`，比较两个类的时候，你应该使用`a.is(b)`而不是`==`。  
Groovy中的`==`可以自动避免NullPointerException异常  
```groovy  
status == "包青天"  
//等价于Java中的  
status != null && status.equals("包青天")  
```  
  
### switch方法  
在Groovy中，switch方法变得更加灵活，可以同时支持更多的参数类型：  
```groovy  
def x = null  
def result = ""  
switch (x) {  
    case "foo": result = "found foo" //没有 break 时会继续向下判断  
    case "bar": result += "bar"  
        break  
    case [4, 5, 6]: result = "list" //匹配集合中的元素  
        break  
    case 12..30: result = "range" //匹配某个范围内的元素  
        break  
    case Integer: result = "integer" //匹配Integer类型  
        break  
    case { it > 3 }: result = "number > 3" //匹配表达式  
        break  
    case Number: result = "number" //匹配Number类型  
        break  
    default: result = "default"  
}  
println result  
```  
  
### 字符串分行  
Java中，字符串过长需要换行时我们一般会这样写：  
```groovy  
throw new PluginException("Failed to execute command list-applications:" +  
    " The group with name " +  
    parameterMap.groupname[0] +  
    " is not compatible group of type " +  
    SERVER_TYPE_NAME)  
```  
  
Groovy中你可以用 `\` 字符，而不需要添加一堆的双引号：  
```groovy  
throw new PluginException("Failed to execute command list-applications: \  
The group with name ${parameterMap.groupname[0]} \  
is not compatible group of type ${SERVER_TYPE_NAME}")  
```  
  
或者使用多行字符串`"""`:  
```groovy  
throw new PluginException("""Failed to execute command list-applications:  
    The group with name ${parameterMap.groupname[0]}  
    is not compatible group of type ${SERVER_TYPE_NAME)}""")  
```  
  
Groovy中，`单引号`引起来的字符串是java字符串，不能使用占位符来替换变量，`双引号`引起的字符串则是java字符串或者Groovy字符串。  
  
### Import 别名  
在java中使用两个类名相同但包名不同的两个类，像`java.util.List`和`java.wt.List`，你必须使用完整的包名才能区分。Groovy中则可以使用import别名：  
```groovy  
import java.util.List as jurist //使用别名  
import java.awt.List as aList  
import java.awt.WindowConstants as WC  
import static pkg.SomeClass.foo //静态引入方法  
```  
  
2019-1-12  
