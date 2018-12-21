| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Luban 鲁班 图片压缩  
***  
目录  
===  

- [介绍](#介绍)
- [turbo 版本](#turbo-版本)
- [算法步骤](#算法步骤)
- [使用方式](#使用方式)
- [案例](#案例)
  
# 介绍  
[GitHub](https://github.com/Curzibn/Luban)  
Luban(鲁班)—Image compression with efficiency very close to WeChat Moments/可能是最接近微信朋友圈的图片压缩算法  
```groovy  
implementation 'top.zibin:Luban:1.1.8'  
```  
  
目前做App开发总绕不开图片这个元素。但是随着手机拍照分辨率的提升，图片的压缩成为一个很重要的问题。单纯对图片进行裁切，压缩已经有很多文章介绍。但是裁切成多少，压缩成多少却很难控制好，裁切过头图片太小，质量压缩过头则显示效果太差。  
于是自然想到App巨头“微信”会是怎么处理，Luban就是通过在微信朋友圈发送近100张不同分辨率图片，对比原图与微信压缩后的图片逆向推算出来的压缩算法。  
因为有其他语言也想要实现Luban，所以描述了一遍[算法步骤](https://github.com/Curzibn/Luban/blob/master/DESCRIPTION.md)。  
因为是逆向推算，效果还没法跟微信一模一样，但是已经很接近微信朋友圈压缩后的效果，具体看以下对比！  
  
| 内容 | 原图 | Luban | Wechat |  
| :------------: | :------------: | :------------: | :------------: |  
| 截屏 720P | 720*1280,390k | 720*1280,87k | 720*1280,56k |  
| 截屏 1080P | 1080*1920,2.21M | 1080*1920,104k | 1080*1920,112k |  
| 拍照 13M(4:3) | 3096*4128,3.12M | 1548*2064,141k | 1548*2064,147k |  
| 拍照 9.6M(16:9) | 4128*2322,4.64M | 1032*581,97k | 1032*581,74k |  
| 滚动截屏 | 1080*6433,1.56M | 1080*6433,351k | 1080*6433,482k |  
  
  
# turbo 版本  
[GitHub](https://github.com/Curzibn/Luban/tree/turbo)  
libjpeg-turbo 是一个C语音编写的高效JPEG图像处理库，Android系统在7.0版本之前内部使用的是libjpeg非turbo版，并且为了性能关闭了Huffman编码。在7.0之后的系统内部使用了libjpeg-turbo库并且启用Huffman编码。  
使用本分支版本可使7.0之前的系统也用上压缩速度更快，压缩效果更好的libjpeg-turbo库，但是需引入额外的so文件，会增加最终打包后app文件的大小。  
本分支c语音源代码可查看此项目：[Luban-Turbo](https://github.com/Curzibn/Luban-Turbo)  
  
```groovy  
implementation 'top.zibin:Luban-turbo:1.0.0'  
```  
  
引入JNI动态文件  
1、在build.gradle中添加ndk配置  
```groovy  
android {  
    defaultConfig {  
        ndk {  
            abiFilters 'armeabi-v7a', 'arm64-v8a' //or x86、x86_64  
        }  
    }  
}  
```  
2、拷贝so文件到项目jniLibs文件夹  
Luban 提供4个平台的so文件：[`armeabi-v7a`](https://github.com/Curzibn/Luban/blob/turbo/library/src/main/jniLibs/armeabi-v7a/libluban.so)、 [`arm64-v8a`](https://github.com/Curzibn/Luban/blob/turbo/library/src/main/jniLibs/arm64-v8a/libluban.so)、 [`x86`](https://github.com/Curzibn/Luban/blob/turbo/library/src/main/jniLibs/x86/libluban.so)、 [`x86_64`](https://github.com/Curzibn/Luban/blob/turbo/library/src/main/jniLibs/x86_64/libluban.so)  
  
# 算法步骤  
注：下文所说“比例”统一表示：图片短边除以长边为该图片比例  
注：之前版本可以设置压缩等级，但最新版本只有第三挡压缩，参考最新版微信压缩效果  
  
```  
1、判断图片比例值，是否处于以下区间内；  
- [1, 0.5625) 即图片处于 [1:1 ~ 9:16) 比例范围内  
- [0.5625, 0.5) 即图片处于 [9:16 ~ 1:2) 比例范围内  
- [0.5, 0) 即图片处于 [1:2 ~ 1:∞) 比例范围内  
  
2、判断图片最长边是否过边界值；  
- [1, 0.5625) 边界值为：n=1时 1664 * n；n=2时 4990 * n；n≥3时 1280 * pow(2, n-1)  
- [0.5625, 0.5) 边界值为：n≥1时 1280 * pow(2, n-1)  
- [0.5, 0) 边界值为：n≥1时 1280 * pow(2, n-1)  
  
3、计算压缩图片实际边长值，以第2步计算结果为准，超过某个边界值则：width / pow(2, n-1)，height/pow(2, n-1)  
  
4、计算压缩图片的实际文件大小，以第2、3步结果为准，图片比例越大则文件越大。  
size = (newW * newH) / (width * height) * m；  
- [1, 0.5625) 则 width & height 对应 1664，4990，1280 * n（n≥3），m 对应 150，300，300；  
- [0.5625, 0.5) 则 width = 1440，height = 2560, m = 200；  
- [0.5, 0) 则 width = 1280，height = 1280 / scale，m = 500；注：scale为比例值  
  
5、判断第4步的size是否过小  
[1, 0.5625) 则最小 size 对应 60，60，100  
[0.5625, 0.5) 则最小 size 都为 100  
[0.5, 0) 则最小 size 都为 100  
  
6、将前面求到的值压缩图片 width, height, size 传入压缩流程，压缩图片直到满足以上数值  
  
```  
# 使用方式  
方法列表  
- load    传入原图  
- filter    [1.1.4版本增加]设置不压缩的条件  
- ignoreBy    设置不压缩的阈值，当原始图片大小小于此值时不压缩，单位为K，默认为 100K  
- setTargetDir    设置压缩后保存压缩图片的路径，不指定时存放在【/storage/emulated/0/Android/data/包名/cache/luban_disk_cache/保存文件的时间戳.--】  
- setCompressListener    压缩回调接口，实际发现，只有异步调用时才会回调，同步调用时设置此回调无意义  
- setRenameListener    [1.1.5版本增加]压缩前重命名接口，注意，参数 filePath 表示传入文件路径，返回值表示用来重命名后的文件名，使用时要留意这两者的区别  
- setFocusAlpha    [1.1.6版本增加]设置是否保留透明通道，保留通道压缩缓慢，不保留透明部分会被黑色背景代替，默认为true  
  
load 可以穿入的值  
- String：路径  
- File：文件  
- Uri：[1.1.4版本增加]统一资源识别符  
- InputStreamProvider：[1.1.4版本增加]通过此接口获取输入流，以兼容文件、FileProvider方式获取到的图片  
- List：集合，支持上面String、File、Uri这三种类型的集合，如果集合中某一文件不存在并不影响其他文件的压缩  
  
注意：对于 get 返回值，如果图片被压缩了，返回的是新生成的压缩图片的路径，否则返回的是原始图片的路径(文件不存在情况下也是如此)。  
  
# 案例  
```java  
public class LubanActivity extends ListActivity {  
    private static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pics/";  
    private static final String SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pics/tem/";  
    private List<String> paths = Arrays.asList(PATH + "pic.jpg", PATH + "icon.jpg", PATH + "icon.png", PATH + "flower.jpg");  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {  
                "异步调用",  
                "同步调用：批处理",  
                "同步调用：只处理一个",  
                "和 rxJava 一起使用",  
                "常用 API 的使用演示",  
        };  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
          
        File filePath = new File(SAVE_PATH);  
        if (!filePath.exists()) {  
            filePath.mkdirs();  
        }  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                Luban.with(this)  
                        .load(paths) //传入原图  
                        .setCompressListener(getListener())  
                        .launch();  
                break;  
            case 1:  
                try {  
                    List<File> fileList = Luban.with(this).load(paths).get();  
                    for (File file : fileList) {  
                        Log.i("bqt", file.length() / 1024 + "，" + file.getAbsolutePath());  
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
                break;  
            case 2:  
                try {  
                    String path = paths.get(0);  
                    File file1 = Luban.with(this).load(paths).get(path);//压缩了很多个，但只取指定其中一个压缩后的结果  
                    File file2 = Luban.with(this).load(path).get(path);//压缩了一个，取压缩后的结果  
                    Log.i("bqt", file1.length() / 1024 + "，" + file1.getAbsolutePath());  
                    Log.i("bqt", file2.length() / 1024 + "，" + file2.getAbsolutePath());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
                break;  
            case 3:  
                Flowable.just(paths)  
                        .observeOn(Schedulers.io()) //在子线程压缩  
                        .map(paths -> Luban.with(this).load(paths).get())//直接返回压缩后的文件  
                        .flatMap((Function<List<File>, Flowable<File>>) Flowable::fromIterable) //扁平化  
                        .subscribeOn(AndroidSchedulers.mainThread()) //回到主线程  
                        .subscribe(file -> Log.i("bqt", file.length() / 1024 + "，" + file.getAbsolutePath()));  
                break;  
            case 4:  
                try {  
                    List<File> fileList = Luban.with(this)  
                            .load(paths)  
                            .ignoreBy(1)//设置不压缩的阈值，当原始图片大小小于此值时不压缩，单位为K，默认为 100K  
                            .setFocusAlpha(true) //设置是否保留透明通道，设为 false 速度更快，但是可能有一个黑色的背景，默认为true  
                            .setTargetDir(SAVE_PATH) //设置压缩后保存压缩图片的路径  
                            .filter(path -> !(TextUtils.isEmpty(path) || path.endsWith("flower.jpg"))) //设置不压缩的条件  
                            .setCompressListener(getListener())  
                            .get();  
                    for (File file : fileList) {  
                        //如果图片被压缩了，返回的是新生成的压缩图片的路径，否则返回的是原始图片的路径  
                        Log.i("bqt", file.length() / 1024 + "，" + file.getAbsolutePath());  
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
                break;  
        }  
    }  
      
    @NonNull  
    private OnCompressListener getListener() {  
        return new OnCompressListener() { //内部采用IO线程进行图片压缩，外部调用只需设置好结果监听即可  
            @Override  
            public void onStart() {  
                Log.i("bqt", "【onStart】" + isMainThread());//true  
            }  
              
            @Override  
            public void onSuccess(File file) {  
                Log.i("bqt", "【onSuccess】" + isMainThread() + "，" + file.length() / 1024 + "，" + file.getAbsolutePath());//true  
            }  
              
            @Override  
            public void onError(Throwable e) {  
                e.printStackTrace();  
                Log.i("bqt", "【onError】" + e.getMessage());  
            }  
        };  
    }  
      
    private boolean isMainThread() {  
        return Looper.myLooper() == Looper.getMainLooper();  
    }  
}  
```  
  
2018-8-28  
