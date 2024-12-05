import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
  java
  jacoco
  id("org.sonarqube") version "5.0.0.4638"
  id("com.google.cloud.artifactregistry.gradle-plugin") version "2.1.5"
  id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
  id("org.openapi.generator") version "7.4.0"
  id("org.springframework.boot") version "3.2.2"
  id("io.spring.dependency-management") version "1.0.15.RELEASE"
  id("jacoco-report-aggregation")
  id("com.gorylenko.gradle-git-properties") version "2.4.1"
  id("org.jsonschema2pojo") version "1.2.1"
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("org.liquibase.gradle") version "2.2.0"
}

ext { set("springCloudVersion", "2023.0.0") }

group = "com.cdg.pmg.ngp.me.vehiclecomm.techframework"

version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_17

jacoco { toolVersion = "0.8.8" }

repositories {
  mavenCentral()
  mavenLocal()
  maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
  apply(plugin = "java")

  implementation(project(":application"))
  implementation(project(":domain"))

  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.configuration.processor)

  // lombok compile and processor
  compileOnly(libs.lombok)
  annotationProcessor(libs.lombok)
  implementation(libs.mapstruct)

  annotationProcessor(libs.lombok.mapstruct.binding)
  annotationProcessor(libs.mapstruct.processor)

  implementation(libs.jackson.datatype)

  implementation(libs.dd.trace.api)
  implementation(libs.git.commit.id.maven.plugin)

  // AWS Parameter Store & Secrets Manager for externalized configuration
  implementation(libs.spring.cloud.aws.starter)
  implementation(libs.spring.cloud.aws.starter.parameter.store)
  implementation(libs.spring.cloud.aws.starter.secrets.manager)

  // Actuator, metric, logging and tracing
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.micrometer.core)
  implementation(libs.micrometer.registry.prometheus)
  implementation(libs.micrometer.tracing.bridge.otel)

  implementation(libs.logback.json.classic)
  implementation(libs.logback.jackson)

  implementation(libs.git.commit.id.maven.plugin)

  // Runtime library
  implementation(libs.therapi.runtime.javadoc)
  annotationProcessor(libs.therapi.runtime.javadoc)

  // Test implementation
  testImplementation(libs.spring.boot.starter.test)

  // messaging
  implementation(libs.spring.boot.starter.aop)
  implementation(libs.spring.kafka)
  implementation(libs.io.confluent.kafka.json.schema.serializer)
  implementation(libs.io.confluent.kafka.schema.registry.client)

  // Rest
  implementation(libs.spring.boot.starter.web)

  // Validation -- need to explicitly add this in for openapi to generate annotation correctly
  implementation(libs.jakarta.validation)

  // OpenAPI
  implementation(libs.springdoc.openapi.starter.webmvc.ui)
  implementation(libs.logbook.spring.boot.starter)
  implementation(libs.spring.boot.web.flux)
  implementation(libs.spring.data.commons)
  implementation(libs.swagger.annotation.jakarta)

  implementation(libs.spring.boot.starter.data.redis)

  // Config Server/client dependencies
  implementation(libs.spring.cloud.starter.config)
  implementation(libs.spring.cloud.aws)
  implementation(libs.spring.cloud.bus)

  // Database dependencies
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.postgresql)
  implementation(libs.liquibase)

  // Test Implementation
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.testcontainers)
  testImplementation(libs.testcontainers.junit.jupiter)
  testImplementation(libs.testcontainers.kafka)
  testImplementation(libs.testcontainers.redis)
  testImplementation(libs.testcontainers.postgresql)
  testImplementation(libs.retrofit)
  testImplementation(libs.retrofit.jackson)
  testImplementation(libs.awaitility)

  liquibaseRuntime("org.liquibase:liquibase-core:4.16.1")
  liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.2")
  liquibaseRuntime("info.picocli:picocli:4.6.1")
  liquibaseRuntime("org.postgresql:postgresql")
}

apply(plugin = "org.liquibase.gradle")

