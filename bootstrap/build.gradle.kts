plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.unicloudapp.bootstrap"
version = "unspecified"

repositories {
    mavenCentral()
}

val postgresVersion = "42.7.6"

dependencies {
    runtimeOnly("org.postgresql:postgresql:$postgresVersion")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    implementation("org.jetbrains:annotations:26.0.2")
    implementation(project(":users"))
    implementation(project(":cloud-managment"))
    implementation(project(":commons"))
    implementation(project(":groups"))
    implementation(project(":auth"))
    testRuntimeOnly("com.h2database:h2")
    compileOnly("org.projectlombok:lombok:1.18.38")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.test {
    useJUnitPlatform()
}