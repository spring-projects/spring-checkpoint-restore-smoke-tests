FROM ubuntu:focal-20220531

ADD setup.sh /setup.sh
ADD get-crac-jdk-url.sh /get-crac-jdk-url.sh
ADD get-docker-url.sh /get-docker-url.sh
ADD get-ytt-url.sh /get-ytt-url.sh
ADD get-docker-compose-url.sh /get-docker-compose-url.sh
ADD get-gradle-url.sh /get-gradle-url.sh
RUN ./setup.sh

ENV JAVA_HOME /opt/crac-jdk
ENV PATH $JAVA_HOME/bin:/opt/ytt/bin:/opt/docker-compose/bin:/opt/gradle/bin:$PATH
ENV GRADLE_OPTS -Dorg.gradle.project.buildDir=/tmp/gradle-build
RUN git config --global --add safe.directory /workspace
ADD docker-lib.sh /docker-lib.sh
