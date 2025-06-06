plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.pmessanger"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pmessanger"
        minSdk = 23
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.sdp.android)
    implementation(libs.firebase.auth)
    implementation(libs.circleimageview)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.ccp)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ui.firestore)
    implementation(libs.imagepicker)
    implementation(libs.glide)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}