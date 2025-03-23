plugins {
    alias(libs.plugins.ksp)
}
dependencies {
    api(platform(project(":dependencies")))
    ksp(platform(project(":dependencies")))
    api(project(":api"))
    api("me.ahoo.wow:wow-spring")
    api("io.github.oshai:kotlin-logging-jvm")
    ksp("me.ahoo.wow:wow-compiler")
    testImplementation("me.ahoo.wow:wow-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test, tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                minimum = 0.8.toBigDecimal()
            }
        }
    }
}
