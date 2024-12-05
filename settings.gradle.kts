rootProject.name = "ngp-me-vehiclecomm-svc"

include("techframework", "application", "domain")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    maven(url = "https://packages.confluent.io/maven/")
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      version("spring-boot", "3.2.2")
      version("spring-cloud-aws-starter", "3.1.0")
      version("jakarta-servlet-api", "6.0.0")
      version("jakarta.persistence-api", "3.1.0")
      version("jakarta-validation", "3.0.2")
      version("kafka-json-schema-serializer", "7.3.2")
      version("mapstruct", "1.5.5.Final")
      version("lombok-mapstruct-binding", "0.2.0")
      version("jackson-annotations", "2.15.3")
      version("jackson-datatype", "2.15.3")

      version("testcontainers", "1.19.0")
      version("hibernate-validator", "8.0.1.Final")
      version("jackson-databind", "2.18.1")
      version("aws-advanced-jdbc-wrapper", "2.2.5")
      version("micrometer", "1.11.5")
      version("micrometer-tracing-bridge-otel", "1.1.6")
      version("logbook-spring-boot-starter", "3.5.0")
      version("logback-contrib", "0.1.5")
      version("sl4j", "2.0.9")
      version("lombok", "1.18.30")
      version("springdoc", "2.2.0")
      version("therapi", "0.15.0")
      version("git-commit-id-maven-plugin", "5.0.0")
      version("junit", "5.10.0")
      version("mockito-junit-jupiter", "5.6.0")
      version("apache-commons-lang", "3.13.0")
      version("spring-kafka", "3.0.12")
      version("kafka-schema-registry-client", "7.3.2")
      version("swagger-annotation-jakarta", "2.2.19")

      version("spring-context", "6.0.13")
      version("retrofit", "2.9.0")
      version("testcontainer-redis", "1.6.4")

      version("spring-cloud-starter-config", "4.1.0")
      version("spring-cloud-aws", "2.4.4")
      version("awaitility", "4.2.0")
      version("datadoghq", "1.32.0")
      version("postgresql", "42.6.0")
      version("liquibase", "4.20.0")
      // version("spring-data-common", "")
      // :s:3.2.0

      // springboot libraries
      library("spring-boot-starter-web", "org.springframework.boot", "spring-boot-starter-web")
          .versionRef("spring-boot")
      library(
              "spring-boot-starter-validation",
              "org.springframework.boot",
              "spring-boot-starter-validation")
          .versionRef("spring-boot")
      library("spring-boot-starter-aop", "org.springframework.boot", "spring-boot-starter-aop")
          .versionRef("spring-boot")
      library(
              "spring-boot-configuration-processor",
              "org.springframework.boot",
              "spring-boot-configuration-processor")
          .versionRef("spring-boot")
      library("spring-data-commons", "org.springframework.data", "spring-data-commons")
          .versionRef("spring-boot")
      library("spring-boot-web-flux", "org.springframework.boot", "spring-boot-starter-webflux")
          .versionRef("spring-boot")
      library(
              "spring-boot-starter-data-redis",
              "org.springframework.boot",
              "spring-boot-starter-data-redis")
          .versionRef("spring-boot")

      library("spring-context", "org.springframework", "spring-context")
          .versionRef("spring-context")

      // aws spring cloud starter
      library("spring-cloud-aws-starter", "io.awspring.cloud", "spring-cloud-aws-starter")
          .versionRef("spring-cloud-aws-starter")
      library(
              "spring-cloud-aws-starter-parameter-store",
              "io.awspring.cloud",
              "spring-cloud-aws-starter-parameter-store")
          .versionRef("spring-cloud-aws-starter")
      library(
              "spring-cloud-aws-starter-secrets-manager",
              "io.awspring.cloud",
              "spring-cloud-aws-starter-secrets-manager")
          .versionRef("spring-cloud-aws-starter")

      // Jakarta Servlet API - Since from Spring Boot 3, javax namespace was changed to jakarta
      library("jakarta-servlet-api", "jakarta.servlet", "jakarta.servlet-api")
          .versionRef("jakarta-servlet-api")
      library("jakarta-persistence-api", "jakarta.persistence", "jakarta.persistence-api")
          .versionRef("jakarta.persistence-api")
      library("jakarta-validation", "jakarta.validation", "jakarta.validation-api")
          .versionRef("jakarta-validation")
      library("swagger-annotation-jakarta", "io.swagger.core.v3", "swagger-annotations-jakarta")
          .versionRef("swagger-annotation-jakarta")

      // ORM
      library("mapstruct", "org.mapstruct", "mapstruct").versionRef("mapstruct")
      library("mapstruct-processor", "org.mapstruct", "mapstruct-processor").versionRef("mapstruct")

      library("lombok-mapstruct-binding", "org.projectlombok", "lombok-mapstruct-binding")
          .versionRef("lombok-mapstruct-binding")

      library("jackson-annotations", "com.fasterxml.jackson.core", "jackson-annotations")
          .versionRef("jackson-annotations")
      library("jackson-datatype", "com.fasterxml.jackson.datatype", "jackson-datatype-jsr310")
          .versionRef("jackson-datatype")
      library("hibernate-validator", "org.hibernate.validator", "hibernate-validator")
          .versionRef("hibernate-validator")
      library("jackson-databind", "com.fasterxml.jackson.core", "jackson-databind")
          .versionRef("jackson-databind")

      // Actuator, metric, logging and tracing
      library(
              "spring-boot-starter-actuator",
              "org.springframework.boot",
              "spring-boot-starter-actuator")
          .versionRef("spring-boot")
      library("micrometer-core", "io.micrometer", "micrometer-core").versionRef("micrometer")
      library("micrometer-registry-prometheus", "io.micrometer", "micrometer-registry-prometheus")
          .versionRef("micrometer")
      library("micrometer-tracing-bridge-otel", "io.micrometer", "micrometer-tracing-bridge-otel")
          .versionRef("micrometer-tracing-bridge-otel")

      library("logbook-spring-boot-starter", "org.zalando", "logbook-spring-boot-starter")
          .versionRef("logbook-spring-boot-starter")

      library("logback-json-classic", "ch.qos.logback.contrib", "logback-json-classic")
          .versionRef("logback-contrib")
      library("logback-jackson", "ch.qos.logback.contrib", "logback-jackson")
          .versionRef("logback-contrib")

      library("slf4j-api", "org.slf4j", "slf4j-api").versionRef("sl4j")

      // Lombok
      library("lombok", "org.projectlombok", "lombok").versionRef("lombok")

      // datadog
      library("dd-trace-api", "com.datadoghq", "dd-trace-api").versionRef("datadoghq")

      // Test Libraries
      library("spring-boot-starter-test", "org.springframework.boot", "spring-boot-starter-test")
          .versionRef("spring-boot")
      library("junit-bom", "org.junit", "junit-bom").versionRef("junit")
      library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
      library("junit-jupiter-api", "org.junit.jupiter", "junit-jupiter-api").versionRef("junit")
      library("mockito-junit-jupiter", "org.mockito", "mockito-junit-jupiter")
          .versionRef("mockito-junit-jupiter")
      library("testcontainers", "org.testcontainers", "testcontainers").versionRef("testcontainers")
      library("testcontainers-kafka", "org.testcontainers", "kafka").versionRef("testcontainers")
      library("testcontainers-junit-jupiter", "org.testcontainers", "junit-jupiter")
          .versionRef("testcontainers")
      library("testcontainers-redis", "com.redis.testcontainers", "testcontainers-redis")
          .versionRef("testcontainer-redis")
      library("testcontainers-postgresql", "org.testcontainers", "postgresql")
          .versionRef("testcontainers")
      library("awaitility", "org.awaitility", "awaitility").versionRef("awaitility")

      // OpenAPI
      library(
              "springdoc-openapi-starter-webmvc-ui",
              "org.springdoc",
              "springdoc-openapi-starter-webmvc-ui")
          .versionRef("springdoc")

      // Runtime library
      library("therapi-runtime-javadoc", "com.github.therapi", "therapi-runtime-javadoc")
          .versionRef("therapi")

      // Maven Plugins
      library("git-commit-id-maven-plugin", "io.github.git-commit-id", "git-commit-id-maven-plugin")
          .versionRef("git-commit-id-maven-plugin")

      // Common
      library("apache-commons-lang", "org.apache.commons", "commons-lang3")
          .versionRef("apache-commons-lang")

      // Spring Kafka
      library("spring-kafka", "org.springframework.kafka", "spring-kafka")
          .versionRef("spring-kafka")
      library(
              "io-confluent-kafka-json-schema-serializer",
              "io.confluent",
              "kafka-json-schema-serializer")
          .versionRef("kafka-json-schema-serializer")

      library(
              "io-confluent-kafka-schema-registry-client",
              "io.confluent",
              "kafka-schema-registry-client")
          .versionRef("kafka-schema-registry-client")
      library("retrofit", "com.squareup.retrofit2", "retrofit").versionRef("retrofit")
      library("retrofit-jackson", "com.squareup.retrofit2", "converter-jackson")
          .versionRef("retrofit")

      library(
              "spring-cloud-starter-config",
              "org.springframework.cloud",
              "spring-cloud-starter-config")
          .versionRef("spring-cloud-starter-config")
      library("spring-cloud-bus", "org.springframework.cloud", "spring-cloud-starter-bus-kafka")
          .versionRef("spring-cloud-starter-config")
      library("spring-cloud-aws", "io.awspring.cloud", "spring-cloud-aws-dependencies")
          .versionRef("spring-cloud-aws")

      // Database dependencies
      library(
              "spring-boot-starter-data-jpa",
              "org.springframework.boot",
              "spring-boot-starter-data-jpa")
          .versionRef("spring-boot")
      library("postgresql", "org.postgresql", "postgresql").versionRef("postgresql")
      library("liquibase", "org.liquibase", "liquibase-core").versionRef("liquibase")
    }
  }
}
