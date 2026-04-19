package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class FinanceConfig : HokiConfig("AdventureFinance.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val SHARE_PRICE_MOVE_CAP_PERCENT = Option(
            "share.price_move_cap_percent",
            10,
            "maximum allowed price movement per settlement cycle in the limited secondary market."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val SHARE_MAX_SETTLEMENT_TRADES = Option(
            "share.max_settlement_trades",
            3,
            "maximum number of share transfers a player may execute within one settlement cycle."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val SHARE_MAX_TOTAL_EXPOSURE_PERCENT = Option(
            "share.max_total_exposure_percent",
            25,
            "maximum percentage of liquid commercial capital that may be exposed to the secondary market."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val SHARE_MAX_PROJECT_EXPOSURE_PERCENT = Option(
            "share.max_project_exposure_percent",
            15,
            "maximum percentage of liquid commercial capital that may be concentrated in one project."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val SHARE_BUYBACK_FLOOR_PERCENT = Option(
            "share.buyback_floor_percent",
            70,
            "discounted buyback floor for completed public projects."
        ) { obj, path ->
            obj.getInt(path)
        }
    }
}
