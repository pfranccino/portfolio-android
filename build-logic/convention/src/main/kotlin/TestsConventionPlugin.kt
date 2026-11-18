import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class TestsConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.withPlugin("com.android.application") {
      extensions.configure<ApplicationExtension> {
        defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testOptions.unitTests.all { it.useJUnitPlatform() }
      }
    }
    pluginManager.withPlugin("com.android.library") {
      extensions.configure<LibraryExtension> {
        defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testOptions.unitTests.all { it.useJUnitPlatform() }
      }
    }

    val catalog = libs
    dependencies {
      add("testImplementation", platform(catalog.library("junit-bom")))
      add("testImplementation", catalog.library("junit-jupiter"))
      add("testRuntimeOnly", catalog.library("junit-platform-launcher"))
      add("testImplementation", catalog.library("kotlinx-coroutines-test"))

      add("androidTestImplementation", catalog.library("androidx-test-core"))
      add("androidTestImplementation", catalog.library("androidx-test-ext-junit"))
      add("androidTestImplementation", catalog.library("androidx-test-runner"))
      add("androidTestImplementation", catalog.library("androidx-test-espresso-core"))
    }
  }
}
