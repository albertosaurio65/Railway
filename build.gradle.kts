/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
 *
 * Licensed under the GNU Lesser General Public License v3 or later.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.ithundxr.silk.ChangelogText
import me.modmuss50.mpp.ModPublishExtension
import org.gradle.api.publish.maven.MavenPublication
import java.util.zip.Deflater
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    java
    id("maven-publish")
    id("net.neoforged.gradle") version "6.0.18"
}

println("Steam 'n' Rails v${"mod_version"()}")

val isRelease = System.getenv("RELEASE_BUILD")?.toBoolean() ?: false
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toInt()
val gitHash = "\"${calculateGitHash() + if (hasUnstaged()) "-modified" else ""}\""

extra["gitHash"] = gitHash

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        withSourcesJar()
    }

    group = "maven_group"()
    base.archivesName.set("archives_base_name"())

    val build = buildNumber?.let { "-build.${it}" } ?: "-local"
    version = "${"mod_version"()}+mc${"minecraft_version"() + if (isRelease) "" else build}"

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

subprojects {
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    setupRepositories()

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        withSourcesJar()
    }

    val remapJar = tasks.register("remapJar") {
        // Puedes añadir aquí la transformación de clases si quieres
    }

    publishing {
        publications {
            create<MavenPublication>("maven${project.name.capitalize()}") {
                artifactId = "${"archives_base_name"()}-${project.name}-${"minecraft_version"()}"
                from(components["java"])
            }
        }

        repositories {
            val mavenToken = System.getenv("MAVEN_TOKEN")
            if (!mavenToken.isNullOrEmpty()) {
                maven {
                    url = uri("https://maven.ithundxr.dev/releases")
                    credentials {
                        username = "railways-github"
                        password = mavenToken
                    }
                }
            }
        }
    }

    val releaseType = when {
        version.toString().contains("alpha") -> me.modmuss50.mpp.ReleaseType.ALPHA
        version.toString().contains("beta") -> me.modmuss50.mpp.ReleaseType.BETA
        else -> me.modmuss50.mpp.ReleaseType.STABLE
    }

    configure<ModPublishExtension> {
        file.set(remapJar.flatMap { it.outputs.files.singleFile })
        version.set(project.version.toString())
        changelog = ChangelogText.getChangelogText(rootProject).toString()
        type = releaseType
        displayName = "Steam 'n' Rails ${"mod_version"()} ${project.name} ${"minecraft_version"()}"

        modLoaders.add("neoforge")
        curseforge {
            projectId = "curseforge_id"()
            accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
            minecraftVersions.add("minecraft_version"())
            requires { slug = "create" }
        }
        modrinth {
            projectId = "modrinth_id"()
            accessToken = providers.environmentVariable("MODRINTH_TOKEN")
            minecraftVersions.add("minecraft_version"())
            requires { slug = "create" }
        }
    }
}

fun Project.setupRepositories() {
    repositories {
        mavenCentral()
        maven("https://maven.createmod.net") // Create, Ponder, Flywheel
        maven("https://maven.terraformersmc.com/releases/") // Mod Menu, EMI
        maven("https://maven.ithundxr.dev/mirror") {
            content { includeGroup("com.tterrag.registrate") }
        }
        maven("$rootDir/local-maven")
    }
}

fun calculateGitHash(): String {
    return try {
        val output = providers.exec { commandLine("git", "rev-parse", "HEAD") }
        output.standardOutput.asText.get().trim()
    } catch (_: Throwable) { "unknown" }
}

fun hasUnstaged(): Boolean {
    return try {
        val output = providers.exec { commandLine("git", "status", "--porcelain") }
        output.standardOutput.asText.get().trim().isNotEmpty()
    } catch (_: Throwable) { false }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String ?: throw IllegalStateException("Property $this is not defined")
}
