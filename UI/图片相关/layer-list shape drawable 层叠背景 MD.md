| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
layer-list shape drawable 层叠背景 MD  
***  
目录  
===  

- [layer-list 简介](#layer-list-简介)
	- [圆角矩形描边效果](#圆角矩形描边效果)
	- [案例1](#案例1)
		- [单一边线效果](#单一边线效果)
		- [双边线效果](#双边线效果)
		- [阴影效果](#阴影效果)
		- [选择器效果](#选择器效果)
	- [案例2](#案例2)
		- [圆环效果](#圆环效果)
		- [层叠效果](#层叠效果)
	- [案例3](#案例3)
		- [最后一个bitmap](#最后一个bitmap)
		- [最后一个 drawable](#最后一个-drawable)
  
# layer-list 简介  
[参考](http://blog.csdn.net/north1989/article/details/53485729)  
  
**layer-list 是啥**  
简单理解，layer 是层，list 是列表，那么 layer-list 就是层列表的意思。但是，是什么层列表呢？ 其实 layer-list 是用来创建 `LayerDrawable` 的，LayerDrawable 是 `DrawableResource` 的一种， 所以，layer-list 创建出来的图层列表，也就是一个 drawable 图形。  
  
因 layer-list 创建出来的也是 drawable 资源，所以，同 shape selector 一样，都是定义在 res 中的 `drawable` 文件夹中，也是一个 xml 文件。使用的时候，同shape selector , 布局文件中使用 `@drawable/ xxx` 引用, 代码中使用 `R.drawable.xxx` 引用。  
  
**layer-list 的大致原理**  
layer-list 的大致原理类似RelativeLayout或者FrameLayout ，也是一层层的叠加 ，`后添加的会覆盖先添加的`。在 layer-list 中可以通过控制后添加图层距离最底部图层的上下左右的四个边距、旋转等属性，得到不同的显示效果。  
  
## 圆角矩形描边效果  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">  
    <item>  
        <shape android:shape="rectangle">  
            <!--这里的目的是将四个角盖住，和下面的stroke的值要保持一致，否则圆角会被切割-->  
            <stroke  
                android:width="8dp"  
                android:color="#fff"/>  
        </shape>  
    </item>  
  
    <item>  
        <shape android:shape="rectangle">  
            <!--圆角的大小(不能太大，否则部分区域不能被遮挡)-->  
            <corners android:radius="12dp"/>  
  
            <!--圆角线条的宽度-->  
            <stroke  
                android:width="8dp"  
                android:color="#fff"/>  
        </shape>  
    </item>  
</layer-list>  
```  
  
## 案例1  
![](index_files/3a33a50d-6111-4c55-b4d9-ed58119f2cd4.png)  
  
### 单一边线效果  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">  
  
    <item>  
        <shape>  
            <solid android:color="#f00"/>  
        </shape>  
    </item>  
  
    <item android:top="1dp">  
        <shape>  
            <solid android:color="#fff"/>  
        </shape>  
    </item>  
</layer-list>  
```  
  
### 双边线效果  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">  
  
    <item>  
        <shape>  
            <solid android:color="#f00"/>  
        </shape>  
    </item>  
  
    <item  
        android:bottom="1dp"  
        android:top="1dp">  
        <shape>  
            <solid android:color="#fff"/>  
        </shape>  
    </item>  
</layer-list>  
```  
  
### 阴影效果  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">  
  
    <item  
        android:left="3dp"  
        android:top="6dp">  
        <shape>  
            <solid android:color="#b4b5b6"/>  
        </shape>  
    </item>  
  
    <item  
        android:bottom="6dp"  
        android:right="3dp">  
        <shape>  
            <solid android:color="#fff"/>  
        </shape>  
    </item>  
</layer-list>  
```  
  
### 选择器效果  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<selector xmlns:android="http://schemas.android.com/apk/res/android">  
  
    <item android:state_pressed="true">  
        <layer-list>  
            <item>  
                <color android:color="#f00"/>  
            </item>  
            <item android:bottom="2dp">  
                <color android:color="#fff"/>  
            </item>  
        </layer-list>  
    </item>  
  
    <item>  
        <layer-list>  
            <item>  
                <color android:color="#f00"/>  
            </item>  
            <item android:bottom="1dp">  
                <color android:color="#fff"/>  
            </item>  
        </layer-list>  
    </item>  
</selector>  
```  
  
## 案例2  
![](index_files/ea0b1581-8263-4e00-91b3-5034afd4047c.png)  
  
### 圆环效果  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">  
  
    <item>  
        <shape  
            android:dither="true"  
            android:shape="oval">  
            <solid android:color="#00f"/>  
            <stroke  
                android:width="1dp"  
                android:color="#fff"/>  
        </shape>  
    </item>  
  
    <item  
        android:bottom="10dp"  
        android:left="10dp"  
        android:right="10dp"  
        android:top="10dp">  
        <shape  
            android:shape="oval">  
            <solid android:color="#0f0"/>  
            <size  
                android:width="30dp"  
                android:height="30dp"/>  
            <stroke  
                android:width="1dp"  
                android:color="#fff"/>  
        </shape>  
    </item>  
  
    <item  
        android:bottom="20dp"  
        android:left="20dp"  
        android:right="20dp"  
        android:top="20dp">  
        <shape  
            android:shape="oval">  
            <solid android:color="#f00"/>  
            <size  
                android:width="30dp"  
                android:height="30dp"/>  
            <stroke  
                android:width="1dp"  
                android:color="#fff"/>  
        </shape>  
    </item>  
</layer-list>  
```  
  
### 层叠效果  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">  
    <item  
        android:left="20dp"  
        android:right="20dp">  
        <shape android:shape="rectangle">  
            <solid android:color="#ff0"/>  
            <corners  
                android:bottomLeftRadius="5dp"  
                android:bottomRightRadius="5dp"  
                android:topLeftRadius="5dp"  
                android:topRightRadius="5dp"/>  
        </shape>  
    </item>  
  
    <item  
        android:left="10dp"  
        android:right="10dp"  
        android:top="10dp">  
        <shape android:shape="rectangle">  
            <solid android:color="#0ff"/>  
            <corners  
                android:bottomLeftRadius="5dp"  
                android:bottomRightRadius="5dp"  
                android:topLeftRadius="5dp"  
                android:topRightRadius="5dp"/>  
        </shape>  
    </item>  
  
    <item android:top="20dp">  
        <shape android:shape="rectangle">  
            <solid android:color="#f00"/>  
            <corners  
                android:bottomLeftRadius="5dp"  
                android:bottomRightRadius="5dp"  
                android:topLeftRadius="5dp"  
                android:topRightRadius="5dp"/>  
        </shape>  
    </item>  
</layer-list>  
```  
  
## 案例3  
![](index_files/8c8edcd8-3391-4bd2-ba60-1c0b81b2f300.jpg)  
  
### 最后一个bitmap  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">  
    <!--不设置 gravity=center 时会自动缩放-->  
    <item>  
        <shape>  
            <solid android:color="#1000"/>  
        </shape>  
    </item>  
  
    <item android:gravity="center">  
        <bitmap android:src="@drawable/icon5"/>  
    </item>  
  
    <item android:gravity="left|top">  
        <bitmap android:src="@drawable/icon1"/>  
    </item>  
  
    <item android:gravity="right|top">  
        <bitmap android:src="@drawable/icon2"/>  
    </item>  
  
    <item android:gravity="right|bottom">  
        <bitmap android:src="@drawable/icon3"/>  
    </item>  
  
    <item android:gravity="left|bottom">  
        <bitmap android:src="@drawable/icon4"/>  
    </item>  
</layer-list>  
```  
  
### 最后一个 drawable  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">  
    <!--不设置 gravity=center 时会自动缩放-->  
    <item>  
        <shape>  
            <solid android:color="#1000"/>  
        </shape>  
    </item>  
  
    <item  
        android:bottom="15sp"  
        android:drawable="@drawable/icon5"  
        android:gravity="center"  
        android:left="15sp"  
        android:right="15sp"  
        android:top="15sp"/>  
  
    <item  
        android:bottom="30dp"  
        android:drawable="@drawable/icon1"  
        android:gravity="center"  
        android:right="30dp"/>  
  
    <item  
        android:bottom="30dp"  
        android:drawable="@drawable/icon2"  
        android:gravity="center"  
        android:left="30dp"/>  
  
    <item  
        android:drawable="@drawable/icon3"  
        android:gravity="center"  
        android:left="30dp"  
        android:top="30dp"/>  
  
    <item  
        android:drawable="@drawable/icon4"  
        android:gravity="center"  
        android:right="30dp"  
        android:top="30dp"/>  
</layer-list>  
```  
  
2018-8-8  
