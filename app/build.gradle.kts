import com.android.build.api.dsl.ApplicationExtension
import java.util.Properties

plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose") // Compose compiler plugin
    id("kotlin-parcelize")
    alias(libs.plugins.sqldelight) // Apply the plugin
}

// ENV CONFIG

val localProperties = Properties()
val secretsPropertiesFile = rootProject.file("app/secrets.properties")
if (secretsPropertiesFile.exists()) {
    localProperties.load(secretsPropertiesFile.inputStream())
}

configure<ApplicationExtension> {
    namespace = "com.team695.scoutifyapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.team695.scoutifyapp"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        fun requireSecret(key: String): String =
            localProperties[key]?.toString()
                ?: if (secretsPropertiesFile.exists()) error("Missing key '$key' in secrets.properties")
                   else ""   // allow local/CI builds without secrets

        buildConfigField("String", "CASDOOR_ENDPOINT", "\"${requireSecret("casdoor_endpoint")}\"")
        buildConfigField("String", "CASDOOR_CLIENT_ID", "\"${requireSecret("casdoor_client_id")}\"")
        buildConfigField("String", "CASDOOR_CLIENT_SECRET", "\"${requireSecret("casdoor_client_secret")}\"")
        buildConfigField("String", "CASDOOR_REDIRECT_URI", "\"${requireSecret("casdoor_redirect_uri")}\"")
        buildConfigField("String", "CASDOOR_APP_NAME", "\"${requireSecret("casdoor_app_name")}\"")

        proguardFiles()
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.team695.scoutifyapp.db")
        }
    }
}

dependencies {
    implementation(libs.androidx.compose.adaptive)
    implementation(libs.androidx.compose.adaptive.layout)
    implementation(libs.androidx.compose.adaptive.navigation.v130alpha08)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.systemuicontroller) // Control status bar color
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // Casdoor Login
    implementation(group = "org.casbin", name = "casdoor-android-sdk", version = "0.0.1")
    // SQL Delight
    implementation(libs.sqldelight.android)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.sqldelight.primitive.adapters)
    implementation(libs.androidx.security.crypto)
    // datastore
    implementation(libs.androidx.datastore)
}

android.buildFeatures.buildConfig = true