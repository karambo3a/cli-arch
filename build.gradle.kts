plugins {
    java
    application
}

group = "org.cli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("org.cli.CLI")
}

tasks.register<JavaExec>("runCLI") {
    group = "Execution"
    description = "Run the CLI in interactive mode"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.cli.CLI")

    standardInput = System.`in`
    systemProperty("cli.interactive", "true")

    jvmArgs = listOf(
        "-Djava.awt.headless=true",
        "-Djline.terminal=jline.UnixTerminal"
    )
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("cli.jar")
}

tasks.test {
    useJUnitPlatform()
}

