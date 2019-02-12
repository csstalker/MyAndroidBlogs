| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
字节码 反编译 重新打jar包 MD  
***  
目录  
===  

- [如何修改一个 Jar 包中的内容](#如何修改一个-jar-包中的内容)
- [借助 Javassist 修改 jar 包](#借助-javassist-修改-jar-包)
	- [背景](#背景)
	- [案例](#案例)
	- [拓展](#拓展)
- [使用 Javac 命令编译类的注意事项](#使用-javac-命令编译类的注意事项)
	- [基本使用](#基本使用)
	- [引入 jar 包的情况](#引入-jar-包的情况)
	- [添加 package 的情况](#添加-package-的情况)
	- [引入 jar 包并添加 package 的情况](#引入-jar-包并添加-package-的情况)
  
# 如何修改一个 Jar 包中的内容  
基本步骤  
- 使用反编译工具 `JD-GUI` 打开 `jar` 文件，这时你可以看到源码(可能是混淆过的源码)  
- 将某一个 `class` 文件保存为 `java` 文件(也可以一键保存整个`jar`包中所有的`Java`文件)  
- 根据你的需求修改此 `java` 文件  
- 使用 `javac` 命令将该 `java` 文件编译为 `class` 文件  
  
> 这一步其实是有问题的，一般情况下不可能编译成功，因为这个java文件很可能会引用到其他你提供不了源码的类，比如 Android 中的 Context，比如第三方库中的类  
> 当然这种情况下你可以通过新建一个Java或Android或JavaEE工程，并将此Java类以及此Java类所引用的类、jar包等引入进来，这样可以解决引用问题  
> 但是使用这种方式的工作量往往远超预期，因为此Java类所引用的类很可能还会引用更多的Java类，因此会导致你这个工程引入的类呈指数级增长  
  
- 使用 `Winrar` 等解压工具将 `jar` 包解压到指定目录，替换需要修改的 `class` 文件  
- 使用 `cd` 命令定位到要打包的目录，使用 `jar -cvf bqt.jar .` 或`jar cvf bqt.jar *` 命令重新打包  
  
> 注意。打包时要把`META-INF`也包含进去，否则虽然能打包成功，但使用时就会出错。  
  
- 至此，就会在指定目录生成我们修改后的新的jar包  
  
# 借助 Javassist 修改 jar 包  
## 背景  
这是实际项目中遇到的一个问题：lib目录下某个第三方jar包中的某个方法的返回值不符合我们要求，而这个方法在很多地方被调用到，我们怎么修改这个方法的返回值呢？  
  
可能有很多解决方法，比如动态代理，比如AOP，但是这些方案都有很多问题。比如动态代理，我们这个方法是一个普通的方法，使用JDK的动态代理是行不通的，而cglib在Android中是用不了的；而对于AOP，我也确实尝试了，但发现使用aspectj时，对于混淆过的jar包中的方法总是拦截不成功(当然，使用沪江封装的aspectjX是可以的)。另外一个问题是，使用这些方案对项目改动都是非常大的，特别是引用AOP这些框架，由于很小的一点改动就这么大动干戈的话有点不合适。  
  
那有没有其他方法呢？  
  
其实有一个最简单的方案，那就是反编译，我们只需要将jar包中那个相应的class文件中方法修改一下就可以了。  
  
但是正如上面描述的那样，这种方式是有局限性的！但是如果借助 `Javassist` 去做的话，问题就相当简单了。  
  
## 案例  
比如，我想将Gson库中Gson类的下面的方法  
```java  
public String toJson(Object src) {  
    if (src == null) {  
        return toJson(JsonNull.INSTANCE);  
    }  
    return toJson(src, src.getClass());  
}  
```  
  
修改为：  
```java  
public String toJson(Object src) {  
    if (src == null) {  
        return "";  
    }  
    return src.toString();  
}  
```  
  
那么我只需执行以下逻辑就可以了：  
```java  
public class Test {  
  
    public static void main(String[] args) {  
        try {  
            ClassPool pool = ClassPool.getDefault();  
            pool.insertClassPath("D:/test/gson-2.8.1.jar");//加载指定路径下的库。如果此库已被引入项目中，可以省略这一步  
            CtClass cc = pool.get("com.google.gson.Gson");//加载指定的类  
            CtClass[] params = new CtClass[] { pool.get("java.lang.Object") };//基本类型和引用类型的描述方式是不一样的  
            CtMethod method = cc.getDeclaredMethod("toJson", params);//取得需要修改的方法  
            method.setBody("{" + // 你只需要正常写代码逻辑就可以了，复制过来时，一些IDE，比如AS会自动帮你添加转义字符  
                    "if ($1 == null) {\n" + //$0代表的是this，$1代表方法参数的第一个参数、$2代表方法参数的第二个参数  
                    "\treturn \"\";\n" + //  
                    "}\n" + //  
                    "return $1.toString();" + //  
                    "}"); //修改方法  
            cc.writeFile("D:/test");//保存到指定位置，执行完这一步后就会在指定目录生成你需要的 class 文件  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}  
```  
  
执行后就会在指定目录生成你需要的 `class` 文件：  
![](index_files/75b938ba-5c3d-4308-9587-42700272f750.png)  
  
然后我们按照上面的步骤打成jar包(注意最后的一个`.`代表的是当前路径)：  
```java  
D:\test>jar -cvf mygson.jar .  
```  
  
要打包的路径下的文件：  
![](index_files/16593204-10ce-4189-830c-83f8288dfb10.png)  
  
然后我们就可以使用我们修改后的Gson库了，反编译后里面的逻辑如下：  
![](index_files/6a63285f-7cc4-46a1-aa41-a829acad610b.png)  
  
## 拓展  
1、上面这种方式的思路是，在`编译期`修改指定`Java`类编译后生成的`class`文件，然后重新打成`jar`包。  
  
2、其实还有另外一种思路，那就是不在编译期处理，而在`运行期`处理，也即我们可以在运行期先修改、然后直接加载修改后的`class`文件。  
  
经过在Android和Java工程中测试发现，这种方式也是非常靠谱的。  
  
3、在代码层面，和上面那种方式的区别是，我们最后不是调用  
```java  
cc.writeFile("D:/test");  
```  
方法将修改后的`class`文件保存到指定位置，而是调用  
```java  
cc.toClass();  
```  
方法返回、并加载此新生成的`class`文件。  
  
4、但是这种方式有一个局限性。  
  
因为同一个 class 是不能在同个 ClassLoader 中被加载两次的，所以在调用 `toClass()` 前要保证此要修改的类不能是被加载过的类，否则直接报异常。  
  
5、但是这种情况下也并不是无解的。  
  
我们可以自定义一个 `ClassLoader` 去加载新生成的类：  
```java  
Class clazz = classLoader.loadClass("com.bqt.test.Person")  
```  
  
6、但是要知道，如果这样做的话，此时JVM中是有两个Person类的，并且除非是通过返回的这个 `clazz` 创建的对象，其使用的是新生成的类：  
```java  
clazz.getDeclaredMethod("hello", String.class).invoke(clazz.newInstance(), "你妹"); //调用的是新类的方法  
```  
  
其他任何方式创建的对象，使用的仍是原始的类(包括通过反射方式)：  
```java  
new Person().hello("泥煤"); //调用的是原始类的方法  
Class clazz2 = Class.forName("com.bqt.test.Person");  
clazz2.getDeclaredMethod("hello", String.class).invoke(clazz2.newInstance(), "你美"); //调用原始类的方法  
```  
  
# 使用 Javac 命令编译类的注意事项  
在使用原始的`javac`编译Java类文件时，如果当前类文件对其他类有依赖，那么就可能会出现问题。  
  
举例如下：在桌面新建一个文件夹test，然后建立两个类：A.java和B.java，两个类的代码都很简单，其中B类对A类有依赖：  
  
```java  
import java.sql.Date;  
  
public class A {  
    public static void i(Date date) {  
        System.out.println(date.toString());  
    }  
}  
```  
```java  
import java.sql.Date;  
  
public class B {  
    public static void i(String info) {  
        Date date = new Date(System.currentTimeMillis());  
        A.i(date);  
    }  
}  
```  
  
## 基本使用  
直接用javac命令编译两个源文件，且先编译 `A.java`，结果如下：  
```java  
C:\Users\baiqi>cd/d C:\Users\baiqi\Desktop\test  
C:\Users\baiqi\Desktop\test>javac A.java  
C:\Users\baiqi\Desktop\test>javac B.java  
```  
可以看到编译成功，生成了对应的class文件。  
  
注意：  
- 若在没有编译`A.java`情况下直接编译 `B.java`，则会`同时`生成A和B对应的class文件。  
- 虽然我们代码中导入了`import java.sql.Date;`但因为是JDK中的类，所以仍可以直接编译  
  
## 引入 jar 包的情况  
我们在`B.java`中利用import语句导入一个包：  
```java  
import java.sql.Date;  
import com.google.gson.Gson;  
  
public class B {  
    public static void i(String info) {  
        Date date = new Date(System.currentTimeMillis());  
        System.out.println(new Gson().toJson(date));  
        A.i(date);  
    }  
}  
```  
  
然后直接编译`B.java`：  
```java  
C:\Users\baiqi\Desktop\test>javac B.java  
B.java:2: 错误: 程序包com.google.gson不存在  
import com.google.gson.Gson;  
                      ^  
B.java:7: 错误: 找不到符号  
                System.out.println(new Gson().toJson(date));  
                                       ^  
  符号:   类 Gson  
  位置: 类 B  
2 个错误  
```  
  
可以看到，`B.java`文件编译失败，这是因为Gson并非Java标准类库中的内容，因此编译器找不到对应的包，就会出错。  
  
我们将需要的`jar`文件放入当前目录，并使用`-cp`参数将库文件导入，然后继续编译：  
```java  
C:\Users\baiqi\Desktop\test>javac -cp gson-2.8.1.jar B.java  
B.java:8: 错误: 找不到符号  
                A.i(date);  
                ^  
  符号:   变量 A  
  位置: 类 B  
1 个错误  
```  
  
怎么提示找不到 A 呢？  
这是因为，默认情况下，编译器会在`当前目录`下寻找需要的类文件，但是如果我们使用`cp`参数修改了类文件查找路径，而并没有包含`当前目录`，那么就会编译失败。因为我们在使用`cp`参数时，需要将当前目录包含进去：  
```java  
C:\Users\baiqi\Desktop\test>javac -cp gson-2.8.1.jar;. B.java  
```  
  
## 添加 package 的情况  
我们在`A.java`和`B.java`文件中添加`package`语句：  
```java  
package com.bqt.test;  
  
import java.sql.Date;  
  
public class A {  
    public static void i(Date date) {  
        System.out.println(date.toString());  
    }  
}  
```  
```java  
package com.bqt.test;  
  
import java.sql.Date;  
  
public class B {  
    public static void i(String info) {  
        Date date = new Date(System.currentTimeMillis());  
        A.i(date);  
    }  
}  
```  
  
然后我们将`A.java`和`B.java`放入相应的包中：  
![](index_files/364e9807-54d1-425a-a3e4-4b324ba67806.png)  
然后直接编译`B.java`(注意要指定相对或绝对路径)：  
```java  
C:\Users\baiqi\Desktop\test>javac com\bqt\test\B.java  
```  
  
## 引入 jar 包并添加 package 的情况  
我们在上面第三种情况下再给B引入jar包：  
```java  
package com.bqt.test;  
  
import java.sql.Date;  
import com.google.gson.Gson;  
  
public class B {  
    public static void i(String info) {  
        Date date = new Date(System.currentTimeMillis());  
        System.out.println(new Gson().toJson(date));  
        A.i(date);  
    }  
}  
```  
  
同样，命令也结合以上两种形式即可：  
```java  
C:\Users\baiqi\Desktop\test>javac -cp gson-2.8.1.jar;. com\bqt\test\B.java  
```  
  
2019-1-6  
