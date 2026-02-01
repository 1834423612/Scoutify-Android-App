//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//
//buildscript {
//    extra.apply {
//        set("room_version", "2.6.0")
//    }
//}
//
//plugins {
////    alias(libs.plugins.android.application) apply false
////    alias(libs.plugins.kotlin.android) apply false
////    alias(libs.plugins.kotlin.compose) apply false
//
//    id("com.android.application") version "8.8.0" apply false
//    id("com.android.library") version "8.8.0" apply false
//    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
//    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
//}
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply {
        set("room_version", "2.6.0")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    //alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    kotlin("android") version "2.0.20" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
}