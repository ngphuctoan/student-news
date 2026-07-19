import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.5.1"
}

val next: String = providers.gradleProperty("nextReleaseVersion").orElse("0.0.0").get()
val date: String = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.BASIC_ISO_DATE)
val hash: Provider<String> =
    providers.exec { commandLine("git", "rev-parse", "--short=7", "HEAD") }.standardOutput.asText.map(String::trim)

group = "io.github.ngphuctoan.studentnews"
version = providers.gradleProperty("version").orElse(hash.map { "$next-dev.$date.$it" }).get()

application {
    mainClass = "Main"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencyLocking {
    lockAllConfigurations()
}

dependencies {
    implementation(platform(libs.jdbi3.bom))
    implementation(libs.jsoup)
    implementation(libs.jackson.core.databind)
    implementation(libs.angus.mail)
    implementation(libs.jdbi3.core)
    implementation(libs.jdbi3.sqlobject)
    implementation(libs.h2)
    implementation(libs.flyway.core)
    implementation(libs.jetbrains.annotations)
    implementation(libs.slf4j.api)
    runtimeOnly(libs.logback.classic)
}

tasks.compileJava {
    options.release = 25
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "Main"
    }
}

