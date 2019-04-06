| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [中介者模式](#中介者模式)
	- [简介](#简介)
	- [演示](#演示)
		- [同事](#同事)
		- [中介者](#中介者)
		- [客户端测试](#客户端测试)
  
# 中介者模式  
## 简介  
  
用一个中介者对象封装一系列的对象交互，中介者使各对象不需要显示地相互作用，从而使耦合松散，而且可以独立地改变它们之间的交互。  
  
中介者模式也称为调解者模式或者调停者模式。  
  
当程序存在大量的类时，多个对象之间存在着依赖的关系，呈现出`网状结构`，那么程序的可读性和可维护性就变差了，并且修改一个类需要牵涉到其他类，不符合开闭原则。  
![](https://upload-images.jianshu.io/upload_images/6163786-9895aed658c4cd0c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/431/format/webp)  
  
因此我们可以引入中介者，`将网状结构转化成星型结构`，可以降低程序的复杂性，并且可以减少各个对象之间的耦合。  
![](https://upload-images.jianshu.io/upload_images/6163786-9f940f9d6c3755a7.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/427/format/webp)  
  
角色说明：  
- 抽象中介者Mediator：定义用于各个同事类之间通信的接口  
- 具体中介者ConcreteMediator：实现抽象中介者中定义的方法，它需要了解并维护每个同事对象，并负责协调各个同事对象的交互关系  
- 抽象同事类Colleague：同事对象只知道中介者而不知道其他同事对象，同事类之间必须通过中介者才能进行消息传递  
- 具体同事类ConcreteColleague：每个具体同事类都知道自己本身的行为，其他的行为只能通过中介者去进行  
  
中介者模式的优点  
- 适当地使用中介者模式可以`避免同事类之间的过度耦合`，使得各同事类之间可以相对独立地使用  
- 降低了系统对象之间的耦合性，使得对象易于独立的被复用  
- 提高系统的灵活性，使得系统易于扩展和维护  
- 使用中介者模式可以将对象间一对多的关联转变为一对一的关联，使对象间的关系易于理解和维护  
- 使用中介者模式可以将对象的行为和协作进行抽象，能够比较灵活的处理对象间的相互作用  
  
适用场景  
- 在面向对象编程中，一个类必然会与其他的类发生依赖关系，完全独立的类是没有意义的。一个类同时依赖多个类的情况也相当普遍，既然存在这样的情况，说明，一对多的依赖关系有它的合理性，适当的使用中介者模式可以使原本凌乱的对象关系清晰。  
- 一般来说，只有对于那种同事类之间是网状结构的关系，才会考虑使用中介者模式。  
- 使用中介者模式，可以将网状结构变为星状结构（星状结构的中心当然是中介者啦），使同事类之间的关系变的清晰一些。  
  
> 中介者模式是一种比较常用的模式，也是一种比较容易被滥用的模式。对于大多数的情况，同事类之间的关系不会复杂到混乱不堪的网状结构，因此，大多数情况下，将对象间的依赖关系封装的同事类内部就可以的，没有必要非引入中介者模式。滥用中介者模式，只会让事情变的更复杂。  
  
优点  
- 降低类的关系复杂度，将多对多转化成一对多，实现解耦。  
- 符合迪米特原则，依赖的类最少。  
  
缺点  
- 同事类越多，中介者的逻辑就越复杂，会变得越难维护。  
- 如果本来类的依赖关系不复杂，但是使用了中介者会使原来不复杂的逻辑变得复杂。  
  
通过使用一个中间对象来进行消息分发以及减少类之间的直接依赖。  
  
## 演示  
  
说到中介者，肯定就想到了房屋中介，下面以房屋中介为例，房东通过中介发布出售信息，中介就会把房屋信息传递给有这需求的购房者，购房者再通过中介去看房买房等等。  
  
### 同事  
  
抽象同事角色  
无论是房东还是购房者，他们都能够发布信息和接受信息  
```java  
public abstract class Person {//人物类  
    protected HouseMediator houseMediator;  
  
    public Person(HouseMediator houseMediator) {  
        this.houseMediator = houseMediator;//获取中介  
    }  
  
    public abstract void send(String message);//发布信息  
    public abstract void getNotice(String message);//接受信息  
}  
```  
  
具体同事角色  
下面分别创建一个房东类和一个买房者类  
```java  
public class Purchaser extends Person {  
    public Purchaser(HouseMediator houseMediator) {  
        super(houseMediator);  
    }  
  
    @Override  
    public void send(String message) {  
        System.out.println("买房者发布信息：" + message);  
        houseMediator.notice(this, message);  
    }  
  
    @Override  
    public void getNotice(String message) {  
        System.out.println("买房者收到消息：" + message);  
    }  
}  
```  
  
```java  
public class Landlord extends Person {  
    public Landlord(HouseMediator houseMediator) {  
        super(houseMediator);  
    }  
  
    @Override  
    public void send(String message) {  
        System.out.println("房东发布信息：" + message);  
        houseMediator.notice(this, message);  
    }  
  
    @Override  
    public void getNotice(String message) {  
        System.out.println("房东收到消息：" + message);  
    }  
}  
```  
  
### 中介者  
  
抽象中介者角色  
这里就是房屋中介，定义一个通知的方法：  
```java  
 public interface HouseMediator {//房屋中介类  
    void notice(Person person, String msg);//通知方法  
}  
```  
  
具体中介者角色  
具体的房屋中介，以链家为例，他们能从房东和买房者获得信息，然后做出不同的行为：  
```java  
public class Lianjia implements HouseMediator {  
    Purchaser mPurchaser; //买房者类  
    Landlord mLandlord; //房东  
  
    public void setPurchaser(Purchaser purchaser) {//设置买房者  
        mPurchaser = purchaser;  
    }  
  
    public void setLandlord(Landlord landlord) {//设置房东  
        mLandlord = landlord;  
    }  
  
  
    @Override  
    public void notice(Person person, String message) {//发送通知  
        System.out.println("中介收到信息，并转发给相应的目标人群");  
        if (person == mPurchaser) {  
            mLandlord.getNotice(message);  
        } else if (person == mLandlord) {  
            mPurchaser.getNotice(message);  
        }  
    }  
}  
```  
  
### 客户端测试  
```java  
 public void test() {  
    Lianjia houseMediator = new Lianjia();  
    Purchaser purchaser = new Purchaser(houseMediator);  
    Landlord landlord = new Landlord(houseMediator);  
    houseMediator.setLandlord(landlord);  
    houseMediator.setPurchaser(purchaser);  
  
    landlord.send("出售一套别墅");  
    purchaser.send("求购一套学区房");  
}  
```  
  
2016-03-20  
