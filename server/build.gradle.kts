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
    // me.ahoo.wow.service: the service's Manifest and OpenAPI, and `checkWowManifest` against manifest/baseline.json.
    id("wow.application-conventions")
}

tasks.jar.configure {
    manifest {
        attributes(
            "Implementation-Title" to application.applicationName,
            "Implementation-Version" to archiveVersion,
        )
    }
}
application {
    mainClass.set("me.ahoo.wow.template.server.ServerKt")
    applicationDefaultJvmArgs = listOf(
        "-Xlog:gc*:file=logs/$applicationName-gc.log:time,tags:filecount=10,filesize=32M",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-XX:HeapDumpPath=logs",
        // jar 内 application.yaml 为基线配置，发布包 config/ 目录用于环境覆盖。
        "-Dspring.config.additional-location=optional:file:./config/",
    )
}

tasks.named<JavaExec>("run") {
    // 上述 JVM 参数面向发行版目录结构（logs/、config/），本地 `server:run` 不使用。
    jvmArgs = emptyList()
}

dependencies {
    implementation(project(":domain"))
    // The Wow host: Spring Boot auto-configuration, the public routes (WebFlux) and the internal port.
    implementation("me.ahoo.wow:wow-spring-boot")
    // Traces and metrics of the node's seams, through the OpenTelemetry Java agent when one is attached.
    implementation("me.ahoo.wow:wow-observability")
    // Swagger UI over the service's routes (/swagger-ui.html): the host merges Wow's document into springdoc's.
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui")
    // A Bean Validation provider: the host checks each command's Jakarta constraints at admission.
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Actuator: the host gives the node's health to the readiness and liveness groups (deploy/deployment.yaml).
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // A backend: without one the node keeps its data in memory (development, or a loopback-only internal port).
//    implementation("me.ahoo.wow:wow-postgres")
//    runtimeOnly("com.zaxxer:HikariCP")
//    implementation("me.ahoo.wow:wow-mongo")
    // Published streams and commands between contexts.
//    implementation("me.ahoo.wow:wow-kafka")
    // Snapshots and their queries on Elasticsearch, as the only store or a search copy.
//    implementation("me.ahoo.wow:wow-elasticsearch")
    // CoSec 8 behind the CoSec gateway (`wow.cosec.mode: gateway`).
//    implementation("me.ahoo.wow:wow-cosec")
//    implementation("me.ahoo.cosec:cosec-spring-boot")
//    implementation("me.ahoo.cosec:cosec-webflux")
    // Aggregate ids from CosId 5 generators (the node's own ids stay UUIDv7).
//    implementation("me.ahoo.wow:wow-cosid")
//    implementation("me.ahoo.cosid:cosid-spring-boot-starter")
    // A shared state cache per aggregate type, read by peers as StateViewCache<V>.
//    implementation("me.ahoo.wow:wow-cocache")
//    implementation("me.ahoo.cocache:cocache-spring-boot-starter")
    // `wowServer<App>()`: the application on random ports with typed command and query clients.
    testImplementation("me.ahoo.wow:wow-test-spring-boot")
}
