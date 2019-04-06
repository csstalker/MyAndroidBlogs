| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
文件 File 递归复制 修改后缀名 生成Markdown目录 字符串解析 MD  
***  
目录  
===  

- [使用方式](#使用方式)
- [设计](#设计)
	- [一些常量](#一些常量)
	- [文件目录递归修改](#文件目录递归修改)
		- [递归修改文件后缀名并修改文件内容](#递归修改文件后缀名并修改文件内容)
		- [递归修改文件后缀名](#递归修改文件后缀名)
	- [递归获取格式化后的文件名](#递归获取格式化后的文件名)
	- [文件复制](#文件复制)
		- [递归复制目录下的所有文件](#递归复制目录下的所有文件)
		- [复制一个文件或一个目录](#复制一个文件或一个目录)
		- [复制一个文件并修改文件内容](#复制一个文件并修改文件内容)
	- [其他工具方法](#其他工具方法)
		- [将字符串复制到剪切板](#将字符串复制到剪切板)
		- [生成格式化的 Markdown 目录](#生成格式化的-Markdown-目录)
	- [生成 Markdown 的目录](#生成-Markdown-的目录)
		- [用到的常量](#用到的常量)
		- [递归为文件生成Markdown目录](#递归为文件生成Markdown目录)
		- [插入的目录格式](#插入的目录格式)
		- [其他一些用到的工具方法](#其他一些用到的工具方法)
- [源码](#源码)
	- [MDUtils](#MDUtils)
	- [TOCUtils](#TOCUtils)
	- [FrontMatterUtils](#FrontMatterUtils)
	- [DirUtils](#DirUtils)
  
# 使用方式  
工具类的作用：生成`GitHub`上仓库[MyAndroidBlogs](https://github.com/baiqiantao/MyAndroidBlogs)的`REANME.MD`中博客的目录。  
  
使用步骤式：  
1、通过为知笔记导出指定目录下的所有文件，导出为U8格式的纯文本  
2、执行以下代码  
```java  
String DIR = "D:/为知笔记导出/导出的笔记";  
MDUtils.modifyFile(new File(DIR)); //递归修改文件后缀名并修改文件内容  
TOCUtils.insertToc(new File(DIR)); //递归为文件生成目录  
FrontMatterUtils.insertFrontMatter(new File(DIR)); //递归为文件生成FrontMatter  
DirUtils.getFormatFilesNames(new File(DIR)); //递归获取文件名，并将格式化后的内容复制到粘贴板  
```  
  
3、将粘贴板中的内容粘贴到`REANME.MD`中  
  
# 设计  
## 一些常量  
```java  
private static final String DIR = "D:/为知笔记导出/MyAndroidBlogs";  
private static final String WEBSITE = "https://github.com/baiqiantao/MyAndroidBlogs/blob/master/";  
private static final String FILTER = "MD";  
private static final String SUFFIX_MD_TXT = ".md.txt";  
private static final String SUFFIX_TXT = ".txt";  
private static final String SUFFIX_MD = ".md";  
private static final String SPACE = " ";  
private static final String SPACE_FORMAT = "%20";  
private static final String[] EXCLUDE_FILES = new String[]{".git", "README.md"};  
private static final String[] REPLACE_STRINGS = new String[] { " " };//特殊字符  
private static final String BLANK = " ";//空格  
```  
## 文件目录递归修改  
### 递归修改文件后缀名并修改文件内容  
```java  
/**  
 * 递归修改文件后缀名并修改文件内容  
 */  
public static void modifyFile(File from) {  
    if (from == null || !from.exists()) {  
        throw new RuntimeException("源目录或文件不存在");  
    }  
    if (from.isFile()) {  
        if (from.getName().contains(FILTER)) {//只处理带指定标识的文件  
            copyAndModifyFileContent(from, new File(from.getParent(), getNewName(from)));  
        }  
        from.delete();//删除源文件  
        return;  
    }  
  
    File[] listFiles = from.listFiles();  
    if (listFiles == null || listFiles.length == 0) {  
        System.out.println("----------------------" + from.getName() + "中不存在任何文件");  
        from.delete();//删除空文件夹  
        return;  
    }  
  
    for (File file : listFiles) {  
        if (file.isDirectory()) {  
            modifyFile(file);//递归  
        } else if (file.isFile()) {  
            if (file.getName().contains(FILTER)) {//只处理带指定标识的文件  
                copyAndModifyFileContent(file, new File(file.getParent(), getNewName(file)));  
            }  
            file.delete();  
        }  
    }  
  
    listFiles = from.listFiles();  
    if (listFiles == null || listFiles.length == 0) {  
        System.out.println("----------------------" + from.getName() + "的文件全部不符合要求");  
        from.delete();//删除空文件夹  
    }  
}  
```  
  
### 递归修改文件后缀名  
```java  
/**  
 * 递归修改文件后缀名  
 */  
public static void modifyFileSuffix(File from) {  
    if (from == null || !from.exists()) {  
        throw new RuntimeException("源目录或文件不存在");  
    }  
    if (from.isFile()) {  
        if (from.getName().contains(FILTER)) {//只处理带指定标识的文件  
            from.renameTo(new File(from.getParent(), getNewName(from)));  
        } else {  
            from.delete();//删除源文件  
        }  
        return;  
    }  
  
    File[] listFiles = from.listFiles();  
    if (listFiles == null || listFiles.length == 0) {  
        System.out.println("----------------------" + from.getName() + "中不存在任何文件");  
        from.delete();//删除空文件夹  
        return;  
    }  
  
    for (File file : listFiles) {  
        if (file.isDirectory()) {  
            modifyFileSuffix(file);//递归  
        } else if (file.isFile()) {  
            if (file.getName().contains(FILTER)) {//只处理带指定标识的文件  
                file.renameTo(new File(file.getParent(), getNewName(file)));  
            } else {  
                file.delete();  
            }  
        }  
    }  
  
    listFiles = from.listFiles();  
    if (listFiles == null || listFiles.length == 0) {  
        System.out.println("----------------------" + from.getName() + "的文件全部不符合要求");  
        from.delete();//删除空文件夹  
    }  
}  
```  
  
```java  
/**  
 * 获取重命名的文件名  
 */  
private static String getNewName(File file) {  
    String name = file.getName();  
    if (name.endsWith(SUFFIX_MD_TXT)) { //处理指定后缀的文件  
        name = name.substring(0, name.indexOf(SUFFIX_MD_TXT) + SUFFIX_MD.length());  
    } else if (name.endsWith(SUFFIX_TXT)) {  
        name = name.substring(0, name.indexOf(SUFFIX_TXT)) + SUFFIX_MD;  
    }  
    return name;  
}  
```  
  
## 递归获取格式化后的文件名  
```java  
/**  
 * 获取指定目录及其子目录下的文件的文件名，并对文件名进行格式化，并存储到一个指定的集合中，并将内容复制到粘贴板  
 */  
public static List<String> getFormatFilesNames(File from) {  
    List<String> filePathList = new ArrayList<>();  
    getDirFormatFilesNames(filePathList, from, 0);  
    StringBuilder sBuilder = new StringBuilder();  
    for (String string : filePathList) {  
        System.out.print(string);  
        sBuilder.append(string);  
    }  
    setSysClipboardText(sBuilder.toString());  
    return filePathList;  
}  
```  
  
```java  
/**  
 * 递归获取指定目录及其子目录下的文件的文件名，并对文件名进行格式化，并存储到一个指定的集合中  
 *  
 * @param filePathList 将结果保存到指定的集合中  
 * @param from         要遍历的目录  
 * @param curLeval     记录当前递归所在层级  
 */  
private static void getDirFormatFilesNames(List<String> filePathList, File from, int curLeval) {  
    if (from == null || !from.exists()) {  
        throw new RuntimeException("源目录或文件不存在");  
    } else if (isExcludeFile(from.getName())) {  
        System.out.println("----------------------" + "忽略文件" + from.getName());  
        return;  
    } else {  
        filePathList = filePathList == null ? new ArrayList<String>() : filePathList;  
        filePathList.add(getTitle(curLeval, from));  
    }  
      
    curLeval++;  
    File[] files = from.listFiles();  
    if (files == null || files.length == 0) {  
        System.out.println("----------------------" + from.getName() + "中不存在任何文件");  
        return;  
    }  
      
    for (File file : files) {  
        if (file.isDirectory()) {  
            getDirFormatFilesNames(filePathList, file, curLeval);//递归  
        } else if (file.isFile() && !isExcludeFile(file.getName())) {  
            filePathList.add(getTitle(curLeval, file));  
        }  
    }  
}  
```  
  
```java  
/**  
 * 判断是否是忽略的文件  
 */  
private static boolean isExcludeFile(String fileName) {  
    for (String name : EXCLUDE_FILES) {  
        if (name.equals(fileName)) {  
            return true;  
        }  
    }  
    return false;  
}  
```  
  
## 文件复制  
### 递归复制目录下的所有文件  
```java  
/**  
 * 递归复制目录下的所有文件  
 */  
public static void copyFiles(File from, File to) {  
    if (from == null || !from.exists() || to == null) {  
        throw new RuntimeException("源文件或目标文件不存在");  
    }  
    if (from.equals(to)) {  
        System.out.println("----------------------源文件和目标文件是同一个" + from.getAbsolutePath());  
    }  
      
    if (from.isDirectory()) {  
        if (to.isFile()) {  
            throw new RuntimeException("目录不能复制为文件");  
        }  
        if (!to.exists()) {  
            to.mkdirs();//创建目录  
        }  
    } else {  
        if (to.isDirectory()) {  
            throw new RuntimeException("文件不能复制为目录");  
        }  
        copyFile(from, to);//文件的话直接复制  
        return;  
    }  
      
    File[] files = from.listFiles();  
    if (files == null || files.length == 0) {  
        System.out.println(from.getName() + "中不存在任何文件");  
        return;  
    }  
      
    for (File file : files) {  
        if (file.isDirectory()) {  
            File copyDir = new File(to, file.getName());  
            if (!copyDir.exists()) {  
                copyDir.mkdirs();  
                System.out.println("创建子目录\t\t" + copyDir.getAbsolutePath());  
            }  
            copyFiles(file, copyDir);//递归  
        } else if (file.getName().contains(FILTER)) {  
            copyFile(file, new File(to, file.getName()));  
        }  
    }  
}  
```  
  
### 复制一个文件或一个目录  
```java  
/**  
 * 复制一个文件或一个目录(不会递归复制目录下的文件)  
 */  
public static void copyFile(File from, File to) {  
    if (from == null || !from.exists() || to == null) {  
        throw new RuntimeException("源文件或目标文件不存在");  
    }  
    if (from.equals(to)) {  
        System.out.println("----------------------源文件和目标文件是同一个" + from.getAbsolutePath());  
    }  
      
    if (from.isDirectory()) {  
        if (to.isFile()) {  
            throw new RuntimeException("目录不能复制为文件");  
        }  
        if (!to.exists()) {  
            to.mkdirs();//创建目录  
        }  
        return; //没有下面的刘操作  
    } else {  
        if (to.isDirectory()) {  
            throw new RuntimeException("文件不能复制为目录");  
        }  
        if (!to.getParentFile().exists()) {  
            to.getParentFile().mkdirs(); //复制父目录  
        }  
    }  
      
    try {  
        BufferedInputStream bufis = new BufferedInputStream(new FileInputStream(from));  
        BufferedOutputStream bufos = new BufferedOutputStream(new FileOutputStream(to));  
        int ch;  
        while ((ch = bufis.read()) != -1) {  
            bufos.write(ch);  
        }  
        bufis.close();  
        bufos.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
### 复制一个文件并修改文件内容  
```java  
/**  
 * 复制一个文件或目录(不会复制目录下的文件)，复制的时候会修改文件中的一些内容  
 */  
public static void copyAndModifyFileContent(File from, File to) {  
    if (from == null || !from.exists() || to == null) {  
        throw new RuntimeException("源文件或目标文件不存在");  
    }  
    if (from.equals(to)) {  
        System.out.println("----------------------源文件和目标文件是同一个" + from.getAbsolutePath());  
    }  
  
    if (from.isDirectory()) {  
        if (to.isFile()) {  
            throw new RuntimeException("目录不能复制为文件");  
        }  
        if (!to.exists()) {  
            to.mkdirs();//创建目录  
        }  
        return; //没有下面的刘操作  
    } else {  
        if (to.isDirectory()) {  
            throw new RuntimeException("文件不能复制为目录");  
        }  
        if (!to.getParentFile().exists()) {  
            to.getParentFile().mkdirs(); //复制父目录  
        }  
    }  
  
    try {  
        BufferedReader bufr = new BufferedReader(new FileReader(from));  
        BufferedWriter bufw = new BufferedWriter(new FileWriter(to));  
        String line;  
        //另外开辟一个缓冲区，存储读取的一行数据，返回包含该行内容的字符串，不包含换行符，如果已到达流末尾，则返回【 null】  
        while ((line = bufr.readLine()) != null) {  
            for (String string : REPLACE_STRINGS) {  
                line = line.replace(string, BLANK);//替换为空格  
            }  
            bufw.write(line);  
            bufw.write(BLANK + BLANK);//加两个空格  
            bufw.newLine();// 写入一个行分隔符  
            bufw.flush();  
        }  
        bufr.close();  
        bufw.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
}  
```  
  
## 其他工具方法  
### 将字符串复制到剪切板  
```java  
/**  
 * 将字符串复制到剪切板  
 */  
public static void setSysClipboardText(String writeMe) {  
    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();  
    Transferable tText = new StringSelection(writeMe);  
    clip.setContents(tText, null);  
}  
```  
  
### 生成格式化的 Markdown 目录  
```java  
/***  
 * 格式化目录  
 */  
private static String getTitle(int level, File file) {  
    StringBuilder sb = new StringBuilder();  
    StringBuilder parentPath = new StringBuilder();  
    File parent = file;  
    for (int x = 1; x < level; x++) {  
        sb.append("\t");  
        parent = parent.getParentFile();//逐级获取父文件  
        parentPath.insert(0, parent.getName() + "/");//在前面插入父文件的文件名  
    }  
  
    sb.append("-").append(" ")//无序列表  
            .append("[")//超链接显示的字符  
            .append(file.getName().endsWith(SUFFIX_MD) ? file.getName().substring(0, file.getName().lastIndexOf(SUFFIX_MD)) : file.getName())//  
            .append("]")//  
            .append("(")//拼接超链接  
            .append(WEBSITE)//前缀  
            .append(parentPath.toString())//父目录  
            .append(file.getName().replaceAll(SPACE, SPACE_FORMAT))//文件名  
            .append(")")//  
            .append("\n");  
    return sb.toString();  
}  
```  
  
## 生成 Markdown 的目录  
### 用到的常量  
```java  
private static final String HEADER_STRING = "#";  
private static final char HEADER_CHAR = '#';  
private static final String TOC_TITLE = "";//在目录前插入的内容  
private static final String REPLACE_TOC = "[TOC]"; //目录要替换的内容  
private static final String HEADER_1 = "="; //一级目录  
private static final String HEADER_2 = "--"; //二级目录  
  
private static final String PATTERN = "%s- [%s](#%s)";//标题的格式  
private static final String SPACES = " ";  
private static final String CODES = "%([abcdef]|\\d){2,2}";  
private static final String SPECIAL_CHARS = "[\\/?!:\\[\\]`.,()*\"';{}+=<>~\\$|#]";  
private static final String DASH = "-";  
private static final String EMPTY = "";  
private static final String AFFIX = "\t";  
```  
  
### 递归为文件生成Markdown目录  
```java  
/**  
 * 递归为文件生成Markdown目录  
 */  
public static void insertToc(File from) {  
    if (from == null || !from.exists()) {  
        throw new RuntimeException("源目录或文件不存在");  
    }  
    if (from.isFile()) {  
        insertTocIntoFile(from, from, REPLACE_TOC, Integer.MAX_VALUE);  
        return;  
    }  
  
    File[] listFiles = from.listFiles();  
    if (listFiles == null || listFiles.length == 0) {  
        System.out.println("----------------------" + from.getName() + "中不存在任何文件");  
        return;  
    }  
  
    for (File file : listFiles) {  
        if (file.isDirectory()) {  
            insertToc(file);//递归  
        } else if (file.isFile()) {  
            insertTocIntoFile(file, file, REPLACE_TOC, Integer.MAX_VALUE);  
        }  
    }  
}  
```  
  
```java  
/**  
 * 为文件生成Markdown目录  
 */  
public static void insertTocIntoFile(File from, File to, String replaceAt, int deepLevel) {  
    BufferedReader reader = null;  
    try {  
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(from)));  
        String line;//当前行的内容  
        String previousLine = null;  
        List<String> contentList = new ArrayList<>();//每一行的内容集合  
        List<String> titleList = new ArrayList<>();//标题集合  
        int currentLine = 0;  
        int patternLineNumber = 1; //目录插在那一行  
  
        while ((line = reader.readLine()) != null) {  
            ++currentLine;  
            boolean skipLine = false;  
            String trimLineString = line.trim();  
            if (trimLineString.startsWith(HEADER_STRING)) { //检测到标题  
                int count = getCharCount(trimLineString, HEADER_CHAR);  
                if (count < 1 || count > deepLevel) { //层级控制  
                    System.out.println("----------------------------超过最大层级 " + deepLevel);  
                    previousLine = line;  
                    continue;  
                }  
                String headerName = trimLineString.substring(count).trim(); //去掉层级后的文字  
                titleList.add(getTitle(count, headerName));  
            } else if (line.startsWith(HEADER_1) && isNotEmpty(previousLine) && line.replaceAll(HEADER_1, "").isEmpty()) {  
                titleList.add(getTitle(1, previousLine));  
            } else if (line.startsWith(HEADER_2) && isNotEmpty(previousLine) && line.replaceAll(HEADER_2, "").isEmpty()) {  
                titleList.add(getTitle(2, previousLine));  
            } else if (patternLineNumber <= 1 && line.trim().equals(replaceAt)) {  
                patternLineNumber = currentLine; //找到这个字符串时就插在这一行(替换这个字符串)，找到之后就不再继续找了  
                skipLine = true;//忽略这一行  
            }  
            if (!skipLine) {  
                contentList.add(line);  
            }  
            previousLine = line;  
        }  
        closeStream(reader); //必须先关掉这个读取，流才能开启写入流  
  
        if (patternLineNumber <= 0) {  
            System.out.println("----------------------------目录插入位置有误");  
            return;  
        }  
  
        contentList.add(patternLineNumber - 1, TOC_TITLE);//在指定位置插入目录前面的标题  
        for (String title : titleList) {  
            contentList.add(patternLineNumber, title);  
            patternLineNumber++;  
        }  
        writeFile(to, contentList);  
    } catch (Exception e) {  
        e.printStackTrace();  
    } finally {  
        closeStream(reader);  
    }  
}  
```  
  
```java  
/**  
 * 写内容到指定文件  
 */  
private static void writeFile(File to, List<String> contentList) {  
    PrintWriter writer = null;  
    try {  
        writer = new PrintWriter(new FileWriter(to));  
        for (String string : contentList) {  
            writer.append(string).append("\n");  
        }  
        writer.close();  
    } catch (IOException e) {  
        e.printStackTrace();  
    } finally {  
        closeStream(writer);  
    }  
}  
```  
  
### 插入的目录格式  
```java  
/**  
 * 插入的目录的模型  
 */  
private static String getTitle(int deepLevel, String headerName) {  
    StringBuilder title = new StringBuilder();  
    if (deepLevel > 1) {  
        for (int i = 0; i < deepLevel - 1; i++) {  
            title.append(AFFIX);  
        }  
    }  
  
    String headerLink = headerName.trim()//  
            .replaceAll(SPACES, DASH)//替换特殊字符，比如必须将空格换成'-'  
            .replaceAll(CODES, EMPTY)//  
            .replaceAll(SPECIAL_CHARS, EMPTY).toLowerCase();  
  
    title.append(DASH).append(SPACES)//格式为【%s- [%s](#%s)】或【缩进- [标题][#链接]】  
            .append("[").append(headerName).append("]")//  
            .append("(").append("#")//  
            .append(headerLink)//  
            .append(")");  
    return title.toString();  
}  
```  
  
### 其他一些用到的工具方法  
```java  
/**  
 * 关闭流  
 */  
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
  
```java  
/**  
 * 计算字符串中字符的个数  
 */  
private static int getCharCount(String string, char c) {  
    int count = 0;  
    for (int i = 0; i < string.length(); i++) {  
        if (string.charAt(i) == c) {  
            ++count;  
        } else {  
            break;  
        }  
    }  
    return count;  
}  
```  
  
```java  
/**  
 * 获取文件行数  
 */  
public static int getFileLines(File file) {  
    LineNumberReader reader = null;  
    try {  
        reader = new LineNumberReader(new FileReader(file));  
        reader.skip(Long.MAX_VALUE);  
        return reader.getLineNumber() + 1;  
    } catch (IOException ignored) {  
    } finally {  
        closeStream(reader);  
    }  
    return -1;  
}  
```  
  
```java  
/**  
 * 判断字符串非空  
 */  
public static boolean isNotEmpty(String string) {  
    return string != null && !string.isEmpty();  
}  
```  
  
# 源码  
  
## MDUtils  
```java  
public class MDUtils {  
    private static final String FILTER = "MD";  
    private static final String SUFFIX_MD_TXT = ".md.txt";  
    private static final String SUFFIX_TXT = ".txt";  
    private static final String SUFFIX_MD = ".md";  
    private static final String[] REPLACE_STRINGS = new String[] { " " };//特殊字符  
    private static final String BLANK = " ";//空格  
  
    /**  
     * 递归修改文件后缀名并修改文件内容  
     */  
    public static void modifyFile(File from) {  
        if (from == null || !from.exists()) {  
            throw new RuntimeException("源目录或文件不存在");  
        }  
        if (from.isFile()) {  
            if (from.getName().contains(FILTER)) {//只处理带指定标识的文件  
                copyAndModifyFileContent(from, new File(from.getParent(), getNewName(from)));  
            }  
            from.delete();//删除源文件  
            return;  
        }  
  
        File[] listFiles = from.listFiles();  
        if (listFiles == null || listFiles.length == 0) {  
            System.out.println("----------------------" + from.getName() + "中不存在任何文件");  
            from.delete();//删除空文件夹  
            return;  
        }  
  
        for (File file : listFiles) {  
            if (file.isDirectory()) {  
                modifyFile(file);//递归  
            } else if (file.isFile()) {  
                if (file.getName().contains(FILTER)) {//只处理带指定标识的文件  
                    copyAndModifyFileContent(file, new File(file.getParent(), getNewName(file)));  
                }  
                file.delete();  
            }  
        }  
  
        listFiles = from.listFiles();  
        if (listFiles == null || listFiles.length == 0) {  
            System.out.println("----------------------" + from.getName() + "的文件全部不符合要求");  
            from.delete();//删除空文件夹  
        }  
    }  
  
    /**  
     * 复制一个文件或目录(不会复制目录下的文件)，复制的时候会修改文件中的一些内容  
     */  
    private static void copyAndModifyFileContent(File from, File to) {  
        if (from == null || !from.exists() || to == null) {  
            throw new RuntimeException("源文件或目标文件不存在");  
        }  
        if (from.equals(to)) {  
            System.out.println("----------------------源文件和目标文件是同一个" + from.getAbsolutePath());  
        }  
  
        if (from.isDirectory()) {  
            if (to.isFile()) {  
                throw new RuntimeException("目录不能复制为文件");  
            }  
            if (!to.exists()) {  
                to.mkdirs();//创建目录  
            }  
            return; //没有下面的刘操作  
        } else {  
            if (to.isDirectory()) {  
                throw new RuntimeException("文件不能复制为目录");  
            }  
            if (!to.getParentFile().exists()) {  
                to.getParentFile().mkdirs(); //复制父目录  
            }  
        }  
  
        try {  
            BufferedReader bufr = new BufferedReader(new FileReader(from));  
            BufferedWriter bufw = new BufferedWriter(new FileWriter(to));  
  
            String line;  
            //另外开辟一个缓冲区，存储读取的一行数据，返回包含该行内容的字符串，不包含换行符，如果已到达流末尾，则返回【 null】  
            while ((line = bufr.readLine()) != null) {  
                for (String string : REPLACE_STRINGS) {  
                    line = line.replace(string, BLANK);//替换为空格  
                }  
                bufw.write(line);  
                bufw.write(BLANK + BLANK);//加两个空格  
                bufw.newLine();// 写入一个行分隔符  
                bufw.flush();  
            }  
            bufr.close();  
            bufw.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    /**  
     * 获取重命名的文件名  
     */  
    private static String getNewName(File file) {  
        String name = file.getName();  
        if (name.endsWith(SUFFIX_MD_TXT)) { //处理指定后缀的文件  
            name = name.substring(0, name.indexOf(SUFFIX_MD_TXT) + SUFFIX_MD.length());  
        } else if (name.endsWith(SUFFIX_TXT)) {  
            name = name.substring(0, name.indexOf(SUFFIX_TXT)) + SUFFIX_MD;  
        }  
        return name;  
    }  
}  
```  
  
## TOCUtils  
```java  
public class TOCUtils {  
    private static final String HEADER_STRING = "#";  
    private static final char HEADER_CHAR = '#';  
    private static final String TOC_TITLE = "";//在目录前插入的内容  
    private static final String REPLACE_TOC = "[TOC]"; //目录要替换的内容  
    private static final String HEADER_1 = "="; //一级目录  
    private static final String HEADER_2 = "--"; //二级目录  
    private static final String SPACES = " ";  
    private static final String CODES = "%([abcdef]|\\d){2,2}";  
    private static final String SPECIAL_CHARS = "[\\/?!:\\[\\]`.,()*\"';{}+=<>~\\$|#]";  
    private static final String DASH = "-";  
    private static final String EMPTY = "";  
    private static final String AFFIX = "\t";  
  
    /**  
     * 递归为为文件生成目录  
     */  
    public static void insertToc(File from) {  
        if (from == null || !from.exists()) {  
            throw new RuntimeException("源目录或文件不存在");  
        }  
        if (from.isFile()) {  
            insertTocIntoFile(from, from, REPLACE_TOC, Integer.MAX_VALUE);  
            return;  
        }  
  
        File[] listFiles = from.listFiles();  
        if (listFiles == null || listFiles.length == 0) {  
            System.out.println("----------------------" + from.getName() + "中不存在任何文件");  
            return;  
        }  
  
        for (File file : listFiles) {  
            if (file.isDirectory()) {  
                insertToc(file);//递归  
            } else if (file.isFile()) {  
                insertTocIntoFile(file, file, REPLACE_TOC, Integer.MAX_VALUE);  
            }  
        }  
    }  
  
    /**  
     * 为文件生成目录  
     */  
    private static void insertTocIntoFile(File from, File to, String replaceAt, int deepLevel) {  
        BufferedReader reader = null;  
        try {  
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(from)));  
            String line;//当前行的内容  
            String previousLine = null;  
            List<String> contentList = new ArrayList<>();//每一行的内容集合  
            List<String> titleList = new ArrayList<>();//标题集合  
            int currentLine = 0;  
            int patternLineNumber = 1; //目录插在那一行  
  
            while ((line = reader.readLine()) != null) {  
                ++currentLine;  
                boolean skipLine = false;  
                String trimLineString = line.trim();  
                if (trimLineString.startsWith(HEADER_STRING)) { //检测到标题  
                    int count = getCharCount(trimLineString, HEADER_CHAR);  
                    if (count < 1 || count > deepLevel) { //层级控制  
                        System.out.println("----------------------------超过最大层级 " + deepLevel);  
                        previousLine = line;  
                        continue;  
                    }  
                    String headerName = trimLineString.substring(count).trim(); //去掉层级后的文字  
                    System.out.println("【" + headerName + "】");  
                    titleList.add(getTitle(count, headerName));  
                } else if (line.startsWith(HEADER_1) && isNotEmpty(previousLine) && line.replaceAll(HEADER_1, "").isEmpty()) {  
                    //titleList.add(getTitle(1, previousLine.trim()));//暂时不需要  
                } else if (line.startsWith(HEADER_2) && isNotEmpty(previousLine) && line.replaceAll(HEADER_2, "").isEmpty()) {  
                    //titleList.add(getTitle(2, previousLine.trim()));//暂时不需要  
                } else if (patternLineNumber <= 1 && line.trim().equals(replaceAt)) {  
                    patternLineNumber = currentLine; //找到这个字符串时就插在这一行(替换这个字符串)，找到之后就不再继续找了  
                    skipLine = true;//忽略这一行  
                }  
                if (!skipLine) {  
                    contentList.add(line);  
                }  
                previousLine = line;  
            }  
            closeStream(reader); //必须先关掉这个读取，流才能开启写入流  
  
            if (patternLineNumber <= 0) {  
                System.out.println("----------------------------目录插入位置有误");  
                return;  
            }  
  
            contentList.add(patternLineNumber - 1, TOC_TITLE);//在指定位置插入目录前面的标题  
            for (String title : titleList) {  
                contentList.add(patternLineNumber, title);  
                patternLineNumber++;  
            }  
            writeFile(to, contentList);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            closeStream(reader);  
        }  
    }  
  
    /**  
     * 写内容到指定文件  
     */  
    private static void writeFile(File file, List<String> contentList) {  
        PrintWriter writer = null;  
        try {  
            writer = new PrintWriter(new FileWriter(file));  
            for (String string : contentList) {  
                writer.append(string).append("\n");  
            }  
            writer.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            closeStream(writer);  
        }  
    }  
  
    /**  
     * 关闭流  
     */  
    private static void closeStream(Closeable... closeable) {  
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
  
    /**  
     * 判断字符串非空  
     */  
    private static boolean isNotEmpty(String string) {  
        return string != null && !string.isEmpty();  
    }  
  
    /**  
     * 计算字符串中字符的个数  
     */  
    private static int getCharCount(String string, char c) {  
        int count = 0;  
        for (int i = 0; i < string.length(); i++) {  
            if (string.charAt(i) == c) {  
                ++count;  
            } else {  
                break;  
            }  
        }  
        return count;  
    }  
  
    /**  
     * 插入的目录的模型  
     */  
    private static String getTitle(int deepLevel, String headerName) {  
        StringBuilder title = new StringBuilder();  
        if (deepLevel > 1) {  
            for (int i = 0; i < deepLevel - 1; i++) {  
                title.append(AFFIX);  
            }  
        }  
  
        String headerLink = headerName.trim()//  
                .replaceAll(SPACES, DASH)//替换特殊字符，比如必须将空格换成'-'  
                .replaceAll(CODES, EMPTY)//这一块可能会有问题！！！！！！！！！！！！！！！！！！！！！  
                .replaceAll(SPECIAL_CHARS, EMPTY)/* .toLowerCase() */;  
  
        title.append(DASH).append(SPACES)//格式为【%s- [%s](#%s)】或【缩进- [标题][#链接]】  
                .append("[").append(headerName).append("]")//  
                .append("(").append("#")//  
                .append(headerLink)//  
                .append(")");  
        return title.toString();  
    }  
}  
```  
  
## FrontMatterUtils  
```java  
public class FrontMatterUtils {  
    private static String ROOT_DIR = null;  
  
    private final static String[] HEX_DIGITS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };//十六进制下数字到字符的映射数组  
    private final static String KEY = "wangyang"; // 可以自定义生成 MD5 加密字符传前的混合 KEY  
  
    private final static String[] CHARS = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",  
            "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",  
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };// 要使用生成 URL 的字符  
  
    /**  
     * 递归为为文件生成FrontMatter  
     */  
    public static void insertFrontMatter(File from) {  
        if (from == null || !from.exists()) {  
            throw new RuntimeException("源目录或文件不存在");  
        } else if (ROOT_DIR == null) {  
            ROOT_DIR = from.getAbsolutePath();  
        }  
        if (from.isFile()) {  
            writeFrontMatter(from);  
            return;  
        }  
  
        File[] listFiles = from.listFiles();  
        if (listFiles == null || listFiles.length == 0) {  
            System.out.println("----------------------" + from.getName() + "中不存在任何文件");  
            return;  
        }  
  
        for (File file : listFiles) {  
            if (file.isDirectory()) {  
                insertFrontMatter(file);//递归  
            } else if (file.isFile()) {  
                writeFrontMatter(file);  
            }  
        }  
    }  
  
    //写内容到指定文件  
    public static void writeFrontMatter(File file) {  
        String title = file.getName().substring(0, file.getName().indexOf(".md"));  
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));  
        String dir = file.getParent().substring(ROOT_DIR.length() + 1).replace("\\", "/");  
  
        StringBuilder builder = new StringBuilder();  
        builder.append("---").append(System.lineSeparator())//  
                .append("title: ").append(title).append(System.lineSeparator())//标题  
                .append("date: ").append(date).append(System.lineSeparator())//生成日期  
                .append("bqtdir: ").append(dir).append("/").append(shortUrl(title)).append(System.lineSeparator())//自定义路径  
                .append("categories:").append(System.lineSeparator());//分类  
        for (String category : dir.split("/")) {  
            builder.append("  - ").append(category).append(System.lineSeparator());//路径分类  
        }  
        builder.append("---").append(System.lineSeparator()).append(System.lineSeparator());  
  
        try {  
            FileInputStream inputStream = new FileInputStream(file);  
            byte[] content = new byte[inputStream.available()];  
            inputStream.read(content); //一次读取全部字节  
            inputStream.close();  
  
            FileOutputStream outputStream = new FileOutputStream(file);  
            outputStream.write(builder.toString().getBytes());  
            outputStream.write(content);  
            outputStream.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static String shortUrl(String url) {  
        return shortUrls(url)[0];  
    }  
  
    /**会产生4 组6 位字符串，任意一组都可以作为当前字符串的短链接地址*/  
    public static String[] shortUrls(String url) {  
        String hex = md5(KEY + url);// 对传入网址进行 MD5 加密  
        String[] resUrl = new String[4];//得到 4组短链接字符串  
        for (int i = 0; i < 4; i++) {  
            String sTempSubString = hex.substring(i * 8, i * 8 + 8); // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算  
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);// 这里需要使用 long 型来转换  
            String outChars = ""; //循环获得每组6位的字符串  
            for (int j = 0; j < 6; j++) {  
                long index = 0x0000003D & lHexLong; // 取得字符数组 chars 索引  
                outChars += CHARS[(int) index];// 把取得的字符相加  
                lHexLong = lHexLong >> 5;// 每次循环按位右移 5 位  
            }  
            resUrl[i] = outChars;// 把字符串存入对应索引的输出数组  
        }  
        return resUrl;  
    }  
  
    /**对字符串进行MD5编码*/  
    public static String md5(String originString) {  
        if (originString != null) {  
            try {  
                MessageDigest md5 = MessageDigest.getInstance("MD5");//创建具有指定算法名称的信息摘要  
                byte[] results = md5.digest(originString.getBytes());//使用指定的字节数组对摘要进行最后更新，然后完成摘要计算  
                String result = byteArrayToHexString(results);//将得到的字节数组变成字符串返回  
                return result;  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return null;  
    }  
  
    //轮换字节数组为十六进制字符串  
    private static String byteArrayToHexString(byte[] b) {  
        StringBuffer resultSb = new StringBuffer();  
        for (int i = 0; i < b.length; i++) {  
            resultSb.append(byteToHexString(b[i]));  
        }  
        return resultSb.toString();  
    }  
  
    //将一个字节转化成十六进制形式的字符串  
    private static String byteToHexString(byte b) {  
        int n = b;  
        if (n < 0) n = 256 + n;  
        int d1 = n / 16;  
        int d2 = n % 16;  
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];  
    }  
}  
```  
  
## DirUtils  
```java  
public class DirUtils {  
    private static final String WEBSITE = "https://github.com/baiqiantao/MyAndroidBlogs/blob/master/";  
    private static final String SPACE = " ";  
    private static final String SPACE_FORMAT = "%20";  
    private static final String[] EXCLUDE_FILES = new String[] { ".git", "README.md" };  
    private static final String SUFFIX_MD = ".md";  
  
    /**  
     * 获取指定目录及其子目录下的文件的文件名，并对文件名进行格式化，并存储到一个指定的集合中，并将内容复制到粘贴板  
     */  
    public static List<String> getFormatFilesNames(File from) {  
        List<String> filePathList = new ArrayList<>();  
        getDirFormatFilesNames(filePathList, from, 0);  
        StringBuilder sBuilder = new StringBuilder();  
        for (String string : filePathList) {  
            System.out.print(string);  
            sBuilder.append(string);  
        }  
        setSysClipboardText(sBuilder.toString());  
        return filePathList;  
    }  
  
    /**  
     * 获取指定目录及其子目录下的文件的文件名，并对文件名进行格式化，并存储到一个指定的集合中  
     *  
     * @param filePathList 将结果保存到指定的集合中  
     * @param from         要遍历的目录  
     * @param curLeval     记录当前递归所在层级  
     */  
    private static void getDirFormatFilesNames(List<String> filePathList, File from, int curLeval) {  
        if (from == null || !from.exists()) {  
            throw new RuntimeException("源目录或文件不存在");  
        } else if (isExcludeFile(from.getName())) {  
            System.out.println("----------------------" + "忽略文件" + from.getName());  
            return;  
        } else {  
            filePathList = filePathList == null ? new ArrayList<String>() : filePathList;  
            filePathList.add(getTitle(curLeval, from));  
        }  
  
        curLeval++;  
        File[] files = from.listFiles();  
        if (files == null || files.length == 0) {  
            System.out.println("----------------------" + from.getName() + "中不存在任何文件");  
            return;  
        }  
  
        for (File file : files) {  
            if (file.isDirectory()) {  
                getDirFormatFilesNames(filePathList, file, curLeval);//递归  
            } else if (file.isFile() && !isExcludeFile(file.getName())) {  
                filePathList.add(getTitle(curLeval, file));  
            }  
        }  
    }  
  
    /**  
     * 是否是忽略的文件  
     */  
    private static boolean isExcludeFile(String fileName) {  
        for (String name : EXCLUDE_FILES) {  
            if (name.equals(fileName)) {  
                return true;  
            }  
        }  
        return false;  
    }  
  
    /**  
     * 将字符串复制到剪切板  
     */  
    private static void setSysClipboardText(String writeMe) {  
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();  
        Transferable tText = new StringSelection(writeMe);  
        clip.setContents(tText, null);  
    }  
  
    /***  
     * 格式化目录  
     */  
    private static String getTitle(int level, File file) {  
        StringBuilder sb = new StringBuilder();  
        StringBuilder parentPath = new StringBuilder();  
        File parent = file;  
        for (int x = 1; x < level; x++) {  
            sb.append("\t");  
            parent = parent.getParentFile();//逐级获取父文件  
            parentPath.insert(0, parent.getName() + "/");//在前面插入父文件的文件名  
        }  
  
        sb.append("-").append(" ")//无序列表  
                .append("[")//超链接显示的字符  
                .append(file.getName().endsWith(SUFFIX_MD) ? file.getName().substring(0, file.getName().lastIndexOf(SUFFIX_MD)) : file.getName())//  
                .append("]")//  
                .append("(")//拼接超链接  
                .append(WEBSITE)//前缀  
                .append(parentPath.toString())//父目录  
                .append(file.getName().replaceAll(SPACE, SPACE_FORMAT))//文件名  
                .append(")")//  
                .append("\n");  
        return sb.toString();  
    }  
}  
```  
  
2018-10-24  
