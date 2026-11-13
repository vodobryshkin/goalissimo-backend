import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.Pmd
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReportBase

plugins {
    java
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("checkstyle")
    id("pmd")
    id("jacoco")
    id("com.github.shalousun.smart-doc") version "4.0.0"
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


dependencies { implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(project(":users-api"))
    implementation(project(":users-spi"))
    implementation(project(":users-core"))
    implementation(project(":users-adapter-web"))
    implementation(project(":users-adapter-jpa"))
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

    dependsOn(
        subprojects.map { project ->
            project.tasks.named("check")
        }
    )
}

smartdoc {
    configFile = file("src/main/resources/smart-doc.json")
}

tasks.named("build") {
    dependsOn("smartDocOpenApi")
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "checkstyle")
    apply(plugin = "pmd")
    apply(plugin = "jacoco")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        add(
            "implementation",
            platform(
                "org.springframework.boot:" +
                        "spring-boot-dependencies:4.1.2"
            )
        )

        add(
            "testImplementation",
            "org.junit.jupiter:junit-jupiter"
        )
        add(
            "testImplementation",
            "org.assertj:assertj-core"
        )
        add(
            "testRuntimeOnly",
            "org.junit.platform:junit-platform-launcher"
        )
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()

        reports {
            junitXml.required.set(true)
            html.required.set(true)
        }

        testLogging {
            events("failed", "skipped")
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    extensions.configure<CheckstyleExtension> {
        toolVersion = "14.1.0"

        configFile = rootProject.file(
            "config/checkstyle/checkstyle.xml"
        )

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

    extensions.configure<PmdExtension> {
        toolVersion = "7.16.0"

        isConsoleOutput = true
        isIgnoreFailures = false

        rulesMinimumPriority = 5
        maxFailures.set(0)

        ruleSetFiles = rootProject.files(
            "config/pmd/pmd.xml"
        )
        ruleSets = emptyList()
    }

    tasks.withType<Pmd>().configureEach {
        group = "verification"

        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    extensions.configure<JacocoPluginExtension> {
        toolVersion = "0.8.13"
    }
}

val coverageProjects = allprojects.toList()

val coverageTests = coverageProjects.map { project ->
    project.tasks.named<Test>("test")
}

val coverageMainSources = coverageProjects.map { project ->
    project.extensions
        .getByType<SourceSetContainer>()
        .getByName("main")
}

val coverageData = files(
    coverageProjects.map { project ->
        project.layout.buildDirectory.file("jacoco/test.exec")
    }
).filter { it.isFile }

val verifyCoverageData = tasks.register("verifyCoverageData") {
    group = "verification"

    dependsOn(coverageTests)

    doLast {
        check(coverageData.files.isNotEmpty()) {
            "Нет данных JaCoCo: проверь, что тесты были выполнены."
        }
    }
}

tasks.withType<JacocoReportBase>().configureEach {
    dependsOn(verifyCoverageData)

    executionData.setFrom(coverageData)

    classDirectories.setFrom(
        coverageMainSources.map { it.output.classesDirs }
    )

    sourceDirectories.setFrom(
        coverageMainSources.map { it.allJava.srcDirs }
    )
}