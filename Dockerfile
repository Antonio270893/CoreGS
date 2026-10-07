FROM eclipse-temurin:23-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "app.jar"]