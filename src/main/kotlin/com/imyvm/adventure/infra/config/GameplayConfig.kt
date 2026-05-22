package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class GameplayConfig : HokiConfig("AdventureGameplay.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val SCHEDULER_HEARTBEAT_SECONDS = Option(
            "runtime.scheduler_heartbeat_seconds",
            60,
            "heartbeat interval for the runtime scheduler in debug mode."
        ) { obj, path ->
            obj.getInt(path)
        }
    }
}
