package me.ahoo.wow.template.server

import me.ahoo.wow.api.annotation.BoundedContext
import me.ahoo.wow.template.api.DemoService
import me.ahoo.wow.template.domain.DemoBoundedContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@BoundedContext(name = DemoService.SERVICE_NAME)
@SpringBootApplication(
    scanBasePackageClasses = [DemoBoundedContext::class, DemoApplication::class],
)
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
