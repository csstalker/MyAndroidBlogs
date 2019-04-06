| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
文件 File 常见操作 工具  
***  
目录  
===  

- [文件基本操作](#文件基本操作)
	- [创建文件或目录](#创建文件或目录)
	- [重命名](#重命名)
	- [创建多级目录](#创建多级目录)
	- [创建带目录的文件](#创建带目录的文件)
	- [删除多级目录](#删除多级目录)
	- [获取目录下的文件列表](#获取目录下的文件列表)
	- [其他常见操作](#其他常见操作)
- [常用封装的工具](#常用封装的工具)
	- [递归遍历目录下的文件](#递归遍历目录下的文件)
	- [递归删除目录下的文件](#递归删除目录下的文件)
	- [生成格式良好的文件目录](#生成格式良好的文件目录)
		- [效果演示](#效果演示)
		- [代码](#代码)
		- [配置 Config](#配置-Config)
	- [格式化文件大小](#格式化文件大小)
- [文件复制的几种方式](#文件复制的几种方式)
	- [速度比较](#速度比较)
	- [copyFileByBuffer](#copyFileByBuffer)
	- [copyFileByByteArray](#copyFileByByteArray)
	- [copyFileByByteArray2](#copyFileByByteArray2)
	- [copyFileByByte](#copyFileByByte)
	- [copyFileByByteOnce](#copyFileByByteOnce)
	- [copyFileByBufLine](#copyFileByBufLine)
	- [copyFileByCharArray](#copyFileByCharArray)
	- [copyFileByChar](#copyFileByChar)
# 文件基本操作  
## 创建文件或目录  
```java  
private static void testCreate() {  
    File file = new File("d:/不存在的文件.txt");//注意，这一步并没有在磁盘创建文件，只是封装了一个对象  
    System.out.println("不存在的文件：" + (file.exists() || file.isFile() || file.isDirectory()));//【false】文件不存在时既不是 isFile 也不是 isDirectory  
    try {  
        System.out.println(file.createNewFile());//【true】这一步是在磁盘中创建文件  
        System.out.println(file.exists() + "--" + file.isFile() + "--" + file.isDirectory());//【true--true--false】  
        System.out.println(file.createNewFile() || file.mkdirs());//【false】如果文件已存在，则不创建  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
  
    File dir = new File("d:/不存在的目录");  
    System.out.println("不存在的目录：" + (dir.exists() || dir.isFile() || dir.isDirectory()));//【false】  
    System.out.println(dir.mkdirs());//【true】创建目录  
    System.out.println(dir.exists() + "--" + dir.isFile() + "--" + dir.isDirectory());//【true--false--true】  
    try {  
        System.out.println(dir.mkdirs() || dir.createNewFile());//【false】如果目录已存在，则不创建  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
## 重命名  
```java  
private static void testRename() {  
    File dir = new File("d:/目录");  
    System.out.println("原目录下的文件列表：" + Arrays.toString(dir.list())); //[文件.txt]  
    File renameDir = new File(dir.getParent(), "重命名目录");  
    //return  true if and only if the renaming succeeded; false otherwise  
    boolean succee = dir.renameTo(renameDir);//重命名时须指定重命名后存放的路径，否则会移动到默认路径  
    System.out.println("重命名是否成功：" + succee + "，原目录是否还存在：" + dir.exists()); //【true，false】  
    System.out.println("新目录下的文件列表：" + Arrays.toString(renameDir.list())); //[文件.txt]  
}  
```  
  
## 创建多级目录  
```java  
private static void testCreateDir() {  
    File dir1 = new File("d:/a/aa/1");  
    File dir2 = new File("d:/a/aa");  
    File dir3 = new File("d:/a/");  
    //mkdir 只创建最后一级目录，若父目录不存在则创建失败(但不报异常)；mkdirs 可以同时创建多级目录；创建新目录时不会删除旧目录  
    //return true if and only if the directory was created, along with all necessary parent directories最后一级目录创建成功则返回true  
    System.out.println(dir1.mkdir() || dir2.mkdir() || dir1.exists() || dir2.exists() || dir2.exists());//【false】  
    System.out.println(dir1.mkdirs() && dir1.exists() && dir2.exists() && dir3.exists());//【true】  
    System.out.println(dir1.mkdirs() || dir1.mkdir() || dir2.mkdirs() || dir2.mkdir() || dir3.mkdirs() || dir3.mkdir());//【false】  
}  
```  
  
## 创建带目录的文件  
```java  
private static void testCreateFileWithDir() {  
    boolean success;  
    try {  
        //return true if the named file does not exist and was successfully created; false if the named file already exists  
        success = new File("d:/aaa/aaa.txt").createNewFile();//目录不存在时调用 createNewFile 方法会报异常  
        System.out.println("创建文件是否成功：" + success);  
    } catch (IOException e) {  
        e.printStackTrace();  
        System.out.println("创建文件出现异常，往往是因为父目录不存在");  
    }  
      
    //创建带目录的文件的标准姿势  
    File file = new File("d:/aaa/aaa.txt");  
    if (!file.getParentFile().exists()) {  
        success = file.getParentFile().mkdirs();//如果父目录不存在则先创建父目录  
        System.out.println("父目录是否创建成功：" + success);  
    }  
    try {  
        System.out.println("创建文件是否成功：" + file.createNewFile());  
    } catch (IOException e) {  
        e.printStackTrace();  
        System.out.println("创建文件出现异常，往往是因为父目录不存在");  
    }  
}  
```  
  
## 删除多级目录  
```java  
private static void testDelDir() {  
    File dir1 = new File("d:/b/删除/1");  
    File dir2 = new File("d:/b/删除");  
    File dir3 = new File("d:/b");  
    System.out.println(dir1.mkdirs() && dir1.exists() && dir2.exists() && dir3.exists());//【true】  
      
    //如果有目录下有内容(不管是目录还是文件)，则不能此目录不能被删除  
    System.out.println(dir3.delete() + "--" + dir2.delete() + "--" + dir3.delete());//【false--false--false】  
      
    //如果目录中无内容，则可以删除此目录(仅仅是删掉最里层的那一个目录)，所以文件(isFile()为true)是可以直接删除的  
    System.out.println(dir1.delete() + "--" + dir2.delete() + "--" + dir3.delete());//【true--true--true】  
    System.out.println(dir1.exists() || dir2.exists() || dir3.exists());//【false】  
}  
```  
  
## 获取目录下的文件列表  
```java  
private static void testList() {  
    File[] rootDirs = File.listRoots();  
    System.out.println("获取系统根目录的文件列表(磁盘列表)\n" + Arrays.toString(rootDirs));//[C:\, D:\]  
      
    File file = new File("d:/一个不存在的目录");  
    //如果 file 不是一个目录，包括：①是一个文件 ②既不是文件也不是目录(即 file 不存在)，则所有相关的 list 操作都返回 null  
    //如果 file 是一个目录(是一个目录的必要条件为：此目录一定是在磁盘中存在的)但目录下面没有任何文件，则返回一个长度为 0 的数组  
    //注意，所有 list 操作获取的列表中，都包含此目录下的所有文件以及文件夹(包含隐藏文件)，但不包括文件夹下面的子目录(不递归)  
    System.out.println("\n必须保证是一个目录\n" + (!file.isDirectory() && file.list() == null && file.listFiles() == null));//【true】  
      
    file = new File("d:/");  
    System.out.println("\n获取【文件名称】列表\n" + Arrays.toString(file.list()));//String 类型数组  
      
    String[] txtFileNames = file.list((dir, name) -> name.endsWith(".txt") && new File(dir, name).length() > 5 * 1024);  
    System.out.println("\n带过滤功能\n" + Arrays.toString(txtFileNames));  
      
    File[] mp3Files = file.listFiles((dir, name) -> name.endsWith(".mp3") && new File(dir, name).length() > 5 * 1024);//File 类型数组  
    System.out.println("\n获取【文件】列表\n" + Arrays.toString(mp3Files));  
}  
```  
  
## 其他常见操作  
```java  
private static void printFileInfo(File file) {  
    System.out.println("------------------系统相关的常量------------------");  
    System.out.println("separatorChar：" + File.separatorChar);//【\】  
    System.out.println("separator：" + File.separator);//【\】最常用的  
    System.out.println("pathSeparatorChar：" + File.pathSeparatorChar);//【;】  
    System.out.println("pathSeparator：" + File.pathSeparator);//【;】  
  
    System.out.println("\n------------------基本信息------------------");  
    System.out.println("名称：" + file.getName());  
    System.out.println("类型：" + (file.isFile() ? "文件" : (file.isDirectory() ? "目录" : "文件不存在")));  
    System.out.println("大小：" + FileUtils.getDataSize(file.length()));  
    System.out.println("上次修改时间：" + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(file.lastModified())));  
  
    System.out.println("\n------------------属性信息------------------");  
    System.out.println("是否存在：" + file.exists());  
    System.out.println("是否隐藏：" + file.isHidden());  
    System.out.println("是否可执行：" + file.canExecute());  
    System.out.println("是否可读写：" + (file.canRead() ? "可读" : "不可读") + "-" + (file.canWrite() ? "可写" : "不可写"));  
  
    System.out.println("\n------------------路径信息------------------");  
    System.out.println("路径：" + file.getPath());//这里的 Path 指的是 new File() 时传进去的路径，可能为相对路径，也可能为绝对路径  
    System.out.println("是否为绝对路径：" + file.isAbsolute());//在Windows 上，如果路径名的前缀是后跟 "\\" 的盘符，或者是 "\\\\"，那么该路径名是绝对路径名  
    System.out.println("绝对路径：" + file.getAbsolutePath());//【e:\】  
    System.out.println("父路径：" + file.getParent());//如果没有父目录，比如【e:】，则返回 null  
    try {  
        System.out.println("规范路径：" + file.getCanonicalPath());//【E:\】  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
  
    System.out.println("\n------------------磁盘信息------------------");  
    //注意，对于任何有效的文件，下列操作获取的都是此文件所属【磁盘】的容量，而非此【目录或文件】的容量  
    System.out.println("所在磁盘未分配容量：" + FileUtils.getDataSize(file.getUsableSpace()));  
    System.out.println("所在磁盘可用容量：" + FileUtils.getDataSize(file.getFreeSpace()));  
    System.out.println("所在磁盘容量：" + FileUtils.getDataSize(file.getTotalSpace()));  
  
    System.out.println("\n------------------延伸信息------------------");  
    System.out.println("toString：" + file.toString());//toString 返回的内容就是 getPath  
    System.out.println("URI：" + file.toURI().toString());//【file:/e:/】  
    System.out.println("Path：" + file.toPath().toString());//【e:\】  
    System.out.println("根目录：" + file.toPath().getRoot());//【e:\】  
    System.out.println("父目录层级：" + file.toPath().getNameCount());//【2】代表有几级父目录  
}  
```  
  
# 常用封装的工具  
## 递归遍历目录下的文件  
调用案例：  
```java  
listDirFiles(file, null, true);  
```  
```java  
FilenameFilter filter = (dir, name) -> name.endsWith(".md") && new File(dir, name).length() > 10 * 1024;  
listDirFiles(file, filter, false);  
```  
  
```java  
/**  
 * 获取指定目录及其子目录下的指定格式文件的文件  
 *  
 * @param dirFile      要遍历的目录，必须是一个目录  
 * @param filter       对文件的过滤规则(不作用于目录)，如果要遍历所有文件请设为 null  
 * @param isContainDir 遍历的结果中是否包含目录本身  
 */  
public static List<File> listDirFiles(File dirFile, FilenameFilter filter, boolean isContainDir) {  
    if (dirFile == null || !dirFile.isDirectory()) {  
        System.out.println("不是一个目录");  
        return null;  
    }  
    List<File> files = new ArrayList<>();  
    getDirFiles(files, dirFile, filter, isContainDir);  
    for (File file : files) {  
        System.out.println(file.getName());  
    }  
    System.out.println("------------------------【遍历完成，文件总数：" + files.size() + "】------------------------");  
    return files;  
}  
```  
  
```java  
/**  
 * 对指定目录中的文件进行深度遍历，并按照指定过滤器进行过滤，将过滤后的内容存储到一个指定的集合中  
 *  
 * @param fileList     将结果保存到指定的集合中。由于要递归遍历，不能定义为局部变量，否则每次递归时都是把结果放到了一个新的集合中  
 * @param dirFile      要遍历的目录，必须是一个目录  
 * @param filter       对文件的过滤规则(不作用于目录)，如果要遍历所有文件请设为 null  
 * @param isContainDir 遍历的结果中是否包含目录本身  
 */  
private static void getDirFiles(List<File> fileList, File dirFile, FilenameFilter filter, boolean isContainDir) {  
    if (dirFile == null || !dirFile.isDirectory()) {  
        return;  
    }  
    for (File file : dirFile.listFiles()) {//也可以使用listFiles()在获取列表时直接过滤，注意这种方式检索时不要遗漏了目录文件  
        if (file.isDirectory()) {  
            if (isContainDir) {  
                fileList.add(file);  
            }  
            getDirFiles(fileList, file, filter, isContainDir);//递归  
        } else { //这里不需要判断是否为文件了，因为 listFiles 中的要么是 isDirectory 要么是 isFile  
            if (filter == null || filter.accept(dirFile, file.getName())) {//是否满足过滤规则  
                fileList.add(file);  
            }  
        }  
    }  
}  
```  
  
## 递归删除目录下的文件  
调用案例：  
```java  
deleateFiles(file, null, true);  
```  
```java  
FilenameFilter filter = (dir, name) -> name.endsWith(".md") && new File(dir, name).length() > 10 * 1024;  
deleateFiles(file, filter, false);  
```  
  
```java  
/**  
 * 删除一个文件，或删除一个目录下的所有文件  
 *  
 * @param dirFile     要删除的目录，可以是一个文件  
 * @param filter       对要删除的文件的匹配规则(不作用于目录)，如果要删除所有文件请设为 null  
 * @param isDeleateDir 是否删除目录，false时只删除目录下的文件而不删除目录  
 */  
public static void deleateFiles(File dirFile, FilenameFilter filter, boolean isDeleateDir) {  
    if (dirFile.isDirectory()) {//是目录  
        for (File file : dirFile.listFiles()) {  
            deleateFiles(file, filter, isDeleateDir);//递归  
        }  
        if (isDeleateDir) {  
            System.out.println("目录【" + dirFile.getAbsolutePath() + "】删除" + (dirFile.delete() ? "成功" : "失败"));//必须在删除文件后才能删除目录  
        }  
    } else if (dirFile.isFile()) {//是文件。注意 isDirectory 为 false 并非就等价于 isFile 为 true  
        String symbol = isDeleateDir ? "\t" : "";  
        if (filter == null || filter.accept(dirFile.getParentFile(), dirFile.getName())) {//是否满足匹配规则  
            System.out.println(symbol + "- 文件【" + dirFile.getAbsolutePath() + "】删除" + (dirFile.delete() ? "成功" : "失败"));  
        } else {  
            System.out.println(symbol + "+ 文件【" + dirFile.getAbsolutePath() + "】不满足匹配规则，不删除");  
        }  
    } else {  
        System.out.println("文件不存在");  
    }  
}  
```  
  
## 生成格式良好的文件目录  
### 效果演示  
最简单的使用方式：  
```java  
listDirFilesNames(file, Config.newBuilder().build());  
```  
效果：  
```  
1 D:\博客\Java  
    1.1 D:\博客\Java\Openfire XMPP Smack RTC IM 即时通讯 聊天 MD.md  
    1.2 D:\博客\Java\Proxy 动态代理 InvocationHandler CGLIB MD.md  
2 D:\博客\RxJava  
    2.1 D:\博客\RxJava\RxJava RxBinding RxView 控件事件 MD.md  
3 D:\博客\UI  
    3.1 D:\博客\UI\Glide 缓存策略 内存缓存 磁盘缓存 图片加载 MD.md  
    3.2 D:\博客\UI\自定义View  
        3.2.1 D:\博客\UI\自定义View\自定义Toast 土司 MD.md  
4 D:\博客\优化  
    4.1 D:\博客\优化\LeakCanary 内存泄漏 监测 性能优化 简介 原理 MD.md  
------------------------【文件总数：48】------------------------  
```  
  
可以定制的内容：  
```java  
Config config = Config.newBuilder().format(true).showNum(false).maxLeval(2).filter(filter).space("\t").build();  
listDirFilesNames(file, config);  
```  
效果：  
```java  
 D:\博客\Java  
     D:\博客\Java\Openfire XMPP Smack RTC IM 即时通讯 聊天 MD.md  
     D:\博客\Java\Proxy 动态代理 InvocationHandler CGLIB MD.md  
 D:\博客\RxJava  
     D:\博客\RxJava\RxJava RxBinding RxView 控件事件 MD.md  
 D:\博客\UI  
     D:\博客\UI\Glide 缓存策略 内存缓存 磁盘缓存 图片加载 MD.md  
     D:\博客\UI\自定义View  
 D:\博客\优化  
     D:\博客\优化\LeakCanary 内存泄漏 监测 性能优化 简介 原理 MD.md  
------------------------【文件总数：42】------------------------  
```  
  
### 代码  
```java  
/**  
 * 获取指定目录及其子目录下的指定格式文件的文件名，并对文件名进行格式化  
 */  
public static List<String> listDirFilesNames(File dirFile, Config config) {  
    List<String> list = new ArrayList<>();  
    getDirFormatFilesNames(list, dirFile, config, 0, null);  
    for (String path : list) {  
        System.out.println(path);  
    }  
    System.out.println("------------------------【文件总数：" + list.size() + "】------------------------");  
    return list;  
}  
```  
  
```java  
/**  
 * 获取指定目录及其子目录下的指定格式文件的文件名，并对文件名进行格式化，并存储到一个指定的集合中  
 *  
 * @param filePathList 将结果保存到指定的集合中  
 * @param dirFile      要遍历的目录，必须是一个目录  
 */  
private static void getDirFormatFilesNames(List<String> filePathList, File dirFile, Config config, int curLeval, String indexBegin) {  
    if (dirFile == null || !dirFile.isDirectory()) {  
        System.out.println("不是一个目录");  
        return;  
    }  
      
    if (curLeval < config.maxLeval) {  
        curLeval++;  
        File[] files = dirFile.listFiles();  
        StringBuilder filePath;  
        for (int i = 0; i < files.length; i++) {  
            filePath = new StringBuilder();  
            if (config.format) {//格式化  
                for (int j = 1; j < curLeval; j++) {  
                    filePath.append(config.space);//缩进  
                }  
            }  
            String curIndex = config.showNum ? (indexBegin == null || indexBegin.equals("") ? (i + 1) + "" : (indexBegin + "." + (i + 1))) : ""; //序号  
            filePath.append(curIndex).append(" ").append(files[i].getAbsolutePath());//路径  
              
            if (files[i].isDirectory()) {//目录，递归  
                filePathList.add(filePath.toString());  
                getDirFormatFilesNames(filePathList, files[i], config, curLeval, curIndex);//递归  
            } else if (config.filter == null || config.filter.accept(dirFile, files[i].getName())) {//文件，判断是否满足过滤规则  
                filePathList.add(filePath.toString());  
            }  
        }  
    }  
}  
```  
  
### 配置 Config  
```java  
public class Config {  
    public FilenameFilter filter; //只遍历目录中的指定类型文件，如果要遍历所有文件请设为null  
    public boolean format; //是否格式化  
    public boolean showNum;//是否显示编号  
    public int maxLeval;//要遍历的最大层级(多少层目录)  
    public String space; //每一级目录添加的符号，format为true时有效  
      
    private Config(Builder builder) {  
        filter = builder.filter;  
        format = builder.format;  
        showNum = builder.showNum;  
        maxLeval = builder.maxLeval;  
        space = builder.space;  
    }  
      
    public static Builder newBuilder() {  
        return new Builder();  
    }  
      
    public static final class Builder {  
        private FilenameFilter filter = null;  
        private boolean format = true;  
        private boolean showNum = true;  
        private int maxLeval = Integer.MAX_VALUE;  
        private String space = " |____";  
          
        private Builder() {  
        }  
          
        public Builder filter(FilenameFilter val) {  
            filter = val;  
            return this;  
        }  
          
        public Builder format(boolean val) {  
            format = val;  
            return this;  
        }  
          
        public Builder showNum(boolean val) {  
            showNum = val;  
            return this;  
        }  
          
        public Builder maxLeval(int val) {  
            maxLeval = val;  
            return this;  
        }  
          
        public Builder space(String val) {  
            space = val;  
            return this;  
        }  
          
        public Config build() {  
            return new Config(this);  
        }  
    }  
}  
```  
  
## 格式化文件大小  
```java  
/**   
 * 格式化文件大小  
 * @param size 文件大小   
 */  
public static String getDataSize(long size) {  
    DecimalFormat formater = new DecimalFormat("####.00");  
    if (size < 1024) return size + "B";  
    else if (size < Math.pow(1024, 2)) return formater.format(size * Math.pow(1024, -1)) + "KB";  
    else if (size < Math.pow(1024, 3)) return formater.format(size * Math.pow(1024, -2)) + "MB";  
    else if (size < Math.pow(1024, 4)) return formater.format(size * Math.pow(1024, -3)) + "GB";  
    else if (size < Math.pow(1024, 5)) return formater.format(size * Math.pow(1024, -4)) + "TB";  
    else return "";  
}  
```  
  
# 文件复制的几种方式  
## 速度比较  
```java  
public static void main(String[] args) throws IOException {  
    new File(FILE_PATH2).delete();  
    long start = System.currentTimeMillis();  
    copyFileByBuffer(FILE_PATH, FILE_PATH2);//14-23毫秒  
    //copyFileByByteArray(FILE_PATH, FILE_PATH2);//4-6毫秒  
    //copyFileByByteArray2(FILE_PATH, FILE_PATH2);//3-5毫秒  
    //copyFileByByte(FILE_PATH, FILE_PATH2);//700-800毫秒  
    //copyFileByByteOnce(FILE_PATH, FILE_PATH2);//2-4毫秒  
    //copyFileByBufLine(FILE_PATH, FILE_PATH2);//28-78毫秒  
    //copyFileByCharArray(FILE_PATH, FILE_PATH2);//14-22毫秒  
    //copyFileByChar(FILE_PATH, FILE_PATH2);//60-80毫秒  
    System.out.println("耗时：" + (System.currentTimeMillis() - start));  
}  
```  
  
## copyFileByBuffer  
```java  
public static void copyFileByBuffer(String from, String to) {  
    try {  
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(from));  
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(to));  
        int ch;  
        while ((ch = inputStream.read()) != -1) {  
            outputStream.write(ch); //先读取到一个缓冲容器后再写入  
        }  
        inputStream.close();  
        outputStream.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
## copyFileByByteArray  
```java  
public static void copyFileByByteArray(String from, String to) {  
    try {  
        FileInputStream inputStream = new FileInputStream(from);  
        FileOutputStream outputStream = new FileOutputStream(to);  
        byte[] buf = new byte[1024];  
        int length;  
        while ((length = inputStream.read(buf)) != -1) {  
            outputStream.write(buf, 0, length); //一次读取一个指定长度数组的字节  
        }  
        inputStream.close();  
        outputStream.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
## copyFileByByteArray2  
```java  
public static boolean copyFileByByteArray2(String from, String to) {  
    try {  
        FileInputStream inputStream = new FileInputStream(from);  
        FileOutputStream outputStream = new FileOutputStream(to);  
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int length;  
        while ((length = inputStream.read(buffer)) != -1) {  
            arrayOutputStream.write(buffer, 0, length);//一次读取一个指定长度数组的字节  
        }  
        arrayOutputStream.writeTo(outputStream);  
  
        inputStream.close();  
        outputStream.close();  
        return true;  
    } catch (IOException e) {  
        e.printStackTrace();  
        return false;  
    }  
}  
```  
  
## copyFileByByte  
```java  
public static void copyFileByByte(String from, String to) {  
    try {  
        FileInputStream inputStream = new FileInputStream(from);  
        FileOutputStream outputStream = new FileOutputStream(to);  
        int ch;  
        while ((ch = inputStream.read()) != -1) {  
            outputStream.write(ch); //一次读取一个字节  
        }  
        inputStream.close();  
        outputStream.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
## copyFileByByteOnce  
```java  
public static void copyFileByByteOnce(String from, String to) {  
    try {  
        FileInputStream inputStream = new FileInputStream(from);  
        FileOutputStream outputStream = new FileOutputStream(to);  
        byte[] buf = new byte[inputStream.available()];  
        inputStream.read(buf); //一次读取全部字节  
        outputStream.write(buf);  
        inputStream.close();  
        outputStream.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
## copyFileByBufLine  
```java  
public static void copyFileByBufLine(String from, String to) {  
    try {  
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(from), CHARSET));  
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(to), CHARSET));  
        String line;  
        while ((line = reader.readLine()) != null) {  
            writer.write(line); //一次写入一行字符  
            writer.newLine();// 写入一个行分隔符  
            writer.flush();  
        }  
        reader.close();  
        writer.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
## copyFileByCharArray  
```java  
public static boolean copyFileByCharArray(String from, String to) {  
    try {  
        InputStreamReader reader = new InputStreamReader(new FileInputStream(from), CHARSET);  
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(to), CHARSET);  
        char[] buf = new char[1024];  
        int len;  
        while ((len = reader.read(buf)) != -1) {  
            writer.write(buf, 0, len); //一次写入指定个数的字符  
            writer.flush();  
        }  
        reader.close();  
        writer.close();  
        return true;  
    } catch (IOException e) {  
        e.printStackTrace();  
        return false;  
    }  
}  
```  
  
## copyFileByChar  
```java  
public static boolean copyFileByChar(String from, String to) {  
    try {  
        InputStreamReader reader = new InputStreamReader(new FileInputStream(from), CHARSET);  
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(to), CHARSET);  
        int ch;  
        while ((ch = reader.read()) != -1) {  
            writer.write(ch);//一次写入一个字符  
        }  
        reader.close();  
        writer.close();  
        return true;  
    } catch (IOException e) {  
        e.printStackTrace();  
        return false;  
    }  
}  
```  
  
2018-11-25  
