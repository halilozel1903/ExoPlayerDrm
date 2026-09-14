plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.halil.ozel.exoplayerdrm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.halil.ozel.exoplayerdrm"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        fun quotedProperty(name: String): String {
            val value = providers.gradleProperty(name).orNull.orEmpty()
            val escaped = value.replace("\\", "\\\\").replace("\"", "\\\"")
            return "\"$escaped\""
        }

        // Optional overrides. Empty values fall back to Google Media3 demo test URLs
        // (Widevine) or disable playback (ClearKey — no keys are bundled).
        buildConfigField("String", "WIDEVINE_MANIFEST_URI", quotedProperty("drm.widevine.manifestUri"))
        buildConfigField("String", "WIDEVINE_LICENSE_URI", quotedProperty("drm.widevine.licenseUri"))
        buildConfigField("String", "CLEARKEY_MANIFEST_URI", quotedProperty("drm.clearkey.manifestUri"))
        buildConfigField("String", "CLEARKEY_LICENSE_URI", quotedProperty("drm.clearkey.licenseUri"))
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

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.ui)
    testImplementation(libs.junit)
}
