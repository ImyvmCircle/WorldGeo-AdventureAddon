package com.imyvm.adventure.domain.model

class AdventurePersistentState {
    var schemaVersion: Int = 1
    val regionProfiles: MutableMap<Int, AdventureRegionProfile> = linkedMapOf()
    val anchors: MutableMap<String, AdventureAnchor> = linkedMapOf()
    val boards: MutableMap<String, AdventureBoard> = linkedMapOf()
    val projects: MutableMap<String, AdventureProject> = linkedMapOf()
    val listings: MutableMap<String, ShareTransferListing> = linkedMapOf()
    val playerRisk: MutableMap<String, PlayerRiskState> = linkedMapOf()
}
