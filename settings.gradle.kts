rootProject.name = "backend"

include(
    "users-domain",
    "users-application",
    "users-infrastructure"
)

project(":users-domain").projectDir = file("users/domain")
project(":users-application").projectDir = file("users/application")
project(":users-infrastructure").projectDir = file("users/infrastructure")