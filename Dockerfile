#Build JAR using Maven
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper & project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Make mvnw runnable
RUN chmod +x mvnw

# Build project (skip tests)
RUN ./mvnw clean package -DskipTests

#Run the JAR
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy built jar from stage 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
