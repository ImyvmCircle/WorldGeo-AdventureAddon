package com.imyvm.adventure.entrypoint.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStreamReader

abstract class JsonResourceLoader(private val resourcePath: String) {
    private var cached: JsonElement = JsonObject()

    fun load() {
        val stream = javaClass.classLoader.getResourceAsStream(resourcePath)
        cached = if (stream == null) {
            JsonObject()
        } else {
            stream.use { input ->
                InputStreamReader(input, Charsets.UTF_8).use { reader ->
                    JsonParser.parseReader(reader)
                }
            }
        }
    }

    fun data(): JsonElement = cached
}
