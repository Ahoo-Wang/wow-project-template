pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "wow-project-template"
include(":api")
include(":domain")
include(":server")
include(":code-coverage-report")
include(":dependencies")
include(":bom")
