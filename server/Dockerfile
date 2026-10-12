ARG APP_NAME=server
ARG WORK_HOME=/opt/${APP_NAME}

FROM dragonwell-registry.cn-hangzhou.cr.aliyuncs.com/dragonwell/dragonwell:25-ubuntu AS base

FROM base AS build
ARG WORK_HOME
ARG APP_NAME

WORKDIR ${WORK_HOME}
COPY build/install/${APP_NAME} .

FROM base AS run
ARG WORK_HOME

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

COPY --from=build --chown=appuser:appgroup ${WORK_HOME} ${WORK_HOME}

WORKDIR ${WORK_HOME}
EXPOSE 8080
# 健康检查由编排层负责（见 deploy/deployment.yaml 的 startup/readiness/liveness 探针），
# 避免依赖基础镜像中不一定存在的 curl。

USER appuser
STOPSIGNAL SIGTERM

ENTRYPOINT ["bin/server"]
