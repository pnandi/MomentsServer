#!/bin/sh

set -e

#chmod -R 777 /opt/moments
#chown -R jetty:jetty /var/lib/jetty/work

exec /docker-entrypoint.sh "$@"
