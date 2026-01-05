package com.example.projectmanagement.datageneral.core.config

import android.util.Log
import com.example.projectmanagement.BuildConfig

object SupabaseConfig {
    val SUPABASE_URL = BuildConfig.SUPABASE_URL
    val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

    init {
        // Validate configuration
//        if (SUPABASE_URL == "https://your-project.supabase.co" || SUPABASE_URL.isEmpty()) {
//            Log.e("SupabaseConfig", "⚠️ WARNING: Supabase URL is not configured! Please add SUPABASE_URL to local.properties")
//        }
//        if (SUPABASE_ANON_KEY == "your-anon-key" || SUPABASE_ANON_KEY.isEmpty()) {
//            Log.e("SupabaseConfig", "⚠️ WARNING: Supabase ANON KEY is not configured! Please add SUPABASE_ANON_KEY to local.properties")
//        }
    }
}