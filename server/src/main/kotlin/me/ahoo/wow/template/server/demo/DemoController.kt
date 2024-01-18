package me.ahoo.wow.template.server.demo

import me.ahoo.wow.template.api.demo.DemoApi
import me.ahoo.wow.template.api.demo.query.DemoDto
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class DemoController : DemoApi {
    override fun getById(id: String): Mono<DemoDto> {
        return Mono.just(DemoDto(id))
    }
}