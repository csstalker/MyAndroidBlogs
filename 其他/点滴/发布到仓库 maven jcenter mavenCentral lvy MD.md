| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
发布到仓库 maven jcenter mavenCentral lvy  
***  
目录  
===  

- [通过 AS 创建 jar 或 aar 的步骤](#通过-as-创建-jar-或-aar-的步骤)
- [发布到 Jcenter 仓库](#发布到-jcenter-仓库)
- [发布到 Github 仓库](#发布到-github-仓库)
  
# 通过 AS 创建 jar 或 aar 的步骤  
- 1、创建一个新的普通android工程(New Project 而不是 New Module)  
- 2、修改module层的build.gradle文件，删除 applicationId，将【apply plugin: 'com.android.application'】改成【apply plugin: 'com.android.library'】，然后同步  
- 3、执行 gradle 命令【gradle assembleRelease】，或者点击菜单 Build > `Make Project`   
- 4、执行完以后可以看到 `project-name/module-name/build/outputs/aar/` 文件夹下生成了.aar文件  
  
# 发布到 Jcenter 仓库  
[参考一](http://www.jcodecraeer.com/plus/view.php?aid=3097)  
[参考二](https://www.jianshu.com/p/0ba8960f80a9)  
  
# 发布到 Github 仓库  
第一步：在Github上创建一个仓库存放aar等文件，例如：  
`https://github.com/baiqiantao/aartest.git`  
  
第二步：把仓库clone到本地，并在此目录中创建你的库，编写你的代码，例如目录为：  
`D:\aartest`  
  
第三步：在项目中要打包为aar的build.gradle末尾添加以下代码：  
  
```groovy  
//**********************************************************************   打包发布  
apply plugin: 'maven'  
  
uploadArchives {  
    def GITHUB_REPO_PATH = "D:\\aartest"  // 从Github上clone下来的项目的本地地址，也是要保存的生成的aar目录的地址  
    repositories.mavenDeployer {  
        repository(url: "file://${file(GITHUB_REPO_PATH).absolutePath}")  
        pom.project {  //引用时的格式为【implementation 'com.bqt.aartest:blibrary:1.0.0'】  
            groupId 'com.bqt.aartest'  
            artifactId 'blibrary'  
            version '1.0.0'  
        }  
    }  
}  
  
// 和源代码一起打包  
task androidSourcesJar(type: Jar) {  
    classifier = 'sources'  
    from android.sourceSets.main.java.sourceFiles  
}  
artifacts {  
    archives androidSourcesJar  
}  
```  
  
第四步：构建并上传  
`在 Gradle > app > Tasks > upload > 双击 uploadArchives`  
  
或者使用命令行进入到项目的根目录后，执行以下命令：  
`gradlew uploadArchives`  
  
然后等待编译完成：  
```  
Executing tasks: [uploadArchives]  
...  
BUILD SUCCESSFUL in 6s  
26 actionable tasks: 23 executed, 3 up-to-date  
16:18:38: Task execution finished 'uploadArchives'.  
```  
  
第五步：上传源码及生成的文件  
编译完成后会在如下目录生成以下一些文件：  
  
  
我们将整个目录 add 到 github 仓库，然后提交(项目源码也可以一起提交)。  
  
第六步：使用此aar  
在需要使用此库的 project 的 build.gradle 中添加以下代码：  
```groovy  
allprojects {  
    repositories {  
        ...  
        maven { url "https://raw.githubusercontent.com/baiqiantao/aartest/master" } //baiqiantao 为用户名，aartest为项目名，其他为固定值  
    }  
}  
```  
  
在需要使用此库的 module 的 build.gradle 中添加如下依赖：  
```groovy  
implementation 'com.bqt.aartest:blibrary:1.0.0'  
```  
  
然后就可以愉快的使用库中的各种资源了：  
```java  
startActivity(new Intent(this, AARActivity.class));//库中的组件  
AARUtils.showToast(this, "库中的方法");  
imageView.setImageResource(R.drawable.icon_aar);//库中的资源  
```  
