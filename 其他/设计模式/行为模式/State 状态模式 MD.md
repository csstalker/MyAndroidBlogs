| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
***  
目录  
===  

- [状态模式](#状态模式)
	- [简介](#简介)
	- [和策略模式的比较](#和策略模式的比较)
	- [案例一](#案例一)
	- [案例二](#案例二)
		- [抽象状态类](#抽象状态类)
		- [具体状态类](#具体状态类)
		- [环境类](#环境类)
		- [测试](#测试)
  
# 状态模式  
  
状态模式看起来和策略模式很像，但是是两个不一样的设计模式。状态模式是一个类根据`内部的状态`动态的选择`行为`。策略模式一般用于`算法`，通过设置不同的`策略类`来执行不同的算法。  
  
## 简介  
  
状态模式允许一个对象在其内部`状态改变的时候改变其行为`，这个对象看上去就像是改变了它的类一样。  
状态模式把所研究的对象的【行为】包装在不同的【状态对象】里，每一个状态对象都属于抽象状态类的一个子类。  
状态模式的意图是让一个对象在其内部状态改变的时候，其行为也随之改变。  
  
角色  
- 抽象状态类State：定义一个或一组接口，表示该状态下类的`行为`  
- 具体状态类ConcreteState：实现抽象状态类中定义的接口，从而达到不同状态下的不同行为  
- 环境类Context：定义客户感兴趣的接口，维护一个抽象状态类的子类的实例，这个实例代表对象当前的状态  
  
## 和策略模式的比较  
  
状态模式和策略模式的`结构`几乎完全一样，但它们的`目的`却完全不一样。  
状态模式的行为是平行的、`不可替换`的，策略模式的行为是彼此独立、`可相互替换`的。  
  
状态模式和策略模式都是为了实现解耦，避免一大堆的if-else的写法，但是状态模式更多的是适合不同状态下，同样的行为有不同的响应。根据实际生活来说，这种模式适合有明显的状态区分的时候。比如电视的开，关就是两种状态。  
  
状态模式的关键点在于不同的状态下对于同一行为有不同的响应，这其实就是一个将if-else用`多态`来实现的具体实例  
  
状态模式将所有与一个特定状态相关的行为都放入一个状态对象中，它提供了一个更好的方法来组织与特定状态相关的代码，将繁琐的状态判断转换成结构清晰的状态类族，在避免代码膨胀的同时也保证了可扩展性和可维护性  
  
使用场景  
- 一个类的状态决定了他的行为，而且在运行时必须根据当前的状态来改变行为。  
- 跟策略模式一样，解决一大堆分支语句的存在。  
  
优缺点：  
- 优点：将所有的状态和该状态的行为封装在同一个实现类中，然后有一个共同的接口，代码结构清晰，便于扩展和修改。  
- 缺点：也是要增加很多类  
  
## 案例一  
  
下面我们以日常开发中的登录功能来简单应用下状态模式  
  
在登录模块中一般我们会根据用户是否是登录状态而有不同的操作，这里简单模拟2个操作，转发和评论  
```java  
public interface UserState {  
    void forward(Context context); //转发  
    void comment(Context context); //评论  
}  
```  
  
登录状态，调用转发和评论直接简单地弹出个Toast  
```java  
public class LoginedState implements UserState {  
  
    @Override  
    public void forward(Context context) {  
        Toast.makeText(context, "转发成功", Toast.LENGTH_SHORT).show();  
    }  
  
    @Override  
    public void comment(Context context) {  
        Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();  
    }  
}  
```  
  
未登录状态，调用转发和评论跳转到登录页面，这里直接简单地弹出登录的提示  
```java  
public class LogoutState implements UserState {  
  
    @Override  
    public void forward(Context context) {  
        Toast.makeText(context, "未登录，跳转到登录页面", Toast.LENGTH_SHORT).show();  
    }  
  
    @Override  
    public void comment(Context context) {  
        Toast.makeText(context, "未登录，跳转到登录页面", Toast.LENGTH_SHORT).show();  
    }  
}  
```  
  
> 相当于将每一个条件分支下的行为全部放入了一个独立的类中，如果不采用这种模式，则针对每一种行为都必须采取if-else方式判断  
> 状态模式把对象的行为包装在不同的状态对象里，状态模式的意图是让一个对象在其内部状态发生改变的时候，其行为也随之改变  
> 状态模式的行为是平行的、不可替换的  
  
下面我们看看对外提供服务的环境类  
```java  
public class LoginContext {  
    private UserState userState = new LoginedState(); //用户状态，默认为登录状态  
  
    public void setUserState(UserState userState) { //设置用户状态  
        this.userState = userState;  
    }  
  
    public void forward(Context context) {  
        userState.forward(context);  
    }  
  
    public void comment(Context context) {  
        userState.comment(context);  
    }  
}  
```  
  
下面我们看看怎么调用：  
```java  
LoginContext context = LoginContext();  
context.forward(MainActivity.this); //默认是登录状态，所以可以直接调用转发功能  
  
context.setUserState(new LogoutState()); //登录状态改为未登录  
context.forward(MainActivity.this); //注销登录后再调用转发或是评论功能，执行的就会是弹出登录提示  
```  
  
通过上面的例子我们可以看出，状态模式把对象的不同状态下的行为封装起来，对象的状态改变了，对象的行为也会改变。  
  
## 案例二  
  
### 抽象状态类  
```java  
interface IState {  
    void open(); //电梯门开启动作  
    void close();//电梯关闭动作  
    void run();//运行电梯动作  
    void stop();//停止电梯动作  
}  
```  
  
### 具体状态类  
```java  
class OpenningState implements IState {  
    private Context context;  
      
    public OpenningState(Context context) {  
        this.context = context;  
    }  
      
    @Override  
    public void open() {   
        System.out.println("电梯门开启...");  
    }  
      
    @Override  
    public void close() {  
        context.setState(Context.closeingState);//状态修改  
        context.getState().close(); //动作委托给CloseState来执行  
    }  
      
    @Override  
    public void run() {  
        System.out.println("------------------电梯门开启状态下运行？要死啊，你！");  
    }  
      
    @Override  
    public void stop() {  
        System.out.println("------------------电梯门开启状态下停止？额，这个本来就应该是这样的吧！");  
    }  
}  
```  
  
```java  
class ClosingState implements IState {  
    private Context context;  
      
    public ClosingState(Context context) {  
        this.context = context;  
    }  
      
    @Override  
    public void close() {  
        System.out.println("电梯门关闭...");  
    }  
      
    @Override  
    public void open() {  
        context.setState(Context.openningState);  
        context.getState().open();  
    }  
      
    @Override  
    public void run() {  
        context.setState(Context.runningState);  
        context.getState().run();  
    }  
      
    @Override  
    public void stop() {  
        context.setState(Context.stoppingState);  
        context.getState().stop();  
    }  
}  
```  
  
```java  
class RunningState implements IState {  
    private Context context;  
      
    public RunningState(Context context) {  
        this.context = context;  
    }  
      
    @Override  
    public void close() {  
        System.out.println("------------------运行状态下关闭电梯门？电梯门本来就是关着的！");  
    }  
      
    @Override  
    public void open() {  
        System.out.println("------------------运行状态下开电梯门？你疯了！");  
    }  
      
    @Override  
    public void run() {  
        System.out.println("电梯上下跑...");  
    }  
      
    @Override  
    public void stop() {  
        context.setState(Context.stoppingState);  
        context.getState().stop();  
    }  
}  
```  
  
```java  
class StoppingState implements IState {  
    private Context context;  
      
    public StoppingState(Context context) {  
        this.context = context;  
    }  
      
    @Override  
    public void close() {  
        System.out.println("------------------停止状态下关门？电梯门本来就是关着的！");  
    }  
      
    @Override  
    public void open() {  
        context.setState(Context.openningState);  
        context.getState().open();  
    }  
      
    @Override  
    public void run() {  
        context.setState(Context.runningState);  
        context.getState().run();  
    }  
      
    @Override  
    public void stop() {  
        System.out.println("电梯停止了...");  
    }  
}  
```  
  
### 环境类  
```java  
class Context {  
    public static OpenningState openningState;  
    public static ClosingState closeingState;  
    public static RunningState runningState;  
    public static StoppingState stoppingState;  
  
    private IState state;  
      
    public IState getState() {  
        return state;  
    }  
      
    public Context() {  
        openningState = new OpenningState(this);  
        closeingState = new ClosingState(this);  
        runningState = new RunningState(this);  
        stoppingState = new StoppingState(this);  
    }  
      
    public void setState(IState state) {  
        this.state = state;  
    }  
      
    public void open() {  
        state.open();  
    }  
      
    public void close() {  
        state.close();  
    }  
      
    public void run() {  
        state.run();  
    }  
      
    public void stop() {  
        state.stop();  
    }  
}  
```  
  
### 测试  
```java  
class Test {  
    public static void main(String[] args) {  
        Context context = new Context();  
        IState[] states = {new OpenningState(context), new ClosingState(context), new RunningState(context), new StoppingState(context)};  
        for (IState state : states) {  
            System.out.println("当前状态【" + state.getClass().getSimpleName() + "】");  
            context.open();  
            context.close();  
            context.run();  
            context.stop();  
        }  
    }  
}  
```  
  
2016-10-20  
