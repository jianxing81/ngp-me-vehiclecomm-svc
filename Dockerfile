# syntax=docker/dockerfile:1
# Provide ECR URI
ARG ECR_URI
# Download GuanceAgent
FROM ${ECR_URI}/curl:7.81.0 AS GUANCE_AGENT
ARG GUANCE_AGENT_URL="https://static.guance.com/dd-image/dd-java-agent.jar"
RUN curl --silent --fail -L ${GUANCE_AGENT_URL} -o "/tmp/dd-java-agent.jar"

FROM ${ECR_URI}/openjdk:17 AS deploy
ARG profile
ENV APP_HOME /app
ENV APP_USER appuser
ENV JAVA_OPTS="${JAVA_OPTS} -Dspring.profiles.active=$profile"

WORKDIR $APP_HOME

COPY ./redis_ca.pem cacert.pem
RUN keytool -import -noprompt -trustcacerts -alias rediscloudcacert -file ./cacert.pem -cacerts -storepass changeit

COPY techframework/build/libs/techframework-0.0.1-SNAPSHOT.jar ./service.jar
COPY --from=GUANCE_AGENT /tmp/dd-java-agent.jar ./dd-java-agent.jar
COPY startup.sh ./startup.sh
RUN chmod -x ./startup.sh

RUN adduser -M -N $APP_USER
RUN chown -R $APP_USER $APP_HOME
USER $APP_USER

EXPOSE 8080
ENTRYPOINT ["sh", "./startup.sh"]