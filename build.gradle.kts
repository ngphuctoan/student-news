plugins {
    id("java")
}

group = "io.github.ngphuctoan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.22.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.0")
    implementation("org.jetbrains:annotations:26.0.2")
}

tasks.test {
    useJUnitPlatform()
}
