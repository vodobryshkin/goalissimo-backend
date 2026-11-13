import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
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

val springBootBom =
    "org.springframework.boot:spring-boot-dependencies:4.1.2"

allprojects {
    if (this != rootProject) {
        apply(plugin = "java-library")
    }

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
        add("implementation", platform(springBootBom))

        add("annotationProcessor", platform(springBootBom))
        add("testAnnotationProcessor", platform(springBootBom))

        add("compileOnly", "org.projectlombok:lombok")
        add("annotationProcessor", "org.projectlombok:lombok")

        add("testCompileOnly", "org.projectlombok:lombok")
        add("testAnnotationProcessor", "org.projectlombok:lombok")

        add("testImplementation", "org.junit.jupiter:junit-jupiter")
        add("testImplementation", "org.assertj:assertj-core")

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

configure(
    subprojects.filter { project ->
        project.name.startsWith("users-adapter-")
    }
) {
    dependencies {
        add(
            "implementation",
            "org.springframework.boot:spring-boot-starter"
        )
    }
}

dependencies {
    implementation(project(":users-api"))
    implementation(project(":users-spi"))
    implementation(project(":users-core"))
    implementation(project(":users-adapter-web"))
    implementation(project(":users-adapter-jpa"))

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")

    developmentOnly(
        "org.springframework.boot:spring-boot-docker-compose"
    )

    runtimeOnly("org.postgresql:postgresql")

    testImplementation(
        "org.springframework.boot:spring-boot-starter-test"
    )
    testImplementation(
        "org.springframework.boot:spring-boot-testcontainers"
    )
    testImplementation(
        "org.springframework.security:spring-security-test"
    )
    testImplementation(
        "org.testcontainers:testcontainers-junit-jupiter"
    )
    testImplementation(
        "org.testcontainers:testcontainers-postgresql"
    )
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
    description = "Проверяет наличие данных покрытия JaCoCo."

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
        coverageMainSources.map { sourceSet ->
            sourceSet.output.classesDirs
        }
    )

    sourceDirectories.setFrom(
        coverageMainSources.map { sourceSet ->
            sourceSet.allJava.srcDirs
        }
    )
}

tasks.named<JacocoReport>("jacocoTestReport") {
    description = "Создаёт общий отчёт покрытия всех проектов."

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)

        xml.outputLocation.set(
            layout.buildDirectory.file(
                "reports/jacoco/test/jacocoTestReport.xml"
            )
        )

        html.outputLocation.set(
            layout.buildDirectory.dir(
                "reports/jacoco/test/html"
            )
        )
    }
}

tasks.named<JacocoCoverageVerification>(
    "jacocoTestCoverageVerification"
) {
    description = "Проверяет общее покрытие: минимум 80%."

    violationRules {
        rule {
            element = "BUNDLE"

            listOf("LINE", "BRANCH", "INSTRUCTION").forEach { coverageCounter ->
                limit {
                    counter = coverageCounter
                    value = "COVEREDRATIO"
                    minimum = "0.80".toBigDecimal()
                }
            }
        }
    }
}

tasks.named<Test>("test") {
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.named("check") {
    dependsOn(
        tasks.named("jacocoTestReport"),
        tasks.named("jacocoTestCoverageVerification")
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