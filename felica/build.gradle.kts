// Pure Kotlin/JVM module: FeliCa frame building and Suica/Pasmo history decoding.
// It has no Android dependency, so it can be unit tested on any JVM and reused
// later from a Kotlin Multiplatform project.
plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
