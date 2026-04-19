package com.imyvm.adventure.domain.model

data class AdventureBoard(
    val boardId: String,
    val anchorId: String,
    var contentMode: String = "ADMIN_CONFIGURED",
    var templateId: String? = null,
    val tags: MutableSet<String> = linkedSetOf()
)
