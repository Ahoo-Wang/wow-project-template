/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    // me.ahoo.wow.service：服务的 Manifest、OpenAPI 与 Helm values，`check` 中对 manifest/baseline.json 的兼容性校验，
    // 以及 integrationTest 测试套件。
    id("wow.server-conventions")
}

application {
    mainClass.set("me.ahoo.wow.template.server.BlogServerKt")
    applicationDefaultJvmArgs = listOf(
        // 发行版目录下的 config/application.yaml 覆盖 jar 内的基线配置。
        "-Dspring.config.additional-location=optional:file:./config/",
    )
}

dependencies {
    implementation(project(":blog-domain"))
    // Wow 宿主：Spring Boot 自动配置、公开端口的路由（WebFlux）与内部端口。
    implementation("me.ahoo.wow:wow-spring-boot")
    // `mongo` profile：MongoDB 副本集上的日志、快照、预留与定时（Wow 推荐的默认存储）。
    // 不启用该 profile 时，本机运行以内存保存数据。
    implementation("me.ahoo.wow:wow-mongo")
    // `cosec` profile：在 CoSec 网关之后，读取网关验证并转发的 CoSec-Identity 作为调用方。
    implementation("me.ahoo.wow:wow-cosec")
    implementation(platform(libs.cosec.bom))
    implementation("me.ahoo.cosec:cosec-spring-boot")
    implementation("me.ahoo.cosec:cosec-webflux")
    // 节点各环节的指标与链路，由部署时挂载的 OpenTelemetry Java agent 导出；未挂载时为空操作。
    implementation("me.ahoo.wow:wow-observability")
    // Swagger UI（/swagger-ui.html）：宿主把 Wow 由 Manifest 生成的文档并入 springdoc 的文档。
    implementation(platform(libs.springdoc.bom))
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui")
    // Bean Validation 实现：宿主在准入时用它校验命令的 Jakarta 约束。
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Actuator：宿主把节点的健康状态接入 readiness 与 liveness 探针组。
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // CoSec 网关模式的测试：以网关转发的身份（CoSecIdentity）调用服务。
    testImplementation(platform(libs.cosec.bom))
    testImplementation("me.ahoo.cosec:cosec-testkit")
    // integrationTest：在容器中的 MongoDB 副本集上运行服务。
    integrationTestImplementation("org.testcontainers:testcontainers-mongodb")
}
