import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.5.10"

    //plugin for linting
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
}

group = "dev.warvdine"
version = "1.0.0-alpha"

repositories {
    mavenCentral()
}

dependencies {
    // Allows for kotlin reflection to read companion objects and similar
    // https://kotlinlang.org/docs/reflection.html
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")

    // SLF4J logger https://www.slf4j.org/download.html
    implementation("org.slf4j:slf4j-api:1.7.36")
    // Logger recommended here: https://docs.discord4j.com/logging
    implementation("ch.qos.logback:logback-classic:1.2.11")

    // Paralleldots API for semantic analysis
    implementation("com.paralleldots:paralleldots:1.0.3")

    // Discord4J bot https://docs.discord4j.com/quickstart#download--installation
    implementation("com.discord4j:discord4j-core:3.2.2")

    // Kotest library and assertions
    testImplementation("io.kotest:kotest-runner-junit5:5.2.1")
    testImplementation("io.kotest:kotest-assertions-core:5.2.1")
    // Mockk Library for testing
    testImplementation("io.mockk:mockk:1.12.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("dev.warvdine.qotddiscordbot.MainKt")
}