FROM debian:wheezy

RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927 && \
	echo "deb http://repo.mongodb.org/apt/debian wheezy/mongodb-org/3.2 main" | tee /etc/apt/sources.list.d/mongodb-org-3.2.list && \
	apt-get update && \
	apt-get install -y mongodb-org-tools=3.2.6 mongodb-org-server=3.2.6 mongodb-org-shell=3.2.6 && \
	mkdir -p /data/db && \
	chown -R mongodb:mongodb /data/db

USER mongodb
EXPOSE 27017

RUN mongod --fork --syslog && \
	sleep 1 && \
	mongo admin --eval 'db.system.version.remove({});db.system.version.insert({ "_id" : "authSchema", "currentVersion" : 3 })' && \
	mongo xdb --eval 'db.createUser({user: "momentsroot",pwd: "moments",roles: [ { role: "dbOwner", db: "moments_db" } ]});db.auth("momentsroot", "moments")' && \
	mongod --shutdown

CMD ["mongod", "--auth"]
