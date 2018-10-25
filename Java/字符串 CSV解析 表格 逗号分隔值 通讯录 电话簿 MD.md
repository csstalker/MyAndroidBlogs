| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
字符串 CSV解析 表格 逗号分隔值 通讯录 电话簿 MD    
***  
目录  
===  

- [CSV文件简介](#CSV文件简介)
- [解析工具类](#解析工具类)
	- [数据格式：](#数据格式：)
	- [工具类](#工具类)
	- [数据模型](#数据模型)
  
# CSV文件简介  
>    
逗号分隔值：`Comma-Separated Values`，CSV，有时也称为`字符分隔值`，因为分隔字符也可以不是逗号。    
逗号分隔值文件`以纯文本形式存储表格数据`。纯文本意味着该文件是一个字符序列，不含必须像二进制数字那样被解读的数据。    
  
CSV文件由任意数目的`记录`组成，记录间以某种`换行符`分隔；每条记录由`字段`组成，字段间的`分隔符`是其它字符或字符串，最常见的是`逗号或制表符`。    
通常，所有记录都有完全相同的字段序列。通常都是纯文本文件。建议使用`记事本`来开启，再则先另存新档后用EXCEL开启，也是方法之一。    
  
CSV是一种通用的、相对简单的文件格式，被用户、商业和科学广泛应用。最广泛的应用是`在程序之间转移表格数据`，而这些程序本身是在不兼容的格式上进行操作的（往往是私有的和/或无规范的格式）。因为大量程序都支持某种CSV变体，至少是作为一种可选择的`输入/输出`格式。    
  
> CSV文件格式的通用标准并不存在，但是在`RFC 4180`中有基础性的描述。使用的字符编码同样没有被指定，但是`7-bitASCII`是最基本的通用编码。    
  
CSV并不是一种单一的、定义明确的格式，在实践中，术语 CSV 泛指具有以下特征的任何文件：  
- 纯文本，使用某个字符集，比如ASCII、Unicode、EBCDIC或GB2312；  
- 由记录组成（典型的是每行一条记录）；  
- 每条记录被分隔符分隔为字段（典型分隔符有逗号、分号或制表符；有时分隔符可以包括可选的空格）；  
- 每条记录都有同样的字段序列。  
  
> 在这些常规的约束条件下，存在着许多CSV变体，故CSV文件`并不完全互通`。然而，这些变异非常小，并且有许多应用程序允许用户`预览`文件（这是可行的，因为它是纯文本），然后`指定分隔符、转义规则`等。如果一个特定CSV文件的变异过大，超出了特定接收程序的支持范围，那么可行的做法往往是`人工检查并编辑文件`，或通过简单的程序来修复问题。因此在实践中，CSV文件还是非常方便的。  
  
# 解析工具类  
## 数据格式：  
```csv  
微信号,bqt20094,这里是密码,邮箱#909120849@qq.com,QQ#909120849,  
```  
  
## 工具类  
```java  
//解析方式之所以定义成这样，是为了兼容我所使用的一款叫"密码本子"的APP  
public class CsvUtils {  
    private static final String COMMA = ",";  
    private static final String SEPARATOR = "#";  
    private static final String LINE_SEPARATOR = File.separator;  
    private static final String ENCODING = "GBK";  
      
    public static void obj2CsvFils(List<CsvBean> dataList, File file) {  
        String content = obj2String(dataList);  
        writeFile(content, file);  
    }  
      
    public static List<CsvBean> csvFils2Obj(String filePath) {  
        String content = readFile(filePath);  
        return string2Obj(content);  
    }  
      
    private static String obj2String(List<CsvBean> dataList) {  
        StringBuilder sb = new StringBuilder();  
        for (CsvBean cvsBean : dataList) {  
            sb.append(cvsBean.name).append(COMMA).append(cvsBean.account).append(COMMA);  
            if (isNotEmpty(cvsBean.password)) sb.append(cvsBean.password);//密码有可能为空  
            sb.append(COMMA);  
            if (cvsBean.other != null && !cvsBean.other.keySet().isEmpty()) {  
                for (String key : cvsBean.other.keySet()) {  
                    String value = cvsBean.other.get(key);  
                    if (isNotEmpty(value)) sb.append(key).append(SEPARATOR).append(value).append(COMMA);  
                }  
            }  
            sb.append(LINE_SEPARATOR);  
        }  
        return sb.toString();  
    }  
      
    private static void writeFile(String content, File file) {  
        try {  
            FileOutputStream writer = new FileOutputStream(file);  
            writer.write(content.getBytes(ENCODING));  
            writer.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
    private static List<CsvBean> string2Obj(String content) {  
        if (content == null) return null;  
          
        String[] array = content.split(LINE_SEPARATOR);  
        List<CsvBean> list = new ArrayList<CsvBean>();  
        for (String string : array) {  
            int index = string.indexOf(COMMA);  
            if (index >= 0) {  
                CsvBean bean = new CsvBean();  
                bean.name = string.substring(0, index);  
                string = string.substring(index + 1);  
                index = string.indexOf(COMMA);  
                if (index >= 0) {  
                    bean.account = string.substring(0, index);  
                    string = string.substring(index + 1);  
                    index = string.indexOf(COMMA);  
                    if (index >= 0) {  
                        bean.password = string.substring(0, index);  
                        string = string.substring(index + 1);  
                        for (int i = 0; i <= string.length(); i++) {  
                            if (string.endsWith(",")) string = string.substring(0, string.length() - 1);  
                            else break;  
                        }  
                        if (string.length() > 0) {  
                            String[] otherStrings = string.split(COMMA);  
                            bean.other = new HashMap<>();  
                            for (String other : otherStrings) {  
                                String[] keyValue = other.split(SEPARATOR);  
                                if (keyValue.length >= 2) {  
                                    bean.other.put(keyValue[0], keyValue[1]);  
                                }  
                            }  
                        }  
                    } else {  
                        bean.password = string;  
                    }  
                } else {  
                    bean.account = string;  
                }  
                list.add(bean);  
            }  
        }  
        return list;  
    }  
      
    private static String readFile(String filePath) {  
        File file = new File(filePath);  
        byte[] temp = new byte[(int) file.length()];  
        try {  
            FileInputStream in = new FileInputStream(file);  
            in.read(temp);  
            in.close();  
            return new String(temp, ENCODING);  
        } catch (IOException e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
      
    private static boolean isNotEmpty(String string) {  
        return string != null && string.trim().length() > 0 && !string.equalsIgnoreCase("null");  
    }  
}  
```  
  
## 数据模型  
```java  
//数据结构之所以定义成这样，是为了兼容我所使用的一款叫"密码本子"的APP  
public class CsvBean {  
    public String name;//必选项  
    public String account;//必选项  
    public String password;//可选项  
    public HashMap<String, String> other;//可选项  
    @Override  
    public String toString() {  
        return "CvsBean [name=" + name + ", account=" + account + ", password=" + password + ", other=" + other + "]";  
    }  
}  
```  
  
2018-9-8  
