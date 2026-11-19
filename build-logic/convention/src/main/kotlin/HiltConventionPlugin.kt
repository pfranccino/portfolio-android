import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("com.google.devtools.ksp")
    pluginManager.apply("com.google.dagger.hilt.android")

    val catalog = libs
    dependencies {
      add("implementation", catalog.library("hilt-android"))
      add("ksp", catalog.library("hilt-compiler"))
    }
  }
}
