#
# harmony-alpine
#
# FROM adoptopenjdk/openjdk8:jre8u292-b10-alpine
FROM alpine:latest

# Define environment variables for product, version, etc.
ENV CLEO_HOME=/opt/harmony

RUN apk add openjdk8-jre fontconfig \
    && addgroup cleo \
    && adduser -h /home/cleo -G cleo harmony -D \
    && rm -rf /tmp/*.apk /tmp/gcc /tmp/gcc-libs.tar* /tmp/libz /tmp/libz.tar.xz /var/cache/apk/*

USER harmony
WORKDIR $CLEO_HOME

CMD ["daemon"]
ENTRYPOINT ["./harmonyentrypoint"]
# ENTRYPOINT ["/bin/ash", "-c", "sleep infinity"]
