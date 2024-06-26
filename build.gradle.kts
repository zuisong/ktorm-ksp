plugins {
    kotlin("jvm") apply false
}

buildscript {
    dependencies {
        classpath(libs.gradlePlugin.kotlin)
    }
}

val fileVersion = file("ktorm-ksp.version").readText().trim()

allprojects {
    group = "org.ktorm"
    version = fileVersion
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.allWarningsAsErrors = true
        kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        maxParallelForks = Runtime.getRuntime().availableProcessors()
    }

    repositories {
        mavenLocal()
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }
    }

    configureDetekt()
//    configureCopyrightCheck()
}
