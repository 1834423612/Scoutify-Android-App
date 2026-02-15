import com.android.build.api.dsl.ApplicationExtension
import java.util.Properties

plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose") // Compose compiler plugin
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
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "CASDOOR_ENDPOINT", "\"${localProperties["casdoor_endpoint"]}\"")
        buildConfigField("String", "CASDOOR_CLIENT_ID", "\"${localProperties["casdoor_client_id"]}\"")
        buildConfigField("String", "CASDOOR_CLIENT_SECRET", "\"${localProperties["casdoor_client_secret"]}\"")
        buildConfigField("String", "CASDOOR_REDIRECT_URI", "\"${localProperties["casdoor_redirect_uri"]}\"")
        buildConfigField("String", "CASDOOR_APP_NAME", "\"${localProperties["casdoor_app_name"]}\"")

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
    implementation(libs.accompanist.systemuicontroller) // 控制状态栏颜色
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
    implementation(libs.androidx.security.crypto)
    // datastore
    implementation(libs.androidx.datastore)
}

android.buildFeatures.buildConfig = true