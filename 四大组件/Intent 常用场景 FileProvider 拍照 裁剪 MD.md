| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Intent 常用场景 FileProvider 拍照 裁剪  
***  
目录  
===  

- [常用的 Intent 场景](#常用的-intent-场景)
	- [拍照、选择照片、裁剪照片](#拍照、选择照片、裁剪照片)
		- [涉及到的权限](#涉及到的权限)
		- [需要配置 FileProvider](#需要配置-fileprovider)
		- [Activity](#activity)
		- [工具类](#工具类)
	- [其他简单场景](#其他简单场景)
		- [拨打电话](#拨打电话)
		- [发送短信](#发送短信)
		- [发送彩信](#发送彩信)
		- [打开浏览器](#打开浏览器)
		- [打开浏览器并搜索内容](#打开浏览器并搜索内容)
		- [发邮件](#发邮件)
		- [打开地图并定位到指定位置](#打开地图并定位到指定位置)
		- [路径规划](#路径规划)
		- [多媒体播放](#多媒体播放)
		- [打开应用在应用市场的详情页](#打开应用在应用市场的详情页)
		- [进入手机设置界面](#进入手机设置界面)
		- [安装和卸载 apk](#安装和卸载-apk)
		- [查看指定联系人](#查看指定联系人)
		- [调用系统编辑添加联系人](#调用系统编辑添加联系人)
		- [打开另一程序](#打开另一程序)
		- [强制和某QQ聊天](#强制和某qq聊天)
		- [打开录音机](#打开录音机)
  
# 常用的 Intent 场景  
## 拍照、选择照片、裁剪照片  
### 涉及到的权限  
```xml  
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>  
<uses-permission android:name="android.permission.CAMERA"/>  
```  
  
### 需要配置 FileProvider  
在清单文件中声明FileProvider：  
```xml  
<provider  
    android:name="android.support.v4.content.FileProvider"  
    android:authorities="${applicationId}.fileprovider"  
    android:exported="false"  
    android:grantUriPermissions="true">  
    <meta-data  
        android:name="android.support.FILE_PROVIDER_PATHS"  
        android:resource="@xml/file_paths"/>  
</provider>  
```  
  
在`res/xml/`下添加`file_paths.xml`配置文件  
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<paths>  
    <files-path  
        name="files"  
        path="/"/>  
    <cache-path  
        name="cache"  
        path="/"/>  
    <external-path  
        name="external"  
        path="/"/>  
    <external-files-path  
        name="external_file_path"  
        path="/"/>  
    <external-cache-path  
        name="external_cache_path"  
        path="/"/>  
    <!--<external-media-path  
        name="external-media-path"  
        path=""/>-->  
</paths>  
```  
  
### Activity  
```java  
public class MainActivity extends ListActivity {  
    public static final String PATH = Environment.getExternalStorageDirectory().getPath() + File.separator;  
    public static final String SAVE_FILE_NAME = "savePic.png";  
    public static final String CROP_FILE_NAME = "cropPic.png";  
      
    public static final int REQUEST_CODE_GET_IMG = 1;  
    public static final int REQUEST_CODE_SAVE_IMG = 2;  
    public static final int REQUEST_CODE_PICK_IMG = 3;  
      
    public static final int REQUEST_CODE_CROP_FALSE = 4;  
    public static final int REQUEST_CODE_CROP_TRUE = 5;  
      
    public static final int REQUEST_CODE_PICK_CROP_TRUE = 6;  
      
    private ImageView imageView;  
    private File saveFile;  
    private File cropFile;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {  
            "调用系统相机拍照，并返回拍摄的照片，获取的是裁剪后的小图",  
            "调用系统相机拍照，并保存照片到指定位置，保存的为原图",  
            "调用图库获取照片，并返回选择的照片",  
            "调用图库裁剪照片，并保存照片到指定位置",  
            "调用图库裁剪照片，并返回裁剪后的照片，不保证一定能成功",  
            "调用图库获取照片，并返回选择的照片，然后调用图库裁剪照片，并保存照片到指定位置",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
        imageView = new ImageView(this);  
        getListView().addFooterView(imageView);  
          
        saveFile = new File(PATH + SAVE_FILE_NAME);  
        cropFile = new File(PATH + CROP_FILE_NAME);  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0://调用系统相机拍照，并返回拍摄的照片，获取的是裁剪后的小图  
                Intent getCameraPicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
                startActivityForResult(getCameraPicIntent, REQUEST_CODE_GET_IMG);  
                break;  
            case 1://调用系统相机拍照，并保存照片到指定位置，保存的为原图  
                Uri saveUri = Utils.getFileUri(this, saveFile);  
                Intent saveCameraPicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
                saveCameraPicIntent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);  
                startActivityForResult(saveCameraPicIntent, REQUEST_CODE_SAVE_IMG);  
                break;  
            case 2: //调用图库获取照片，并返回选择的照片  
                Intent pickIntent = new Intent(Intent.ACTION_PICK);  
                pickIntent.setType("image/*");  
                startActivityForResult(pickIntent, REQUEST_CODE_PICK_IMG);  
                break;  
            case 3: //调用图库裁剪照片，并保存照片到指定位置  
                Uri dataUri = Utils.getFileUri(this, saveFile);  
                Intent cropPicIntent = Utils.getCropImageNotReturnDataIntent(dataUri, 400, 400, cropFile);  
                startActivityForResult(cropPicIntent, REQUEST_CODE_CROP_FALSE);  
                break;  
            case 4: //调用图库裁剪照片，并返回裁剪后的照片，不保证一定能成功，特别是大于 200 *200 时通常都会失败  
                Intent cropPicIntent2 = Utils.getCropImageReturnDataIntent(Utils.getFileUri(this, saveFile), 200, 200);  
                startActivityForResult(cropPicIntent2, REQUEST_CODE_CROP_TRUE);  
                break;  
            case 5: //调用图库获取照片，并返回选择的照片，然后调用图库裁剪照片，并保存照片到指定位置  
                Intent intent = new Intent(Intent.ACTION_PICK);  
                intent.setType("image/*");  
                startActivityForResult(intent, REQUEST_CODE_PICK_CROP_TRUE);  
                break;  
            default:  
                break;  
        }  
    }  
      
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        if (resultCode != RESULT_OK) {  
            Toast.makeText(this, "没有成功返回", Toast.LENGTH_SHORT).show();  
            return;  
        }  
        switch (requestCode) {  
            case REQUEST_CODE_GET_IMG://调用系统相机拍照，并返回拍摄的照片，获取的是裁剪后的小图  
                if (data != null && data.getExtras() != null) {  
                    Bitmap dataBitmap = (Bitmap) data.getExtras().get("data");// 注意，拍摄的照片会自动进行压缩，且会压缩的非常非常小！  
                    if (dataBitmap != null) {  
                        imageView.setImageBitmap(dataBitmap);//返回的是Bitmap，因为有大小限制，所以会压缩  
                        showToast("尺寸为" + dataBitmap.getWidth() + " * " + dataBitmap.getHeight()); //188 * 252  
                    }  
                }  
                break;  
            case REQUEST_CODE_SAVE_IMG://调用系统相机拍照，并保存照片到指定位置，保存的为原图  
                showToast("照片已保存到指定的Uri中");//注意，由于数据已保存到Uri中，所以这里返回的 data 为 null  
                imageView.setImageBitmap(BitmapFactory.decodeFile(saveFile.getAbsolutePath()));  
                break;  
            case REQUEST_CODE_PICK_IMG://调用图库获取照片，并返回选择的照片  
                if (data != null && data.getData() != null) {  
                    showToast("选择的照片：" + data.getData().toString());//【content://media/external/images/media/247778】  
                    imageView.setImageURI(data.getData());  
                }  
                break;  
            case REQUEST_CODE_CROP_FALSE: //调用图库裁剪照片，并保存照片到指定位置  
                showToast("剪切后的照片已保存到指定的Uri中");  
                imageView.setImageBitmap(BitmapFactory.decodeFile(cropFile.getAbsolutePath()));  
                break;  
            case REQUEST_CODE_CROP_TRUE: //调用图库裁剪照片，并返回裁剪后的照片，不保证一定能成功  
                if (data != null) {  
                    Bitmap cropBitmap = data.getParcelableExtra("data");  
                    if (cropBitmap != null) {  
                        imageView.setImageBitmap(cropBitmap);  
                        showToast("尺寸为" + cropBitmap.getWidth() + " * " + cropBitmap.getHeight());  
                    }  
                }  
                break;  
            case REQUEST_CODE_PICK_CROP_TRUE://调用图库获取照片，并返回选择的照片，然后调用图库裁剪照片，并保存照片到指定位置  
                if (data != null && data.getData() != null) {  
                    Intent cropPicIntent = Utils.getCropImageNotReturnDataIntent(data.getData(), 400, 400, cropFile);  
                    startActivityForResult(cropPicIntent, REQUEST_CODE_CROP_FALSE);  
                }  
                break;  
            default:  
                break;  
        }  
    }  
      
    private void showToast(String text) {  
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();  
    }  
}  
```  
  
### 工具类  
```java  
public class Utils {  
    public static Uri getFileUri(Context context, File saveFile) {  
        Uri uri;  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  
            String authority = context.getPackageName() + ".fileprovider"; //【清单文件中provider的authorities属性的值】  
            uri = FileProvider.getUriForFile(context, authority, saveFile);  
        } else {  
            uri = Uri.fromFile(saveFile);  
        }  
        Log.i("bqt", "【uri】" + uri);//【content://{$authority}/files/bqt/temp】或【file:///storage/emulated/0/bqt/temp】  
        return uri;  
    }  
      
    /**  
     * 调出图库APP的裁剪图片功能，将指定Uri中的图片裁剪为指定大小，裁剪的图片会在 onActivityResult 中的 intent 中返回  
     * 经过测试发现，这种方式在 7.0 之后不能用了！  
     */  
    public static Intent getCropImageReturnDataIntent(Uri dataUri, int desWidth, int desHeight) {  
        Intent intent = getCropImageIntent(dataUri, desWidth, desHeight);  
        //设置为true时，裁剪的图片不会在给定的 uri 中返回，而会在 onActivityResult 中的 intent 中返回  
        intent.putExtra("return-data", true);  
        return intent;  
    }  
      
    /**  
     * 调出图库APP的裁剪图片功能，将指定Uri中的图片裁剪为指定大小，裁剪的图片会保存到指定的 uri 中  
     */  
    public static Intent getCropImageNotReturnDataIntent(Uri dataUri, int desWidth, int desHeight, File cropFile) {  
        Intent intent = getCropImageIntent(dataUri, desWidth, desHeight);  
        //设置为false时，裁剪的图片会保存到指定的 uri 中，而不会在 onActivityResult 中的 intent 中返回  
        intent.putExtra("return-data", false);  
        // 注意：若裁减时打开图片的 uri 与保存图片的 uri 相同，会产生冲突，导致裁减完成后图片的大小变成0Byte  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropFile));  
        return intent;  
    }  
      
    private static Intent getCropImageIntent(Uri dataUri, int desWidth, int desHeight) {  
        Intent intent = new Intent("com.android.camera.action.CROP");//指定action是使用系统图库裁剪图片  
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //必须加这个临时权限  
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);  
        intent.setDataAndType(dataUri, "image/*");  
        intent.putExtra("crop", "true");//可裁剪  
        intent.putExtra("aspectX", 1);//设置裁剪比例及裁剪图片的具体宽高  
        intent.putExtra("aspectY", 1);  
        intent.putExtra("outputX", desWidth);  
        intent.putExtra("outputY", desHeight);  
        intent.putExtra("scale", true);//设置是否允许拉伸  
        intent.putExtra("scaleUpIfNeeded", true);//没什么作用  
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//设置输出格式  
        intent.putExtra("noFaceDetection", true);//设置是否需要人脸识别  
        return intent;  
    }  
}  
```  
  
## 其他简单场景  
### 拨打电话  
```java  
intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:10086"));//打开系统默认拨号程序，不需要权限  
intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:10000"));//直接呼叫号码，权限：CALL_PHONE  
startActivity(intent);  
```  
  
### 发送短信  
```java  
Uri uri = Uri.parse("smsto:10086");  
Intent intent = new Intent(Intent.ACTION_SENDTO, uri);  
intent.putExtra("sms_body", "Hello");  
startActivity(intent);  
```  
  
### 发送彩信  
```java  
Intent intent = new Intent(Intent.ACTION_SEND);  
intent.putExtra("sms_body", "Hello");  
Uri uri = Uri.parse("content://media/external/images/media/23");//或Uri.parse("file:///sdcard/a.png")  
intent.putExtra(Intent.EXTRA_STREAM, uri);  
intent.setType("image/png");  
startActivity(intent);  
```  
  
### 打开浏览器  
```java  
Uri uri = Uri.parse("http://www.baidu.com");  
Intent intent  = new Intent(Intent.ACTION_VIEW, uri);  
startActivity(intent);  
```  
  
### 打开浏览器并搜索内容  
```java  
Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);  
intent.putExtra(SearchManager.QUERY,"searchString");//通过浏览器默认搜索引擎搜索内容  
startActivity(intent);  
```  
  
### 发邮件  
使用默认邮件应用的默认账户发邮件  
```java  
Uri uri = Uri.parse("mailto:someone@domain.com");  
Intent intent = new Intent(Intent.ACTION_SENDTO, uri);  
startActivity(intent);  
```  
  
详细设置  
```java  
Intent intent=new Intent(Intent.ACTION_SEND);  
String[] tos = {"1@abc.com", "2@abc.com"};   
String[] ccs = {"3@abc.com", "4@abc.com"};  
String[] bccs = {"5@abc.com", "6@abc.com"};   
intent.putExtra(Intent.EXTRA_EMAIL, tos);// 收件人  
intent.putExtra(Intent.EXTRA_CC, ccs); // 抄送  
intent.putExtra(Intent.EXTRA_BCC, bccs);// 密送  
intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");//主题  
intent.putExtra(Intent.EXTRA_TEXT, "Hello");//内容  
intent.putExtra(Intent.EXTRA_STREAM, "file:///sdcard/eoe.mp3");//附件  
intent.setType("message/rfc822"); //或 "text/plain" 或 "audio/mp3"  
startActivity(intent);  
```  
  
### 打开地图并定位到指定位置  
```java  
Uri uri = Uri.parse("geo:39.9,116.3");  
Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
startActivity(intent);  
```  
  
### 路径规划  
```java  
Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=39.9 116.3&daddr=31.2 121.4");//从A地到B地  
Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
startActivity(intent);  
```  
  
### 多媒体播放  
```java  
Intent intent = new Intent(Intent.ACTION_VIEW);  
Uri uri = Uri.parse("file:///sdcard/foo.mp3");  
intent.setDataAndType(uri, "audio/mp3");//类型Type可以指定为 audio/x-mpeg ，或者不指定也行  
startActivity(intent);  
```  
  
获取SD卡下所有音频文件，然后播放第一首  
```java  
Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");  
Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
startActivity(intent);  
```  
  
### 打开应用在应用市场的详情页  
```java  
Uri uri = Uri.parse("market://details?id=" + "包名"); //搜索应用为 Uri.parse("market://search?q=pname:包名");  
Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
startActivity(intent);  
```  
  
### 进入手机设置界面  
```java  
Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);  
startActivityForResult(intent, 0);  
```  
  
### 安装和卸载 apk  
安装 apk  
```java  
Intent intent = new Intent(Intent.ACTION_VIEW);  
intent.setDataAndType(Uri.parse("file://" + path + "a.apk"), "application/vnd.android.package-archive");  
startActivity(intent);  
```  
  
卸载apk  
```java  
Uri uri = Uri.fromParts("package", strPackageName, null);  
Intent it = new Intent(Intent.ACTION_DELETE, uri);  
startActivity(it);  
```  
  
### 查看指定联系人  
```java  
Uri personUri = ;  
Intent intent = new Intent(Intent.ACTION_VIEW);  
intent.setData(ContentUris.withAppendedId(People.CONTENT_URI, info.id));//联系人ID  
startActivity(intent);  
```  
  
### 调用系统编辑添加联系人  
```java  
Intent it = newIntent(Intent.ACTION_INSERT_OR_EDIT);      
it.setType(Contacts.CONTENT_ITEM_TYPE);  
it.putExtra(Contacts.Intents.Insert.NAME, "My Name");  
it.putExtra(Contacts.Intents.Insert.COMPANY, "organization");      
it.putExtra(Contacts.Intents.Insert.EMAIL,"email");      
it.putExtra(Contacts.Intents.Insert.EMAIL_TYPE, Contacts.ContactMethodsColumns.TYPE_WORK);      
it.putExtra(Contacts.Intents.Insert.PHONE,"homePhone");      
it.putExtra(Contacts.Intents.Insert.PHONE_TYPE,Contacts.PhonesColumns.TYPE_MOBILE);      
it.putExtra(Contacts.Intents.Insert.SECONDARY_PHONE,"mobilePhone");      
it.putExtra(Contacts.Intents.Insert.TERTIARY_PHONE,"workPhone");      
it.putExtra(Contacts.Intents.Insert.JOB_TITLE,"title");      
startActivity(it);  
```  
  
### 打开另一程序  
```java  
Intent intent = new Intent();  
intent.setComponent(new ComponentName(context.getPackageName(), "com.xiaomi.mipushdemo.MainActivity"));  
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
//intent.setAction("android.intent.action.MAIN");  
startActivity(intent);  
```  
  
方式2  
```java  
boolean exists =new File("/data/data/" + "包名").exists();//判断是否安装目标应用  
if (exists) {  
    Intent intent = getPackageManager().getLaunchIntentForPackage("包名");  
    startActivity(intent);  
}  
```  
  
### 强制和某QQ聊天  
```java  
String qqNo = "909120849";  
String qqUri = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNo + "&version=1";  
Uri uri = Uri.parse(qqUri);  
intent = new Intent(Intent.ACTION_VIEW, uri);//跳转到QQ，并直接进入到和指定QQ号码聊天的界面，且可以是陌生人的QQ号  
startActivity(intent);  
```  
  
### 打开录音机  
```java  
Intent mi = new Intent(Media.RECORD_SOUND_ACTION);  
startActivity(mi);  
```  
  
2018-11-17  
