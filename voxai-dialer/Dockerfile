FROM eclipse-temurin:8-jre
WORKDIR /app
COPY target/voxai-dialer-*.jar /app/dialer.jar
ENTRYPOINT ["java", "-jar", "/app/dialer.jar"]
