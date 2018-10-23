| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |
| :------------: | :------------: | :------------: | :------------: | :------------: |
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |

[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs
[GitHub]:https://github.com/baiqiantao
[博客]:http://www.cnblogs.com/baiqiantao/

Openfire XMPP Smack RTC IM 即时通讯 聊天 MD
***
目录
===
[TOC]

# 简介
Demo地址：https://github.com/baiqiantao/OpenFireTest.git
[官网](https://www.igniterealtime.org/index.jsp)
[官方文档](http://download.igniterealtime.org/openfire/docs/latest/documentation/index.html)
[OpenFire下载](https://www.igniterealtime.org/downloads/index.jsp#openfire)
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181018/1g44CI3ccb.png?imageslim)

## Openfire 简介
- Openfire是一个根据开源Apache许可证授权的`实时协作服务器` `real time collaboration (RTC)`。它使用唯一广泛采用的`即时消息开放协议XMPP(Jabber)`。 Openfire非常容易设置和管理，但提供坚如磐石的安全性和性能。
- Openfire是一个`功能丰富`的`即时消息和跨平台实时协作服务器`，使用`XMPP协议`提供全面的`群聊和即时消息服务`。
- OpenFire是采用Java编程语言开发的实时协作服务器，可以轻易的构建高效率的即时通信服务器，安装和使用简单，利用 Web 进行管理，单台服务器可支持上万并发用户

## 相关的几个名词
简单说，OpenFire 是服务器，XMPP 是协议，Smack 是类库，Spark 是客户端。

### Smack
[GitHub](https://github.com/igniterealtime/Smack)
[Flowdalic/asmack](https://github.com/Flowdalic/asmack)

- Smack 是一个`基于 XMPP 协议的 Java 实现`，提供一套`可扩展的API`，与 OpenFire 进行通信。
- Smack 是一个开源，易于使用的 `XMPP 客户端类库`，可以实现即时通讯和聊天。
- Smack 是Spark项目的核心。

优点：
- 简单，功能强大，只需短短几行代码就可以向用户发送文本消息；
- 不像其他类库那样强制你进行包级别的编码，Smack提供了智能的、更高级的构造，像Chat和Roster类，可以让你进行更高效的编程；
- 你不需要熟悉 XMPP XML 格式，甚至不需要熟悉XML；
- 提供了简单的机器到机器通讯，允许在每个消息中设置任意数量的属性，包括java对象；
- Apache许可下的开源类库，这意味着使用者可以将Smack整合进商业的或者非商业的应用中。

缺点是其API并非为大量并发用户设计，每个客户要1个线程，占用资源大。

### Spark
- Spark 相当与电脑版QQ，通过 smack 与 openfire 进行通信。
- Spark 是一个 XMPP 协议通信聊天的CS端的IM软件，它可以通过 openfire 进行聊天对话。
```xml
<message from="admin@myopenfire.com" to="bqt@myopenfire.com">消息内容</message>
```

### JID
- 基于历史原因, 一个XMPP实体的地址称为`Jabber Identifier`或`JID`，它用来标示XMPP网络中的各个XMPP实体。
- 鉴于协议的分布式特征, JID 应包含`联系到用户`所需的所有信息。
- 个人认为可以把JID理解为Email地址，就比较好理解了。
- 一个合法的JID包括节点名user、域名domain、资源名resource，其中 user 和 resource 是可有可无的，domain 是必须的。domain和user部分是不分大小写的，但是resource区分大小写。
    - domainpart 通常指网络中的`网关或者服务器`
    - localpart(user、node) 通常表示一个向服务器或网关请求和使用网络服务的`实体`(比如一个客户端)，当然它也能够表示其他的`实体`(比如在多用户聊天系统中的一个房间)。
    - resourcepart：通常表示一个特定的会话（与某个设备），连接（与某个地址），或者一个附属于某个节点ID实体相关实体的对象（比如多用户聊天室中的一个参加者）。
- JID的格式为：`jid = [ localpart "@" ] domainpart [ "/" resourcepart ]`，例如：
    - stpeter@jabber.org：表示`服务器jabber.org`上的`用户stpeter`。
    - room@service：一个用来提供多用户聊天服务的特定的聊天室。这里 room 是`聊天室`的名字，service 是多用户聊天服务的`主机名`。
    - room@service/nick：加入了聊天室的用户nick的地址。这里 nick 是用户在聊天室的`昵称`。

### XMPP
Extensible Messaging and Presence Protocol，可扩展通讯和表示协议
- XMPP 是基于 XML 的协议，这表明 XMPP 是可扩展的。
- XMPP 包含了针对服务器端的软件协议，用于即时消息以及在线现场探测。
- XMPP 的前身是Jabber(1998 年)，一个开源形式组织产生的网络即时通信协议。
- XMPP 是一个由IETF标准化的开放协议，由XMPP标准基金会支持和扩展。

XMPP是一种基于标准通用标记语言的子集`XML`的协议，它继承了在XML环境中灵活的发展性。因此，基于XMPP的应用具有超强的`可扩展性`。经过扩展以后的XMPP可以通过发送扩展的信息来处理用户的需求，以及在XMPP的顶端建立如内容发布系统和基于地址的服务等应用程序。而且，XMPP包含了针对服务器端的软件协议，使之能与另一个进行通话，这使得开发者更容易建立客户应用程序或给一个配好系统添加功能。

优点：开放、可扩展、标准、证实可用、分散、安全  
缺点 ：数据负载过重，没有二进制传输  

**基本网络结构**
- XMPP中定义了三个角色，`客户端，服务器，网关`，通信能够在这三者的任意两个之间双向发生。
- `服务器`同时承担了客户端信息记录，连接管理和信息的路由功能。
- `网关`承担着与异构即时通信系统的互联互通，异构系统可以包括SMS，MSN，ICQ等。
- 基本的网络形式是`单客户端`通过`TCP/IP`连接到单服务器，然后在之上传输XML。

**XMPP 工作流程**
- 节点连接到服务器
- 服务器利用本地目录系统中的证书对其认证
- 节点指定目标地址，让服务器告知目标状态
- 服务器查找、连接并进行相互认证
- 节点之间进行交互

XMPP核心协议通信的基本模式就是先建立一个`stream`，然后协商一堆`安全`之类的东西，中间通信过程就是客户端发送`XML Stanza(节点)`，一个接一个的。服务器根据客户端发送的信息以及程序的逻辑，发送`XML Stanza`给客户端。但是这个过程并不是一问一答的，任何时候都有可能从一方发信给另外一方。通信的最后阶段是`</stream>`关闭流，关闭`TCP/IP`连接。

**传输的内容**
传输的是与即时通讯相关的`指令`。在以前这些命令要么用`2进制`的形式发送（比如QQ），要么用`纯文本指令加空格加参数加换行`符的方式发送（比如MSN）。而XMPP传输的即时通讯指令的逻辑与以往相仿，只是协议的形式变成了`XML格式`的纯文本。这不但使得解析容易了，人也容易阅读了，方便了开发和查错。

XMPP 的核心部分就是一个在网络上分片段发送 XML 的`流协议`。这个流协议是 XMPP 的即时通讯指令的传递基础，可以说 XMPP 用 TCP 传的是 XML 流。

**真实通讯案例**
Xmpp协议是建立在xml的基础上的，所以，看起来，xmpp协议就像一个xml。

客户端 8049a646c63e65e8 发出去的消息：
```xml
<message from='8049a646c63e65e8@oatest.dgcb.com.cn/phone' id='5U6Mk-5' to='903e652d2334628a@oatest.dgcb.com.cn' type='chat'>
    <body>{"fromId":"8049a646c63e65e8","fromName":"韩大东","messageType":1,"secret":false,"textContent":"你好","toName":"郑西风","toUserID":"903e652d2334628a"}</body>
    <request xmlns='urn:xmpp:receipts'/>
</message>
```
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/aLLdaC4bBA.png?imageslim)

客户端 8049a646c63e65e8 接收到的消息：
```xml
<message from="903e652d2334628a@oatest.dgcb.com.cn/phone" id="Bw4c9-4" to="8049a646c63e65e8@oatest.dgcb.com.cn" type="chat">
    <body>{"fromId":"903e652d2334628a","fromName":"郑西风","messageType":1,"secret":false,"textContent":"你好"}</body>
    <request xmlns="urn:xmpp:receipts"/>
    <send time="2018-10-19 16:08:21:999" xmlns="icitic:msg:single"/>
</message>
```
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/em7ahdkE8J.png?imageslim)

其实 XMPP 是一种很类似于http协议的一种`数据传输协议`，用户只需要明白它接收的类型，并理解它返回的类型，就可以很好的利用xmpp来进行数据通讯。

目前不少IM应用系统如Google公司的`Google Talk`以及`Jive Messenger`等开源应用，都是遵循XMPP协议集而设计实现的，这些应用具有很好的互通性。

## Openfire 安装配置
安装时除了修改一下安装路径，其他一路Next就Ok了。

安装完毕后会自动启动Openfire服务并自动打开 [配置页面](http://localhost:9090/setup/index.jsp) (可能需要手动刷新一下)。也可以通过双击 `\Openfire\bin\openfire.exe` 或 `\Openfire\bin\openfired.exe` 启动Openfire服务后手动打开配置页面。  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181018/Ggm0D2481A.png?imageslim)

然后按照指引设置 Openfire 服务器：
- 选择语言：中文简体
- 配置服务器域名【127.0.0.1】
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/EDiBJ267C1.png?imageslim)

- 选择数据库
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/cD09aK92h2.png?imageslim)

- 选择特性配置，默认即可
- 设置管理员帐户【0909082401@163.com】【123456a】
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/0CahGafhd6.png?imageslim)

- 提示安装完成，点击登录管理员[控制台页面](http://localhost:9090/login.jsp)【admin】【123456a】
- 进入后可以看到服务器名称等信息【127.0.0.1】
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/a09hL622ba.png?imageslim)

- 创建用户【admin】【baiqiantao】【bqt】【test】
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181018/A23hIGLFeI.png?imageslim)  

