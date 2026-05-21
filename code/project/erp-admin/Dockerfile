# 多阶段构建：第 1 阶段编译，第 2 阶段只装 JRE + jar
# 从仓库根的 code/project 目录构建：
#   docker build -f erp-admin/Dockerfile -t erp:latest .

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 先拷 pom 文件，最大化利用 docker layer cache
COPY pom.xml ./
COPY erp-common/pom.xml      ./erp-common/
COPY erp-framework/pom.xml   ./erp-framework/
COPY erp-system/pom.xml      ./erp-system/
COPY erp-business/pom.xml    ./erp-business/
COPY erp-admin/pom.xml       ./erp-admin/
RUN mvn -B -q dependency:go-offline || true

# 再拷源码
COPY erp-common      ./erp-common
COPY erp-framework   ./erp-framework
COPY erp-system      ./erp-system
COPY erp-business    ./erp-business
COPY erp-admin       ./erp-admin

RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/erp-admin/target/erp-admin-*.jar app.jar
ENV JAVA_OPTS="-Xms512m -Xmx1g -Dfile.encoding=UTF-8"
ENV TZ=Asia/Shanghai
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
