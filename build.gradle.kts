plugins {
    java
    id("io.izzel.taboolib") version "1.30"
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
        "module-nms-util"
    )

    classifier = null
    version = "6.0.3-8"
}

repositories {
    mavenCentral()
    maven {
        isAllowInsecureProtocol = true
        setUrl("http://xkmy.club/repository/")
    }
}

dependencies {
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly("ink.ptms.core:v11604:11604:all")
    compileOnly("ink.ptms.core:v11600:11600:all")
    compileOnly("ink.ptms.core:v11500:11500:all")
    compileOnly("ink.ptms.core:v11400:11400:all")
    compileOnly("ink.ptms.core:v11300:11300:all")
    compileOnly("ink.ptms.core:v11200:11200:all")
    compileOnly("ink.ptms.core:v11100:11100:all")
    compileOnly("ink.ptms.core:v11000:11000:all")
    compileOnly("ink.ptms.core:v10900:10900:all")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    implementation("io.lumine.xikage.mythicmobs:MythicMobs:4.7.2@jar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
