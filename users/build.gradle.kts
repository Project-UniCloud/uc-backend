plugins {
    java
    id("groovy")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.unicloudapp.user"
version = "unspecified"

repositories {
    mavenCentral()
}

val jetbrainsAnnotationsVersion = "26.0.2"
val lombokVersion = "1.18.38"
val jakartaValidationVersion = "3.0.0"
val hibernateValidatorVersion = "8.0.0.Final"
val assertJVersion = "3.27.3"
val mapStructVersion = "1.5.5.Final"

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("org.mockito:mockito-core:5.14.2")

    testImplementation("org.spockframework:spock-core:2.4-M6-groovy-4.0")
    testImplementation("org.spockframework:spock-spring:2.4-M6-groovy-4.0")

    compileOnly("org.projectlombok:lombok:$lombokVersion")

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
    implementation(project(":commons"))
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
    implementation("org.mapstruct:mapstruct:$mapStructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")
    implementation("org.apache.commons:commons-lang3:3.17.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.getByName<Jar>("jar") {
    enabled = true
}
tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
}