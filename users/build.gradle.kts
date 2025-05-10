plugins {
    id("java")
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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("org.springframework:spring-test:6.2.5")
    testImplementation("org.springframework.boot:spring-boot-test:3.4.4")
    testImplementation("com.diffblue.cover:cover-annotations:1.3.0")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")

    compileOnly("org.projectlombok:lombok:$lombokVersion")

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
    implementation(project(":commons"))
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
    compileOnly("com.diffblue.cover:cover-annotations:1.3.0")
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