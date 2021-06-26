group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("stdlib"))
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.6.2")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}