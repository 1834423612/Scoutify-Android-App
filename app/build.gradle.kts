import com.android.build.api.dsl.ApplicationExtension

plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.android")
//    id("io.objectbox") // ObjectBox plugin
//    //id("io.objectbox") version "4.0.2" apply false
    id("com.android.application")
    //id("io.objectbox") // ObjectBox plugin
    id("org.jetbrains.kotlin.plugin.compose") // Compose compiler plugin
    alias(libs.plugins.sqldelight) // Apply the plugin
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
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0") // 控制状态栏颜色
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // Casdoor Login
    implementation(group = "org.casbin", name = "casdoor-android-sdk", version = "0.0.1")
    implementation("io.objectbox:objectbox-android:4.0.2")
    // SQL Delight
    implementation(libs.sqldelight.android)
    implementation(libs.sqldelight.coroutines)
}