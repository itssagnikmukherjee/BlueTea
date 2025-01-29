import com.android.build.api.variant.BuildConfigField
import org.jetbrains.kotlin.fir.declarations.builder.buildConstructor
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
    namespace = "com.itssagnikmukherjee.blueteaadmin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.itssagnikmukherjee.blueteaadmin"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String","SUPABASE_KEY",properties.getProperty("SUPABASE_KEY"))
        buildConfigField("String","SUPABASE_URL",properties.getProperty("SUPABASE_URL"))

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
    implementation(libs.kotlinx.serialization.json)

    //dagger hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    //firebase
    implementation(libs.firebase.firestore)


    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.play.services.maps)

    //supabase
    implementation("io.github.jan-tennert.supabase:storage-kt:3.0.3")
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.3"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:3.0.3")

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