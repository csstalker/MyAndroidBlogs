| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Glide Picasso Fresco UIL 图片框架 MD  
***  
目录  
===  

- [[Glide](https://github.com/bumptech/glide) Google推荐 24k](#glidehttpsgithubcombumptechglide-google推荐-24k)
	- [简介](#简介)
	- [GitHub 上的介绍](#github-上的介绍)
	- [基本使用](#基本使用)
	- [可以 load 的资源类型](#可以-load-的资源类型)
	- [Glide 缓存策略](#glide-缓存策略)
		- [磁盘缓存](#磁盘缓存)
		- [内存缓存](#内存缓存)
		- [缓存动态 URL 的图片](#缓存动态-url-的图片)
	- [图片质量](#图片质量)
	- [使用 GlideModule 定制 Glide](#使用-glidemodule-定制-glide)
	- [比 Picasso 强大的地方](#比-picasso-强大的地方)
	- [小结](#小结)
- [[Picasso](https://github.com/square/picasso) 16K](#picassohttpsgithubcomsquarepicasso-16k)
- [[Fresco](https://github.com/facebook/fresco) 15K](#frescohttpsgithubcomfacebookfresco-15k)
- [[Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader) 16K](#universal-image-loaderhttpsgithubcomnostra13android-universal-image-loader-16k)
  
# [Glide](https://github.com/bumptech/glide) Google推荐 24k  
[Glide 官方文档](https://github.com/bumptech/glide)  
[Glide 英文教程](https://futurestud.io/tutorials/glide-getting-started)   
[Glide 中文教程](https://mrfu.me/2016/02/27/Glide_Getting_Started/)   
  
## 简介  
我们要晓得：Glide是Google推荐的图片加载库，专注于流畅的滚动  
  
Glide 是 Google 一位员工的大作，他完全是基于 Picasso 的，沿袭了 Picasso 的简洁风格，但是在此做了大量优化与改进。  
  
Glide 默认的 Bitmap 格式是 `RGB_565` 格式，而 Picasso 默认的是 `ARGB_8888` 格式，相比而言，这个内存开销要小一半。  
  
在磁盘缓存方面，Picasso 只会缓存原始尺寸的图片，而 Glide 缓存的是多种规格，也就意味着 Glide 会根据你 ImageView 的大小来缓存相应大小的图片尺寸，比如你 ImageView 大小是 `200*200`，原图是 `400*400` ，而使用 Glide 就会缓存 `200*200` 规格的图，而 Picasso 只会缓存 `400*400` 规格的。这个改进就会导致 Glide 比 Picasso 加载的速度要快，毕竟少了每次裁剪重新渲染的过程。  
  
最重要的一个特性是 Glide 支持加载 `Gif` 动态图，而 Picasso 不支持该特性。  
  
除此之外，还有很多其他配置选项的增加。  
  
总体来说，Glide 是在 Picasso 基础之上进行的二次开发，各个方面做了不少改进，不过这也导致他的包比 Picasso 大不少，不过也就不到 500k，Picasso 是100多k，方法数也比 Picasso 多不少，不过毕竟级别还是蛮小的，影响不是很大。  
  
## GitHub 上的介绍  
> An image loading and caching library for Android focused on smooth scrolling  
  
> Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.  
  
> Glide supports fetching, decoding, and displaying video stills, images, and animated GIFs. Glide includes a flexible API that allows developers to plug in to almost any network stack. By default Glide uses a custom HttpUrlConnection based stack, but also includes utility libraries plug in to Google's Volley project or Square's OkHttp library instead.  
  
> Glide's primary首要的 focus is on making scrolling any kind of a list of images as smooth and fast as possible, but Glide is also effective for almost any case where you need to fetch, resize, and display a remote image.  
  
## 基本使用  
添加依赖：  
```java  
repositories {  
  mavenCentral()  
  google()  
}  
  
dependencies {  
  implementation 'com.github.bumptech.glide:glide:4.8.0'  
  annotationProcessor 'com.github.bumptech.glide:compiler:4.8.0'  
}  
```  
  
加载图片：  
```java  
Glide.with(context)  
    .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")  
    .into(ivImg);  
```  
  
> Glide的 with 方法不光接受 Context，还接受 Activity 和 Fragment。  
同时将 Activity/Fragment 作为 with() 参数的好处是：图片加载会和 Activity/Fragment 的生命周期保持一致，比如在 Paused 状态暂停加载，在 Resumed 的时候又自动重新加载。所以我建议传参的时候传递 Activity 和 Fragment 给Glide，而不是 Context。  
当然，如果使用 Application 作为上下文，Glide 请求将不受 Activity/Fragment 生命周期控制。  
  
总结  
- 优点：加载速度极快，框架体积小，四五百KB  
- 缺点：因为机制的选择不同，速度快了，但是图片质量低了，RGB565  
- 特点：根据ImageView大小来进行缓存，也就是说一张图片可能根据展示情况来缓存不同尺寸的几份  
  
## 可以 load 的资源类型  
Glide基本可以load任何可以拿到的媒体资源，如：   
```java  
load(String string)  
load SD卡资源：load("file://"+ Environment.getExternalStorageDirectory().getPath()+"/test.jpg")   
load assets资源：load("file:///android_asset/f003.gif")   
load raw资源：load("Android.resource://com.frank.glide/raw/raw_1")  
          或 load("android.resource://com.frank.glide/raw/"+R.raw.raw_1)   
load drawable资源：load("android.resource://com.frank.glide/drawable/news")  
               或 load("android.resource://com.frank.glide/drawable/"+R.drawable.news)   
load ContentProvider资源：load("content://media/external/images/media/139469")   
load http资源：load("http://img.my.csdn.net/uploads/201508/05/1438760757_3588.jpg")   
load https资源：load("https://img.alicdn.com/tps/TB1uyhoMpXXXXcLXVXXXXXXXXXX-476-538.jpg_240x5000q50.jpg_.webp")   
```  
  
当然，load不限于String类型，还可以：   
- load(File file) file：The File containing the image。这个文件可能不存在于你的设备中，然而你可以用任何文件路径，去指定一个图片路径。  
- load(Integer resourceId) resourceId：the id of the resource containing the image。可以用R.drawable或R.mipmap  
- load(Uri uri) uri：The Uri representing the image. Must be of a type handled by UriLoader  
- load(byte[] model) model：the data to load.  
- loadFromMediaStore(Uri uri) uri：The uri representing the media.  
- load(T model) model：The model the load.  
- ~~load(URL url)：deprecated~~  
  
load的资源也可以是本地视频，但如果是一个网络 URL 的视频，它是不工作的！  
  
从资源 id 转换成 Uri 的方法：  
```java  
public static Uri resourceIdToUri(Context context, int resourceId) {  
    return Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);  
}  
```  
  
## Glide 缓存策略  
[官方文档](https://github.com/bumptech/glide/wiki/Caching-and-Cache-Invalidation)   
  
> 本文主要介绍了如何配置和管理`Glide`中的缓存(内存缓存和磁盘缓存)，其中大部分内容都可以直接在官方`Wiki`中找到，这里只是进行了整理和汇总。  
  
Picasso 和 Glide 在磁盘缓存策略上有很大的不同。Picasso 缓存的是一个全尺寸的图片，而 Glide 缓存的是跟 ImageView 尺寸相同的图片，而且如果加载的是 RGB565 图片，那么缓存中的图片也是 RGB565。  
  
Picasso 只缓存一个全尺寸的。而 Glide 则不同，它会为每种大小的 ImageView 缓存 一次。尽管一张图片已经缓存了一次，但是假如你要在另外一个地方再次以不同尺寸显示，需要重新下载，调整成新尺寸的大小，然后将这个尺寸的也缓存起来。  
  
不过，你可以改变这种行为，让Glide既缓存全尺寸又缓存其他尺寸：  
```java  
Glide.with(this)  
     .load("http://nuuneoi.com/uploads/source/playstore/cover.jpg")  
     .diskCacheStrategy(DiskCacheStrategy.ALL)  
     .into(ivImgGlide);  
```  
下次在任何 ImageView 中加载图片的时候，全尺寸的图片将从缓存中取出，重新调整大小，然后缓存。  
  
Glide 的这种方式优点是加载显示非常快。而 Picasso 的方式则因为需要在显示之前重新调整大小而导致一些延迟。  
  
### 磁盘缓存  
**磁盘缓存位置和缓存大小**  
一般的图片缓存指的就是磁盘缓存，把网络上的图片缓存到本地，这样就不需要每次都从网络加载，既提高了加载速度，又为用户节省了流量。    
  
Glide在默认情况下是开启磁盘缓存的，而且提供了丰富的API来让开发者自己配置和管理磁盘缓存。  
  
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
  
> Note: `getMyCacheLocationBlockingIO` 方法返回的文件不能为空，而且必须是一个已经创建好的文件目录，不可以是文件。  
  
**磁盘缓存策略**  
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
  
**磁盘缓存算法**  
在Glide中磁盘缓存默认使用的是`LRU（Least Recently Used）`算法。如果你想使用其他的缓存算法，就只能通过实现`DiskCache`接口来完成了。  
  
### 内存缓存  
使用内存缓存可以获得更快的图片加载速度，因为减少了耗时的IO操作。    
  
众所周知，Bitmap是Android中的内存大户，频繁的创建和回收Bitmap必然会引起内存抖动。    
  
Glide中有一个叫做`BitmapPool`的类，可以复用其中的Bitmap对象，从而避免Bitmap对象的创建，减小内存开销。  
  
当配置内存缓存时，我们也应该同时配置`BitmapPool`的大小。具体方法也是通过自定义的`GlideModule`来实现的：  
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
  
### 缓存动态 URL 的图片  
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
  
  
## 图片质量  
如果将一张 `1920x1080` 像素的图片加载到 `768x432` 的 ImageView 中，会感觉 Glide 加载的图片质量要稍差于 Picasso，Glide 加载的图片没有 Picasso 那么平滑，这是因为 Glide 默认的 Bitmap 格式是 `RGB_565`，比 `ARGB_8888` 格式的内存开销要小一半。  
  
如果你通过创建一个新的 `GlideModule` 将 Bitmap 格式转换到 `ARGB_8888`，会发现 Picasso 的内存开销仍然远大于 Glide。原因在于 Picasso 是加载了全尺寸的图片到内存，然后让 GPU 来实时重绘大小。而 Glide 加载的大小和 ImageView 的大小是一致的，因此更小。  
  
当然，Picasso 也可以指定加载的图片大小的：  
```java  
Picasso.with(this)  
    .load("http://nuuneoi.com/uploads/source/playstore/cover.jpg")  
    .resize(768, 432)  
    .into(ivImgPicasso);  
```  
  
但是问题在于你需要主动计算 ImageView 的大小，或者说你的 ImageView 大小是具体的值（而不是wrap_content），你也可以这样：  
```java  
Picasso.with(this)  
    .load("http://nuuneoi.com/uploads/source/playstore/cover.jpg")  
    .fit()  
    .centerCrop()  
    .into(ivImgPicasso);  
```  
现在Picasso的内存开销就和Glide差不多了。  
  
虽然内存开销差距不大，但是在这个问题上 Glide 完胜 Picasso。因为 Glide 可以自动计算出任意情况下的 ImageView 大小。  
  
## 使用 GlideModule 定制 Glide  
[官方文档](https://github.com/bumptech/glide/wiki/Configuration )  
  
如果你对 Glide 默认的配置不满意，你可以创建一个新的 GlideModule 对 Glide 默认配置进行修改，比如上面说的将 Bitmap 格式转换到 `ARGB_8888`：  
```java  
public class GlideConfiguration implements GlideModule {  
    @Override  
    public void applyOptions(Context context, GlideBuilder builder) {  
        // Apply options to the builder here.  
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);  
    }  
   
    @Override  
    public void registerComponents(Context context, Glide glide) {  
        // register ModelLoaders here.  
    }  
}  
```  
GlideModule 用来在 Glide 单例创建之前应用所有的选项配置，该方法每次实现只会被调用一次。  
  
GlideModule接口的第二个方法`registerComponents(Context context, Glide glide)`用来在Glide单例创建之后但请求发起之前注册组件，该方法每次实现只会被调用一次。通常在该方法中注册 ModelLoader。  
GlideModule接口的第一个方法`applyOptions(Context context, GlideBuilder builder)`给你了一个 GlideBuilder 对象作为变量，这个 GlideBuilder 对象给你访问了 Glide 重要的核心组件，我们可以调用的方法有：  
  
1、setBitmapPool(BitmapPool bitmapPool)     Bitmap池  
```java  
builder.setBitmapPool(new LruBitmapPool(sizeInBytes));  
```  
Bitmap 池用来允许不同尺寸的 Bitmap 被重用，这可以显著地减少因为图片解码像素数组分配内存而引发的垃圾回收。默认情况下 Glide 使用 `LruBitmapPool` 作为 Bitmap 池，`LruBitmapPool` 采用LRU算法保存最近使用的尺寸的 Bitmap。我们可以通过它的构造器设置最大缓存内存大小。  
  
2、setMemoryCache(MemoryCache memoryCache)    内存缓存  
```java  
builder.setMemoryCache(new LruResourceCache(yourSizeInBytes));  
```  
`MemoryCache` 用来把 `resources` 缓存在内存里，以便能马上能拿出来显示。默认情况下 Glide 使用 `LruResourceCache`，我们可以通过它的构造器设置最大缓存内存大小。  
  
`MemoryCache` 和 `BitmapPool` 的默认大小由 `MemorySizeCalculator` 类决定，`MemorySizeCalculator` 会根据给定屏幕大小可用内存算出合适的缓存大小，这也是推荐的缓存大小，我们可以根据这个推荐大小做出调整。  
  
3、setDiskCache(DiskCache.Factory diskCacheFactory)    磁盘缓存  
设置一个用来创建 `DiskCache` 的工厂。默认情况下 Glide 使用 `InternalCacheDiskCacheFactory` 内部工厂类创建`DiskCache`，缓存目录为程序内部缓存目录`/data/data/your_package_name/image_manager_disk_cache/`(不能被其它应用访问)且缓存最大为`250MB`。  
  
当然，可以通过 `InternalCacheDiskCacheFactory` 构造器更改缓存的目录和最大缓存大小，如：  
```java  
builder.setDiskCache(new InternalCacheDiskCacheFactory(context,cacheDirectoryName, yourSizeInBytes));  
```  
  
还可以指定缓存到外部磁盘SD卡上：  
```java  
builder.setDiskCache(new ExternalCacheDiskCacheFactory(context,cacheDirectoryName, yourSizeInBytes));  
```  
  
4、setResizeService(ExecutorService service) 和 setDiskCacheService(ExecutorService service)  
设置一个用来检索 cache 中没有的 Resource 的 `ExecutorService` 实现。为了使缩略图请求正确工作，实现类必须把请求根据 `Priority` 优先级排好序。  
  
5、setDecodeFormat(DecodeFormat decodeFormat)    图片质量  
为所有的默认解码器设置解码格式。如 `DecodeFormat.PREFER_ARGB_8888`。默认是`DecodeFormat.PREFER_RGB_565`，因为相对于`ARGB_8888`的`4字节/像素`可以节省一半的内存，但不支持透明度且某些图片会出现条带。  
  
设置完以后，下一步我们需要全局的去声明这个类，让 Glide 知道它应该在哪里被加载和使用。Glide 会扫描 `AndroidManifest.xml` 为 `GlideModule` 的 `meta-data` 声明，因此，你必须在 `AndroidManifest.xml` 的 `<application>` 标签内去声明这个刚刚创建的 GlideModule。  
```XML  
<meta-data android:name="***.GlideConfiguration"  
           android:value="GlideModule"/>  
```  
  
请确保你将 `android:name` 属性改成你的`包名+类名`的形式。如果你想删掉 `GlideModule`，只需要把它从 `AndroidManifest.xml` 中移除就可以了，Java 类可以保存，说不定以后会用呢。如果它没有在 `AndroidManifest.xml` 中被引用，那它不会被加载或被使用。你可以同时声明多个 `GlideModule`。Glide 将会（没有特定顺序）得到所有的声明 module。  
  
## 比 Picasso 强大的地方  
- Glide 可以加载 GIF 动态图，而 Picasso 不能。同时因为 Glide 和 Activity/Fragment 的生命周期是一致的，因此 gif 的动画也会自动的随着 Activity/Fragment 的状态暂停、重放。Glide 的缓存在 gif 这里也是一样，调整大小然后缓存。  
- Glide 可以将任何的本地视频解码成一张静态图片。  
- 可以配置图片显示的动画，而 Picasso 只有一种动画：fading in。  
- 可以使用 thumbnail() 产生一个你所加载图片的 thumbnail。  
  
其实还有一些特性，不过不是非常重要，比如将图像转换成字节数组等。  
  
## 小结  
本人四个库都使用了一遍，对比后发现Fresco确实强大，加载大图 Fresco 最强，有的图 Glide 和 Picasso 加载不出来，换上 Fresco 妥妥的，不过 Fresco 比较庞大，推荐在主要都是图片的 app 中使用，一般的 app 使用 Glide 或 Picasso 就够了！  
  
Glide 和 Picasso 都是非常完美的库，Glide 加载图像以及磁盘缓存的方式都要优于 Picasso，速度更快，并且 Glide 更有利于减少 OutOfMemoryError 的发生。，GIF 动画是 Glide 的杀手锏，不过 Picasso 的图片质量更高。  
  
建议使用 Glide，如有需要，可以 将Bitmap 格式换成 ARGB_8888、让 Glide 缓存同时缓存全尺寸和改变尺寸两种。  
  
# [Picasso](https://github.com/square/picasso) 16K  
Picasso是Square公司开源的一个Android平台上的图片加载框架，简单易用，一句话搞定项目中的图片加载，好用到令人发指。相比而言，这个也算是一个出来时间比较长的框架了。  
  
使用示例：  
```java  
Picasso.with(this)  
    .load("url")  
    .placeholder(R.mipmap.ic_default)  
    .into(imageView);  
```  
  
添加依赖  
```java  
compile 'com.squareup.picasso:picasso:2.5.2'  
```  
  
A powerful image downloading and caching library for Android  
  
总结  
- 优点：图片质量高  
- 缺点：加载速度一般  
- 特点：只缓存一个全尺寸的图片，根据需求的大小在压缩转换  
  
# [Fresco](https://github.com/facebook/fresco) 15K  
FaceBook出品，支持Android 2.3 (Gingerbread)及以上  
  
尼玛，他竟然有专门的 [中文文档](https://www.fresco-cn.org/docs/index.html)  
  
Fresco 是 Facebook 出品的新一代的图片加载库，我们知道 Android 应用程序可用的内存有限，经常会因为图片加载导致 OOM，虽然我们有各种手段去优化，尽量减少出现 OOM 的可能性，但是永远没法避免，尤其某些低端手机 OOM 更是严重。而 Facebook 就另辟蹊径，既然没法在 Java 层处理，我们就在更底层的 Native 堆做手脚。于是 Fresco 将图片放到一个特别的内存区域叫 Ashmem 区，就是属于 Native 堆，图片将不再占用 App 的内存，Java 层对此无能为力，这里是属于 C++ 的地盘，所以能大大的减少 OOM。  
  
添加依赖  
```java  
compile 'com.facebook.fresco:fresco:1.2.0'  
  
//**************下面的依赖需要根据需求添加******************//  
// 在 API < 14 上的机器支持 WebP 时，需要添加  
compile 'com.facebook.fresco:animated-base-support:1.2.0'  
  
// 支持 GIF 动图，需要添加  
compile 'com.facebook.fresco:animated-gif:1.2.0'  
  
// 支持 WebP （静态图+动图），需要添加  
compile 'com.facebook.fresco:animated-webp:1.2.0'  
compile 'com.facebook.fresco:webpsupport:1.2.0'  
  
// 仅支持 WebP 静态图，需要添加  
compile 'com.facebook.fresco:webpsupport:1.2.0'  
```  
  
GitHub上的介绍：  
> Fresco is a powerful system for displaying images in Android applications.  
  
> Fresco takes care of image loading and display, so you don't have to. It will load images from the network, local storage, or local resources, and display a placeholder占位符 until the image has arrived. It has two levels of cache; one in memory and another in internal storage.  
  
> In Android 4.x and lower, Fresco puts images in a special region of Android memory. This lets your application run faster - and suffer the dreaded OutOfMemoryError much less often.  
  
> Fresco also supports:  
- streaming of progressive JPEGs  
- display of animated GIFs and WebPs  
- extensive广阔的 customization of image loading and display  
- and much more!  
  
Fresco 支持许多URI格式，但 Fresco 不支持 相对路径的URI。所有的 URI 都必须是绝对路径，并且带上该 URI 的 scheme。如下：  
  
| 类型 |                   SCHEME |    示例 |  
| :------------: | :------------: | :------------: |  
| 远程图片 |             http:// |    HttpURLConnection或者参考使用其他网络加载方案 |  
| 本地文件 |             file:// |    FileInputStream |  
| ContentProvider | content://  |  ContentResolver |  
| asset目录下的资源 | asset:// |    AssetManager |  
| res目录下的资源 | res:// |    Resources.openRawResource |  
| Uri中指定图片数据 | data:mime/type;base64 |    数据类型必须符合rfc2397规定(仅支持UTF-8) |  
  
总结  
- 优点：支持图像渐进式呈现，大公司出品，后期维护有保障  
- 缺点：框架体积较大，3M左右会增大apk的大小；操作方式不是特别简单，有一定学习成本  
- 特点：有两级内存一级文件的缓存机制，并且有自己特别的内存区域来处理缓存，避免oom  
  
# [Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader) 16K  
[jar包下载](https://raw.githubusercontent.com/nostra13/Android-Universal-Image-Loader/master/downloads/universal-image-loader-1.9.5.jar)  
  
UIL可以算是老牌最火的图片加载库了，使用过这个框架的项目可以说多到教你做人，我第一次把第三方开源图片加载框架加入项目中的就是这个了，当时感觉瞬间逼格上涨，妈妈再也不用担心出现OOM和ListView图片错乱了。  
  
Powerful and flexible library for loading, caching and displaying images on Android.  
  
> 尼玛，虽然这哥们很久基本都不更新了，而且还不提供Gradle的支持，但目前依然是星星最多的一个图片处理库  
  
**加载策略**  
- 每一个图片的加载和显示任务都运行在独立的线程中，除非这个图片缓存在【内存】中，这种情况下图片会立即显示。  
- 如果需要的图片缓存在【本地】，他们会开启一个独立的线程队列。  
- 如果在缓存中没有正确的图片，任务线程会从线程池中获取，因此，快速显示缓存图片时不会有明显的障碍。  
  
**控制OOM**  
- 减少线程池中线程的个数，在ImageLoaderConfiguration中的.threadPoolSize中配置，推荐配置1-5  
- 在DisplayImageOptions选项中配置bitmapConfig为Bitmap.Config.RGB_565，因为默认是ARGB_8888， 使用RGB_565会比使用ARGB_8888少消耗2倍的内存  
- 在ImageLoaderConfiguration中配置图片的内存缓存为memoryCache(new WeakMemoryCache()) 或者不使用内存缓存  
- 在DisplayImageOptions选项中设置.imageScaleType(ImageScaleType.IN_SAMPLE_INT)或者imageScaleType(ImageScaleType.EXACTLY)  
  
我们在使用该框架的时候尽量的使用`displayImage()`方法去加载图片，`loadImage()`是将图片对象回调到`ImageLoadingListener`接口的`onLoadingComplete()`方法中，需要我们手动去设置到`ImageView`上面，`displayImage()`方法中，对`ImageView`对象使用的是`WeakReferences`，方便垃圾回收器回收`ImageView`对象，如果我们要加载固定大小的图片的时候，使用`loadImage()`方法需要传递一个`ImageSize`对象，而`displayImage()`方法会根据`ImageView`对象的测量值，或者`android:layout_width`和`android:layout_height`设定的值，或者`android:maxWidth` 和/或 `android:maxHeight`设定的值来裁剪图片。  
  
总结  
- 优点：丰富的配置选项  
- 缺点：有不维护的趋势，可能被当前图片框架替代  
- 特点：三级缓存的策略  
  
2018-12-20  
