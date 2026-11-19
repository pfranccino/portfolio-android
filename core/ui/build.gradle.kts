plugins {
  alias(libs.plugins.pfranccino.android.library)
  alias(libs.plugins.pfranccino.compose)
  alias(libs.plugins.pfranccino.material)
  alias(libs.plugins.pfranccino.tests)
}

android {
  namespace = "dev.pfranccino.portfolio.core.ui"
}

dependencies {
  implementation(project(":core:designsystem"))
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.browser)
}
