import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("com.google.protobuf") version "0.9.6"
    id("io.spring.dependency-management")
    id("org.springframework.boot")
}

group = "com.unicloudapp.cloud"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
val grpcVersion = "1.78.0"
val protobufJavaVersion = "4.33.2"
val lombokVersion = "1.18.42"
val javaxAnnotationsVersion = "1.3.2"
val lombokMapstructBindingVersion = "0.2.0"
val mapstructVersion = "1.6.2"

dependencies {
    // Implementation dependencies (application runtime + compile)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation(project(":commons"))

    // gRPC and Protobuf
    implementation("com.google.protobuf:protobuf-java:$protobufJavaVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")

    // MapStruct (API + processors for annotation processing)
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")

    // Lombok (compileOnly) and its processor
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // Test-only dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
}

tasks.test {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufJavaVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

sourceSets {
    main {
        proto {
            srcDir("${projectDir.path}/src/main/resources/proto/proto")
        }
        java {
            srcDir("build/generated/source/proto/main/java")
            srcDir("build/generated/source/proto/main/grpc")
        }
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}
tasks.getByName<Jar>("jar") {
    enabled = true
}