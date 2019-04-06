| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Template Method 模板方法 设计模式  
***  
目录  
===  

- [简介](#简介)
- [模板方法模式](#模板方法模式)
	- [抽象模板](#抽象模板)
	- [具体模板](#具体模板)
	- [客户端使用演示](#客户端使用演示)
  
# 简介   
定义一个操作中的`算法的骨架`，而将一些`步骤`延迟到子类中。  
模板方法使得子类可以不改变一个算法的`结构`即可重定义该算法的某些特定步骤的`细节`  
  
抽象模板类的方法分为两类：  
- 基本方法(`primitive method`)：由子类实现的方法，并且在模板方法被调用，客户端不关心这些基本方法，所以可以声明成 protected abstract   
- 模板方法(`template method`)：可以有多个, 一般是具体方法，也就是骨架，实现`对基本方法的调度`，完成固定的逻辑。一般声明为final以防止被覆写  
  
模板方法模式需要开发抽象类和具体子类的程序员之间的协作。一个程序员负责给出一个算法的`轮廓和骨架`，另一些程序员则负责给出这个算法的各个`逻辑步骤`。  
  
代表这些具体逻辑步骤的方法称做基本方法，而将这些基本方法汇总起来的方法叫做模板方法，这个设计模式的名字就是从此而来的。  
  
优点  
- 模板方法模式通过把`不变的行为`搬移到超类，去除了子类中的重复代码。   
- 子类实现算法的某些细节，有助于算法的扩展。   
- 通过一个父类调用子类实现的操作，通过子类扩展增加新的行为，符合“开闭原则”。   
  
适用场景：让子类可以重写方法的一部分，而不是整个重写，你可以控制子类需要重写那些操作。   
  
# 模板方法模式  
## 抽象模板  
```java  
public abstract class Template {  
    //【基本方法】：由子类实现，并且在模板方法中被调用的方法；客户端不关心这些基本方法，所以可以声明成 protected abstract  
    protected abstract void printName();  
    protected abstract void printAge();  
    protected void printDate() {  
        System.out.println(new SimpleDateFormat("yyyy.MM.dd HH-mm-ss").format(new Date()));  
    }  
      
    //【模板方法】：可以有多个, 一般是具体方法，也就是骨架，实现对基本方法的调度，完成固定的逻辑；一般声明为final以防止被覆写  
    public final void print() {  
        printName();  
        printAge();  
        printDate();  
    }  
}  
```  
  
## 具体模板  
```java  
public class TemplateConcrete extends Template {  
    @Override  
    protected void printName() {  
        System.out.println("包青天");  
    }  
    @Override  
    protected void printAge() {  
        System.out.println(27);  
    }  
}  
```  
  
## 客户端使用演示  
```java  
public class Test {  
    public static void main(String[] args) {  
        Template temp = new TemplateConcrete();  
        temp.print();  
    }  
}  
```  
2018-2-22  
