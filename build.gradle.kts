plugins {
  java
  jacoco
  id("com.diffplug.spotless") version "6.22.0"
}

// ================= Defining the group and version of the project =================
group = "com.cdg.pmg.ngp.me.vehiclecomm"

version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_17

// ================= Definition of repositories =================
repositories {
  mavenCentral()
  mavenLocal()
}

// ================= Inclusion of sub modules =================
dependencies {
  implementation(project(":application"))
  implementation(project(":domain"))
  implementation(project(":techframework"))
}

// ================= Application of plugins to all projects =================
allprojects {
  apply(plugin = "com.diffplug.spotless")
  spotless {
    kotlinGradle {
      target("**/*.gradle.kts")
      ktfmt()
      lineEndings = com.diffplug.spotless.LineEnding.UNIX
    }
    java {
      targetExclude("**/generated/**/*.java")
      removeUnusedImports()
      importOrder()
      googleJavaFormat()
      endWithNewline()
      lineEndings = com.diffplug.spotless.LineEnding.UNIX
    }
    format("shell") {
      target("**/*.sh")
      lineEndings = com.diffplug.spotless.LineEnding.UNIX
    }
  }
  tasks.build { dependsOn("spotlessApply") }
}

// ================= Jacoco Settings ================
jacoco { toolVersion = "0.8.8" }

// ================= Definition of showDirs Task =================
// When the "showDirs" task is executed, it will log the relative paths of the project's root
// directory
// to the base directory for project reports and the directory for test results.
// This custom task is useful for quickly checking the relative paths of specific directories within
// the
// project without having to navigate through the file system manually.
tasks.register("showDirs") {
  val rootDir = project.rootDir
  val reportsDir = project.reporting.baseDirectory
  val testResultsDir = project.java.testResultsDir

  doLast {
    logger.quiet(rootDir.toPath().relativize(reportsDir.get().asFile.toPath()).toString())
    logger.quiet(rootDir.toPath().relativize(testResultsDir.get().asFile.toPath()).toString())
  }
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "jacoco")
  apply(plugin = "com.diffplug.spotless")
}
