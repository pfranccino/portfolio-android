plugins {
  alias(libs.plugins.pfranccino.android.library)
  alias(libs.plugins.pfranccino.compose)
  alias(libs.plugins.pfranccino.material)
  alias(libs.plugins.pfranccino.tests)
}

android {
  namespace = "dev.pfranccino.portfolio.core.designsystem"
}
