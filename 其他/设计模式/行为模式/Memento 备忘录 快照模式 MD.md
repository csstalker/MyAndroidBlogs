| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [备忘录模式](#备忘录模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 备忘录模式  
## 简介  
  
在不破坏封装的前提下，捕获一个对象的【内部状态】，并在该对象之外保存这个状态，这样以后就可以将该对象恢复到原先保存的状态。  
  
角色：  
- 发起人Originator：要被备份的成员，它提供一创建备忘录的方法，其实就是`将它自身的某些信息拷贝一份放到一个备忘录对象中`。并提供另外一个方法将备忘录中的信息覆盖自身的信息。  
- 备忘录Memento：备忘录对象中`包含存储发起人状态的成员变量`，它提供set，get或构造方法保存发起人状态及获取发起人状态。  
- 管理角色Caretaker：用于管理备忘录对象的实现类。  
  
适用场合：  
- 它适用于对象在执行某些操作时，为防止意外，而在执行操作前，将对象状态备份的场景，有点类似于【事务回滚】的意思。  
- 功能比较复杂的，但是需要维护或记录属性历史的类  
- 需要保存的属性只是众多属性的一小部分时  
  
好处：  
- 有时一些发起人对象的内部信息必须【保存在】发起人对象以外的地方，但是必须要由发起人对象自己读取，这时使用备忘录模式可以把复杂的发起人内部信息对其他的对象屏蔽起来，从而可以恰当地保持【封装】的边界  
- 本模式【简化】了发起人类。发起人不再需要管理和保存其内部状态的一个个版本，客户端可以自行管理他们所需要的这些状态的版本  
- 当发起人角色的状态改变的时候，有可能这个状态无效，这时候就可以使用暂时存储起来的备忘录将状态复原  
  
缺点：  
- 如果发起人角色的状态需要完整地存储到备忘录对象中，那么在【资源消耗】上面备忘录对象会很昂贵  
- 当负责人角色将一个备忘录存储起来的时候，负责人可能并不知道这个状态会占用多大的存储空间，从而无法提醒用户一个操作是否很昂贵  
  
## 案例  
  
发起人Originator，要【被备份】的类  
```java  
public class Originator {  
    public String date; //需要被保存的内部状态  
    public int num; //需要被保存的内部状态  
  
    //提供一个【创建备忘录】的方法，其实就是将它自身的某些信息拷贝一份到一个备忘录对象中  
    public Memento createMemento() {  
        return new Memento(this); //备忘录持有当前对象的引用  
    }  
  
    public void setMemento(Memento memento) {  
        date = memento.date; //用备忘录中的信息覆盖自身信息  
        num = memento.num;  
    }  
    public void show() {  
        System.out.println(date + "---" + num);  
    }  
}  
```  
  
备忘录Memento，备忘录对象中包含存储发起人状态的成员变量，它提供set，get或构造方法保存发起人状态及获取发起人状态  
```java  
public class Memento {  
    public String date;  
    public int num;  
    public Memento(Originator org) {  
        this.date = org.date;  
        this.num = org.num;  
    }  
}  
```  
  
管理角色Caretaker，用于管理备忘录对象。不能对备忘录的内部进行操作或检查  
```java  
public class Caretaker {  
    private Memento memento;  
    public Memento getMemento() {  
        return this.memento;  
    }  
    public void setMemento(Memento memento) {  
        this.memento = memento;  
    }  
}  
```  
  
```java  
public class Test {  
    public static void main(String[] args) {  
        //原始数据  
        Originator org = new Originator();  
        org.date = new SimpleDateFormat("HH-mm-ss_SSS").format(new Date());  
        org.num = 99;  
        org.show();  
  
        //创建一个备忘录对象，保存原始数据的状态，目的是可以在需要时恢复  
        Memento memento = org.createMemento();  
        Caretaker ctk = new Caretaker(); //创建一个备忘录管理类，用于管理备忘录对象（其实基本没什么卵用）  
        ctk.setMemento(memento);  
  
        //改变原始数据的状态  
        org.date = new SimpleDateFormat("HH-mm-ss_SSS").format(new Date());  
        org.num = 100;  
        org.show();  
  
        //恢复之前保存的状态（将保存的数据导入）  
        org.setMemento(ctk.getMemento());  
        org.show();  
    }  
}  
```  
  
去除掉管理类  
```java  
public class Test {  
    public static void main(String[] args) {  
        Originator org = new Originator();//原始数据  
        //...对原始数据进行各种操作  
        Memento memento = org.createMemento(); //创建一个备忘录对象，保存原始数据的状态，目的是可以在需要时恢复  
        //...改变原始数据的状态  
        org.setMemento(memento);//恢复之前保存的状态（将保存的数据导入）  
        org.show();  
    }  
}  
```  
  
