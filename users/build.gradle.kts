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

val h2Version = "2.3.232"
val jetbrainsAnnotationsVersion = "26.0.2"
val lombokVersion = "1.18.38"
val jakartaValidationVersion = "3.0.0"
val hibernateValidatorVersion = "8.0.0.Final"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    runtimeOnly("com.h2database:h2:$h2Version")

    compileOnly("org.projectlombok:lombok:$lombokVersion")

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
    implementation(project(":commons"))
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
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