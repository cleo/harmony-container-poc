#
# vlproxy-alpine
#
# FROM adoptopenjdk/openjdk8:jre8u292-b10-alpine
FROM alpine:latest

# Define environment variables for product, version, etc.
ENV CLEO_HOME=/opt/vlproxy
ENV VLPCONFIG=

# Establish defaults for the options to add as well as the files to modify
# ARG LAX_OPTIONS='-Djava.security.egd=file:/dev/./urandom'
# ARG LAX_FILES="${CLEO_HOME}/*.lax"
ARG VLPROXY_VERSION=5.7.0.1

COPY --chown=vlproxy target/install.bin $CLEO_HOME/install.bin
RUN apk add openjdk8-jre fontconfig curl xmlstarlet \
  && addgroup cleo \
  && adduser -h $CLEO_HOME -G cleo vlproxy -D \
  && chmod a+x $CLEO_HOME/install.bin \
  && $CLEO_HOME/install.bin -i silent -DUSER_INSTALL_DIR="$CLEO_HOME" \
  && rm $CLEO_HOME/install.bin \
  && curl -o $CLEO_HOME/docker-harmony-tool.jar 'https://raw.githubusercontent.com/cleo/harmony-container-poc/repository/com/cleo/labs/docker-harmony-tool/5.7.0.0/docker-harmony-tool-5.7.0.0.jar' \
  && echo '#!/bin/sh'$'\njava -cp $CLEO_HOME/docker-harmony-tool.jar:$(find $CLEO_HOME/lib -type d|sed "s|$|/*|"|paste -s -d : -):$CLEO_HOME/webserver/AjaxSwing/lib/ajaxswing.jar com.cleo.labs.docker.harmony.Tool "$@"' > $CLEO_HOME/docker-harmony-tool \
  && chmod a+x $CLEO_HOME/docker-harmony-tool \
  && chown -R vlproxy:cleo $CLEO_HOME \
  && rm -rf /tmp/*.apk /tmp/gcc /tmp/gcc-libs.tar* /tmp/libz /tmp/libz.tar.xz /var/cache/apk/*

COPY --chown=vlproxy:cleo vlproxyentrypoint $CLEO_HOME
RUN chmod a+x $CLEO_HOME/vlproxyentrypoint

USER vlproxy
WORKDIR $CLEO_HOME

# RUN /opt/harmony/Harmonyd start \
#   && sleep 10 \
#   && /opt/harmony/Harmonyc -s service,stop \
#   && xml ed --inplace -a 'Users/Usergroup[@alias="Administrators"]/User[last()]' -t elem -n Vlpoolaccess -v mySystem conf/Users.xml \
#   && /opt/harmony/Harmonyc \
#        -p "Local Listener/AS2"                   -t "<Service><enabled>True" \
#        -p "Local Listener/AS2"                   -t "<Service><Hostname>localhost" \
#        -p "Local Listener/AS3"                   -t "<Service><enabled>False" \
#        -p "Local Listener/ebXML Message Service" -t "<Service><enabled>False" \
#        -p "Local Listener/fasp"                  -t "<Service><enabled>False" \
#        -p "Local Listener/Odette FTP"            -t "<Service><enabled>False" \
#        -p "Local Listener/RosettaNet"            -t "<Service><enabled>False" \
#        -p "Local Listener/SMTP"                  -t "<Service><enabled>False"

CMD ["sh"]
ENTRYPOINT ["/opt/vlproxy/vlproxyentrypoint"]