- 安装spark客户端，这个spark仅仅是拿来测试用的。

至此代码以外的环境已经配置好了。

# Stanza 节
Xml是由节点构成的，而基于xml的xmpp协议中与通信有关三个最核心的节(Stanza)是：`<message>、<presence>、<iq>`，可以通过组织不同的节来达到各式各样不同的通讯目的。接下来就对这些Stanza做一个大致的了解。

## 共同属性
每个节都有其属性，虽然不同的节其属性各有不同，但是一些基本的属性是这些所有的节所共同的以下这些是他们的共同属性。

- from
表示Stanza的发送方，在发送Stanza时，一般来说不推荐设定，`服务器会自动设定正确的值`，如果你设定了不正确的值，服务器将会拒收你的Stanza信息。

- to
表示Stanza的接收方。这个节点一般是自己设置的，若到达服务器的数据中没收设置该属性，则服务器会认为这条信息是发送给自己的。

- type
指定Stanza的类型。
这个节与前两个不同，设置的值不可以统一而论，不同的节有不同的设定值，每种Stanza都有固定的几种可能的设定值。
虽然不同节点的type属性各有不同，但是都有一个error类型，表示这是一条错误信息，服务器接收到这种类型的信息的时候不需要作出任何的回应。

- id
用于标志唯一的一条特定信息，表示一个特定的请求。
在<iq>节中，这个属性是必须要指定的，但是在其他两个Stanza中是一个可选属性。

