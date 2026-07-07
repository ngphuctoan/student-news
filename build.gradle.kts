plugins {
    id("java")
}

group = "io.github.ngphuctoan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.jsoup:jsoup:1.22.2")
}

tasks.test {
    useJUnitPlatform()
}
