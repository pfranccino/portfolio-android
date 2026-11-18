import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    pluginManager.withPlugin("com.android.application") {
      extensions.configure<ApplicationExtension> {
        buildFeatures.compose = true
      }
    }
    pluginManager.withPlugin("com.android.library") {
      extensions.configure<LibraryExtension> {
        buildFeatures.compose = true
      }
    }

    val catalog = libs
    dependencies {
      val bom = platform(catalog.library("androidx-compose-bom"))
      add("implementation", bom)
      add("androidTestImplementation", bom)
      add("implementation", catalog.library("androidx-compose-ui"))
      add("implementation", catalog.library("androidx-compose-ui-tooling-preview"))
      add("debugImplementation", catalog.library("androidx-compose-ui-tooling"))
      add("debugImplementation", catalog.library("androidx-compose-ui-test-manifest"))
      add("androidTestImplementation", catalog.library("androidx-compose-ui-test-junit4"))
    }
  }
}
