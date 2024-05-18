import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0-Beta2"
    application
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

    //Database
    implementation("org.postgresql:postgresql:42.7.3")

    //Testing
    testImplementation(kotlin("test"))
}



kotlin {
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    jvmToolchain(8)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.github.panarik.StartKt")
}