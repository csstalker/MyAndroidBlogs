| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
IO流 简介 总结 API 案例  
***  
目录  
===  

- [IO 流](#IO-流)
	- [简介](#简介)
	- [关闭流的正确方式](#关闭流的正确方式)
	- [关闭流的封装方法](#关闭流的封装方法)
	- [InputStream 转 String 的方式](#InputStream-转-String-的方式)
- [转换流](#转换流)
	- [InputStreamReader](#InputStreamReader)
	- [OutputStreamWriter](#OutputStreamWriter)
	- [测试代码](#测试代码)
- [文件读写流 File-Stream](#文件读写流-File-Stream)
	- [FileInputStream](#FileInputStream)
	- [FileOutputStream](#FileOutputStream)
	- [FileReader(转换流)](#FileReader转换流)
	- [FileWriter(转换流)](#FileWriter转换流)
	- [测试代码](#测试代码)
- [字符串读写流 StringReader/Writer](#字符串读写流-StringReaderWriter)
	- [StringReader](#StringReader)
	- [StringWriter](#StringWriter)
	- [测试代码](#测试代码)
- [字节数组读写流 ByteArray-Stream](#字节数组读写流-ByteArray-Stream)
	- [ByteArrayInputStream](#ByteArrayInputStream)
	- [ByteArrayOutputStream](#ByteArrayOutputStream)
	- [测试代码](#测试代码)
- [二进制数据读写相关的几个接口](#二进制数据读写相关的几个接口)
	- [接口 DataInput](#接口-DataInput)
	- [接口 DataOutput](#接口-DataOutput)
	- [接口 ObjectInput](#接口-ObjectInput)
	- [接口 ObjectOutput](#接口-ObjectOutput)
- [基本数据类型读写流 Data-Stream](#基本数据类型读写流-Data-Stream)
	- [DataInputStream](#DataInputStream)
	- [DataOutputStream](#DataOutputStream)
	- [测试代码](#测试代码)
- [对象序列化反序列化流 Object-Stream](#对象序列化反序列化流-Object-Stream)
	- [ObjectInputStream](#ObjectInputStream)
	- [ObjectOutputStream](#ObjectOutputStream)
	- [测试代码](#测试代码)
- [随机访问文件读写 RandomAccessFile](#随机访问文件读写-RandomAccessFile)
	- [介绍](#介绍)
	- [测试代码](#测试代码)
- [合并输入流 SequenceInputStream](#合并输入流-SequenceInputStream)
- [文本扫描工具 Scanner](#文本扫描工具-Scanner)
- [系统输入输出流 System.in/out/err](#系统输入输出流-Systeminouterr)
  
# IO 流  
## 简介  
IO流用来处理设备之间的数据传输，Java对数据的操作是通过流的方式。  
  
输入流和输出流相对于内存设备而言。  
- 将外设中的数据读取到内存中：输入  
- 将内存中的数据写入到外设中：输出  
  
流按操作的数据不同可分为两种：字节流与字符流。  
字符流的由来：字节流读取文字字节数据后，不直接操作而是先查指定的编码表(默认的码表)，获取对应的文字，再对这个文字进行操作。  
简单说：字符流 = 字节流 + 编码表。  
  
IO流基类：  
- 字节流的抽象基类：InputStream，OutputStream  
- 字符流的抽象基类：Reader，Writer  
  
## 关闭流的正确方式  
```java  
public void closeStream() {  
    InputStream inputStream = null;  
    try {  
        inputStream = new FileInputStream("");  
        //...  
    } catch (IOException e) {  
        e.printStackTrace();  
    } finally {  
        try {  
            if (inputStream != null) {  
                inputStream.close();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}  
```  
  
## 关闭流的封装方法  
```java  
public static void closeStream(Closeable... closeable) {  
    for (Closeable c : closeable) {  
        if (c != null) {  
            try {  
                c.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}  
```  
  
## InputStream 转 String 的方式  
```java  
public static String inputStreamtoString(InputStream inputStream) throws IOException {  
    byte[] bytes = new byte[inputStream.available()]; //一次性读取全部内容  
    inputStream.read(bytes);  
    return new String(bytes, CHARSET);  
}  
  
public static String inputStreamtoStringByBuffer(InputStream inputStream) throws IOException {  
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));  
    StringBuilder sb = new StringBuilder(); //一次读取一行内容  
    String line;  
    while ((line = reader.readLine()) != null) {  
        sb.append(line).append("\n");  
    }  
    return sb.toString();  
}  
  
public static String inputStreamtoStringByByteArray(InputStream inputStream) throws IOException {  
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
    byte[] buffer = new byte[1024]; //一次读取固定长度内容，相对来说最高效的方式  
    int length;  
    while ((length = inputStream.read(buffer)) != -1) {  
        outputStream.write(buffer, 0, length);  
    }  
    return outputStream.toString(CHARSET);  
}  
```  
  
# 转换流  
- 数据从【硬盘】中读入【内存】时，顺序是：文件流(字节流，FileInputStream)->转换流(即InputStreamReader)->缓冲流(字符流，BufferedReader)，所以`InputStreamReader是字节流到字符流的桥梁`；  
- 数据从【内存】中写入【硬盘】时，顺序是：缓冲流(字符流，BufferedWriter)->转化流(即OutputStreamWriter)->文件流(字节流，FileOutputStream)，所以`OutputStreamWriter是字符流到字节流的桥梁`；  
  
## InputStreamReader  
InputStreamReader 是`字节流通向字符流的桥梁`：它`使用指定的 charset 读取字节并将其解码为字符`。它使用的字符集可以由名称指定或显式给定，或者可以接受平台默认的字符集。  
  
每次调用 InputStreamReader 中的一个 `read()` 方法都会导致从底层输入流读取一个或多个字节。要启用从字节到字符的有效转换，可以提前从底层流读取更多的字节，使其超过满足当前读取操作所需的字节。  
  
为了达到最高效率，可要考虑在 `BufferedReader` 内包装 `InputStreamReader`。例如：  
```java  
BufferedReader in = new BufferedReader(new InputStreamReader(System.in));  
```  
  
**继承关系**  
- class InputStreamReader extends Reader  
- 所有已实现的接口：Closeable, Readable  
- 直接已知子类：FileReader  
  
**构造方法**  
- `InputStreamReader(InputStream in)`  创建一个使用`默认字符集`的 InputStreamReader。  
- `InputStreamReader(InputStream in, Charset cs)`  创建使用`给定字符集`的 InputStreamReader。  
- `InputStreamReader(InputStream in, CharsetDecoder dec)`  创建使用`给定字符集解码器`的 InputStreamReader。  
- `InputStreamReader(InputStream in, String charsetName)`  创建使用`指定字符集`的 InputStreamReader。  
  
**常用方法**  
- `void close()`  关闭该流并释放与之关联的所有资源。关闭以前关闭的流无效。  
- `String getEncoding()`  返回此流使用的字符编码的名称。如果流已经关闭，则返回 null  
- `int read()`  读取单个字符。返回读取的字符数，如果已到达流的末尾，则返回 -1  
- `int read(char[] cbuf)` 将读取到的字符存到数组中。返回读取的字符数，如果已到达流的末尾，则返回 -1  
- `int read(char[] cbuf, int offset, int length)`  将字符读入数组中的某一部分。返回读取的字符数，如果已到达流的末尾，则返回 -1  
- `boolean ready()`  判断此流是否已经准备好用于读取。如果其输入缓冲区不为空，或者可从底层字节流读取字节，则 InputStreamReader 已做好被读取准备。  
- 从类 `java.io.Reader` 继承的方法：mark, markSupported, read, read, reset, skip  
  
## OutputStreamWriter  
OutputStreamWriter 是`字符流通向字节流的桥梁`：可使用指定的 charset 将要写入流中的字符编码成字节。  
  
每次调用 `write()` 方法都会导致在给定字符（或字符集）上调用编码转换器。在写入底层输出流之前，得到的这些字节将在缓冲区中累积。可以指定此缓冲区的大小，不过，默认的缓冲区对多数用途来说已足够大。注意，传递给 `write()` 方法的字符没有缓冲。  
  
为了获得最高效率，可考虑将 `OutputStreamWriter` 包装到 `BufferedWriter` 中，以避免频繁调用转换器。例如：  
```java  
BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));  
```  
  
**构造方法**  
- `OutputStreamWriter(OutputStream out)` 创建使用`默认字符编码`的 OutputStreamWriter。  
- `OutputStreamWriter(OutputStream out, Charset cs)` 创建使用`给定字符集`的 OutputStreamWriter。  
- `OutputStreamWriter(OutputStream out, CharsetEncoder enc)` 创建使用`给定字符集编码器`的 OutputStreamWriter。  
- `OutputStreamWriter(OutputStream out, String charsetName)` 创建使用`指定字符集`的 OutputStreamWriter。  
  
**常用方法**  
- `void close()` 关闭此流，但要先刷新它。关闭以前关闭的流无效。  
- `void flush()` 刷新该流的缓冲。  
- `String getEncoding()` 返回此流使用的字符编码的名称。如果流已经关闭，则可能返回 null  
- `void write(char[] cbuf, int off, int len)` 写入字符数组的某一部分。  
- `void write(int c)` 写入单个字符。  
- `void write(String str, int off, int len)` 写入字符串的某一部分。  
- 从类 `java.io.Writer` 继承的方法：append, append, append, write, write  
  
## 测试代码  
```java  
public static void test() {  
    try {  
        //写文件，不带缓冲区  
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(FILE_PATH), CHARSET);  
        writer.write("\n包青天abc-" + FORMAT.format(new Date()));  
        writer.flush();  
        writer.close();  
          
        //写文件，带缓冲区  
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH, true), CHARSET));//可以续写  
        bufferedWriter.write("\n白乾涛abc-" + FORMAT.format(new Date()));  
        bufferedWriter.close();  
          
        //读文件，不带缓冲区  
        InputStreamReader reader = new InputStreamReader(new FileInputStream(FILE_PATH), CHARSET);  
        char[] chars = new char[1024];  
        int length = reader.read(chars);  
        System.out.println(new String(chars, 0, length));  
        reader.close();  
          
        //读文件，带缓冲区  
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_PATH), CHARSET));  
        String line;  
        while ((line = bufferedReader.readLine()) != null) {  
            System.out.println(line);  
        }  
        bufferedReader.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
# 文件读写流 File-Stream  
- 文件字节读写流：FileInputStream、FileOutputStream  
- 文件字符读写流：FileReader、FileWriter  
  
## FileInputStream  
FileInputStream 从文件系统中的某个文件中获得输入字节。哪些文件可用取决于主机环境。  
FileInputStream 用于读取诸如图像数据之类的`原始字节流`。要读取`字符流`，请考虑使用 FileReader。  
  
**继承关系**  
- class FileInputStream extends InputStream  
- 所有已实现的接口：Closeable  
  
**构造方法**  
- `FileInputStream(File file)`  通过打开一个到实际`文件`的连接来创建一个 FileInputStream，该文件通过文件系统中的 File 对象 file 指定。  
- `FileInputStream(FileDescriptor fdObj)`  通过使用文件描述符 fdObj 创建一个 FileInputStream，该文件描述符表示到文件系统中某个实际文件的现有连接。  
- `FileInputStream(String name)`  通过打开一个到实际文件的连接来创建一个 FileInputStream，该文件通过文件系统中的路径名 name 指定。  
  
如果指定文件不存在，或者它是一个`目录`，抑或因为其他某些原因而无法打开进行读取，则抛出 FileNotFoundException。  
  
**常用方法**  
- `int available()`  返回下一次对此输入流调用的方法可以不受阻塞地从此输入流读取（或跳过）的估计剩余字节数。  
- `void close()`  关闭此文件输入流并释放与此流有关的所有系统资源。  
- `FileChannel getChannel()`  返回与此文件输入流有关的唯一 FileChannel 对象(文件通道对象)。  
- `FileDescriptor getFD()`  返回表示到文件系统中实际文件的连接的 FileDescriptor 对象(文件描述符对象)，该文件系统正被此 FileInputStream 使用。  
- `int read()`  从此输入流中读取一个数据字节。如果没有输入可用，则此方法将阻塞。返回下一个数据字节；如果已到达文件末尾，则返回 -1。  
- `int read(byte[] b)`  从此输入流中将最多 b.length 个字节的数据读入一个 byte 数组中。在某些输入可用之前，此方法将阻塞。返回读入缓冲区的字节总数，如果因为已经到达文件末尾而没有更多的数据，则返回 -1。  
- `int read(byte[] b, int off, int len)`  从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。  
- `long skip(long n)`  从输入流中跳过并丢弃 n 个字节的数据。  
- 从类 `java.io.InputStream` 继承的方法：mark, markSupported, reset  
  
## FileOutputStream  
文件输出流是用于将数据写入 File 或 FileDescriptor 的输出流。文件是否可用或能否可以被创建取决于基础平台。特别是某些平台一次只允许一个 FileOutputStream（或其他文件写入对象）打开文件进行写入。在这种情况下，如果所涉及的文件已经打开，则此类中的构造方法将失败。  
  
FileOutputStream 用于写入诸如图像数据之类的`原始字节的流`。要写入`字符流`，请考虑使用 FileWriter。  
  
**继承关系**  
- class FileOutputStream extends OutputStream  
- 所有已实现的接口：Closeable, Flushable  
  
**构造方法**  
- `FileOutputStream(File file)` 创建一个向指定 File 对象表示的文件中写入数据的文件输出流。创建一个新 FileDescriptor 对象来表示此文件连接。  
- `FileOutputStream(File file, boolean append)` 创建一个向指定 File 对象表示的文件中写入数据的文件输出流。如果第二个参数为 true，则将字节写入文件末尾处，而不是写入文件开始处。创建一个新 FileDescriptor 对象来表示此文件连接。  
- `FileOutputStream(FileDescriptor fdObj)` 创建一个向指定文件描述符处写入数据的输出文件流，该文件描述符表示一个到文件系统中的某个实际文件的现有连接。  
- `FileOutputStream(String name)` 创建一个向具有指定名称的文件中写入数据的输出文件流。  
- `FileOutputStream(String name, boolean append)` 创建一个向具有指定 name 的文件中写入数据的输出文件流。如果第二个参数为 true，则将字节写入文件末尾处，而不是写入文件开始处。创建一个新 FileDescriptor 对象来表示此文件连接。  
  
如果该文件存在，但它是一个目录，而不是一个常规文件；或者该文件不存在，但无法创建它；抑或因为其他某些原因而无法打开它，则抛出 FileNotFoundException。  
  
**常用方法**  
- `void close()` 关闭此文件输出流并释放与此流有关的所有系统资源。  
- `protected void finalize()` 清理到文件的连接，并确保在不再引用此文件输出流时调用此流的 close 方法。  
- `FileChannel getChannel()` 返回与此文件输出流有关的唯一 FileChannel 对象。  
- `FileDescriptor getFD()` 返回与此流有关的文件描述符。  
- `void write(byte[] b)` 将 b.length 个字节从指定 byte 数组写入此文件输出流中。  
- `void write(byte[] b, int off, int len)` 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此文件输出流。  
- `void write(int b)` 将指定字节写入此文件输出流。  
- 从类 `java.io.OutputStream` 继承的方法：flush  
  
## FileReader(转换流)  
用来读取`字符文件`的便捷类。此类的构造方法假定`默认字符编码`和`默认字节缓冲区大小`都是适当的。要自己指定这些值，可以先在 FileInputStream 上构造一个 InputStreamReader。  
  
FileReader 用于读取字符流。要读取原始字节流，请考虑使用 FileInputStream。  
  
**继承关系**  
- class FileReader extends InputStreamReader  
- 所有已实现的接口：Closeable, Readable  
  
**构造方法**  
- `FileReader(File file)`  在给定从中读取数据的 File 的情况下创建一个新 FileReader。  
- `FileReader(FileDescriptor fd)`  在给定从中读取数据的 FileDescriptor 的情况下创建一个新 FileReader。  
- `FileReader(String fileName)`  在给定从中读取数据的文件名的情况下创建一个新 FileReader。  
  
**常用方法**  
- 从类 `java.io.Reader` 继承的方法：mark, markSupported, read, read, reset, skip  
- 从类 `java.io.InputStreamReader` 继承的方法：close, getEncoding, read, read, ready  
  
## FileWriter(转换流)  
用来写入字符文件的便捷类。此类的构造方法假定默认字符编码和默认字节缓冲区大小都是可接受的。要自己指定这些值，可以先在 FileOutputStream 上构造一个 OutputStreamWriter。  
  
文件是否可用或是否可以被创建取决于底层平台。特别是某些平台一次只允许一个 FileWriter（或其他文件写入对象）打开文件进行写入。在这种情况下，如果所涉及的文件已经打开，则此类中的构造方法将失败。  
  
FileWriter 用于写入字符流。要写入原始字节流，请考虑使用 FileOutputStream。  
  
**继承关系**  
- class FileWriter extends OutputStreamWriter  
- 所有已实现的接口：Closeable, Flushable, Appendable  
  
**构造方法**  
- `FileWriter(File file)`  根据给定的 File 对象构造一个 FileWriter 对象。  
- `FileWriter(File file, boolean append)`  根据给定的 File 对象构造一个 FileWriter 对象。  
- `FileWriter(FileDescriptor fd)`  构造与某个文件描述符相关联的 FileWriter 对象。  
- `FileWriter(String fileName)`  根据给定的文件名构造一个 FileWriter 对象。  
- `FileWriter(String fileName, boolean append)`  根据给定的文件名以及指示是否附加写入数据的 boolean 值来构造 FileWriter 对象。  
  
**常用方法**  
- 从类 `java.io.Writer` 继承的方法：append, append, append, write, write  
- 从类 `java.io.OutputStreamWriter` 继承的方法：close, flush, getEncoding, write, write, write  
  
## 测试代码  
```java  
public static void test() {  
    try {  
        char[] chars = new char[1024];  
        int length;  
        //字符流读写文件  
        FileOutputStream outputStream = new FileOutputStream(FILE_PATH);  
        outputStream.write(("字符流读写文件-" + FORMAT.format(new Date())).getBytes());  
        outputStream.close();  
          
        FileInputStream inputStream = new FileInputStream(FILE_PATH);  
        byte[] bytes = new byte[1024];  
        length = inputStream.read(bytes);  
        System.out.println(new String(bytes, 0, length));  
        inputStream.close();  
          
        //字节流读写文件  
        FileWriter writer = new FileWriter(FILE_PATH);  
        writer.write("字节流读写文件-" + FORMAT.format(new Date()));  
        writer.close();  
          
        FileReader reader = new FileReader(FILE_PATH);  
        length = reader.read(chars);  
        System.out.println(new String(chars, 0, length));  
        reader.close();  
          
        //转换流读写文件  
        OutputStreamWriter streamWriter = new OutputStreamWriter(new FileOutputStream(FILE_PATH), CHARSET);  
        streamWriter.write("转换流读写文件-" + FORMAT.format(new Date()));  
        streamWriter.close();  
          
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(FILE_PATH), CHARSET);  
        length = streamReader.read(chars);  
        System.out.println(new String(chars, 0, length));  
        streamReader.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
# 字符串读写流 StringReader/Writer  
为什么会存在这两个类？  
当你有一组应用程序接口（API）只允许用 Writer 或 Reader 作为输入，但你的数据源又仅仅是一个现成的String类型数据，而无需从文件等流中读写，这时可以用StringWriter或StringReader。  
  
## StringReader  
一个源为字符串的字符流  
  
**继承关系**  
- class StringReader extends Reader  
  
**构造方法**  
- `StringReader(String s)` 创建一个新字符串 reader。参数 s 为提供字符流的字符串。  
  
**常用方法**  
- `void close()` 关闭该流并释放与之关联的所有系统资源。  
- `int read()` 读取单个字符。返回读取的字符，如果已到达流的末尾，则返回 -1  
- `int read(char[] cbuf, int off, int len)` 将字符读入数组的某一部分。返回读取的字符数，如果已到达流的末尾，则返回 -1  
- `long skip(long ns)` 跳过流中指定数量的字符。返回跳过的字符数。  
  
**其他方法**  
- `void mark(int readAheadLimit)` 标记流中的当前位置。对 reset() 的后续调用会将该流重新定位到此点。  
- `boolean markSupported()` 判断此流是否支持 mark() 操作以及支持哪一项操作。当且仅当此流支持此 mark 操作时，返回 true。  
- `boolean ready()` 判断此流是否已经准备好用于读取。如果保证下一个 read() 不阻塞输入，则返回 true  
- `void reset()` 将该流重置为最新的标记，如果从未标记过，则将其重置到该字符串的开头。  
- 从类 `java.io.Reader` 继承的方法：read, read  
  
## StringWriter  
一个字符流，可以用其回收在字符串缓冲区中的输出来构造字符串。  
  
注意，StringWriter中，写入的数据只是存在于缓存中，并不会写入实质的存储介质之中。  
关闭 StringWriter 无效。此类中的方法在关闭该流后仍可被调用，而不会产生任何 IOException。  
  
**继承关系**  
- class StringWriter extends Writer  
  
**构造方法**  
- `StringWriter()` 使用默认初始字符串缓冲区大小创建一个新字符串 writer。  
- `StringWriter(int initialSize)` 使用指定初始字符串缓冲区大小创建一个新字符串 writer。参数 initialSize - 在此缓冲区自动扩展前适合它的 char 值数。  
  
**常用方法**  
- `StringWriter append(char c)` 将指定字符添加到此 writer。参数 c 为要添加的 16 位字符。行为与`out.write(c)`完全相同  
- `StringWriter append(CharSequence csq)` 将指定的字符序列添加到此 writer。行为与`out.write(csq.toString())`完全相同。如果 csq 为 null，则向此 writer 添加四个字符 "null"。  
- `StringWriter append(CharSequence csq, int start, int end)` 将指定字符序列的子序列添加到此 writer。行为与`out.write(csq.subSequence(start, end).toString())`完全相同。如果 csq 为 null，则向此 writer 添加四个字符 "null"。  
- `void write(char[] cbuf, int off, int len)` 写入字符数组的某一部分。  
- `void write(int c)` 写入单个字符。  
- `void write(String str)` 写入一个字符串。  
- `void write(String str, int off, int len)` 写入字符串的某一部分。  
- `StringBuffer getBuffer()` 返回该字符串缓冲区本身。  
- `String toString()` 以字符串的形式返回该缓冲区的当前值。和`getBuffer().toString()`的效果相同  
  
**其他方法**  
- `void close()` 关闭 StringWriter 无效。  
- `void flush()` 刷新该流的缓冲。  
  
## 测试代码  
```java  
public static void test(String string) {  
    try {  
        StringReader sr = new StringReader(string);  
        StringWriter sw = new StringWriter();  
        int c = -1;  
        while ((c = sr.read()) != -1) {  
            sw.write(c);  
        }  
        sw.close(); //close方法是一个空实现  
        System.out.println(sw.getBuffer().toString() + "，" + sw.toString());  
    } catch (Exception e) {  
        e.printStackTrace();  
    }  
}  
```  
  
# 字节数组读写流 ByteArray-Stream  
`ByteArrayInputStream` 和 `ByteArrayOutputStream`，用于以IO流的方式来完成对`字节数组`内容的读写，来支持类似【内存虚拟文件】或者【内存映射文件】的功能。  
  
相比之下，`ByteArrayOutputStream` 要比 `ByteArrayInputStream` 常见的多。  
  
## ByteArrayInputStream  
ByteArrayInputStream 包含一个内部缓冲区，该缓冲区包含从流中读取的字节。内部计数器跟踪 read 方法要提供的下一个字节。  
关闭 ByteArrayInputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何 IOException。  
  
**继承关系**  
- class ByteArrayInputStream extends InputStream  
- 所有已实现的接口：Closeable  
  
**构造方法**  
- `ByteArrayInputStream(byte[] buf)` 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组。该缓冲区数组不是复制得到的。 pos 的初始值是 0， count 的初始值是 buf 的长度。  
- `ByteArrayInputStream(byte[] buf, int offset, int length)` 创建 ByteArrayInputStream，使用 buf 作为其缓冲区数组。该缓冲区数组不是复制得到的。pos 的初始值是 offset， count 的初始值是 offset+length 和 buf.length 中的最小值。  
  
**常用方法**  
- `int read()`  从此输入流中读取下一个数据字节。返回一个 0 到 255 范围内的 int 字节值。如果因为到达流末尾而没有可用的字节，则返回值 -1。此 read 方法不会阻塞。  
- `int read(byte[] b, int off, int len)`  将最多 len 个数据字节从此输入流读入 byte 数组。如果 pos 等于 count，则返回 -1 指示文件结束。否则，读取的字节数 k 等于 len 和 count-pos 中的较小者。此 read 方法不会阻塞。  
- `int available()`  返回可从此输入流读取（或跳过）的剩余字节数。返回值是 count - pos，它是要从输入缓冲区中读取的剩余字节数。  
  
**其他方法**  
- `void close()`  关闭 ByteArrayInputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何 IOException。  
- `void reset()`  将缓冲区的位置重置为标记位置。除非已标记了另一个位置，或者在构造方法中指定了一个偏移量，否则该标记位置是 0。  
- `long skip(long n)`  从此输入流中跳过 n 个输入字节。如果已到达输入流末尾，则可能会跳过较少的字节。实际跳过的字节数 k 等于 n 和 count-pos 中的较小者。将值 k 与 pos 相加并返回 k。  
- `void mark(int readAheadLimit)`  设置流中的当前标记位置。构造时默认将 ByteArrayInputStream 对象标记在位置零处。如果尚未设置标记，则标记值是传递给构造方法的偏移量（如果未提供偏移量，则标记值为 0）。参数 readAheadLimit 表示在标记位置失效前可以读取字节的最大限制。  
- `boolean markSupported()`  测试此 InputStream 是否支持 mark/reset。ByteArrayInputStream 的此方法始终返回 true。  
- 从类 `java.io.InputStream` 继承的方法：read  
  
## ByteArrayOutputStream  
此类实现了一个输出流，其中的数据被写入一个 byte 数组。缓冲区会随着数据的不断写入而自动增长。可使用 toByteArray() 和 toString() 获取数据。  
关闭 ByteArrayOutputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何 IOException。  
  
**继承关系**  
- class ByteArrayOutputStream  extends OutputStream  
- 所有已实现的接口：Closeable, Flushable  
  
**构造方法**  
- `ByteArrayOutputStream()` 创建一个新的 byte 数组输出流。缓冲区的容量最初是 32 字节，如有必要可增加其大小。  
- `ByteArrayOutputStream(int size)` 创建一个新的 byte 数组输出流，它具有指定大小的缓冲区容量（以字节为单位）。  
  
**常用方法**  
- `void  write(int b)`  将指定的字节写入此 byte 数组输出流。  
- `void  write(byte[] b, int off, int len)`  将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte 数组输出流。  
- `void  writeTo(OutputStream out)`  将此 byte 数组输出流的全部内容写入到指定的输出流参数中，这与使用 `out.write(buf, 0, count)`  调用该输出流的 write 方法效果一样。  
- `int  size()`  返回缓冲区的当前大小。  
- `byte[]  toByteArray()`  创建一个新分配的 byte 数组。其大小是此输出流的当前大小，并且缓冲区的有效内容已复制到该数组中。  
- `String  toString()`  使用平台默认的字符集，通过解码字节将缓冲区内容转换为字符串。  
- `String  toString(String charsetName)`  使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串。  
  
**其他方法**  
- `void  close()`  关闭 ByteArrayOutputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何 IOException。  
- `void  reset()`  将此 byte 数组输出流的 count 字段重置为零，从而丢弃输出流中目前已累积的所有输出。通过重新使用已分配的缓冲区空间，可以再次使用该输出流。  
- 从类 `java.io.OutputStream` 继承的方法：flush, write  
  
## 测试代码  
```java  
public static void byteArrayStream() {  
    byte[] buf = "包青天_baiqiantao_2018".getBytes();  
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
    ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);  
    switch (count % 3) {  
        case 0:  
            int ch;  
            while ((ch = inputStream.read()) != -1) {  
                outputStream.write(ch);  
            }  
            Log.i("bqt", inputStream.available() + "，内容：" + outputStream.toString()); //0，包青天_baiqiantao_2018  
            break;  
        case 1:  
            outputStream.write(buf, 0, buf.length);  
            Log.i("bqt", outputStream.toString()); //包青天_baiqiantao_2018  
            Log.i("bqt", outputStream.size() + "，字节数组：" + Arrays.toString(outputStream.toByteArray()));//25  
            break;  
        case 2:  
            outputStream.write(buf, 0, buf.length);  
            byte[] data = FORMAT.format(new Date()).getBytes();  
            outputStream.write(data, 0, data.length);//拼接日期，而非覆盖  
            try {  
                OutputStream out = new FileOutputStream(FILE_PATH); //写入文件中  
                outputStream.writeTo(out);  
                out.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            Log.i("bqt", outputStream.toString()); //包青天_baiqiantao_2018 2018.11.21 21:52:12 414  
            break;  
        default:  
            break;  
    }  
    //outputStream.close();//关闭流无意义(在关闭此流后仍可被调用)，因为它们没有调用底层资源，所有的操作都是在内存中完成的  
    //inputStream.close();  
}  
```  
# 二进制数据读写相关的几个接口  
## 接口 DataInput  
- 已知子接口：`ObjectInput`  
- 已知实现类：`DataInputStream`, `ObjectInputStream`, `RandomAccessFile`  
  
DataInput 接口用于从二进制流中读取字节，并根据所有 Java 基本类型数据进行重构。同时还提供根据 UTF-8 修改版格式的数据重构 String 的工具。  
  
**接口 DataInput 中的方法**  
- `boolean readBoolean()`  读取一个输入字节，如果该字节不是零，则返回 true，如果是零，则返回 false。  
- `byte readByte()`  读取并返回一个输入字节(8 位值)。该字节被看作是 -128 到 127（包含）范围内的一个有符号值。  
- `char readChar()`  读取两个输入字节并返回一个 char 值。  
- `double readDouble()`  读取八个输入字节并返回一个 double 值。  
- `float readFloat()`  读取四个输入字节并返回一个 float 值。  
- `void readFully(byte[] b)`  从输入流中读取一些字节，并将它们存储在缓冲区数组 b 中。  
- `void readFully(byte[] b, int off, int len)`  从输入流中读取 len 个字节。  
- `int readInt()`  读取四个输入字节并返回一个 int 值。  
- `String readLine()`  从输入流中读取下一文本行。  
- `long readLong()`  读取八个输入字节并返回一个 long 值。  
- `short readShort()`  读取两个输入字节并返回一个 short 值(16 位值)。  
- `int readUnsignedByte()`  读取一个输入字节，将它左侧补零 (zero-extend) 转变为 int 类型，并返回结果，所以结果的范围是 0 到 255(无符号 8 位值)。  
- `int readUnsignedShort()`  读取两个输入字节，并返回 0 到 65535 范围内的一个 int 值(无符号 16 位值)。  
- `String readUTF()`  读入一个已使用 UTF-8 修改版格式编码的字符串。  
- `int skipBytes(int n)`  试图在输入流中跳过数据的 n 个字节，并丢弃跳过的字节。  
  
## 接口 DataOutput  
- 已知子接口：`ObjectOutput`  
- 已知实现类：`DataOutputStream`, `ObjectOutputStream`, `RandomAccessFile`  
  
DataOutput 接口用于将数据从任意 Java 基本类型转换为一系列字节，并将这些字节写入二进制流。同时还提供了一个将 String 转换成 UTF-8 修改版格式并写入所得到的系列字节的工具。  
对于此接口中写入字节的所有方法，如果由于某种原因无法写入某个字节，则抛出 IOException。  
  
**接口 DataOutput 中的方法**  
- `void write(byte[] b)`  将数组 b 中的所有字节写入输出流。  
- `void write(byte[] b, int off, int len)`  将数组 b 中的 len 个字节按顺序写入输出流。  
- `void write(int b)`  将参数 b 的八个低位写入输出流。  
- `void writeBoolean(boolean v)`  将一个 boolean 值写入输出流。  
- `void writeByte(int v)`  将参数 v 的八个低位写入输出流。  
- `void writeBytes(String s)`  将一个字符串写入输出流。  
- `void writeChar(int v)`  将一个 char 值写入输出流，该值由两个字节组成。  
- `void writeChars(String s)`  将字符串 s 中的所有字符按顺序写入输出流，每个字符用两个字节表示。  
- `void writeDouble(double v)`  将一个 double 值写入输出流，该值由八个字节组成。  
- `void writeFloat(float v)`  将一个 float 值写入输出流，该值由四个字节组成。  
- `void writeInt(int v)`  将一个 int 值写入输出流，该值由四个字节组成。  
- `void writeLong(long v)`  将一个 long 值写入输出流，该值由八个字节组成。  
- `void writeShort(int v)`  将两个字节写入输出流，用它们表示参数值。  
- `void writeUTF(String s)`  将表示长度信息的两个字节写入输出流，后跟字符串 s 中每个字符的 UTF-8 修改版表示形式。  
  
## 接口 ObjectInput  
- public interface ObjectInput extends DataInput  
- 所有超级接口：DataInput  
- 所有已知实现类：ObjectInputStream  
  
ObjectInput 扩展 DataInput 接口以包含`对象的读操作`。  
DataInput 包括`基本类型`的输入方法，ObjectInput 扩展了该接口，以包含`对象、数组和 String`的输出方法。  
  
**接口 ObjectInput 中的方法**  
- `int read()`  读取数据字节。如果不存在可用的输入，此方法将发生阻塞。返回读取的实际字节数，当到达流的末尾时返回 -1。  
- `int read(byte[] b)`  读入 byte 数组。在某些输入可用之前，此方法将发生阻塞。返回读取的实际字节数，当到达流的末尾时返回 -1。  
- `int read(byte[] b, int off, int len)`  读入 byte 数组。在某些输入可用之前，此方法将发生阻塞。返回读取的实际字节数，当到达流的末尾时返回 -1。  
- `int available()`  返回可以无阻塞地读取的字节数。  
- `void close()`  关闭输入流。必须调用此方法以释放与流相关的所有资源。  
- `Object readObject()`  读取并返回对象。实现此接口的类定义从哪里“读取”对象。  
- `long skip(long n)`  跳过输入的 n 个字节。  
  
**从接口 java.io.DataInput 继承的方法**  
readBoolean, readByte, readChar, readDouble, readFloat, readFully, readFully, readInt, readLine, readLong, readShort, readUnsignedByte, readUnsignedShort, readUTF, skipBytes  
  
## 接口 ObjectOutput  
- public interface ObjectOutput extends DataOutput  
- 所有超级接口：DataOutput  
- 所有已知实现类：ObjectOutputStream  
  
ObjectOutput 扩展 DataOutput 接口以包含`对象的写入操作`。  
DataOutput 包括基本类型的输出方法，ObjectOutput 扩展了该接口，以包含`对象、数组和 String`的输出方法。  
  
**接口 ObjectOutput 中的方法**  
- `void write(int b)`  写入字节。在实际写入字节前，此方法将阻塞。  
- `void write(byte[] b)`  写入 byte 数组。  
- `void write(byte[] b, int off, int len)`  写入字节的子数组。  
- `void writeObject(Object obj)`  将对象写入底层存储或流。实现此接口的类定义如何写入对象。  
- `void close()`  关闭该流。必须调用此方法以释放与此流相关的所有资源。  
- `void flush()`  刷新该流的缓冲。此操作将写入所有已缓冲的输出字节。  
  
**从接口 java.io.DataOutput 继承的方法**  
writeBoolean, writeByte, writeBytes, writeChar, writeChars, writeDouble, writeFloat, writeInt, writeLong, writeShort, writeUTF  
  
# 基本数据类型读写流 Data-Stream  
允许应用程序以`与机器无关方式`从底层输入流中读写`Java基本数据类型`。  
  
**适用场景**  
若我们想输出一个long类型(8个字节)或float类型(4个字节)的数据，怎么办呢？  
可以一个字节一个字节输出，也可以转换成字符串输出，但是这样转换费时间，若是直接输出该多好啊！  
数据流`DataOutputStream`就解决了我们输出基本数据类型的困难。  
数据流可以直接输出float类型或long类型，提高了数据读写的效率。  
  
另外，我们有时只是要存储一个对象的成员数据，没有必要存储整个对象的信息，假设成员数据的类型都是Java的基本数据类型，这样的需求不必使用到与Object输入、输出相关的流对象，可以使用DataInputStream、DataOutputStream来写入或读出数据。  
  
## DataInputStream  
DataInputStream 允许应用程序以与机器无关方式从底层输入流中读取基本 Java 数据类型。  
DataInputStream 对于多线程访问不一定是安全的。  
  
**继承关系**  
- class DataInputStream extends FilterInputStream implements DataInput   
- class FilterInputStream extends InputStream  
- 所有已实现的接口：Closeable, DataInput  
  
**构造方法**  
- `DataInputStream(InputStream in)` 使用指定的底层 InputStream 创建一个 DataInputStream。  
  
**常用方法**  
- `int read(byte[] b)`  从包含的输入流中读取一定数量的字节，并将它们存储到缓冲区数组 b 中。  
    - 以整数形式返回实际读取的字节数。  
    - 在输入数据可用、检测到文件末尾或抛出异常之前，此方法将一直阻塞。  
    - 如果 b 为 null，则抛出 NullPointerException。  
    - 如果 b 的长度为 0，则不读取字节并返回 0；否则，尝试读取至少一个字节。  
    - 如果因为流位于文件末尾而没有字节可用，则返回值 -1；否则至少读取一个字节并将其存储到 b 中。  
- `int read(byte[] buf,int off,int len)`  从包含的输入流中将最多 len 个字节读入一个 byte 数组中。  
    - 尽量读取 len 个字节，但读取的字节数可能少于 len 个，也可能为零。以整数形式返回实际读取的字节数。  
    - 在输入数据可用、检测到文件末尾或抛出异常之前，此方法将阻塞。  
    - 如果 len 为零，则不读取任何字节并返回 0；否则，尝试读取至少一个字节。  
    - 如果因为流位于文件未尾而没有字节可用，则返回值 -1；否则，至少读取一个字节并将其存储到 b 中。  
- `static String readUTF(DataInput in)`  从流 in 中读取用 UTF-8 修改版格式编码的 Unicode 字符格式的字符串；然后以 String 形式返回此字符串。UTF-8 修改版表示形式的一些细节与 DataInput 的 readUTF 方法完全相同。  
  
**其他方法**  
- 从类 `java.io.FilterInputStream` 继承的方法：available, close, mark, markSupported, read, reset, skip    
- 从接口 `java.io.DataInput` 继承的方法：...  
  
## DataOutputStream  
DataOutputStream 允许应用程序以适当方式将基本 Java 数据类型写入输出流中。  
  
**继承关系**  
- class DataOutputStream extends FilterOutputStream implements DataOutput  
- class FilterOutputStream extends OutputStream  
- 所有已实现的接口：Closeable, DataOutput, Flushable  
  
**构造方法**  
- DataOutputStream(OutputStream out)  //创建一个新的数据输出流，将数据写入指定基础输出流。  
  
**写入八种基本类型数据**  
- `void writeBoolean(boolean v)`  //将一个 boolean 值以 1-byte 值形式写入基础输出流。值 true 以值 (byte)1 的形式被写出；值 false 以值 (byte)0 的形式被写出。如果没有抛出异常，则计数器 written 增加 1。  
- `void writeByte(int v)`  //将一个 byte 值以 1-byte 值形式写出到基础输出流中。如果没有抛出异常，则计数器 written 增加 1。  
- `void writeChar(int v)`  //将一个 char 值以 2-byte 值形式写入基础输出流中，先写入高字节。如果没有抛出异常，则计数器 written 增加 2。  
- `void writeDouble(double v)`  //使用 Double 类中的 doubleToLongBits 方法将 double 参数转换为一个 long 值，然后将该 long 值以 8-byte 值形式写入基础输出流中，先写入高字节。如果没有抛出异常，则计数器 written 增加 8。  
- `void writeFloat(float v)`  //使用 Float 类中的 floatToIntBits 方法将 float 参数转换为一个 int 值，然后将该 int 值以 4-byte 值形式写入基础输出流中，先写入高字节。如果没有抛出异常，则计数器 written 增加 4。  
- `void writeInt(int v)`  //将一个 int 值以 4-byte 值形式写入基础输出流中，先写入高字节。如果没有抛出异常，则计数器 written 增加 4。  
- `void writeLong(long v)`  //将一个 long 值以 8-byte 值形式写入基础输出流中，先写入高字节。如果没有抛出异常，则计数器 written 增加 8。  
- `void writeShort(int v)`  //将一个 short 值以 2-byte 值形式写入基础输出流中，先写入高字节。如果没有抛出异常，则计数器 written 增加 2。  
  
**写入其他类型数据**  
- `void write(byte[] b, int off, int len)`  //将指定 byte 数组中从偏移量 off 开始的 len 个字节写入基础输出流。如果没有抛出异常，则计数器 written 增加 len。  
- `void write(int b)`  //将指定字节（参数 b 的八个低位）写入基础输出流。如果没有抛出异常，则计数器 written 增加 1。  
- `void writeBytes(String s)`  //将字符串按字节顺序写出到基础输出流中。按顺序写出字符串中每个字符，丢弃其八个高位。如果没有抛出异常，则计数器 written 增加 s 的长度。中文会乱码！  
- `void writeChars(String s)`  //将字符串按字符顺序写入基础输出流。通过 writeChar 方法将每个字符写入数据输出流。如果没有抛出异常，则计数器 written 增加 s 长度的两倍。  
- `void writeUTF(String str)`  //以与机器无关方式使用 UTF-8 修改版编码将一个字符串写入基础输出流。  
> 首先，通过 writeShort 方法将`两个字节`写入输出流，表示后跟的字节数。该值是实际写出的字节数，不是字符串的长度。根据此长度，使用字符的 UTF-8 修改版编码按顺序输出字符串的每个字符。如果没有抛出异常，则计数器 written 增加写入输出流的字节总数。该值至少是 2 加 str 的长度(英文字符)，最多是 2 加 str 的三倍长度(中文字符)。  
  
**其他方法**  
- `void flush()`  //清空此数据输出流。这迫使所有缓冲的输出字节被写出到流中。  
- `int size()`  //返回计数器 written 的当前值，即到目前为止写入此数据输出流的字节数。  
- 从类 `java.io.FilterOutputStream` 继承的方法：close, write  
- 从接口 `java.io.DataOutput` 继承的方法：write  
  
## 测试代码  
```java  
public static void dataStream() {  
    try {  
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(FILE_PATH));  
        DataInputStream inputStream = new DataInputStream(new FileInputStream(FILE_PATH));  
        switch (count % 4) {  
            case 0:  
                outputStream.writeBoolean(true);  
                outputStream.writeByte(97);  
                outputStream.writeChar('b');  
                outputStream.writeInt(99);  
                outputStream.writeShort(100);  
                outputStream.writeLong(101);  
                outputStream.writeFloat(0.5f);  
                outputStream.writeDouble(0.5d);  
                outputStream.flush();  
                  
                String content = inputStream.readBoolean() + "," + inputStream.readByte() + "," + inputStream.readChar() + ","  
                    + inputStream.readInt() + "," + inputStream.readShort() + "," + inputStream.readLong() + "," +  
                    inputStream.readFloat() + "," + inputStream.readDouble();  
                Log.i("bqt", "读取的写入数据：" + content);//true,97,b,99,100,101,0.5,0.5  
                break;  
            case 1:  
                outputStream.write(255);//将指定字节的最低8位写入基础输出流，1111 1111  
                outputStream.write(256);//1 0000 0000  
                outputStream.flush();  
                  
                byte[] array = new byte[inputStream.available()];// 读取字节数组  
                int length = inputStream.read(array);  
                Log.i("bqt", length + "," + array.length + ",读取的写入数据：" + Arrays.toString(array));//2,2,[-1, 0]  
                break;  
            case 2:  
                String text = "青天abc12";  
                byte[] textArray = text.getBytes();//2*3 + 5*1 = 11  
                outputStream.write(textArray, 0, textArray.length);  
                outputStream.writeBytes(text);  //7，一个字符以一个字节的长度写入，中文会乱码！  
                outputStream.writeChars(text); //14，一个字符两个字节  
                outputStream.writeUTF(text);//将一个字符串用UTF-8编码写到基本输出流  
                outputStream.flush();  
                  
                byte[] full = new byte[inputStream.available()];  
                inputStream.readFully(full);  
                Log.i("bqt", full.length + "," + new String(full));//45,青天abc12R)abc12�RY)��a��b��c��1��2��青天abc12  
                  
                inputStream = new DataInputStream(new FileInputStream(FILE_PATH));  
                byte[] temp = new byte[textArray.length];  
                Log.i("bqt", inputStream.read(temp) + "," + new String(temp));//11,青天abc12  
                temp = new byte[text.length()];  
                Log.i("bqt", inputStream.read(temp) + "," + new String(temp));//7,R)abc12  
                temp = new byte[2 * text.length()];  
                Log.i("bqt", inputStream.read(temp) + "," + new String(temp));//14,�RY)��a��b��c��1��2  
                Log.i("bqt", inputStream.readUTF());//青天abc12。长度为【2 + 3 * 2 + 5 * 1 = 13】  
                break;  
            case 3:  
                outputStream.writeChars("青天abc12");  
                StringBuilder sb = new StringBuilder();  
                for (int i = 0; i < outputStream.size(); i += 2) {  
                    char c = inputStream.readChar();  
                    sb.append(c);  
                }  
                Log.i("bqt", sb.toString()); // 青天abc12  
                break;  
            default:  
                break;  
        }  
        outputStream.close();  
        inputStream.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
# 对象序列化反序列化流 Object-Stream  
## ObjectInputStream  
ObjectInputStream 对以前使用 ObjectOutputStream 写入的`基本数据和对象`进行`反序列化`。  
  
ObjectOutputStream 和 ObjectInputStream 分别与 FileOutputStream 和 FileInputStream 一起使用时，可以为应用程序提供对对象图形的持久存储。ObjectInputStream 用于恢复那些以前序列化的对象。其他用途包括使用套接字流在主机之间传递对象，或者用于编组和解组远程通信系统中的实参和形参。  
  
ObjectInputStream 确保从流创建的图形中所有对象的类型与 Java 虚拟机中显示的类相匹配。使用标准机制按需加载类。  
  
只有支持 `java.io.Serializable 或 java.io.Externalizable` 接口的对象才能从流读取。  
  
readObject 方法用于从流读取对象。应该使用 Java 的安全强制转换来获取所需的类型。在 Java 中，字符串和数组都是对象，所以在序列化期间将其视为对象。读取时，需要将其强制转换为期望的类型。  
  
可以使用 DataInput 上的适当方法从流读取基本数据类型。  
  
默认情况下，对象的反序列化机制会将每个字段的内容恢复为写入时它所具有的值和类型。反序列化进程将忽略声明为`瞬态或静态`的字段。对其他对象的引用使得根据需要从流中读取这些对象。使用`引用共享机制`能够正确地恢复对象的图形。反序列化时始终分配新对象，这样可以避免现有对象被重写。  
  
读取对象类似于运行新对象的构造方法。为对象分配内存并将其初始化为零 (NULL)。为不可序列化类调用无参数构造方法，然后从以最接近 java.lang.object 的可序列化类开始和以对象的最特定类结束的流恢复可序列化类的字段。  
  
**继承关系**  
- class ObjectInputStream extends InputStream implements ObjectInput, ObjectStreamConstants  
- 所有已实现的接口：Closeable, DataInput, ObjectInput, ObjectStreamConstants  
- 接口 ObjectStreamConstants 中没有定义任何方法，但是定义了很多常量  
  
**构造方法**  
- `ObjectInputStream(InputStream in)` 创建从指定 InputStream 读取的 ObjectInputStream。  
  
**接口 ObjectInput 和 DataInput 中的方法**  
  
**其他方法**  
- `void defaultReadObject()`  从此流读取当前类的非静态和非瞬态字段。  
- `ObjectInputStream.GetField readFields()`  按名称从流中读取持久字段并使其可用。  
- `Object readUnshared()`  从 ObjectInputStream 读取“非共享”对象。  
- `void registerValidation(ObjectInputValidation obj, int prio)`  在返回图形前注册要验证的对象。  
- 从类 `java.io.InputStream` 继承的方法：mark, markSupported, read, reset, skip  
- 从接口 `java.io.ObjectInput` 继承的方法：read, skip  
  
## ObjectOutputStream  
ObjectOutputStream 将 Java 对象的基本数据类型和图形写入 OutputStream。可以使用 ObjectInputStream 读取（重构）对象。通过在流中使用文件可以实现对象的持久存储。如果流是网络套接字流，则可以在另一台主机上或另一个进程中重构对象。  
  
只能将支持 `java.io.Serializable` 接口的对象写入流中。每个 serializable 对象的类都被编码，编码内容包括`类名和类签名、对象的字段值和数组`值，以及`从初始对象中引用的其他所有对象的闭包`。  
  
writeObject 方法用于将对象写入流中。所有对象（包括 String 和数组）都可以通过 writeObject 写入。可将多个对象或基元写入流中。必须使用与写入对象时相同的`类型和顺序`从相应 ObjectInputstream 中读回对象。  
  
还可以使用 DataOutput 中的适当方法将`基本数据类型`写入流中。还可以使用 writeUTF 方法写入`字符串`。  
  
对象的默认序列化机制写入的内容是：对象的类，类签名，以及`非瞬态和非静态字段`的值。其他对象的引用(瞬态和静态字段除外)也会导致写入那些对象。可使用`引用共享机制`对单个对象的多个引用进行编码，这样即可将对象的图形恢复为最初写入它们时的形状。  
  
**继承关系**  
- class ObjectOutputStream extends OutputStream implements ObjectOutput, ObjectStreamConstants  
- 所有已实现的接口：Closeable, DataOutput, Flushable, ObjectOutput, ObjectStreamConstants  
- 接口 ObjectStreamConstants 中没有定义任何方法，但是定义了很多常量  
  
**构造方法**  
- `ObjectOutputStream(OutputStream out)`  创建写入指定 OutputStream 的 ObjectOutputStream。  
  
**接口 ObjectOutput 和 DataOutput 中的方法**  
  
**其他方法**  
- `void    writeFields()`  将已缓冲的字段写入流中。  
- `void    writeUnshared(Object obj)`  将“未共享”对象写入 ObjectOutputStream。  
- `void    defaultWriteObject()`  将当前类的非静态和非瞬态字段写入此流。  
- `ObjectOutputStream.PutField putFields()`  获取用于缓冲写入流中的持久存储字段的对象。  
- `void    reset()`  重置将丢弃已写入流中的所有对象的状态。  
- `void    useProtocolVersion(int version)`  指定要在写入流时使用的流协议版本。  
  
## 测试代码  
```java  
public static void objectStream() {  
    try {  
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_PATH));  
        outputStream.writeInt(10086); //可以使用 DataOutput 中的适当方法将基本数据类型写入流中  
        outputStream.writeUTF("UTF格式"); //可以使用 writeUTF 方法写入字符串  
        outputStream.writeObject("也是对象"); //字符串也可以当做对象来写入  
          
        Person writePerson = new Person("包青天", 28, new Person.Work("小米", 25000));  
        outputStream.writeObject(writePerson);//所有引用到的类都必须实现了Serializable接口，否则会报 NotSerializableException  
        outputStream.flush();  
          
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(FILE_PATH));  
        Log.i("bqt", inputStream.readInt() + "," + inputStream.readUTF() + "," + inputStream.readObject()); //10086,UTF格式,也是对象  
          
        Person readPerson = (Person) inputStream.readObject();//将字节重建成一个对象。注意，这个对象和之前保存的对象不是同一个对象！  
        Log.i("bqt", (writePerson == readPerson) + "，" + (writePerson.equals(readPerson))); //false，false  
        Log.i("bqt", writePerson.toString() + "\n" + readPerson.toString()); //所有字段的值都是相同的，但引用都是不同的！  
          
        outputStream.close();  
        inputStream.close();  
    } catch (Exception e) {  
        e.printStackTrace();  
    }  
}  
```  
  
# 随机访问文件读写 RandomAccessFile  
## 介绍  
此类的实例支持对`随机访问文件`的`读取和写入`。随机访问文件的行为类似存储在文件系统中的一个`大型 byte 数组`。存在指向该隐含数组的光标或索引，称为`文件指针`；输入操作从文件指针开始读取字节，并随着对字节的读取而前移此文件指针。如果随机访问文件以读取/写入模式创建，则输出操作也可用；输出操作从文件指针开始写入字节，并随着对字节的写入而前移此文件指针。写入隐含数组的当前末尾之后的输出操作导致该数组`扩展`。该文件指针可以通过 `getFilePointer` 方法读取，并通过 `seek` 方法设置。  
  
随机访问文件RandomAccessFile不是io体系中的子类，可以认为是一个io`工具类`，其特点有：  
- 该对象即能读、又能写。  
- 该对象内部维护了一个byte数组，并通过指针可以操作数组中的元素。  
- 可以通过getFilePointer方法获取指针的位置，和通过seek方法设置指针的位置。  
- 其实该对象就是将字节输入流和输出流进行了封装。  
- 该对象的源或者目的只能是文件。  
  
**继承关系**  
- class RandomAccessFile implements DataOutput, DataInput, Closeable  
- 所有已实现的接口：Closeable, DataInput, DataOutput  
  
**构造方法**  
- `RandomAccessFile(File file, String mode)` 创建从中读取和向其中写入（可选）的随机访问文件流，该文件由 File 参数指定。  
- `RandomAccessFile(String name, String mode)` 创建从中读取和向其中写入（可选）的随机访问文件流，该文件具有指定名称。  
  
**可选的模式**  
- "r"   以只读方式打开。调用结果对象的任何 write 方法都将导致抛出 IOException。  
- "rw"  打开以便读取和写入。如果该文件尚不存在，则尝试创建该文件。  
- "rws" 打开以便读取和写入，对于 "rw"，还要求对文件的`内容或元数据`的每个更新都`同步`写入到底层存储设备。  
- "rwd" 打开以便读取和写入，对于 "rw"，还要求对文件`内容`的每个更新都`同步`写入到底层存储设备。  
  
## 测试代码  
```java  
public static void randomAccessFile() {  
    try {  
        switch (count % 3) {  
            case 0:  
                randomTest();  
                break;  
            case 1:  
                byte[] content = "什么是最重要的，什么是最紧迫的，什么是最有价值的，什么是最有意义的。".getBytes();  
                multiDownload(content, new Random().nextInt(10) + 1);  
                break;  
            case 2:  
                RandomAccessFile accessFile = new RandomAccessFile(FILE_PATH, "rw");  
                byte[] bytes = new byte[(int) accessFile.length()];  
                accessFile.read(bytes);  
                System.out.println("完整内容为：" + new String(bytes));  
                break;  
            default:  
                break;  
        }  
    } catch (Exception e) {  
        e.printStackTrace();  
    }  
}  
  
private static void randomTest() {  
    try {  
        new File(FILE_PATH).delete();  
        RandomAccessFile accessFile = new RandomAccessFile(FILE_PATH, "rw");//文件不存在自动创建  
        accessFile.seek(0);  
        accessFile.writeInt(10086);  
        accessFile.seek(6);//从指定位置开始读取  
        accessFile.writeChar('哀');  
        accessFile.writeUTF("塞翁失马");  
        accessFile.writeChars("哀叹");  
          
        accessFile.seek(0);  
        byte[] bytes = new byte[(int) accessFile.length()];  
        accessFile.read(bytes);  
        System.out.println("完整内容为：" + new String(bytes)); //����'f����T���塞翁失马T�S�  
          
        accessFile.seek(0);  
        Log.i("bqt", "" + accessFile.readInt()); //10086  
        accessFile.seek(6);//从指定位置开始读取  
        Log.i("bqt", accessFile.readChar() + "," + accessFile.readUTF()); //哀,塞翁失马  
        Log.i("bqt", new String(new char[]{accessFile.readChar(), accessFile.readChar()})); //哀叹  
        accessFile.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
  
private static void multiDownload(byte[] content, int number) {  
    new File(FILE_PATH).delete();  
    try {  
        RandomAccessFile accessFile = new RandomAccessFile(FILE_PATH, "rw");  
        accessFile.setLength(content.length); //预分配文件所占的磁盘空间，磁盘中会创建一个指定大小的文件  
        accessFile.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
      
    int partLength = content.length / number; //每一部分复制的最大长度  
    for (int i = 0; i <= number; i++) {  
        final int skipPos = i * partLength;  
        int length = (content.length - skipPos) > partLength ? partLength : (content.length - skipPos);  
        if (length > 0) {  
            byte[] subContent = new byte[length];  
            System.arraycopy(content, skipPos, subContent, 0, length);  
            // 从源数组 content 的 skipPos 位置开始复制，复制到 subContent 的 0 位置，当复制 length 个后结束复制  
            new Thread(() -> download(FILE_PATH, skipPos, subContent)).start();//利用多线程同时写入一个文件  
        }  
    }  
}  
  
private static void download(String filepath, long skipPos, byte[] content) {  
    try {  
        RandomAccessFile accessFile = new RandomAccessFile(filepath, "rw");  
        accessFile.seek(skipPos);  
        accessFile.write(content);  
        accessFile.close();  
        Log.i("bqt", "已复制【" + new String(content) + "】到文件中");  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
# 合并输入流 SequenceInputStream  
有些情况下，我们需要从多个输入流中向程序读入数据，此时，可以使用合并流，合并流的作用是将多个源合并成一个源。  
  
在操作上，该类从第1个InputStream对象进行读取，直到读取完全部内容，然后切换到第2个InputStream对象。对于使用Enumeration对象的情况，该类将持续读取所有InputStream对象中的内容，直到到达最后一个InputStream对象的末尾为止。  
  
当到达每个InputStream对象的末尾时，与之关联的流就会被关闭。  
关闭通过SequenceInputStream创建的流，会导致关闭所有未关闭的流。  
  
**构造方法**  
- `SequenceInputStream(Enumeration<? extends InputStream> e)` 通过记住参数来初始化新创建的 SequenceInputStream，该参数必须是生成运行时类型为 InputStream 对象的 Enumeration 型参数。将按顺序读取由该枚举生成的输入流，以提供从此 SequenceInputStream 读取的字节。在用尽枚举中的每个输入流之后，将通过调用该流的 close 方法将其关闭。  
- `SequenceInputStream(InputStream s1, InputStream s2)` 通过记住这两个参数来初始化新创建的 SequenceInputStream（将按顺序读取这两个参数，先读取 s1，然后读取 s2），以提供从此 SequenceInputStream 读取的字节。  
  
**常用的几个方法**  
-  `int available()` 返回不受阻塞地从当前底层输入流读取（或跳过）的字节数的估计值，方法是通过下一次调用当前底层输入流的方法。此方法仅调用当前底层输入流的 available 方法并返回结果。  
-  `void close()` 关闭此输入流并释放与此流关联的所有系统资源。关闭的 SequenceInputStream 无法执行输入操作，且无法重新打开。  
-  `int read()` 从此输入流中读取下一个数据字节。返回 0 到 255 范围内的 int 字节。如果因为已经到达流的末尾而没有可用的字节，则返回值 -1。在输入数据可用、检测到流的末尾或者抛出异常之前，此方法一直阻塞。  
-  `int read(byte[] b, int off, int len)` 将最多 len 个数据字节从此输入流读入 byte 数组。  
- 从类 java.io.InputStream 继承的方法：mark, markSupported, read, reset, skip  
  
**案例：文件切割与合并**  
文件切割：  
```java  
splitFile(new File("d:/卖火柴的小女孩.mp3"), new File("d:/split"), 1024 * 1024);  
```  
```java  
public static void splitFile(File source, File saveDir, int maxLength) {  
    if (!saveDir.exists()) {  
        System.out.println("创建目录：" + saveDir.mkdirs());  
    }  
    try {  
        FileInputStream inputStream = new FileInputStream(source);  
        byte[] buf = new byte[maxLength];  
        int len;  
        int count = 0;  
        while ((len = inputStream.read(buf)) != -1) {  
            String fileName = source.getName() + "-part" + (++count);  
              
            FileOutputStream outputStream = new FileOutputStream(new File(saveDir, fileName));  
            outputStream.write(buf, 0, len);  
            outputStream.flush();  
            outputStream.close();  
              
        }  
        inputStream.close();  
        System.out.println("切割完成");  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
文件合并  
```java  
mergeFile(new File("d:/split").listFiles(), new File("d:/卖火柴的小女孩2.mp3"));  
```  
```java  
public static void mergeFile(File[] splitFiles, File mergedFile) {  
    try {  
        Arrays.sort(splitFiles); //默认是按升序排序的，如数字类型按照 数字由小到大 的顺序，字符串按照 abcd 的顺序  
        System.out.println("要合并的文件列表：" + Arrays.toString(splitFiles));  
        ArrayList<FileInputStream> inputStreams = new ArrayList<>();  
        for (File file : splitFiles) {  
            inputStreams.add(new FileInputStream(file));  
        }  
        SequenceInputStream inputStream = new SequenceInputStream(Collections.enumeration(inputStreams));  
        FileOutputStream outputStream = new FileOutputStream(mergedFile);  
        byte[] buf = new byte[1024];  
        int len;  
        while ((len = inputStream.read(buf)) != -1) {  
            outputStream.write(buf, 0, len);  
        }  
        outputStream.close();  
        inputStream.close();  
        System.out.println("合并完成");  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
# 文本扫描工具 Scanner  
`java.util.Scanner`是 Java5 的新特征，主要功能是简化文本扫描。  
这个类最实用的地方表现在`获取控制台输入`，其他的功能都很鸡肋，尽管 Java API 文档中列举了大量的 API 方法，但是都不怎么地。  
  
当通过`new Scanner(System.in)`创建一个Scanner，控制台会一直等待输入，直到敲回车键结束，把所输入的内容传给Scanner，作为扫描对象。如果要获取输入的内容，则只需要调用Scanner的`nextLine()`或`next()`方法即可。  
  
**常用的几个方法**  
- useDelimiter(String pattern) 指定新的匹配分隔符的 Pattern  
- delimiter() 返回此 Scanner 当前正在用于匹配分隔符的 Pattern  
- hasNext() 判断扫描器中当前扫描位置后是否还存在下一段  
- hasNextLine() 如果在此扫描器的输入中存在另一行，则返回 true  
- next()  查找并返回来自此扫描器的下一个完整标记  
- nextLine() 此扫描器执行当前行，并返回跳过的输入信息  
  
**next()方法的特点**  
- 直到读取到有效字符后才结束输入  
- 只有输入有效字符后才将其后面输入的空白作为分隔符或者结束符  
- 对输入有效字符之前遇到的空白会自动将其去掉  
- 不能得到带有空格的字符串  
  
**nextLine()方法的特点**  
- 以Enter为结束符，也就是说 nextLine()方法返回的是输入回车之前的所有字符  
- 可以获得空白符  
  
> 注意：如果要输入 int 或 float 类型的数据，在 Scanner 类中也有支持，但是在输入之前最好先使用 `hasNextXxx()` 方法进行验证，再使用 `nextXxx()` 来读取。  
  
**案例一**  
```java  
public static void scannerTest(Scanner scanner) {  
    System.out.println(scanner.delimiter());  
    System.out.println("开始输入吧");  
    while (scanner.hasNext()) {  
        String text = scanner.next();  
        System.out.println("你输入了：" + text);  
        if ("88".equals(text)) {  
            scanner.close();  
            break;  
        }  
    }  
    System.out.println("结束");  
}  
```  
  
  
场景1：  
```java  
scannerTest(new Scanner(System.in));  
```  
```java  
\p{javaWhitespace}+  
开始输入吧  
1  
你输入了：1  
2  
你输入了：2  
88  
你输入了：88  
结束  
```  
  
场景2：  
```java  
scannerTest(new Scanner("1 2  3-4\n5\t67"));  
```  
```java  
\p{javaWhitespace}+  
开始输入吧  
你输入了：1  
你输入了：2  
你输入了：3-4  
你输入了：5  
你输入了：67  
结束  
```  
  
场景3：  
```java  
Scanner scanner = new Scanner("1 2  3-4\n5\t67");  
scanner.useDelimiter("-");//指定新的分隔符  
scannerTest(scanner);  
```  
```java  
-  
开始输入吧  
你输入了：1 2  3  
你输入了：4  
5    67  
结束  
```  
  
**案例二**  
```java  
public static void getInputText() {  
    String account;  
    String password;  
    Scanner scanner = new Scanner(System.in);  
    while (true) {  
        System.out.println("请输入你的账号：");  
        account = scanner.nextLine();  
        System.out.println("请输入你的密码：");  
        password = scanner.nextLine();  
        if ("1".equals(account) && "1".equals(password)) {  
            System.out.println("欢迎光临");  
            scanner.close();  
            break;  
        } else {  
            System.out.println("账号密码错误：" + account + "，" + password);  
        }  
    }  
    System.out.println("结束");  
}  
```  
  
以上使用 `nextLine` 时的效果：  
```java  
请输入你的账号：  
12 34  
请输入你的密码：  
5  
账号密码错误：12 34，5  
请输入你的账号：  
```  
  
以上使用 `next` 时的效果：  
```java  
请输入你的账号：  
12 34  
请输入你的密码：  
账号密码错误：12，34  
请输入你的账号：  
```  
  
# 系统输入输出流 System.in/out/err  
`System.in, System.out, System.err`这3个流是System类中的静态成员，并且已经预先在JVM启动的时候初始化完成：  
- `System.in`是一个连接控制台程序和`键盘`输入的`InputStream`流。注意：其`read()`方法是一个`阻塞`式方法。  
- `System.out`是一个`PrintStream`流(`OutputStream`的间接子类)。System.out一般会把你写到其中的数据输出到`控制台`上。  
- `System.err`是一个`PrintStream`流。System.err与System.out的运行方式类似，但它更多的是用于`打印错误文本`。  
  
定义：  
- `public static final InputStream in`：“标准”输入流。此流已打开并准备提供输入数据。通常，此流对应于键盘输入或者由主机环境或用户指定的另一个输入源。  
- `public static final PrintStream out`：“标准”输出流。此流已打开并准备接受输出数据。通常，此流对应于显示器输出或者由主机环境或用户指定的另一个输出目标。  
- `public static final PrintStream err`：“标准”错误输出流。此流已打开并准备接受输出数据。  
- 通常，此流对应于显示器输出或者由主机环境或用户指定的另一个输出目标。按照惯例，此输出流用于显示错误消息，或者显示那些即使用户输出流（变量 out 的值）已经重定向到通常不被连续监视的某一文件或其他目标，也应该立刻引起用户注意的其他信息。  
  
可以使用`System.setIn()、System.setOut()、System.setErr()`方法设置新的系统流，之后的数据都将会在新的流中进行读写。  
这三个方法均为静态方法，内部调用了本地`native`方法重新设置系统流。  
  
如：  
```java  
try {  
    System.setOut(new PrintStream(new FileOutputStream("d:\\system.out.txt")));  
} catch (FileNotFoundException e) {  
    e.printStackTrace();  
}  
System.out.println("不会在控制台打印，而会输出到文件中");  
```  
请务必在 JVM 关闭之前调用`flush()`方法冲刷 System.out，确保 System.out 把数据输出到了文件中。  
  
2018-11-24  
