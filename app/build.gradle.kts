plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.chaquo.python")
}

android {
    namespace = "com.example.brelax"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.brelax"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }



    flavorDimensions += "pyVersion"
    productFlavors {
        create("py308") { dimension = "pyVersion" }

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
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.mediation.test.suite)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.runtime.livedata)
    val koinVersion = "3.5.0"
    implementation(libs.koin.android) // dependency injection
    implementation(libs.koin.androidx.compose) // DI for compose
    implementation(libs.androidx.constraintlayout.compose) // layout
    implementation(libs.accompanist.permissions) // get permission
    //implementation(libs.compose.m3)
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("uk.me.berndporr:iirj:1.7")

    //implementation( "uk.me.berndporr.iirj" )
}



chaquopy {
    defaultConfig {
        buildPython("C:/Users/USER/AppData/Local/Programs/Python/Python308/python.exe")


        pip{
            install("pandas")
            install("numpy")
            install("joblib" )
            install("scikit-learn" )
        }

    }
    productFlavors {
        getByName("py308") { version = "3.8" }

    }
    sourceSets { }


}