FROM adoptopenjdk/openjdk11:ubi

COPY ./app/build/libs/* /app/

RUN mkdir /app/logs

EXPOSE 8991

WORKDIR /app

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f localhost:8991/actuator/health || exit 1

CMD ["java", "-Dcom.sun.management.jmxremote",
             "-Dcom.sun.management.jmxremote.port=22324",
             "-Dcom.sun.management.jmxremote.authenticate=false",
             "-Dcom.sun.management.jmxremote.ssl=false",
             "-jar", "sus-0.0.1-SNAPSHOT.jar"]