## Presence 在线状态
Presence Stanza 用来控制和表示实体的在线状态，可以展示离线、在线、离开、不能打扰等复杂状态，另外，还能被用来建立和结束在线状态的订阅。

除了类型信息外，Presence还包含其他一些可选的属性：
- Status: 用于表示用户状态的`自定义文本`，例如：外出吃饭
- Priority: 一个表示发送者资源`优先级`的非负数
- Mode: 表示五种状态之一

**案例**
`<presence/>` 设定用户状态为在线
`<presence type="unavailable"/>` 设定用户状态为离线

```xml
<presence>
    <show>away</show>
    <status>at the ball</status>
</presence> 
```
用于显示用户状态的详细信息。上面的例子表明用户因为`at the ball`在离开状态。
- `<show>` 标签在presence节点中最多出现一次，取值可以为`Presence.Mode`中的某一个。
- `<status>`标签用于显示额外信息

```xml
<presence>
    <status>touring the countryside</status>
    <priority>10</priority>
</presence>
```
在这个节中，出现了一个`<priority>`标签，表示现在连接的优先级。每个连接可以设置从`-128到127`的优先级，默认是设置为`0`，用户可以在这个标签里修改相应的优先级。

**在线状态预定**
首先我们来看一个例子：
```xml
<presence
    from="william_duan@jabber.org"
    to="test_account@jabber.org"
    type="subscribe"/>
```
```xml
<presence
    from="test_account@jabber.org"
    to="william_duan@jabber.org"
    type="subscribed"/>
```
通过上述交互，william_duan 就能看到 test_account 的在线状态，并能接收到 test_account 的在线状态通知了(例如上线提醒功能)。

**Presence.Type**
```java
package org.jivesoftware.smack.packet;
public enum Presence.Type {
    available, //【在线，可接收消息】The user is available to receive messages (default).
    unavailable,//【离线，不可接收消息】The user is unavailable to receive messages.
    subscribe,//【申请添加对方为好友】Request subscription to recipient's presence.
    subscribed, //【同意对方添加自己为好友】Grant subscription to sender's presence.
    unsubscribe, //【删除好友的申请】Request removal of subscription to sender's presence.
    unsubscribed,//【拒绝添加对方为好友】Grant removal of subscription to sender's presence.
    error,//【错误】The presence stanza(/packet) contains an error message.
    probe,;//【账号是否存在】A presence probe as defined in section 4.3 of RFC 6121
    public static Type fromString(String string) {
        return Type.valueOf(string.toLowerCase(Locale.US));
    }
}
```

**Presence.Mode**
```java
package org.jivesoftware.smack.packet;
public enum Presence.Mode {
    chat, //【交谈中】，Free to chat.
    available, //【在线】Available (the default).
    away, //【离开】，Away.
    xa, //【离开一段时间】，Away for an extended period of time.
    dnd; //【请勿打扰】，Do not disturb.
    public static Presence.Mode fromString(String string) {
        return Presence.Mode.valueOf(string.toLowerCase(Locale.US));
    }
}
```

## Message 传递消息
用于在用户之间传递信息，这消息可以是单纯的聊天信息，也可以某种格式化的信息。

message节点信息是传递之后就被忘记的。当消息被送出之后，发送者是不管这个消息是否已经送出或者什么时候被接收到。但是通过扩展协议，可以改变这样一种状况。

**案例**
私人聊天信息：
```xml
<message
    from="william_duan@jabber.org"
    to="test_account@jabber.org"
    type="chat">
    <body>Come on</body>
    <thread>23sdfewtr234weasdf</thread>
</message> 
```

