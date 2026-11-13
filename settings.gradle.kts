rootProject.name = "backend"

val usersModules = listOf(
    "users-api",
    "users-domain",
    "users-spi",
    "users-core",
    "users-adapter-web",
    "users-adapter-jpa"
)

usersModules.forEach { moduleName ->
    include(moduleName)

    project(":$moduleName").projectDir =
        file("users/$moduleName")
}