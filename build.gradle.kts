import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val exposedVersion: String by project

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization").version("1.5.10")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("io.arrow-kt:arrow-core:1.1.2")
                implementation("io.arrow-kt:arrow-fx-coroutines:1.1.2")
                implementation("io.arrow-kt:arrow-fx-stm:1.1.2")
                implementation("io.arrow-kt:arrow-optics:1.1.2")
                val kotlinVersion = "1.7.20"
                implementation(kotlin("gradle-plugin", version = kotlinVersion))
                implementation(kotlin("serialization", version = kotlinVersion))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

                val koin_version= "3.3.0"
                implementation("io.insert-koin:koin-core:$koin_version")

                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.postgresql:postgresql:42.5.1")
            }
        }
        val jvmTest by getting
    }
}



compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "database"
            packageVersion = "1.0.0"
        }
    }
}
