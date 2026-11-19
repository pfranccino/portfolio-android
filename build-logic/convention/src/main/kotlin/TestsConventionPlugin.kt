import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class TestsConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    val catalog = libs

    pluginManager.withPlugin("com.android.application") {
      extensions.configure<ApplicationExtension> {
        defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testOptions.unitTests.all { it.useJUnitPlatform() }
      }
      addAndroidTestDependencies()
    }
    pluginManager.withPlugin("com.android.library") {
      extensions.configure<LibraryExtension> {
        defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testOptions.unitTests.all { it.useJUnitPlatform() }
      }
      addAndroidTestDependencies()
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
      tasks.withType<Test>().configureEach {
        useJUnitPlatform()
      }
    }

    dependencies {
      add("testImplementation", platform(catalog.library("junit-bom")))
      add("testImplementation", catalog.library("junit-jupiter"))
      add("testRuntimeOnly", catalog.library("junit-platform-launcher"))
      add("testImplementation", catalog.library("kotlinx-coroutines-test"))
    }
  }

  private fun Project.addAndroidTestDependencies() {
    val catalog = libs
    dependencies {
      add("androidTestImplementation", catalog.library("androidx-test-core"))
      add("androidTestImplementation", catalog.library("androidx-test-ext-junit"))
      add("androidTestImplementation", catalog.library("androidx-test-runner"))
      add("androidTestImplementation", catalog.library("androidx-test-espresso-core"))
    }
  }
}
