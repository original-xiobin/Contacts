plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("kapt") version "2.1.10"
}

group = "org.xiobin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.moshi:moshi:1.15.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.2")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.8.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}