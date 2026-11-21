plugins {
    `java-library`
    id("com.github.shalousun.smart-doc")
}

val mapstructVersion = "1.6.3"

dependencies {
    implementation(project(":middleware"))

    implementation(project(":users-domain"))
    implementation(project(":users-application"))

    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

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
    implementation("org.springframework.security:spring-security-crypto")

    runtimeOnly("org.bouncycastle:bcprov-jdk18on:1.87")
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
        "bootstrap/src/main/resources/smart-doc.json"
    )
}

tasks.named("build") {
    dependsOn("smartDocOpenApi")
}
