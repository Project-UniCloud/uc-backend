plugins {
    id("org.springframework.boot") version "3.5.6" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("com.vanniktech.dependency.graph.generator") version "0.8.0"
    id("jacoco")
}

allprojects {
    repositories {
        mavenCentral()
    }
}

jacoco {
    toolVersion = "0.8.12"
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "java")
    apply(plugin = "jacoco")

    jacoco {
        toolVersion = rootProject.extensions.getByType<JacocoPluginExtension>().toolVersion
    }

    tasks.withType<Test>().configureEach {
        finalizedBy("jacocoTestReport")
        this.extensions.getByType<JacocoTaskExtension>().apply {
            isEnabled = true
            includes = listOf("com.unicloudapp.*")
        }
    }

    tasks.withType<JacocoReport>().configureEach {
        dependsOn(tasks.named("test"))

        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }

        val mainSourceSet = project.extensions.findByType<SourceSetContainer>()?.findByName("main")
        if (mainSourceSet != null) {
            sourceDirectories.from(mainSourceSet.allSource.srcDirs.filter { it.exists() })

            classDirectories.from(
                project.files(mainSourceSet.output.classesDirs).asFileTree.matching {
                    include("com/unicloudapp/**/*.class")
                }
            )
        }
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    description = "Generuje zagregowany raport pokrycia kodu dla wszystkich podmodułów."

    val executionDataFiles = subprojects.flatMap { subproject ->
        subproject.tasks.withType<Test>().mapNotNull { testTask ->
            testTask.extensions.findByType<JacocoTaskExtension>()?.destinationFile?.takeIf { it.exists() }
        }
    }
    executionData.from(files(executionDataFiles))

    val sourceDirectoriesFiles = subprojects.flatMap { subproject ->
        subproject.extensions.findByType<SourceSetContainer>()
            ?.findByName("main")
            ?.allSource
            ?.srcDirs
            ?.filter { it.exists() && !it.absolutePath.contains("build", ignoreCase = true) }
            ?: emptyList()
    }
    sourceDirectories.from(files(sourceDirectoriesFiles))

    val classDirectoriesFromSubprojects = subprojects.flatMap { subproject ->
        subproject.extensions.findByType<SourceSetContainer>()
            ?.findByName("main")
            ?.output
            ?.classesDirs
            ?.files
            ?.filter { it.exists() }
            ?: emptyList()
    }
    classDirectories.from(files(classDirectoriesFromSubprojects).asFileTree.matching {
        include("com/unicloudapp/**/*.class")
        exclude(
            "**/*Config.class",
            "**/config/**",
            "**/*Configuration.class"
        )
    })

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    dependsOn(subprojects.mapNotNull { it.tasks.findByName("test") })

    doFirst {
        println("Generowanie zagregowanego raportu JaCoCo...")
        println("Pliki .exec (${executionData.files.size}): ${executionData.files.joinToString { it.name }}")
        println("Katalogi źródłowe (${sourceDirectories.files.size}): ${sourceDirectories.files.joinToString { it.path.substringAfter(rootProject.projectDir.path) }}")
        println("Liczba filtrowanych plików klas: ${classDirectories.files.size}")
    }

    doLast {
        println("Zagregowany raport JaCoCo wygenerowany w: ${reports.html.outputLocation.get().asFile}")
    }
}