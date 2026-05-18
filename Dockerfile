FROM eclipse-temurin:11-jdk

WORKDIR /app

COPY . .

RUN ./mvnw -q -DskipTests package || mvn -q -DskipTests package

EXPOSE 8080

CMD ["java", "-jar", "target/gatewatch-api-security-console.jar"]
