import java.util.Properties

plugins {
    kotlin("multiplatform") version "1.7.10"
    `maven-publish`
}

group = "com.tomuvak.optional"
version = "0.0.8-SNAPSHOT"

val localProperties = Properties()
project.rootProject.file("local.properties").takeIf { it.canRead() }?.inputStream()?.let(localProperties::load)
fun local(key: String): String? = localProperties.getProperty(key)

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/tomuvak/testing")
        credentials {
            username = local("githubUser")
            password = local("githubToken")
        }
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    linuxX64()
    macosX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.tomuvak.optional-type:optional-type:0.0.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.tomuvak.testing:testing:0.0.7")
                implementation("com.tomuvak.optional-test:optional-test:0.0.5")
            }
        }

        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting

        val nativeMain by creating { dependsOn(commonMain) }
        val nativeTest by creating { dependsOn(commonTest) }

        val linuxX64Main by getting { dependsOn(nativeMain) }
        val linuxX64Test by getting { dependsOn(nativeTest) }
        val macosX64Main by getting { dependsOn(nativeMain) }
        val macosX64Test by getting { dependsOn(nativeTest) }
        val mingwX64Main by getting { dependsOn(nativeMain) }
        val mingwX64Test by getting { dependsOn(nativeTest) }
    }
}

local("githubRepository")?.let { githubRepository ->
    publishing {
        repositories {
            maven {
                url = uri("https://maven.pkg.github.com/$githubRepository")
                credentials {
                    username = local("githubUser")
                    password = local("githubToken")
                }
            }
        }
    }
}