liquibase {
  activities.register("main") {
    this.arguments =
        mapOf(
            "searchPath" to "${project.rootDir}/techframework/src/main/resources/",
            "logLevel" to "debug",
            "changelogFile" to "liquibase/changelog/db.changelog-master.xml",
            "url" to project.property("url"),
            "username" to project.property("username"),
            "password" to project.property("password"))
  }
}

// ============================ GitProperties Settings ===============================
configure<com.gorylenko.GitPropertiesPluginExtension> {
  failOnNoGitDirectory = false
  (dotGitDirectory as DirectoryProperty).set(file("${project.rootDir}/.git"))
  gitPropertiesName = "git.properties"
  extProperty = "gitProps"
  (gitPropertiesResourceDir as DirectoryProperty).set(
      file("${project.layout.buildDirectory.get()}/git.properties"))
}

tasks.generateGitProperties { outputs.upToDateWhen { false } }

// ================= Resources Settings ================
tasks.processResources {
  mustRunAfter(tasks.generateGitProperties)
  dependsOn(tasks.generateGitProperties)
  outputs.upToDateWhen { false }
}

// ================= Jar Settings ================
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
  archiveClassifier.set("") // Empty to replace the regular jar
  manifest {
    attributes(
        "Main-Class" to
            "com.cdg.pmg.ngp.me.vehiclecomm.techframework.TechFrameworkApplication") // Specify your
    // main class
    // here
  }
}

// ============================ Kafka Schema Registry Settings ============================
jsonSchema2Pojo {
  targetDirectory = file("${project.layout.buildDirectory.get()}/generated/jsontopojo")
  targetPackage = "jsontopojo"
  generateBuilders = true
  useInnerClassBuilders = true
  includeAdditionalProperties = false
  setSource(files("$projectDir/src/main/resources/json"))
  setAnnotationStyle("none")
  includeToString = true
  includeHashcodeAndEquals = false
  useJodaLocalTimes = false
  dateTimeType = "java.time.LocalDateTime"
  dateType = "java.time.LocalDate"
  customDateTimePattern = "yyyy-MM-dd HH:mm"
  customDatePattern = "yyyy-MM-dd"
}

// ============================== OpenAPI Settings =========================================
// tasks to generated REST interfaces based on OpenAPI yaml specification
tasks.openApiGenerate {
  generatorName.set("spring")
  inputSpec.set("$projectDir/src/main/resources/openapi/vehiclecomm-service.yaml")
  outputDir.set("${project.layout.buildDirectory.get()}/generated/openapi")
  apiPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.apis")
  modelPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models")
  configOptions.set(
      mapOf(
          "generatorName" to "java",
          "dateLibrary" to "java8",
          "generateApis" to "true",
          "generateApiTests" to "false",
          "generateModels" to "true",
          "generateModelTests" to "false",
          "generateModelDocumentation" to "false",
          "generateSupportingFiles" to "false",
          "hideGenerationTimestamp" to "true",
          "interfaceOnly" to "true",
          "library" to "spring-boot",
          "serializableModel" to "true",
          "useBeanValidation" to "true",
          "useTags" to "true",
          "implicitHeaders" to "true",
          "openApiNullable" to "false",
          "oas3" to "true",
          "ignoreUnknownProperties" to "true",
          "useSpringBoot3" to "true"))
  typeMappings.set(
      mapOf("LocalDate" to "java.time.Instant", "OffsetDateTime" to "java.time.Instant"))
}

tasks.register("generateJobDispatchApi", GenerateTask::class) {
  group = "openapi"
  description = "Generates the classes required to call ME Job Dispatch Service"
  generatorName.set("java")
  inputSpec.set("$projectDir/src/main/resources/openapi/clients/job-dispatch.yaml")
  outputDir.set("${project.layout.buildDirectory.get()}/generated/openapi-job-dispatch-service")
  packageName.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client")
  apiPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis")
  modelPackage.set(
      "com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.models")
  configOptions.set(
      mapOf(
          "dateLibrary" to "java8-localdatetime",
          "generateApis" to "true",
          "generateApiTests" to "false",
          "generateModels" to "true",
          "generateModelTests" to "false",
          "generateModelDocumentation" to "false",
          "generateSupportingFiles" to "false",
          "hideGenerationTimestamp" to "true",
          "interfaceOnly" to "true",
          "library" to "webclient",
          "serializableModel" to "true",
          "useBeanValidation" to "false",
          "useTags" to "true",
          "implicitHeaders" to "false",
          "openApiNullable" to "false",
          "oas3" to "true",
          "useSpringBoot3" to "true"))
}

