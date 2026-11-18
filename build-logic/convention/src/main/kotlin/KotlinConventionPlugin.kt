import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

class KotlinConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.kotlin.jvm")

    extensions.configure<KotlinBaseExtension> {
      jvmToolchain(JVM_TARGET)
    }
    tasks.withType<Test>().configureEach {
      useJUnitPlatform()
    }
  }
}
