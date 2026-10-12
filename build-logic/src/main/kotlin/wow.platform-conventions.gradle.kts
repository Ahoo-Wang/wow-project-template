/**
 * Gradle 平台（BOM）：dependencies、bom。
 */
plugins {
    id("wow.base-conventions")
    `java-platform`
    id("wow.publish-conventions")
}

javaPlatform {
    allowDependencies()
}

publishing {
    publications {
        create<MavenPublication>("mavenBom") {
            from(components["javaPlatform"])
        }
    }
}
