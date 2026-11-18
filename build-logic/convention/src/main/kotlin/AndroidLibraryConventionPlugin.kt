import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

class AndroidLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("com.android.library")

    val catalog = libs
    extensions.configure<LibraryExtension> {
      compileSdk = catalog.version("compileSdk").toInt()
      defaultConfig {
        minSdk = catalog.version("minSdk").toInt()
      }
      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
      }
    }
    extensions.configure<KotlinBaseExtension> {
      jvmToolchain(JVM_TARGET)
    }
  }
}
