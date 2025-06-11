pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    
    versionCatalogs {
        create("libs") {
            // Versions
            version("kotlin", "2.0.0")
            version("compose", "1.5.0")
            version("hilt", "2.48.1")
            version("room", "2.6.1")
            version("lifecycle", "2.7.0")
            version("navigation", "2.7.7")
            version("activity", "1.8.2")
            
            // AndroidX Core
            library("core-ktx", "androidx.core", "core-ktx").version("1.12.0")
            library("appcompat", "androidx.appcompat", "appcompat").version("1.6.1")
            library("material", "com.google.android.material", "material").version("1.11.0")
            
            // Compose
            val composeBom = "2023.10.01"
            library("compose-bom", "androidx.compose", "compose-bom").version(composeBom)
            library("compose-ui", "androidx.compose.ui", "ui").versionRef("compose")
            library("compose-ui-graphics", "androidx.compose.ui", "ui-graphics").versionRef("compose")
            library("compose-ui-tooling-preview", "androidx.compose.ui", "ui-tooling-preview").versionRef("compose")
            library("compose-material3", "androidx.compose.material3", "material3").version("1.2.0")
            library("activity-compose", "androidx.activity", "activity-compose").versionRef("activity")
            
            // Hilt
            library("hilt-android", "com.google.dagger", "hilt-android").versionRef("hilt")
            library("hilt-compiler", "com.google.dagger", "hilt-android-compiler").versionRef("hilt")
            library("hilt-navigation-compose", "androidx.hilt", "hilt-navigation-compose").version("1.1.0")
            
            // Room
            library("room-runtime", "androidx.room", "room-runtime").versionRef("room")
            library("room-compiler", "androidx.room", "room-compiler").versionRef("room")
            library("room-ktx", "androidx.room", "room-ktx").versionRef("room")
            
            // Lifecycle
            library("lifecycle-runtime-ktx", "androidx.lifecycle", "lifecycle-runtime-ktx").versionRef("lifecycle")
            library("lifecycle-viewmodel-ktx", "androidx.lifecycle", "lifecycle-viewmodel-ktx").versionRef("lifecycle")
            library("lifecycle-livedata-ktx", "androidx.lifecycle", "lifecycle-livedata-ktx").versionRef("lifecycle")
            
            // Navigation
            library("navigation-compose", "androidx.navigation", "navigation-compose").versionRef("navigation")
            
            // Testing
            library("junit", "junit:junit:4.13.2")
            library("androidx-junit", "androidx.test.ext:junit:1.1.5")
            library("espresso-core", "androidx.test.espresso:espresso-core:3.5.1")
        }
    }
}

rootProject.name = "MoneyTracker"
include(":app")
