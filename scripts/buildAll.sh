#!/bin/bash
scriptDir=$(dirname $0)

IMAGE_NAME=quay.io/ibmcase/eda-kc-order-ms-mq
./mvnw clean package -DskipTests -P !registry
docker build -f src/main/docker/Dockerfile.jvm -t ${IMAGE_NAME} .
docker push ${IMAGE_NAME}
