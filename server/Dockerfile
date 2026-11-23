# 服务镜像：复制 `./gradlew :blog-server:installDist` 的发行版（server/build/install/blog-server），在 JDK 25 上运行。
# Wow 以 JDK 21 构建、可在其上运行，但生产请用 JDK 25：JDK 24 之前，在 synchronized 中阻塞的虚拟线程会钉住载体线程。
#
#   ./gradlew :blog-server:installDist
#   docker build -t <registry>/blog-server:<tag> server
ARG BASE_IMAGE=dragonwell-registry.cn-hangzhou.cr.aliyuncs.com/dragonwell/dragonwell:25-ubuntu
ARG APP_NAME=blog-server
ARG WORK_HOME=/opt/${APP_NAME}

FROM ${BASE_IMAGE} AS base

FROM base AS build
ARG WORK_HOME
ARG APP_NAME

WORKDIR ${WORK_HOME}
COPY build/install/${APP_NAME} .

FROM base AS run
ARG WORK_HOME

# 以非 root 用户运行：UID 10001 与 wow-service chart 的 runAsUser 一致。
RUN groupadd -r -g 10001 appgroup && useradd -r -u 10001 -g appgroup appuser

COPY --from=build --chown=10001:10001 ${WORK_HOME} ${WORK_HOME}

WORKDIR ${WORK_HOME}
# 8080：公开端口（路由、actuator）；9090：Wow 的内部端口（集群通道），只在平台网络内可达。
EXPOSE 8080 9090

ENV JAVA_OPTS="-XX:+ExitOnOutOfMemoryError"

USER 10001
STOPSIGNAL SIGTERM

# 健康检查由编排层负责（chart 的 startup/readiness/liveness 探针），不依赖镜像里不一定有的 curl。
ENTRYPOINT ["bin/blog-server"]