多人聊天信息：
```xml
<message
    from="test_account@jabber.org"
    to="william_duan@jabber.org"
    type="groupchat">
    <body>welcome</body>
</message>
```

上面的两个例子都包含了一个`<type>`标签，这个标签表明了消息的类型，可以取 `Message.Type` 中的任一值。

`<body>`标签里面是具体的消息内容。

**Message.Type**
```java
package org.jivesoftware.smack.packet;
public enum Message.Type {
    normal,//【广播】(Default) a normal text message used in email like interface.
    chat,//【单聊】Typically short text message used in line-by-line chat interfaces.
    groupchat,//【群聊】Chat message sent to a groupchat server for group chats.
    headline,//【通知，不需要回应】Text message to be displayed in scrolling marquee滚动选框 displays.
    error;//【错误】indicates a messaging error. error消息为系统自动发送的，往往是由于错误发送消息
    public static Type fromString(String string) {
        return Type.valueOf(string.toLowerCase(Locale.US));
    }
}
```

## IQ 请求响应
- IQ Stanza 主要是用于`Info/Query`模式的消息请求，他和`Http`协议比较相似。
- IQ节点`需要有回应`。
- 可以发出`get`以及`set`请求，就如同http中的`GET`以及`POST`。
- 有`result`以及`error`两种回应。

**案例**
william_duan 请求自己的联系人列表:
```xml
<iq
    from="william_duan@jabber.org/study"
    id="roster1"
    type="get">
    <query xmlns="jabber:iq:roster"/>
</iq> 
```

请求发生错误：
```xml
<iq
    id="roster1"
    to="william_duan@jabber.org/study"
    type="error">
    <query xmlns="jabber:iq:roster"/>
    <error type="cancel">
        <feature-not-implemented xmlns="urn:ietf:params:xml:ns:xmpp-stanzas"/>
    </error>
</iq>
```

请求成功，返回 william_duan 的联系人列表。每一个`<item>`标签代表了一个联系人信息：
```xml
<iq
    id="roster1"
    to="william_duan@jabber.org/study"
    type="error">
    <query xmlns="jabber:iq:roster"/>
    <item
        name="one"
        jid="account_one@jabber.org"/>
    <item
        name="two"
        jid="account_two@jabber.org"/>
</iq> 
```

**IQ.Type**
```java
public enum IQ.Type {
    get, //【请求消息】The IQ stanza requests information, inquires about what data is needed in order to complete further operations, etc.
    set, //【设置消息】The IQ stanza provides data that is needed for an operation to be completed, sets new values, replaces existing values, etc.
    result, //【成功】The IQ stanza is a response to a successful get or set request.
    error,; //【失败】The IQ stanza reports an error that has occurred regarding processing or delivery of a get or set request.
    
    public static IQ.Type fromString(String string) {
        return IQ.Type.valueOf(string.toLowerCase(Locale.US));
    }
}
```

# 测试代码
Demo地址：https://github.com/baiqiantao/OpenFireTest.git

XMPPConnection的连接需要首先通过`XMPPTCPConnectionConfiguration.builder()`配置你在Openfire设置的配置，然后根据配置构造一个 XMPPTCPConnection ，以后所有操作基本都需要用到这个 XMPPTCPConnection 。
```java
connection = new XMPPTCPConnection(configuration);
```

通过了上面的配置后，咱们可以登录Openfire系统了，相当简单:
```java
XMPPUtils.getConnection().login(username, password);
```

下面我们重点分析下登录过程的报文内容以及一些最常用的API。

## connect 过程
在建立了Socket后，client会向服务器发出一条xml：
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/BglE7h557F.png?imageslim)
```xml
<stream:stream xmlns:stream='http://etherx.jabber.org/streams'
               from='8049a646c63e65e8@oatest.dgcb.com.cn'
               to='oatest.dgcb.com.cn'
               version='1.0'
               xmlns='jabber:client'
               xml:lang='en'>
```

服务器解析到上面的指令后，会返回用于告诉client可选的SASL方式
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/k7k0d2ldFb.png?imageslim)
```xml
<?xml version='1.0' encoding='UTF-8'?>
<stream:stream xmlns:stream="http://etherx.jabber.org/streams"
               from="oatest.dgcb.com.cn"
               id="36ebm4blnf"
               version="1.0"
               xmlns="jabber:client"
               xml:lang="en">
    <stream:features>
        <starttls xmlns="urn:ietf:params:xml:ns:xmpp-tls"></starttls>
        <mechanisms xmlns="urn:ietf:params:xml:ns:xmpp-sasl">
            <mechanism>PLAIN</mechanism>
            <mechanism>SCRAM-SHA-1</mechanism>
            <mechanism>CRAM-MD5</mechanism>
            <mechanism>DIGEST-MD5</mechanism>
        </mechanisms>
        <compression xmlns="http://jabber.org/features/compress">
            <method>zlib</method>
        </compression>
        <ver xmlns="urn:xmpp:features:rosterver"/>
        <register xmlns="http://jabber.org/features/iq-register"/>
    </stream:features>
```

