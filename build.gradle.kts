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

    // Paralleldots API for semantic analysis https://dashboard.komprehend.io/plugin-api
    implementation("com.paralleldots:paralleldots:1.0.3")

    // Discord4J bot https://docs.discord4j.com/quickstart#download--installation
    implementation("com.discord4j:discord4j-core:3.2.2")
    // Spring context, used for Dependency Injection and Application Setup
    // https://mvnrepository.com/artifact/org.springframework/spring-context
    implementation("org.springframework:spring-context:5.3.17")

    // MongoDB Driver https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-reactivestreams
    implementation("org.mongodb:mongodb-driver-sync:4.5.1")
    // KMongo for integrated Kotlin support for MongoDB Queries https://litote.org/kmongo/quick-start/
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.5.0")


    // Kotest library
    testImplementation("io.kotest:kotest-runner-junit5:5.2.1")
    // Kotest assertions
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