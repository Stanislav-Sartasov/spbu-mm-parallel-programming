import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "spbu.mm.parallel.programming"
version = "0.0.1"

plugins {
    kotlin("jvm") version "1.4.30" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.4"
            apiVersion = "1.4"
        }
    }
}