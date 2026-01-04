package com.example.projectmanagement.datageneral.core

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.ktor.client.engine.okhttp.OkHttp

class SupabaseClient(private val url: String, private val key: String) {
    private val client = createSupabaseClient(url, key) {
        install(Auth.Companion)
        install(Postgrest.Companion)
        install(Realtime.Companion)
        httpEngine = OkHttp.create()
    }
    val db = client.postgrest
    val auth = client.auth
    val realtime = client.realtime
}