package com.imyvm.adventure.util.text

import com.imyvm.adventure.WorldGeoAdventureAddon.Companion.MOD_ID
import com.imyvm.adventure.infra.WildernessConfig
import com.imyvm.hoki.i18n.HokiLanguage
import com.imyvm.hoki.i18n.HokiTranslator
import net.minecraft.network.chat.Component

object Translator : HokiTranslator() {
    private var languageInstance = createLanguage(WildernessConfig.LANGUAGE.value)

    init {
        WildernessConfig.LANGUAGE.changeEvents.register { option, _, _ ->
            languageInstance = createLanguage(option.value)
        }
    }

    fun tr(key: String?, vararg args: Any?): Component {
        val raw = key?.let { languageInstance.get(it) }
        val formatted = if (args.isNotEmpty()) {
            raw?.let { java.text.MessageFormat.format(it, *args) }
        } else {
            raw
        }
        return formatted?.let { TextParser.parse(it) } ?: Component.empty()
    }

    fun raw(key: String?, vararg args: Any?): String? {
        val rawValue = key?.let { languageInstance.get(it) }
        return if (args.isNotEmpty()) {
            rawValue?.let { java.text.MessageFormat.format(it, *args) }
        } else {
            rawValue
        }
    }

    private fun createLanguage(languageId: String) = HokiLanguage.create(
        HokiLanguage.getResourcePath(MOD_ID, languageId)
            .let { Translator::class.java.getResourceAsStream(it) }
    )
}
