plugins {
    `java-library`
}

description = "Общие технические конфигурации Goalissimo"

dependencies {
    api("org.springframework.security:spring-security-core")
    // implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}