tasks.register("generateFleetAnalyticApi", GenerateTask::class) {
  group = "openapi"
  description = "Generates the classes required to call ME Fleet Analytic Service"
  generatorName.set("java")
  inputSpec.set("$projectDir/src/main/resources/openapi/clients/fleet-analytic.yaml")
  outputDir.set("${project.layout.buildDirectory.get()}/generated/openapi-fleet-analytic-service")
  packageName.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client")
  apiPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.apis")
  modelPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.models")
  configOptions.set(
      mapOf(
          "dateLibrary" to "java8-localdatetime",
          "generateApis" to "true",
          "generateApiTests" to "false",
          "generateModels" to "true",
          "generateModelTests" to "false",
          "generateModelDocumentation" to "false",
          "generateSupportingFiles" to "false",
          "hideGenerationTimestamp" to "true",
          "interfaceOnly" to "true",
          "library" to "webclient",
          "serializableModel" to "true",
          "useBeanValidation" to "false",
          "useTags" to "true",
          "implicitHeaders" to "false",
          "openApiNullable" to "false",
          "oas3" to "true",
          "useSpringBoot3" to "true"))
}

tasks.register("generateFareApi", GenerateTask::class) {
  group = "openapi"
  description = "Generates the classes required to call ME Fare Service"
  generatorName.set("java")
  inputSpec.set("$projectDir/src/main/resources/openapi/clients/fare-service.yaml")
  outputDir.set("${project.layout.buildDirectory.get()}/generated/openapi-fare-service")
  packageName.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client")
  apiPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.apis")
  modelPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.models")
  configOptions.set(
      mapOf(
          "dateLibrary" to "java8-localdatetime",
          "generateApis" to "true",
          "generateApiTests" to "false",
          "generateModels" to "true",
          "generateModelTests" to "false",
          "generateModelDocumentation" to "false",
          "generateSupportingFiles" to "false",
          "hideGenerationTimestamp" to "true",
          "interfaceOnly" to "true",
          "library" to "webclient",
          "serializableModel" to "true",
          "useBeanValidation" to "false",
          "useTags" to "true",
          "implicitHeaders" to "false",
          "openApiNullable" to "false",
          "oas3" to "true",
          "useSpringBoot3" to "true"))
}

tasks.register("generateVehicleApi", GenerateTask::class) {
  group = "openapi"
  description = "Generates the classes required to call ME Vehicle Service"
  generatorName.set("java")
  inputSpec.set("$projectDir/src/main/resources/openapi/clients/vehicle-service.yaml")
  outputDir.set("${project.layout.buildDirectory.get()}/generated/openapi-vehicle-service")
  packageName.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client")
  apiPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.apis")
  modelPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.models")
  configOptions.set(
      mapOf(
          "dateLibrary" to "java8-localdatetime",
          "generateApis" to "true",
          "generateApiTests" to "false",
          "generateModels" to "true",
          "generateModelTests" to "false",
          "generateModelDocumentation" to "false",
          "generateSupportingFiles" to "false",
          "hideGenerationTimestamp" to "true",
          "interfaceOnly" to "true",
          "library" to "webclient",
          "serializableModel" to "true",
          "useBeanValidation" to "false",
          "useTags" to "true",
          "implicitHeaders" to "false",
          "openApiNullable" to "false",
          "oas3" to "true",
          "useSpringBoot3" to "true"))
}

