FROM java:8

COPY target/spring-boot-web-0.0.1-SNAPSHOT.jar /

WORKDIR /

CMD java -jar spring-boot-web-0.0.1-SNAPSHOT.jar