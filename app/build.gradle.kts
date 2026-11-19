plugins {
  alias(libs.plugins.pfranccino.android.application)
  alias(libs.plugins.pfranccino.compose)
  alias(libs.plugins.pfranccino.material)
  alias(libs.plugins.pfranccino.tests)
  alias(libs.plugins.pfranccino.hilt)
}

android {
  namespace = "dev.pfranccino.portfolio"
  defaultConfig {
    applicationId = "dev.pfranccino.portfolio"
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  androidResources {
    generateLocaleConfig = true
  }
  buildFeatures {
    aidl = false
    buildConfig = false
    shaders = false
  }
}

dependencies {
  implementation(project(":core:designsystem"))
  implementation(project(":core:common"))
  implementation(project(":feature:home"))
  implementation(project(":feature:settings"))
  implementation(project(":feature:projects"))
  implementation(project(":feature:articles"))
  implementation(project(":feature:series"))

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Navigation
  implementation(libs.androidx.navigation.compose)
}
