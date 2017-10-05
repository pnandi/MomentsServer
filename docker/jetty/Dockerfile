#FROM ubuntu
FROM jetty:9.3

RUN java -jar $JETTY_HOME/start.jar --add-to-startd=jmx,stats,servlets,logging

VOLUME /var/lib/jetty/work

EXPOSE 8080

ENTRYPOINT ["/moments-entrypoint.sh"]
CMD ["java","-Xms256m","-Xmx512m","-jar","/usr/local/jetty/start.jar"]
