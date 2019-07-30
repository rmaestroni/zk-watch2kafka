FROM maven:3.6-jdk-11 AS maven-build
LABEL maintainer Roberto Maestroni <r.maestroni@gmail.com>

ENV RELEASE_VERSION SNAPSHOT

WORKDIR /app

# download dependencies first to leverage build cache
COPY pom.xml .
RUN ["mvn", "verify", "clean", "--fail-never"]
RUN ["mvn", "dependency:copy-dependencies"]

# run Maven build
COPY src src
RUN ["mvn", "package"]
RUN cd target && ln -s "zk-watch2kafka-${RELEASE_VERSION}.jar" zk-watch2kafka.jar

# end of build stage

FROM openjdk:11

WORKDIR /app

ENV LOG_LEVEL="INFO" \
  JAVA_OPTIONS="-Xmx512m"

COPY --from=maven-build /app/target/zk-watch2kafka.jar .
COPY --from=maven-build /app/target/dependency ./dependency
COPY entrypoint.sh .

RUN chmod u+x entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]
