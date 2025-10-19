# ---- Build with Maven image (reliable) ----
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

COPY src src
RUN mvn -B clean package -DskipTests

# ---- Runtime ----
ENV JAVA_OPTS="-XX:+UseSerialGC -XX:+UseContainerSupport -XX:MaxRAMPercentage=60 -XX:InitialRAMPercentage=60 -XX:MaxMetaspaceSize=96m -XX:ReservedCodeCacheSize=48m -XX:MaxDirectMemorySize=64m -Xss256k -XX:+ExitOnOutOfMemoryError -Xlog:gc*:stdout:time,level,tags"

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
#EXPOSE 8081
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar $APP_OPTS \"$@\""]
CMD ["-Xmx256m", ""]
