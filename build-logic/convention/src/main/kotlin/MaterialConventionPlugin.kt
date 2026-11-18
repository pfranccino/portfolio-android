import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class MaterialConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    val catalog = libs
    dependencies {
      add("implementation", catalog.library("androidx-compose-material3"))
    }
  }
}
