dependencies {
    api(platform(project(":dependencies")))
    api(project(":api"))
    api("me.ahoo.wow:wow-apiclient")
    api("me.ahoo.coapi:coapi-api")
    api("org.springframework:spring-web")
    api("io.projectreactor:reactor-core")
}
