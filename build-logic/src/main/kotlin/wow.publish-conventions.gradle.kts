/**
 * 可发布制品的 POM 与签名配置。发布物（publication）由 wow.api 约定创建。
 */
plugins {
    `maven-publish`
    signing
}

val libs = the<VersionCatalogsExtension>().named("libs")

// Kotlin Gradle 插件在 signing 插件存在时创建 kotlinBouncyCastleConfiguration，作为它的签名辅助任务（generatePgpKeys、
// checkSigningConfiguration、uploadPublicPgpKey，只在手动执行时运行）的类路径，以默认依赖写死 BouncyCastle 1.84。
// 加约束（而非依赖）不会取消默认依赖，只把版本抬到修复版本；BouncyCastle 的各模块须同一版本。发布签名用的是 Gradle 自带的
// BouncyCastle，不经过这个配置。
configurations.named { it == "kotlinBouncyCastleConfiguration" }.configureEach {
    for (module in listOf("bcprov", "bcpkix", "bcpg", "bcutil")) {
        project.dependencies.constraints.add(name, libs.findLibrary("bouncycastle-$module").get())
    }
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
