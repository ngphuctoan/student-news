plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.5.1"
}

group = "io.github.ngphuctoan"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.github.ngphuctoan.StudentNews")
}

repositories {
    mavenCentral()
}

dependencies {
    // Load bill of materials (BOM) for Jdbi.
    implementation(platform("org.jdbi:jdbi3-bom:3.54.0"))

    implementation("org.jsoup:jsoup:1.22.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.0")
    implementation("org.jetbrains:annotations:26.0.2")
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-sqlobject")
    implementation("com.h2database:h2:2.4.240")
    implementation("org.flywaydb:flyway-core:12.10.0")
    implementation("org.eclipse.angus:angus-mail:2.1.0-M1")
}

tasks.test {
    useJUnitPlatform()
}
