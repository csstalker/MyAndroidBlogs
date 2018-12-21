| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
ORM数据库框架 SQLite ORMLite  
***  
目录  
===  

- [简介](#简介)
- [注解](#注解)
	- [@DatabaseTable](#@databasetable)
	- [@DatabaseField](#@databasefield)
	- [@ForeignCollectionField](#@foreigncollectionfield)
- [常用API](#常用api)
	- [添加数据](#添加数据)
	- [删除数据](#删除数据)
	- [更新数据](#更新数据)
	- [查询数据](#查询数据)
	- [Builder](#builder)
	- [事物管理](#事物管理)
- [完整案例](#完整案例)
	- [SqliteOpenHelper](#sqliteopenhelper)
	- [User](#user)
	- [测试代码](#测试代码)
  
# 简介  
[Demo地址](https://github.com/baiqiantao/AndroidOrmTest.git)    
[GitHub](https://github.com/j256/ormlite-android)     
[下载地址](http://ormlite.com/releases/)      
[文档](http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Use-With-Android)     
[示例代码](https://github.com/j256/ormlite-examples)  
  
Android中现在最常见、最常用的SQLite数据库框架是`ORMLite和GreenDAO`，相比GreenDAO来说，ORMLite是一个更加`轻量级`的框架，而且学习成本更低。  
  
Gradle配置：  
```groovy  
implementation 'com.j256.ormlite:ormlite-core:4.48' //核心包，不知道什么原因，新版本5.0和5.1用不了  
implementation 'com.j256.ormlite:ormlite-android:4.48' //Android端专用  
```  
  
也可以手动下载并添加 `ormlite-core` 包，根据需要添加 `ormlite-jdbc` 包(服务器端使用)或 `ormlite-android` 包(Android端使用)。  
  
# 注解  
## @DatabaseTable  
标记要存储在数据库中的`类`的注解。仅在您要标记类[mark the class]或更改其默认`tableName`时才需要使用它。您可以在要保留到数据库的类上方指定此注解。  
需要持久化的类必须具有至少`包级可见`[package visibility]的`无参数构造函数`，以便在执行查询时可以创建此类的对象。  
  
此注解的定义：  
```java  
@Target(TYPE)  
@Retention(RUNTIME)  
public @interface DatabaseTable {  
    String tableName() default ""; //数据库中表的名称。如果未设置，则名称为类名的小写形似。  
    Class<?> daoClass() default Void.class; //与此类对应的DAO类。DaoManager 在内部构造DAO时使用它。  
}  
```  
  
## @DatabaseField  
用于标识一个类中与数据库中的`列`对应、且将被持久化的`字段`。  
瞬态[transient]字段或其他临时[temporary]字段不应被持久化。  
  
此注解的定义：  
```java  
@Target(FIELD)  
@Retention(RUNTIME)  
public @interface DatabaseField {  
    public static final String DEFAULT_STRING = "__ormlite__ no default value string was specified";  
    public static final int NO_MAX_FOREIGN_AUTO_REFRESH_LEVEL_SPECIFIED = -1;  
    public static final int DEFAULT_MAX_FOREIGN_AUTO_REFRESH_LEVEL = 2;  
  
    String columnName() default ""; //数据库中列的名称。如果未设置，则从字段名称中获取名称  
    DataType dataType() default DataType.UNKNOWN; //  
    String defaultValue() default DEFAULT_STRING; //用于创建表的字段的默认值  
    int width() default 0; //  
    boolean canBeNull() default true; //是否可以为空  
    boolean id() default false; //当前字段是不是id字段，一个实体类中只能设置一个id字段  
    boolean generatedId() default false; //是否自增长，id、generatedId、generatedIdSequence 只能指定一个  
    String generatedIdSequence() default ""; //  
    boolean foreign() default false; //当前字段是否是外键  
    boolean useGetSet() default false; //是否使用Getter/Setter方法来访问这个字段  
    String unknownEnumName() default ""; // //  
    boolean throwIfNull() default false; //  
    boolean persisted() default true; //  
    String format() default ""; //  
    boolean unique() default false; //是否唯一  
    boolean uniqueCombo() default false; //  
    boolean index() default false; //将此设置为true以使数据库为此字段添加索引。这将创建一个名为 columnName + "_ idx" 的索引  
    boolean uniqueIndex() default false; //  
    String indexName() default ""; //给此值设置为一个字符串可使数据库使用此名称为此字段添加索引  
    String uniqueIndexName() default ""; //  
    boolean foreignAutoRefresh() default false; //如果设置为true，在关联查询的时候就不需要再调用refresh()了  
    int maxForeignAutoRefreshLevel() default NO_MAX_FOREIGN_AUTO_REFRESH_LEVEL_SPECIFIED; //  
    Class<? extends DataPersister> persisterClass() default VoidType.class; //  
    boolean allowGeneratedIdInsert() default false; //  
    String columnDefinition() default ""; //  
    boolean foreignAutoCreate() default false; //  
    boolean version() default false; //  
    String foreignColumnName() default ""; //外键约束指向的类中的属性名  
    boolean readOnly() default false; //  
}  
```  
  
> 以上那么多字段基本都没用过，也基本用不到  
  
## @ForeignCollectionField  
一对多关联，表示一个UserBean关联着多个ArticleBean（必须使用ForeignCollection集合）  
  
此注解的定义：  
```java  
@Target(FIELD)  
@Retention(RUNTIME)  
public @interface ForeignCollectionField {  
    public static final int MAX_EAGER_LEVEL = 1;  
      
    boolean eager() default false;  
    @Deprecated  
    int maxEagerForeignCollectionLevel() default MAX_EAGER_LEVEL;  
    int maxEagerLevel() default MAX_EAGER_LEVEL;  
    String columnName() default "";  
    String orderColumnName() default "";  
    boolean orderAscending() default true;  
    @Deprecated  
    String foreignColumnName() default "";  
    String foreignFieldName() default "";  
}  
```  
  
# 常用API  
## 添加数据  
```java  
int create(T t) throws SQLException;      
int create(Collection<T> var1) throws SQLException;  
T createIfNotExists(T t) throws SQLException;  
```  
  
## 删除数据  
```java  
int delete(T t) throws SQLException;      
int deleteById(Intenger id) throws SQLException;      
int delete(Collection<T> var1) throws SQLException;      
int deleteIds(Collection<Intenger>  ids) throws SQLException;    
```  
  
## 更新数据  
```java  
int update(T var1) throws SQLException;  
int updateId(T var1, ID var2) throws SQLException;  
```  
  
## 查询数据  
```java  
T queryForId(ID var1) throws SQLException;      
List<T> queryForAll() throws SQLException;  
```  
  
## Builder  
DAO 中提供了几个相关的 Builder 方法，可以进行按条件操作，使用方式相似：  
```java  
QueryBuilder<T, ID>  queryBuilder();    
UpdateBuilder<T, ID> updateBuilder();      
DeleteBuilder<T, ID> deleteBuilder();  
```  
  
QueryBuilder对象常用方法  
```java  
QueryBuilder<User, Integer> builder = userDao.queryBuilder()  
        .distinct()// 排重  
        .groupBy("id")  //分组  
        .limit(10L)//限制数量  
        .offset(3L) //偏移  
        .orderBy("date", true); //排序  
List<User> list = builder.where().like("string", "%包青天%").query();//条件查询  
```  
  
## 事物管理  
```java  
DatabaseConnection dc = userDao.startThreadConnection();//也可以 new AndroidDatabaseConnection  
dc.setAutoCommit(false); //设置不自动提交  
Savepoint savePoint = dc.setSavePoint("savePointName");//设置回滚点，参数是一个名字，没什么影响  
  
for (User user : new User[]{user, user}) {  
    userDao.createOrUpdate(user);  
}  
  
dc.commit(savePoint);//提交事务。保存大量数据时以事务的方式提交，可以大幅提高速度  
userDao.endThreadConnection(dc);  
```  
  
# 完整案例  
## SqliteOpenHelper  
```java  
public class DbHelper extends OrmLiteSqliteOpenHelper {  
    private static final String TABLE_NAME = "sqlite-test.db";  
    private static final int DATABASE_VERSION = 1;  
  
    private Dao<User, Integer> userDao = null;  
    private RuntimeExceptionDao<User, Integer> userRuntimeDao;  
  
    public DbHelper(Context context) {  
        super(context, TABLE_NAME, null, DATABASE_VERSION);  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {  
        try {  
            Log.i("bqt", "onCreate，版本：" + DATABASE_VERSION);  
            TableUtils.createTable(connectionSource, User.class);  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
  
    @Override  
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {  
        try {  
            Log.i("bqt", "onUpgrade，旧版本：" + oldVersion + "，新版本：" + newVersion);  
            TableUtils.dropTable(connectionSource, User.class, true);//删除旧版本  
            onCreate(database, connectionSource);//创建新版本  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
  
    @Override  
    public void close() {  
        super.close();  
        userDao = null;  
    }  
  
    public Dao<User, Integer> getUserDao() throws SQLException {  
        return userDao == null ? getDao(User.class) : userDao;  
    }  
  
    public RuntimeExceptionDao<User, Integer> getUserRuntimeDao() {  
        return userRuntimeDao == null ? getRuntimeExceptionDao(User.class) : userRuntimeDao;  
    }  
}  
```  
  
## User  
```java  
public class User {  
    @DatabaseField(generatedId = true) public int id;  
    @DatabaseField(index = true) public String string;  
    @DatabaseField public Date date;  
  
    public User() { //必须具有至少包机可见的无参数构造函数，以便在执行查询时可以创建此类的对象  
    }  
    //...  
}  
```  
  
## 测试代码  
```java  
public class ORMLiteActivity extends ListActivity {  
    private DbHelper helper;  
    private Dao<User, Integer> userDao;  
    private RuntimeExceptionDao<User, Integer> userRuntimeDao;  
      
    private User user;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"0、添加：create",  
                "1、添加：createIfNotExists",  
                "2、添加：createOrUpdate",  
                "3、事务操作：使用DatabaseConnection",  
                "4、事务操作：使用AndroidDatabaseConnection",  
                "5、更新：update、updateId",  
                "6、删除：delete、deleteIds、deleteById",  
                "7、QueryBuilder、UpdateBuilder、DeleteBuilder",  
                "8、其他API",  
                "9、查询并清除所有数据：queryForAll",};  
          
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
          
        user = User.newBuilder().date(new Date()).date(new Date()).string("====包青天====").build();  
        try {  
            helper = new DbHelper(this);  
            userDao = helper.getUserDao();  
            userRuntimeDao = helper.getUserRuntimeDao();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        helper.close();  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        user.string = "【包青天" + id + "】" + System.currentTimeMillis();  
          
        try {  
            switch (position) {  
                case 0:  
                    int updatNum1 = userDao.create(user);  
                    boolean isEqual1 = userDao.objectsEqual(user, userDao.queryForId(1));  
                    int updatNum2 = userDao.create(user);//[注意]对于同一个对象，不管是否存在，每次就会添加一条新的数据  
                    boolean isEqual2 = userDao.objectsEqual(user, userDao.queryForId(1));  
                    boolean isEqual3 = userDao.objectsEqual(user, userDao.queryForId(2));  
                    Log.i("bqt", "是否为同一对象：" + Arrays.asList(isEqual1, isEqual2, isEqual3));//[true, false, true]  
                      
                    int updatNum3 = userDao.create(User.newBuilder().id(100).string("包青天0").build());//[注意]设置id无效  
                    Log.i("bqt", "更新数量：" + Arrays.asList(updatNum1, updatNum2, updatNum3));//[1, 1, 1]  
                    break;  
                case 1://如果指定的对象不存在则插入，如果存在则不插入  
                    User newUser1 = userDao.createIfNotExists(user);//[垃圾]上面添加了多个，但这里只返回了最后一个  
                    //[注意]因为user已经存在，所以不会添加，也不更新数据库中对象的数据；并且返回的是数据库中的对象(旧的数据)  
                    boolean isEqual11 = userDao.objectsEqual(newUser1, user);  
                    boolean isEqual12 = userDao.objectsEqual(newUser1, userDao.queryForId(2));  
                    Log.i("bqt", "是否为同一对象：" + Arrays.asList(isEqual11, isEqual12));//[false, true]  
                      
                    User newUser2 = userDao.createIfNotExists(User.newBuilder().string("包青天1").build());  
                    Log.i("bqt", new Gson().toJson(Arrays.asList(newUser1, newUser2)));  
                    break;  
                case 2:  
                    Dao.CreateOrUpdateStatus status1 = userDao.createOrUpdate(user);//[注意]上面添加了多个，但这里只更改了最后一个  
                    Log.i("bqt", "是否为同一对象：" + userDao.objectsEqual(user, userDao.queryForId(2)));//true  
                      
                    Dao.CreateOrUpdateStatus status2 = userDao.createOrUpdate(User.newBuilder().string("包青天2").build());  
                    //[{"created":false,"numLinesChanged":1,"updated":true},{"created":true,"numLinesChanged":1,"updated":false}]  
                    Log.i("bqt", new Gson().toJson(Arrays.asList(status1, status2)));  
                    break;  
                case 3://使用DatabaseConnection  
                    DatabaseConnection dc = userDao.startThreadConnection();//也可以 new AndroidDatabaseConnection  
                    dc.setAutoCommit(false); //设置不自动提交  
                      
                    Savepoint savePoint = dc.setSavePoint("savePointName");//设置回滚点，参数是一个名字，没什么影响  
                    for (User user : new User[]{user, user}) {  
                        userDao.createOrUpdate(user);  
                    }  
                    dc.commit(savePoint);//提交事务。保存大量数据时以事务的方式提交，可以大幅提高速度  
                    userDao.endThreadConnection(dc);  
                    break;  
                case 4://使用AndroidDatabaseConnection  
                    AndroidDatabaseConnection adc = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);  
                    userRuntimeDao.setAutoCommit(adc, false);// 设置不自动提交  
                    Savepoint savePoint4 = adc.setSavePoint("savePointName");//设置回滚点，参数是一个名字，没什么影响  
                    for (User user : new User[]{user, User.newBuilder().string("包青天4").build()}) {  
                        userDao.createOrUpdate(user);  
                    }  
                    adc.commit(savePoint4);  
                    adc.rollback(savePoint4); // 回滚事物。一般用于在发生异常时进行回滚  
                    break;  
                case 5:  
                    int uNum1 = userDao.update(user);//[注意]同样更新的是最后添加的数据  
                    int uNum2 = userDao.update(User.newBuilder().build());  
                    int uNum3 = userDao.updateId(user, 100);//[注意]新添加的数据的id将从最大值开始  
                    int uNum4 = userDao.updateId(userDao.queryForId(3), -100);//[注意]值可以为负值  
                    Log.i("bqt", new Gson().toJson(Arrays.asList(uNum1, uNum2, uNum3, uNum4)));//[1,0,1,1]  
                    break;  
                case 6:  
                    int dNum1 = userDao.delete(user);//[注意]这个删掉的也是最后一个添加的数据，前面添加的数据已经和user脱离关联了  
                    int dNum2 = userDao.delete(user);  
                    int dNum3 = userDao.delete(Arrays.asList(user, User.newBuilder().build()));  
                    int dNum4 = userDao.deleteById(3);  
                    int dNum5 = userDao.deleteIds(Arrays.asList(5, 6, 7, 8));  
                    Log.i("bqt", new Gson().toJson(Arrays.asList(dNum1, dNum2, dNum3, dNum4, dNum5)));//[1,0,0,1,1]  
                    break;  
                case 7:  
                    QueryBuilder<User, Integer> builder = userDao.queryBuilder()  
                            .distinct()// 排重  
                            .groupBy("id")  //分组  
                            .limit(10L)//限制数量  
                            .offset(3L) //偏移  
                            .orderBy("date", true); //排序  
                    List<User> list = builder.where().like("string", "%包青天%").query();//条件查询  
                    Log.i("bqt", "查询到的数据：" + new Gson().toJson(list));  
                    break;  
                case 8:  
                    User data = userDao.queryForId(1);//如果不存在则返回 null  
                    long count = userDao.countOf();//总数量  
                    Log.i("bqt", count + " " + new Gson().toJson(data));  
                    Log.i("bqt", userDao.isTableExists() + " " + userDao.isUpdatable() + " " + userDao.idExists(1));  
                case 9:  
                    userDao.delete(userDao.queryForAll());  
                    break;  
            }  
            Log.i("bqt", "全部数据：" + new Gson().toJson(userDao.queryForAll()));  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
}  
```  
  
2018-8-16  
