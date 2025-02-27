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
    alias(libs.plugins.ksp)
    application
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
    kotlin("kapt")
}

tasks.jar.configure {
    exclude("application.yaml", "bootstrap.yaml")
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
        "-XX:HeapDumpPath=data",
        "-Dcom.sun.management.jmxremote",
        "-Dcom.sun.management.jmxremote.authenticate=false",
        "-Dcom.sun.management.jmxremote.ssl=false",
        "-Dcom.sun.management.jmxremote.port=5555",
        "-Dspring.cloud.bootstrap.enabled=true",
        "-Dspring.cloud.bootstrap.location=config/bootstrap.yaml",
        "-Dspring.config.location=file:./config/",
    )
}

dependencies {
    implementation(platform(project(":dependencies")))
    ksp(platform(project(":dependencies")))
    kapt(platform(project(":dependencies")))
    ksp("me.ahoo.wow:wow-compiler")
    implementation("io.netty:netty-all")
    implementation(project(":domain"))
//    implementation("me.ahoo.wow:wow-kafka")
//    implementation("me.ahoo.wow:wow-mongo")
//    implementation("me.ahoo.wow:wow-elasticsearch")
    implementation("me.ahoo.wow:wow-mock")
    implementation("me.ahoo.wow:wow-opentelemetry")
    implementation("me.ahoo.wow:wow-webflux")
    implementation("me.ahoo.wow:wow-spring-boot-starter")
    implementation("me.ahoo.coapi:coapi-spring-boot-starter")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui")
//    implementation("me.ahoo.cosid:cosid-mongo")
//    implementation("me.ahoo.cosid:cosid-spring-redis")
//    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("me.ahoo.cosid:cosid-spring-boot-starter")
//    implementation("me.ahoo.cosec:cosec-webflux")
//    implementation("me.ahoo.cosec:cosec-spring-boot-starter")
//    implementation("me.ahoo.cosky:cosky-spring-cloud-starter-discovery")
//    implementation("me.ahoo.cosky:cosky-spring-cloud-starter-config")
//    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
