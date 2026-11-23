/**
 * 每个 Kotlin 模块共享的约定：工具链、编译选项、静态检查、测试与覆盖率。角色约定（wow.api / wow.domain / wow.server）在此之上
 * 各自应用 Wow 的一个 Gradle 插件。
 */
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
    id("org.gradle.test-retry")
}

repositories {
    mavenCentral()
}

val libs = the<VersionCatalogsExtension>().named("libs")

kotlin {
    // Wow 10 以 JDK 21 构建；生产运行在 JDK 25 上（server/Dockerfile）。
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmDefault = JvmDefaultMode.ENABLE
        javaParameters = true
    }
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
    }
}

configure<DetektExtension> {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    // CI 只检查不改写源码；本地构建时自动修正格式问题。
    autoCorrect = !isInCI
}

dependencies {
    "detektPlugins"(libs.findLibrary("detekt-formatting").get())
    // 由 Spring Boot 的 BOM 统一管理各模块测试运行时的版本（JUnit launcher、Logback）。
    "testImplementation"(platform(libs.findLibrary("spring-boot-dependencies").get()))
    "testImplementation"(platform(libs.findLibrary("junit-bom").get()))
    "testImplementation"(platform(libs.findLibrary("fluent-assert-bom").get()))
    "testImplementation"("me.ahoo.test:fluent-assert-core")
    "testImplementation"("org.junit.jupiter:junit-jupiter")
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    "testRuntimeOnly"("ch.qos.logback:logback-classic")
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

kover {
    reports {
        filters {
            excludeWowGenerated()
        }
    }
}
