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

        fun quotedLiteral(value: String): String {
            val escaped = value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
            return "\"$escaped\""
        }

        fun quotedProperty(name: String): String {
            return quotedLiteral(providers.gradleProperty(name).orNull.orEmpty())
        }

        fun quotedClearKeyKeys(): String {
            val keysFile = providers.gradleProperty("drm.clearkey.keysFile").orNull.orEmpty()
            val fromFile = if (keysFile.isNotBlank()) {
                val file = rootProject.file(keysFile)
                if (file.isFile) file.readText() else ""
            } else {
                ""
            }
            val fromProperty = providers.gradleProperty("drm.clearkey.keysJson").orNull.orEmpty()
            return quotedLiteral(fromFile.ifBlank { fromProperty })
        }

        // Optional overrides. Empty Widevine values keep the in-app Google test radios
        // on the Media3 demo UAT proxy. Custom Widevine / ClearKey stay disabled until set.
        buildConfigField("String", "WIDEVINE_MANIFEST_URI", quotedProperty("drm.widevine.manifestUri"))
        buildConfigField("String", "WIDEVINE_LICENSE_URI", quotedProperty("drm.widevine.licenseUri"))
        buildConfigField(
            "String",
            "WIDEVINE_LICENSE_REQUEST_HEADERS",
            quotedProperty("drm.widevine.licenseRequestHeaders")
        )
        buildConfigField("String", "CLEARKEY_MANIFEST_URI", quotedProperty("drm.clearkey.manifestUri"))
        buildConfigField("String", "CLEARKEY_LICENSE_URI", quotedProperty("drm.clearkey.licenseUri"))
        buildConfigField("String", "CLEARKEY_KEYS_JSON", quotedClearKeyKeys())
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
