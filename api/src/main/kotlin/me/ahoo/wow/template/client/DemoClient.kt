package me.ahoo.wow.template.client

import me.ahoo.coapi.api.CoApi
import me.ahoo.wow.template.DemoService
import me.ahoo.wow.template.api.demo.DemoApi

@CoApi(serviceId = DemoService.SERVICE_NAME)
interface DemoClient : DemoApi
