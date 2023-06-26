#
# ubuntu:bionic:harmony
#
ARG VERSION=bionic
FROM --platform=linux/amd64 ubuntu:$VERSION as base

# Install curl, ping, rsync, unzip, wget & libraries
RUN  sed 's/main$/main universe/' -i /etc/apt/sources.list \
  && apt update && apt install -y \
	curl iputils-ping net-tools python3-pip rsync unzip wget vim \
  && apt-get install -y --no-install-recommends libfontconfig1 \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/* \
  && rm -rf /tmp/*

# Define environment variables for product, version, etc.
ENV CLEO_INSTALL_FOLDER=/opt/harmony

# Make a non-root user and group
RUN mkdir $CLEO_INSTALL_FOLDER \
  && groupadd -f cleo \
  && useradd -m -G cleo harmony \
  && chown harmony:cleo $CLEO_INSTALL_FOLDER
USER harmony
WORKDIR /tmp

FROM base as installer

# Establish defaults for the options to add as well as the files to modify
ARG LAX_OPTIONS='-Djava.security.egd=file:/dev/./urandom'
ARG LAX_FILES="${CLEO_INSTALL_FOLDER}/*.lax"

COPY --chown=harmony target/install.bin .
RUN chmod +x install.bin \
  && ./install.bin \
	-i silent \
	-DUSER_INSTALL_DIR="$CLEO_INSTALL_FOLDER" \
  && sed -i -- "s|lax.nl.java.option.additional=|lax.nl.java.option.additional=${LAX_OPTIONS} |g" ${LAX_FILES} \
  && rm ./install.bin

COPY hosts $CLEO_INSTALL_FOLDER/hosts
COPY licenses $CLEO_INSTALL_FOLDER/licenses
COPY syncs $CLEO_INSTALL_FOLDER/syncs
COPY conf $CLEO_INSTALL_FOLDER/conf
COPY docker-entrypoint.sh $CLEO_INSTALL_FOLDER

COPY --chown=harmony target/0.6.zip /opt/harmony/0.6.zip

FROM base
COPY --from=installer --chown=harmony /opt/harmony /opt/harmony
WORKDIR /opt/harmony
ENV INDEX=1
RUN chmod +x /opt/harmony/docker-entrypoint.sh
ENTRYPOINT ["/opt/harmony/docker-entrypoint.sh"]
CMD ["bash"]
