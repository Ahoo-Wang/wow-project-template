plugins {
    // me.ahoo.wow: KSP with wow-compiler over the aggregates and processors, and wow-kernel. No Spring here.
    id("wow.domain-conventions")
}

wow {
    // The package of the generated `DemoContract` that other services import: set once, kept as aggregates are added.
    contractPackage = "me.ahoo.wow.template.api"
    // The service hosting the context; must equal `spring.application.name`.
    service = "demo-service"
}

dependencies {
    api(project(":api"))
    // The functional aggregate and saga test DSL (aggregateSpec, sagaSpec).
    testImplementation("me.ahoo.wow:wow-test")
}

kover {
    reports {
        filters {
            excludes {
                // KSP output: the generated models, handlers and the binding index are the compiler's, not ours.
                annotatedBy("javax.annotation.processing.Generated", "me.ahoo.wow.api.annotation.Generated")
                classes("*ModelProvider*", "*Model", "*ModelKt", "*Subscriptions*", "*Properties")
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}
