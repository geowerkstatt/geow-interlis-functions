FROM scratch
ARG VERSION
LABEL org.opencontainers.image.source=https://github.com/geowerkstatt/geow-interlis-functions
COPY build/libs/geow-interlis-functions-${VERSION}.jar /
