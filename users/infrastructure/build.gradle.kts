plugins {
    `java-library`
    id("com.github.shalousun.smart-doc")
}

dependencies {
    implementation(project(":users-domain"))
    implementation(project(":users-application"))

    implementation(
        "org.springframework.boot:spring-boot-starter-security"
    )
    implementation(
        "org.springframework.boot:spring-boot-starter-webmvc"
    )
    implementation(
        "org.springframework.boot:spring-boot-starter-jooq"
    )
    implementation(
        "org.springframework.boot:spring-boot-starter-data-jpa"
    )
    implementation(
        "org.springframework.boot:spring-boot-starter-validation"
    )
    implementation(
        "org.springframework.boot:spring-boot-starter-liquibase"
    )

    runtimeOnly("org.postgresql:postgresql")

    testImplementation(
        "org.springframework.boot:spring-boot-starter-test"
    )
    testImplementation(
        "org.springframework.security:spring-security-test"
    )
}

smartdoc {
    configFile = rootProject.file(
        "src/main/resources/smart-doc.json"
    )
}

tasks.named("build") {
    dependsOn("smartDocOpenApi")
}