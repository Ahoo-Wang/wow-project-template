package me.ahoo.wow.template.api.demo

import me.ahoo.wow.template.DemoService
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange(DemoService.DEMO_AGGREGATE_NAME)
interface DemoApi
