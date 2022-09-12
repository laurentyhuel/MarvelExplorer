import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("marvelexplorer.android.library")
    id("kotlinx-serialization")
}


val localProperties = gradleLocalProperties(rootDir)

android {
    namespace = "com.lyh.marvelexplorer.data.remote"

    defaultConfig {
        buildConfigField("String", "PUBLIC_API_KEY", localProperties.getProperty("publicApiKey"))
        buildConfigField("String", "PRIVATE_API_KEY", localProperties.getProperty("privateApiKey"))
    }
}

dependencies {
    implementation(libs.retrofit.core)

    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp.logging)

    implementation(libs.kotlinx.serialization.json)
}