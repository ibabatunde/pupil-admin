import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}
hilt {
    enableAggregatingTask = false
}

android {
    namespace = "com.okediran.administrator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.okediran.administrator"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val secretProperties = Properties()
        val secretFile = rootProject.file("secrets.properties")

        if (secretFile.exists()) {
            secretFile.inputStream().use { input ->
                secretProperties.load(input)
            }

            val apiBaseUrl = secretProperties.getProperty("BASEURL") ?: "https://google-api.com/"
            buildConfigField("String", "BASE_URL", "\"${apiBaseUrl}\"")

            val xRequestId = secretProperties.getProperty("X_REQUEST_ID") ?: "1234567890"
            buildConfigField("String", "X_REQUEST_ID", "\"${xRequestId}\"")

            val userAgent = secretProperties.getProperty("USER_AGENT") ?: "1234567890"
            buildConfigField("String", "USER_AGENT", "\"${userAgent}\"")

            val otherProperty = secretProperties.getProperty("OTHER_PROPERTY") ?: "default value"
            buildConfigField("String", "OTHER_PROPERTY", "\"${otherProperty}\"")

        } else {
            println("WARNING: staging.properties file not found!")
            val defaultApiBaseUrl = "https://default-api.com/"
            buildConfigField("String", "API_BASE_URL", "\"${defaultApiBaseUrl}\"")

            val defaultOtherProperty = "default value"
            buildConfigField("String", "OTHER_PROPERTY", "\"${defaultOtherProperty}\"")
        }
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

        // Enable core library desugaring
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.retrofit2)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinpoet)
    implementation(libs.gson)

    // Core library desugaring dependency
    coreLibraryDesugaring(libs.android.desugaring)
}
