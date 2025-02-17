FROM amazoncorretto:22
WORKDIR /app
COPY build/libs/onlyTradeBE-all.jar ktor-app.jar
EXPOSE 8080
CMD ["java", "-jar", "ktor-app.jar"]