至此，connect 算是完成了，此时会回调 `ConnectionListener` 的 `connected` 方法。

## login 过程
```java
XMPPUtils.getConnection().login(username, password);
```

1、客户端选择PLAIN认证方式
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/HDA9kkHf41.png?imageslim)
```xml
<auth mechanism='PLAIN'
      xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>ADgwNDlhNjQ2YzYzZTY1ZTgAQkRFNEM3QzBGMzdENEZGRTlENDlGNDcwMTdFNUJCRjc=
</auth>
```

服务器通过计算加密后的密码后，服务器将返回
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/3eaJcAje5J.png?imageslim)
```xml
<success xmlns="urn:ietf:params:xml:ns:xmpp-sasl"/>
```

2、当客户端收到以上命令后，将首次发起连接的id发送到服务器
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/4Lbd6jgBeK.png?imageslim)
```xml
<stream:stream xmlns:stream='http://etherx.jabber.org/streams'
               from='8049a646c63e65e8@oatest.dgcb.com.cn'
               id='36ebm4blnf'
               to='oatest.dgcb.com.cn'
               version='1.0'
               xmlns='jabber:client'
               xml:lang='en'>
```

这时服务器会返回如下内容说明此时已经成功绑定了当前的Socket
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/Ic4EmGdbe4.png?imageslim)
```xml
<?xml version='1.0' encoding='UTF-8'?>
<stream:stream xmlns:stream="http://etherx.jabber.org/streams"
               from="oatest.dgcb.com.cn"
               id="36ebm4blnf"
               version="1.0"
               xmlns="jabber:client"
               xml:lang="en">
    <stream:features>
        <compression xmlns="http://jabber.org/features/compress">
            <method>zlib</method>
        </compression>
        <ver xmlns="urn:xmpp:features:rosterver"/>
        <bind xmlns="urn:ietf:params:xml:ns:xmpp-bind"/>
        <session xmlns="urn:ietf:params:xml:ns:xmpp-session">
            <optional/>
        </session>
        <sm xmlns='urn:xmpp:sm:2'/>
        <sm xmlns='urn:xmpp:sm:3'/>
    </stream:features>
```

3、压缩
3.1、客户端在接收到如上的内容后会告诉服务器开启压缩
> 项目中没有使用压缩，所以下面的过程不存在，以下为参考别人的案例

```xml
<compress xmlns='http://jabber.org/protocol/compress'><method>zlib</method></compress>
```

服务器返回
```xml
<compressed xmlns='http://jabber.org/protocol/compress'/>
```

3.2、客户端收到服务器的响应命令后，重新建立一个Socket，发送指令
```xml
<stream:stream 
    xmlns='jabber:client'       
    to='server domain' 
    xmlns:stream='http://etherx.jabber.org/streams' 
    version='1.0' 
    from='username@server domain'  
    id='c997c3a8' 
    xml:lang='en'>
```

服务器将返回，不知道你有没有发现，这里的id还是那个id
```xml
<?xml version='1.0' encoding='UTF-8'?>
    <stream:stream 
        xmlns:stream="http://etherx.jabber.org/streams" 
        xmlns="jabber:client" 
        from="im" 
        id="c997c3a8" 
        xml:lang="en" 
        version="1.0">
        <stream:features>
            <mechanisms 
            xmlns="urn:ietf:params:xml:ns:xmpp-sasl">
                <mechanism>PLAIN</mechanism>
                <mechanism>ANONYMOUS</mechanism>
                <mechanism>JIVE-SHAREDSECRET</mechanism>
            </mechanisms>
            <bind 
                xmlns="urn:ietf:params:xml:ns:xmpp-bind"/>
                <session 
                    xmlns="urn:ietf:params:xml:ns:xmpp-session"/>
    </stream:features>
```

4、客户端发送绑定Socket的指令：
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/f0b225dKlE.png?imageslim)
```xml
<iq
    id='SG6jR-3'
    type='set'>
    <bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>
        <resource>phone</resource>
    </bind>
</iq>
```

服务器返回绑定了具有指定 JID 的客户端
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/JA881Gm69b.png?imageslim)
```xml
<iq
    id="SG6jR-3"
    to="oatest.dgcb.com.cn/36ebm4blnf"
    type="result">
    <bind xmlns="urn:ietf:params:xml:ns:xmpp-bind">
        <jid>8049a646c63e65e8@oatest.dgcb.com.cn/phone</jid>
    </bind>
</iq>
```

5、开启一个session
> 项目中没有开启一个session的逻辑，所以下面的过程不存在，以下为参考别人的案例

```xml
<iq id='b86j8-6' type='set'><session xmlns='urn:ietf:params:xml:ns:xmpp-session'/></iq>
```

这时服务器返回
```xml
<iq 
    type="result" 
    id="b86j8-6" 
    to="c997c3a8@im/c997c3a8"/>
```

6、认证
因为项目中没有开启认证，所以这里没有报文通讯，只有如下日志：
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181020/i2i1HF336I.png?imageslim)

