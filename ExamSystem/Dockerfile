FROM openjdk:11-jre-slim
VOLUME /tmp
ARG SET_IMPL=default
ENV SET_IMPL=${SET_IMPL}
COPY target/exam-system-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["sh", "-c", "java -jar /app.jar --spring.profiles.active=${SET_IMPL}"]
