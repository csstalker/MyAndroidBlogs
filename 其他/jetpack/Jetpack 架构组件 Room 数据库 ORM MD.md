| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
***  
目录  
===  

- [简介](#简介)
- [Entity 相关注解](#Entity-相关注解)
- [DAO：增删改查](#DAO：增删改查)
	- [Insert](#Insert)
	- [Delete](#Delete)
	- [Update](#Update)
	- [Query](#Query)
- [其他](#其他)
	- [类型转换](#类型转换)
	- [模糊匹配](#模糊匹配)
	- [输出模式](#输出模式)
	- [数据库升级](#数据库升级)
		- [正常升级](#正常升级)
		- [跳跃升级](#跳跃升级)
- [使用案例](#使用案例)
	- [添加依赖](#添加依赖)
	- [定义Database](#定义Database)
	- [定义实体](#定义实体)
	- [定义Dao](#定义Dao)
	- [代码中使用](#代码中使用)
  
# 简介  
[官方文档](https://developer.android.google.cn/topic/libraries/architecture/room)  
[查看最新版本](https://developer.android.google.cn/jetpack/androidx/releases/room)  
[参考文章](https://blog.csdn.net/qq_24442769/article/details/79412408)  
  
Room是什么？  
  
Room是一个持久性数据库。  
  
The Room persistence library provides an abstraction layer over SQLite to allow for more robust database access while harnessing the full power of SQLite.  
> Room持久性数据库提供了SQLite的抽象层，以便在充分利用SQLite的同时允许流畅的数据库访问。  
  
**Room 的优点**  
在Android领域，目前已有不少优秀的开源的数据库给大家使用，如SQLite、XUtils、greenDao、Realm，那为什么我们还要去学习使用这个库呢？因为Room有下面几个优点：  
- SQL查询在`编译时`就会验证 - 在编译时检查每个@Query和@Entity等，并且它不仅检查语法问题，还会检查是否有该表，这就意味着没有任何运行时错误的风险可能会导致应用程序崩溃  
- 与`LiveData`、`rxjava`集成，特别是与`LiveData`等架构组件配合使用后，能产生让人惊叹的效果  
- 较少的模板代码  
  
**Room 的组成**  
Room中有三个主要组件组成：  
- Database: 用这个组件创建一个`数据库`。注解定义了一系列entities，并且类中提供一系列Dao的抽象方法，也是下层主要连接的访问点。注解的类应该是一个继承 RoomDatabase 的抽象类。在运行时，你能通过调用`Room.databaseBuilder()`或者 `Room.inMemoryDatabaseBuilder()`获得一个实例  
- Entity： 用这个组件`创建表`，Database类中的entities数组通过引用这些entity类创建数据库表。每个entity中的字段都会被持久化到数据库中，除非用`@Ignore`注解  
- DAO： 这个组件代表了一个用来操作表增删改查的dao。Dao 是Room中的主要组件，负责定义访问数据库的方法。被注解`@Database`的类必须包含一个没有参数的且返回注解为`@Dao`的类的抽象方法。在编译时，Room创建一个这个类的实现。  
  
# Entity 相关注解  
当一个类被注解为`@Entity`并且引用到带有`@Database`注解的`entities`属性，Room为这个数据库引用的entity创建一个数据表。   
  
Entity类能够有一个空的构造函数（如果dao类能够访问每个持久化的字段）或者一个参数带有匹配entity中的字段的类型和名称的构造函数  
  
Entity的字段必须为public或提供setter或者getter方法。  
  
**Ignore：忽略**  
默认情况下，Room为每个定义在entity中的字段创建一个列。如果一个entity的一些字段不想持久化，可以使用`@Ignore`注解它们  
```java  
@Ignore Bitmap picture;  
```  
  
**PrimaryKey：主键**  
  
每个entity必须定义至少一个字段作为主键，即使这里只有一个字段，仍然需要使用`@PrimaryKey`注解这个字段。并且，如果想Room动态给entity分配自增主键，可以设置@PrimaryKey的`autoGenerate`属性为true。如果entity有个组合的主键，你可以使用`@Entity`注解的primaryKeys属性：  
```java  
@PrimaryKey(autoGenerate = true) public int id // 自增主键  
```  
  
组合主键：  
```java  
@Entity(primaryKeys = {"firstName", "lastName"})  
class User {}  
```  
  
**tableName：数据库的表名**  
默认情况下，Room使用类名作为数据库的表名。如果希望表有一个不同的名称，设置@Entity注解的tableName属性，如下所示：  
```java  
@Entity(tableName = "users")  
class User {}  
```  
  
> 注意： SQLite中的表名是大小写敏感的。  
  
**列名称：ColumnInfo**  
Room使用字段名称作为列名称。如果你希望一个列有不同的名称，为字段增加`@ColumnInfo`注解  
```java  
@ColumnInfo(name = "first_name") public String firstName;  
```  
  
**indices：索引**  
数据库索引可以加速数据库查询，@Entity的`indices`属性可以用于添加索引。在索引或者组合索引中列出你希望包含的列的名称：  
```java  
@Entity(indices = {@Index("name"), @Index({"first_name", "last_name"}),})  
public class User {}  
```  
  
有时，表中的某个字段或字段组合需要确保唯一性，可以设置@Entity的@Index注解的unique属性为true。  
```java  
@Entity(indices = {@Index(value = {"first_name", "last_name"}, unique = true)})  
public class User {}  
```  
  
**foreignKeys：外键约束**  
例如：如果有一个entity叫book，你可以通过使用@ForeignKey注解定义它和user的关系：  
```java  
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))  
class Book {}  
```  
  
> 外键是十分强大的，因为它们允许你指明当引用的entity被更新后做什么。  
> 例如，如果相应的user实例被删除了，你可以通过包含@ForeignKey注解的`onDelete=CASCADE`属性让SQLite为这个user删除所有的书籍。   
  
> SQLite处理@Insert(OnConflict=REPLACE)作为一个REMOVE和REPLACE操作而不是单独的UPDATE操作。这个替换冲突值的方法能够影响你的外键约束。  
  
**Embedded：嵌入式字段**  
有时，希望entity中包含一个`具有多个字段的对象`作为字段。在这种情况下，可以使用`@Embedded`注解去代表一个希望分解成一个表中的次级字段的对象。接着你就可以像其他单独的字段那样查询嵌入字段：  
```java  
@Embedded public Address address;  
```  
```java  
class Address {  
    @ColumnInfo(name = "post_code") public int postCode; //嵌入式字段还可以包含其他嵌入式字段  
}  
```  
  
如果一个实体具有相同类型的多个内嵌字段，则可以通过设置前缀属性(prefix)使每个列保持惟一。然后将所提供的值添加到嵌入对象中每个列名的开头  
```java  
@Embedded(prefix = "foo_") Coordinates coordinates;  
```  
  
**对象引用**  
SQLite是个关系型数据库，能够指明两个对象的关系。大多数ORM库支持entity对象引用其他的。**Room明确的禁止这样。**更多细节请参考[Understand why Room doesn’t allow object references](https://developer.android.google.cn/training/data-storage/room/referencing-data.html#understand-no-object-references)  
  
# DAO：增删改查  
Room的三大组件之一Dao，以一种干净的方式去访问数据库。  
  
Room **不允许在主线程中访问数据库**。除非在建造器中调用`allowMainThreadQueries()`，这可能会造成长时间的锁住UI。  
  
> 注意，异步查询API（返回LiveData或者RxJava流的查询）从这个规则中豁免，因为它们异步的在后台线程中进行查询。  
  
## Insert  
在Dao中创建一个方法并且使用`@Insert`注解它，Room会为其生成一个实现，此实现会在单独事务中插入所有参数到数据库中：   
```java  
@Insert  
long insertUser(User user); //参数至少有一个，有一个时可以返回long(代表新插入item的rowId)或不返回  
  
@Insert  
long[] insertAll(User... users); //参数也可以是集合或者数组，此时可以返回long[]或者List<Long>或不返回  
  
@Insert(onConflict = OnConflictStrategy.REPLACE)  
void insertAll(List<User> users);//可以执行事务操作  
```  
```java  
long id = db.userDao().insertUser(new User("哎"));  
List<Long> ids = db.userDao().insertAll(new User("哎哎" + new Random().nextInt(100)));  
db.userDao().insertAll(Arrays.asList(new User("哎啊"), new User("啊哎")));  
```  
  
> @Insert方法必须至少有一个参数，如果参数只有一个，它可以返回一个新插入item的rowId的long值(或不返回)  
> 如果参数是一个集合或数组，它可以返回`long[]`或者`List<Long>`(或不返回)  
  
@Insert，@Update都可以执行事务操作，定义在`OnConflictStrategy`注解类中：  
```java  
@Retention(SOURCE)  
public @interface OnConflictStrategy {  
    int REPLACE = 1; //替换旧数据  
    int ROLLBACK = 2; //回滚事务  
    int ABORT = 3; //就退出事务  
    int FAIL = 4; //使事务失败   
    int IGNORE = 5; //忽略冲突  
}  
```  
  
## Delete   
@Delete是一个从数据库中删除一系列给定参数的entities的惯例方法。它使用`主键`找到要删除的entities：  
```java  
@Delete  
int delete(User user);//根据主键删除entities，可以没有返回值或者返回int，返回int值时表示删除的数量  
  
@Delete  
int deleteAll(List<User> users); //参数至少有一个，也可以是集合或者数组，参数必须是使用@Entity注解标注的类，且不能为null  
```  
```java  
User user = db.userDao().findUserByName("啊哎"); //如果没找到的话返回null  
if (user != null) Log.i("bqt", "【delete数量】" + db.userDao().delete(user)); //不能传入空，否则崩溃  
Log.i("bqt", "【deleteAll数量】" + db.userDao().deleteAll(db.userDao().getAll()));  
```  
  
> 尽管通常不是必须的，你能够拥有这个方法返回int值指示数据库中删除的数量。  
> 没有提供根据主键等方式删除(包括更新)数据的方式，参数必须是使用@Entity注解标注的类，不能是字符串！  
  
## Update  
`@Update` 是更新一系列entities集合、给定参数的惯例方法。它使用query来匹配每个entity的主键：  
```java  
@Update  
int updateAll(User... users);//规则和delete一样  
```  
  
> 尽管通常不是必须的，你能够拥有这个方法返回int值指示数据库中更新的数量。  
  
## Query  
  
@Query 是DAO类中使用的主要注解，每个@Query方法在`编译时`(而不是运行时)被校验，`如果查询包含语法错误，或者如果用户表不存在`，Room会报出合适的错误消息：  
- 如果仅有一些字段匹配会警告  
- 如果没有字段匹配会报错  
  
```java  
@Query("SELECT * FROM table_user")  
List<User> getAll();//返回值为集合或数组的话，返回找到的所有数据，没找到时返回长度为0的集合或数组  
```  
  
**查询时传入参数**  
如果你需要传入参数到查询语句中去过滤操作，你可以使用`方法参数`：  
```java  
@Query("SELECT * FROM user WHERE age > :minAge") User[] loadAllUsersOlderThan(int minAge);  
@Query("SELECT * FROM user WHERE uid IN (:userIds)") List<User> loadAllByIds(int[] userIds);  
```  
  
当这个查询在编译期被处理，Room会匹配`:minAge`绑定的方法参数。Room通过使用`参数名称`执行匹配，如果没有匹配到，在你的app编译期将会报错。  
  
查询时可以传入多个参数或者多次引用同一个参数。  
```java  
@Query("SELECT * FROM user WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")  
User findByName(String first, String last);  
```  
  
**返回列中的子集**  
多数时候，我们仅需要获取一个entity中的部分字段。例如，你的UI可能只展示user第一个和最后一个名称，而不是所有关于用户的细节。通过获取展示在UI的有效数据列可以使查询完成的`更快`。   
  
只要查询中列结果集能够被映射到返回的对象中，Room`允许你返回任何java对象`。例如，通过创建如下POJO拿取用户的姓和名：  
```java  
@Query("SELECT first_name, last_name FROM user") List<NameTuple> loadFullName();  
```  
```java  
public class NameTuple {  
    @ColumnInfo(name="first_name") public String firstName;  
    @ColumnInfo(name="last_name") public String lastName;  
}  
```  
  
> Room理解查询返回first_name和last_name的列值被映射到NameTuple类中。因此，Room能够生成合适的代码。如果查询返回太多columns，或者一个列不存在，Room将会报警。  
  
**返回LiveData对象**  
使用返回值类型为LiveData实现数据库更新时ui数据自动更新：  
```java  
db.userDao().loadUsersByName2("%哎%") //可以在主线程调用  
    .observe(this, users -> tvTitle.setText("【最新查询结果】" + new Gson().toJson(users)));  
```  
  
**返回RxJava对象**   
Room也能返回RxJava2的`Publisher和Flowable对象`，需添加`android.arch.persistence.room:rxjava2`依赖：  
```java  
@Query("SELECT * from user where uid = :uid") Flowable<User> findUserById(int uid);  
```  
  
**直接游标访问**  
如果你的应用逻辑直接访问返回的行，你可以从你的查询当中返回一个Cursor对象：  
```java  
@Query("SELECT * FROM user WHERE uid > :uid LIMIT 5") Cursor biggerId(int uid);//不推荐  
```  
  
> 注意：非常不建议使用Cursor API 因为它不能保证行是否存在或者行包含什么值。  
> 使用这个功能仅仅是因为你已经有一个返回cursor的代码，并且你不能轻易的重构。  
  
**多表查询**  
在SQLite数据库中，我们可以指定对象之间的关系，因此我们可以将一个或多个对象与一个或多个其他对象绑定。这就是所谓的一对多和多对多的关系。  
  
一些查询可能访问多个表去查询结果，Room允许你写任何查询，所以你也能连接表。  
  
如下代码段展示如何执行一个根据`借书人姓名`模糊查询`借的书`的相关信息。  
```java  
@Query("SELECT * FROM book "  
       + "INNER JOIN loan ON loan.book_id = book.id "  
       + "INNER JOIN user ON user.id = loan.user_id "  
       + "WHERE user.name LIKE :userName")  
List<Book> findBooksBorrowedByNameSync(String userName);  
```  
  
从这些查询当中也能返回POJOs，例如，可以写一个POJO去装载userName和petName，如下：  
```java  
@Query("SELECT user.name AS userName, pet.name AS petName FROM user, pet WHERE user.id = pet.user_id")  
LiveData<List<UserPet>> loadUserAndPetNames();  
```  
```java  
class UserPet {  
   public String userName;  
   public String petName;  
}  
```  
  
# 其他  
  
## 类型转换  
Room为`原始类型`和可选的`装箱类型`提供嵌入支持。然而，有时你可能使用一个单独存入数据库的`自定义数据类型`。为了添加这种类型的支持，你可以提供一个`TypeConverter`把自定义类转化为一个Room能够持久化的已知类型。  
  
例如：如果我们想持久化Date的实例，我们可以写如下TypeConverter在数据库中去存储相等的Unix时间戳：  
```java  
public Date birthday;  
```  
```java  
public class Converters {  
    @TypeConverter  
    public static Date fromTimestamp(Long value) {  
        return value == null ? null : new Date(value);  
    }  
  
    @TypeConverter  
    public static Long dateToTimestamp(Date date) {  
        return date == null ? null : date.getTime();  
    }  
}  
```  
  
接着，你增加`@TypeConverters`注解到`RoomDatabase`子类：   
```java  
@Database(entities = {User.java}, version = 1)  
@TypeConverters({Converter.class})  
public abstract class AppDatabase extends RoomDatabase {  
    public abstract UserDao userDao();  
}  
```  
  
使用这些转换器后，你就可以像使用的原始类型一样使用自定义类型了：   
  
```java  
@Query("SELECT * FROM user WHERE birthday BETWEEN :from AND :to")  
List<User> findUsersBornBetweenDates(Date from, Date to);  
```  
  
> 注意：除了将 @TypeConverters 放在RoomDatabase里面影响全局外，还可以将 @TypeConverter 限制在不同的范围内，包含单独的entity、Dao和Dao中的 methods，具体查看官方文档。  
  
## 模糊匹配  
[参考](https://www.jianshu.com/p/61e7a92d3ba1)  
  
通常，我们使用SQL语句模糊查询时是这样的：  
```java  
select * from user where name like '%包_天%'; //%代表任意个字符，_代表一个字符  
```  
  
但是在Dao中不能简单地这么写，任你各种拼接啊、转义啊，都不能把那个`%`给它弄进去。  
  
这里不得不说一个Room的一个好处，就是你在写sql语句的时候它会检查，格式不对，里面会报红线，根本就编译不通过。  
  
皇天不负有心人，最终在Stack Overflow上面搜了一下，找到了最终答案，应该是这样的  
```java  
@Query("SELECT * from user where last_name LIKE '%' || :name || '%'")  
LiveData<List<User>> loadUsersByName(String name);  
```  
  
原来是用双竖杠去拼接，而不是加号，欲哭无泪啊。然后我就去搜了一下sql语句拼接，找到这样一段话：  
> 在SQL中的SELECT语句中，可使用一个特殊的操作符来拼接两个列。根据你所使用的DBMS，此操作符可用加号（+）或两个竖杠（||）表示。  
> 在MySQL和MariaDB中，必须使用特殊的函数。  
> Access和SQL Server使用 + 号。  
> SQLite、DB2、Oracle、PostgreSQL、Open Office Base使用 ||。  
  
其实除了我上面用双竖杠拼接的方式外，还有一种方法，就是在传参的时候，把`%_%`给拼接好，这种方式我也是测试了一下，也是可以的，因为最终都是转换成sql的标准查询语句。  
```java  
db.userDao().loadUsersByName("%包_天%")  
```  
  
而且，我感觉这种方式写起来更优雅，应该也是Google变相推荐的方式。  
  
**总结**  
- 方式一，在传给Dao方法之前自行拼接：如`db.userDao().loadUsersByName("%包_天%")`  
- 方式二：在Dao方法中的`方法参数`前后使用`||`拼接，如：`@Query("SELECT * from user where name LIKE '_%' || :name || '%'")`  
  
## 输出模式  
在编译时，将数据库的模式信息导出到JSON文件中，这样可有利于我们更好的调试和排错（DataBase的exportSchema = true）  
  
module中的build.gradle：  
```java  
android {  
    defaultConfig {  
        javaCompileOptions {  
            annotationProcessorOptions {  
                arguments = ["room.schemaLocation": "$projectDir/schemas".toString()]//room的数据库概要、记录  
            }  
        }  
    }  
}  
```  
  
配置完毕之后，我们在构建项目之后会在对应路径生成schemas文件夹。  
文件夹内的`1.json`等文件分别是数据库版本1、2、3等版本对应的概要、记录。  
  
## 数据库升级  
  
如果咱们删除了entity中的一个字段，运行程序后，就会出现下面这个问题。  
> java.lang.IllegalStateException: Room cannot verify the data integrity. Looks like you've changed schema but forgot to update the version number. You can simply fix this by increasing the version number.  
  
大致的意思是：你修改了数据库，但是没有升级数据库的版本  
  
这时候咱们根据错误提示增加版本号，但没有提供migration，APP一样会crash。  
> java.lang.IllegalStateException: A migration from 1 to 2 was required but not found. Please provide the necessary Migration path via `RoomDatabase.Builder.addMigration(Migration ...)` or allow for destructive migrations via one of the `RoomDatabase.Builder.fallbackToDestructiveMigration*` methods.  
  
大致的意思是：让我们添加一个addMigration或者调用fallbackToDestructiveMigration完成迁移  
  
接下来，咱们增加版本号并使用`fallbackToDestructiveMigration()`，虽然可以使用了，但是我们会发现，数据库的内容都被我们清空了，显然这种方式是不友好的。如果不想清空数据库，就需要提供一个实现了的migration。  
  
> Room允许使用`Migration`类保留用户数据。每个Migration类在运行时指明一个开始版本和一个结束版本，Room执行每个Migration类的`migrate()`方法，使用正确的顺序去迁移数据库到一个最近版本。  
> 如果不提供必需的Migrations类，Room会重建数据库，这意味着你将丢失数据库中的所有数据。  
  
### 正常升级  
  
比如咱们要在Department中添加phoneNum  
```java  
public class Department {  
    @PrimaryKey(autoGenerate = true) int id;  
    String dept;  
    @ColumnInfo(name = "emp_id") int empId;  
    @ColumnInfo(name = "phone_num") String phoneNum;  
}  
```  
  
1、把版本号自增  
```java  
@Database(entities = {Department.class, Company.class}, version = 2, exportSchema = false)  
@TypeConverters(DateConverter.class)  
public abstract class DepartmentDatabase extends RoomDatabase {}  
```  
  
2、添加一个version：1->2的migration  
```java  
static final Migration MIGRATION_1_2 = new Migration(1, 2) {  
    @Override  
    public void migrate(SupportSQLiteDatabase database) {  
        database.execSQL("ALTER TABLE department ADD COLUMN phone_num TEXT");  
    }  
};  
```  
  
3、把migration 添加到 databaseBuilder  
```java  
Room.databaseBuilder(context, DepartmentDatabase.class, DB_NAME)  
    .addMigrations(MIGRATION_1_2)  
    .build();  
```  
  
再次运行APP就会发现，数据库表更新了，并且旧数据也保留了。  
  
### 跳跃升级  
在平时的开发时，数据库的升级并不总是按部就班的从 version: 1->2，2->3，3->4。总是会出现 version：1->3，或 2->4 的情况。这时候我们又该怎么办呢？  
  
方法很简单。当用户升级 APP 时，我们替用户升级数据库版本。  
  
具体做法：  
version：1->2  
```java  
database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, PRIMARY KEY(`id`))"); //创建表  
```  
  
version：2->3  
```java  
database.execSQL("ALTER TABLE Book ADD COLUMN pub_year INTEGER"); //添加字段  
```  
  
version：3->4  
```java  
//字段类型修改  
db.execSQL("CREATE TABLE db_new (_id TEXT, _name TEXT, _phone INTEGER, PRIMARY KEY(_id))");  //创建表  
db.execSQL("INSERT INTO db_new (_id, _name, _phone) SELECT _id, _name, _phone FROM db_old"); //复制表  
db.execSQL("DROP TABLE db_old");  //删除表  
db.execSQL("ALTER TABLE db_new RENAME TO students");  //修改表名称  
```  
  
然后把 migration 添加到 Room database builder:  
```java  
Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name")  
    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)  
    .build();  
```  
  
这样，要是用户刚下载的 APP，目前我们定义了migrations：version 1 到 2, version 2 到 3, version 3 到 4, 所以 Room 会一个接一个的触发所有 migration。  
  
其实 Room 可以处理大于 1 的版本增量：我们可以一次性定义一个`从 1 到 4` 的 migration，以提升迁移的速度。  
```java  
.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_1_4)  
```  
  
# 使用案例  
  
## 添加依赖  
```groovy  
implementation "android.arch.persistence.room:runtime:1.1.1"  //Room  
annotationProcessor "android.arch.persistence.room:compiler:1.1.1"  
testImplementation "android.arch.persistence.room:testing:1.1.1" //Test helpers for Room  
implementation "android.arch.persistence.room:rxjava2:1.1.1" //为room添加rxjava支持库  
implementation "android.arch.lifecycle:reactivestreams:1.1.1" //和LiveData一起使用  
```  
  
## 定义Database  
```java  
@Database(entities = {User.class}, version = 1)  
@TypeConverters({MyConverters.class})  
public abstract class AppDatabase extends RoomDatabase {  
    public abstract UserDao userDao(); //没有参数的抽象方法，返回值所代表的类必须用@Dao注解  
}  
```  
  
Build后会自动生成的AppDatabase的实现类：  
![](index_files/d5113551-765b-4b0d-b769-78dd8604dde8.png)  
  
## 定义实体  
```java  
@Entity(tableName = "table_user") //自定义表名称  
public class User {  
    @PrimaryKey(autoGenerate = true) public int uid;//自增主键  
    @ColumnInfo(name = "user_name") public String name; //自定义列名称  
    public Date birthday; //类型转换  
    @Ignore public String tips = "忽略的字段"; //忽略  
    //省略了get、set、构造方法等代码  
}  
```  
  
## 定义Dao  
```java  
@Dao  
public interface UserDao {  
      
    @Insert  
    long insertUser(User user); //参数至少有一个，有一个时可以返回long(代表新插入item的rowId)或不返回  
      
    @Insert  
    List<Long> insertAll(User... users); //参数也可以是集合或者数组，此时可以返回long[]或者List<Long>或不返回  
      
    @Insert(onConflict = OnConflictStrategy.REPLACE)  
    void insertAll(List<User> users);//可以执行事务操作  
      
    @Delete  
    int delete(User user);//根据主键删除entities，可以没有返回值或者返回int，返回int值时表示删除的数量  
      
    @Delete  
    int deleteAll(List<User> users); //参数至少有一个，也可以是集合或者数组，参数必须是使用@Entity注解标注的类，且不能为null  
      
    @Query("SELECT * FROM table_user")  
    List<User> getAll();//返回值为集合或数组的话，返回找到的所有数据，没找到时返回长度为0的集合或数组  
      
    @Query("SELECT * FROM table_user WHERE user_name LIKE :name LIMIT 1")  
    User findUserByName(String name); //返回值为User的话，只返回找到的第一条数据，没找到时返回null  
      
    @Query("SELECT * FROM table_user WHERE uid IN (:userIds)")  
    User[] loadAllByIds(int[] userIds); //没找到时返回长度为0的集合或数组  
      
    @Query("SELECT * FROM table_user WHERE uid > :uid LIMIT 5")  
    Cursor biggerId(int uid);//不推荐  
      
    @Query("SELECT * from table_user where uid = :uid")  
    Flowable<User> findUserById(int uid); //和rxjava结合使用  
      
    @Query("SELECT * from table_user where user_name LIKE :name ")  
    LiveData<List<User>> loadUsersByName(String name); //和LiveData结合使用  
      
    @Query("SELECT * from table_user where user_name LIKE '_%' || :name || '%'")  
    LiveData<List<User>> loadUsers(String name);//模糊搜索  
}  
```  
  
Build后自动生成的UserDao的实现类：  
![](index_files/4703c11f-a707-4e58-8085-b001fd41befb.png)  
  
## 代码中使用  
  
```java  
public class MainActivity extends FragmentActivity {  
    private TextView tvTitle, tvWeixin, tvFriend, tvContact, tvSetting;  
    private String[] titles = {"微信", "通讯录", "发现", "我"};  
    private MyViewModel mModel;  
    private AppDatabase db;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        initView();  
        mModel = ViewModelProviders.of(this).get(MyViewModel.class);  
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "dbname").build();  
        db.userDao().loadUsersByName("%哎_%") //返回LiveData<List<User>>，实际数据库操作工作在子线程  
            .observe(this, users -> {  
                Log.i("bqt", "【是否在主线程中】" + (Looper.getMainLooper() == Looper.myLooper())); //true  
                String newUsers = GsonUtils.toJson(users);  
                Log.i("bqt", "【数据库中查询到的数据发生变化】" + newUsers);  
                mModel.getMutableLiveData().setValue(newUsers); //这里是主线程，可以直接使用setValue  
            });  
    }  
      
    private void initView() {  
        tvWeixin = findViewById(R.id.tv_tab_bottom_weixin);  
        tvFriend = findViewById(R.id.tv_tab_bottom_friend);  
        tvContact = findViewById(R.id.tv_tab_bottom_contact);  
        tvSetting = findViewById(R.id.tv_tab_bottom_setting);  
          
        tvWeixin.setTag(0);  
        tvFriend.setTag(1);  
        tvContact.setTag(2);  
        tvSetting.setTag(3);  
          
        tvWeixin.setOnClickListener(v -> onClickTab((Integer) v.getTag()));  
        tvFriend.setOnClickListener(v -> onClickTab((Integer) v.getTag()));  
        tvContact.setOnClickListener(v -> onClickTab((Integer) v.getTag()));  
        tvSetting.setOnClickListener(v -> onClickTab((Integer) v.getTag()));  
          
        tvTitle = findViewById(R.id.tv_title);  
        tvTitle.setOnClickListener(v -> {  
            new Thread(() -> {  
                List<User> allUsers = db.userDao().getAll();//必须在子线程访问数据库  
                mModel.getMutableLiveData().postValue("全部数据\n" + GsonUtils.toJson(allUsers)); //在子线程必须用postValue  
            }).start();  
            tvTitle.setBackgroundColor(0xFF000000 + new Random().nextInt(0xFFFFFF));  
        });  
        getSupportFragmentManager().beginTransaction().add(R.id.id_container, MyFragment.newInstance("MyFragment")).commit();  
    }  
      
    private void onClickTab(int position) {  
        tvWeixin.setSelected(position == 0);  
        tvFriend.setSelected(position == 1);  
        tvContact.setSelected(position == 2);  
        tvSetting.setSelected(position == 3);  
        tvTitle.setText(titles[position]);  
          
        switch (position) {  
            case 0:  
                new Thread(() -> {  
                    long id = db.userDao().insertUser(new User("哎"));  
                    List<Long> ids = db.userDao().insertAll(new User("哎哎" + new Random().nextInt(100)));  
                    db.userDao().insertAll(Arrays.asList(new User("哎啊"), new User("啊哎")));  
                    Log.i("bqt", "【id】" + id + "【ids】" + ids);  
                }).start(); //不能在主线程访问数据库，否则报IllegalStateException  
                break;  
            case 1:  
                new Thread(() -> {  
                    User user = db.userDao().findUserByName("啊哎"); //如果没找到的话返回null  
                    if (user != null) Log.i("bqt", "【delete数量】" + db.userDao().delete(user)); //不能传入空，否则崩溃  
                    User[] users = db.userDao().loadAllByIds(new int[]{1, 2, 3, 4, 5, 6}); //没找到时返回长度为0的集合或数组  
                    Log.i("bqt", "【deleteAll数量】" + db.userDao().deleteAll(Arrays.asList(users)));  
                }).start();  
                break;  
            case 2:  
                new Thread(() -> {  
                    User user = db.userDao().findUserByName("哎%"); //如果没找到的话返回null  
                    user.name = "更新name";  
                    Log.i("bqt", "【updateAll数量】" + db.userDao().updateAll(user));  
                }).start();  
                break;  
            case 3:  
                db.userDao().findUserById(1) //返回Flowable<User>  
                    .map(GsonUtils::toJson) //数组类型转换  
                    .subscribeOn(Schedulers.io()) //必须切换到子线程中访问数据库  
                    //.observeOn(AndroidSchedulers.mainThread()) //可以在子线程中使用postValue，也可以切换到主线程中使用setValue  
                    .subscribe(s -> mModel.getMutableLiveData().postValue(s));  
            default:  
                break;  
        }  
    }  
}  
```  
  
2019-3-29  
