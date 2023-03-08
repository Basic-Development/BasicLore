plugins {
    id("net.kyori.indra") version "2.0.2"
    //disabled during development || id("net.kyori.indra.checkstyle") version "2.0.2"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.vestriamc"
version = "2.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
}

tasks {
    indra {
        javaVersions {
            target(17)
        }
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        expand("version" to rootProject.version)
    }

    shadowJar {
        archiveClassifier.set(null as String?)
        archiveFileName.set(project.name + ".jar")
    }
}
