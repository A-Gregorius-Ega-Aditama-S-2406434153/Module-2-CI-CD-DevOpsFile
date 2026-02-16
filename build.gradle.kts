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

val githubRepositoryProvider = providers.environmentVariable("GITHUB_REPOSITORY")

val sonarProjectKeyProvider = providers.gradleProperty("sonarProjectKey")
    .orElse(providers.environmentVariable("SONAR_PROJECT_KEY"))
    .orElse(githubRepositoryProvider.map { it.replace("/", "_") })
    .orElse("undefined_project_key")

val sonarOrganizationProvider = providers.gradleProperty("sonarOrganization")
    .orElse(providers.environmentVariable("SONAR_ORGANIZATION"))
    .orElse(githubRepositoryProvider.map { it.substringBefore("/") })
    .orElse("undefined_org")

val sonarTokenProvider = providers.gradleProperty("sonarToken")
    .orElse(providers.environmentVariable("SONAR_TOKEN"))
    .orElse("")

val nvdApiKeyProvider = providers.environmentVariable("NVD_API_KEY")


sonar {
    properties {
        property("sonar.host.url", sonarHostUrlProvider.get())
        property("sonar.projectKey", sonarProjectKeyProvider.get())
        property("sonar.organization", sonarOrganizationProvider.get())
        property("sonar.token", sonarTokenProvider.get())
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.absolutePath
        )
        property(
            "sonar.junit.reportPaths",
            layout.buildDirectory.dir("test-results/test").get().asFile.absolutePath
        )
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
