// Top-level build file where you can add configuration options common to all sub-projects/modules.
//buildscript {
//    dependencies {
//        classpath("io.objectbox:objectbox-gradle-plugin:4.0.2") // or latest
//    }
//}
//plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false
//    alias(libs.plugins.compose.compiler) apply false
//    alias(libs.plugins.kotlin.compose) apply false
//    alias(libs.plugins.kotlin.android) apply false
//}// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies{
        classpath("io.objectbox:objectbox-gradle-plugin:4.0.2")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.android) apply false
}
//plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false
//    alias(libs.plugins.compose.compiler) apply false
//    alias(libs.plugins.kotlin.compose) apply false
//}

