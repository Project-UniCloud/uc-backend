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

val postgresVersion = "42.7.8"

dependencies {
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    runtimeOnly("org.postgresql:postgresql:$postgresVersion")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("org.liquibase:liquibase-core")
    implementation(project(":users"))
    implementation(project(":cloud-management"))
    implementation(project(":commons"))
    implementation(project(":groups"))
    implementation(project(":auth"))
    implementation(project(":notifications"))
    testRuntimeOnly("com.h2database:h2")
    compileOnly("org.projectlombok:lombok:1.18.42")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.test {
    useJUnitPlatform()
}