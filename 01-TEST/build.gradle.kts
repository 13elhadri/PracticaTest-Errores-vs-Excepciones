plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // Logger
    implementation("org.lighthousegames:logging:1.5.0")
    implementation("ch.qos.logback:logback-classic:1.5.8")

    // Result ROP
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.0.0")

    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}