import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization") version "2.1.10"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.itssagnikmukherjee.blueteauser"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.itssagnikmukherjee.blueteauser"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String","SUPABASE_KEY",properties.getProperty("SUPABASE_KEY"))
        buildConfigField("String","SUPABASE_URL",properties.getProperty("SUPABASE_URL"))
        buildConfigField("String","STRIPE_PUBLISHABLE_KEY",properties.getProperty("STRIPE_PUBLISHABLE_KEY"))
        buildConfigField("String","STRIPE_SECRET_KEY",properties.getProperty("STRIPE_SECRET_KEY"))
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
        compose = true
        buildConfig = true
    }
}

dependencies {

    //serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    //dagger hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.firebase.auth)
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    //firebase firestore
    implementation(libs.firebase.firestore)

    //coil
    implementation(libs.coil.compose)
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    //horizontal pager
    implementation("com.google.accompanist:accompanist-pager:0.32.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.32.0")

    //navigation
    implementation(libs.androidx.navigation.compose)

    //supabase
    implementation("io.github.jan-tennert.supabase:storage-kt:3.0.3")
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.3"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:3.0.3")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    //gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //stripe
    implementation("com.stripe:stripe-android:21.4.2")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}