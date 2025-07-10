FROM openjdk:17-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y iputils-ping postgresql-client
COPY target/quarkus-sample-1.0.0-SNAPSHOT-runner.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]