plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.legacy.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.fayyad.siskamlingapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.fayyad.siskamlingapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // HILT
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // FIREBASE
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)

    // RETROFIT (API Gambar)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)

    // GLIDE (Menampilkan Gambar)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // SWIPE TO REFRESH (Tarik ke Bawah untuk Update)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}