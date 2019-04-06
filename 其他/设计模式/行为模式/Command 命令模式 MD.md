| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [命令模式](#命令模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 命令模式  
## 简介  
将来自客户端的`请求`封装为一个对象，从而使你可用不同的请求对客户进行参数化，对请求排队或记录请求日志，以及支持可撤消的动作。  
  
用于【行为请求者】与【行为实现者】解耦，可实现二者之间的松耦合，以便适应变化。  
  
角色  
- Command  命令接口，声明执行的方法。  
- ConcreteCommand  命令接口实现类，是“虚”的实现；通常会`持有接收者`，并调用接收者的功能来完成命令要执行的操作。  
- Receiver  接收者，真正执行命令的对象。任何类都可能成为一个接收者，只要它能够实现命令要求实现的相应功能。  
- Invoker  要求命令对象执行请求，通常会持有命令对象，可以持有很多的命令对象。这个是客户端真正触发命令并要求命令执行相应操作的地方，也就是说相当于使用命令对象的入口。  
- Client  创建具体的命令对象，并且设置命令对象的接收者。注意这个不是我们常规意义上的客户端，而是在组装命令对象和接收者，或许，把这个Client称为装配者会更好理解，因为真正使用命令的客户端是从Invoker来触发执行。  
  
优点  
- 降低对象之间的耦合度  
- 新的命令可以很容易地加入到系统中  
- 可以比较容易地设计一个组合命令  
- 调用同一方法实现不同的功能  
  
适用情况  
- 系统需要将【请求调用者】和【请求接收者】解耦，使得调用者和接收者不直接交互。  
- 系统需要在不同的时间指定请求、将请求排队和执行请求。  
- 系统需要支持命令的撤销(Undo)操作和恢复(Redo)操作。  
- 系统需要将一组操作【组合】在一起，即支持宏命令。  
  
总结  
- 命令模式的本质是对【命令】进行封装，将【发出命令的责任】和【执行命令的责任】分割开。  
- 每一个命令都是一个操作：请求的一方发出请求，要求执行一个操作；接收的一方收到请求，并执行操作。  
- 命令模式将请求的一方和接收的一方独立开来，使得请求的一方不必知道接收请求的一方的接口，更不必知道请求是怎么被接收，以及操作是否被执行、何时被执行，以及是怎么被执行的。  
- 命令模式使请求本身成为一个对象，这个对象和其他对象一样可以被存储和传递。  
- 命令模式的关键在于引入了抽象命令接口，且发送者针对抽象命令接口编程，只有实现了抽象命令接口的具体命令才能与接收者相关联。  
  
JDK中体现：Runnable；Callable；ThreadPoolExecutor  
  
## 案例  
  
命令的执行者（接收者）  
```java  
public class Receiver {  
    public void turnOn() {  
        System.out.println("电视打开了");  
    }  
    public void turnOff() {  
        System.out.println("电视关闭了");  
    }  
    public void changeChannel(int channel) {  
        System.out.println("切换到频道 " + channel);  
    }  
}  
```  
  
抽象命令接口，声明执行的方法  
```java  
public interface ICommand {  
    public void execute();  
}  
```  
  
命令接口实现类 ConcreteCommand，通常会持有接收者，并调用接收者的功能来完成命令要执行的操作  
//开机命令  
```java  
class CommandOn implements ICommand {  
    private Receiver receiver;//持有一个接收者对象  
    public CommandOn(Receiver receiver) {  
        this.receiver = receiver;  
    }  
    @Override  
    public void execute() {  
        receiver.turnOn(); //最终是通过调用接收者的方法，来完成命令要执行的操作  
    }  
}  
```  
  
//关机命令  
```java  
class CommandOff implements ICommand {  
    private Receiver receiver;  
    public CommandOff(Receiver receiver) {  
        this.receiver = receiver;  
    }  
    @Override  
    public void execute() {  
        receiver.turnOff();  
    }  
}  
```  
  
//频道切换命令  
```java  
class CommandChange implements ICommand {  
    private Receiver receiver;  
    private int channel;  
    public CommandChange(Receiver receiver, int channel) {  
        this.receiver = receiver;  
        this.channel = channel;  
    }  
    public void execute() {  
        receiver.changeChannel(channel);  
    }  
}  
```  
  
命令的调用者（请求方），接收命令，并执行命令，可以看作是遥控器  
```java  
public class Invoker {  
    private ICommand onCommand, offCommand, changeChannel;  
    public Invoker(ICommand onCommand, ICommand offCommand, ICommand changeChannel) {  
        this.onCommand = onCommand;  
        this.offCommand = offCommand;  
        this.changeChannel = changeChannel;  
    }  
    public void turnOn() {  
        onCommand.execute();  
    }  
    public void turnOff() {  
        offCommand.execute();  
    }  
    public void changeChannel() {  
        changeChannel.execute();  
    }  
}  
```  
  
测试类  
```java  
public class Test {  
    public static void main(String[] args) {  
        Receiver receiver = new Receiver(); // 命令接收者（电视）  
        ICommand on = new CommandOn(receiver); // 不同的命令（开机、换台、关机）    
        ICommand off = new CommandOff(receiver);  
        ICommand channel = new CommandChange(receiver, new Random().nextInt(100));  
  
        Invoker invoker = new Invoker(on, off, channel);// 命令控制对象（遥控器）  
        invoker.turnOn(); // 执行命令  
        invoker.changeChannel();  
        invoker.turnOff();  
    }  
}  
```  
  
2016-04-29  
