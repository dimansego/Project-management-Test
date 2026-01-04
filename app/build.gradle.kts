plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.ksp)

}

android {
    namespace = "com.example.projectmanagement"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.projectmanagement"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Supabase Configuration
        // Please add these lines to your local.properties:
        // SUPABASE_URL="your_supabase_url"
        // SUPABASE_ANON_KEY="your_supabase_anon_key"

        val supabaseUrl = project.findProperty("SUPABASE_URL") as? String ?: "https://tkfbyyianifuaelcigzl.supabase.co"
        val supabaseAnonKey = project.findProperty("SUPABASE_ANON_KEY") as? String ?: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRrZmJ5eWlhbmlmdWFlbGNpZ3psIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA3OTc3MTcsImV4cCI6MjA3NjM3MzcxN30.7bWt9BwgFFUL_d_eljP-KjpVhNXdtWVzEsEOHnDuppI"

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = false
        buildConfig = true
    }
}

dependencies {
    val room_version = "2.7.1"
    
    // Room
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.3")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    
    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.realtime)

    // Ktor
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}