tasks.register("generateMdtApi", GenerateTask::class) {
  group = "openapi"
  description = "Generates the classes required to call ME MDT Service"
  generatorName.set("java")
  inputSpec.set("$projectDir/src/main/resources/openapi/clients/mdt-service.yaml")
  outputDir.set("${project.layout.buildDirectory.get()}/generated/openapi-mdt-service")
  packageName.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client")
  apiPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis")
  modelPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models")
  configOptions.set(
      mapOf(
          "dateLibrary" to "java8-localdatetime",
          "generateApis" to "true",
          "generateApiTests" to "false",
          "generateModels" to "true",
          "generateModelTests" to "false",
          "generateModelDocumentation" to "false",
          "generateSupportingFiles" to "false",
          "hideGenerationTimestamp" to "true",
          "interfaceOnly" to "true",
          "library" to "webclient",
          "serializableModel" to "true",
          "useBeanValidation" to "false",
          "useTags" to "true",
          "implicitHeaders" to "false",
          "openApiNullable" to "false",
          "oas3" to "true",
          "useSpringBoot3" to "true"))
}

tasks.register("generatePaxPaymentServiceApi", GenerateTask::class) {
  group = "openapi"
  description = "Generates the classes required to call ME Pax Payment Service"
  generatorName.set("java")
  inputSpec.set("$projectDir/src/main/resources/openapi/clients/pax-payment-service.yaml")
  outputDir.set("${project.layout.buildDirectory.get()}/generated/openapi-pax-payment-service")
  packageName.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client")
  apiPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.apis")
  modelPackage.set(
      "com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.models")
  configOptions.set(
      mapOf(
          "dateLibrary" to "java8-localdatetime",
          "generateApis" to "true",
          "generateApiTests" to "false",
          "generateModels" to "true",
          "generateModelTests" to "false",
          "generateModelDocumentation" to "false",
          "generateSupportingFiles" to "false",
          "hideGenerationTimestamp" to "true",
          "interfaceOnly" to "true",
          "library" to "webclient",
          "serializableModel" to "true",
          "useBeanValidation" to "false",
          "useTags" to "true",
          "implicitHeaders" to "false",
          "openApiNullable" to "false",
          "oas3" to "true",
          "useSpringBoot3" to "true"))
}

tasks.register("generateBookingServiceApi", GenerateTask::class) {
  group = "openapi"
  description = "Generates the classes required to call ME Booking Service"
  generatorName.set("java")
  inputSpec.set("$projectDir/src/main/resources/openapi/clients/booking-api.yaml")
  outputDir.set("${project.layout.buildDirectory.get()}/generated/openapi-booking-service")
  packageName.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client")
  apiPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.apis")
  modelPackage.set("com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.models")
  configOptions.set(
      mapOf(
          "dateLibrary" to "java8-localdatetime",
          "generateApis" to "true",
          "generateApiTests" to "false",
          "generateModels" to "true",
          "generateModelTests" to "false",
          "generateModelDocumentation" to "false",
          "generateSupportingFiles" to "false",
          "hideGenerationTimestamp" to "true",
          "interfaceOnly" to "true",
          "library" to "webclient",
          "serializableModel" to "true",
          "useBeanValidation" to "false",
          "useTags" to "true",
          "implicitHeaders" to "false",
          "openApiNullable" to "false",
          "oas3" to "true",
          "useSpringBoot3" to "true"))
}

// Task to add generated OpenAPI classes into sourceset
sourceSets {
  main {
    java { srcDirs("${project.layout.buildDirectory.get()}/generated/openapi/src/main/java") }
    java {
      srcDirs(
          "${project.layout.buildDirectory.get()}/generated/openapi-job-dispatch-service/src/main/java")
    }
    java {
      srcDirs(
          "${project.layout.buildDirectory.get()}/generated/openapi-fleet-analytic-service/src/main/java")
    }
    java {
      srcDirs("${project.layout.buildDirectory.get()}/generated/openapi-fare-service/src/main/java")
    }
    java {
      srcDirs(
          "${project.layout.buildDirectory.get()}/generated/openapi-vehicle-service/src/main/java")
    }
    java {
      srcDirs("${project.layout.buildDirectory.get()}/generated/openapi-mdt-service/src/main/java")
    }
    java {
      srcDirs(
          "${project.layout.buildDirectory.get()}/generated/openapi-pax-payment-service/src/main/java")
    }
    java {
      srcDirs(
          "${project.layout.buildDirectory.get()}/generated/openapi-booking-service/src/main/java")
    }
  }
}

