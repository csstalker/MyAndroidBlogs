| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
***  
目录  
===  

- [加密算法简介](#加密算法简介)
	- [加密算法的分类](#加密算法的分类)
	- [加密算法的选择](#加密算法的选择)
- [常见的几种加密算法](#常见的几种加密算法)
	- [DES](#DES)
	- [3DES](#3DES)
	- [AES](#AES)
	- [RSA](#RSA)
- [使用示例](#使用示例)
	- [3DES 加解密示例](#3DES-加解密示例)
	- [3DES 加解密的一个小疑惑](#3DES-加解密的一个小疑惑)
	- [AES 加解密示例](#AES-加解密示例)
	- [RSA 加解密及验证签名示例](#RSA-加解密及验证签名示例)
  
# 加密算法简介  
数据加密的基本过程就是对原来为明文的文件或数据按某种算法进行处理，使其成为不可读的一段代码，通常称为【密文】，使其只能在输入相应的【密钥】之后才能显示出本来的内容，通过这样的途径来达到保护数据不被非法人窃取、阅读的目的。  
  
该过程的逆过程为解密，即将该编码信息转化为其原来数据的过程。  
  
## 加密算法的分类  
加密算法通常分为对称性加密算法和非对称性加密算法  
  
**对称加密算法**  
信息接收双方都需事先知道密匙和加解密算法且其密匙是相同的，之后便是对数据进行加解密了。  
- DES：DES是一种分组数据加密技术(先将数据分成固定长度的小数据块，之后进行加密)，速度较快，适用于大量数据加密  
- 3DES：3DES是一种基于DES的加密算法，使用3个不同密匙对同一个分组数据块进行3次加密，如此以使得密文强度更高  
- AES：相较于DES和3DES而言，AES有着更高的速度和资源使用效率，安全级别也较之更高了，被称为下一代加密标准  
  
**非对称加密算法**  
发送双方A、B事先均自行生成一对密匙，然后【A将自己的公钥发送给B】【B将自己的公有密匙发送给A】，如果A要给B发送消息，则用B的公钥对消息进行加密，然后发送给B，此时B再用自己的私钥进行消息解密，B向A发送消息时为同样的道理。由于A、B的私钥不需要再网络上传输，所以就避免了密钥被泄漏的风险。  
  
它的优越性就在这里，如果对称式的加密方法是在网络上传输加密文件，就很难不把密钥告诉对方，不管用什么方法告诉对方，密钥都有可能被别窃听到。而非对称式的加密方法有两个密钥，且其中的“公钥”是可以公开的，也就不怕别人知道，收件人解密时只要用【自己的私钥】即可，这样就很好地避免了密钥的传输安全性问题。  
  
> 注意：MD5,SHA1,HMAC是线性散列算法，也即签名算法，一般不算做加密算法  
  
## 加密算法的选择  
由于对称加密算法的`密钥管理`是一个复杂的过程，密钥的管理直接决定着他的安全性，因此当数据量很小时，我们可以考虑采用非对称加密算法  
  
在实际的操作过程中，我们通常采用的方式是：采用【非对称加密算法】加密【对称算法的密钥】，然后用【对称加密算法】加密【数据】，这样我们就集成了两类加密算法的优点，既实现了加密速度快的优点，又实现了安全方便管理密钥的优点。  
  
# 常见的几种加密算法  
  
常见加密算法的特点：  
- DES（Data Encryption Standard，数据加密标准）：对称算法，速度较快，适用于加密大量数据的场合  
- 3DES（Triple DES，三倍DES）：是基于DES的对称算法，对一块数据用三个不同的密钥进行三次加密，强度更高  
- AES（Advanced Encryption Standard，高级加密标准）：对称算法，是下一代的加密算法标准，速度快，安全级别高，在21世纪AES标准的一个实现是Rijndael算法  
- RC2和RC4：对称算法，用变长密钥对大量数据进行加密，比 DES 快  
- IDEA（International Data Encryption Algorithm）国际数据加密算法，使用 128 位密钥提供非常强的安全性  
- RSA：（由三个发明者的姓氏开头字母拼在一起组成的），非对称算法是一个支持变长密钥的公共密钥算法，需要加密的文件块的长度也是可变的  
- BLOWFISH，它使用变长的密钥，长度可达448位，运行速度很快；  
- DSA（Digital Signature Algorithm）：数字签名算法，是一种标准的 DSS（数字签名标准），严格来说不算加密算法  
- MD5：（Message Digest Algorithm 5，消息摘要算法第五版）严格来说不算加密算法，只能说是摘要算法  
- PKCS：（The Public-Key Cryptography Standards，公钥密码学标准）美国RSA数据安全公司及其合作伙伴制定的一组公钥密码学标准  
- SSF33，SSF28，SCB2(SM1)：国家密码局的隐蔽不公开的商用算法，在国内民用和商用的，除这些都不容许使用外，其他的都可以使用  
  
## DES  
DES全称为Data Encryption Standard，即数据加密标准，1977年被美国联邦政府的国家标准局确定为联邦资料处理标准（FIPS），并授权在非密级政府通信中使用，随后该算法在国际上广泛流传开来。需要注意的是，在某些文献中，作为算法的DES称为数据加密算法（Data Encryption Algorithm,DEA），已与作为标准的DES区分开来。  
  
DES的原始思想可以参照二战德国的恩尼格玛机，其基本思想大致相同。传统的密码加密都是由古代的循环移位思想而来，恩尼格玛机在这个基础之上进行了扩散模糊，但是本质原理都是一样的。现代DES在二进制级别做着同样的事：替代模糊，增加分析的难度。  
  
**DES算法的设计原则**  
DES设计中使用了分组密码设计的两个原则：混淆（confusion）和扩散(diffusion)，其目的是抗击敌手对密码系统的统计分析。【混淆】是使密文的统计特性与密钥的取值之间的关系尽可能复杂化，以使密钥和明文以及密文之间的依赖性对密码分析者来说是无法利用的。【扩散】的作用就是将每一位明文的影响尽可能迅速地作用到较多的输出密文位中，以便在大量的密文中消除明文的统计结构，并且使每一位密钥的影响尽可能迅速地扩展到较多的密文位中，以防对密钥进行逐段破译。  
  
**DES算法的原理**  
DES 使用一个 56 位的密钥以及附加的 8 位奇偶校验位（每组的第8位作为奇偶校验位），产生最大 64 位的分组大小。这是一个迭代的分组密码，使用称为 Feistel 的技术，其中将加密的文本块分成两半。使用子密钥对其中一半应用循环功能，然后将输出与另一半进行“异或”运算；接着交换这两半，这一过程会继续下去，但最后一个循环不交换。DES 使用 16 轮循环，使用异或，置换，代换，移位操作四种基本运算。  
  
**DES算法的安全性**  
目前只有一种方法可以破解该算法，那就是穷举法，即重复尝试各种密钥直到有一个符合为止。如果 DES 使用 56 位的密钥，则可能的密钥数量是 2 的 56 次方个。随着计算机系统能力的不断发展，DES 的安全性比它刚出现时会弱得多，然而从非关键性质的实际出发，仍可以认为它是足够的。不过 ，DES 现在仅用于旧系统的鉴定，而更多地选择新的加密标准 — 高级加密标准（Advanced Encryption Standard，AES）。  
  
## 3DES  
3DES又称Triple DES，是DES加密算法的一种模式，由于计算机运算能力的增强，原版DES密码的密钥长度变得容易被暴力破解，3DES即是设计用来提供一种相对简单的方法，即通过增加DES的密钥长度（3*56）来避免类似的攻击，而不是设计一种全新的块密码算法。如果三个 56 位的子元素都相同，则三重 DES 向后兼容 DES。  
  
3DES是DES向AES过渡的加密算法（1999年，NIST将3DES指定为过渡的加密标准），它使用3条56位的密钥对数据进行三次加密，是DES的一个更安全的变形。它以DES为基本模块，通过组合分组方法设计出分组加密算法。比起最初的DES，3DES更为安全。  
  
3DES使用【两个】密钥，执行三次DES算法，加密的过程是【加密-解密-加密】，解密的过程是【解密-加密-解密】。  
设Ek()和Dk()代表DES算法的加密和解密过程，K代表DES算法使用的密钥，P代表明文，C代表密文，则  
- 3DES加密过程为：C=Ek3(Dk2(Ek1(P)))  
- 3DES解密过程为：P=Dk1(EK2(Dk3(C)))  
  
3DES采用两个密钥进行三重加密的好处有：  
- 两个密钥合起来有效密钥长度有112bit，可以满足商业应用的需要，若采用总长为168bit的三个密钥，会产生不必要的开销。  
- 加密时采用加密-解密-加密，而不是加密-加密-加密的形式，这样有效的实现了与现有DES系统的向后兼容问题。因为当K1=K2时，三重DES的效果就和原来的DES一样，有助于逐渐推广三重DES。  
- 三重DES具有足够的安全性，目前还没有关于攻破3DES的报道。  
  
## AES  
AES（Advanced Encryption Standard，高级加密标准），在密码学中又称Rijndael加密法，是美国联邦政府采用的一种区块加密标准。这个标准用来替代原先的DES，已经被多方分析且广为全世界所使用。经过五年的甄选流程，高级加密标准由美国国家标准与技术研究院（NIST）于2001年11月26日发布于FIPS PUB 197，并在2002年5月26日成为有效的标准。2006年，高级加密标准已然成为对称密钥加密中最流行的算法之一。  
  
**形成过程**  
1997年4月15日，美国ANSI发起征集AES（advanced encryption standard）的活动，并为此成立了AES工作小组。  
1997年9月12日，美国联邦登记处公布了正式征集AES候选算法的通告。对AES的基本要求是： 比三重DES快、至少与三重DES一样安全、数据分组长度为128比特、密钥长度为128/192/256比特。  
1998年8月12日，在首届AES候选会议上公布了AES的15个候选算法，任由全世界各机构和个人攻击和评论。  
1999年3月，在第2届AES候选会议上经过对全球各密码机构和个人对候选算法分析结果的讨论，从15个候选算法中选出了5个。分别是RC6、Rijndael、SERPENT、Twofish和MARS。  
2000年4月13日至14日，召开了第3届AES候选会议，继续对最后5个候选算法进行讨论。  
2000年10月2日，NIST宣布Rijndael作为新的AES。经过3年多的讨论，Rijndael终于脱颖而出。  
Rijndael由比利时的Joan Daemen和Vincent Rijmen设计。算法的原型是Square算法，它的设计策略是宽轨迹策略。算法有很好的抵抗差分密码分析及线性密码分析的能力。  
  
**加密标准**  
对称密码体制的发展趋势将以分组密码为重点。分组密码算法通常由密钥扩展算法和加解密算法两部分组成。密钥扩展算法将b字节用户主密钥扩展成r个子密钥。加密算法由一个密码学上的弱函数f与r个子密钥迭代r次组成。混乱和密钥扩散是分组密码算法设计的基本原则。抵御已知明文的差分和线性攻击，可变长密钥和分组是该体制的设计要点。  
  
AES加密数据块分组长度必须为128比特，密钥长度可以是128比特、192比特、256比特中的任意一个（如果数据块及密钥长度不足时，会补齐）。  
在应用方面，尽管DES在安全上是脆弱的，但由于快速DES芯片的大量生产，使得DES仍能暂时继续使用，为提高安全强度，通常使用独立密钥的三级DES。但是DES迟早要被AES代替。流密码体制较之分组密码在理论上成熟且安全，但未被列入下一代加密标准。  
  
## RSA  
RSA【公钥加密算法】是【1977】年由罗纳德·李维斯特（Ron Rivest）、阿迪·萨莫尔（Adi Shamir）和伦纳德·阿德曼（Leonard Adleman）一起提出的,RSA就是他们三人姓氏开头字母拼在一起组成的。RSA是目前最有影响力的公钥加密算法，它能够抵抗到目前为止已知的绝大多数密码攻击，已被ISO推荐为公钥数据加密标准。今天只有短的RSA钥匙才可能被强力方式解破。到2008年为止，世界上还没有任何可靠的攻击RSA算法的方式。只要其钥匙的长度足够长，用RSA加密的信息实际上是不能被解破的。但在分布式计算和量子计算机理论日趋成熟的今天，RSA加密安全性受到了挑战。  
  
**背景知识**  
所谓的公开密钥密码体制就是，使用不同的加密密钥与解密密钥，是一种"由已知【加密密钥】推导出【解密密钥】在计算上是不可行的"密码体制。在公开密钥密码体制中，加密密钥（即公开密钥）PK是公开信息，而解密密钥（即秘密密钥）SK是需要保密的。加密算法E和解密算法D也都是公开的。虽然SK是由PK决定的，但却不能根据PK计算出SK。  
正是基于这种理论，1978年出现了著名的RSA算法。  
RSA算法是第一个能同时用于加密和数字签名的算法，也易于理解和操作。  
RSA是被研究得最广泛的公钥算法，从提出到现今的三十多年里，经历了各种攻击的考验，逐渐为人们接受，普遍认为是目前最优秀的公钥方案之一。  
  
**对密钥长度的要求**  
SET(Secure Electronic Transaction)协议中要求CA采用2048比特的密钥，其他实体使用1024比特的密钥。RSA密钥长度随着保密级别提高，增加很快。  
  
为减少计算量，在传送信息时，常采用传统加密方法与公开密钥加密方法相结合的方式，即【信息】采用改进的DES或IDEA对话密钥加密，然后使用RSA密钥加密【对话密钥和信息摘要】。对方收到信息后，用不同的密钥解密并可核对信息摘要。  
  
**基本原理**  
RSA算法基于一个十分简单的数论事实：将两个【大质数】相乘十分容易，但是想要对其乘积进行【因式分解】却极其困难，因此可以将乘积公开作为加密密钥。  
RSA的算法涉及三个参数，n、e1、e2。其中，n是两个大质数p、q的积，n的二进制表示时所占用的位数，就是所谓的密钥长度。  
质数又称素数，定义为：在大于1的自然数中，除了1和它本身以外不再有其他因数的数。  
  
**缺点**  
- 产生密钥很麻烦，受到素数产生技术的限制，因而难以做到一次一密。  
- RSA的安全性依赖于大数分解，但是否等同于大数分解一直未能得到理论上的证明，因为没有证明破解RSA就一定需要作大数分解。不过RSA 的一些变种算法已被证明等价于大数分解。  
- 速度太慢。由于进行的都是大数计算，使得RSA最快的情况也比DES慢上好几倍，无论是软件还是硬件实现。RSA的速度比对应同样安全级别的对称密码算法要慢1000倍左右，且随着大数分解技术的发展，这个长度还在增加，不利于数据格式的标准化。  
  
# 使用示例  
## 3DES 加解密示例  
  
打印日志：  
```  
密钥(24字节)：[7, 86, 58, 63, -29, -69, -25, -29, -70, -124, 67, 26, -39, -48, 85, -81, 7, 86, 58, 63, -29, -69, -25, -29]  
生成的密文：[-49, 24, 35, -6, -105, 54, 126, -35, 38, -70, 42, -60, -80, 86, 120, 78]  
通过Base64编码为ASCII字符后传输的密文：zxgj+pc2ft0muirEsFZ4Tg==  
收到的密文：[-49, 24, 35, -6, -105, 54, 126, -35, 38, -70, 42, -60, -80, 86, 120, 78]  
解密后的内容：爱老虎油  
```  
  
测试代码  
```java  
public class DESedeUtils {  
  
    private static final String ALGORITHM_MD5 = "md5";  
    private static final String ALGORITHM_DESEDE = "DESede";// 加密算法，可用 DES，DESede，Blowfish  
    private static final String CHARSET = "UTF-8";  
  
    public static void main(String[] args) throws Exception {  
        byte[] key = get3DESKeyBytes("521");// 用于生成密钥  
        System.out.println("密钥(" + key.length + "字节)：" + Arrays.toString(key));// 密钥的长度必须是24个字节，否则加解密异常  
        byte[] source = "爱老虎油".getBytes(CHARSET);  
        test(source, key);  
    }  
  
    private static void test(byte[] src, byte[] key) {  
        try {  
            byte[] encryptedBytes = encryptOrDecrypt(src, key, Cipher.ENCRYPT_MODE);  
            System.out.println("生成的密文：" + Arrays.toString(encryptedBytes));  
  
            String encryptedBase64String = Base64.getEncoder().encodeToString(encryptedBytes);  
            System.out.println("通过Base64编码为ASCII字符后传输的密文：" + encryptedBase64String);  
  
            byte[] receivedEncryptedBytes = Base64.getDecoder().decode(encryptedBase64String.getBytes(CHARSET));  
            System.out.println("收到的密文：" + Arrays.toString(receivedEncryptedBytes));  
  
            byte[] target = encryptOrDecrypt(receivedEncryptedBytes, key, Cipher.DECRYPT_MODE);  
            System.out.println("解密后的内容：" + new String(target, CHARSET));  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /**  
     * 加密或解密。加密和解密用的同一个算法和密钥  
     *   
     * @param src  要加密或解密的数据  
     * @param key  密钥。密钥长度必须为24，不能多也不能少，否则抛【java.security.InvalidKeyException: Wrong key size】  
     * @param mode 加密或解密模式。值请选择Cipher.DECRYPT_MODE或Cipher.ENCRYPT_MODE  
     * @return 加密或解密后的数据  
     */  
    public static byte[] encryptOrDecrypt(byte[] src, byte[] key, int mode) throws Exception {  
        Cipher cipher = Cipher.getInstance(ALGORITHM_DESEDE);// 加密算法，可用 DES，DESede，Blowfish  
        Key key3DES = new SecretKeySpec(key, ALGORITHM_DESEDE);  
        cipher.init(mode, key3DES);  
        return cipher.doFinal(src);  
    }  
  
    /**  
     * 根据字符串生成3DES的密钥字节数组(这个方法的目的只是保证密钥长度为24，具体的算法可以任意)  
     */  
    public static byte[] get3DESKeyBytes(String sKey) throws Exception {  
        MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);  
        md.update(sKey.getBytes(CHARSET));  
        byte[] digestOfPassword = md.digest(); // 长度为16，一个字节(byte)占8位(bit)，所以共占16*8=128bit  
  
        // Java中要求3des的密钥必须为24字节(192bit)，因为长度不满足要求，所以需要再补充8字节。注意c代码的话长度只要16字节。  
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24); // 将长度为16的数组拷贝到一个长度为24的数组中  
        for (int j = 0, k = 16; j < 8;) {  
            keyBytes[k++] = keyBytes[j++]; // 用前8个元素对应补全后8个元素(这一步是非必须的，只要能保证密钥长度为24字节即可)  
        }  
        return keyBytes;  
    }  
}  
```  
  
## 3DES 加解密的一个小疑惑  
  
打印日志：  
```  
[49, 49, 50, 50, 52, 52, 55, 55, 56, 56, 44, 44, 44, 44, 49, 49, 50, 50, 52, 52, 55, 55, 56, 56]  
[48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 45, 45, 45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57]  
  
加密前【虽然两个Key并不相同，但是却可以用一个加密用另一个解密】  
解密后【虽然两个Key并不相同，但是却可以用一个加密用另一个解密】  
```  
  
测试代码  
```java  
public class Test {  
    private static final String ALGORITHM_DESEDE = "DESede";  
    private static final String CHARSET = "UTF-8";  
  
    public static void main(String[] args) throws Exception {  
        byte[] keyBytes = "0123456789----0123456789".getBytes(CHARSET);  
        SecretKey key1 = SecretKeyFactory.getInstance(ALGORITHM_DESEDE).generateSecret(new DESedeKeySpec(keyBytes));  
        SecretKeySpec key2 = new SecretKeySpec(keyBytes, ALGORITHM_DESEDE);  
        System.out.println(Arrays.toString(key1.getEncoded()) + "\n" + Arrays.toString(key2.getEncoded()) + "\n");  
  
        byte[] source = "虽然两个Key并不相同，但是却可以用一个加密用另一个解密".getBytes(CHARSET);  
        byte[] cryptograph = encryptOrDecrypt(source, key1, Cipher.ENCRYPT_MODE); // 用第一个Key加密  
        byte[] target = encryptOrDecrypt(cryptograph, key2, Cipher.DECRYPT_MODE); // 用第二个Key解密  
  
        System.out.println("加密前【" + new String(source, CHARSET) + "】\n解密后【" + new String(target, CHARSET) + "】");  
    }  
  
    public static byte[] encryptOrDecrypt(byte[] src, Key key3DES, int mode) throws Exception {  
        Cipher cipher = Cipher.getInstance(ALGORITHM_DESEDE);  
        cipher.init(mode, key3DES); //估计问题的原因在于 init 方法  
        return cipher.doFinal(src);  
    }  
}  
```  
  
## AES 加解密示例  
  
使用起来，除了密钥长度不一样，其他和3DES没任何区别。  
  
打印日志：  
```  
密钥(16字节)：[-60, -54, 66, 56, -96, -71, 35, -126, 13, -52, 80, -102, 111, 117, -124, -101]  
生成的密文：[-110, -22, -114, -82, 9, -106, 123, -72, 23, 50, 2, 111, -24, -83, 48, 8]  
通过Base64编码为ASCII字符后传输的密文：kuqOrgmWe7gXMgJv6K0wCA==  
收到的密文：[-110, -22, -114, -82, 9, -106, 123, -72, 23, 50, 2, 111, -24, -83, 48, 8]  
解密后的内容：爱老虎油  
```  
  
测试代码  
```java  
public class AESUtils {  
      
    private static final String ALGORITHM_MD5 = "md5";  
    private static final String CHARSET = "UTF-8";  
    private static final String ALGORITHM_AES = "AES";  
  
    public static void main(String[] args) throws Exception {  
        byte[] key = getAESKeyBytes("1");// 要求密钥必须是16位的  
        System.out.println("密钥(" + key.length + "字节)：" + Arrays.toString(key));// 密钥的长度必须是24个字节，否则加解密异常  
        byte[] source = "爱老虎油".getBytes(CHARSET);  
  
        byte[] encryptedBytes = encryptOrDecrypt(source, key, Cipher.ENCRYPT_MODE);  
        System.out.println("生成的密文：" + Arrays.toString(encryptedBytes));  
  
        String encryptedBase64String = Base64.getEncoder().encodeToString(encryptedBytes);  
        System.out.println("通过Base64编码为ASCII字符后传输的密文：" + encryptedBase64String);  
  
        byte[] receivedEncryptedBytes = Base64.getDecoder().decode(encryptedBase64String.getBytes(CHARSET));  
        System.out.println("收到的密文：" + Arrays.toString(receivedEncryptedBytes));  
  
        byte[] target = encryptOrDecrypt(receivedEncryptedBytes, key, Cipher.DECRYPT_MODE);  
        System.out.println("解密后的内容：" + new String(target, CHARSET));  
    }  
  
    /**  
     * 加密或解密。加密和解密用的同一个算法和密钥  
     *   
     * @param source 要加密或解密的数据  
     * @param key    密钥。密钥长度必须为16，不能多也不能少，否则抛【InvalidKeyException: Invalid AES key length】  
     * @param mode   加密或解密模式。值请选择Cipher.DECRYPT_MODE或Cipher.ENCRYPT_MODE  
     * @return 加密或解密后的数据  
     */  
    public static byte[] encryptOrDecrypt(byte[] source, byte[] key, int mode) throws Exception {  
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES); // 加密算法，可用 AES  
        Key keyAES = new SecretKeySpec(key, ALGORITHM_AES);  
        cipher.init(mode, keyAES);  
        return cipher.doFinal(source);  
    }  
  
    /**  
     * 根据字符串生成AES的密钥字节数组(这个方法的目的只是保证密钥长度为16，具体的算法可以任意)  
     */  
    public static byte[] getAESKeyBytes(String sKey) throws Exception {  
        MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);  
        md.update(sKey.getBytes(CHARSET));  
        return md.digest();  
    }  
}  
```  
  
## RSA 加解密及验证签名示例  
  
打印日志：  
```java  
生成的密文：[-116, -121, 24, -119, -56, -106, -22, -9, -49, 73, 122, 119, -3, -123, 105, -17, -100, 91, 93, -91, 95, 62, -81, -70, 79, -5, -103, -82, 24, 78, -41, 122, 106, -78, 26, 35, 1, 81, -7, 106, -27, -86, 39, 99, -67, -109, 70, -47, 11, -62, 46, -39, 70, 105, -84, 97, -108, -91, -58, 122, 42, 24, -95, -44, 35, -47, -42, -90, -118, -113, 29, 108, 94, -124, 12, -30, -4, -86, 103, -87, 105, -106, 56, -84, -114, 19, 35, -86, 88, 110, 51, 55, 82, 22, -118, 20, -91, 37, 34, -31, -56, 50, 50, 68, -21, 15, 51, 14, -2, 65, 25, 102, 88, -99, -70, -24, -94, 20, 15, -11, 89, -88, -8, 75, -111, -35, 77, -55]  
通过Base64编码为ASCII字符后传输的密文：jIcYiciW6vfPSXp3/YVp75xbXaVfPq+6T/uZrhhO13pqshojAVH5auWqJ2O9k0bRC8Iu2UZprGGUpcZ6Khih1CPR1qaKjx1sXoQM4vyqZ6lpljisjhMjqlhuMzdSFooUpSUi4cgyMkTrDzMO/kEZZliduuiiFA/1Waj4S5HdTck=  
收到的密文：[-116, -121, 24, -119, -56, -106, -22, -9, -49, 73, 122, 119, -3, -123, 105, -17, -100, 91, 93, -91, 95, 62, -81, -70, 79, -5, -103, -82, 24, 78, -41, 122, 106, -78, 26, 35, 1, 81, -7, 106, -27, -86, 39, 99, -67, -109, 70, -47, 11, -62, 46, -39, 70, 105, -84, 97, -108, -91, -58, 122, 42, 24, -95, -44, 35, -47, -42, -90, -118, -113, 29, 108, 94, -124, 12, -30, -4, -86, 103, -87, 105, -106, 56, -84, -114, 19, 35, -86, 88, 110, 51, 55, 82, 22, -118, 20, -91, 37, 34, -31, -56, 50, 50, 68, -21, 15, 51, 14, -2, 65, 25, 102, 88, -99, -70, -24, -94, 20, 15, -11, 89, -88, -8, 75, -111, -35, 77, -55]  
解密后的内容：爱老虎油  
签名数据[73, 17, -106, 78, 113, -41, -95, 63, 20, 20, 11, 39, 3, -59, -97, 123, 78, 109, 77, -42, 77, -124, -113, -71, -20, 33, 75, 17, 120, -43, -60, -57, 49, 123, 96, -67, -42, -101, -14, 82, 77, 75, 50, -93, -76, -46, -42, -36, -18, -24, 121, 58, 92, 31, -84, 46, 42, -94, 84, 11, -89, -22, 10, -25, -41, -61, 24, 19, -59, -57, 27, 112, 106, 85, -60, 110, -124, 22, -125, -125, -23, -10, 127, -3, -59, -41, 6, -57, 78, 29, -13, -87, -99, 104, 86, 20, 38, -127, 87, 111, 3, -46, -104, -105, 34, -122, -83, 18, -108, -15, -51, 19, 127, -23, -114, 40, -87, -103, -67, 114, -18, -66, 3, 13, 97, -45, 12, 60]  
签名是否正确：true  
```  
  
测试代码  
```java  
//RSA非对称加密算法。用法：1 公钥加密、私钥解密；2 私钥签名、公钥验证签名。  
public class RSAUtils {  
  
    public static final String ALGORITHM = "RSA"; // 加密算法  
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA"; // 签名算法  
    public static final int KEYSIZE = 1024; // key的长度  
    public static final String CHARSET = "UTF-8";  
  
    public static void main(String[] args) throws Exception {  
        byte[] source = "爱老虎油".getBytes();  
  
        // *******************************测试加解密*******************************  
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);  
        kpg.initialize(KEYSIZE, new SecureRandom());  
        KeyPair keyPair = kpg.generateKeyPair(); // 生成密匙对  
  
        byte[] encryptedBytes = encryptOrDecrypt(source, keyPair.getPublic(), Cipher.ENCRYPT_MODE);// 公钥加密  
        System.out.println("生成的密文：" + Arrays.toString(encryptedBytes));  
  
        String encryptedBase64String = Base64.getEncoder().encodeToString(encryptedBytes);  
        System.out.println("通过Base64编码为ASCII字符后传输的密文：" + encryptedBase64String);  
  
        byte[] receivedEncryptedBytes = Base64.getDecoder().decode(encryptedBase64String.getBytes(CHARSET));  
        System.out.println("收到的密文：" + Arrays.toString(receivedEncryptedBytes));  
  
        byte[] target = encryptOrDecrypt(receivedEncryptedBytes, keyPair.getPrivate(), Cipher.DECRYPT_MODE);// 私钥解密  
        System.out.println("解密后的内容：" + new String(target, CHARSET));  
  
        // *******************************测试验证签名*******************************  
        byte[] signature = sign(source, keyPair.getPrivate()); // 私钥签名  
        System.out.println("签名数据" + Arrays.toString(signature));  
        System.out.println("签名是否正确：" + verify(source, signature, keyPair.getPublic())); // 公钥验证签名  
    }  
  
    public static KeyPair generateKeyPair() throws Exception {  
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);  
        kpg.initialize(KEYSIZE, new SecureRandom());  
        return kpg.generateKeyPair(); // 生成密匙对  
    }  
  
    /**  
     * 加密或解密。加密和解密用不同的密钥  
     *   
     * @param src  要加密或解密的数据  
     * @param key  密钥，公钥或私钥  
     * @param mode 加密或解密模式。值请选择Cipher.DECRYPT_MODE或Cipher.ENCRYPT_MODE  
     * @return 加密或解密后的数据  
     */  
    public static byte[] encryptOrDecrypt(byte[] src, Key key, int mode) throws Exception {  
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());  
        cipher.init(mode, key);  
        int inputLen = src.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        // 加密时支持的最大字节数为【证书位数/8 -11】，1024位的证书加密时最大支持117个字节，2048位的证书加密时最大支持245个字节  
        // 解密时支持的最大字节数为【证书位数/8】，1024位的证书解密时最大支持128个字节，2048位的证书解密时最大支持256个字节  
        final int MAX_SIZE = mode == Cipher.ENCRYPT_MODE ? (KEYSIZE / 8 - 11 - 1) : (KEYSIZE / 8);  
        for (int i = 0; inputLen - offSet > 0; offSet = i * MAX_SIZE) {  
            byte[] cache = cipher.doFinal(src, offSet, Math.min(inputLen - offSet, MAX_SIZE));  
            out.write(cache, 0, cache.length);  
            i++;  
        }  
        return out.toByteArray(); // 关闭ByteArray**Stream无效，此类中的方法在关闭此流后仍可被调用，而不会产生任何 IOException  
    }  
  
    public static byte[] sign(byte[] data, PrivateKey key) throws Exception {  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initSign(key); // 用私钥对信息生成数字签名  
        signature.update(data);  
        return signature.sign();  
    }  
  
    public static boolean verify(byte[] data, byte[] sign, PublicKey key) throws Exception {  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(key); // 用公钥校验数字签名  
        signature.update(data);  
        return signature.verify(sign);  
    }  
}  
```  
  
2016-12-24  
