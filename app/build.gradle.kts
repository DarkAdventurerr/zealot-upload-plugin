plugins {
    `java-library`
    kotlin("jvm")
    `maven-publish`
}

kotlin {
    jvmToolchain(17)
}

val okhttpVersion = "3.12.12"
val gsonVersion = "2.8.8"

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.android.tools.build:gradle:8.9.0")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])
            groupId = "com.dragonpass.intlapp"
            artifactId = "zealot-upload-plugin"
            version = "1.0.0-alpha01"
        }
    }
    repositories {
        maven {
            url = uri("../publishPluginRepo")
        }
    }
}

repositories {
    mavenCentral()
}