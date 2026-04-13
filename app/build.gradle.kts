import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")

    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val envFile = project.rootProject.file(".env")
if (!envFile.exists()) {
    throw GradleException(".env file not found at project root")
}
val envProps = Properties().apply { load(FileInputStream(envFile)) }
val supabaseUrl: String = envProps.getProperty("SUPABASE_URL")?.takeIf { it.isNotBlank() }
    ?: throw GradleException("SUPABASE_URL is missing or blank in .env")
val supabaseKey: String = envProps.getProperty("SUPABASE_KEY")?.takeIf { it.isNotBlank() }
    ?: throw GradleException("SUPABASE_KEY is missing or blank in .env")

val keystoreFile = rootProject.file("vital-release-key-new.jks")
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) load(FileInputStream(keystorePropsFile))
}
fun keystoreValue(key: String, env: String): String? =
    keystoreProps.getProperty(key)?.takeIf { it.isNotBlank() }
        ?: System.getenv(env)?.takeIf { it.isNotBlank() }

android {
    namespace = "com.vital.health"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vital.health"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "2.4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val storePass = keystoreValue("storePassword", "VITAL_KEYSTORE_PASSWORD")
        val alias = keystoreValue("keyAlias", "VITAL_KEY_ALIAS")
        val keyPass = keystoreValue("keyPassword", "VITAL_KEY_PASSWORD")
        if (keystoreFile.exists() && storePass != null && alias != null && keyPass != null) {
            create("release") {
                storeFile = keystoreFile
                storePassword = storePass
                keyAlias = alias
                keyPassword = keyPass
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfigs.findByName("release")?.let { signingConfig = it }
        }
        configureEach {
            buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
            buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // Supabase & Ktor
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.0.0")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.0.0")
    implementation("io.github.jan-tennert.supabase:storage-kt:3.0.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    val ktorVersion = "3.0.0"
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Room
    val roomVersion = "2.8.4"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.59.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")

    // UI
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.5")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
