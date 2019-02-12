| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
AS 注解处理器 APT Processor MD  
***  
目录  
===  

- [注解处理器](#注解处理器)
	- [基本实现](#基本实现)
		- [实现Processor接口](#实现processor接口)
		- [注册注解处理器](#注册注解处理器)
		- [AutoService](#autoservice)
	- [相关概念](#相关概念)
		- [元素 Element](#元素-element)
		- [类型 TypeMirror](#类型-typemirror)
		- [创建文件 Filer](#创建文件-filer)
		- [打印错误信息 Messager](#打印错误信息-messager)
		- [配置选项参数 getOptions](#配置选项参数-getoptions)
		- [处理过程](#处理过程)
		- [获取注解元素](#获取注解元素)
- [[注解处理器示例 -1](https://github.com/yuweiguocn/CustomAnnotation)](#注解处理器示例--1httpsgithubcomyuweiguocncustomannotation)
	- [[自定义注解](https://github.com/yuweiguocn/CustomAnnotation/blob/master/annotation/src/main/java/io/github/yuweiguocn/annotation/CustomAnnotation.java)](#自定义注解httpsgithubcomyuweiguocncustomannotationblobmasterannotationsrcmainjavaiogithubyuweiguocnannotationcustomannotationjava)
	- [[注解处理器](https://github.com/yuweiguocn/CustomAnnotation/blob/master/compiler/src/main/java/io/github/yuweiguocn/processor/CustomProcessor.java)](#注解处理器httpsgithubcomyuweiguocncustomannotationblobmastercompilersrcmainjavaiogithubyuweiguocnprocessorcustomprocessorjava)
	- [[测试代码](https://github.com/yuweiguocn/CustomAnnotation/tree/master/app/src/main/java/io/github/yuweiguocn/customannotation)](#测试代码httpsgithubcomyuweiguocncustomannotationtreemasterappsrcmainjavaiogithubyuweiguocncustomannotation)
	- [编译结果](#编译结果)
- [注解处理器示例 -2](#注解处理器示例--2)
	- [实现一个 ButterKnife 框架](#实现一个-butterknife-框架)
	- [项目基本结构](#项目基本结构)
	- [编写代码生成规则](#编写代码生成规则)
	- [使用效果](#使用效果)
  
# 注解处理器  
[参考1](https://www.jianshu.com/p/50d95fbf635c)  
[参考2](https://github.com/yuweiguocn/CustomAnnotation)  
  
APT(Annotation Processing Tool) 即注解处理器，是`javac`的一个工具，用来`在编译期扫描和处理注解，通过注解来生成文件(通常是java文件)`。即以注解作为桥梁，通过预先规定好的代码生成规则来自动生成 Java 文件。这些生成的java文件会同其手动编写的java代码一样会被`javac`编译。此类注解框架的代表有 `ButterKnife、Dragger2、EventBus` 等。  
  
Java API 已经提供了扫描源码并解析注解的框架，开发者可以通过实现`Processor`接口或继承 `AbstractProcessor` 类来实现自己的注解解析逻辑。  
  
APT 的原理就是在注解了某些代码元素后，编译器在编译时会检查 `Processor` 的子类，并且自动调用其 `process()` 方法，然后将添加了指定注解的所有代码元素作为参数传递给该方法，开发者再根据注解元素在编译期输出对应的 Java 代码  
  
## 基本实现  
实现一个自定义注解处理器需要有两个步骤，第一是实现`Processor`接口处理注解，第二是注册注解处理器。  
  
### 实现Processor接口  
通过实现Processor接口可以自定义注解处理器  
```java  
public interface Processor {  
    Set<String> getSupportedOptions();  
    Set<String> getSupportedAnnotationTypes();  
    SourceVersion getSupportedSourceVersion();  
    void init(ProcessingEnvironment var1);  
    boolean process(Set<? extends TypeElement> var1, RoundEnvironment var2);  
    Iterable<? extends Completion> getCompletions(Element var1, AnnotationMirror var2, ExecutableElement var3, String var4);  
}  
```  
  
这里我们采用更简单的方法，通过继承`AbstractProcessor`类并实现抽象方法process处理我们想要的功能：  
```  
public class CustomProcessor extends AbstractProcessor {  
    @Override  
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {  
        return false;  
    }  
}  
```  
  
除此之外，我们还需要通过重写getSupportedAnnotationTypes方法和getSupportedSourceVersion方法指定`支持的注解类型`以及`支持的Java版本`：  
```  
public class CustomProcessor extends AbstractProcessor {  
    @Override  
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {  
        return false;  
    }  
  
    @Override  
    public Set<String> getSupportedAnnotationTypes() {  
        Set<String> annotataions = new LinkedHashSet<String>();  
        annotataions.add(CustomAnnotation.class.getCanonicalName());  
        return annotataions;  
    }  
  
    @Override  
    public SourceVersion getSupportedSourceVersion() {  
        return SourceVersion.latestSupported();  
    }  
}  
```  
  
对于支持的注解类型，我们还可以通过注解的方式进行指定：  
```  
@SupportedAnnotationTypes({"io.github.yuweiguocn.annotation.CustomAnnotation"})  
public class CustomProcessor extends AbstractProcessor {  
    @Override  
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {  
        return false;  
    }  
  
    @Override  
    public SourceVersion getSupportedSourceVersion() {  
        return SourceVersion.latestSupported();  
    }  
}  
```  
  
因为Android平台可能会有兼容问题，建议使用重写`getSupportedAnnotationTypes`方法指定支持的注解类型。  
  
### 注册注解处理器  
你可能会问，我怎样将注解处理器注册到`javac`中？  
你必须提供一个`.jar`文件，就像其他`.jar`文件一样，你打包你的注解处理器到此文件中。并且，在你的jar中，你需要打包一个特定的文件`javax.annotation.processing.Processor`到`META-INF/services`路径下。所以，你的.jar文件看起来就像下面这样：  
```  
－myprcessor.jar  
－－com  
－－－example  
－－－－MyProcessor.class  
－－META-INF  
－－－services  
－－－－javax.annotation.processing.Processor  
```  
![](https://upload-images.jianshu.io/upload_images/618971-8f83316fe9177859.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/322)  
  
打包进`javax.annotation.processing.Processor`的内容是处理器的合法全称，每一个元素换行分割：  
```  
com.example.myprocess.MyProcessorA  
com.example.myprocess.MyProcessorB  
```  
  
把`.jar`放到你的`Builpath`中，javac会自动检查和读取`javax.annotation.processing.Processor`中的内容，并且注册Processor作为注解处理器。  
  
### AutoService  
上面这种注册的方式太麻烦了，谷歌帮我们写了一个注解处理器 [google/auto](https://link.jianshu.com?t=https://github.com/google/auto) 来生成这个文件，添加依赖：  
```  
compile 'com.google.auto.service:auto-service:1.0-rc2'  
```  
  
然后一个注解搞定：  
```  
@AutoService(Processor.class)  
public class CustomProcessor extends AbstractProcessor {  
    //...  
}  
```  
后面我们只需关注注解处理器中的处理逻辑即可。  
  
## 相关概念  
抽象类中还有一个`init`方法，这是`Processor`接口中提供的一个方法，当我们编译程序时注解处理器工具会调用此方法并且提供实现`ProcessingEnvironment`接口的对象作为参数：  
```  
@Override  
public synchronized void init(ProcessingEnvironment processingEnvironment) {  
    super.init(processingEnvironment);  
}  
```  
  
我们可以使用`ProcessingEnvironment`获取一些实用类以及获取选项参数等：  
```java  
public interface ProcessingEnvironment {  
    Map<String, String> getOptions(); //返回指定的参数选项  
    Messager getMessager(); //返回实现Messager接口的对象，用于报告错误信息、警告提醒  
    Filer getFiler(); //返回实现Filer接口的对象，用于创建文件、类和辅助文件  
    Elements getElementUtils(); //返回实现Elements接口的对象，用于操作元素的工具类  
    Types getTypeUtils(); //返回实现Types接口的对象，用于操作类型的工具类  
    SourceVersion getSourceVersion();  
    Locale getLocale();  
}  
```  
  
### 元素 Element  
`Element`元素是一个接口，表示一个程序元素，比如`包、类或者方法`。以下元素类型接口全部继承自Element接口：  
  
| 类型 | 说明 |  
| --- | --- |  
| ExecutableElement | 表示某个类或接口的方法、构造方法或初始化程序(静态或实例)，包括注解类型元素。 |  
| PackageElement | 表示一个`包`程序元素。提供对有关包及其成员的信息的访问。 |  
| TypeElement | 表示一个`类或接口`程序元素。提供对有关类型及其成员的信息的访问。 |  
| TypeParameterElement | 表示一般类、接口、方法或构造方法元素的`形式类型参数`。 |  
| VariableElement | 表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数。 |  
  
> 注意，枚举类型是一种类，而注解类型是一种接口。  
  
如果我们要判断一个元素的类型，应该使用`Element.getKind()`方法配合`ElementKind`枚举类进行判断。尽量避免使用`instanceof`进行判断，因为比如`TypeElement`既表示类又表示一个接口，这样判断的结果可能不是你想要的。例如我们判断一个元素是不是一个类：  
```  
if (element instanceof TypeElement) {} //错误，也有可能是一个接口  
if (element.getKind() == ElementKind.CLASS) {} //正确  
```  
  
以下为`ElementKind`枚举类的定义：  
```java  
public enum ElementKind {  
    PACKAGE, //一个包  
    ENUM, //一个枚举类型  
    CLASS, //没有用更特殊的种类（如 ENUM）描述的类  
    ANNOTATION_TYPE, //一个注解类型  
    INTERFACE, //没有用更特殊的种类（如 ANNOTATION_TYPE）描述的接口  
    ENUM_CONSTANT, //一个枚举常量  
    FIELD, //没有用更特殊的种类（如 ENUM_CONSTANT）描述的字段  
    PARAMETER, //方法或构造方法的参数  
    LOCAL_VARIABLE, //局部变量  
    EXCEPTION_PARAMETER,  
    METHOD, //一个方法  
    CONSTRUCTOR, //一个构造方法  
    STATIC_INIT,  
    INSTANCE_INIT,  
    TYPE_PARAMETER, //一个类型参数  
    OTHER,  
    RESOURCE_VARIABLE;  
  
    private ElementKind() {  
    }  
  
    public boolean isClass() {  
        return this == CLASS || this == ENUM;  
    }  
  
    public boolean isInterface() {  
        return this == INTERFACE || this == ANNOTATION_TYPE;  
    }  
  
    public boolean isField() {  
        return this == FIELD || this == ENUM_CONSTANT;  
    }  
}  
```  
  
### 类型 TypeMirror  
`TypeMirror`是一个接口，表示 Java 编程语言中的类型。这些类型包括基本类型、声明类型(类和接口类型)、数组类型、类型变量和 null 类型。还可以表示通配符类型参数、executable 的签名和返回类型，以及对应于包和关键字 void 的伪类型。以  
  
下类型接口全部继承自`TypeMirror`接口：  
  
| 类型 | 说明 |  
| --- | --- |  
| ArrayType | 表示一个数组类型。多维数组类型被表示为组件类型也是数组类型的数组类型。 |  
| DeclaredType | 表示某一声明类型，是一个类 (class) 类型或接口 (interface) 类型。这包括参数化的类型（比如 java.util.Set<String>）和原始类型。TypeElement 表示一个类或接口元素，而 DeclaredType 表示一个类或接口类型，后者将成为前者的一种使用（或调用）。 |  
| ErrorType | 表示无法正常建模的类或接口类型。 |  
| ExecutableType | 表示 executable 的类型。executable 是一个方法、构造方法或初始化程序。 |  
| NoType | 在实际类型不适合的地方使用的伪类型。 |  
| NullType | 表示 null 类型。 |  
| PrimitiveType | 表示一个基本类型。这些类型包括 boolean、byte、short、int、long、char、float 和 double。 |  
| ReferenceType | 表示一个引用类型。这些类型包括类和接口类型、数组类型、类型变量和 null 类型。 |  
| TypeVariable | 表示一个类型变量。 |  
| WildcardType | 表示通配符类型参数。 |  
  
同样，如果我们想判断一个`TypeMirror`的类型，应该使用`TypeMirror.getKind()`方法配合TypeKind枚举类进行判断。尽量避免使用`instanceof`进行判断，因为比如`DeclaredType`既表示类 类型又表示接口类型，这样判断的结果可能不是你想要的。  
  
以下为`TypeKind`枚举类的定义：  
```java  
public enum TypeKind {  
    BOOLEAN,  
    BYTE,  
    SHORT,  
    INT,  
    LONG,  
    CHAR,  
    FLOAT,  
    DOUBLE,  
    //*****************以上为8种基本类型*****************  
    VOID, //对应于关键字 void 的伪类型  
    NONE,  
    NULL, //null 类型  
    ARRAY, //数组类型  
    DECLARED,  
    ERROR,  
    TYPEVAR,  
    WILDCARD,  
    PACKAGE, //对应于包元素的伪类型  
    EXECUTABLE, //方法、构造方法或初始化程序  
    OTHER,  
    UNION,  
    INTERSECTION;  
    //...  
```  
  
### 创建文件 Filer  
Filer接口支持通过注解处理器创建新文件。可以创建三种文件类型：源文件、类文件和辅助资源文件。  
  
**创建源文件**  
```  
JavaFileObject createSourceFile(CharSequence name, Element... elements) throws IOException  
```  
- 创建一个新的源文件，并返回一个对象以允许写入它  
- 文件的名称和路径(相对于源文件的根目录输出位置)基于该文件中声明的类型  
- 如果声明的类型不止一个，则应该使用主要顶层类型的名称(例如，声明为 public 的那个)  
- 还可以创建源文件来保存有关某个包的信息，包括包注解  
- 要为指定包创建源文件，可以用 name 作为包名称，后跟 `.package-info`  
- 要为未指定的包创建源文件，可以使用 `package-info`  
  
**创建类文件**  
```  
JavaFileObject createClassFile(CharSequence name, Element... elements) throws IOException  
```  
- 创建一个新的类文件，并返回一个对象以允许写入它  
- 文件的名称和路径(相对于类文件的根目录输出位置)基于将写入的类型名称  
- 还可以创建类文件来保存有关某个包的信息，包括包注解  
- 要为指定包创建类文件，可以用 name 作为包名称，后跟 `.package-info`  
- 为未指定的包创建类文件不受支持  
- 对于生成Java文件，可以使用Square公司的开源类库 [JavaPoet](https://link.jianshu.com?t=https://github.com/square/javapoet)  
  
**创建辅助资源文件**  
```  
FileObject createResource(JavaFileManager.Location location,  
                          CharSequence pkg,  
                          CharSequence relativeName,  
                          Element... originatingElements)  
                          throws IOException  
```  
- 创建一个用于写入操作的新辅助资源文件，并为它返回一个文件对象  
- 该文件可以与新创建的源文件、新创建的二进制文件或者其他受支持的位置一起被查找  
- 位置 CLASS_OUTPUT 和 SOURCE_OUTPUT 必须受支持  
- 资源可以是相对于某个包(该包是源文件和类文件)指定的，并通过相对路径名从中取出  
- 从不太严格的角度说，新文件的完全路径名将是 location、 pkg 和 relativeName 的串联  
  
### 打印错误信息 Messager  
Messager接口提供注解处理器用来报告错误消息、警告和其他通知的方式。  
```java  
public interface Messager {  
    void printMessage(Kind var1, CharSequence var2); //打印指定种类的消息  
    void printMessage(Kind var1, CharSequence var2, Element var3); //在元素的位置上打印指定种类的消息  
    void printMessage(Kind var1, CharSequence var2, Element var3, AnnotationMirror var4); //在已注解元素的注解镜像位置上打印指定种类的消  
    void printMessage(Kind var1, CharSequence var2, Element var3, AnnotationMirror var4, AnnotationValue var5); //在已注解元素的注解镜像内部注解值的位置上打印指定种类的消息  
}  
```  
  
> 注意：我们应该对在处理过程中可能发生的异常进行捕获，通过Messager接口提供的方法通知用户。  
此外，使用带有Element参数的方法连接到出错的元素，用户可以直接点击错误信息跳到出错源文件的相应行。  
如果你在process()中抛出一个异常，那么运行注解处理器的JVM将会崩溃，这样用户会从javac中得到一个非常难懂出错信息。  
  
### 配置选项参数 getOptions  
我们可以通过`getOptions()`方法获取在`gradle`文件中配置的选项参数值。  
  
例如我们配置了一个名为`bqtOptions`的参数值：  
```  
android {  
    defaultConfig {  
        javaCompileOptions {  
            annotationProcessorOptions {  
                arguments = [ bqtOptions : 'io.github.yuweiguocn.customannotation.MyCustomAnnotation' ]  
            }  
        }  
    }  
}  
```  
  
在注解处理器中重写`getSupportedOptions`方法指定支持的选项参数名称，通过`getOptions`方法获取选项参数值：  
```  
public static final String CUSTOM_ANNOTATION = "bqtOptions";  
  
@Override  
public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {  
   String resultPath = processingEnv.getOptions().get(CUSTOM_ANNOTATION);  
    //...  
   return true;  
}  
  
@Override  
public Set<String> getSupportedOptions() {  
   Set<String> options = new LinkedHashSet<String>();  
   options.add(CUSTOM_ANNOTATION);  
   return options;  
}  
```  
  
### 处理过程  
Java官方文档给出的注解处理过程的定义：注解处理过程是一个`有序的循环过程`。在每次循环中，一个处理器可能被要求去处理那些在`上一次循环`中产生的`源文件和类文件`中的注解。第一次循环的输入是运行此工具的初始输入，这些初始输入，可以看成是虚拟的第0次的循环的输出。因此我们实现的process方法有可能会被调用多次，因为我们生成的文件也有可能会包含相应的注解。  
  
例如，我们的源文件为SourceActivity.class，生成的文件为Generated.class，这样就会有三次循环，第一次输入为SourceActivity.class，输出为Generated.class；第二次输入为Generated.class，输出并没有产生新文件；第三次输入为空，输出为空。  
  
每次循环都会调用process方法：  
- process方法的第一个参数是我们请求处理注解类型的集合，也就是我们通过重写`getSupportedAnnotationTypes`方法所指定的注解类型  
- process方法的第二个参数是有关当前和上一次循环的信息的环境  
- process方法的返回值表示这些注解是否由此 Processor 声明，如果返回 true，则这些注解已声明并且不要求后续 Processor 处理它们；如果返回 false，则这些注解未声明并且可能要求后续 Processor 处理它们。  
  
### 获取注解元素  
我们可以通过`RoundEnvironment`接口获取注解元素：  
```java  
public interface RoundEnvironment {  
    boolean processingOver(); //如果循环处理完成返回true，否则返回false  
    boolean errorRaised();  
    Set<? extends Element> getRootElements();  
    Set<? extends Element> getElementsAnnotatedWith(TypeElement var1); //返回被指定注解类型注解的元素集合  
    Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> var1); //返回被指定注解类型注解的元素集合  
}  
```  
  
# [注解处理器示例 -1](https://github.com/yuweiguocn/CustomAnnotation)  
下来我们来看一个示例，主要功能为自定义一个注解，此注解只能用在public的方法上，我们通过注解处理器拿到`类名和方法名`存储到List集合中，然后生成通过`参数选项`指定的`文件`，通过此文件可以获取List集合。  
  
> 注意：如果你clone了工程代码，你可能会发现注解和注解处理器是单独的module，这是因为我们的注解处理器只需要在编译的时候使用，并不需要打包到APK中，因此为了用户考虑，我们需要将注解处理器分离为单独的module。  
  
## [自定义注解](https://github.com/yuweiguocn/CustomAnnotation/blob/master/annotation/src/main/java/io/github/yuweiguocn/annotation/CustomAnnotation.java)  
```  
@Documented  
@Target({ElementType.METHOD})  
@Retention(RetentionPolicy.RUNTIME)  
public @interface CustomAnnotation {  
}  
```  
  
## [注解处理器](https://github.com/yuweiguocn/CustomAnnotation/blob/master/compiler/src/main/java/io/github/yuweiguocn/processor/CustomProcessor.java)  
```  
@AutoService(Processor.class)  
public class CustomProcessor extends AbstractProcessor {  
    private static final String CUSTOM_ANNOTATION = "bqtOptions";  
    private Filer filer;  
    private Messager messager;  
    private List<String> result = new ArrayList<>();  
    private int round;  
      
    @Override  
    public Set<String> getSupportedAnnotationTypes() {  
        Set<String> annotations = new LinkedHashSet<>();  
        annotations.add(CustomAnnotation.class.getCanonicalName());  
        return annotations;  
    }  
      
    @Override  
    public SourceVersion getSupportedSourceVersion() {  
        return SourceVersion.latestSupported();  
    }  
      
    @Override  
    public Set<String> getSupportedOptions() {  
        Set<String> options = new LinkedHashSet<>();  
        options.add(CUSTOM_ANNOTATION);  
        return options;  
    }  
      
    @Override  
    public synchronized void init(ProcessingEnvironment processingEnvironment) {  
        super.init(processingEnvironment);  
        filer = processingEnvironment.getFiler();  
        messager = processingEnvironment.getMessager();  
    }  
      
    @Override  
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {  
        messager.printMessage(Diagnostic.Kind.NOTE, "****************************************` round " + round);  
        try {  
            String resultPath = processingEnv.getOptions().get(CUSTOM_ANNOTATION);  
            if (resultPath == null) {  
                messager.printMessage(Diagnostic.Kind.ERROR, "No option " + CUSTOM_ANNOTATION +  
                    " passed to annotation processor");  
                return false;  
            }  
              
            round++;  
            messager.printMessage(Diagnostic.Kind.NOTE, "round " + round + " process over " + roundEnv.processingOver());  
            for (TypeElement annotation : annotations) {  
                messager.printMessage(Diagnostic.Kind.NOTE, "name is " + annotation.getSimpleName().toString());  
            }  
              
            if (roundEnv.processingOver()) {  
                if (!annotations.isEmpty()) {  
                    messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected processing state: annotations still available after processing over");  
                    return false;  
                }  
            }  
              
            if (annotations.isEmpty()) {  
                return false;  
            }  
              
            for (Element element : roundEnv.getElementsAnnotatedWith(CustomAnnotation.class)) {  
                if (element.getKind() != ElementKind.METHOD) {  
                    messager.printMessage(Diagnostic.Kind.ERROR,  
                        String.format("Only methods can be annotated with @%s", CustomAnnotation.class.getSimpleName()),  
                        element);  
                    return true;  
                }  
                  
                if (!element.getModifiers().contains(Modifier.PUBLIC)) {  
                    messager.printMessage(Diagnostic.Kind.ERROR, "Subscriber method must be public", element);  
                    return true;  
                }  
                  
                ExecutableElement execElement = (ExecutableElement) element;  
                TypeElement classElement = (TypeElement) execElement.getEnclosingElement();  
                result.add(classElement.getSimpleName().toString() + "#" + execElement.getSimpleName().toString());  
            }  
            if (!result.isEmpty()) {  
                messager.printMessage(Diagnostic.Kind.NOTE, "bqtOptions: " + resultPath);  
                generateFile(resultPath);  
            } else {  
                messager.printMessage(Diagnostic.Kind.WARNING, "No @CustomAnnotation annotations found");  
            }  
            result.clear();  
        } catch (Exception e) {  
            e.printStackTrace();  
            messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected error in CustomProcessor: " + e);  
        }  
        return true;  
    }  
      
    private void generateFile(String path) {  
        BufferedWriter writer = null;  
        try {  
            JavaFileObject sourceFile = filer.createSourceFile(path);  
            int period = path.lastIndexOf('.');  
            String myPackage = period > 0 ? path.substring(0, period) : null;  
            String clazz = path.substring(period + 1);  
            writer = new BufferedWriter(sourceFile.openWriter());  
            if (myPackage != null) {  
                writer.write("package " + myPackage + ";\n\n");  
            }  
            writer.write("import java.util.ArrayList;\n");  
            writer.write("import java.util.List;\n\n");  
            writer.write("/` This class is generated by CustomProcessor, do not edit. */\n");  
            writer.write("public class " + clazz + " {\n");  
            writer.write("    private static final List<String> ANNOTATIONS;\n\n");  
            writer.write("    static {\n");  
            writer.write("        ANNOTATIONS = new ArrayList<>();\n\n");  
            writeMethodLines(writer);  
            writer.write("    }\n\n");  
            writer.write("    public static List<String> getAnnotations() {\n");  
            writer.write("        return ANNOTATIONS;\n");  
            writer.write("    }\n\n");  
            writer.write("}\n");  
        } catch (IOException e) {  
            throw new RuntimeException("Could not write source for " + path, e);  
        } finally {  
            if (writer != null) {  
                try {  
                    writer.close();  
                } catch (IOException e) {  
                    //Silent  
                }  
            }  
        }  
    }  
      
    private void writeMethodLines(BufferedWriter writer) throws IOException {  
        for (int i = 0; i < result.size(); i++) {  
            writer.write("        ANNOTATIONS.add(\"" + result.get(i) + "\");\n");  
        }  
    }  
}  
```  
  
## [测试代码](https://github.com/yuweiguocn/CustomAnnotation/tree/master/app/src/main/java/io/github/yuweiguocn/customannotation)  
  
配置：  
```java  
android {  
    defaultConfig {  
        javaCompileOptions {  
            annotationProcessorOptions {  
                arguments = [bqtOptions: 'io.github.yuweiguocn.MyCustomAnnotation'] //代表的是生成的Java文件的限定类名  
            }  
        }  
    }  
}  
  
dependencies {  
    annotationProcessor project(":compiler") //或 'io.github.yuweiguocn:custom-annotation-processor:1.3.0'  
    implementation project(":annotation")  
}  
```  
  
使用注解处理器生成的类：  
```java  
public class MainActivity extends AppCompatActivity {  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
          
        StringBuilder sb = new StringBuilder();  
        List<String> annotations = MyCustomAnnotation.getAnnotations(); //编译之后才能找到 MyCustomAnnotation 类  
        for (int i = 0; i < annotations.size(); i++) {  
            sb.append(annotations.get(i)).append("\n");  
        }  
          
        ((TextView) findViewById(R.id.tv_annotation)).setText(sb.toString());  
    }  
      
    @CustomAnnotation  
    public void testAnnotation() {  
        Log.d("test", "test annotation");  
    }  
}  
```  
  
```java  
public class Test {  
    @CustomAnnotation  
    public void onTest() {}  
      
    @CustomAnnotation  
    public static void showToast() {}  
}  
```  
  
## 编译结果  
点击 `Build > ReBuild Project` 进行编译，编译时输出的日志：  
![](index_files/ed510a67-153a-4ee8-96aa-6f466a4fd4d7.png)  
  
编译后生成的文件：  
![](index_files/01327eaf-5225-40a8-a82f-112e24313835.png)  
```java  
package io.github.yuweiguocn;  
  
import java.util.ArrayList;  
import java.util.List;  
  
/` This class is generated by CustomProcessor, do not edit. */  
public class MyCustomAnnotation {  
    private static final List<String> ANNOTATIONS;  
  
    static {  
        ANNOTATIONS = new ArrayList<>();  
  
        ANNOTATIONS.add("MainActivity#testAnnotation");  
        ANNOTATIONS.add("Test#onTest");  
        ANNOTATIONS.add("Test#showToast");  
    }  
  
    public static List<String> getAnnotations() {  
        return ANNOTATIONS;  
    }  
}  
```  
  
# 注解处理器示例 -2  
[参考](https://github.com/leavesC/Android_APT)  
  
## 实现一个 ButterKnife 框架  
这里以 `ButterKnife` 为实现目标，在讲解 `Android APT` 的内容的同时，逐步实现一个轻量的控件绑定框架，即通过注解来自动生成如下所示的 `findViewById()` 代码  
```  
package hello.leavesc.apt;  
  
public class MainActivityViewBinding {  
    public static void bind(MainActivity _mainActivity) {  
        _mainActivity.tvName = (android.widget.TextView) (_mainActivity.findViewById(2131165333));  
        _mainActivity.btnSend = (android.widget.Button) (_mainActivity.findViewById(2131165219));  
        _mainActivity.etName = (android.widget.EditText) (_mainActivity.findViewById(2131165246));  
    }  
}  
```  
  
控件绑定的方式如下所示  
```  
@BindView(R.id.tv_name) TextView tvName;  
@BindView(R.id.btn_send) Button btnSend;  
@BindView(R.id.et_name) EditText etName;  
```  
  
## 项目基本结构  
1、首先在工程中新建一个 `Java Library`，命名为 `apt_processor`，用于存放 `Processor` 的实现类。  
  
其需要添加如下依赖：  
```  
dependencies {  
    implementation fileTree(dir: 'libs', include: ['*.jar'])  
    implementation 'com.google.auto.service:auto-service:1.0-rc2' //Google 开源的注解注册处理器(可选)  
    implementation 'com.squareup:javapoet:1.10.0' //square 开源的 Java 代码生成框架(可选)  
    implementation project(':apt_annotation') //需要依赖定义的注解  
}  
```  
  
- `auto-service` 是由 Google 开源的注解注册处理器  
- `JavaPoet` 是 square 开源的 Java 代码生成框架，可以很方便地通过其提供的 API 来生成指定格式（修饰符、返回值、参数、函数体等）的代码。  
  
实际上，上面两个依赖库并不是必须的，可以通过硬编码代码生成规则来替代。但使用这两个库后代码的可读性会更高，也能提高开发效率。  
  
2、再新建一个 `Java Library`，命名为 `apt_annotation` ，用于定义注解。  
  
3、在 `app Module` 中依赖这两个 Java Library：  
```  
implementation project(':apt_annotation') //需要依赖定义的注解  
annotationProcessor project(':apt_processor') //使用apt  
```  
  
这样子，我们需要的所有基础依赖关系就搭建好了  
![](index_files/3278dd7c-3450-489d-82a1-b27c632a0a05.png)  
  
## 编写代码生成规则  
首先观察自动生成的代码，可以归纳出几点需要实现的地方：  
- 1、文件和源 Activity 处在同个包名下  
- 2、类名以 `Activity名 + ViewBinding` 组成  
- 3、`bind()` 方法通过传入 Activity 对象来获取其声明的控件对象来对其进行实例化，这也是 ButterKnife 要求需要绑定的控件变量不能声明为 `private` 的原因  
  
`BindView` 注解的声明如下所示，放在 `apt_annotation` 中，注解值 `value` 用于声明 `viewId`：  
```  
@Retention(RetentionPolicy.CLASS)  
@Target(ElementType.FIELD)  
public @interface BindView {  
    int value();  
}  
```  
  
在 `apt_processor` 中创建 BindViewProcessor 类并继承 `AbstractProcessor` 抽象类，该抽象类含有一个抽象方法 `process()` 以及一个非抽象方法 `getSupportedAnnotationTypes()` 需要由我们来实现：  
```java  
@AutoService(Processor.class)  
public class BindViewProcessor extends AbstractProcessor {  
  
    @Override  
    public Set<String> getSupportedAnnotationTypes() {  
        Set<String> hashSet = new HashSet<>();  
        hashSet.add(BindView.class.getCanonicalName());  
        return hashSet; //指定目标注解对象  
    }  
  
    @Override  
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment environment) {  
        return true; //处理包含指定注解对象的代码元素  
    }  
}  
```  
  
基本步骤：  
- 通过`getSupportedAnnotationTypes()`方法指定目标注解对象，然后在`process()`方法中处理包含指定注解对象的代码元素  
- 要自动生成 `findViewById()` 方法，则需要获取到控件变量的引用以及对应的 `viewid`，所以需要先遍历出每个 `Activity` 包含的所有注解对象  
- `Element` 用于代表程序的一个元素，这个元素可以是：包、类、接口、变量、方法等多种概念  
- 这里以 Activity 对象作为 Key ，通过 map 来存储不同 Activity 下的所有注解对象  
- 获取到所有的注解对象后，就可以来构造 `bind()` 方法了  
- `MethodSpec` 是 `JavaPoet` 提供的一个概念，用于抽象出生成一个函数时需要的基础元素  
  
完整的代码声明如下所示  
```  
@AutoService(Processor.class)  
public class BindViewProcessor extends AbstractProcessor {  
      
    private Elements elementUtils;  
      
    @Override  
    public synchronized void init(ProcessingEnvironment processingEnv) {  
        super.init(processingEnv);  
        elementUtils = processingEnv.getElementUtils();  
    }  
      
    @Override  
    public Set<String> getSupportedAnnotationTypes() {  
        Set<String> hashSet = new HashSet<>();  
        hashSet.add(BindView.class.getCanonicalName());  
        return hashSet;  
    }  
      
    @Override  
    public SourceVersion getSupportedSourceVersion() {  
        return SourceVersion.latestSupported();  
    }  
      
    @Override  
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment environment) {  
        Map<TypeElement, Map<Integer, VariableElement>> typeElementMap = getTypeElementMap(environment);  
        for (TypeElement key : typeElementMap.keySet()) {  
            Map<Integer, VariableElement> elementMap = typeElementMap.get(key);  
            TypeSpec typeSpec = generateCodeByPoet(key, elementMap);  
            String packageName = elementUtils.getPackageOf(key).getQualifiedName().toString();  
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();  
            try {  
                javaFile.writeTo(processingEnv.getFiler());  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return true;  
    }  
      
    //以 Activity 对象作为 Key ，通过 map 来存储不同 Activity 下的所有注解对象  
    private Map<TypeElement, Map<Integer, VariableElement>> getTypeElementMap(RoundEnvironment environment) {  
        Map<TypeElement, Map<Integer, VariableElement>> typeElementMap = new HashMap<>();  
        Set<? extends Element> elementSet = environment.getElementsAnnotatedWith(BindView.class);  
        //Element 代表程序的一个元素，这个元素可以是：包、类、接口、变量、方法等，这里是获取所有包含指定注解的元素  
          
        //遍历所有包含 BindView 的注解对象  
        for (Element element : elementSet) {  
            VariableElement varElement = (VariableElement) element;//因为 BindView 的作用对象是 FIELD，因此可以直接强转  
            TypeElement typeElement = (TypeElement) varElement.getEnclosingElement();//返回封装此 Element 的最里层元素  
            //如果 Element 直接封装在另一个元素的声明中，则返回该封装元素，此处表示的即 Activity 类对象  
              
            Map<Integer, VariableElement> varElementMap = typeElementMap.get(typeElement);  
            if (varElementMap == null) {  
                varElementMap = new HashMap<>(); //看指定的key(即Activity)是否已经存在，如果不存在的话创建并添加到map中  
                typeElementMap.put(typeElement, varElementMap);  
            }  
              
            BindView bindAnnotation = varElement.getAnnotation(BindView.class); //获取注解  
            int viewId = bindAnnotation.value();//获取注解值  
            varElementMap.put(viewId, varElement);//将每个包含了 BindView 注解的字段对象以及其注解值保存起来  
        }  
        return typeElementMap;  
    }  
      
    /**  
     * 生成 Java 类，以 Activity名 + ViewBinding 进行命名  
     *  
     * @param typeElement   注解对象上层元素对象，即 Activity 对象  
     * @param varElementMap Activity 包含的注解对象以及注解的目标对象  
     */  
    private TypeSpec generateCodeByPoet(TypeElement typeElement, Map<Integer, VariableElement> varElementMap) {  
        return TypeSpec.classBuilder(typeElement.getSimpleName().toString() + "ViewBinding")  
            .addModifiers(Modifier.PUBLIC)  
            .addMethod(generateMethodByPoet(typeElement, varElementMap))  
            .build();  
    }  
      
    /**  
     * 生成方法  
     *  
     * @param typeElement   注解对象上层元素对象，即 Activity 对象  
     * @param varElementMap Activity 包含的注解对象以及注解的目标对象  
     */  
    private MethodSpec generateMethodByPoet(TypeElement typeElement, Map<Integer, VariableElement> varElementMap) {  
        ClassName className = ClassName.bestGuess(typeElement.getQualifiedName().toString());  
        String parameter = "_" + toLowerCaseFirstChar(className.simpleName());//方法参数名  
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")  
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)  
            .returns(void.class)  
            .addParameter(className, parameter);  
          
        for (int viewId : varElementMap.keySet()) {  
            VariableElement element = varElementMap.get(viewId);  
            String name = element.getSimpleName().toString();//被注解的字段名  
            String type = element.asType().toString();//被注解的字段的对象类型的全名称  
            String text = "{0}.{1}=({2})({3}.findViewById({4}));";  
            //把需要的参数元素填充进去，循环生成每一行的 findViewById 方法  
            methodBuilder.addCode(MessageFormat.format(text, parameter, name, type, parameter, String.valueOf(viewId)));  
        }  
        return methodBuilder.build();  
    }  
      
    //将首字母转为小写  
    private static String toLowerCaseFirstChar(String text) {  
        if (text == null || text.length() == 0 || Character.isLowerCase(text.charAt(0))) return text;  
        else return String.valueOf(Character.toLowerCase(text.charAt(0))) + text.substring(1);  
    }  
}  
```  
  
## 使用效果  
首先在 `MainActivity` 中使用 `BindView` 注解，然后 `Rebuild Project`，使编译器根据 `BindViewProcessor` 生成我们需要的代码  
  
`Rebuild` 结束后，可以看到，在 `generatedJava` 文件夹下自动生成了 `MainActivityViewBinding` 类  
  
![](index_files/b5407aa0-dc1a-46c3-a6fa-5762eb023b74.png)  
  
其源码为  
```java  
package leavesc.hello.apt;  
  
public class MainActivityViewBinding {  
    public static void bind(MainActivity _mainActivity) {  
        _mainActivity.btnSend = (android.widget.Button) (_mainActivity.findViewById(2131165218));  
        _mainActivity.tvName = (android.widget.TextView) (_mainActivity.findViewById(2131165327));  
        _mainActivity.etName = (android.widget.EditText) (_mainActivity.findViewById(2131165240));  
    }  
}  
```  
  
此时有两种方式可以用来触发 `bind()` 方法  
- 在 `MainActivity` 方法中直接调用 `MainActivityViewBinding` 的 `bind()` 方法  
- 调用`ButterKnife.bind(this);`并通过反射来触发 `MainActivityViewBinding` 的 `bind()` 方法  
  
```  
public class ButterKnife {  
      
    public static void bind(Activity activity) {  
        Class clazz = activity.getClass();  
        try {  
            Class bindViewClass = Class.forName(clazz.getName() + "ViewBinding");  
            Method method = bindViewClass.getMethod("bind", activity.getClass());  
            method.invoke(bindViewClass.newInstance(), activity);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}  
```  
  
两种方式各有优缺点：  
- 第一种方式在每次 `build project` 后才会生成代码，在这之前无法引用到对应的 `ViewBinding` 类  
- 第二种方式可以用固定的方法调用方式，但是相比方式一，反射会略微多消耗一些性能(推荐)  
  
但这两种方式的运行结果是完全相同的。  
  
2019-1-10  
