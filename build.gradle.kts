plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.5.1"
}

group = "io.github.ngphuctoan.studentnews"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.github.ngphuctoan.studentnews.Main")
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