至此，客户端的登录过程算是完成了。
> 注意，connect 和 login 都是同步操作，所以在 `login(username, password)` 方法调用以后，如果没有报异常，就是登陆成功了。

## 获取通讯录
登陆以后接着会自动发送一条获取通讯录的指令，并会将通讯录缓存起来，所以以后再获取通讯录时，并不需要访问网络。
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/AifLFmjhdl.png?imageslim)
```xml
<iq
    id='gZYnq-5'
    type='get'>
    <query xmlns='jabber:iq:roster'></query>
</iq>
```

服务器将返回
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/amaKgD6Kb3.png?imageslim)
```xml
<iq
    id="SG6jR-5"
    to="8049a646c63e65e8@oatest.dgcb.com.cn/phone"
    type="result">
    <query ver="-491295515"
           xmlns="jabber:iq:roster">
        <item
            name="李**"
            jid="0347a8a25e9074b0@oatest.dgcb.com.cn"
            subscription="to"/>
        <item
            jid="903e652d2334628a@oatest.dgcb.com.cn"
            subscription="from"/>
        <item
            ask="subscribe"
            jid="28af56d053cbbf3e@oatest.dgcb.com.cn"
            subscription="none"/>
    </query>
</iq>
```

此过程完成以后会回调 `RosterListener` 的 `entriesAdded` 方法。

## 告诉服务器在线状态
虽然已经登录了，但是还需要告诉服务器自己的状态，否则服务器不会认为你是在线状态，这时你可能就收不到其他好友发来的消息(我们我们项目中有集成离线推送功能，如果没有告诉服务器你在笑，服务器会走离线消息推送的逻辑。)
```java
XMPPUtils.getConnection().sendStanza(presence);
```

客户端发送 presence 消息告诉服务器自己在线：
```xml
<presence
    from='8049a646c63e65e8@oatest.dgcb.com.cn/phone'
    id='91kqC-27'>
    <status>IchatMM</status>
    <priority>0</priority>
    <c hash='sha-1'
       node='http://www.igniterealtime.org/projects/smack'
       ver='NfJ3flI83zSdUDzCEICtbypursw='
       xmlns='http://jabber.org/protocol/caps'/>
</presence>
```

服务器响应：
```xml
<presence
    from="c53706e24ce32f72@oatest.dgcb.com.cn/pc"
    to="8049a646c63e65e8@oatest.dgcb.com.cn/phone">
    <priority>0</priority>
    <c hash="sha-1"
       node="http://camaya.net/gloox"
       ver="9ZtEa+bYQasYo2pVBGT9ShIT+Yc="
       xmlns="http://jabber.org/protocol/caps"></c>
</presence>
```

收到响应后，会回调 `RosterListener` 的 `presenceChanged` 方法，此后，就可以愉快的玩耍了。

## 判断是否在线
服务器会定时(默认3分钟)主动发送一条 ping 消息，以确定客户端是否在线：
```Java
PingManager.getInstanceFor(connection).setPingInterval(60);//ping消息间隔
```

![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181020/52LIiAFLgd.png?imageslim)
```xml
<iq
    from="oatest.dgcb.com.cn"
    id="553-595"
    to="8049a646c63e65e8@oatest.dgcb.com.cn/phone"
    type="get">
    <ping xmlns="urn:xmpp:ping"/>
</iq>
```

客户端响应：
```xml
<iq
    id='553-595'
    to='oatest.dgcb.com.cn'
    type='result'></iq>
```

到此，整个登录流程已经成功了，接下来可以做一些用户信息的获取等操作。

## 发送消息
```Java
//发送方式一，简单的发送文本消息
ChatManager.getInstanceFor(XMPPUtils.getConnection()).createChat(to).sendMessage(text);
//发送方式二，发送一个Message对象，可包含一些信息，一般使用这种方式
XMPPUtils.getConnection().sendStanza(msg);
```

除去消息内容后的日志：
```
14:51:02.365 客户端A I/bqt: 【chatCreated】
14:51:02.366 客户端A D/SMACK: SENT (0)
14:51:02.399 客户端A D/SMACK: RECV (0)
14:51:02.400 客户端A I/bqt: 【processPacket】
14:51:02.402 客户端A I/bqt: 【processMessage】
14:51:02.404 客户端A D/SMACK: RECV (0)
14:51:02.404 客户端B D/SMACK: RECV (0)
14:51:02.407 客户端A I/bqt: 【processPacket】
14:51:02.407 客户端A I/bqt: 【processMessage】
14:51:02.409 客户端B I/bqt: 【processPacket】
14:51:02.410 客户端B I/bqt: 【chatCreated】
14:51:02.411 客户端B I/bqt: 【processMessage】
14:51:02.412 客户端B I/bqt: 消息类型：chat
```
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181020/9LLL5AikDj.png?imageslim)

1、客户端A发送消息：
```xml
<message
    from='8049a646c63e65e8@oatest.dgcb.com.cn/phone'
    id='nCRIE-44'
    to='903e652d2334628a@oatest.dgcb.com.cn/phone'
    type='chat'>
    <body>你好，我是包青天</body>
    <thread>6828a752-cfae-4149-9d4d-c8fb83a17175</thread>
</message>
```

