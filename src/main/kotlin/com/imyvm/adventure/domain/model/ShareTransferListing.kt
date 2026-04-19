package com.imyvm.adventure.domain.model

data class ShareTransferListing(
    val listingId: String,
    val projectId: String,
    val sellerUuid: String,
    var shareCount: Int,
    var unitPrice: Long,
    var status: String = "OPEN"
)
