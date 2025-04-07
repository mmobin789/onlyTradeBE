FROM amazoncorretto:22
WORKDIR /app
COPY build/libs/onlyTradeBE-all.jar ktor-app.jar
CMD ["sh", "-c", "java -jar ktor-app.jar -Dktor.deployment.port=$PORT"]
