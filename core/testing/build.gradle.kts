plugins {
  alias(libs.plugins.pfranccino.android.library)
  alias(libs.plugins.pfranccino.hilt)
}

android {
  namespace = "dev.pfranccino.portfolio.core.testing"
}

dependencies {
  api(libs.hilt.android.testing)
  api(libs.androidx.test.runner)
  api(libs.androidx.activity.compose)
}
