/**
 * 可发布制品共享的 POM 与签名配置。发布物（publication）由 library/platform 约定各自创建。
 */
plugins {
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            name = "projectBuildRepo"
            url = uri(layout.buildDirectory.dir("repos"))
        }
    }
    publications.withType<MavenPublication>().configureEach {
        artifactId = base.archivesName.get()
        pom {
            name.set(rootProject.name)
            description.set(providers.gradleProperty("description"))
            url.set(providers.gradleProperty("website"))
            issueManagement {
                system.set("GitHub")
                url.set(providers.gradleProperty("issues"))
            }
            scm {
                url.set(providers.gradleProperty("website"))
                connection.set(providers.gradleProperty("vcs"))
            }
            licenses {
                license {
                    name.set(providers.gradleProperty("license_name"))
                    url.set(providers.gradleProperty("license_url"))
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("ahoo-wang")
                    name.set("ahoo wang")
                    organization {
                        url.set(providers.gradleProperty("website"))
                    }
                }
            }
        }
    }
}

signing {
    // 本地 publishToMavenLocal 无需密钥；CI 中强制签名。
    isRequired = isInCI
    if (isInCI) {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_KEYID"),
            System.getenv("SIGNING_SECRETKEY"),
            System.getenv("SIGNING_PASSWORD"),
        )
    }
    sign(publishing.publications)
}