客户端收到服务器的回执(msgId相同)：
```xml
<message
    from="903e652d2334628a@oatest.dgcb.com.cn/phone"
    to="8049a646c63e65e8@oatest.dgcb.com.cn/phone">
    <received msgId="nCRIE-44"
              status="1"
              time="2018-10-20 14:50:16:566"
              xmlns="urn:xmpp:receipts"/>
</message>
```

2、然后，客户端B会收到客户端A发送的消息(id相同)：
```xml
<message
    from="8049a646c63e65e8@oatest.dgcb.com.cn/phone"
    id="nCRIE-44"
    to="903e652d2334628a@oatest.dgcb.com.cn/phone"
    type="chat">
    <body>你好，我是包青天</body>
    <thread>6828a752-cfae-4149-9d4d-c8fb83a17175</thread>
    <send time="2018-10-20 14:50:16:572"
          xmlns="icitic:msg:single"/>
</message>
```

客户端A也会收到的回执消息(id后面拼接了mutisingle)：
```xml
<message
    from="8049a646c63e65e8@oatest.dgcb.com.cn"
    id="nCRIE-44mutisingle"
    to="8049a646c63e65e8@oatest.dgcb.com.cn"
    type="chat">
    <subject>903e652d2334628a@oatest.dgcb.com.cn</subject>
    <body>你好，我是包青天</body>
    <send time="2018-10-20 14:50:16:571"
          xmlns="icitic:msg:single"/>
</message>
```

## 测试案例代码
### 项目结构
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181019/H6fB6k95H8.png?imageslim)

```java
implementation 'org.igniterealtime.smack:smack-android:4.1.4'
implementation 'org.igniterealtime.smack:smack-tcp:4.1.4'
implementation 'org.igniterealtime.smack:smack-im:4.1.4'
implementation 'org.igniterealtime.smack:smack-extensions:4.1.4'
```

### MainActivity
```java
public class MainActivity extends ListActivity {
    private boolean switchUser = false;
    private EditText etAccount, etPassword, etChat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] array = {"初始化",
                "登录",
                "发送在线状态消息",
                "发消息",
                "获取好友信息",
                "创建聊天室",
                "加入聊天室",
                "邀请好友进入聊天室",
                "注销登录",
                "",};
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));
        etAccount = new EditText(this);
        etPassword = new EditText(this);
        etChat = new EditText(this);
        
        etAccount.setText(switchUser ? "8049a646c63e65e8" : "903e652d2334628a");
        etPassword.setText(switchUser ? "1E6210BB50614D978F4758B2DC9D76C9" : "40C61DE3492C41B1846281833434D997");
        etChat.setText(switchUser ? "903e652d2334628a@oatest.dgcb.com.cn/phone" : "8049a646c63e65e8@oatest.dgcb.com.cn/phone");
        getListView().addFooterView(etAccount);
        getListView().addFooterView(etPassword);
        getListView().addFooterView(etChat);//要聊天的用户的ID
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String account = etAccount.getText().toString();
        String password = etPassword.getText().toString();
        String jid = etChat.getText().toString();
        new Thread(() -> testApi(position, account, password, jid)).start();
    }
    
    private void testApi(int position, String account, String password, String jid) {
        switch (position) {
            case 0:
                XMPPUtils.init(account, password);//初始化
                break;
            case 1:
                XMPPUtils.login(account, password);//登录
                break;
            case 2:
                XMPPUtils.setOnLineStatus();//在线
                break;
            case 3:
                XMPPUtils.sendMessage(account + "@oatest.dgcb.com.cn/phone", jid, "你好，我是包青天");//发消息
                break;
            case 4:
                XMPPUtils.getMyFriends();//获取好友信息
                break;
            case 5:
                XMPPUtils.createMucRoom(jid, "包青天");//创建聊天室
                break;
            case 6:
                XMPPUtils.joinChatRoom(jid, account);//加入聊天室
                break;
            case 7:
                XMPPUtils.inviteToTalkRoom(jid, account, password, "快来参加第二十八届英雄大会");//邀请好友进入聊天室
                break;
            case 8:
                XMPPUtils.logout();//注销登录
                break;
            default:
                break;
        }
    }
}
```

