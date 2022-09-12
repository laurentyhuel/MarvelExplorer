plugins {
    id("marvelexplorer.android.library")
}

android {
    namespace = "com.lyh.marvelexplorer.data"
}

dependencies {

    implementation(libs.retrofit.core)
    implementation(project(":data:data-remote"))
    implementation(project(":data:data-local"))
    implementation(project(":domain"))
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.turbine)
}