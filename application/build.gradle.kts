import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  java
  jacoco
  id("io.spring.dependency-management") version "1.0.15.RELEASE"
}

group = "com.cdg.pmg.ngp.me.vehiclecomm.application"

version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_17

jacoco { toolVersion = "0.8.8" }

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation(project(":domain"))

  implementation(libs.mapstruct)
  implementation(libs.jackson.annotations)
  implementation(libs.hibernate.validator)
  implementation(libs.jackson.databind)
  implementation(libs.jakarta.validation)
  implementation(libs.apache.commons.lang)
  implementation(libs.slf4j.api)
  implementation(libs.spring.context)

  compileOnly(libs.lombok)
  annotationProcessor(libs.lombok)

  // Test implementation
  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.mockito.junit.jupiter)
  annotationProcessor(libs.lombok.mapstruct.binding)
  annotationProcessor(libs.mapstruct.processor)
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