### 常用功能封装的工具栏
```java
public class XMPPUtils {
    private static XMPPTCPConnection connection;
    
    /**
     * 初始化
     */
    public static synchronized void init(CharSequence username, String password) {
        if (connection == null) {
            //初始化XMPPTCPConnection相关配置
            XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(username, password)//设置登录openfire的用户名和密码
                    .setServiceName("oatest.dgcb.com.cn")//设置服务器名称
                    .setHost("oatest.dgcb.com.cn")//设置主机地址
                    .setPort(25222)//设置端口号
                    .setResource("phone") //默认为Smack
                    .setDebuggerEnabled(true)//是否查看debug日志
                    //**********************************************  以下为进阶配置  *************************************************
                    .setConnectTimeout(10 * 1000)//设置连接超时的最大时间
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)//设置安全模式，关闭安全模式
                    .setCompressionEnabled(false) //开启通讯压缩，开启后传输的流量将节省90%
                    .setSendPresence(false)
                    .setCustomSSLContext(getSSLContext()) //自定义的TLS登录
                    .setHostnameVerifier((hostname, session) -> true)
                    .build();
            
            connection = new XMPPTCPConnection(configuration);
            connection.setFromMode(XMPPConnection.FromMode.USER);
            connection.addConnectionListener(new MyConnectionListener()); //监听connect状态
            connection.addAsyncStanzaListener(new MyStanzaListener(), StanzaTypeFilter.MESSAGE);// 注册包的监听器
            PingManager.getInstanceFor(connection).setPingInterval(60);//ping消息间隔
            
            //SASL认证
            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
            SASLAuthentication.blacklistSASLMechanism(SASLPlainMechanism.DIGESTMD5);
            SASLAuthentication.registerSASLMechanism(new SASLPlainMechanism());
            
            Roster.getInstanceFor(connection).addRosterListener(new MyRosterListener());
            ChatManager.getInstanceFor(connection).addChatListener(new MyChatManagerListener()); //监听与聊天相关的事件
            MultiUserChatManager.getInstanceFor(connection).addInvitationListener(new MyInvitationListener()); //被邀请监听
        }
    }
    
    private static SSLContext getSSLContext() {
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new TLSUtils.AcceptAllTrustManager()}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return context;
    }
    
    public static XMPPTCPConnection getConnection() {
        return connection;
    }
    
    /**
     * 登录
     */
    public static void login(CharSequence username, String password) {
        try {
            if (!XMPPUtils.getConnection().isConnected()) {
                XMPPUtils.getConnection().connect();
            }
            if (XMPPUtils.getConnection().isConnected()) {
                Log.i("bqt", "开始登录");
                XMPPUtils.getConnection().login(username, password);
                Log.i("bqt", "登录成功");
            } else {
                Log.i("bqt", "登录失败");
            }
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 告诉服务器登录状态
     */
    public static void setOnLineStatus() {
        if (XMPPUtils.getConnection().isAuthenticated()) {
            try {
                Presence presence = new Presence(Presence.Type.available);
                presence.setStatus("IchatMM"); //显示额外信息，内容根据需求可随意定制
                presence.setPriority(0); //连接的优先级
                XMPPUtils.getConnection().sendStanza(presence);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 注销登录
     */
    public static void logout() {
        if (!XMPPUtils.getConnection().isConnected()) {
            XMPPUtils.getConnection().disconnect();
        }
    }
    
    /**
     * 发消息
     */
    public static void sendMessage(String from, String to, String text) {
        try {
            ChatManager.getInstanceFor(XMPPUtils.getConnection()).createChat(to).sendMessage(text);//直接发送一条文本
            /*Message msg = new Message(to, Message.Type.chat);
            msg.setStanzaId(System.currentTimeMillis() + "");
            msg.setFrom(from);
            msg.setBody(text);
            XMPPUtils.getConnection().sendStanza(msg);//发送一个Message对象，可包含一些信息，一般使用后者*/
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取好友信息
     */
    public static void getMyFriends() {
        //并不需要访问网络，因为在登录后已经拿到用户的通讯录了，这里是直接从缓存中读取的
        Set<RosterEntry> set = Roster.getInstanceFor(XMPPUtils.getConnection()).getEntries();
        for (RosterEntry entry : set) {
            Log.i("bqt", "JID：" + entry.getUser() + "，Name：" + entry.getName());
        }
    }
    
    /**
     * 创建聊天室
     */
    public static void createMucRoom(String jid, String nickname) {
        try {
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(XMPPUtils.getConnection()).getMultiUserChat(jid);
            muc.create(nickname);//昵称
            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            
            for (FormField field : form.getFields()) {
                if (!FormField.Type.hidden.equals(field.getType()) && field.getVariable() != null) {
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            List<String> list = new ArrayList<>();
            list.add("20");
            List<String> owners = new ArrayList<>();
            owners.add("guochen@192.168.0.245");
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            submitForm.setAnswer("muc#roomconfig_maxusers", list);
            submitForm.setAnswer("muc#roomconfig_roomname", "room01");
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
            submitForm.setAnswer("x-muc#roomconfig_registration", false);
            muc.sendConfigurationForm(submitForm);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 加入聊天室
     */
    public static void joinChatRoom(String jid, String nickname) {
        try {
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(XMPPUtils.getConnection()).getMultiUserChat(jid);
            muc.join(nickname);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 邀请好友进入聊天室
     */
    public static void inviteToTalkRoom(String jid, String nickname, String user, String reason) {
        try {
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(XMPPUtils.getConnection()).getMultiUserChat(jid);
            muc.addInvitationRejectionListener((invitee, rejectReason) -> Log.i("bqt", "拒绝了，" + invitee + "，" + rejectReason));
            muc.join(nickname);
            muc.invite(user, reason);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
    }
}
```

2018-10-19