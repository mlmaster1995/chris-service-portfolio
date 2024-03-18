# test the integration of jenkins and docker server
FROM tomcat:9.0.86-jdk17-corretto-al2
WORKDIR /usr/local/tomcat/webapps
COPY webapp.war .
EXPOSE 8080
CMD ["catalina.sh", "run"]