tasks.withType<JavaCompile> {
  dependsOn(tasks.openApiGenerate)
  dependsOn("generateJobDispatchApi")
  dependsOn("generateFleetAnalyticApi")
  dependsOn("generateFareApi")
  dependsOn("generateMdtApi")
  dependsOn("generateVehicleApi")
  dependsOn("generatePaxPaymentServiceApi")
  dependsOn("generateBookingServiceApi")
}

tasks.spotlessJava {
  dependsOn("openApiGenerate")
  dependsOn("generateJobDispatchApi")
  dependsOn("generateFleetAnalyticApi")
  dependsOn("generateFareApi")
  dependsOn("generateVehicleApi")
  dependsOn("generateMdtApi")
  dependsOn("generatePaxPaymentServiceApi")
  dependsOn("generateBookingServiceApi")
  dependsOn("generateJsonSchema2Pojo")
}

// ========================================= Unit Test Settings ====================================
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

// ============================ Definition of Integration Test Type ============================
var integrationTest: SourceSet =
    sourceSets.create("integrationTest") {
      java {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        srcDir("src/integrationTest/java")
      }
      resources { srcDir("src/integrationTest/resources") }
    }

tasks.named<ProcessResources>("processIntegrationTestResources") {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

configurations[integrationTest.implementationConfigurationName].extendsFrom(
    configurations.testImplementation.get())

configurations[integrationTest.implementationConfigurationName].extendsFrom(
    configurations.implementation.get())

configurations[integrationTest.compileOnlyConfigurationName].extendsFrom(
    configurations.compileOnly.get())

configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(
    configurations.testRuntimeOnly.get())

configurations[integrationTest.annotationProcessorConfigurationName].extendsFrom(
    configurations.annotationProcessor.get())

val integrationTestTask =
    tasks.register<Test>("integrationTest") {
      group = "verification"
      description = "Perform integration tests for this project"
      useJUnitPlatform()
      reports { junitXml.required.set(true) }

      testClassesDirs = integrationTest.output.classesDirs
      classpath = sourceSets["integrationTest"].runtimeClasspath

      shouldRunAfter(tasks.test)
    }

// ================================= Jacoco Settings ========================================
// Configure Jacoco Test Coverage Report for project's unit test source set
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
}

// configuration for integration test reporting.
val jacocoIntegrationTestReport by
    tasks.creating(JacocoReport::class) {
      dependsOn(integrationTestTask)
      reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(
            layout.buildDirectory.file("reports/jacoco/integration/jacocoTestReport.xml"))
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/integration/html"))
      }
      sourceDirectories.setFrom(
          files(sourceSets["main"].allSource.srcDirs),
          file("../domain/build/classes/java/main"),
          file("../application/build/classes/java/main"))

      classDirectories.setFrom(
          files(sourceSets["main"].output),
          file("../domain/build/classes/java/main"),
          file("../application/build/classes/java/main"))

      // Define the output location for the integration test exec file
      executionData.setFrom(layout.buildDirectory.file("jacoco/integrationTest.exec"))
    }

