| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
数据库 升级 SQLite onUpgrade 总结 MD  
***  
目录  
===  

- [SQLite数据库简介](#SQLite数据库简介)
- [SQLite数据库升级](#SQLite数据库升级)
	- [单级升级](#单级升级)
	- [跨级升级](#跨级升级)
	- [案例](#案例)
		- [第一版需求](#第一版需求)
		- [第二版需求](#第二版需求)
		- [第三版需求](#第三版需求)
  
# SQLite数据库简介  
[官方网站](http://www.sqlite.org)  
  
Android使用开源的、与操作系统无关的SQL数据库`SQLite`。  
  
SQLite第一个Alpha版本诞生于2000年5月，它是一款轻量级数据库，它的设计目标是`嵌入式`的，占用资源非常的低，只需要`几百K`的内存就够了。  
  
SQLite已经被多种软件和产品使用，Mozilla FireFox就是使用SQLite来存储配置数据的，`Android和iPhone`都是使用SQLite来存储数据的。  
  
SQLite数据库是D.Richard Hipp用`C`语言编写的开源嵌入式数据库，支持的数据库大小为`2TB`。  
  
它具有如下特征：  
- 轻量级：SQLite和 C\S 模式的数据库软件不同，它是`进程内`的数据库引擎，因此`不存在数据库的客户端和服务器`。使用SQLite一般只需要带上它的一个动态库，就可以享受它的全部功能。而且那个动态库的尺寸也相当小。   
- 独立性：SQLite数据库的核心引擎本身不依赖第三方软件，使用它也不需要安装，所以在使用的时候能够省去不少麻烦。   
- 隔离性：SQLite数据库中的所有信息（比如表、视图、触发器）都包含在一个文件内，方便管理和维护。   
- 跨平台：SQLite数据库支持大部分操作系统，除了我们在电脑上使用的操作系统之外，很多手机操作系统同样可以运行，比如Android、Windows Mobile、Symbian、Palm等。   
- 多语言接口：SQLite数据库支持很多语言编程接口，比如C\C++、Java、Python、dotNet、Ruby、Perl等，得到更多开发者的喜爱。   
- 安全性：SQLite数据库通过数据库级上的独占性和共享锁来实现独立事务处理。这意味着`多个进程可以在同一时间从同一数据库读取数据，但只有一个可以写入数据`。在某个进程或线程向数据库执行`写`操作之前，必须获得独占锁定。在发出独占锁定后，其他的读或写操作将不会再发生。   
  
# SQLite数据库升级  
在app版本升级时，同时升级了Sqlite数据库的版本号的话，如果需要保留之前的数据，需要在`onUpgrade`方法中做处理。这里记录一下在onUpgrade处理升级的时候的一些注意事项。  
  
先看下常用的SQLiteOpenHelper的方法：  
  
```java  
public class DatabaseHelper extends SQLiteOpenHelper {  
  
    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {  
        super(context, name, factory, version);  
    }  
  
    /**  
     * 1、在第一次打开数据库的时候才会走  
     * 2、清除数据后再次运行(相当于第一次打开)这个方法也会走  
     * 3、没有清除数据，这个方法不会走  
     * 4、数据库升级的时候这个方法不会走  
     */  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
  
    }  
  
    /**  
     * 1、这个方法只有当数据库已经存在，而且版本升高的时候，才会走  
     * 2、第一次创建数据库的时候，这个方法不会走  
     * 3、清除数据后再次运行(相当于第一次创建)这个方法也不会走  
     */  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
  
    }  
}  
```  
  
## 单级升级  
如果第一次的数据库版本为 n，现在升级之后版本为 n+1，即当前app版本比上次的app版本中的数据库的版本号高 1 级，先看下这时的几种处理情况：  
  
**1、摒弃之前的表并重新创建该表**  
  
这种情况一般是数据库中的数据可能是一些无关紧要的临时数据，处理比较简单粗暴，`直接删除重建`，且表的结构跟之前完全一样。不过应该很少有采用这种方式的吧。  
  
```java  
@Override  
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
       switch (oldVersion) {  
           case 1://这里的数值是上次的版本，也就是针对上次的版本，本次的版本要做哪些改变  
                db.execSQL("DROP TABLE IF EXISTS " + TableEvent.TABLE_NAME); //删除旧表  
                db.execSQL(TableEvent.CREATE_SQL); //创建新表  
           default:  
                break;  
        }  
}  
```  
  
**2、在之前基础上创建新的表**  
  
这种情况也比较简单，主要是要创建新的表，而且这个表跟之前的表没有什么关系。这时同时也要在onCreate中加上执行新表创建的sql(针对新用户)。  
  
```java  
@Override  
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
       switch (oldVersion) {  
           case 1://这里的数值是上次的版本，也就是针对上次的版本，本次的版本要做哪些改变  
                //这种情况需要【同时】在onCreate中加上执行新表创建的sql(针对新用户)  
                db.execSQL(xxxxx); //创建新表  
           default:  
                break;  
        }  
}  
```  
  
**3、在老表基础上新增字段 **  
  
这种情况需要修改老的表新增字段  
  
```java  
@Override  
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
       switch (oldVersion) {  
           case 1://这里的数值是上次的版本，也就是针对上次的版本，本次的版本要做哪些改变  
                //这种情况需要【同时】在onCreate中加上执行新增字段的创建表sql(针对新用户)  
                db.execSQL("ALTER TABLE TableEvent.CREATE_SQL ADD COLUMN TableEvent.xxx VARCHAR(255)"); //新增字段  
           default:  
                break;  
        }  
}  
```  
  
**4、数据库升级时保留老的数据并同步到新的表当中 **  
  
这种情况是新表保留老表的字段结构情况，这种是面试经常考察的知识点。  
  
基本思路：  
- 将原表A改名为临时表B  
- 创建新表C(名称与原表A一样)  
- 导入数据到新表C　　  
- 删除临时表B  
  
```java  
@Override  
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
       switch (oldVersion) {  
           case 1://这里的数值是上次的版本，也就是针对上次的版本，本次的版本要做哪些改变  
                db.execSQL("alter table book rename to _temp_book"); //重命名老表  
                db.execSQL("create table book(bookId integer primarykey, bookName text);"); //创建新的表，表名跟原来一样，并保留原来的字段；这句要同时放到onCreate中，针对新用户  
                db.execSQL("insert into book select *,'' from _temp_book");//将重命名后的老表中的数据导入新的表中  
                db.execSQL("drop table _temp_book");//删除老表  
           default:  
                break;  
        }  
}  
```  
  
**5、新表跟以前的表字段结构完全不一样**  
  
这种情况是新表跟以前字段结构完全不一样，或者说有大部分的差异，跟上面第4种处理差不多，但是需要`手动查询`老表的某些字段的数据，然后插入的新表的对应字段，最后删除老表即可。  
  
## 跨级升级  
  
前面几种情况是单级升级的时候，针对上次的版本本次的版本需要做的处理，如果是跨级升级，比如用户app很久没有更新了，数据库版本号一直是1，而客户端最新版本其实对应的数据库版本已经是3了，那么我中途可能对数据库做了很多修改，可以迭代升级，方便代码理解和维护。  
  
基本方法：  
- 使用`不带break的switch语句`迭代升级  
- 增加新列：直接在表上新建列  
- 删除列或者更改一个已经存在的字段的名称、数据类型、限定符等：要`建立新表`，将原表的数据`复制`到新表中  
  
使用不带break的switch迭代升级：  
```java  
@Override  
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    switch (oldVersion) {  
        case 1:  
                db.execSQL(CREATE_TBL_CATEGORY)  // 建立新表  
        case 2:  
                db.execSQL("ALTER TABLE Book ADD COLUMN category_id INTEGER");  // 增加字段  
        case 3:  
                //同步老的数据到新的表  
                db.execSQL("alter table book rename to _temp_book");  
                db.execSQL("create table book(bookId integer primarykey, bookName text);");  
                db.execSQL("insert into book select *,'' from _temp_book");  
                db.execSQL("drop table _temp_book");  
        default:  
            break;  
    }  
}  
```  
  
注意，这里OnUpgrade() 方法中的 switch 语句是没有 break 的，会一直执行到语句结束。为什么要这么写想想就明白了。比如用户手上的版本是 1，新版 App 的版本是 5，那么就会有 4 个版本的数据库升级，switch() 自然不能中途 break，必须执行这 4 个版本的数据库升级语句。  
  
同时注意每次的case中添加的创建新表的sql代码不要忘了在onCreate中同时添加，因为新用户也是要执行的。  
  
这样的好处是每次更新数据库的时候只需要在onUpgrade方法的末尾加一段从上个版本升级到新版本的代码，易于理解和维护。但是如果版本跨级比较多，这个地方可能比较费时了，建议在可控的版本差之内强制app版本升级。  
  
## 案例  
[参考](https://blog.csdn.net/xx326664162/article/details/50311717)  
  
### 第一版需求  
有个图书管理相关的 App，有一张图书表 —— Book，其表结构如下  
  
| 字段名 | 类型 | 说明 |  
| --- | :-- | :-- |  
| id | INTEGER | 主键 |  
| name | TEXT | 书名 |  
| author | TEXT | 作者 |  
| price | INTEGER | 价格 |  
| pages | INTEGER | 页数 |  
  
```java  
public class BookStoreDbHelper extends SQLiteOpenHelper {  
  
    private static final String DB_NAME = "BookStore.db";  
    private static final int VERSION = 1;  
  
    private static final String CREATE_TBL_BOOK = "CREATE TABLE Book ("  
        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  
        + "name TEXT, "  
        + "author TEXT, "  
        + "prices INTEGER, "  
        + "pages INTEGER"  
        + ")";  
  
    public BookStoreDbHelper(Context context) {  
        this(context, null, null, 0);  
    }  
  
    private BookStoreDbHelper(Context context, String name, CursorFactory factory, int version) {  
        super(context, DB_NAME, null, VERSION);  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL(CREATE_TBL_BOOK); //创建表  
    }  
  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    }  
  
}  
```  
  
### 第二版需求  
  
几个星期之后，第二版的需求来了，需要增加一个书籍分类表 —— Category，其表结构如下：  
  
| 字段名 | 类型 | 说明 |  
| --- | :-- | :-- |  
| id | INTEGER | 主键 |  
| category_name | TEXT | 分类名称 |  
| category_code | INTEGER | 分类代码 |  
  
这个时候，就需要对数据库进行升级，增加一张表。这时候一个问题就来了：  
  
如何在不影响旧版本 App 稳定性的情况下，对数据库进行升级？  
  
先来看看代码实现，等下再细说为什么这么做。  
  
```java  
private static final int VERSION = 2;  // 版本号加 1  
  
// Categroy 建表语句  
private static final String CREATE_TBL_CATEGORY = "CREATE TABLE Category ("  
    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  
    + "category_name TEXT, "  
    + "category_code INTEGER"  
    + ")";  
  
@Override  
public void onCreate(SQLiteDatabase db) {  
    db.execSQL(CREATE_TBL_CATEGORY)  
    db.execSQL(CREATE_TBL_BOOK);  // 建立新表  
}  
  
@Override  
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    switch (oldVersion) {  
        case 1:  
            db.execSQL(CREATE_TBL_CATEGORY)  // 建立新表  
        default:  
            break;  
    }  
}  
```  
  
现在来说说为什么这么写。   
  
注意到一点，这是第二版的 App，那么就会有两种用户：  
- 之前没有安装过这款 App，安装时是全新安装。只会执行 DbHelper 的 onCreate() 方法，直接建立两张表。  
- 之前已经安装过这个 App，安装时时覆盖安装。onCreate() 方法不会再执行了，只会执行 OnUpgrade() 方法。这时候就要判断老版本号是多少，然后在老版本的数据库上进行升级。  
  
### 第三版需求  
  
第三版的 App 又提出来一个新需求：要给 Book 表和 Category 表建立关联，要在 Book 表中添加一个 category_id 字段，代码如下  
  
```java  
private static final int VERSION = 3;  // 版本号加 1，现在是 3  
  
// Book 表  
private static final String CREATE_TBL_BOOK = "CREATE TABLE Book ("  
        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  
        + "name TEXT, "  
        + "author TEXT, "  
        + "prices INTEGER, "  
        + "pages INTEGER, "  
        + "category_id INTEGER"  // 增加字段  
        + ")";  
  
@Override  
public void onCreate(SQLiteDatabase db) {  
    db.execSQL(CREATE_TBL_CATEGORY)  
    db.execSQL(CREATE_TBL_BOOK);  // 建立新表  
}  
  
@Override  
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    switch (oldVersion) {  
        case 1:  
            db.execSQL(CREATE_TBL_CATEGORY)  // 建立新表  
        case 2:  
            db.execSQL("ALTER TABLE Book ADD COLUMN category_id INTEGER");  // 增加字段  
        default:  
            break;  
    }  
}  
```  
  
注意：OnUpgrade() 方法中的 switch 语句是没有 break 的，会一直执行到语句结束。  
  
- 优点：每次更新数据库的时候只需要在onUpgrade方法的末尾加一段从上个版本升级到新版本的代码，易于理解和`维护`  
- 缺点：当版本变多之后，多次迭代升级可能需要花费不少`时间`，增加用户等待时间  
  
**另一种方式：使用if判断，针对当前newVersion版本号，对每个版本的数据库进行升级操作**  
  
- 优点：跨版本升级`效率`高，可以保证每个版本的用户都可以在消耗最少的`时间`升级到最新的数据库而无需做无用的数据多次转存  
- 缺点：代码维护量将越来越大，强迫开发者记忆所有版本数据库的完整结构，且每次升级时onUpgrade方法都必须全部重写  
- 结论：这种跳跃升级不建议使用  
  
2019-2-27  
