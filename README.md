# WeWork-notification
WeWork Notification For Jenkins build

> 企业微信Jenkins构建通知插件
> 2019-07-13

项目配置


项目运行
```
mvn org.jenkins-ci.tools:maven-hpi-plugin:run
```

项目DEBUG
````
set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n
````

项目打包
````
mvn package
````
