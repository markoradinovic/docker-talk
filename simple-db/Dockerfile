FROM anapsix/alpine-java:jdk8

ENV VERTICLE_FILE simple-db-0.0.1-SNAPSHOT-fat.jar
ENV VERTICLE_HOME /usr/verticles

COPY entrypoint.sh /
COPY target/$VERTICLE_FILE $VERTICLE_HOME/

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]
