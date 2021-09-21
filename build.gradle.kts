import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
}

// Project variables.
group = "com.hugokindel"
version = "1.0"

repositories {
    // Maven repository.
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    val jUnitVersion: String by project

    // Main dependencies.
    implementation("net.dv8tion:JDA:4.3.0_324")
    implementation("com.jagrosh:jda-utilities:3.0.5")
    implementation("com.github.azzerial.slash-commands:api:1.1")
    implementation("com.github.hugokindel:lavaplayer:master-SNAPSHOT")
    implementation("se.michaelthelin.spotify:spotify-web-api-java:6.5.4")
    implementation("com.typesafe:config:1.4.1")
    implementation("org.jsoup:jsoup:1.14.2")
    implementation("com.github.jagrosh:JLyrics:-SNAPSHOT")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-nop:1.7.32")

    // JUnit libraries.
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
}

tasks {
    // Force unicode support.
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }

    // JUnit support.
    "test"(Test::class) {
        useJUnitPlatform()
    }

    // Set main class to run.
    jar {
        manifest {
            attributes(
                "Main-Class" to "com.hugokindel.bot.music.Main"
                //"Main-Class" to "com.jagrosh.jmusicbot.JMusicBot"
            )
        }
    }
}

// Changes the standard input (useful because Gradle can hide the input in some cases).
val run by tasks.getting(JavaExec::class) {
    standardInput = System.`in`
}

// Set main class.
application.mainClass.set("com.hugokindel.bot.music.Main")
//application.mainClass.set("com.jagrosh.jmusicbot.JMusicBot")

// List of JVM options to pass.
var jvmOptions = mutableListOf<String>()
// Gradle has an issue supporting unicode within Powershell or cmd.exe,
// you need to use `chcp 65001` to enable unicode characters
// (this is not an issue in distributed builds, only within gradle commands output).
jvmOptions.add("-Dfile.encoding=utf-8")
// Pass an IDE name information to know within the engine's code if we are debugging within an IDE.
if (project.gradle.startParameter.taskNames.contains("run") && System.getProperty("idea.vendor.name") == "JetBrains") {
    jvmOptions.add("-Dide=JetBrains")
}
application.applicationDefaultJvmArgs = jvmOptions

// Set minimal JDK version.
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}