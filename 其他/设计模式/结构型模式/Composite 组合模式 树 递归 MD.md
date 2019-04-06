| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [组合模式](#组合模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 组合模式  
## 简介   
  
> 将对象组合成【树】形结构以表示【部分-整体】的层次结构，组合模式使得用户对【单个对象】和对【组合对象】的使用具有一致性。  
  
组合模式有时候又叫做部分-整体模式，它使我们在树型结构的问题中，模糊了简单元素和复杂元素的概念。  
  
当发现需求中是体现部分与整体层次结构，并且你希望用户可以忽略组合对象与单个对象的不同，以统一的方式使用组合结构中的所有对象时，就应该考虑组合模式了。  
  
组合模式解耦了客户程序与复杂元素内部结构，从而使客户程序可以像处理简单元素一样来处理复杂元素。  
  
组合模式让你可以优化处理【递归】或【分级】数据结构。  
  
> 关于分级数据结构的一个普遍性的例子是你每次使用电脑时所遇到的文件系统。文件系统由目录和文件组成，每个目录都可以装内容，目录的内容可以是文件，也可以是目录。按照这种方式，计算机的文件系统就是以递归结构来组织的。如果你想要描述这样的数据结构，那么你可以使用组合模式。  
  
涉及角色：  
- Component：为参加组合的对象声明一个公共接口，不管是组合还是叶结点都实现这个接口  
- Leaf：在组合中表示叶子结点对象，叶子结点没有子结点  
- Composite：表示组合中有子结点的对象  
  
特点：使得客户端看来单个对象和对象的组合是同等的。换句话说，某个类型的方法同时也接受自身类型作为参数。  
例如：  
```java  
java.util.List#addAll(Collection)  
```  
  
## 案例  
  
Component：为参加组合的对象声明一个公共接口  
```java  
public abstract class Component {  
    /**表示直接叶子节点（直接下属团队成员）的集合 */  
    protected List<Component> employList;  
    public String name;  
      
    public final void display() {  
        System.out.println(name);//打印自己  
        display(employList);//打印子节点  
    }  
      
    private final void display(List<Component> list) {  
        if (list != null) {  
            for (int i = 0; i < list.size(); i++) {  
                Component sonComponent = list.get(i);//子节点  
                System.out.println(sonComponent.name);//打印子节点  
                List<Component> sonList = sonComponent.employList;//子节点的节点集合   
                if (sonList != null) {//不为空说明是非叶子节点  
                    display(sonList); //递归  
                }  
            }  
        }  
    }  
      
    public abstract boolean add(Component employer);  
    public abstract boolean delete(Component employer);  
}  
```  
  
Leaf：组合中的叶子结点对象，叶子结点没有子结点  
```java  
public class Leaf extends Component {  
    public Leaf(String name) {  
        this.name = name;  
        this.employList = null;//没有叶子节点  
    }  
      
    @Override  
    public boolean add(Component employer) {  
        throw new RuntimeException("叶子结点没有子结点");   
    }  
    @Override  
    public boolean delete(Component employer) {  
        throw new RuntimeException("叶子结点没有子结点");   
    }  
}  
```  
  
Composite：组合中有子结点的对象  
```java  
public class Composite extends Component {  
    public Composite(String name) {  
        this.name = name;  
        this.employList = new ArrayList<Component>();   
    }  
      
    @Override  
    public boolean add(Component employer) {  
        return this.employList.add(employer);  
    }  
    @Override  
    public boolean delete(Component employer) {  
        return this.employList.remove(employer);  
    }  
}  
```  
  
演示  
```java  
public class Test {  
    public static void main(String[] args) {  
        Component employer1 = new Composite("【项目经理一】");   
        Component employer11 = new Leaf("包青天");  
        Component employer12 = new Leaf("白乾涛");  
        Component employer13 = new Leaf("baiqiantao");  
          
        Component employer2 = new Composite("【项目经理二】");   
        Component employer21 = new Leaf("bqt");  
          
        Component employer3 = new Composite("boss");  
        employer3.add(employer1);// 为boss添加一个非叶子节点  
        employer3.add(employer2);  
          
        employer1.add(employer11);// 为项目经理添加一个叶子节点   
        employer1.add(employer12);  
        employer1.add(employer13);  
          
        employer2.add(employer21);  
          
        employer11.display();  
        System.out.println("----------------------------------");  
        employer1.display();  
        System.out.println("----------------------------------");  
        employer2.display();  
        System.out.println("----------------------------------");  
        employer3.display();  
        System.out.println("----------------------------------");  
          
        System.out.println(employer2.add(employer21));//true  
        System.out.println(employer2.delete(employer11));//false  
        employer2.display();  
    }  
}  
```  
  
2016-10-31  
