# ============================================
# Shop-Online 多阶段构建 Dockerfile
# ============================================

# ———— Stage 1: Maven 构建 ————
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ———— Stage 2: 运行镜像 ————
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 创建非 root 用户
RUN addgroup -g 1000 app && adduser -u 1000 -G app -D app
USER app

# 复制构建产物
COPY --from=builder /app/target/*.jar app.jar

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
