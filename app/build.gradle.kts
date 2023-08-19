plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.cloudcup"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.cloudcup"
        minSdk = 22
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        create("customDebugType"){
            isDebuggable = true;
            }
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

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.firebase:firebase-database:20.2.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


  //  implementation("com.google.android.gms:play-services-plus:17.0.0")
    implementation("com.firebase:firebase-client-android:2.5.2")
     implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
     implementation("com.google.firebase:firebase-analytics")
     implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth:22.1.1")
    implementation("com.google.android.gms:play-services-auth:20.6.0")
  //  implementation("com.google.android.gms:play-services-games-v2:17.0.0")
}




