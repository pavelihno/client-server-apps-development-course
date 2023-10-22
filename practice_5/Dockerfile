FROM openjdk:17-jdk-slim

WORKDIR /app
COPY . /app

# RUN ./gradlew bootJar

CMD ["java", "-jar", "/app/build/libs/app.jar"]

EXPOSE 8080