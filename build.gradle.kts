import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml.Load

plugins {
    id("net.kyori.indra") version "4.0.0"
    id("net.kyori.indra.checkstyle") version "4.0.0"
    id("com.gradleup.shadow") version "9.4.0"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
    id("xyz.jpenilla.run-paper") version "3.1.0"
    kotlin("jvm") version "2.4.0"
}

group = "com.vestriamc.basiclore"
version = "4.0"

indra {
    javaVersions {
        minimumToolchain(25)
        target(25)
    }
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/") // New Paper API endpoint
    maven("https://nexus.neetgames.com/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.essentialsx.net/releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.74-stable")

    // libraries
    implementation("org.incendo:cloud-paper:2.1.0-SNAPSHOT")
    implementation("org.incendo:cloud-minecraft-extras:2.1.0-SNAPSHOT")

    // integrations
    compileOnly("net.essentialsx:EssentialsX:2.21.2") {
        isTransitive = false
    }
    compileOnly("net.luckperms:api:5.5") {
        isTransitive = false
    }
}


tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set(null as String?)
        archiveFileName.set(project.name + ".jar")
    }

    runServer {
        minecraftVersion("26.2.1")

        downloadPlugins {
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
            github("EssentialsX", "Essentials", "2.22.0", "EssentialsX-2.22.0.jar")
            hangar("ViaVersion", "5.11.0")
            hangar("ViaBackwards", "5.11.0")
            modrinth("luckperms", "v5.5.81-bukkit")
        }
    }
}

paperPluginYaml {
    main = "com.vestriamc.basiclore.BasicLore"
    authors = listOf("GeneralSarcasam")
    apiVersion = "26.2.1"

    dependencies {
        server("Vault", Load.BEFORE, required = true)
        server("EssentialsX", Load.BEFORE, required = true)
        server("LuckPerms", Load.BEFORE, required = true)
    }
}