package com.example.projectmanagement.datageneral.core.config

import kotlinx.serialization.json.Json

object JsonConfig {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
        explicitNulls = false
        prettyPrint = true
    }
}