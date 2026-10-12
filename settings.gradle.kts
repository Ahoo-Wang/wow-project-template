pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "wow-project-template"
include(":api")
include(":api-client")
include(":domain")
include(":server")
include(":code-coverage-report")
include(":dependencies")
include(":bom")
