package me.ahoo.wow.template.server

import me.ahoo.wow.template.domain.DemoBoundedContext
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(
    scanBasePackageClasses = [DemoBoundedContext::class, Server::class],
)
class Server

fun main(args: Array<String>) {
    SpringApplication.run(arrayOf(Server::class.java), args)
}
