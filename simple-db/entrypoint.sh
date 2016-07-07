#!/bin/bash
set -e

echo "Inside entrypoint.sh:" ${VERTICLE_HOME} ${VERTICLE_FILE}
exec java -Djava.security.egd=file:/dev/./urandom -jar $VERTICLE_HOME/$VERTICLE_FILE
