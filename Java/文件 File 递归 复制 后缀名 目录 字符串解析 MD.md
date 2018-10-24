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

步骤一：
- 通过为知笔记导出指定目录下的所有文件，导出为U8格式的纯文本
- 通过 `modifyFileSuffix` 修改后缀名
- 通过 `getFormatFilesNames` 获取目录
- 将获取的内容粘贴到`REANME.MD`中就可以了

```java
modifyFileSuffix(new File(DIR));
getFormatFilesNames(new File(DIR));
```

步骤二：   
通过以上步骤操作之后发现有一个小问题，就是为知笔记里面的空格实际上不是空格，而是一个特殊符号，这个符号放在GitHub中不起作用，比如：
```java
System.out.println(" ".toCharArray()[0]+0);//160，MD中的空格
System.out.println(" ".toCharArray()[0]+0);//32，正常的空格
```

这就很坑爹了呀！所以优化了上面的步骤，采用下面的步骤：
- 通过为知笔记导出指定目录下的所有文件，导出为U8格式的纯文本
- 通过 `modifyFileSuffix` 修改后缀名
- 通过 `getFormatFilesNames` 获取目录
- 将获取的内容粘贴到`REANME.MD`中就可以了

```java
1
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
private static final String[] REPLACE_STRINGS = new String[] { " " };//特殊字符
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
    } else if (from.equals(to)) {
        throw new RuntimeException("源文件和目标文件是同一个");
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
    } else if (from.equals(to)) {
        throw new RuntimeException("源文件和目标文件是同一个");
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
    } else if (from.equals(to)) {
        throw new RuntimeException("源文件和目标文件是同一个");
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