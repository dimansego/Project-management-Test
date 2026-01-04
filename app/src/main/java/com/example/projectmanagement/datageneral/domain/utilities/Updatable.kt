package com.example.projectmanagement.datageneral.domain.utilities

import kotlinx.serialization.json.JsonObject

interface Updatable {
    val modifiedFields: MutableSet<String>
    fun asUpdateMap(): JsonObject
}