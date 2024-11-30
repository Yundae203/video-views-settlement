#Build Phase
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./

RUN gradle dependencies --no-daemon

COPY . /app

RUN gradle clean build --no-daemon

#Run Phase
FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/build/libs/*.jar ./world.jar

EXPOSE 8080
CMD ["java", "-jar", "world.jar"]