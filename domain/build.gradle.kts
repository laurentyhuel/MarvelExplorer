plugins {
    id("marvelexplorer.android.library")
}

android {
    namespace = "com.lyh.marvelexplorer.domain"
}

dependencies {
    implementation(libs.androidx.paging.compose)
    testImplementation(libs.turbine)
}