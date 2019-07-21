# Qy-Wechat-Notification
Qy  Wechat For Jenkins build

> 企业微信Jenkins构建通知插件
> 2019-07-13
## 下载安装

qy-wechat-notification-1.0.0.hpi [点击下载安装](http://cdn.itwake.com/qy-wechat-notification/qy-wechat-notification-1.0.0.hpi)

## 项目配置

先添加微信群机器人，得到Webhook地址
![](http://cdn.itwake.com/15637075518533.jpg)

将地址信息输入Jenkins中
![](http://cdn.itwake.com/15637076950124.jpg)

构建开始通知
![](http://cdn.itwake.com/15637078101376.jpg)

构建成功通知
![](http://cdn.itwake.com/15637078640589.jpg)

构建失败通知
![](http://cdn.itwake.com/15637079190249.jpg)

项目运行
```
mvn org.jenkins-ci.tools:maven-hpi-plugin:run
```

打开Jenkins地址
```
http://127.0.0.1:8080/jenkins
```

项目DEBUG
````
set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n
````

项目打包
````
mvn package
````
