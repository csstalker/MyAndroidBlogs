| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [享元模式](#享元模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 享元模式  
## 简介  
  
在JAVA语言中，String类型就是使用了享元模式，JAVA中的字符串常量都是存在常量池中的，JAVA会确保`一个字符串常量在常量池中只有一个拷贝`，避免了在创建N多相同对象时所产生的不必要的大量的资源消耗。  
  
```java  
String string1 = "abc";  
String string2 = "abc";  
String string3 = new String("abc");  
String string4 = new String("abc");  
System.out.println(string1 == string2);//true  
System.out.println(string3 == string4 || string3 == string1 || string4 == string1);//false  
```  
  
享元模式是对象的结构模式，享元模式以共享的方式高效地支持大量的细粒度对象。  
  
> Flyweight在拳击比赛中指最轻量级拳击选手，即“蝇量级”，这里选择使用“享元模式”的意译，是因为这样更能反映模式的用意。  
  
享元模式使用共享物件，用来尽可能减少内存使用量以及分享资讯给尽可能多的相似物件；它适合用于只是因重复而导致使用无法令人接受的大量内存的大量物件。通常物件中的部分状态是可以分享。常见做法是把它们放在外部数据结构，当需要使用时再将它们传递给享元。  
  
享元对象能做到共享的关键是区分内蕴状态Internal State和外蕴状态External State。  
- 一个内蕴状态是存储在享元对象内部的，并且是不会随环境的改变而有所不同。因此，一个享元可以具有内蕴状态并可以共享。  
- 一个外蕴状态是随环境的改变而改变的、不可以共享的。享元对象的外蕴状态必须由客户端保存，并在享元对象被创建之后，在需要使用的时候再传入到享元对象内部。  
- 外蕴状态不可以影响享元对象的内蕴状态，它们是相互独立的。  
  
享元模式所涉及到的角色如下：  
- 抽象享元(Flyweight)角色 ：给出一个抽象接口，以规定出所有具体享元角色需要实现的方法。  
- 具体享元(ConcreteFlyweight)角色：实现抽象享元角色所规定出的接口。如果有内蕴状态的话，必须负责为内蕴状态提供存储空间。  
- 享元工厂(FlyweightFactory)角色 ：负责创建和管理享元角色。`本角色必须保证享元对象可以被系统适当地共享`。当一个客户端对象调用一个享元对象的时候，享元工厂角色会检查系统中是否已经有一个符合要求的享元对象，如果已经有了，享元工厂角色就应当提供这个已有的享元对象；如果系统中没有一个适当的享元对象的话，享元工厂角色就应当创建一个合适的享元对象。  
  
享元模式的优点在于它大幅度地降低内存中对象的数量，但是，它做到这一点所付出的代价也是很高的：  
- 享元模式使得系统更加复杂。为了使对象可以共享，需要将一些状态外部化，这使得程序的逻辑复杂化  
- 享元模式将享元对象的状态外部化，而读取外部状态使得运行时间稍微变长  
  
作用：共享对象，节省内存  
优点：享元模式可以降低内存中的对象数量，提高性能  
缺点：需要将一些状态外部化，程序逻辑将变得复杂，且在读取外部状态时耗时增加  
  
## 案例  
抽象享元类  
```java  
public interface IFlyweight {  
    action(int arg);//参数是外蕴状态，通过这个接口，享元类可以接受并作用于外部状态  
}  
```  
  
具体享元类  
```java  
public class ConcreteFlyweight implements IFlyweight {  
    private String intrinsicState = null;//有一个内蕴状态，内蕴状态在对象创建之后，就不能再改变了  
    public ConcreteFlyweight(String intrinsicState) {  
        this.intrinsicState = intrinsicState; //一般在构造享元对象时通过参数确定内蕴状态  
    }  
  
    @Override  
    public void action(int arg) { //外蕴状态作为参数传入方法中，可以改变方法的行为，但并不改变对象的内蕴状态  
        System.out.println("参数值: " + arg + "   intrinsicState = " + this.intrinsicState);  
    }  
}  
```  
  
享元工厂类  
客户端不可以直接构造享元类对象，只能由享元工厂创建并管理，在一个项目中只会存在一个享元工厂，因此用单例来获取，而享元对象则通过工厂获取，有则取用，无则创建  
```java  
public class FlyweightFactory {  
    private Map<String, IFlyweight> flyweights = new HashMap<>();//存储已创建的对象  
      
    public IFlyweight getFlyweight(String arg) {  
        IFlyweight flyweight = flyweights.get(arg); //先从系统中取，如果存在直接使用，如果不存在则创建并保存起来  
        if (flyweight == null) {  
            flyweight = new ConcreteFlyweight(arg);  
            flyweights.put(arg, flyweight);  
        }  
        return flyweight;  
    }  
      
    //以下是标准的单例模式写法  
    private FlyweightFactory() {  
    }  
      
    public static FlyweightFactory getInstance() {  
        return SingleHolder.INSTANCE;  
    }  
      
    private static class SingleHolder {  
        private static final FlyweightFactory INSTANCE = new FlyweightFactory();  
    }  
}  
```  
  
2016-03-17  
