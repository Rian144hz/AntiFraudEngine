plugins {
    kotlin("js") version "2.1.20"
}

group = "org.antifraudengine"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser {
            binaries.executable()
        }
    }
}
