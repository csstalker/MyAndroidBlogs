| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Gson Json 序列化 最常用的功能 MD  
***  
目录  
===  

	- [通过 fromJson 反序列化为对象](#通过-fromJson-反序列化为对象)
	- [更优雅的打印一个对象](#更优雅的打印一个对象)
	- [parse 对特殊字符串的解析](#parse-对特殊字符串的解析)
	- [fromJson 对特殊字符串的处理](#fromJson-对特殊字符串的处理)
	- [为某个字段提供多个属性名](#为某个字段提供多个属性名)
	- [用 JsonElement 去定义未知类型的字段](#用-JsonElement-去定义未知类型的字段)
	- [解析 Json 字符串中的内容](#解析-Json-字符串中的内容)
	- [格式化 Json 字符串](#格式化-Json-字符串)
	- [将序列化后的内容写到 控制台、文件、SB中](#将序列化后的内容写到-控制台、文件、SB中)
	- [使用 JsonWriter 编写 Json 串](#使用-JsonWriter-编写-Json-串)
  
```groovy  
implementation 'com.google.code.gson:gson:2.8.2'  
```  
  
## 通过 fromJson 反序列化为对象  
普通对象  
```java  
Person person = gson.fromJson(jsonString, Person.class);  
Person person = gson.fromJson(jsonString, new TypeToken<Person>() {}.getType());  
```  
数组  
```java  
String[] array = gson.fromJson(jsonString, String[].class);  
String[] array = gson.fromJson(jsonString, new TypeToken<String[]>() {}.getType());  
int[][] array = gson.fromJson(jsonString, int[][].class);  
```  
集合  
```java  
List<Person> list = gson.fromJson(jsonString, new TypeToken<List<Person>>() {}.getType());//支持带泛型  
Map<String, Integer> map = gson.fromJson(jsonString2, new TypeToken<Map<String, Integer>>() {}.getType());  
```  
问题：为何要用 `TypeToken` 去包装要解析的类型？为何不直接用 `List<Person>.class` ？  
> 解疑：因为编译之后，List<Person> 和 List<User> 这写类型的字节码文件只一个，那就是 `List.class`，这是Java泛型使用时要注意的问题：`泛型擦除`。  
  
## 更优雅的打印一个对象  
```java  
class Fu {  
    protected String key = "fu";  
}  
```  
```java  
class A extends Fu {  
    private static String NAME = "A";  
    private boolean isBest = true;  
    private int age = 28;  
}  
```  
```java  
System.out.println(new Gson().toJson(new A())); //{"isBest":true,"age":28,"key":"fu"}  
```  
  
## parse 对特殊字符串的解析  
关于解析后的数据类型：  
```java  
System.out.println(new JsonParser().parse("").getClass().getSimpleName());//【JsonNull】，注意不是字符串  
" " -> 【JsonNull】，注意不是字符串  
"null" -> 【JsonNull】，注意不是字符串  
  
"\"\"" -> 【JsonPrimitive】,是空字符串【】  
"\"null\"" -> 【JsonPrimitive】,是字符串【null】  
"\"[null]\"" -> 【JsonPrimitive】,是字符串【[null]】  
  
.parse("[]").getAsJsonArray().size();//【0】，长度为0的数组。此数组并不为null，所以解析后不必判空  
.parse("[0]").getAsJsonArray().get(0).getClass().getSimpleName();//【JsonPrimitive】，有一个元素的数组  
"[null]" -> 【JsonNull】，有一个元素的数组  
"[{}]" -> 【JsonObject】，有一个元素的数组  
```  
  
注意，所有的`引号、大括号、中括号`都必须完整(这是一个合法的json字符串的基本要求)，不然都会出现解析异常：  
```java  
new JsonParser().parse("{");//End of input at line 1 column 2 path $.  
new JsonParser().parse("{");//End of input at line 1 column 2 path $[0]  
new JsonParser().parse("\"");//Unterminated string at line 1 column 2 path $  
  
new JsonParser().parse("{key}");//Expected ':' at line 1 column 6 path $.key  
new JsonParser().parse("1,2");//Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 3 path $  
```  
  
下面这些是可以的：  
```java  
new JsonParser().parse("{key:value}");//这样是可以的【{"key":"value"}】，所有的引号都可以去掉  
new JsonParser().parse("{key:\"value\"}"));//这样也是可以的【{"key":"value"}】  
new JsonParser().parse("[0,null,\"\",{}]");  
```  
  
## fromJson 对特殊字符串的处理  
通过 Gson.fromJson 方法将一个json字符串解析为对象 AA 时，要求解析结果必须是一个`JsonObject`，且要求必须保证解析后的每个字段的类型和 AA 中定义的一致：  
```java  
new Gson().fromJson("[]", AA.class);//Expected BEGIN_OBJECT but was BEGIN_ARRAY at line 1 column 2 path $  
System.out.println(new Gson().fromJson("1", AA.class));//Expected BEGIN_OBJECT but was NUMBER at line 1 column 2 path $  
```  
  
但是，其可以将解析后为 JsonNull 的结果转换为 null 而不报异常：  
```java  
String jString = null;  
System.out.println(new Gson().fromJson(jString, AA.class));//null，注意是一个空对象，所以不能对此结果做任何操作，包括getClass  
System.out.println(new Gson().fromJson(" ", AA.class));//和上述结果一样为 null  
```  
  
所以，解析时为了防止解析异常，在不确定数据结构的情况下，可以做一些安全性判断：  
```java  
if (!TextUtils.isEmpty(response) && response.trim().startsWith("{")) {  
    //为了防止服务器返回数据格式错误导致解析异常  
}  
```  
  
## 为某个字段提供多个属性名  
或者说将 json 字符串中的某一字段的值赋给要转化的对象中的某一指定字段。比如，服务器可能通过不同的字段返回给我们错误信息，有的用tips、有的用msg、有的用errorinfo……现在，我们不管服务器用哪种方式返回给我们错误信息，我们都用 `message` 字段去接收。  
```java  
@SerializedName(value = "message", alternate = {"tips", "msg","errorinfo","error"})  
public String message;  
```  
  
当上面的几个属性中出现任意一个时均可以得到正确的结果，`如果多种属性同时出，那么是以最后一个出现的值为准`。  
  
## 用 JsonElement 去定义未知类型的字段  
可以用Gson框架中的基类 【JsonElement】去定义json数据中不确定具体类型的某些字段。  
  
如下面的 data ，对于不同的网络请求，其内容是完全不一样的，可能是一个JsonObject，也可能是一个JsonArray，也可能是一个String，那么就可以用一个 JsonElement 类型的字段去接收，`Gson解析时不会解析具体内容，而是把此字段中的值【原封不动】的返回给你`。  
> PS：JsonElement 是 Gson 框架中所有元素的父类，其子类包括 JsonObject、JsonArray、JsonPrimitive、JsonNull。Json字符串中的所有 value 都可以被解析为 JsonElement 的子类。  
  
```java  
public class Test {  
    public static void main(String[] args) {  
        String content1 = "{\"code\":\"100\",\"data\":{\"errorinfo\":\"成功\"}}";  
        String content2 = "{\"code\":\"100\",\"data\":\"成功\"}";  
        String content3 = "{\"code\":\"100\",\"data\":[\"成功\"]}";  
  
        System.out.println(new Gson().fromJson(content1, Response.class).data);  
        System.out.println(new Gson().fromJson(content2, Response.class).data);  
        System.out.println(new Gson().fromJson(content3, Response.class).data);  
    }  
  
    static class Response {  
        public String code;  
        public JsonElement data;//用一个JsonElement去接收不想解析的内容  
    }  
}  
```  
打印内容为：  
```  
{"errorinfo":"成功"}  
"成功"  
["成功"]  
```  
  
- 拓展1，如果用 Object 去定义 data，则打印内容为：  
  
```  
{errorinfo=成功}  
成功  
[成功]  
```  
可以看到，两者被解析后的内容是`不一样`的，用 JsonElement 定义时得到的内容是带有双引号的`完整的原始结果`，而用 Object 去定义时得到的内容是被Gson框架`处理后的结果`。  
  
- 拓展2，如果用一个泛型 去定义 data，比如：  
  
```java  
static class Response<T> {  
    public String code;  
    public T data;  
}  
```  
测试结果和使用 Object 时完全一致。  
  
- 拓展3，如果用 一个普通对象 AA 去定义 data，对于 content1，打印内容为：`AA@5910e440`。对于 content2、content3，直接异常！  
  
## 解析 Json 字符串中的内容  
注意，一定要保证 get 和 getAs* 方法获取的 key 存在且格式兼容，否则会直接崩掉！  
```java  
String jsonString = "{'flag':true,'data':{'name':'张三','age':18,'address':'广州'}}";  
JsonObject root = new JsonParser().parse(jsonString).getAsJsonObject();  
  
boolean flag = root.getAsJsonPrimitive("flag").getAsBoolean();  
String name = root.getAsJsonObject("data").get("name").getAsString();  
System.out.println(flag + " " + name);//true 张三  
```  
  
## 格式化 Json 字符串  
```java  
return new GsonBuilder()  
      .setPrettyPrinting()  
      .create()  
      .toJson(new JsonParser().parse(content));  
```  
  
## 将序列化后的内容写到 控制台、文件、SB中  
```java  
gson.toJson(user, System.out); //直接写到控制台。System.out 继承自 PrintStream 实现了Appendable接口  
  
FileWriter fileWriter=new FileWriter("d://a.txt");//将toJson后的字符串拼接到文件中  
gson.toJson(person, fileWriter);//FileWriter 继承自 OutputStreamWriter 继承自 Writer 实现了Appendable接口  
fileWriter.close();  
  
StringBuilder builder=new StringBuilder();//将toJson后的字符串拼接到StringBuilder中  
gson.toJson(person, builder);//StrngBuilder、StringBuffer都是Appendable接口的实现类  
System.out.println(builder.toString());  
```  
  
## 使用 JsonWriter 编写 Json 串  
```java  
JsonWriter writer = new JsonWriter(new OutputStreamWriter(System.out));//可以先写到文件中，再从文件中读取  
writer.beginObject()  
 .name("name").value("包青天")  
 .name("sport").beginArray().value("篮球").value("排球").endArray()//数据  
 .name("info").beginObject().name("age").value(28).endObject()//对象  
 .endObject();  
writer.close(); //{"name":"包青天","sport":["篮球","排球"],"info":{"age":28}}  
```  
  
写到指定位置：  
```json  
StringWriter stringWriter = new StringWriter();  
JsonWriter writer = new JsonWriter(stringWriter);//可以先写到文件中，再从文件中读取  
writer.setIndent(" ");//设置缩进格式，一般使用2-4个空格，或使用制表符\t  
//...上面的内容  
System.out.println(stringWriter.getBuffer().toString());  
```  
  
结果  
```json  
{  
    "name": "包青天",  
    "sport": [  
        "篮球",  
        "排球"  
    ],  
    "info": {  
        "age": 28  
    }  
}  
```  
2018-5-27  
