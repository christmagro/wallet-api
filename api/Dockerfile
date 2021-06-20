FROM adoptopenjdk/openjdk11
LABEL maintainer="christmagro@gmail.com"
WORKDIR /app
COPY target/api-1.0.0-SNAPSHOT.jar /app/wallet-service.jar
ENTRYPOINT ["java","-jar","wallet-service.jar"]
