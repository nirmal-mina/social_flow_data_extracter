plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.pipe.social_flow_data_extracter"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        // TODO: Specify your own unique Application ID (https://developer.android.com/studio/build/application-id.html).
        applicationId = "com.pipe.social_flow_data_extracter"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    buildTypes {
        release {
            // TODO: Add your own signing config for the release build.
            // Signing with the debug keys for now, so `flutter run --release` works.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}
dependencies {
        // Comment this if you're building Release, this is for Android Studio indexing
        // api("io.flutter:flutter_embedding_debug:1.0.0-d4453f601890ec682bbf8f5659b70f15cce1d67d")
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
        implementation("androidx.annotation:annotation:1.6.0")
    implementation("com.github.TeamNewPipe:NewPipeExtractor:v0.24.8")
        implementation("org.jsoup:jsoup:1.16.1")
        implementation("org.mozilla:rhino:1.7.15")
        implementation("com.github.spotbugs:spotbugs-annotations:4.8.3")
        implementation("org.nibor.autolink:autolink:0.11.0")
        testImplementation("junit:junit:4.13.2")
        implementation("com.squareup.okhttp3:okhttp:4.11.0")
        implementation("com.google.code.gson:gson:2.10.1")
    }

flutter {
    source = "../.."
}
