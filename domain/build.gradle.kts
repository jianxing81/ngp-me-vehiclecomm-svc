import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  java
  jacoco
  id("io.spring.dependency-management") version "1.0.15.RELEASE"
}

group = "com.cdg.pmg.ngp.me.vehiclecomm.domain"

version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_17

// ================= Repositories ================
repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  compileOnly(libs.lombok)
  annotationProcessor(libs.lombok)

  implementation(libs.jakarta.validation)
  implementation(libs.jackson.annotations)
  implementation(libs.spring.context)
  implementation(libs.mapstruct)
  annotationProcessor(libs.lombok.mapstruct.binding)
  annotationProcessor(libs.mapstruct.processor)

  // Test implementation
  testImplementation(platform(rootProject.libs.junit.bom))
  testImplementation(rootProject.libs.junit.jupiter)
  testImplementation(rootProject.libs.mockito.junit.jupiter)
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events(
        TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.STANDARD_ERROR, TestLogEvent.SKIPPED)
    exceptionFormat = TestExceptionFormat.FULL
    showExceptions = true
    showCauses = true
    showStackTraces = true
  }
  reports {
    html.required.set(true)
    junitXml.apply {
      // isOutputPerTestCase = true // defaults to false
      mergeReruns.set(true) // defaults to false
    }
  }
  outputs.upToDateWhen { false }
  addTestListener(
      object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}

        override fun beforeTest(testDescriptor: TestDescriptor) {}

        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}

        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
          if (suite.parent == null) {
            println("----")
            println("Suite name: ${suite.name}")
            println("Test result: ${result.resultType}")
            println(
                "Test summary: ${result.testCount} tests, " +
                    "${result.successfulTestCount} succeeded, " +
                    "${result.failedTestCount} failed, " +
                    "${result.skippedTestCount} skipped")
          }
        }
      })
}

tasks.jacocoTestReport {
  dependsOn(tasks.test) // tests are required to run before generating the report
  outputs.upToDateWhen { false }

  reports {
    xml.required.set(true)
    html.required.set(true)
    csv.required.set(false)
    xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/unit/testReport.xml"))
    html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/unit/html"))
  }
  sourceDirectories.setFrom(files(sourceSets["main"].allSource.srcDirs))
  classDirectories.setFrom(files(sourceSets["main"].output.classesDirs))
}

tasks.test { finalizedBy(tasks.jacocoTestReport) }
