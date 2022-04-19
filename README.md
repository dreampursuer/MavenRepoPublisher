# MavenRepoPublisher

## 简介
本程序主要用于把本地maven仓库中的jar包发布到远程的git仓库中。

## Build
./gradlew shadowJar

在libs目录下会生成build的输出，这个输出会把整个依赖库打包在一起，这样就可以直接运行。

## 使用方法

```shell
java -jar MavenRepoPublisher-1.0-SNAPSHOT-all.jar [local maven repository directory] [maven repo]
```

```mermaid
flowchart TD
    Start --> Stop
```