// configuration for aggregate test reporting
val jacocoAggregateReport by
    tasks.creating(JacocoReport::class) {
      dependsOn(
          tasks.jacocoTestReport, jacocoIntegrationTestReport, ":domain:test", ":application:test")
      reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(
            layout.buildDirectory.file("reports/jacoco/aggregate/testReport.xml"))
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregate/html"))
      }
      sourceDirectories.setFrom(
          files(sourceSets["main"].allSource.srcDirs),
          file("../domain/build/classes/java/main"),
          file("../application/build/classes/java/main"))

      // Include execution data from both unit and integration tests
      // add exec files from unit tests
      val execFiles =
          files(
                  layout.buildDirectory.file("jacoco/test.exec"),
                  layout.buildDirectory.file("jacoco/integrationTest.exec"),
                  layout.projectDirectory.file("../domain/build/jacoco/test.exec"),
                  layout.projectDirectory.file("../application/build/jacoco/test.exec"))
              .filter { it.exists() }
      executionData.setFrom(execFiles)

      // include classes from application and domain dependencies
      var classDirectoriesToInclude =
          files(
              fileTree(
                  mapOf(
                      "dir" to layout.buildDirectory.dir("../domain/build/classes/java/main"),
                      "excludes" to
                          listOf(
                              "**/entities/**",
                              "**/enums/**",
                              "**/constants/**",
                              "**/exceptions/**",
                          ))),
              fileTree(
                  mapOf(
                      "dir" to layout.buildDirectory.dir("../application/build/classes/java/main"),
                      "excludes" to
                          listOf(
                              "**/constant/**",
                              "**/outbound/**",
                          ))),
              fileTree(
                  mapOf(
                      "dir" to layout.buildDirectory.dir("classes/java/main"),
                      "excludes" to
                          listOf(
                              "**/configs/**",
                              "**/exceptions/**",
                              "**/mappers/*",
                              "**/mapper/*",
                              "**/constant/*",
                              "**/constants/*",
                              "**/entities/**",
                              "**/dtos/*",
                              "**/dto/*",
                              "**/enums/*",
                              "**/models/**",
                              "**/language/**",
                              "**/model/**",
                              "**/request/**",
                              "**/response/**",
                              "**/validator/**",
                              "**/restful/annotation/**"))))
      classDirectories.setFrom(classDirectoriesToInclude)
    }

tasks.check { dependsOn(jacocoAggregateReport) }

tasks.testCodeCoverageReport {
  mustRunAfter(tasks.generateGitProperties)
  dependsOn(tasks.generateGitProperties)
  dependsOn(tasks.jacocoTestReport)
  outputs.upToDateWhen { false }
}

// ================== SonarQube Settings ==================

sonar {
  val coverageExcludeDirList =
      arrayListOf(
          "**/configs/**",
          "**/exceptions/**",
          "**/mappers/*",
          "**/mapper/*",
          "**/constant/*",
          "**/constants/*",
          "**/entities/**",
          "**/dtos/*",
          "**/dto/*",
          "**/enums/*",
          "**/models/**",
          "**/language/**",
          "**/model/**",
          "**/request/**",
          "**/response/**",
          "**/validator/**",
          "**/restful/annotation/**",
      )
  val duplicationExcludeDirList =
      arrayListOf(
          "**/configs/**",
          "**/exceptions/**",
          "**/mapper/*",
          "**/enums/*",
          "**/entities/**",
          "**/dtos/**",
          "**/models/**",
          "**/strategy/**")
  val coverageExcludeDir = coverageExcludeDirList.joinToString(",")
  val duplicationExcludeDir = duplicationExcludeDirList.joinToString(",")

  properties {
    // project identification
    property("sonar.projectName", "ngp-me-vehiclecomm-svc")
    property("sonar.projectVersion", "1.0")
    property("sonar.projectBaseDir", "$projectDir/..")

    // report paths and binaries
    property(
        "sonar.junit.reportPaths",
        "$projectDir/build/test-results/test,$projectDir/build/test-results/integrationTest")
    property(
        "sonar.java.test.binaries",
        "$projectDir/build/classes/java/integrationTest,$projectDir/build/classes/java/test")
    property("sonar.test", "$projectDir/src/test/java,$projectDir/src/integrationTest/java")
    property("sonar.java.binaries", "$projectDir/build/classes/java/main")
    property(
        "sonar.coverage.jacoco.xmlReportPaths",
        "$projectDir/build/reports/jacoco/aggregate/testReport.xml")
    property("sonar.sources", "$projectDir/src/main/java")
    property("sonar.coverage.exclusions", coverageExcludeDir)
    property("sonar.cpd.exclusions", duplicationExcludeDir)

    // Exclude domain and application directories from being indexed again
    property("sonar.exclusions", "$projectDir/../domain/**,$projectDir/../application/**")
  }
}

// ============================ Misc Configuration ============================
dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${ext.get("springCloudVersion")}")
  }
}
