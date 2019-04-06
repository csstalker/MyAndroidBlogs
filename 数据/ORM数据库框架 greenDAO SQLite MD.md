| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
ORM数据库框架 greenDAO SQLite MD  
***  
目录  
===  

- [简介](#简介)
	- [相关资源](#相关资源)
	- [特性与优点](#特性与优点)
	- [其他开源项目](#其他开源项目)
	- [官网介绍文档](#官网介绍文档)
- [Greendao 注解](#Greendao-注解)
	- [实体注解 @Entity](#实体注解-@Entity)
	- [属性注解](#属性注解)
		- [@Id 主键](#@Id-主键)
		- [@Property 字段名](#@Property-字段名)
		- [@NotNull 非空](#@NotNull-非空)
		- [@Transient 忽略](#@Transient-忽略)
	- [索引注解](#索引注解)
		- [@Index 索引](#@Index-索引)
		- [@Unique 唯一](#@Unique-唯一)
	- [关系注解(略)](#关系注解略)
	- [@Generated 自动生成](#@Generated-自动生成)
	- [@Keep 保留](#@Keep-保留)
	- [对 Generated 和 Keep 的理解](#对-Generated-和-Keep-的理解)
	- [@Convert 自定义类型](#@Convert-自定义类型)
- [使用教程](#使用教程)
	- [核心类简介](#核心类简介)
	- [核心初始化](#核心初始化)
	- [增删改](#增删改)
		- [delete](#delete)
		- [insert](#insert)
		- [save](#save)
		- [update](#update)
		- [load](#load)
	- [查询](#查询)
		- [QueryBuilder 查询条件](#QueryBuilder-查询条件)
		- [Order 排序](#Order-排序)
		- [Limit, Offset 偏移和分页](#Limit-Offset-偏移和分页)
		- [Custom Types as Parameters](#Custom-Types-as-Parameters)
		- [Query and LazyList](#Query-and-LazyList)
		- [在多线程中使用查询](#在多线程中使用查询)
		- [使用原始 SQL 语句查询](#使用原始-SQL-语句查询)
		- [DeleteQuery](#DeleteQuery)
		- [查询时疑难问题的处理](#查询时疑难问题的处理)
	- [自定义类型](#自定义类型)
	- [数据库加密](#数据库加密)
- [使用示例](#使用示例)
	- [配置 gradle](#配置-gradle)
	- [定义实体类](#定义实体类)
	- [演示 DAO 的使用](#演示-DAO-的使用)
  
# 简介  
## 相关资源  
- Demo地址：https://github.com/baiqiantao/AndroidOrmTest.git    
- [GitHub](https://github.com/greenrobot/greenDAO)      
- [官网](http://greenrobot.org/greendao/)      
- [Features](http://greenrobot.org/greendao/features/)      
- [greenDAO 3](http://greenrobot.org/greendao/documentation/updating-to-greendao-3-and-annotations/)       
- [Documentation](http://greenrobot.org/greendao/documentation/)       
- [Changelog](http://greenrobot.org/greendao/changelog/)      
- [Technical FAQ](http://greenrobot.org/greendao/documentation/technical-faq/)      
- [Non-Technical FAQ](http://greenrobot.org/greendao/documentation/faq/)  
  
## 特性与优点  
- greenDAO是一款轻巧快捷的Android版ORM，可将对象映射到SQLite数据库。greenDAO`针对Android进行了高度优化，性能卓越，占用内存极`少。  
- GreenDao支持`Protocol buffers`协议数据的直接存储，如果通过protobuf协议和服务器交互，不需要任何的映射。  
  
> Protocol Buffers协议：以一种高效`可扩展`的对`结构化数据`进行`编码`的方式。google内部的`RPC`协议和文件格式大部分都是使用它。  
> RPC：`Remote Procedure Call`，`远程过程调用`，是一个计算机通信协议，它是一种通过网络从远程计算机程序上请求服务，而不需要了解底层网络技术的协议。  
  
特性：  
- 坚如磐石[Rock solid]：greenDAO自2011年发布以来一直被无数著名的应用程序使用着  
- 超级简单：简洁直接[concise and straight-forward]的API，在V3版本中支持注解  
- 小：库大小<150K，它只是简单的Java jar包  
- 快速：可能是`Android上最快的ORM`，由`智能代码生成器`驱动  
- 安全和富有表现力[expressive]的查询API：`QueryBuilder`使用属性常量来避免拼写错误[typos]  
- 强大的 joins 连接：跨实体查询[query across entities]，甚至是复杂关系的链接  
- 灵活的属性类型：使用自定义类或枚举来表示实体中的数据  
- 加密：支持使用 `SQLCipher` 加密数据库  
  
优点：  
- **是 Android 上最流行、最高效、支持RxJava操作而且还在迭代的关系型数据库**  
- 存取速度快：每秒中可以操作数千个实体，速度是 OrmLite 的数倍  
- 支持数据库加密：支持SQLite和SQLCipher(在SQLite基础上加密型数据库)  
- 轻量级：greenDao的代码库仅仅`100k`大小  
- 激活实体：处于激活状态下的实体可以有更多操作方法  
- 支持缓存：能够将使用过的实体存在`内存缓存`中，下次使用时可以直接从缓存中取，这样可以使性能提高N个数量级  
- `代码自动生成`：greenDao 会根据modle类自动生成 DAO 类(DaoMaster、DaoSession 和 **DAO)  
  
## 其他开源项目  
greenrobot 其他流行的开源项目：  
- [ObjectBox](https://github.com/objectbox/objectbox-java) 是一个面向移动设备的新的超快速`面向对象数据`库[object-oriented database]。是比SQLite更快的对象持久化方案[object persistence]。  
- [EventBus](https://github.com/greenrobot/EventBus) 是为Android设计的`中央发布/订阅总线`，具有可选的`传递线程`[delivery]，`优先级`和`粘性事件`[sticky]。这是一个非常好的工具，可以将组件 (e.g. Activities, Fragments, logic components)彼此分离。  
  
## 官网介绍文档  
[文档地址](http://greenrobot.org/greendao/)  
  
**greenDA：适用于您 SQLite 数据库的 Android ORM**  
注意：对于新的应用程序，我们建议使用 ObjectBox，这是一个新的面向对象的数据库，它比 SQLite 快得多并且更易于使用。 对于基于 greenDAO 的现有应用程序，我们提供 DaoCompat 以实现轻松切换（另请参阅 [announcement](http://greenrobot.org/release/daocompat-greendao-on-objectbox/)）。  
  
greenDAO是一个开源的 Android ORM，使 SQLite 数据库的开发再次变得有趣。它减轻了开发人员处理低级数据库需求的同时节省了开发时间。 SQLite是一个很棒的嵌入式关系数据库。尽管如此，编写 SQL 和解析查询结果仍然是一项非常繁琐且耗时的任务。greenDAO 通过将 Java 对象映射到数据库表（称为ORM，“对象/关系映射”）将您从这些中解放出来。 这样，您可以使用简单的面向对象的API来存储，更新，删除和查询Java对象。  
  
**greenDAO的特点一览**  
- 最高性能（可能是Android上最快的ORM）; 我们的基准测试也是开源的  
- 易于使用的强大API，涵盖关系和连接[relations and joins]  
- 最小的内存消耗  
- 较小的库大小（<100KB）以保持较低的构建时间并避免65k方法限制  
- 数据库加密：greenDAO支持SQLCipher，以确保用户的数据安全  
- 强大的社区：超过5000个GitHub星表明有一个强大而活跃的社区  
  
您想了解有关greenDAO功能的更多信息，例如活动实体[active entities]，protocol buffers 支持或者预加载[eager loading]？ 可以看看我们的完整功能列表。  
  
**如何开始使用greenDAO，文档**  
有关greenDAO的第一步，请查看 [documentation](http://greenrobot.org/greendao/documentation/)，尤其是 [getting started guide](http://greenrobot.org/greendao/documentation/how-to-get-started/) 和 [introduction tutorial](http://greenrobot.org/greendao/documentation/introduction/)。  
  
**谁在使用greenDAO？**  
许多顶级Android应用都依赖于greenDAO。 其中一些应用程序的安装量超过1000万。 我们认为，这表明在行业中是可靠的。 在 [AppBrain](http://www.appbrain.com/stats/libraries/details/greendao/greendao) 上查看自己的当前统计数据。  
  
**greenDAO真的那么快吗？ 它是最快的Android ORM吗？**  
我们相信它是。我们不是营销人员，我们是开发人员。 我们经常做基准测试来优化性能，因为我们认为性能很重要。 我们希望提供最快的Android ORM。 虽然有些事情让我们感到很自豪，但我们并不热衷于营销演讲。 我们所有的基准测试都是开源的，可以在达到高标准的同时实现最大透明度。你可以自己检查最新的基准测试结果并得出自己的结论。  
  
# Greendao 注解  
此库中注解的保留策略都是：`@Retention(RetentionPolicy.SOURCE)`  
- CLASS：在class文件中有效。编译器将把注解记录在class文件中，但在运行时 JVM 不需要保留注解。这是默认的行为。也就是说，默认行为是：当运行Java程序时，JVM不可获取Annotation信息。  
- RUNTIME：在运行时有效。编译器将把注解记录在class文件中，在运行时 JVM 将保留注解，因此可以反射性地读取。  
- SOURCE：只在`源文件`中有效, `编译器要丢弃的注解`。  
  
> greenDAO尝试使用合理的默认值，因此开发人员不必配置每一个属性值。  
> 例如，数据库端的`表名和列名`是从`实体名和属性名`派生的，而 ***不是*** Java中使用的驼峰案例样式，`默认数据库名称是大写的，使用下划线来分隔单词`。例如，名为 creationDate 的属性将成为数据库列CREATION_DATE。  
  
## 实体注解 @Entity  
`@Entity` 注解用于将 Java 类转换为数据库支持的实体。这也将指示 greenDAO 生成必要的代码(例如DAO)。  
> 注意：仅支持Java类。 如果你喜欢另一种语言，如Kotlin，你的实体类仍然必须是Java。  
  
```java  
@Entity( //为 greendao 指明这是一个需要映射到数据库的实体类，通常不需要任何额外的参数  
        nameInDb = "AWESOME_USERS",// 指定该表在数据库中的名称，默认是基于实体类名  
        indexes = {@Index(value = "name DESC", unique = true)},// 在此处定义跨越多列的索引  
        createInDb = true,// 是否创建该表。默认为true。如果有多个实体映射到一个表，或者该表是在greenDAO外部创建的，则可置为false  
        schema = "myschema",// 如果您有多个模式，则可以告诉greenDAO实体所属的模式(选择任何字符串作为名称)  
        active = true,// 标记一个实体处于活动状态，活动实体(设置为true)有更新、删除和刷新方法。默认为false  
        generateConstructors = true,//是否生成所有属性的构造器。注意：无参构造器总是会生成。默认为true  
        generateGettersSetters = true//是否为属性生成getter和setter方法。因为**Dao会用到这些方法，如果不自动生成，必须手动生成。默认为true  
)  
```  
  
注意，当使用Gradle插件时，目前不支持多个模式。目前，继续使用你的 [generator](http://greenrobot.org/greendao/documentation/generator/) 项目。  
  
```java  
@Target(ElementType.TYPE)  
public @interface Entity {  
    String nameInDb() default ""; //指定此实体映射到的DB侧的名称（例如，表名）。 默认情况下，名称基于实体类名称。  
    Index[] indexes() default {}; //注意：要创建单列索引，请考虑在属性本身上使用 Index  
    boolean createInDb() default true; //高级标志，设置为false时可禁用数据库中的表创建。这可以用于创建部分实体，其可能仅使用子属性的子集。但请注意，greenDAO不会同步多个实体，例如在缓存中。  
    String schema() default "default"; //指定实体的模式名称：greenDAO可以为每个模式生成独立的类集。属于不同模式的实体应不具有关系。  
    boolean active() default false; //是否应生成更新/删除/刷新方法。如果实体已定义 ToMany 或 ToOne 关系，那么它 active 独立于此值  
    boolean generateConstructors() default true; //是否应生成一个具有所有属性构造函数。一个 no-args 的构造函数总是需要的。  
    boolean generateGettersSetters() default true; //如果缺少，是否应生成属性的getter和setter。  
    Class protobuf() default void.class; //定义此实体的protobuf类，以便为其创建额外的特殊DAO。  
}  
```  
  
添加 `active = true` 时会自动生成以下代码：  
```java  
@Generated(hash = 2040040024) private transient DaoSession daoSession; //Used to resolve relations  
@Generated(hash = 363862535)  private transient NoteDao myDao; //Used for active entity operations.  
​  
//被内部机制所调用，自己不要调用  
@Generated(hash = 799086675)  
public void __setDaoSession(DaoSession daoSession) {  
    this.daoSession = daoSession;  
    myDao = daoSession != null ? daoSession.getNoteDao() : null;  
}  
​  
//方便调用 org.greenrobot.greendao.AbstractDao 的 delete(Object) 方法。实体必须附加到实体上下文。  
@Generated(hash = 128553479)  
public void delete() {  
    if (myDao == null) throw new DaoException("Entity is detached from DAO context");  
    myDao.delete(this);  
}  
​  
//方便调用 org.greenrobot.greendao.AbstractDao 的 refresh(Object) 方法。实体必须附加到实体上下文。  
@Generated(hash = 1942392019)  
public void refresh() {  
    if (myDao == null) throw new DaoException("Entity is detached from DAO context");  
    myDao.refresh(this);  
}  
​  
//方便调用 org.greenrobot.greendao.AbstractDao 的 update(Object) 方法。实体必须附加到实体上下文。  
@Generated(hash = 713229351)  
public void update() {  
    if (myDao == null)  throw new DaoException("Entity is detached from DAO context");  
    myDao.update(this);  
}  
```  
  
设置 `generateConstructors = false` 时，如果没有无参的构造方法，则会生成一个普通的无参构造方法(如果已存在此无参构造方法，则不会生成)：    
  
    public Note() {}  
  
添加 `generateConstructors = true` 时，如果没有无参的构造方法，则会生成一个带 `@Generated` 注解的无参构造方法(如果已存在此无参构造方法，则不会生成)：  
```java  
@Generated(hash = 1272611929)  
public Note() {  
}  
```  
  
并且会生成一个带 `@Generated` 注解的具有所有属性的构造方法(如果已存在此具有所有属性的构造方法，会编译时会报错，提示你 `Can't replace constructor` )：  
```java  
@Generated(hash = 2139673067)  
public Note(Long id, @NotNull String text, Date date, NoteType type) {  
    this.id = id;  
    this.text = text;  
    this.date = date;  
    this.type = type;  
}  
```  
  
## 属性注解  
### @Id 主键  
@Id 注解选择`long/Long`属性作为实体ID，在数据库术语中，它是主键[primary key]。可以通过 `autoincrement = true` 设置自增长(不重用旧值)  
  
```java  
@Target(ElementType.FIELD)  
public @interface Id {  
    boolean autoincrement() default false; //指定id应自增长（仅适用于Long/long字段）。SQLite上的自增长会引入额外的资源使用，通常可以避免  
}  
```  
  
官方文档对主键的说明：  
> 目前，实体必须使用long或Long属性作为其主键。这是Android和SQLite的推荐做法。  
> 要解决此问题[To work around this]，请将你的键属性定义为其他属性[additional property]，但为其创建唯一索引[create a unique index for it]：  
  
```java  
@Id private Long id;  
@Index(unique = true) private String key;  
```  
  
### @Property 字段名  
`@Property(nameInDb = "")` 为该属性在数据库中映射的字段名设置一个非默认的名称。默认是将单词大写，用下划线分割单词，如属性名 customName 对应列名 CUSTOM_NAME  
  
```java  
//可选：为持久字段配置映射列。 此注释也适用于@ToOne，无需额外的外键属性  
@Target(ElementType.FIELD)  
public @interface Property {  
    String nameInDb() default ""; //此属性的数据库列的名称。 默认为字段名称。  
}  
```  
    @Property(nameInDb = "USERNAME") private String name;  
  
### @NotNull 非空  
`@NotNull` 表明这个列非空，通常使用 @NotNull 标记基本类型，然而可使用包装类型(Long, Integer, Short, Byte)使其可空  
  
```java  
//您还可以使用任何其他NotNull或NonNull注释（来自任何库或您自己的），它们是等价的  
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})  
public @interface NotNull { }  
```  
  
```java  
@NotNull private int age;  
@NotNull private String name; //在保存到数据库中之前需要保证此值不为null，否则会报 IllegalArgumentException  
```  
  
### @Transient 忽略  
@Transient 表明此字段不存储到数据库中，用于不需要持久化的字段，比如临时状态  
  
```java  
@Target(ElementType.FIELD)  
public @interface Transient { }  
```  
    @Transient private int tempUsageCount;  
  
## 索引注解  
### @Index 索引  
@Index 为相应的数据库列[column]创建数据库索引[index]。如果不想使用 greenDAO 为该属性生成的默认索引名称，可通过`name`设置；向索引添加`UNIQUE`约束，强制所有值是唯一的。  
  
```java  
//可以用来：1、指定应对属性编制索引；2、通过 Entity 的 indexes() 定义多列索引  
@Target(ElementType.FIELD)  
public @interface Index {  
    String value() default ""; //以逗号分隔的应该 be indexed 的属性列表，例如 “propertyA，propertyB，propertyC”。要指定顺序，请在列名后添加 ASC 或 DESC，例如：“propertyA DESC，propertyB ASC”。只有在 Entity 的 indexes() 中使用此注解时才应设置此项  
    String name() default ""; //索引的可选名称。如果省略，则由 greenDAO 自动生成基于属性列名称  
    boolean unique() default false; //是否应该基于此索引创建唯一约束  
}  
```  
  
    @Index(name = "indexNo", unique = true) private String name;  
  
### @Unique 唯一  
@Unique 为相应列添加唯一约束。注意，SQLite会隐式地为该列创建索引  
  
```java  
//在表创建期间标记属性应具有UNIQUE约束。此注释也适用于 @ToOne，无需额外的外键属性。要在创建表后拥有唯一约束，可以使用 Index 的 unique()。注意同时拥有 @Unique 和 Index 是多余的，会导致性能降低  
@Target(ElementType.FIELD)  
public @interface Unique { }  
```  
    @Unique private String name;  
  
## 关系注解(略)  
- @ToOne 一对一  
- @ToMany 一对多  
- @JoinEntity 多对多  
- @JoinEntity 多对多  
- @OrderBy 指定 ToMany 关系的相关集合的顺序  
  
## @Generated 自动生成  
`@Generated` 用以标记由 greenDAO 自动生成的字段、构造函数或方法。  
  
greenDAO中的实体类由开发人员创建和编辑。然而，在代码生成过程中，greenDAO可能会增加实体的源代码。greenDAO将为它创建的方法和字段添加一个`@Generated`注解，以通知开发人员并防止任何代码丢失。在大多数情况下，你不必关心使用`@Generated`注解的代码。  
  
作为预防措施，greenDAO不会覆盖现有代码，并且如果手动更改生成的代码会引发错误：  
> Constructor (see Note:34) has been changed after generation.    
Please either mark it with @Keep annotation instead of @Generated to keep it untouched[保持不变], or use @Generated (without hash) to allow to replace it[允许替换].  
  
正如错误消息所提示的，通常有两种方法来解决此问题：  
- 还原对使用`@Generated`注解的代码的更改。或者，你也可以完全删除更改的构造函数或方法，它们将在下一次build时重新生成。  
- 使用`@Keep`注解替换`@Generated`注解。这将告诉greenDAO永远不要触摸[touch]带注解的代码。请记住，你的更改可能会中断实体和其他greenDAO之间的约定[break the contract]。 此外，未来的greenDAO版本可能期望生成的方法中有不同的代码。所以，要谨慎！采用单元测试的方法来避免麻烦是个不错的选择。  
  
```java  
//All the code elements that are marked with this annotation can be changed/removed during next run of generation in respect of[就...而言] model changes  
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})  
public @interface Generated {  
    int hash() default -1;  
}  
```  
  
## @Keep 保留  
`@Keep` 指定在下次运行 greenDAO 生成期间应保留的目标。  
  
在 Entity 类上使用此批注会以静默方式禁用任何类修改[itself silently disables any class modification]。由用户负责编写和支持 greenDAO 所需的任何代码。如果您不完全确定自己在做什么，请不要在类成员上使用此注释，因为在模型更改的情况下，greenDAO 将无法对目标代码进行相应的更改。  
  
```java  
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})  
public @interface Keep { }  
```  
  
作用：注解代码段以禁止 greenDAO 修改注解的代码段，注解实体类以禁止 greenDAO 修改此类。  
> 注意：不再支持旧版本的greenDAO中使用的KEEP。  
> KEEP sections used in older versions of greenDAO are no longer supported。  
  
## 对 Generated 和 Keep 的理解  
由 `@Generated` 注解的代码一般是 greenDAO 需要的但是之前不存在的代码，所以在 build 时 greenDAO 自动生成了这些代码。  
  
比如，示例代码中会自动生成以下一个 @Generated 注解的构造方法：  
```java  
@Generated(hash = 1272611929)  
public Note() {  
}  
```  
  
此时，如果更改此方法为：  
```java  
@Generated(hash = 1272611929)  
public Note() {  
    System.out.println("需要自定义此构造方法");  
}  
```  
  
则在 build 时会报错，按照提示，可以改为：  
```java  
@Generated()  
public Note() {  
    System.out.println("需要自定义此构造方法");  
}  
```  
  
但实际上，这样的修改并没有任何意义，因为下次 build 时会删掉你添加的所有代码，自动会恢复原样(和整个删掉这些代码的效果相同)。  
> PS：恢复的注解中的 hash 值和之前也是完全一样的，因为其实 hash 是根据生成的代码计算后得来的，改动此块代码就会导致 hash 值不符，相同代码的 hash 值也一样。  
  
或者使用 `@Keep` 代替 `@Generated`，这将告诉 greenDao 不会修改带有改注解的代码：  
```java  
@Keep  
public Note() {  
    System.out.println("需要自定义此构造方法");  
}  
```  
  
这样，下次 build 时此代码就会保持你自定义的样子。但是这可能会破坏 entities 类和 greenDAO 的其他部分的连接(因为你修改了 greenDAO 需要的逻辑)，并且文档中也明确说明了，一般情况下都不建议使用 `@Keep`。  
  
其实，我感觉添加 `@Keep` 的效果和去掉所有注解的效果可能是一样的，比如改成这样：  
```java  
public Note() {  
    System.out.println("需要自定义此构造方法");  
}  
```  
  
但是我感觉可能在这种情况下，greenDAO 仍然可以在需要的时候自动修改此方法(也即不会像添加 @Keep 那样可能会破坏 greenDAO 的逻辑)，因为按照文档的意思，只有添加 @Keep 才会阻止 greenDAO 自动修改代码。  
  
但如果是这样的话，为何错误提示没有说明呢？  
  
## @Convert 自定义类型  
[自定义类型](http://greenrobot.org/greendao/custom-types/)    
  
```java  
public @interface Convert {  
    Class<? extends PropertyConverter> converter();  
    Class columnType(); //可以在DB中保留的列的类。这仅限于greenDAO原生支持的所有java类。  
}  
```  
  
```java  
@Convert(converter = NoteTypeConverter.class, columnType = String.class) //类型转换类，自定义的类型在数据库中存储的类型  
private NoteType type; //在保存到数据库时会将自定义的类型 NoteType 通过 NoteTypeConverter 转换为数据库支持的 String 类型。反之亦然  
```  
  
# 使用教程  
greenDAO是Android的对象/关系映射（ORM）工具。 它为关系数据库SQLite提供了一个面向对象的接口。像greenDAO一类的ORM工具为你做很多重复性的任务，提供简单的数据接口。  
  
## 核心类简介  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181015/3m7bAk70AL.png?imageslim)    
  
一旦项目构建完毕，你就可以在Android项目中开始使用greenDAO了。  
以下核心类是greenDAO的基本接口：  
- `DaoMaster`：是 GreenDao 的入口，DaoMaster保存数据库对象(SQLiteDatabase)并管理特定模式[specific schema]的DAO类。它有静态方法来创建表或删除表。它的内部类OpenHelper和DevOpenHelper都是SQLiteOpenHelper的实现，用来在SQLite数据库中创建 schema。  
- `DaoSession`：管理特定schema的所有可用DAO对象，你可以使用其中一个的getter方法获取DAO对象。DaoSession还为实体提供了一些通用的持久性[persistence]方法，如插入，加载，更新，刷新和删除。 最后，DaoSession对象也跟踪[keeps track of] identity scope。  
- `DAOs`：数据访问对象[Data access objects]，用于实体的持久化和查询。 对于每个实体，greenDAO会生成一个DAO。 它比DaoSession拥有更多的持久化方法，例如：count，loadAll和insertInTx。  
- `Entities`：可持久化对象[Persistable objects]。 通常，实体是使用标准Java属性（如POJO或JavaBean）表示数据库行的对象。  
  
## 核心初始化  
最后，以下代码示例说明了初始化数据库和核心greenDAO类的第一步：  
```java  
//下面代码仅仅需要执行一次，一般会放在application  
DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");  
Database db = helper.getWritableDb();  
DaoMaster daoMaster = new DaoMaster(db);  
DaoSession daoSession = daoMaster.newSession();  
```  
  
    NoteDao noteDao = daoSession.getNoteDao();//一般在activity或者fragment中获取Dao对象  
> 该示例假设存在一个Note实体。 通过使用它的DAO，我们可以调用这个特定实体的持久化操作。  
  
完成以上所有工作以后，我们的数据库就已经自动生成了，接下来就可以对数据库进行操作了。  
  
## 增删改  
> PS：API 中的 `PK` 是 `PrimaryKey` 的意思，也就是`主键`的意思。  
  
### delete  
```java  
// 数据删除相关   
void    delete(T entity)  // 从数据库中删除给定的实体   
void    deleteAll()   // 删除数据库中全部数据   
void    deleteByKey(K key)  // 从数据库中删除给定Key所对应的实体   
void    deleteByKeyInTx(java.lang.Iterable<K> keys)  // 使用事务操作删除数据库中给定的所有key所对应的实体   
void    deleteByKeyInTx(K... keys)  // 使用事务操作删除数据库中给定的所有key所对应的实体   
void    deleteInTx(java.lang.Iterable<T> entities)  // 使用事务操作删除数据库中给定实体集合中的实体   
void    deleteInTx(T... entities)  // 使用事务操作删除数据库中给定的实体   
```  
  
### insert  
```java  
// 数据插入相关   
long    insert(T entity)  // 将给定的实体插入数据库   
void    insertInTx(java.lang.Iterable<T> entities)  // 使用事务操作，将给定的实体集合插入数据库   
void    insertInTx(java.lang.Iterable<T> entities, boolean setPrimaryKey)  // 使用事务操作将给定的实体集合插入数据库，并设置是否设定主键   
void    insertInTx(T... entities)  // 将给定的实体插入数据库   
long    insertOrReplace(T entity)  // 将给定的实体插入数据库，若此实体类存在，则覆盖   
void    insertOrReplaceInTx(java.lang.Iterable<T> entities)  // 使用事务操作，将给定的实体插入数据库，若此实体类存在，则覆盖   
void    insertOrReplaceInTx(java.lang.Iterable<T> entities, boolean setPrimaryKey)  // 插入数据库，若此存在则覆盖，并设置是否设定主键   
void    insertOrReplaceInTx(T... entities)  // 使用事务操作，将给定的实体插入数据库，若此实体类存在，则覆盖   
long    insertWithoutSettingPk(T entity)  // 将给定的实体插入数据库,但不设定主键  
```  
  
### save  
```java  
// 新增数据插入相关API   
void    save(T entity)  // 将给定的实体插入数据库，若此实体类存在，则更新   
void    saveInTx(java.lang.Iterable<T> entities) // 使用事务操作，将给定的实体插入数据库，若此实体类存在，则更新   
void    saveInTx(T... entities)  // 使用事务操作，将给定的实体插入数据库，若此实体类存在，则更新   
```  
  
### update  
```java  
//更新数据   
void    update(T entity)   // 更新给定的实体   
void    updateInTx(java.lang.Iterable<T> entities)   // 使用事务操作，更新给定的实体   
void    updateInTx(T... entities)  // 使用事务操作，更新给定的实体  
```  
  
### load  
```java  
// 加载相关  
T   load(K key)  // 加载给定主键的实体   
List<T>     loadAll()  // 加载数据库中所有的实体   
T   loadByRowId(long rowId)   // 加载某一行并返回该行的实体   
```  
  
## 查询  
[官方文档](http://greenrobot.org/greendao/documentation/queries/)    
查询返回符合特定条件[certain criteria]的实体。在 greenDAO 中，你可以使用原始[raw] SQL 来制定查询[formulate queries]，或者使用 `QueryBuilder` API，相对而言后者会更容易。  
  
此外，查询支持`懒加载`结果，当在大结果集上操作时，可以节省内存和性能。  
  
### QueryBuilder 查询条件  
编写SQL可能很困难，并且容易出现错误，这些错误仅在运行时被察觉。QueryBuilder类允许你为再不写SQL语句的情况下为实体构建自定义查询，并帮助在编译时检测错误。  
  
简单条件[condition]示例：查询所有名为“Joe”的用户，按姓氏排序：  
```java  
List<User> joes = userDao.queryBuilder()  
  .where(Properties.FirstName.eq("Joe"))  
  .orderAsc(Properties.LastName)  
  .list();  
```  
  
嵌套条件[Nested conditions]示例：获取1970年10月或之后出生的名为“Joe”的用户：  
```java  
QueryBuilder<User> qb = userDao.queryBuilder();  
qb.where(Properties.FirstName.eq("Joe"),  
    qb.or(Properties.YearOfBirth.gt(1970),  
        qb.and(Properties.YearOfBirth.eq(1970), Properties.MonthOfBirth.ge(10))));  
List<User> youngJoes = qb.list();  
```  
  
greenDao 的 `Properties` 支持的操作：  
- eq()      ==   
- noteq()   !=  
- gt()      >  
- lt()      <  
- ge        >=  
- le        <=  
- like()    包含  
- between   俩者之间  
- in        在某个值内  
- notIn     不在某个值内  
- isNull    为空  
- isNotNull 不为空  
  
### Order 排序  
您可以排序查询结果。基于姓氏和出生年份的人的例子：  
```java  
queryBuilder.orderAsc(Properties.LastName); // order by last name  
queryBuilder.orderDesc(Properties.LastName); // in reverse  
queryBuilder.orderAsc(Properties.LastName).orderDesc(Properties.YearOfBirth); // order by last name and year of birth  
```  
  
greenDAO使用的默认排序规则是`COLLATE NOCASE`，但可以使用 `stringOrderCollation()` 进行自定义。有关影响结果顺序的其他方法，请参阅QueryBuilder类的文档。  
  
### Limit, Offset 偏移和分页  
有时候，你只需要一个查询结果的子集，例如在用户界面中显示前10个元素。当拥有大量实体时，这是非常有用的(并且也节省资源)，并且你也不能使用 where 语句来限制结果。QueryBuilder 有定义限制和偏移的方法：  
- limit(int) //限制查询返回的结果数。  
- offset(int) //结合 limit 设置查询结果的偏移量。将跳过第一个偏移结果，并且结果的总数将受 limit 限制。你不能脱离 limit 使用offset。  
  
### Custom Types as Parameters  
通常，greenDAO以透明方式映射查询中使用的类型。 例如，`boolean`被映射到具有`0或1值的INTEGER`，而`Date`被映射到`(long)INTEGER`值。    
  
自定义类型是一个例外：在构建查询时，您始终必须使用数据库值类型。 例如，如果使用转换器将枚举类型映射到 int 值，则应在查询中使用 int 值。  
  
```java  
public enum NoteType { TEXT, PICTURE, UNKNOWN }  
​  
public static final String TYPE_TEXT = "文本";  
public String convertToDatabaseValue(NoteType entityProperty) {  
    if (entityProperty == TEXT) return TYPE_TEXT;  
    ...  
}  
```  
    NoteDao.Properties.Type.eq(NoteTypeConverter.TYPE_TEXT); //这里必须使用数据库中的值类型 String 而不能使用枚举类型 NoteType.TEXT  
  
### Query and LazyList  
Query 类表示可以多次执行的查询[executed multiple times]。当你使用 QueryBuilder 中的一个方法来获取结果时，`执行过程中 QueryBuilder 内部会使用 Query 类`。如果要多次运行同一个查询，应该在 QueryBuilder 上调用 `build()` 来创建 query 而并非执行它。  
  
greenDAO 支持唯一结果(0或1个结果)和结果列表。如果你想在 Query(或QueryBuilder)上有唯一的结果，请调用 `unique()`，这将为你提供单个结果或者在没有找到匹配的实体时返回 null。如果你的用例禁止 null 作为结果，调用 `uniqueOrThrow()` 将保证返回一个非空的实体(否则会抛出一个 DaoException)。  
  
如果希望多个实体作为查询结果，请使用以下方法之一：  
- list()：所有实体都加载到内存中。结果通常是一个简单的ArrayList。最方便使用。  
- listLazy()：实体按需[on-demand]加载到内存中。一旦列表中的元素第一次被访问[Once an element in the list is accessed for the first time]，它将被加载并缓存以备将来使用(否则不加载)。使用完后必须关闭。  
- listLazyUncached()：一个"虚拟"实体列表，对列表元素的任何访问都导致从数据库加载其数据[results in loading its data from the database]。使用完后必须关闭。  
- listIterator()：让我们通过按需加载数据(lazily)来遍历结果。数据未缓存。使用完后必须关闭。  
  
方法 listLazy()、listLazyUncached() 和 listIterator() 使用 greenDAO 的 LazyList 类。为了按需加载数据，它保存对数据库游标的引用[holds a reference to a database cursor]，这就是为什么你必须确保关闭懒性列表和迭代器(通常在try/finally块)。  
  
来自 listLazy() 的缓存 lazy 列表和 listIterator() 中的 lazy 迭代器在访问或遍历所有元素后自动关闭游标。如果列表处理过早停止，开发者需要自己调用close()进行处理[it’s your job to call close() if the list processing stops prematurely]。  
  
### 在多线程中使用查询  
如果在多个线程中使用查询，则必须调用 forCurrentThread() 获取当前线程的Query实例。 Query实例绑定到构建查询的那个线程[Object instances of Query are bound to their owning thread that built the query]。  
  
你可以安全地设置Query对象的参数，因为其他线程不会干涉到你[while other threads cannot interfere]。 如果其他线程尝试修改查询参数，或执行 query boun 到另一个线程，系统将抛出异常。 用这种方式，你就不再需要 synchronized 语句了。 实际上，你应该避免锁定，因为如果并发事务[concurrent transactions]使用相同的 Query 对象，这可能导致死锁。  
  
每次在使用构建器构建查询时，参数设置为初始参数，forCurrentThread() 将被调用[Every time, forCurrentThread() is called, the parameters are set to the initial parameters at the time the query was built using its builder.]。  
  
### 使用原始 SQL 语句查询  
如果QueryBuilder不能满足你的需求，有两种方法来执行原始 SQL 语句并同样可以返回实体对象。  
  
第一种，首选的方法是使用 QueryBuilder 和 WhereCondition.StringCondition。这样，你可以将任何 SQL 语句作为 WHERE 子句传递给查询构建器。  
```java  
WhereCondition cond = new WhereCondition.StringCondition(NoteDao.Properties.Id.columnName + ">=? ", 2L);  
List<Note> list = dao.queryBuilder().where(cond).build().list();  
```  
  
以下代码是一个理论[theoretical]示例，说明如何运行子选择(使用 join 连接将是更好的解决方案)：  
```java  
Query<User> query = userDao.queryBuilder()  
    .where(new StringCondition("_ID IN " +"(SELECT USER_ID FROM USER_MESSAGE WHERE READ_FLAG = 0)"))  
    .build();  
```  
  
第二种方法不使用QueryBuilder， 而是使用 queryRaw 或 queryRawCreate 方法。它们允许您传递一个原始 SQL 字符串，它附加在 SELECT 和实体列[entities columns]之后。 这样，你可以有任何 WHERE 和 ORDER BY 子句来选择实体。  
  
可以使用生成的常量引用表和列名称。这是建议的方式，因为可以避免打错字，因为编译器将检查名称。在实体的DAO中，你将发现 TABLENAME 包含数据库表的名称，以及一个内部类 Properties，其中包含所有的属性的常量(字段columnName)。  
```java  
String where = "where " + NoteDao.Properties.Type.columnName + " like ? AND " + NoteDao.Properties.Id.columnName + ">=?";  
ist<Note> list = dao.queryRaw(where, "%" + NoteTypeConverter.TYPE_TEXT + "%", Long.toString(2L));  
```  
  
可以使用别名 T 来引用实体表[entity table]。    
  
以下示例显示如何创建一个查询，该查询使用连接检索名为“admin”的组的用户(同样，greenDAO本身支持 joins 连接，这只是为了演示)：  
  
    Query<User> query = userDao.queryRawCreate(", GROUP G WHERE G.NAME=? AND T.GROUP_ID=G._ID", "admin");  
  
### DeleteQuery  
批量删除不会删除单个实体[Bulk deletes do not delete individual entities]，但是所有实体都符合一些条件。 要执行批量删除，请创建一个 QueryBuilder，调用其 buildDelete() 方法，并执行返回的 DeleteQuery。  
  
> 这一部分的API可能在将来会改变，例如可能会添加使用起来更方便的方法。  
> 请注意，批量删除目前不影响 identity scope 中的实体，例如，如果已删除的实体先前已缓存并通过其ID(load 方法)进行访问，则可以“复活”[resurrect]。 如果这可能会导致你的用例的问题[cause issues for your use case]，请考虑立即清除身份范围[identity scope]。  
  
### 查询时疑难问题的处理  
QueryBuilder 有两个静态标志可以启用 SQL 和参数 logging，启用后可以帮助你分析为何您的查询没有返回预期的结果：  
```java  
QueryBuilder.LOG_SQL = true;  
QueryBuilder.LOG_VALUES = true;  
```  
  
当调用其中一个构建方法时，它们将 log 生成的 SQL 命令和传递的值，并将它们与实际所需的值进行比较。此外，它可能有助于将生成的 SQL 复制到一些 SQLite 数据库浏览器中，并看看它如何执行。  
  
## 自定义类型  
自定义类型允许实体具有任何类型的属性。默认情况下，greenDAO支持以下类型：  
- boolean, Boolean  
- int, Integer  
- short, Short  
- long, Long  
- float, Float  
- double, Double  
- byte, Byte  
- byte[]  
- String  
- Date  
  
如果 greenDao 的默认参数类型满足不了你的需求，那么你可以使用数据库支持的原生数据类型(上面列出的那些)通过 `PropertyConverter` 类转换成你想要的属性。  
  
- 1、给自定义类型参数添加 @Convert 注释并添加对应参数  
  
```java  
@Convert(converter = NoteTypeConverter.class, columnType = String.class) //类型转换类，自定义的类型在数据库中存储的类型  
private NoteType type; //在保存到数据库时会将自定义的类型 NoteType 通过 NoteTypeConverter 转换为数据库支持的 String 类型。反之亦然  
```  
  
```java  
public enum NoteType {  
    TEXT, PICTURE, UNKNOWN  
}  
```  
  
- 2、自定义 `PropertyConverter` 接口的实现类，用于自定义类型和在数据库中存储的类型之间的相互转换：  
  
```java  
public class NoteTypeConverter implements PropertyConverter<NoteType, String> {  
    public static final String TYPE_TEXT = "文本";  
    public static final String TYPE_PICTURE = "图片";  
    public static final String TYPE_UNKNOWN = "未知格式";  
  
    @Override  
    public NoteType convertToEntityProperty(String databaseValue) { //将 数据库中存储的String类型 转换为 自定义的NoteType类型  
        switch (databaseValue) {  
            case TYPE_TEXT:  
                return NoteType.TEXT;  
            case TYPE_PICTURE:  
                return NoteType.PICTURE;  
            default: //不要忘记正确处理空值  
                return NoteType.UNKNOWN;  
        }  
    }  
  
    @Override  
    public String convertToDatabaseValue(NoteType entityProperty) { //将 自定义的NoteType类型 转换为在数据库中存储的String类型  
        switch (entityProperty) {  
            case TEXT:  
                return TYPE_TEXT;  
            case PICTURE:  
                return TYPE_PICTURE;  
            default:  
                return TYPE_UNKNOWN;  
        }  
    }  
}  
```  
  
注意：为了获得最佳性能，greenDAO将为所有转换使用单个转换器实例。确保转换器除了无参数默认构造函数之外没有任何其他构造函数。另外，确保它是线程安全的，因为它可能在多个实体上并发调用。  
  
如何正确转换枚举  
- 不要持久化枚举的序号或名称[ordinal or name]：两者都不稳定[unstable]，并且很有可能下次编辑枚举定义[edit your enum definitions]的时候就变化了。  
- 使用稳定的ids：在你的枚举中定义一个保证稳定的自定义属性(整数或字符串)。使用它来进行持久性映射[Use this for your persistence mapping]。  
- 未知值：定义一个 UNKNOWN 枚举值。它可以用于处理空值或未知值。这将允许你处理像 旧的枚举值被删除 而不会导致您的应用程序崩溃的情况。  
  
## 数据库加密  
[数据库加密](http://greenrobot.org/greendao/documentation/database-encryption/)  
  
greenDAO 支持加密的数据库来保护敏感数据。  
  
虽然较新版本的Android支持文件系统加密[file system encryption]，但Android本身并不为数据库文件提供加密。因此，如果攻击者获得对数据库文件的访问(例如通过利用安全缺陷或欺骗有 root 权限的设备的用户)，攻击者可以访问该数据库内的所有数据。使用受密码保护的加密数据库增加了额外的安全层。它防止攻击者简单地打开数据库文件。  
  
- 1、使用自定义SQLite构建    
  
因为Android不支持加密数据库，所以你需要在APK中捆绑自定义构建的SQLite[bundle a custom build of SQLite]。 这些定制构建包括CPU相关[dependent]和本地代码。 所以你的APK大小将增加几MByte。 因此，你应该只在你真的需要它时使用加密数据库。  
  
- 2、设置数据库加密  
  
greenDAO直接支持带有绑定的 [SQLCipher](https://www.zetetic.net/sqlcipher/)。 SQLCipher是使用`256位AES加密`的自定义构建的SQLite。  
请参阅 [SQLCipher for Android](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/)，了解如何向项目添加SQLCipher。  
  
在创建数据库实例时，只需调用 `.getEncryptedWritableDb()` 而不是 `.getWritableDb()` 即可像使用SQLite一样使用SQLCipher：  
```java  
DevOpenHelper helper = new DevOpenHelper(this, "notes-db-encrypted.db");  
Database db = helper.getEncryptedWritableDb("<your-secret-password>");  
daoSession = new DaoMaster(db).newSession();  
```  
  
- 3、数据库抽象[Abstraction]  
  
greenDAO 对所有数据库交互使用一个小型的抽象层[a thin abstraction layer]，因此支持标准和非标准的SQLite实现：  
- Android的标准 `android.database.sqlite.SQLiteDatabase`  
- SQLCipher的 `net.sqlcipher.database.SQLiteDatabase`  
- 任何SQLite兼容的数据库，它要实现 `org.greenrobot.greendao.database.Database`  
  
这使您能够轻松地从标准数据库切换到加密数据库，因为在针对DaoSession和单个DAO时，代码将是相同的。  
  
# 使用示例  
Demo地址：https://github.com/baiqiantao/AndroidOrmTest.git  
  
## 配置 gradle  
在根 build.gradle 文件中：  
```  
buildscript {  
    repositories {  
        mavenCentral() //greendao  
    }  
    dependencies {  
        classpath 'org.greenrobot:greendao-gradle-plugin:3.2.2' //greendao  
    }  
}  
```  
  
在模块的 build.gradle 文件中：  
```java  
greendao {  
    schemaVersion 1 //数据库模式的当前版本  
}  
```  
  
可配置的属性：  
- `schemaVersion`：数据库模式[database schema]的当前版本。 这用于 *OpenHelpers 类在模式版本之间迁移。`如果更改 实体/数据库 模式，则必须增加此值`。 默认值为1。  
- `daoPackage`：生成的DAO，DaoMaster和DaoSession的包名称。默认为源实体的包名称。  
- `targetGenDir`：生成的源码应存储的位置。默认目录为 `build/generated/source/greendao`。  
- `generateTests`：设置为 true 以自动生成单元测试。  
- `targetGenDirTests`：生成的单元测试应存储在的基本目录。 默认为 `src/androidTest/java`。  
  
在模块的 build.gradle 文件中：  
  
```java  
apply plugin: 'org.greenrobot.greendao' //greendao  
implementation 'org.greenrobot:greendao:3.2.2' //greendao  
```  
  
在构建项目时，它会生成 `DaoMaster，DaoSession 和 **DAO` 等类。如果在更改实体类之后遇到错误，请尝试重新生成项目，以确保清除旧生成的类。  
  
## 定义实体类  
```java  
@Entity(  
        nameInDb = "BQT_USERS",// 指定该表在数据库中的名称，默认是基于实体类名  
        indexes = {@Index(value = "text, date DESC", unique = true)},// 定义跨多个列的索引  
        createInDb = true,// 高级标志，是否创建该表。默认为true。如果有多个实体映射一个表，或者该表已在外部创建，则可置为false  
        //schema = "bqt_schema",// 告知GreenDao当前实体属于哪个schema。属于不同模式的实体应不具有关系。  
        active = true,// 标记一个实体处于活动状态，活动实体(设置为true)有更新、删除和刷新方法。默认为false  
        generateConstructors = true,//是否生成所有属性的构造器。注意：无参构造器总是会生成。默认为true  
        generateGettersSetters = true//是否应该生成属性的getter和setter。默认为true  
)  
public class Note {  
    @Id(autoincrement = false) //必须 Long 或 long 类型的，SQLite 上的自增长会引入额外的资源使用，通常可以避免使用  
    private Long id;  
  
    @NotNull//在保存到数据库中之前需要保证此值不为null，否则会报 IllegalArgumentException ，并直接崩溃  
    private String text;  
  
    @Transient //表明此字段不存储到数据库中，用于不需要持久化的字段，比如临时状态  
    private String comment;  
  
    @Property(nameInDb = "TIME") //为该属性在数据库中映射的字段名设置一个非默认的名称  
    private Date date;  
  
    @Convert(converter = NoteTypeConverter.class, columnType = String.class) //类型转换类，自定义的类型在数据库中存储的类型  
    private NoteType type; //在保存到数据库时会将自定义的类型 NoteType 通过 NoteTypeConverter 转换为 String 类型。反之亦然  
}  
```  
  
编写完实体类以后按下 Ctrl+F9(Make project)，程序会自动编译生成 dao 文件，生成的文件一共有三个：DaoMaster，DaoSession 和 **DAO。  
  
## 演示 DAO 的使用  
```java  
public class GreenDaoActivity extends ListActivity {  
    private Note mNote = Note.newBuilder().text("【text】").date(new Date()).comment("包青天").type(NoteType.PICTURE).build();  
    private NoteDao dao;  
    private EditText editText;  
  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"0、插入一条数据",  
                "1、演示 insert 数据时一些特殊情况",  
                "2、插入数据：insertInTx、insertOrReplace、save、saveInTx、insertWithoutSettingPk",  
                "3、删除数据：delete、deleteAll、deleteByKey、deleteByKeyInTx、deleteInTx",  
                "4、更新数据：update、updateInTx、updateKeyAfterInsert",  
                "5、查询数据：where、whereOr、limit、offset、*order*、distinct、or、and、queryRaw*",  
                "6、数据加载和缓存：load、loadByRowId、loadAll、detach、detachAll、unique、uniqueOrThrow",  
                "7、其他API：getKey、getPkProperty、getProperties、getPkColumns、getNonPkColumns",  
                "8、删除所有数据：deleteAll",  
                "tag 值 +1",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
        editText = new EditText(this);  
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);  
        getListView().addFooterView(editText);  
        initDB();  
    }  
  
    private void initDB() {  
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");  
        Database db = helper.getWritableDb();  
        DaoMaster daoMaster = new DaoMaster(db);  
        DaoSession daoSession = daoMaster.newSession();  
        dao = daoSession.getNoteDao();  
    }  
  
    int tag = -1;  
  
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        if (TextUtils.isEmpty(editText.getText())) {  
            tag++;  
            Toast.makeText(this, "最新值为" + tag, Toast.LENGTH_SHORT).show();  
        } else {  
            tag = Integer.parseInt(editText.getText().toString());  
            editText.setText("");  
        }  
        switch (position) {  
            case 0://插入一条数据  
                simpleInsert();  
                break;  
            case 1://演示 insert 数据时一些特殊情况  
                insert();  
                break;  
            case 2://插入数据  
                inserts();  
                break;  
            case 3://删除数据  
                deletes();  
                break;  
            case 4://更新数据  
                updates();  
                break;  
            case 5://查询数据  
                query();  
                break;  
            case 6:  
                loadAndDetach();  
                break;  
            case 7:  
                testOtherApi();  
                break;  
            case 8:  
                dao.deleteAll();  
                break;  
        }  
    }  
  
    private void simpleInsert() {  
        Note insertNote = Note.newBuilder().type(tag % 3 == 2 ? NoteType.UNKNOWN :  
                (tag % 3 == 1 ? NoteType.PICTURE : NoteType.TEXT))  
                .comment("comment" + tag).date(new Date()).text("text" + tag).build();  
        long insertId = dao.insert(insertNote);// 将给定的实体插入数据库，默认情况下，插入的数据的Id将从1开始递增  
        Log.i("bqt", "插入数据的ID= " + insertId + getAllDataString());  
    }  
  
    private void insert() {  
        long id = -10086;//以下注释不管是否给 id 设置【@Id(autoincrement = true)】注解时的得出的结论都是一样的  
        if (tag % 9 == 6) id = dao.insert(new Note());//同样会崩溃，插入数据的 text 不能为空(IllegalArgumentException)  
        else if (tag % 9 == 5) id = dao.insert(Note.newBuilder().id(6L).text("text5").build());//会崩溃，因为此 id 已经存在  
        else if (tag % 9 == 4) id = dao.insert(Note.newBuilder().text("text4").build());//id =12  
        else if (tag % 9 == 3) id = dao.insert(Note.newBuilder().id(6L).text("text3").build());//自定义id，id =6  
        else if (tag % 9 == 2) id = dao.insert(Note.newBuilder().text("text2").type(NoteType.UNKNOWN).build());//id = 11  
        else if (tag % 9 == 1) id = dao.insert(Note.newBuilder().id(10L).text("text1").build());//插入的数据的Id将从最大值开始  
        else if (tag % 9 == 0) id = dao.insert(Note.newBuilder().id(-2L).text("text0").build());//id =-2，并不限制id的值  
        Log.i("bqt", "插入数据的ID= " + id + getAllDataString());  
    }  
  
    private void inserts() {  
        Note note1 = Note.newBuilder().text("text1-" + tag).date(new Date()).comment("包青天").type(NoteType.TEXT).build();  
        Note note2 = Note.newBuilder().text("text2-" + tag).date(new Date()).comment("包青天").type(NoteType.TEXT).build();  
        if (tag % 9 >= 6) {  
            dao.save(note1);// 将给定的实体插入数据库，若此实体类存在，则【更新】  
            Note quryNote = dao.queryBuilder().build().list().get(0);  
            quryNote.setText("【新的Text2】");  
            mNote.setText("【新的Text2】");  
            dao.saveInTx(quryNote, mNote);//同样参数可以时集合，基本上所有的 **InTx 方法都是这样的  
        } else if (tag % 9 == 5) {  
            Note quryNote = dao.queryBuilder().build().list().get(0);  
            quryNote.setText("【新的Text】");  
            mNote.setText("【新的Text】");  
            dao.insertOrReplaceInTx(quryNote, mNote);//同样参数可以时集合，并可选择是否设定主键  
        } else if (tag % 9 == 4) {  
            long rowId = dao.insertOrReplace(mNote);// 将给定的实体插入数据库，若此实体类存在则【覆盖】，返回新插入实体的行ID  
            Log.i("bqt", "对象的ID=" + mNote.getId() + "  返回的ID=" + rowId);  
        } else if (tag % 9 == 3) dao.insertWithoutSettingPk(note1);//插入数据库但不设定主键(不明白含义)，返回新插入实体的行ID  
        else if (tag % 9 == 2) dao.insertInTx(Arrays.asList(note1, note2), false);//并设置是否设定主键(不明白含义)  
        else if (tag % 9 == 1) dao.insertInTx(Arrays.asList(note1, note2));// 使用事务操作，将给定的实体集合插入数据库  
        else if (tag % 9 == 0) dao.insertInTx(note1, note2);// 使用事务操作，将给定的实体集合插入数据库  
        Log.i("bqt", getAllDataString());  
    }  
  
    private void deletes() {  
        if (tag % 9 >= 7) dao.deleteAll(); //删除数据库中全部数据。再插入的数据的 Id 同样将从最大值开始  
        else if (tag % 9 == 6) dao.deleteByKeyInTx(Arrays.asList(1L, 3L));  
        else if (tag % 9 == 5) dao.deleteByKeyInTx(1L, 2L);// 使用事务操作删除数据库中给定的所有key所对应的实体  
        else if (tag % 9 == 4) dao.deleteByKey(1L);//从数据库中删除给定Key所对应的实体  
        else if (tag % 9 == 3) dao.delete(new Note());//从数据库中删除给定的实体  
        else if (tag % 9 == 2) dao.deleteInTx(new Note(), new Note());// 使用事务操作删除数据库中给定实体集合中的实体  
        else if (tag % 9 == 1) dao.deleteInTx(Arrays.asList(new Note(), new Note()));  
        else if (tag % 9 == 0) dao.deleteInTx(dao.queryBuilder().limit(1).list());  
        Log.i("bqt", getAllDataString());  
    }  
  
    private void query() {  
        WhereCondition cond1 = NoteDao.Properties.Id.eq(1);//==  
        WhereCondition cond2 = NoteDao.Properties.Type.notEq(NoteTypeConverter.TYPE_UNKNOWN);//!=  
        //在构建查询时，必须使用数据库值类型。 因为我们使用转换器将枚举类型映射到 String 值，则应在查询中使用 String 值  
        WhereCondition cond3 = NoteDao.Properties.Id.gt(10);//大于  
        WhereCondition cond4 = NoteDao.Properties.Id.le(5);// less and eq 小于等于  
        WhereCondition cond5 = NoteDao.Properties.Id.in(1, 4, 10);//可以是集合。在某些值内，notIn   不在某些值内  
        WhereCondition cond6 = NoteDao.Properties.Text.like("%【%");//包含  
        // 最常用Like通配符：下划线_代替一个任意字符(相当于正则表达式中的 ?)   百分号%代替任意数目的任意字符(相当于正则表达式中的 *)  
        WhereCondition cond7 = NoteDao.Properties.Date.between(System.currentTimeMillis() - 1000 * 60 * 20, new Date());//20分钟  
        WhereCondition cond8 = NoteDao.Properties.Date.isNotNull();//isNull  
        WhereCondition cond9 = new WhereCondition.StringCondition(NoteDao.Properties.Id.columnName + ">=? ", 2L);  
  
        //其他排序API：orderRaw(使用原生的SQL语句)、orderCustom、stringOrderCollation、preferLocalizedStringOrder  
        Property[] orders = new Property[]{NoteDao.Properties.Type, NoteDao.Properties.Date, NoteDao.Properties.Text};//同样支持可变参数  
        List<Note> list = null;  
  
        if (tag % 9 == 8) {  
            String where = "where " + NoteDao.Properties.Type.columnName + " like ? AND " + NoteDao.Properties.Id.columnName + ">=?";  
            list = dao.queryRaw(where, "%" + NoteTypeConverter.TYPE_TEXT + "%", Long.toString(2L));  
            Log.i("bqt", "queryRaw查询到的数据" + new Gson().toJson(list));  
            list = dao.queryRawCreate(where, "%" + NoteTypeConverter.TYPE_TEXT + "%", Long.toString(2L)).list();  
        } else if (tag % 9 == 7) list = dao.queryBuilder().where(cond9).build().list(); //执行原生的SQL查询语句  
        else if (tag % 9 == 6) {  
            QueryBuilder<Note> qb = dao.queryBuilder();  
            WhereCondition condOr = qb.or(cond2, cond3, cond4);  
            WhereCondition condAnd = qb.and(cond6, cond7, cond8);  
            list = qb.where(cond1, condOr, condAnd).build().list();  
        } else if (tag % 9 == 5) list = dao.queryBuilder().whereOr(cond5, cond6, cond7).build().list();//多个语句间是 OR 的关系  
        else if (tag % 9 == 4) list = dao.queryBuilder().where(cond5, cond6, cond7).build().list();//多个语句间是 AND 的关系  
        else if (tag % 9 == 3) list = dao.queryBuilder().where(cond4).offset(1).limit(2).build().list();//(必须)与limit一起设置查询结果的偏移量  
        else if (tag % 9 == 2) list = dao.queryBuilder().where(cond5).limit(2).distinct().build().list();//限制查询结果个数，避免重复的实体(不明白)  
        else if (tag % 9 == 1) list = dao.queryBuilder().where(cond2).orderAsc(orders).build().list(); //常用升序orderAsc、降序orderDesc  
        else if (tag % 9 == 0) list = dao.queryBuilder().build().list();  
        Log.i("bqt", getAllDataString() + "\n查询到的数据" + new Gson().toJson(list));  
    }  
  
    private void updates() {  
        dao.save(mNote);//确保要更新的实体已存在  
  
        if (tag % 9 >= 4) dao.update(Note.newBuilder().text("text-update0").build()); //如果要更新的实体不存在，则会报DaoException异常  
        else if (tag % 9 == 3) {  
            mNote.setText("【Text-update3】");  
            long rowId = dao.updateKeyAfterInsert(mNote, 666L);  
            Log.i("bqt", "rowId=" + rowId);//更新实体内容，并更新key  
        } else if (tag % 9 == 2) {  
            mNote.setText("【Text-update2】");  
            dao.updateInTx(Arrays.asList(dao.queryBuilder().build().list().get(0), mNote));// 使用事务操作，更新给定的实体  
        } else if (tag % 9 == 1) {  
            Note updateNote = dao.queryBuilder().build().list().get(0);  
            updateNote.setText("【Text-update1】");  
            dao.updateInTx(updateNote, mNote);// 使用事务操作，更新给定的实体  
        } else if (tag % 9 == 0) dao.update(mNote);//更新给定的实体，不管有没有变化，只需用新的实体内容替换旧的内容即可(必须是同一实体)  
        Log.i("bqt", getAllDataString());  
    }  
  
    private void loadAndDetach() {  
        Note note1 = dao.load(1L);// 加载给定主键的实体  
        Note note2 = dao.load(10086L);//如果给定主键不存在则返回null  
        Note note3 = dao.loadByRowId(1L);// 加载某一行并返回该行的实体  
        Note note4 = dao.queryBuilder().limit(1).unique();//返回一个元素(可能为null)。如果查询结果数量不是0或1，则抛出DaoException  
        Note note5 = dao.queryBuilder().limit(1).build().uniqueOrThrow(); //保证返回一个非空的实体(否则会抛出一个 DaoException)  
  
        if (tag % 9 >= 2) {  
            note1.setText("-------Text" + System.currentTimeMillis());  
            Log.i("bqt", dao.load(1L).getText());//因为这里获取到的实体对象仍然是 note1 ，所以这里的值立即就改变了！  
            dao.detachAll();//清除指定实体缓存，将会重新从数据库中查询，然后封装成新的对象  
            Log.i("bqt", dao.load(1L).getText());//因为只更改了实体但没有调用更新数据库的方法，所以数据库中的数据并没有改变  
        } else if (tag % 9 == 1) {  
            dao.detach(note4);//清除指定实体缓存；dao.detachAll 清除当前表的所有实体缓存；daoSession.clear() 清除所有的实体缓存  
            Note note7 = dao.load(1L);// 清除指定Dao类的缓存后，再次得到的实体就是构造的新的对象  
            Log.i("bqt", (note1 == note3 && note1 == note4 && note1 == note5) + "  " + (note1 == note7));//true  false  
        } else if (tag % 9 == 0) {  
            Log.i("bqt", new Gson().toJson(Arrays.asList(note1, note2, note3, note4, note5)));  
        }  
    }  
  
    private void testOtherApi() {  
        dao.save(mNote);//确保要更新的实体已存在  
        //查找指定实体的Key，当指定实体在数据库表中不存在时返回 null(而不是返回-1或其他值)  
        Log.i("bqt", "实体的 Key = " + dao.getKey(mNote) + "  " + dao.getKey(Note.newBuilder().text("text").build()));  
  
        Property pk = dao.getPkProperty();//id  _id 0   true    class java.lang.Long  
        for (Property p : dao.getProperties()) {  
            Log.i("bqt", p.name + "\t" + p.columnName + "\t" + p.ordinal + "\t" + p.primaryKey + "\t" + p.type + "\t" + (p == pk));  
        }  
  
        Log.i("bqt", "所有的PK列：" + Arrays.toString(dao.getPkColumns()));//[_id]  
        Log.i("bqt", "所有的非PK列：" + Arrays.toString(dao.getNonPkColumns()));//[TEXT, TIME, TYPE]  
    }  
  
    private String getAllDataString() {  
        List<Note> list1 = dao.queryBuilder().build().list();//缓存查询结果  
        List<Note> list2 = dao.queryBuilder().list();  
        List<Note> list3 = dao.loadAll();  
        return " 个数=" + dao.count() + "，等价=" + (list1.equals(list2) && list1.equals(list3)) + "，数据=" + new Gson().toJson(list1);//true  
    }  
}  
```  
  
2018-5-28  
