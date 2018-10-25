
- [使用方式](# 使用方式)
	- [方式一](# 方式一)
	- [方式二](# 方式二)
- [一些常量](# 一些常量)
- [文件目录递归修改](# 文件目录递归修改)
	- [递归修改文件后缀名并修改文件内容](# 递归修改文件后缀名并修改文件内容)
	- [递归修改文件后缀名](# 递归修改文件后缀名)
- [递归获取格式化后的文件名](# 递归获取格式化后的文件名)
- [文件复制](# 文件复制)
	- [递归复制目录下的所有文件](# 递归复制目录下的所有文件)
	- [复制一个文件或一个目录](# 复制一个文件或一个目录)
	- [复制一个文件并修改文件内容](# 复制一个文件并修改文件内容)
- [其他工具方法](# 其他工具方法)
	- [将字符串复制到剪切板](# 将字符串复制到剪切板)
	- [生成格式化的 Markdown 目录](# 生成格式化的 markdown 目录)
- [方式二中所用到的所有工具方法](# 方式二中所用到的所有工具方法)
- [生成 Markdown 的目录](# 生成 markdown 的目录)
| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
文件 File 递归复制 修改后缀名 生成Markdown目录 字符串解析 MD  
***  
  
# 使用方式  
工具类的作用：生成`GitHub`上厂库[MyAndroidBlogs](https://github.com/baiqiantao/MyAndroidBlogs)的`REANME.MD`中博客的目录。  
  
## 方式一  
不修改文件内容：  
- 通过为知笔记导出指定目录下的所有文件，导出为U8格式的纯文本  
- 通过 `MDUtils.modifyFileSuffix` 递归修改文件的后缀名，同时删除不匹配的文件  
- 通过`TOCUtils.insertToc`方法递归给 markdown 文件添加目录  
- 通过 `MDUtils.getFormatFilesNames` 获取格式化目录，并将内容复制到粘贴板  
- 将粘贴板中的内容粘贴到`REANME.MD`中就可以了  
  
```java  
MDUtils.modifyFileSuffix(new File(DIR));  
TOCUtils.insertToc(new File(DIR));  
MDUtils.getFormatFilesNames(new File(DIR));  
```  
  
## 方式二  
通过方式一操作之后发现有一个小问题，就是因为`为知笔记`里面的空格实际上不是空格，而是一个`特殊符号`，这个符号放在GitHub中不起作用，比如：  
```java  
System.out.println(" ".toCharArray()[0]+0);//160，MD中的空格  
System.out.println(" ".toCharArray()[0]+0);//32，正常的空格  
```  
  
这就很坑爹了呀，这导致我几乎所有的标签都不能用了！    
所以优化了上面的一点细节，采用下面的步骤：  
- 通过为知笔记导出指定目录下的所有文件，导出为U8格式的纯文本  
- 通过 `MDUtils.modifyFile` 递归修改文件的后缀名，同时删除不匹配的文件，**同时修改文件中的一些不太正常的字符**  
- 通过`TOCUtils.insertToc`方法递归给 markdown 文件添加目录  
- 通过 `MDUtils.getFormatFilesNames` 获取格式化目录，并将内容复制到粘贴板  
- 将粘贴板中的内容粘贴到`REANME.MD`中就可以了  
  
```java  
MDUtils.modifyFile(new File(DIR));  
TOCUtils.insertToc(new File(DIR));  
MDUtils.getFormatFilesNames(new File(DIR));  
```  
  
# 一些常量  
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
# 文件目录递归修改  
## 递归修改文件后缀名并修改文件内容  
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
  
## 递归修改文件后缀名  
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
  
# 递归获取格式化后的文件名  
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
  
# 文件复制  
## 递归复制目录下的所有文件  
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
  
## 复制一个文件或一个目录  
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
  
## 复制一个文件并修改文件内容  
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
  
# 其他工具方法  
## 将字符串复制到剪切板  
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
  
## 生成格式化的 Markdown 目录  
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
  
# 方式二中所用到的所有工具方法  
```java  
public class MDUtils {  
    private static final String WEBSITE = "https://github.com/baiqiantao/MyAndroidBlogs/blob/master/";  
    private static final String FILTER = "MD";  
    private static final String SUFFIX_MD_TXT = ".md.txt";  
    private static final String SUFFIX_TXT = ".txt";  
    private static final String SUFFIX_MD = ".md";  
    private static final String SPACE = " ";  
    private static final String SPACE_FORMAT = "%20";  
    private static final String[] EXCLUDE_FILES = new String[] { ".git", "README.md" };  
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
    public static void setSysClipboardText(String writeMe) {  
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
  
# 生成 Markdown 的目录  
```java  
public class TOCUtils {  
    private static final String HEADER_STRING = "#";  
    private static final char HEADER_CHAR = '#';  
    private static final String TOC_TITLE = "";//在目录前插入的内容  
    private static final String REPLACE_TOC = "[TOC]"; //目录要替换的内容  
  
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
                insertTocIntoFile(from, from, REPLACE_TOC, Integer.MAX_VALUE);  
            }  
        }  
    }  
  
    /**  
     * 为文件生成目录  
     */  
    public static void insertTocIntoFile(File from, File to, String replaceAt, int deepLevel) {  
        BufferedReader reader = null;  
        try {  
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(from)));  
            String line;//当前行的内容  
            List<String> contentList = new ArrayList<>();//每一行的内容集合  
            List<TitleModel> modelList = new ArrayList<>();//标题集合  
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
                        continue;  
                    }  
                    String headerName = trimLineString.substring(count).trim(); //去掉层级后的文字  
                    modelList.add(new TitleModel(count, headerName, normalize(headerName)));  
                } else if (patternLineNumber <= 1 && line.trim().equals(replaceAt)) {  
                    patternLineNumber = currentLine; //找到这个字符串时就插在这一行(替换这个字符串)，找到之后就不再继续找了  
                    skipLine = true;//忽略这一行  
                }  
                if (!skipLine) {  
                    contentList.add(line);  
                }  
            }  
  
            if (patternLineNumber <= 0) {  
                System.out.println("----------------------------目录插入位置有误");  
                return;  
            }  
  
            contentList.add(patternLineNumber - 1, TOC_TITLE);//在指定位置插入目录前面的标题  
            for (TitleModel titleModel : modelList) {  
                contentList.add(patternLineNumber, titleModel.create());  
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
            System.out.println("----------------------------已完成 " + to.getAbsolutePath());  
        }  
    }  
  
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
  
    /**  
     * 字符串中字符的个数  
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
  
    private static final String CODES = "%([abcdef]|\\d){2}";  
    private static final String SPECIAL_CHARS = "[/?!:\\[\\]`.,()*\"';{}+=<>~$|#]";  
    private static final String EMPTY = "";  
  
    /**  
     * 替换特殊字符  
     */  
    public static String normalize(final String taintedURL) {  
        return taintedURL.trim().replaceAll(CODES, EMPTY).replaceAll(SPECIAL_CHARS, EMPTY).toLowerCase();  
    }  
  
    /**  
     * 插入的目录的模型  
     */  
    private static class TitleModel {  
        private static final String PATTERN = "%s- [%s](# %s)";  
        private static final String AFFIX = "\t";  
  
        private String headerName; //标题  
        private int deepLevel; //级别  
        private String headerLink; //链接  
  
        TitleModel(int deepLevel, String headerName, String headerLink) {  
            this.deepLevel = deepLevel;  
            this.headerName = headerName;  
            this.headerLink = headerLink;  
        }  
  
        String create() {  
            StringBuilder affixs = new StringBuilder();  
            if (deepLevel > 1) {  
                for (int i = 0; i < deepLevel - 1; i++) {  
                    affixs.append(AFFIX);  
                }  
            }  
            return String.format(PATTERN, affixs.toString(), headerName, headerLink);  
        }  
    }  
}  
```  
  
2018-10-24  
