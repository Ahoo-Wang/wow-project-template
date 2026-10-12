/**
 * 所有 Kotlin 模块（含 server）共享：工具链、编译选项、测试运行时与测试依赖。
 */
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode

plugins {
    id("wow.base-conventions")
    id("org.jetbrains.kotlin.jvm")
    id("org.gradle.test-retry")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        // 等价于旧的 -Xjvm-default=all-compatibility
        jvmDefault = JvmDefaultMode.ENABLE
        javaParameters = true
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
    jvmArgs("-Dlogback.configurationFile=${rootProject.file("config/logback.xml")}")
    retry {
        if (isInCI) {
            maxRetries = 2
            maxFailures = 20
        }
        failOnPassedAfterRetry = true
    }
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    "implementation"(platform(project(":dependencies")))
    "testImplementation"(platform(libs.findLibrary("junit-bom").get()))
    "testImplementation"("me.ahoo.test:fluent-assert-core")
    "testImplementation"("io.mockk:mockk") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    "testImplementation"("ch.qos.logback:logback-classic")
    "testImplementation"("org.junit.jupiter:junit-jupiter-api")
    "testImplementation"("org.junit.jupiter:junit-jupiter-params")
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine")
}
