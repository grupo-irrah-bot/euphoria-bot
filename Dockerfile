FROM amazoncorretto:21 as builder

WORKDIR /app
COPY . .
FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

COPY .env .env

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
