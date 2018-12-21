| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
protobuf Protocol Buffers 简介 案例  
***  
目录  
===  

- [简介](#简介)
	- [优缺点](#优缺点)
	- [proto3简介](#proto3简介)
- [使用步骤](#使用步骤)
	- [下载 protobuf 编译器](#下载-protobuf-编译器)
	- [编写 .proto 文件](#编写-proto-文件)
	- [编译 .proto 文件生成 java 类](#编译-proto-文件生成-java-类)
	- [项目中添加 protobuf runtime](#项目中添加-protobuf-runtime)
	- [测试 java 类](#测试-java-类)
- [在 Android 中使用 Java Lite runtime](#在-android-中使用-java-lite-runtime)
  
# 简介  
[案例](https://github.com/baiqiantao/ProtocolBufferTest.git)  
[GitHub](https://github.com/google/protobuf)     
[在Java中使用](https://github.com/google/protobuf/tree/master/java)     
[官网--需翻墙](https://developers.google.com/protocol-buffers/)     
  
- `Protocol Buffers` (a.k.a., `protobuf`) 是Google开发的一种用于序列化`结构化数据`(比如Java中的Object，C中的Structure)的`语言中立、平台中立、可扩展`的`数据描述语言`，可用于数据存储、通信协议等方面。  
- Protocol Buffers可以理解为是更快、更简单、更小的JSON或者XML，区别在于Protocol Buffers是`二进制格式`，而JSON和XML是`文本格式`。  
- 目前 protobuf 支持的语言包括：`C++、C#、Java、JS、OC、PHP、Ruby`这七种。  
  
## 优缺点  
**Protobuf 相对于 XML 的优点**  
- 简洁，具有更少的歧义性  
- 体积小，消息大小只需要XML的`1/10 ～ 1/3`  
- 速度快，解析速度比XML快`20 ～ 100`倍  
- 自动生成数据访问类，方便应用程序的使用  
- 更好的兼容性，能够很好的支持向下或向上兼容  
  
> 注意：protobuf传输的是`对象的二进制内容`，而非编译前的 `.proto文件`，更非编译后的`Java源文件`，.proto文件只是为了方便阅读、调试和编辑用的。而json和xml传输的就是你看到的json和xml`文本内容`(字符串)  
  
**Protobuf 的优点**  
- Protobuf 有如 XML，不过它`更小、更快、也更简单`。你可以定义自己的数据结构，然后使用`代码生成器`生成的代码来读写这个数据结构。你甚至可以在无需重新部署程序的情况下更新数据结构。只需使用 Protobuf 对数据结构进行一次描述，即可利用各种不同语言或从各种不同数据流中对你的结构化数据轻松读写。  
- 它有一个非常棒的特性，即`向后兼容性好`，人们不必破坏已部署的、依靠“老”数据格式的程序就可以对数据结构进行升级。这样您的程序就可以不必担心因为消息结构的改变而造成的大规模的代码重构或者迁移的问题。因为添加新的消息中的 field 并不会引起已经发布的程序的任何改变。  
- Protobuf 语义更清晰，无需类似 XML `解析器`的东西，因为 Protobuf `编译器`会将 .proto 文件编译生成对应的`数据访问类`以对 Protobuf 数据进行`序列化、反序列化`操作。  
- 使用 Protobuf 无需学习复杂的文档对象模型，Protobuf 的编程模式比较友好，简单易学，同时它拥有良好的文档和示例，对于喜欢简单事物的人们而言，Protobuf 比其他的技术更加有吸引力。  
  
**Protobuf 的不足**  
- Protbuf 与 XML 相比也有不足之处。它功能简单，`无法用来表示复杂的概念`。  
- XML 已经成为多种行业标准的编写工具，Protobuf 只是 Google 公司内部使用的工具，在`通用性`上还差很多。  
- Protobuf 不适合用来描述一个基于文本的标记型文档（比如HTML），因为你无法轻易的交错文本的结构。另外，XML具有很好的可读性和可编辑性，而protocol buffers，至少在它们的原生形式上并不具备这个特点。XML同时也是可扩展、自描述的，而一个protocol buffer只有在具有message 定义(在.proto文件中定义)时才会有意义。  
  
个人觉得在 Android 应用中，protocol buffer 有一个**致命的缺点**，那就是生成的 java 文件太大，会是我们的 apk 体积增大很多！例如，在我之前做过的 ogod 项目中，由pb生成的十几个Java类，体积竟然有几 MB ，其大小几乎等于其余四百多个类的总和。  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/eEbIHGk40b.png?imageslim)  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/ab5am99Bf6.png?imageslim)  
  
## proto3简介  
我们最新的版本`version 3 alpha release`引进了一个新的语言版本--`Protocol Buffers version 3` (称之为`proto3`)，它在我们现存的语言版本(proto2)上引进了一些新特性。proto3简化了protocol buffer language，这使其可以更便于使用和支持更多的编程语言：我们现在的`alpha release`版本可以让你能产生JAVA、C++、Pthyon、JavaNano、Ruby、Objective-C和C#版本的protocol buffer code，不过可能有时会有一些局限性。另外，你可以使用最新的`Go protoc`插件来产生Go语言版本的proto3 code，这可以从`golang/protobuf` Github repository获取。  
  
以下情况下，我们现在只推荐你使用proto3:  
- 如果你想尝试在我们新支持的语言中使用protocol buffers  
- 如果你想尝试我们最新开源的`RPC`实现gRPC(目前仍处于alpha release版本)，我们建议你为所有的 gRPC 服务器和客户端都使用proto3以避免兼容性问题。  
  
注意两个版本的语言APIs并不是完全兼容的，为了避免给原来的用户造成不便，我们将会继续维护之前的那个proto2版本。  
  
# 使用步骤  
## 下载 protobuf 编译器  
[下载路径](https://github.com/google/protobuf/releases)   
注意这个东西在每个版本的下载部分的最下面，例如 [protoc-3.6.1-win32.zip](https://github.com/google/protobuf/releases/download/v3.6.1/protoc-3.6.1-win32.zip)  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/3ldcCGGFGb.png?imageslim)   
  
它包含 `protoc` 二进制文件以及与 protobuf 一起分发的一组标准 `.proto`文件。  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/khfd0dhfBg.png?imageslim)  
  
解压后可以把`protoc.exe`所在的目录加入系统变量Path，例如【C:\Android\protobuf\protoc-3.6.1-win32\bin】  
然后可以在任何路径测试`protoc`命令是否可用，例如：  
> protoc --version  
libprotoc 3.6.1  
  
## 编写 .proto 文件  
可以在 PC 任意位置通过任意纯文本编辑器编写 .proto 文件，例如：  
```  
syntax = "proto3";//指定编译时所使用的语法版本，不指定时会有 WARNING 提示：默认使用 proto2。  
option java_package = "com.bqt.test.protomodel";//生成的类的【包名】  
option java_outer_classname = "PersonEntity";//生成的类的【类名】  
message Person { //真正用来封装数据的【内部类】的类名  
  int32 id = 1;  
  string name = 2;  
  string email = 3;  
}  
```  
  
## 编译 .proto 文件生成 java 类  
通过`protoc`命令编译`.proto`文件生成`java`类：  
命令：`protoc -I=目录1 --java_out=目录2 文件3`  
- 【-I=目录1】：`输入目录`。这个参数是简称，全称为：【--proto_path=目录1】。可同时指定多个，目录将按顺序搜索。如果没有给出，则使用当前工作目录。  
- 【--java_out=目录2】：`输出目录`，指定生成的Java源文件的目录。  
- 【文件3】：`输入文件`。  
    - 如果指定了相对文件路径，则将在工作目录[working directory]中搜索该文件。  
    - 选项 `--proto_path` 不会影响搜索此参数文件的方式。  
    - 文件内容将在参数列表中的 `@<filename>` 位置展开。  
    - 请注意，shell扩展不会应用于文件的内容(即，您不能使用引号、通配符、转义、命令[quotes, wildcards, escapes, commands]等)。  
    - 每行对应一个参数，即使它包含空格。  
  
生成 Java 类时使用【java_out】，生成其他语言的结构化数据时只需更改这个参数即可，例如如果用在iOS上，可以使用【objc_out】，这就是其跨平台的体现。  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/A6bkdeg52h.png?imageslim)  
  
使用案例：`protoc` `-I=D:\bqt\proto` `--java_out=D:\bqt\out` `test.proto`  
  
    protoc -I=D:\bqt\proto --java_out=D:\bqt\out test.proto  
  
如果在当前目录执行命令，可以简化为：`protoc` `--java_out=D:\bqt\out` `test.proto`  
  
执行上面代码后会发现，在指定的目录下面生成了包含`包名`的`PersonEntity.java`类(虽然定义很简单，但是生成的类有将近1000行代码)。  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/LDGlFAkf2h.png?imageslim)    
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/mj0A3Dja1b.png?imageslim)  
  
## 项目中添加 protobuf runtime  
确保 runtime 的版本号与 protoc 的版本号匹配（或者版本更新）。  
  
方式一：下载源码(下载路径同上) ，下载后将需要的源码`连同包名`一起复制到项目中。  
需要的源码一般在以下目录中：  
```  
protobuf-3.6.1\java\core\src\main\java\com\google\protobuf    //核心包  
protobuf-3.6.1\java\util\src\main\java\com\google\protobuf\util    //可选包，提供 JsonFormat 等功能  
```  
  
方式二：直接添加protobuf依赖即可，版本号要比使用的 protoc.exe 版本相同或更高：  
```groovy  
implementation 'com.google.protobuf:protobuf-java:3.6.1'//protobuf  
//implementation 'com.google.protobuf:protobuf-lite:3.0.1'//protobuf lite  
//implementation 'com.google.protobuf:protobuf-java-util:3.6.1'//可选，JsonFormat 等功能  
```  
  
## 测试 java 类  
将上面生成的 java 类连同包名复制到项目中，然后就可以很方便的调用封装好的Java类的API了。  
```java  
public class MainActivity extends ListActivity {  
      
    private static final String PROTO_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_proto";  
    private static final String JSON_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_json";  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"以proto格式保存对象",  
            "解析proto方式保存的对象内容",  
            "以Json格式保存对象",  
            "解析son方式保存的对象内容",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                PersonEntity.Person protoPerson = PersonEntity.Person.newBuilder()  
                    .setId(3)  
                    .setName("zhangsan")  
                    .setEmail("test@qq.com")  
                    .build();  
                Log.i("bqt", "原始的proto格式对象内容：" + protoPerson.toString());  
                FileUtils.writeToFile(protoPerson.toByteArray(), PROTO_FILE_PATH);//以proto格式保存对象，保存的长度=25  
                break;  
            case 1:  
                byte[] protoFileBytes = FileUtils.readFromFile(PROTO_FILE_PATH);  
                try {  
                    PersonEntity.Person parseProtoPerson = PersonEntity.Person.parseFrom(protoFileBytes);  
                    Log.i("bqt", "解析proto方式保存的对象内容：" + parseProtoPerson.toString());  
                } catch (InvalidProtocolBufferException e) {  
                    e.printStackTrace();  
                }  
                break;  
            case 2:  
                Person jsonPerson = Person.newBuilder().id(3).name("zhangsan").email("test@qq.com").build();  
                Log.i("bqt", "原始的Json格式对象内容：" + jsonPerson.toString());  
                FileUtils.writeToFile(new Gson().toJson(jsonPerson).getBytes(), JSON_FILE_PATH);//以Json格式保存对象，保存的长度=48  
                break;  
            case 3:  
                byte[] jsonFilebytes = FileUtils.readFromFile(JSON_FILE_PATH);  
                String json = new String(jsonFilebytes);  
                Person parseJsonPerson = new Gson().fromJson(json, Person.class);  
                Log.i("bqt", parseJsonPerson.toString());  
                break;  
            default:  
                break;  
        }  
    }  
}  
```  
  
# 在 Android 中使用 Java Lite runtime  
[使用说明](https://github.com/google/protobuf/blob/master/java/lite.md)  
  
对于Android用户，为了更小的代码大小，建议使用 protobuf 的`Java Lite runtime`。Java Lite runtime 也可以与`Proguard`一起使用，因为它`不依赖于Java反射`，并且已经过优化以允许尽可能多的代码剥离[code stripping]。  
  
要使用 Java Lite runtime，除了和上面一样需要安装 protobuf 编译器外，还需要安装 [protoc-gen-javalite](https://repo1.maven.org/maven2/com/google/protobuf/protoc-gen-javalite/) 插件。  
    
同样选择适用于您的平台的版本，例如`protoc-gen-javalite-3.0.0-windows-x86_64.exe`，然后将其重命名为`protoc-gen-javalite.exe`，并在 PATH 中配置其路径，建议将其和 `protoc` 放在同一目录中：  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/5FjibLD55d.png?imageslim)  
  
之后，只需使用【javalite_out】代替【java_out】，您就可以和上面一样编译 .proto 文件生成 Java Lite 代码，例如：  
  
    //protoc -I=D:\bqt\proto --java_out=D:\bqt\out test.proto  
    protoc -I=D:\bqt\proto --javalite_out=D:\bqt\out test.proto  
  
对比两次生成的文件我们发现，Java Lite runtime 生成的 Java 类虽然明显比之前小了很多，但是仍旧比较大！  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/bb51Ddj81j.png?imageslim)  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181110/jCAmBfBJGe.png?imageslim)  
  
同样，项目中应该添加 Java Lite 的源文件(或jar包)或添加 Java Lite 的依赖：  
  
    implementation 'com.google.protobuf:protobuf-lite:3.0.1'//protobuf lite  
  
这里的版本号要比使用的 protoc 插件版本相同或更高(文档中说只要大于3.0.0就可以)。  
  
> 以下内容我还没去研究  
Use Protobuf Java Lite Runtime with Bazel：  
Bazel has native build rules to work with protobuf。对于Java Lite运行时，您可以使用java_lite_proto_library规则。 查看我们的构建文件示例以了解如何使用它。  
  
2018-8-17  
