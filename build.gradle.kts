plugins {
    java
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("checkstyle")
    id("pmd")
    id("jacoco")
}

group = "ru.goalissimo"
version = "0.0.1-SNAPSHOT"
description = "goalissimo"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springModulithVersion"] = "2.1.1"

dependencies {
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}

tasks.named("check") {
    dependsOn("jacocoTestReport")
}

checkstyle {
    toolVersion = "14.1.0"
    configFile = file("config/checkstyle/checkstyle.xml")

    isIgnoreFailures = false
    isShowViolations = true
}

pmd {
    isConsoleOutput = true
    toolVersion = "7.16.0"
    rulesMinimumPriority = 5

    ruleSetFiles = files("config/pmd/pmd.xml")
    ruleSets = emptyList()
    isIgnoreFailures = false
}
