| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [工厂模式](#工厂模式)
	- [简单工厂模式 Simple Factory](#简单工厂模式-Simple-Factory)
		- [抽象产品类](#抽象产品类)
		- [具体产品类](#具体产品类)
		- [工厂类](#工厂类)
		- [客户端使用](#客户端使用)
	- [工厂方法模式 Factory Method](#工厂方法模式-Factory-Method)
		- [抽象产品类(产品接口)](#抽象产品类产品接口)
		- [具体产品类](#具体产品类)
		- [工厂接口类](#工厂接口类)
		- [具体工厂类](#具体工厂类)
		- [客户端使用](#客户端使用)
	- [抽象工厂模式 Abstract Factory](#抽象工厂模式-Abstract-Factory)
		- [产品](#产品)
			- [产品接口](#产品接口)
			- [抽象产品类(产品等级)](#抽象产品类产品等级)
			- [具体产品类(产品族)](#具体产品类产品族)
		- [工厂](#工厂)
			- [工厂接口](#工厂接口)
			- [抽象工厂类(产生产品等级)](#抽象工厂类产生产品等级)
			- [具体工厂类](#具体工厂类)
		- [客户端使用](#客户端使用)
  
# 工厂模式  
  
工厂模式是我们最常用的实例化对象模式了，是用工厂方法代替new操作的一种模式。著名的Jive论坛 ,就大量使用了工厂模式，工厂模式在Java程序系统可以说是随处可见。因为工厂模式就相当于创建实例对象的new，我们经常要根据类Class生成实例 对象，如A a=new A() 工厂模式也是用来创建实例对象的，所以以后new时就要多个心眼，是否可以考虑使用工厂模式，虽然这样做，可能多做一些工作，但会给你系统带来更大的可扩 展性和尽量少的修改量。  
  
  
简单工厂、工厂方法、抽象工厂的区别  
  
简单工厂，只有一个`产品接口`  
- 定义：专门定义一个类来负责创建其他类的实例，被创建的实例通常具有共同的父类或实现同一接口   
- 优点：客户端可以直接消费产品，而不必关心具体产品的实现，消除了客户端直接创建产品对象的责任，实现了对责任的分割   
- 局限：工厂类记录了所有同类产品的创建逻辑，一旦不能正常工作，整个系统都会受到影响；当产品种类多、结构复杂的时候，把所有创建工作放进一个工厂中来，会使后期程序的扩展较为困难  
- 简单工厂模式没有抽象工厂类且其工厂类的工厂方法是静态的  
  
工厂方法，既有`产品接口`也有`工厂接口`  
- 定义：在简单工厂的基础上，为工厂类定义了工厂接口，让其子类决定实例化哪个产品类  
- 优点：简单工厂是把创建产品的职能都全部放在一个类里面，而工厂方法则是`把不同的产品放在实现了工厂接口的不同工厂类里面`，分割了工厂类的职能。   
  
抽象工厂  
- 定义：抽象工厂提供一个固定的接口，用于创建一系列关联或者相依存的对象，而不必指定其具体类或其创建的细节。  
- 特点：工厂方法用来创建一个产品，它没有分类的概念，而抽象工厂则用于创建一系列产品，所以产品分类成了抽象工厂的重点。  
  
## 简单工厂模式 Simple Factory  
  
简单工厂模式（Simple Factory Pattern）又叫静态工厂方法模式（Static FactoryMethod Pattern），是通过专门`定义一个类来负责创建其他类的实例`，被创建的实例通常都具有共同的父类或接口。  
  
作用：`代替构造函数创建对象`   
  
简单工厂模式中包含的角色及其相应的职责如下：  
- 工厂角色：这是简单工厂模式的核心，由它负责创建所有的类的内部逻辑。  
- 抽象产品角色：简单工厂模式所创建的所有对象的父类，它负责描述所有实例所共有的公共接口。   
- 具体产品角色：简单工厂所创建的具体实例对象，这些具体的产品往往都拥有共同的父类。   
  
JDK中体现：Integer.valueOf、Class.forName   
  
### 抽象产品类  
  
女娲造人案例  
  
首先定义人类的总称，可以通过抽象类或接口定义  
```java  
public interface IHuman {  
    public void talk();  
}  
```  
  
### 具体产品类  
然后定义你所需要的具体的人种，不同的产品具有不同的特性或功能  
```java  
class WhiteHuman implements IHuman {  
    @Override  
    public void talk() {  
        System.out.println("白色人种会说English");  
    }  
}  
```  
  
```java  
class BlackHuman implements IHuman {  
    @Override  
    public void talk() {  
        System.out.println("黑人可以说话，一般人听不懂");  
    }  
}  
```  
  
```java  
class YellowHuman implements IHuman {  
    @Override  
    public void talk() {  
        System.out.println("黄色人种会说汉语");  
    }  
}  
```  
  
### 工厂类  
```java  
public class HumanFactory {  
    public static IHuman createHuman(Class<? extends IHuman> c) { //通过泛型限定了要生产的对象的类型  
        //注意，生成产品的方式可以是各种各样的，比如最常见的方式为根据传入的String或int值，构造出不同的对象  
        IHuman human = null;  
        try {  
            human = (IHuman) Class.forName(c.getName()).newInstance(); //这里是通过反射方式生成的  
        } catch (Exception e) {  
            System.out.println("构造失败");  
        }  
        return human;  
    }  
}  
```  
  
> 由于简单工厂模式新增产品时需要直接修改工厂类，违反了开放封闭原则。因此可以使用反射来创建实例对象，确保能够遵循开放封闭原则。  
  
### 客户端使用  
```java  
IHuman yellowHuman = HumanFactory.createHuman(YellowHuman.class);  
yellowHuman.talk();  
```  
  
## 工厂方法模式 Factory Method  
  
定义一个用于创建对象的接口，让子类决定实例化哪个类。  
  
定义了一个工厂接口，由抽象工厂类的子类决定要实例化的类是抽象产品类的哪一个子类。  
  
工厂方法让类的实例化推迟到子类。  
  
角色说明：  
- Product（抽象产品类）：要创建的复杂对象，定义对象的公共接口。  
- ConcreteProduct（具体产品类）：实现Product接口。  
- Factory（抽象工厂类）：该方法返回一个Product类型的对象。  
- ConcreteFactory（具体工厂类）：返回ConcreteProduct实例。  
  
优点  
- 符合开放封闭原则。新增产品时，只需增加相应的具体产品类和相应的工厂子类即可。  
- 符合单一职责原则。每个具体工厂类只负责创建对应的产品。  
  
缺点  
- 一个具体工厂只能创建一种具体产品，增加新产品时，还需增加相应的工厂类，系统类的个数将成对增加，增加了系统的复杂度和性能开销。  
- 引入的抽象类也会导致类结构的复杂化。  
  
工厂方法模式新增产品时只需新建一个工厂类即可，符合开放封闭原则；而简单工厂模式需要直接修改工厂类，违反了开放封闭原则。  
  
### 抽象产品类(产品接口)  
```java  
public interface IWork {  
    void doWork();  
}  
```  
  
### 具体产品类  
```java  
class StudentWork implements IWork {  
    @Override  
    public void doWork() {  
        System.out.println("学生的工作是做作业!");  
    }  
}  
```  
  
```java  
class TeacherWork implements IWork {  
    @Override  
    public void doWork() {  
        System.out.println("老师的工作是审批作业!");  
    }  
}  
```  
  
### 工厂接口类  
工厂方法是创建一个框架，让【子类】决定要如何实现具体的产品。   
```java  
public interface IWorkFactory {  
    IWork getWork();  
}  
```  
  
### 具体工厂类  
根据不同的产品，定义不同的具体工厂类  
```java  
class StudentWorkFactory implements IWorkFactory {  
    @Override  
    public IWork getWork() {  
        return new StudentWork();//子类决定如何实现具体的产品  
    }  
}  
```  
  
```java  
class TeacherWorkFactory implements IWorkFactory{  
    @Override  
    public IWork getWork() {  
        return new TeacherWork();//子类决定如何实现具体的产品  
    }  
}  
```  
  
### 客户端使用  
```java  
IWorkFactory studentWorkFactory = new StudentWorkFactory();  
IWork studentWork = studentWorkFactory.getWork();//使用StudentWorkFactory构建StudentWork  
studentWork.doWork();//学生的工作是做作业!  
  
IWorkFactory teacherWorkFactory = new TeacherWorkFactory();  
IWork teacherWork = teacherWorkFactory.getWork();//使用TeacherWorkFactory构建TeacherWork  
teacherWork.doWork();//老师的工作是审批作业!  
```  
  
## 抽象工厂模式 Abstract Factory  
  
为创建一组相关或者相互依赖的对象提供一个接口，而无需指定它们的具体类。  
  
工厂方法模式每个工厂只能创建一种类型的产品，而抽象工厂模式则能够创建多种类型的产品。  
> 例如：硬盘工厂只生产硬盘这种产品，而电脑工厂则组合不同的硬盘、内存、CPU等生产出电脑来。  
  
角色说明：  
- AbstractProduct（抽象产品类）：定义产品的公共接口。  
- ConcreteProduct（具体产品类）：定义产品的具体对象，实现抽象产品类中的接口。  
- AbstractFactory（抽象工厂类）：定义工厂中用来创建不同产品的方法。  
- ConcreteFactory（具体工厂类）：实现抽象工厂中定义的创建产品的方法。  
  
应用场景：生产多个产品组合的对象时。  
优点：代码解耦，创建实例的工作与使用实例的工作分开，使用者不必关心类对象如何创建。  
缺点：如果增加新的产品，则修改抽象工厂和所有的具体工厂，违反了开放封闭原则   
  
工厂方法模式与抽象工厂模式比较  
- 工厂方法用来创建一个产品，它没有分类的概念，而抽象工厂则用于创建一系列产品，所以`产品分类`成了抽象工厂的重点。  
- 在工厂方法模式中，具体工厂负责生产具体的产品，每一个具体工厂对应一种具体产品，工厂方法具有唯一性；抽象工厂模式则可以提供多个产品对象，而不是单一的产品对象。  
  
### 产品  
#### 产品接口  
定义人类的统称  
```java  
public interface IHuman {  
    void talk();  
    void sex();//定义性别，注意，这个是用来给人类【分类】的，在抽象工厂中，很重要的一步就是合理的分类  
}  
```  
  
#### 抽象产品类(产品等级)  
人类的接口定义好，然后根据接口创建两个抽象类，也就是两个【产品等级】  
```java  
public abstract class AbstractYellowHuman implements IHuman {  
    @Override  
    public void talk() {  
        System.out.println("黄种人会说汉语");  
    }  
}  
```  
  
```java  
public abstract class AbstractWhiteHuman implements IHuman {  
    @Override  
    public void talk() {  
        System.out.println("白色人种会说English");  
    }  
}  
```  
  
#### 具体产品类(产品族)  
然后就是些实现类了  
```java  
public class YellowFemaleHuman extends AbstractYellowHuman {  
    @Override  
    public void sex() {  
        System.out.println("女性黄种人");  
    }  
}  
```  
  
```java  
public class YellowMaleHuman extends AbstractYellowHuman {  
    @Override  
    public void sex() {  
        System.out.println("男性黄种人");  
    }  
}  
```  
  
```java  
public class WhiteFemaleHuman extends AbstractWhiteHuman {  
    @Override  
    public void sex() {  
        System.out.println("女性白种人");  
    }  
}  
```  
  
```java  
public class WhiteMaleHuman extends AbstractWhiteHuman {  
    @Override  
    public void sex() {  
        System.out.println("男性白种人");  
    }  
}  
```  
  
### 工厂  
#### 工厂接口  
```java  
public interface IHumanFactory {  
    IHuman createYellowHuman();//制造黄色人种  
    IHuman createWhiteHuman();//制造白色人种  
}  
```  
  
#### 抽象工厂类(产生产品等级)  
这个抽象类的目的就是减少下边实现类的代码量。  
```java  
public abstract class AbstractHumanFactory implements IHumanFactory {  
    protected IHuman createHuman(HumanEnum humanEnum) {  
        IHuman human = null; //给定一个性别人种，创建一个人类出来 专业术语是产生产品等级  
        try {  
            human = (IHuman) Class.forName(humanEnum.getValue()).newInstance();//直接产生一个实例  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return human;  
    }  
}  
```  
  
#### 具体工厂类  
男性创建工厂，只创建男性  
```java  
public class MaleHumanFactory extends AbstractHumanFactory {  
    //创建一个男性白种人  
    @Override  
    public IHuman createWhiteHuman() {  
        return super.createHuman(HumanEnum.WhiteMaleHuman);  
    }  
    //创建一个男性黄种人  
    @Override  
    public IHuman createYellowHuman() {  
        return super.createHuman(HumanEnum.YelloMaleHuman);  
    }  
}  
```  
  
女性创建工厂，只创建女性  
```java  
public class FemaleHumanFactory extends AbstractHumanFactory {  
    //创建一个女性白种人  
    @Override  
    public IHuman createWhiteHuman() {  
        return super.createHuman(HumanEnum.WhiteFemaleHuman);  
    }  
    //创建一个女性黄种人\  
    @Override  
    public IHuman createYellowHuman() {  
        return super.createHuman(HumanEnum.YelloFemaleHuman);  
    }  
}  
```  
  
### 客户端使用  
产品定义好了，工厂也定义好了，万事俱备只欠东风，那咱就开始造人吧，哦，不对，女娲开始造人了：  
```java  
public class Test {  
    //女娲建立起了两条生产线，分别是男性生产线和女性生产线  
    public static void main(String[] args) {  
        //第一条生产线，男性生产线，可以生产各种各样的男性  
        IHumanFactory maleHumanFactory = new MaleHumanFactory();  
        IHuman maleYellowHuman = maleHumanFactory.createYellowHuman();//生产黄色的男性  
        IHuman maleWhiteHuman = maleHumanFactory.createWhiteHuman();//生产白色的男性  
  
        //第二条生产线，女性生产线，可以生产各种各样的女性  
        IHumanFactory femaleHumanFactory = new FemaleHumanFactory();  
        IHuman femaleYellowHuman = femaleHumanFactory.createYellowHuman();//生产黄色的女性  
        IHuman femaleWhiteHuman = femaleHumanFactory.createWhiteHuman();//生产白色的女性  
    }  
}  
```  
  
2019-4-6  
