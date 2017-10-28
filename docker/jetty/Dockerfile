#FROM ubuntu
FROM jetty:9.3

#ADD https://github.com/jetty-project/logging-modules/tree/master/logback/logging.mod modules/
RUN java -jar $JETTY_HOME/start.jar --add-to-startd=logging --add-to-startd=servlets --add-to-startd=jmx --add-to-startd=stats

VOLUME /opt/moments
VOLUME /var/lib/jetty/work

#RUN chmod -R 777 /opt/moments
#RUN chown -R jetty:jetty /var/lib/jetty/work

EXPOSE 8080

ENTRYPOINT ["/moments-entrypoint.sh"]
CMD ["java","-Xms256m","-Xmx512m","-jar","/usr/local/jetty/start.jar"]
