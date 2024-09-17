plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.smile.sniffer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.smile.sniffer"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core libraries
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.2")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))



    // Compose libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation ("androidx.core:core-ktx:1.13.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Additional Compose dependencies
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.35.1-alpha")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.35.1-alpha")
    implementation("com.google.accompanist:accompanist-permissions:0.35.1-alpha")
    implementation("androidx.paging:paging-compose:3.3.2")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.runtime:runtime-rxjava2")


    //zxing library
    implementation ("com.google.zxing:core:3.5.2")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-firestore:25.1.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.0")


    //camera
    implementation ("androidx.camera:camera-lifecycle:1.3.4")
    implementation ("androidx.camera:camera-view:1.3.4")
    implementation("androidx.camera:camera-core:1.3.4")
    implementation ("androidx.camera:camera-camera2:1.3.4")

    // refresh
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.35.1-alpha")
    implementation ("androidx.compose.foundation:foundation:1.7.0")
    implementation ("com.airbnb.android:lottie-compose:6.0.0")



    //guava
    implementation ("com.google.guava:guava:33.0.0-android")
    implementation("androidx.wear.compose:compose-material:1.4.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


    // Debugging
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Retrofit core library
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")

    // Converter for JSON (Gson)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //firebase
    implementation ("com.google.firebase:firebase-storage-ktx:21.0.0")
    // Firebase Firestore
    implementation ("com.google.firebase:firebase-firestore-ktx:25.1.0")
    // Coil for image loading
    implementation ("io.coil-kt:coil-compose:2.7.0")

       //shimmer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("me.vponomarenko:compose-shimmer:1.0.0")


}
