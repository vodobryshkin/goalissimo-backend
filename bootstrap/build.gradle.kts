import org.gradle.api.tasks.testing.Test
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java

    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

description = "Запуск и сборка приложения Goalissimo"

dependencies {
    implementation(project(":middleware"))

    implementation(project(":users-domain"))
    implementation(project(":users-application"))
    implementation(project(":users-infrastructure"))

    implementation(
        "org.springframework.boot:spring-boot-starter"
    )

    developmentOnly(
        "org.springframework.boot:spring-boot-docker-compose"
    )

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

springBoot {
    mainClass.set(
        "ru.goalissimo.backend.GoalissimoApplication"
    )
}

tasks.named<BootRun>("bootRun") {
    workingDir(rootProject.projectDir)
}

tasks.named<Test>("test") {
    finalizedBy(
        rootProject.tasks.named("jacocoTestReport")
    )
}