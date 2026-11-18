import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("com.android.application")

    val catalog = libs
    extensions.configure<ApplicationExtension> {
      compileSdk = catalog.version("compileSdk").toInt()
      defaultConfig {
        minSdk = catalog.version("minSdk").toInt()
        targetSdk = catalog.version("targetSdk").toInt()
      }
      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
      }
      packaging {
        resources {
          excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
      }
    }
    extensions.configure<KotlinBaseExtension> {
      jvmToolchain(JVM_TARGET)
    }
  }
}
