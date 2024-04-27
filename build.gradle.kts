plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.github.panarik"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {

    //Logging
    implementation("org.slf4j:slf4j-simple:2.0.7")

    //HTTP Client
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.9")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")

    //Testing
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}