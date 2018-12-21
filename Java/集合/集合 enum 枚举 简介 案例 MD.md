| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
集合 enum 枚举 简介 案例  
***  
目录  
===  

- [Enum 类](#enum-类)
	- [构造方法](#构造方法)
	- [静态方法](#静态方法)
	- [新增的方法](#新增的方法)
	- [Object 中的方法](#object-中的方法)
- [关键字 enum](#关键字-enum)
- [测试代码](#测试代码)
	- [最简单的定义形式](#最简单的定义形式)
	- [详细使用案例](#详细使用案例)
	- [测试](#测试)
	- [在 switch 语句中使用 enum](#在-switch-语句中使用-enum)
  
# Enum 类  
这是所有 Java 语言枚举类型的公共基本类。  
`public abstract class Enum<E extends Enum<E>> extends Object implements Comparable<E>, Serializable`  
  
## 构造方法  
`protected  Enum(String name, int ordinal)`  单独的构造方法。  
- 程序员无法调用此构造方法。  
- 该构造方法用于由响应枚举类型声明的编译器发出的代码。  
- 参数 name - - 此枚举常量的名称，它是用来声明该常量的标识符。  
- 参数 ordinal - - 枚举常量的序数(它在枚举声明中的位置，其中初始常量序数为零)。  
  
## 静态方法  
`static <T extends Enum<T>>  T  valueOf(Class<T> enumType, String name)`  返回带指定名称的指定枚举类型的枚举常量。  
- 名称必须与在此类型中声明枚举常量所用的标识符完全匹配。不允许使用额外的空白字符。  
- 参数：enumType - 要从中返回常量的枚举类型的 Class 对象  
- 参数：name - 要返回的常量名称  
- 抛出：  
    - IllegalArgumentException - 如果指定枚举类型不包含指定名称的常量，或者指定类对象不表示枚举类型  
    - NullPointerException - 如果 enumType 或 name 为空  
  
另外两个没有在API文档中出现但实际存在的静态方法：  
- `static  T  valueOf(String name)`  返回带指定名称的当前枚举类型的枚举常量。  
- `static  T[]  values()`  返回当前枚举类型的枚举常量数组。  
  
## 新增的方法  
`Class<E>  getDeclaringClass()`  返回与此枚举常量的枚举类型相对应的 Class 对象。  
- 当且仅当 e1.getDeclaringClass() == e2.getDeclaringClass() 时，两个枚举常量 e1 和 e2 的枚举类型才相同。  
- 由该方法返回的值不同于由 Object.getClass() 方法返回的值， Object.getClass() 方法用于带有特定常量的类主体的枚举常量。  
  
`String  name()`  返回此枚举常量的名称，在其枚举声明中对其进行声明。  
- 与此方法相比，大多数程序员应该优先考虑使用 toString() 方法，因为 toString 方法返回更加用户友好的名称。  
- 该方法主要设计用于特殊情形，其正确性取决于获取正确的名称，其名称不会随版本的改变而改变。  
  
`int  ordinal()`  返回枚举常量的序数(它在枚举声明中的位置，其中初始常量序数为零)。  
- 大多数程序员不会使用此方法。它被设计用于复杂的基于枚举的数据结构，比如 EnumSet 和 EnumMap。  
  
## Object 中的方法  
`int  compareTo(E o)`  比较此枚举与指定对象的顺序。  
- 在该对象小于、等于或大于指定对象时，分别返回负整数、零或正整数。   
- 枚举常量只能与相同枚举类型的其他枚举常量进行比较。  
- 该方法实现的自然顺序就是声明常量的顺序。  
  
`boolean  equals(Object other)`  当指定对象等于此枚举常量时，返回 true。  
  
`protected  void  finalize()`   枚举类不能有 finalize 方法。  
- 类 Object 中的 finalize 方法的作用是：当垃圾回收器确定不存在对该对象的更多引用时，由对象的垃圾回收器调用此方法。  
  
`int  hashCode()`  返回枚举常量的哈希码。  
  
`String  toString()`  返回枚举常量的名称，它包含在声明中。  
- 它包含在声明中。可以重写此方法，虽然一般来说没有必要。  
- 当存在更加“程序员友好的”字符串形式时，应该使用枚举类型重写此方法。  
  
`protected  Object  clone()`  throws CloneNotSupportedException  返回此实例的一个副本。  
- 如果对象的类不支持 Cloneable 接口，则抛出 CloneNotSupportedException。这可保证永远不会复制枚举，这对于保留其“单元素”状态是必需的。  
- 如果对象的类不支持 Cloneable 接口，则重写 clone 方法的子类也会抛出此异常，以指示无法复制某个实例。  
  
# 关键字 enum  
创建枚举类型要使用 enum 关键字，隐含了所创建的类型都是 java.lang.Enum 类的子类。枚举类型符合通用模式 Class Enum<E extends Enum<E>>，而 E 表示枚举类型的名称。枚举类型的每一个值都将映射到 protected Enum(String name, int ordinal) 构造函数中，在这里，每个值的名称都被转换成一个字符串，并且序数设置表示了此设置被创建的顺序。  
  
enum 的语法结构尽管和 class 的语法不一样，但是经过编译器编译之后产生的是一个 class 文件。该 class 文件经过反编译可以看到实际上是生成了一个类，该类继承了 java.lang.Enum<E>。比如：  
  
```java  
public enum EnumTest {  
    MON, TUE;  
}  
```  
      
首先编译一下(使用 java 命令)，然后直接读取编译后的 EnumTest.class 文件，内容如下：  
```java  
public enum EnumTest {  
    MON,  
    TUE,  
    private EnumTest() {   
    }  
}  
```  
  
经过反编译之后得到的内容如下：  
```java  
javap EnumTest.class  
Compiled from "EnumTest.java"  
public final class test.EnumTest extends java.lang.Enum<test.EnumTest> { //继承自 Enum  
  public static final test.EnumTest MON;  
  public static final test.EnumTest TUE;  
  public static test.EnumTest[] values();    //多了一个 values 方法  
  public static test.EnumTest valueOf(java.lang.String);    //重载了一个 valueOf 方法  
  static {};  
}  
```  
  
所以，实际上 enum 就是一个 class，只不过 java 编译器帮我们做了语法的解析和编译而已。  
  
总结：  
可以把 enum 看成是一个普通的 class，它们都可以定义一些属性和方法，不同之处是：enum 不能使用 extends 关键字继承其他类，因为 enum 已经继承了 java.lang.Enum (单一继承)。  
  
# 测试代码  
## 最简单的定义形式  
```java  
public enum EnumTest {  
    MON, TUE, WED, THU, FRI, SAT, SUN; //这段代码实际上调用了7次构造方法 Enum(String name, int ordinal)：  
}  
```  
  
## 详细使用案例  
给 enum 自定义属性和方法：  
```java  
enum MyEnum {  
    MON("bqt1", 1), TUE("bqt2", 2) {  
        @Override  
        public String tips() {  
            return "黑色星期二";  
        }  
    },  
    WED("bqt3", 3) {  
        @Override  
        public boolean isBoy() {  
            return true;  
        }  
    },  
    THU("bqt1", 1);  
  
    // 成员变量  
    public String key;  
    public int value;  
  
    // 构造方法  
    MyEnum(String key, int value) {  
        this.key = key;  
        this.value = value;  
    }  
  
    public String tips() {  
        return "Nothing";  
    }  
  
    public boolean isBoy() {  
        return false;  
    }  
}  
```  
  
## 测试  
```java  
System.out.println(MyEnum.MON instanceof Enum); // true  
System.out.println(MyEnum.valueOf("MON") + "  " + Enum.valueOf(MyEnum.class, "MON")); // MON  MON  
  
//常用方法  
System.out.println(MyEnum.MON.name() + "  " + MyEnum.MON.toString()); // MON  MON  
System.out.println(MyEnum.MON.ordinal() + "  " + MyEnum.MON.getDeclaringClass()); // 0  class MyEnum  
System.out.println(MyEnum.MON.equals(MyEnum.THU) + " " + MyEnum.MON.compareTo(MyEnum.THU)); // false -3  
  
//给 enum 自定义属性和方法  
System.out.println(MyEnum.MON.key + "  " + MyEnum.MON.value); // bqt1  1  
System.out.println(MyEnum.MON.tips() + "  " + MyEnum.TUE.tips()); // Nothing  黑色星期二  
System.out.println(MyEnum.MON.isBoy() + "  " + MyEnum.WED.isBoy()); // false  true  
  
//遍历  
MyEnum[] array = MyEnum.values();  
System.out.println(Arrays.toString(array)); // [MON, TUE, WED, THU]  
for (MyEnum myEnum : MyEnum.values()) {  
    System.out.print(myEnum.toString() + "  "); // MON  TUE  WED  THU    
}  
```  
  
## 在 switch 语句中使用 enum  
比如上面的 MyEnum ，如果按下面形式去使用：  
```java  
MyEnum myEnum = MyEnum.TUE;  
switch (myEnum) {  
case MyEnum.MON:  
    break;  
case MyEnum.TUE:  
    break;  
default:  
    break;  
}  
```  
  
此时会提示：an enum switch case label must be the unqualified name of an enumeration constant  
意思是: 枚举的 switch case 标签必须为枚举常量的非限定名称。  
什么意思呢？意思就是 case 语句中只能写枚举类定义的变量名称，不能加类名。  
  
正常代码如下：  
```java  
MyEnum myEnum = MyEnum.TUE;  
switch (myEnum) {  
case MON:  
    break;  
case TUE:  
    break;  
default:  
    break;  
}  
```  
  
为什么必须这么写呢？  
我们看 [官方文档](https://docs.oracle.com/javase/specs/jls/se8/html/jls-14.html#jls-14.11) 中对 switch 语句的描述  
  
>   
- If the type of the switch statement’s Expression is an enum type, then every case constant associated with the switch statement must be an enum constant of that type.  
- Every case label has a case constant, which is either a constant expression or the name of an enum constant.   
- If the type of the switch statement's语句 Expression is an enum type, then every case constant associated with关联的 the switch statement must be an enum constant of that type.  
- If the switch statement's Expression is of a reference type引用类型, that is, String or a boxed primitive type or an enum type, then an exception will be thrown will occur if the Expression evaluates to null at run time.  
- A Java compiler is encouraged鼓励 (but not required) to provide a warning if a switch on an enum-valued expression lacks缺少 a default label and lacks case labels for one or more of the enum's constants. Such a switch will silently do nothing if the expression evaluates to one of the missing constants.  
  
里面也貌似并没有细说为何不能带限定名。  
  
2018-7-16  
