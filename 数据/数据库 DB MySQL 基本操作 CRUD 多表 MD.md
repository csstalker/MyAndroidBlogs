| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
数据库 DB MySQL 基本操作 CRUD 多表 MD  
***  
目录  
===  

- [操作数据库](#操作数据库)
- [操作表](#操作表)
- [操作表记录](#操作表记录)
	- [INSERT](#insert)
	- [UPDATE](#update)
	- [DELETE](#delete)
	- [SELECT](#select)
- [其他知识点](#其他知识点)
- [多表设计与多表查询](#多表设计与多表查询)
	- [外键约束](#外键约束)
	- [多表设计](#多表设计)
	- [多表查询](#多表查询)
	- [多表查询实例](#多表查询实例)
- [补充](#补充)
	- [Java 获取所有表](#java-获取所有表)
	- [查看 Android 数据库表结构方法](#查看-android-数据库表结构方法)
	- [命令行显示中文乱码解决方案](#命令行显示中文乱码解决方案)
	- [sqlite3:not found 解决方法](#sqlite3not-found-解决方法)
  
# 操作数据库  
创建数据库：`create`  
- 创建一个`名称`为mydb1的数据库 `create database mydb1;`  
- 创建一个使用gbk`字符集`的mydb2数据库 `create database mydb2 character set gbk;`  
- 创建一个使用utf8字符集，并带`校对规则`的mydb3数据库  `create database mydb3 character set utf-8 collate utf8_bin;`  
  
查看数据库：`show`  
- 查看当前数据库服务器中的`所有数据库`  `show databases;`  
- 查看前面创建的mydb2数据库的`定义信息`  `show create database mydb3;`  
  
修改数据库：`alter`  
- 把mydb2数据库的字符集修改为utf8  `alter database mydb2 character set utf8;`  
  
删除数据库：`drop`  
- 删除前面创建的mydb1数据库 `drop database mydb1;`  
  
选择数据库：`use`  
- 进入数据库，切换数据库  `use mydb1;`  
- 查看当前所选的数据库  `select database();`  
  
# 操作表  
**MySQL常用数据类型**  
- 字符串型：`VARCHAR`、`CHAR`  
- 大数据类型：`BLOB`、`TEXT`  
- 整数数值型：`TINYINT` 、`SMALLINT`、`INT`、`BIGINT`  
- 浮点数值型：`FLOAT`、`DOUBLE`  
- 逻辑型：`BIT`  
- 日期型：`DATE`、`TIME`、`DATETIME`、`TIMESTAMP`（时间戳）  
  
**定义单表字段的约束**  
- 定义主键约束：`primary key` 不允许为空，不允许重复  
- 删除主键：`alter table tablename drop primary key`   
- 主键自动增长：`auto_increment`   
- 定义唯一约束：`unique`   
- 定义非空约束：`not null`    
- 定义外键约束：`foreign key`  
  
**创建表**  
> 注意：创建表前，要先使用 `use db_name` 语句使用库。  
  
案例：创建一个员工表 employee  
```sql  
create table employee(  
   id int primary key auto_increment,  
   name varchar(20) unique,  
   gender bit not null,  
   birthday date,  
   entry_date date,  
   job varchar(40),  
   salary double,  
   resume text  
);  
```  
  
**查看表**  
- 查看表结构：`desc tabName`  
- 查看当前数据库中所有表：`show tables`  
- 查看当前数据库表建表语句：`show create table tabName`  
  
**修改表**  
- 在上面员工表的基本上增加一个image列：`alter table employee add image blob;`  
- 修改job列，使其长度为60：`alter table employee modify job varchar(60);`  
- 删除gender列：`alter table employee drop gender;`  
- 表名改为user：`rename table employee to user;`  
- 修改表的字符集为gbk：`alter table user character set gbk;`  
- 列名name修改为username：`alter table user change name username varchar(20);`  
  
**删除表**  
删除user表：`drop table user;`  
  
# 操作表记录  
## INSERT  
```sql  
INSERT INTO table [(column [, column...])] VALUES (value [, value...]);  
```  
  
- 插入的数据应与字段的数据类型相同。  
- 数据的大小应在列的规定范围内，例如：不能将一个长度为80的字符串加入到长度为40的列中。  
- 在values中列出的数据位置必须与被加入的列的排列位置相对应。  
- 字符和日期型数据应包含在单引号中。  
- 插入空值：不指定或insert into table value(null)  
- 如果要插入所有字段可以省写列列表，直接按表中字段顺序写值列表  
  
向表中插入四个员工的信息：  
```sql  
insert into employee (id,name,gender,birthday,entry_date,job,salary,resume) values (null,'张飞',1,'1999-09-09','1999-10-01','打手',998.0,'老三');  
```  
```sql  
insert into employee values (null,'关羽',1,'1998-08-08','1998-10-01','财神爷',9999999.00,'老二');  
```  
```sql  
insert into employee values (null,'刘备',0,'1990-01-01','1991-01-01','ceo',100000.0,'老大'),(null,'赵云',1,'2000-01-01','2001-01-01','保镖',1000.0,'七进七出');  
```  
  
## UPDATE  
```sql  
UPDATE  tbl_name SET col_name1=expr1 [, col_name2=expr2 ...] [WHERE where_definition]    
```  
  
- UPDATE语法可以用新值更新原有表行中的各列。  
- SET子句指示要修改哪些列和要给予哪些值。  
- WHERE子句指定应更新哪些行。如没有WHERE子句，则更新所有的行   
  
案例  
- 将所有员工薪水修改为5000元：`update employee set salary = 5000;`  
- 将姓名为’张飞’的员工薪水修改为3000元：`update employee set salary = 3000 where name='张飞';`  
- 将姓名为’关羽’的员工薪水修改为4000元,job改为ccc：`update employee set salary=4000,job='ccc' where name='关羽';`  
- 将刘备的薪水在原有基础上增加1000元：`update employee set salary=salary+1000 where name='刘备';`  
  
## DELETE  
```sql  
delete from tbl_name [WHERE where_definition]    
```  
  
如果不使用where子句，将删除表中所有数据  
Delete语句不能删除某一列的值（可使用update）  
使用delete语句仅删除记录，不删除表本身。如要删除表，使用`drop table`语句  
同insert和update一样，从一个表中删除记录将引起其它表的参照完整性问题，在修改数据库数据时，头脑中应该始终不要忘记这个潜在的问题  
删除表中数据也可使用`TRUNCATE TABLE`语句，它和delete有所不同，参看mysql文档。  
  
案例  
- 删除表中名称为’张飞’的记录：`delete from employee where name='张飞';`  
- 删除表中所有记录：`delete from employee;`  
- 使用truncate删除表中记录：`truncate table employee;`  
  
## SELECT  
**基本查询**  
- 查询表中所有学生的信息：`select * from exam;`  
- 查询表中所有学生的姓名和对应的英语成绩：`select name,english from exam;`  
- 过滤表中重复数据：`select distinct english from exam;`  
- 在所有学生分数上加10分特长分显示：`select name, math+10, english+10, chinese+10 from exam;`  
- 统计每个学生的总分：`select name, english+math+chinese from exam;`  
- 使用别名表示学生总分(as可以省略)：`select name as 姓名 ,english+math+chinese 总成绩 from exam; `  
  
**对查询结果进行过滤 where**  
- 查询姓名为张飞的学生成绩：`select * from exam where name='张飞';`  
- 查询英语成绩大于90分的同学：`select * from exam where english > 90;`  
- 查询总分大于230分的所有同学：`select name 姓名, math+english+chinese 总分 from exam where math+english+chinese>230;`  
- 查询英语分数在 80－100之间的同学：`select * from exam where english between 80 and 100;`  
- 查询数学分数为75,76,77的同学，in(集合)：`select * from exam where math in(75,76,77);`  
- 查询所有姓张的学生成绩，%代表任意个字符，_代表一个字符：`select * from exam where name like '张%';`  
- 查询数学分>70且语文分>80的同学，支持and,or,not逻辑运算符：`select * from exam where math>70 and chinese>80;`  
  
**对查询结果进行排序 order by**  
`asc` 升序(默认)，`desc` 降序；order by 语句应放在结尾  
- 对语文成绩排序后输出：`select name,chinese from exam order by chinese desc;`  
- 对总分排序按从高到低的顺序输出：`select name 姓名,chinese+math+english 总成绩 from exam order by 总成绩 desc;`  
- 对姓张的学生成绩排序输出：`select name 姓名,chinese+math+english 总成绩 from exam where name like '张%' order by 总成绩 desc;`  
  
**聚合函数 Count、SUM、AVG、MAX、MIN**  
Count：用来统计符合条件的行的个数  
- 统计一个班级共有多少学生：`select count(*) from exam;`  
- 统计数学成绩大于90的学生有多少个：`select count(*) from exam where math>70;`  
- 统计总分大于230的人数有多少：`select count(*) from exam where math+english+chinese > 230;`  
  
SUM：用来将符合条件的记录的指定列进行求和操作，注意：sum仅对数值起作用，否则会报错  
- 统计一个班级数学总成绩：`select sum(math) from exam;`  
- 统计一个班级语文、英语、数学各科的总成绩：`select sum(math),sum(english),sum(chinese) from exam;`  
- 注意：在执行计算时，只要有null参与计算，整个计算的结构都是null，此时可以用ifnull函数进行处理  
- 统计一个班级语文、英语、数学的成绩总和:`select sum(ifnull(chinese,0)+ifnull(english,0)+ifnull(math,0)) from exam;`  
- 统计一个班级语文成绩平均分：`select sum(chinese)/count(*) 语文平均分 from exam;`  
  
AVG：用来计算符合条件的记录的指定列的值的平均值  
- 求一个班级数学平均分：`select avg(math) from exam;`  
- 求一个班级总分平均分：`select avg(ifnull(chinese,0)+ifnull(english,0)+ifnull(math,0)) from exam;`  
  
MAX/MIN：用来获取符合条件的所有记录指定列的最大值和最小值  
- 求班级最高分：`select max(ifnull(chinese,0)+ifnull(english,0)+ifnull(math,0)) from exam;`   
  
**分组查询 group by**  
- 对订单表中商品归类后，显示每一类商品的总价：`select product,sum(price) from orders group by product;`  
- 查询购买了几类商品，并且每类总价大于100的商品：`select product 商品名,sum(price)商品总价 from orders group by product having sum(price)>100;`  
- 查询单价小于100而总价大于150的商品的名称：`select product from orders where price<100 group by product having sum(price)>150;`  
  
# 其他知识点  
**where子句和having子句的区别**  
- where子句在`分组之前`进行过滤，having子句在`分组之后`进行过滤  
- having子句中可以使用`聚合函数`，where子句中不能使用聚合函数  
- where子句不能在对数据进行某些`运算`后使用，having子句可以  
- 很多情况下使用where子句的地方可以使用having子句进行替代  
  
**顺序**  
sql语句书写顺序：`select from where groupby having orderby`  
sql语句执行顺序：`from where select group by having order by`    
  
**备份数据库**  
备份前先退出数据库，回到CMD窗口下，命令：`quit`  
在cmd窗口下执行命令：  
```sql  
mysqldump -u root(用户名) -p dbName(数据库名) > c:/1.sql(可不带后缀名，结尾不能带分号)  
```  
例如：  
```sql  
quit  
mysqldump -u root -p dbName > c:/1.sql  
```  
  
**恢复数据库**  
要注意恢复数据只能恢复`数据本身`，原先的数据库(名)没法恢复，所以恢复前需要先自己创建出数据文件  
恢复方式1：在cmd窗口下  
```sql  
mysql -u root -p dbName < c:/1.sql  
```  
例如  
```sql  
mysql -u root -p dbName  
create database newDBName;  
```  
  
恢复方式2：在mysql命令下  
```sql  
source c:/1.sql  
```  
例如  
```sql  
use newDBName;  
source c:/newDBName.sql  
```  
  
# 多表设计与多表查询  
## 外键约束  
表是用来保存现实生活中的数据的，而现实生活中数据和数据之间往往具有一定的关系，我们在使用表来存储数据时，可以明确的声明`表和表之前的依赖关系`，命令数据库来帮我们维护这种关系，像这种约束就叫做`外键约束`。    
  
定义外键约束：  
```sql  
foreign key(本表的列名 ordersid) references orders(id)(引用的表明及列名)  
```  
  
案例：  
```sql  
create table dept(  
   id int primary key auto_increment,  
   name varchar(20)  
);  
create table emp(  
   id int primary key auto_increment,  
   name varchar(20),  
   dept_id int,  
   foreign key(dept_id) references dept(id)  
);  
```  
  
## 多表设计  
- 一对多：在`多的一方`保存`一的一方`的主键做为外键  
- 一对一：在任意一方保存另一方的主键作为外键  
- 多对多：创建`第三方关系表`保存两张表的主键作为外键，保存他们对应关系  
  
![](index_files/0ae65fe3-d0b3-4f05-8d16-19604b5bdc42.jpg)  
  
## 多表查询  
**笛卡尔积查询**  
将两张表的记录进行一个`相乘`的操作查询出来的结果就是笛卡尔积查询  
如果左表有n条记录，右表有m条记录，笛卡尔积查询出有`n*m`条记录，其中往往包含了很多错误的数据，所以这种查询方式并不常用  
```sql  
select * from dept,emp;  
```  
  
**内连接查询**  
查询的是左边表和右边表都能找到对应记录的记录  
```sql  
select * from dept,emp where dept.id = emp.dept_id;  
select * from dept inner join emp on dept.id=emp.dept_id;  
```  
  
**外连接查询**  
左外连接查询：在内连接的基础上增加左边表有而右边表没有的记录  
```sql  
select * from dept left join emp on dept.id=emp.dept_id;  
```  
  
右外连接查询：在内连接的基础上增加右边表有而左边表没有的记录  
```sql  
select * from dept right join emp on dept.id=emp.dept_id;  
```  
  
全外连接查询：在内连接的基础上增加左边表有而右边表没有的记录和右边表有而左表表没有的记录  
```sql  
select * from dept full join emp on dept.id=emp.dept_id;  
```  
  
但是mysql不支持全外连接，不过我们可以使用`union`关键字模拟全外连接  
```sql  
select * from dept left join emp on dept.id = emp.dept_id  
union  
select * from dept right join emp on dept.id = emp.dept_id;  
```  
  
## 多表查询实例  
创建表a  
```sql  
create table a(id int primary key,job varchar(20));  
insert into a values(1,'AA'), (2,'BB'), (3,'CC');  
select * from a;  
```  
![](index_files/7cf0f934-9fb5-47e8-89f7-8cfa8ec28714.png)  
  
创建表b  
```sql  
create table b(id int ,name varchar(20) ,foreign key(id) references a(id));  
insert into b values(1,'bqt'), (2,'bqt2'), (2,'bqt3');  
select * from b;  
```  
![](index_files/326b1c07-d098-4d49-ad50-391e8270aeb6.png)  
  
> PS：  
被参照的a(id)必须定义为unique才可以被参照，否则报错（逻辑错误）。  
插入数据的id必须在被参照的表中已经存在，否则报错（逻辑错误）。  
  
1、笛卡尔积查询  
```sql  
select * from a,b;  
```  
![](index_files/56911521-45b0-49c5-abf2-d9a6d6a1bf6e.png)  
  
2、内连接查询：查询的是左边表和右边表都能找到对应记录的记录  
```sql  
select * from a,b where a.id = b.id;  
或  
select * from a inner join b on a.id=b.id;  
```  
![](index_files/9dd36945-4354-4f82-bd7a-f9ef00e959b5.png)  
  
3、左外连接查询：在内连接的基础上增加左边表有而右边表没有的记录  
```sql  
select * from a left join b on a.id=b.id;  
```  
![](index_files/8c5d86f0-ecb7-4e57-aee7-8466dc89d187.png)  
  
4、右外连接查询:在内连接的基础上增加右边表有而左边表没有的记录  
```sql  
select * from a right join b on a.id=b.id;  
```  
![](index_files/1859dd58-20b1-4027-b05f-eaa38d315546.png)  
  
> PS：由于右边表参照左边表，所以不存在右边表有而左边表没有的记录。  
  
5、全外连接查询：在内连接的基础上增加左边表有而右边表没有，和右边表有而左表表没有的记录  
```sql  
select * from a left  join b on a.id = b.id  
union  
select * from a right join b on a.id = b.id;  
```  
![](index_files/1eec03b7-5d21-4a39-9507-efd849b9d144.png)  
  
# 补充  
## Java 获取所有表  
```java  
public List<String> getChatTables(){  
    initDataBaseHelper();  
  
    ArrayList<String> tables = new ArrayList<>();  
    Cursor cursor = dbWrite.rawQuery("select name from sqlite_master where type='table' and name like 'chat%' ", null);  
    cursor.moveToPrevious();  
  
    while(cursor.moveToNext()){  
       String name = cursor.getString(0); //遍历出表名  
       if(name.startsWith("chat_")){ //这一步的判断是可以省略的  
           tables.add(name);  
       }  
    }  
      cursor.close();  
      return tables;  
}  
```  
  
## 查看 Android 数据库表结构方法  
在android中，为某个应用程序创建的数据库文件位于Android设备的 `/data/data/包名/databases` 文件夹中，并且只有此应用程序自己可以访问，其它应用程序是不能访问的。开发者若要查看数据库中的内容，方式有两种 ：  
- 1、将数据库文件通过`File Explorer`或其他方式导出后使用可视化工具`SQLite Expert`软件打开(工具是收费的)  
- 2、在cmd命令下通过使用系统自带的`sqlite3`命令查看。  
  
下面介绍第二种方式，大致步骤为：  
- 1、可以先通过`adb devices`查看是否有设备连接  
- 2、通过`adb shell`进入手机根目录，若测试的是真机，默认为普通用户权限，此时会有一个符号 `$`(超管为 `#`)，此时没有访问`/data`目录的权限，若手机已获得了root权限(基本上没有手机会主动给这个权限)，可以输入`su`或`su root`申请root权限，然后在手机上点击授权(如有必要)，这样就可以访问`/data`目录了  
- 3、定位到指定目录`cd data/data/包名/databases`，查看目录下的文件`ls -l `，注意`*.db-journal`为临时文件  
- 4、使用`sqlite3`命令打开数据库文件`sqlite3 person.db`，若提示`sh: sqlite3: not found`，解决办法见下文  
- 5、执行数据库语句`select * from person;`，注意最后要有一个分号；也可执行其它指令，如`.table`查看所有表，`.schema`查看所有语句，`.help`查看帮助  
- 6、若显示中文时乱码，详见下文  
  
## 命令行显示中文乱码解决方案  
1、退出后重新开启CMD窗口，输入：`chcp 65001` 回车  
注意 65001 是指使用`UTF-8`的编码，`GB2312` 为 936，可以从 EditPlus 工具的菜单--文档--文档类型 查看  
2、在命令行标题栏上点击右键，选择属性 - 字体，将字体修改为`Lucida Console`(默认为点阵字体)，确定  
3、完成后再通过命令进入sqlite3，select一下含有中文的记录，乱码就解决了。  
  
## sqlite3:not found 解决方法  
sqlite3 为一个可执行脚本程序，在`system/xbin/`下面，某些品牌手机 rom 不带这个东西，导致使用该工具出现 `sqlite3：not found` 错误，下面就详细讲解如何解决此问题。  
  
注意：  
- sqlite3 一般情况下都是通用的，所以随便去找个 sqlite3 就好，比如 [这个](http://download.csdn.net/detail/chenguiyi/8680795)或 [这个](http://pan.baidu.com/share/link?shareid=534077&uk=839950715)  
- 若此文件还是不行，那就使用相应版本的模拟器中的`/system/xbin`目录下的 sqlite3  
- 不能使用 x86 模拟器上的 sqlite3，否则运行会出现 `/system/xbin/sqlite3: not executable: magic 7F45`错误  
- 若也缺少 `libncurses.so` 文件，会报 `libncurses.so not found`错误，处理方式是一样的，建议同时导入这两个文件  
  
完整步骤：  
1、查看是否连接设备：`adb devices`  
  
2、获取root权限  
```  
adb shell  
su  
```  
  
3、让/system文件夹可读写  
```  
mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system  
```  
  
4、导入所需的文件到sdcard  
从模拟器的 `/system/xbin` 中导出 `sqlite3` 文件，从 `/system/lib` 中导出 `libncurses.so` 文件  
将上面导出的文件或从网上下载的文件导入到真机的sdcard中  
为验证是否导入成功，可先`ls`，我们发现里面有一个`sdcard`，我们进入此目录`cd sdcard`，再`ls`，若发现里面有我们导入的这两个文件，证明导入成功  
  
5、将sdcard中的文件复制到手机系统相应目录中  
首先切换到根目录，然后执行以下命令：  
```  
cd /  
cat /sdcard/sqlite3 > /system/xbin/sqlite3  
cat /sdcard/libncurses.so > /system/lib/libncurses.so  
```  
  
6、修改文件权限  
```  
chmod 4755 /system/xbin/sqlite3  
chmod 4755 /system/lib/libncurses.so  
```  
  
7、设置 /system为只读文件  
```  
mount -o remount,ro -t yaffs2 /dev/block/mtdblock3 /system  
```  
  
至此，就可以使用 sqlite3 命令来操作 SQLite 数据库了。  
  
2018-4-16  
