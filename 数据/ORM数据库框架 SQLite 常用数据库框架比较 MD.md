| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
ORM数据库框架 SQLite 常用数据库框架比较  
***  
目录  
===  

- [简介](#简介)
- [关系型数据库](#关系型数据库)
	- [greenDAO 智能代码生成 10K+ 首选](#greendao-智能代码生成-10k-首选)
	- [ORMLite 注解+反射 1.5K 通用](#ormlite-注解反射-15k-通用)
	- [LitePal 注解+反射 5K 最简单](#litepal-注解反射-5k-最简单)
	- [DBFlow 5K](#dbflow-5k)
	- [ActiveAndroid 4.5K](#activeandroid-45k)
	- [sugar 2.5K](#sugar-25k)
- [NoSQL 对象型数据库](#nosql-对象型数据库)
	- [Realm 10K-](#realm-10k-)
	- [ObjectBox 2.5K](#objectbox-25k)
  
# 简介  
[Demo](https://github.com/baiqiantao/AndroidOrmTest.git)  
  
这些ORM框架的实现方式主要有以下几种：  
- `注解`。分为`运行时注解`(Retention为RUNTIME)，`编译时注解`(另外两种Retention)，运行时注解其实也是利用了反射的原理，非运行时注解一般都是用于(编译时)生成代码的  
- `反射`。除非运行前已经自动生成了代码，否则可以肯定就是通过反射方式实现的。  
- `直接生成代码`。一般是通过程序根据`注解`自动生成需要的代码。  
  
通过运行时注解或反射去建立数据表和实体的关系会导致`性能比较低`。从性能角度出发，应该选择使用编译时注解或代码生成的框架。当然，成熟程度，文档资料等也是考量点。  
  
# 关系型数据库  
## greenDAO 智能代码生成 10K+ 首选  
[GitHub](https://github.com/greenrobot/greenDAO)  
greenDAO与其他常见的ORM框架不同，其原理`不是根据反射进行数据库的各项操作`，而是一开始就人工`生成业务需要的Model和DAO文件`，业务中可以直接调用相应的DAO文件进行数据库操作，从而避免了因反射带来的性能损耗和效率低下。  
  
以查询为例，其首先是创建数据库，然后在`SQLiteOpenHelper.onCreate`方法中根据已生成的model创建所有的表，而db.query其实就是Android`原生的查询操作`，只不过参数是经过DAO文件处理过的，无需手动匹配。  
  
由于需要人工生成model和DAO文件，所以greenDAO的配置就略显复杂。  
  
优点：效率高，速度快，文件较小，占用更少的内存，操作实体灵活  
缺点：学习成本较高。  
  
## ORMLite 注解+反射 1.5K 通用  
[GitHub](https://github.com/j256/ormlite-android)  
基于`注解和反射`的的方式，导致ormlite性能有着一定的损失(运行时注解其实也是利用了反射的原理)  
  
OrmLite 不是 Android 平台专用的ORM框架，它是`Java ORM`。支持JDBC连接，Spring以及Android平台。语法中广泛使用了运行时注解。  
  
优点：文档较全面，社区活跃，有好的维护，使用简单，易上手。  
缺点：基于反射，效率较低(GreenDAO比OrmLite要快几乎4.5倍)  
  
## LitePal 注解+反射 5K 最简单  
[GitHub](https://github.com/LitePalFramework/LitePal)  
An Android library that makes developers use SQLite database extremely easy.  
  
- LitePal通过LitePal.xml文件获取数据库的名称、版本号以及表，然后自动创建数据库和表，以及表数据类型和非空约束等。  
- 要执行增删改查操作的数据model都会继承DataSupport，最后将查询得到的数据转换成List并返回。  
- LitePal不管是创建数据库、表还是执行增删改查，都是根据Model的类名和属性名，每次都需要进行反射拼装，然后调用Android原生的数据库操作，或者直接执行sql语句，实现相应的功能。  
  
特点  
- 根据反射进行数据库的各项操作(速度比GreenDAO要慢很多很多)  
- 采用对象关系映射(ORM)的模式  
- 很“轻”，jar包只有100k不到  
- 使用起来比较简单  
- 支持直接用sql原始语句实现查询的api方法  
  
## DBFlow 5K  
[GitHub](https://github.com/Raizlabs/DBFlow)  
A blazing fast, powerful, and very simple ORM android database library that writes database code for you.  
  
特点  
- 相关代码通过`编译时注解`生成，不会导致性能瓶颈。  
- 功能特性比较丰富，文档较完善。  
- 数据库版本升级和数据迁移有较简便的解决方案，支持集成`SQLCipher`加密，支持Content Provider Generation。  
  
## ActiveAndroid 4.5K  
[GitHub](https://github.com/pardom-zz/ActiveAndroid)    
[官网](http://www.activeandroid.com)    
Active record[活动目录] style SQLite persistence[持久化] for Android  
Active Record是Yii、Rails等框架中对ORM实现的典型命名方式。Active Android 帮助你以面向对象的方式来操作SQLite。  
  
## sugar 2.5K  
[GitHub](https://github.com/chennaione/sugar)  
[官网](http://satyan.github.com/sugar/)  
Insanely easy way to work with Android Database.     
  
SugarORM 是 Android 平台专用ORM。提供简单易学的APIs，可以很容易的处理1对1和1对多的关系型数据，并通过3个函数save(), delete() 和 find() (或者 findById()) 来简化CRUD基本操作。  
  
# NoSQL 对象型数据库  
NoSQL是趋势。其实对关系型数据库引入ORM，就是实现了对象型数据库要做的事情。  
  
## Realm 10K-  
[GitHub](https://github.com/realm/realm-java)  
[官网](http://realm.io)  
  
Realm is a mobile database: a replacement for SQLite & ORMs  
  
基于C++编写，直接运行在你的设备硬件上(不需要被解释)，因此运行很快。  
Realm是一个直接在手机，平板电脑或可穿戴设备中运行的移动数据库。 此存储库包含Realm的Java版本的源代码，该版本目前仅在Android上运行。  
  
特征：  
- 移动优先：Realm是第一个直接在手机、平板电脑和可穿戴设备内部运行的数据库。  
- 简单：数据直接作为对象公开，并且可以通过代码查询，从而消除了对ORM性能和维护问题的需求。 此外，我们努力将我们的API保持在极少数类上：我们的大多数用户直观地选择它[pick it up intuitively]，在几分钟内启动并运行简单的应用程序。  
- 现代[Modern]：Realm支持简单的线程安全，关系和加密[relationships & encryption]。  
- 快速：Realm在常见操作上比原始SQLite更快，同时保持极其丰富的功能集。  
  
## ObjectBox 2.5K  
[GitHub](https://github.com/objectbox/objectbox-java)  
[官网](http://objectbox.io)    
  
ObjectBox是一个超快的面向对象数据库，具有强大的关系支持[strong relation support]。 ObjectBox是嵌入到您的Android，Linux，macOS或Windows应用程序中。  
  
特性：  
- 超快：我们构建ObjectBox的动机是提供最佳性能。到目前为止，ObjectBox优于我们测试过的所有嵌入式数据库。  
- 对象API：不再有行、列和SQL - ObjectBox是一个从出生开始就是为对象构建的移动数据库(没有ORM，没有SQLite)。API简洁、易于学习，只需要使用SQLite所需的一小部分代码。  
- QueryBuilder：通过使用ObjectBox，会在编译时检查，因此，您没有更多的因拼写错误而导致的运行时崩溃。  
- 对象关系：对象引用/关系[Object references / relationships]是内置类型，它们是原生的引用[native references]。  
- 反应[Reactive]：对数据变化做出的反应简单而有力。使用ObjectBox中的反应数据观察器[reactive data observers]或与RxJava集成。  
- 多平台：ObjectBox已经支持Android和普通Java(Linux和Windows)。 MacOS和iOS是路线图中的下一个平台。  
- 即时单元测试：使用我们的多平台方法[multiplatform approach]，您可以在几秒钟内使用真实数据库在桌面上运行普通单元测试(无需Robolectric，无需 instrumentation tests)。  
- 强大的技术[Robust technology]：ACID属性和多版本并发控制(Multiversion Concurrency Control, MVCC)为您提供安全的事务和并行性[safe transactions and parallelism]。 ACID代表：原子[Atomic]，Consitent，隔离[Isolated]，耐用[Durable]。  
- 简单线程：ObjectBox返回的对象在没有附加字符串的所有线程中工作。  
- 无手动模式迁移[No manual schema migrations]：ObjectBox负责处理具有添加，删除和重命名属性的对象新版本[new object versions]。  
- DaoCompat库：已经使用了greenDAO？这个小程序库为您提供了 familiar greenDAO APIs for ObjectBox.  
- 历经测试：自从ObjectBox处于测试阶段以来，我们已经在拥有超过150,000个月活跃用户和数千个设备的应用程序中运行它。当然，我们内部进行了大量的单元测试(> 1000次单独测试)。  
  
2018-8-17  
