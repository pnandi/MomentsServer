#FROM ubuntu
FROM jetty:9.3

RUN java -jar $JETTY_HOME/start.jar --add-to-startd=jmx,stats,servlets,logging

VOLUME /opt/moments
VOLUME /var/lib/jetty/work

#RUN chmod -R 777 /opt/moments
#RUN chown -R jetty:jetty /var/lib/jetty/work

EXPOSE 8080

ENTRYPOINT ["/moments-entrypoint.sh"]
CMD ["java","-Xms256m","-Xmx512m","-jar","/usr/local/jetty/start.jar"]
