package com.template.plugin

import com.template.flow.YoFlow
import com.template.service.TemplateService
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.node.CordaPluginRegistry
import net.corda.core.node.PluginServiceHub
import net.corda.core.serialization.SerializationCustomization
import java.util.function.Function
import com.template.api.YoApi
import net.corda.core.crypto.Party

class YoPlugin : CordaPluginRegistry() {
    override val webApis = listOf(Function(::YoApi))
    override val requiredFlows = mapOf(YoFlow::class.java.name to setOf(Party::class.java.name))
    override val servicePlugins: List<Function<PluginServiceHub, out Any>> = listOf()
    override val staticServeDirs = mapOf("template" to javaClass.classLoader.getResource("templateWeb").toExternalForm())
}