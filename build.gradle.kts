plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.34"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    description {
        contributors {
            name("小坤")
        }
        dependencies {
            name("MythicMobs").optional(true)
        }
    }

    install(
        "common",
        "common-5",
        "platform-bukkit",
        "module-configuration",
        "module-lang",
        "module-chat",
        "module-ui",
        "module-nms",
        "module-nms-util",
        "module-database",
        "expansion-javascript"
    )

    classifier = null
    version = "6.0.7-6"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.xkmc6.cn/minecraft")
    }
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11800:11800-minimize:api")
    compileOnly("ink.ptms.core:v11800:11800-minimize:mapped")
    compileOnly(fileTree("libs"))
    compileOnly("io.lumine.xikage.mythicmobs:MythicMobs:4.7.2@jar")
    compileOnly("com.google.code.gson:gson:2.8.9")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}