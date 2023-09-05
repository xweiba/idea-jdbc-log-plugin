# Idea-JDBC-Log-Plugin
> 基于Java Agent模式，通过ByteBuddy对java.sql.PreparedStatement execute接口做代理，打印SQL。

## build
1. jdbc-log-agent执行shadowJar生成build/libs/jdbc-log-agent-xxx.jar，复制到jdbc-log-idea/libs中。
2. jdbc-log-plugin的gradle面板执行Tasks-intellij-buildPlugin生成插件离线包 jdbc-log-idea/build/distributions/jdbc-log-idea-xxx.zip

## 适配新驱动
修改`tk.weiba.idea.plugin.jdbc.InterceptMethod`，未适配时会通过日志打出驱动对应class。

## 安装
Idea设置->插件->从磁盘安装插件：jdbc-log-idea/build/distributions/jdbc-log-idea-xx.zip