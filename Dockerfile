FROM gradle:8.8-jdk21 AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
RUN ls -l /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]