# 指定基础镜像，在其上进行定制
FROM amazoncorretto:17.0.9

# 设置环境变量
ENV LANG "C.utf8"
ENV LANG "zh_CN.UTF-8"
ENV LANGUAGE "zh_CN:zh:en_US:en"

# 维护者信息
MAINTAINER yangshare <simayifeng@gmail.com>

# 添加应用jar包
COPY target/*.jar marsview4j.jar

# 暴露端口
EXPOSE 9001

# 定义容器启动命令[配置文件放到指定路径/app/conf/下方便映射到宿主机]
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8","-Dspring.config.location=/app/conf/", "-jar", "marsview4j.jar"]
