| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
ORM数据库框架 LitePal SQLite  
***  
目录  
===  

- [简介](#简介)
	- [特性](#特性)
	- [使用过程](#使用过程)
	- [配置](#配置)
- [API](#api)
	- [创建表](#创建表)
	- [更新表](#更新表)
	- [保存数据](#保存数据)
	- [更新数据](#更新数据)
	- [删除数据](#删除数据)
	- [查找数据](#查找数据)
	- [异步操作](#异步操作)
	- [多库操作(动态创建数据库)](#多库操作动态创建数据库)
	- [监听数据库的创建和更新](#监听数据库的创建和更新)
	- [混淆](#混淆)
- [测试demo](#测试demo)
	- [实体类](#实体类)
	- [测试类](#测试类)
  
# 简介  
[Demo地址](https://github.com/baiqiantao/AndroidOrmTest.git)   
[GitHub](https://github.com/LitePalFramework/LitePal)  
[litepal-1.6.1.jar](https://github.com/LitePalFramework/LitePal/raw/master/downloads/litepal-1.6.1.jar)  
[litepal-1.6.1-src.jar](https://github.com/LitePalFramework/LitePal/raw/master/downloads/litepal-1.6.1-src.jar)  
  
> An Android library that makes developers use SQLite database extremely easy.  
  
LitePal是一个开源的Android库，允许开发人员非常容易地使用SQLite数据库。 您可以完成大部分数据库操作，而无需编写SQL语句[statement]，包括创建或升级表，crud操作，聚合函数[aggregate functions]等。LitePal的设置也非常简单，您可以在不到5分钟内将其集成到项目中。  
  
## 特性  
- 使用对象关系映射[object-relational mapping,ORM]模式。  
- 很“轻”，jar包只有100k不到，使用起来比较简单  
- 几乎为零配置，只有一个属性很少的配置文件。  
- 自动维护[Maintains]所有表。例如，创建、更改或删除表。  
- 支持多数据库。  
- 封装的API，用于避免编写SQL语句。  
- 极好用的流式查询API。  
- 作为替代选择，你仍可使用SQL，但使用起来比原始API更易用。  
  
> LitePal不管是创建数据库、创建表还是执行增删改查，都是`根据Model的类名和属性名`，每次都需要进行`反射拼装`，然后`调用Android原生的数据库操作`，实现相应的功能。  
  
## 使用过程  
- 首先引入lib，可以通过gradle引入也可以将下载的`litepal.jar`包直接放入libs目录下。  
- 然后在`assets`目录下新建一个 `litepal.xml` 文件，文件名称不能更改。  
- 然后在 AndroidManifest 中配置一下 `LitePalApplication`，如果已使用自定义的Application，只需在你的Application的onCreate方法中调用 `LitePal.initialize(this)` 即可。  
- 接下来创建继承自`DataSupport`的Mode类，之后这些实体类就拥有了进行`CRUD`操作的能力，LitePal框架会自动创建好相应的数据表，以及表数据类型和非空约束等。  
  
## 配置  
**添加依赖**  
```  
implementation 'org.litepal.android:java:3.0.0' //2018-10月最新版本  
//implementation 'org.litepal.android:kotlin:3.0.0' //kotlin  
```  
  
**配置 litepal.xml**  
在项目的 `assets` 目录中创建一个 `litepal.xml`文件，并按以下方式配置。  
几个标签的作用：  
- `dbname`：配置`数据库名称`  
- `version`：配置`数据库版本`，**每次要升级数据库时，此处的值加1**  
- `list`：配置`映射类`  
- `storag`：配置`数据库文件的存储位置`，只能选择【internal】或【external】  
  
示例：  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<litepal>  
    <!--定义应用程序的数据库名称。默认情况下，每个数据库名称应以.db结尾。如果您没有使用.db命名数据库端，LitePal会自动为您添加后缀。-->  
    <dbname value="demo" />  
  
    <!--定义数据库的版本。每次要升级数据库时，version标记都会有所帮助。修改您在映射标记中定义的模型，只需将版本值加1，数据库升级将自动处理，你无需关心。-->  
    <version value="1" />  
  
    <!--定义模型，LitePal将为每个映射类创建表。模型中定义的受支持字段将映射到列中。-->  
    <list>  
        <mapping class="com.test.model.Reader" />  
        <mapping class="com.test.model.Magazine" />  
    </list>  
  
    <!--定义.db文件的位置。【internal】表示.db文件将存储在内部存储的数据库文件夹中，无人可以访问(默认值)。【external】表示.db文件将存储在主外部存储设备所在目录的路径中，应用程序可以放置它拥有的每个人都可以访问的持久文件。-->  
    <storage value="external" />  
</litepal>  
```  
  
**配置 LitePalApplication**  
如果没有自定义Application：`android:name="org.litepal.LitePalApplication"`  
如果已自定义Application：`LitePal.initialize(this);`  
  
> 始终记得使用 application 作为参数。不要将任何 activity 或 service 等的实例用作参数，以避免内存泄漏。  
  
# API  
## 创建表  
首先定义模型，例如：  
```java  
public class Album extends LitePalSupport {  
    @Column(unique = true, defaultValue = "unknown")  
    private String name;  
    private float price;  
    private byte[] cover;  
    private List<Song> songs = new ArrayList<Song>();  
    // generated getters and setters.  
    ...  
}  
```  
  
```java  
public class Song extends LitePalSupport {  
    @Column(nullable = false)  
    private String name;  
    private int duration;  
    @Column(ignore = true)  
    private String uselessField;  
    private Album album;  
    // generated getters and setters.  
    ...  
}  
```  
  
> 注意，3.0.0之后是继承自 LitePalSupport，之前是继承自 DataSupport  
  
然后将这些 models 添加到`litepal.xml`的 list 标签中：  
```xml  
<list>  
    <mapping class="org.litepal.litepalsample.model.Album" />  
    <mapping class="org.litepal.litepalsample.model.Song" />  
</list>  
```  
  
下次`操作数据库时`将生成上面添加的数据库表。  
  
> LitePal不管是创建数据库、创建表还是执行增删改查，都是`根据Model的类名和属性名`，每次都需要进行`反射拼装`，然后`调用Android原生的数据库操作`，实现相应的功能。  
  
## 更新表  
LitePal中的升级表非常简单，只需修改你想要的模型：  
```java  
public class Album extends DataSupport {  
    ...  
    @Column(ignore = true)  
    private float price;//忽略掉此字段  
    private Date releaseDate;//添加此字段  
}  
```  
  
添加了 `releaseDate` 字段，并给 `price` 字段添加了 `ignore` 注解用以忽略。  
然后增加 litepal.xml 中的版本号：`<version value="2" />`  
  
这些表将在您下次运行数据库时升级，升级后 releaseDate 列将添加到 album 表中，原始的 price 列将被删除。  
升级后，除了那些删除的列之外，album 表中的所有数据都将被保留。  
  
但是有些升级条件下LitePal无法兼容的处理数据，这些情况下，升级后表中的所有数据都将被清除：  
- 添加了一个带有 `unique = true` 注解的字段(因为旧的数据的这个字段的值不满足 unique 条件)  
- 为一个已存在的字段添加了 `unique = true` 注解(同样是因为旧的数据的这个字段的值不满足 unique 条件)  
- 为一个已存在的字段添加了 `nullable = false` 注解(因为旧的数据的这个字段的值不满足非 nullable 条件)  
  
> 一定要注意，进行上述操作会导致数据丢失！  
  
## 保存数据  
所有继承自LitePalSupport的model都有`save()`方法用于保存数据到数据库：  
```java  
Album album = new Album();  
album.setName("album");  
album.setPrice(10.99f);  
album.setCover(getCoverImageBytes());  
album.save();  
```  
  
## 更新数据  
最简单的方法是使用 `save` 方法更新 `find` 找到的记录：  
```java  
Album albumToUpdate = LitePal.find(Album.class, 1); //3.0.0之前是通过 DataSupport 查找的  
albumToUpdate.setPrice(20.99f); //更改 price  
albumToUpdate.save();  
```  
  
从LitePalSupport 继承的每个model也都具有 `update` 和 `updateAll` 方法。 您可以通过指定 ID 更新单个记录：  
```java  
Album albumToUpdate = new Album();  
albumToUpdate.setPrice(20.99f);  
albumToUpdate.update(id);  
```  
  
你也可以通过使用 `where` 语句一次更新多条数据:  
```java  
Album albumToUpdate = new Album();  
albumToUpdate.setPrice(20.99f);  
albumToUpdate.updateAll("name = ?", "album");  
```  
  
## 删除数据  
```java  
LitePal.delete(Song.class, id); //删除一条数据  
DataSupport.deleteAll(Song.class, "duration > ?" , "350"); //删除多条数据  
```  
  
## 查找数据  
```java  
Song song = LitePal.find(Song.class, id); //通过id查找单条数据  
List<Song> allSongs = LitePal.findAll(Song.class); //查找全部数据  
List<Song> songs = LitePal.where("name like ? and duration < ?", "song%", "200") //条件查询  
    .order("duration")  
    .find(Song.class);  
  
```  
  
## 异步操作  
默认情况下，每个数据库操作都在主线程上，如果您的操作可能花费很长时间，例如保存或查询大量记录，您可能希望使用异步操作。LitePal支持所有crud方法的异步操作。  
  
例如在后台线程中异步查找song表中的所有记录：  
```java  
LitePal.findAllAsync(Song.class).listen(new FindMultiCallback<Song>() {  
    @Override  
    public void onFinish(List<Song> t) {  
        //异步返回查询结果  
    }  
});  
```  
  
在后台线程中异步保存数据:  
```java  
album.saveAsync().listen(new SaveCallback() {  
    @Override  
    public void onFinish(boolean success) {  
        //异步返回保存结果  
    }  
});  
```  
  
## 多库操作(动态创建数据库)  
如果您的应用需要多个数据库，LitePal会完全支持它。您可以在运行时创建任意数量的数据库。例如：  
```java  
LitePalDB litePalDB = new LitePalDB("demo2", 1);  
litePalDB.addClassName(Singer.class.getName());  
litePalDB.addClassName(Album.class.getName());  
litePalDB.addClassName(Song.class.getName());  
LitePal.use(litePalDB);  
```  
这将创建一个包含 singer, album 和 song 表的名为 demo2 的数据库。  
  
创建一个配置与 litepal.xml 相同的新的数据库：`LitePal.use(LitePalDB.fromDefault("newdb"));`  
切换回默认数据库：`LitePal.useDefault();`  
通过数据库名称删除数据库`LitePal.deleteDatabase("newdb");`  
  
## 监听数据库的创建和更新  
```java  
LitePal.registerDatabaseListener(new DatabaseListener() {  
    @Override  
    public void onCreate() {  
        // fill some initial data  
    }  
  
    @Override  
    public void onUpgrade(int oldVersion, int newVersion) {  
        // upgrade data in db  
    }  
});  
```  
  
## 混淆  
如果您要在项目中启用ProGuard，您需要添加以下内容：  
```java  
-keep class org.litepal.** {  
    *;  
}  
  
-keep class * extends org.litepal.crud.DataSupport {  
    *;  
}  
  
-keep class * extends org.litepal.crud.LitePalSupport {  
    *;  
}  
```  
  
# 测试demo  
## 实体类   
```java  
public class Album extends LitePalSupport {  
    @Column(unique = true, defaultValue = "包青天")  
    private String name;  
      
    private int age;//新增一个字段时需要更改数据库版本，否则操作数据时会失败  
    private float price;  
      
    @Column(ignore = true)//忽略掉的字段，此字段的值不会保存到数据库中  
    private int size;  
      
    //必须有一个默认的、public的无参构造器，否则执行 update 等语句时会崩溃  
    public Album() {  
    }  
    //省略了 set/get 方法，以及我添加的 Build 相关的代码  
}  
```  
  
## 测试类  
```java  
public class LitePalActivity extends ListActivity {  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"findAll()",  
            "save() ",  
            "find() 和 save()",  
            "update()",  
            "updateAll()",  
            "delete() 和 deleteAll()",  
            "fluent query",  
            "Async operations: saveAsync() + listen()",  
            "Async operations: findAllAsync() + listen()",  
        };  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        int random = new Random().nextInt(5);  
        switch (position) {  
            case 0:  //findAll()  
                showTips(new Gson().toJson(LitePal.findAll(Album.class)));    //[{"age":1,"name":"包青天1","price":1.0,"size":0,"baseObjId":1}]  
                break;  
            case 1:  //save()  
                Album saveAlbum = Album.newBuilder().name("包青天" + random).age(random).price(random).size(random).build();  
                showTips(saveAlbum.save() + "  " + new Gson().toJson(saveAlbum));  
                break;  
            case 2:  //find() 和 save() 和 findAll  
                Album findAlbum = LitePal.find(Album.class, random);  
                if (findAlbum != null) {  
                    findAlbum.setPrice(9.99f); //仅更改指定的值  
                    showTips(random + "  " + findAlbum.save());  
                } else {  
                    showTips("没有找到id=" + random + "的数据");  
                }  
                break;  
            case 3: //update()  
                Album updateAlbum = Album.newBuilder().name("包青天0").age(random + 100).build();  
                //注意：如果指定为 unique 字段的值已存在，则会报 DataSupportException: UNIQUE constraint failed: album.name  
                try {  
                    int rowsAffected = updateAlbum.update(random); //未指定的字段的值不会更改，比如这里不会更改 price的值  
                    showTips(random + "  更新" + (rowsAffected == 0 ? "失败(未找到指定 id 的数据)" : rowsAffected + "条"));  
                } catch (Exception e) {  
                    e.printStackTrace();  
                    showTips(random + "  更新失败(主键冲突 UNIQUE constraint failed )");  
                }  
                break;  
            case 4: //updateAll()  
                Album whereAlbum = Album.newBuilder().age(random + 200).price(random + 200).size(random + 200).build();  
                int rowsAffected = whereAlbum.updateAll("name like ? and age > ?", "包青天%", Integer.toString(3));  
                showTips(random + "  更新" + (rowsAffected == 0 ? "失败(未找到指定 id 的数据)" : rowsAffected + "条"));  
                break;  
            case 5://delete() 和 deleteAll()  
                int rowsAffected1 = LitePal.delete(Album.class, random);  
                int rowsAffected2 = LitePal.deleteAll(Album.class, "age < ?", Integer.toString(200));  
                showTips(random + "  " + rowsAffected1 + "  " + rowsAffected2);  
                break;  
            case 6: //fluent query  
                List<Album> albumList = LitePal.where("name like ? and price <= ?", "包青天%", Float.toString(3.0f))  
                    .order("name")  
                    .find(Album.class);  
                showTips(new Gson().toJson(albumList));  
                break;  
            case 7: //Async operations: saveAsync() + listen()  
                Album saveAsyncAlbum = Album.newBuilder().name("包青天" + random).age(random).price(random).size(random).build();  
                saveAsyncAlbum.saveAsync()  
                    .listen(success -> showTips(random + "," + success + "," + (Looper.myLooper() == Looper.getMainLooper())));//主线程  
                break;  
            case 8: //Async operations: findAllAsync() + listen()  
                LitePal.findAllAsync(Album.class)  
                    .listen(list -> showTips(new Gson().toJson(list)));  
                break;  
        }  
    }  
      
    private void showTips(String s) {  
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();  
        Log.i("bqt", s);  
    }  
}  
```  
  
2018-5-28  
