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

val postgresVersion = "42.7.2"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    runtimeOnly("org.postgresql:postgresql:$postgresVersion")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    implementation("org.jetbrains:annotations:26.0.2")
    implementation(project(":users"))
    implementation(project(":cloud-managment"))
    implementation(project(":commons"))
    implementation(project(":groups"))

    compileOnly("org.projectlombok:lombok:1.18.38")
}

tasks.test {
    useJUnitPlatform()
}