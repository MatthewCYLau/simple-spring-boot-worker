FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn clean package -DskipTests -Pprod

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache dumb-init
RUN mkdir /app
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
COPY --from=build /project/target/awsapp-0.0.1-SNAPSHOT.jar /app/awsapp.jar
WORKDIR /app
RUN chown -R javauser:javauser /app
USER javauser

# Use JSON array syntax for exec form
CMD ["dumb-init", "java", "-jar", "awsapp.jar"]