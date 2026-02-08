// I changed the structure and fixed the dependency errors, so it is
// a bit different from the tutorial, but it is equivalent to the tutorial.

plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
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

val seleniumJavaVersion = "4.14.1"
val seleniumJupiterVersion = "5.0.1"
val webdrivermanagerVersion = "5.6.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
    testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
    testImplementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
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
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.register<JacocoReport>("jacocoUnitTestReport") {
    description = "Generates Jacoco coverage report for unit tests."
    group = "verification"
    dependsOn(tasks.named("unitTest"))

    executionData.setFrom(fileTree(buildDir).include("jacoco/unitTest.exec"))
    sourceSets(sourceSets["main"])

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
