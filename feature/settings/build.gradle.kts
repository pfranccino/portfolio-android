plugins {
  alias(libs.plugins.pfranccino.android.library)
  alias(libs.plugins.pfranccino.compose)
  alias(libs.plugins.pfranccino.material)
  alias(libs.plugins.pfranccino.tests)
  alias(libs.plugins.pfranccino.hilt)
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "dev.pfranccino.portfolio.feature.settings"
  defaultConfig {
    testInstrumentationRunner = "dev.pfranccino.portfolio.core.testing.HiltTestRunner"
  }
}

dependencies {
  implementation(project(":core:common"))
  implementation(project(":core:ui"))
  implementation(project(":core:designsystem"))
  implementation(libs.androidx.navigation.compose)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
  implementation(libs.androidx.datastore.preferences)

  androidTestImplementation(libs.androidx.navigation.testing)
  androidTestImplementation(project(":core:testing"))
  kspAndroidTest(libs.hilt.compiler)
}
