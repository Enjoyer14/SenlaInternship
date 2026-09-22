FROM eclipse-temurin:23-jre-noble

ENV TOMCAT_VERSION=11.0.26 \
    CATALINA_HOME=/usr/local/tomcat \
    PATH=$PATH:/usr/local/tomcat/bin

RUN groupadd -r tomcat && useradd -r -g tomcat tomcat && \
    apt-get update && apt-get install -y curl && \
    curl -fSL https://archive.apache.org/dist/tomcat/tomcat-11/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -o tomcat.tar.gz && \
    mkdir -p ${CATALINA_HOME} && \
    tar -xvf tomcat.tar.gz -C ${CATALINA_HOME} --strip-components=1 && \
    rm tomcat.tar.gz && \
    rm -rf ${CATALINA_HOME}/webapps/* && \
    chown -R tomcat:tomcat ${CATALINA_HOME} && \
    apt-get purge -y --auto-remove curl && \
    rm -rf /var/lib/apt/lists/*

COPY target/*.war ${CATALINA_HOME}/webapps/ROOT.war
RUN chown tomcat:tomcat ${CATALINA_HOME}/webapps/ROOT.war

USER tomcat

EXPOSE 8080
CMD ["catalina.sh", "run"]
