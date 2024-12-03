plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.yousefwissam.dailyspark"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.yousefwissam.dailyspark"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            )
        }
    }
}

dependencies {
    implementation(libs.identity.jvm)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.core.ktx)
    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.androidx.rules)

    // Kotlin Coroutines
    val coroutinesVersion = "1.6.4"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // AndroidX Lifecycle - ViewModel and LiveData
    val lifecycleVersion = "2.5.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.6.1")

    // Test Libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

    // Firestore and Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.4.1")

    // WorkManager for scheduling tasks
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    // Unit Testing with Mockito and Robolectric
    testImplementation(libs.mockito.core)
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.mockito:mockito-inline:4.2.0")
    testImplementation("org.robolectric:robolectric:4.8")
    testImplementation("org.mockito:mockito-core:4.8.0")

    // Protobuf (to resolve compatibility issues)
    implementation("com.google.protobuf:protobuf-javalite:3.21.2") // Updated version

    // AndroidX Test dependencies for RecyclerViewActions and Activity transitions
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0") {
        exclude(group = "com.android.support", module = "appcompat")
        exclude(group = "com.android.support", module = "support-annotations")
    }

    // AndroidX Arch Core Testing (for InstantTaskExecutorRule)
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    // Kotlin Coroutines Test Library for Testing ViewModel
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    implementation ("com.google.firebase:firebase-bom:32.1.0")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-analytics")
    implementation ("com.google.protobuf:protobuf-javalite:3.21.0")


}

apply(plugin = "com.google.gms.google-services")
