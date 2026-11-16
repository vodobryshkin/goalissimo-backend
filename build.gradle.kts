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
    base
    jacoco

    id("org.springframework.boot") version "4.1.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false

    id("com.github.shalousun.smart-doc") version "4.0.0" apply false
}

group = "ru.goalissimo"
version = "0.0.1-SNAPSHOT"
description = "goalissimo"

val springBootBom =
    "org.springframework.boot:spring-boot-dependencies:4.1.2"

allprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }
}

subprojects {
    if (name == "bootstrap") {
        apply(plugin = "java")
    } else {
        apply(plugin = "java-library")
    }

    apply(plugin = "checkstyle")
    apply(plugin = "pmd")
    apply(plugin = "jacoco")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        // BOM управляет версиями, но не добавляет Spring в модули.
        add("implementation", platform(springBootBom))

        add("annotationProcessor", platform(springBootBom))
        add("testAnnotationProcessor", platform(springBootBom))

        // Lombok для основного кода.
        add("compileOnly", "org.projectlombok:lombok")
        add("annotationProcessor", "org.projectlombok:lombok")

        // Lombok для тестов.
        add("testCompileOnly", "org.projectlombok:lombok")
        add("testAnnotationProcessor", "org.projectlombok:lombok")

        // Общие зависимости тестов.
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

jacoco {
    toolVersion = "0.8.13"
}

val coverageProjects = subprojects.toList()

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

val jacocoTestReport = tasks.register<JacocoReport>(
    "jacocoTestReport"
) {
    group = "verification"
    description = "Создаёт общий отчёт покрытия всех модулей."

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

val jacocoTestCoverageVerification =
    tasks.register<JacocoCoverageVerification>(
        "jacocoTestCoverageVerification"
    ) {
        group = "verification"
        description = "Проверяет общее покрытие: минимум 80%."

        dependsOn(jacocoTestReport)

        violationRules {
            rule {
                element = "BUNDLE"

                listOf("LINE", "BRANCH", "INSTRUCTION")
                    .forEach { coverageCounter ->
                        limit {
                            counter = coverageCounter
                            value = "COVEREDRATIO"
                            minimum = "0.80".toBigDecimal()
                        }
                    }
            }
        }
    }

tasks.named("check") {
    dependsOn(jacocoTestCoverageVerification)

    dependsOn(
        subprojects.map { project ->
            project.tasks.named("check")
        }
    )
}

tasks.named("assemble") {
    dependsOn(
        subprojects.map { project ->
            project.tasks.named("assemble")
        }
    )
}

tasks.named("clean") {
    dependsOn(
        subprojects.map { project ->
            project.tasks.named("clean")
        }
    )
}

val smartDocOpenApi = tasks.register("smartDocOpenApi") {
    group = "documentation"
    description = "Генерирует OpenAPI для инфраструктурного модуля."

    dependsOn(":users-infrastructure:smartDocOpenApi")
}

tasks.named("build") {
    dependsOn(smartDocOpenApi)
}