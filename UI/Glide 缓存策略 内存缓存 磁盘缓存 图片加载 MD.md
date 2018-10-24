| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Glide 缓存策略 内存缓存 磁盘缓存 图片加载 MD    
[官方文档](https://github.com/bumptech/glide/wiki/Caching-and-Cache-Invalidation)    
***  
目录  
===  
[TOC]  
  
> 本文主要介绍了如何配置和管理`Glide`中的缓存，其中大部分内容都可以直接在官方`Wiki`中找到，这里只是进行了整理和汇总。    
> 其实Glide只支持图片的二级缓存而不是三级缓存，因为从网络加载并不属于缓存，这二级缓存即内存缓存和磁盘缓存。    
  
# 磁盘缓存  
一般的图片缓存指的就是磁盘缓存，把网络上的图片缓存到本地，这样就不需要每次都从网络加载，既提高了加载速度，又为用户节省了流量。    
Glide在默认情况下是开启磁盘缓存的，而且提供了丰富的API来让开发者自己配置和管理磁盘缓存。  
  
**缓存位置和大小**    
开发者可以通过构建一个自定义的`GlideModule`来配置Glide磁盘缓存的位置和大小。最简单的方法如下：  
```java  
public class DiskCacheMoudle implements GlideModule {  
    @Override  
    public void applyOptions(Context context, GlideBuilder builder) {  
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glide_cache", 100 * 1024 * 1024));  
        //builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, "glide_cache", 100 * 1024 * 1024));  
    }  
    @Override  
    public void registerComponents(Context context, Glide glide) {  
    }  
}  
```  
  
> 其中第二个参数为缓存的目录名称，第三个参数为缓存大小，单位是Byte。  
> `InternalCache`构建的缓存是在应用的内部储存，而`ExternalCache`则是在外部储存。  
  
如果不想把缓存放在上面的两个位置怎么办？Glide当然也支持，具体通过`DiskLruCacheFactory`来实现：  
```java  
builder.setDiskCache(new DiskLruCacheFactory(new DiskLruCacheFactory.CacheDirectoryGetter() {  
    @Override  
    public File getCacheDirectory() {  
        return getMyCacheLocationBlockingIO();  
    }  
}), 100 * 1024 * 1024);  
```  
  
> Note: getMyCacheLocationBlockingIO方法返回的文件不能为空，而且必须是一个已经创建好的文件目录，不可以是文件。  
  
## 磁盘缓存策略  
与其他图片加载库的缓存机制不同，Glide缓存图片时默认只缓存最终加载的那张图片。    
举个栗子，你要加载的图片分辨率为1000x1000，但是最终显示该图片的ImageView大小只有500x500，那么Glide就会只缓存500x500的小图。    
这也是在从磁盘缓存中加载图片时Glide比Picasso快的原因。    
  
不过，你可以改变这种行为，让Glide既缓存全尺寸又缓存其他尺寸：  
```java  
Glide.diskCacheStrategy(DiskCacheStrategy.ALL)  
```  
  
Glide目前提供了四种缓存策略：  
- DiskCacheStrategy.NONE  不缓存文件  
- DiskCacheStrategy.SOURCE  只缓存原图  
- DiskCacheStrategy.RESULT  只缓存最终加载的图（默认的缓存策略）  
- DiskCacheStrategy.ALL  同时缓存原图和结果图  
  
## 磁盘缓存算法  
在Glide中磁盘缓存默认使用的是`LRU（Least Recently Used）`算法。如果你想使用其他的缓存算法，就只能通过实现`DiskCache`接口来完成了。  
  
# 内存缓存  
使用内存缓存可以获得更快的图片加载速度，因为减少了耗时的IO操作。    
众所周知，Bitmap是Android中的内存大户，频繁的创建和回收Bitmap必然会引起内存抖动。    
Glide中有一个叫做`BitmapPool`的类，可以复用其中的Bitmap对象，从而避免Bitmap对象的创建，减小内存开销。  
  
当配置内存缓存时，我们也应该同时配置BitmapPool的大小。具体方法也是通过自定义的GlideModule来实现的：  
```java  
builder.setMemoryCache(new LruResourceCache(yourSizeInBytes));  
builder.setBitmapPool(new LruBitmapPool(sizeInBytes));  
```  
  
一般情况下，开发者是不需要自己去指定它们的大小的，因为Glide已经帮我们做好了。    
默认的内存缓存和BitmapPool的大小由`MemorySizeCalculator`根据当前设备的屏幕大小和可用内存计算得到。   
此外Glide还支持动态的缓存大小调整，在存在大量图片的`Activity/Fragment`中，开发者可以通过`setMemoryCategory`方法来提高Glide的内存缓存大小，从而加快图片的加载速度。  
```java  
Glide.get(context)  
   .setMemoryCategory(MemoryCategory.HIGH);  
```  
  
MemoryCategory有3个值可供选择：  
- MemoryCategory.HIGH（初始缓存大小的1.5倍）  
- MemoryCategory.NORMAL（初始缓存大小的1倍）  
- MemoryCategory.LOW（初始缓存大小的0.5倍）  
  
在有些情况下我们不希望做内存缓存（比如加载GIF图片），这个时候可以调用`skipMemoryCache(true)`方法跳过内存缓存。  
  
## 如何缓存动态URL的图片  
一般情况下我们从网络上获取到的图片Url都是静态的，即一张图片对应一个Url。那么如果是一张图片对应多个Url呢？缓存不就没有意义了。因为图片加载库都是拿图片的Url来作为缓存的key的，Glide也不例外，只是会更加复杂一些。如果你开启了Glide的log，就会在控制台看到Glide是如何指定缓存key的。关于如何打开log，请参考[这篇文章](http://www.jianshu.com/p/9bd6efca8724)。一般来说，Glide的key由图片的url、view的宽和高、屏幕的尺寸大小和signature组成。  
  
在什么情况下才会出现动态的Url呢？一个很典型的例子就是因为图片的安全问题在原来图片的Url后面加上访问凭证。访问凭证与时间关联，这样一来，在不同时间同一图片的Url就会不同，缓存就会失效。以七牛的私有空间为例，我们来看看如何去缓存这类图片。从七牛关于私有空间的文档中可以得到：最终的Url = 原Url + ?e=过期时间 + token=下载凭证。那么就只需要在Glide缓存时将Url中“?”后面的字符串截去就可以了。  
  
首先新建一个叫做QiNiuImage的类：  
```java  
public class QiNiuImage {  
    private final String imageUrl;  
    public QiNiuImage(String imageUrl) {  
        this.imageUrl = imageUrl;  
    }  
    public String getImageUrl() {  
        return imageUrl;  
    }  
    public String getImageId() {  
        if (imageUrl.contains("?")) {  
            return imageUrl.substring(0, imageUrl.lastIndexOf("?"));  
        } else {  
            return imageUrl;  
        }  
    }  
}  
```  
其中getImageUrl方法返回真实的Url，getImageId方法返回未添加下载凭证前的Url。  
  
然后再自定义一个实现ModelLoader接口的QiNiuImageLoader:  
```java  
public class QiNiuImageLoader implements StreamModelLoader<QiNiuImage> {  
    @Override  
    public DataFetcher<InputStream> getResourceFetcher(final QiNiuImage model, int width, int height) {  
        return new HttpUrlFetcher(new GlideUrl(model.getImageUrl())) {  
            @Override  
            public String getId() {  
                return model.getImageId();  
            }  
        };  
    }  
    public static class Factory implements ModelLoaderFactory<QiNiuImage, InputStream> {  
        @Override  
        public ModelLoader<QiNiuImage, InputStream> build(Context context, GenericLoaderFactory factories) {  
            return new QiNiuImageLoader();  
        }  
        @Override  
        public void teardown() { /* no op */ }  
    }  
}  
```  
其中HttpUrlFetcher的getId方法就是组成缓存的key的重要部分。这也是我们的核心原理。  
  
将这个ModelLoader注册到GlideModule中，并在AndroidManifest.xml中注册:  
```java  
public class QiNiuModule implements GlideModule {  
    @Override  
    public void applyOptions(Context context, GlideBuilder builder) {  
    }  
    @Override  
    public void registerComponents(Context context, Glide glide) {  
        glide.register(QiNiuImage.class, InputStream.class, new QiNiuImageLoader.Factory());  
    }  
}  
```  
```xml  
<meta-data  
        android:name="com.yourpackagename.QiNiuModule"  
        android:value="GlideModule"/>  
```  
  
最后只需要在加载此类图片时，使用下面这段代码就可以了。即使图片的token更换了也不会重新从网络上下载而是直接读取本地缓存。  
```java  
Glide.with(context)  
      .load(new QiNiuImage(imageUrl)  
      .into(imageView);  
```  
2017-5-5  
