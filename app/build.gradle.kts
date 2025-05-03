plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.aronid.weighttrackertft"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aronid.weighttrackertft"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {

    implementation(libs.itext)
    implementation(libs.open.csv)

    // Firebase dependencies
    implementation(platform(libs.firebase.bom)) // Firebase Bill of Materials (BoM) to manage Firebase dependencies versions
    implementation(libs.firebase.crashlytics) // Firebase Crashlytics for crash reporting
    implementation(libs.firebase.analytics) // Firebase Analytics for tracking user behavior
    implementation(libs.firebase.auth) // Firebase Authentication for user authentication
    implementation(libs.firebase.firestore) // Firebase Firestore for cloud database

    implementation(libs.ycharts.compose)

    implementation(libs.calendar.compose)

    implementation(libs.konfetti)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    implementation(libs.okhttp)

    implementation(libs.androidx.compose.animation)

// Jetpack Compose dependencies
    implementation(libs.compose.material.icons.extended) // Extended Material Icons for Jetpack Compose

// LiveData dependencies
    implementation(libs.runtime.livedata) // LiveData for lifecycle-aware data observation

// Hilt dependencies
    implementation(libs.hilt.android) // Hilt for dependency injection
    kapt(libs.hilt.compiler) // Hilt compiler for generating code
    implementation(libs.hilt.navigation.compose) // Hilt navigation integration for Jetpack Compose

// Paging dependencies
//    implementation(libs.pagingCompose) // Paging library for loading data in pages in Jetpack Compose
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

// Coil dependencies
    implementation(libs.coil.compose) // Coil for image loading in Jetpack Compose

// Date Picker dependencies
    implementation(libs.easy.date.picker) // Easy Date Picker for date selection

// ViewModel dependencies
    implementation(libs.lifecycle.viewmodel.compose) // ViewModel integration for Jetpack Compose

// Constraint Layout dependencies
    implementation(libs.androidx.constraint.layout.compose) // Constraint Layout for Jetpack Compose

// Navigation dependencies
    implementation(libs.androidx.navigation.compose) // Navigation component for Jetpack Compose

// Core KTX dependencies
    implementation(libs.androidx.core.ktx) // Core KTX extensions for Android

// Lifecycle Runtime dependencies
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle runtime extensions for Android

// Activity Compose dependencies
    implementation(libs.androidx.activity.compose) // Activity integration for Jetpack Compose

// Compose BOM dependencies
    implementation(platform(libs.androidx.compose.bom)) // Compose Bill of Materials (BoM) to manage Compose dependencies versions

// Compose UI dependencies
    implementation(libs.androidx.ui) // Core UI components for Jetpack Compose
    implementation(libs.androidx.ui.graphics) // Graphics components for Jetpack Compose
    implementation(libs.androidx.ui.tooling.preview) // Tooling support for Jetpack Compose previews

// Material3 dependencies
    implementation(libs.androidx.material3) // Material Design 3 components for Jetpack Compose

// Runtime LiveData dependencies
    implementation(libs.androidx.runtime.livedata) // Runtime LiveData integration for Jetpack Compose

// Testing dependencies
    testImplementation(libs.junit) // JUnit for unit testing
    androidTestImplementation(libs.androidx.junit) // AndroidX JUnit extensions for Android testing
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose Bill of Materials (BoM) for Android testing
    androidTestImplementation(libs.androidx.ui.test.junit4) // JUnit4 integration for Jetpack Compose testing

// Debug dependencies
    debugImplementation(libs.androidx.ui.tooling) // Tooling support for Jetpack Compose debugging
    debugImplementation(libs.androidx.ui.test.manifest) // Manifest support for Jetpack Compose testing
}