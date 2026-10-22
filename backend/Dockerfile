FROM maven:3.9.14-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:24-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -g 10001 -S omya && adduser -u 10001 -S omya -G omya
COPY --from=build --chown=omya:omya /workspace/target/hospital-backend-2.0.0.jar app.jar
USER 10001:10001
EXPOSE 8080 9091
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -q -O - http://127.0.0.1:9091/actuator/health/readiness || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
