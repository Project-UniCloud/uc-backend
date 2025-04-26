plugins {
    id("org.springframework.boot") version "3.4.4" apply false
    id("io.spring.dependency-management") version "1.1.4"
}

val springBootVersion = "3.4.4"

allprojects {
    group = "com.uncloudapp"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
}