plugins {
    id("java")
    `maven-publish`
}

group = "io.github.sunshinewzy"
version = "1.0.4"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
    
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    jar {
        destinationDirectory.set(file("E:/Kotlin/Debug/Spigot-1.18.2/plugins"))
    }
    
//    processResources {
//        val props = mutableMapOf(
//            "version" to version
//        )
//        
//        inputs.properties(props)
//        
//        filteringCharset = "UTF-8"
//        filesMatching("*") {
//            expand(props)
//        }
//    }
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}