import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.Pmd
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

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
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom(
            "org.springframework.modulith:spring-modulith-bom:" +
                    property("springModulithVersion")
        )
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }

    testLogging {
        events("failed", "skipped")
        exceptionFormat =
            org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.test {
    finalizedBy(tasks.named("jacocoTestReport"))
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.test)

    violationRules {
        rule {
            element = "BUNDLE"

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }

            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

checkstyle {
    toolVersion = "14.1.0"
    configFile = file("config/checkstyle/checkstyle.xml")

    isIgnoreFailures = false
    isShowViolations = true

    maxErrors = 0
    maxWarnings = 0
}

tasks.withType<Checkstyle>().configureEach {
    group = "verification"

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

pmd {
    toolVersion = "7.16.0"

    isConsoleOutput = true
    isIgnoreFailures = false

    rulesMinimumPriority = 5
    maxFailures.set(0)

    ruleSetFiles = files("config/pmd/pmd.xml")
    ruleSets = emptyList()
}

tasks.withType<Pmd>().configureEach {
    group = "verification"

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.named("check") {
    dependsOn(
        "jacocoTestReport",
        "jacocoTestCoverageVerification"
    )
}