FROM ubuntu:latest

RUN apt-get update && apt-get upgrade -y
# main stuff we need
RUN apt-get install -y \
    openjdk-21-jdk \
    curl \
    bash \
    git \
    unzip \
    wget \
    software-properties-common

# gradle installation
RUN curl -fsSL https://services.gradle.org/distributions/gradle-8.11.1-bin.zip -o /tmp/gradle.zip \
    && unzip /tmp/gradle.zip -d /opt/ \
    && ln -s /opt/gradle-8.11.1/bin/gradle /usr/bin/gradle \
    && rm -rf /tmp/gradle.zip

# maven installation
RUN curl -fsSL https://downloads.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz | tar -xz -C /opt/ \
    && ln -s /opt/apache-maven-3.9.5/bin/mvn /usr/bin/mvn

# remove the old things.
RUN apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY scripts/build.sh /build.sh

ENTRYPOINT ["/bin/bash", "-l", "-c"]