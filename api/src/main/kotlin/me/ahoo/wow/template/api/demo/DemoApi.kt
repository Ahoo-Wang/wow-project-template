package me.ahoo.wow.template.api.demo

import me.ahoo.wow.template.api.demo.query.DemoDto
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono

@HttpExchange("demo")
interface DemoApi {

    @GetExchange("{id}")
    fun getById(@PathVariable id: String): Mono<DemoDto>
}
