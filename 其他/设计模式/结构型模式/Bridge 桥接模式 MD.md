| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [桥接模式](#桥接模式)
	- [简介](#简介)
	- [案例1](#案例1)
	- [案例2](#案例2)
  
# 桥接模式  
## 简介  
将抽象部分与实现部分分离，使它们都可以独立的变化。  
业务抽象角色引用业务实现角色，或者说业务抽象角色的部分实现是由业务实现角色完成的  
  
Bridge模式基于类的最小设计原则，通过使用封装，聚合以及继承等行为来`让不同的类承担不同的责任`。它的主要特点是把抽象与行为实现分离开来，从而可以`保持各部分的独立性`以及应对它们的功能扩展。  
  
可以这么理解，抽象是一个事物的本身的特征，行为是一个事物可以做的动作，特征是相对独享的，行为是可以共享的。  
  
作用：解耦。  
  
适用性  
- 你不希望在抽象和它的实现部分之间有一个固定的绑定关系。例如这种情况可能是因为在程序运行时刻，实现部分应可以选择或者切换。  
- 类的抽象以及它的实现都应该可以通过生成子类的方法加以扩充。这时Bridge模式使你可以对不同的抽象接口和实现部分进行组合，并分别对它们进行扩充。  
- 对一个抽象的实现部分的修改应对客户不产生影响，即客户的代码不必重新编译。  
- 想在多个对象间共享实现（可能使用引用计数），但同时要求客户并不知道这一点。  
  
Window 与 WindowManager 之间的桥接模式  
Window 类和 PhoneWindow 类为抽象部分，PhoneWindow 为 Window 类的唯一实现子类，在 Window 类中，持有了一个 WindowManager 类的引用，并且在 setWindowManager 方法中将其赋值为了 WindowManagerImpl 对象，对应着的 WindowManager 接口和 WindowManagerImpl 类就组成了实现部分，所以说这四个类使用的就是典型的桥接模式。Window 中的 addView，removeView 等操作都桥接给了 WindowManagerImpl 去处理。  
  
  
## 案例1  
  
抽象部分  
业务抽象角色  
```java  
public abstract class Person {  
    public Clothing clothing;  
    public String type;  
    public abstract void dress();  
}  
```  
  
```java  
class Man extends Person {  
    public Man() {  
        this.type = "男人";  
    }  
    @Override  
    public void dress() {  
        clothing.personDressCloth(this);  
    }  
}  
```  
  
```java  
class Lady extends Person {  
    public Lady() {  
        this.type = "女人";  
    }  
    @Override  
    public void dress() {  
        clothing.personDressCloth(this);  
    }  
}  
```  
  
实现部分  
业务实现角色（穿衣服）是一个事物（人）可以做的动作，在程序运行时刻，实现部分可以选择或者切换  
```java  
public abstract class Clothing {  
    public abstract void personDressCloth(Person person);//人穿衣服  
}  
```  
  
```java  
class Trouser extends Clothing {  
    @Override  
    public void personDressCloth(Person person) {  
        System.out.println(person.type + "穿裤子");  
    }  
}  
```  
  
```java  
class Jacket extends Clothing {  
    @Override  
    public void personDressCloth(Person person) {  
        System.out.println(person.type + "穿马甲");  
    }  
}  
```  
  
```java  
public class Test {  
    public static void main(String[] args) {  
        Person man = new Man();  
        Person lady = new Lady();  
        Clothing jacket = new Jacket();  
        Clothing trouser = new Trouser();  
  
        jacket.personDressCloth(man);  
        trouser.personDressCloth(man);  
  
        jacket.personDressCloth(lady);  
        trouser.personDressCloth(lady);  
    }  
}  
```  
  
## 案例2  
  
例如一辆赛车搭配的是硬胎就能够在干燥的马路上行驶，而如果要在下雨的路面行驶，就需要搭配雨胎了。这种根据行驶的路面不同，需要搭配不同的轮胎的变化的情况，我们从软件设计的角度来分析，就是一个系统由于自身的逻辑，会有两个或多个维度的变化，而为了应对这种变化，我们就可以使用桥接模式来进行系统的解耦。   
  
桥接模式，作用是`将一个系统的抽象部分和实现部分分离`，使它们`都可以独立地进行变化`，对应到上面就是`赛车的种类可以相对变化`，`轮胎的种类可以相对变化`，形成一种交叉的关系，最后的结果就是一种赛车对应一种轮胎就能够成功产生一种结果和行为。   
  
```java  
public abstract class Car {  
    private ITire tire;  
  
    public Car(ITire tire) {  
        this.tire = tire;  
    }  
  
    public ITire getTire() {  
        return tire;  
    }  
  
    public abstract void run();  
}  
```  
  
```java  
public class RacingCar extends Car{   
    public RacingCar(ITire tire) {  
        super(tire);  
    }  
  
    @Override  
    public void run() {  
        Log.e("shawn", "racing car " + getTire().run());  
    }  
}  
```  
  
```java  
public class SedanCar extends Car{  
    public SedanCar(ITire tire) {  
        super(tire);  
    }  
  
    @Override  
    public void run() {  
        Log.e("shawn", "sedan car " + getTire().run());  
    }  
}  
```  
  
实现部分  
```java  
public interface ITire {  
    String run();  
}  
```  
  
```java  
public class RainyTire implements ITire{  
    @Override  
    public String run() {  
        return "run on the rainy road.";  
    }  
}  
```  
  
```java  
public class SandyTire implements ITire{  
    @Override  
    public String run() {  
        return "run on the sandy road.";  
    }  
}  
```  
  
测试代码：  
```java  
Car car = new SedanCar(new RainyTire()); //有四种组合  
car.run();  
```  
  
2016-03-30  
