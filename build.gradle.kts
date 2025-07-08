import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.1.0"
  id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "com.hexagram2021"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    create("IC", "2025.1")
    bundledPlugin("com.intellij.java")
    testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
  }
}

intellijPlatform {
  pluginConfiguration {
    ideaVersion {
      sinceBuild = "251"
    }

    changeNotes = """
      Initial version
    """.trimIndent()
  }
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "8"
    targetCompatibility = "8"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
  }
}
