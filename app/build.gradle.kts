plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.moneytracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moneytracker"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.10"
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
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xjvm-default=all"
        )
        languageVersion = "1.9" // Explicitly set to 1.9 for KAPT compatibility
    }
    
    // KAPT configuration
    kapt {
        correctErrorTypes = true
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

    // Core Android
    implementation(libs.findLibrary("androidx-core-ktx").get())
    implementation(libs.findLibrary("appcompat").get())
    implementation(libs.findLibrary("material").get())
    implementation(libs.findLibrary("constraintlayout").get())
    
    // Compose
    implementation(platform(libs.findLibrary("androidx-compose-bom").get()))
    implementation(libs.findLibrary("androidx-ui").get())
    implementation(libs.findLibrary("androidx-ui-graphics").get())
    implementation(libs.findLibrary("androidx-ui-tooling-preview").get())
    implementation(libs.findLibrary("androidx-material3").get())
    implementation(libs.findLibrary("androidx-activity-compose").get())
    implementation(libs.findLibrary("androidx-navigation-compose").get())
    implementation(libs.findLibrary("androidx-material-icons-extended").get())
    
    // Hilt
    implementation(libs.findLibrary("hilt-android").get())
    kapt(libs.findLibrary("hilt-compiler").get())
    implementation(libs.findLibrary("hilt-navigation-compose").get())
    
    // Room
    implementation(libs.findLibrary("room-runtime").get())
    kapt(libs.findLibrary("room-compiler").get())
    implementation(libs.findLibrary("room-ktx").get())
    
    // Lifecycle
    implementation(libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
    implementation(libs.findLibrary("lifecycle-viewmodel-ktx").get())
    implementation(libs.findLibrary("lifecycle-livedata-ktx").get())
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Network for AI integration
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20231013")
    
    // Testing
    testImplementation(libs.findLibrary("junit").get())
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform(libs.findLibrary("compose-bom").get()))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // Room testing
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
}