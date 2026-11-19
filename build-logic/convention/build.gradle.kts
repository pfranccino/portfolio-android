plugins {
  `kotlin-dsl`
}

group = "dev.pfranccino.portfolio.buildlogic"

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.compose.compiler.gradlePlugin)
  compileOnly(libs.hilt.gradlePlugin)
  compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("androidApplication") {
      id = "dev.pfranccino.android.application"
      implementationClass = "AndroidApplicationConventionPlugin"
    }
    register("androidLibrary") {
      id = "dev.pfranccino.android.library"
      implementationClass = "AndroidLibraryConventionPlugin"
    }
    register("kotlinJvm") {
      id = "dev.pfranccino.kotlin"
      implementationClass = "KotlinConventionPlugin"
    }
    register("compose") {
      id = "dev.pfranccino.compose"
      implementationClass = "ComposeConventionPlugin"
    }
    register("material") {
      id = "dev.pfranccino.material"
      implementationClass = "MaterialConventionPlugin"
    }
    register("tests") {
      id = "dev.pfranccino.tests"
      implementationClass = "TestsConventionPlugin"
    }
    register("hilt") {
      id = "dev.pfranccino.hilt"
      implementationClass = "HiltConventionPlugin"
    }
  }
}
