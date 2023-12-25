import com.akuleshov7.buildutils.*
import com.saveourtool.diktat.plugin.gradle.DiktatExtension
import com.saveourtool.diktat.plugin.gradle.DiktatGradlePlugin

plugins {
    kotlin("multiplatform") apply false
    kotlin("plugin.serialization") version Versions.KOTLIN apply false
    id("com.akuleshov7.buildutils.publishing-configuration")
    id("com.saveourtool.diktat") version "2.0.0"
}

configureVersioning()

allprojects {
    repositories {
        mavenCentral()
    }

    apply<DiktatGradlePlugin>()

    configure<DiktatExtension> {
        diktatConfigFile = rootProject.file("diktat-analysis.yml")
        githubActions = findProperty("diktat.githubActions")?.toString()?.toBoolean() ?: false
        inputs {
            // using `Project#path` here, because it must be unique in gradle's project hierarchy
            if (path == rootProject.path) {
                include("$rootDir/buildSrc/src/**/*.kt", "$rootDir/*.kts", "$rootDir/buildSrc/**/*.kts")
                exclude("src/test/**/*.kt", "src/commonTest/**/*.kt")  // path matching this pattern will not be checked by diktat
            } else {
                include("src/**/*.kt", "**/*.kts")
                exclude("src/**test/**/*.kt", "src/commonTest/**/*.kt", "src/jvmTest/**/*.kt")  // path matching this pattern will not be checked by diktat
            }
        }
        debug = true
        reporters {
            plain()
        }
    }

    configureDetekt()
}

createDetektTask()
installGitHooks()
