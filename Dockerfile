FROM eclipse-temurin:23-jre-noble

ENV TOMCAT_VERSION=10.1.24

# Устанавливаем Tomcat, а затем сразу удаляем curl и очищаем кэш apt в одном слое (RUN)
RUN apt-get update && apt-get install -y curl && \
    curl -O https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    tar -xvf apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    mv apache-tomcat-${TOMCAT_VERSION} /usr/local/tomcat && \
    rm apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    apt-get purge -y --auto-remove curl && \
    rm -rf /var/lib/apt/lists/*

RUN rm -rf /usr/local/tomcat/webapps/*
COPY target/main-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]