#
# alpine:vlproxy
#
FROM alpine:latest as base

# Install openjdk, rsync, unzip, wget, fontconfig, curl, and bash 
RUN apk --update add openjdk8 rsync unzip wget curl bash expect

# Define environment variables for product, version, etc.
ENV CLEO_INSTALL_FOLDER=/opt/vlproxy
RUN mkdir $CLEO_INSTALL_FOLDER
ARG HARMONY_SERIALS=UH0001-AL8914

# Make a non-root user and group
RUN addgroup cleo \
  && adduser -h /home/cleo -G cleo vlproxy -D \
  && chown vlproxy:cleo $CLEO_INSTALL_FOLDER
USER vlproxy
WORKDIR /opt/vlproxy

FROM base as installer

# Establish defaults for the options to add as well as the files to modify
ARG LAX_OPTIONS='-Djava.security.egd=file:/dev/./urandom'
ARG LAX_FILES="${CLEO_INSTALL_FOLDER}/*.lax"

COPY --chown=vlproxy target/install.bin $CLEO_INSTALL_FOLDER/install.bin
RUN chmod +x install.bin
RUN ./install.bin LAX_VM /usr/lib/jvm/java-1.8-openjdk/jre/bin/java -i silent -DUSER_INSTALL_DIR="$CLEO_INSTALL_FOLDER"
RUN sed -i -- "s|lax.nl.java.option.additional=|lax.nl.java.option.additional=${LAX_OPTIONS} |g" ${LAX_FILES} 
RUN rm ./install.bin

COPY configure.sh $CLEO_INSTALL_FOLDER

FROM base
COPY --from=installer --chown=vlproxy /opt/vlproxy /opt/vlproxy
WORKDIR /opt/vlproxy
ENV INDEX=1
RUN chmod +x configure.sh
RUN ./configure.sh $CLEO_INSTALL_FOLDER $HARMONY_SERIALS 
ENTRYPOINT ["/opt/vlproxy/VLProxyc"]
CMD ["bash"]
