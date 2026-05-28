package com.imyvm.adventure.infra.config

import com.imyvm.adventure.domain.model.ActionClass
import com.imyvm.adventure.domain.model.ActionEventType
import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class EconomyConfig : HokiConfig("AdventureEconomy.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val PLAYER_CES_RHO = Option(
            "player_ces.rho",
            -1.0,
            "CES elasticity rho for the player weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val PLAYER_CES_ETA = Option(
            "player_ces.eta",
            0.70,
            "CES weight eta for the player weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val PLAYER_BREAK_EVEN_RATIO = Option(
            "player_ces.break_even_ratio",
            0.60,
            "break-even ratio for the player weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val COMMUNITY_CES_RHO = Option(
            "community_ces.rho",
            -1.0,
            "CES elasticity rho for the community weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val COMMUNITY_CES_ETA = Option(
            "community_ces.eta",
            0.60,
            "CES weight eta for the community weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val COMMUNITY_BREAK_EVEN_RATIO = Option(
            "community_ces.break_even_ratio",
            0.85,
            "break-even ratio for the community weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val MOON_PHASE_WEIGHTS = Option(
            "moon_phase.phase_weights",
            listOf(1.0, 0.7, 0.4, 0.2, 0.1, 0.2, 0.4, 0.7),
            "phase_weight multiplier indexed by Minecraft moon phase 0..7."
        ) { obj, path -> obj.getDoubleList(path).map { it.toDouble() } }

        @JvmField
        @ConfigOption
        val OP_CLASS_WEIGHT_PROBE = Option(
            "operation_score.class_weight.probe",
            ActionClass.PROBE.defaultWeight,
            "w_class multiplier for probe actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_CLASS_WEIGHT_SAMPLE = Option(
            "operation_score.class_weight.sample",
            ActionClass.SAMPLE.defaultWeight,
            "w_class multiplier for sample actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_CLASS_WEIGHT_COMBAT = Option(
            "operation_score.class_weight.combat",
            ActionClass.COMBAT.defaultWeight,
            "w_class multiplier for combat actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_CLASS_WEIGHT_CACHE = Option(
            "operation_score.class_weight.cache",
            ActionClass.CACHE.defaultWeight,
            "w_class multiplier for cache actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_CLASS_WEIGHT_AERIAL = Option(
            "operation_score.class_weight.aerial",
            ActionClass.AERIAL.defaultWeight,
            "w_class multiplier for aerial actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_CLASS_WEIGHT_LOGISTICS_TRADE = Option(
            "operation_score.class_weight.logistics_trade",
            ActionClass.LOGISTICS_TRADE.defaultWeight,
            "w_class multiplier for logistics and trade actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_READ = Option(
            "operation_score.base.read",
            ActionEventType.READ.defaultBaseScore,
            "baseScore for a read event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_SAMPLE_BLOCK = Option(
            "operation_score.base.sample_block",
            ActionEventType.SAMPLE_BLOCK.defaultBaseScore,
            "baseScore for a sample_block event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_SAMPLE_ENTITY = Option(
            "operation_score.base.sample_entity",
            ActionEventType.SAMPLE_ENTITY.defaultBaseScore,
            "baseScore for a sample_entity event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_BRUSH = Option(
            "operation_score.base.brush",
            ActionEventType.BRUSH.defaultBaseScore,
            "baseScore for a brush event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_COMBAT = Option(
            "operation_score.base.combat",
            ActionEventType.COMBAT.defaultBaseScore,
            "baseScore for a combat event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_CHEST = Option(
            "operation_score.base.chest",
            ActionEventType.CHEST.defaultBaseScore,
            "baseScore for a chest event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_RARE_CACHE = Option(
            "operation_score.base.rare_cache",
            ActionEventType.RARE_CACHE.defaultBaseScore,
            "baseScore for a rare_cache event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_AIR_HIT = Option(
            "operation_score.base.air_hit",
            ActionEventType.AIR_HIT.defaultBaseScore,
            "baseScore for an air_hit event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_AIR_HAUL = Option(
            "operation_score.base.air_haul",
            ActionEventType.AIR_HAUL.defaultBaseScore,
            "baseScore for an air_haul event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_LOGISTICS = Option(
            "operation_score.base.logistics",
            ActionEventType.LOGISTICS.defaultBaseScore,
            "baseScore for a logistics event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val OP_BASE_SCORE_TRADE = Option(
            "operation_score.base.trade",
            ActionEventType.TRADE.defaultBaseScore,
            "baseScore for a trade event."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val ALLOWANCE_FRACTION_PROBE = Option(
            "allowance.fraction.probe",
            ActionClass.PROBE.defaultAllowanceFraction,
            "alpha_allowance for probe actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val ALLOWANCE_FRACTION_SAMPLE = Option(
            "allowance.fraction.sample",
            ActionClass.SAMPLE.defaultAllowanceFraction,
            "alpha_allowance for sample actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val ALLOWANCE_FRACTION_COMBAT = Option(
            "allowance.fraction.combat",
            ActionClass.COMBAT.defaultAllowanceFraction,
            "alpha_allowance for combat actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val ALLOWANCE_FRACTION_CACHE = Option(
            "allowance.fraction.cache",
            ActionClass.CACHE.defaultAllowanceFraction,
            "alpha_allowance for cache actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val ALLOWANCE_FRACTION_AERIAL = Option(
            "allowance.fraction.aerial",
            ActionClass.AERIAL.defaultAllowanceFraction,
            "alpha_allowance for aerial actions."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val ALLOWANCE_FRACTION_LOGISTICS_TRADE = Option(
            "allowance.fraction.logistics_trade",
            ActionClass.LOGISTICS_TRADE.defaultAllowanceFraction,
            "alpha_allowance for logistics and trade actions."
        ) { obj, path -> obj.getDouble(path) }

        fun classWeightFor(actionClass: ActionClass): Double = when (actionClass) {
            ActionClass.PROBE -> OP_CLASS_WEIGHT_PROBE.value
            ActionClass.SAMPLE -> OP_CLASS_WEIGHT_SAMPLE.value
            ActionClass.COMBAT -> OP_CLASS_WEIGHT_COMBAT.value
            ActionClass.CACHE -> OP_CLASS_WEIGHT_CACHE.value
            ActionClass.AERIAL -> OP_CLASS_WEIGHT_AERIAL.value
            ActionClass.LOGISTICS_TRADE -> OP_CLASS_WEIGHT_LOGISTICS_TRADE.value
        }

        fun allowanceFractionFor(actionClass: ActionClass): Double = when (actionClass) {
            ActionClass.PROBE -> ALLOWANCE_FRACTION_PROBE.value
            ActionClass.SAMPLE -> ALLOWANCE_FRACTION_SAMPLE.value
            ActionClass.COMBAT -> ALLOWANCE_FRACTION_COMBAT.value
            ActionClass.CACHE -> ALLOWANCE_FRACTION_CACHE.value
            ActionClass.AERIAL -> ALLOWANCE_FRACTION_AERIAL.value
            ActionClass.LOGISTICS_TRADE -> ALLOWANCE_FRACTION_LOGISTICS_TRADE.value
        }

        fun baseScoreFor(eventType: ActionEventType): Double = when (eventType) {
            ActionEventType.READ -> OP_BASE_SCORE_READ.value
            ActionEventType.SAMPLE_BLOCK -> OP_BASE_SCORE_SAMPLE_BLOCK.value
            ActionEventType.SAMPLE_ENTITY -> OP_BASE_SCORE_SAMPLE_ENTITY.value
            ActionEventType.BRUSH -> OP_BASE_SCORE_BRUSH.value
            ActionEventType.COMBAT -> OP_BASE_SCORE_COMBAT.value
            ActionEventType.CHEST -> OP_BASE_SCORE_CHEST.value
            ActionEventType.RARE_CACHE -> OP_BASE_SCORE_RARE_CACHE.value
            ActionEventType.AIR_HIT -> OP_BASE_SCORE_AIR_HIT.value
            ActionEventType.AIR_HAUL -> OP_BASE_SCORE_AIR_HAUL.value
            ActionEventType.LOGISTICS -> OP_BASE_SCORE_LOGISTICS.value
            ActionEventType.TRADE -> OP_BASE_SCORE_TRADE.value
        }
    }
}
