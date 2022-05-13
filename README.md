# 企业微信Jenkins构建通知插件

[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins%2Fqy-wechat-notification-plugin%2Fmaster)](https://ci.jenkins.io/job/Plugins/job/qy-wechat-notification-plugin/job/master/)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/qy-wechat-notification.svg)](https://plugins.jenkins.io/qy-wechat-notification)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/qy-wechat-notification.svg?color=blue)](https://plugins.jenkins.io/qy-wechat-notification)

> 该插件适用于使用"企业微信"工作的小伙伴，在Jenkins项目构建时使用群机器人进行状态通知
>
> 需要不低于企业微信 2.8.7版本

## 添加群机器人

#### 任意群成员，都可以通过`右键`群名称的进行添加群机器人

![添加微信机器人](http://cdn.itwake.com/mweb/20190813/15656561814910.jpg)

#### 企业微信会给新增加的群机器人分配一个Webhook，作为通知接口

![添加微信群机器人](http://cdn.itwake.com/FjyselmMyd45a2dMDPD0rYPyJvn9.png)

## Freestyle Job配置

#### 在Jenkins项目底部的`构建后操作`，添加`企业微信通知配置`
![添加微信群机器人](http://cdn.itwake.com/Fnz3LqHVE7kwG6CLblrMUJ1tfEve.png)

#### 将Webhook地址信息输入Jenkins中，即可完成最简单配置
![将地址信息输入Jenkins中](http://cdn.itwake.com/15637076950124.jpg)

## Pipeline Job参考配置
```
pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                echo 'Hello World'
            }
        }
    }
    post{
        success{
            qyWechatNotification failNotify: true, mentionedId: '需要通知UserID', mentionedMobile: '需要通知的通知手机号码', webhookUrl: 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx-xxxxxx-xxxxxx', moreInfo:'额外的信息'
        }
        failure{
            qyWechatNotification failNotify: true, mentionedId: '需要通知UserID', mentionedMobile: '需要通知的通知手机号码', webhookUrl: 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx-xxxxxx-xxxxxx', moreInfo:'额外的信息'
        }
    }
}

```

## 运行效果

#### 在构建开始的时候，群机器人会执行开始构建通知
![构建开始通知](http://cdn.itwake.com/FqztU6i9mJd6NOdvdkR_H-E2bd7c.png)

#### 构建成功后，群机器人会执行构建成功的通知
![构建成功通知](http://cdn.itwake.com/Ft9p4--ek2U1sXXLeq4AteIK4EuK.png)

#### 构建失败时，群机器人则会执行失败的通知
![构建失败通知](http://cdn.itwake.com/Fr34HIw4g--6Mcln_WpY89wkXE0H.png)

## 项目开发
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

## 更多支持

需要更多的支持请通过下方地址进行留言

https://itwake.blog.csdn.net/article/details/122043499