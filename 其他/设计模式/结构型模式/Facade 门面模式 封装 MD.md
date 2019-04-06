| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [门面模式](#门面模式)
	- [简介](#简介)
	- [案例](#案例)
		- [使用门面模式前](#使用门面模式前)
		- [使用门面模式后](#使用门面模式后)
  
# 门面模式  
## 简介  
  
作用：封装系统功能，简化系统调用  
  
门面模式要求一个系统的外部与其内部的通信必须通过一个统一的门面(Facade)对象进行。门面模式提供一个高层次的接口，使得系统更易于使用。  
  
门面模式的门面类将客户端与系统的内部复杂性分隔开，使得客户端只需要与门面对象打交道，而不需要与系统内部的很多对象打交道。   
  
> 医院的例子  
> 如果把医院作为一个系统，按照部门职能，这个系统可以划分为挂号、门诊、划价、化验、收费、取药等。看病的病人要与这些部门打交道，就如同一个系统的客户端与一个系统的各个类打交道一样，不是一件容易的事情。  
> 首先病人必须先挂号，然后门诊。如果医生要求化验，病人必须首先划价，然后缴款，才能到化验部门做化验。化验后，再回到门诊室。  
> 解决这种不便的方法便是引进门面模式。可以设置一个`接待员`的角色，由接待员负责代为挂号、划价、缴费、取药等。这个接待员就是门面模式的体现，`病人只接触接待员，由接待员负责与医院的各个部门打交道`。  
  
在什么情况下使用门面模式  
- 为一个复杂系统提供一个简单接口   
- 提高系统的独立性  
- 在层次化结构中，可以使用Facade模式定义系统中每一层的入口  
  
## 案例  
### 使用门面模式前  
对于一个复杂的系统，可能需要多个业务才能完成，如把大象关进冰箱里面 ，需要的操作：  
```java  
interface IServiceA {  
    void methodA();  
}  
  
interface IServiceB {  
    void methodB();  
}  
  
interface IServiceC {  
    void methodC();  
}  
```  
  
实现类：  
```java  
class ServiceAImpl implements IServiceA {  
    @Override  
    public void methodA() {  
        System.out.println("打开冰箱门");  
    }  
}  
  
class ServiceBImpl implements IServiceB {  
    @Override  
    public void methodB() {  
        System.out.println("把大象放到冰箱里");  
    }  
}  
  
class ServiceCImpl implements IServiceC {  
    @Override  
    public void methodC() {  
        System.out.println("关闭冰箱门");  
    }  
}  
```  
  
这种情况下，使用者必须知道系统各个业务的具体`功能及先后顺序`才能使用  
客户端使用示例：  
```java  
public class Test {  
    public static void main(String[] args) {  
        IServiceA sa = new ServiceAImpl();  
        IServiceB sb = new ServiceBImpl();  
        IServiceC sc = new ServiceCImpl();  
  
        //在使用前，我必须先知道IServiceA、IServiceB、IServiceC是做什么的，并且知道调用顺序，之后我才能正确完成此功能  
        sa.methodA();  
        sb.methodB();  
        sc.methodC();  
    }  
}  
```  
  
### 使用门面模式后  
下面我们定义一个类，对子系统进行`封装`，以简化子系统调用  
  
为系统提供一个简单的访问接口 ：  
```java  
interface IFacade {  
    void storeElephant(); //把大象装到冰箱里面  
    void freeElephant(); //放开那头大象  
}  
```  
  
门面类将客户端与系统的内部复杂性分隔开，使得客户端只需要与门面对象打交道，而不需要与系统内部的很多对象打交道。  
```java  
class Facade implements IFacade {  
    private IServiceA sa;  
    private IServiceB sb;  
    private IServiceC sc;  
  
    public Facade() {  
        sa = new ServiceAImpl();  
        sb = new ServiceBImpl();  
        sc = new ServiceCImpl();  
    }  
  
    @Override  
    public void storeElephant() {  
        sa.methodA();  
        sb.methodB();  
        sc.methodC();  
    }  
  
    @Override  
    public void freeElephant() {  
        sa.methodA();  
        System.out.println("把大象从冰箱里拽出来");//这里为了省事，直接把这步操作放在了这里  
        sc.methodC();  
    }  
}  
```  
  
这种情况下，使用者不需要知道系统各个业务的具体功能及先后顺序就能使用  
  
客户端使用示例：  
```java  
public class Test {  
    public static void main(String[] args) {  
        IFacade facade = new Facade();  
        facade.storeElephant();  
        System.out.println("=======================");  
        facade.freeElephant();  
    }  
}  
```  
  
这是一个很好的封装方法，如果一个子系统比较复杂的话，就可以封装出一个或多个门面出来，可以使项目的结构更简单，而且扩展性非常好。   
  
2016-10-31  
