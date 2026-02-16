// I changed the structure and fixed the dependency errors, so it is
// a bit different from the tutorial, but it is equivalent to the tutorial.

plugins {
    java
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "7.1.0.6387"
    id("org.owasp.dependencycheck") version "12.2.0"
    jacoco
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"
description = "eshop"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val seleniumJavaVersion = "4.40.0"
val seleniumJupiterVersion = "6.3.1"
val webdrivermanagerVersion = "6.3.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.apache.commons:commons-lang3:3.18.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
    testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
    testImplementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

val sonarHostUrlProvider = providers.gradleProperty("sonarHostUrl")
    .orElse(providers.environmentVariable("SONAR_HOST_URL"))
    .orElse("https://sonarcloud.io")
val sonarProjectKeyProvider = providers.gradleProperty("sonarProjectKey")
    .orElse(providers.environmentVariable("A-Gregorius-Ega-Aditama-S-2406434153_Module-2-CI-CD-DevOpsFile"))
val sonarOrganizationProvider = providers.gradleProperty("sonarOrganization")
    .orElse(providers.environmentVariable("a-gregorius-ega-aditama-s-2406434153"))
val sonarTokenProvider = providers.gradleProperty("sonarToken")
    .orElse(providers.environmentVariable("SONAR_TOKEN"))
val githubRepositoryProvider = providers.environmentVariable("GITHUB_REPOSITORY")
val nvdApiKeyProvider = providers.environmentVariable("NVD_API_KEY")

sonar {
    properties {
        val githubRepository = githubRepositoryProvider.orNull
        val derivedProjectKey = githubRepository?.replace("/", "_")
        val derivedOrganization = githubRepository?.substringBefore("/")

        val sonarProjectKey = sonarProjectKeyProvider.orNull ?: derivedProjectKey
        val sonarOrganization = sonarOrganizationProvider.orNull ?: derivedOrganization

        property("sonar.host.url", sonarHostUrlProvider.get())
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.absolutePath
        )
        property(
            "sonar.junit.reportPaths",
            layout.buildDirectory.dir("test-results/test").get().asFile.absolutePath
        )

        if (!sonarProjectKey.isNullOrBlank()) {
            property("sonar.projectKey", sonarProjectKey)
        }
        if (!sonarOrganization.isNullOrBlank()) {
            property("sonar.organization", sonarOrganization)
        }
        if (!sonarTokenProvider.orNull.isNullOrBlank()) {
            property("sonar.token", sonarTokenProvider.get())
        }
    }
}

dependencyCheck {
    formats = listOf("HTML", "JSON")
    scanConfigurations = listOf("runtimeClasspath", "testRuntimeClasspath")
    failBuildOnCVSS = 11.0F
    nvd.apiKey = nvdApiKeyProvider.orNull
}


fun Test.inheritFromTestTask() {
    val testTask = tasks.named<Test>("test").get()
    testClassesDirs = testTask.testClassesDirs
    classpath = testTask.classpath
    useJUnitPlatform()
}

tasks.register<Test>("unitTest") {
    description = "Runs unit tests."
    group = "verification"
    inheritFromTestTask()

    filter {
        excludeTestsMatching("*FunctionalTest")
    }
}

tasks.register<Test>("functionalTest") {
    description = "Runs functional tests."
    group = "verification"
    inheritFromTestTask()

    filter {
        includeTestsMatching("*FunctionalTest")
    }
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.matching { it.name == "sonar" || it.name == "sonarqube" }.configureEach {
    dependsOn(tasks.test, tasks.jacocoTestReport)
}

tasks.register<JacocoReport>("jacocoUnitTestReport") {
    description = "Generates Jacoco coverage report for unit tests."
    group = "verification"
    dependsOn(tasks.named("unitTest"))

    executionData.setFrom(fileTree(layout.buildDirectory).include("jacoco/unitTest.exec"))
    sourceSets(sourceSets["main"])

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
