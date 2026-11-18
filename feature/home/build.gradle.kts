plugins {
  alias(libs.plugins.pfranccino.android.library)
  alias(libs.plugins.pfranccino.compose)
  alias(libs.plugins.pfranccino.material)
  alias(libs.plugins.pfranccino.tests)
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "dev.pfranccino.portfolio.feature.home"
}

dependencies {
  implementation(project(":core:designsystem"))
  implementation(libs.androidx.navigation.compose)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.androidx.lifecycle.runtime.compose)

  androidTestImplementation(libs.androidx.navigation.testing)
}
